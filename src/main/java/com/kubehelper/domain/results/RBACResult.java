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

/**
 * @author JDev
 */
public class RBACResult {
    private int id;
    private String resourceName = "";
    private String subjectKind = "";
    private String subjectName = "";
    private String roleName = "";
    private Resource resourceType;
    private String namespace = "";
    private String apiGroup = "";
    //    private String resourceNames = "";
    private boolean all;
    private boolean get;
    private boolean list;
    private boolean create;
    private boolean update;
    private boolean patch;
    private boolean watch;
    private boolean delete;
    private boolean deletecollection;
    private boolean impersonate;
    private boolean escalate;
    private boolean approve;
    private boolean sign;
    private boolean proxy;
    private boolean use;
    private boolean bind;
    private String others = "";

//other verbs: impersonate,escalate approve,sign proxy use bind

    public RBACResult() {
    }

    public RBACResult(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public RBACResult setVerb(String verb) {

        switch (verb) {
            case "*" -> all = true;
            case "get" -> get = true;
            case "list" -> list = true;
            case "create" -> create = true;
            case "update" -> update = true;
            case "patch" -> patch = true;
            case "watch" -> watch = true;
            case "delete" -> delete = true;
            case "deletecollection" -> deletecollection = true;
            default -> others = verb;
        }
        return this;
    }

    public String getResourceName() {
        return resourceName;
    }

    public RBACResult setResourceName(String resourceName) {
        this.resourceName = resourceName;
        return this;
    }

    public String getSubjectKind() {
        return subjectKind;
    }

    public RBACResult setSubjectKind(String subjectKind) {
        this.subjectKind = subjectKind;
        return this;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public RBACResult setSubjectName(String subjectName) {
        this.subjectName = subjectName;
        return this;
    }

    public String getNamespace() {
        return namespace;
    }

    public RBACResult setNamespace(String namespace) {
        this.namespace = namespace;
        return this;
    }

    public String getResourceType() {
        return Resource.getValueByKey(resourceType.name());
    }

    public RBACResult setResourceType(Resource resourceType) {
        this.resourceType = resourceType;
        return this;
    }

    public String getApiGroup() {
        return apiGroup;
    }

    public RBACResult setApiGroup(String apiGroup) {
        this.apiGroup = apiGroup;
        return this;
    }

    public String getRoleName() {
        return roleName;
    }

    public RBACResult setRoleName(String roleName) {
        this.roleName = roleName;
        return this;
    }

    public boolean isAll() {
        return all;
    }

    public RBACResult setAll(boolean all) {
        this.all = all;
        return this;
    }

    public boolean isGet() {
        return get;
    }

    public RBACResult setGet(boolean get) {
        this.get = get;
        return this;
    }

    public boolean isList() {
        return list;
    }

    public RBACResult setList(boolean list) {
        this.list = list;
        return this;
    }

    public boolean isCreate() {
        return create;
    }

    public RBACResult setCreate(boolean create) {
        this.create = create;
        return this;
    }

    public boolean isUpdate() {
        return update;
    }

    public RBACResult setUpdate(boolean update) {
        this.update = update;
        return this;
    }

    public boolean isPatch() {
        return patch;
    }

    public RBACResult setPatch(boolean patch) {
        this.patch = patch;
        return this;
    }

    public boolean isWatch() {
        return watch;
    }

    public RBACResult setWatch(boolean watch) {
        this.watch = watch;
        return this;
    }

    public boolean isDelete() {
        return delete;
    }

    public RBACResult setDelete(boolean delete) {
        this.delete = delete;
        return this;
    }

    public boolean isDeletecollection() {
        return deletecollection;
    }

    public RBACResult setDeletecollection(boolean deletecollection) {
        this.deletecollection = deletecollection;
        return this;
    }

    public boolean isImpersonate() {
        return impersonate;
    }

    public RBACResult setImpersonate(boolean impersonate) {
        this.impersonate = impersonate;
        return this;
    }

    public boolean isEscalate() {
        return escalate;
    }

    public RBACResult setEscalate(boolean escalate) {
        this.escalate = escalate;
        return this;
    }

    public boolean isApprove() {
        return approve;
    }

    public RBACResult setApprove(boolean approve) {
        this.approve = approve;
        return this;
    }

    public boolean isSign() {
        return sign;
    }

    public RBACResult setSign(boolean sign) {
        this.sign = sign;
        return this;
    }

    public boolean isProxy() {
        return proxy;
    }

    public RBACResult setProxy(boolean proxy) {
        this.proxy = proxy;
        return this;
    }

    public boolean isUse() {
        return use;
    }

    public RBACResult setUse(boolean use) {
        this.use = use;
        return this;
    }

    public boolean isBind() {
        return bind;
    }

    public RBACResult setBind(boolean bind) {
        this.bind = bind;
        return this;
    }

    public String getOthers() {
        return others;
    }

    public RBACResult setOthers(String others) {
        this.others = others;
        return this;
    }

}