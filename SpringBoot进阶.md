# SpringBoot进阶

## @Configuration注解

- `proxyBeanMethods`：代理bean中的方法，默认开启。
  - Full(proxyBeanMethods=true)：保证每个@Bean对象被调用多次返回的实例都是单例的。
  - Lite(proxyBeanMethods=false)：每个@Bean方法被调用多次返回的实例都是不相同的。

## @Import导入注解



## @Conditional条件注解

## @ImportResource导入spring配置文件

## @CongigurationProperties配置绑定