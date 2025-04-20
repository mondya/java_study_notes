# SpringBoot进阶

## @Configuration注解

- `proxyBeanMethods`：代理bean中的方法，默认开启。
  - Full(proxyBeanMethods=true)：保证每个@Bean对象被调用多次返回的实例都是单例的。
  - Lite(proxyBeanMethods=false)：每个@Bean方法被调用多次返回的实例都是不相同的。

## @ImportResource注解

作用：==导入Spring的XML配置文件==

和`@Import`的区别：`@Import`注解是为了引入java类

## @Import导入注解

### Import导入

```java
public class Blue {
}
 
public class Yellow {
}

@Import({Blue.class, Yellow.class})
@Configuration
public class BeanConfig { 
}
```

```java
public class ImportTest {
 
    private static AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(BeanConfig.class);
 
    @Test
    public void test1(){
 
        String[] beanDefinitionNames = applicationContext.getBeanDefinitionNames();
        for (String beanDefinitionName : beanDefinitionNames) {
            System.out.println(beanDefinitionName);
        }
    }
}
```

打印结果：==beanId为类名的全路径==

```java
com.xhh.smalldemobackend.Blud
com.xhh.smalldemobackend.Yellow
```

### ImportSelector

```java
/**
 * 自定义逻辑返回需要导入的组件
 * @date: 2021/2/25 18:15
 */
public class MyImportSelector implements ImportSelector {
 
    /**
     * 导入容器集合
     * @param importingClassMetadata 当前标注@Import注解的类的所有注解信息
     * @date: 2021/2/25 18:15
     * @return: java.lang.String[] 返回值，就是到导入到容器中的组件全类名
     */
    public String[] selectImports(AnnotationMetadata importingClassMetadata) {
 
        return new String[]{"com.xhh.smalldemobackend.Red","com.xhh.smalldemobackend.Green"};
    }
}
```

bean配置类中导入

```java
@Import({Blue.class, Yellow.class,MyImportSelector.class})
@Configuration
public class BeanConfig {
 
}
```

打印结果：

```java
com.xhh.smalldemobackend.Blud
com.xhh.smalldemobackend.Yellow
com.xhh.smalldemobackend.Red
com.xhh.smalldemobackend.Green
```

### ImportBeanDefinitionRegistrar

`ImportBeanDefinitionRegistrar`与`ImportSelector`的使用大致相同，它是接口，我们只需实现它，并将它作为参数，放在@Import中即可。

```java
public class MyImportBeanDefinitionRegistrar implements ImportBeanDefinitionRegistrar {
 
    /**
     * AnnotationMetadata：当前类的注解信息
     * BeanDefinitionRegistry: BeanDefinition注册类；
     * 		把所有需要添加到容器中的bean；调用
     * 		BeanDefinitionRegistry.registerBeanDefinition手工注册进来
     */
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
 
        boolean definition = registry.containsBeanDefinition("com.xhh.smalldemobackend.Red");
        boolean definition2 = registry.containsBeanDefinition("com.xhh.smalldemobackend.Blue");
        if(definition && definition2){
            //指定Bean定义信息；（Bean的类型，Bean。。。）
            RootBeanDefinition beanDefinition = new RootBeanDefinition(RainBow.class);
            //注册一个Bean，指定bean名
            registry.registerBeanDefinition("rainBow", beanDefinition);
        }
 
    }
}
```

配置类中导入

```java
@Import({Blue.class, Yellow.class,MyImportSelector.class,MyImportBeanDefinitionRegistrar.class})
@Configuration
public class BeanConfig {
}
```

控制台打印：

```java
com.xhh.smalldemobackend.Blud
com.xhh.smalldemobackend.Yellow
com.xhh.smalldemobackend.Red
com.xhh.smalldemobackend.Green
rainBow
```

## @Conditional条件注解

### @ConditionalOnProperty

application.properties 或 application.yml 文件中 mybean.enable 为 true 才会加载 MyCondition 这个 Bean，如果没有匹配上也会加载，因为 matchIfMissing = true，默认值是 false。

### @ConditionalOnBean 和 ConditionalOnMissingBean

两个作用：根据当前环境或者容器情况来动态注入bean，要配合@Bean使用

- `@ConditionalOnMissingBean`作用：==当某个特定类型的Bean没有在容器中注册时，才会创建并注册指定的Bean==。
  - 当使用`@ConditionalOnMissingBean`注解在一个Bean的声明上时，Spring Boot会检查容器中是否已经存在该类型的Bean。
  - 如果容器中不存在该类型的Bean，则Spring Boot会创建并注册被注解的Bean。
  - 如果容器中已经存在该类型的Bean，则被注解的Bean不会被创建和注册，因为已经有其他Bean提供了相同的功能。
- `@ConditionalOnBean`作用：==用于在 Spring 容器中存在指定类型的 Bean 时才会生效==
  - 当使用 `@ConditionalOnBean` 注解在某个配置类或 Bean 的声明上时，Spring Boot 会检查 Spring 容器中是否存在指定类型的 Bean。
  - 如果 Spring 容器中存在指定类型的 Bean，则被注解的配置类或 Bean 才会生效，也就是说相关的配置或 Bean 会被加载进 Spring 容器。
  - 如果 Spring 容器中不存在指定类型的 Bean，则被注解的配置类或 Bean 不会生效，相关的配置或 Bean 不会被加载进 Spring 容器。

### @ConditionalOnClass 和 @ConditionalOnMissingClass

- `@ConditionalOnClass`：==用于在特定类存在于 Classpath（类路径）上时，才会生效==

  - 当使用 `@ConditionalOnClass` 注解在某个配置类或 Bean 的声明上时，Spring Boot 会检查 Classpath（类路径）上是否存在该注解中指定的类。
  - 如果 Classpath 上存在该类，则被注解的配置类或 Bean 才会生效，也就是说相关的配置或 Bean 会被加载进 Spring 容器。
  - 如果 Classpath 上不存在该类，则被注解的配置类或 Bean 不会生效，相关的配置或 Bean 不会被加载进 Spring 容器。

  

- `@ConditionalOnMissingClass`：==用于在特定类不存在于 Classpath（类路径）上时，才会生效==

  - 当使用 `@ConditionalOnMissingClass` 注解在某个配置类或 Bean 的声明上时，Spring Boot 会检查 Classpath（类路径）上是否不存在该注解中指定的类。

  - 如果 Classpath 上不存在该指定的类，那么被注解的配置类或 Bean 就会生效，即相关的配置或 Bean 会被加载进 Spring 容器。这可以用于在某些依赖库未引入时，提供默认的配置或 Bean 实现。

  - 如果 Classpath 上存在该指定的类，那么被注解的配置类或 Bean 不会生效，相关的配置或 Bean 不会被加载进 Spring 容器。这种情况下，可能会有其他基于 `@ConditionalOnClass` 等注解配置的类或 Bean 发挥作用。

## @CongigurationProperties配置绑定

```yml
spring:
	car:
		brand: byd
		price: 10
```

```java
// 只有被spring管理的bean才能注入属性
@Component
@Data
@ConfigurationProperties(prefix = "spring.car")
public class Car {
  private String brand;
  private Integer price;
}

// 第二种情况，当Car不被spring管理时，需要使用EnableConfigurationProperties(Car.class)
// 开启Car类的属性绑定；把Car注册到Spring容器中
```

## 自动配置原理

### @SpringBootApplication注解

```java
@SpringBootConfiguration
@EnableAutoConfiguration
@ComponentScan(
    excludeFilters = {@Filter(
    type = FilterType.CUSTOM,
    classes = {TypeExcludeFilter.class}
), @Filter(
    type = FilterType.CUSTOM,
    classes = {AutoConfigurationExcludeFilter.class}
)}
)
```

#### @SpringBootConfiguration注解

==被@Configuration注解修饰，代表这个一个配置类==

#### @ComponentScan

==Spring 会扫描指定包及其子包下的所有类，将带有 `@Component`、`@Service`、`@Repository`、`@Controller` 等注解的类自动注册为 Spring Bean，目的是为了把开发者编写的业务类注册到==

`TypeExcludeFilter` 是 Spring Boot 提供的一个自定义过滤器，主要用于在测试场景中排除特定类型的组件。在生产环境的主启动类里，一般不需要这些测试相关的类型被扫描到，所以排除 `TypeExcludeFilter` 可以减少不必要的扫描，提高应用启动速度。

`AutoConfigurationExcludeFilter` 用于排除自动配置类。在某些情况下，Spring Boot 的自动配置可能不符合你的需求，你可能已经手动配置了某些组件，或者不想让某些自动配置生效。排除 `AutoConfigurationExcludeFilter` 可以让你更精细地控制哪些自动配置类会被加载。

#### @EableAutoConfiguration

```java
@AutoConfigurationPackage
@Import(AutoConfigurationImportSelector.class)
public @interface EnableAutoConfiguration {
```

##### @AutoConfigurationPackage

```java
@Import(AutoConfigurationPackages.Registrar.class)
public @interface AutoConfigurationPackage {
    
// 利用Registrar给容器导入一系列组件
// 将指定包下的所有组件导入spring容器
```

![image-20250413185115703](https://gitee.com/cnuto/images/raw/master/image/image-20250413185115703.png)

##### @AutoConfigurationPackage和@ComponentScan的区别

`@AutoConfigurationPackage`在默认的情况下是将：主配置类（`@SpringBootApplication`）的所在包及其子包里边的组件扫描到Spring容器中。比如说，你使用了Spring Data JPA，可能会在实体类上使用`@Entity`注解。这个注解由`@AutoConfigurationPackage`扫描加载，而我们开发常用的`@Controller`，`@Service`，`@Component`，`@Repository`这些注解由`@ComponentScan`来扫描加载。

https://blog.itpub.net/70024922/viewspace-2953012/

总结：

- 两者都是用来扫描Bean的。
- `@Component`用来扫描和Spring容器相关的Bean
- `@AutoConfigurationPackage`用来扫描第三方的Bean。比如Mybatis的Mapper，除了使用`@MapperScan`扫描之外，使用`@AutoConfigurationPackage`扫描也是生效的。

##### @Import(AutoConfigurationImportSelector.class)

```java
// 利用
```

