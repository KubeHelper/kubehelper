package com.kubehelper.model;

import com.kubehelper.common.Resource;

/**
 * @author JDev
 */
public class SearchResult implements Comparable {
    private String namespace = "";
    private Resource resourceType;
    private String resourceName = "";
    private String creationTime = "";
    private String additionalInfo = "";
    private String foundString = "";

    public String getNamespace() {
        return namespace;
    }

    public SearchResult setNamespace(String namespace) {
        this.namespace = namespace;
        return this;
    }

    public String getResourceType() {
        return Resource.getValueByKey(resourceType.name());
    }

    public SearchResult setResourceType(Resource resourceType) {
        this.resourceType = resourceType;
        return this;
    }

    public String getResourceName() {
        return resourceName;
    }

    public SearchResult setResourceName(String resourceName) {
        this.resourceName = resourceName;
        return this;
    }

    public String getCreationTime() {
        return creationTime;
    }

    public SearchResult setCreationTime(String creationTime) {
        this.creationTime = creationTime;
        return this;
    }

    public String getAdditionalInfo() {
        return additionalInfo;
    }

    public SearchResult setAdditionalInfo(String additionalInfo) {
        this.additionalInfo = additionalInfo;
        return this;
    }

    public String getFoundString() {
        return foundString;
    }

    public SearchResult setFoundString(String foundString) {
        this.foundString = foundString;
        return this;
    }

    @Override
    public int compareTo(Object o) {
//        return this.compareTo(o);
//        return Comparator.comparing(Person::getFirstName)
//                .thenComparing(Person::getLastName)
//                .thenComparingInt(Person::getAge)
//                .compare(this, o);
        return 0;
    }
}