# RocketMQ

## 基本概念

### 1. 消息(Message)

消息是指，消息系统所传输的物理载体，生产和消费数据的最小单位，每条消息必须属于一个主题(topic)。

### 2. 主题(Topic)

> topic:message 1:n
>
> message:topic 1:1

Topic表示一类消息的集合，每个topic包含若干条消息，每条消息只能属于一个Topic,是RocketMQ进行消息订阅的基本单位。

> producer:topic  1:n
>
> consumer:topic 1:1

一个生产者可以同时发送多种Topic消息；而一个消费者只对某种特定的Topic感兴趣，即只可以订阅和消费一种Topic的消息。

### 3. 标签(Tag)

为消息设置的标签，用于同一主题下区分不同类型的消息。来自同一业务单元的消息，可以根据不同业务目的在同一主题下设置不同标签。标签能够有效地保持代码的清晰度和连贯性，并优化RocketMQ提供的查询系统。消费者可以根据Tag实现对不同子主题的不同消费逻辑，实现更好的拓展性。

### 4. 队列(Queue)

存储消息的物理实体。一个Topic中可以包含多个Queue，每个Queue中存放的就是该Topic的消息。一个Topic的Queue也被称为一个Topic中消息的分区

### 5. 消息标识(MessageId/Key)

RocketMQ中每个消息拥有唯一的MessageId, 且可以携带业务标识的key, 以方便对消息的查询。不过需要注意的是，MessageId有两个：在生产者send()消息时会自动生成一个MessageId(msgId)，当消息到达Broker后，Broker也会自动生成一个MessageId(offsetMsgId)。msgId和offsetMsgId都称为消息标识。

- msgId：由producer端生成，其生成规则为：

​	producerIp + 进程pid + MessageClientSetter类的ClassLoader的hashCode + 当前时间 + AutomicInteger自增计数器

- offsetMsgId：由Broker端生成，其规则为:brokerIp + 物理分区的offset
- key：由用户指定的业务相关的唯一标识



## 系统架构

RocketMQ架构上主要分为四部分构成：

### 1.Producer

消息生产者，负责生产消息。Producer通过MQ的负载均衡模块选择相应的Broker集群队列进行消息投递，投递的过程支持快速失败并且低延迟。

RocketMQ中的消息生产者都是以生产者组(Producer Group)的形式出现的。生产者组是同一类生产者的集合，这类Producer发送相同的Topic类型的消息。

一个生产者组可以同时发送多个主题的消息。

### 2.Consumer

消息消费者，负责消费消息。一个消息消费者会从Broker服务器中获取到消息，并对消息进行相关业务处理。RocketMQ中的消息消费者都是以消费者组(Consumer Group)的形式出现的。消费者组是同一类消费者的集合，这类Consumer消费的同一个Topic类型的消息。消费者组使得在消息消费方面，实现负载均衡和容错的目标变得非常容易。

消费组中Consumer的数量应该小于等于订阅Topic的Queue数量。如果超过Queue数量，则多出的Consumer将不能消费消息。

消费组中的消费者可以同时消费多个Queue,一个主题中的Queue只能被一个Consumer消费

### 3.Name Server

NameServer是一个Broker与Topic路由的注册中心，支持Broker的动态注册与发现。

主要包括两个功能：

- **Broker管理**：接受Broker集群的注册信息并且保存下来作为路由信息的基本数据；提供心跳检测机制，检查Broker是否存活。
- **路由信息管理**：每个NameServer中都保存着Broker集群的整个路由信息和用户客户端查询的队列信息。Producer和Consumer通过NameServer可以获取整个Broker集群的路由信息，从而信息消息的投递和消费。

#### 路由注册

NameServer通常是以集群的方式部署，NameServer是无状态的，即NameServer集群中的各个节点间是无差异的，各节点间相互不进行信息通讯。在Broker节点启动时，轮询NameServer列表，与每个NameServer节点建立长连接，发起注册请求。在NameServer内部维护一个Broker列表，用来动态存储Broker的信息。Broker节点为了证明自己alive状态，为了维护与NameServer的长连接，会将最新的信息以心跳包的方式上报给NameServer，每30秒发送一次心跳。心跳包括BrokerId,Broker地址,Broker名称，Broker所属集群名称等等。NamerServer在接收到心跳包后，会更新心跳时间戳，记录这个Broker的最新存活时间。

> NameServer无状态方式，有什么优缺点：
>
> 优点：NameServer集群搭建简单，扩容简单
>
> 缺点：对于Broker，必须明确指出所有NameServer地址。否则未指出的将不会去注册。正因为如初，NameServer并不能随便扩容。Broker不重新配置，新增的NameServer对于Broker来说是不可见的，其不会向这个NameServer进行注册。

#### 路由剔除

由于Broker关机，宕机或网络抖动等原因，NameServer没有收到Broker心跳，NameServer可能会将其从Broker列表中剔除。NameServer中有一个定时任务，每隔10秒就会扫描一次Broker列表，查看每个Broker的最新心跳时间戳距离当前时间是否超过120秒，如果超过，则会判定Broker失效，然后将其从Broker列表中剔除。

#### 路由发现

RockerMQ的路由发现采用的是Pull模型。当Topic路由信息出现变化时，NameServer不会主动推送给客户端，而是客户端定时拉取主题最新的路由。默认客户端每30秒会拉取一次最新的路由。

> push模型：推送模型。实时性较好，是一个“发布-订阅”模型，需要维护一个长连接。而长连接的维护是需要成本的。
>
> pull模型：拉取模型。实时性较差。

#### 客户端NameServer选择策略

> Producer与Consumer

客户端在配置时必须填写NameServer集群地址，客户端在连接NameServer节点时首先随机数，然后在与NameServer节点数量取模，此时得到的就是所要连接节点的索引，然后进行连接。如果连接失败，则会采用round-robin策略，逐个尝试去连接其他节点。

首先采用的是`随机策略`进行选择，失败后采用`轮询策略`

### 4.Broker

#### 功能介绍

Broker充当着消息中转的角色，负责存储消息、转发消息。Broker在RocketMQ系统中负责接收并存储从生产者发送来的消息，同时为消费者的拉取请求作准备。Broker同时也存储着消息相关的元数据，包括消费者组进度偏移offset、主题、队列等。



![image-20220721215923086](D:\study_code\java_study_notes\images\image-20220721215923086.png)

==Remote Module==：整个Broker的实体，负责处理来自clients端的请求。而这个Broker实体则由以下模块构成。

`Client Manager`：客户端管理器。负责接收、解析客户端(Producer/Consumer)请求，管理客户端。例如，维护Consumer的Topic订阅消息。

`Stroe Service`：存储服务。提供方便简单的API接口，处理消息存储到物理硬盘和消息查询功能。

`HA Service`：高可用服务，提供Master Broker和Slave Broker之间的数据同步功能。

`Index Service`：索引服务。根据特定的Message key,对投递到Broker的消息进行索引服务，同时也提供根据Message Key对消息进行快速查询的功能。

#### 集群部署

![](./images/RocketMQ-0726.png)

为了增强Broker性能与吞吐量，Broker一般都是以集群的形式出现的。各集群节点中可能存放着相同Topic的不同Queue。Broker节点集群是一个主从集群，即集群中具有master和slave两种角色。Master负责处理读写操作请求，而slave负责读操作请求。一个Master可以包含多个Slave，但是一个slave只能属于一个Master。Master和Slave的对应关系是通过指定相同的BrokerName、不同的BrokerId来确定的。BrokerId为0表示Master，非0表示Slave。每个Master和Slave集群中的所有节点建立长连接，定时注册Topic信息到所有NameServer。 

### 5.工作流程

#### 具体流程

- 启动NameServer，NameServer启动后开始监听端口，等待Broker、Producer、Consumer链接。
- 启动Broker时，Broker会与所有的NameServer建立长连接，然后每30秒向NameServer发送心跳包
- 收发消息前，可以先创建Topic，创建Topic时需要指定该Topic要存储在哪些Broker上，当然，在创建Topic时也会将Topic与Broker的关系写入到NameServer中。这一步是可选的，也可以在发送消息时自动创建Topic。
- Producer发送消息，启动先跟NameServer集群中的其中一台建立长连接，并从NameServer中获取路由信息，即当前发送的Topic的Queue与Broker的地址(IP+Port)的映射关系。然后根据算法策略从队列中选择一个Queue，与队列所在的Broker建立长连接从而向Broker发送消息。在获取到路由信息后，Producer会首先将路由信息缓存到本地，在每30秒从NameServer更新一次路由信息。
- Consumer和Producer类似，跟其中一台NameServer建立长连接，获取其所订阅的Topic的路由信息，然后根据算法策略从路由信息中获取到其所要的Queue,然后直接跟Broker建立长连接，开始消费其中的消息。Consumer在获取到路由信息后，同样也会每30秒从NameServer更新一次路由信息。不同于Producer，Consumer还会向Broker发送心跳，以确保Broker存活状

#### Topic的创建模式

Topic的创建方式有两种模式：

**集群模式**：该模式下创建的Topic在该集群中，所有Broker中的Queue数量是相同的。
**Broker模式**：该模式下创建的Topic在该集群中，每个Broker中的Queue数量可以不同。

自动创建Topic时，默认采用的是Broker模式，会为每个Broker默认创建4个Queue

#### 读/写队列

从物理上来讲，读/写队列是同一个队列。所以，不存在读/写队列数据同步问题，读/写队列是逻辑上进行区分的概念。一般情况下，读写队列的数量是相同的。

例如，创建Topic时设置的写队列数量为8，读队列为4，此时系统会创建8个Queue，分别是0-7。Producer会将消息写入到这8个队列中，但Consumer只会消费0123这4个队列中的消息，4567中的消息不会被消费到。

在例如，创建Topic时设置的写队列数量为4，读队列为8，此时系统会创建8个Queue，分别是0-7。Producer会将消息写入到这0123这4个队列中，但Consumer只会消费8个队列中的消息，4567中不存在消息。此时假设ConsumerGroup中包含两个Consumer,Consumer1消费0123，而Consumer2消费4567.但是实际上，Consumer2没有消息可以消费。

> 设计目的

例如，原来创建的Topic中包含16个Queue，如何能够使其Queue缩容为8个，还不会丢失消息？可以动态修改写队列数据为8，读队列数量不变。此时新消息只能写入8个队列，而消费的是16个队列。当发现后8个Queue中的数据消费完毕后，就可以把读队列中的Queue数量设置为16.整个过程，没有丢失任何消息。

### 安装与启动

### 安装

官网安装教程https://rocketmq.apache.org/docs/quick-start/

> 解压安装

```bash
  > unzip rocketmq-all-4.9.4-source-release.zip
  > cd rocketmq-all-4.9.4-source-release/
  > mvn -Prelease-all -DskipTests clean install -U
  > cd distribution/target/rocketmq-4.9.4/rocketmq-4.9.4
```

#### 修改初始内存

修改`runserver.sh`和`runbroker.sh`,更改`-Xms`相关参数

#### 启动

> Start Name Server

```bash
  > nohup sh bin/mqnamesrv &
  > tail -f ~/logs/rocketmqlogs/namesrv.log
  The Name Server boot success...
```

> Start Broker

```bash
  > nohup sh bin/mqbroker -n localhost:9876 &
  > tail -f ~/logs/rocketmqlogs/broker.log 
  The broker[%s, 172.30.30.233:10911] boot success...
```

> 发送接收消息

--来自官网：Before sending/receiving messages, we need to tell clients the location of name servers. RocketMQ provides multiple ways to achieve this. For simplicity, we use environment variable `NAMESRV_ADDR`

```bash
 > export NAMESRV_ADDR=localhost:9876
 > sh bin/tools.sh org.apache.rocketmq.example.quickstart.Producer
 SendResult [sendStatus=SEND_OK, msgId= ...

 > sh bin/tools.sh org.apache.rocketmq.example.quickstart.Consumer
 ConsumeMessageThread_%d Receive New Messages: [MessageExt...
```

> 关闭 Servers

```bash
> sh bin/mqshutdown broker
The mqbroker(36695) is running...
Send shutdown request to mqbroker(36695) OK

> sh bin/mqshutdown namesrv
The mqnamesrv(36664) is running...
Send shutdown request to mqnamesrv(36664) OK
```

