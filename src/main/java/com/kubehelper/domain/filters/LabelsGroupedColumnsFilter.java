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
public class LabelsGroupedColumnsFilter {

    private String name = "", additionalInfo = "";

    private String selectedNamespaceFilter = "";
    private String selectedResourceTypeFilter = "";
    private String selectedResourcePropertyFilter = "";
    private String selectedResourceNameFilter = "";

    private List<String> namespacesFilter = new ArrayList<>();
    private List<String> resourceTypesFilter = new ArrayList<>();
    private List<String> resourcePropertiesFilter = new ArrayList<>();
    private List<String> resourceNamesFilter = new ArrayList<>();


    public LabelsGroupedColumnsFilter() {
    }

    public LabelsGroupedColumnsFilter addNamespacesFilter(String namespaceFilter) {
        if (!namespacesFilter.contains(namespaceFilter)) {
            namespacesFilter.add(namespaceFilter);
        }
        return this;
    }

    public LabelsGroupedColumnsFilter addResourceTypesFilter(String resourceTypeFilter) {
        if (!resourceTypesFilter.contains(resourceTypeFilter)) {
            resourceTypesFilter.add(resourceTypeFilter);
        }
        return this;
    }

    public LabelsGroupedColumnsFilter addResourceNamesFilter(String resourceNameFilter) {
        if (StringUtils.isNotBlank(resourceNameFilter) && !resourceNamesFilter.contains(resourceNameFilter)) {
            resourceNamesFilter.add(resourceNameFilter);
        }
        return this;
    }

    public LabelsGroupedColumnsFilter addResourcePropertiesFilter(String resourcePropertyFilter) {
        if (!resourcePropertiesFilter.contains(resourcePropertyFilter)) {
            resourcePropertiesFilter.add(resourcePropertyFilter);
        }
        return this;
    }

    public boolean isFilterActive() {
        return StringUtils.isNoneBlank(name, additionalInfo, selectedNamespaceFilter, selectedResourceTypeFilter, selectedResourcePropertyFilter, selectedResourceNameFilter);
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

    public List<String> getResourcePropertiesFilter() {
        return resourcePropertiesFilter;
    }

    public LabelsGroupedColumnsFilter setResourcePropertiesFilter(List<String> resourcePropertiesFilter) {
        this.resourcePropertiesFilter = resourcePropertiesFilter;
        return this;
    }

    public String getAdditionalInfo() {
        return additionalInfo;
    }

    public LabelsGroupedColumnsFilter setAdditionalInfo(String additionalInfo) {
        this.additionalInfo = additionalInfo== null ? "" : additionalInfo;
        return this;
    }


    public List<String> getNamespacesFilter() {
        return namespacesFilter;
    }

    public LabelsGroupedColumnsFilter setNamespacesFilter(List<String> namespacesFilter) {
        this.namespacesFilter = namespacesFilter;
        return this;
    }

    public List<String> getResourceTypesFilter() {
        return resourceTypesFilter;
    }

    public LabelsGroupedColumnsFilter setResourceTypesFilter(List<String> resourceTypesFilter) {
        this.resourceTypesFilter = resourceTypesFilter;
        return this;
    }

    public List<String> getResourceNamesFilter() {
        return resourceNamesFilter;
    }

    public LabelsGroupedColumnsFilter setResourceNamesFilter(List<String> resourceNamesFilter) {
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
