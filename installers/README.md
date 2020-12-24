# Kube Helper Installers

### Helm
```shell script
helm install stable/kube-helper 
```

### Kubernetes
```shell script
kubectl apply -f kube-helper-deployment.yaml
or
kubectl apply -f https://raw.githubusercontent.com/JWebDev/kube-helper/master/installers/kubernetes/kube-helper-deployment.yaml
```

### Terraform
```shell script
terraform apply -target=module_name.kube_helper
```   

