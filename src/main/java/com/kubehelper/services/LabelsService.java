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
import com.kubehelper.common.ResourceProperty;
import com.kubehelper.domain.models.LabelsModel;
import com.kubehelper.domain.results.LabelResult;
import io.kubernetes.client.openapi.models.V1ConfigMap;
import io.kubernetes.client.openapi.models.V1ConfigMapList;
import io.kubernetes.client.openapi.models.V1DaemonSet;
import io.kubernetes.client.openapi.models.V1DaemonSetList;
import io.kubernetes.client.openapi.models.V1Deployment;
import io.kubernetes.client.openapi.models.V1DeploymentList;
import io.kubernetes.client.openapi.models.V1Job;
import io.kubernetes.client.openapi.models.V1JobList;
import io.kubernetes.client.openapi.models.V1LabelSelectorRequirement;
import io.kubernetes.client.openapi.models.V1Namespace;
import io.kubernetes.client.openapi.models.V1NamespaceList;
import io.kubernetes.client.openapi.models.V1NetworkPolicy;
import io.kubernetes.client.openapi.models.V1NetworkPolicyList;
import io.kubernetes.client.openapi.models.V1ObjectMeta;
import io.kubernetes.client.openapi.models.V1PersistentVolume;
import io.kubernetes.client.openapi.models.V1PersistentVolumeClaim;
import io.kubernetes.client.openapi.models.V1PersistentVolumeClaimList;
import io.kubernetes.client.openapi.models.V1PersistentVolumeList;
import io.kubernetes.client.openapi.models.V1Pod;
import io.kubernetes.client.openapi.models.V1PodList;
import io.kubernetes.client.openapi.models.V1ReplicaSet;
import io.kubernetes.client.openapi.models.V1ReplicaSetList;
import io.kubernetes.client.openapi.models.V1Secret;
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
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static com.kubehelper.common.Resource.CLUSTER_ROLE;
import static com.kubehelper.common.Resource.CLUSTER_ROLE_BINDING;
import static com.kubehelper.common.Resource.CONFIG_MAP;
import static com.kubehelper.common.Resource.DAEMON_SET;
import static com.kubehelper.common.Resource.DEPLOYMENT;
import static com.kubehelper.common.Resource.JOB;
import static com.kubehelper.common.Resource.NAMESPACE;
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
import static com.kubehelper.common.ResourceProperty.ANNOTATION;
import static com.kubehelper.common.ResourceProperty.LABEL;
import static com.kubehelper.common.ResourceProperty.NODE_SELECTOR;
import static com.kubehelper.common.ResourceProperty.SELECTOR;

/**
 * Labels service.
 *
 * @author JDev
 */
@Service
public class LabelsService {

    private static Logger logger = LoggerFactory.getLogger(LabelsService.class);

    @Autowired
    private KubeAPI kubeAPI;

    /**
     * Searches string selected kubernetes resources by selected namespace.
     *
     * @param searchModel - search model
     */
    public void search(LabelsModel searchModel, Set<Resource> selectedResources) {
        searchModel.getSearchResults().clear();
        searchModel.getSearchExceptions().clear();

//        PolicyV1beta1PodSecurityPolicyList policyV1beta1PodSecurityPolicyList = kubeAPI.getPolicyV1beta1PodSecurityPolicyList();
//        V1PodList v1PodsList = kubeAPI.getV1PodsList(searchModel.getSelectedNamespace());
//        V1beta1PodDisruptionBudgetList v1beta1PodDisruptionBudgetsList = kubeAPI.getV1beta1PodDisruptionBudgetsList(searchModel.getSelectedNamespace());
//        V1NetworkPolicyList v1NetworkPolicyList = kubeAPI.getV1NetworkPolicyList(searchModel.getSelectedNamespace());
//        V1beta1PodDisruptionBudgetList podDisruptionBudgetsList = kubeAPI.getV1beta1PodDisruptionBudgetsList(searchModel.getSelectedNamespace());
//
//
//        kubeAPI.testApis();
//        V1PodSecurityContext

        try {

            if (selectedResources.contains(POD)) {
                searchInPods(searchModel);
            }
            if (selectedResources.contains(CONFIG_MAP)) {
                searchInConfigMaps(searchModel);
            }
            if (selectedResources.contains(SERVICE)) {
                searchInServices(searchModel);
            }
            if (selectedResources.contains(NAMESPACE)) {
                searchInNamespaces(searchModel);
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
     * Searches labels in Pods by selected namespace.
     *
     * @param searchModel - search model
     */
    private void searchInPods(LabelsModel searchModel) {
        V1PodList podsList = kubeAPI.getV1PodsList(searchModel.getSelectedNamespace(), searchModel);
        for (V1Pod pod : podsList.getItems()) {
            try {
                V1ObjectMeta meta = pod.getMetadata();
                if (!skipKubeNamespace(searchModel, meta)) {
                    Optional.ofNullable(meta.getLabels()).ifPresent(map -> addSearchResultsToModel(map, meta, POD, LABEL, searchModel, ""));
                    Optional.ofNullable(meta.getAnnotations()).ifPresent(map -> addSearchResultsToModel(map, meta, POD, ANNOTATION, searchModel, ""));
                    Optional.ofNullable(pod.getSpec().getNodeSelector()).ifPresent(map -> addSearchResultsToModel(map, meta, POD, NODE_SELECTOR, searchModel, ""));
                }
            } catch (RuntimeException e) {
                searchModel.addSearchException(e);
                logger.error(e.getMessage(), e);
            }
        }
    }

    /**
     * Searches labels in ConfigMaps by selected namespace.
     *
     * @param searchModel - search model
     */
    private void searchInConfigMaps(LabelsModel searchModel) {
        V1ConfigMapList configMapsList = kubeAPI.getV1ConfigMapsList(searchModel.getSelectedNamespace(), searchModel);
        for (V1ConfigMap configMap : configMapsList.getItems()) {
            try {
                V1ObjectMeta meta = configMap.getMetadata();
                if (!skipKubeNamespace(searchModel, meta)) {
                    Optional.ofNullable(meta.getLabels()).ifPresent(map -> addSearchResultsToModel(map, meta, CONFIG_MAP, LABEL, searchModel, ""));
                    Optional.ofNullable(meta.getAnnotations()).ifPresent(map -> addSearchResultsToModel(map, meta, CONFIG_MAP, ANNOTATION, searchModel, ""));
                }
            } catch (RuntimeException e) {
                searchModel.addSearchException(e);
                logger.error(e.getMessage(), e);
            }
        }
    }

    /**
     * Searches labels in Services by selected namespace.
     *
     * @param searchModel - search model
     */
    private void searchInServices(LabelsModel searchModel) {
        V1ServiceList servicesList = kubeAPI.getV1ServicesList(searchModel.getSelectedNamespace(), searchModel);
        for (V1Service service : servicesList.getItems()) {
            try {
                V1ObjectMeta meta = service.getMetadata();
                if (!skipKubeNamespace(searchModel, meta)) {
                    Optional.ofNullable(meta.getLabels()).ifPresent(map -> addSearchResultsToModel(map, meta, SERVICE, LABEL, searchModel, ""));
                    Optional.ofNullable(meta.getAnnotations()).ifPresent(map -> addSearchResultsToModel(map, meta, SERVICE, ANNOTATION, searchModel, ""));
                    Optional.ofNullable(service.getSpec().getSelector()).ifPresent(map -> addSearchResultsToModel(map, meta, SERVICE, SELECTOR, searchModel, ""));
                }
            } catch (RuntimeException e) {
                searchModel.addSearchException(e);
                logger.error(e.getMessage(), e);
            }
        }
    }

    /**
     * Searches labels in Namespaces by selected namespace.
     *
     * @param searchModel - search model
     */
    private void searchInNamespaces(LabelsModel searchModel) {
        V1NamespaceList namespacesList = kubeAPI.getV1NamespacesList(searchModel);
        for (V1Namespace namespace : namespacesList.getItems()) {
            try {
                V1ObjectMeta meta = namespace.getMetadata();
                if (!skipKubeNamespace(searchModel, meta)) {
                    Optional.ofNullable(namespace.getMetadata().getLabels()).ifPresent(map -> addSearchResultsToModel(map, meta, NAMESPACE, LABEL, searchModel, ""));
                    Optional.ofNullable(namespace.getMetadata().getAnnotations()).ifPresent(map -> addSearchResultsToModel(map, meta, NAMESPACE, ANNOTATION, searchModel, ""));
                }
            } catch (RuntimeException e) {
                searchModel.addSearchException(e);
                logger.error(e.getMessage(), e);
            }
        }
    }

    /**
     * Searches labels in PersistentVolumes by selected namespace.
     *
     * @param searchModel - search model
     */
    private void searchInPersistentVolumes(LabelsModel searchModel) {
        V1PersistentVolumeList persistentVolumesList = kubeAPI.getV1PersistentVolumesList(searchModel);
        for (V1PersistentVolume pv : persistentVolumesList.getItems()) {
            try {
                V1ObjectMeta meta = pv.getMetadata();
                Optional.ofNullable(meta.getLabels()).ifPresent(map -> addSearchResultsToModel(map, meta, PERSISTENT_VOLUME, LABEL, searchModel, ""));
                Optional.ofNullable(meta.getAnnotations()).ifPresent(map -> addSearchResultsToModel(map, meta, PERSISTENT_VOLUME, ANNOTATION, searchModel, ""));
            } catch (RuntimeException e) {
                searchModel.addSearchException(e);
                logger.error(e.getMessage(), e);
            }
        }
    }

    /**
     * Searches labels in PersistentVolumeClaims by selected namespace.
     *
     * @param searchModel - search model
     */
    private void searchInPersistentVolumeClaims(LabelsModel searchModel) {
        V1PersistentVolumeClaimList persistentVolumeClaimsList = kubeAPI.getV1PersistentVolumeClaimsList(searchModel.getSelectedNamespace(), searchModel);
        for (V1PersistentVolumeClaim pvc : persistentVolumeClaimsList.getItems()) {
            try {
                V1ObjectMeta meta = pvc.getMetadata();
                if (!skipKubeNamespace(searchModel, meta)) {
                    Optional.ofNullable(meta.getLabels()).ifPresent(map -> addSearchResultsToModel(map, meta, PERSISTENT_VOLUME_CLAIM, LABEL, searchModel, ""));
                    Optional.ofNullable(meta.getAnnotations()).ifPresent(map -> addSearchResultsToModel(map, meta, PERSISTENT_VOLUME_CLAIM, ANNOTATION, searchModel, ""));
                    Optional.ofNullable(pvc.getSpec().getSelector()).ifPresent(selector -> addSearchResultsToModel(selector.getMatchLabels(), meta, PERSISTENT_VOLUME_CLAIM, SELECTOR, searchModel,
                            getMatchExpressions(selector.getMatchExpressions())));
                }
            } catch (RuntimeException e) {
                searchModel.addSearchException(e);
                logger.error(e.getMessage(), e);
            }
        }
    }

    /**
     * Searches labels in ServiceAccounts by selected namespace.
     *
     * @param searchModel - search model
     */
    private void searchInServiceAccounts(LabelsModel searchModel) {
        V1ServiceAccountList serviceAccountsList = kubeAPI.getV1ServiceAccountsList(searchModel.getSelectedNamespace(), searchModel);
        for (V1ServiceAccount serviceAccount : serviceAccountsList.getItems()) {
            try {
                V1ObjectMeta meta = serviceAccount.getMetadata();
                if (!skipKubeNamespace(searchModel, meta)) {
                    Optional.ofNullable(meta.getLabels()).ifPresent(map -> addSearchResultsToModel(map, meta, SERVICE_ACCOUNT, LABEL, searchModel, ""));
                    Optional.ofNullable(meta.getAnnotations()).ifPresent(map -> addSearchResultsToModel(map, meta, SERVICE_ACCOUNT, ANNOTATION, searchModel, ""));
                }
            } catch (RuntimeException e) {
                searchModel.addSearchException(e);
                logger.error(e.getMessage(), e);
            }
        }
    }

    /**
     * Searches labels in Secrets by selected namespace.
     *
     * @param searchModel - search model
     */
    private void searchInSecrets(LabelsModel searchModel) {
        V1SecretList secretsList = kubeAPI.getV1SecretsList(searchModel.getSelectedNamespace(), searchModel);
        for (V1Secret secret : secretsList.getItems()) {
            try {
                V1ObjectMeta meta = secret.getMetadata();
                if (!skipKubeNamespace(searchModel, meta)) {
                    Optional.ofNullable(meta.getLabels()).ifPresent(map -> addSearchResultsToModel(map, meta, SECRET, LABEL, searchModel, ""));
                    Optional.ofNullable(meta.getAnnotations()).ifPresent(map -> addSearchResultsToModel(map, meta, SECRET, ANNOTATION, searchModel, ""));
                }
            } catch (RuntimeException e) {
                searchModel.addSearchException(e);
                logger.error(e.getMessage(), e);
            }
        }
    }

    /**
     * Searches labels in DaemonSets by selected namespace.
     *
     * @param searchModel - search model
     */
    private void searchInDaemonSets(LabelsModel searchModel) {
        V1DaemonSetList setsList = kubeAPI.getV1DaemonSetList(searchModel.getSelectedNamespace(), searchModel);
        for (V1DaemonSet set : setsList.getItems()) {
            try {
                V1ObjectMeta meta = set.getMetadata();
                if (!skipKubeNamespace(searchModel, meta)) {
                    Optional.ofNullable(meta.getLabels()).ifPresent(map -> addSearchResultsToModel(map, meta, DAEMON_SET, LABEL, searchModel, ""));
                    Optional.ofNullable(meta.getAnnotations()).ifPresent(map -> addSearchResultsToModel(map, meta, DAEMON_SET, ANNOTATION, searchModel, ""));
                    Optional.ofNullable(set.getSpec().getSelector()).ifPresent(selector -> addSearchResultsToModel(selector.getMatchLabels(), meta, DAEMON_SET, SELECTOR, searchModel,
                            getMatchExpressions(selector.getMatchExpressions())));
                }
            } catch (RuntimeException e) {
                searchModel.addSearchException(e);
                logger.error(e.getMessage(), e);
            }
        }
    }

    /**
     * Searches labels in Deployments by selected namespace.
     * D
     *
     * @param searchModel - search model
     */
    private void searchInDeployments(LabelsModel searchModel) {
        V1DeploymentList deploymentsList = kubeAPI.getV1DeploymentList(searchModel.getSelectedNamespace(), searchModel);
        for (V1Deployment deployment : deploymentsList.getItems()) {
            try {
                V1ObjectMeta meta = deployment.getMetadata();
                if (!skipKubeNamespace(searchModel, meta)) {
                    Optional.ofNullable(meta.getLabels()).ifPresent(map -> addSearchResultsToModel(map, meta, DEPLOYMENT, LABEL, searchModel, ""));
                    Optional.ofNullable(meta.getAnnotations()).ifPresent(map -> addSearchResultsToModel(map, meta, DEPLOYMENT, ANNOTATION, searchModel, ""));
                    Optional.ofNullable(deployment.getSpec().getSelector()).ifPresent(selector -> addSearchResultsToModel(selector.getMatchLabels(), meta, DEPLOYMENT, SELECTOR, searchModel,
                            getMatchExpressions(selector.getMatchExpressions())));
                }
            } catch (RuntimeException e) {
                searchModel.addSearchException(e);
                logger.error(e.getMessage(), e);
            }
        }
    }


    /**
     * Searches labels in ReplicaSets by selected namespace.
     *
     * @param searchModel - search model
     */
    private void searchInReplicaSets(LabelsModel searchModel) {
        V1ReplicaSetList replicaSetsList = kubeAPI.getV1ReplicaSetList(searchModel.getSelectedNamespace(), searchModel);
        for (V1ReplicaSet replicaSet : replicaSetsList.getItems()) {
            try {
                V1ObjectMeta meta = replicaSet.getMetadata();
                if (!skipKubeNamespace(searchModel, meta)) {
                    Optional.ofNullable(meta.getLabels()).ifPresent(map -> addSearchResultsToModel(map, meta, REPLICA_SET, LABEL, searchModel, ""));
                    Optional.ofNullable(meta.getAnnotations()).ifPresent(map -> addSearchResultsToModel(map, meta, REPLICA_SET, ANNOTATION, searchModel, ""));
                    Optional.ofNullable(replicaSet.getSpec().getSelector()).ifPresent(selector -> addSearchResultsToModel(selector.getMatchLabels(), meta, REPLICA_SET, SELECTOR, searchModel,
                            getMatchExpressions(selector.getMatchExpressions())));
                }
            } catch (RuntimeException e) {
                searchModel.addSearchException(e);
                logger.error(e.getMessage(), e);
            }
        }
    }

    /**
     * Searches labels in StatefulSets by selected namespace.
     *
     * @param searchModel - search model
     */
    private void searchInStatefulSets(LabelsModel searchModel) {
        V1StatefulSetList statefulSetstList = kubeAPI.getV1StatefulSetList(searchModel.getSelectedNamespace(), searchModel);
        for (V1StatefulSet statefulSet : statefulSetstList.getItems()) {
            try {
                V1ObjectMeta meta = statefulSet.getMetadata();
                if (!skipKubeNamespace(searchModel, meta)) {
                    Optional.ofNullable(meta.getLabels()).ifPresent(map -> addSearchResultsToModel(map, meta, STATEFUL_SET, LABEL, searchModel, ""));
                    Optional.ofNullable(meta.getAnnotations()).ifPresent(map -> addSearchResultsToModel(map, meta, STATEFUL_SET, ANNOTATION, searchModel, ""));
                    Optional.ofNullable(statefulSet.getSpec().getSelector()).ifPresent(selector -> addSearchResultsToModel(selector.getMatchLabels(), meta, STATEFUL_SET, SELECTOR, searchModel,
                            getMatchExpressions(selector.getMatchExpressions())));
                }
            } catch (RuntimeException e) {
                searchModel.addSearchException(e);
                logger.error(e.getMessage(), e);
            }
        }
    }

    /**
     * Searches labels in Jobs by selected namespace.
     *
     * @param searchModel - search model
     */
    private void searchInJobs(LabelsModel searchModel) {
        V1JobList jobsList = kubeAPI.getV1JobList(searchModel.getSelectedNamespace(), searchModel);
        for (V1Job job : jobsList.getItems()) {
            try {
                V1ObjectMeta meta = job.getMetadata();
                if (!skipKubeNamespace(searchModel, meta)) {
                    Optional.ofNullable(meta.getLabels()).ifPresent(map -> addSearchResultsToModel(map, meta, JOB, LABEL, searchModel, ""));
                    Optional.ofNullable(meta.getAnnotations()).ifPresent(map -> addSearchResultsToModel(map, meta, JOB, ANNOTATION, searchModel, ""));
                    Optional.ofNullable(job.getSpec().getSelector()).ifPresent(selector -> addSearchResultsToModel(selector.getMatchLabels(), meta, JOB, SELECTOR, searchModel,
                            getMatchExpressions(selector.getMatchExpressions())));
                }
            } catch (RuntimeException e) {
                searchModel.addSearchException(e);
                logger.error(e.getMessage(), e);
            }
        }
    }

    /**
     * Searches labels in ClusterRoleBindings by selected namespace.
     *
     * @param searchModel - search model
     */
    private void searchInClusterRoleBindings(LabelsModel searchModel) {
        V1beta1ClusterRoleBindingList clusterRoleBindingsList = kubeAPI.getV1ClusterRolesBindingsList(searchModel);
        for (V1beta1ClusterRoleBinding binding : clusterRoleBindingsList.getItems()) {
            try {
                V1ObjectMeta meta = binding.getMetadata();
                if (!skipKubeNamespace(searchModel, meta)) {
                    Optional.ofNullable(meta.getLabels()).ifPresent(map -> addSearchResultsToModel(map, meta, CLUSTER_ROLE_BINDING, LABEL, searchModel, ""));
                    Optional.ofNullable(meta.getAnnotations()).ifPresent(map -> addSearchResultsToModel(map, meta, CLUSTER_ROLE_BINDING, ANNOTATION, searchModel, ""));
                }
            } catch (RuntimeException e) {
                searchModel.addSearchException(e);
                logger.error(e.getMessage(), e);
            }
        }
    }

    /**
     * Searches labels in ClusterRoles by selected namespace.
     *
     * @param searchModel - search model
     */
    private void searchInClusterRoles(LabelsModel searchModel) {
        V1beta1ClusterRoleList clusterRolesList = kubeAPI.getV1ClusterRolesList(searchModel);
        for (V1beta1ClusterRole clusterRole : clusterRolesList.getItems()) {
            try {
                V1ObjectMeta meta = clusterRole.getMetadata();
                if (!skipKubeNamespace(searchModel, meta)) {
                    Optional.ofNullable(meta.getLabels()).ifPresent(map -> addSearchResultsToModel(map, meta, CLUSTER_ROLE, LABEL, searchModel, ""));
                    Optional.ofNullable(meta.getAnnotations()).ifPresent(map -> addSearchResultsToModel(map, meta, CLUSTER_ROLE, ANNOTATION, searchModel, ""));
                }
            } catch (RuntimeException e) {
                searchModel.addSearchException(e);
                logger.error(e.getMessage(), e);
            }
        }
    }

    /**
     * Searches labels in RoleBindings by selected namespace.
     *
     * @param searchModel - search model
     */
    private void searchInRoleBindings(LabelsModel searchModel) {
        V1beta1RoleBindingList v1RolesBindingsList = kubeAPI.getV1RolesBindingList(searchModel.getSelectedNamespace(), searchModel);
        for (V1beta1RoleBinding binding : v1RolesBindingsList.getItems()) {
            try {
                V1ObjectMeta meta = binding.getMetadata();
                if (!skipKubeNamespace(searchModel, meta)) {
                    Optional.ofNullable(meta.getLabels()).ifPresent(map -> addSearchResultsToModel(map, meta, ROLE_BINDING, LABEL, searchModel, ""));
                    Optional.ofNullable(meta.getAnnotations()).ifPresent(map -> addSearchResultsToModel(map, meta, ROLE_BINDING, ANNOTATION, searchModel, ""));
                }
            } catch (RuntimeException e) {
                searchModel.addSearchException(e);
                logger.error(e.getMessage(), e);
            }
        }
    }

    /**
     * Searches labels in Roles by selected namespace.
     *
     * @param searchModel - search model
     */
    private void searchInRoles(LabelsModel searchModel) {
        V1beta1RoleList clusterRolesList = kubeAPI.getV1RolesList(searchModel.getSelectedNamespace(), searchModel);
        for (V1beta1Role clusterRole : clusterRolesList.getItems()) {
            try {
                V1ObjectMeta meta = clusterRole.getMetadata();
                if (!skipKubeNamespace(searchModel, meta)) {
                    Optional.ofNullable(meta.getLabels()).ifPresent(map -> addSearchResultsToModel(map, meta, ROLE, LABEL, searchModel, ""));
                    Optional.ofNullable(meta.getAnnotations()).ifPresent(map -> addSearchResultsToModel(map, meta, ROLE, ANNOTATION, searchModel, ""));
                }
            } catch (RuntimeException e) {
                searchModel.addSearchException(e);
                logger.error(e.getMessage(), e);
            }
        }
    }

    /**
     * Searches labels in NetworkPolicies by selected namespace.
     *
     * @param searchModel - search model
     */
    private void searchInNetworkPolicies(LabelsModel searchModel) {
        V1NetworkPolicyList networkPolicyList = kubeAPI.getV1NetworkPolicyList(searchModel.getSelectedNamespace(), searchModel);
        for (V1NetworkPolicy networkPolicy : networkPolicyList.getItems()) {
            try {
                V1ObjectMeta meta = networkPolicy.getMetadata();
                if (!skipKubeNamespace(searchModel, meta)) {
                    Optional.ofNullable(meta.getLabels()).ifPresent(map -> addSearchResultsToModel(map, meta, NETWORK_POLICY, LABEL, searchModel, ""));
                    Optional.ofNullable(meta.getAnnotations()).ifPresent(map -> addSearchResultsToModel(map, meta, NETWORK_POLICY, ANNOTATION, searchModel, ""));
                    Optional.ofNullable(networkPolicy.getSpec().getPodSelector()).ifPresent(selector -> addSearchResultsToModel(selector.getMatchLabels(), meta, NETWORK_POLICY, SELECTOR, searchModel,
                            getMatchExpressions(selector.getMatchExpressions())));
                }
            } catch (RuntimeException e) {
                searchModel.addSearchException(e);
                logger.error(e.getMessage(), e);
            }
        }
    }

    /**
     * Searches labels in PodDistributionBudgets by selected namespace.
     *
     * @param searchModel - search model
     */
    private void searchInPodDistributionBudgets(LabelsModel searchModel) {
        V1beta1PodDisruptionBudgetList podDisruptionBudgetsList = kubeAPI.getV1beta1PodDisruptionBudgetsList(searchModel.getSelectedNamespace(), searchModel);
        for (V1beta1PodDisruptionBudget budget : podDisruptionBudgetsList.getItems()) {
            try {
                V1ObjectMeta meta = budget.getMetadata();
                if (!skipKubeNamespace(searchModel, meta)) {
                    Optional.ofNullable(meta.getLabels()).ifPresent(map -> addSearchResultsToModel(map, meta, POD_DISRUPTION_BUDGET, LABEL, searchModel, ""));
                    Optional.ofNullable(meta.getAnnotations()).ifPresent(map -> addSearchResultsToModel(map, meta, POD_DISRUPTION_BUDGET, ANNOTATION, searchModel, ""));
                    Optional.ofNullable(budget.getSpec().getSelector()).ifPresent(selector -> addSearchResultsToModel(selector.getMatchLabels(), meta, POD_DISRUPTION_BUDGET, SELECTOR, searchModel,
                            getMatchExpressions(selector.getMatchExpressions())));
                }
            } catch (RuntimeException e) {
                searchModel.addSearchException(e);
                logger.error(e.getMessage(), e);
            }
        }
    }

    /**
     * Searches labels in PodSecurityPolicies by selected namespace.
     *
     * @param searchModel - search model
     */
    private void searchInPodSecurityPolicies(LabelsModel searchModel) {
        V1beta1PodSecurityPolicyList podSecurityPolicyList = kubeAPI.getPolicyV1beta1PodSecurityPolicyList(searchModel);
        for (V1beta1PodSecurityPolicy policy : podSecurityPolicyList.getItems()) {
            try {
                V1ObjectMeta meta = policy.getMetadata();
                if (!skipKubeNamespace(searchModel, meta)) {
                    Optional.ofNullable(meta.getLabels()).ifPresent(map -> addSearchResultsToModel(map, meta, POD_SECURITY_POLICY, LABEL, searchModel, ""));
                    Optional.ofNullable(meta.getAnnotations()).ifPresent(map -> addSearchResultsToModel(map, meta, POD_SECURITY_POLICY, ANNOTATION, searchModel, ""));
                }
            } catch (RuntimeException e) {
                searchModel.addSearchException(e);
                logger.error(e.getMessage(), e);
            }
        }
    }


    /**
     * Add search result to model.
     *
     * @param map              - labels, annotations or selectors map
     * @param metadata         - resource metadata
     * @param resource         - @{@link Resource}
     * @param resourceProperty - {@link ResourceProperty}
     * @param searchModel      - @{@link LabelsModel}
     * @param additionalInfo   - additional information
     */
    private void addSearchResultsToModel(Map<String, String> map, V1ObjectMeta metadata, Resource resource, ResourceProperty resourceProperty, LabelsModel searchModel, String additionalInfo) {
        for (Map.Entry<String, String> entry : map.entrySet()) {
            if (searchModel.isSkipHashLabels() && entry.getKey().endsWith("-hash")) {
                continue;
            }
            LabelResult newSearchResult = new LabelResult(searchModel.getSearchResults().size() + 1)
                    .setName(entry.getKey() + "=" + entry.getValue())
                    .setNamespace(metadata.getNamespace() == null ? "N/A" : metadata.getNamespace())
                    .setResourceType(resource)
                    .setResourceName(metadata.getName())
                    .setResourceProperty(resourceProperty)
                    .setAdditionalInfo(additionalInfo);
            searchModel.addSearchResult(newSearchResult);
        }
    }

    private boolean skipKubeNamespace(LabelsModel searchModel, V1ObjectMeta meta) {
        return searchModel.isSkipKubeNamespaces() && StringUtils.isNotBlank(meta.getNamespace()) && meta.getNamespace().startsWith("kube-");
    }


    private String getMatchExpressions(List<V1LabelSelectorRequirement> matchExpressions) {
        return matchExpressions == null ? "" : matchExpressions.toString();
    }

}
