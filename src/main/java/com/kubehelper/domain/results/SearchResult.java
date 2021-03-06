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

import java.util.StringJoiner;

/**
 * @author JDev
 */
public class SearchResult {

    private int id;
    private String namespace = "";
    private Resource resourceType;
    private String resourceName = "";
    private String creationTime = "";
    private String additionalInfo = "";
    private String foundString = "";
    private String fullDefinition = "";


    public SearchResult(int id) {
        this.id = id;
    }

    public String getNamespace() {
        return namespace;
    }

    public SearchResult setNamespace(String namespace) {
        this.namespace = namespace;
        return this;
    }

    public String getResourceType() {
        return resourceType.getKind();
    }

    public SearchResult setResourceType(Resource resourceType) {
        this.resourceType = resourceType;
        return this;
    }

    public Resource getRawResourceType() {
        return resourceType;
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

    public int getId() {
        return id;
    }

    public String getFullDefinition() {
        return fullDefinition;
    }

    public SearchResult setFullDefinition(String fullDefinition) {
        this.fullDefinition = fullDefinition;
        return this;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", SearchResult.class.getSimpleName() + "[", "]")
                .add("id=" + id)
                .add("namespace='" + namespace + "'")
                .add("resourceType=" + resourceType)
                .add("resourceName='" + resourceName + "'")
                .add("creationTime='" + creationTime + "'")
                .add("additionalInfo='" + additionalInfo + "'")
                .add("foundString='" + foundString + "'")
                .add("fullDefinition='" + fullDefinition + "'")
                .toString();
    }
}