## 简介

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

![image-20230221224431445](https://gitee.com/cnuto/images/raw/master/image/image-20230221224431445.png)

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

`docker info`：查看docker系统信息，包括镜像和容器数

`doker inspect [容器id/容器别名]`：获取镜像的相关信息

`docker --help`：查看帮助

`docker 具体命令 --help`：查看docker命令帮助文档

### 镜像命令

#### docker images查看镜像

`docker images -a`：列出本地所有镜像，包括历史镜像

`docker images -q`：只显示镜像ID

`docker images`：查看主机上安装的docker镜像

![image-20230223220950358](https://gitee.com/cnuto/images/raw/master/image/image-20230223220950358.png)

REPOSITORY：表示仓库的镜像源

TAG：镜像的标签（同一个仓库可以有多个TAG版本）

IMAGE ID：镜像ID

CREATED：镜像创建时间

SIZE：镜像大小

#### 下载/查询

`docker search xxx` ：查询某个镜像

![image-20230224201903420](https://gitee.com/cnuto/images/raw/master/image/image-20230224201903420.png)

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

![image-20230306210840030](https://gitee.com/cnuto/images/raw/master/image/image-20230306210840030.png)

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

--port 端口号：指定端口号

`--restart=always`：随着docker重启而重启

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

![image-20230306210955250](https://gitee.com/cnuto/images/raw/master/image/image-20230306210955250.png)

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

`docker exec -it 容器ID/容器指定别名 [/bin/bash]`:

`docker attach 容器ID`

区别：attach直接进入容器启动命令的终端，不会启动新的线程，用exit退出，会导致容器的停止

exec是在容器中打开新的终端，并且可以启动新的进程，用exit退出，不会导致容器的停止

#### 从容器中复制文件到主机上

`docker cp 容器ID：容器内路径 目的主机路径`

#### 导入和导出容器

`docker export 容器ID > 文件名.tar`：导出容器的内容流作为一个tar归档文件

`cat 文件名.tar | docker import - 镜像用户/镜像名：镜像版本号`：从tar包中的内容创建一个新的文件系统并导入为镜像

#### 查看容器CPU，内存和网络流量的使用情况

`docker stats`

## 镜像的分层概念

UnionFS(联合文件系统)：UnionFS文件系统是一种分层、轻量级并且高性能的文件系统，它支持对文件系统的修改作为一次提交来一层层叠加，同时可以将不同目录挂载到同一个虚拟文件系统下。Union文件系统是Docker镜像的基础。==镜像可以通过分层来进行继承==，基于基础镜像（没有父镜像），可以制作各种具体的应用镜像。

特性：一次同时加载多个文件系统，但是从外表看只能看到一个文件系统，联合加载会把各层文件系统叠加起来，这样最终的文件系统会包含所有底层的文件和目录。

==Docker镜像层都是只读的，容器层是可写的==。当容器启动时，一个新的可写层别加载到镜像的顶部，这一层通常被称为“容器层”，“容器层”之下的都叫做“镜像层”。所有对容器的改动，无论添加，删除，修改都只会发生在容器层中。

![image-20230304222537180](https://gitee.com/cnuto/images/raw/master/image/image-20230304222537180.png)

Docker中的镜像分层，==支持通过扩展现有镜像，创建新的镜像==。类似于Java继承于一个Base基础类，自己按需拓展，新镜像是从base镜像一层一层叠加生成的，每安装一个软件，就在现有镜像的基础上增加一层。

![image-20230304231011263](https://gitee.com/cnuto/images/raw/master/image/image-20230304231011263.png)

## commit命令

docker commit提交容器副本使之成为一个新的镜像

`docker commit -m="提交的描述信息" -a="作者" 容器id 要创建的目标镜像名:[标签名]`

> 在ubuntu中安装vim

- `docker run -it ubuntu`启动ubuntu
- `vim helloworld.java`提示vim :command not found
- `apt-get update`
- `apt-get -y install vim`，安装vim

![image-20230304224805660](https://gitee.com/cnuto/images/raw/master/image/image-20230304224805660.png)

> commit安装过vim命令的ubuntu

![image-20230304225657128](https://gitee.com/cnuto/images/raw/master/image/image-20230304225657128.png)

## 发布阿里云

todo

## Dokcer私有仓库

- `docker pull registry`：下载镜像私有库
- 启动registry:`docker run -d -p 5000:5000 -v /xhh/myregistry/:/tep/resigtry --privileged=true registry`，默认情况下，仓库被创建在容器的/var/lib/registry目录下，建议自行用容器卷映射，方便宿主机联调

- commit自制的镜像`docker commit -m="ubuntu add ipconfig" -a="xhh" 0fe074bfc978 ubuntu-ipconfig:1.0`

- `curl -XGET http://localhost:5000/v2/_catalog`：查看本地镜像仓库的镜像

![image-20230305144520439](https://gitee.com/cnuto/images/raw/master/image/image-20230305144520439.png)

- 按照公式 `docker tag 镜像:Tag Host:Port/Repository:Tag`，host，port为自己主机`docker tag 7cafd0fc013b localhost:5000/xhhubuntu:1.0`

![image-20230305145412114](https://gitee.com/cnuto/images/raw/master/image/image-20230305145412114.png)

- 推送到本地仓库`docker push localhost:5000/xhhubuntu:1.0`

![image-20230305145755840](https://gitee.com/cnuto/images/raw/master/image/image-20230305145755840.png)

> 验证

![image-20230305150335811](https://gitee.com/cnuto/images/raw/master/image/image-20230305150335811.png)

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

  ![image-20230305202854104](https://gitee.com/cnuto/images/raw/master/image/image-20230305202854104.png)

![image-20230305202951121](https://gitee.com/cnuto/images/raw/master/image/image-20230305202951121.png)

### 读写

`docker run -it --privileged=true -v /tmp/host_data:/tmp/docker_data:ro ubuntu`此命令代表docker内docker_data目录下的文件是只读的，不指定默认rw（可读可写）

### 容器之间的继承

容器2继承容器1的卷规则：`docker run -it --privileged=true --volumes-from 父类 [--name 自定义镜像名] 容器:tag`

这样host，容器1，容器2数据内容共享

## Tomcat的安装

`docker pull tomcat`：下载最新版tomcat镜像

`docker run -d -p 8080:8080 --name t1 tomcat `：后台启动tomcat，此时访问tomcat80端口报404，需要把tomcat中的webapps.dist改名为webapps，原来的webapps目录为空，可以删除

![image-20230306213241360](https://gitee.com/cnuto/images/raw/master/image/image-20230306213241360.png)

## MySQL的安装

`docker pull mysql:8.0.18`：下载mysql镜像

`docker run -p 3306:3306 -e MYSQL_ROOT_PASSWORD=xhh1999.02.10 -d mysql:8.0.18`：启动mysql并且设置root的密码

`docker exec -it d278c6a0069e /bin/bash`进入mysql容器内部

`mysql -uroot -p`：输入mysql密码进入mysql

`show databases;`：查看mysql中的数据库

![image-20230306223514456](https://gitee.com/cnuto/images/raw/master/image/image-20230306223514456.png)

`create database db01;`：新建数据库db01

`use db01;`

`create table if not exist user(id bigint, name varchar(255))`;：创建表

`insert into user values(1,'helloworld');`插入数据

`select * from user;`：查询数据

> 需要注意中文乱码问题，mysql8.0默认编码utf8，但是5.7不是

![image-20230306223639855](https://gitee.com/cnuto/images/raw/master/image/image-20230306223639855.png)

## MySQL安装进阶

`docker run -d -p 3306:3306 --privileged=true -v /xhh/mysql/log:/var/log/mysql -v /xhh/mysql/data:/var/lib/mysql -v /xhh/mysql/conf:/etc/mysql/conf.d -e MYSQL_ROOT_PASSWORD=xhh1999.02.10 --name=mysql mysql:8.0.18`

启动mysql，映射3306端口, 挂载mysql  log,data,conf目录，在conf目录中新建`my.cnf`文件，可添加utf8默认配置，解决5版本下不能插入中文数据问题

## Redis安装

`docker run -d -p 6379:6379 --name=myredis --privileged=true -v /xhh/redis/redis.conf:/etc/redis/redis.conf -v /xhh/redis/data:/data redis redis-server /etc/redis/redis.conf`

在主机上新建redis相关目录，与docker镜像中的redis配置文件进行映射，指定redis的配置文件启动，`redis-cli`进入redis

验证配置文件修改后是否生效：更改redis默认角标大小，默认`databases 16`，在主机中更改，docker容器中的redis也会同步修改，此时select 大于修改后的值，报错

## MySQL主从复制搭建

以安装mysql为例

```yaml
[mysqld]
## 【必须】设置servier_id，同一局域网中需要唯一
server_id=101
## 指定不需要同步的数据库名称
binlog-ignore-db=mysql
## 设置需要复制的数据库，默认全部记录。建议不设置，默认
binlog-do-db=timetabling
## 【必须】开启二进制日志功能，指明路径。比如：自己本地的路径/log/mysqlbin
log-bin=mall-mysql-bin
## 可选：0默认表示读写（主机），1表示只读（从机）
read-only=0
## 设置二进制日志使用内存大小
binlog_cache_size=1M
## 设置使用的二进制日志格式(mixed,statement,row)
binlog_format=mixed
## 二进制日志过期清理时间，默认为0，表示不自动清理
expire_logs_days = 7
## 跳过主从复制中遇到的所有错误或指定类型的错误，避免slave端复制中断
## 如1062错误：是指一些主键重复，1032错误指主从数据库数据不一致
slave_skip_errors=1062
```



### 新建主服务器容器实例3307

启动master  ： `docker run -p 3307:3306 --name=mysql-master --privileged=true -v /xhh/mysql-master/log:/var/log/mysql -v /xhh/mysql-master/data:/var/lib/mysql -v /xhh/mysql-master/conf:/etc/mysql/conf.d -e MYSQL_ROOT_PASSWORD=admin123 -d mysql:8.0.18`

在`/xhh/mysql-master/conf`目录下新建my.cnf文件

重启mysql-master:`docker restart mysql-master`

mysql5:

```mysql
# mysql5只需要设置这一步
GRANT REPLICATION SLAVE ON *.* to 'slave'@'从机数据库IP' IDENTIFIED BY 'admin123'; # 5.5, 5,7
```

mysql8:

创建用户`create user 'slave'@'%' identified by '123456';`

`grant replication slave, replication client on *.* to 'slave'@'%';`：主从复制

==注意==：mysql8版本设置账号要加一步：执行1：`ALTER USER 'slave'@'%' IDENTIFIED WITH mysql_native_password BY '123456';`

执行2：`flush privileges;`



![image-20230311002001884](https://gitee.com/cnuto/images/raw/master/image/image-20230311002001884.png)

### 新建从服务器容器实例3308

启动slave ： ``docker run -p 3308:3306 --name=mysql-slave --privileged=true -v /xhh/mysql-slave/log:/var/log/mysql -v /xhh/mysql-slave/data:/var/lib/mysql -v /xhh/mysql-slave/conf:/etc/mysql/conf.d -e MYSQL_ROOT_PASSWORD=admin123 -d mysql:8.0.18``

在`/xhh/mysql-slave/conf`目录下新建my.cnf

```yaml
[mysqld]
## 设置servier_id，同一局域网中需要唯一
server_id=102
## 指定不需要同步的数据库名称
binlog-ignore-db=mysql
## 开启二进制日志功能
log-bin=mall-mysql-bin
## 设置二进制日志使用内存大小
binlog_cache_size=1M
## 设置使用的二进制日志格式(mixed,statement,row)
binlog_format=mixed
## 二进制日志过期清理时间，默认为0，表示不自动清理
expire_logs_days = 7
## 跳过主从复制中遇到的所有错误或指定类型的错误，避免slave端复制中断
## 如1062错误：是指一些主键重复，1032错误指主从数据库数据不一致
slave_skip_errors=1062
## relay_log配置中继
relay_log=mall-mysql-relay-bin
## log_slave_updates表示slave将复制事件写进自己的二进制日志
log_slave_updates=1
## slave设置为只读（具有super权限的用户除外）
read_only=1
```

重启master-slave

### 在主数据库中查看主从同步状态

`show master status;`

![image-20230311002626606](https://gitee.com/cnuto/images/raw/master/image/image-20230311002626606.png)

### 在从数据库中配置主从复制

`change master to master_host='宿主机ip',master_user='slave',master_password='123456',master_port=3307,master_log_file='mall-mysql-bin.000004',master_log_pos=710,master_connect_retry=30;`

`master_host`：主数据库的IP地址

`master_port`：主数据库运行端口

`master_user`：在主数据库创建的用于同步数据的用户账号

`master_password`：在主数据库创建的用于同步数据的用户密码

`master_log_file`：指定从数据库要复制数据的日志文件，通过查看主数据的状态，获取file参数

`master_log_pos`：指定从数据库从哪个位置开始复制数据，通过查看主数据的状态，获取position参数

`master_connect_retry`：连接失败重试的时间间隔，单位为秒

`change master to master_host='192.168.31.142',master_user='slave',master_password='123456',master_port=3307,master_log_file='mall-mysql-bin.000004',master_log_pos=1160,master_connect_retry=30;`

![image-20230311005045281](https://gitee.com/cnuto/images/raw/master/image/image-20230311005045281.png)

### 在从数据库中查看主从同步状态

`show slave status \G;`

![image-20230311005145772](https://gitee.com/cnuto/images/raw/master/image/image-20230311005145772.png)

### 在从数据库中开启主从同步

`start slave;`

![image-20230311005315282](https://gitee.com/cnuto/images/raw/master/image/image-20230311005315282.png)

![image-20230311145231446](https://gitee.com/cnuto/images/raw/master/image/image-20230311145231446.png)

### 验证

在主数据库中创建数据库`db01`，创建表`student`，插入一条数据。

在从数据库中验证是否查询到数据。

![image-20230311145930523](https://gitee.com/cnuto/images/raw/master/image/image-20230311145930523.png)

## 分布式存储

### hash取余算法

==优点==：简单有效，只需要预估好数据规划好节点，例如3台，8台，10台，就能保证一段时间的数据支撑。使用Hash算法让固定的一部分请求落到同一台服务器上，这样每台服务器固定处理一部分请求（并维护这些请求的信息），起到负载均衡+分而治之的作用。

==缺点==：扩容/缩容麻烦，每次扩缩导致节点有变动，映射关系需要重新计算，假设有宕机情况，原来的取模公式就会发生变化，导致hash取余全部数据重新清洗。

### 一致性哈希环

把redis节点按照取模算法放入0-2^31组成的圆环中，把key映射成hash值，顺时针或者逆时针遇到的第一个redis节点就是需要放入数据的服务器

==容错性==：在一致性hash算法中，如果一台服务器不可用，则受影响的数据仅仅是此服务器到其环空间中前一台服务器之间的数据，其他不会受到影响。

==扩展性==：数据量增加了，需要增加一台服务器NodeX。X的位置在A-B之间，受到影响数据是A-X之间的数据，重新把A-X的数据录入到X上即可，不会导致hash取余全部数据重新洗牌。

==缺点==：hash环的数据倾斜问题，一致性hash算法在服务节点太少时，容易因为节点分布不均匀而造成数据倾斜（被缓存的对象大部分集中缓存在某一台服务器上）问题。

### hash槽分区

哈希槽实质上就是一个数组，数组[0, 2^14 -1]形成hash slot空间

目的：解决均匀分配的问题，==在数据和节点之间又加入了一层，把这层成为哈希槽(slot)，用于管理数据和节点之间的关系==

Redis集群并没有使用一致性hash而是引入了哈希槽的概念。Redis集群有16384个哈希槽，每个key通过CRC16校验后对16384取模来决定放置在哪个槽，集群的每一个节点负责一部分hash槽。

CRC16算法产生的hash值有16bit，该算法可以产生2^16=65535个值

> 为什么不是对65535取模

- 如果槽位为65535，发送心跳信息的消息头达8k，发送的心跳包过于庞大。在消息头中最占空间的是myslots[CLUSTER_SLOTS/8]，当槽位为65536时，这块的大小是65536/8/1024=8kb，比较浪费带宽
- redis集群主节点数量基本不可能超过1000个。集群节点越多，心跳包的消息体内携带的数据越多，如果节点过1000个，也会导致网络拥堵，对于节点数在1000以内的redis cluster集群，16384个槽位足够使用
- 槽位越小，节点少的情况下，压缩比高，容易传输。Redis主节点的配置信息中它所负责的哈希槽是通过一张bitmap的形式来保存的，在传输过程中会对bitmap进行压缩，但是如果bitmap的填充率slots/N很高的话（N表示节点数），bitmap的压缩率就很低。如果节点数很少，而哈希槽数量多的话，bitmap的压缩率也很低。

## 3主3从Redis集群配置

### 启动6台docker

1：`docker run -d --name=redis-node-1 --net host --privileged=true -v /data/redis/share/redis-node-1:/data redis --cluster-enabled yes --appendonly yes --port 6381`

2：`docker run -d --name=redis-node-2 --net host --privileged=true -v /data/redis/share/redis-node-2:/data redis --cluster-enabled yes --appendonly yes --port 6382`

3：`docker run -d --name=redis-node-3 --net host --privileged=true -v /data/redis/share/redis-node-3:/data redis --cluster-enabled yes --appendonly yes --port 6383`

4：`docker run -d --name=redis-node-4 --net host --privileged=true -v /data/redis/share/redis-node-4:/data redis --cluster-enabled yes --appendonly yes --port 6384`

5：`docker run -d --name=redis-node-5 --net host --privileged=true -v /data/redis/share/redis-node-5:/data redis --cluster-enabled yes --appendonly yes --port 6385`

6：`docker run -d --name=redis-node-6 --net host --privileged=true -v /data/redis/share/redis-node-6:/data redis --cluster-enabled yes --appendonly yes --port 6386`

> --net host：使用宿主机的IP和端口，默认
>
> --cluster-enabled true：开启redis集群
>
> -- appendonly yes：开启持久化

![image-20230311221139778](https://gitee.com/cnuto/images/raw/master/image/image-20230311221139778.png)

### 进入容器redis-node-1并为6台机器构建集群关系

主机ip:`192.168.31.142`

`--cluster-replicas 1`：表示为每个master创建一个slave节点，1主1从

进入某个redis docker实例，构建集群关系

`redis-cli --cluster create 192.168.31.142:6381 192.168.31.142:6382 192.168.31.142:6383 192.168.31.142:6384 192.168.31.142:6385 192.168.31.142:6386 --cluster-replicas 1`

![image-20230311223009098](https://gitee.com/cnuto/images/raw/master/image/image-20230311223009098.png)

![image-20230311223128176](https://gitee.com/cnuto/images/raw/master/image/image-20230311223128176.png)

### 查看集群状态/信息

以6381为切入点，查看节点状态

`redis-cli -p 6381`：进入redis，redis默认端口6379，此时端口号被改成6381

#### 查看集群的状态

需要进入redis

查看集群的状态：`cluster info`

![image-20230311224701219](https://gitee.com/cnuto/images/raw/master/image/image-20230311224701219.png)

#### 查看节点之间的关系

需要进入redis

查看节点之间的关系：`cluster nodes`

![image-20230311225426239](https://gitee.com/cnuto/images/raw/master/image/image-20230311225426239.png)

#### 查看集群的槽位分配相关信息

查看集群的槽位分配相关信息：`redis-cli --cluster check [主机ip]:端口号`

![image-20230312142553364](https://gitee.com/cnuto/images/raw/master/image/image-20230312142553364.png)

### 读写数据

不能使用`redis-cli -p 6381 `，这样只连接了一台redis，不在该redis分配的槽位时数据不能写入

![image-20230312141442699](https://gitee.com/cnuto/images/raw/master/image/image-20230312141442699.png)

加入`-c`，`redis-cli -p 6381 -c`：链接集群

![image-20230312142156208](https://gitee.com/cnuto/images/raw/master/image/image-20230312142156208.png)

### Redis集群主从切换

#### 关闭redis-node-1

`docker stop redis-node-1`

![image-20230312145501121](https://gitee.com/cnuto/images/raw/master/image/image-20230312145501121.png)

#### 重启redis-node-1

`docker restart redis-node-1`

![image-20230312145947112](https://gitee.com/cnuto/images/raw/master/image/image-20230312145947112.png)

#### 保持原来的主从关系

`docker stop redis-node-6`,`docker start redis-node-6`：先关闭，在重启

![image-20230312151246610](https://gitee.com/cnuto/images/raw/master/image/image-20230312151246610.png)

### 主从扩容

#### 新增6387,6388docker实例

`docker run -d --name=redis-node-7 --net host --privileged=true -v /data/redis/share/redis-node-7:/data redis --cluster-enabled yes --appendonly yes --port 6387`

`docker run -d --name=redis-node-8 --net host --privileged=true -v /data/redis/share/redis-node-8:/data redis --cluster-enabled yes --appendonly yes --port 6388`

#### 将新增的6387节点作为master节点加入集群

`redis-cli --cluster add-node [ip地址]:6387  [ip地址]:6381`

`redis-cli --cluster add-node 192.168.31.142:6387  192.168.31.142:6381`

6387就是将要作为master新增节点

6381就是已存在的节点，指定一个已经存在节点的Ip和端口号，用于将新节点加入到已经存在的集群中

![image-20230312160355025](https://gitee.com/cnuto/images/raw/master/image/image-20230312160355025.png)

![image-20230312160516868](https://gitee.com/cnuto/images/raw/master/image/image-20230312160516868.png)

#### 重新分配槽号

`redis-cli --cluster reshard IP地址:端口号`

`redis-cli --cluster reshard 192.168.31.142:6381`

![image-20230312174444786](https://gitee.com/cnuto/images/raw/master/image/image-20230312174444786.png)

再次查看槽位分配信息

![image-20230312174607089](https://gitee.com/cnuto/images/raw/master/image/image-20230312174607089.png)

#### 为主节点6387挂载slave6388

`redis-cli --cluster add-node ip:新slave端口ip  ip:新master端口ip  --cluster-slave --cluster-master-id 新主机节点ID`

`redis-cli --cluster add-node 192.168.31.142:6388 192.168.31.142:6387 --cluster-slave --cluster-master-id 3a816d4fe438e4e5def869a4fa03996f0cf80622`

![image-20230312181828862](https://gitee.com/cnuto/images/raw/master/image/image-20230312181828862.png)

#### 再次查看节点分配信息

`redis-cli --cluster check 192.168.31.142:6387`

![image-20230312182342170](https://gitee.com/cnuto/images/raw/master/image/image-20230312182342170.png)

### 主从缩容

#### 先删除从节点

`redis-cli --cluster del-node ip:从机端口 从机6388节点ID`

`redis-cli --cluster del-node 192.168.31.142:6388 2eff6b953985e980dfdfdf2850053d93de7bbf22`

![image-20230312184537624](https://gitee.com/cnuto/images/raw/master/image/image-20230312184537624.png)

#### 重新分配槽号

`redis-cli --cluster reshard IP地址:端口号`

![image-20230312185434211](https://gitee.com/cnuto/images/raw/master/image/image-20230312185434211.png)

#### 再次查看节点分配信息

![image-20230312185618506](https://gitee.com/cnuto/images/raw/master/image/image-20230312185618506.png)

#### 删除被清空的master节点6387

`redis-cli --cluster del-node 192.168.31.142:6387 3a816d4fe438e4e5def869a4fa03996f0cf80622`

![image-20230312194423548](https://gitee.com/cnuto/images/raw/master/image/image-20230312194423548.png)

## DockerFile

DockerFile是用来构建Docker镜像的文本文件，是由一条条构建镜像所需的指令和参数构成的脚本。

### DockerFile内容基础知识

- 每条保留字指令都==必须为大写字母==并且后面要跟随至少一个参数
- 指令按照从上到下，顺序执行
- `# `表示注释
- 每条指令都会创建一个新的镜像层并对镜像进行提交

从应用软件的角度来看，DockerFile，Docker镜像和Docker容器分别代表软件的三个不同阶段，DockerFile是软件的原材料，Docker镜像是软件的交付品，Docker容器则可以认为是软件镜像的运行态，也即依照镜像运行的容器实例。

### DockerFile保留字

#### FROM

基础镜像，当前新镜像是基于哪个镜像，指定一个已经存在的镜像作为模板，第一条必须是FROM

#### MAINTAINER

镜像维护者的姓名和邮箱地址

#### RUN

容器构建时需要运行的命令，它有两种格式：`shell格式`或者`exec格式`，RUN在docker build时运行，等同于在终端执行命令

#### EXPOSE

当前容器对外暴露出的端口

#### WORKDIR

指定在创建容器后，终端默认登陆进来的工作目录

#### USER

指定该镜像以什么样的用户去执行，如果不指定，默认root用户

#### ENV

用来在构建镜像过程中设置环境变量

```dockerfile
ENV MY_PATH /user/mytest

# 这个环境变量可以在后续的任何RUN指令中使用，这就如同在命令前面指定了环境变量前缀一样；也可以直接在其他指令中直接使用这些环境变量，比如 WORKDIR $MY_PATH
```

#### ADD

将宿主机目录下的文件拷贝进镜像且会自动处理URL和解压tar压缩包

#### COPY

类似ADD，拷贝文件和目录到镜像中。将从构建上下文目录中<源文件>的文件/目录复制到新的一层的镜像内的<目标路径>位置

```dockerfile
COPY [src] [dest]

# <src源路径>：源文件或者源目录  <dest目标路径>：容器内指定的路径，该路径不用事先建好
```

#### VOLUME

容器数据卷，用于数据保存和持久化工作

#### CMD

指定容器启动后要执行的操作

注意：DockerFile中可以有多个CMD指令，==但是只有最后一个生效，CMD会被docker run之后的参数替换==

和RUN命令的区别：CMD在docker run阶段运行；RUN在docker build阶段运行

如果此时我们在容器启动时加上`/bin/bash`，即`docker run -it -p 8080:8080 tomcat /bin/bash`此时进入`localhost:8080`会发现页面404

```dockerfile
# 运行Tomcat容器时，容器最后执行的CMD命令
EXPOSE 8080
CMD ["catalina.sh", "run"]

# 此时加上/bin/bash相当于在命令后面
CMD ["/bin/bash", "run"]
# catalina.sh被覆盖了
```

#### ENTRYPOINT

也是用来指定一个容器启动时要运行的命令

类似于CMD指令，但是ENTRYPOINT==不会被docker run后面的命令覆盖==，而且这些命令行参数==会被当做参数带给ENTRYPOINT指定的程序==

当指定了ENTRYPOINT后，CMD的含义就发生了变化，不在是直接运行其命令而是将CMD的内容作为参数传递给ENTRYPOINT指令

假设已通过DockerFile构建了nginx:test镜像

```dockerfile
FROM nginx

ENTRYPOINT ["nginx", "-c"] #定参
CMD ["/etc/nginx/nginx.conf"] #变参
```

按照dockerfile编写执行：`docker run nginx:test`  ---> 衍生出的实际命令`nginx -c /etc/nginx/nginx.conf`

传参运行：`docker run nignx:test -c /etc/nignx/new.conf`  ---> 衍生出的实际命令`nginx -c /etc/nginx/new.conf`

![image-20230315231026453](https://gitee.com/cnuto/images/raw/master/image/image-20230315231026453.png)

### 使用DockerFile创建自定义centos

```dockerfile
FROM centos:7
MAINTAINER xhh<xhh19990210@gmail.com>
ENV MYPATH /usr/local
WORKDIR $MYPATH

# 安装vim编辑器
RUN yum -y install vim

# 安装ifconfig命令
RUN yum -y install net-tools

# 安装java8以及lib库
RUN yum -y install glibc.i686
RUN mkdir /usr/local/java

# ADD是相对路径jar,把jdk-8u171-linux-x64.tar.gz添加到容器中，安装包必须和DockerFile文件在同一个目录下
ADD jdk-8u171-linux-x64.tar.gz /usr/local/java/

# 配置java环境变量
ENV JAVA_HOME /usr/local/java/jdk1.8.0_171
ENV JRE_HOME $JAVA_HOME/jre
ENV CLASSPATH $JAVA_HOME/lib/dt.jar:$JAVA_HOME/lib/tools:$JRE_HOME/lib:$CLASSPATH
ENV PATH $JAVA_HOME/bin:$PATH

EXPOSE 80

CMD echo $MYPATH
CMD echo "success-----------ok"
CMD /bin/bash
```

## 构建镜像

`docker build -t 新镜像名字:TAG .`，注意TAG后面有个空格，有个点，代表上下文路径。上下文路径是指docker在构建镜像，有时候需要使用本机的文件，docker build得知这个路径后，会将路径下的所有内容打包。

`docker build -t centosjava8:1.5 .`

![image-20230316220231696](https://gitee.com/cnuto/images/raw/master/image/image-20230316220231696.png)

![image-20230316220457635](https://gitee.com/cnuto/images/raw/master/image/image-20230316220457635.png)

## 虚悬镜像

仓库名，标签都是<none>的镜像，虚悬镜像都是发生错误的镜像，应该剔除

`docker images ls -f dangling=true`：列出虚悬镜像，即resository和tag都为<none>的镜像

`docker image prune`：剔除所有虚悬镜像

## Docker微服务

把项目打包成jar

### 编写Dockerfile

```dockerfile
# 基础镜像使用Java
FROM java:8
# 作者
MAINTAINER xhh

# VOLUME 指定临时文件目录为/tmp，在主机/var/lib/docker目录下创建了一个临时文件并链接到容器的/tmp
VOLUME /tmp

# 将jar包添加到容器中更名为xhh_test.jar
ADD spring-twoSon-1.0.jar xhh_test.jar

# 运行jar包
RUN bash -c 'touch /xhh_test.jar'
ENTRYPOINT ["java", "-jar", "/xhh_test.jar"]

# 暴露6001端口作为微服务端口
EXPOSE 6001
```

### 构建

`docker build -t xhh_docker:1.0 .`

![image-20230318223627898](https://gitee.com/cnuto/images/raw/master/image/image-20230318223627898.png)

### 验证

![image-20230318224921550](https://gitee.com/cnuto/images/raw/master/image/image-20230318224921550.png)

![image-20230318224955086](https://gitee.com/cnuto/images/raw/master/image/image-20230318224955086.png)

## Docker网络

`docker network ls`：查看docker网络

`docker network create [网络名]`：创建自定义网络

| 网络模式       | 简介                                                         |
| -------------- | ------------------------------------------------------------ |
| bridge（默认） | 为每一个容器分配、设置IP等，并将容器链接到一个`docker0`，虚拟网桥，默认为该模式 |
| host           | 容器将不会虚拟出自己的网卡，配置自己的IP等，而是使用主机的IP和端口 |
| none           | 容器有独立的NetWork namespace，但是并没有对其进行任何网络设置，如分配weth pari和网桥链接，IP等 |
| container      | 新创建的容器不会创建自己的网卡和配置自己的IP，而是和一个指定的容器共享IP，端口范围等 |

### Bridge

Docker服务默认创建一个docker0网桥（其上有一个docker0内部接口），该桥接网络的名称为docker0，它在内核层连通了其他的物理或虚拟网卡，这就将所有容器和本地主机都放在同一个物理网络。Docker默认指定了docker0接口的IP地址和子网掩码，==让主机和容器之间可以通过网桥相互通信==。

`docker network inspect bridge | grep name`：查看bridge网络的详细信息，并通过grep获取名称项，名称不填写默认docke0

- Docker使用Linux桥接，在宿主机虚拟一个Docker容器网桥（docker0），Docker启动一个容器时会根据Docker网桥的网段分配给容器一个IP地址，称为Container-IP，同时Docker网桥是每个容器的默认网关。因为在同一宿主机内的容器都接入同一个网段，这样容器之间就能够通过容器的Container-IP直接通信。
- docker run的时候，没有指定network时默认使用的桥接模式就是bridge，也就是docker0。

- 整个宿主机的网桥模式都是docker0，类似一个交换机有一堆接口，每个接口叫做veth，在本地主机和容器内分别创建也给虚拟接口，并让他们彼此联通（这样一对接口叫做veth pari）；每个容器实例内部也有一块网卡，每个接口叫做eth0;docker0上面的每个veth匹配某个容器实例内部的eth0，两两配对，一一匹配

![image-20230321213952115](https://gitee.com/cnuto/images/raw/master/image/image-20230321213952115.png)

`docker run -d -p 8081:8080 --name tomcat1 tomcat`

`docker run -d -p 8082:8080 --name tomcat2 tomcat`

启动两个tomcat，查看主机端口`ip addr`

![image-20230321220643796](https://gitee.com/cnuto/images/raw/master/image/image-20230321220643796.png)

### Host

直接使用宿主机的IP和外界进行通信，不在需要额外进行NAT转换

`docker run -d -p 8083:8080 --network host --name tomcat3 tomcat`

使用宿主机Ip启动tomcat会发出警告

原因：docker启动时指定--network=host或者-net=host，如果还指定了-p端口映射，这时会发出警告，并且通过-p 设置的参数将会失效，端口号会以主机端口号为主，重复时递增

容器将==不会获得==一个独立的NetWork NameSpace，而是和宿主机共用一个NetWork NameSpace。==容器将不会虚拟出自己的网卡而是使用宿主机的IP和端口==

![image-20230321223313309](https://gitee.com/cnuto/images/raw/master/image/image-20230321223313309.png)

### None

禁用网络功能，只有lo标识(即只有127.0.0.1)

### Container

新建的容器和已经存在的一个容器共享一个网络Ip配置而不是和宿主机共享。新创建的容器不会创建自己的网卡，配置自己的IP，而是和一个指定的容器共享IP，端口范围等。同样，两个容器除了网络方面，其他的如问价系统，进程列表等还是隔离的

![image-20230323212401608](https://gitee.com/cnuto/images/raw/master/image/image-20230323212401608.png)

`docker run -d -p 8085:8080 --name tomcat85 tomcat`
`docker run -d -p 8086:8080 --network container:tomcat85 --name tomcat`

报错，两个tomcat都是8080，端口冲突，使用alpine验证

`docker run -it --name alpine1 alpine /bin/sh `

`docker run -it --network container:apline1 --name apline2 /bin/sh`

![image-20230323213815548](https://gitee.com/cnuto/images/raw/master/image/image-20230323213815548.png)

### 自定义网络

为了能够直接Ping通服务名

`docker network create xhh_netwokr`

启动两个tomcat：`docker run -d -p 8081:8080 --network xhh_network --name tomcat81 tomcat`,`docker run -d -p 8082:8080 --network xhh_network --name tomcat82 tomcat`

此时ping服务名能够ping通

==自定义网络本身就维护了主机名和ip的对应关系（ip和域名都能通）==

## Docker-compose容器编排

Compose允许用户通过一个单独的==docker-compose.yml==（YAML格式）来定义一组相关联的应用容器为一个项目(project)

### 安装docker compose

- 下载`curl -SL https://github.com/docker/compose/releases/download/v2.16.0/docker-compose-linux-x86_64 -o /usr/local/bin/docker-compose`

- 更改权限`chomd +x /usr/local/bin/docker-compose`

![image-20230324212608123](https://gitee.com/cnuto/images/raw/master/image/image-20230324212608123.png)

### 常用命令

`docker-compose up`：启动所有docker-compose服务

`docker-compose up -d`：==启动所有的docker-compose服务并后台运行==

`docker-compose down`：==停止并删除容器，网络，卷，镜像==

`docker-compose exec yml里面的服务id /bin/bash` ：进入容器实例内部

`docker-compose ps`：展示当前docker-compose编排过的运行的所有容器

`docker-compose logs yml里面的服务Id`： 查看容器输入日志

`docker-compose config`：==检查配置==

`docker-compose config -q`：==检查配置，有问题才有输出==

`docker-compose restart`：重启服务

`docker-compose start`：启动服务

`docker-compose stop`：停止服务

### docker-compose.yml文件

```yaml
version : "3"

services:
	microService:
		image: xhh_docker:1.0
		container_name: ms01
		ports:
			- "6001:6001"
		volumes:
			- /app/microService:/data
		newworks:
			- xhh_net
		depends_on:
			- redis
			- mysql
	
    
    redis:
    	image: redis:6.0.8
    	ports: 
    		- "6379:6379"
    	volumes:
    		- /app/redis/redis.conf:/etc/redis/redis.conf
    		- /app/redis/data:/data
    	newworks:
    		- xhh_net
    	command: redis-server /etc/redis/redis.conf
    	
    mysql:
   		image: mysql:8.0.18
   		environment:
   			MYSQL_ROOT_PASSWORD: 'admin123'
   			MYSQL_ALLOW_EMPTY_PASSWORD: 'no'
   			MYSQL_DATABASE: 'db2021'
   		ports:
   			- '3306:3306'
   		volumes:
   			- /app/mysql/db: /var/lib/mysql
   			- /app/mysql/conf/my.cnf: /etc/my.cnf
   			- /app/mysql/init: /docker-entrypoint-initdb.d
   		network:
   			- xhh_net
   		command: --default-authentication-plugin = mysql_native_password # 解决外部无法访问
   	networks:
   		xhh_net:
			
```

## Portainer可视化

// TODO

## Docker容器监控（CAdvisor+InfluxDB+Granfana）

### CAdvisor

CAdvisor是一个容器资源监控工具，包括容器的内存，CPU，网络IO，磁盘IO等监控，同时提供了一个WEB页面用于查看容器的实时运行状态。CAdvisor默认存储2分钟的数据，而且只是针对单物理机。

CAdvisor功能主要有两点：

- 展示Host和容器两个层次的监控数据
- 展示历史变化数据

### InfluxDB

InfluxDB是用GO编写的一个开源分布式时序，时间和指标数据库，无需外部依赖

主要功能：

- 基于时间序列，支持与时间有关的相关函数（如最大、最小、求和等）
- 可度量性：你可以实时对大量数据进行计算
- 基于事件：它支持任意的是事件数据

### Granfana

是一个开源的数据监控分析可视化平台

主要特性：

- 灵活丰富的图形化选项