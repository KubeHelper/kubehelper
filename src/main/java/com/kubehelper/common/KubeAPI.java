package com.kubehelper.common;

import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.AppsV1Api;
import io.kubernetes.client.openapi.apis.AuditregistrationV1alpha1Api;
import io.kubernetes.client.openapi.apis.AuthenticationV1Api;
import io.kubernetes.client.openapi.apis.AuthorizationV1Api;
import io.kubernetes.client.openapi.apis.BatchV1Api;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.apis.DiscoveryV1beta1Api;
import io.kubernetes.client.openapi.apis.NetworkingV1Api;
import io.kubernetes.client.openapi.apis.PolicyV1beta1Api;
import io.kubernetes.client.openapi.apis.RbacAuthorizationV1beta1Api;
import io.kubernetes.client.openapi.apis.SettingsV1alpha1Api;
import io.kubernetes.client.openapi.models.PolicyV1beta1PodSecurityPolicyList;
import io.kubernetes.client.openapi.models.V1ConfigMapList;
import io.kubernetes.client.openapi.models.V1ControllerRevisionList;
import io.kubernetes.client.openapi.models.V1DaemonSetList;
import io.kubernetes.client.openapi.models.V1DeploymentList;
import io.kubernetes.client.openapi.models.V1EventList;
import io.kubernetes.client.openapi.models.V1JobList;
import io.kubernetes.client.openapi.models.V1NamespaceList;
import io.kubernetes.client.openapi.models.V1NetworkPolicyList;
import io.kubernetes.client.openapi.models.V1NodeList;
import io.kubernetes.client.openapi.models.V1PersistentVolumeClaimList;
import io.kubernetes.client.openapi.models.V1PersistentVolumeList;
import io.kubernetes.client.openapi.models.V1PodList;
import io.kubernetes.client.openapi.models.V1ReplicaSetList;
import io.kubernetes.client.openapi.models.V1SecretList;
import io.kubernetes.client.openapi.models.V1ServiceAccountList;
import io.kubernetes.client.openapi.models.V1ServiceList;
import io.kubernetes.client.openapi.models.V1StatefulSetList;
import io.kubernetes.client.openapi.models.V1alpha1PodPresetList;
import io.kubernetes.client.openapi.models.V1beta1ClusterRoleBindingList;
import io.kubernetes.client.openapi.models.V1beta1ClusterRoleList;
import io.kubernetes.client.openapi.models.V1beta1PodDisruptionBudgetList;
import io.kubernetes.client.openapi.models.V1beta1RoleBindingList;
import io.kubernetes.client.openapi.models.V1beta1RoleList;
import io.kubernetes.client.util.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zul.Window;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

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

    public void testApis() {
        try {
            AuthenticationV1Api authenticationApi = new AuthenticationV1Api(Config.defaultClient());
            SettingsV1alpha1Api settingsApi = new SettingsV1alpha1Api(Config.defaultClient());
            V1alpha1PodPresetList v1alpha1PodPresetList = settingsApi.listPodPresetForAllNamespaces(null, null, null, null, null, null, null, null, null);
            AuthorizationV1Api authorizationApi = new AuthorizationV1Api(Config.defaultClient()); //TOKEn REVIEW
            DiscoveryV1beta1Api discoveryApi = new DiscoveryV1beta1Api(Config.defaultClient());
            AuditregistrationV1alpha1Api auditregistrationApi = new AuditregistrationV1alpha1Api(Config.defaultClient());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ApiException e) {
            e.printStackTrace();
        }
    }


    public V1DeploymentList getV1DeploymentList(String selectedNamespace) {
        try {
            if ("all".equals(selectedNamespace)) {
                return appsV1Api.listDeploymentForAllNamespaces(null, null, null, null, null, null, null, null, null);
            } else {
                return appsV1Api.listNamespacedDeployment(selectedNamespace, null, null, null, null, null, null, null, null, null);
            }
        } catch (ApiException e) {
            showErrorDialog(e);
        }
        return new V1DeploymentList();
    }

    public V1DaemonSetList getV1DaemonSetList(String selectedNamespace) {
        try {
            if ("all".equals(selectedNamespace)) {
                return appsV1Api.listDaemonSetForAllNamespaces(null, null, null, null, null, null, null, null, null);
            } else {
                return appsV1Api.listNamespacedDaemonSet(selectedNamespace, null, null, null, null, null, null, null, null, null);
            }
        } catch (ApiException e) {
            showErrorDialog(e);
        }
        return new V1DaemonSetList();
    }

    public V1ReplicaSetList getV1ReplicaSetList(String selectedNamespace) {
        try {
            if ("all".equals(selectedNamespace)) {
                return appsV1Api.listReplicaSetForAllNamespaces(null, null, null, null, null, null, null, null, null);
            } else {
                return appsV1Api.listNamespacedReplicaSet(selectedNamespace, null, null, null, null, null, null, null, null, null);
            }
        } catch (ApiException e) {
            showErrorDialog(e);
        }
        return new V1ReplicaSetList();
    }

    public V1JobList getV1JobList(String selectedNamespace) {
        try {
            if ("all".equals(selectedNamespace)) {
                return batchV1Api.listJobForAllNamespaces(null, null, null, null, null, null, null, null, null);
            } else {
                return batchV1Api.listNamespacedJob(selectedNamespace, null, null, null, null, null, null, null, null, null);
            }
        } catch (ApiException e) {
            showErrorDialog(e);
        }
        return new V1JobList();
    }

    public V1beta1RoleList getV1RolesList(String selectedNamespace) {
        try {
            if ("all".equals(selectedNamespace)) {
                return rbacAuthorizationV1beta1Api.listRoleForAllNamespaces(null, null, null, null, null, null, null, null, null);
            } else {
                return rbacAuthorizationV1beta1Api.listNamespacedRole(selectedNamespace, null, null, null, null, null, null, null, null, null);
            }
        } catch (ApiException e) {
            showErrorDialog(e);
        }
        return new V1beta1RoleList();
    }

    public V1beta1ClusterRoleList getV1ClusterRolesList() {
        try {
            return rbacAuthorizationV1beta1Api.listClusterRole(null, null, null, null, null, null, null, null, null);
        } catch (ApiException e) {
            showErrorDialog(e);
        }
        return new V1beta1ClusterRoleList();
    }

    public V1beta1ClusterRoleBindingList getV1ClusterRolesBindingsList() {
        try {
            return rbacAuthorizationV1beta1Api.listClusterRoleBinding(null, null, null, null, null, null, null, null, null);
        } catch (ApiException e) {
            showErrorDialog(e);
        }
        return new V1beta1ClusterRoleBindingList();
    }

    public V1beta1RoleBindingList getV1RolesBindingList(String selectedNamespace) {
        try {
            if ("all".equals(selectedNamespace)) {
                return rbacAuthorizationV1beta1Api.listRoleBindingForAllNamespaces(null, null, null, null, null, null, null, null, null);
            } else {
                return rbacAuthorizationV1beta1Api.listNamespacedRoleBinding(selectedNamespace, null, null, null, null, null, null, null, null, null);
            }
        } catch (ApiException e) {
            showErrorDialog(e);
        }
        return new V1beta1RoleBindingList();
    }


    public V1NetworkPolicyList getV1NetworkPolicyList(String selectedNamespace) {
        try {
            if ("all".equals(selectedNamespace)) {
                return networkingApi.listNetworkPolicyForAllNamespaces(null, null, null, null, null, null, null, null, null);
            } else {
                return networkingApi.listNamespacedNetworkPolicy(selectedNamespace, null, null, null, null, null, null, null, null, null);
            }
        } catch (ApiException e) {
            showErrorDialog(e);
        }
        return new V1NetworkPolicyList();
    }


    public V1beta1PodDisruptionBudgetList getV1beta1PodDisruptionBudgetsList(String selectedNamespace) {
        try {
            if ("all".equals(selectedNamespace)) {
                return policyV1beta1Api.listPodDisruptionBudgetForAllNamespaces(null, null, null, null, null, null, null, null, null);
            } else {
                return policyV1beta1Api.listNamespacedPodDisruptionBudget(selectedNamespace, null, null, null, null, null, null, null, null, null);
            }
        } catch (ApiException e) {
            showErrorDialog(e);
        }
        return new V1beta1PodDisruptionBudgetList();
    }


    public PolicyV1beta1PodSecurityPolicyList getPolicyV1beta1PodSecurityPolicyList() {
        try {
            return policyV1beta1Api.listPodSecurityPolicy(null, null, null, null, null, null, null, null, null);
        } catch (ApiException e) {
            showErrorDialog(e);
        }
        return new PolicyV1beta1PodSecurityPolicyList();
    }


    public V1StatefulSetList getV1StatefulSetList(String selectedNamespace) {
        try {
            if ("all".equals(selectedNamespace)) {
                return appsV1Api.listStatefulSetForAllNamespaces(null, null, null, null, null, null, null, null, null);
            } else {
                return appsV1Api.listNamespacedStatefulSet(selectedNamespace, null, null, null, null, null, null, null, null, null);
            }
        } catch (ApiException e) {
            showErrorDialog(e);
        }
        return new V1StatefulSetList();
    }


    /**
     * Get services list depends on namespace.
     *
     * @param selectedNamespace - selected namespace. all - all namespaces.
     * @return - list with found services.
     */
    public V1ServiceList getV1ServicesList(String selectedNamespace) {
        try {
            if ("all".equals(selectedNamespace)) {
                return apiV1.listServiceForAllNamespaces(null, null, null, null, null, null, null, null, null);
            } else {
                return apiV1.listNamespacedService(selectedNamespace, null, null, null, null, null, null, null, null, null);
            }
        } catch (ApiException e) {
            showErrorDialog(e);
        }
        return new V1ServiceList();
    }

    public V1PersistentVolumeList getV1PersistentVolumesList() {
        try {
            return apiV1.listPersistentVolume(null, null, null, null, null, null, null, null, null);
        } catch (ApiException e) {
            showErrorDialog(e);
        }
        return new V1PersistentVolumeList();
    }

    public V1PersistentVolumeClaimList getV1PersistentVolumeClaimsList(String selectedNamespace) {
        try {
            if ("all".equals(selectedNamespace)) {
                return apiV1.listPersistentVolumeClaimForAllNamespaces(null, null, null, null, null, null, null, null, null);
            } else {
                return apiV1.listNamespacedPersistentVolumeClaim(selectedNamespace, null, null, null, null, null, null, null, null, null);
            }
        } catch (ApiException e) {
            showErrorDialog(e);
        }
        return new V1PersistentVolumeClaimList();
    }


    public V1NamespaceList getV1NamespacesList() {
        try {
            return apiV1.listNamespace(null, null, null, null, null, null, null, null, null);
        } catch (ApiException e) {
            showErrorDialog(e);
        }
        return new V1NamespaceList();
    }

    public V1PodList getV1PodsList(String selectedNamespace) {
        try {
            if ("all".equals(selectedNamespace)) {
                return apiV1.listPodForAllNamespaces(null, null, null, null, null, null, null, null, null);
            } else {
                return apiV1.listNamespacedPod(selectedNamespace, null, null, null, null, null, null, null, null, null);
            }
        } catch (ApiException e) {
            showErrorDialog(e);
        }
        return new V1PodList();
    }

    public V1SecretList getV1SecretsList(String selectedNamespace) {
        try {
            if ("all".equals(selectedNamespace)) {
                return apiV1.listSecretForAllNamespaces(null, null, null, null, null, null, null, null, null);
            } else {
                return apiV1.listNamespacedSecret(selectedNamespace, null, null, null, null, null, null, null, null, null);
            }
        } catch (ApiException e) {
            showErrorDialog(e);
        }
        return new V1SecretList();
    }

    public V1ServiceAccountList getV1ServiceAccountsList(String selectedNamespace) {
        try {
            if ("all".equals(selectedNamespace)) {
                return apiV1.listServiceAccountForAllNamespaces(null, null, null, null, null, null, null, null, null);
            } else {
                return apiV1.listNamespacedServiceAccount(selectedNamespace, null, null, null, null, null, null, null, null, null);
            }
        } catch (ApiException e) {
            showErrorDialog(e);
        }
        return new V1ServiceAccountList();
    }

    public V1ControllerRevisionList getV1ControllerRevisionsList(String selectedNamespace) {
        try {
            if ("all".equals(selectedNamespace)) {
                return appsV1Api.listControllerRevisionForAllNamespaces(null, null, null, null, null, null, null, null, null);
            } else {
                return appsV1Api.listNamespacedControllerRevision(selectedNamespace, null, null, null, null, null, null, null, null, null);
            }
        } catch (ApiException e) {
            showErrorDialog(e);
        }
        return new V1ControllerRevisionList();
    }


    /**
     * Get config maps list depends on namespace.
     *
     * @param selectedNamespace - selected namespace. all - all namespaces.
     * @return - list with found config maps.
     */
    public V1ConfigMapList getV1ConfigMapsList(String selectedNamespace) {
        try {
            if ("all".equals(selectedNamespace)) {
                return apiV1.listConfigMapForAllNamespaces(null, null, null, null, null, null, null, null, null);
            } else {
                return apiV1.listNamespacedConfigMap(selectedNamespace, null, null, null, null, null, null, null, null, null);
            }
        } catch (ApiException e) {
            showErrorDialog(e);
        }
        return new V1ConfigMapList();
    }

    public CoreV1Api getApiV1() {
        return apiV1;
    }

    private void showErrorDialog(Exception exception) {
        logger.error(exception.getMessage(), exception);
        Window window = (Window) Executions.createComponents("~./zul/components/errors.zul", null, Map.of("errors", Arrays.asList(new KubeHelperException(exception))));
        window.doModal();
    }


    public V1NodeList getV1NodesList(String selectedNamespace) {
        try {
            if ("all".equals(selectedNamespace)) {
                return apiV1.listNode(null, null, null, null, null, null, null, null, null);
            }
        } catch (ApiException e) {
            showErrorDialog(e);
        }
        return new V1NodeList();
    }

    /**
     * Get endpoints list depends on namespace.
     *
     * @param selectedNamespace - selected namespace. all - all namespaces.
     * @return - list with found endpoints.
     */
    public V1EventList getV1EventList(String selectedNamespace) {
        try {
            if ("all".equals(selectedNamespace)) {
                return apiV1.listEventForAllNamespaces(null, null, null, null, null, null, null, null, null);
            } else {
                return apiV1.listNamespacedEvent(selectedNamespace, null, null, null, null, null, null, null, null, null);
            }
        } catch (ApiException e) {
            showErrorDialog(e);
        }
        return new V1EventList();
    }
}
