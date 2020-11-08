package com.kubehelper.domain.results;

import com.kubehelper.common.Resource;

/**
 * @author JDev
 */
public class EventResult {
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

    public EventResult() {
    }

    public EventResult(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public EventResult setId(int id) {
        this.id = id;
        return this;
    }

    public String getNamespace() {
        return namespace;
    }

    public EventResult setNamespace(String namespace) {
        this.namespace = namespace;
        return this;
    }

    public String getResourceType() {
        return Resource.getValueByKey(resourceType.name());
    }

    public EventResult setResourceType(Resource resourceType) {
        this.resourceType = resourceType;
        return this;
    }

    public String getResourceName() {
        return resourceName;
    }

    public EventResult setResourceName(String resourceName) {
        this.resourceName = resourceName;
        return this;
    }

    public String getCreationTime() {
        return creationTime;
    }

    public EventResult setCreationTime(String creationTime) {
        this.creationTime = creationTime;
        return this;
    }

    public String getAdditionalInfo() {
        return additionalInfo;
    }

    public EventResult setAdditionalInfo(String additionalInfo) {
        this.additionalInfo = additionalInfo;
        return this;
    }

    public String getIp() {
        return ip;
    }

    public EventResult setIp(String ip) {
        this.ip = ip;
        return this;
    }

    public String getHostInfo() {
        return hostInfo;
    }

    public EventResult setHostInfo(String hostInfo) {
        this.hostInfo = hostInfo;
        return this;
    }

    public String getPorts() {
        return ports;
    }

    public EventResult setPorts(String ports) {
        this.ports = ports;
        return this;
    }

    public String getDetails() {
        return details;
    }

    public EventResult setDetails(String details) {
        this.details = details;
        return this;
    }
}