package com.kubehelper.viewmodels;

import com.kubehelper.common.Global;
import com.kubehelper.common.Resource;
import com.kubehelper.domain.filters.SearchFilter;
import com.kubehelper.domain.models.SearchModel;
import com.kubehelper.domain.results.SearchResult;
import com.kubehelper.services.CommonService;
import com.kubehelper.services.SearchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ContextParam;
import org.zkoss.bind.annotation.ContextType;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Path;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zk.ui.util.Notification;
import org.zkoss.zkplus.spring.DelegatingVariableResolver;
import org.zkoss.zul.Auxhead;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Footer;
import org.zkoss.zul.Hlayout;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Vlayout;
import org.zkoss.zul.Window;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author JDev
 */
@VariableResolver(DelegatingVariableResolver.class)
public class SearchVM implements EventListener {

    private static Logger logger = LoggerFactory.getLogger(SearchVM.class);

    private boolean isSearchButtonPressed;

    @Wire
    private Footer searchGridTotalItemsFooter;

    private Set<Resource> selectedResources = new HashSet<>() {{
        add(Resource.CONFIG_MAP);
        add(Resource.POD);
        add(Resource.SERVICE);
        add(Resource.DEPLOYMENT);
        add(Resource.STATEFUL_SET);
        add(Resource.REPLICA_SET);
        add(Resource.ENV_VARIABLE);
    }};
    private ListModelList<SearchResult> searchResults = new ListModelList<>();

    private SearchModel searchModel;

    @WireVariable
    private CommonService commonService;

    @WireVariable
    private SearchService searchService;

    @Init
    @NotifyChange("*")
    public void init() {
        searchModel = (SearchModel) Global.ACTIVE_MODELS.computeIfAbsent(Global.SEARCH_MODEL, (k) -> Global.NEW_MODELS.get(Global.SEARCH_MODEL));
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
        createKubeResourcesCheckboxes();
        Selectors.wireComponents(view, this, false);
    }

    @Command
    @NotifyChange({"totalItems", "searchResults", "filter"})
    public void search() {
        clearAll();
        searchModel.setFilter(new SearchFilter());
        searchService.search(searchModel.getSelectedNamespace(), searchModel);
        searchModel.setNamespaces(commonService.getAllNamespaces());
        isSearchButtonPressed = true;
        logger.info("Found {} namespaces.", searchModel.getNamespaces());
        onInitPreparations();
    }

    /**
     * Select all resources command. For mark or unmark all kubernetes resources with one kubeResourcesGBoxCheckAll CheckBox.
     *
     * @param component - kubeResourcesGBoxCheckAll Checkbox itself.
     */
    @Command
    @NotifyChange("kubeResources")
    public void selectAllResources(@ContextParam(ContextType.COMPONENT) Component component) {
        boolean isResourcesCheckBoxChecked = ((Checkbox) component).isChecked();
        selectedResources = isResourcesCheckBoxChecked ? EnumSet.allOf(Resource.class) : new HashSet<>();
        Vlayout checkboxesVLayout = (Vlayout) Path.getComponent("//indexPage/templateInclude/kubeResources");
        for (int i = 0; i < checkboxesVLayout.getChildren().size(); i++) {
            checkboxesVLayout.getChildren().get(i).getChildren().forEach(cBox -> {
                ((Checkbox) cBox).setChecked(isResourcesCheckBoxChecked);
            });
        }
    }

    /**
     * Prepare view for result depends on filters or new searches
     */
    private void onInitPreparations() {
        searchModel.setNamespaces(searchModel.getNamespaces().isEmpty() ? commonService.getAllNamespaces() : searchModel.getNamespaces());
        if (searchModel.getFilter().isFilterActive() && !searchModel.getSearchResults().isEmpty()) {
            filterSearches();
        } else {
            searchResults = new ListModelList<>(searchModel.getSearchResults());
        }
        sortResultsByNamespace();
//        updateHeightsAndRerenderVM();
    }

    private void sortResultsByNamespace() {
        searchResults.sort(Comparator.comparing(SearchResult::getNamespace));
    }

    /**
     * Filters searches and refresh total items label and search results view.
     */
    @Command
    @NotifyChange({"totalItems", "searchResults"})
    public void filterSearches() {
        searchResults.clear();
        for (SearchResult searchResult : searchModel.getSearchResults()) {
            if (searchResult.getFoundString().toLowerCase().contains(getFilter().getFoundString().toLowerCase()) &&
                    searchResult.getCreationTime().toLowerCase().contains(getFilter().getCreationTime().toLowerCase()) &&
                    searchResult.getAdditionalInfo().toLowerCase().contains(getFilter().getAdditionalInfo().toLowerCase()) &&
                    searchResult.getResourceName().toLowerCase().contains(getFilter().getSelectedResourceNameFilter().toLowerCase()) &&
                    searchResult.getResourceType().toLowerCase().contains(getFilter().getSelectedResourceTypeFilter().toLowerCase()) &&
                    searchResult.getNamespace().toLowerCase().contains(getFilter().getSelectedNamespaceFilter().toLowerCase())) {
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
        searchModel.setSearchResults(new ListModelList<>())
                .setFilter(new SearchFilter())
                .setNamespaces(commonService.getAllNamespaces())
                .setSelectedNamespace("all")
                .setSearchString("")
                .setSearchExceptions(new ArrayList<>());
        searchResults = new ListModelList<>();
        clearAllFilterComboboxes();
    }

    /**
     * Removes last selected value from all filter comboboxes.
     */
    private void clearAllFilterComboboxes() {
        Auxhead searchGridAuxHead = (Auxhead) Path.getComponent("//indexPage/templateInclude/searchGridAuxHead");
        for (Component child : searchGridAuxHead.getFellows()) {
            if (Arrays.asList("filterResourceNamesCBox", "filterNamespacesCBox", "filterResourceTypesCBox").contains(child.getId())) {
                Combobox cBox = (Combobox) child;
                cBox.setValue("");
            }
        }
    }

    /**
     * Create dynamically CheckBoxes for all kubernetes resources. 10 per Hlayout.
     */
    private void createKubeResourcesCheckboxes() {
        List<Resource> resources = Arrays.asList(Resource.values());
        Hlayout hlayout = null;
        Vlayout checkboxesVLayout = (Vlayout) Path.getComponent("//indexPage/templateInclude/kubeResources");
        for (int i = 0; i < resources.size(); i++) {
            Resource resource = resources.get(i);

            if (i % 10 == 0) {
                hlayout = new Hlayout();
                hlayout.setValign("middle");
            }
            Checkbox resourceCheckbox = new Checkbox(Resource.getValueByKey(resource.name()));
            resourceCheckbox.setId(resource.name() + "_Checkbox");
            resourceCheckbox.setStyle("padding: 5px;");
            resourceCheckbox.addEventListener("onCheck", this);
            resourceCheckbox.setChecked(selectedResources.contains(resource));
            hlayout.appendChild(resourceCheckbox);
            checkboxesVLayout.appendChild(hlayout);
        }
    }

    /**
     * Kubernetes Resources CheckBoxes Events handling.
     *
     * @param event - onCheck event.
     */
    @Override
    public void onEvent(Event event) {
        //Add or remove selected resource to selectedResources model.
        String resourceId = ((Checkbox) event.getTarget()).getId();
        String resourceName = resourceId.substring(0, resourceId.lastIndexOf("_"));
        if (selectedResources.contains(Resource.valueOf(resourceName))) {
            selectedResources.remove(Resource.valueOf(resourceName));
        } else {
            selectedResources.add(Resource.valueOf(resourceName));
        }

        //Set checked kubeResourcesGBoxCheckAll CheckBox if at least one resource selected.
        Checkbox kubeResourcesCheckAll = (Checkbox) Path.getComponent("//indexPage/templateInclude/kubeResourcesGBoxCheckAll");
        if (!kubeResourcesCheckAll.isChecked() && !selectedResources.isEmpty()) {
            kubeResourcesCheckAll.setChecked(true);
            BindUtils.postNotifyChange(null, null, this, "kubeResourcesGBoxCheckAll");
        }
    }


    public boolean isSkipKubeNamespaces() {
        return searchModel.isSkipKubeNamespaces();
    }

    public void setSkipKubeNamespaces(boolean skipKubeNamespaces) {
        this.searchModel.setSkipKubeNamespaces(skipKubeNamespaces);
    }

    public boolean isSkipNativeEnvVars() {
        return searchModel.isSkipNativeEnvVars();
    }

    public void setSkipNativeEnvVars(boolean skipNativeEnvVars) {
        this.searchModel.setSkipNativeEnvVars(skipNativeEnvVars);
    }

    public String getSelectedNamespace() {
        return searchModel.getSelectedNamespace();
    }

    public SearchVM setSelectedNamespace(String selectedNamespace) {
        this.searchModel.setSelectedNamespace(selectedNamespace);
        return this;
    }

    public String getSearchString() {
        return this.searchModel.getSearchString();
    }

    public void setSearchString(String searchString) {
        this.searchModel.setSearchString(searchString);
    }

    public SearchFilter getFilter() {
        return searchModel.getFilter();
    }

    public String getTotalItems() {
        return String.format("Total Items: %d", searchResults.size());
    }

    public String getProgressLabel() {
        return "Progress: ";
//        return "Progress: " + searchService.getProgressLabel();
    }

    /**
     * Returns search results for grid and shows Notification if nothing was found or/and error window if some errors has occurred while parsing the results.
     *
     * @return - search results
     */
    public ListModelList<SearchResult> getSearchResults() {
        if (isSearchButtonPressed && searchResults.isEmpty()) {
            Notification.show("Nothing found.", "info", searchGridTotalItemsFooter, "before_end", 2000);
        }
        if (isSearchButtonPressed && searchModel.hasSearchErrors()) {
            Window window = (Window) Executions.createComponents("~./zul/components/errors.zul", null, Map.of("errors", searchModel.getSearchExceptions()));
            window.doModal();
        }
        isSearchButtonPressed = false;
        return searchResults;
    }

    public List<String> getNamespaces() {
        return searchModel.getNamespaces();
    }

    public String getSelectedResourceNameFilter() {
        return getFilter().getSelectedResourceNameFilter();
    }

}
