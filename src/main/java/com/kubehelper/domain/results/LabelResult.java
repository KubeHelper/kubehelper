package com.kubehelper.domain.results;

import com.kubehelper.common.Resource;
import com.kubehelper.common.ResourceProperty;

/**
 * @author JDev
 */
public class LabelResult {
    private int id;
    private String name = "";
    private ResourceProperty resourceProperty;
    private Resource resourceType;
    private String resourceName = "";
    private String namespace = "";
    private String additionalInfo = "";

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

    public String getName() {
        return name;
    }

    public LabelResult setName(String name) {
        this.name = name;
        return this;
    }

    public String getResourceProperty() {
        return ResourceProperty.getValueByKey(resourceProperty.name());
    }

    public LabelResult setResourceProperty(ResourceProperty resourceProperty) {
        this.resourceProperty = resourceProperty;
        return this;
    }

    public int getId() {
        return id;
    }
}