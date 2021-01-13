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
public class PodSecurityContextResult {

    private int id;
    private String resourceName = "";

    private String fsGroup = "";
    private String fsGroupChangePolicy = "";
    private String runAsGroup = "";
    private String runAsNonRoot = "";
    private String runAsUser = "";
    private String seLinuxOptions = "";
    private String supplementalGroups = "";
    private String sysctls = "";
    private String windowsOptions = "";

    private String namespace = "";
    private String creationTime = "";
    private String fullDefinition = "";


    public PodSecurityContextResult(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getResourceName() {
        return resourceName;
    }

    public PodSecurityContextResult setResourceName(String resourceName) {
        this.resourceName = resourceName;
        return this;
    }

    public String getFsGroup() {
        return fsGroup;
    }

    public PodSecurityContextResult setFsGroup(String fsGroup) {
        this.fsGroup = fsGroup;
        return this;
    }

    public String getFsGroupChangePolicy() {
        return fsGroupChangePolicy;
    }

    public PodSecurityContextResult setFsGroupChangePolicy(String fsGroupChangePolicy) {
        this.fsGroupChangePolicy = fsGroupChangePolicy;
        return this;
    }

    public String getRunAsGroup() {
        return runAsGroup;
    }

    public PodSecurityContextResult setRunAsGroup(String runAsGroup) {
        this.runAsGroup = runAsGroup;
        return this;
    }

    public String getRunAsNonRoot() {
        return runAsNonRoot;
    }

    public PodSecurityContextResult setRunAsNonRoot(String runAsNonRoot) {
        this.runAsNonRoot = runAsNonRoot;
        return this;
    }

    public String getRunAsUser() {
        return runAsUser;
    }

    public PodSecurityContextResult setRunAsUser(String runAsUser) {
        this.runAsUser = runAsUser;
        return this;
    }

    public String getSeLinuxOptions() {
        return seLinuxOptions;
    }

    public PodSecurityContextResult setSeLinuxOptions(String seLinuxOptions) {
        this.seLinuxOptions = seLinuxOptions;
        return this;
    }

    public String getSupplementalGroups() {
        return supplementalGroups;
    }

    public PodSecurityContextResult setSupplementalGroups(String supplementalGroups) {
        this.supplementalGroups = supplementalGroups;
        return this;
    }

    public String getSysctls() {
        return sysctls;
    }

    public PodSecurityContextResult setSysctls(String sysctls) {
        this.sysctls = sysctls;
        return this;
    }

    public String getWindowsOptions() {
        return windowsOptions;
    }

    public PodSecurityContextResult setWindowsOptions(String windowsOptions) {
        this.windowsOptions = windowsOptions;
        return this;
    }

    public String getNamespace() {
        return namespace;
    }

    public PodSecurityContextResult setNamespace(String namespace) {
        this.namespace = namespace;
        return this;
    }

    public String getCreationTime() {
        return creationTime;
    }

    public PodSecurityContextResult setCreationTime(String creationTime) {
        this.creationTime = creationTime;
        return this;
    }

    public String getFullDefinition() {
        return fullDefinition;
    }

    public PodSecurityContextResult setFullDefinition(String fullDefinition) {
        this.fullDefinition = fullDefinition;
        return this;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", PodSecurityContextResult.class.getSimpleName() + "[", "]")
                .add("id=" + id)
                .add("resourceName='" + resourceName + "'")
                .add("fsGroup='" + fsGroup + "'")
                .add("fsGroupChangePolicy='" + fsGroupChangePolicy + "'")
                .add("runAsGroup='" + runAsGroup + "'")
                .add("runAsNonRoot='" + runAsNonRoot + "'")
                .add("runAsUser='" + runAsUser + "'")
                .add("seLinuxOptions='" + seLinuxOptions + "'")
                .add("supplementalGroups='" + supplementalGroups + "'")
                .add("sysctls='" + sysctls + "'")
                .add("windowsOptions='" + windowsOptions + "'")
                .add("namespace='" + namespace + "'")
                .add("creationTime='" + creationTime + "'")
                .add("fullDefinition='" + fullDefinition + "'")
                .toString();
    }
}