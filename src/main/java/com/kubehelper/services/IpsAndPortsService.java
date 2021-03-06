/*
Kube Helper
Copyright (C) 2021 JDev

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
package com.kubehelper.services;

import com.kubehelper.common.KubeAPI;
import com.kubehelper.common.Resource;
import com.kubehelper.domain.models.IpsAndPortsModel;
import com.kubehelper.domain.results.IpsAndPortsResult;
import io.kubernetes.client.custom.IntOrString;
import io.kubernetes.client.openapi.models.V1Container;
import io.kubernetes.client.openapi.models.V1Pod;
import io.kubernetes.client.openapi.models.V1Service;
import io.kubernetes.client.openapi.models.V1ServicePort;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.text.StringSubstitutor;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.StringJoiner;
import java.util.function.Consumer;

/**
 * Service for {@link com.kubehelper.viewmodels.IpsAndPortsVM} View Model.
 *
 * @author JDev
 */
@Service
public class IpsAndPortsService {

    private final String LS = System.getProperty("line.separator");

    private static Logger logger = LoggerFactory.getLogger(IpsAndPortsService.class);

    private String podDetailsTemplate;
    private String serviceDetailsTemplate;
    private String containerDetailsTemplate;

    @Autowired
    private KubeAPI kubeAPI;

    @Autowired
    private CommonService commonService;

    /**
     * Constructor initializes templates for the detail view.
     */
    @PostConstruct
    private void postConstruct() {
        podDetailsTemplate = commonService.getClasspathResourceAsStringByPath("/templates/ips-and-ports/pod-details.html");
        serviceDetailsTemplate = commonService.getClasspathResourceAsStringByPath("/templates/ips-and-ports/service-details.html");
        containerDetailsTemplate = commonService.getClasspathResourceAsStringByPath("/templates/ips-and-ports/container-details.html");
    }


    /**
     * Clears active {@link IpsAndPortsModel} model and fill it with new data from pods and services.
     *
     * @param ipsAndPortsModel - model for {@link com.kubehelper.viewmodels.IpsAndPortsVM}
     */
    public void get(IpsAndPortsModel ipsAndPortsModel) {
        try {
            ipsAndPortsModel.getIpsAndPortsResults().clear();
            fillModelWithPodsInfo(ipsAndPortsModel);
            fillModelWithServicesInfo(ipsAndPortsModel);
        } catch (RuntimeException e) {
            ipsAndPortsModel.addSearchException(e);
            logger.error(e.getMessage(), e);
        }
    }

    /**
     * Fill {@link IpsAndPortsModel} with new data from pods depends on namespace.
     *
     * @param model - model for @{@link com.kubehelper.viewmodels.IpsAndPortsVM} view.
     */
    private void fillModelWithPodsInfo(IpsAndPortsModel model) {
        for (V1Pod pod : kubeAPI.getV1PodsList(model.getSelectedNamespace(), model).getItems()) {
            IpsAndPortsResult ipsAndPortsResult = new IpsAndPortsResult(model.getIpsAndPortsResults().size() + 1);
            StringJoiner portsJoiner = getStringsJoiner(), containerNamesJoiner = getStringsJoiner();
            String resourceName = pod.getMetadata().getName() + ": ";
            Map<String, String> detailsMap = new HashMap<>(), containersDetailsMap = new HashMap<>();

//          compose pod ips
            for (V1Container container : pod.getSpec().getContainers()) {
                if (ObjectUtils.isNotEmpty(container.getPorts())) {
                    container.getPorts().forEach(port -> portsJoiner.add(String.valueOf(port.getContainerPort())));
                }
                containerNamesJoiner.add(container.getName());
            }

//          compose service data to details
            Optional.ofNullable(pod.getMetadata().getName()).ifPresent(p -> detailsMap.put("id", ipsAndPortsResult.getId() + " => " + p));

//          compose service labels
            Optional.ofNullable(pod.getMetadata().getLabels()).ifPresent(labels -> joinKeyValuesFromEntrySet(labels.entrySet(), detailsMap, "labels"));

//          compose pod annotations
            Optional.ofNullable(pod.getMetadata().getAnnotations()).ifPresent(annotations -> joinKeyValuesFromEntrySet(annotations.entrySet(), detailsMap, "annotations"));

//          compose other service data into details
            Optional.ofNullable(pod.getApiVersion()).ifPresent(p -> detailsMap.put("apiVersion", p));
            Optional.ofNullable(pod.getMetadata().getSelfLink()).ifPresent(p -> detailsMap.put("selfLink", p));
            Optional.ofNullable(pod.getMetadata().getUid()).ifPresent(p -> detailsMap.put("uid", p));

            Optional.ofNullable(pod.getStatus().getContainerStatuses()).ifPresent(containerStatuses -> containerStatuses.forEach(container -> {
                Optional.ofNullable(container.getName()).ifPresent(c -> containersDetailsMap.put("name", c));
                Optional.ofNullable(container.getImage()).ifPresent(c -> containersDetailsMap.put("image", c));
                Optional.ofNullable(container.getImageID()).ifPresent(c -> containersDetailsMap.put("imageId", c));
                Optional.ofNullable(container.getContainerID()).ifPresent(c -> containersDetailsMap.put("containerId", c));
                Optional.ofNullable(container.getStarted()).ifPresent(c -> containersDetailsMap.put("started", String.valueOf(c)));
                Optional.ofNullable(container.getReady()).ifPresent(c -> containersDetailsMap.put("ready", String.valueOf(c)));
                Optional.ofNullable(container.getRestartCount()).ifPresent(c -> containersDetailsMap.put("restartCount", String.valueOf(c)));
            }));

            String podDetailsString = buildHtmlDetails(detailsMap, podDetailsTemplate) + buildHtmlDetails(containersDetailsMap, containerDetailsTemplate);

            ipsAndPortsResult
                    .setIp(pod.getStatus().getPodIP())
                    .setPorts(portsJoiner.toString())
                    .setNamespace(pod.getMetadata().getNamespace())
                    .setHostInfo(pod.getSpec().getNodeName() + ": [ " + pod.getStatus().getHostIP() + " ] ")
                    .setResourceName(resourceName + containerNamesJoiner.toString())
                    .setResourceType(Resource.POD)
                    .setAdditionalInfo("Phase: " + pod.getStatus().getPhase())
                    .setCreationTime(getParsedCreationTime(pod.getMetadata().getCreationTimestamp()))
                    .setDetails(podDetailsString);
            model.addIpsAndPortsResult(ipsAndPortsResult);
        }
    }

    /**
     * Fill {@link IpsAndPortsModel} with new data from services depends on namespace.
     *
     * @param model - model for @{@link com.kubehelper.viewmodels.IpsAndPortsVM} view.
     */
    private void fillModelWithServicesInfo(IpsAndPortsModel model) {

        for (V1Service service : kubeAPI.getV1ServicesList(model.getSelectedNamespace(), model).getItems()) {
            Map<String, String> detailsMap = new HashMap<>();
            IpsAndPortsResult ipsAndPortsResult = new IpsAndPortsResult(model.getIpsAndPortsResults().size() + 1);
            StringBuilder servicePortsBuilder = new StringBuilder();
            StringJoiner ipsJoiner = new StringJoiner(LS);

            //compose service ips
            Optional.ofNullable(service.getSpec().getClusterIP()).ifPresent(ipsJoiner::add);
            Optional.ofNullable(service.getSpec().getLoadBalancerIP()).ifPresent(ipsJoiner::add);
            Optional.ofNullable(service.getSpec().getExternalIPs()).ifPresent(externalIps -> {
                StringJoiner externalIpsJoiner = new StringJoiner(",");
                for (String externalIp : externalIps) {
                    externalIpsJoiner.add(externalIp);
                }
                ipsJoiner.add("\nExternalIPs: " + externalIpsJoiner.toString());
            });

            //compose service portss
            Optional.ofNullable(service.getSpec().getPorts()).ifPresent(ports -> {
                for (V1ServicePort port : ports) {
                    StringJoiner portsJoiner = getStringsJoiner();
                    Optional.ofNullable(port.getNodePort()).ifPresent(concatIntegerWithPort("NodePort: ", portsJoiner));
                    Optional.ofNullable(port.getPort()).ifPresent(concatIntegerWithPort("Port: ", portsJoiner));
                    Optional.ofNullable(port.getTargetPort()).ifPresent(concatIntOrStringWithPort(portsJoiner));
                    StringJoiner portsNamesJoiner = getStringsJoiner();
                    Optional.ofNullable(port.getName()).ifPresent(concatStringLabelWithPort("name: ", portsNamesJoiner));
                    Optional.ofNullable(port.getProtocol()).ifPresent(concatStringLabelWithPort("protocol: ", portsNamesJoiner));
                    servicePortsBuilder.append("[ ").append(portsJoiner.toString()).append(" ").append(portsNamesJoiner.toString()).append(" ]").append(LS);
                }
            });

            //compose service selectors
            StringJoiner selectorsJoiner = getStringsJoiner();
            Optional.ofNullable(service.getSpec().getSelector()).ifPresent(selectors -> {
                for (Map.Entry<String, String> entry : selectors.entrySet()) {
                    selectorsJoiner.add(entry.getKey() + ": " + entry.getValue());
                }
            });

            //compose service data to details
            Optional.ofNullable(service.getMetadata().getName()).ifPresent(s -> detailsMap.put("id", ipsAndPortsResult.getId() + " => " + s));

            //compose service labels
            Optional.ofNullable(service.getMetadata().getLabels()).ifPresent(labels -> joinKeyValuesFromEntrySet(labels.entrySet(), detailsMap, "labels"));

            //compose service annotations
            Optional.ofNullable(service.getMetadata().getAnnotations()).ifPresent(annotations -> joinKeyValuesFromEntrySet(annotations.entrySet(), detailsMap, "annotations"));

            //compose other service data into details
            Optional.ofNullable(service.getApiVersion()).ifPresent(s -> detailsMap.put("apiVersion", s));
            Optional.ofNullable(service.getSpec().getSessionAffinity()).ifPresent(s -> detailsMap.put("sessionAffinity", s));
            Optional.ofNullable(service.getMetadata().getSelfLink()).ifPresent(s -> detailsMap.put("selfLink", s));
            Optional.ofNullable(service.getMetadata().getUid()).ifPresent(s -> detailsMap.put("uid", s));

            String additionalInfo = (service.getSpec().getClusterIP() == null ? "Type: LoadBalancerIP" : "Type: ClusterIP" + "\nSelectors: " + selectorsJoiner.toString());

            ipsAndPortsResult
                    .setIp(ipsJoiner.toString())
                    .setPorts(servicePortsBuilder.toString())
                    .setNamespace(service.getMetadata().getNamespace())
                    .setHostInfo("")
                    .setResourceName(service.getMetadata().getName())
                    .setResourceType(Resource.SERVICE)
                    .setAdditionalInfo(additionalInfo)
                    .setCreationTime(getParsedCreationTime(service.getMetadata().getCreationTimestamp()))
                    .setDetails(buildHtmlDetails(detailsMap, serviceDetailsTemplate));
            model.addIpsAndPortsResult(ipsAndPortsResult);
        }
    }

    /**
     * Joins key/values from EntrySet into String-Map collector.
     *
     * @param entries   - key/values to join.
     * @param collector - String-Map collector
     * @param key       - key for save joins into map.
     */
    private void joinKeyValuesFromEntrySet(Set<Map.Entry<String, String>> entries, Map<String, String> collector, String key) {
        StringJoiner joiner = getStringsJoiner();
        for (Map.Entry<String, String> entry : entries) {
            joiner.add(entry.getKey() + ": " + entry.getValue());
        }
        collector.put(key, joiner.toString());
    }

    /**
     * Build html string from template and map with key/values for replace.
     *
     * @param detailsMap - key/values for replace values in html template.
     * @param template   - html template.
     * @return - html string for render in details section.
     */
    private String buildHtmlDetails(Map<String, String> detailsMap, String template) {
        StringSubstitutor sub = new StringSubstitutor(detailsMap);
        String html = sub.replace(template);
        return html.replaceAll("\\$\\{\\w*\\}", "");
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

    private Consumer<IntOrString> concatIntOrStringWithPort(StringJoiner tmpPortsJoiner) {
        return port -> tmpPortsJoiner.add("TargetPort: " + port);
    }

}
