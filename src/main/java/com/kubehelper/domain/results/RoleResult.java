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

import com.google.common.collect.Table;
import com.kubehelper.common.Resource;
import org.zkoss.zul.Rows;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author JDev
 */
public class RoleResult {

//1Row(Table) ROLES info
//2Row(List) 1-3 Rows with subjects in separate table
//3Row(Table) Role rules in other table
//4Row(List) click on rule shows CRUD with verbs below crud

    private int id;
    private String resourceName = "";
    private String namespace = "";
    private String creationTime = "";
    private String fullDefinition = "";
    //TODO ClusterRolle or SimpleRole
    private Resource resourceType;
    private List<String> roleSelectors = new ArrayList<>();
//    Subjects from role binding, belongs ro the role, pack into separate row
    private List<RoleBindingSubject> subjects = new ArrayList<>();
    //key is Role Rule id
    private Map<Integer, RoleRuleResult> roleRules = new HashMap<>();

    public RoleResult() {
    }

    public RoleResult(int id) {
        this.id = id;
    }

    public String getNamespace() {
        return namespace;
    }

    public RoleResult setNamespace(String namespace) {
        this.namespace = namespace;
        return this;
    }

    public String getResourceType() {
        return Resource.getValueByKey(resourceType.name());
    }

    public RoleResult setResourceType(Resource resourceType) {
        this.resourceType = resourceType;
        return this;
    }


    public class RoleBindingSubject{
        private String kind;
        private String name;
        private String namespace;
    }
}