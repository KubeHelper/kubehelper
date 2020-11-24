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
import org.zkoss.zul.ListModelList;

/**
 * @author JDev
 */
public class RoleRulesSecurityFilter {
    private String name = "", additionalInfo = "";

    private String selectedNamespaceFilter = "";
    private String selectedResourceTypeFilter = "";
    private String selectedResourcePropertyFilter = "";
    private String selectedResourceNameFilter = "";

    private ListModelList<String> namespacesFilter = new ListModelList<>();
    private ListModelList<String> resourceTypesFilter = new ListModelList<>();
    private ListModelList<String> resourcePropertiesFilter = new ListModelList<>();
    private ListModelList<String> resourceNamesFilter = new ListModelList<>();

    public RoleRulesSecurityFilter() {
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
        if (StringUtils.isNotBlank(resourceNameFilter) && !resourceNamesFilter.contains(resourceNameFilter)) {
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

    public RoleRulesSecurityFilter setName(String name) {
        this.name = name;
        return this;
    }

    public String getSelectedResourcePropertyFilter() {
        return selectedResourcePropertyFilter;
    }

    public RoleRulesSecurityFilter setSelectedResourcePropertyFilter(String selectedResourcePropertyFilter) {
        this.selectedResourcePropertyFilter = selectedResourcePropertyFilter == null ? "" : selectedResourcePropertyFilter;
        return this;
    }

    public ListModelList<String> getResourcePropertiesFilter() {
        return resourcePropertiesFilter;
    }

    public RoleRulesSecurityFilter setResourcePropertiesFilter(ListModelList<String> resourcePropertiesFilter) {
        this.resourcePropertiesFilter = resourcePropertiesFilter;
        return this;
    }

    public String getAdditionalInfo() {
        return additionalInfo;
    }

    public RoleRulesSecurityFilter setAdditionalInfo(String additionalInfo) {
        this.additionalInfo = additionalInfo;
        return this;
    }

    public boolean isFilterActive() {
        return StringUtils.isNoneBlank(name, additionalInfo, selectedNamespaceFilter, selectedResourceTypeFilter, selectedResourcePropertyFilter, selectedResourceNameFilter);
    }

    public ListModelList<String> getNamespacesFilter() {
        return namespacesFilter;
    }

    public RoleRulesSecurityFilter setNamespacesFilter(ListModelList<String> namespacesFilter) {
        this.namespacesFilter = namespacesFilter;
        return this;
    }

    public ListModelList<String> getResourceTypesFilter() {
        return resourceTypesFilter;
    }

    public RoleRulesSecurityFilter setResourceTypesFilter(ListModelList<String> resourceTypesFilter) {
        this.resourceTypesFilter = resourceTypesFilter;
        return this;
    }

    public ListModelList<String> getResourceNamesFilter() {
        return resourceNamesFilter;
    }

    public RoleRulesSecurityFilter setResourceNamesFilter(ListModelList<String> resourceNamesFilter) {
        this.resourceNamesFilter = resourceNamesFilter;
        return this;
    }

    public String getSelectedNamespaceFilter() {
        return selectedNamespaceFilter;
    }

    public RoleRulesSecurityFilter setSelectedNamespaceFilter(String selectedNamespaceFilter) {
        this.selectedNamespaceFilter = selectedNamespaceFilter == null ? "" : selectedNamespaceFilter;
        return this;
    }

    public String getSelectedResourceTypeFilter() {
        return selectedResourceTypeFilter;
    }

    public RoleRulesSecurityFilter setSelectedResourceTypeFilter(String selectedResourceTypeFilter) {
        this.selectedResourceTypeFilter = selectedResourceTypeFilter == null ? "" : selectedResourceTypeFilter;
        return this;
    }

    public String getSelectedResourceNameFilter() {
        return selectedResourceNameFilter;
    }

    public RoleRulesSecurityFilter setSelectedResourceNameFilter(String selectedResourceNameFilter) {
        this.selectedResourceNameFilter = selectedResourceNameFilter == null ? "" : selectedResourceNameFilter;
        return this;
    }
}
