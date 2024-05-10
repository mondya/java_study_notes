#  SpringCloud

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

#### 引入jar包

调用方引入jar包(如果引入过spring-cloud-starter-consul-discovery，可以不需要显示引入loadbalancer)

```xml
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-loadbalancer</artifactId>
        </dependency>
```

#### ==调用方使用restTemplate时需要注入添加@LoadBalanced==

被调用方存在多个实例

8002

![image-20240419225519451](https://gitee.com/cnuto/images/raw/master/image/image-20240419225519451.png)

8001

![image-20240419225548097](https://gitee.com/cnuto/images/raw/master/image/image-20240419225548097.png)

## OpenFeign

### 配置

#### 引入jar包

```xml
        <!--引入feigin支持-->
		<dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-openfeign</artifactId>
        </dependency>
		<!--注册服务到注册中心-->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-consul-discovery</artifactId>
            <exclusions>
                <exclusion>
                    <groupId>commons-logging</groupId>
                    <artifactId>commons-logging-api</artifactId>
                </exclusion>
            </exclusions>
        </dependency>
```

#### 调用方主启动类添加==@EnableFeignClients==注解

#### 添加interface

```java
@FeignClient(name = "cloud-provider-payment")
public interface PayApi {
    
    @RequestMapping(method = RequestMethod.GET, value = "/pay/list")
    ResultVO getList();
}
```

#### controller层引入

```java
@RestController
@RequestMapping("consumer")
public class ConsumerController {

    @Autowired
    private PayApi payApi;


    @GetMapping
    public ResultVO getPayList() {
        return payApi.getList();
    }
    
}
```

- 调用成功结果同上

### 超时控制

默认OpenFeign客户端==等待60秒==。

#### yml文件配置

```yml
spring:
  application:
    name: cloud-consumer-81
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
    openfeign:
      client:
        config:
          default:
            connectTimeout: 60000
          cloud-provider-payment:
            connectTimeout: 60000
```

### 重试机制

#### 添加config类

```java
@Configuration
public class FeginConfig {
    
    // http状态码500
    @Bean
    public Retryer retryer() {
        // 默认不开启，无须单独设置
//        return Retryer.NEVER_RETRY;
        
        
        // period：间隔ms，maxPeriod：最大间隔s，maxAttempts：最大重试次数
        return new Retryer.Default(100, 1, 3);
    }
}
```

### 性能优化HttpClient5

如果不做特殊配置，OpenFeign默认使用JDK自带的HttpURLConnection发送HTTP请求，由于默认的HttpURLConnection没有连接池，性能和效率比较低。

#### 引入jar

```xml
<!--        httpclient5-->
        <dependency>
            <groupId>org.apache.httpcomponents.client5</groupId>
            <artifactId>httpclient5</artifactId>
            <version>5.1.4</version>
        </dependency>
<!--        feign-hc5-->
        <dependency>
            <groupId>io.github.openfeign</groupId>
            <artifactId>feign-hc5</artifactId>
            <version>13.1</version>
        </dependency>
```

#### 修改yml文件

```yml
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
    openfeign:
      client:
        config:
          default:
            connectTimeout: 60000
          cloud-provider-payment:
            connectTimeout: 60000
      # 启用httpclient5
      httpclient:
        hc5:
          enabled: true
```

### 请求响应压缩

#### 对请求和响应进行GZIP压缩

```properties
spring.cloud.openfeign.compression.request.enabled=true
spring.cloud.openfegin.compression.response.enabled=true
```

#### 细粒度化设置

对请求压缩做一些更细致的设置，比如下面设置指定压缩的请求数据类型并设置了请求压缩的大小下限，只有操作这个大小的请求才会进行压缩。

```properties
spring.cloud.openfeign.compression.request.enabled=true
spring.cloud.openfeign.compression.request.mine-types=text/xml,application/xml,application/json #触发压缩数据类型
spring.cloud.openfeign.compression.request.min-request-size=2048 #最小触发压缩的大小
```

### 日志打印

#### 配置类

```java
@Configuration
public class FeginConfig {
    
    
    @Bean
    Logger.Level feginLogger() {
        return Logger.Level.FULL;
    }
}
```

#### yml文件配置

==logging.level==+含有@FeignClient注解的完整带包名的接口名+debug

```yml
---
logging:
  level:
    com.xhh.feign.PayApi: debug      
---
```

![image-20240422203336471](https://gitee.com/cnuto/images/raw/master/image/image-20240422203336471.png)

## Resilience4j

基于Spring Cloud Circuit Breaker实现

### Circult Breaker（服务熔断+服务降级）

“断路由”本身是一种开关装置，当某个服务单元发生故障之后，通过断路由的故障监控（类似熔断保险丝），==向调用方返回一个符合预期的，可处理的备选响应（Fallback），而不是长时间的等待或者抛出调用方无法处理的异常==，这样就保证了服务调用方的线程不会被长时间，不必要地占用，从而避免了故障在分布式系统的蔓延，最后导致雪崩。

当一个组件或服务出现故障时，CircuitBreaker会迅速切换到OPEN状态，阻止请求发送到该组件或者服务从而避免更多的请求发送。

- 断路由有三个普通状态：关闭（CLOSED），开启（OPEN），半开（HALF_OPEN），还有两个特殊状态：禁用（DISABLED），强制开启（FORCED_OPEN）
- 当熔断器关闭时，所有的请求都会通过熔断器
  - 如果失败率超过设定的阈值，熔断器就会从关闭状态转换成打开状态，这时所有的请求都会被拒绝。
  - 当经过一段时间后，熔断器会从打开状态转换到半开状态，这时仅有一定数量的请求会被放入，并重新计算失败率
  - 如果失败率超过阈值，则变为打开状态，如果失败率低于阈值，则变为关闭状态
- 断路器使用滑动窗口来存储和统计调用的结果，可以选择基于调用数量的滑动窗口或者基于时间的滑动窗口
  - 基于访问数量的滑动窗口统计了最近N次调用的返回接口，基于时间的滑动窗口统计最近N秒的调用返回结果
- DISABLED和FORCED_OPEN
  - 这两个状态不会生成熔断事件（除状态转换外），并且不会记录事件的成功和失败
  - 退出这两个状态的唯一方法是触发状态转换或者重置熔断器

#### 配置属性

![image-20240423203016747](https://gitee.com/cnuto/images/raw/master/image/image-20240423203016747.png)

#### 调用方引入jar包

```xml
        <!--引入feigin支持-->
		<dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-openfeign</artifactId>
        </dependency>
		<!--注册服务到注册中心-->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-consul-discovery</artifactId>
            <exclusions>
                <exclusion>
                    <groupId>commons-logging</groupId>
                    <artifactId>commons-logging-api</artifactId>
                </exclusion>
            </exclusions>
        </dependency>
		<dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-circuitbreaker-resilience4j</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-aop</artifactId>
        </dependency>
```

#### 调用方配置yml文件（按照次数）

```yaml
---
resilience4j:
  circuitbreaker:
    configs:
      default:
        failure-rate-threshold: 50 # 设置50%的调用失败时打开断路器，操作失败请求百分比circuitbreaker变为OPEN状态
        sliding-window-type: COUNT_BASED # 滑动窗口的类型
        sliding-window-size: 6 # 滑动窗口的大小， COUNT_BASED表示6个请求；TIME_BASED表示6秒内
        minimum-number-of-calls: 6 # 断路器计算失败率或慢调用率之前所需的最小样本（每个滑动窗口周期），如果为10，则必须记录10个样本，然后才能计算失败率，如果记录了9次调用，即使前面9次都失败，断路器也不会开启
        automatic-transition-from-open-to-half-open-enabled: true #是否启用自动从开启状态过度到半开状态，默认值为true
        wait-duration-in-open-state:
          seconds: 5 # 从OPEN状态到HALF_OPEN状态需要等待5秒
        # wait-duration-in-open-state: 5s，和上面等价，但是这个5s内调用失败但是5s之后调用会正常，上面那个5秒内再次调用一直失败，即使5s之后还是失败
        permitted-number-of-calls-in-half-open-state: 2 # 半开状态下允许的请求数量,默认值10。在半开状态下，circuitbreaker将允许2个请求通过，如果有任何一个请求失败，则circuitbreaker将再次进入OPEN状态
        record-exceptions:
          - java.lang.Exception
    instances:
    # 被调用方实例
      cloud-provider-payment:
        base-config: default
---
```

##### 调用方

```java
@RestController
@RequestMapping("consumer/circuit")
public class ConsumerCircuitController {

    @Autowired
    private PayApi payApi;

    @GetMapping("/{id}")
    @CircuitBreaker(name = "cloud-provider-payment", fallbackMethod = "myCircuitFallback")
    public ResultVO getById(@PathVariable(name = "id") Long id) {
        return payApi.getIdByCircuit(id);
    }
    
    
    // 服务降级后调用的方法
    public ResultVO myCircuitFallback(Throwable throwable) {
        return ResultVO.fail();
    }
}
```

##### 被调用方

```java
@RestController
@RequestMapping("pay/circuit")
@RequiredArgsConstructor
public class PayCircuitController {

    private final PayService payService;

    @GetMapping("list/{id}")
    public ResultVO getById(@PathVariable(name = "id") Long id) {
        ResultVO resultVO = new ResultVO();
        if (Objects.equals(id, -4)) {
            throw new RuntimeException("系统异常");
        }
        resultVO.getResult().put("pay", payService.getById(id));
        resultVO.getResult().put("from" ,  "cloud-provider-8001");
        return resultVO;
    }
}
```

##### feign

```java
@FeignClient(name = "cloud-provider-payment")
public interface PayApi {

    @RequestMapping(method = RequestMethod.GET, value = "pay/circuit/list/{id}")
    ResultVO getIdByCircuit(@PathVariable(name = "id") Long id);
}
```

##### 正常访问

![image-20240423220105437](https://gitee.com/cnuto/images/raw/master/image/image-20240423220105437.png)

##### 测试熔断

分别进行3次正常调用，3次异常调用，再次调用正常数据也返回系统繁忙

![image-20240423215502705](https://gitee.com/cnuto/images/raw/master/image/image-20240423215502705.png)

##### 测试半开状态

5秒之后调用一次正常，一次失败，再次调用正常，返回系统繁忙

![image-20240423215658229](https://gitee.com/cnuto/images/raw/master/image/image-20240423215658229.png)

#### 调用方配置yml文件（按照时间）

```yaml
resilience4j:
  timelimiter:
    configs:
      default:
        timeout-duration: 10s # timelimiter默认限制远程1s,超过1s就超时异常，配置了降级就走降级逻辑
  circuitbreaker:
    configs:
      default:
        failure-rate-threshold: 50 # 设置50%的调用失败时打开断路器，操作失败请求百分比circuitbreaker变为OPEN状态
        slow-call-duration-threshold: 2s # 慢调用时间阈值，高于此阈值视为慢调用并增加慢调用比例
        slow-call-rate-threshold: 30 # 慢调用比例阈值，慢调用比例达到阈值，circuitbreaker变为OPEN状态
        sliding-window-type: time_based # 窗口类型
        sliding-window-size: 2 # 滑动窗口大小，TIME_BASED表示统计2秒内的调用结果
        minimum-number-of-calls: 2 # 断路器计算失败率或慢调用率之前所需的最小样本
        permitted-number-of-calls-in-half-open-state: 2 # 半开状态下允许的请求数量,默认值10。在半开状态下，circuitbreaker将允许2个请求通过，如果有任何一个请求失败，则circuitbreaker将再次进入OPEN状态
        wait-duration-in-open-state: 5s # 从OPEN状态到HALF_OPEN状态需要等待5秒
        record-exceptions:
          - java.lang.Exception
    
    instances:
      cloud-provider-payment:
        base-config: default
```

##### 被调用方

```JAVA
    @GetMapping("list/{id}")
    public ResultVO getById(@PathVariable(name = "id") Long id) {
        ResultVO resultVO = new ResultVO();
        if (Objects.equals(id, -4L)) {
            throw new RuntimeException("系统异常");
        }
        
        // 增加超时时间
        if (Objects.equals(id , 9999L)) {
            try {
                Thread.sleep(5000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        resultVO.getResult().put("pay", payService.getById(id));
        resultVO.getResult().put("from" ,  "cloud-provider-8001");
        return resultVO;
    }
```

##### 测试熔断

同一时间多次调用超时接口

![image-20240424215040192](https://gitee.com/cnuto/images/raw/master/image/image-20240424215040192.png)

### BulkHead（隔离）

Resilience4j提供了两种隔离的实现方式，可以限制并发执行的数量

- SemaphoreBulkhead使用了信号量
- FixedThreadPoolBulkhead使用了有界队列和固定大小线程池

#### 引入jar包

```xml
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-circuitbreaker-resilience4j</artifactId>
        </dependency>
        <dependency>
            <groupId>io.github.resilience4j</groupId>
            <artifactId>resilience4j-bulkhead</artifactId>
        </dependency>
```

#### SemaphoreBulkhead

##### yml配置

```yaml
resilience4j:
  timelimiter:
    configs:
      default:
        timeout-duration: 10s # timelimiter默认限制远程1s,超过1s就超时异常，配置了降级就走降级逻辑
  circuitbreaker:
    configs:
      default:
        failure-rate-threshold: 50 # 设置50%的调用失败时打开断路器，操作失败请求百分比circuitbreaker变为OPEN状态
        slow-call-duration-threshold: 2s # 慢调用时间阈值，高于此阈值视为慢调用并增加慢调用比例
        slow-call-rate-threshold: 30 # 慢调用比例阈值，慢调用比例达到阈值，circuitbreaker变为OPEN状态
        sliding-window-type: time_based # 窗口类型
        sliding-window-size: 2 # 滑动窗口大小，TIME_BASED表示统计两秒内的调用结果
        minimum-number-of-calls: 2 # 断路器计算失败率或慢调用率之前所需的最小样本
        permitted-number-of-calls-in-half-open-state: 2 # 半开状态下允许的请求数量,默认值10。在半开状态下，circuitbreaker将允许2个请求通过，如果有任何一个请求失败，则circuitbreaker将再次进入OPEN状态
        wait-duration-in-open-state: 5s # 从OPEN状态到HALF_OPEN状态需要等待5秒
        record-exceptions:
          - java.lang.Exception
    
    instances:
      cloud-provider-payment:
        base-config: default
  bulkhead:
    configs:
      default:
        max-concurrent-calls: 2 # 隔离允许并发线程执行的最大数量，默认25
        max-wait-duration: 1s # 当达到并发调用数量时，线程的阻塞时间，默认0
    instances:
      cloud-provider-payment:
        base-config: default
```

##### 调用方

```java
@RestController
@RequestMapping("consumer/circuit")
public class ConsumerCircuitController {

    @Autowired
    private PayApi payApi;

    @GetMapping("/{id}")
    @CircuitBreaker(name = "cloud-provider-payment", fallbackMethod = "myCircuitFallback")
    public ResultVO getById(@PathVariable(name = "id") Long id) {
        return payApi.getIdByCircuit(id);
    }
    
    public ResultVO myCircuitFallback(Throwable throwable) {
        return ResultVO.fail();
    }


    @GetMapping("/bulkhead/{id}")
    @Bulkhead(name = "cloud-provider-payment", fallbackMethod = "myCircuitFallback", type = Bulkhead.Type.SEMAPHORE)
    public ResultVO getByBulkhead(@PathVariable(name = "id") Long id) {
        return payApi.getIdByCircuitBulkhead(id);
    }
    
}
```

##### 测试触发隔离

同时请求两次慢调用，在调用正常数据接口返回报错，由于maxConcurrentCalls=2，其他请求降级

![image-20240424222933576](https://gitee.com/cnuto/images/raw/master/image/image-20240424222933576.png)

#### FixedThreadPoolBulkhead

##### yml配置

```yaml
resilience4j:
  timelimiter:
    configs:
      default:
        timeout-duration: 10s # timelimiter默认限制远程1s,超过1s就超时异常，配置了降级就走降级逻辑
  circuitbreaker:
    configs:
      default:
        failure-rate-threshold: 50 # 设置50%的调用失败时打开断路器，操作失败请求百分比circuitbreaker变为OPEN状态
        slow-call-duration-threshold: 2s # 慢调用时间阈值，高于此阈值视为慢调用并增加慢调用比例
        slow-call-rate-threshold: 30 # 慢调用比例阈值，慢调用比例达到阈值，circuitbreaker变为OPEN状态
        sliding-window-type: time_based # 窗口类型
        sliding-window-size: 2 # 滑动窗口大小，TIME_BASED表示统计两秒内的调用结果
        minimum-number-of-calls: 2 # 断路器计算失败率或慢调用率之前所需的最小样本
        permitted-number-of-calls-in-half-open-state: 2 # 半开状态下允许的请求数量,默认值10。在半开状态下，circuitbreaker将允许2个请求通过，如果有任何一个请求失败，则circuitbreaker将再次进入OPEN状态
        wait-duration-in-open-state: 5s # 从OPEN状态到HALF_OPEN状态需要等待5秒
        record-exceptions:
          - java.lang.Exception
    
    instances:
      cloud-provider-payment:
        base-config: default
#  bulkhead:
#    configs:
#      default:
#        max-concurrent-calls: 2 # 隔离允许并发线程执行的最大数量，默认25
#        max-wait-duration: 1s # 当达到并发调用数量时，线程的阻塞时间，默认0
#    instances:
#      cloud-provider-payment:
#        base-config: default
  thread-pool-bulkhead:
    configs:
      default:
        core-thread-pool-size: 1 # 最多支持max-thread + queue-capacity个队列
        max-thread-pool-size: 1
        queue-capacity: 1
    instances:
      cloud-provider-payment:
        base-config: default
# spring.cloud.openfeign.circuitbreaker.group.enable=false，避免对线程池进行分组管理        
```

##### 调用方

```java
@RestController
@RequestMapping("consumer/circuit")
public class ConsumerCircuitController {

    @Autowired
    private PayApi payApi;



    @GetMapping("/bulkhead/{id}")
    @Bulkhead(name = "cloud-provider-payment", fallbackMethod = "myCircuitFallback", type = Bulkhead.Type.SEMAPHORE)
    public ResultVO getByBulkhead(@PathVariable(name = "id") Long id) {
        return payApi.getIdByCircuitBulkhead(id);
    }

    // 需要返回CompletableFuture
    @GetMapping("/bulkhead/threadPool/{id}")
    @Bulkhead(name = "cloud-provider-payment", fallbackMethod = "myCircuitFallbackFuture", type = Bulkhead.Type.THREADPOOL)
    public CompletableFuture<ResultVO> getByBulkheadThreadPool(@PathVariable(name = "id") Long id) {
        return CompletableFuture.supplyAsync(() -> {
            return payApi.getIdByCircuitBulkhead(id);
        });
    }

    public CompletableFuture<ResultVO> myCircuitFallbackFuture(Throwable throwable) {
        return CompletableFuture.supplyAsync(ResultVO::fail);
    }
}
```

##### 注意点

- 参数id的值不同才会触发，相同请求共享一个核心线程池
-  spring.cloud.openfeign.circuitbreaker.group.enable=false或者不进行设置，避免对线程池进行分组管理  

### Ratelimiter（限流器）

限流算法

#### 漏斗算法（Leaky Bucket）

一个固定容量的漏桶，按照设定常量固定速率流出水滴，类似医院打吊针。如果流入水滴超出了桶的容量，则流入的水滴将会溢出（被丢弃），而漏桶的容量是不变的。

缺点：桶的大小（burst），漏洞的大小（rate）两个参数，其中rate是固定参数，==对于存在突发特性的流量来说缺乏效率==。

![image-20240425203636627](https://gitee.com/cnuto/images/raw/master/image/image-20240425203636627.png)

#### ==令牌桶算法（Token Bucket）(SpringCloud默认算法)==

#### ![image-20240425204304791](https://gitee.com/cnuto/images/raw/master/image/image-20240425204304791.png)

#### 滚动时间窗（tumbling time window）

允许固定数量的请求进入（比如1秒内允许4次请求），超过数量执行排队或者拒绝策略，等下一段时间进入。

缺点：间隔临界时间的请求可能会超过系统限制，导致系统崩溃

#### 滑动时间窗口（sliding time window）

把固定时间片进行划分并且随着时间的移动，移动方式为开始时间点变为时间列表中的第2个时间点，结束时间点增加一个时间点，不断重复，通过这种方式避开计数器临界点问题。

#### 调用方引入jar包

```xml
        <dependency>
            <groupId>io.github.resilience4j</groupId>
            <artifactId>resilience4j-ratelimiter</artifactId>
        </dependency>
```

#### yml文件配置

```yaml
resilience4j:
  ratelimiter:
    configs:
      default:
        limit-for-period: 2 # 在一次刷新周期内，允许执行的最大请求数
        limit-refresh-period: 1s # 限流器每隔limit-refresh-period刷新一次，将允许处理的最大请求数量重置为limit-for-period
        timeout-duration: 1 #线程等待权限的默认等待时间
    instances:
      cloud-provider-payment:
        base-config: default
```

#### 调用方代码

```java
@RestController
@RequestMapping("consumer/circuit")
public class ConsumerCircuitController {

    @Autowired
    private PayApi payApi;
    
    public ResultVO myCircuitFallback(Throwable throwable) {
        return ResultVO.fail();
    }


    @GetMapping("/rateLimit/{id}")
    @RateLimiter(name = "cloud-provider-payment", fallbackMethod = "myCircuitFallback")
    public ResultVO getByRateLimit(@PathVariable(name = "id") Long id) {
        return payApi.getIdByRateLimit(id);
    }
```

#### 验证

1s内点击超过2次接口，触发限流

## Micrometer+ZipKin（分布式链路追踪搜集+展示）

一条链路通过TraceId唯一标识，Span标识发起的请求信息，各Span通过parent id 关联

![image-20240428154324964](https://gitee.com/cnuto/images/raw/master/image/image-20240428154324964.png)

## GateWay（网关）

GateWay是spring生态系统之上构建的API网关服务，旨在为微服务架构提供一种简单有效的==统一的API路由管理方式==。

![image-20240428212406341](https://gitee.com/cnuto/images/raw/master/image/image-20240428212406341.png)

- Spring Cloud GateWay组件的核心是一系列的过滤器，通过这些过滤器可以将客户端发送的请求转发（路由）到对应的微服务。
- Spring Cloud GateWay是加在整个微服务最前沿的防火墙和代理器，隐藏微服务结点IP端口信息，从而加强安全保护。
- Spring Cloud GateWay本身也是一个微服务，需要注册到服务注册中心。

![image-20240428212722024](https://gitee.com/cnuto/images/raw/master/image/image-20240428212722024.png)

### 三大核心组件

#### Route（路由）

路由是构建网关的基本模块，它由ID，目标URL，一系列的断言和过滤器组成，如果断言为true，则匹配该路由

#### Predicate（断言）

匹配HTTP请求中的所有内容（例如请求头或者请求参数），如果请求与断言

#### Filter（过滤）

使用过滤器可以在请求被路由前或者之后进行修改

### 工作流程

![image-20240428214257582](https://gitee.com/cnuto/images/raw/master/image/image-20240428214257582.png)

客户端向GateWay发送请求，然后在GateWay Handler Mapping中找到与请求相匹配的路由，将其发送到GateWay Web Handler。Handler再通过指定的过滤器链来将请求发送到我们实际的服务执行业务逻辑，然后返回。过滤器之间使用虚线是因为过滤器可能会在发送代理请求前或者之后执行逻辑。

在Pre类型的过滤器可以做参数校验，权限校验，流量监控，日志输出，协议转换等

在Post类型的过滤器可以做响应内容，响应头修改，日志输出，流量监控

### 配置

#### 引入jar包（创建新项目gateway）

```xml
    <dependencies>
        <dependency>
            <groupId>com.xhh</groupId>
            <artifactId>cloud-common</artifactId>
            <version>0.0.1-SNAPSHOT</version>
        </dependency>
        
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-actuator</artifactId>
        </dependency>
        
        <dependency>
            <groupId>cn.hutool</groupId>
            <artifactId>hutool-all</artifactId>
        </dependency>
        <dependency>
            <groupId>com.alibaba.fastjson2</groupId>
            <artifactId>fastjson2</artifactId>
        </dependency>
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <!--将 scope 设置为 provided 的依赖不会参与项目的war打包。假如打包为jar，设置与不设置provided并不会影响maven将依赖打包到jar当中-->
            <scope>provided</scope>
        </dependency>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-consul-discovery</artifactId>
        </dependency>

        <!--网关是响应式编程删除spring-boot-starter-web，引入gateway使用netty作为启动项目的容器-->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-gateway</artifactId>
        </dependency>
    </dependencies>
```

#### yml文件配置

```yaml
server:
  port: 9527
---
spring:
  application:
    name: cloud-gateway
  cloud:
    consul:
      host: localhost
      port: 8500
      discovery:
        prefer-ip-address: true
        service-name: ${spring.application.name}
    gateway:
      routes:
        - id: pay_routh1 # 路由的id，没有固定规则但是要求尽量统一
          uri: lb://cloud-provider-payment # 匹配后提供服务的路由地址
          predicates:
            - Path=/pay/gateway/list/** # 断言，路径相匹配的才会被路由
        - id: pay_routh2 # 路由的id，没有固定规则但是要求尽量统一
          uri: lb://cloud-provider-payment # 匹配后提供服务的路由地址,lb:loadbalancer用于指示该路由的目标服务采用负载均衡的方式进行请求转发
          predicates:
            - Path=/pay/gateway/info/** # 断言，路径相匹配的才会被路由
```

#### 被调用方代码

```java
@RestController
@RequestMapping("pay/gateway")
@RequiredArgsConstructor
public class PayGateWayController {


    private final PayService payService;

    @GetMapping("list")
    public ResultVO getPayList(@RequestParam(value = "searchValue", required = false) String searchValue, @RequestParam(value = "p", required = false, defaultValue = "1") int p, @RequestParam(value = "s", required = false, defaultValue = "10") int s) {
        ResultVO resultVO = new ResultVO();
        resultVO.setResult(payService.getPayList(searchValue, p - 1, s));
        resultVO.setMsg("from" +  "cloud-gateway-provider-8001");
        return resultVO;
    }


    @GetMapping("info")
    public ResultVO getInfo(@RequestParam(value = "searchValue", required = false) String searchValue, @RequestParam(value = "p", required = false, defaultValue = "1") int p, @RequestParam(value = "s", required = false, defaultValue = "10") int s) {
        ResultVO resultVO = new ResultVO();
        resultVO.getResult().put("id", IdUtil.fastUUID());
        return resultVO;
    }
}
```

#### 验证

调用http://localhost:9527/pay/gateway/info

![image-20240428231541130](https://gitee.com/cnuto/images/raw/master/image/image-20240428231541130.png)

### Predicate

#### After

#### Between

#### Cookie

```yaml
          predicates:
            - Path=/pay/gateway/info/** # 断言，路径相匹配的才会被路由
            - Cookie=username,xhh #正则表达式 key=username,value=xhh
```

![image-20240430220455647](https://gitee.com/cnuto/images/raw/master/image/image-20240430220455647.png)

#### Header

```yaml
- Header=X-Request-Id, \d+ # 正则表达式，key=X-Request-Id,value必须是数字
```

![image-20240430221138709](https://gitee.com/cnuto/images/raw/master/image/image-20240430221138709.png)

#### Host

```yaml
- Host=**.xhh.com,**.baidu.com
```

![image-20240430221620739](https://gitee.com/cnuto/images/raw/master/image/image-20240430221620739.png)

#### Query

配置请求必须携带的参数

#### RemoteAddr

限制请求的地址

#### Method

配置请求的方式：POST, GET

### 自定义Predicate

#### 配置RoutePredicateFactory

```java
@Component
// 配置必须是以RoutePredicateFactory结尾
public class MyRoutePredicateFactory extends AbstractRoutePredicateFactory<MyRoutePredicateFactory.Config> {


    public MyRoutePredicateFactory() {
        super(Config.class);
    }

    // 使配置支持短配置- My=xhh
    @Override
    public List<String> shortcutFieldOrder() {
        return Collections.singletonList("name");
    }

    @Override
    public Predicate<ServerWebExchange> apply(Config config) {
        return new GatewayPredicate() {
            @Override
            public boolean test(ServerWebExchange serverWebExchange) {
                String name = serverWebExchange.getRequest().getQueryParams().getFirst("name");
                if (!StringUtils.hasLength(name)) {
                    return false;
                }
                
                if (Objects.equals(name, config.getName())) {
                    return true;
                }
                return false;
            }
        };
    }

    
    @Getter
    @Validated
    public static class Config {
        @NotNull
        @Setter
        private String name;
    }
}
```

#### yml文件配置

```yaml
    gateway:
      routes:
        - id: pay_routh1 # 路由的id，没有固定规则但是要求尽量统一
          uri: lb://cloud-provider-payment # 匹配后提供服务的路由地址,lb:loadbalancer用于指示该路由的目标服务采用负载均衡的方式进行请求转发
          predicates:
            - Path=/pay/gateway/list/** # 断言，路径相匹配的才会被路由
        - id: pay_routh2 # 路由的id，没有固定规则但是要求尽量统一
          uri: lb://cloud-provider-payment # 匹配后提供服务的路由地址
          predicates:
            - Path=/pay/gateway/info/** # 断言，路径相匹配的才会被路由
              # 如果MyRoutePredicateFactory没有重写shortcutFieldOrder，那么需要使用这种方式填写
#            - name: My
#              args:
#                name: xhh
            # 判断两次，先判断xhh，如果相等返回true，在判断jy
            - My=xhh
            - My=jy
```

### 自定义Filter

- 全局默认过滤器Global Filters，gateway出厂默认，主要作用于所有的路由，不需要在配置文件中配置，作用在所有的路由上，实现GlobalFilter接口即可
- 单一内置过滤器GatewayFilter，网关过滤器，主要用于单一路由或者某个路由分组

#### 全局过滤器

```java
@Component
@Slf4j
public class MyGlobalFilter implements GlobalFilter, Ordered {

    public static final String BEGIN_TIME = "begin_Time";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        exchange.getAttributes().put(BEGIN_TIME, System.currentTimeMillis());
        return chain.filter(exchange).then(Mono.fromRunnable(() -> {
            Long beginTime = exchange.getAttribute(BEGIN_TIME);
            if (beginTime != null) {
                log.info("访问主机:{}", exchange.getRequest().getURI().getHost());
                log.info("访问端口:{}", exchange.getRequest().getURI().getPort());
                log.info("访问接口url:{}", exchange.getRequest().getURI().getPath());
                log.info("访问参数:{}", exchange.getRequest().getQueryParams());
                log.info("访问耗时:{}", System.currentTimeMillis() - beginTime);
            }
        }));
    }

    /**
     * 数字越小，优先级越高
     *
     * @return
     */
    @Override
    public int getOrder() {
        return 0;
    }
}
```

#### 自定义过滤器

```java
@Component
@Slf4j
/**
 * 自定义过滤器，必须以GatewayFillterFacotry结尾
 */
public class MyGatewayFilterFactory extends AbstractGatewayFilterFactory<MyGatewayFilterFactory.Config> {
    
    MyGatewayFilterFactory() {
        super(Config.class);
    }
    
    @Override
    public List<String> shortcutFieldOrder() {
        return Collections.singletonList("status");
    }

    @Override
    public GatewayFilter apply(MyGatewayFilterFactory.Config config) {
        return new GatewayFilter() {
            @Override
            public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
                ServerHttpRequest request = exchange.getRequest();
                log.info("get status:{}", config.getStatus());
                if (request.getQueryParams().containsKey("xhh")) {
                    return chain.filter(exchange);
                } else {
                    exchange.getResponse().setStatusCode(HttpStatus.BAD_REQUEST);
                    return exchange.getResponse().setComplete();
                }
            }
        };
    }
    
    public static class Config {
        @Setter
        @Getter
        private Integer status;
    }
}
```

#### 自定义filter的yml文件配置

```yaml
          filters:
            - My=1    
```

# SpringCloudAlibaba

## Nacos

### 引入Jar包

```xml
        <dependency>
            <groupId>com.alibaba.cloud</groupId>
            <artifactId>spring-cloud-starter-alibaba-nacos-discovery</artifactId>
        </dependency>
```

### 配置yml文件

```yaml
spring:
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3306/mall?characterEncoding=utf-8&useSSL=false&serverTimezone=GMT%2B8&rewriteBatchedStatements=true&allowPublicKeyRetrieval=true
    username: root
    password: xhh1999.02.10
    type: com.alibaba.druid.pool.DruidDataSource
#  profiles:
#    active: de
  application:
    name: cloud-consumer-81
  cloud:
    nacos:
      discovery:
        server-addr: localhost:8848
        service: ${spring.application.name}
        group: pay # 调用方和被调用方需要在同一个组
        namespace: 5edeff2b-485b-4858-9fdd-5cc28e6ce47d # 同一个命名空间
```

### 使用FeignClient注意点

==特别注意：和consul不同的是，consul包含了loadbalancer包，不需要显示引入，但是使用Nacos+openFeign需要显式引入loadbalancer==

```xml
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-loadbalancer</artifactId>
        </dependency>
```

### namespace-group-dataId

nacos数据模型Key由三元组唯一确定，Namespace默认是public，分组默认是DEFAULT_GROUP。

![image-20240508223140543](https://gitee.com/cnuto/images/raw/master/image/image-20240508223140543.png)

- 类似于Java中的包名和类名，最外层的Namespace可以用来区分部署环境，Group和DataID逻辑上区分两个目标对象；

- ==默认情况下，Namespace=public, Group=DEFAULT_GROUP==

## Sentinel

### 安装

下载jar包启动

```shell
java -jar sentinel-dashboard-2.0.0-alpha-preview.jar
# 默认端口8080
```

#### 引入jar包

```xml
        <dependency>
            <groupId>com.alibaba.cloud</groupId>
            <artifactId>spring-cloud-starter-alibaba-sentinel</artifactId>
        </dependency>
```

#### 编写controller接口

```java
@RestController
public class FlowLimitController {

    @GetMapping("/testA")
    public String testA() {
        return "testA";
    }

    @GetMapping("/testB")
    public String testB() {
        return "testB";
    }

    @GetMapping("/testC")
    public String testC() {
        return "testC";
    }
}
```



#### 配置yml文件

```yaml
server:
  port: 8401
---
spring:
  application:
    name: cloudalibaba-setinel-8401
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3306/mall?characterEncoding=utf-8&useSSL=false&serverTimezone=GMT%2B8&rewriteBatchedStatements=true&allowPublicKeyRetrieval=true
    username: root
    password: xhh1999.02.10
    type: com.alibaba.druid.pool.DruidDataSource
  cloud:
    nacos:
      discovery:
        server-addr: localhost:8848 # nacos注册中心地址
        service: cloudalibaba-setinel
        group: pay # 同一个组
        namespace: 5edeff2b-485b-4858-9fdd-5cc28e6ce47d # 同一个命名空间
    sentinel:
      transport:
        dashboard: localhost:8080 # sentinel控制台地址
        port: 8719 # 默认8719端口，加入被占用会自动从8719开始依次加1，直至找到未被占用的端口
```

#### 验证

==由于sentinel采用懒加载，需要在注册的项目上调用接口才会显示==

![image-20240509212433018](https://gitee.com/cnuto/images/raw/master/image/image-20240509212433018.png)

### 流控模式

Sentinel能够对流量进行控制，主要是监控应用的QPS流量或者并发线程数等指标，如果达到指定的阈值，就会被流量进行控制，以避免服务被瞬时的高并发流量击垮，保证服务的高可靠性。

#### 直连

==默认的流控模式，当接口达到限流条件时，直接开启限流功能。==

![image-20240509222306919](https://gitee.com/cnuto/images/raw/master/image/image-20240509222306919.png)

![image-20240509222242303](https://gitee.com/cnuto/images/raw/master/image/image-20240509222242303.png)

#### 关联

当关联的资源达到阈值时，就限流自己；当与A关联的资源B达到阈值后，就限流自己

![image-20240509222159910](https://gitee.com/cnuto/images/raw/master/image/image-20240509222159910.png)

Jmiter不停调用/testB，触发Block

#### 链路

来自不同链路的请求对同一个目标访问时，实施针对性的不同限流措施，比如C请求来访问就限流，D请求来访问就是OK

- 创建service

```java
@Service
public class FlowLimitService {
    
    @SentinelResource(value = "common")
    public void common() {
        System.out.println("--------FlowLimitService come in");
    }
}
```

- 创建controller

```java
    @GetMapping("/testC")
    public String testC() {
        flowLimitService.common();
        return "testC";
    }

    @GetMapping("/testD")
    public String testD() {
        flowLimitService.common();
        return "testD";
    }
```

- 配置

![image-20240509232105702](https://gitee.com/cnuto/images/raw/master/image/image-20240509232105702.png)

- 当并发调用C时，触发限流；当并发调用D时，不触发限流。

### 流控效果

#### 快速失败（默认流控处理）

#### 预热（WarmUp）

冷启动：当系统长期处于低水位情况下，当流量突然增加时，直接把系统拉升到高水位可能瞬间把系统压垮，通过冷启动，让通过的流量缓慢增加，在一定时间内组件增加到阈值上限，给冷系统一个预热的时间，避免系统被压垮；公式：阈值除以冷却因子coldFactor（默认值3），经过预热时长后才会达到阈值。

案例：单机阈值为10，预热时长设置5秒。系统初始化的阈值为10/3约等于3，即单机阈值刚开始为3（人工设置单机阈值为10，sentinel计算后QPS判定为3开始），过了5秒后阈值才慢慢升高恢复到设置的单机阈值，也就是说5秒种内QPS为3，过了保护期5秒后QPS为10

![image-20240510212217571](https://gitee.com/cnuto/images/raw/master/image/image-20240510212217571.png)

验证：

一直点击/testB，3次后触发流控，还是一直点击，5秒后此时又恢复正常

![image-20240510214428104](https://gitee.com/cnuto/images/raw/master/image/image-20240510214428104.png)

#### 排队等待

![image-20240510215135794](https://gitee.com/cnuto/images/raw/master/image/image-20240510215135794.png)