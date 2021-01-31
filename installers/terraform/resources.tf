#ServiceAccount
resource "kubernetes_service_account" "kubehelper_service_account" {
  metadata {
    name = "kubehelper-sa"
    namespace = local.namespace
    labels = var.kubehelper_labels
  }
}

#Service
resource "kubernetes_service" "kubehelper_svc" {
  metadata {
    name = "kubehelper-svc"
    namespace = local.namespace
    labels = var.kubehelper_labels
  }

  spec {
    selector = var.kubehelper_selector

    port {
      name = "http"
      port = 80
      target_port = 8080
      //      node_port = 31222
      protocol = "TCP"
    }

    type = "ClusterIP"
    //        type = "NodePort"
  }
}

#ClusterRole
resource "kubernetes_cluster_role" "kubehelper_cr" {
  metadata {
    name = "kubehelper-cr"
  }
  rule {
    api_groups = [
      "*"]
    resources = [
      "*"]
    verbs = [
      "get",
      "list"]
  }
}

#ClusterRoleBinding
resource "kubernetes_cluster_role_binding" "kubehelper_crb" {
  metadata {
    name = "kubehelper-crb"
  }
  role_ref {
    api_group = "rbac.authorization.k8s.io"
    kind = "ClusterRole"
    name = kubernetes_cluster_role.kubehelper_cr.metadata[0].name
  }
  subject {
    kind = "ServiceAccount"
    name = kubernetes_service_account.kubehelper_service_account.metadata[0].name
    namespace = local.namespace
  }
}

#Deployment
resource "kubernetes_deployment" "kubehelper_deployment" {
  metadata {
    name = "kubehelper-deployment"
    namespace = local.namespace
    labels = var.kubehelper_labels
  }

  spec {
    replicas = 1

    selector {
      match_labels = var.kubehelper_selector
    }

    template {
      metadata {
        labels = var.kubehelper_labels
      }

      spec {
        service_account_name = kubernetes_service_account.kubehelper_service_account.metadata[0].name
        automount_service_account_token = true
        security_context {
          run_as_non_root = true
          run_as_user = 1000
        }
        container {
          image = "kubehelper/kubehelper:1.0.0"
          name = "kubehelper"
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
        }
        node_name = "node1"
      }
    }
  }
}