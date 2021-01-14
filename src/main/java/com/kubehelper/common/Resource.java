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
package com.kubehelper.common;

import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;

/**
 * @author JDev
 */
public enum Resource {
    //    BINDING("Binding"), //Deprecated in 1.7, please use the bindings subresource of pods instead.
    ENV_VARIABLE("env", "EnvironmentVariable"),
    //    COMPONENT_STATUS("ComponentStatus"), //@ComponentStatus (and @ComponentStatusList) holds the cluster validation info.
    CONFIG_MAP("configmaps", "ConfigMap"),
    //    ENDPOINTS("Endpoints"),
    EVENT("events", "Event"),
    //    LIMIT_RANGE("LimitRange"),
    NAMESPACE("namespaces", "Namespace"),
    //    NODE("Node"),
    PERSISTENT_VOLUME_CLAIM("persistentvolumeclaims", "PersistentVolumeClaim"),
    PERSISTENT_VOLUME("persistentvolumes", "PersistentVolume"),
    POD("pods", "Pod"),
    //    POD_TEMPLATE("PodTemplate"),
//    REPLICATION_CONTROLLER("ReplicationController"),
//    RESOURCE_QUOTA("ResourceQuota"),
    SECRET("secrets", "Secret"),
    SERVICE_ACCOUNT("serviceaccounts", "ServiceAccount"),
    SERVICE("services", "Service"),
    //    MUTATING_WEBHOOK_CONFIGURATION("MutatingWebhookConfiguration"),
//    VALIDATING_WEBHOOK_CONFIGURATION("ValidatingWebhookConfiguration"),
    CUSTOM_RESOURCE_DEFINITION("customresourcedefinitions", "CustomResourceDefinition"),
    //    API_SERVICE("APIService"),
//    CONTROLLER_REVISION("ControllerRevision"),
    DAEMON_SET("daemonsets", "DaemonSet"),
    DEPLOYMENT("deployments", "Deployment"),
    REPLICA_SET("replicasets", "ReplicaSet"),
    STATEFUL_SET("statefulsets", "StatefulSet"),
    //    TOKEN_REVIEW("TokenReview"), //just skipped
//    LOCAL_SUBJECT_ACCESS_REVIEW("LocalSubjectAccessReview"), //just skipped
//    SELF_SUBJECT_ACCESS_REVIEW("SelfSubjectAccessReview"), //just skipped
//    SELF_SUBJECT_RULES_REVIEW("SelfSubjectRulesReview"), //just skipped
//    SUBJECT_ACCESS_REVIEW("SubjectAccessReview"), //just skipped
//    HORIZONTAL_POD_AUTOSCALER("HorizontalPodAutoscaler"), //just skipped
//    CRON_JOB("CronJob"),  //just skipped
    JOB("jobs", "Job"),
    //    CERTIFICATE_SIGNING_REQUEST("CertificateSigningRequest"), //just skipped
//    LEASE("Lease"), //just skipped
//    ENDPOINT_SLICE("EndpointSlice"), //just skipped
//    INGRESS("Ingress"), //just skipped
//    NODE_METRICS("NodeMetrics"), //just skipped
//    POD_METRICS("PodMetrics"),  //just skipped
//    INGRESS_CLASS("IngressClass"),  //just skipped
    NETWORK_POLICY("networkpolicies", "NetworkPolicy"),
    //    RUNTIME_CLASS("RuntimeClass"), //just skipped
    POD_DISRUPTION_BUDGET("poddisruptionbudgets", "PodDisruptionBudget"),
    POD_SECURITY_POLICY("podsecuritypolicies", "PodSecurityPolicy"),
    CLUSTER_ROLE_BINDING("clusterrolebindings", "ClusterRoleBinding"),
    CLUSTER_ROLE("clusterroles", "ClusterRole"),
    ROLE_BINDING("rolebindings", "RoleBinding"),
    ROLE("roles", "Role"),
    //    PRIORITY_CLASS("PriorityClass"), //just skipped
//    CSI_DRIVER("CSIDriver"), //just skipped
//    CSI_NODE("CSINode"), //just skipped
    STORAGE_CLASS("storageclasses", "StorageClass"),
    VOLUME_ATTACHMENT("volumeattachments", "VolumeAttachment"),
    KUBE_HELPER_CUSTOM("custom", "KubeHelperCustom"),
    KUBE_HELPER_CONTAINER_SECURITY_CONTEXT("custom", "KubeHelperContainerSecurityContext"),
    KUBE_HELPER_POD_SECURITY_CONTEXT("custom", "KubeHelperPodSecurityContext");

    private String name;
    private String kind;

    private static final Map<String, Resource> BY_RESOURCE_NAME = new HashMap<>();
    private static final Map<String, Resource> BY_RESOURCE_KIND = new HashMap<>();

    static {
        for (Resource resource : values()) {
            BY_RESOURCE_NAME.put(resource.getName(), resource);
            BY_RESOURCE_KIND.put(resource.getKind(), resource);
        }
    }

    Resource(String name, String kind) {
        this.name = name;
        this.kind = kind;
    }

    public String getKind() {
        return kind;
    }

    public String getName() {
        return name;
    }

    public static Resource getResourceByKind(String kind) {
        return BY_RESOURCE_KIND.get(kind);
    }

    public static Resource getResourceByName(String name) {
        return BY_RESOURCE_NAME.get(name);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Resource.class.getSimpleName() + "[", "]")
                .add("name='" + name + "'")
                .add("kind='" + kind + "'")
                .toString();
    }
}
