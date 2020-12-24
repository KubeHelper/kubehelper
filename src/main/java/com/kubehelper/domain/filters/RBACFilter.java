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
package com.kubehelper.domain.filters;

import org.apache.commons.lang3.StringUtils;
import org.zkoss.zul.ListModelList;

import java.util.Arrays;
import java.util.List;

/**
 * @author JDev
 */
public class RBACFilter {
    private String apiGroup = "", others = "";

    private String selectedResourceNameFilter = "";
    private String selectedSubjectKindFilter = "";
    private String selectedSubjectNameFilter = "";
    private String selectedResourceTypeFilter = "";
    private String selectedNamespaceFilter = "";
    private String selectedRoleNameFilter = "";

    private String verbAll = "";
    private String verbGet = "";
    private String verbList = "";
    private String verbCreate = "";
    private String verbUpdate = "";
    private String verbPatch = "";
    private String verbWatch = "";
    private String verbDelete = "";
    private String verbDeleteCollection = "";
    private String verbOthers = "";

    private ListModelList<String> resourceNamesFilter = new ListModelList<>();
    private ListModelList<String> subjectKindsFilter = new ListModelList<>();
    private ListModelList<String> subjectNamesFilter = new ListModelList<>();
    private ListModelList<String> resourceTypesFilter = new ListModelList<>();
    private ListModelList<String> namespacesFilter = new ListModelList<>();
    private ListModelList<String> roleNamesFilter = new ListModelList<>();

    public RBACFilter() {
    }

    public void addResourceNamesFilter(String resourceNameFilter) {
        if (!resourceNamesFilter.contains(resourceNameFilter)) {
            resourceNamesFilter.add(resourceNameFilter);
        }
    }

    public void addSubjectKindsFilter(String subjectKindFilter) {
        if (!subjectKindsFilter.contains(subjectKindFilter)) {
            subjectKindsFilter.add(subjectKindFilter);
        }
    }

    public void addSubjectNamesFilter(String subjectNameFilter) {
        if (!subjectNamesFilter.contains(subjectNameFilter)) {
            subjectNamesFilter.add(subjectNameFilter);
        }
    }


    public void addNamespacesFilter(String namespaceFilter) {
        namespaceFilter = namespaceFilter == null ? "N/A" : namespaceFilter;
        if (!namespacesFilter.contains(namespaceFilter)) {
            namespacesFilter.add(namespaceFilter);
        }
    }

    public void addResourceTypesFilter(String resourceTypeFilter) {
        if (!resourceTypesFilter.contains(resourceTypeFilter)) {
            resourceTypesFilter.add(resourceTypeFilter);
        }
    }

    public void addRoleNamesFilter(String roleName) {
        if (!roleNamesFilter.contains(roleName)) {
            roleNamesFilter.add(roleName);
        }
    }

    public boolean isFilterActive() {
        return StringUtils.isNoneBlank(apiGroup, others, verbAll, verbGet, verbList, verbCreate, verbUpdate, verbPatch, verbWatch, verbDelete, verbDeleteCollection, verbOthers,
                selectedRoleNameFilter, selectedResourceNameFilter, selectedSubjectKindFilter, selectedSubjectNameFilter, selectedResourceTypeFilter, selectedNamespaceFilter);
    }

    public String getApiGroup() {
        return apiGroup;
    }

    public RBACFilter setApiGroup(String apiGroup) {
        this.apiGroup = apiGroup;
        return this;
    }

    public String getOthers() {
        return others;
    }

    public RBACFilter setOthers(String others) {
        this.others = others;
        return this;
    }

    public ListModelList<String> getNamespacesFilter() {
        return namespacesFilter;
    }

    public ListModelList<String> getResourceTypesFilter() {
        return resourceTypesFilter;
    }

    public ListModelList<String> getResourceNamesFilter() {
        return resourceNamesFilter;
    }

    public ListModelList<String> getSubjectKindsFilter() {
        return subjectKindsFilter;
    }

    public ListModelList<String> getSubjectNamesFilter() {
        return subjectNamesFilter;
    }

    public ListModelList<String> getRoleNamesFilter() {
        return roleNamesFilter;
    }

    public String getSelectedNamespaceFilter() {
        return selectedNamespaceFilter;
    }

    public RBACFilter setSelectedNamespaceFilter(String selectedNamespaceFilter) {
        this.selectedNamespaceFilter = selectedNamespaceFilter == null ? "" : selectedNamespaceFilter;
        return this;
    }

    public String getSelectedResourceTypeFilter() {
        return selectedResourceTypeFilter;
    }

    public RBACFilter setSelectedResourceTypeFilter(String selectedResourceTypeFilter) {
        this.selectedResourceTypeFilter = selectedResourceTypeFilter == null ? "" : selectedResourceTypeFilter;
        return this;
    }

    public String getSelectedResourceNameFilter() {
        return selectedResourceNameFilter;
    }

    public RBACFilter setSelectedResourceNameFilter(String selectedResourceNameFilter) {
        this.selectedResourceNameFilter = selectedResourceNameFilter == null ? "" : selectedResourceNameFilter;
        return this;
    }

    public String getSelectedSubjectKindFilter() {
        return selectedSubjectKindFilter;
    }

    public RBACFilter setSelectedSubjectKindFilter(String selectedSubjectKindFilter) {
        this.selectedSubjectKindFilter = selectedSubjectKindFilter == null ? "" : selectedSubjectKindFilter;
        return this;
    }

    public String getSelectedSubjectNameFilter() {
        return selectedSubjectNameFilter;
    }

    public RBACFilter setSelectedSubjectNameFilter(String selectedSubjectNameFilter) {
        this.selectedSubjectNameFilter = selectedSubjectNameFilter == null ? "" : selectedSubjectNameFilter;
        return this;
    }

    public String getSelectedRoleNameFilter() {
        return selectedRoleNameFilter;
    }

    public RBACFilter setSelectedRoleNameFilter(String selectedRoleNameFilter) {
        this.selectedRoleNameFilter = selectedRoleNameFilter == null ? "" : selectedRoleNameFilter;
        return this;
    }

    public String getVerbAll() {
        return verbAll;
    }

    public RBACFilter setVerbAll(String verbAll) {
        this.verbAll = verbAll == null ? "" : verbAll;
        return this;
    }

    public String getVerbGet() {
        return verbGet;
    }

    public RBACFilter setVerbGet(String verbGet) {
        this.verbGet = verbGet == null ? "" : verbGet;
        return this;
    }

    public String getVerbList() {
        return verbList;
    }

    public RBACFilter setVerbList(String verbList) {
        this.verbList = verbList == null ? "" : verbList;
        return this;
    }

    public String getVerbCreate() {
        return verbCreate;
    }

    public RBACFilter setVerbCreate(String verbCreate) {
        this.verbCreate = verbCreate == null ? "" : verbCreate;
        return this;
    }

    public String getVerbUpdate() {
        return verbUpdate;
    }

    public RBACFilter setVerbUpdate(String verbUpdate) {
        this.verbUpdate = verbUpdate == null ? "" : verbUpdate;
        return this;
    }

    public String getVerbPatch() {
        return verbPatch;
    }

    public RBACFilter setVerbPatch(String verbPatch) {
        this.verbPatch = verbPatch == null ? "" : verbPatch;
        return this;
    }

    public String getVerbWatch() {
        return verbWatch;
    }

    public RBACFilter setVerbWatch(String verbWatch) {
        this.verbWatch = verbWatch == null ? "" : verbWatch;
        return this;
    }

    public String getVerbDelete() {
        return verbDelete;
    }

    public RBACFilter setVerbDelete(String verbDelete) {
        this.verbDelete = verbDelete == null ? "" : verbDelete;
        return this;
    }

    public String getVerbDeleteCollection() {
        return verbDeleteCollection;
    }

    public RBACFilter setVerbDeleteCollection(String verbDeleteCollection) {
        this.verbDeleteCollection = verbDeleteCollection == null ? "" : verbDeleteCollection;
        return this;
    }

    public String getVerbOthers() {
        return verbOthers;
    }

    public RBACFilter setVerbOthers(String verbOthers) {
        this.verbOthers = verbOthers == null ? "" : verbOthers;
        return this;
    }

    public ListModelList<String> getVerbsFilter() {
        return new ListModelList<>(List.of("Yes", "No"));
    }
}
