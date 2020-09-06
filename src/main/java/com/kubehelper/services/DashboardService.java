package com.kubehelper.services;

import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1NamespaceList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author JDev
 */
@Service
public class DashboardService {

    @Autowired
    private CoreV1Api api;

    public List<String> getAllNamespaces() {
        List<String> namespaces = new ArrayList<String>(Arrays.asList("all"));
        try {
            V1NamespaceList v1NamespacesList = api.listNamespace(null, false, null, null, null, 0, null, 30, false);
            v1NamespacesList.getItems().forEach(v1Namespace -> namespaces.add(v1Namespace.getMetadata().getName()));
        } catch (ApiException e) {
//            TODO Exception in toast
            e.printStackTrace();
        }
        return namespaces;
    }

    //            V1NodeList v1NodeList = api.listNode(null, null, null, null, null, null, null, null, null);
//            V1EndpointsList v1EndpointsList = api.listEndpointsForAllNamespaces(null, null, null, null, null, null, null, null, null);
//            V1SecretList v1SecretList = api.listSecretForAllNamespaces(null, null, null, null, null, null, null, null, null);
//            Exec exec = new Exec(api.getApiClient());
////            exec.exec()
}
