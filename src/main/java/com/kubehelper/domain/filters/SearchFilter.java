package com.kubehelper.domain.filters;

import org.apache.commons.lang3.StringUtils;
import org.zkoss.zul.ListModelList;

/**
 * @author JDev
 */
public class SearchFilter {
    private String namespace = "", creationTime = "", foundString = "", additionalInfo = "";

    private String selectedNamespaceFilter = "";
    private String selectedResourceTypeFilter = "";
    private String selectedResourceNameFilter = "";

    private ListModelList<String> namespacesFilter = new ListModelList<>();
    private ListModelList<String> resourceTypesFilter = new ListModelList<>();
    private ListModelList<String> resourceNamesFilter = new ListModelList<>();

    public SearchFilter() {
        namespacesFilter.clear();
        resourceTypesFilter.clear();
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

    public String getNamespace() {
        return namespace;
    }

    public SearchFilter setNamespace(String namespace) {
        this.namespace = namespace == null ? "" : namespace.trim();
        return this;
    }

    public String getCreationTime() {
        return creationTime;
    }

    public SearchFilter setCreationTime(String creationTime) {
        this.creationTime = creationTime == null ? "" : creationTime.trim();
        return this;
    }

    public String getFoundString() {
        return foundString;
    }

    public SearchFilter setFoundString(String foundString) {
        this.foundString = foundString;
        return this;
    }

    public String getAdditionalInfo() {
        return additionalInfo;
    }

    public SearchFilter setAdditionalInfo(String additionalInfo) {
        this.additionalInfo = additionalInfo;
        return this;
    }

    public boolean isFilterActive() {
        return StringUtils.isNoneBlank(namespace, creationTime, foundString, additionalInfo, selectedNamespaceFilter, selectedResourceTypeFilter, selectedResourceNameFilter);
    }

    public ListModelList<String> getNamespacesFilter() {
        return namespacesFilter;
    }

    public SearchFilter setNamespacesFilter(ListModelList<String> namespacesFilter) {
        this.namespacesFilter = namespacesFilter;
        return this;
    }

    public ListModelList<String> getResourceTypesFilter() {
        return resourceTypesFilter;
    }

    public SearchFilter setResourceTypesFilter(ListModelList<String> resourceTypesFilter) {
        this.resourceTypesFilter = resourceTypesFilter;
        return this;
    }

    public ListModelList<String> getResourceNamesFilter() {
        return resourceNamesFilter;
    }

    public SearchFilter setResourceNamesFilter(ListModelList<String> resourceNamesFilter) {
        this.resourceNamesFilter = resourceNamesFilter;
        return this;
    }

    public String getSelectedNamespaceFilter() {
        return selectedNamespaceFilter;
    }

    public SearchFilter setSelectedNamespaceFilter(String selectedNamespaceFilter) {
        this.selectedNamespaceFilter = selectedNamespaceFilter == null ? "" : selectedNamespaceFilter;
        return this;
    }

    public String getSelectedResourceTypeFilter() {
        return selectedResourceTypeFilter;
    }

    public SearchFilter setSelectedResourceTypeFilter(String selectedResourceTypeFilter) {
        this.selectedResourceTypeFilter = selectedResourceTypeFilter == null ? "" : selectedResourceTypeFilter;
        return this;
    }

    public String getSelectedResourceNameFilter() {
        return selectedResourceNameFilter;
    }

    public SearchFilter setSelectedResourceNameFilter(String selectedResourceNameFilter) {
        this.selectedResourceNameFilter = selectedResourceNameFilter == null ? "" : selectedResourceNameFilter;
        return this;
    }
}
