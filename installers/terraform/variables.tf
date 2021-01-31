locals {
  namespace = "infra"
  kube_helper_ui_username = "kube"
  kube_helper_ui_password = "helper"
}

variable "kubehelper_labels" {
  default = {
    app = "kubehelper"
    ns = "infra"
  }
}

variable "kubehelper_selector" {
  default = {
    app = "kubehelper"
    ns = "infra"
  }
}