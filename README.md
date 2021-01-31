# [Kube Helper](https://github.com/KubeHelper/kubehelper)  

<p align="center">
  <img src="https://github.com/KubeHelper/kubehelper/blob/develop/src/main/resources/web/img/kube-helper-512.png" width="300" />
</p>
 
---       
##### KubeHelper - simplifies many daily cluster tasks through a graphical web interface. Search, analysis, run commands, cron jobs, reports, filters, git config and many more.     

[![LICENSE](https://img.shields.io/badge/license-GNU%20v3-green)](https://github.com/KubeHelper/kubehelper/blob/main/LICENSE)
![Docker Pulls](https://img.shields.io/docker/pulls/kubehelper/kubehelper)
![Docker Stars](https://img.shields.io/docker/stars/kubehelper/kubehelper)
![Snyk Vulnerabilities for GitHub Repo](https://img.shields.io/snyk/vulnerabilities/github/KubeHelper/kubehelper)
![Sonar Coverage](https://img.shields.io/sonar/coverage/kubehelper?server=https%3A%2F%2Fsonarcloud.io)
![Sonar Quality Gate](https://img.shields.io/sonar/quality_gate/kubehelper?server=https%3A%2F%2Fsonarcloud.io)
![Liberapay receiving](https://img.shields.io/liberapay/receives/kubehelper)
![Open Collective backers and sponsors](https://img.shields.io/opencollective/all/kubehelper)
![GitHub Sponsors](https://img.shields.io/github/sponsors/kubehelper?style=social)
![GitHub Repo stars](https://img.shields.io/github/stars/KubeHelper/kubehelper?style=social)

## Features

* [Dashboard - common cluster information.](https://github.com/KubeHelper/kubehelper/wiki/Home)
* [Search - search, filter, view resources in a cluster.](https://github.com/KubeHelper/kubehelper/wiki/Home)
* [Ips and Ports - search, filter, view services and pods ips, ports and detailed information.](https://github.com/KubeHelper/kubehelper/wiki/Home)
* [Security - search, filter, view roles, rules, RBAC, pod and container security contexts, service accounts, pod security policies.](https://github.com/KubeHelper/kubehelper/wiki/Home)
* [Labels Annotations Selectors - search, filter, view, group of labels, annotations and selectors in resources.](https://github.com/KubeHelper/kubehelper/wiki/Home)
* [Commands - commands execution, management, creation, history.](https://github.com/KubeHelper/kubehelper/wiki/Home)
* [Cron Jobs - creating, execution, control of cron jobs and view reports.](https://github.com/KubeHelper/kubehelper/wiki/Home)
* [Configurations - configure KubeHelper, change config, push, pull config commands, cron jobs from repository.](https://github.com/KubeHelper/kubehelper/wiki/Home)
* [Versions - KubeHelper utils, shells and plugins versions.](https://github.com/KubeHelper/kubehelper/wiki/Home)

![KubeHelper](screenshots/main.png)

## Motivation
              
## Quick start
Before installing KubeHelper in your cluster, you can configure the installation so that the KubeHelper is visible through the NodePort otherwise by default KubeHelper creates Service with 
``ClusterIP`` and you need to specify service ``http://kubehelper-svc.YOUR-NAMESPACE/`` in your ingress or proxy.   

KubeHelper has basic protection with a username and password. You can customize/replace them by replacing the environment variables in the deployment. By default, you can login with these 
credentials (username/password) **``kube/helper``**.   

The interface consists of two parts, the control panel on the left and content area. KubeHelper combines a lot of different functionality that is divided into sections. Everyone will find 
something for themselves.  

❗&nbsp; Attention, if you have a wonderful command that will be useful to the community and you want to share it. Please write a message in the discussions, or even better, add a message with a 
command and description in accordance with the KubeHelper format. You can see how should command looks like [here.](https://github.com/KubeHelper/kubehelper/wiki/Installation)

For your safety, KubeHelper is installed with read rights. Using the KubeHelper with default settings, you cannot modify, create, or delete resources. Therefore, you can only execute commands intended for reading and viewing resources.  

But KubeHelper is very flexible in this regard, when installing the program, you can change the ClusterRole rights up to the cluster administrator &nbsp; 💪. ❗&nbsp; Be careful with these rights!

In this case, you can execute any commands and perform any actions with the cluster from web GUI.  

Read more about fine-tuning and customization in the [installation section.](https://github.com/KubeHelper/kubehelper/wiki/Installation)

🔥 🔥 🔥 &nbsp; Happy using. &nbsp; 🔥🔥🔥

🚀 🚀 🚀 ⭐⭐⭐ &nbsp; **Thank you in advance for your support, repost, fork, star.** &nbsp; ⭐⭐⭐🚀🚀🚀

## Installation 
### Helm
[Configure and customize Helm installation.](https://github.com/KubeHelper/kubehelper/wiki/Installation)
#### Installing KubeHelper with Helm.
//TODO.
```shell
KUBE_HELPER_NAMESPACE="YOUR_NAMESPACE_NAME"
helm repo add jdev 'https://github.com/JWebDev/helm-charts/'
helm repo update
helm search <your-chart-name>
https://helm.sh/docs/topics/chart_repository/
```
#### Remove KubeHelper with kubectl.
```shell
kubectl delete deploy,sa,po,svc,clusterrole -l app=kubehelper -n YOUR_NAMESPACE_NAME
kubectl delete clusterrolebinding kubehelper-crb
```

### Terraform
[Configure and customize Terraform installation.](https://github.com/KubeHelper/kubehelper/wiki/Installation)
#### Installing KubeHelper with Terraform.
//TODO

* Add module to your main.tf. If necessary change the name and path to the module.
```shell
module "infra_kube_helper" {
  source = "./namespaces/infra/kube-helper"
}

#refresh terraform state
terraform init
```

* Install module with terraform.
```shell
terraform apply -auto-approve -compact-warnings -target=module.infra_kube_helper
```

#### Remove KubeHelper module with Terraform.
```shell
terraform destroy -auto-approve -compact-warnings -target=module.infra_kube_helper
``` 

### kubectl
[Configure and customize kubectl installation.](https://github.com/KubeHelper/kubehelper/wiki/Installation)
#### Installing KubeHelper with kubectl.  
Replace YOUR_NAMESPACE_NAME with your namespace name.
```shell
KUBE_HELPER_NAMESPACE="YOUR_NAMESPACE_NAME"
kubectl apply -f https://raw.githubusercontent.com/KubeHelper/kubehelper/main/installers/kubectl/kubehelper-deployment.yaml —n $KUBE_HELPER_NAMESPACE
kubectl apply -f https://raw.githubusercontent.com/KubeHelper/kubehelper/develop/installers/kubectl/kubehelper-clusterrole.yaml —n $KUBE_HELPER_NAMESPACE
kubectl create clusterrolebinding kubehelper-crb --clusterrole=kubehelper-cr --serviceaccount=$KUBE_HELPER_NAMESPACE:kubehelper-sa
```
#### Remove KubeHelper with kubectl.
```shell
kubectl delete deploy,sa,po,svc,clusterrole -l app=kubehelper -n YOUR_NAMESPACE_NAME
kubectl delete clusterrolebinding kubehelper-crb
```

## Comes soon
KubeHelper as [Terraform Module](https://registry.terraform.io/browse/modules)  
KubeHelper as [Helm Chart](https://artifacthub.io)
                                                                                                                       
## Usage
For detailed instructions on how to configure, customize, use, and more read the [KubeHelper Wiki](https://github.com/KubeHelper/kubehelper/wiki/Home).
    
## Support  
💥 &nbsp; [Enhancement Request.](https://github.com/JWebDev/kube-helper/issues/new?labels=kind:Enhancement&amp;template=ENHANCEMENT_REQUEST.md)  
🚀 &nbsp; [New Feature Request.](https://github.com/JWebDev/kube-helper/issues/new?labels=kind:Feature&amp;template=FEATURE_REQUEST.md)  
🐞 &nbsp; [Bug Report.](https://github.com/JWebDev/kube-helper/issues/new?labels=kind:Bug&amp;template=BUG_REPORT.md)  
❓ &nbsp; [Support Request.](https://github.com/JWebDev/kube-helper/issues/new?labels=kind:Support&amp;template=SUPPORT_REQUEST.md)  
🤓 &nbsp; [Be a contributor.](https://github.com/JWebDev/kube-helper/issues/new?labels=kind:Enhancement&amp;template=ENHANCEMENT_REQUEST.md)  
⭐ &nbsp; [Help the project => RATE US](https://github.com/JWebDev/kubehelper/stargazers)  

---
## License
Licensed GPL-3.0, see [LICENSE](https://github.com/KubeHelper/kubehelper/blob/main/LICENSE).

