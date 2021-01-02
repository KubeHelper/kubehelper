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
 * Filter for Ips and Ports View Model.
 *
 * @author JDev
 */
public class IpsAndPortsFilter {
    private String resourceName = "", creationTime = "", ip = "", hostInfo = "", ports = "", additionalInfo = "";

    private String selectedNamespaceFilter = "";
    private String selectedResourceTypeFilter = "";

    private ListModelList<String> namespacesFilter = new ListModelList<>();
    private ListModelList<String> resourceTypesFilter = new ListModelList<>();

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

    public String getSelectedNamespaceFilter() {
        return selectedNamespaceFilter;
    }

    public IpsAndPortsFilter setSelectedNamespaceFilter(String selectedNamespaceFilter) {
        this.selectedNamespaceFilter =StringUtils.isBlank(selectedNamespaceFilter) ? "" : selectedNamespaceFilter;
        return this;
    }

    public String getSelectedResourceTypeFilter() {
        return selectedResourceTypeFilter;
    }

    public IpsAndPortsFilter setSelectedResourceTypeFilter(String selectedResourceTypeFilter) {
        this.selectedResourceTypeFilter = StringUtils.isBlank(selectedResourceTypeFilter) ? "" : selectedResourceTypeFilter;
        return this;
    }

    public String getResourceName() {
        return resourceName;
    }

    public IpsAndPortsFilter setResourceName(String resourceName) {
        this.resourceName = StringUtils.isBlank(resourceName) ? "" : resourceName.trim();
        return this;
    }

    public String getCreationTime() {
        return creationTime;
    }

    public IpsAndPortsFilter setCreationTime(String creationTime) {
        this.creationTime = StringUtils.isBlank(creationTime) ? "" : creationTime.trim();
        return this;
    }

    public String getIp() {
        return ip;
    }

    public IpsAndPortsFilter setIp(String ip) {
        this.ip = StringUtils.isBlank(ip) ? "" : ip.trim();
        return this;
    }

    public String getHostInfo() {
        return hostInfo;
    }

    public IpsAndPortsFilter setHostInfo(String hostInfo) {
        this.hostInfo = StringUtils.isBlank(hostInfo) ? "" : hostInfo.trim();
        return this;
    }

    public String getPorts() {
        return ports;
    }

    public IpsAndPortsFilter setPorts(String ports) {
        this.ports = StringUtils.isBlank(ports) ? "" : ports.trim();
        return this;
    }

    public String getAdditionalInfo() {
        return additionalInfo;
    }

    public IpsAndPortsFilter setAdditionalInfo(String additionalInfo) {
        this.additionalInfo = StringUtils.isBlank(additionalInfo) ? "" : additionalInfo.trim();
        return this;
    }

    public ListModelList<String> getNamespacesFilter() {
        return namespacesFilter;
    }

    public ListModelList<String> getResourceTypesFilter() {
        return resourceTypesFilter;
    }

    public boolean isFilterActive() {
        return StringUtils.isNoneBlank(selectedNamespaceFilter, selectedResourceTypeFilter, resourceName, creationTime, ip, hostInfo, ports, additionalInfo);
    }
}
