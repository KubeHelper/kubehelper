/*
Kube Helper
Copyright (C) 2021 JDev

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
package com.kubehelper.viewmodels;

import com.google.common.collect.Iterables;
import com.kubehelper.common.Global;
import com.kubehelper.common.Resource;
import com.kubehelper.domain.filters.SearchFilter;
import com.kubehelper.domain.models.SearchModel;
import com.kubehelper.domain.results.SearchResult;
import com.kubehelper.services.CommonService;
import com.kubehelper.services.SearchService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ContextParam;
import org.zkoss.bind.annotation.ContextType;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Path;
import org.zkoss.zk.ui.event.AfterSizeEvent;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zk.ui.util.Notification;
import org.zkoss.zkplus.spring.DelegatingVariableResolver;
import org.zkoss.zul.Auxhead;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Footer;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Vbox;
import org.zkoss.zul.Window;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.StreamSupport;

import static com.kubehelper.common.Resource.CLUSTER_ROLE;
import static com.kubehelper.common.Resource.CLUSTER_ROLE_BINDING;
import static com.kubehelper.common.Resource.CONFIG_MAP;
import static com.kubehelper.common.Resource.DAEMON_SET;
import static com.kubehelper.common.Resource.DEPLOYMENT;
import static com.kubehelper.common.Resource.ENV_VARIABLE;
import static com.kubehelper.common.Resource.JOB;
import static com.kubehelper.common.Resource.NAMESPACE;
import static com.kubehelper.common.Resource.NETWORK_POLICY;
import static com.kubehelper.common.Resource.PERSISTENT_VOLUME;
import static com.kubehelper.common.Resource.PERSISTENT_VOLUME_CLAIM;
import static com.kubehelper.common.Resource.POD;
import static com.kubehelper.common.Resource.POD_DISRUPTION_BUDGET;
import static com.kubehelper.common.Resource.POD_SECURITY_POLICY;
import static com.kubehelper.common.Resource.REPLICA_SET;
import static com.kubehelper.common.Resource.ROLE;
import static com.kubehelper.common.Resource.ROLE_BINDING;
import static com.kubehelper.common.Resource.SECRET;
import static com.kubehelper.common.Resource.SERVICE;
import static com.kubehelper.common.Resource.SERVICE_ACCOUNT;
import static com.kubehelper.common.Resource.STATEFUL_SET;

/**
 * View Model for searching for text in resources and visualize results in table.
 * ViewModel initializes ..kubehelper/pages/search.zul
 *
 * @author JDev
 */
@VariableResolver(DelegatingVariableResolver.class)
public class SearchVM implements EventListener {

    private static Logger logger = LoggerFactory.getLogger(SearchVM.class);

    private boolean isSearchButtonPressed;

    private int centerLayoutHeight = 700;

    private Set<Resource> selectedResources = new HashSet<>() {{
        add(CONFIG_MAP);
        add(POD);
        add(NAMESPACE);
        add(DEPLOYMENT);
        add(STATEFUL_SET);
        add(REPLICA_SET);
        add(ENV_VARIABLE);
        add(DAEMON_SET);
        add(SERVICE_ACCOUNT);
        add(SERVICE);
    }};
    private List<Resource> searchResources = Arrays.asList(ENV_VARIABLE, POD, CONFIG_MAP, SECRET, SERVICE_ACCOUNT, SERVICE, DAEMON_SET, DEPLOYMENT, REPLICA_SET, STATEFUL_SET, JOB, NAMESPACE,
            PERSISTENT_VOLUME_CLAIM, PERSISTENT_VOLUME, CLUSTER_ROLE_BINDING, CLUSTER_ROLE, ROLE_BINDING, ROLE, NETWORK_POLICY, POD_DISRUPTION_BUDGET, POD_SECURITY_POLICY);
    private ListModelList<SearchResult> searchResults = new ListModelList<>();

    private SearchModel model;

    @WireVariable
    private CommonService commonService;

    @WireVariable
    private SearchService searchService;

    @Wire
    private Footer searchGridTotalItemsFooter;

    @Init
    @NotifyChange("*")
    public void init() {
        model = (SearchModel) Global.ACTIVE_MODELS.computeIfAbsent(Global.SEARCH_MODEL, (k) -> Global.NEW_MODELS.get(Global.SEARCH_MODEL));
        onInitPreparations();
    }

    @Listen("onAfterSize=#centerLayoutSearchGrBox")
    public void onAfterSizeCenter(AfterSizeEvent event) {
        centerLayoutHeight = event.getHeight() - 10;
        BindUtils.postNotifyChange(this, ".");
    }


    /**
     * Creates CheckBox components Dynamically after UI render.
     * <p>
     * Explanation:
     * Selectors.wireComponents() in order to be able to @Wire GUI components.
     * Selectors.wireEventListeners() in order to be able to work with listeners and events.
     */
    @AfterCompose
    public void afterCompose(@ContextParam(ContextType.VIEW) Component view) {
        Selectors.wireComponents(view, this, false);
        Selectors.wireEventListeners(view, this);
        createKubeResourcesCheckboxes();
    }

    /**
     * Searches for a string in selected resources.
     */
    @Command
    @NotifyChange({"totalItems", "searchResults", "filter"})
    public void search() {
        model.setFilter(new SearchFilter());
        clearAllFilterComboboxes();
        searchService.search(model, selectedResources);
        isSearchButtonPressed = true;
        onInitPreparations();
    }

    /**
     * Select all resources command. For mark or unmark all kubernetes resources with one kubeResourcesGBoxCheckAll CheckBox.
     *
     * @param component - kubeResourcesGBoxCheckAll Checkbox itself.
     */
    @Command
    @NotifyChange("kubeResourcesVBox")
    public void selectAllResources(@ContextParam(ContextType.COMPONENT) Component component) {
        boolean isResourcesCheckBoxChecked = ((Checkbox) component).isChecked();
        selectedResources = isResourcesCheckBoxChecked ? EnumSet.allOf(Resource.class) : new HashSet<>();
        Vbox checkboxesVLayout = (Vbox) Path.getComponent("//indexPage/templateInclude/kubeResourcesVBox");
        for (int i = 0; i < checkboxesVLayout.getChildren().size(); i++) {
            checkboxesVLayout.getChildren().get(i).getChildren().forEach(cBox -> ((Checkbox) cBox).setChecked(isResourcesCheckBoxChecked));
        }
    }

    /**
     * Prepare view for result depends on filters or new searches
     */
    private void onInitPreparations() {
        model.setNamespaces(model.getNamespaces().isEmpty() ? commonService.getAllNamespaces() : model.getNamespaces());
        if (model.getFilter().isFilterActive() && !model.getSearchResults().isEmpty()) {
            filterSearches();
        } else {
            searchResults = new ListModelList<>(model.getSearchResults());
        }
        sortResultsByNamespace();
        logger.debug("Found {} namespaces.", model.getNamespaces());
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
        for (SearchResult searchResult : model.getSearchResults()) {
            if (StringUtils.containsIgnoreCase(searchResult.getFoundString(), getFilter().getFoundString()) &&
                    StringUtils.containsIgnoreCase(searchResult.getCreationTime(), getFilter().getCreationTime()) &&
                    StringUtils.containsIgnoreCase(searchResult.getAdditionalInfo(), getFilter().getAdditionalInfo()) &&
                    commonService.checkEqualsFilter(searchResult.getResourceName(), getFilter().getSelectedResourceNameFilter()) &&
                    commonService.checkEqualsFilter(searchResult.getResourceType(), getFilter().getSelectedResourceTypeFilter()) &&
                    commonService.checkEqualsFilter(searchResult.getNamespace(), getFilter().getSelectedNamespaceFilter())) {
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
        model = new SearchModel();
        Global.ACTIVE_MODELS.replace(Global.SEARCH_MODEL, model);
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
        Vbox checkboxesVLayout = (Vbox) Path.getComponent("//indexPage/templateInclude/kubeResourcesVBox");
        StreamSupport.stream(Iterables.partition(searchResources, 10).spliterator(), false).forEach(list -> {
            Hbox hbox = createNewHbox();
            for (Resource resource : list) {
                Checkbox resourceCheckbox = new Checkbox(resource.getKind());
                resourceCheckbox.setId(resource.name() + "_Checkbox");
                resourceCheckbox.setStyle("padding: 5px;");
                resourceCheckbox.addEventListener("onCheck", this);
                resourceCheckbox.setChecked(selectedResources.contains(resource));
                hbox.appendChild(resourceCheckbox);
            }
            checkboxesVLayout.appendChild(hbox);
        });
    }

    private Hbox createNewHbox() {
        Hbox hbox = new Hbox();
        hbox.setHflex("1");
        hbox.setStyle("flex-wrap: flex");
        return hbox;
    }

    /**
     * Kubernetes Resources CheckBoxes Events handling.
     *
     * @param event - onCheck event.
     */
    @Override
    public void onEvent(Event event) {
        //Add or remove selected resource to selectedResources model.
        String resourceId = event.getTarget().getId();
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
            BindUtils.postNotifyChange(this, "kubeResourcesGBoxCheckAll");
        }
    }

    @Command
    public void showAdditionalInfo(@BindingParam("item") SearchResult item) {
        String content = "";
        if (CONFIG_MAP.getKind().equals(item.getResourceType())) {
            //escape XML <> symbols for <pre> tag
            content = item.getAdditionalInfo().replace("<", "&lt;").replace(">", "&gt;");
        } else {
            content = item.getAdditionalInfo();
        }
        Map<String, Object> parameters = getParametersMap(item.getRawResourceType(), item.getResourceName(), item.getFoundString(), item.getNamespace(), content);
        Window window = (Window) Executions.createComponents(Global.PATH_TO_RAW_RESOURCE_ZUL, null, parameters);
        window.doModal();
    }

    /**
     * Shows full resource definition in popup window.
     *
     * @param item - @{@link SearchResult} item.
     */
    @Command
    public void showFullDefinition(@BindingParam("item") SearchResult item) {
        Resource resource = item.getRawResourceType() == ENV_VARIABLE ? POD : item.getRawResourceType();
        String name = item.getResourceName();

        if (item.getRawResourceType() == ENV_VARIABLE) {
            resource = POD;
            name = item.getResourceName().indexOf("[") != -1 ? item.getResourceName().substring(0, item.getResourceName().indexOf("[") - 1).trim() : item.getResourceName();
        }
        Map<String, Object> parameters = getParametersMap(resource, name, item.getResourceName(), item.getNamespace(), item.getFullDefinition());
        Window window = (Window) Executions.createComponents(Global.PATH_TO_RAW_RESOURCE_ZUL, null, parameters);
        window.doModal();
    }


    private Map<String, Object> getParametersMap(Resource resource, String name, String title, String namespace, String content) {
        return Map.of("resource", resource, "name", name, "title", title, "namespace", namespace, "content", content);
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
        if (isSearchButtonPressed && !searchResults.isEmpty()) {
            Notification.show(String.format("Found %s items", searchResults.size()), "info", searchGridTotalItemsFooter, "before_end", 2000);
        }
        if (isSearchButtonPressed && model.hasSearchErrors()) {
            Window window = (Window) Executions.createComponents(Global.PATH_TO_ERROR_RESOURCE_ZUL, null, Map.of("errors", model.getSearchExceptions()));
            window.doModal();
            model.setSearchExceptions(new ArrayList<>());
        }
        isSearchButtonPressed = false;
        return searchResults;
    }


    public boolean isSkipKubeNamespaces() {
        return model.isSkipKubeNamespaces();
    }

    public void setSkipKubeNamespaces(boolean skipKubeNamespaces) {
        this.model.setSkipKubeNamespaces(skipKubeNamespaces);
    }

    public boolean isSkipNativeEnvVars() {
        return model.isSkipNativeEnvVars();
    }

    public void setSkipNativeEnvVars(boolean skipNativeEnvVars) {
        this.model.setSkipNativeEnvVars(skipNativeEnvVars);
    }

    public String getSelectedNamespace() {
        return model.getSelectedNamespace();
    }

    public SearchVM setSelectedNamespace(String selectedNamespace) {
        this.model.setSelectedNamespace(selectedNamespace);
        return this;
    }

    public String getSearchString() {
        return this.model.getSearchString();
    }

    public void setSearchString(String searchString) {
        this.model.setSearchString(searchString);
    }

    public SearchFilter getFilter() {
        return model.getFilter();
    }

    public String getTotalItems() {
        return String.format("Total Items: %d", searchResults.size());
    }


    public List<String> getNamespaces() {
        return model.getNamespaces();
    }

    public String getMainGridHeight() {
        return centerLayoutHeight + "px";
    }

}
