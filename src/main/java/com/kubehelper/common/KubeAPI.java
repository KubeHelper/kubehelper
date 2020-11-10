package com.kubehelper.common;

import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.ApisApi;
import io.kubernetes.client.openapi.apis.AppsV1Api;
import io.kubernetes.client.openapi.apis.AuditregistrationV1alpha1Api;
import io.kubernetes.client.openapi.apis.AuthenticationV1Api;
import io.kubernetes.client.openapi.apis.AuthorizationV1Api;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.apis.DiscoveryV1beta1Api;
import io.kubernetes.client.openapi.apis.LogsApi;
import io.kubernetes.client.openapi.apis.NetworkingV1Api;
import io.kubernetes.client.openapi.apis.PolicyV1beta1Api;
import io.kubernetes.client.openapi.apis.SettingsV1alpha1Api;
import io.kubernetes.client.openapi.models.V1ConfigMapList;
import io.kubernetes.client.openapi.models.V1ControllerRevisionList;
import io.kubernetes.client.openapi.models.V1EventList;
import io.kubernetes.client.openapi.models.V1NamespaceList;
import io.kubernetes.client.openapi.models.V1NodeList;
import io.kubernetes.client.openapi.models.V1PersistentVolumeClaimList;
import io.kubernetes.client.openapi.models.V1PersistentVolumeList;
import io.kubernetes.client.openapi.models.V1PodList;
import io.kubernetes.client.openapi.models.V1SecretList;
import io.kubernetes.client.openapi.models.V1ServiceAccountList;
import io.kubernetes.client.openapi.models.V1ServiceList;
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

    public void testApis() throws IOException {

        AuthenticationV1Api authenticationApi = new AuthenticationV1Api(Config.defaultClient());
        SettingsV1alpha1Api settingsApi = new SettingsV1alpha1Api(Config.defaultClient());
        AuthorizationV1Api authorizationApi = new AuthorizationV1Api(Config.defaultClient());
        DiscoveryV1beta1Api discoveryApi = new DiscoveryV1beta1Api(Config.defaultClient());
        AuditregistrationV1alpha1Api auditregistrationApi = new AuditregistrationV1alpha1Api(Config.defaultClient());
        PolicyV1beta1Api policyApi = new PolicyV1beta1Api(Config.defaultClient());
        LogsApi logsApi = new LogsApi(Config.defaultClient());
        NetworkingV1Api networkingApi = new NetworkingV1Api(Config.defaultClient());
        ApisApi apisApi = new ApisApi(Config.defaultClient());
    }

    /**
     * Get pods list depends on namespace.
     *
     * @param selectedNamespace - selected namespace. all - all namespaces.
     * @return - list with found pods.
     */
    public V1PodList getV1PodList(String selectedNamespace) {
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
