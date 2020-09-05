package com.kubehelper.common;

import java.util.HashMap;
import java.util.Map;

/**
 * @author JDev
 */
public enum Resource {
    BINDING("Binding"),
    ENV_VARIABLE("EnvironmentVariable"),
    COMPONENT_STATUS("ComponentStatus"),
    CONFIG_MAP("ConfigMap"),
    ENDPOINTS("Endpoints"),
    EVENT("Event"),
    LIMIT_RANGE("LimitRange"),
    NAMESPACE("Namespace"),
    NODE("Node"),
    PERSISTENT_VOLUME_CLAIM("PersistentVolumeClaim"),
    PERSISTENT_VOLUME("PersistentVolume"),
    POD("Pod"),
    POD_TEMPLATE("PodTemplate"),
    REPLICATION_CONTROLLER("ReplicationController"),
    RESOURCE_QUOTA("ResourceQuota"),
    SECRET("Secret"),
    SERVICE_ACCOUNT("ServiceAccount"),
    SERVICE("Service"),
    MUTATING_WEBHOOK_CONFIGURATION("MutatingWebhookConfiguration"),
    VALIDATING_WEBHOOK_CONFIGURATION("ValidatingWebhookConfiguration"),
    CUSTOM_RESOURCE_DEFINITION("CustomResourceDefinition"),
    API_SERVICE("APIService"),
    CONTROLLER_REVISION("ControllerRevision"),
    DAEMON_SET("DaemonSet"),
    DEPLOYMENT("Deployment"),
    REPLICA_SET("ReplicaSet"),
    STATEFUL_SET("StatefulSet"),
    TOKEN_REVIEW("TokenReview"),
    LOCAL_SUBJECT_ACCESS_REVIEW("LocalSubjectAccessReview"),
    SELF_SUBJECT_ACCESS_REVIEW("SelfSubjectAccessReview"),
    SELF_SUBJECT_RULES_REVIEW("SelfSubjectRulesReview"),
    SUBJECT_ACCESS_REVIEW("SubjectAccessReview"),
    HORIZONTAL_POD_AUTOSCALER("HorizontalPodAutoscaler"),
    CRON_JOB("CronJob"),
    JOB("Job"),
    CERTIFICATE_SIGNING_REQUEST("CertificateSigningRequest"),
    LEASE("Lease"),
    ENDPOINT_SLICE("EndpointSlice"),
    INGRESS("Ingress"),
    NODE_METRICS("NodeMetrics"),
    POD_METRICS("PodMetrics"),
    INGRESS_CLASS("IngressClass"),
    NETWORK_POLICY("NetworkPolicy"),
    RUNTIME_CLASS("RuntimeClass"),
    POD_DISRUPTION_BUDGET("PodDisruptionBudget"),
    POD_SECURITY_POLICY("PodSecurityPolicy"),
    CLUSTER_ROLE_BINDING("ClusterRoleBinding"),
    CLUSTER_ROLE("ClusterRole"),
    ROLE_BINDING("RoleBinding"),
    ROLE("Role"),
    PRIORITY_CLASS("PriorityClass"),
    CSI_DRIVER("CSIDriver"),
    CSI_NODE("CSINode"),
    STORAGE_CLASS("StorageClass"),
    VOLUME_ATTACHMENT("VolumeAttachment");

    private String value;

    private static final Map<Resource, String> resourcesMap = new HashMap<Resource, String>();

    static {
        for (Resource resource : values()) {
            resourcesMap.put(resource, resource.getValue());
        }
    }


    Resource(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static String getValueByKey(String key) {
        return resourcesMap.get(Resource.valueOf(key));
    }

}
