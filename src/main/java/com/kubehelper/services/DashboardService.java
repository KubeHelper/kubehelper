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
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
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

    /**
     * get cluster and nodes info.
     *
     * @param dashboardModel - {@link DashboardModel}
     */
    public void showDashboard(DashboardModel dashboardModel) {

        dashboardModel.getNodesResults().clear();
        ClusterResult clusterResult = new ClusterResult();

        try (KubernetesClient client = new DefaultKubernetesClient()) {

            NodeList nodesList = client.nodes().list();
            clusterResult.setTotalNodes(nodesList.getItems().size());

            clusterResult.setTotalPods(client.pods().inAnyNamespace().list().getItems().size());

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
        } catch (Exception ex) {
            dashboardModel.addException("An error occurred while getting information about the cluster nodes." + ex.getMessage(), ex);
            logger.error(ex.getMessage(), ex);
        }
    }

    private String calculateImagesTotalSize(List<ContainerImage> images) {
        return FileUtils.byteCountToDisplaySize(images.stream().mapToLong(ContainerImage::getSizeBytes).sum());
    }

    private String getJoinedNodeImagesList(List<ContainerImage> images) {
        return images.stream().
                map(this::getParsedContainerImage).
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
                map(this::getParsedAddress).
                collect(Collectors.joining("\n"));
    }

    private String getParsedAddress(NodeAddress nodeAddress) {
        return "address=" + nodeAddress.getAddress() + ", " +
                "type=" + nodeAddress.getType();
    }

    private String getJoinedTaintList(List<Taint> taints) {
        return taints.stream().
                map(this::getParsedTaint).
                collect(Collectors.joining("\n"));
    }

    private String getParsedTaint(Taint taint) {
        return "effect=" + taint.getEffect() + "\n" +
                "key=" + taint.getKey() + "\n" +
                "timeAdded=" + taint.getTimeAdded() + "\n" +
                "value=" + taint.getValue();
    }

    private String getJoinedQuantitiesMap(Map<String, Quantity> allocatables) {
        return allocatables.entrySet().stream().
                map(entry -> getParsedQuantity(entry.getKey(), entry.getValue())).
                collect(Collectors.joining("\n"));
    }

    private String getParsedQuantity(String key, Quantity quantity) {
        return key + " = " +
                "[amount=" + quantity.getAmount() + ", " +
                "format=" + (StringUtils.isBlank(quantity.getFormat()) ? "null" : quantity.getFormat()) + "]";
    }


    private String getJoinedConditionsList(List<NodeCondition> conditions) {
        return conditions.stream().
                map(this::getParsedNodeCondition).
                collect(Collectors.joining("\n"));
    }

    private String getParsedNodeCondition(NodeCondition condition) {
        return "lastHeartbeatTime=" + condition.getLastHeartbeatTime() + "\n" +
                "lastTransitionTime=" + condition.getLastTransitionTime() + "\n" +
                "message=" + condition.getMessage() + "\n" +
                "reason=" + condition.getReason() + "\n" +
                "status=" + condition.getStatus() + "\n" +
                "type=" + condition.getType();
    }

}
