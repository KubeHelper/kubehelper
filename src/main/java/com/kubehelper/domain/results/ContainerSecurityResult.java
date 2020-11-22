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

import io.kubernetes.client.openapi.models.V1Capabilities;
import io.kubernetes.client.openapi.models.V1SELinuxOptions;
import io.kubernetes.client.openapi.models.V1WindowsSecurityContextOptions;

/**
 * @author JDev
 */
public class ContainerSecurityResult {
    private int id;
    private String resourceName = "";

    private Boolean allowPrivilegeEscalation;

    private V1Capabilities capabilities;
    private Boolean privileged;
    private String procMount;
    private Boolean readOnlyRootFilesystem;
    private Long runAsGroup;
    private Boolean runAsNonRoot;
    private Long runAsUser;
    private V1SELinuxOptions seLinuxOptions;
    private V1WindowsSecurityContextOptions windowsOptions;

    private String namespace = "";
    private String creationTime = "";
    private String fullDefinition = "";

    public ContainerSecurityResult() {
    }

    public ContainerSecurityResult(int id) {
        this.id = id;
    }

}