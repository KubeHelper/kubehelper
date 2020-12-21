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
package com.kubehelper.domain.models;

import com.kubehelper.common.Global;
import com.kubehelper.common.KubeHelperException;
import com.kubehelper.common.Resource;
import com.kubehelper.domain.filters.RBACFilter;
import com.kubehelper.domain.filters.RolesSecurityFilter;
import com.kubehelper.domain.results.ContainerSecurityResult;
import com.kubehelper.domain.results.PodSecurityContextResult;
import com.kubehelper.domain.results.PodSecurityPoliciesResult;
import com.kubehelper.domain.results.RBACResult;
import com.kubehelper.domain.results.RoleResult;
import com.kubehelper.domain.results.ServiceAccountResult;
import io.kubernetes.client.openapi.models.V1beta1Subject;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author JDev
 */
public class SecurityModel implements PageModel {

    private int mainGridHeight = 600;
    private PropertyChangeSupport grid = new PropertyChangeSupport(this);

    private String templateUrl = "~./zul/pages/security.zul";
    public static String NAME = Global.SECURITY_MODEL;

    private String selectedRolesNamespace = "all";
    private String selectedRBACsNamespace = "all";
    private String selectedPodsSecurityContextsNamespace = "all";
    private String selectedContainersSecurityNamespace = "all";
    private String selectedServiceAccountsNamespace = "all";
    private String selectedPodSecurityPoliciesNamespace = "all";

    private List<String> namespaces = new ArrayList<>();
    private Map<Integer, RoleResult> rolesResults = new HashMap<>();
    private List<PodSecurityContextResult> podsSecurityContextResults = new ArrayList<>();
    private List<RBACResult> rbacsResults = new ArrayList<>();
    private List<ContainerSecurityResult> containersSecurityResults = new ArrayList<>();
    private List<ServiceAccountResult> serviceAccountsResults = new ArrayList<>();
    private List<PodSecurityPoliciesResult> podSecurityPoliciesResults = new ArrayList<>();
    //key is RoleResult id
    private RolesSecurityFilter rolesFilter = new RolesSecurityFilter();
    private RBACFilter rbacsFilter = new RBACFilter();
    private List<KubeHelperException> searchExceptions = new ArrayList<>();
    private boolean skipKubeNamespaces = true;

    public SecurityModel() {
    }

    @Override
    public void setPageMainContentHeight(int newHeight) {
        int oldMainGridHeight = this.mainGridHeight;
        this.mainGridHeight = newHeight;
        grid.firePropertyChange(null, oldMainGridHeight, newHeight);
    }

    public int getMainGridHeight() {
        return mainGridHeight;
    }

    public void addPropertyChangeListener(PropertyChangeListener pcl) {
        grid.addPropertyChangeListener(pcl);
    }

    public SecurityModel addRoleResult(RoleResult roleResult) {
        rolesResults.put(roleResult.getId(), roleResult);
        rolesFilter.addResourceNamesFilter(roleResult.getResourceName());
        rolesFilter.addNamespacesFilter(roleResult.getNamespace());
        rolesFilter.addResourceTypesFilter(roleResult.getResourceType());
        return this;
    }

    public SecurityModel addRBACResult(RBACResult rbacResult) {
        rbacsResults.add(rbacResult);
        rbacsFilter.addResourceNamesFilter(rbacResult.getResourceName());
        rbacsFilter.addSubjectKindsFilter(rbacResult.getSubjectKind());
        rbacsFilter.addSubjectNamesFilter(rbacResult.getSubjectName());
        rbacsFilter.addResourceTypesFilter(rbacResult.getResourceType());
        rbacsFilter.addNamespacesFilter(rbacResult.getNamespace());
        rbacsFilter.addRoleNamesFilter(rbacResult.getRoleName());
        return this;
    }

    public SecurityModel addPodSecurityContext(PodSecurityContextResult result) {
        podsSecurityContextResults.add(result);
        return this;
    }

    public SecurityModel addContainerSecurityResult(ContainerSecurityResult containerResult) {
        containersSecurityResults.add(containerResult);
        return this;
    }

    public SecurityModel addServiceAccountResult(ServiceAccountResult serviceAccountResult) {
        serviceAccountsResults.add(serviceAccountResult);
        return this;
    }

    public SecurityModel addPodSecurityPolicy(PodSecurityPoliciesResult result) {
        podSecurityPoliciesResults.add(result);
        return this;
    }


    public void addRoleSubjects(String roleName, Resource resource, List<V1beta1Subject> subjects) {
        Optional<RoleResult> role = findRole(roleName, resource);
        if (role.isPresent()) {
            role.get().addRoleSubjects(subjects);
        }
    }

    private Optional<RoleResult> findRole(String name, Resource resource) {
        return rolesResults.values().stream().filter(item -> item.getResourceName().equals(name) && item.getResourceType().equals(resource.getValue())).findFirst();
    }

    public void addSearchException(Exception exception) {
        this.searchExceptions.add(new KubeHelperException(exception));
    }

    public void addSearchException(String message, Exception exception) {
        this.searchExceptions.add(new KubeHelperException(message, exception));
    }

    public SecurityModel setSearchExceptions(List<KubeHelperException> searchExceptions) {
        this.searchExceptions = searchExceptions;
        return this;
    }


    public List<KubeHelperException> getSearchExceptions() {
        return searchExceptions;
    }

    @Override
    public String getTemplateUrl() {
        return templateUrl;
    }

    @Override
    public String getName() {
        return NAME;
    }


    public boolean hasSearchErrors() {
        return !searchExceptions.isEmpty();
    }

    public String getSelectedRolesNamespace() {
        return selectedRolesNamespace;
    }

    public SecurityModel setSelectedRolesNamespace(String selectedRolesNamespace) {
        this.selectedRolesNamespace = selectedRolesNamespace;
        return this;
    }

    public String getSelectedPodsSecurityContextsNamespace() {
        return selectedPodsSecurityContextsNamespace;
    }

    public SecurityModel setSelectedPodsSecurityContextsNamespace(String selectedPodsSecurityContextsNamespace) {
        this.selectedPodsSecurityContextsNamespace = selectedPodsSecurityContextsNamespace;
        return this;
    }

    public String getSelectedContainersSecurityNamespace() {
        return selectedContainersSecurityNamespace;
    }

    public SecurityModel setSelectedContainersSecurityNamespace(String selectedContainersSecurityNamespace) {
        this.selectedContainersSecurityNamespace = selectedContainersSecurityNamespace;
        return this;
    }

    public String getSelectedServiceAccountsNamespace() {
        return selectedServiceAccountsNamespace;
    }

    public SecurityModel setSelectedServiceAccountsNamespace(String selectedServiceAccountsNamespace) {
        this.selectedServiceAccountsNamespace = selectedServiceAccountsNamespace;
        return this;
    }

    public String getSelectedPodSecurityPoliciesNamespace() {
        return selectedPodSecurityPoliciesNamespace;
    }

    public SecurityModel setSelectedPodSecurityPoliciesNamespace(String selectedPodSecurityPoliciesNamespace) {
        this.selectedPodSecurityPoliciesNamespace = selectedPodSecurityPoliciesNamespace;
        return this;
    }

    public String getSelectedRBACsNamespace() {
        return selectedRBACsNamespace;
    }

    public SecurityModel setSelectedRBACsNamespace(String selectedRBACsNamespace) {
        this.selectedRBACsNamespace = selectedRBACsNamespace;
        return this;
    }

    public RolesSecurityFilter getRolesFilter() {
        return rolesFilter;
    }


    public SecurityModel setRolesFilter(RolesSecurityFilter rolesFilter) {
        this.rolesFilter = rolesFilter;
        return this;
    }


    public Map<Integer, RoleResult> getRolesResults() {
        return rolesResults;
    }

    public List<RoleResult> getRolesResultsList() {
        return new ArrayList<>(rolesResults.values());
    }

    public SecurityModel setRolesResults(Map<Integer, RoleResult> rolesResults) {
        this.rolesResults = rolesResults;
        return this;
    }

    public List<RBACResult> getRbacsResults() {
        return rbacsResults;
    }

    public SecurityModel setRbacsResults(List<RBACResult> rbacsResults) {
        this.rbacsResults = rbacsResults;
        return this;
    }

    public RBACFilter getRbacsFilter() {
        return rbacsFilter;
    }

    public SecurityModel setRbacsFilter(RBACFilter rbacsFilter) {
        this.rbacsFilter = rbacsFilter;
        return this;
    }

    public List<PodSecurityContextResult> getPodsSecurityContextsResults() {
        return podsSecurityContextResults;
    }

    public SecurityModel setPodsSecurityContextResults(List<PodSecurityContextResult> podsSecurityContextResults) {
        this.podsSecurityContextResults = podsSecurityContextResults;
        return this;
    }


    public List<ContainerSecurityResult> getContainersSecurityResults() {
        return containersSecurityResults;
    }

    public SecurityModel setContainersSecurityResults(List<ContainerSecurityResult> containersSecurityResults) {
        this.containersSecurityResults = containersSecurityResults;
        return this;
    }

    public List<ServiceAccountResult> getServiceAccountsResults() {
        return serviceAccountsResults;
    }

    public SecurityModel setServiceAccountsResults(List<ServiceAccountResult> serviceAccountsResults) {
        this.serviceAccountsResults = serviceAccountsResults;
        return this;
    }

    public List<PodSecurityPoliciesResult> getPodSecurityPoliciesResults() {
        return podSecurityPoliciesResults;
    }

    public SecurityModel setPodSecurityPoliciesResults(List<PodSecurityPoliciesResult> podSecurityPoliciesResults) {
        this.podSecurityPoliciesResults = podSecurityPoliciesResults;
        return this;
    }

    public List<String> getNamespaces() {
        return namespaces;
    }

    public SecurityModel setNamespaces(List<String> namespaces) {
        this.namespaces = namespaces;
        return this;
    }

    public boolean isSkipKubeNamespaces() {
        return skipKubeNamespaces;
    }

    public SecurityModel setSkipKubeNamespaces(boolean skipKubeNamespaces) {
        this.skipKubeNamespaces = skipKubeNamespaces;
        return this;
    }
}
