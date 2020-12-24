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

import com.kubehelper.domain.models.PageModel;
import io.kubernetes.client.common.KubernetesObject;
import io.kubernetes.client.custom.NodeMetrics;
import io.kubernetes.client.custom.PodMetrics;
import io.kubernetes.client.extended.kubectl.Kubectl;
import io.kubernetes.client.extended.kubectl.KubectlApiResources;
import io.kubernetes.client.extended.kubectl.KubectlExec;
import io.kubernetes.client.extended.kubectl.KubectlGet;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.AppsV1Api;
import io.kubernetes.client.openapi.apis.BatchV1Api;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.apis.NetworkingV1Api;
import io.kubernetes.client.openapi.apis.PolicyV1beta1Api;
import io.kubernetes.client.openapi.apis.RbacAuthorizationV1beta1Api;
import io.kubernetes.client.openapi.models.V1Binding;
import io.kubernetes.client.openapi.models.V1ConfigMapList;
import io.kubernetes.client.openapi.models.V1DaemonSetList;
import io.kubernetes.client.openapi.models.V1Deployment;
import io.kubernetes.client.openapi.models.V1DeploymentList;
import io.kubernetes.client.openapi.models.V1JobList;
import io.kubernetes.client.openapi.models.V1NamespaceList;
import io.kubernetes.client.openapi.models.V1NetworkPolicyList;
import io.kubernetes.client.openapi.models.V1Node;
import io.kubernetes.client.openapi.models.V1NodeList;
import io.kubernetes.client.openapi.models.V1PersistentVolumeClaimList;
import io.kubernetes.client.openapi.models.V1PersistentVolumeList;
import io.kubernetes.client.openapi.models.V1Pod;
import io.kubernetes.client.openapi.models.V1PodList;
import io.kubernetes.client.openapi.models.V1ReplicaSetList;
import io.kubernetes.client.openapi.models.V1ReplicationController;
import io.kubernetes.client.openapi.models.V1SecretList;
import io.kubernetes.client.openapi.models.V1Service;
import io.kubernetes.client.openapi.models.V1ServiceAccountList;
import io.kubernetes.client.openapi.models.V1ServiceList;
import io.kubernetes.client.openapi.models.V1StatefulSetList;
import io.kubernetes.client.openapi.models.V1beta1ClusterRoleBinding;
import io.kubernetes.client.openapi.models.V1beta1ClusterRoleBindingList;
import io.kubernetes.client.openapi.models.V1beta1ClusterRoleList;
import io.kubernetes.client.openapi.models.V1beta1PodDisruptionBudgetList;
import io.kubernetes.client.openapi.models.V1beta1PodSecurityPolicyList;
import io.kubernetes.client.openapi.models.V1beta1RoleBinding;
import io.kubernetes.client.openapi.models.V1beta1RoleBindingList;
import io.kubernetes.client.openapi.models.V1beta1RoleList;
import io.kubernetes.client.util.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.rmi.dgc.Lease;

/**
 * @author JDev
 */
@Service
public class KubectlHelper {

    private static Logger logger = LoggerFactory.getLogger(KubectlHelper.class);

    @Autowired
    private CoreV1Api apiV1;

    @Autowired
    private AppsV1Api appsV1Api;

    @Autowired
    private BatchV1Api batchV1Api;

    @Autowired
    private RbacAuthorizationV1beta1Api rbacAuthorizationV1beta1Api;

    @Autowired
    private NetworkingV1Api networkingApi;

    @Autowired
    private PolicyV1beta1Api policyV1beta1Api;

    public static KubectlApiResources getKubectlApiResources() throws IOException {
        return Kubectl.apiResources().apiClient(Config.defaultClient());
    }

    public static KubectlExec getKubectlExec() throws IOException {
        return Kubectl.exec().apiClient(Config.defaultClient());
    }

    public static KubectlGet getKubectlGet(String apiTypeClassString) throws IOException {
        return Kubectl.get(getClassForKind(apiTypeClassString)).apiClient(Config.defaultClient());
    }

//    private static Class resolveApiTypeClass(String apiTypeClassString){
////        Class<ApiType> apiTypeClass = null;
//        switch (apiTypeClassString) {
//            case "binding","bindings" -> apiTypeClass = V1Binding.class;
//            case "pods" -> apiTypeClass = V1Pod.class;
////            case "list" -> list = true;
////            case "create" -> create = true;
////            case "update" -> update = true;
////            case "patch" -> patch = true;
////            case "watch" -> watch = true;
////            case "delete" -> delete = true;
////            case "deletecollection" -> deletecollection = true;
////            default -> others = verb;
//        }
//        return apiTypeClass;
//    }

    static Class<? extends KubernetesObject> getClassForKind(String kind) {
        switch (kind) {
            case "pod":
            case "pods":
                return V1Pod.class;
            case "deployment":
            case "deployments":
                return V1Deployment.class;
            case "service":
            case "services":
                return V1Service.class;
            case "node":
            case "nodes":
                return V1Node.class;
            case "replicationcontroller":
            case "replicationcontrollers":
                return V1ReplicationController.class;
        }
        return null;
    }

//    bindings                                                                      true         Binding
//    componentstatuses                 cs                                          false        ComponentStatus
//    configmaps                        cm                                          true         ConfigMap
//    endpoints                         ep                                          true         Endpoints
//    events                            ev                                          true         Event
//    limitranges                       limits                                      true         LimitRange
//    namespaces                        ns                                          false        Namespace
//    nodes                             no                                          false        Node
//    persistentvolumeclaims            pvc                                         true         PersistentVolumeClaim
//    persistentvolumes                 pv                                          false        PersistentVolume
//    pods                              po                                          true         Pod
//    podtemplates                                                                  true         PodTemplate
//    replicationcontrollers            rc                                          true         ReplicationController
//    resourcequotas                    quota                                       true         ResourceQuota
//    secrets                                                                       true         Secret
//    serviceaccounts                   sa                                          true         ServiceAccount
//    services                          svc                                         true         Service
//    mutatingwebhookconfigurations                  admissionregistration.k8s.io   false        MutatingWebhookConfiguration
//    validatingwebhookconfigurations                admissionregistration.k8s.io   false        ValidatingWebhookConfiguration
//    customresourcedefinitions         crd,crds     apiextensions.k8s.io           false        CustomResourceDefinition
//    apiservices                                    apiregistration.k8s.io         false        APIService
//    controllerrevisions                            apps                           true         ControllerRevision
//    daemonsets                        ds           apps                           true         DaemonSet
//    deployments                       deploy       apps                           true         Deployment
//    replicasets                       rs           apps                           true         ReplicaSet
//    statefulsets                      sts          apps                           true         StatefulSet
//    tokenreviews                                   authentication.k8s.io          false        TokenReview
//    localsubjectaccessreviews                      authorization.k8s.io           true         LocalSubjectAccessReview
//    selfsubjectaccessreviews                       authorization.k8s.io           false        SelfSubjectAccessReview
//    selfsubjectrulesreviews                        authorization.k8s.io           false        SelfSubjectRulesReview
//    subjectaccessreviews                           authorization.k8s.io           false        SubjectAccessReview
//    horizontalpodautoscalers          hpa          autoscaling                    true         HorizontalPodAutoscaler
//    cronjobs                          cj           batch                          true         CronJob
//    jobs                                           batch                          true         Job
//    certificatesigningrequests        csr          certificates.k8s.io            false        CertificateSigningRequest
//    leases                                         coordination.k8s.io            true         Lease
//    endpointslices                                 discovery.k8s.io               true         EndpointSlice
//    events                            ev           events.k8s.io                  true         Event
//    ingresses                         ing          extensions                     true         Ingress
//    nodes                                          metrics.k8s.io                 false        NodeMetrics
//    pods                                           metrics.k8s.io                 true         PodMetrics
//    ingressclasses                                 networking.k8s.io              false        IngressClass
//    ingresses                         ing          networking.k8s.io              true         Ingress
//    networkpolicies                   netpol       networking.k8s.io              true         NetworkPolicy
//    runtimeclasses                                 node.k8s.io                    false        RuntimeClass
//    poddisruptionbudgets              pdb          policy                         true         PodDisruptionBudget
//    podsecuritypolicies               psp          policy                         false        PodSecurityPolicy
//    clusterrolebindings                            rbac.authorization.k8s.io      false        ClusterRoleBinding
//    clusterroles                                   rbac.authorization.k8s.io      false        ClusterRole
//    rolebindings                                   rbac.authorization.k8s.io      true         RoleBinding
//    roles                                          rbac.authorization.k8s.io      true         Role
//    priorityclasses                   pc           scheduling.k8s.io              false        PriorityClass
//    csidrivers                                     storage.k8s.io                 false        CSIDriver
//    csinodes                                       storage.k8s.io                 false        CSINode
//    storageclasses                    sc           storage.k8s.io                 false        StorageClass
//    volumeattachments                              storage.k8s.io                 false        VolumeAttachment

}
