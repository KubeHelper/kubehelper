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
public class RolesSecurityFilter {

    private String creationTime = "";

    private String selectedResourceNameFilter = "";
    private String selectedNamespaceFilter = "";
    private String selectedResourceTypeFilter = "";

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

    public void addResourceNamesFilter(String resourceName) {
        if (!resourceNamesFilter.contains(resourceName)) {
            resourceNamesFilter.add(resourceName);
        }
    }

    public boolean isFilterActive() {
        return StringUtils.isNoneBlank(creationTime, selectedNamespaceFilter, selectedResourceTypeFilter, selectedResourceNameFilter);
    }

    public String getSelectedResourceNameFilter() {
        return selectedResourceNameFilter;
    }

    public RolesSecurityFilter setSelectedResourceNameFilter(String selectedResourceNameFilter) {
        this.selectedResourceNameFilter = selectedResourceNameFilter == null ? "" : selectedResourceNameFilter;
        return this;
    }

    public List<String> getResourceNamesFilter() {
        return resourceNamesFilter;
    }

    public RolesSecurityFilter setResourceNamesFilter(List<String> resourceNamesFilter) {
        this.resourceNamesFilter = resourceNamesFilter;
        return this;
    }

    public String getCreationTime() {
        return creationTime;
    }

    public RolesSecurityFilter setCreationTime(String creationTime) {
        this.creationTime = creationTime == null ? "" : creationTime;
        return this;
    }

    public List<String> getNamespacesFilter() {
        return namespacesFilter;
    }

    public RolesSecurityFilter setNamespacesFilter(List<String> namespacesFilter) {
        this.namespacesFilter = namespacesFilter;
        return this;
    }

    public List<String> getResourceTypesFilter() {
        return resourceTypesFilter;
    }

    public RolesSecurityFilter setResourceTypesFilter(List<String> resourceTypesFilter) {
        this.resourceTypesFilter = resourceTypesFilter;
        return this;
    }

    public String getSelectedNamespaceFilter() {
        return selectedNamespaceFilter;
    }

    public RolesSecurityFilter setSelectedNamespaceFilter(String selectedNamespaceFilter) {
        this.selectedNamespaceFilter = selectedNamespaceFilter == null ? "" : selectedNamespaceFilter;
        return this;
    }

    public String getSelectedResourceTypeFilter() {
        return selectedResourceTypeFilter;
    }

    public RolesSecurityFilter setSelectedResourceTypeFilter(String selectedResourceTypeFilter) {
        this.selectedResourceTypeFilter = selectedResourceTypeFilter == null ? "" : selectedResourceTypeFilter;
        return this;
    }
}
