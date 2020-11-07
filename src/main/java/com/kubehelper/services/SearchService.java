package com.kubehelper.services;

import com.kubehelper.common.KubeAPI;
import com.kubehelper.common.KubeHelperException;
import com.kubehelper.common.Resource;
import com.kubehelper.domain.models.SearchModel;
import com.kubehelper.domain.results.SearchResult;
import io.kubernetes.client.Exec;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.models.V1ConfigMapKeySelector;
import io.kubernetes.client.openapi.models.V1Container;
import io.kubernetes.client.openapi.models.V1EnvVar;
import io.kubernetes.client.openapi.models.V1EnvVarSource;
import io.kubernetes.client.openapi.models.V1ObjectMeta;
import io.kubernetes.client.openapi.models.V1Pod;
import io.kubernetes.client.openapi.models.V1PodSpec;
import io.kubernetes.client.openapi.models.V1PodStatus;
import io.kubernetes.client.openapi.models.V1ResourceFieldSelector;
import io.kubernetes.client.openapi.models.V1SecretKeySelector;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Search service.
 *
 * @author JDev
 */
@Service
public class SearchService {

    private static Logger logger = LoggerFactory.getLogger(SearchService.class);

//    TODO fix progress label
//    private String progressLabel = "";
//    private int currentItemNumber;
//    private int totalItems;

    @Autowired
    private KubeAPI kubeAPI;

    @Autowired
    private Exec exec;

    /**
     * Searches string in pods of selected namespace.
     *
     * @param selectedNamespace - selected namespace. all - all namespaces.
     * @param searchModel       - search model
     */
    public void search(String selectedNamespace, SearchModel searchModel) {

        List<V1Pod> podList = kubeAPI.getV1PodList(selectedNamespace).getItems();

        searchModel.getSearchResults().clear();
        searchModel.getSearchExceptions().clear();
//        currentItemNumber = 0;
//        totalItems = podList.size();

        for (V1Pod pod : podList) {

            //skips search in kube- namespace
            if (searchModel.isSkipKubeNamespaces() && pod.getMetadata().getNamespace().startsWith("kube-")) {
                continue;
            }
            try {
                for (V1Container container : pod.getSpec().getContainers()) {
                    if (ObjectUtils.isNotEmpty(container.getEnv())) {
                        for (V1EnvVar v1EnvVar : container.getEnv()) {
                            if (ObjectUtils.isEmpty(v1EnvVar.getValueFrom())) {
                                //get simple Environment variables
                                composeValueFromToSearchResult(v1EnvVar, pod, container, searchModel, null, null, null);
                            } else {
                                V1EnvVarSource valueFrom = v1EnvVar.getValueFrom();

                                //get Environment variables from FieldRef
                                if (ObjectUtils.isNotEmpty(valueFrom.getFieldRef())) {
                                    String fieldPath = valueFrom.getFieldRef().getFieldPath();
                                    String declaredField = fieldPath.substring(fieldPath.lastIndexOf(".") + 1);
                                    if (valueFrom.getFieldRef().getFieldPath().startsWith("metadata")) {
                                        String envValueFromFieldObject = getEnvValueFromFieldObject(V1ObjectMeta.class.getDeclaredField(declaredField), pod.getMetadata());
                                        composeValueFromToSearchResult(v1EnvVar, pod, container, searchModel, "fieldRef", fieldPath, envValueFromFieldObject);
                                    }
                                    if (fieldPath.startsWith("status")) {
                                        String envValueFromFieldObject = getEnvValueFromFieldObject(V1PodStatus.class.getDeclaredField(declaredField), pod.getStatus());
                                        composeValueFromToSearchResult(v1EnvVar, pod, container, searchModel, "fieldRef", fieldPath, envValueFromFieldObject);
                                    }
                                    if (fieldPath.startsWith("spec")) {
                                        String envValueFromFieldObject = getEnvValueFromFieldObject(V1PodSpec.class.getDeclaredField(declaredField), pod.getSpec());
                                        composeValueFromToSearchResult(v1EnvVar, pod, container, searchModel, "fieldRef", fieldPath, envValueFromFieldObject);
                                    }
                                }

                                //get Environment variables from ResourceFieldRef
                                if (ObjectUtils.isNotEmpty(valueFrom.getResourceFieldRef())) {
                                    V1ResourceFieldSelector resourceFieldRef = valueFrom.getResourceFieldRef();
                                    StringBuilder valueFromValue = new StringBuilder();
                                    valueFromValue.append("{ containerName: ").append(resourceFieldRef.getContainerName() == null ? "null" : resourceFieldRef.getContainerName()).append(", ");
                                    valueFromValue.append("divisor: ").append(resourceFieldRef.getContainerName() == null ? "null" : resourceFieldRef.getContainerName()).append(", ");
                                    valueFromValue.append("resource: ").append(resourceFieldRef.getContainerName() == null ? "null" : resourceFieldRef.getContainerName()).append(" }");
                                    composeValueFromToSearchResult(v1EnvVar, pod, container, searchModel, "resourceFieldRef", "", valueFromValue.toString());
                                }

                                //get Environment variables from ConfigMapKeyRef
                                if (ObjectUtils.isNotEmpty(valueFrom.getConfigMapKeyRef())) {
                                    V1ConfigMapKeySelector configMapKeyRef = valueFrom.getConfigMapKeyRef();
                                    StringBuilder valueFromValue = new StringBuilder();
                                    valueFromValue.append("{ key: ").append(configMapKeyRef.getKey() == null ? "null" : configMapKeyRef.getKey()).append(", ");
                                    valueFromValue.append("name: ").append(configMapKeyRef.getName() == null ? "null" : configMapKeyRef.getName()).append(" }");
                                    composeValueFromToSearchResult(v1EnvVar, pod, container, searchModel, "configMapKeyRef", "", valueFromValue.toString());
                                }

                                //get Environment variables from SecretKeyRef
                                if (ObjectUtils.isNotEmpty(valueFrom.getSecretKeyRef())) {
                                    V1SecretKeySelector secretKeyRef = valueFrom.getSecretKeyRef();
                                    StringBuilder valueFromValue = new StringBuilder();
                                    valueFromValue.append("{ key: ").append(secretKeyRef.getKey() == null ? "null" : secretKeyRef.getKey()).append(", ");
                                    valueFromValue.append("name: ").append(secretKeyRef.getName() == null ? "null" : secretKeyRef.getName()).append(" }");
                                    composeValueFromToSearchResult(v1EnvVar, pod, container, searchModel, "secretKeyRef", "", valueFromValue.toString());
                                }
                            }

                        }
                    }
                }
            } catch (NoSuchFieldException | IllegalAccessException e) {
                searchModel.addSearchException(new KubeHelperException(e));
                logger.error(e.getMessage(), e);
            }
            //collect all env vars from pod with exec
            if (!searchModel.isSkipNativeEnvVars()) {
                mergeNativeEnvVarsToSearchResult(pod, searchModel);
            }
        }

    }

    /**
     * Check, calculate and compose pod valueFrom field to search model.
     *
     * @param v1EnvVar        - valueFrom Environment Variable
     * @param pod             - pod
     * @param container       - container
     * @param searchModel     - search model
     * @param valueFromSource - environment from value source
     * @param valueFromValue  - environment from value
     * @param envValue        - environment value
     */
    private void composeValueFromToSearchResult(V1EnvVar v1EnvVar, V1Pod pod, V1Container container, SearchModel searchModel, String valueFromSource, String valueFromValue, String envValue) {
        String envName, additionalInfo = "";
        envName = v1EnvVar.getName().toLowerCase();
        if (StringUtils.isNotBlank(valueFromSource)) {
            additionalInfo = "Value From: [" + valueFromSource + "] " + valueFromValue;
        } else {
            additionalInfo = "Simple Environment Variable";
            envValue = v1EnvVar.getValue().toLowerCase();
        }
        if (envName.contains(searchModel.getSearchString().toLowerCase()) || envValue.contains(searchModel.getSearchString().toLowerCase())) {
            String resourceName = pod.getMetadata().getName() + " [" + container.getName() + "]";
            addNewSearchResultToModel(pod, searchModel, v1EnvVar.getName(), envValue, resourceName, additionalInfo);
        }
    }

    /**
     * Executes env command on pod, and collects native environment variables.
     *
     * @param pod         - kubernetes pod
     * @param searchModel - search model
     * @return - properties object with key=value native environment variables map.
     */
    private Properties getPodEnvironmentVars(V1Pod pod, SearchModel searchModel) {
        Properties envVars = new Properties();
        String[] command = new String[]{"env"};
        try {
            Process process = exec.exec(pod, command, false);
            BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = br.readLine()) != null) {
                int idx = line.indexOf('=');
                if (idx != -1) {
                    String key = line.substring(0, idx);
                    String value = line.substring(idx + 1);
                    envVars.setProperty(key, value);
                }
            }
        } catch (ApiException | IOException | RuntimeException e) {
            searchModel.addSearchException(new KubeHelperException(e));
            logger.error(e.getMessage(), e);
        }
        return envVars;
    }


    /**
     * Merge native environment variables (env) into search model.
     *
     * @param pod         - kubernetes pod
     * @param searchModel - search model
     */
    private void mergeNativeEnvVarsToSearchResult(V1Pod pod, SearchModel searchModel) {
        Properties podEnvironmentVars = getPodEnvironmentVars(pod, searchModel);
        //copy list, because concurrent modification exception
        List<SearchResult> searchResultList = new ArrayList<>(searchModel.getSearchResults().getInnerList());
        for (Map.Entry<Object, Object> entry : podEnvironmentVars.entrySet()) {
            String key = (String) entry.getKey(), envValue = (String) entry.getValue();
            boolean isEnvVarNotFound = true;
            for (SearchResult searchResult : searchResultList) {
                //Check if environment variable already exists in search model
                if (searchResult.getResourceName().startsWith(pod.getMetadata().getName()) && searchResult.getFoundString().startsWith(key)) {
                    isEnvVarNotFound = false;
                    break;
                }
            }
            //add new native environment variable
            if (isEnvVarNotFound && (key.contains(searchModel.getSearchString().toLowerCase()) || envValue.contains(searchModel.getSearchString().toLowerCase()))) {
                addNewSearchResultToModel(pod, searchModel, key, envValue, pod.getMetadata().getName(), "Native Environment Variable");
            }
        }
    }

    /**
     * Add new environment variable to search result
     *
     * @param pod            - kubernetes pod
     * @param searchModel    - search model
     * @param envKey         - environment key
     * @param envValue       - environment value
     * @param resourceName   - resource name
     * @param additionalInfo - additional info
     */
    private void addNewSearchResultToModel(V1Pod pod, SearchModel searchModel, String envKey, String envValue, String resourceName, String additionalInfo) {
        SearchResult newSearchResult = new SearchResult(searchModel.getSearchResults().size() + 1)
                .setNamespace(pod.getMetadata().getNamespace())
                .setResourceType(Resource.ENV_VARIABLE)
                .setResourceName(resourceName)
                .setAdditionalInfo(additionalInfo)
                .setFoundString(envKey + "=" + envValue)
                .setCreationTime(getParsedCreationTime(pod.getMetadata().getCreationTimestamp()));
        searchModel.addSearchResult(newSearchResult)
                .addResourceNameFilter(pod.getMetadata().getName());
    }

    private String getParsedCreationTime(DateTime dateTime) {
        return dateTime.toString("dd.MM.yyyy HH:mm:ss");
    }

    private String getEnvValueFromFieldObject(Field field, Object fieldObject) throws IllegalAccessException {
        field.setAccessible(true);
        return ((String) field.get(fieldObject)).toLowerCase();
    }


//    private void buildProgressLabel() {
//        currentItemNumber++;
//        this.progressLabel = String.format("Parsed %d of %d.", currentItemNumber, totalItems);
//        this.progressUpdateListener.firePropertyChange("progressLabel", progressLabel, progressLabel);
//        System.out.println("BuildProgressLabel :"+progressLabel);
//    }
//
//    public String getProgressLabel() {
//        System.out.println("getProgressLabel :"+progressLabel);
//        return progressLabel;
//    }
}
