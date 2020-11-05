package com.kubehelper.domain.filters;

import org.apache.commons.lang3.StringUtils;

/**
 * @author JDev
 */
public class IpsAndPortsFilter {
    private String namespace = "", resourceType = "", resourceName = "", creationTime = "", ip = "", hostInfo = "", ports = "", additionalInfo = "";

    public String getNamespace() {
        return namespace;
    }

    public IpsAndPortsFilter setNamespace(String namespace) {
        this.namespace = namespace == null ? "" : namespace.trim();
        return this;
    }

    public String getResourceType() {
        return resourceType;
    }

    public IpsAndPortsFilter setResourceType(String resourceType) {
        this.resourceType = resourceType == null ? "" : resourceType.trim();
        return this;
    }

    public String getResourceName() {
        return resourceName;
    }

    public IpsAndPortsFilter setResourceName(String resourceName) {
        this.resourceName = resourceName == null ? "" : resourceName.trim();
        return this;
    }

    public String getCreationTime() {
        return creationTime;
    }

    public IpsAndPortsFilter setCreationTime(String creationTime) {
        this.creationTime = creationTime == null ? "" : creationTime.trim();
        return this;
    }

    public String getIp() {
        return ip;
    }

    public IpsAndPortsFilter setIp(String ip) {
        this.ip = ip == null ? "" : ip.trim();
        return this;
    }

    public String getHostInfo() {
        return hostInfo;
    }

    public IpsAndPortsFilter setHostInfo(String hostInfo) {
        this.hostInfo = hostInfo;
        return this;
    }

    public String getPorts() {
        return ports;
    }

    public IpsAndPortsFilter setPorts(String ports) {
        this.ports = ports == null ? "" : ports.trim();
        return this;
    }

    public String getAdditionalInfo() {
        return additionalInfo;
    }

    public IpsAndPortsFilter setAdditionalInfo(String additionalInfo) {
        this.additionalInfo = additionalInfo;
        return this;
    }

    public boolean isFilterActive() {
        return StringUtils.isNoneBlank(namespace, resourceType, resourceName, creationTime, ip, hostInfo, ports, additionalInfo);
    }
}
