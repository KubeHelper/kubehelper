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
import com.kubehelper.domain.models.SearchModel;
import com.kubehelper.domain.models.SecurityModel;
import com.kubehelper.domain.results.PodSecurityResult;
import com.kubehelper.domain.results.RoleResult;
import com.kubehelper.domain.results.RoleRuleResult;
import io.kubernetes.client.Exec;
import io.kubernetes.client.openapi.models.V1ObjectMeta;
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
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.kubehelper.common.Resource.CLUSTER_ROLE;
import static com.kubehelper.common.Resource.POD_SECURITY_POLICY;
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

    public void getPods(SecurityModel securityModel) {
        securityModel.getPodsResults().clear();
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
                securityModel.addRoleSubjects(binding.getRoleRef().getName(), binding.getSubjects());
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
                securityModel.addRoleSubjects(roleBinding.getRoleRef().getName(), roleBinding.getSubjects());
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
        PodSecurityResult result = new PodSecurityResult(securityModel.getPodsResults().size() + 1)
                .setFsGroup(spec.getFsGroup().toString())
                .setNamespace(metadata.getNamespace())
                .setRunAsGroup(spec.getRunAsGroup().toString())
                .setRunAsUser(spec.getRunAsUser().toString())
                .setFullDefinition(fullDefinition)
                .setCreationTime(getParsedCreationTime(metadata.getCreationTimestamp()))
                .setNamespace(metadata.getNamespace());
//                .setRunAsNonRoot(spec.getr)
        //TODO
//        securityModel.addRoleResult(newRoleResult);
    }


    private String getParsedCreationTime(DateTime dateTime) {
        return dateTime.toString("dd.MM.yyyy HH:mm:ss");
    }
}
