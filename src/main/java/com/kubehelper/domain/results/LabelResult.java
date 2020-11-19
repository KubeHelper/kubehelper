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