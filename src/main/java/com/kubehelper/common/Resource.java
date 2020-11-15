package com.kubehelper.common;

import java.util.HashMap;
import java.util.Map;

/**
 * @author JDev
 */
public enum Resource {
//    BINDING("Binding"), //Deprecated in 1.7, please use the bindings subresource of pods instead.
    ENV_VARIABLE("EnvironmentVariable"),
//    COMPONENT_STATUS("ComponentStatus"), //@ComponentStatus (and @ComponentStatusList) holds the cluster validation info.
    CONFIG_MAP("ConfigMap"),
//    ENDPOINTS("Endpoints"),
    EVENT("Event"),
//    LIMIT_RANGE("LimitRange"),
    NAMESPACE("Namespace"),
//    NODE("Node"),
    PERSISTENT_VOLUME_CLAIM("PersistentVolumeClaim"),
    PERSISTENT_VOLUME("PersistentVolume"),
    POD("Pod"),
//    POD_TEMPLATE("PodTemplate"),
//    REPLICATION_CONTROLLER("ReplicationController"),
//    RESOURCE_QUOTA("ResourceQuota"),
    SECRET("Secret"),
    SERVICE_ACCOUNT("ServiceAccount"),
    SERVICE("Service"),
//    MUTATING_WEBHOOK_CONFIGURATION("MutatingWebhookConfiguration"),
//    VALIDATING_WEBHOOK_CONFIGURATION("ValidatingWebhookConfiguration"),
    CUSTOM_RESOURCE_DEFINITION("CustomResourceDefinition"),
//    API_SERVICE("APIService"),
//    CONTROLLER_REVISION("ControllerRevision"),
    DAEMON_SET("DaemonSet"),
    DEPLOYMENT("Deployment"),
    REPLICA_SET("ReplicaSet"),
    STATEFUL_SET("StatefulSet"),
//    TOKEN_REVIEW("TokenReview"), //just skipped
//    LOCAL_SUBJECT_ACCESS_REVIEW("LocalSubjectAccessReview"), //just skipped
//    SELF_SUBJECT_ACCESS_REVIEW("SelfSubjectAccessReview"), //just skipped
//    SELF_SUBJECT_RULES_REVIEW("SelfSubjectRulesReview"), //just skipped
//    SUBJECT_ACCESS_REVIEW("SubjectAccessReview"), //just skipped
//    HORIZONTAL_POD_AUTOSCALER("HorizontalPodAutoscaler"), //just skipped
//    CRON_JOB("CronJob"),  //just skipped
    JOB("Job"),
//    CERTIFICATE_SIGNING_REQUEST("CertificateSigningRequest"), //just skipped
//    LEASE("Lease"), //just skipped
//    ENDPOINT_SLICE("EndpointSlice"), //just skipped
//    INGRESS("Ingress"), //just skipped
//    NODE_METRICS("NodeMetrics"), //just skipped
//    POD_METRICS("PodMetrics"),  //just skipped
//    INGRESS_CLASS("IngressClass"),  //just skipped
    NETWORK_POLICY("NetworkPolicy"),
//    RUNTIME_CLASS("RuntimeClass"), //just skipped
    POD_DISRUPTION_BUDGET("PodDisruptionBudget"),
    POD_SECURITY_POLICY("PodSecurityPolicy"),
    CLUSTER_ROLE_BINDING("ClusterRoleBinding"),
    CLUSTER_ROLE("ClusterRole"),
    ROLE_BINDING("RoleBinding"),
    ROLE("Role"),
//    PRIORITY_CLASS("PriorityClass"), //just skipped
//    CSI_DRIVER("CSIDriver"), //just skipped
//    CSI_NODE("CSINode"), //just skipped
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
