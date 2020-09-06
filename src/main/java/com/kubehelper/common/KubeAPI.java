package com.kubehelper.common;

import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.AppsV1Api;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1PodList;
import io.kubernetes.client.openapi.models.V1ServiceList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.zkoss.zul.Messagebox;

/**
 * @author JDev
 */
@Service
public class KubeAPI {

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
            Messagebox.show(e.getMessage(), "Fetch Pods from Namespace Error", Messagebox.OK, Messagebox.ERROR);
            e.printStackTrace();
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
            Messagebox.show(e.getMessage(), "Fetch Services from Namespace Error", Messagebox.OK, Messagebox.ERROR);
            e.printStackTrace();
        }
        return new V1ServiceList();
    }

    public CoreV1Api getApiV1() {
        return apiV1;
    }
}
