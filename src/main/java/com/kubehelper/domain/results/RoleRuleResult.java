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
public class RoleRuleResult {
    private int id;
    private List<String> apiGroups = new ArrayList<>();
    private List<String> nonResourceURLs = new ArrayList<>();
    private List<String> resourceNames = new ArrayList<>();
    private List<String> resources = new ArrayList<>();
    private List<String> verbs = new ArrayList<>();
    private String fullDefinition = "";

    //Generate a simple panel with 4 columns and 2 rows in one row create, read, update and delete and below ist verbs which belongs to this CRUD will be shown if rule is selected
    private boolean create;
    private boolean read;
    private boolean update;
    private boolean delete;

    public RoleRuleResult() {
    }

    public RoleRuleResult(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public RoleRuleResult setId(int id) {
        this.id = id;
        return this;
    }

    public List<String> getApiGroups() {
        return apiGroups;
    }

    public RoleRuleResult setApiGroups(List<String> apiGroups) {
        this.apiGroups = apiGroups;
        return this;
    }

    public List<String> getNonResourceURLs() {
        return nonResourceURLs;
    }

    public RoleRuleResult setNonResourceURLs(List<String> nonResourceURLs) {
        this.nonResourceURLs = nonResourceURLs;
        return this;
    }

    public List<String> getResourceNames() {
        return resourceNames;
    }

    public RoleRuleResult setResourceNames(List<String> resourceNames) {
        this.resourceNames = resourceNames;
        return this;
    }

    public List<String> getResources() {
        return resources;
    }

    public RoleRuleResult setResources(List<String> resources) {
        this.resources = resources;
        return this;
    }

    public List<String> getVerbs() {
        return verbs;
    }

    public RoleRuleResult setVerbs(List<String> verbs) {
        this.verbs = verbs;
        return this;
    }

    public String getFullDefinition() {
        return fullDefinition;
    }

    public RoleRuleResult setFullDefinition(String fullDefinition) {
        this.fullDefinition = fullDefinition;
        return this;
    }
}