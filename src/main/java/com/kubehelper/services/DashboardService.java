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

import io.fabric8.kubernetes.api.model.NodeList;
import io.fabric8.kubernetes.api.model.apps.DeploymentList;
import io.fabric8.kubernetes.api.model.networking.v1.NetworkPolicyList;
import io.fabric8.kubernetes.api.model.rbac.RoleList;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientException;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author JDev
 */
@Service
public class DashboardService {

    @Autowired
    private CoreV1Api api;

    public void showDashboard(){
        try (KubernetesClient client = new DefaultKubernetesClient()) {

            RoleList list = client.rbac().roles().list();
            NodeList list1 = client.nodes().list();
            DeploymentList list2 = client.apps().deployments().list();
            NetworkPolicyList list3 = client.network().networkPolicies().list();

            client.pods().inNamespace("default").list().getItems().forEach(
                    pod -> System.out.println(pod.getMetadata().getName())
            );

        } catch (KubernetesClientException ex) {
            // Handle exception
            ex.printStackTrace();
        }
    }
}
