locals {
  namespace = "default"
  kube_helper_ui_username = "admin"
  kube_helper_ui_password = "admin13"
}

variable "kube_helper_labels" {
  default = {
    app = "kube-helper"
    ns = local.namespace
  }
}