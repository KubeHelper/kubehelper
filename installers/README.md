# Installation

## Helm

KubeHelper can be installed using Helm. It is highly recommended to change the default username and password.
You can find a detailed description of all customize properties in file [values.yaml](https://github.com/KubeHelper/kubehelper/blob/main/installers/helm/kubehelper/values.yaml).

To install Helm Chart you have many different options. Here are some of them.
```shell
#Download kubehelper.tar.gz with curl. Replace Version with last Version nummer
curl -O https://github.com/KubeHelper/kubehelper/blob/main/installers/helm/kubehelper-VERSION.tar.gz

#Download kubehelper.tar.gz with wget. Replace Version with last Version nummer
wget https://github.com/KubeHelper/kubehelper/blob/main/installers/helm/kubehelper-VERSION.tar.gz

#or simply clone repo, and go to sources folder.
cd /tmp && git clone https://github.com/KubeHelper/kubehelper.git
```

```shell
#Install KubeHelper with your myvals.yaml file from tar.gz
helm install -f myvals.yaml -n YOURNAMESPACE kubehelper ./kubehelper-VERSION.tar.gz

#Install KubeHelper with default file and change some values(username and password)
helm install --set kubehelper.username=myusername --set kubehelper.password=mypassword -n YOURNAMESPACE kubehelper ./kubehelper-VERSION.tar.gz

#Install KubeHelper with default values from tar.gz
helm install -n YOURNAMESPACE kubehelper ./kubehelper-VERSION.tar.gz

#Install KubeHelper from cloned github repo with default values.
helm install -n infra kubehelper /tmp/kubehelper/installers/helm/kubehelper/
```

#### Remove KubeHelper with Helm.

```shell
helm uninstall kubehelper -n YOURNAMESPACE
``` 

#### Troubleshooting:
Wget and Curl can download tar.gz file as html document. Then during installation you will receive the following error.
```shell
gzip: stdin: not in gzip format
tar: Child returned status 1
tar: Error is not recoverable: exiting now
```
or something like this
```shell
... does not appear to be a gzipped archive; got 'text/html; charset=utf-8'
```
```shell
#You can check the file type as follows.
file kubehelper-VERSION.tar.gz
```
There are 2 solutions, download the archive correctly or use the git clone and install the helm chart from the sources.


---

## Terraform

The Terraform module is ready for installation. You need to connect it to your modules and specify the namespace in which the KubeHelper should be installed. Don't forget to change default username
and password in [variables.tf](https://github.com/KubeHelper/kubehelper/blob/main/installers/terraform/variables.tf).

* Add module to your main.tf. Change the name and path to the module.

```shell
module "infra_kube_helper" {
  source = "./namespaces/infra/kubehelper"
}
```

* Refresh terraform state.

```shell
terraform init
```

* Install module with terraform.

```shell
terraform apply -auto-approve -compact-warnings -target=module.infra_kube_helper
```

* Remove KubeHelper module with Terraform.

```shell
terraform destroy -auto-approve -compact-warnings -target=module.infra_kube_helper
``` 

---

## kubectl

[Configure and customize kubectl installation.](https://github.com/KubeHelper/kubehelper/wiki/Installation)

#### Installing KubeHelper with kubectl.

Replace YOUR_NAMESPACE_NAME with your namespace name. ❗&nbsp; Run order is important. This installation installs

```shell
KUBE_HELPER_NAMESPACE="YOUR_NAMESPACE_NAME"
kubectl create clusterrolebinding kubehelper-crb --clusterrole=kubehelper-cr --serviceaccount=$KUBE_HELPER_NAMESPACE:kubehelper-sa
kubectl apply -f https://raw.githubusercontent.com/KubeHelper/kubehelper/main/installers/kubectl/kubehelper-clusterrole.yaml -n=$KUBE_HELPER_NAMESPACE
kubectl apply -f https://raw.githubusercontent.com/KubeHelper/kubehelper/main/installers/kubectl/kubehelper-deployment.yaml -n=$KUBE_HELPER_NAMESPACE
```

The above method will install KubeHelper with the default user and password. To change the user and password, download the file to your computer, change the password and install from a local file. You
can also change role rules, other parameters and names.

```shell
KUBE_HELPER_NAMESPACE="YOUR_NAMESPACE_NAME"
#download
curl -o kubehelper-deployment.yaml https://raw.githubusercontent.com/KubeHelper/kubehelper/main/installers/kubectl/kubehelper-deployment.yaml
#change username and password
vi kubehelper-deployment.yaml
#create ClusterRoleBinding
kubectl create clusterrolebinding kubehelper-crb --clusterrole=kubehelper-cr --serviceaccount=$KUBE_HELPER_NAMESPACE:kubehelper-sa
#download
curl -o kubehelper-clusterrole.yaml https://raw.githubusercontent.com/KubeHelper/kubehelper/main/installers/kubectl/kubehelper-clusterrole.yaml
#change Role Rules if needed
vi kubehelper-clusterrole.yaml

#install KubeHelper and ClusterRole
kubectl apply -f kubehelper-clusterrole.yaml  -n=$KUBE_HELPER_NAMESPACE
kubectl apply -f kubehelper-deployment.yaml  -n=$KUBE_HELPER_NAMESPACE
```

#### Remove KubeHelper with kubectl.

```shell
kubectl delete deploy,sa,po,svc,clusterrole -l app=kubehelper -n $KUBE_HELPER_NAMESPACE
kubectl delete clusterrolebinding kubehelper-crb
```

---  

## ClusterRole Rights/Permissions

For your safety, KubeHelper is installed with read rights. Using the KubeHelper with default settings, you cannot modify, create, or delete resources.  
Therefore, you can only execute commands intended for reading and viewing resources.  
But KubeHelper is very flexible in this regard, you can change the ClusterRole rights up to the cluster administrator.  
In this case, you can execute any commands and perform any actions with the cluster. Be careful with these rights❗

In this case, you have 2 options.

#### Option1

Add the rules that you need to the list. Here is a list of possible verbs: `[create | delete | deletecollection | get | list | patch | update | watch]`  
or see what operations your cluster supports by executing this command `kubectl api-resources -o wide`.

```yaml
#Example:
rules:
  - apiGroups: [ "*" ]
    resources: [ "*" ]
    verbs: [ "get", "list", "create" ]
  - nonResourceURLs: [ "*" ]
    verbs: [ "get", "list", "delete" ]
```

#### Option2

CLUSTER ADMIN! BE CAREFUL WITH THESE RIGHTS❗

```yaml
#Example: CLUSTER-ADMIN
rules:
  - apiGroups: [ "*" ]
    resources: [ "*" ]
    verbs: [ "*" ]
  - nonResourceURLs: [ "*" ]
    verbs: [ "*" ]
```