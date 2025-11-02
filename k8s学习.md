# Kubernetes

## 云平台

### 阿里云

#### VPC（专有网络）

192.168.0.0/16 代表转换成二进制之后的前16位不改变，后16位改变。

![](https://gitee.com/cnuto/images/raw/master/image/image-20251025224109728.png)

## k8s组件

https://developer.aliyun.com/article/1334886

![image-20251102151845133](https://gitee.com/cnuto/images/raw/master/image/image-20251102151845133.png)

## k8s常用命令

```bash
# 查看集群所有节点
kubectl get nodes

# 根据配置文件，给集群创建资源
kubectl apply -f xxx.yaml

# 查看集群部署了哪些应用
docker ps
kubectl get pods -A

# 使用标签查询pod
kubectl get pod -l app=app名称

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

##### calico

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

#### 进入dashboard

https://47.98.245.107:31489/#/login

![image-20251102153156583](https://gitee.com/cnuto/images/raw/master/image/image-20251102153156583.png)

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

### 滚动更新

```bash
kubectl set image deployment/my-dep nginx=nginx:1.16.1 --record

# 查看名为 my-dep 的 Deployment 的滚动更新状态
kuebectl rollout status deployment/my-dep
```

```bash
# 或者使用kubectl edit deployment/my-dept   更改Yaml文件中的镜像
```



![image-20251102150518616](https://gitee.com/cnuto/images/raw/master/image/image-20251102150518616.png)

### 版本回退

```bash
# 查看历史记录
kubectl rollout history deployment/my-dep

# 查看某个历史的详情
kubectl rollout history deployment/my-dep --version=2

# 回滚（回到上次）
kubectl rollout undo deployment/my-dep

# 回滚（回滚到指定版本）
kubectl rollout undo deployment/my-dep --to-version=2
```

## Service

将一组Pods公开为网络服务的抽象方法

```bash
# 默认为ClusterIP，服务名.空间名称.svc:8000只能在集群内部访问
kubectl expose deploy my-dep --port=8000 --target-port=80 --type=ClusterIP

# 集群外访问
kubectl expose deploy my-dep --port=8000 --target-port=80 --type=NodePort
```

==使用NodePort创建的service，端口的范围30000-32767之间==

对应yaml文件：

```yaml
apiVersion: v1
kind: Service
metadata:
  labels:
    app: my-dep
  name: my-dep
spec:
  selector:
    app: my-dep
    ports:
    - port: 8000
      protocol: TCP
      targetPort: 80
```



![image-20251102155151318](https://gitee.com/cnuto/images/raw/master/image/image-20251102155151318.png)

![image-20251102153745621](https://gitee.com/cnuto/images/raw/master/image/image-20251102153745621.png)

### 访问方式

#### ip+端口

#### 服务名

`服务名.所在服务空间.svc`：**这种方式只能在Pod内部才能够访问，外部master和worker节点主机上是不能访问的。**

例如：`my-dep.default.svc:8000`

## Ingress

Service的统一网关入口

![image-20251102172302184](https://gitee.com/cnuto/images/raw/master/image/image-20251102172302184.png)

### 安装

```bash
# Gitee 镜像（v0.47.0 版本）
wget https://gitee.com/mirrors/ingress-nginx/raw/controller-v0.47.0/deploy/static/provider/baremetal/deploy.yaml

# 下载名称为deploy.yaml，为了防止冲突，改名为ingress.yaml
cp deploy.yaml ingress.yaml

# 把yaml文件中的镜像地址更换
image: registry.cn-hangzhou.aliyuncs.com/lfy_k8s_images/ingress-nginx-controller:v0.46.0

# 在service中产生ClusterIP和NodePort两个Service
kubectl apply -f ingress.yaml
```

![image-20251102175037217](https://gitee.com/cnuto/images/raw/master/image/image-20251102175037217.png)

输入http://47.98.245.107:31480 返回404

![image-20251102193338104](https://gitee.com/cnuto/images/raw/master/image/image-20251102193338104.png)

### Demo测试

```yaml
apiVersion: networking.k8s.io/v1
kind: Ingress
metadata:
  name: ingress-host-bar
spec:
  ingressClassName: nginx
  rules:
  - host: "hello.atguigu.com"
    http:
      paths:
      - pathType: Prefix  
        path: "/"
        backend:
          service:
            name: hello-server
            port:
              number: 8000 
  - host: "demo.atguigu.com"  
    http:
      paths:
      - pathType: Prefix 
        path: "/nginx"
        backend:
          service:
            name: nginx-demo
            port:
              number: 8000 
```

![image-20251102182650596](https://gitee.com/cnuto/images/raw/master/image/image-20251102182650596.png)

> 为什么 path: "/nginx" 和path: "/"会有不同404结果

paht: "/nginx" 进去Pod，404由Pod中的nginx透出

path: "/" 404由Ingress报出

### 路径重写

在部署yaml文件metadata下添加：

```yaml
metadata:
  onannotations:
    nginx.ingress.kubernetes.io/rewrite-target: /$2
```

### 流量限制

// TODO

### 网络模型总结

![image-20251102190823492](https://gitee.com/cnuto/images/raw/master/image/image-20251102190823492.png)

## 存储抽象

### 环境准备

#### 所有节点安装nfs

```bash
# 所有机器安装
yum install -y nfs-utils
```

#### 主节点

```bash
# nfs主节点
echo "/nfs/data/ *(insecure,rw,sync,no_root_squash)"> /etc/exports
mkdir -p /nfs/data
systemctl enable rpcbind --now
systemctl enable nfs-server --now
#配置生效
exportfs -r
```

#### 从节点

```bash
showmount -e [自己ip]
#执行以下命令挂载nfs服务器上的共享目录到本机路径/root/nfsmount
mkdir -p /nfs/mount
mount -t nfs [自己ip]:/nfs/data /nfs/mount
#写入一个测试文件
echo "hello nfs server" > /nfs/mount/test.txt
```



```bash
showmount -e 172.26.243.229
#执行以下命令挂载nfs服务器上的共享目录到本机路径/root/nfsmount
mkdir -p /nfs/mount
mount -t nfs 172.26.243.229:/nfs/data /nfs/mount
#写入一个测试文件
echo "hello nfs server" > /nfs/mount/test.txt
```

![image-20251102201201375](https://gitee.com/cnuto/images/raw/master/image/image-20251102201201375.png)

### 挂载

#### 原生挂载

缺点：

- 指定文件夹不会自动创建，需要手动创建

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  labels:
    app: nginx-pv-demo
  name: nginx-pv-demo
spec:
  replicas: 2
  selector:
    matchLabels:
      app: nginx-pv-demo
  template:
    metadata:  
      labels:
        app: nginx-pv-demo
    spec:
      containers:  
      - image: nginx
        name: nginx
        volumeMounts:
        - name: html
          mountPath: /usr/share/nginx/html  
      volumes:  
      - name: html  
        nfs:  
          server: 172.26.243.229
          path: /nfs/data/nginx-pv
```

```bash
# 在/nfs/data/nginx-pv 目录下执行
echo "111222" > index.html
```

进入容器内部：

![image-20251102205446432](https://gitee.com/cnuto/images/raw/master/image/image-20251102205446432.png)

#### PV&PVC

`PV`：PersistenVolume 持久卷

`PVC`：PersistenVolumeClaim 持久卷声明

##### 创建PV池

> 静态供应

```bash
#nfs主节点
mkdir -p /nfs/data/01
mkdir -p /nfs/data/02
mkdir -p /nfs/data/03
```

```yaml
apiVersion: v1
kind: PersistentVolume
metadata:
  name: pv01-10m
spec:
  capacity:
    storage: 10M
  accessModes:
    - ReadWriteMany
  storageClassName: nfs
  nfs:
    path: /nfs/data/01
    server: 172.26.243.229
---
apiVersion: v1
kind: PersistentVolume
metadata:
  name: pv02-1gi
spec:
  capacity:
    storage: 1Gi
  accessModes:
    - ReadWriteMany
  storageClassName: nfs
  nfs:
    path: /nfs/data/02
    server: 172.26.243.229
---
apiVersion: v1 
kind: PersistentVolume
metadata:
  name: pv03-3gi
spec:
  capacity:
    storage: 3Gi
  accessModes:
    - ReadWriteMany
  storageClassName: nfs
  nfs:
    path: /nfs/data/03
    server: 172.26.243.229
```

![image-20251102223247033](https://gitee.com/cnuto/images/raw/master/image/image-20251102223247033.png)

```bash
kubectl get pv
```



![image-20251102223311736](https://gitee.com/cnuto/images/raw/master/image/image-20251102223311736.png)

##### PVC创建与绑定

申请一个200M的PVC

```yaml
kind: PersistentVolumeClaim
apiVersion: v1
metadata:
  name: nginx-pvc  
spec:
  accessModes:     
    - ReadWriteMany  
  resources:       
    requests:      
      storage: 10M  
  storageClassName: nfs
```

```bash
kubectl get pvc
```

![image-20251102223824804](https://gitee.com/cnuto/images/raw/master/image/image-20251102223824804.png)

##### 创建POD绑定PVC

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  labels:
    app: nginx-deploy-pvc
  name: nginx-deploy-pvc
spec:
  replicas: 2
  selector:
    matchLabels:
      app: nginx-deploy-pvc
  template:
    metadata:  
      labels:
        app: nginx-deploy-pvc
    spec:
      containers:  
      - image: nginx
        name: nginx
        volumeMounts:
        - name: html
          mountPath: /usr/share/nginx/html  
      volumes:  
      - name: html  
        persistentVolumeClaim:
          claimName: nginx-pvc
```

![image-20251102224503814](https://gitee.com/cnuto/images/raw/master/image/image-20251102224503814.png)

> 进入/nfs/data/01， echo "hello world" > index.html，进入容器内部

![image-20251102224806676](https://gitee.com/cnuto/images/raw/master/image/image-20251102224806676.png)

## ConfigMap

抽取应用配置，并且可以自动更新

### redis示例

```bash
# 创建配置，redis配置保存到k8s的etcd
kubectl create cm redis-conf --from-file=redis.conf
```

```yaml
apiVersion:v1
  data:
    redis.conf:
      appendonly yes
kind: ConfigMap
metadata:
  name: redis-conf
  namespace: default
```

![image-20251102225437318](https://gitee.com/cnuto/images/raw/master/image/image-20251102225437318.png)

```bash
# 以yaml格式查看redis-conf
kubectl get cm redis-conf -oyaml
```

以yaml格式查看redis-conf

```yaml
apiVersion: v1
data:   # data下存放数据
  redis.conf: |
    appendonly yes
kind: ConfigMap
metadata:
  creationTimestamp: "2025-11-02T14:54:18Z"
  managedFields:
  - apiVersion: v1
    fieldsType: FieldsV1
    fieldsV1:
      f:data:
        .: {}
        f:redis.conf: {}
    manager: kubectl-create
    operation: Update
    time: "2025-11-02T14:54:18Z"
  name: redis-conf
  namespace: default
  resourceVersion: "169103"
  uid: b5f18445-97b4-49ce-aace-59918c816638
```

##### 创建POD

```yaml
apiVersion: v1
kind: Pod
metadata:
  name: redis
spec:
  containers:
  - name: redis
    image: redis
    command:
    - redis-server
    - "/redis-master/redis.conf"  # 指的是redis容器内部的位置
    ports:
    - containerPort: 6379
    volumeMounts:
    - mountPath: /data
      name: data
    - mountPath: /redis-master
      name: config
  volumes:
  - name: data
    emptyDir: {}
  - name: config
    configMap:
      name: redis-conf
      items:
      - key: redis.conf  # 从configMap中获取key为redis.conf的key
        path: redis.conf # 从configMap中获取key为redis.conf的路径 
```

##### 注意点

redis自身不会热更新，配置更改后需要重启POD才会生效。

## Secret

Secret对象类型用来保存敏感信息，例如密码、OAUTH令牌和SSH密钥。将这些信息保存在secret中比放在POD的定义或者容器镜像内更加安全和灵活。

```bash
kubectl create secret docker-registry leifengyang-docker\
  --docker-username=leifengyang\
  --docker-password=Lfy123456 \
  --docker-email=534096094@qq.com
#命令格式
kubectl create secret docker-registry regcred \
  --docker-server=<你的镜像仓库服务器>\
  --docker-username=<你的用户名>\
  --docker-password=<你的密码>\
  --docker-email=<你的邮箱地址>
```

```bash
kubectl create secret docker-registry xhh \
  --docker-server=crpi-23kczhfnz9e8k3ge.cn-hangzhou.personal.cr.aliyuncs.com \
  --docker-username=xhh19990210  \
  --docker-password=密码 \
  --docker-email=xhh19990210@gmail.com
```

```yaml
# 部署POD时指定secret
apiVersion:v1
kind:Pod
metadata:
  name: private-nginx
spec:
  containers:
  - name: private-nginx
    image: xhh/guignginx:v1.0
  imagePullSecrets:
  - name: xhh
```

