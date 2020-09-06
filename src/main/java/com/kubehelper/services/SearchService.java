package com.kubehelper.services;

import com.kubehelper.common.KubeAPI;
import com.kubehelper.common.Resource;
import com.kubehelper.domain.models.SearchModel;
import io.kubernetes.client.Exec;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.models.V1Container;
import io.kubernetes.client.openapi.models.V1EnvVar;
import io.kubernetes.client.openapi.models.V1ObjectMeta;
import io.kubernetes.client.openapi.models.V1Pod;
import io.kubernetes.client.openapi.models.V1PodSpec;
import io.kubernetes.client.openapi.models.V1PodStatus;
import org.apache.commons.lang3.ObjectUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.util.Objects;
import java.util.Properties;

/**
 * @author JDev
 */
@Service
public class SearchService {

    @Autowired
    private KubeAPI kubeAPI;

    @Autowired
    private Exec exec;

    public void search(String selectedNamespace, String searchString, SearchModel searchModel) {

        for (V1Pod pod : kubeAPI.getV1PodList(selectedNamespace).getItems()) {

            Properties podEnvironmentVars = getPodEnvironmentVars(pod);

            for (V1Container container : Objects.requireNonNull(pod.getSpec()).getContainers()) {
                if (ObjectUtils.isNotEmpty(container.getEnv())) {
                    for (V1EnvVar v1EnvVar : container.getEnv()) {
                        try {
                            if (ObjectUtils.isEmpty(v1EnvVar.getValueFrom())) {
                                composeValueFromToSearchResult(null, null, null, v1EnvVar, pod, container, searchModel, searchString);
                            } else {
                                if (ObjectUtils.isNotEmpty(v1EnvVar.getValueFrom().getFieldRef())) {

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
                        } catch (NoSuchFieldException | IllegalAccessException e) {
                            //TODO set all errors to one block errors collapsable after search result
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    private void composeValueFromToSearchResult(Field field, Object fieldObject, String fieldPath, V1EnvVar v1EnvVar, V1Pod pod, V1Container container, SearchModel searchModel, String searchString) throws IllegalAccessException {
        String envValue, envName, additionalInfo = "";
        envName = v1EnvVar.getName().toLowerCase();
        if (ObjectUtils.isNotEmpty(field)) {
            field.setAccessible(true);
            envValue = ((String) field.get(fieldObject)).toLowerCase();
            additionalInfo = "ValueFrom: " + fieldPath;
        } else {
            envValue = v1EnvVar.getValue().toLowerCase();
        }
        if (envName.contains(searchString.toLowerCase()) || envValue.contains(searchString.toLowerCase())) {
            String resourceName = pod.getMetadata().getName() + "/" + container.getName();
            String foundString = v1EnvVar.getName() + "=" + envValue;
            searchModel.addSearchResult(pod.getMetadata().getNamespace(), Resource.ENV_VARIABLE, resourceName, additionalInfo, foundString, getParsedCreationTime(pod.getMetadata().getCreationTimestamp()));
        }
    }

    private String getParsedCreationTime(DateTime dateTime) {
        return dateTime.toString("dd.MM.yyyy HH:mm:ss");
    }

    //    TODO add feature, to mark env variables which comes from container itself
    private Properties getPodEnvironmentVars(V1Pod pod) {
        Properties envVars = new Properties();
        String[] command = new String[]{"env"};
        try {
            Process process = exec.exec(pod, command, false);
            BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = br.readLine()) != null) {
                int idx = line.indexOf('=');
                System.out.println("Line:" + line);
                String key = line.substring(0, idx);
                String value = line.substring(idx + 1);
                envVars.setProperty(key, value);
                System.out.println(key + " = " + value);
            }
        } catch (ApiException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return envVars;
    }
}
