locals {
  namespace = "default"
  kube_helper_ui_username = "kube"
  kube_helper_ui_password = "helper"
}

variable "kube_helper_labels" {
  default = {
    app = "kube-helper"
    kubernetes.io/cluster-service = "true"
    ns = local.namespace
  }
}

variable "kube_helper_selector" {
  default = {
    app = "kube-helper"
    ns = local.namespace
  }
}