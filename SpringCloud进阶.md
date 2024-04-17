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