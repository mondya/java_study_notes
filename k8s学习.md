# Kubernetes

## 云平台

### 阿里云

#### VPC（专有网络）

192.168.0.0/16 代表转换成二进制之后的前16位不改变，后16位改变。

![](https://gitee.com/cnuto/images/raw/master/image/image-20251025224109728.png)

## k8s组件

https://developer.aliyun.com/article/1334886

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

