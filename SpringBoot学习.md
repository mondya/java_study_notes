#  第一个SpringBoot程序
    
使用idea创建springboot程序

# SpringBoot原理

自动配置：

**pom.xml**

- spring-boot-dependencies: 核心依赖在父工程中
- 在引入依赖时，不需要指定版本

**启动器**

```xml
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
```

- 启动器：SpringBoot的启动场景

**主程序**

```java
package com.xhh.helloworld;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
//@SpringBootApplication：标注这个类是一个springboot的应用：启动类下的所有资源都被导入
@SpringBootApplication
public class HelloworldApplication {

    public static void main(String[] args) {
        //将springboot应用启动
        SpringApplication.run(HelloworldApplication.class, args);
    }

}
```

- 注解

  - ```java
    @SpringBootConfiguration	springboot的配置
    	@Configuration	spring配置类
    		@Component	说明是一个spring的组件
    @EnableAutoConfiguration	自动配置
    	@AutoConfigurationPackage	自动配置包
    		@Import({Registrar.class})	自动配置包注册
    	@Import({AutoConfigurationImportSelector.class})	自动导入选择	
    ```

结论：springboot所有的自动配置都是在启动的时候扫描并加载：`spring.factory`所有的自动配置类都在这里面，但是不一定生效，要判断条件是否成立，只要导入了对应的start，就有了对应的启动器，自动装配就会生效，配置成功

>  SpringApplication

这个类主要做了以下四件事情：

- 推断应用的类型是普通的项目还是web项目
- 查找并加载所有可用初始化器，设置到initializers属性中
- 找出所有的应用程序监听器，设置到listeners属性中
- 推断并设置main方法的定义类，找到运行的主类

# SpringBoot配置文件

> 配置文件

SpringBoot使用一个全局的配置文件，配置文件名称是固定的

- application.properties
  - 语法结构： key=value
- application.yml
  - 语法结构：key:空格 value

**配置文件的作用：**修改SpringBoot自动配置的默认值，因为SpringBoot在底层已经帮我们自动配置

> application.yaml

```yaml
#对空格的要求严格
name: xhh
#对象
student1:
  name: xhh
  age: 29
#行类写法
student: {name: xhh,age: 29}
#数组
pets:
  - cat
  - dog
  - pig

pets2: [cat,dog,pig]
```

> application.properties

```properties
#properties只能保存键值对
name=xhh
student.name=xhh
student.age=29
```

## 给属性赋值

```yaml
person:
  name: xhh
  age: 23
  happy: false
  birthday: 2021/03/20
  maps: {k1: v1,k2: v2}
  lists:
    - code
    - music
    - movie
  dog:
    name: 旺财
    age: 3
```

`@ConfigurationProperties(prefix = "person")`作用：

将配置文件中配置的每一个属性的值，映射到组件中；告诉SpringBoot将本类中的所有属性和配置文件中相关的配置进行绑定，参数prefix = "person":将配置文件中的person下面的所有属性一一对应

只有这个组件是容器中的组件，才能够使用容器提供的@ConfigurationProperties功能

## jsr303校验

类上添加`@Validated`注解(pom文件中添加依赖),可以在属性上添加条件限制依赖

```java
    @Email
    private String name;
```

name没有按照邮件格式填写会报错

> yaml配置不同环境端口

```yaml
server:
  port: 8080
spring:
  profiles:
    active: dev
---
server:
  port: 8081
spring:
  profiles: dev
---
server:
  port: 8082
spring:
  profiles: test
```

默认优先级：`file/config/application.yaml `  > `file/applicaion.yaml`  > `file/src/resources/config/application.yaml`  > `file/src/resources/application.yaml`

# SpringBoot Web开发

> 静态资源

1.在springboot,我们可以使用以下方式处理静态资源

- webjars
- public , static , /** ,    resources

2.优先级：resources  >  static(默认)  > public

**总结：**

- 静态文件的访问默认是在static目录下的文件，如果编写控制类来访问，则必须在返回值后面添加文件类型后缀
- 动态文件的访问默认是在templates目录下，并且在pom.xml文件中导入thymeleaf依赖。如果此时编写控制类访问，则会覆盖静态文件的路径，直接访问templates目录下的文件
- templates下的文件不能通过文件名直接访问，必须发起请求（编写控制类），或者向系统容器中添加相应的组件。并且添加组件的方式可以改变访问路径

