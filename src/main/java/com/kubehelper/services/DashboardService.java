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

import com.kubehelper.domain.models.DashboardModel;
import com.kubehelper.domain.results.ClusterResult;
import com.kubehelper.domain.results.NodeResult;
import io.fabric8.kubernetes.api.model.ContainerImage;
import io.fabric8.kubernetes.api.model.NodeAddress;
import io.fabric8.kubernetes.api.model.NodeCondition;
import io.fabric8.kubernetes.api.model.NodeList;
import io.fabric8.kubernetes.api.model.Quantity;
import io.fabric8.kubernetes.api.model.Taint;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author JDev
 */
@Service
public class DashboardService {

    private static Logger logger = LoggerFactory.getLogger(DashboardService.class);

    public void showDashboard(DashboardModel dashboardModel) {

        dashboardModel.getNodesResults().clear();
        ClusterResult clusterResult = new ClusterResult();

        try (KubernetesClient client = new DefaultKubernetesClient()) {

            NodeList nodesList = client.nodes().list();
            clusterResult.setTotalNodes(nodesList.getItems().size());
            clusterResult.setTotalPods(client.pods().list().getItems().size());

            nodesList.getItems().forEach(node -> {
                NodeResult nodeResult = new NodeResult(dashboardModel.getNodesResults().size() + 1)
                        .setName(node.getMetadata().getName())
                        .setCreationTime(node.getMetadata().getCreationTimestamp())
                        .setAnnotations(getKeyValueJoinedMap(node.getMetadata().getAnnotations()))
                        .setLabels(getKeyValueJoinedMap(node.getMetadata().getLabels()))
                        .setPodCIDR(node.getSpec().getPodCIDR())
                        .setTaints(getJoinedTaintList(node.getSpec().getTaints()))
                        .setAddresses(getJoinedAddressesList(node.getStatus().getAddresses()))
                        .setAllocatable(getJoinedQuantitiesMap(node.getStatus().getAllocatable()))
                        .setCapacity(getJoinedQuantitiesMap(node.getStatus().getCapacity()))
                        .setConditions(getJoinedConditionsList(node.getStatus().getConditions()))
                        .setImages(getJoinedNodeImagesList(node.getStatus().getImages()))
                        .setTotalImagesSize(calculateImagesTotalSize(node.getStatus().getImages()))
                        .setPodsCount("podsCount") //TODO
                        .setArchitecture(node.getStatus().getNodeInfo().getArchitecture())
                        .setBootID(node.getStatus().getNodeInfo().getBootID())
                        .setContainerRuntimeVersion(node.getStatus().getNodeInfo().getContainerRuntimeVersion())
                        .setKernelVersion(node.getStatus().getNodeInfo().getKernelVersion())
                        .setKubeProxyVersion(node.getStatus().getNodeInfo().getKubeProxyVersion())
                        .setKubeletVersion(node.getStatus().getNodeInfo().getKubeletVersion())
                        .setMachineID(node.getStatus().getNodeInfo().getMachineID())
                        .setOperatingSystem(node.getStatus().getNodeInfo().getOperatingSystem())
                        .setOsImage(node.getStatus().getNodeInfo().getOsImage())
                        .setSystemUUID(node.getStatus().getNodeInfo().getSystemUUID());

                clusterResult.addTotalAllowedPods(node.getStatus().getCapacity().get("pods").getAmount());
                clusterResult.addTotalCpu(node.getStatus().getCapacity().get("cpu").getAmount());
                clusterResult.addTotalMemory(Quantity.getAmountInBytes(node.getStatus().getCapacity().get("memory")).longValue());
                clusterResult.addTotalCpuTime(node.getStatus().getAllocatable().get("cpu").getAmount());
                clusterResult.setTotalCpusTimeFormat(node.getStatus().getAllocatable().get("cpu").getFormat());
                clusterResult.addTotalHdd(Quantity.getAmountInBytes(node.getStatus().getCapacity().get("ephemeral-storage")).longValue());

                dashboardModel.addNodeResult(nodeResult);
            });

            dashboardModel.setClusterResult(clusterResult);
        } catch (KubernetesClientException ex) {
            dashboardModel.addException("An error occurred while getting information about the cluster nodes." + ex.getMessage(), ex);
            logger.error(ex.getMessage(), ex);
        }
    }

    private String calculateImagesTotalSize(List<ContainerImage> images) {
        return FileUtils.byteCountToDisplaySize(images.stream().mapToLong(image -> image.getSizeBytes()).sum());
    }

    private String getJoinedNodeImagesList(List<ContainerImage> images) {
        return images.stream().
                map(image -> getParsedContainerImage(image)).
                collect(Collectors.joining("\n"));
    }

    private String getParsedContainerImage(ContainerImage image) {
        Optional<String> img = image.getNames().stream().filter(name -> !name.contains("@sha256")).findFirst();
        String imageName = img.orElse(image.getNames().isEmpty() ? "Noname" : image.getNames().get(0));
        return new StringBuilder().append(imageName).append(" ").append("[").append(FileUtils.byteCountToDisplaySize(image.getSizeBytes())).append("]").toString();
    }

    private String getKeyValueJoinedMap(Map<String, String> map) {
        return map.entrySet().stream().
                map(entrySet -> entrySet.getKey() + ":" + entrySet.getValue()).
                collect(Collectors.joining("\n"));
    }

    private String getJoinedAddressesList(List<NodeAddress> addresses) {
        return addresses.stream().
                map(address -> getParsedAddress(address)).
                collect(Collectors.joining("\n"));
    }

    private String getParsedAddress(NodeAddress nodeAddress) {
        return new StringBuilder()
                .append("address=").append(nodeAddress.getAddress()).append(", ")
                .append("type=").append(nodeAddress.getType()).toString();
    }

    private String getJoinedTaintList(List<Taint> taints) {
        return taints.stream().
                map(taint -> getParsedTaint(taint)).
                collect(Collectors.joining("\n"));
    }

    private String getParsedTaint(Taint taint) {
        return new StringBuilder()
                .append("effect=").append(taint.getEffect()).append("\n")
                .append("key=").append(taint.getKey()).append("\n")
                .append("timeAdded=").append(taint.getTimeAdded()).append("\n")
                .append("value=").append(taint.getValue()).toString();
    }

    private String getJoinedQuantitiesMap(Map<String, Quantity> allocatables) {
        return allocatables.entrySet().stream().
                map(entry -> getParsedQuantity(entry.getKey(), entry.getValue())).
                collect(Collectors.joining("\n"));
    }

    private String getParsedQuantity(String key, Quantity quantity) {
        return new StringBuilder().append(key).append(" = ")
                .append("[amount=").append(quantity.getAmount()).append(", ")
                .append("format=").append(StringUtils.isBlank(quantity.getFormat()) ? "null" : quantity.getFormat()).append("]").toString();
    }


    private String getJoinedConditionsList(List<NodeCondition> conditions) {
        return conditions.stream().
                map(condition -> getParsedNodeCondition(condition)).
                collect(Collectors.joining("\n"));
    }

    private String getParsedNodeCondition(NodeCondition condition) {
        return new StringBuilder()
                .append("lastHeartbeatTime=").append(condition.getLastHeartbeatTime()).append("\n")
                .append("lastTransitionTime=").append(condition.getLastTransitionTime()).append("\n")
                .append("message=").append(condition.getMessage()).append("\n")
                .append("reason=").append(condition.getReason()).append("\n")
                .append("status=").append(condition.getStatus()).append("\n")
                .append("type=").append(condition.getType()).toString();
    }


    private String getParsedCreationTime(DateTime dateTime) {
        return dateTime == null ? "null" : dateTime.toString("dd.MM.yyyy HH:mm:ss");
    }
}
