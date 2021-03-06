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
import io.kubernetes.client.openapi.models.V1beta1Subject;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

/**
 * @author JDev
 */
public class RoleResult {

    private int id;
    private String resourceName = "";
    private String namespace = "";
    private String creationTime = "";
    private String fullDefinition = "";
    private Resource resourceType;
    private List<RoleBindingSubject> subjects = new ArrayList<>();
    private List<RoleRuleResult> roleRules = new ArrayList<>();


    public RoleResult(int id) {
        this.id = id;
    }

    public void addRoleRules(List<RoleRuleResult> rules) {
        roleRules = rules;
    }

    public void addRoleSubjects(List<V1beta1Subject> kubeSubjects) {
        if (kubeSubjects != null) {
            kubeSubjects.forEach(s -> subjects.add(convertSubject(s)));
        }
    }

    public RoleBindingSubject convertSubject(V1beta1Subject subject) {
        RoleBindingSubject roleBindingSubject = new RoleBindingSubject();
        roleBindingSubject.kind = subject.getKind();
        roleBindingSubject.name = subject.getName();
        roleBindingSubject.namespace = subject.getNamespace();
        return roleBindingSubject;
    }

    public int getId() {
        return id;
    }

    public String getResourceName() {
        return resourceName;
    }

    public RoleResult setResourceName(String resourceName) {
        this.resourceName = resourceName;
        return this;
    }

    public String getCreationTime() {
        return creationTime;
    }

    public RoleResult setCreationTime(String creationTime) {
        this.creationTime = creationTime;
        return this;
    }

    public String getNamespace() {
        return namespace;
    }

    public RoleResult setNamespace(String namespace) {
        this.namespace = namespace;
        return this;
    }

    public String getResourceType() {
        return resourceType.getKind();
    }

    public Resource getRawResourceType() {
        return resourceType;
    }

    public RoleResult setResourceType(Resource resourceType) {
        this.resourceType = resourceType;
        return this;
    }

    public String getFullDefinition() {
        return fullDefinition;
    }

    public RoleResult setFullDefinition(String fullDefinition) {
        this.fullDefinition = fullDefinition;
        return this;
    }

    public List<RoleRuleResult> getRoleRules() {
        return roleRules;
    }

    public RoleResult setRoleRules(List<RoleRuleResult> roleRules) {
        this.roleRules = roleRules;
        return this;
    }

    public List<RoleBindingSubject> getSubjects() {
        return subjects;
    }

    public RoleResult setSubjects(List<RoleBindingSubject> subjects) {
        this.subjects = subjects;
        return this;
    }

    public class RoleBindingSubject {
        private String kind = "";
        private String name = "";
        private String namespace = "";

        public String getKind() {
            return kind;
        }

        public String getName() {
            return name;
        }

        public String getNamespace() {
            return namespace;
        }

        @Override
        public String toString() {
            return new StringJoiner(", ", RoleBindingSubject.class.getSimpleName() + "[", "]")
                    .add("kind='" + kind + "'")
                    .add("name='" + name + "'")
                    .add("namespace='" + namespace + "'")
                    .toString();
        }
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", RoleResult.class.getSimpleName() + "[", "]")
                .add("id=" + id)
                .add("resourceName='" + resourceName + "'")
                .add("namespace='" + namespace + "'")
                .add("creationTime='" + creationTime + "'")
                .add("fullDefinition='" + fullDefinition + "'")
                .add("resourceType=" + resourceType)
                .add("subjects=" + subjects)
                .add("roleRules=" + roleRules)
                .toString();
    }
}