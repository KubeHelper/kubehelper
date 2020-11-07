package com.kubehelper.common;

import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1ConfigMapList;
import io.kubernetes.client.openapi.models.V1PodList;
import io.kubernetes.client.openapi.models.V1ServiceList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zul.Window;

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

//    @Autowired
//    private AppsV1Api appsV1Api;

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
}
