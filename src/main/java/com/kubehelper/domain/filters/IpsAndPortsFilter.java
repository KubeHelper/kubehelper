package com.kubehelper.domain.filters;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

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
        return !StringUtils.isAllBlank(namespace, resourceType, resourceName, creationTime, ip, hostInfo, ports, additionalInfo);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        IpsAndPortsFilter that = (IpsAndPortsFilter) o;

        return new EqualsBuilder()
                .append(namespace, that.namespace)
                .append(resourceType, that.resourceType)
                .append(resourceName, that.resourceName)
                .append(creationTime, that.creationTime)
                .append(ip, that.ip)
                .append(hostInfo, that.hostInfo)
                .append(ports, that.ports)
                .append(additionalInfo, that.additionalInfo)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(namespace)
                .append(resourceType)
                .append(resourceName)
                .append(creationTime)
                .append(ip)
                .append(hostInfo)
                .append(ports)
                .append(additionalInfo)
                .toHashCode();
    }
}
