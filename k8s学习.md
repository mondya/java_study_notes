# Kubernetes

## 云平台

### 阿里云

#### VPC（专有网络）

192.168.0.0/16 代表转换成二进制之后的前16位不改变，后16位改变。

![](https://gitee.com/cnuto/images/raw/master/image/image-20251025224109728.png)

## k8s组件

https://developer.aliyun.com/article/1334886

## k8s常用命令

```bash
# 查看集群所有节点
kubectl get nodes

# 根据配置文件，给集群创建资源
kubectl apply -f xxx.yaml

# 查看集群部署了哪些应用
docker ps
kubectl get pods -A


# master节点创建加入woker节点的token
kubeadm token create --print-join-command
```



## kubeadm创建集群

官方地址：https://kubernetes.io/zh-cn/docs/setup/production-environment/tools/kubeadm/install-kubeadm/

3台2核4g服务器 

### 前置条件

#### 安装Docker

#### 设置主机名

```bash
# 查看主机名称
hostname
# 设置主机名称
hostnamectl set-hostname k8s-master
```

#### 禁用交换分区

```bash
# 关闭swap
swapoff -a
sed -ri 's/.*swap.*/#&/' /etc/fstab
```

#### 允许iptables桥接网络

```bash
cat <<EOF | sudo tee /etc/modules-load.d/k8s.conf
br_netfilter
EOF

cat <<EOF | sudo tee /etc/sysctl.d/k8s.conf
net.bridge.bridge-nf-call-ip6tables = 1
net.bridge.bridge-nf-call-iptables = 1
EOF
```

#### 配置生效

```bash
sudo sysctl --system
```

### 安装kubelet、kubeadm、kubectl

- `kubeadm`：用来初始化集群的指令。
- `kubelet`：在集群中的每个节点上用来启动 Pod 和容器等。
- `kubectl`：用来与集群通信的命令行工具。

以下基于Red Hat发行版

#### 将 SELinux 设置为 permissive 模式
```bash
# 将 SELinux 设置为 permissive 模式（相当于将其禁用）
sudo setenforce 0
sudo sed -i 's/^SELINUX=enforcing$/SELINUX=permissive/' /etc/selinux/config
```

#### 添加 Kubernetes 的 `yum` 仓库

>  官方教程

```bash
# 此操作会覆盖 /etc/yum.repos.d/kubernetes.repo 中现存的所有配置
cat <<EOF | sudo tee /etc/yum.repos.d/kubernetes.repo
[kubernetes]
name=Kubernetes
baseurl=https://pkgs.k8s.io/core:/stable:/v1.34/rpm/
enabled=1
gpgcheck=1
gpgkey=https://pkgs.k8s.io/core:/stable:/v1.34/rpm/repodata/repomd.xml.key
exclude=kubelet kubeadm kubectl cri-tools kubernetes-cni
EOF
```

> 视频教程版本

```bash
# 此操作会覆盖 /etc/yum.repos.d/kubernetes.repo 中现存的所有配置
cat <<EOF | sudo tee /etc/yum.repos.d/kubernetes.repo
[kubernetes]
name=Kubernetes
baseurl=https://mirrors.aliyun.com/kubernetes/yum/repos/kubernetes-el7-x86_64
enabled=1
gpgcheck=0
repo_gpgcheck=0
gpgkey=https://mirrors.aliyun.com/kubernetes/yum/doc/yum-key.gpg https://mirrors.aliyun.com/kubernetes/yum/doc/rpm-package-key.gpg
exclude=kubelet kubeadm kubectl
EOF
```

#### 安装 kubelet、kubeadm 和 kubectl，并启用 kubelet 以确保它在启动时自动启动

> 官方教程

```bash
sudo yum install -y kubelet kubeadm kubectl --disableexcludes=kubernetes
sudo systemctl enable --now kubelet
```

> 视频教程

```bash
sudo yum install -y kubelet-1.20.9 kubeadm-1.20.9 kubectl-1.20.9 --disableexcludes=kubernetes
sudo systemctl enable --now kubelet
```

#### 下载机器需要的镜像

```bash
sudo tee ./images.sh <<-'EOF'
#!/bin/bash
images=(
kube-apiserver:v1.20.9
kube-proxy:v1.20.9
kube-controller-manager:v1.20.9
kube-scheduler:v1.20.9
coredns:1.7.0
etcd:3.4.13-0
pause:3.2
)
for imageName in ${images[@]} ; do
docker pull registry.cn-hangzhou.aliyuncs.com/lfy_k8s_images/$imageName
done
EOF

```

```bash
# 启动sh脚本拉取镜像
chmod +x ./images.sh && ./images.sh
```

#### 初始化主节点



```bash
# 注意：这里的ip需要更换成自己的ip
echo "172.26.243.229 cluster-endpoint" >> /etc/hosts 

# 主节点运行
# 注意avertise-address=主机地址
# service的ip和pod的ip和ecs主机的ip段不能冲突
kubeadm init \
--apiserver-advertise-address=172.26.243.229 \
--control-plane-endpoint=cluster-endpoint \
--image-repository registry.cn-hangzhou.aliyuncs.com/lfy_k8s_images \
--kubernetes-version v1.20.9 \
--service-cidr=10.96.0.0/16 \
--pod-network-cidr=192.168.0.0/16
```

![image-20251101001401893](https://gitee.com/cnuto/images/raw/master/image/image-20251101001401893.png)

> 初始化成功后

##### 使用集群前，需要执行命令

```bash
  mkdir -p $HOME/.kube
  sudo cp -i /etc/kubernetes/admin.conf $HOME/.kube/config
  sudo chown $(id -u):$(id -g) $HOME/.kube/config
```

##### 添加主节点命令

24小时有效，失效后重新获取

```bash
# master节点输入此命令创建token
kubeadm token create --print-join-command
```

```bash
  kubeadm join cluster-endpoint:6443 --token h0m858.8bxii6weh9oejlrx \
    --discovery-token-ca-cert-hash sha256:9f6e6d37f2c6352f7ad3877a8d066fef84d9c986bb2b8816dab84cf5a744f4e4 \
    --control-plane 
```



##### 添加worker节点命令

24小时有效，失效后重新获取

```bash
kubeadm join cluster-endpoint:6443 --token h0m858.8bxii6weh9oejlrx \
    --discovery-token-ca-cert-hash sha256:9f6e6d37f2c6352f7ad3877a8d066fef84d9c986bb2b8816dab84cf5a744f4e4 
```



```text
Your Kubernetes control-plane has initialized successfully!

To start using your cluster, you need to run the following as a regular user:

# 在使用集群前，需要执行以下命令
  mkdir -p $HOME/.kube
  sudo cp -i /etc/kubernetes/admin.conf $HOME/.kube/config
  sudo chown $(id -u):$(id -g) $HOME/.kube/config

Alternatively, if you are the root user, you can run:

  export KUBECONFIG=/etc/kubernetes/admin.conf

You should now deploy a pod network to the cluster.
Run "kubectl apply -f [podnetwork].yaml" with one of the options listed at:
  https://kubernetes.io/docs/concepts/cluster-administration/addons/

You can now join any number of control-plane nodes by copying certificate authorities
and service account keys on each node and then running the following as root:

# 添加主节点
  kubeadm join cluster-endpoint:6443 --token h0m858.8bxii6weh9oejlrx \
    --discovery-token-ca-cert-hash sha256:9f6e6d37f2c6352f7ad3877a8d066fef84d9c986bb2b8816dab84cf5a744f4e4 \
    --control-plane 

Then you can join any number of worker nodes by running the following on each as root:
# 添加worker节点
kubeadm join cluster-endpoint:6443 --token h0m858.8bxii6weh9oejlrx \
    --discovery-token-ca-cert-hash sha256:9f6e6d37f2c6352f7ad3877a8d066fef84d9c986bb2b8816dab84cf5a744f4e4 
```

#### 安装网络组件

calico

如果上面在初始化主节点时，pod 更换了ip，需要在calico.yaml文件中更改

![image-20251101145337140](https://gitee.com/cnuto/images/raw/master/image/image-20251101145337140.png)

```bash
curl https://docs.projectcalico.org/manifests/calico.yaml -O
# 上面地址已经失效
# Calico 版本需与 K8s 版本对应
# 官方归档（v3.17 版本，适配 K8s v1.20
curl -O https://docs.projectcalico.org/archive/v3.17/manifests/calico.yaml

# 若官方地址访问慢，可使用 Tigera（Calico 母公司）的归档镜像
curl -O https://calico-archive.tigera.io/v3.17/manifests/calico.yaml


kubectl apply -f calico.yaml
```

![image-20251101005140938](https://gitee.com/cnuto/images/raw/master/image/image-20251101005140938.png)

#### 查看节点状态

执行命令`kubectl get nodes`，只能在主节点执行

![image-20251101005827246](https://gitee.com/cnuto/images/raw/master/image/image-20251101005827246.png)

#### 添加Woker节点

使用上面的添加节点命令

![image-20251101012459614](https://gitee.com/cnuto/images/raw/master/image/image-20251101012459614.png)

![image-20251101015229959](https://gitee.com/cnuto/images/raw/master/image/image-20251101015229959.png)

### 部署dashboard

```bash
# 时间太慢，建议看镜像版本
kubectl apply -f https://raw.githubusercontent.com/kubernetes/dashboard/v2.3.1/aio/deploy/recommended.yaml
```

#### 设置访问端口

**type: ClusterIP更改为type: NodePort**

```bash
kubectl edit svc kubernetes-dashboard -n kubernetes-dashboard
```

```bash
kubectl get svc -A |grep kubernetes-dashboard
```

![image-20251101152002732](https://gitee.com/cnuto/images/raw/master/image/image-20251101152002732.png)

#### 创建访问账号

```yaml
# 创建访问账号，准备一个yaml文件；vim dash-usr.yaml
apiVersion: v1
kind: ServiceAccount
metadata:
  name: admin-user
  namespace: kubernetes-dashboard
---
apiVersion: rbac.authorization.k8s.io/v1
kind: ClusterRoleBinding
metadata:
  name: admin-user
roleRef:
  apiGroup: rbac.authorization.k8s.io
  kind: ClusterRole
  name: cluster-admin
subjects:
- kind: ServiceAccount
  name: admin-user
  namespace: kubernetes-dashboard
  
```

![image-20251101154348295](https://gitee.com/cnuto/images/raw/master/image/image-20251101154348295.png)

#### 获取访问令牌

```bash
# 获取访问令牌
kubectl -n kubernetes-dashboard get secret $(kubectl -n kubernetes-dashboard get sa/admin-user -o jsonpath="{.secrets[0].name}") -o go-template="{{.data.token | base64decode}}"

```

```bash
eyJhbGciOiJSUzI1NiIsImtpZCI6ImNQemc0WU9fcXJyajNTLXlCSnUyOHVKNG92NGN5VWVVQ3JRRy1jaURpUHMifQ.eyJpc3MiOiJrdWJlcm5ldGVzL3NlcnZpY2VhY2NvdW50Iiwia3ViZXJuZXRlcy5pby9zZXJ2aWNlYWNjb3VudC9uYW1lc3BhY2UiOiJrdWJlcm5ldGVzLWRhc2hib2FyZCIsImt1YmVybmV0ZXMuaW8vc2VydmljZWFjY291bnQvc2VjcmV0Lm5hbWUiOiJhZG1pbi11c2VyLXRva2VuLTZ3dGd6Iiwia3ViZXJuZXRlcy5pby9zZXJ2aWNlYWNjb3VudC9zZXJ2aWNlLWFjY291bnQubmFtZSI6ImFkbWluLXVzZXIiLCJrdWJlcm5ldGVzLmlvL3NlcnZpY2VhY2NvdW50L3NlcnZpY2UtYWNjb3VudC51aWQiOiJlODdjMTI2Ny1mYjg5LTRiMDQtOTE1Mi02NzM0MGUxYTliYmQiLCJzdWIiOiJzeXN0ZW06c2VydmljZWFjY291bnQ6a3ViZXJuZXRlcy1kYXNoYm9hcmQ6YWRtaW4tdXNlciJ9.iyhSde8SlpF7IOX27Bh7VwzJ_NEcjRPKzewYgs1V1mJt5rsc_zgCXhnuDIYX5GZsPZU_NjiHCVqIPaXtpoUnApN-qCt_8kHKY-Wp096TQP-AKv2SXlclGYAhtjHJAQwOkkbtAd_loFr86ARRXbHM41OSVuIGBT3PAu6ntpJRIciUIRVxLuW8baVrRcqZLJz386k-jw6mDxGQ4LPDCBY2j8HzmkRWf6PJ4PiVjbE3IbKD4mgVTbCArFhJATSUhliw6xqEIzSOuFi6xc3YwfxNbEaC3QHa8j5_q36SI6oA-bSjWlB3T0VVW7vBDiC6fqVfOqmGKuGmT-j7tvTREcQ2UQ
```

![image-20251101191748638](https://gitee.com/cnuto/images/raw/master/image/image-20251101191748638.png)

### 命名空间

```bash
# 添加命名空间
kubectl create ns xhh

# 删除命名空间
kubectl delete ns xhh

# 查看命名空间
kubectl get ns
```

#### yaml文件格式创建

执行`kubectl apply -f xxx.yaml`

```yaml
apiVersion: v1
kind: NameSpace
metadata:
  name: xhh
```

### 创建Pod

```
kubectl run mynginx --image=nginx

# 描述pod：用来排查问题
kubectl describe pod [Pod名称]
```

#### yaml文件格式创建

```yaml
apiVersion: v1
kind: Pod
metadata:
  labels:
    run: mynginx
  name: mynginx
spec:
  containers:
  - image: nginx
    name: mynginx
```

#### Pod包含多个服务

```yaml
apiVersion: v1
kind: Pod
metadata:
  labels:
    run: myapp
  name: myapp
spec:
  containers:
  - image: nginx
    name: nginx
  - image: tomcat:8.5.68
    name: tomcat

```



### 删除Pod

```bash
# 删除指定Pod，如果Pod不在默认命名空间，后面追加参数
kubectl delete pod [镜像名称] -n [命名空间]
```

## Deployment

> 控制Pod，使Pod拥有多个副本，自愈，扩缩容等能力

```bash
kubectl create deployment mytomcat --image=tomcat:8.5.68
```

![image-20251102005034884](https://gitee.com/cnuto/images/raw/master/image/image-20251102005034884.png)

使用deployment创建的Pod，用kubectl delete pod删除Pod，Pod会重新生成一个

```bash
kubectl delete deployment mytomcat
```

### 多副本

```bash
kubectl create deployment my-dep --image=nginx --replicas=3
```

![image-20251102005847659](https://gitee.com/cnuto/images/raw/master/image/image-20251102005847659.png)

等同于

![image-20251102010158890](https://gitee.com/cnuto/images/raw/master/image/image-20251102010158890.png)

### 扩缩容

```bash
kubectl scale deploy/my-dep --replicas=5
```

![image-20251102011112775](https://gitee.com/cnuto/images/raw/master/image/image-20251102011112775.png)

### 自愈

容器被删除或者ecs故障后会主动恢复到replicas节点数
