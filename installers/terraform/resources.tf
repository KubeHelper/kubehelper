//TODO add ClusterRole/ClusterRoleBinding

#Service Account
resource "kubernetes_service_account" "kube_helper_service_account" {
  metadata {
    name = "kube-helper-sa"
  }
  secret {
    name = "${kubernetes_secret.kube_helper_secret.metadata.0.name}"
  }
}

resource "kubernetes_secret" "kube_helper_secret" {
  metadata {
    name = "kube-helper-secret"
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
    selector = var.kube_helper_labels

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
      match_labels = var.kube_helper_labels
    }

    template {
      metadata {
        labels = var.kube_helper_labels
      }

      spec {
        container {
          image = "kubehelper:1.0"
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