package com.kubehelper.domain.filters;

import org.apache.commons.lang3.StringUtils;
import org.zkoss.zul.ListModelList;

/**
 * @author JDev
 */
public class LabelsGroupedColumnsFilter {
    private String name = "", additionalInfo = "";

    private String selectedNamespaceFilter = "";
    private String selectedResourceTypeFilter = "";
    private String selectedResourcePropertyFilter = "";
    private String selectedResourceNameFilter = "";

    private ListModelList<String> namespacesFilter = new ListModelList<>();
    private ListModelList<String> resourceTypesFilter = new ListModelList<>();
    private ListModelList<String> resourcePropertiesFilter = new ListModelList<>();
    private ListModelList<String> resourceNamesFilter = new ListModelList<>();

    public LabelsGroupedColumnsFilter() {
        namespacesFilter.clear();
        resourceTypesFilter.clear();
        resourcePropertiesFilter.clear();
        resourceNamesFilter.clear();
    }

    public void addNamespacesFilter(String namespaceFilter) {
        if (!namespacesFilter.contains(namespaceFilter)) {
            namespacesFilter.add(namespaceFilter);
        }
    }

    public void addResourceTypesFilter(String resourceTypeFilter) {
        if (!resourceTypesFilter.contains(resourceTypeFilter)) {
            resourceTypesFilter.add(resourceTypeFilter);
        }
    }

    public void addResourceNamesFilter(String resourceNameFilter) {
        if (!resourceNamesFilter.contains(resourceNameFilter)) {
            resourceNamesFilter.add(resourceNameFilter);
        }
    }

    public void addResourcePropertiesFilter(String resourcePropertyFilter) {
        if (!resourcePropertiesFilter.contains(resourcePropertyFilter)) {
            resourcePropertiesFilter.add(resourcePropertyFilter);
        }
    }

    public String getName() {
        return name;
    }

    public LabelsGroupedColumnsFilter setName(String name) {
        this.name = name;
        return this;
    }

    public String getSelectedResourcePropertyFilter() {
        return selectedResourcePropertyFilter;
    }

    public LabelsGroupedColumnsFilter setSelectedResourcePropertyFilter(String selectedResourcePropertyFilter) {
        this.selectedResourcePropertyFilter = selectedResourcePropertyFilter == null ? "" : selectedResourcePropertyFilter;
        return this;
    }

    public ListModelList<String> getResourcePropertiesFilter() {
        return resourcePropertiesFilter;
    }

    public LabelsGroupedColumnsFilter setResourcePropertiesFilter(ListModelList<String> resourcePropertiesFilter) {
        this.resourcePropertiesFilter = resourcePropertiesFilter;
        return this;
    }

    public String getAdditionalInfo() {
        return additionalInfo;
    }

    public LabelsGroupedColumnsFilter setAdditionalInfo(String additionalInfo) {
        this.additionalInfo = additionalInfo;
        return this;
    }

    public boolean isFilterActive() {
        return StringUtils.isNoneBlank(name, additionalInfo, selectedNamespaceFilter, selectedResourceTypeFilter, selectedResourcePropertyFilter, selectedResourceNameFilter);
    }

    public ListModelList<String> getNamespacesFilter() {
        return namespacesFilter;
    }

    public LabelsGroupedColumnsFilter setNamespacesFilter(ListModelList<String> namespacesFilter) {
        this.namespacesFilter = namespacesFilter;
        return this;
    }

    public ListModelList<String> getResourceTypesFilter() {
        return resourceTypesFilter;
    }

    public LabelsGroupedColumnsFilter setResourceTypesFilter(ListModelList<String> resourceTypesFilter) {
        this.resourceTypesFilter = resourceTypesFilter;
        return this;
    }

    public ListModelList<String> getResourceNamesFilter() {
        return resourceNamesFilter;
    }

    public LabelsGroupedColumnsFilter setResourceNamesFilter(ListModelList<String> resourceNamesFilter) {
        this.resourceNamesFilter = resourceNamesFilter;
        return this;
    }

    public String getSelectedNamespaceFilter() {
        return selectedNamespaceFilter;
    }

    public LabelsGroupedColumnsFilter setSelectedNamespaceFilter(String selectedNamespaceFilter) {
        this.selectedNamespaceFilter = selectedNamespaceFilter == null ? "" : selectedNamespaceFilter;
        return this;
    }

    public String getSelectedResourceTypeFilter() {
        return selectedResourceTypeFilter;
    }

    public LabelsGroupedColumnsFilter setSelectedResourceTypeFilter(String selectedResourceTypeFilter) {
        this.selectedResourceTypeFilter = selectedResourceTypeFilter == null ? "" : selectedResourceTypeFilter;
        return this;
    }

    public String getSelectedResourceNameFilter() {
        return selectedResourceNameFilter;
    }

    public LabelsGroupedColumnsFilter setSelectedResourceNameFilter(String selectedResourceNameFilter) {
        this.selectedResourceNameFilter = selectedResourceNameFilter == null ? "" : selectedResourceNameFilter;
        return this;
    }
}
