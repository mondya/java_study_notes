## 简介

`Docker镜像`（Image）就是一个只读的模板。`镜像`可以用来创建Docker容器，一个镜像可以创建多个`容器`；镜像类似于Java中的类，容器类似于new出来的实例；仓库是存放镜像的地方，需要哪个镜像，直接拉取。

## 安装

### 卸载旧docker

```bash
sudo yum remove docker \
                  docker-client \
                  docker-client-latest \
                  docker-common \
                  docker-latest \
                  docker-latest-logrotate \
                  docker-logrotate \
                  docker-engine
```

### yum安装gcc

```bash
yum -y install gcc
yum -y install gcc-c++
```

### 安装需要的软件包

```bash
yum install -y yum-utils
```

### 设置远程镜像

> 国内镜像

```bash
yum-config-manager --add-repo http://mirrors.aliyun.com/docker-ce/linux/centos/docker-ce.repo
```

> 官网教程镜像(成功)

```bash
yum-config-manager --add-repo https://download.docker.com/linux/centos/docker-ce.repo
```

### 安装最新版引擎

```bas
yum install docker-ce docker-ce-cli containerd.io docker-buildx-plugin docker-compose-plugin
```

### 启动

```bash
systemctl start docker
```

![image-20230221224431445](.\images\image-20230221224431445.png)

### helloWord

注意：如果开启了软路由，即使配置了镜像还是会拉取失败

```bash
docker run hello-world
```

## 命令

### 帮助启动类命令

`systemctl start docker`：启动docker

`systemctl stop docker`：停止docker

`systemctl restart docker`：重启docker

`systemctl status docker`：查看docker状态

`systemctl enable docker`：开机启动

`docker info`：查看docker概要信息

`docker --help`：查看帮助

`docker 具体命令 --help`：查看docker命令帮助文档

### 镜像命令

`docker images`：查看主机上安装的docker镜像

`docker images -a`：列出本地所有镜像，包括历史镜像

`docker images -q`：只显示镜像ID

![image-20230223220950358](D:.\images\image-20230223220950358.png)

REPOSITORY：表示仓库的镜像源

TAG：镜像的标签（同一个仓库可以有多个TAG版本）

IMAGE ID：镜像ID

CREATED：镜像创建事件

SIZE：镜像大小

`docker search xxx` ：查询某个镜像

`docker pull`