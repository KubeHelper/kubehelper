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

import com.google.gson.annotations.SerializedName;
import io.kubernetes.client.openapi.models.V1SELinuxOptions;
import io.kubernetes.client.openapi.models.V1Sysctl;
import io.kubernetes.client.openapi.models.V1WindowsSecurityContextOptions;
import io.kubernetes.client.openapi.models.V1beta1AllowedCSIDriver;
import io.kubernetes.client.openapi.models.V1beta1AllowedFlexVolume;
import io.kubernetes.client.openapi.models.V1beta1AllowedHostPath;
import io.kubernetes.client.openapi.models.V1beta1FSGroupStrategyOptions;
import io.kubernetes.client.openapi.models.V1beta1HostPortRange;
import io.kubernetes.client.openapi.models.V1beta1RunAsGroupStrategyOptions;
import io.kubernetes.client.openapi.models.V1beta1RunAsUserStrategyOptions;
import io.kubernetes.client.openapi.models.V1beta1RuntimeClassStrategyOptions;
import io.kubernetes.client.openapi.models.V1beta1SELinuxStrategyOptions;
import io.kubernetes.client.openapi.models.V1beta1SupplementalGroupsStrategyOptions;

import java.util.List;

/**
 * @author JDev
 */
public class PodSecurityPoliciesResult {

    private int id;
    private String resourceName = "";

    private Boolean allowPrivilegeEscalation;
    private Boolean defaultAllowPrivilegeEscalation;
    private Boolean hostIPC;
    private Boolean hostNetwork;
    private Boolean hostPID;
    private Boolean privileged;
    private Boolean readOnlyRootFilesystem;

    private String namespace = "";
    private String creationTime = "";
    private String fullDefinition = "";

    public PodSecurityPoliciesResult() {
    }

    public PodSecurityPoliciesResult(int id) {
        this.id = id;
    }

}