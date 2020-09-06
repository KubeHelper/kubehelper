package com.kubehelper.services;

import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.AppsV1Api;
import io.kubernetes.client.openapi.apis.ExtensionsV1beta1Api;
import io.kubernetes.client.openapi.models.ExtensionsV1beta1IngressList;
import io.kubernetes.client.openapi.models.V1DaemonSetList;
import io.kubernetes.client.openapi.models.V1DeploymentList;
import io.kubernetes.client.openapi.models.V1ReplicaSetList;
import io.kubernetes.client.openapi.models.V1StatefulSetList;
import org.zkoss.zul.Messagebox;

/**
 * @author JDev
 */
public class TempService {

//    @Autowired
    private AppsV1Api appsV1Api;

//    @Autowired
    private ExtensionsV1beta1Api extensionsV1beta1Api;

    public void get(String selectedNamespace) {

        V1DaemonSetList v1DaemonSetsList = getV1DaemonSetsList(selectedNamespace);
        V1DeploymentList v1DeploymentsList = getV1DeploymentsList(selectedNamespace);
        V1ReplicaSetList v1ReplicaSetsList = getV1ReplicaSetsList(selectedNamespace);
        V1StatefulSetList v1StatefulSetsList = getV1StatefulSetsList(selectedNamespace);
        ExtensionsV1beta1IngressList v1IngressesList = getV1IngressesList(selectedNamespace);

    }

    private V1DaemonSetList getV1DaemonSetsList(String selectedNamespace) {

        try {
            if ("all".equals(selectedNamespace)) {
                return appsV1Api.listDaemonSetForAllNamespaces(null, null, null, null, null, null, null, null, null);
            } else {
                return appsV1Api.listNamespacedDaemonSet(selectedNamespace, null, null, null, null, null, null, null, null, null);
            }
        } catch (ApiException e) {
            Messagebox.show(e.getMessage(), "Fetch Services from Namespace Error", Messagebox.OK, Messagebox.ERROR);
            e.printStackTrace();
        }
        return new V1DaemonSetList();
    }

    private V1DeploymentList getV1DeploymentsList(String selectedNamespace) {
        try {
            if ("all".equals(selectedNamespace)) {
                return appsV1Api.listNamespacedDeployment(null, null, null, null, null, null, null, null, null, null);
            } else {
                return appsV1Api.listNamespacedDeployment(selectedNamespace, null, null, null, null, null, null, null, null, null);
            }
        } catch (ApiException e) {
            Messagebox.show(e.getMessage(), "Fetch Services from Namespace Error", Messagebox.OK, Messagebox.ERROR);
            e.printStackTrace();
        }
        return new V1DeploymentList();
    }

    private V1StatefulSetList getV1StatefulSetsList(String selectedNamespace) {
        try {
            if ("all".equals(selectedNamespace)) {
                return appsV1Api.listStatefulSetForAllNamespaces(null, null, null, null, null, null, null, null, null);
            } else {
                return appsV1Api.listNamespacedStatefulSet(selectedNamespace, null, null, null, null, null, null, null, null, null);
            }
        } catch (ApiException e) {
            Messagebox.show(e.getMessage(), "Fetch Services from Namespace Error", Messagebox.OK, Messagebox.ERROR);
            e.printStackTrace();
        }
        return new V1StatefulSetList();
    }

    private V1ReplicaSetList getV1ReplicaSetsList(String selectedNamespace) {
        try {
            if ("all".equals(selectedNamespace)) {
                return appsV1Api.listReplicaSetForAllNamespaces(null, null, null, null, null, null, null, null, null);
            } else {
                return appsV1Api.listNamespacedReplicaSet(selectedNamespace, null, null, null, null, null, null, null, null, null);
            }
        } catch (ApiException e) {
            Messagebox.show(e.getMessage(), "Fetch Services from Namespace Error", Messagebox.OK, Messagebox.ERROR);
            e.printStackTrace();
        }
        return new V1ReplicaSetList();
    }

    private ExtensionsV1beta1IngressList getV1IngressesList(String selectedNamespace) {
        try {
            if ("all".equals(selectedNamespace)) {
                return extensionsV1beta1Api.listIngressForAllNamespaces(null, null, null, null, null, null, null, null, null);
            } else {
                return extensionsV1beta1Api.listNamespacedIngress(selectedNamespace, null, null, null, null, null, null, null, null, null);
            }
        } catch (ApiException e) {
            Messagebox.show(e.getMessage(), "Fetch Ingresses from Namespace Error", Messagebox.OK, Messagebox.ERROR);
            e.printStackTrace();
        }
        return new ExtensionsV1beta1IngressList();
    }

    //            V1NodeList v1NodeList = api.listNode(null, null, null, null, null, null, null, null, null);
//            V1EndpointsList v1EndpointsList = api.listEndpointsForAllNamespaces(null, null, null, null, null, null, null, null, null);
//            V1SecretList v1SecretList = api.listSecretForAllNamespaces(null, null, null, null, null, null, null, null, null);
}
