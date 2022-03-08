# Spring

> 使用set方法

```java
public class UserServiceImpl implements UserService {
    // private UserDao userDao = new UserDaoImpl();
    private UserDao userDao;
    //利用set进行动态实现值的注入
    public void setUserDao(UserDao userDao){
        this.userDao = userDao;
    }
    public void getUser() {
        userDao.getUser();
    }
}

```

- 之前需要程序员主动创建对象
- 使用set注入后，程序不再具有主动性，而是变成了被动的接受对象

这种思想，从本质上解决了问题，我们不需要再去管理对象的创建，系统的耦合性大大降低，可以更加专注在业务的实现上！这是IOC的原型！

## IOC本质

控制反转IOC，是一种设计思想，DI（依赖注入）是实现IOC的一种方法。在没有IOC的程序中，我们使用面对对象编程，对象的创建与对象间的依赖关系完全硬编码在程序中，对象的创建由程序自己控制，控制反转后将对象的创建转移给第三方，也就是：获得依赖的方式反转了    

### 控制反转

`控制:` 谁来控制对象的创建 , 传统应用程序的对象是由程序本身控制创建的 , 使用Spring后 , 对象是由Spring来创建的 .

`反转 :`程序本身不创建对象 , 而变成被动的接收对象 .

## IOC创建对象的方式

- 默认使用无参构造创建对象
- 有参构造创建对象

```xml
    <bean id="user" class="com.xhh.pojo.User">
        <!--第一种方式,通过给下标赋值-->
        <constructor-arg index="0" value="王五"/>
    </bean>
```

```xml
<!--第二种，通过类型创建，不建议使用-->
    <bean id="user" class="com.xhh.pojo.User">
        <constructor-arg type="java.lang.String" value="赵六"/>
</bean>
```

```xml
<!--    第三种，通过参数名赋值-->
        <bean id="user" class="com.xhh.pojo.User">
            <constructor-arg name="name" value="777"/>
        </bean>
```

**在配置文件加载的时候，容器中管理的对象就已经初始化了**

## Spring配置

### 别名

```xml
<!--别名，如果添加了别名，我们也可以使用别名获取对象-->
<alias name="user" alias="userNew"/>
```

### Bean配置

```xml
<!--
id:bean  的唯一标识符，也就是相当于对象名
class: bean 对象所对应的全限定名： 包名+类名
name:别名，而且name可以同时取多个别名
-->
    <bean id="user" class="com.xhh.pojo.User" name="user2,u2,u3;u4">
        <constructor-arg name="name" value="你好"/>
    </bean>
```

### import

import一般用于团队开发，可以将多个配置文件合成一个文件

```xml
<import resource="beans.xml"/>
```

## 依赖注入

### 构造器注入

### Set方式注入

- 依赖注入：set注入
  - 依赖：bean对象的创建依赖于容器
  - 注入：bean对象中的所有属性由容器注入

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="http://www.springframework.org/schema/beans
        https://www.springframework.org/schema/beans/spring-beans.xsd">

    <bean id="address" class="com.xhh.pojo.Address">
        <property name="address" value="江西南昌"/>
    </bean>
    <bean id="student" class="com.xhh.pojo.Student">
        <!--第一种，普通值注入,value-->
        <property name="name" value="张三"/>
        <!--第二种，Bean注入，ref-->
        <property name="address" ref="address"/>

        <!--数组-->
        <property name="books">
            <array>
                <value>西游记</value>
                <value>红楼梦</value>
                <value>三国演义</value>
            </array>
        </property>

        <!--list-->
        <property name="hobbys">
            <list>
                <value>打游戏</value>
                <value>敲代码</value>
            </list>
        </property>

        <!--map-->
        <property name="card">
            <map>
                <entry key="身份证" value="1232456789789"/>
                <entry key="电话号码" value="12345678945"/>
            </map>
        </property>

        <!--Set-->
        <property name="games">
            <set>
                <value>lol</value>
                <value>ow</value>
            </set>
        </property>

        <!--空值-->
        <property name="wife">
            <null/>
        </property>

        <!--Properties-->
        <property name="info">
            <props>
                <prop key="学号">20172803</prop>
                <prop key="性别">男</prop>
                <prop key="姓名">张三</prop>
            </props>
        </property>
    </bean>
</beans>
```

### c命名空间和p命名空间注入

> 在beans文件中添加

```xml
xmlns:p="http://www.springframework.org/schema/p"
xmlns:c="http://www.springframework.org/schema/c"
```

> 注入方式

```xml
    <!-- p命名空间注入，可以直接注入属性的值：property   -->
    <bean id="user" class="com.xhh.pojo.User" p:age="18" p:name="张三"/>
    
    <!--c命名空间注入，通过有参构造器注入:construct-args    -->
    <bean id="user2" class="com.xhh.pojo.User" c:age="18" c:name="李四"/>
```

## bean的作用域

1.单例模式（Spring默认机制）

```xml
<bean id="user2" class="com.xhh.pojo.User" c:age="18" c:name="李四" scope="singleton"/>
```

2.原型模式：每次从容器中get的时候，都会产生一个新对象

```xml
<bean id="user2" class="com.xhh.pojo.User" c:age="18" c:name="李四" scope="prototype"/>
```

## Bean的自动装配

- 自动装配是Spring满足bean依赖的一种方式
- Spring会在上下文中自动寻找，并自动给bean装配属性

> 在Spring中有三种装配方式

1.在xml文件中显示配置

2.在java中显示

3.隐式的自动装配bean【重点】

> ByName和ByType自动装配

```xml
    <bean id="cat" class="com.xhh.pojo.Cat"/>
    <bean id="dog" class="com.xhh.pojo.Dog"/>
    <!--byName:  会自动在容器上下文查找，查询自己对象set方法后面对应的bean id  -->
    <!--byType:  会自动在容器上下文查找，查询对象属性类型相同的bean-->
    <bean id="people" class="com.xhh.pojo.People" autowire="byName">
        <property name="name" value="张三"/>
    </bean>
```

byname需要保证bean的id唯一    

### 使用注解实现自动装配

添加支持

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xmlns:context="http://www.springframework.org/schema/context"
    xsi:schemaLocation="http://www.springframework.org/schema/beans
        https://www.springframework.org/schema/beans/spring-beans.xsd
        http://www.springframework.org/schema/context
        https://www.springframework.org/schema/context/spring-context.xsd">

    <context:annotation-config/>

</beans>
```

@Autowired

直接在属性上使用，也可以在set方法上使用

使用autowired后不在需要编写set方法

```java
    @Autowired
    @Qualifier(value = "cat2")
    private Cat cat;
    @Autowired
    private Dog dog;
    private String name;
```

如果@Autowired自动装配的环境比较复杂，自动装配无法通过一个`@Autowired`完成时，可以使用`@Qualifier(value = "cat2")`去配置`@Autowired`使用，指定一个唯一的bean对象

`@Autowired`和`@Resource`的区别：

`@Resource`和@`Autowired`都是做bean的注入时使用，其实`@Resource`并不是Spring的注解，它的包是`javax.annotation.Resource`，需要导入，但是Spring支持该注解的注入

> 相同点

两者都可以写在字段和setter方法上。两者如果都写在字段上，那么就不需要再写setter方法

> 不同点

- `@Autowired`注解是按照类型（byType）装配依赖对象，默认情况下它要求依赖对象必须存在，如果允许null值，可以设置它的required属性为false。如果我们想使用按照名称（byName）来装配，可以结合`@Qualifier`注解一起使用。
- `@Resource`默认按照ByName自动注入，由J2EE提供，需要导入包`javax.annotation.Resource`。`@Resource`有两个重要的属性：name和type，而Spring将`@Resource`注解的name属性解析为bean的名字，而type属性则解析为bean的类型。所以，如果使用name属性，则使用byName的自动注入策略，而使用type属性时则使用byType自动注入策略。如果既不制定name也不制定type属性，这时将通过反射机制使用byName自动注入策略

## 使用注解开发

> 属性注入

```java
// 相当于<bean id="user" class="com.xhh.pojo.User"/>
@Component
public class User {
    private String name="张三";
}
```

> 衍生的注解

`@Component`,dao`@Repository`,service`@Service`,controller`@Controller`

这四个注解功能都是一样的，都是 代表将某个类注册到spring中，装配Bean

```java
@Configuration
public class XhhConfig {
    //注册一个bean
    @Bean
    public User getUser(){
        return new User();
    }
}
```

```java
@Component
public class User {
    @Value("张三")
    private String name;

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                '}';
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
```

```java
public class MyTest {
    public static void main(String[] args) {
        //如果完全使用了配置类方式去做，就只能通过AnnotationConfig上下文来获取容器，通过配置类的class对象加载！
        ApplicationContext context = new AnnotationConfigApplicationContext(XhhConfig.class);
        User user = (User) context.getBean("getUser");
        System.out.println(user.getName());
    }
}
```



## xml与注解

- `xml`更加万能，适用于任何场合！维护简单方便
- `注解`不是本类不能使用，维护相对复杂

### 最佳实践

- xml用来管理bean
- 注解只负责完成属性的注入
- 在使用的过程中，注意一个问题：让注解生效，需要开启注解的支持

## 代理模式

### 静态代理

角色分析：

- 抽象角色：一般使用抽象接口或者抽象类来解决
- 真实角色：被代理的角色
- 代理角色：代理真实角色，做一些操作、
- 客户：访问代理对象

> 步骤：

1.接口

```java
public interface Rent {
    //出租房屋
    public void rent();
}
```

2.真实角色

```java
//房东
public class Host implements Rent {
    public void rent() {
        System.out.println("房东要出租房子");
    }
}
```

3.代理角色

```java
 package com.xhh.demo1;

/**
 * @author xhh
 * @date 2020/12/6 22:27
 */
public class Proxy implements Rent{
    private Host host;
    public Proxy(){}

    public Proxy(Host host){
        this.host = host;
    }

    public void rent() {
        host.rent();
        seeHouse();
        fare();
        contract();
    }
    //看房
    public void seeHouse(){
        System.out.println("中介带你看房");
    }
    //收中介费
    public void fare(){
        System.out.println("中介收钱");
    }
    //签合同
    public void contract(){
        System.out.println("签合同");
    }
}
```

4.客户端访问代理角色

```java
public class Client {
    public static void main(String[] args) {
        Host host = new Host();

        Proxy proxy = new Proxy(host);
        proxy.rent();
    }
}
```

### 动态代理

- 动态代理和静态代理角色相同
- 动态代理的代理类是动态生成的
- 动态代理分为两大类：基于接口的动态代理，基于类的动态代理

> 真实角色

```java
public class Host implements Rent {
    public void rent() {
        System.out.println("房东要出租房子");
    }
}
```

> 接口

```java
public interface Rent {
    //出租房屋
    public void rent();

}
```

> 自动生成代理类

```java
//自动生成代理类
public class ProxyInvocationHandler implements InvocationHandler {
    //被代理的接口
    private Rent rent;

    public void setRent(Rent rent) {
        this.rent = rent;
    }

    //生成得到代理类实例对象
    public Object getProxy(){
        return Proxy.newProxyInstance(this.getClass().getClassLoader(),rent.getClass().getInterfaces(),this);
    }
    //处理代理实例，并返回结果
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        //动态代理的本质，是使用反射机制实现的
        Object result = method.invoke(rent, args);
        return result ;
    }
}
```

> 用户

```java
public class Client {
    public static void main(String[] args) {
        //真实角色
        Host host = new Host();
        //代理角色
        ProxyInvocationHandler pih = new ProxyInvocationHandler();
        //通过调用程序处理角色来处理我们要调用的接口对象
        pih.setRent(host);

        Rent proxy = (Rent) pih.getProxy();
        proxy.rent();
    }
}
```

## AOP

AOP:面向切面编程，通过预编译方式和运行期动态代理实现程序功能的统一维护的一种技术

> 方式一：使用Spring的API接口来实现AOP

实现Springaop的接口，生成执行环绕前后的类

```java
public class BeforeLog implements MethodBeforeAdvice {
    //method: 要执行的目标对象的方法
    // args: 参数
    //target: 目标对象
    public void before(Method method, Object[] args, Object target) throws Throwable {
        System.out.println(target.getClass().getName()+"执行了"+method.getName()+"方法");
    }
}
```

```java
public class  AfterLog implements AfterReturningAdvice {
    public void afterReturning(Object returnValue, Method method, Object[] args, Object target) throws Throwable {
        System.out.println("执行了"+method.getName()+"的方法，返回结果为"+returnValue);
    }
}
```

编写xml文件将执行环绕类横切进去

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:aop="http://www.springframework.org/schema/aop"
       xsi:schemaLocation="http://www.springframework.org/schema/beans
        https://www.springframework.org/schema/beans/spring-beans.xsd
         http://www.springframework.org/schema/aop
        https://www.springframework.org/schema/aop/spring-aop.xsd">

    <bean id="userService" class="com.xhh.service.UserServiceImpl"/>
    <bean id="before" class="com.xhh.log.BeforeLog"/>
    <bean id="after" class="com.xhh.log.AfterLog"/>
 
   <!-- aop的第一种方式-->
   <aop:config>
         <aop:pointcut id="pointCut" expression="execution(*     com.xhh.service.UserServiceImpl.*(..))"/>
         <aop:advisor advice-ref="before" pointcut-ref="pointCut"/>
         <aop:advisor advice-ref="after" pointcut-ref="pointCut"/>
     </aop:config>
</beans>
```

**方式二：自定义来实现AOP【主要是切面定义**】

自定义一个切面

```java
public class DiyPointCut {
    public void before(){
        System.out.println("==========方法执行前==========");
    }
    public void after(){
        System.out.println("==========方法执行后==========");
    }
}
```

```xml
    <bean id="userService" class="com.hao.service.UserServiceImpl"/>
<!--第二种方法-->
        <bean id="diy" class="com.hao.diy.DiyPointCut"/>
        <aop:config>
            <aop:aspect ref="diy">
                <aop:pointcut id="point" expression="execution(* com.xhh.service.UserServiceImpl.*(..))"/>
                <aop:before method="before" pointcut-ref="point"/>
                <aop:after method="after" pointcut-ref="point"/>
            </aop:aspect>
        </aop:config>
    </beans>
```

**方式三：使用注解实现的aop**

```java
@Component
@Aspect  //标注这个类为一个切面
public class AnnotationPointCut {
    @Before("execution(* com.hao.service.UserServiceImpl.*(..))")
    public void before(){
        System.out.println("==========方法执行前===========");
    }
}
```

注意：在使用注解实现aop时，需要在xml配置文件导入注解的支持

```xml
    <bean id="userService" class="com.hao.service.UserServiceImpl"/>
    <!--第三种aop实现方式-->
    <context:component-scan base-package="com.hao"/>
    <context:annotation-config/>
    <aop:aspectj-autoproxy/>
```

# 整合Mybatis

> 步骤：

1.导入相关jar包[junit,mybatis,mysql数据库，spring,aop织入，mybatis-spring(new)]

```xml
    <dependencies>
        <dependency>
            <groupId>junit</groupId>
            <artifactId>junit</artifactId>
            <version>4.12</version>
        </dependency>
        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
            <version>5.1.47</version>
        </dependency>
        <dependency>
            <groupId>org.mybatis</groupId>
            <artifactId>mybatis</artifactId>
            <version>3.5.2</version>
        </dependency>
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-webmvc</artifactId>
            <version>5.2.0.RELEASE</version>
        </dependency>
        <dependency>
            <groupId>org.aspectj</groupId>
            <artifactId>aspectjweaver</artifactId>
            <version>1.8.13</version>
        </dependency>
        <dependency>
            <groupId>org.mybatis</groupId>
            <artifactId>mybatis-spring</artifactId>
            <version>2.0.2</version>
        </dependency>
    </dependencies>
```

## 总结步骤

1.编写数据源

```xml
    <!-- DataSource：使用Spring的数据源替换Mybatis的配置-->
    <bean id="datasource" class="org.springframework.jdbc.datasource.DriverManagerDataSource">
        <property name="driverClassName" value="com.mysql.jdbc.Driver"/>
        <property name="url" value="jdbc:mysql://localhost:3306/mybatis?useSSL=true&amp;useUnicode=true&amp;characterEncoding=UTF-8"/>
        <property name="username" value="root"/>
        <property name="password" value="root"/>
    </bean>
```

2.sqlSessionFactory

```xml
    <bean id="sqlSessionFactory" class="org.mybatis.spring.SqlSessionFactoryBean">
        <property name="dataSource" ref="datasource"/>
        <!--绑定mybatis配置文件-->
        <property name="configLocation" value="classpath:mybatis-config.xml"/>
        <property name="mapperLocations" value="classpath:com/xhh/mapper/UserMapper.xml"/>
    </bean>
```

3.sqlSessionTemplate

```xml
    <!--SqlSessionTemplate:就是我们所使用的sqlSession-->
    <bean id="sqlSession" class="org.mybatis.spring.SqlSessionTemplate">
        <!--只能使用构造器注入sqlSessionFactory，没有set方法-->
        <constructor-arg index="0" ref="sqlSessionFactory"/>
    </bean>
```

4.需要给接口加实现类

```java
public class UserMapperImpl implements UserMapper {
    private SqlSessionTemplate sqlSessionTemplate;

    public void setSqlSessionTemplate(SqlSessionTemplate sqlSessionTemplate) {
        this.sqlSessionTemplate = sqlSessionTemplate;
    }

    public List<User> getUser() {
        UserMapper mapper = sqlSessionTemplate.getMapper(UserMapper.class);
        return mapper.getUser();
    }
}
```

5.测试

```java
    @Test
    public void test(){
        ApplicationContext context = new ClassPathXmlApplicationContext("spring-dao.xml");
        UserMapper userMapper = context.getBean("userMapper", UserMapper.class);
        List<User> user = userMapper.getUser();
        for (User user1 : user) {
            System.out.println(user1);
        }
    }
```

# Spring事务

- 声明式事务：AOP 

```xml
JDBC事务
<bean id="transaction" class="org.springframework.jdbc.datasource.DataSourceTransactionManager">
        <property name="dataSource" ref="dataSource"/>
    </bean>
    <!--配置事务通知，结合AOP实现事务的织入-->
    <tx:advice id="TxAdvice" transaction-manager="transaction">
        <tx:attributes>
            <tx:method name="*" propagation="REQUIRED"/>
        </tx:attributes>
    </tx:advice>
    <aop:config>
        <aop:pointcut id="txPointCut" expression="execution(* com.xhh.mapper.*.*(..))"/>
        <aop:advisor advice-ref="TxAdvice" pointcut-ref="txPointCut"/>
    </aop:config>                                                         
```

​	