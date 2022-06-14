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

#### 客户端NameServer选择策略

客户端在配置时必须填写NameServer集群地址，客户端在连接NameServer节点时首先随机数，然后在与NameServer节点数量取模，此时得到的就是所要连接节点的索引，然后进行连接。如果连接失败，则会采用round-robin策略，逐个尝试去连接其他节点。