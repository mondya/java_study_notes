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

#### docker images

`docker images -a`：列出本地所有镜像，包括历史镜像

`docker images -q`：只显示镜像ID

`docker images`：查看主机上安装的docker镜像

![image-20230223220950358](D:.\images\image-20230223220950358.png)

REPOSITORY：表示仓库的镜像源

TAG：镜像的标签（同一个仓库可以有多个TAG版本）

IMAGE ID：镜像ID

CREATED：镜像创建时间

SIZE：镜像大小

#### 下载/查询

`docker search xxx` ：查询某个镜像

![image-20230224201903420](D:.\images\image-20230224201903420.png)

name：镜像名称

description：镜像说明

starts：点赞数量

official：是否是官方

automated：是否是自动构建

`docker search --limit 5 xxx`：查看前5个镜像

`docker pull xxx`：下载镜像， 没有TAG就是最新版，等价于docker pull :latest

`docker system sf `：查看镜像/容器/数据卷所占的空间

#### 移除镜像

`docker rmi [-f] 某个镜像的ID`:移除某个镜像  -f 强制删除

`docker rmi [-f] 镜像名1：TAG 镜像名2：TAG`：删除多个镜像，按照TAG删除

`docker rmi [-f] ${docker images -qa}`：删除本机上所有镜像

#### 移除容器

`docker rm [-f] 容器Id`：移除已停止的容器（-f 强制）

#### 启动

`docker run [OPTIONS] xxxx`：运行容器

--name = "新容器名称" ：为容器指定一个名称；

-d：后台运行容器并返回容器ID，也即启动守护式容器（后台运行）；

==-i：以交互模式运行容器，通常与-t一起使用==

==-t：为容器重新分配一个伪输入终端，通常与-i同时使用==，也即启动交互式容器（前台有伪终端，等待交互）

-P：随机端口映射，P为大写

-P：指定端口映射，小写p

> 在censtos中启动ubuntu
>
> docker run -it ubuntu /bin/bash
>
> -i：交互式操作
>
> -t：终端
>
> ubuntu：Ubuntu镜像
>
> /bin/bash：放在镜像名的命令，交互式Shell，exit退出

#### 运行相关

 `docker ps`：获取正在运行的容器

-a：列出当前所有正在运行的容器+历史上运行过的

-l：显示最近创建的容器

-n 5：显示最近5个创建的容器

-q：静默模式，只显示容器编号

#### 退出容器

`exit`：run进去容器，exit退出，容器终止

`ctrl + p + q`：run进去容器，ctrl + p + q退出，容器不停止

#### 启动已经停止运行的容器

`docker start 容器ID或容器名`

#### 重启/停止

`docker restart 容器ID或者容器名`

`docker stop 容器ID或者容器名`

`docker kill 容器ID或者容器名`：强制停止容器

#### 重要

交互式容器：ubuntu，如果使用docker run -d ubuntu启动容器，然后docker ps -a会发现==容器已经退出==

Docker容器后台运行，必须有一个前台进程。容器运行的命令不是一直挂起的命令（比如运行top,tail），就会自动退出

守护式容器：redis