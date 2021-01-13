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

import com.kubehelper.common.Resource;

import java.util.StringJoiner;

/**
 * @author JDev
 */
public class PodSecurityPoliciesResult {

    private int id;
    private String resourceName = "";
    private Resource resourceType = Resource.POD_SECURITY_POLICY;

    private String allowPrivilegeEscalation = "";
    private String defaultAllowPrivilegeEscalation = "";
    private String allowedCSIDrivers = "";
    private String allowedCapabilities = "";
    private String allowedFlexVolumes = "";
    private String allowedHostPaths = "";
    private String allowedProcMountTypes = "";
    private String allowedUnsafeSysctls = "";
    private String defaultAddCapabilities = "";
    private String forbiddenSysctls = "";
    private String fsGroup = "";
    private String hostIPC = "";
    private String hostNetwork = "";
    private String hostPID = "";
    private String hostPorts = "";
    private String privileged = "";
    private String readOnlyRootFilesystem = "";
    private String requiredDropCapabilities = "";
    private String runAsGroup = "";
    private String runAsUser = "";
    private String runtimeClass = "";
    private String seLinux = "";
    private String supplementalGroups = "";
    private String volumes = "";

    private String namespace = "";
    private String creationTime = "";
    private String fullDefinition = "";


    public PodSecurityPoliciesResult(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getResourceName() {
        return resourceName;
    }

    public PodSecurityPoliciesResult setResourceName(String resourceName) {
        this.resourceName = resourceName;
        return this;
    }

    public Resource getRawResourceType(){
        return resourceType;
    }

    public String getAllowPrivilegeEscalation() {
        return allowPrivilegeEscalation;
    }

    public PodSecurityPoliciesResult setAllowPrivilegeEscalation(String allowPrivilegeEscalation) {
        this.allowPrivilegeEscalation = allowPrivilegeEscalation;
        return this;
    }

    public String getDefaultAllowPrivilegeEscalation() {
        return defaultAllowPrivilegeEscalation;
    }

    public PodSecurityPoliciesResult setDefaultAllowPrivilegeEscalation(String defaultAllowPrivilegeEscalation) {
        this.defaultAllowPrivilegeEscalation = defaultAllowPrivilegeEscalation;
        return this;
    }

    public String getAllowedCSIDrivers() {
        return allowedCSIDrivers;
    }

    public PodSecurityPoliciesResult setAllowedCSIDrivers(String allowedCSIDrivers) {
        this.allowedCSIDrivers = allowedCSIDrivers;
        return this;
    }

    public String getAllowedCapabilities() {
        return allowedCapabilities;
    }

    public PodSecurityPoliciesResult setAllowedCapabilities(String allowedCapabilities) {
        this.allowedCapabilities = allowedCapabilities;
        return this;
    }

    public String getAllowedFlexVolumes() {
        return allowedFlexVolumes;
    }

    public PodSecurityPoliciesResult setAllowedFlexVolumes(String allowedFlexVolumes) {
        this.allowedFlexVolumes = allowedFlexVolumes;
        return this;
    }

    public String getAllowedHostPaths() {
        return allowedHostPaths;
    }

    public PodSecurityPoliciesResult setAllowedHostPaths(String allowedHostPaths) {
        this.allowedHostPaths = allowedHostPaths;
        return this;
    }

    public String getAllowedProcMountTypes() {
        return allowedProcMountTypes;
    }

    public PodSecurityPoliciesResult setAllowedProcMountTypes(String allowedProcMountTypes) {
        this.allowedProcMountTypes = allowedProcMountTypes;
        return this;
    }

    public String getAllowedUnsafeSysctls() {
        return allowedUnsafeSysctls;
    }

    public PodSecurityPoliciesResult setAllowedUnsafeSysctls(String allowedUnsafeSysctls) {
        this.allowedUnsafeSysctls = allowedUnsafeSysctls;
        return this;
    }

    public String getDefaultAddCapabilities() {
        return defaultAddCapabilities;
    }

    public PodSecurityPoliciesResult setDefaultAddCapabilities(String defaultAddCapabilities) {
        this.defaultAddCapabilities = defaultAddCapabilities;
        return this;
    }

    public String getForbiddenSysctls() {
        return forbiddenSysctls;
    }

    public PodSecurityPoliciesResult setForbiddenSysctls(String forbiddenSysctls) {
        this.forbiddenSysctls = forbiddenSysctls;
        return this;
    }

    public String getFsGroup() {
        return fsGroup;
    }

    public PodSecurityPoliciesResult setFsGroup(String fsGroup) {
        this.fsGroup = fsGroup;
        return this;
    }

    public String getHostIPC() {
        return hostIPC;
    }

    public PodSecurityPoliciesResult setHostIPC(String hostIPC) {
        this.hostIPC = hostIPC;
        return this;
    }

    public String getHostNetwork() {
        return hostNetwork;
    }

    public PodSecurityPoliciesResult setHostNetwork(String hostNetwork) {
        this.hostNetwork = hostNetwork;
        return this;
    }

    public String getHostPID() {
        return hostPID;
    }

    public PodSecurityPoliciesResult setHostPID(String hostPID) {
        this.hostPID = hostPID;
        return this;
    }

    public String getHostPorts() {
        return hostPorts;
    }

    public PodSecurityPoliciesResult setHostPorts(String hostPorts) {
        this.hostPorts = hostPorts;
        return this;
    }

    public String getPrivileged() {
        return privileged;
    }

    public PodSecurityPoliciesResult setPrivileged(String privileged) {
        this.privileged = privileged;
        return this;
    }

    public String getReadOnlyRootFilesystem() {
        return readOnlyRootFilesystem;
    }

    public PodSecurityPoliciesResult setReadOnlyRootFilesystem(String readOnlyRootFilesystem) {
        this.readOnlyRootFilesystem = readOnlyRootFilesystem;
        return this;
    }

    public String getRequiredDropCapabilities() {
        return requiredDropCapabilities;
    }

    public PodSecurityPoliciesResult setRequiredDropCapabilities(String requiredDropCapabilities) {
        this.requiredDropCapabilities = requiredDropCapabilities;
        return this;
    }

    public String getRunAsGroup() {
        return runAsGroup;
    }

    public PodSecurityPoliciesResult setRunAsGroup(String runAsGroup) {
        this.runAsGroup = runAsGroup;
        return this;
    }

    public String getRunAsUser() {
        return runAsUser;
    }

    public PodSecurityPoliciesResult setRunAsUser(String runAsUser) {
        this.runAsUser = runAsUser;
        return this;
    }

    public String getRuntimeClass() {
        return runtimeClass;
    }

    public PodSecurityPoliciesResult setRuntimeClass(String runtimeClass) {
        this.runtimeClass = runtimeClass;
        return this;
    }

    public String getSeLinux() {
        return seLinux;
    }

    public PodSecurityPoliciesResult setSeLinux(String seLinux) {
        this.seLinux = seLinux;
        return this;
    }

    public String getSupplementalGroups() {
        return supplementalGroups;
    }

    public PodSecurityPoliciesResult setSupplementalGroups(String supplementalGroups) {
        this.supplementalGroups = supplementalGroups;
        return this;
    }

    public String getVolumes() {
        return volumes;
    }

    public PodSecurityPoliciesResult setVolumes(String volumes) {
        this.volumes = volumes;
        return this;
    }

    public String getNamespace() {
        return namespace;
    }

    public PodSecurityPoliciesResult setNamespace(String namespace) {
        this.namespace = namespace;
        return this;
    }

    public String getCreationTime() {
        return creationTime;
    }

    public PodSecurityPoliciesResult setCreationTime(String creationTime) {
        this.creationTime = creationTime;
        return this;
    }

    public String getFullDefinition() {
        return fullDefinition;
    }

    public PodSecurityPoliciesResult setFullDefinition(String fullDefinition) {
        this.fullDefinition = fullDefinition;
        return this;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", PodSecurityPoliciesResult.class.getSimpleName() + "[", "]")
                .add("id=" + id)
                .add("resourceName='" + resourceName + "'")
                .add("resourceType=" + resourceType)
                .add("allowPrivilegeEscalation='" + allowPrivilegeEscalation + "'")
                .add("defaultAllowPrivilegeEscalation='" + defaultAllowPrivilegeEscalation + "'")
                .add("allowedCSIDrivers='" + allowedCSIDrivers + "'")
                .add("allowedCapabilities='" + allowedCapabilities + "'")
                .add("allowedFlexVolumes='" + allowedFlexVolumes + "'")
                .add("allowedHostPaths='" + allowedHostPaths + "'")
                .add("allowedProcMountTypes='" + allowedProcMountTypes + "'")
                .add("allowedUnsafeSysctls='" + allowedUnsafeSysctls + "'")
                .add("defaultAddCapabilities='" + defaultAddCapabilities + "'")
                .add("forbiddenSysctls='" + forbiddenSysctls + "'")
                .add("fsGroup='" + fsGroup + "'")
                .add("hostIPC='" + hostIPC + "'")
                .add("hostNetwork='" + hostNetwork + "'")
                .add("hostPID='" + hostPID + "'")
                .add("hostPorts='" + hostPorts + "'")
                .add("privileged='" + privileged + "'")
                .add("readOnlyRootFilesystem='" + readOnlyRootFilesystem + "'")
                .add("requiredDropCapabilities='" + requiredDropCapabilities + "'")
                .add("runAsGroup='" + runAsGroup + "'")
                .add("runAsUser='" + runAsUser + "'")
                .add("runtimeClass='" + runtimeClass + "'")
                .add("seLinux='" + seLinux + "'")
                .add("supplementalGroups='" + supplementalGroups + "'")
                .add("volumes='" + volumes + "'")
                .add("namespace='" + namespace + "'")
                .add("creationTime='" + creationTime + "'")
                .add("fullDefinition='" + fullDefinition + "'")
                .toString();
    }
}