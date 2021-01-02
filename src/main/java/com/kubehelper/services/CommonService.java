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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.kubehelper.common.Resource;
import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.KubernetesResource;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.internal.SerializationUtils;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1NamespaceList;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.zkoss.zul.Messagebox;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.SequenceInputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * @author JDev
 */
@Service
public class CommonService {

    private ProcessBuilder processBuilder = new ProcessBuilder();

    @Autowired
    private CoreV1Api api;

    private KubernetesClient fabric8Client = new DefaultKubernetesClient();

    private static Logger logger = LoggerFactory.getLogger(CommonService.class);

    public List<String> getAllNamespaces(List<String> initNamespaces) {
        V1NamespaceList v1NamespacesList = null;
        try {
            v1NamespacesList = api.listNamespace(null, false, null, null, null, 0, null, 30, false);
            v1NamespacesList.getItems().forEach(v1Namespace -> initNamespaces.add(v1Namespace.getMetadata().getName()));
        } catch (ApiException e) {
            Messagebox.show(e.getMessage(), "Fetch Namespaces Error", Messagebox.OK, Messagebox.ERROR);
            e.printStackTrace();
        }
        return initNamespaces;
    }

    public List<String> getAllNamespaces() {
        return getAllNamespaces(new ArrayList<>(Arrays.asList("all")));
    }

    public List<String> getAllNamespacesWithoutAll() {
        return getAllNamespaces(new ArrayList<>());
    }


    public boolean checkEqualsFilter(String resource, String filter) {
        if (filter.equals("")) {
            return true;
        }
        if (resource.equals(filter)) {
            return true;
        }
        return false;
    }

    public String getResourcesAsStringByPath(String path) {
        String data = "";
        ClassPathResource cpr = new ClassPathResource(path);
        try {
            byte[] bdata = FileCopyUtils.copyToByteArray(cpr.getInputStream());
            data = new String(bdata, StandardCharsets.UTF_8);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        return data;
    }

    public File getResourcesAsFileByPath(String path) {
        File file = null;
        try {
            file = new ClassPathResource(path).getFile();
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        return file;
    }

    public String getYamlResource(Resource resource, String resourceName) {
        KubernetesResource found = getHasMetadata(resource, resourceName);
        if (Objects.nonNull(found)) {
            try {
                return SerializationUtils.dumpAsYaml((HasMetadata) found);
            } catch (JsonProcessingException e) {
                logger.error(String.format("getYamlResource: Resource=%s, resourceName=%s", resource.getKind(), resourceName) + e.getMessage(), e);
            }
        }
        return "";
    }

    /**
     * KUBE_HELPER_POD_SECURITY_CONTEXT, KUBE_HELPER_CONTAINER_SECURITY_CONTEXT and other resources that do not implements Interface @{@link HasMetadata} cannot be converted in Yaml.
     *
     * @param resource
     * @param resourceName
     * @return
     */
    private KubernetesResource getHasMetadata(Resource resource, String resourceName) {
        KubernetesResource meta = null;
        try {
            switch (resource) {
                case CONFIG_MAP -> meta = fabric8Client.configMaps().list().getItems().stream().filter(map -> resourceName.equals(map.getMetadata().getName())).findFirst().get();
                case EVENT -> meta = fabric8Client.v1().events().list().getItems().stream().filter(event -> resourceName.equals(event.getMetadata().getName())).findFirst().get();
                case NAMESPACE -> meta = fabric8Client.namespaces().list().getItems().stream().filter(ns -> resourceName.equals(ns.getMetadata().getName())).findFirst().get();
                case PERSISTENT_VOLUME_CLAIM -> meta = fabric8Client.persistentVolumeClaims().list().getItems().stream().filter(pvc -> resourceName.equals(pvc.getMetadata().getName())).findFirst().get();
                case PERSISTENT_VOLUME -> meta = fabric8Client.persistentVolumes().list().getItems().stream().filter(pv -> resourceName.equals(pv.getMetadata().getName())).findFirst().get();
                case POD -> meta = fabric8Client.pods().list().getItems().stream().filter(pod -> resourceName.equals(pod.getMetadata().getName())).findFirst().get();
                case SECRET -> meta = fabric8Client.secrets().list().getItems().stream().filter(secret -> resourceName.equals(secret.getMetadata().getName())).findFirst().get();
                case SERVICE_ACCOUNT -> meta = fabric8Client.serviceAccounts().list().getItems().stream().filter(sa -> resourceName.equals(sa.getMetadata().getName())).findFirst().get();
                case SERVICE -> meta = fabric8Client.services().list().getItems().stream().filter(svc -> resourceName.equals(svc.getMetadata().getName())).findFirst().get();
                case DAEMON_SET -> meta = fabric8Client.apps().daemonSets().list().getItems().stream().filter(ds -> resourceName.equals(ds.getMetadata().getName())).findFirst().get();
                case DEPLOYMENT -> meta = fabric8Client.apps().deployments().list().getItems().stream().filter(d -> resourceName.equals(d.getMetadata().getName())).findFirst().get();
                case REPLICA_SET -> meta = fabric8Client.apps().replicaSets().list().getItems().stream().filter(rs -> resourceName.equals(rs.getMetadata().getName())).findFirst().get();
                case STATEFUL_SET -> meta = fabric8Client.apps().statefulSets().list().getItems().stream().filter(ss -> resourceName.equals(ss.getMetadata().getName())).findFirst().get();
                case JOB -> meta = fabric8Client.batch().jobs().list().getItems().stream().filter(job -> resourceName.equals(job.getMetadata().getName())).findFirst().get();
                case NETWORK_POLICY -> meta = fabric8Client.network().networkPolicies().list().getItems().stream().filter(p -> resourceName.equals(p.getMetadata().getName())).findFirst().get();
                case POD_DISRUPTION_BUDGET -> meta = fabric8Client.policy().podDisruptionBudget().list().getItems().stream().filter(p -> resourceName.equals(p.getMetadata().getName())).findFirst().get();
                case POD_SECURITY_POLICY -> meta = fabric8Client.policy().podSecurityPolicies().list().getItems().stream().filter(p -> resourceName.equals(p.getMetadata().getName())).findFirst().get();
                case CLUSTER_ROLE_BINDING -> meta = fabric8Client.rbac().clusterRoleBindings().list().getItems().stream().filter(crb -> resourceName.equals(crb.getMetadata().getName())).findFirst().get();
                case CLUSTER_ROLE -> meta = fabric8Client.rbac().clusterRoles().list().getItems().stream().filter(cr -> resourceName.equals(cr.getMetadata().getName())).findFirst().get();
                case ROLE_BINDING -> meta = fabric8Client.rbac().clusterRoleBindings().list().getItems().stream().filter(rb -> resourceName.equals(rb.getMetadata().getName())).findFirst().get();
                case ROLE -> meta = fabric8Client.rbac().roles().list().getItems().stream().filter(role -> resourceName.equals(role.getMetadata().getName())).findFirst().get();
                case STORAGE_CLASS -> meta = fabric8Client.storage().storageClasses().list().getItems().stream().filter(sc -> resourceName.equals(sc.getMetadata().getName())).findFirst().get();
            }
        } catch (RuntimeException e) {
            if (logger.isDebugEnabled()) {
                logger.error(String.format("getHasMetadata: Resource=%s, resourceName=%s", resource.getKind(), resourceName) + e.getMessage(), e);
            }
        }
        return meta;
    }

    //    ~ » exec kubectl exec -i -t -n kube-system calico-node-q5jd2 -c calico-node "--" sh -c "clear; (bash || ash || sh)"
    public String getJsonResource(Resource resource, String resourceName, String namespace) {
        namespace = (StringUtils.isBlank(namespace) || "N/A".equals(namespace)) ? "" : "--namespace=" + namespace;
        String command = String.format("kubectl get %s %s %s -ojson", resource.getName(), resourceName, namespace);
        return executeCommand("bash", command);
    }

    public String executeCommand(String shell, String command) {
        String result = "";
        processBuilder.command(shell, "-c", command);
        Process process;
        try {
            process = processBuilder.start();
            result = readOutput(new SequenceInputStream(process.getInputStream(), process.getErrorStream()));
        } catch (IOException e) {
            if (logger.isDebugEnabled()) {
                logger.error(String.format("executeCommand: Command=%s", command) + e.getMessage(), e);
            }
        }
        return result;
    }

    private String readOutput(InputStream inputStream) throws IOException {
        try (BufferedReader output = new BufferedReader(new InputStreamReader(inputStream))) {
            return output.lines().map(line -> line = line + "\n").reduce("", String::concat);
        }
    }

}
