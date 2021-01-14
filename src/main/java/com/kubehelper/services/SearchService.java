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
import com.kubehelper.domain.results.SearchResult;
import io.kubernetes.client.Exec;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.models.V1ConfigMap;
import io.kubernetes.client.openapi.models.V1ConfigMapKeySelector;
import io.kubernetes.client.openapi.models.V1ConfigMapList;
import io.kubernetes.client.openapi.models.V1Container;
import io.kubernetes.client.openapi.models.V1DaemonSet;
import io.kubernetes.client.openapi.models.V1DaemonSetList;
import io.kubernetes.client.openapi.models.V1Deployment;
import io.kubernetes.client.openapi.models.V1DeploymentList;
import io.kubernetes.client.openapi.models.V1EnvVar;
import io.kubernetes.client.openapi.models.V1EnvVarSource;
import io.kubernetes.client.openapi.models.V1Job;
import io.kubernetes.client.openapi.models.V1JobList;
import io.kubernetes.client.openapi.models.V1NetworkPolicy;
import io.kubernetes.client.openapi.models.V1NetworkPolicyList;
import io.kubernetes.client.openapi.models.V1ObjectMeta;
import io.kubernetes.client.openapi.models.V1PersistentVolume;
import io.kubernetes.client.openapi.models.V1PersistentVolumeClaim;
import io.kubernetes.client.openapi.models.V1PersistentVolumeClaimList;
import io.kubernetes.client.openapi.models.V1PersistentVolumeList;
import io.kubernetes.client.openapi.models.V1Pod;
import io.kubernetes.client.openapi.models.V1PodList;
import io.kubernetes.client.openapi.models.V1PodSpec;
import io.kubernetes.client.openapi.models.V1PodStatus;
import io.kubernetes.client.openapi.models.V1ReplicaSet;
import io.kubernetes.client.openapi.models.V1ReplicaSetList;
import io.kubernetes.client.openapi.models.V1ResourceFieldSelector;
import io.kubernetes.client.openapi.models.V1Secret;
import io.kubernetes.client.openapi.models.V1SecretKeySelector;
import io.kubernetes.client.openapi.models.V1SecretList;
import io.kubernetes.client.openapi.models.V1Service;
import io.kubernetes.client.openapi.models.V1ServiceAccount;
import io.kubernetes.client.openapi.models.V1ServiceAccountList;
import io.kubernetes.client.openapi.models.V1ServiceList;
import io.kubernetes.client.openapi.models.V1StatefulSet;
import io.kubernetes.client.openapi.models.V1StatefulSetList;
import io.kubernetes.client.openapi.models.V1beta1ClusterRole;
import io.kubernetes.client.openapi.models.V1beta1ClusterRoleBinding;
import io.kubernetes.client.openapi.models.V1beta1ClusterRoleBindingList;
import io.kubernetes.client.openapi.models.V1beta1ClusterRoleList;
import io.kubernetes.client.openapi.models.V1beta1PodDisruptionBudget;
import io.kubernetes.client.openapi.models.V1beta1PodDisruptionBudgetList;
import io.kubernetes.client.openapi.models.V1beta1PodSecurityPolicy;
import io.kubernetes.client.openapi.models.V1beta1PodSecurityPolicyList;
import io.kubernetes.client.openapi.models.V1beta1Role;
import io.kubernetes.client.openapi.models.V1beta1RoleBinding;
import io.kubernetes.client.openapi.models.V1beta1RoleBindingList;
import io.kubernetes.client.openapi.models.V1beta1RoleList;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.StringJoiner;

import static com.kubehelper.common.Resource.CLUSTER_ROLE;
import static com.kubehelper.common.Resource.CLUSTER_ROLE_BINDING;
import static com.kubehelper.common.Resource.CONFIG_MAP;
import static com.kubehelper.common.Resource.DAEMON_SET;
import static com.kubehelper.common.Resource.DEPLOYMENT;
import static com.kubehelper.common.Resource.ENV_VARIABLE;
import static com.kubehelper.common.Resource.JOB;
import static com.kubehelper.common.Resource.NETWORK_POLICY;
import static com.kubehelper.common.Resource.PERSISTENT_VOLUME;
import static com.kubehelper.common.Resource.PERSISTENT_VOLUME_CLAIM;
import static com.kubehelper.common.Resource.POD;
import static com.kubehelper.common.Resource.POD_DISRUPTION_BUDGET;
import static com.kubehelper.common.Resource.POD_SECURITY_POLICY;
import static com.kubehelper.common.Resource.REPLICA_SET;
import static com.kubehelper.common.Resource.ROLE;
import static com.kubehelper.common.Resource.ROLE_BINDING;
import static com.kubehelper.common.Resource.SECRET;
import static com.kubehelper.common.Resource.SERVICE;
import static com.kubehelper.common.Resource.SERVICE_ACCOUNT;
import static com.kubehelper.common.Resource.STATEFUL_SET;
import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * Search service.
 *
 * @author JDev
 */
@Service
public class SearchService {

    private static Logger logger = LoggerFactory.getLogger(SearchService.class);

    @Autowired
    private KubeAPI kubeAPI;

    @Autowired
    private Exec exec;

    /**
     * Searches string selected kubernetes resources by selected namespace.
     *
     * @param searchModel - search model
     */
    public void search(SearchModel searchModel, Set<Resource> selectedResources) {

        searchModel.getSearchResults().clear();
        searchModel.getSearchExceptions().clear();
        try {

            if (selectedResources.contains(ENV_VARIABLE)) {
                searchInEnvironmentVariables(searchModel);
            }
            if (selectedResources.contains(CONFIG_MAP)) {
                searchInConfigMaps(searchModel);
            }
            if (selectedResources.contains(SERVICE)) {
                searchInServices(searchModel);
            }
            if (selectedResources.contains(POD)) {
                searchInPods(searchModel);
            }
            if (selectedResources.contains(PERSISTENT_VOLUME)) {
                searchInPersistentVolumes(searchModel);
            }
            if (selectedResources.contains(PERSISTENT_VOLUME_CLAIM)) {
                searchInPersistentVolumeClaims(searchModel);
            }
            if (selectedResources.contains(SECRET)) {
                searchInSecrets(searchModel);
            }
            if (selectedResources.contains(SERVICE_ACCOUNT)) {
                searchInServiceAccounts(searchModel);
            }
            if (selectedResources.contains(DAEMON_SET)) {
                searchInDaemonSets(searchModel);
            }
            if (selectedResources.contains(DEPLOYMENT)) {
                searchInDeployments(searchModel);
            }
            if (selectedResources.contains(REPLICA_SET)) {
                searchInReplicaSets(searchModel);
            }
            if (selectedResources.contains(STATEFUL_SET)) {
                searchInStatefulSets(searchModel);
            }
            if (selectedResources.contains(JOB)) {
                searchInJobs(searchModel);
            }
            if (selectedResources.contains(CLUSTER_ROLE_BINDING)) {
                searchInClusterRoleBindings(searchModel);
            }
            if (selectedResources.contains(CLUSTER_ROLE)) {
                searchInClusterRoles(searchModel);
            }
            if (selectedResources.contains(ROLE_BINDING)) {
                searchInRoleBindings(searchModel);
            }
            if (selectedResources.contains(ROLE)) {
                searchInRoles(searchModel);
            }
            if (selectedResources.contains(NETWORK_POLICY)) {
                searchInNetworkPolicies(searchModel);
            }
            if (selectedResources.contains(POD_DISRUPTION_BUDGET)) {
                searchInPodDistributionBudgets(searchModel);
            }
            if (selectedResources.contains(POD_SECURITY_POLICY)) {
                searchInPodSecurityPolicies(searchModel);
            }

        } catch (RuntimeException e) {
            searchModel.addSearchException(e);
            logger.error(e.getMessage(), e);
        }
    }


    /**
     * Searches string in Pods by selected namespace.
     *
     * @param model - search model
     */
    private void searchInPods(SearchModel model) {
        V1PodList podsList = kubeAPI.getV1PodsList(model.getSelectedNamespace(), model);
        for (V1Pod pod : podsList.getItems()) {
            try {
                if (isStringsContainsSearchString(model.getSearchString(), pod.getMetadata().getName())) {
                    addSearchResultToModel(pod.getMetadata(), model, POD, pod.getMetadata().getName(), pod.getMetadata().getName(), "", pod.toString());
                }
            } catch (RuntimeException e) {
                model.addSearchException(e);
                logger.error(e.getMessage(), e);
            }
        }
    }

    /**
     * Searches string in PersistentVolumes by selected namespace.
     *
     * @param model - search model
     */
    private void searchInPersistentVolumes(SearchModel model) {
        V1PersistentVolumeList persistentVolumesList = kubeAPI.getV1PersistentVolumesList(model);
        for (V1PersistentVolume persistentVolume : persistentVolumesList.getItems()) {
            try {
                if (isStringsContainsSearchString(model.getSearchString(), persistentVolume.getMetadata().getName())) {
                    addSearchResultToModel(persistentVolume.getMetadata(), model, PERSISTENT_VOLUME, persistentVolume.getMetadata().getName(), persistentVolume.getMetadata().getName(), "", persistentVolume.toString());
                }
            } catch (RuntimeException e) {
                model.addSearchException(e);
                logger.error(e.getMessage(), e);
            }
        }
    }

    /**
     * Searches string in PersistentVolumeClaims by selected namespace.
     *
     * @param model - search model
     */
    private void searchInPersistentVolumeClaims(SearchModel model) {
        V1PersistentVolumeClaimList persistentVolumeClaimsList = kubeAPI.getV1PersistentVolumeClaimsList(model.getSelectedNamespace(), model);
        for (V1PersistentVolumeClaim persistentVolumeClaim : persistentVolumeClaimsList.getItems()) {
            try {
                if (isStringsContainsSearchString(model.getSearchString(), persistentVolumeClaim.getMetadata().getName())) {
                    addSearchResultToModel(persistentVolumeClaim.getMetadata(), model, PERSISTENT_VOLUME_CLAIM, persistentVolumeClaim.getMetadata().getName(), persistentVolumeClaim.getMetadata().getName(), "", persistentVolumeClaim.toString());
                }
            } catch (RuntimeException e) {
                model.addSearchException(e);
                logger.error(e.getMessage(), e);
            }
        }
    }

    /**
     * Searches string in Services by selected namespace.
     *
     * @param model - search model
     */
    private void searchInServices(SearchModel model) {
        V1ServiceList servicesList = kubeAPI.getV1ServicesList(model.getSelectedNamespace(), model);
        for (V1Service service : servicesList.getItems()) {
            try {
                if (skipKubeNamespace(model, service.getMetadata())) {
                    continue;
                }
                if (isStringsContainsSearchString(model.getSearchString(), service.getMetadata().getName())) {
                    addSearchResultToModel(service.getMetadata(), model, SERVICE, service.getMetadata().getName(), service.getMetadata().getName(), "", service.toString());
                }
            } catch (RuntimeException e) {
                model.addSearchException(e);
                logger.error(e.getMessage(), e);
            }
        }
    }

    /**
     * Searches string in ServiceAccounts by selected namespace.
     *
     * @param model - search model
     */
    private void searchInServiceAccounts(SearchModel model) {
        V1ServiceAccountList serviceAccountsList = kubeAPI.getV1ServiceAccountsList(model.getSelectedNamespace(), model);
        for (V1ServiceAccount serviceAccount : serviceAccountsList.getItems()) {
            try {
                if (skipKubeNamespace(model, serviceAccount.getMetadata())) {
                    continue;
                }
                StringJoiner additionalInfo = new StringJoiner(",", "[", "]");
                if (isStringsContainsSearchString(model.getSearchString(), serviceAccount.getMetadata().getName())) {
                    if (ObjectUtils.isNotEmpty(serviceAccount.getSecrets())) {
                        serviceAccount.getSecrets().forEach(secretObject -> additionalInfo.add(secretObject.getName()));
                    }
                    addSearchResultToModel(serviceAccount.getMetadata(), model, SERVICE_ACCOUNT, serviceAccount.getMetadata().getName(), serviceAccount.getMetadata().getName(),
                            "secrets " + additionalInfo.toString(), serviceAccount.toString());
                }
            } catch (RuntimeException e) {
                model.addSearchException(e);
                logger.error(e.getMessage(), e);
            }
        }
    }

    /**
     * Searches string in Secrets by selected namespace.
     *
     * @param model - search model
     */
    private void searchInSecrets(SearchModel model) {
        V1SecretList secretsList = kubeAPI.getV1SecretsList(model.getSelectedNamespace(), model);
        for (V1Secret secret : secretsList.getItems()) {
            try {
                if (skipKubeNamespace(model, secret.getMetadata())) {
                    continue;
                }
                if (ObjectUtils.isNotEmpty(secret.getData())) {
                    secret.getData().forEach((secretName, secretValue) -> {
                        if (isStringsContainsSearchString(model.getSearchString(), secret.getMetadata().getName(), secretName)) {
                            String secretType = secret.getType() == null ? "null" : secret.getType();
                            String foundString = secret.getMetadata().getName() + " [" + secretType + "] : " + secretName;
                            addSearchResultToModel(secret.getMetadata(), model, SECRET, secret.getMetadata().getName(), foundString, new String(secretValue, UTF_8), secret.toString());
                        }
                    });
                }
            } catch (RuntimeException e) {
                model.addSearchException(e);
                logger.error(e.getMessage(), e);
            }
        }
    }

    /**
     * Searches string in ConfigMaps by selected namespace.
     *
     * @param model - search model
     */
    private void searchInConfigMaps(SearchModel model) {
        V1ConfigMapList configMapsList = kubeAPI.getV1ConfigMapsList(model.getSelectedNamespace(), model);
        for (V1ConfigMap configMap : configMapsList.getItems()) {
            try {
                if (skipKubeNamespace(model, configMap.getMetadata())) {
                    continue;
                }
                if (ObjectUtils.isNotEmpty(configMap.getData())) {
                    configMap.getData().forEach((configName, configValue) -> {
                        if (isStringsContainsSearchString(model.getSearchString(), configMap.getMetadata().getName(), configName, configValue)) {
                            addSearchResultToModel(configMap.getMetadata(), model, CONFIG_MAP, configMap.getMetadata().getName(), configName, configValue, configMap.toString());
                        }
                    });
                }
            } catch (RuntimeException e) {
                model.addSearchException(e);
                logger.error(e.getMessage(), e);
            }
        }
    }


    /**
     * Searches string in DaemonSets by selected namespace.
     *
     * @param model - search model
     */
    private void searchInDaemonSets(SearchModel model) {
        V1DaemonSetList daemonSetsList = kubeAPI.getV1DaemonSetList(model.getSelectedNamespace(), model);
        for (V1DaemonSet daemonSet : daemonSetsList.getItems()) {
            try {
                if (skipKubeNamespace(model, daemonSet.getMetadata())) {
                    continue;
                }
                if (isStringsContainsSearchString(model.getSearchString(), daemonSet.getMetadata().getName())) {
                    addSearchResultToModel(daemonSet.getMetadata(), model, DAEMON_SET, daemonSet.getMetadata().getName(), daemonSet.getMetadata().getName(), "", daemonSet.toString());
                }
            } catch (RuntimeException e) {
                model.addSearchException(e);
                logger.error(e.getMessage(), e);
            }
        }
    }

    /**
     * Searches string in Deployments by selected namespace.
     *
     * @param model - search model
     */
    private void searchInDeployments(SearchModel model) {
        V1DeploymentList deploymentsList = kubeAPI.getV1DeploymentList(model.getSelectedNamespace(), model);
        for (V1Deployment deployment : deploymentsList.getItems()) {
            try {
                if (skipKubeNamespace(model, deployment.getMetadata())) {
                    continue;
                }
                if (isStringsContainsSearchString(model.getSearchString(), deployment.getMetadata().getName())) {
                    addSearchResultToModel(deployment.getMetadata(), model, DEPLOYMENT, deployment.getMetadata().getName(), deployment.getMetadata().getName(), "", deployment.toString());
                }
            } catch (RuntimeException e) {
                model.addSearchException(e);
                logger.error(e.getMessage(), e);
            }
        }
    }

    /**
     * Searches string in ReplicaSets by selected namespace.
     *
     * @param model - search model
     */
    private void searchInReplicaSets(SearchModel model) {
        V1ReplicaSetList replicaSetsList = kubeAPI.getV1ReplicaSetList(model.getSelectedNamespace(), model);
        for (V1ReplicaSet replicaSet : replicaSetsList.getItems()) {
            try {
                if (skipKubeNamespace(model, replicaSet.getMetadata())) {
                    continue;
                }
                if (isStringsContainsSearchString(model.getSearchString(), replicaSet.getMetadata().getName())) {
                    addSearchResultToModel(replicaSet.getMetadata(), model, REPLICA_SET, replicaSet.getMetadata().getName(), replicaSet.getMetadata().getName(), "", replicaSet.toString());
                }
            } catch (RuntimeException e) {
                model.addSearchException(e);
                logger.error(e.getMessage(), e);
            }
        }
    }

    /**
     * Searches string in StatefulSets by selected namespace.
     *
     * @param model - search model
     */
    private void searchInStatefulSets(SearchModel model) {
        V1StatefulSetList statefulSetsList = kubeAPI.getV1StatefulSetList(model.getSelectedNamespace(), model);
        for (V1StatefulSet statefulSet : statefulSetsList.getItems()) {
            try {
                if (skipKubeNamespace(model, statefulSet.getMetadata())) {
                    continue;
                }
                if (isStringsContainsSearchString(model.getSearchString(), statefulSet.getMetadata().getName())) {
                    addSearchResultToModel(statefulSet.getMetadata(), model, STATEFUL_SET, statefulSet.getMetadata().getName(), statefulSet.getMetadata().getName(), "", statefulSet.toString());
                }
            } catch (RuntimeException e) {
                model.addSearchException(e);
                logger.error(e.getMessage(), e);
            }
        }
    }

    /**
     * Searches string in Jobs by selected namespace.
     *
     * @param model - search model
     */
    private void searchInJobs(SearchModel model) {
        V1JobList jobsList = kubeAPI.getV1JobList(model.getSelectedNamespace(), model);
        for (V1Job job : jobsList.getItems()) {
            try {
                if (skipKubeNamespace(model, job.getMetadata())) {
                    continue;
                }
                if (isStringsContainsSearchString(model.getSearchString(), job.getMetadata().getName())) {
                    addSearchResultToModel(job.getMetadata(), model, JOB, job.getMetadata().getName(), job.getMetadata().getName(), "", job.toString());
                }
            } catch (RuntimeException e) {
                model.addSearchException(e);
                logger.error(e.getMessage(), e);
            }
        }
    }

    /**
     * Searches string in ClusterRoleBindings.
     *
     * @param model - search model
     */
    private void searchInClusterRoleBindings(SearchModel model) {
        V1beta1ClusterRoleBindingList clusterRoleBindingsList = kubeAPI.getV1ClusterRolesBindingsList(model);
        for (V1beta1ClusterRoleBinding binding : clusterRoleBindingsList.getItems()) {
            try {
                if (skipKubeNamespace(model, binding.getMetadata())) {
                    continue;
                }
                if (isStringsContainsSearchString(model.getSearchString(), binding.getMetadata().getName())) {
                    addSearchResultToModel(binding.getMetadata(), model, ROLE_BINDING, binding.getMetadata().getName(), binding.getMetadata().getName(), "", binding.toString());
                }
            } catch (RuntimeException e) {
                model.addSearchException(e);
                logger.error(e.getMessage(), e);
            }
        }
    }

    /**
     * Searches string in ClusterRoles.
     *
     * @param model - search model.
     */
    private void searchInClusterRoles(SearchModel model) {
        V1beta1ClusterRoleList clusterRolesList = kubeAPI.getV1ClusterRolesList(model);
        for (V1beta1ClusterRole clusterRole : clusterRolesList.getItems()) {
            try {
                if (skipKubeNamespace(model, clusterRole.getMetadata())) {
                    continue;
                }
                if (isStringsContainsSearchString(model.getSearchString(), clusterRole.getMetadata().getName())) {
                    addSearchResultToModel(clusterRole.getMetadata(), model, CLUSTER_ROLE, clusterRole.getMetadata().getName(), clusterRole.getMetadata().getName(), "", clusterRole.toString());
                }
            } catch (RuntimeException e) {
                model.addSearchException(e);
                logger.error(e.getMessage(), e);
            }
        }
    }

    /**
     * Searches string in RoleBindings by selected namespace.
     *
     * @param model - search model.
     */
    private void searchInRoleBindings(SearchModel model) {
        V1beta1RoleBindingList rolesBindingsList = kubeAPI.getV1RolesBindingList(model.getSelectedNamespace(), model);
        for (V1beta1RoleBinding roleBinding : rolesBindingsList.getItems()) {
            try {
                if (skipKubeNamespace(model, roleBinding.getMetadata())) {
                    continue;
                }
                if (isStringsContainsSearchString(model.getSearchString(), roleBinding.getMetadata().getName())) {
                    addSearchResultToModel(roleBinding.getMetadata(), model, ROLE_BINDING, roleBinding.getMetadata().getName(), roleBinding.getMetadata().getName(), "", roleBinding.toString());
                }
            } catch (RuntimeException e) {
                model.addSearchException(e);
                logger.error(e.getMessage(), e);
            }
        }
    }

    /**
     * Searches string in Roles by selected namespace.
     *
     * @param model - search model.
     */
    private void searchInRoles(SearchModel model) {
        V1beta1RoleList rolesList = kubeAPI.getV1RolesList(model.getSelectedNamespace(), model);
        for (V1beta1Role role : rolesList.getItems()) {
            try {
                if (skipKubeNamespace(model, role.getMetadata())) {
                    continue;
                }
                if (isStringsContainsSearchString(model.getSearchString(), role.getMetadata().getName())) {
                    addSearchResultToModel(role.getMetadata(), model, ROLE, role.getMetadata().getName(), role.getMetadata().getName(), "", role.toString());
                }
            } catch (RuntimeException e) {
                model.addSearchException(e);
                logger.error(e.getMessage(), e);
            }
        }
    }

    /**
     * Searches string in NetworkPolicies by selected namespace.
     *
     * @param model - search model.
     */
    private void searchInNetworkPolicies(SearchModel model) {
        V1NetworkPolicyList networkPoliciesList = kubeAPI.getV1NetworkPolicyList(model.getSelectedNamespace(), model);
        for (V1NetworkPolicy policy : networkPoliciesList.getItems()) {
            try {
                if (skipKubeNamespace(model, policy.getMetadata())) {
                    continue;
                }
                if (isStringsContainsSearchString(model.getSearchString(), policy.getMetadata().getName())) {
                    addSearchResultToModel(policy.getMetadata(), model, NETWORK_POLICY, policy.getMetadata().getName(), policy.getMetadata().getName(), "", policy.toString());
                }
            } catch (RuntimeException e) {
                model.addSearchException(e);
                logger.error(e.getMessage(), e);
            }
        }
    }

    /**
     * Searches string in PodDisruptionBudgets by selected namespace.
     *
     * @param model - search model.
     */
    private void searchInPodDistributionBudgets(SearchModel model) {
        V1beta1PodDisruptionBudgetList budgetsList = kubeAPI.getV1beta1PodDisruptionBudgetsList(model.getSelectedNamespace(), model);
        for (V1beta1PodDisruptionBudget budget : budgetsList.getItems()) {
            try {
                if (skipKubeNamespace(model, budget.getMetadata())) {
                    continue;
                }
                if (isStringsContainsSearchString(model.getSearchString(), budget.getMetadata().getName())) {
                    addSearchResultToModel(budget.getMetadata(), model, POD_DISRUPTION_BUDGET, budget.getMetadata().getName(), budget.getMetadata().getName(), "", budget.toString());
                }
            } catch (RuntimeException e) {
                model.addSearchException(e);
                logger.error(e.getMessage(), e);
            }
        }
    }

    /**
     * Searches string in PodSecurityPolicies.
     *
     * @param model - search model.
     */
    private void searchInPodSecurityPolicies(SearchModel model) {
        V1beta1PodSecurityPolicyList policiesList = kubeAPI.getPolicyV1beta1PodSecurityPolicyList(model);
        for (V1beta1PodSecurityPolicy policy : policiesList.getItems()) {
            try {
                if (skipKubeNamespace(model, policy.getMetadata())) {
                    continue;
                }
                if (isStringsContainsSearchString(model.getSearchString(), policy.getMetadata().getName())) {
                    addSearchResultToModel(policy.getMetadata(), model, POD_SECURITY_POLICY, policy.getMetadata().getName(), policy.getMetadata().getName(), "", policy.toString());
                }
            } catch (RuntimeException e) {
                model.addSearchException(e);
                logger.error(e.getMessage(), e);
            }
        }
    }


    /**
     * Searches for environment variable in pods by selected namespace.
     *
     * @param model - search model
     */
    private void searchInEnvironmentVariables(SearchModel model) {

        List<V1Pod> podList = kubeAPI.getV1PodsList(model.getSelectedNamespace(), model).getItems();
        for (V1Pod pod : podList) {

            //skips search in kube- namespace
            if (skipKubeNamespace(model, pod.getMetadata())) {
                continue;
            }
            try {
                for (V1Container container : pod.getSpec().getContainers()) {
                    if (ObjectUtils.isNotEmpty(container.getEnv())) {
                        for (V1EnvVar v1EnvVar : container.getEnv()) {
                            if (ObjectUtils.isEmpty(v1EnvVar.getValueFrom())) {
                                //get simple Environment variables
                                composeValueFromToSearchResult(v1EnvVar, pod, container, model, null, null, null);
                            } else {
                                V1EnvVarSource valueFrom = v1EnvVar.getValueFrom();

                                //get Environment variables from FieldRef
                                if (ObjectUtils.isNotEmpty(valueFrom.getFieldRef())) {
                                    String fieldPath = valueFrom.getFieldRef().getFieldPath();
                                    String declaredField = fieldPath.substring(fieldPath.lastIndexOf(".") + 1);
                                    if (valueFrom.getFieldRef().getFieldPath().startsWith("metadata")) {
                                        String envValueFromFieldObject = getEnvValueFromFieldObject(V1ObjectMeta.class.getDeclaredField(declaredField), pod.getMetadata());
                                        composeValueFromToSearchResult(v1EnvVar, pod, container, model, "fieldRef", fieldPath, envValueFromFieldObject);
                                    }
                                    if (fieldPath.startsWith("status")) {
                                        String envValueFromFieldObject = getEnvValueFromFieldObject(V1PodStatus.class.getDeclaredField(declaredField), pod.getStatus());
                                        composeValueFromToSearchResult(v1EnvVar, pod, container, model, "fieldRef", fieldPath, envValueFromFieldObject);
                                    }
                                    if (fieldPath.startsWith("spec")) {
                                        String envValueFromFieldObject = getEnvValueFromFieldObject(V1PodSpec.class.getDeclaredField(declaredField), pod.getSpec());
                                        composeValueFromToSearchResult(v1EnvVar, pod, container, model, "fieldRef", fieldPath, envValueFromFieldObject);
                                    }
                                }

                                //get Environment variables from ResourceFieldRef
                                if (ObjectUtils.isNotEmpty(valueFrom.getResourceFieldRef())) {
                                    V1ResourceFieldSelector resourceFieldRef = valueFrom.getResourceFieldRef();
                                    StringBuilder valueFromValue = new StringBuilder();
                                    valueFromValue.append("{ containerName: ").append(resourceFieldRef.getContainerName() == null ? "null" : resourceFieldRef.getContainerName()).append(", ");
                                    valueFromValue.append("divisor: ").append(resourceFieldRef.getContainerName() == null ? "null" : resourceFieldRef.getContainerName()).append(", ");
                                    valueFromValue.append("resource: ").append(resourceFieldRef.getContainerName() == null ? "null" : resourceFieldRef.getContainerName()).append(" }");
                                    composeValueFromToSearchResult(v1EnvVar, pod, container, model, "resourceFieldRef", "", valueFromValue.toString());
                                }

                                //get Environment variables from ConfigMapKeyRef
                                if (ObjectUtils.isNotEmpty(valueFrom.getConfigMapKeyRef())) {
                                    V1ConfigMapKeySelector configMapKeyRef = valueFrom.getConfigMapKeyRef();
                                    StringBuilder valueFromValue = new StringBuilder();
                                    valueFromValue.append("{ key: ").append(configMapKeyRef.getKey() == null ? "null" : configMapKeyRef.getKey()).append(", ");
                                    valueFromValue.append("name: ").append(configMapKeyRef.getName() == null ? "null" : configMapKeyRef.getName()).append(" }");
                                    composeValueFromToSearchResult(v1EnvVar, pod, container, model, "configMapKeyRef", "", valueFromValue.toString());
                                }

                                //get Environment variables from SecretKeyRef
                                if (ObjectUtils.isNotEmpty(valueFrom.getSecretKeyRef())) {
                                    V1SecretKeySelector secretKeyRef = valueFrom.getSecretKeyRef();
                                    StringBuilder valueFromValue = new StringBuilder();
                                    valueFromValue.append("{ key: ").append(secretKeyRef.getKey() == null ? "null" : secretKeyRef.getKey()).append(", ");
                                    valueFromValue.append("name: ").append(secretKeyRef.getName() == null ? "null" : secretKeyRef.getName()).append(" }");
                                    composeValueFromToSearchResult(v1EnvVar, pod, container, model, "secretKeyRef", "", valueFromValue.toString());
                                }
                            }
                        }
                    }
                }
            } catch (NoSuchFieldException | IllegalAccessException e) {
                model.addSearchException(e);
                logger.error(e.getMessage(), e);
            }
            //collect all env vars from pod with exec
            if (!model.isSkipNativeEnvVars()) {
                mergeNativeEnvVarsToSearchResult(pod, model);
            }
        }
    }

    /**
     * Check, calculate and compose pod valueFrom field to search model.
     *
     * @param v1EnvVar        - valueFrom Environment Variable
     * @param pod             - pod
     * @param container       - container
     * @param model           - search model
     * @param valueFromSource - environment from value source
     * @param valueFromValue  - environment from value
     * @param envValue        - environment value
     */
    private void composeValueFromToSearchResult(V1EnvVar v1EnvVar, V1Pod pod, V1Container container, SearchModel model, String valueFromSource, String valueFromValue, String envValue) {
        String envName, additionalInfo = "";
        envName = v1EnvVar.getName().toLowerCase();
        if (StringUtils.isNotBlank(valueFromSource)) {
            additionalInfo = "Value From: [" + valueFromSource + "] " + valueFromValue;
        } else {
            additionalInfo = "Simple Environment Variable";
            envValue = v1EnvVar.getValue().toLowerCase();
        }
        if (isStringsContainsSearchString(model.getSearchString(), envName, envValue)) {
            String resourceName = pod.getMetadata().getName() + " [" + container.getName() + "]";
            String composedFoundString = v1EnvVar.getName() + "=" + envValue;
            addSearchResultToModel(pod.getMetadata(), model, ENV_VARIABLE, resourceName, composedFoundString, additionalInfo, pod.toString());
        }
    }

    /**
     * Executes env command on pod, and collects native environment variables.
     *
     * @param pod   - kubernetes pod
     * @param model - search model
     * @return - properties object with key=value native environment variables map.
     */
    private Properties getPodEnvironmentVars(V1Pod pod, SearchModel model) {
        Properties envVars = new Properties();
        String[] command = new String[]{"env"};
        try {
            Process process = exec.exec(pod, command, false);
            BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = br.readLine()) != null) {
                int idx = line.indexOf('=');
                if (idx != -1) {
                    String key = line.substring(0, idx);
                    String value = line.substring(idx + 1);
                    envVars.setProperty(key, value);
                }
            }
        } catch (ApiException | IOException | RuntimeException e) {
            model.addSearchException(e);
            logger.error(e.getMessage(), e);
        }
        return envVars;
    }


    /**
     * Merge native environment variables (env) into search model.
     *
     * @param pod   - kubernetes pod
     * @param model - search model
     */
    private void mergeNativeEnvVarsToSearchResult(V1Pod pod, SearchModel model) {
        Properties podEnvironmentVars = getPodEnvironmentVars(pod, model);
        //copy list, because concurrent modification exception
        List<SearchResult> searchResultList = new ArrayList<>(model.getSearchResults());
        for (Map.Entry<Object, Object> entry : podEnvironmentVars.entrySet()) {
            String key = (String) entry.getKey(), envValue = (String) entry.getValue();
            boolean isEnvVarNotFound = true;
            for (SearchResult searchResult : searchResultList) {
                //Check if environment variable already exists in search model
                if (searchResult.getResourceName().startsWith(pod.getMetadata().getName()) && searchResult.getFoundString().startsWith(key)) {
                    isEnvVarNotFound = false;
                    break;
                }
            }
            //add new native environment variable
            if (isEnvVarNotFound && isStringsContainsSearchString(model.getSearchString(), key, envValue)) {
                String composedFoundString = key + "=" + envValue;
                addSearchResultToModel(pod.getMetadata(), model, ENV_VARIABLE, pod.getMetadata().getName(), composedFoundString, "Native Environment Variable", pod.toString());
            }
        }
    }


    /**
     * Add new found variable/text/string to search result.
     *
     * @param meta           - kubernetes resource/object meta
     * @param model          - search model
     * @param resource       - kubernetes @{@link Resource}
     * @param resourceName   - resource name
     * @param foundString    - found string
     * @param additionalInfo - additional info
     */
    private void addSearchResultToModel(V1ObjectMeta meta, SearchModel model, Resource resource, String resourceName, String foundString, String additionalInfo, String fullDefinition) {
        SearchResult newSearchResult = new SearchResult(model.getSearchResults().size() + 1)
                .setNamespace(meta.getNamespace() == null ? "N/A" : meta.getNamespace())
                .setResourceType(resource)
                .setResourceName(resourceName)
                .setFoundString(foundString)
                .setAdditionalInfo(additionalInfo)
                .setCreationTime(getParsedCreationTime(meta.getCreationTimestamp()))
                .setFullDefinition(fullDefinition);
        model.addSearchResult(newSearchResult)
                .addResourceNameFilter(meta.getName());
    }

    /**
     * Checks if searched string contains in strings.
     *
     * @param searchString - searched string.
     * @param strings      - searched strings.
     * @return - true is searches string contains in searched strings.
     */
    private boolean isStringsContainsSearchString(String searchString, String... strings) {
        for (String s : strings) {
            if (StringUtils.containsIgnoreCase(s, searchString)) {
                return true;
            }
        }
        return false;
    }

    private String getParsedCreationTime(DateTime dateTime) {
        return dateTime.toString("dd.MM.yyyy HH:mm:ss");
    }

    private String getEnvValueFromFieldObject(Field field, Object fieldObject) throws IllegalAccessException {
        field.setAccessible(true);
        return ((String) field.get(fieldObject)).toLowerCase();
    }

    private boolean skipKubeNamespace(SearchModel model, V1ObjectMeta meta) {
        return model.isSkipKubeNamespaces() && StringUtils.isNotBlank(meta.getNamespace()) && meta.getNamespace().startsWith("kube-");
    }
}
