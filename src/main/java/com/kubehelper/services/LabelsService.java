package com.kubehelper.services;

import com.kubehelper.common.KubeAPI;
import com.kubehelper.common.Resource;
import com.kubehelper.domain.models.LabelsModel;
import com.kubehelper.domain.models.SearchModel;
import com.kubehelper.domain.results.SearchResult;
import io.kubernetes.client.Exec;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.models.V1ConfigMap;
import io.kubernetes.client.openapi.models.V1ConfigMapKeySelector;
import io.kubernetes.client.openapi.models.V1ConfigMapList;
import io.kubernetes.client.openapi.models.V1Container;
import io.kubernetes.client.openapi.models.V1EnvVar;
import io.kubernetes.client.openapi.models.V1EnvVarSource;
import io.kubernetes.client.openapi.models.V1ObjectMeta;
import io.kubernetes.client.openapi.models.V1PersistentVolume;
import io.kubernetes.client.openapi.models.V1PersistentVolumeClaim;
import io.kubernetes.client.openapi.models.V1PersistentVolumeClaimList;
import io.kubernetes.client.openapi.models.V1PersistentVolumeList;
import io.kubernetes.client.openapi.models.V1Pod;
import io.kubernetes.client.openapi.models.V1PodList;
import io.kubernetes.client.openapi.models.V1PodSpec;
import io.kubernetes.client.openapi.models.V1PodStatus;
import io.kubernetes.client.openapi.models.V1ResourceFieldSelector;
import io.kubernetes.client.openapi.models.V1Secret;
import io.kubernetes.client.openapi.models.V1SecretKeySelector;
import io.kubernetes.client.openapi.models.V1SecretList;
import io.kubernetes.client.openapi.models.V1Service;
import io.kubernetes.client.openapi.models.V1ServiceAccount;
import io.kubernetes.client.openapi.models.V1ServiceAccountList;
import io.kubernetes.client.openapi.models.V1ServiceList;
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
import java.util.Set;
import java.util.StringJoiner;

import static com.kubehelper.common.Resource.CONFIG_MAP;
import static com.kubehelper.common.Resource.ENV_VARIABLE;
import static com.kubehelper.common.Resource.NAMESPACE;
import static com.kubehelper.common.Resource.PERSISTENT_VOLUME;
import static com.kubehelper.common.Resource.PERSISTENT_VOLUME_CLAIM;
import static com.kubehelper.common.Resource.SECRET;
import static com.kubehelper.common.Resource.SERVICE;
import static com.kubehelper.common.Resource.SERVICE_ACCOUNT;
import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * Labels service.
 *
 * @author JDev
 */
@Service
public class LabelsService {

    private static Logger logger = LoggerFactory.getLogger(LabelsService.class);

//    TODO fix progress label
//    private String progressLabel = "";
//    private int currentItemNumber;
//    private int totalItems;

    @Autowired
    private KubeAPI kubeAPI;

    @Autowired
    private Exec exec;

    /**
     * Searches string selected kubernetes resources by selected namespace.
     *
     * @param labelsModel       - search model
     */
    public void search(LabelsModel labelsModel, Set<Resource> selectedResources) {

        labelsModel.getSearchResults().clear();
        labelsModel.getSearchExceptions().clear();
//        try {
//
//            if (selectedResources.contains(ENV_VARIABLE)) {
//                searchInEnvironmentVariables(searchModel);
//            }
//            if (selectedResources.contains(CONFIG_MAP)) {
//                searchInConfigMaps(searchModel);
//            }
//            if (selectedResources.contains(SERVICE)) {
//                searchInServices(searchModel);
//            }
//            if (selectedResources.contains(POD)) {
//                searchInPods(searchModel);
//            }
//            if (selectedResources.contains(PERSISTENT_VOLUME) && "all".equals(searchModel.getSelectedNamespace())) {
//                searchInPersistentVolumes(searchModel);
//            }
//            if (selectedResources.contains(PERSISTENT_VOLUME_CLAIM)) {
//                searchInPersistentVolumeClaims(searchModel);
//            }
//            if (selectedResources.contains(SECRET)) {
//                searchInSecrets(searchModel);
//            }
//            if (selectedResources.contains(SERVICE_ACCOUNT)) {
//                searchInServiceAccounts(searchModel);
//            }
//        } catch (RuntimeException e) {
//            searchModel.addSearchException(e);
//            logger.error(e.getMessage(), e);
//        }
//        currentItemNumber = 0;
//        totalItems = podList.size();
    }


    /**
     * Searches string in Pods by selected namespace.
     *
     * @param searchModel       - search model
     */
    private void searchInPods(SearchModel searchModel) {
        V1PodList podsList = kubeAPI.getV1PodsList(searchModel.getSelectedNamespace());
        for (V1Pod pod : podsList.getItems()) {
            if (isStringsContainsSearchString(searchModel.getSearchString(), pod.getMetadata().getName())) {
                addSearchResultToModel(pod.getMetadata(), searchModel, NAMESPACE, pod.getMetadata().getName(), pod.getMetadata().getName(), "");
            }
        }
    }

    /**
     * Searches string in PersistentVolumes by selected namespace.
     *
     * @param searchModel - search model
     */
    private void searchInPersistentVolumes(SearchModel searchModel) {
        V1PersistentVolumeList persistentVolumesList = kubeAPI.getV1PersistentVolumesList();
        for (V1PersistentVolume persistentVolume : persistentVolumesList.getItems()) {
            if (isStringsContainsSearchString(searchModel.getSearchString(), persistentVolume.getMetadata().getName())) {
                addSearchResultToModel(persistentVolume.getMetadata(), searchModel, PERSISTENT_VOLUME, persistentVolume.getMetadata().getName(), persistentVolume.getMetadata().getName(), "");
            }
        }
    }

    /**
     * Searches string in PersistentVolumeClaims by selected namespace.
     *
     * @param searchModel       - search model
     */
    private void searchInPersistentVolumeClaims(SearchModel searchModel) {
        V1PersistentVolumeClaimList persistentVolumeClaimsList = kubeAPI.getV1PersistentVolumeClaimsList(searchModel.getSelectedNamespace());
        for (V1PersistentVolumeClaim persistentVolumeClaim : persistentVolumeClaimsList.getItems()) {
            if (isStringsContainsSearchString(searchModel.getSearchString(), persistentVolumeClaim.getMetadata().getName())) {
                addSearchResultToModel(persistentVolumeClaim.getMetadata(), searchModel, PERSISTENT_VOLUME_CLAIM, persistentVolumeClaim.getMetadata().getName(), persistentVolumeClaim.getMetadata().getName(), "");
            }
        }
    }

    /**
     * Searches string in Services by selected namespace.
     *
     * @param searchModel       - search model
     */
    private void searchInServices(SearchModel searchModel) {
        V1ServiceList servicesList = kubeAPI.getV1ServicesList(searchModel.getSelectedNamespace());
        for (V1Service service : servicesList.getItems()) {
            if (skipKubeNamespace(searchModel, service.getMetadata())) {
                continue;
            }
            if (isStringsContainsSearchString(searchModel.getSearchString(), service.getMetadata().getName())) {
                addSearchResultToModel(service.getMetadata(), searchModel, SERVICE, service.getMetadata().getName(), service.getMetadata().getName(), "");
            }
        }
    }

    /**
     * Searches string in ServiceAccounts by selected namespace.
     *
     * @param searchModel       - search model
     */
    private void searchInServiceAccounts(SearchModel searchModel) {
        V1ServiceAccountList serviceAccountsList = kubeAPI.getV1ServiceAccountsList(searchModel.getSelectedNamespace());
        for (V1ServiceAccount serviceAccount : serviceAccountsList.getItems()) {
            if (skipKubeNamespace(searchModel, serviceAccount.getMetadata())) {
                continue;
            }
            StringJoiner additionalInfo = new StringJoiner(",", "[", "]");
            if (isStringsContainsSearchString(searchModel.getSearchString(), serviceAccount.getMetadata().getName())) {
                if (ObjectUtils.isNotEmpty(serviceAccount.getSecrets())) {
                    serviceAccount.getSecrets().forEach(secretObject -> additionalInfo.add(secretObject.getName()));
                }
                addSearchResultToModel(serviceAccount.getMetadata(), searchModel, SERVICE_ACCOUNT, serviceAccount.getMetadata().getName(), serviceAccount.getMetadata().getName(),
                        "secrets " + additionalInfo.toString());
            }
        }

    }

    /**
     * Searches string in Secrets by selected namespace.
     *
     * @param searchModel       - search model
     */
    private void searchInSecrets(SearchModel searchModel) {
        V1SecretList secretsList = kubeAPI.getV1SecretsList(searchModel.getSelectedNamespace());
        for (V1Secret secret : secretsList.getItems()) {
            if (skipKubeNamespace(searchModel, secret.getMetadata())) {
                continue;
            }
            if (ObjectUtils.isNotEmpty(secret.getData())) {
                secret.getData().forEach((secretName, secretValue) -> {
                    if (isStringsContainsSearchString(searchModel.getSearchString(), secret.getMetadata().getName(), secretName)) {
                        String secretType = secret.getType() == null ? "null" : secret.getType();
                        String foundString = secret.getMetadata().getName() + " [" + secretType + "] : " + secretName;
                        addSearchResultToModel(secret.getMetadata(), searchModel, SECRET, secret.getMetadata().getName(), foundString, new String(secretValue, UTF_8));
                    }
                });
            }
        }
    }

    /**
     * Searches string in ConfigMaps by selected namespace.
     *
     * @param searchModel       - search model
     */
    private void searchInConfigMaps(SearchModel searchModel) {
        V1ConfigMapList configMapsList = kubeAPI.getV1ConfigMapsList(searchModel.getSelectedNamespace());
        for (V1ConfigMap configMap : configMapsList.getItems()) {
            if (skipKubeNamespace(searchModel, configMap.getMetadata())) {
                continue;
            }
            if (ObjectUtils.isNotEmpty(configMap.getData())) {
                configMap.getData().forEach((configName, configValue) -> {
                    if (isStringsContainsSearchString(searchModel.getSearchString(), configMap.getMetadata().getName(), configName, configValue)) {
                        addSearchResultToModel(configMap.getMetadata(), searchModel, CONFIG_MAP, configMap.getMetadata().getName(), configName, configValue);
                    }
                });
            }
        }
    }

    /**
     * Searches for environment variable in pods by selected namespace.
     *
     * @param searchModel       - search model
     */
    private void searchInEnvironmentVariables(SearchModel searchModel) {

        List<V1Pod> podList = kubeAPI.getV1PodList(searchModel.getSelectedNamespace()).getItems();
        for (V1Pod pod : podList) {

            //skips search in kube- namespace
            if (skipKubeNamespace(searchModel, pod.getMetadata())) {
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
                searchModel.addSearchException(e);
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
        if (isStringsContainsSearchString(searchModel.getSearchString(), envName, envValue)) {
            String resourceName = pod.getMetadata().getName() + " [" + container.getName() + "]";
            String composedFoundString = v1EnvVar.getName() + "=" + envValue;
            addSearchResultToModel(pod.getMetadata(), searchModel, ENV_VARIABLE, resourceName, composedFoundString, additionalInfo);
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
            searchModel.addSearchException(e);
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
            if (isEnvVarNotFound && isStringsContainsSearchString(searchModel.getSearchString(), key, envValue)) {
                String composedFoundString = key + "=" + envValue;
                addSearchResultToModel(pod.getMetadata(), searchModel, ENV_VARIABLE, pod.getMetadata().getName(), composedFoundString, "Native Environment Variable");
            }
        }
    }


    /**
     * Add new found variable/text/string to search result.
     *
     * @param metadata       - kubernetes resource/object metadata
     * @param searchModel    - search model
     * @param resource       - kubernetes @{@link Resource}
     * @param resourceName   - resource name
     * @param foundString    - found string
     * @param additionalInfo - additional info
     */
    private void addSearchResultToModel(V1ObjectMeta metadata, SearchModel searchModel, Resource resource, String resourceName, String foundString, String additionalInfo) {
        SearchResult newSearchResult = new SearchResult(searchModel.getSearchResults().size() + 1)
                .setNamespace(metadata.getNamespace() == null ? "N/A" : metadata.getNamespace())
                .setResourceType(resource)
                .setResourceName(resourceName)
                .setFoundString(foundString)
                .setAdditionalInfo(additionalInfo)
                .setCreationTime(getParsedCreationTime(metadata.getCreationTimestamp()));
        searchModel.addSearchResult(newSearchResult)
                .addResourceNameFilter(metadata.getName());
    }

    private boolean isStringsContainsSearchString(String searchString, String... strings) {
        for (String s : strings) {
            if (StringUtils.containsIgnoreCase(s, searchString)) {
                return true;
            }
        }
        return false;
    }

    private String getParsedCreationTime(DateTime dateTime) {
        return dateTime.toString("dd.MM.yyyy HH:mm:ss");
    }

    private String getEnvValueFromFieldObject(Field field, Object fieldObject) throws IllegalAccessException {
        field.setAccessible(true);
        return ((String) field.get(fieldObject)).toLowerCase();
    }

    private boolean skipKubeNamespace(SearchModel searchModel, V1ObjectMeta meta) {
        return searchModel.isSkipKubeNamespaces() && meta.getNamespace().startsWith("kube-");
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
