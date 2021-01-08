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
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.zkoss.zul.Messagebox;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.SequenceInputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * The common service contains method used by other services and view models.
 *
 * @author JDev
 */
@Service
public class CommonService {

    private ProcessBuilder processBuilder = new ProcessBuilder();

    @Autowired
    private CoreV1Api api;

    private KubernetesClient fabric8Client = new DefaultKubernetesClient();

    private static Logger logger = LoggerFactory.getLogger(CommonService.class);


    /**
     * Gets all namespaces plus all namespaces as choice in namespaces combobox.
     *
     * @return - list with namespaces.
     */
    public List<String> getAllNamespaces() {
        return getAllNamespaces(new ArrayList<>(Arrays.asList("all")));
    }

    /**
     * Gets all namespaces.
     *
     * @return - list with namespaces.
     */
    public List<String> getAllNamespacesWithoutAll() {
        return getAllNamespaces(new ArrayList<>());
    }

    private List<String> getAllNamespaces(List<String> initNamespaces) {
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


    /**
     * This method helps to check correct value at commbobox filters.
     *
     * @param resource - resource.
     * @param filter   - filter.
     * @return - true if resource match filter.
     */
    public boolean checkEqualsFilter(String resource, String filter) {
        if (filter.equals("")) {
            return true;
        }
        if (resource.equals(filter)) {
            return true;
        }
        return false;
    }

    /**
     * Reads file to string by path.
     *
     * @param path - file path.
     * @return - file as String.
     */
    public String getClasspathResourceAsStringByPath(String path) {
        try {
            return new String(FileCopyUtils.copyToByteArray(new ClassPathResource(path).getInputStream()), StandardCharsets.UTF_8);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        return "";
    }

    /**
     * Reads file to string by path.
     *
     * @param path - file path.
     * @return - file as String.
     */
    public String getResourceAsStringByPath(String path) {
        try {
            return IOUtils.toString(new FileInputStream(path));
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        return "";
    }

    /**
     * Gets Lines with strings from file by path.
     *
     * @param path - file path.
     * @return - Lines with strings.
     */
    public List<String> getLinesFromResourceByPath(String path) {
        List<String> lines = new ArrayList<>();
        ClassPathResource cpr = new ClassPathResource(path);
        try {
            lines = new BufferedReader(new InputStreamReader(cpr.getInputStream())).lines().collect(Collectors.toList());
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        return lines;
    }

    /**
     * Gets sets of Strings with paths to files from folder.
     *
     * @param dir       - folder to search files.
     * @param depth     - recursive look depth to folder.
     * @param extension - file extension
     * @return sets of Strings(paths) with paths to files
     * @throws IOException - exception.
     */
    public Set<String> getFilesPathsByDirAndExtension(String dir, int depth, String extension) throws IOException {
        try (Stream<Path> stream = java.nio.file.Files.walk(Paths.get(dir), depth)) {
            return stream
                    .map(path -> path.toString()).filter(f -> f.endsWith(extension))
                    .collect(Collectors.toSet());
        }
    }

    public org.springframework.core.io.Resource[] getFilesPathsFromClasspathByDirAndExtension(String dir, String extension) throws IOException {
        return new PathMatchingResourcePatternResolver().getResources("classpath:" + dir + "/*" + extension);
    }


    /**
     * Gets Kubernetes resource by KubeHelper Resource and resource name.
     * <p>
     * KUBE_HELPER_POD_SECURITY_CONTEXT, KUBE_HELPER_CONTAINER_SECURITY_CONTEXT and other resources that do not implements Interface @{@link HasMetadata} cannot be converted in Yaml.
     *
     * @param resource     - {@link Resource}
     * @param resourceName - resource name.
     * @return - Found {@link KubernetesResource}
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


    /**
     * Gets Kubernetes resource as Yaml string by KubeHelper Resource and resource name.
     *
     * @param resource     - {@link Resource}
     * @param resourceName - resource name.
     * @return - Kubernetes resource as Yaml String.
     */
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
     * Gets Kubernetes resource as Json string by KubeHelper Resource, resource name and namespace.
     *
     * @param resource     - {@link Resource}
     * @param resourceName - resource name.
     * @param namespace    - namespace.
     * @return - Kubernetes resource as Yaml String.
     */
    public String getJsonResource(Resource resource, String resourceName, String namespace) {
        namespace = (StringUtils.isBlank(namespace) || "N/A".equals(namespace)) ? "" : "--namespace=" + namespace;
        String command = String.format("kubectl get %s %s %s -ojson", resource.getName(), resourceName, namespace);
        return executeCommand("bash", command);
    }

    /**
     * Executes kubectl command from shell.
     *
     * @param shell   - shell type.
     * @param command - command to execute.
     * @return - inputStream and errorStream as String.
     */
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

    /**
     * Reaads data from stream to lines and then to string.
     *
     * @param inputStream - inputStream and errorStream
     * @return - readed string.
     * @throws IOException
     */
    private String readOutput(InputStream inputStream) throws IOException {
        try (BufferedReader output = new BufferedReader(new InputStreamReader(inputStream))) {
            return output.lines().map(line -> line = line + "\n").reduce("", String::concat);
        }
    }

    public void cloneGitRepo() {

    }

    public void pullGitRepo() {
    }

    public void pushGitRepo() {

    }
}
