package com.kubehelper.services;

import com.kubehelper.common.KubeAPI;
import com.kubehelper.common.Resource;
import com.kubehelper.domain.models.SearchModel;
import com.kubehelper.domain.results.SearchResult;
import com.kubehelper.viewmodels.SearchVM;
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

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

/**
 * @author JDev
 */
@Service
public class SearchService {

//    TODO fix progress label
//    private String progressLabel = "";
//    private int currentItemNumber;
//    private int totalItems;

    @Autowired
    private KubeAPI kubeAPI;

    @Autowired
    private Exec exec;


    private PropertyChangeSupport progressUpdateListener = new PropertyChangeSupport(SearchVM.class);


    public void search(String selectedNamespace, String searchString, SearchModel searchModel) {

        List<V1Pod> podList = kubeAPI.getV1PodList(selectedNamespace).getItems();

        searchModel.getSearchResults().clear();
//        currentItemNumber = 0;
//        totalItems = podList.size();

        for (V1Pod pod : podList) {
//            buildProgressLabel();

            Properties podEnvironmentVars = getPodEnvironmentVars(pod);
            try {
                for (V1Container container : pod.getSpec().getContainers()) {
                    if (ObjectUtils.isNotEmpty(container.getEnv())) {
                        for (V1EnvVar v1EnvVar : container.getEnv()) {
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

                        }
                    }
                }
            } catch (NoSuchFieldException | IllegalAccessException e) {
                //TODO set all errors to one block errors collapsable after search result
                e.printStackTrace();
            }
        }
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        this.progressUpdateListener.addPropertyChangeListener(listener);
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
            String resourceName = pod.getMetadata().getName() + " [" + container.getName() + " ]";
            String foundString = v1EnvVar.getName() + "=" + envValue;
            SearchResult searchResult = new SearchResult(searchModel.getSearchResults().size() + 1)
                    .setNamespace(pod.getMetadata().getNamespace())
                    .setResourceType(Resource.ENV_VARIABLE)
                    .setResourceName(resourceName)
                    .setAdditionalInfo(additionalInfo)
                    .setFoundString(foundString)
                    .setCreationTime(getParsedCreationTime(pod.getMetadata().getCreationTimestamp()));
            searchModel.addSearchResult(searchResult)
                    .addResourceNameFilter(pod.getMetadata().getName());
        }
    }

    private String getParsedCreationTime(DateTime dateTime) {
        return dateTime.toString("dd.MM.yyyy HH:mm:ss");
    }

    //    TODO add feature, to mark env variables which comes from container itself
    private Properties getPodEnvironmentVars(V1Pod pod) {
        Properties envVars = new Properties();
//        String[] command = new String[]{"env"};
//        try {
//            Process process = exec.exec(pod, command, false);
//            BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
//            String line;
//            while ((line = br.readLine()) != null) {
//                int idx = line.indexOf('=');
//                System.out.println("Line:" + line);
//                String key = line.substring(0, idx);
//                String value = line.substring(idx + 1);
//                envVars.setProperty(key, value);
//                System.out.println(key + " = " + value);
//            }
//        } catch (ApiException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        return envVars;
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