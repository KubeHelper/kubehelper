### KubeHelper Helm installer values

|Parameter|Description|Default|
|------|-------|------------|
| `kubehelper.username` | KubeHelper username. | kube |
| `kubehelper.password` | KubeHelper password. | helper |
| `serviceAccount.name` | The name of the ServiceAccount to be created. | kubehelper-sa |
| `clusterRoleBinding.name` | The name of the ClusterRoleBinding to be created. | kubehelper-crb |
| `clusterRole.name` | The name of the ClusterRole to be created. | kubehelper-cr |
| `service.name` | Name of the Service to be created. | kubehelper-svc |
| `service.type` | Type of the Service to be created. | ClusterIP |
| `service.port` | Port of the Service to be created. | 80 |
| `deployment.name` | Name of the Deployment to be created. | kubehelper-deployment |
| `replicaCount` | replicaCount of the Deployment to be created. | 1 |
| `image` | KubeHelper image. | kubehelper/kubehelper |
| `imageTag` | KubeHelper image tag. | 1.0.0 |
| `imagePullPolicy` | KubeHelper container imagePullPolicy. | Always |
| `podSecurityContext` | KubeHelper container podSecurityContext. | { } |
| `securityContext.runAsNonRoot` | KubeHelper container securityContext. | true |
| `securityContext.runAsUser` | KubeHelper container securityContext. | 1000 |
| `resources` | KubeHelper container resources. | { } |
| `nodeSelector` | KubeHelper nodeSelector. | { } |
| `tolerations` | KubeHelper tolerations. | { } |
| `affinity` | KubeHelper affinity. | { } |
| `rbac` | KubeHelper Role Rules. | RBAC Object look in [values.yaml](https://github.com/KubeHelper/kubehelper/blob/main/installers/helm/kubehelper/values.yaml)  |