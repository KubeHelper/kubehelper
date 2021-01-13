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
package com.kubehelper.domain.results;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.StringJoiner;

/**
 * @author JDev
 */
public class NodeResult {
    private int id;


    //Meta
    private String name = "";
    private String creationTime = "";
    private String annotations = "";
    private String labels = "";

    //NodeInfo
    private String architecture = "";
    private String bootID = "";
    private String containerRuntimeVersion = "";
    private String kernelVersion = "";
    private String kubeProxyVersion = "";
    private String kubeletVersion = "";
    private String machineID = "";
    private String operatingSystem = "";
    private String osImage = "";
    private String systemUUID = "";

    //Spec
    private String podCIDR = "";
    private String taints = "";

    //Status
    private String addresses = "";
    private String allocatable = "";
    private String capacity = "";
    private String conditions = "";
    private String images = "";
    private String totalImagesSize = "";


    public NodeResult(int id) {
        this.id = id;
    }

    public Map<String, String> getMetaMap() {
        return new LinkedHashMap<>() {
            {
                put("name", name);
                put("labels", labels);
                put("annotations", annotations);
                put("creationTime", creationTime);
            }
        };
    }

    public Map<String, String> getNodeInfoMap() {
        return new LinkedHashMap<>() {
            {
                put("architecture", architecture);
                put("bootID", bootID);
                put("containerRuntimeVersion", containerRuntimeVersion);
                put("kernelVersion", kernelVersion);
                put("kubeProxyVersion", kubeProxyVersion);
                put("kubeletVersion", kubeletVersion);
                put("machineID", machineID);
                put("operatingSystem", operatingSystem);
                put("osImage", osImage);
                put("systemUUID", systemUUID);
            }
        };
    }

    public Map<String, String> getSpecMap() {
        return new LinkedHashMap<>() {
            {
                put("podCIDR", podCIDR);
                put("taints", taints);
            }
        };
    }

    public Map<String, String> getStatusMap() {
        return new LinkedHashMap<>() {
            {
                put("addresses", addresses);
                put("allocatable", allocatable);
                put("capacity", capacity);
                put("conditions", conditions);
                put("images", images);
            }
        };
    }

    public String getName() {
        return name;
    }

    public NodeResult setName(String name) {
        this.name = name;
        return this;
    }

    public String getCreationTime() {
        return creationTime;
    }

    public NodeResult setCreationTime(String creationTime) {
        this.creationTime = creationTime;
        return this;
    }

    public String getAnnotations() {
        return annotations;
    }

    public NodeResult setAnnotations(String annotations) {
        this.annotations = annotations;
        return this;
    }

    public String getLabels() {
        return labels;
    }

    public NodeResult setLabels(String labels) {
        this.labels = labels;
        return this;
    }

    public String getPodCIDR() {
        return podCIDR;
    }

    public NodeResult setPodCIDR(String podCIDR) {
        this.podCIDR = podCIDR;
        return this;
    }

    public String getTaints() {
        return taints;
    }

    public NodeResult setTaints(String taints) {
        this.taints = taints;
        return this;
    }

    public String getAddresses() {
        return addresses;
    }

    public NodeResult setAddresses(String addresses) {
        this.addresses = addresses;
        return this;
    }

    public String getAllocatable() {
        return allocatable;
    }

    public NodeResult setAllocatable(String allocatable) {
        this.allocatable = allocatable;
        return this;
    }

    public String getCapacity() {
        return capacity;
    }

    public NodeResult setCapacity(String capacity) {
        this.capacity = capacity;
        return this;
    }

    public String getConditions() {
        return conditions;
    }

    public NodeResult setConditions(String conditions) {
        this.conditions = conditions;
        return this;
    }

    public String getImages() {
        return images;
    }

    public NodeResult setImages(String images) {
        this.images = images;
        return this;
    }

    public String getTotalImagesSize() {
        return totalImagesSize;
    }

    public NodeResult setTotalImagesSize(String totalImagesSize) {
        this.totalImagesSize = totalImagesSize;
        return this;
    }

    public String getArchitecture() {
        return architecture;
    }

    public NodeResult setArchitecture(String architecture) {
        this.architecture = architecture;
        return this;
    }

    public String getBootID() {
        return bootID;
    }

    public NodeResult setBootID(String bootID) {
        this.bootID = bootID;
        return this;
    }

    public String getContainerRuntimeVersion() {
        return containerRuntimeVersion;
    }

    public NodeResult setContainerRuntimeVersion(String containerRuntimeVersion) {
        this.containerRuntimeVersion = containerRuntimeVersion;
        return this;
    }

    public String getKernelVersion() {
        return kernelVersion;
    }

    public NodeResult setKernelVersion(String kernelVersion) {
        this.kernelVersion = kernelVersion;
        return this;
    }

    public String getKubeProxyVersion() {
        return kubeProxyVersion;
    }

    public NodeResult setKubeProxyVersion(String kubeProxyVersion) {
        this.kubeProxyVersion = kubeProxyVersion;
        return this;
    }

    public String getKubeletVersion() {
        return kubeletVersion;
    }

    public NodeResult setKubeletVersion(String kubeletVersion) {
        this.kubeletVersion = kubeletVersion;
        return this;
    }

    public String getMachineID() {
        return machineID;
    }

    public NodeResult setMachineID(String machineID) {
        this.machineID = machineID;
        return this;
    }

    public String getOperatingSystem() {
        return operatingSystem;
    }

    public NodeResult setOperatingSystem(String operatingSystem) {
        this.operatingSystem = operatingSystem;
        return this;
    }

    public String getOsImage() {
        return osImage;
    }

    public NodeResult setOsImage(String osImage) {
        this.osImage = osImage;
        return this;
    }

    public String getSystemUUID() {
        return systemUUID;
    }

    public NodeResult setSystemUUID(String systemUUID) {
        this.systemUUID = systemUUID;
        return this;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", NodeResult.class.getSimpleName() + "[", "]")
                .add("id=" + id)
                .add("name='" + name + "'")
                .add("creationTime='" + creationTime + "'")
                .add("annotations='" + annotations + "'")
                .add("labels='" + labels + "'")
                .add("architecture='" + architecture + "'")
                .add("bootID='" + bootID + "'")
                .add("containerRuntimeVersion='" + containerRuntimeVersion + "'")
                .add("kernelVersion='" + kernelVersion + "'")
                .add("kubeProxyVersion='" + kubeProxyVersion + "'")
                .add("kubeletVersion='" + kubeletVersion + "'")
                .add("machineID='" + machineID + "'")
                .add("operatingSystem='" + operatingSystem + "'")
                .add("osImage='" + osImage + "'")
                .add("systemUUID='" + systemUUID + "'")
                .add("podCIDR='" + podCIDR + "'")
                .add("taints='" + taints + "'")
                .add("addresses='" + addresses + "'")
                .add("allocatable='" + allocatable + "'")
                .add("capacity='" + capacity + "'")
                .add("conditions='" + conditions + "'")
                .add("images='" + images + "'")
                .add("totalImagesSize='" + totalImagesSize + "'")
                .toString();
    }
}