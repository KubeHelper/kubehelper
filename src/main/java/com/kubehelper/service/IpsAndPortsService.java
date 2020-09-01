package com.kubehelper.service;

import com.kubehelper.common.Resource;
import com.kubehelper.model.SearchModel;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1Container;
import io.kubernetes.client.openapi.models.V1EnvVar;
import io.kubernetes.client.openapi.models.V1ObjectMeta;
import io.kubernetes.client.openapi.models.V1Pod;
import io.kubernetes.client.openapi.models.V1PodList;
import io.kubernetes.client.openapi.models.V1PodSpec;
import io.kubernetes.client.openapi.models.V1PodStatus;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.zkoss.zul.Messagebox;

import java.lang.reflect.Field;
import java.util.Set;

/**
 * @author JDev
 */
@Service
public class IpsAndPortsService {

    @Autowired
    private CoreV1Api api;

    public void search(String selectedNamespace, String searchString, boolean caseSensitiveSearch, Set<Resource> selectedResources, SearchModel searchModel) {

//        difference between all namespaces and one, get namespace from pod
        V1PodList list = getV1PodList(selectedNamespace);
        for (V1Pod item : list.getItems()) {
            fillModelWithEnvVariablesFromPod(item, searchModel, searchString);
//            search.addNamespace(item.getSpec().getHostname());
//            System.out.println(item.getMetadata().getName());
        }

//        for (V1Pod item : list.getItems()) {
//            System.out.println(item.getSpec().getContainers().get(0).getEnv());
//        }
    }

    private void fillModelWithEnvVariablesFromPod(V1Pod pod, SearchModel searchModel, String searchString) {
        for (V1Container container : pod.getSpec().getContainers()) {
            if (ObjectUtils.isNotEmpty(container.getEnv())) {
                for (V1EnvVar v1EnvVar : container.getEnv()) {
                    if (ObjectUtils.isEmpty(v1EnvVar.getValueFrom())) {
                        String envName = v1EnvVar.getName().toLowerCase();
                        String envValue = v1EnvVar.getValue().toLowerCase();
                        if (envName.contains(searchString.toLowerCase()) || envValue.contains(searchString.toLowerCase())) {
                            String resourceName = pod.getMetadata().getName() + "/" + container.getName();
                            String foundString = v1EnvVar.getName() + "=" + v1EnvVar.getValue();
                            String creationTime = pod.getMetadata().getCreationTimestamp().toString("dd.MM.yyyy HH:mm:ss");
                            searchModel.addSearchResult(pod.getMetadata().getNamespace(), Resource.ENV_VARIABLE, resourceName, "", foundString, creationTime);
                        }
                    } else {
                        if (ObjectUtils.isNotEmpty(v1EnvVar.getValueFrom().getFieldRef())) {
                            try {
                                String fieldPath = v1EnvVar.getValueFrom().getFieldRef().getFieldPath();
                                String declaredField = fieldPath.substring(fieldPath.lastIndexOf(".") + 1, fieldPath.length());
                                if (v1EnvVar.getValueFrom().getFieldRef().getFieldPath().startsWith("metadata")) {
                                    Field filed = V1ObjectMeta.class.getDeclaredField(declaredField);
                                    composeValueFromToSearchResult(filed, pod.getMetadata(), fieldPath, v1EnvVar, pod, container, searchModel, searchString);
                                }
                                if (fieldPath.startsWith("status")) {
                                    Field filed = V1PodStatus.class.getDeclaredField(declaredField);
                                    composeValueFromToSearchResult(filed, pod.getStatus(), fieldPath, v1EnvVar, pod, container, searchModel, searchString);
                                }
                                if (fieldPath.startsWith("spec")) {
                                    Field filed = V1PodSpec.class.getDeclaredField(declaredField);
                                    composeValueFromToSearchResult(filed, pod.getSpec(), fieldPath, v1EnvVar, pod, container, searchModel, searchString);
                                }
                            } catch (NoSuchFieldException | IllegalAccessException e) {
                                //TODO set all errors to one block errors collapsable after search result
                                e.printStackTrace();
                            }
                        }

//                        if (ObjectUtils.isNotEmpty(v1EnvVar.getValueFrom().getResourceFieldRef())) {
//                            try {
//                                String fieldResource = v1EnvVar.getValueFrom().getResourceFieldRef().getResource();
//                                if (fieldResource.startsWith("requests")) {
//                                    Field filed = V1ObjectMeta.class.getDeclaredField(fieldResource.substring(fieldResource.lastIndexOf(".") + 1, fieldResource.length()));
//                                    filed.setAccessible(true);
//                                    String envName = v1EnvVar.getName().toLowerCase();
//                                    String envValue = ((String) filed.get(pod.getMetadata())).toLowerCase();
//                                    if (envName.contains(searchString.toLowerCase()) || envValue.contains(searchString.toLowerCase())) {
//                                        String resourceName = pod.getMetadata().getName() + "/" + container.getName();
//                                        String foundString = v1EnvVar.getName() + "=" + envValue;
////                                        searchModel.addSearchResult(pod.getMetadata().getNamespace(), Resource.ENV_VARIABLE, resourceName, "ValueFrom: " + fieldPath, foundString);
//                                    }
//                                }
//                            } catch (NoSuchFieldException | IllegalAccessException e) {
//                                e.printStackTrace();
//                            }
//                        }
                    }
                }
            }
        }
    }

    private void composeValueFromToSearchResult(Field field, Object fieldObject, String fieldPath, V1EnvVar v1EnvVar, V1Pod pod, V1Container container, SearchModel searchModel, String searchString) throws IllegalAccessException {
        field.setAccessible(true);
        String envName = v1EnvVar.getName().toLowerCase();
        String envValue = ((String) field.get(fieldObject)).toLowerCase();
        if (envName.contains(searchString.toLowerCase()) || envValue.contains(searchString.toLowerCase())) {
            String resourceName = pod.getMetadata().getName() + "/" + container.getName();
            String foundString = v1EnvVar.getName() + "=" + envValue;
            String creationTime = pod.getMetadata().getCreationTimestamp().toString("dd.MM.yyyy HH:mm:ss");
            searchModel.addSearchResult(pod.getMetadata().getNamespace(), Resource.ENV_VARIABLE, resourceName, "ValueFrom: " + fieldPath, foundString, creationTime);
        }
    }

//    private void checkEnvValuesAndPutInModel(String resourceName,String v1EnvPart,SearchModel searchModel, String namespace){
//        if (StringUtils.isNotBlank(v1EnvPart) && v1EnvPart.toLowerCase().contains(searchString.toLowerCase())) {
//            String foundString = v1EnvVar.getName() + "=" + v1EnvVar.getValue();
//            searchModel.addSearchResult(selectedNamespace, Resource.ENV_VARIABLES, resourceName, foundString);
//        }
//    }

    private V1PodList getV1PodList(String selectedNamespace) {
        try {
            if ("all".equals(selectedNamespace)) {
                return api.listPodForAllNamespaces(null, null, null, null, null, null, null, null, null);
            } else {
                return api.listNamespacedPod(selectedNamespace, null, null, null, null, null, null, null, null, null);
            }
        } catch (ApiException e) {
            Messagebox.show(e.getMessage(), "Fetch Pods from Namespace Error", Messagebox.OK, Messagebox.ERROR);
            e.printStackTrace();
        }
        return new V1PodList();
    }
}
