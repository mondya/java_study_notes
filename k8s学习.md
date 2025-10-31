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
