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