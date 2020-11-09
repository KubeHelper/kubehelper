package com.kubehelper.viewmodels;

import com.kubehelper.common.Global;
import com.kubehelper.domain.filters.EventsFilter;
import com.kubehelper.domain.models.EventsModel;
import com.kubehelper.domain.results.EventResult;
import com.kubehelper.services.CommonService;
import com.kubehelper.services.EventsService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ContextParam;
import org.zkoss.bind.annotation.ContextType;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Path;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zk.ui.util.Notification;
import org.zkoss.zkplus.spring.DelegatingVariableResolver;
import org.zkoss.zul.Auxhead;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Footer;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Window;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * @author JDev
 */
@VariableResolver(DelegatingVariableResolver.class)
public class EventsVM {

    private static Logger logger = LoggerFactory.getLogger(EventsVM.class);

    private boolean isSearchButtonPressed;

    @Wire
    private Footer eventGridTotalItemsFooter;

    private ListModelList<EventResult> searchResults = new ListModelList<>();

    private EventsModel eventsModel;

    @WireVariable
    private CommonService commonService;

    @WireVariable
    private EventsService eventsService;

    @Init
    @NotifyChange("*")
    public void init() {
        eventsModel = (EventsModel) Global.ACTIVE_MODELS.computeIfAbsent(Global.EVENTS_MODEL, (k) -> Global.NEW_MODELS.get(Global.EVENTS_MODEL));
        onInitPreparations();
    }

    /**
     * Creates CheckBox components Dynamically after UI render.
     * <p>
     * Explanation:
     * We need Selectors.wireComponents() in order to be able to @Wire GUI components.
     */
    @AfterCompose
    public void afterCompose(@ContextParam(ContextType.VIEW) Component view) {
        Selectors.wireComponents(view, this, false);
    }

    @Command
    @NotifyChange({"totalItems", "searchResults", "filter"})
    public void search() {
        eventsModel.setFilter(new EventsFilter());
        eventsService.search(eventsModel);
        eventsModel.setNamespaces(commonService.getAllNamespaces());
        eventsModel.setSearchExceptions(new ArrayList<>());
        clearAllFilterComboboxes();
        isSearchButtonPressed = true;
        logger.info("Found {} namespaces.", eventsModel.getNamespaces());
        onInitPreparations();
    }


    /**
     * Prepare view for result depends on filters or new searches
     */
    private void onInitPreparations() {
        eventsModel.setNamespaces(eventsModel.getNamespaces().isEmpty() ? commonService.getAllNamespaces() : eventsModel.getNamespaces());
        if (eventsModel.getFilter().isFilterActive() && !eventsModel.getSearchResults().isEmpty()) {
            filterSearches();
        } else {
            searchResults = new ListModelList<>(eventsModel.getSearchResults());
        }
        sortResultsByNamespace();
    }

    private void sortResultsByNamespace() {
        searchResults.sort(Comparator.comparing(EventResult::getNamespace));
    }

    /**
     * Filters searches and refresh total items label and search results view.
     */
    @Command
    @NotifyChange({"totalItems", "searchResults"})
    public void filterSearches() {
        searchResults.clear();
        for (EventResult searchResult : eventsModel.getSearchResults()) {
            if (StringUtils.containsIgnoreCase(searchResult.getCreationTime(), getFilter().getCreationTime()) &&
                    StringUtils.containsIgnoreCase(searchResult.getAdditionalInfo(), getFilter().getAdditionalInfo()) &&
                    StringUtils.containsIgnoreCase(searchResult.getResourceName(), getFilter().getSelectedResourceNameFilter()) &&
                    StringUtils.containsIgnoreCase(searchResult.getResourceType(), getFilter().getSelectedResourceTypeFilter()) &&
                    StringUtils.containsIgnoreCase(searchResult.getNamespace(), getFilter().getSelectedNamespaceFilter())) {
                searchResults.add(searchResult);
            }
        }
        sortResultsByNamespace();
    }

    /**
     * Clears all components, model and pull all namespaces again.
     */
    @Command
    @NotifyChange("*")
    public void clearAll() {
        eventsModel.setSearchResults(new ListModelList<>())
                .setFilter(new EventsFilter())
                .setNamespaces(commonService.getAllNamespaces())
                .setSelectedNamespace("all")
                .setSearchExceptions(new ArrayList<>());
        searchResults = new ListModelList<>();
        clearAllFilterComboboxes();
    }

    /**
     * Removes last selected value from all filter comboboxes.
     */
    private void clearAllFilterComboboxes() {
        Auxhead searchGridAuxHead = (Auxhead) Path.getComponent("//indexPage/templateInclude/eventsGridAuxHead");
        for (Component child : searchGridAuxHead.getFellows()) {
            if (Arrays.asList("filterResourceNamesCBox", "filterNamespacesCBox", "filterResourceTypesCBox").contains(child.getId())) {
                Combobox cBox = (Combobox) child;
                cBox.setValue("");
            }
        }
    }


    public String getSelectedNamespace() {
        return eventsModel.getSelectedNamespace();
    }

    public EventsVM setSelectedNamespace(String selectedNamespace) {
        this.eventsModel.setSelectedNamespace(selectedNamespace);
        return this;
    }

    public EventsFilter getFilter() {
        return eventsModel.getFilter();
    }

    public String getTotalItems() {
        return String.format("Total Items: %d", searchResults.size());
    }

    /**
     * Returns events results for grid and shows Notification if nothing was found or/and error window if some errors has occurred while parsing the results.
     *
     * @return - events results
     */
    public ListModelList<EventResult> getSearchResults() {
        if (isSearchButtonPressed && searchResults.isEmpty()) {
            Notification.show("Nothing found.", "info", eventGridTotalItemsFooter, "before_end", 2000);
        }
        if (isSearchButtonPressed && !searchResults.isEmpty()) {
            Notification.show("Found: " + searchResults.size() + " items", "info", eventGridTotalItemsFooter, "before_end", 2000);
        }
        if (isSearchButtonPressed && eventsModel.hasSearchErrors()) {
            Window window = (Window) Executions.createComponents("~./zul/components/errors.zul", null, Map.of("errors", eventsModel.getSearchExceptions()));
            window.doModal();
        }
        isSearchButtonPressed = false;
        return searchResults;
    }

    public List<String> getNamespaces() {
        return eventsModel.getNamespaces();
    }

}
