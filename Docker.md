简介

是一种轻量级、可执行的独立软件包，它包含运行某个软件所需的所有内容，我们把应用程序和配置依赖打包好形成一个可交付的运行环境（包括代码、运行时需要的库、环境变量和配置文件等），这个打包好的运行环境就是image镜像文件，通过这个文件才能生成容器实例。

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

#### docker images查看镜像

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

`docker pull [仓库地址]  xxx:镜像版本号`：下载镜像， 没有TAG就是最新版，等价于docker pull :latest

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

-d：后台运行容器并返回容器ID，也即==启动守护式容器（后台运行）==；

==-i：以交互模式运行容器，通常与-t一起使用==

==-t：为容器重新分配一个伪输入终端，通常与-i同时使用==，也即==启动交互式容器（前台有伪终端，等待交互）==

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

#### 重要（交互式容器和守护式容器）

`交互式容器（前台交互式启动）`：ubuntu，如果使用docker run -d ubuntu启动容器，然后docker ps -a会发现==容器已经退出==

Docker容器后台运行，必须有一个前台进程。容器运行的命令不是一直挂起的命令（比如运行top,tail），就会自动退出

例：docker run -it redis:6.0.8

`守护式容器（后台守护式启动）`：redis

例：docker run -d redis:6.0.8

#### 查看容器日志

`docker logs 容器ID`

#### 查看容器内运行的进程

`docker top 容器ID`

#### 查看容器内部细节

`docker inspect 容器ID`

#### 进入正在运行的容器并以命令行交互

`docker exec -it 容器ID [/bin/bash]`:

`docker attach 容器ID`

区别：attach直接进入容器启动命令的终端，不会启动新的线程，用exit退出，会导致容器的停止

exec是在容器中打开新的终端，并且可以启动新的进程，用exit退出，不会导致容器的停止

#### 从容器中复制文件到主机上

`docker cp 容器ID：容器内路径 目的主机路径`

#### 导入和导出容器

`docker export 容器ID > 文件名.tar`：导出容器的内容流作为一个tar归档文件

`cat 文件名.tar | docker import - 镜像用户/镜像名：镜像版本号`：从tar包中的内容创建一个新的文件系统并导入为镜像

## 镜像的分层概念

UnionFS(联合文件系统)：UnionFS文件系统是一种分层、轻量级并且高性能的文件系统，它支持对文件系统的修改作为一次提交来一层层叠加，同时可以将不同目录挂载到同一个虚拟文件系统下。Union文件系统是Docker镜像的基础。==镜像可以通过分层来进行继承==，基于基础镜像（没有父镜像），可以制作各种具体的应用镜像。

特性：一次同时加载多个文件系统，但是从外表看只能看到一个文件系统，联合加载会把各层文件系统叠加起来，这样最终的文件系统会包含所有底层的文件和目录。

==Docker镜像层都是只读的，容器层是可写的==。当容器启动时，一个新的可写层别加载到镜像的顶部，这一层通常被称为“容器层”，“容器层”之下的都叫做“镜像层”。所有对容器的改动，无论添加，删除，修改都只会发生在容器层中。

![image-20230304222537180](.\images\image-20230304222537180.png)

Docker中的镜像分层，==支持通过扩展现有镜像，创建新的镜像==。类似于Java继承于一个Base基础类，自己按需拓展，新镜像是从base镜像一层一层叠加生成的，每安装一个软件，就在现有镜像的基础上增加一层。

![image-20230304231011263](D:.\images\image-20230304231011263.png)

## commit命令

docker commit提交容器副本使之成为一个新的镜像

`docker commit -m="提交的描述信息" -a="作者" 容器id 要创建的目标镜像名:[标签名]`

> 在ubuntu中安装vim

- `docker run -it ubuntu`启动ubuntu
- `vim helloworld.java`提示vim :command not found
- `apt-get update`
- `apt-get -y install vim`，安装vim

![image-20230304224805660](.\images\image-20230304224805660.png)

> commit安装过vim命令的ubuntu

![image-20230304225657128](D:.\images\image-20230304225657128.png)

## 发布阿里云

todo

## Dokcer私有仓库

- `docker pull registry`：下载镜像私有库
- 启动registry:`docker run -d -p 5000:5000 -v /xhh/myregistry/:/tep/resigtry --privileged=true registry`，默认情况下，仓库被创建在容器的/var/lib/registry目录下，建议自行用容器卷映射，方便宿主机联调

- commit自制的镜像`docker commit -m="ubuntu add ipconfig" -a="xhh" 0fe074bfc978 ubuntu-ipconfig:1.0`

- `curl -XGET http://localhost:5000/v2/_catalog`：查看本地镜像仓库的镜像

![image-20230305144520439](D:.\images\image-20230305144520439.png)

- 按照公式 `docker tag 镜像:Tag Host:Port/Repository:Tag`，host，port为自己主机`docker tag 7cafd0fc013b localhost:5000/xhhubuntu:1.0`

![image-20230305145412114](D:.\images\image-20230305145412114.png)

- 推送到本地仓库`docker push localhost:5000/xhhubuntu:1.0`

![image-20230305145755840](.\images\image-20230305145755840.png)

> 验证

![image-20230305150335811](D:.\images\image-20230305150335811.png)

## Docker容器数据卷

卷就是目录或者文件，存在于一个或多个容器中，由docker挂载到容器，但不属于联合文件系统，因此能够绕过Union File System提供一些用户持续存储或数据共享的特性；卷的设计目的就是==数据的持久化==，完全独立于容器的生存周期，因此Docker不会在容器删除时删除其挂载的数据卷。

Docker挂载主机目录访问如果出现cannot open directory.:Permission denied

解决方法：在挂载目录后多加一个 --priviledged = true 参数



### 启动

`docker run [-it] --privileged=true -v /宿主机绝对路径目录:/容器内目录  镜像名`

### 特点

- 数据卷可在容器之间进行共享
- 卷中的数据更改可以实时生效
- 数据卷中的更改不会包含在镜像的更新中
- 数据卷的生命周期一直持续到没有容器使用它为止

### 操作案例

- `docker run -it --privileged=true -v /tmp/host_data:/tmp/docker_data:rw ubuntu`，启动交互式容器ubuntu，指定主机`/tmp/host_data`目录和镜像中`/tmp/docker_data`目录进行挂载。`rw, ro`默认读写

- 在docker中添加文件dockerin.txt，此时主机中也同时存在一个dockerin.txt文件

- 主机对dockerin.txt文件修改，docker中也会自动同步

  ![image-20230305202854104](D:.\images\image-20230305202854104.png)

![image-20230305202951121](.\images\image-20230305202951121.png)

### 读写

`docker run -it --privileged=true -v /tmp/host_data:/tmp/docker_data:ro ubuntu`此命令代表docker内docker_data目录下的文件是只读的，不指定默认rw（可读可写）

### 容器之间的继承

容器2继承容器1的卷规则：`docker run -it --privileged=true --volumes-from 父类 [--name 自定义镜像名] 容器:tag`

这样host，容器1，容器2数据内容共享