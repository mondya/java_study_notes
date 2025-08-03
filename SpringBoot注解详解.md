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

==被@Configuration注解修饰，代表这个一个配置类（类似@Configuration注解）==

#### @ComponentScan

==Spring 会扫描当前包、指定包及其子包下的所有类，将带有 `@Component`、`@Service`、`@Repository`、`@Controller` 等注解的类自动注册为 Spring Bean，目的是为了把开发者编写的业务类注册到容器==

`TypeExcludeFilter` 是 Spring Boot 提供的一个自定义过滤器，主要用于在测试场景中排除特定类型的组件。在生产环境的主启动类里，一般不需要这些测试相关的类型被扫描到，所以排除 `TypeExcludeFilter` 可以减少不必要的扫描，提高应用启动速度。

`AutoConfigurationExcludeFilter` 用于排除自动配置类。在某些情况下，Spring Boot 的自动配置可能不符合你的需求，你可能已经手动配置了某些组件，或者不想让某些自动配置生效。排除 `AutoConfigurationExcludeFilter` 可以让你更精细地控制哪些自动配置类会被加载。

#### @EableAutoConfiguration（自动装配的核心开关，出发自动配置逻辑）

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

##### @Import(AutoConfigurationImportSelector.class)（负责加载并筛选自动配置类）

```java
public String[] selectImports(AnnotationMetadata metadata) {
    // 1. 检查是否开启自动配置（默认开启）
    if (!isEnabled(metadata)) {
        return NO_IMPORTS;
    }
    // 2. 加载自动配置元数据（条件判断的依据）
    AutoConfigurationMetadata autoConfigurationMetadata = AutoConfigurationMetadataLoader.loadMetadata(this.beanClassLoader);
    // 3. 获取候选的自动配置类列表
    // getCandidateConfigurations通过SPI机制加载预定义的自动配置类。在springboot2.7中，自动装配的位置有两个：
    // 1.SpringBoot核心包（如spring-boot-autoconfigure）中，通过文件META-INF/spring/org.springframework.boot.autoconfigure.AUtoConfiguration.imports。已废弃旧版(META/spring.factories)
    // 2.自定义Starter中的自动配置类。第三方 Starter（如 mybatis-spring-boot-starter）需在自己的 JAR 包中创建 META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports 文件，声明其自动配置类，Spring Boot 会自动扫描并加载。
    List<String> configurations = getCandidateConfigurations(metadata, attributes);
    // 4. 去重、排除用户指定的不需要的配置类
    configurations = removeDuplicates(configurations);
    Set<String> exclusions = getExclusions(metadata, attributes);
    checkExcludedClasses(configurations, exclusions);
    configurations.removeAll(exclusions);
    // 5. 根据条件注解过滤（核心步骤）
    configurations = filter(configurations, autoConfigurationMetadata);
    // 6. 触发自动配置导入事件（供外部扩展）
    fireAutoConfigurationImportEvents(configurations, exclusions);
    return StringUtils.toStringArray(configurations);
}
```

![image-20250803184058209](https://gitee.com/cnuto/images/raw/master/image/image-20250803184058209.png)

##### @AutoConfigurationPackage和@ComponentScan的区别

`@AutoConfigurationPackage` 和 `@ComponentScan` 都是 Spring 中用于**指定 Bean 扫描范围**的注解，但它们的设计目的和作用场景有显著区别，主要体现在扫描目标和使用场景上。

###### @ComponentScan：扫描用户自定义组件

`@ComponentScan` 是 Spring 框架的核心注解，用于**扫描并注册用户编写的组件**（如 `@Component`、`@Service`、`@Controller`、`@Repository` 等注解标记的类）。

###### 核心特性

- **扫描目标**：用户代码中标记了 Spring 组件注解的类。
- **默认行为**：若未指定 `basePackages` 或 `basePackageClasses`，默认扫描当前标注该注解的类所在的包及其子包 **。
- **典型场景**：在 Spring 应用中手动指定需要扫描的包，确保自定义的 Service、Controller 等被 Spring 容器管理。

###### **示例**

```java
// 扫描 com.example.demo 包及其子包下的所有组件
@ComponentScan(basePackages = "com.example.demo")
@Configuration
public class AppConfig {
}
```

###### @AutoConfigurationPackage：扫描自动配置相关的组件

`@AutoConfigurationPackage` 是 Spring Boot 引入的注解，专门用于**标记自动配置类的 “默认包”**，主要服务于 Spring Boot 的自动装配机制。

###### 核心特性

- **扫描目标**：通常用于注册**自动配置类、实体类（如 JPA 实体）、配置属性类**等与自动装配相关的组件。
- **默认行为**：将**标注该注解的类所在的包**注册为 “默认包”，Spring Boot 会自动扫描该包下的特定组件（如 `@Entity` 实体类）。
- **底层实现**：通过 `@Import(AutoConfigurationPackages.Registrar.class)` 向 Spring 容器注册一个 `BasePackages` bean，记录包路径，供自动配置类（如 JPA、MyBatis 等）扫描使用。

###### 示例

Spring Boot 主类中，`@SpringBootApplication` 间接包含 `@AutoConfigurationPackage`：

```java
@SpringBootApplication // 隐含 @AutoConfigurationPackage
public class DemoApplication {
    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }
}
// 此时，DemoApplication 所在的包（如 com.example.demo）会被注册为自动配置的默认包
```

###### 核心区别对比

| 维度           | `@ComponentScan`                             | `@AutoConfigurationPackage`               |
| -------------- | -------------------------------------------- | ----------------------------------------- |
| **设计目的**   | 扫描用户自定义组件（Service、Controller 等） | 标记自动配置的基础包，服务于自动装配机制  |
| **扫描目标**   | 带 `@Component` 等注解的类                   | 自动配置相关的组件（如实体类、配置类）    |
| **依赖场景**   | 通用 Spring 应用                             | 仅 Spring Boot 自动装配场景               |
| **默认包范围** | 注解所在类的包及其子包                       | 注解所在类的包（通常是主类所在包）        |
| **典型使用者** | 开发者手动指定扫描范围                       | Spring Boot 自动配置类（如 JPA 自动配置） |

###### 协同工作场景

在 Spring Boot 应用中，两者通常**配合使用**：



- `@ComponentScan`（通过 `@SpringBootApplication` 隐含）负责扫描用户编写的 `@Service`、`@Controller` 等组件。
- `@AutoConfigurationPackage`（通过 `@SpringBootApplication` 隐含）负责标记默认包，供自动配置类（如 JPA 的 `EntityScan`）扫描实体类等特殊组件。



例如，JPA 的自动配置会使用 `@AutoConfigurationPackage` 注册的包路径，自动扫描该包下的 `@Entity` 实体类，无需开发者开发者手动指定 `@EntityScan`。

###### 总结

- `@ComponentScan` 是 “通用扫描工具”，用于注册用户自定义组件。
- `@AutoConfigurationPackage` 是 “自动配置的辅助工具”，用于标记基础包，供 Spring Boot 自动配置类扫描特定组件。
- 两者在 Spring Boot 中通过 `@SpringBootApplication` 协同工作，前者应用的自动装配和组件管理更高效。

