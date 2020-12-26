#ServiceAccount
resource "kubernetes_service_account" "kube_helper_service_account" {
  metadata {
    name = "kube-helper-sa"
    namespace = local.namespace
    labels = var.kube_helper_labels
  }
}

#Service
resource "kubernetes_service" "kube_helper_svc" {
  metadata {
    name = "kube-helper-svc"
    namespace = local.namespace
    labels = var.kube_helper_labels
  }

  spec {
    selector = var.kube_helper_selector

    port {
      name = "http"
      port = 8888
      target_port = 8080
      protocol = "TCP"
    }

    //    If you want a service with a visible port(NodePort), then uncomment this section and comment out the previous one.
    //    port {
    //      name = "http"
    //      port = 8888
    //      target_port = 8080
    //      node_port = 31222
    //      protocol = "TCP"
    //    }
    //
    //    type = "NodePort"
  }
}

#ClusterRole
resource "kubernetes_cluster_role" "kube_helper_cr" {
  metadata {
    name = "kube-helper-cr"
  }

  rule {
    api_groups = [
      ""]
    resources = [
      "apiservices",
      "bindings",
      "clusterrolebindings",
      "clusterroles",
      "componentstatuses",
      "configmaps",
      "controllerrevisions",
      "cronjobs",
      "customresourcedefinitions",
      "csidrivers",
      "csinodes",
      "daemonsets",
      "deployments",
      "events",
      "endpoints",
      "horizontalpodautoscalers",
      "ingress",
      "ingressclasses",
      "jobs",
      "limitranges",
      "localsubjectaccessreviews",
      "mutatingwebhookconfigurations",
      "namespaces",
      "networkpolicies",
      "nodes",
      "pods",
      "poddisruptionbudgets",
      "podsecuritypolicies",
      "podtemplates",
      "persistentvolumes",
      "persistentvolumeclaims",
      "priorityclasses",
      "resourcequotas",
      "replicasets",
      "replicationcontrollers",
      "rolebindings",
      "roles",
      "runtimeclasses",
      "secrets",
      "selfsubjectaccessreviews",
      "selfsubjectrulesreviews",
      "subjectaccessreviews",
      "serviceaccounts",
      "services",
      "statefulsets",
      "storageclasses",
      "tokenreviews",
      "validatingwebhookconfigurations"]
    verbs = [
      "get",
      "list"]
    non_resource_urls = [
      "*"]
  }
}

#ClusterRoleBinding
resource "kubernetes_cluster_role_binding" "kube_helper_crb" {
  metadata {
    name = "kube-helper-crb"
  }
  role_ref {
    api_group = "rbac.authorization.k8s.io/v1"
    kind = "ClusterRole"
    name = "${kubernetes_cluster_role.kube_helper_cr.metadata.0.name}"
  }
  subject {
    kind = "ServiceAccount"
    name = "${kubernetes_service_account.kube_helper_service_account.metadata.0.name}"
    namespace = local.namespace
  }
}

#Deployment
resource "kubernetes_deployment" "kube_helper_deployment" {
  metadata {
    name = "kube-helper-deployment"
    namespace = local.namespace
    labels = var.kube_helper_labels
  }

  spec {
    replicas = 1

    selector {
      match_labels = var.kube_helper_selector
    }

    template {
      metadata {
        labels = var.kube_helper_labels
      }

      spec {
        service_account_name = "${kubernetes_service_account.kube_helper_service_account.metadata.0.name}"
        automount_service_account_token = false
        #TODO change and test
        container {
          image = "kubehelper/kubehelper:1.0"
          name = "kube-helper"
          image_pull_policy = "Always"
          port {
            container_port = 8080
          }
          env {
            name = "KUBE_HELPER_UI_USERNAME"
            value = local.kube_helper_ui_username
          }
          env {
            name = "KUBE_HELPER_UI_PASSWORD"
            value = local.kube_helper_ui_password
          }
          //          TODO
          //          liveness_probe {
          //            tcp_socket {
          //              port = "8080"
          //            }
          //            initial_delay_seconds = 15
          //            period_seconds = 20
          //          }
        }
      }
    }
  }
}