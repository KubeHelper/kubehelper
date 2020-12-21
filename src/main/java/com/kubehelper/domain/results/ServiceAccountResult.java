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

import java.util.ArrayList;
import java.util.List;

/**
 * @author JDev
 */
public class ServiceAccountResult {
    private int id;
    private String resourceName = "";
    private String kind = "";
    private String namespace = "";
    private String secrets = "";
    private String creationTime = "";
    private String fullDefinition = "";

    public ServiceAccountResult() {
    }

    public ServiceAccountResult(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getResourceName() {
        return resourceName;
    }

    public ServiceAccountResult setResourceName(String resourceName) {
        this.resourceName = resourceName;
        return this;
    }

    public String getKind() {
        return kind;
    }

    public ServiceAccountResult setKind(String kind) {
        this.kind = kind;
        return this;
    }

    public String getNamespace() {
        return namespace;
    }

    public ServiceAccountResult setNamespace(String namespace) {
        this.namespace = namespace;
        return this;
    }

    public String getSecrets() {
        return secrets;
    }

    public ServiceAccountResult setSecrets(String secrets) {
        this.secrets = secrets;
        return this;
    }

    public String getCreationTime() {
        return creationTime;
    }

    public ServiceAccountResult setCreationTime(String creationTime) {
        this.creationTime = creationTime;
        return this;
    }

    public String getFullDefinition() {
        return fullDefinition;
    }

    public ServiceAccountResult setFullDefinition(String fullDefinition) {
        this.fullDefinition = fullDefinition;
        return this;
    }
}