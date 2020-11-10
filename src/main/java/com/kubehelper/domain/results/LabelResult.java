package com.kubehelper.domain.results;

import com.kubehelper.common.Resource;

/**
 * @author JDev
 */
public class LabelResult {
    private int id;
    private String namespace = "";
    private Resource resourceType;
    private String resourceName = "";
    private String additionalInfo = "";
    private String foundString = "";

    public LabelResult() {
    }

    public LabelResult(int id) {
        this.id = id;
    }

    public String getNamespace() {
        return namespace;
    }

    public LabelResult setNamespace(String namespace) {
        this.namespace = namespace;
        return this;
    }

    public String getResourceType() {
        return Resource.getValueByKey(resourceType.name());
    }

    public LabelResult setResourceType(Resource resourceType) {
        this.resourceType = resourceType;
        return this;
    }

    public String getResourceName() {
        return resourceName;
    }

    public LabelResult setResourceName(String resourceName) {
        this.resourceName = resourceName;
        return this;
    }

    public String getAdditionalInfo() {
        return additionalInfo;
    }

    public LabelResult setAdditionalInfo(String additionalInfo) {
        this.additionalInfo = additionalInfo;
        return this;
    }

    public String getFoundString() {
        return foundString;
    }

    public LabelResult setFoundString(String foundString) {
        this.foundString = foundString;
        return this;
    }

    public int getId() {
        return id;
    }
}