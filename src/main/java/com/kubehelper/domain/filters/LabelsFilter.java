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
public class LabelsFilter {

    private String name = "", additionalInfo = "";

    private String selectedNamespaceFilter = "";
    private String selectedResourceTypeFilter = "";
    private String selectedResourcePropertyFilter = "";
    private String selectedResourceNameFilter = "";

    private List<String> namespacesFilter = new ArrayList<>();
    private List<String> resourceTypesFilter = new ArrayList<>();
    private List<String> resourcePropertiesFilter = new ArrayList<>();
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

    public LabelsFilter setName(String name) {
        this.name = name;
        return this;
    }

    public String getSelectedResourcePropertyFilter() {
        return selectedResourcePropertyFilter;
    }

    public LabelsFilter setSelectedResourcePropertyFilter(String selectedResourcePropertyFilter) {
        this.selectedResourcePropertyFilter = selectedResourcePropertyFilter == null ? "" : selectedResourcePropertyFilter;
        return this;
    }

    public List<String> getResourcePropertiesFilter() {
        return resourcePropertiesFilter;
    }

    public LabelsFilter setResourcePropertiesFilter(List<String> resourcePropertiesFilter) {
        this.resourcePropertiesFilter = resourcePropertiesFilter;
        return this;
    }

    public String getAdditionalInfo() {
        return additionalInfo;
    }

    public LabelsFilter setAdditionalInfo(String additionalInfo) {
        this.additionalInfo = additionalInfo == null ? "" : additionalInfo;
        return this;
    }

    public boolean isFilterActive() {
        return StringUtils.isNoneBlank(name, additionalInfo, selectedNamespaceFilter, selectedResourceTypeFilter, selectedResourcePropertyFilter, selectedResourceNameFilter);
    }

    public List<String> getNamespacesFilter() {
        return namespacesFilter;
    }

    public LabelsFilter setNamespacesFilter(List<String> namespacesFilter) {
        this.namespacesFilter = namespacesFilter;
        return this;
    }

    public List<String> getResourceTypesFilter() {
        return resourceTypesFilter;
    }

    public LabelsFilter setResourceTypesFilter(List<String> resourceTypesFilter) {
        this.resourceTypesFilter = resourceTypesFilter;
        return this;
    }

    public List<String> getResourceNamesFilter() {
        return resourceNamesFilter;
    }

    public LabelsFilter setResourceNamesFilter(List<String> resourceNamesFilter) {
        this.resourceNamesFilter = resourceNamesFilter;
        return this;
    }

    public String getSelectedNamespaceFilter() {
        return selectedNamespaceFilter;
    }

    public LabelsFilter setSelectedNamespaceFilter(String selectedNamespaceFilter) {
        this.selectedNamespaceFilter = selectedNamespaceFilter == null ? "" : selectedNamespaceFilter;
        return this;
    }

    public String getSelectedResourceTypeFilter() {
        return selectedResourceTypeFilter;
    }

    public LabelsFilter setSelectedResourceTypeFilter(String selectedResourceTypeFilter) {
        this.selectedResourceTypeFilter = selectedResourceTypeFilter == null ? "" : selectedResourceTypeFilter;
        return this;
    }

    public String getSelectedResourceNameFilter() {
        return selectedResourceNameFilter;
    }

    public LabelsFilter setSelectedResourceNameFilter(String selectedResourceNameFilter) {
        this.selectedResourceNameFilter = selectedResourceNameFilter == null ? "" : selectedResourceNameFilter;
        return this;
    }
}
