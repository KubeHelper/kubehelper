package com.kubehelper.services;

import com.kubehelper.common.Resource;
import com.kubehelper.domain.models.IpsAndPortsModel;
import com.kubehelper.domain.results.IpsAndPortsResult;
import io.kubernetes.client.custom.IntOrString;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.AppsV1Api;
import io.kubernetes.client.openapi.apis.AppsV1beta1Api;
import io.kubernetes.client.openapi.apis.AppsV1beta2Api;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.apis.ExtensionsV1beta1Api;
import io.kubernetes.client.openapi.models.ExtensionsV1beta1DeploymentList;
import io.kubernetes.client.openapi.models.ExtensionsV1beta1IngressList;
import io.kubernetes.client.openapi.models.V1Container;
import io.kubernetes.client.openapi.models.V1Pod;
import io.kubernetes.client.openapi.models.V1PodList;
import io.kubernetes.client.openapi.models.V1Service;
import io.kubernetes.client.openapi.models.V1ServiceList;
import io.kubernetes.client.openapi.models.V1ServicePort;
import io.kubernetes.client.openapi.models.V1beta1DaemonSetList;
import io.kubernetes.client.openapi.models.V1beta1ReplicaSetList;
import org.apache.commons.lang3.ObjectUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.zkoss.zul.Messagebox;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.function.Consumer;

/**
 * @author JDev
 */
@Service
public class IpsAndPortsService {

    private final String LS = System.getProperty("line.separator");

    @Autowired
    private CoreV1Api api;

    @Autowired
    private ExtensionsV1beta1Api extensionsV1beta1Api;

    @Autowired
    private AppsV1beta1Api appsV1beta1Api;

    @Autowired
    private AppsV1Api appsV1Api;

    @Autowired
    private AppsV1beta2Api appsV1beta2Api;


    public void get(String selectedNamespace, IpsAndPortsModel ipsAndPortsModel) {
        ipsAndPortsModel.getIpsAndPortsResults().clear();

//        getV1DaemonSetsList(selectedNamespace);
//        getV1DeploymentsList(selectedNamespace);
//        getV1ReplicaSetsList(selectedNamespace);
//        getV1IngressesList(selectedNamespace);

        fillModelWithPodsInfo(selectedNamespace, ipsAndPortsModel);
        fillModelWithServicesInfo(selectedNamespace, ipsAndPortsModel);
    }

    private void fillModelWithPodsInfo(String selectedNamespace, IpsAndPortsModel ipsAndPortsModel) {
        for (V1Pod pod : getV1PodList(selectedNamespace).getItems()) {
            IpsAndPortsResult ipsAndPortsResult = new IpsAndPortsResult(ipsAndPortsModel.getIpsAndPortsResults().size() + 1);
            StringJoiner portsJoiner = getStringsJoiner();
            StringJoiner containerNamesJoiner = getStringsJoiner();
            String resourceName = pod.getMetadata().getName() + ": ";
            StringBuilder servicePortsBuilder = new StringBuilder(), detailsBuilder = new StringBuilder();

//          compose pod ips
            for (V1Container container : Objects.requireNonNull(pod.getSpec()).getContainers()) {
                if (ObjectUtils.isNotEmpty(container.getPorts())) {
                    container.getPorts().forEach(port -> portsJoiner.add(String.valueOf(port.getContainerPort())));
                }
                containerNamesJoiner.add(container.getName());
            }

//          compose service data to details
            Optional.ofNullable(pod.getMetadata().getName()).ifPresent(s -> detailsBuilder.append(ipsAndPortsResult.getId() + " => " + s + LS));

//          compose service labels
            StringJoiner labelsJoiner = getStringsJoiner();
            Optional.ofNullable(pod.getMetadata().getLabels()).ifPresent(labels -> {
                for (Map.Entry<String, String> entry : labels.entrySet()) {
                    labelsJoiner.add(entry.getKey() + ": " + entry.getValue());
                }
            });
            detailsBuilder.append("Labels: ").append(labelsJoiner.toString()).append(LS);

//          compose pod annotations
            StringJoiner annotationsJoiner = getStringsJoiner();
            Optional.ofNullable(pod.getMetadata().getAnnotations()).ifPresent(annotations -> {
                for (Map.Entry<String, String> entry : annotations.entrySet()) {
                    annotationsJoiner.add(entry.getKey() + ": " + entry.getValue());
                }
            });
            detailsBuilder.append("Annotations: ").append(annotationsJoiner.toString()).append(LS);

//          compose other service data into details
            Optional.ofNullable(pod.getApiVersion()).ifPresent(p -> detailsBuilder.append("ApiVersion: " + p + LS));
            Optional.ofNullable(pod.getMetadata().getSelfLink()).ifPresent(s -> detailsBuilder.append("SelfLink: " + s + LS));
            Optional.ofNullable(pod.getMetadata().getUid()).ifPresent(s -> detailsBuilder.append("UID: " + s + LS));

            Optional.ofNullable(pod.getStatus().getContainerStatuses()).ifPresent(containerStatuses -> containerStatuses.forEach(container -> {

                Optional.ofNullable(container.getName()).ifPresent(c -> detailsBuilder.append("Container: " + c + LS));
                Optional.ofNullable(container.getImage()).ifPresent(c -> detailsBuilder.append("\tImage: " + c + LS));
                Optional.ofNullable(container.getImageID()).ifPresent(c -> detailsBuilder.append("\tImageId: " + c + LS));
                Optional.ofNullable(container.getContainerID()).ifPresent(c -> detailsBuilder.append("\tContainerId: " + c + LS));
                Optional.ofNullable(container.getStarted()).ifPresent(c -> detailsBuilder.append("\tStarted: " + c + LS));
                Optional.ofNullable(container.getReady()).ifPresent(c -> detailsBuilder.append("\tReady: " + c + LS));
                Optional.ofNullable(container.getRestartCount()).ifPresent(c -> detailsBuilder.append("\tRestartCount: " + c + LS));
            }));

            ipsAndPortsResult
                    .setIp(pod.getStatus().getPodIP())
                    .setPorts(portsJoiner.toString())
                    .setNamespace(pod.getMetadata().getNamespace())
                    .setHostInfo(pod.getSpec().getNodeName() + ": [ " + pod.getStatus().getHostIP() + " ] ")
                    .setResourceName(resourceName + containerNamesJoiner.toString())
                    .setResourceType(Resource.POD)
                    .setAdditionalInfo("Phase: " + pod.getStatus().getPhase())
                    .setCreationTime(getParsedCreationTime(pod.getMetadata().getCreationTimestamp()))
                    .setDetails(detailsBuilder.toString());
            ipsAndPortsModel.addIpsAndPortsResult(ipsAndPortsResult);
        }
    }

    private void fillModelWithServicesInfo(String selectedNamespace, IpsAndPortsModel ipsAndPortsModel) {

        for (V1Service service : getV1ServicesList(selectedNamespace).getItems()) {
            IpsAndPortsResult ipsAndPortsResult = new IpsAndPortsResult(ipsAndPortsModel.getIpsAndPortsResults().size() + 1);
            StringBuilder servicePortsBuilder = new StringBuilder(), detailsBuilder = new StringBuilder();
            StringJoiner serviceIpsJoiner = new StringJoiner(LS);

//          compose service ips
            Optional.ofNullable(service.getSpec().getClusterIP()).ifPresent(clusterIp -> serviceIpsJoiner.add("ClusterIP: " + clusterIp));
            Optional.ofNullable(service.getSpec().getLoadBalancerIP()).ifPresent(lbIp -> serviceIpsJoiner.add("\nLoadBalancerIP: " + lbIp));
            Optional.ofNullable(service.getSpec().getExternalIPs()).ifPresent(externalIps -> {
                StringJoiner externalIpsJoiner = new StringJoiner(",");
                for (String externalIp : externalIps) {
                    externalIpsJoiner.add(externalIp);
                }
                serviceIpsJoiner.add("\nExternalIPs: " + externalIpsJoiner.toString());
            });

//          compose service portss
            Optional.ofNullable(service.getSpec().getPorts()).ifPresent(ports -> {
                for (V1ServicePort port : ports) {
                    StringJoiner portsJoiner = getStringsJoiner();
                    Optional.ofNullable(port.getNodePort()).ifPresent(concatIntegerWithPort("NodePort: ", portsJoiner));
                    Optional.ofNullable(port.getPort()).ifPresent(concatIntegerWithPort("Port: ", portsJoiner));
                    Optional.ofNullable(port.getTargetPort()).ifPresent(concatIntOrStringWithPort("TargetPort: ", portsJoiner));
                    StringJoiner portsNamenJoiner = getStringsJoiner();
                    Optional.ofNullable(port.getName()).ifPresent(concatStringLabelWithPort("name: ", portsNamenJoiner));
                    Optional.ofNullable(port.getProtocol()).ifPresent(concatStringLabelWithPort("protocol: ", portsNamenJoiner));
                    servicePortsBuilder.append("[ " + portsJoiner.toString() + " " + portsNamenJoiner.toString() + " ]" + LS);
                }
            });

//          compose service selectors
            StringJoiner selectorsJoiner = getStringsJoiner();
            Optional.ofNullable(service.getSpec().getSelector()).ifPresent(selectors -> {
                for (Map.Entry<String, String> entry : selectors.entrySet()) {
                    selectorsJoiner.add(entry.getKey() + ": " + entry.getValue());
                }
            });

//          compose service data to details
            Optional.ofNullable(service.getMetadata().getName()).ifPresent(s -> detailsBuilder.append(ipsAndPortsResult.getId() + " => " + s + LS));

//          compose service labels
            StringJoiner labelsJoiner = getStringsJoiner();
            Optional.ofNullable(service.getMetadata().getLabels()).ifPresent(labels -> {
                for (Map.Entry<String, String> entry : labels.entrySet()) {
                    labelsJoiner.add(entry.getKey() + ": " + entry.getValue());
                }
            });
            detailsBuilder.append("Labels: ").append(labelsJoiner.toString()).append(LS);

//          compose service annotations
            StringJoiner annotationsJoiner = getStringsJoiner();
            Optional.ofNullable(service.getMetadata().getAnnotations()).ifPresent(annotations -> {
                for (Map.Entry<String, String> entry : annotations.entrySet()) {
                    annotationsJoiner.add(entry.getKey() + ": " + entry.getValue());
                }
            });
            detailsBuilder.append("Annotations: ").append(annotationsJoiner.toString()).append(LS);

//          compose other service data into details
            Optional.ofNullable(service.getApiVersion()).ifPresent(s -> detailsBuilder.append("ApiVersion: " + s + LS));
            Optional.ofNullable(service.getSpec().getSessionAffinity()).ifPresent(s -> detailsBuilder.append("SessionAffinity: " + s + LS));
            Optional.ofNullable(service.getMetadata().getSelfLink()).ifPresent(s -> detailsBuilder.append("SelfLink: " + s + LS));
            Optional.ofNullable(service.getMetadata().getUid()).ifPresent(s -> detailsBuilder.append("UID: " + s + LS));

            ipsAndPortsResult
                    .setIp(serviceIpsJoiner.toString())
                    .setPorts(servicePortsBuilder.toString())
                    .setNamespace(service.getMetadata().getNamespace())
                    .setHostInfo("")
                    .setResourceName(service.getMetadata().getName())
                    .setResourceType(Resource.SERVICE)
                    .setAdditionalInfo("Type: ClusterIP\nSelectors: " + selectorsJoiner.toString())
                    .setCreationTime(getParsedCreationTime(service.getMetadata().getCreationTimestamp()))
                    .setDetails(detailsBuilder.toString());
            ipsAndPortsModel.addIpsAndPortsResult(ipsAndPortsResult);
        }
    }

    private String getParsedCreationTime(DateTime dateTime) {
        return dateTime.toString("dd.MM.yyyy HH:mm:ss");
    }

    private StringJoiner getStringsJoiner() {
        return new StringJoiner(", ", "[", "]");
    }

    private Consumer<String> concatStringLabelWithPort(String prefix, StringJoiner tmpPortsJoiner) {
        return portName -> tmpPortsJoiner.add(prefix + portName);
    }

    private Consumer<Integer> concatIntegerWithPort(String prefix, StringJoiner tmpPortsJoiner) {
        return port -> tmpPortsJoiner.add(prefix + port);
    }

    private Consumer<IntOrString> concatIntOrStringWithPort(String prefix, StringJoiner tmpPortsJoiner) {
        return port -> tmpPortsJoiner.add(prefix + port);
    }

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

    private V1ServiceList getV1ServicesList(String selectedNamespace) {
        try {
            if ("all".equals(selectedNamespace)) {
                return api.listServiceForAllNamespaces(null, null, null, null, null, null, null, null, null);
            } else {
                return api.listNamespacedService(selectedNamespace, null, null, null, null, null, null, null, null, null);
            }
        } catch (ApiException e) {
            Messagebox.show(e.getMessage(), "Fetch Services from Namespace Error", Messagebox.OK, Messagebox.ERROR);
            e.printStackTrace();
        }
        return new V1ServiceList();
    }

    private V1beta1DaemonSetList getV1DaemonSetsList(String selectedNamespace) {

        try {
            if ("all".equals(selectedNamespace)) {
                return extensionsV1beta1Api.listDaemonSetForAllNamespaces(null, null, null, null, null, null, null, null, null);
            } else {
                return extensionsV1beta1Api.listNamespacedDaemonSet(selectedNamespace, null, null, null, null, null, null, null, null, null);
            }
        } catch (ApiException e) {
            Messagebox.show(e.getMessage(), "Fetch Services from Namespace Error", Messagebox.OK, Messagebox.ERROR);
            e.printStackTrace();
        }
        return new V1beta1DaemonSetList();
    }

    private ExtensionsV1beta1DeploymentList getV1DeploymentsList(String selectedNamespace) {
        try {
            if ("all".equals(selectedNamespace)) {
                return extensionsV1beta1Api.listNamespacedDeployment(null, null, null, null, null, null, null, null, null, null);
            } else {
                return extensionsV1beta1Api.listNamespacedDeployment(selectedNamespace, null, null, null, null, null, null, null, null, null);
            }
        } catch (ApiException e) {
            Messagebox.show(e.getMessage(), "Fetch Services from Namespace Error", Messagebox.OK, Messagebox.ERROR);
            e.printStackTrace();
        }
        return new ExtensionsV1beta1DeploymentList();
    }

//    private V1ServiceList getV1StatefulSetsList(String selectedNamespace) {
//        try {
//            if ("all".equals(selectedNamespace)) {
//                return extensionsV1beta1Api.set(null, null, null, null, null, null, null, null, null);
//            } else {
//                return extensionsV1beta1Api.listNamespacedService(selectedNamespace, null, null, null, null, null, null, null, null, null);
//            }
//        } catch (ApiException e) {
//            Messagebox.show(e.getMessage(), "Fetch Services from Namespace Error", Messagebox.OK, Messagebox.ERROR);
//            e.printStackTrace();
//        }
//        return new V1ServiceList();
//    }

    private V1beta1ReplicaSetList getV1ReplicaSetsList(String selectedNamespace) {
        try {
            if ("all".equals(selectedNamespace)) {
                return extensionsV1beta1Api.listReplicaSetForAllNamespaces(null, null, null, null, null, null, null, null, null);
            } else {
                return extensionsV1beta1Api.listNamespacedReplicaSet(selectedNamespace, null, null, null, null, null, null, null, null, null);
            }
        } catch (ApiException e) {
            Messagebox.show(e.getMessage(), "Fetch Services from Namespace Error", Messagebox.OK, Messagebox.ERROR);
            e.printStackTrace();
        }
        return new V1beta1ReplicaSetList();
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
//            Exec exec = new Exec(api.getApiClient());
////            exec.exec()
}
