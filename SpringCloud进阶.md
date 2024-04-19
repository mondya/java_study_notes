# SpringCloud

## 服务注册与发现

Eureka：停更；Consul；Etcd：go语言编写；==Nacos==

## 服务调用和负载均衡

Ribbon（不再使用）；==OpenFegin==

## 分布式事务

==Seata==；LCN；Hmily

## 服务熔断和降级

Hystrix（停更）；Circuit Breater：Resilience4j ；==Sentinel==

## 服务链路追踪

Sleuth(收集)+Zipkin(界面)；==Micrometer Tracing==

## 服务网关

Zuul(停更)；==GateWay==

## 分布式配置管理

Config+Bus（不常使用）；Consul；Nacos

## Consul

Consul是一套开源的分布式服务发现和配置管理系统，由HashiCorp公司用go语言开发。提供了微服务系统中的服务治理、配置中心、控制总线等功能。它具有很多优点。包括：基于raft协议，比较简洁；支持健康检查，同时支持HTTP和DNS协议，支持跨数据中心的WAN集群，提供图形界面；跨平台，支持Linux Mac Windows

- 官网下载consul
- `consul agent -dev`运行consul
- ==http://localhost:8500/==访问consul

![image-20240417233029884](https://gitee.com/cnuto/images/raw/master/image/image-20240417233029884.png)

### 服务注册

#### 引入jar包

```xml
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-consul-discovery</artifactId>
        </dependency>
```

#### yml文件配置

```yml
spring:
  application:
    name: cloud-consumer-80
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3306/mall?characterEncoding=utf-8&useSSL=false&serverTimezone=GMT%2B8&rewriteBatchedStatements=true&allowPublicKeyRetrieval=true
    username: root
    password: xhh1999.02.10
    type: com.alibaba.druid.pool.DruidDataSource
  cloud:
    consul:
      host: localhost
      port: 8500
      discovery:
        service-name: ${spring.application.name}
```

#### 主启动类添加@EnableDiscoveryClient注解

```java
@SpringBootApplication
@MapperScan(basePackages = {"com.xhh.mapper"})
@EnableDiscoveryClient
public class Consumer80Application {
    
    public static void main(String[] args) {
        SpringApplication.run(Consumer80Application.class, args);
    }
}
```

![image-20240418211226986](https://gitee.com/cnuto/images/raw/master/image/image-20240418211226986.png)

#### 注意点

使用restTemplate注入时需要添加@LoadBalanced，原因：被调用方可能存在多个实例，需要让restTemplate具有负载均衡的能力，能够自动选择一个实例发送请求。

```JAVA
@Configuration
public class RestTemplateConfig {
    
    @Bean
    @LoadBalanced
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
```

#### 调用成功

```java
@RestController
@RequestMapping("consumer")
public class ConsumerController {
    
    @Autowired
    private RestTemplate restTemplate;
    
    
    @GetMapping
    public ResultVO getPayList()
    {
        return restTemplate.getForObject("http://cloud-provider-payment8081/pay/list", ResultVO.class);
    }
}
```

### 分布式配置中心

通用全局配置信息，直接注册到consul服务器，然后从consul获取配置

#### 引入jar包

```xml
        <dependency>
            // 引入config
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-consul-config</artifactId>
        </dependency>
        <dependency>
            // 引入bootstrap，这样bootstrap.yml文件才能生效
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-bootstrap</artifactId>
        </dependency>
```

#### 说明

- application.yml是用户级的资源配置项
- bootstrap.yml是系统级的，==优先级更高==
- SpringCloud会创建一个Bootstrap Context，作为Spring应用的Application Context的==父上下文==，Bootstrap Context负责从外部源加载配置属性并解析配置。这两个上下文共享一个从外部获取的Environment。
- Bootstrap属性有高优先级，默认情况下，它们不会被本地配置覆盖。BootstrapContext和ApplicationContext有着不同的约定，所以新增了一个bootstrap.yml文件，保证Bootstrap Context和Appplication Context配置的分离。

#### 步骤

- 配置bootstrap.yml文件

```yml
spring:
  application:
    name: cloud-consumer-80
  cloud:
    consul:
      host: localhost
      port: 8500
      discovery:
        service-name: ${spring.application.name}
      config:
        # 用于在consul中检索不同配置文件的分隔符
        profile-separator: '-'
        # 表示在consul中配置文件的格式为YAML格式
        format: YAML
```

- 在consul的key/value页面中配置目录==config/==

- 在config目录下配置服务名cloud-consumer-80

![image-20240419201922655](https://gitee.com/cnuto/images/raw/master/image/image-20240419201922655.png)

- 配置data

![image-20240419202012505](https://gitee.com/cnuto/images/raw/master/image/image-20240419202012505.png)

- 通过spring.profiles.active切换yml文件配置

```java
    @GetMapping("info")
    public String getInfo(@Value("${xhh.info}") String info) {
        return info;
    }
```

![image-20240419202123007](https://gitee.com/cnuto/images/raw/master/image/image-20240419202123007.png)

#### 动态刷新

- ==@RefreshScope注解==

```java
    @Value("${xhh.info}")
    private String info;
    
// 当info作为成员变量存在时，配置修改后不会刷新，需要在类上加入@RefreshScope注解
    @GetMapping("info")
    public String getInfo() {
        return info;
    }
```

#### 配置持久化

//todo

## 负载均衡LoadBalancer

Spring Cloud LoadBalancer是由SpringCloud官方提供的一个开源的、简单易用的==客户端负载均衡器==，它包含在SpringCloud-commons中用来替换Ribbon组件。相比较于Ribbon,SpringCloud LoadBalancer不仅能够支持restTemplate，还支持WebClient（WebClient是Spring Web Flux作用提供的功能，可以实现响应式异步请求）。

> loadBalancer本地负载均衡客户端VSnginx服务器负载均衡区别

- Nginx是服务器负载均衡，客户端所有请求都会交给nginx，然后由nginx实现转发请求，即负载均衡是由服务端实现的。
- LoadBalancer本地负载均衡，在调用微服务接口时，会在注册中心上获取注册信息服务列表之后缓存到本地JVM，从而在本地实现RPC远程服务调用技术

![image-20240419213733592](https://gitee.com/cnuto/images/raw/master/image/image-20240419213733592.png)

### 配置

调用方引入jar包

```xml
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-loadbalancer</artifactId>
        </dependency>
```

被调用方存在多个实例

8002

![image-20240419225519451](https://gitee.com/cnuto/images/raw/master/image/image-20240419225519451.png)

8001

![image-20240419225548097](https://gitee.com/cnuto/images/raw/master/image/image-20240419225548097.png)