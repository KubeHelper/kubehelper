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

import java.util.StringJoiner;

/**
 * @author JDev
 */
public class ContainerSecurityResult {

    private int id;
    private String resourceName = "null";
    private String podName = "null";

    private String allowPrivilegeEscalation = "null";
    private String capabilities = "null";
    private String privileged = "null";
    private String procMount = "null";
    private String readOnlyRootFilesystem = "null";
    private String runAsGroup = "null";
    private String runAsNonRoot = "null";
    private String runAsUser = "null";
    private String seLinuxOptions = "null";
    private String windowsOptions = "null";

    private String namespace = "null";
    private String creationTime = "null";
    private String fullDefinition = "null";


    public ContainerSecurityResult(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getResourceName() {
        return resourceName;
    }

    public ContainerSecurityResult setResourceName(String resourceName) {
        this.resourceName = resourceName;
        return this;
    }

    public String getPodName() {
        return podName;
    }

    public ContainerSecurityResult setPodName(String podName) {
        this.podName = podName;
        return this;
    }

    public String getAllowPrivilegeEscalation() {
        return allowPrivilegeEscalation;
    }

    public ContainerSecurityResult setAllowPrivilegeEscalation(String allowPrivilegeEscalation) {
        this.allowPrivilegeEscalation = allowPrivilegeEscalation;
        return this;
    }

    public String getCapabilities() {
        return capabilities;
    }

    public ContainerSecurityResult setCapabilities(String capabilities) {
        this.capabilities = capabilities;
        return this;
    }

    public String getPrivileged() {
        return privileged;
    }

    public ContainerSecurityResult setPrivileged(String privileged) {
        this.privileged = privileged;
        return this;
    }

    public String getProcMount() {
        return procMount;
    }

    public ContainerSecurityResult setProcMount(String procMount) {
        this.procMount = procMount;
        return this;
    }

    public String getReadOnlyRootFilesystem() {
        return readOnlyRootFilesystem;
    }

    public ContainerSecurityResult setReadOnlyRootFilesystem(String readOnlyRootFilesystem) {
        this.readOnlyRootFilesystem = readOnlyRootFilesystem;
        return this;
    }

    public String getRunAsGroup() {
        return runAsGroup;
    }

    public ContainerSecurityResult setRunAsGroup(String runAsGroup) {
        this.runAsGroup = runAsGroup;
        return this;
    }

    public String getRunAsNonRoot() {
        return runAsNonRoot;
    }

    public ContainerSecurityResult setRunAsNonRoot(String runAsNonRoot) {
        this.runAsNonRoot = runAsNonRoot;
        return this;
    }

    public String getRunAsUser() {
        return runAsUser;
    }

    public ContainerSecurityResult setRunAsUser(String runAsUser) {
        this.runAsUser = runAsUser;
        return this;
    }

    public String getSeLinuxOptions() {
        return seLinuxOptions;
    }

    public ContainerSecurityResult setSeLinuxOptions(String seLinuxOptions) {
        this.seLinuxOptions = seLinuxOptions;
        return this;
    }

    public String getWindowsOptions() {
        return windowsOptions;
    }

    public ContainerSecurityResult setWindowsOptions(String windowsOptions) {
        this.windowsOptions = windowsOptions;
        return this;
    }

    public String getNamespace() {
        return namespace;
    }

    public ContainerSecurityResult setNamespace(String namespace) {
        this.namespace = namespace;
        return this;
    }

    public String getCreationTime() {
        return creationTime;
    }

    public ContainerSecurityResult setCreationTime(String creationTime) {
        this.creationTime = creationTime;
        return this;
    }

    public String getFullDefinition() {
        return fullDefinition;
    }

    public ContainerSecurityResult setFullDefinition(String fullDefinition) {
        this.fullDefinition = fullDefinition;
        return this;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", ContainerSecurityResult.class.getSimpleName() + "[", "]")
                .add("id=" + id)
                .add("resourceName='" + resourceName + "'")
                .add("podName='" + podName + "'")
                .add("allowPrivilegeEscalation='" + allowPrivilegeEscalation + "'")
                .add("capabilities='" + capabilities + "'")
                .add("privileged='" + privileged + "'")
                .add("procMount='" + procMount + "'")
                .add("readOnlyRootFilesystem='" + readOnlyRootFilesystem + "'")
                .add("runAsGroup='" + runAsGroup + "'")
                .add("runAsNonRoot='" + runAsNonRoot + "'")
                .add("runAsUser='" + runAsUser + "'")
                .add("seLinuxOptions='" + seLinuxOptions + "'")
                .add("windowsOptions='" + windowsOptions + "'")
                .add("namespace='" + namespace + "'")
                .add("creationTime='" + creationTime + "'")
                .add("fullDefinition='" + fullDefinition + "'")
                .toString();
    }
}