package com.kubehelper.service;

import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1NamespaceList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.zkoss.zul.Messagebox;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author JDev
 */
@Service
public class CommonService {

    @Autowired
    private CoreV1Api api;

    public List<String> getAllNamespaces() {
        List<String> namespaces = new ArrayList<String>(Arrays.asList("all"));
        V1NamespaceList v1NamespacesList = null;
        try {
            v1NamespacesList = api.listNamespace(null, false, null, null, null, 0, null, 30, false);
            v1NamespacesList.getItems().forEach(v1Namespace -> namespaces.add(v1Namespace.getMetadata().getName()));
        } catch (ApiException e) {
            Messagebox.show(e.getMessage(), "Fetch Namespaces Error", Messagebox.OK, Messagebox.ERROR);
            e.printStackTrace();
        }
        return namespaces;
    }
}
