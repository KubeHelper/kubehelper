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
package com.kubehelper.domain.results;

import com.kubehelper.common.Resource;

/**
 * @author JDev
 */
public class IpsAndPortsResult{
    private int id;
    private String namespace = "";
    private Resource resourceType;
    private String resourceName = "";
    private String creationTime = "";
    private String ip = "";
    private String ports = "";
    private String hostInfo = "";
    private String additionalInfo = "";
    private String details = "";

    public IpsAndPortsResult() {
    }

    public IpsAndPortsResult(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public IpsAndPortsResult setId(int id) {
        this.id = id;
        return this;
    }

    public String getNamespace() {
        return namespace;
    }

    public IpsAndPortsResult setNamespace(String namespace) {
        this.namespace = namespace;
        return this;
    }

    public String getResourceType() {
        return Resource.getValueByKey(resourceType.name());
    }

    public IpsAndPortsResult setResourceType(Resource resourceType) {
        this.resourceType = resourceType;
        return this;
    }

    public String getResourceName() {
        return resourceName;
    }

    public IpsAndPortsResult setResourceName(String resourceName) {
        this.resourceName = resourceName;
        return this;
    }

    public String getCreationTime() {
        return creationTime;
    }

    public IpsAndPortsResult setCreationTime(String creationTime) {
        this.creationTime = creationTime;
        return this;
    }

    public String getAdditionalInfo() {
        return additionalInfo;
    }

    public IpsAndPortsResult setAdditionalInfo(String additionalInfo) {
        this.additionalInfo = additionalInfo;
        return this;
    }

    public String getIp() {
        return ip;
    }

    public IpsAndPortsResult setIp(String ip) {
        this.ip = ip;
        return this;
    }

    public String getHostInfo() {
        return hostInfo;
    }

    public IpsAndPortsResult setHostInfo(String hostInfo) {
        this.hostInfo = hostInfo;
        return this;
    }

    public String getPorts() {
        return ports;
    }

    public IpsAndPortsResult setPorts(String ports) {
        this.ports = ports;
        return this;
    }

    public String getDetails() {
        return details;
    }

    public IpsAndPortsResult setDetails(String details) {
        this.details = details;
        return this;
    }
}