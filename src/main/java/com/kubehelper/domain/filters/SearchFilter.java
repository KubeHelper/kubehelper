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
package com.kubehelper.domain.filters;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author JDev
 */
public class SearchFilter {

    private String namespace = "", creationTime = "", foundString = "", additionalInfo = "";

    private String selectedNamespaceFilter = "";
    private String selectedResourceTypeFilter = "";
    private String selectedResourceNameFilter = "";

    private List<String> namespacesFilter = new ArrayList<>();
    private List<String> resourceTypesFilter = new ArrayList<>();
    private List<String> resourceNamesFilter = new ArrayList<>();


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

    public boolean isFilterActive() {
        return StringUtils.isNoneBlank(namespace, creationTime, foundString, additionalInfo, selectedNamespaceFilter, selectedResourceTypeFilter, selectedResourceNameFilter);
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
        this.foundString = foundString == null ? "" : foundString;
        return this;
    }

    public String getAdditionalInfo() {
        return additionalInfo;
    }

    public SearchFilter setAdditionalInfo(String additionalInfo) {
        this.additionalInfo = additionalInfo == null ? "" : additionalInfo;
        return this;
    }

    public List<String> getNamespacesFilter() {
        return namespacesFilter;
    }

    public SearchFilter setNamespacesFilter(List<String> namespacesFilter) {
        this.namespacesFilter = namespacesFilter;
        return this;
    }

    public List<String> getResourceTypesFilter() {
        return resourceTypesFilter;
    }

    public SearchFilter setResourceTypesFilter(List<String> resourceTypesFilter) {
        this.resourceTypesFilter = resourceTypesFilter;
        return this;
    }

    public List<String> getResourceNamesFilter() {
        return resourceNamesFilter;
    }

    public SearchFilter setResourceNamesFilter(List<String> resourceNamesFilter) {
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
