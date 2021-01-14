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
package com.kubehelper.common;

import com.kubehelper.domain.models.PageModel;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.AppsV1Api;
import io.kubernetes.client.openapi.apis.BatchV1Api;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.apis.NetworkingV1Api;
import io.kubernetes.client.openapi.apis.PolicyV1beta1Api;
import io.kubernetes.client.openapi.apis.RbacAuthorizationV1beta1Api;
import io.kubernetes.client.openapi.models.V1ConfigMapList;
import io.kubernetes.client.openapi.models.V1DaemonSetList;
import io.kubernetes.client.openapi.models.V1DeploymentList;
import io.kubernetes.client.openapi.models.V1JobList;
import io.kubernetes.client.openapi.models.V1NamespaceList;
import io.kubernetes.client.openapi.models.V1NetworkPolicyList;
import io.kubernetes.client.openapi.models.V1PersistentVolumeClaimList;
import io.kubernetes.client.openapi.models.V1PersistentVolumeList;
import io.kubernetes.client.openapi.models.V1PodList;
import io.kubernetes.client.openapi.models.V1ReplicaSetList;
import io.kubernetes.client.openapi.models.V1SecretList;
import io.kubernetes.client.openapi.models.V1ServiceAccountList;
import io.kubernetes.client.openapi.models.V1ServiceList;
import io.kubernetes.client.openapi.models.V1StatefulSetList;
import io.kubernetes.client.openapi.models.V1beta1ClusterRoleBinding;
import io.kubernetes.client.openapi.models.V1beta1ClusterRoleBindingList;
import io.kubernetes.client.openapi.models.V1beta1ClusterRoleList;
import io.kubernetes.client.openapi.models.V1beta1PodDisruptionBudgetList;
import io.kubernetes.client.openapi.models.V1beta1PodSecurityPolicyList;
import io.kubernetes.client.openapi.models.V1beta1RoleBinding;
import io.kubernetes.client.openapi.models.V1beta1RoleBindingList;
import io.kubernetes.client.openapi.models.V1beta1RoleList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author JDev
 */
@Service
public class KubeAPI {

    private static Logger logger = LoggerFactory.getLogger(KubeAPI.class);

    @Autowired
    private CoreV1Api apiV1;

    @Autowired
    private AppsV1Api appsV1Api;

    @Autowired
    private BatchV1Api batchV1Api;

    @Autowired
    private RbacAuthorizationV1beta1Api rbacAuthorizationV1beta1Api;

    @Autowired
    private NetworkingV1Api networkingApi;

    @Autowired
    private PolicyV1beta1Api policyV1beta1Api;


    public V1DeploymentList getV1DeploymentList(String selectedNamespace, PageModel model) {
        try {
            if ("all".equals(selectedNamespace)) {
                return appsV1Api.listDeploymentForAllNamespaces(null, null, null, null, null, null, null, null, null);
            } else {
                return appsV1Api.listNamespacedDeployment(selectedNamespace, null, null, null, null, null, null, null, null, null);
            }
        } catch (ApiException e) {
            String errorMessage = String.format("Error at getV1DeploymentList: namespace=%s. Message: %s", selectedNamespace, e.getMessage());
            model.addException(errorMessage, e);
            logger.error(errorMessage, e);
        }
        return new V1DeploymentList();
    }

    public V1DaemonSetList getV1DaemonSetList(String selectedNamespace, PageModel model) {
        try {
            if ("all".equals(selectedNamespace)) {
                return appsV1Api.listDaemonSetForAllNamespaces(null, null, null, null, null, null, null, null, null);
            } else {
                return appsV1Api.listNamespacedDaemonSet(selectedNamespace, null, null, null, null, null, null, null, null, null);
            }
        } catch (ApiException e) {
            String errorMessage = String.format("Error at getV1DaemonSetList: namespace=%s. Message: %s", selectedNamespace, e.getMessage());
            model.addException(errorMessage, e);
            logger.error(errorMessage, e);
        }
        return new V1DaemonSetList();
    }

    public V1ReplicaSetList getV1ReplicaSetList(String selectedNamespace, PageModel model) {
        try {
            if ("all".equals(selectedNamespace)) {
                return appsV1Api.listReplicaSetForAllNamespaces(null, null, null, null, null, null, null, null, null);
            } else {
                return appsV1Api.listNamespacedReplicaSet(selectedNamespace, null, null, null, null, null, null, null, null, null);
            }
        } catch (ApiException e) {
            String errorMessage = String.format("Error at getV1ReplicaSetList: namespace=%s. Message: %s", selectedNamespace, e.getMessage());
            model.addException(errorMessage, e);
            logger.error(errorMessage, e);
        }
        return new V1ReplicaSetList();
    }

    public V1JobList getV1JobList(String selectedNamespace, PageModel model) {
        try {
            if ("all".equals(selectedNamespace)) {
                return batchV1Api.listJobForAllNamespaces(null, null, null, null, null, null, null, null, null);
            } else {
                return batchV1Api.listNamespacedJob(selectedNamespace, null, null, null, null, null, null, null, null, null);
            }
        } catch (ApiException e) {
            String errorMessage = String.format("Error at getV1JobList: namespace=%s. Message: %s", selectedNamespace, e.getMessage());
            model.addException(errorMessage, e);
            logger.error(errorMessage, e);
        }
        return new V1JobList();
    }

    public V1beta1RoleList getV1RolesList(String selectedNamespace, PageModel model) {
        try {
            if ("all".equals(selectedNamespace)) {
                return rbacAuthorizationV1beta1Api.listRoleForAllNamespaces(null, null, null, null, null, null, null, null, null);
            } else {
                return rbacAuthorizationV1beta1Api.listNamespacedRole(selectedNamespace, null, null, null, null, null, null, null, null, null);
            }
        } catch (ApiException e) {
            String errorMessage = String.format("Error at getV1RolesList: namespace=%s. Message: %s", selectedNamespace, e.getMessage());
            model.addException(errorMessage, e);
            logger.error(errorMessage, e);
        }
        return new V1beta1RoleList();
    }

    public V1beta1ClusterRoleList getV1ClusterRolesList(PageModel model) {
        try {
            return rbacAuthorizationV1beta1Api.listClusterRole(null, null, null, null, null, null, null, null, null);
        } catch (ApiException e) {
            String errorMessage = String.format("Error at getV1ClusterRolesList: Message: %s", e.getMessage());
            model.addException(errorMessage, e);
            logger.error(errorMessage, e);
        }
        return new V1beta1ClusterRoleList();
    }

    public V1beta1ClusterRoleBindingList getV1ClusterRolesBindingsList(PageModel model) {
        try {
            return rbacAuthorizationV1beta1Api.listClusterRoleBinding(null, null, null, null, null, null, null, null, null);
        } catch (ApiException e) {
            String errorMessage = String.format("Error at getV1ClusterRolesBindingsList: Message: %s", e.getMessage());
            model.addException(errorMessage, e);
            logger.error(errorMessage, e);
        }
        return new V1beta1ClusterRoleBindingList();
    }

    public V1beta1RoleBindingList getV1RolesBindingList(String selectedNamespace, PageModel model) {
        try {
            if ("all".equals(selectedNamespace)) {
                return rbacAuthorizationV1beta1Api.listRoleBindingForAllNamespaces(null, null, null, null, null, null, null, null, null);
            } else {
                return rbacAuthorizationV1beta1Api.listNamespacedRoleBinding(selectedNamespace, null, null, null, null, null, null, null, null, null);
            }
        } catch (ApiException e) {
            String errorMessage = String.format("Error at getV1RolesBindingList: namespace=%s. Message: %s", selectedNamespace, e.getMessage());
            model.addException(errorMessage, e);
            logger.error(errorMessage, e);
        }
        return new V1beta1RoleBindingList();
    }

    public V1beta1RoleBinding getV1RoleBinding(String roleName, String namespace, PageModel model) {
        try {
            return rbacAuthorizationV1beta1Api.readNamespacedRoleBinding(roleName, namespace, null);
        } catch (ApiException e) {
            String errorMessage = String.format("Error at getV1RoleBinding: roleName=%s, namespace=%s. Message: %s", roleName, namespace, e.getMessage());
            model.addException(errorMessage, e);
            logger.error(errorMessage, e);
        }
        return null;
    }

    public V1beta1ClusterRoleBinding getV1ClusterRoleBinding(String roleName, PageModel model) {
        try {
            return rbacAuthorizationV1beta1Api.readClusterRoleBinding(roleName, null);
        } catch (ApiException e) {
            String errorMessage = String.format("Error at getV1ClusterRoleBinding: roleName=%s. Message: %s", roleName, e.getMessage());
            model.addException(errorMessage, e);
            logger.error(errorMessage, e);
        }
        return null;
    }


    public V1NetworkPolicyList getV1NetworkPolicyList(String selectedNamespace, PageModel model) {
        try {
            if ("all".equals(selectedNamespace)) {
                return networkingApi.listNetworkPolicyForAllNamespaces(null, null, null, null, null, null, null, null, null);
            } else {
                return networkingApi.listNamespacedNetworkPolicy(selectedNamespace, null, null, null, null, null, null, null, null, null);
            }
        } catch (ApiException e) {
            String errorMessage = String.format("Error at getV1NetworkPolicyList: namespace=%s. Message: %s", selectedNamespace, e.getMessage());
            model.addException(errorMessage, e);
            logger.error(errorMessage, e);
        }
        return new V1NetworkPolicyList();
    }


    public V1beta1PodDisruptionBudgetList getV1beta1PodDisruptionBudgetsList(String selectedNamespace, PageModel model) {
        try {
            if ("all".equals(selectedNamespace)) {
                return policyV1beta1Api.listPodDisruptionBudgetForAllNamespaces(null, null, null, null, null, null, null, null, null);
            } else {
                return policyV1beta1Api.listNamespacedPodDisruptionBudget(selectedNamespace, null, null, null, null, null, null, null, null, null);
            }
        } catch (ApiException e) {
            String errorMessage = String.format("Error at getV1beta1PodDisruptionBudgetsList: namespace=%s. Message: %s", selectedNamespace, e.getMessage());
            model.addException(errorMessage, e);
            logger.error(errorMessage, e);
        }
        return new V1beta1PodDisruptionBudgetList();
    }


    public V1beta1PodSecurityPolicyList getPolicyV1beta1PodSecurityPolicyList(PageModel model) {
        try {
            return policyV1beta1Api.listPodSecurityPolicy(null, null, null, null, null, null, null, null, null);
        } catch (ApiException e) {
            String errorMessage = String.format("Error at getPolicyV1beta1PodSecurityPolicyList: Message: %s", e.getMessage());
            model.addException(errorMessage, e);
            logger.error(errorMessage, e);
        }
        return new V1beta1PodSecurityPolicyList();
    }


    public V1StatefulSetList getV1StatefulSetList(String selectedNamespace, PageModel model) {
        try {
            if ("all".equals(selectedNamespace)) {
                return appsV1Api.listStatefulSetForAllNamespaces(null, null, null, null, null, null, null, null, null);
            } else {
                return appsV1Api.listNamespacedStatefulSet(selectedNamespace, null, null, null, null, null, null, null, null, null);
            }
        } catch (ApiException e) {
            String errorMessage = String.format("Error at getV1StatefulSetList: namespace=%s. Message: %s", selectedNamespace, e.getMessage());
            model.addException(errorMessage, e);
            logger.error(errorMessage, e);
        }
        return new V1StatefulSetList();
    }


    /**
     * Get services list depends on namespace.
     *
     * @param selectedNamespace - selected namespace. all - all namespaces.
     * @return - list with found services.
     */
    public V1ServiceList getV1ServicesList(String selectedNamespace, PageModel model) {
        try {
            if ("all".equals(selectedNamespace)) {
                return apiV1.listServiceForAllNamespaces(null, null, null, null, null, null, null, null, null);
            } else {
                return apiV1.listNamespacedService(selectedNamespace, null, null, null, null, null, null, null, null, null);
            }
        } catch (ApiException e) {
            String errorMessage = String.format("Error at getV1ServicesList: namespace=%s. Message: %s", selectedNamespace, e.getMessage());
            model.addException(errorMessage, e);
            logger.error(errorMessage, e);
        }
        return new V1ServiceList();
    }

    public V1PersistentVolumeList getV1PersistentVolumesList(PageModel model) {
        try {
            return apiV1.listPersistentVolume(null, null, null, null, null, null, null, null, null);
        } catch (ApiException e) {
            String errorMessage = String.format("Error at getV1PersistentVolumesList: Message: %s", e.getMessage());
            model.addException(errorMessage, e);
            logger.error(errorMessage, e);
        }
        return new V1PersistentVolumeList();
    }

    public V1PersistentVolumeClaimList getV1PersistentVolumeClaimsList(String selectedNamespace, PageModel model) {
        try {
            if ("all".equals(selectedNamespace)) {
                return apiV1.listPersistentVolumeClaimForAllNamespaces(null, null, null, null, null, null, null, null, null);
            } else {
                return apiV1.listNamespacedPersistentVolumeClaim(selectedNamespace, null, null, null, null, null, null, null, null, null);
            }
        } catch (ApiException e) {
            String errorMessage = String.format("Error at getV1PersistentVolumeClaimsList: namespace=%s. Message: %s", selectedNamespace, e.getMessage());
            model.addException(errorMessage, e);
            logger.error(errorMessage, e);
        }
        return new V1PersistentVolumeClaimList();
    }


    public V1NamespaceList getV1NamespacesList(PageModel model) {
        try {
            return apiV1.listNamespace(null, null, null, null, null, null, null, null, null);
        } catch (ApiException e) {
            String errorMessage = String.format("Error at getV1NamespacesList: Message: %s", e.getMessage());
            model.addException(errorMessage, e);
            logger.error(errorMessage, e);
        }
        return new V1NamespaceList();
    }

    public V1PodList getV1PodsList(String selectedNamespace, PageModel model) {
        try {
            if ("all".equals(selectedNamespace)) {
                return apiV1.listPodForAllNamespaces(null, null, null, null, null, null, null, null, null);
            } else {
                return apiV1.listNamespacedPod(selectedNamespace, null, null, null, null, null, null, null, null, null);
            }
        } catch (ApiException e) {
            String errorMessage = String.format("Error at getV1PodsList: namespace=%s. Message: %s", selectedNamespace, e.getMessage());
            model.addException(errorMessage, e);
            logger.error(errorMessage, e);
        }
        return new V1PodList();
    }

    public V1SecretList getV1SecretsList(String selectedNamespace, PageModel model) {
        try {
            if ("all".equals(selectedNamespace)) {
                return apiV1.listSecretForAllNamespaces(null, null, null, null, null, null, null, null, null);
            } else {
                return apiV1.listNamespacedSecret(selectedNamespace, null, null, null, null, null, null, null, null, null);
            }
        } catch (ApiException e) {
            String errorMessage = String.format("Error at getV1SecretsList: namespace=%s. Message: %s", selectedNamespace, e.getMessage());
            model.addException(errorMessage, e);
            logger.error(errorMessage, e);
        }
        return new V1SecretList();
    }

    public V1ServiceAccountList getV1ServiceAccountsList(String selectedNamespace, PageModel model) {
        try {
            if ("all".equals(selectedNamespace)) {
                return apiV1.listServiceAccountForAllNamespaces(null, null, null, null, null, null, null, null, null);
            } else {
                return apiV1.listNamespacedServiceAccount(selectedNamespace, null, null, null, null, null, null, null, null, null);
            }
        } catch (ApiException e) {
            String errorMessage = String.format("Error at getV1ServiceAccountsList: namespace=%s. Message: %s", selectedNamespace, e.getMessage());
            model.addException(errorMessage, e);
            logger.error(errorMessage, e);
        }
        return new V1ServiceAccountList();
    }


    /**
     * Get config maps list depends on namespace.
     *
     * @param selectedNamespace - selected namespace. all - all namespaces.
     * @return - list with found config maps.
     */
    public V1ConfigMapList getV1ConfigMapsList(String selectedNamespace, PageModel model) {
        try {
            if ("all".equals(selectedNamespace)) {
                return apiV1.listConfigMapForAllNamespaces(null, null, null, null, null, null, null, null, null);
            } else {
                return apiV1.listNamespacedConfigMap(selectedNamespace, null, null, null, null, null, null, null, null, null);
            }
        } catch (ApiException e) {
            String errorMessage = String.format("Error at getV1ConfigMapsList: namespace=%s. Message: %s", selectedNamespace, e.getMessage());
            model.addException(errorMessage, e);
            logger.error(errorMessage, e);
        }
        return new V1ConfigMapList();
    }

}
