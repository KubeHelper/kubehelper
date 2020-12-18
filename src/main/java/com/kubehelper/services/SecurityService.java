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
package com.kubehelper.services;

import com.kubehelper.common.KubeAPI;
import com.kubehelper.common.Resource;
import com.kubehelper.domain.models.SecurityModel;
import com.kubehelper.domain.results.PodSecurityContextResult;
import com.kubehelper.domain.results.PodSecurityPoliciesResult;
import com.kubehelper.domain.results.RoleResult;
import com.kubehelper.domain.results.RoleRuleResult;
import io.kubernetes.client.Exec;
import io.kubernetes.client.openapi.models.V1ObjectMeta;
import io.kubernetes.client.openapi.models.V1Pod;
import io.kubernetes.client.openapi.models.V1PodList;
import io.kubernetes.client.openapi.models.V1PodSecurityContext;
import io.kubernetes.client.openapi.models.V1beta1ClusterRole;
import io.kubernetes.client.openapi.models.V1beta1ClusterRoleBinding;
import io.kubernetes.client.openapi.models.V1beta1ClusterRoleBindingList;
import io.kubernetes.client.openapi.models.V1beta1ClusterRoleList;
import io.kubernetes.client.openapi.models.V1beta1PodSecurityPolicy;
import io.kubernetes.client.openapi.models.V1beta1PodSecurityPolicyList;
import io.kubernetes.client.openapi.models.V1beta1PodSecurityPolicySpec;
import io.kubernetes.client.openapi.models.V1beta1PolicyRule;
import io.kubernetes.client.openapi.models.V1beta1Role;
import io.kubernetes.client.openapi.models.V1beta1RoleBinding;
import io.kubernetes.client.openapi.models.V1beta1RoleBindingList;
import io.kubernetes.client.openapi.models.V1beta1RoleList;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.kubehelper.common.Resource.CLUSTER_ROLE;
import static com.kubehelper.common.Resource.ROLE;

/**
 * Search service.
 *
 * @author JDev
 */
@Service
public class SecurityService {

    private static Logger logger = LoggerFactory.getLogger(SecurityService.class);

//    TODO fix progress label
//    private String progressLabel = "";
//    private int currentItemNumber;
//    private int totalItems;

    @Autowired
    private KubeAPI kubeAPI;

    @Autowired
    private Exec exec;

    public void getRoles(SecurityModel securityModel) {
        securityModel.getRolesResults().clear();
        securityModel.getSearchExceptions().clear();
        searchInClusterRoles(securityModel);
        searchInRoles(securityModel);
        searchInClusterRoleBindings(securityModel);
        searchInRoleBindings(securityModel);
    }

    public void getRBACs(SecurityModel securityModel) {
        securityModel.getRbacsResults().clear();
        securityModel.getSearchExceptions().clear();
        searchForRBACs(securityModel);
//        searchInRoles(securityModel);
//        searchInClusterRoleBindings(securityModel);
//        searchInRoleBindings(securityModel);
    }

    private void searchForRBACs(SecurityModel securityModel) {
        V1beta1RoleList rolesList = kubeAPI.getV1RolesList(securityModel.getSelectedRolesNamespace());
        V1beta1RoleBindingList rolesBindingsList = kubeAPI.getV1RolesBindingList(securityModel.getSelectedRolesNamespace());
//        for (V1beta1ClusterRole clusterRole : clusterRolesList.getItems()) {
        try {
//
////                kubeAPI.getV1RolesBindingList()
////                addRoleResultToModel(clusterRole.getMetadata(), securityModel, CLUSTER_ROLE, clusterRole.toString(), clusterRole.getRules());
        } catch (RuntimeException e) {
            securityModel.addSearchException(e);
            logger.error(e.getMessage(), e);
        }
//        }
    }


    public void getPodsSecurityContexts(SecurityModel securityModel) {
        securityModel.getPodsSecurityContextsResults().clear();
        securityModel.getSearchExceptions().clear();
        searchInPodSecurityContexts(securityModel);
    }

    public void getPodsSecurityPolicies(SecurityModel securityModel) {
        securityModel.getPodSecurityPoliciesResults().clear();
        securityModel.getSearchExceptions().clear();
        searchInPodSecurityPolicies(securityModel);
    }


    private void searchInClusterRoles(SecurityModel securityModel) {
        V1beta1ClusterRoleList clusterRolesList = kubeAPI.getV1ClusterRolesList();
        for (V1beta1ClusterRole clusterRole : clusterRolesList.getItems()) {
            try {
                addRoleResultToModel(clusterRole.getMetadata(), securityModel, CLUSTER_ROLE, clusterRole.toString(), clusterRole.getRules());
            } catch (RuntimeException e) {
                securityModel.addSearchException(e);
                logger.error(e.getMessage(), e);
            }
        }
    }


    private void searchInRoles(SecurityModel securityModel) {
        V1beta1RoleList rolesList = kubeAPI.getV1RolesList(securityModel.getSelectedRolesNamespace());
        for (V1beta1Role role : rolesList.getItems()) {
            try {
                addRoleResultToModel(role.getMetadata(), securityModel, ROLE, role.toString(), role.getRules());
            } catch (RuntimeException e) {
                securityModel.addSearchException(e);
                logger.error(e.getMessage(), e);
            }
        }
    }

    private void searchInClusterRoleBindings(SecurityModel securityModel) {
        V1beta1ClusterRoleBindingList clusterRoleBindingsList = kubeAPI.getV1ClusterRolesBindingsList();
        for (V1beta1ClusterRoleBinding binding : clusterRoleBindingsList.getItems()) {
            try {
                securityModel.addRoleSubjects(binding.getRoleRef().getName(), CLUSTER_ROLE, binding.getSubjects());
            } catch (RuntimeException e) {
                securityModel.addSearchException(e);
                logger.error(e.getMessage(), e);
            }
        }
    }

    private void searchInRoleBindings(SecurityModel securityModel) {
        V1beta1RoleBindingList rolesBindingsList = kubeAPI.getV1RolesBindingList(securityModel.getSelectedRolesNamespace());
        for (V1beta1RoleBinding roleBinding : rolesBindingsList.getItems()) {
            try {
                securityModel.addRoleSubjects(roleBinding.getRoleRef().getName(), ROLE, roleBinding.getSubjects());
            } catch (RuntimeException e) {
                securityModel.addSearchException(e);
                logger.error(e.getMessage(), e);
            }
        }
    }

    private void searchInPodSecurityPolicies(SecurityModel securityModel) {
        V1beta1PodSecurityPolicyList policiesList = kubeAPI.getPolicyV1beta1PodSecurityPolicyList();
        for (V1beta1PodSecurityPolicy policy : policiesList.getItems()) {
            try {
                addPodSecurityPolicyToModel(policy.getMetadata(), securityModel, policy.getSpec(), policy.toString());
            } catch (RuntimeException e) {
                securityModel.addSearchException(e);
                logger.error(e.getMessage(), e);
            }
        }
    }

    private void searchInPodSecurityContexts(SecurityModel securityModel) {
        V1PodList podsList = kubeAPI.getV1PodsList(securityModel.getSelectedPodsSecurityContextsNamespace());
        for (V1Pod pod : podsList.getItems()) {
            try {
                addPodSecurityContextToModel(pod.getMetadata(), securityModel, pod.getSpec().getSecurityContext());
            } catch (RuntimeException e) {
                securityModel.addSearchException(e);
                logger.error(e.getMessage(), e);
            }
        }
    }


    /**
     * Add new found variable/text/string to search result.
     *
     * @param metadata      - kubernetes resource/object metadata
     * @param securityModel - security model
     * @param resource      - kubernetes @{@link Resource}
     */
    private void addRoleResultToModel(V1ObjectMeta metadata, SecurityModel securityModel, Resource resource, String fullDefinition, List<V1beta1PolicyRule> rules) {
        RoleResult newRoleResult = new RoleResult(securityModel.getRolesResults().size() + 1)
                .setNamespace(metadata.getNamespace() == null ? "N/A" : metadata.getNamespace())
                .setResourceType(resource)
                .setResourceName(metadata.getName())
                .setCreationTime(getParsedCreationTime(metadata.getCreationTimestamp()))
                .setFullDefinition(fullDefinition);
        addRoleRulesToRoleResult(newRoleResult, rules);
        securityModel.addRoleResult(newRoleResult);
    }

    private void addRoleRulesToRoleResult(RoleResult roleResult, List<V1beta1PolicyRule> rules) {
        List<RoleRuleResult> roleRules = new ArrayList<>();
        rules.forEach(rule -> {
            roleRules.add(new RoleRuleResult(roleRules.size() + 1)
                    .setApiGroups(rule.getApiGroups())
                    .setResources(rule.getResources())
                    .setNonResourceURLs(rule.getNonResourceURLs())
                    .setVerbs(rule.getVerbs())
                    .setResourceNames(rule.getResourceNames())
                    .setFullDefinition(rule.toString()));
        });
        roleResult.addRoleRules(roleRules);
    }

    /**
     * Add new found variable/text/string to search result.
     *
     * @param metadata      - kubernetes resource/object metadata
     * @param securityModel - security model
     */
    private void addPodSecurityPolicyToModel(V1ObjectMeta metadata, SecurityModel securityModel, V1beta1PodSecurityPolicySpec spec, String fullDefinition) {
        PodSecurityPoliciesResult result = new PodSecurityPoliciesResult(securityModel.getPodSecurityPoliciesResults().size() + 1)
                .setAllowPrivilegeEscalation(spec.getAllowPrivilegeEscalation().toString())
                .setDefaultAllowPrivilegeEscalation(spec.getDefaultAllowPrivilegeEscalation().toString())
                .setAllowedCSIDrivers(spec.getAllowedCSIDrivers() == null ? "null" : spec.getAllowedCSIDrivers().toString())
                .setAllowedCapabilities(spec.getAllowedCapabilities() == null ? "null" : spec.getAllowedCapabilities().toString())
                .setAllowedFlexVolumes(spec.getAllowedFlexVolumes() == null ? "null" : spec.getAllowedFlexVolumes().toString())
                .setAllowedHostPaths(spec.getAllowedHostPaths() == null ? "null" : spec.getAllowedHostPaths().toString())
                .setAllowedProcMountTypes(spec.getAllowedProcMountTypes() == null ? "null" : spec.getAllowedProcMountTypes().toString())
                .setAllowedUnsafeSysctls(spec.getAllowedUnsafeSysctls() == null ? "null" : spec.getAllowedUnsafeSysctls().toString())
                .setDefaultAddCapabilities(spec.getDefaultAddCapabilities() == null ? "null" : spec.getDefaultAddCapabilities().toString())
                .setForbiddenSysctls(spec.getForbiddenSysctls() == null ? "null" : spec.getForbiddenSysctls().toString())
                .setFsGroup(spec.getFsGroup() == null ? "null" : spec.getFsGroup().toString())
                .setHostIPC(spec.getHostIPC() == null ? "null" : spec.getHostIPC().toString())
                .setHostNetwork(spec.getHostNetwork() == null ? "null" : spec.getHostNetwork().toString())
                .setHostPID(spec.getHostPID() == null ? "null" : spec.getHostPID().toString())
                .setHostPorts(spec.getHostPorts() == null ? "null" : spec.getHostPorts().toString())
                .setPrivileged(spec.getPrivileged() == null ? "null" : spec.getPrivileged().toString())
                .setReadOnlyRootFilesystem(spec.getReadOnlyRootFilesystem() == null ? "null" : spec.getReadOnlyRootFilesystem().toString())
                .setRequiredDropCapabilities(spec.getRequiredDropCapabilities() == null ? "null" : spec.getRequiredDropCapabilities().toString())
                .setRunAsGroup(spec.getRunAsGroup() == null ? "null" : spec.getRunAsGroup().toString())
                .setRunAsUser(spec.getRunAsUser() == null ? "null" : spec.getRunAsUser().toString())
                .setRuntimeClass(spec.getRuntimeClass() == null ? "null" : spec.getRuntimeClass().toString())
                .setSeLinux(spec.getSeLinux() == null ? "null" : spec.getSeLinux().toString())
                .setSupplementalGroups(spec.getSupplementalGroups() == null ? "null" : spec.getSupplementalGroups().toString())
                .setVolumes(spec.getVolumes() == null ? "null" : spec.getVolumes().toString())
                .setFullDefinition(fullDefinition)
                .setCreationTime(getParsedCreationTime(metadata.getCreationTimestamp()))
                .setNamespace(metadata.getNamespace());
        securityModel.addPodSecurityPolicy(result);
    }

    private void addPodSecurityContextToModel(V1ObjectMeta metadata, SecurityModel securityModel, V1PodSecurityContext securityContext) {
        PodSecurityContextResult result = new PodSecurityContextResult(securityModel.getPodsSecurityContextsResults().size() + 1)
                .setResourceName(metadata.getName())
                .setFsGroup(String.valueOf(securityContext.getFsGroup()))
                .setFsGroupChangePolicy(StringUtils.isEmpty(securityContext.getFsGroupChangePolicy()) ? "null" : securityContext.getFsGroupChangePolicy())
                .setRunAsGroup(String.valueOf(securityContext.getRunAsGroup()))
                .setRunAsNonRoot(String.valueOf(securityContext.getRunAsNonRoot()))
                .setRunAsUser(String.valueOf(securityContext.getRunAsUser()))
                .setSeLinuxOptions(securityContext.getSeLinuxOptions() == null ? "null" : securityContext.getSeLinuxOptions().toString())
                .setSupplementalGroups(securityContext.getSupplementalGroups() == null ? "null" : securityContext.getSupplementalGroups().toString())
                .setSysctls(securityContext.getSysctls() == null ? "null" : securityContext.getSysctls().toString())
                .setWindowsOptions(securityContext.getWindowsOptions() == null ? "null" : securityContext.getWindowsOptions().toString())
                .setCreationTime(getParsedCreationTime(metadata.getCreationTimestamp()))
                .setFullDefinition(securityContext.toString())
                .setNamespace(metadata.getNamespace());
        securityModel.addPodSecurityContext(result);
    }


    private String getParsedCreationTime(DateTime dateTime) {
        return dateTime.toString("dd.MM.yyyy HH:mm:ss");
    }
}
