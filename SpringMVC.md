# SpringMVC

==MVC==是模型（model），视图（View）,控制器（controller）的简写，是一种软件设计规范。

模型(dao,service) 视图(jsp) 控制器（servlet）

==Model(模型)：==数据模型，提供要展示的数据，因此包括数据和行为，可以认为是领域模型或JavaBean组件（包含数据和行为），现在一般都分离开：Value Object(数据Dao)和服务层（行为Service）.也就是模型提供了模型数据查询和模型数据的状态更新等功能，包括数据和业务。

==View(视图)：==负责进行模型的展示，一般就是前端的用户界面，客户看到的东西

==Controller(控制器)：==接收用户请求，委托给模型进行处理（状态改变），处理完毕后把返回的模型数据返回给视图，由视图负责展示。

> 导入相关依赖

```xml
        <dependencies>
            <dependency>
                <groupId>junit</groupId>
                <artifactId>junit</artifactId>
                <version>4.12</version>
            </dependency>
            <dependency>
                <groupId>org.springframework</groupId>
                <artifactId>spring-webmvc</artifactId>
                <version>5.1.9.RELEASE</version>
            </dependency>
            <dependency>
                <groupId>javax.servlet</groupId>
                <artifactId>servlet-api</artifactId>
                <version>2.5</version>
            </dependency>
            <dependency>
                <groupId>javax.servlet.jsp</groupId>
                <artifactId>jsp-api</artifactId>
                <version>2.2</version>
            </dependency>
            <dependency>
                <groupId>javax.servlet</groupId>
                <artifactId>jstl</artifactId>
                <version>1.2</version>
            </dependency>
        </dependencies>
```

![image-20201209223848773](https://gitee.com/cnuto/images/raw/master/image/image-20201209223848773.png)

- DispatcherServlet表示前置控制器，是整个SpringMVC的控制中心。用户发出请求，DispatcherServlet接收请求并拦截请求。

- HandlerMapping为处理器映射。DispatcherServlet调用HandlerMapping,HandlerMapping根据请求url查找Handler。

- HandlerExecution表示具体的Handler,其主要作用是根据url查找控制器
- HandlerExecution将解析后的信息传递给DispatcherServlet,如解析控制器映射等。
- HandlerAdapter表示处理器适配器，其按照特定的规则去执行Handler。
- Handler让具体的Controller执行。
- Controller将具体的执行信息返回给HandlerAdapter,如ModelAndView。
- HandlerAdapter将视图逻辑名或模型传递给DispatcherServlet。
- DispatcherServlet调用视图解析器(ViewResolver)来解析HandlerAdapter传递的逻辑视图名。
- 视图解析器将解析的逻辑视图名传给DispatcherServlet。
- DispatcherServlet根据视图解析器解析的视图结果，调用具体的视图。
- 最终视图呈现给用户。

