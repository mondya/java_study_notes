# MyBatis

## 搭建环境

```sql
CREATE DATABASE `mybatis`
use `mybatis`
CREATE TABLE `user` (
  `id` int(4) NOT NULL,
  `name` varchar(20) DEFAULT NULL,
  `pwd` varchar(40) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8

INSERT INTO `user`(`id`,`name`,`pwd`) VALUES（1000，'张三','123456'）,（1000，'李四','123456'),（1000，'王五','123456')
```

> 新建项目

1.新建一个maven项目，导入依赖

```xml
    <!--父工程-->
    <groupId>com.xhh</groupId>
    <artifactId>Mybatis-Study</artifactId>
    <version>1.0-SNAPSHOT</version>
    <!--导入依赖    -->
    <dependencies>
        <!--导入mysql驱动-->
        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
            <version>5.1.46</version>
        </dependency>
        <!--导入mybatis-->
        <dependency>
            <groupId>org.mybatis</groupId>
            <artifactId>mybatis</artifactId>
            <version>3.5.2</version>
        </dependency>
        <!--导入junit-->
        <dependency>
            <groupId>junit</groupId>
            <artifactId>junit</artifactId>
            <version>4.12</version>
        </dependency>
    </dependencies>
```

## 创建一个模块

- 编写mybatis的核心配置文件

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE configuration
        PUBLIC "-//mybatis.org//DTD Config 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-config.dtd">
<!--configuration核心配置文件-->
<configuration>
    <environments default="development">
        <environment id="development">
            <transactionManager type="JDBC"/>
            <dataSource type="POOLED">
                <property name="driver" value="com.mysql.jdbc.Driver"/>
                <property name="url" value="jdbc:mysql://localhost:3306/mybatis?useSSL=true&amp;useUnicode=true&amp;characterEncoding=UTF-8"/>
                <property name="username" value="root"/>
                <property name="password" value="root"/>
            </dataSource>
        </environment>
    </environments>
</configuration>
```

- 编写mybatis工具类

```java
public class MybatisUtils {
    private static SqlSessionFactory sqlSessionFactory;
    static{
        try {
            String resource = "mybatis-config.xml";
            InputStream inputStream = Resources.getResourceAsStream(resource);
            sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    //有了sqlSessionFactory，就可以从中获得sqlSession的实例
    //sqlSession SqlSession 提供了在数据库执行 SQL 命令所需的所有方法。可以通过 SqlSession 实例来直接执行已映射的 SQL 语句
    public static SqlSession getSqlSession(){
        return sqlSessionFactory.openSession();
    }
}
```

## 编写代码

> 实体类

```java
package com.xhh.pojo;

/**
 * @author xhh
 * @date 2020/11/29 16:26
 */
public class User {
    private int id;
    private String name;
    private String pwd;

    public User(){}
    public User(int id, String name, String pwd) {
        this.id = id;
        this.name = name;
        this.pwd = pwd;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", pwd='" + pwd + '\'' +
                '}';
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}

```

> Dao接口

```java
public interface UserDao {
    List<User> getUserList();
}
```

> 接口实现类由原来的UserDaoImpl转变为一个Mapper配置文件

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper
        PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<!--namespace=绑定一个对应的Dao/Mapper接口-->
<mapper namespace="com.xhh.dao.UserDao">
    <select id="getUserList" resultType="com.xhh.pojo.User">
        select * from mybatis.user
    </select>
</mapper>
```

## 问题

>  org.apache.ibatis.binding.BindingException: Type interface com.xhh.dao.UserDao is not known to the MapperRegistry.

在mybatis-config.xml文件中加入

```xml
<mappers>
    <mapper resource="com/xhh/dao/UserMapper.xml"></mapper>
</mappers>
```

> The error may exist in com/xhh/dao/UserMapper.xml
>
> maven由于他的约定大于配置，可能会遇到配置文件无法被导出或者生效的问题

```xml
<!--    在build中配置resources,来防止资源导出失败的问题-->
    <build>
        <resources>
            <resource>
                <directory>src/main/resources</directory>
                <includes>
                    <include>**/*.properties</include>
                    <include>**/*.xml</include>
                </includes>
                <filtering>false</filtering>
            </resource>
            <resource>
                <directory>src/main/java</directory>
                <includes>
                    <include>**/*.properties</include>
                    <include>**/*.xml</include>
                </includes>
                <filtering>false</filtering>
            </resource>
        </resources>
    </build>
```

## 测试

```java
public class UserDaoTest {

    @Test
    public void  test(){
    //第一步，获得SqlSession对象
        SqlSession sqlSession = MybatisUtils.getSqlSession();
        
    //方式1：getMapper
        UserDao mapper = sqlSession.getMapper(UserDao.class);
        List<User> userList = mapper.getUserList();

        for(User user:userList){
            System.out.println(user);
        }
        sqlSession.close();
    }
}
```

## 总结

- 创建maven项目，导入mysql驱动，mybatis,junit
- 编写mybatis核心配置文件
  - 驱动`com.mysql.jdbc.Driver`
  - url`jdbc:mysql://localhost:3306/mybatis?useSSL=true&amp;useUnicode=true&amp;characterEncoding=UTF-8`
  - username:`root`
  - password:`root`
- 在工具类中构建`sqlSessionFactory`实例，`sqlSessionFactory.openSession()`方法获得sqlSession实例

```java
    private static SqlSessionFactory sqlSessionFactory;
    static{
        try {
            String resource = "mybatis-config.xml";
            InputStream inputStream = Resources.getResourceAsStream(resource);
            sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    //有了sqlSessionFactory，就可以从中获得sqlSession的实例
    //sqlSession SqlSession 提供了在数据库执行 SQL 命令所需的所有方法。可以通过 SqlSession 实例来直接执行已映射的 SQL 语句
    public static SqlSession getSqlSession(){
        return sqlSessionFactory.openSession();
    }
```

-  创建实体类和接口

```java
public class User{}

//接口
public interface UserDao {
    List<User> getUserList();
}
```

- 配置UserMapper.xml

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper
        PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<!--namespace=绑定一个对应的Dao/Mapper接口-->
<mapper namespace="com.xhh.dao.UserDao">
    <select id="getUserList" resultType="com.xhh.pojo.User">
        select * from mybatis.user
    </select>
</mapper>
```

- 测试类

```java
public class UserDaoTest {

    @Test
    public void  test(){
    //第一步，获得SqlSession对象
        SqlSession sqlSession = MybatisUtils.getSqlSession();

        //方式1：getMapper
        UserDao mapper = sqlSession.getMapper(UserDao.class);
        List<User> userList = mapper.getUserList();

        for(User user:userList){
            System.out.println(user);
        }
        sqlSession.close();
    }
}
```

## CRUD

### 1.namespace

namespace中的包名和接口名相同

### 2.select

选择，查询语句：

- id:对应的namespace中的方法名
- resultType:Sql语句执行的返回值
- parameterType:参数类型

> 编写接口

```java
    //删除用户
    int deleteUser(int id);
```

> 编写对应mapper中对应的sql语句

```xml
    <delete id="deleteUser" parameterType="int">
        delete from user where id=#{id}
    </delete>
```

> 测试

```java
    //删除用户
    @Test
    public void deleteUser(){
        SqlSession sqlSession = MybatisUtils.getSqlSession();
        UserMapper mapper = sqlSession.getMapper(UserMapper.class);
        int i = mapper.deleteUser(1001);
        if(i>0) {
            System.out.println("修改成功");
        }

        sqlSession.commit();
        sqlSession.close();

    }
```

==注意：增删改需要提交事务==

## Map

假设，实体类或者数据库中的表字段或参数过多，我们应当考虑使用Map!

```java
    //增加用户，map<String,Object>
    @Test
    public void addUser2(){
        SqlSession sqlSession = MybatisUtils.getSqlSession();
        UserMapper mapper = sqlSession.getMapper(UserMapper.class);
        Map<String, Object> map = new HashMap<String, Object>();
 //       map.put("userid",1005);
        map.put("username","小明");
        mapper.addUser2(map);

        sqlSession.commit();
        sqlSession.close();
    }
```

```xml
<!--使用map增加用户-->
    <insert id="addUser2" parameterType="map" >
        insert into mybatis.user(id,name,pwd) values (#{userid},#{username},#{userpassword})
    </insert>

```

Map传递参数，直接在sql中取出即可！

对象传递参数，直接在sql中取对象的属性即可！

## 模糊查询

```java
    //模糊查询
    @Test
    public void getUserLike(){
        SqlSession sqlSession = MybatisUtils.getSqlSession();

        UserMapper mapper = sqlSession.getMapper(UserMapper.class);

        List<User> list = mapper.getUserLike("%张%");
        for(User li:list){
            System.out.println(li);
        }

        sqlSession.commit();
        sqlSession.close();

    }
```

1.java代码执行时，传递通配符%%

```java
List<User> list = mapper.getUserLike("%张%");
```

2.在sql拼接中使用通配符

```xml
    <select id="getUserLike" resultType="com.xhh.pojo.User">
        select * from mybatis.user where name like "%"#{value}"%"
    </select>
```

## 配置解析

### 1.核心配置文件

- mybatis-config.xml
- MyBatis的配置文件包含了会影响MyBatis行为的设置和属性信息

```xml
configuration（配置）
properties（属性）
settings（设置）
typeAliases（类型别名）
typeHandlers（类型处理器）
objectFactory（对象工厂）
plugins（插件）
environments（环境配置）
environment（环境变量）
transactionManager（事务管理器）
dataSource（数据源）
databaseIdProvider（数据库厂商标识）
mappers（映射器）
```

### 2.配置环境（environments）

mybatis可以配置成适应多种环境

**尽管可以配置多个环境，但每个sqlSessionFactory实例只能选择一种环境**

Mybatis默认的事务管理器就是JDBC,连接池：POOLED

### 3.属性(properties)

可以通过properties属性实现引用配置文件

![image-20201130233209499](D:\images\image-20201130233209499.png)

> 编写一个配置文件 db.properties

```xml
driver=com.mysql.jdbc.Driver
url=jdbc:mysql://localhost:3306/mybatis?useSSL=true&useUnicode=true&characterEncoding=UTF-8
username=root
password=root
```

> 在核心配置文件中映入

```xml
    <properties resource="db.properties">
        <property name="username" value="root"/>
        <property name="password" value="123456"/>
    </properties>
```

- 可以直接引入外部文件
- 可以在其中增加一个属性
- 如果两个文件有同一个字段，优先使用外部配置文件

### 4.类型别名（typeAliases）

- 类型别名是为Java类型设置一个短的名字
- 存在的意义仅在于用来减少类完全限定名的冗余

```xml
    <typeAliases>
        <typeAlias type="com.xhh.pojo.User" alias="User"/>
    </typeAliases>
```

也可以指定一个包名,Mybatis会在包名下面搜索需要的JavaBean，比如：扫描实体类的包，它的默认别名就为这个类的类名首字母小写

```xml
    <typeAliases>
        <package name="com.xhh.pojo"/>
    </typeAliases>
```

在实体类比较少的时候，使用第一种方式，如果实体类比较多，则使用第二种方式

> 使用注解的方式

```java
@Alias("user")
public class User{}
```

### 5.settings(设置)

这是 MyBatis 中极为重要的调整设置，它们会改变 MyBatis 的运行时行为。

### 6.映射器(mappers)

> 方式一(推荐使用这种方式)

```xml
<mappers>
    <mapper resource="UserMapper.xml"/>
</mappers>
```

> 方式二

```xml
    <mappers>
    <!--        <mapper resource="UserMapper.xml"/>-->
    <mapper class="com.xhh.dao.UserMapper"/>
    </mappers>
```

注意点：

- 接口和它的Mapper配置文件必须同名
- 接口和它的Mapper配置文件必须在同一个包下

> 方式三

```xml
    <mappers>
<!--        <mapper resource="UserMapper.xml"/>-->
      <package name="com.xhh.dao.UserMapper"/>
    </mappers>
```

注意点：

- 接口和它的Mapper配置文件必须同名
- 接口和它的Mapper配置文件必须在同一个包下

## 解决属性名和字段名不一致问题 

当实体类和数据库字段名不是对应时，出现问题

![image-20201201163542746](D:\images\image-20201201163542746.png)

解决方法：

> 取别名

```xml
    <select id="getUserById" resultType="User">
    select id,name,pwd as password from mybatis.user where id = #{id}
    </select>
```

> resultMap  结果集映射

```xml
<!--结果集映射-->
    <resultMap id="UserMap" type="User">
<!-- column:数据库中的字段,property实体类的属性-->
        <result column="id" property="id"/>
        <result column="name" property="name"/>
        <result column="pwd" property="password"/>
    </resultMap>

    <select id="getUserById" resultMap="UserMap">
    select * from mybatis.user where id = #{id}
    </select>
```

`resultMap`元素是MyBatis中最重要最强大的元素

`resultMap`设计思想是，对于简单的语句根本不需要配置显示的结果映射，而对于复杂一点的语句只需要描述他们的关系就行了

## 日志

### 日志工厂

如果一个数据库操作出现异常，我们需要排除错误，使用日志来排除异常

```xml
    <settings>
        <setting name="logImpl" value="STDOUT_LOGGING"/>
    </settings>
```



![image-20201201172516147](D:\images\image-20201201172516147.png)

### Log4j

1.导包

```xml
    <dependency>
    	<groupId>log4j</groupId>
    	<artifactId>log4j</artifactId>
        <version>1.2.17</version>
    </dependency>
```

2.配置Log4j

```xml
<setting name="logImpl" value="LOG4J"/>
```

## 分页

- 为了减少数据的处理量

> 使用limit分页

```xml
<!--    结果集映射-->
    <resultMap id="userMapper" type="User">
        <result column="pwd" property="password"/>
    </resultMap>
    <select id="getUser" resultMap="userMapper">
    select * from mybatis.user limit #{startIndex},#{pageSize}
    </select>
```

> 接口

```java
public interface UserMapper {
    List<User> getUser(Map<String,Object> map);
}

```

> 测试代码

```java
    @Test
    public void testGetUser(){
        SqlSession sqlSession = MybatisUtils.getSqlSession();
        UserMapper mapper = sqlSession.getMapper(UserMapper.class);
        Map<String,Object> map = new HashMap<String, Object>();
        map.put("startIndex",0);
        map.put("pageSize",2);

        List<User> userList = mapper.getUser(map);

        for (User user : userList) {
            System.out.println(user);
        }
    }
```

## 使用注解开发

```java
public interface UserMapper {
    @Select("select * from mybatis.user")
    List<User> getUser();

    //如果存在多个参数，所有的参数前面必须加上@Param("参数名")注解
    @Select("select id,name,pwd as password from mybatis.user where id=#{id}")
    User getUserById(int id);

    //删除
    @Delete("delete from mybatis.user where id=#{id}")
    public void deleteUser(int id);
}
```

> 在工具类创建的时候实现自动提交事务

```java
    public static SqlSession getSqlSession(){
        return sqlSessionFactory.openSession(true);
    }
```

## 关于@Param()注解

- 基本类型的参数或者String类型，需要加上
- 引用类型不需要加
- 如果有一个基本类型，可以忽略，但是建议加上
- 在SQL中引用的是@Param()中设定的属性名

## #{} 和${}的区别

- \#{}防止sql注入，${}不防止sql注入
- 使用${}方式传入的参数，mybatis不会对它进行特殊处理，而使用#{}传进来的参数，mybatis默认会将其当成字符串 
- \#和$在预编译处理中是不一样的。#类似jdbc中的PreparedStatement，对于传入的参数，在预处理阶段会使用?代替，比如：

```sql
select * from student where id = ?;
```

待真正查询的时候即在数据库管理系统中（DBMS）才会代入参数。

而${}则是简单的替换，如下：

```sql
select * from student where id = 2;
```

> SQL注入问题

sql注入，就是指把用户输入的数据拼接到sql语句后面作为sql语句的一部分执行：

```sql
select * from user where name=' "+name+" ' and password=' "+password+" '
```

那么只要用户输入用户名admin和密码123456' or  'abc' = 'abc',那么拼接出来的语句就为

```sql
select * from user where name=' admin ' and password='123456' or 'abc'= 'abc';
```



## lombok使用

> 导入依赖

```xml
        <!-- https://mvnrepository.com/artifact/org.projectlombok/lombok -->
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <version>1.18.12</version>
            <scope>provided</scope>
        </dependency>
```

## 多对一处理

> 子查询

```xml
<mapper namespace="com.xhh.dao.StudentMapper">
<!--
思路：1.查询所有学生信息
        2.根据查询的tid，寻找对应的老师信息
-->
    <select id="getStudent" resultMap="StudentTeacher">
--         select s.id,s.name,t.name from student s,teacher t where s.tid = t.id
    select * from student
    </select>
    <resultMap id="StudentTeacher" type="Student">
        <result property="id" column="id"/>
        <result property="name" column="name"/>
<!--        复杂的属性，需要单独处理
            对象： association
            集合： collection
-->
        <association property="teacher" column="tid" javaType="Teacher" select="getTeacher"/>
    </resultMap>
    <select id="getTeacher" resultType="Teacher">
        select * from teacher where id=#{id}
    </select>
</mapper>
```

> 按结果查询

```xml
<!--结果嵌套-->
    <select id="getStudent" resultMap="StudentTeacher2">
        select s.id sid, s.name sname , t.name tname
        from student s ,teacher t 
        where s.tid = t.id
    </select>
    
    <resultMap id="StudentTeacher2" type="Student">
        <result property="id" column="sid"/>
        <result property="name" column="sname"/>
        <association property="teacher" javaType="Teacher">
            <result property="name" column="tname"/> 
            
        </association>
    </resultMap>
```

## 一对多处理

> 按照结果查询

```xml
    <select id="getTeacher" resultMap="TeacherStudent">
    select s.id sid, s.name sname, t.name tname , t.id tid
    from student s, teacher t
    where s.tid = t.id and t.id=#{tid}
    </select>

    <resultMap id="TeacherStudent" type="Teacher">
        <result property="name" column="tname"/>
        <result property="id" column="tid"/>
<!--        javaType="" 指定属性的类型
            集合中的泛型信息，我们使用ofType获取
-->
        <collection property="students" ofType="Student">
            <result property="id" column="sid"/>
            <result property="name" column="sname"/>
            <result property="tid" column="tid"/>
        </collection>
    </resultMap>
```

> 嵌套查询

```xml
<!--    嵌套查询-->
    <select id="getTeacher2" resultMap="TeacherStudent">
        select * from mybatis.teacher where id = #{tid}
    </select>
    <resultMap id="TeacherStudent" type="Teacher">
        <collection property="students" javaType="ArrayList" ofType="Student" select="getTeacherById" column="id"/>
    </resultMap>
    <select id="getTeacherById" resultType="Student">
        select * from mybatis.student where tid=#{tid}
    </select>
```

## 小结：

- 关联association（多对一）
- 集合collection（一对多）
- javaType & ofType
  - JavaType用来指定实体类中属性的类型
  - ofType用来指定映射到List或者集合中的Pojo类型，泛型中的约束类型 

## 动态SQL

### if

```xml
<select id="findActiveBlogWithTitleLike"
     resultType="Blog">
  SELECT * FROM BLOG
  WHERE state = ‘ACTIVE’
  <if test="title != null">
    AND title like #{title}
  </if>
</select>
```

这条语句提供了可选的查找文本功能。如果不传入 “title”，那么所有处于 “ACTIVE” 状态的 BLOG 都会返回；如果传入了 “title” 参数，那么就会对 “title” 一列进行模糊查找并返回对应的 BLOG 结果（细心的读者可能会发现，“title” 的参数值需要包含查找掩码或通配符字符）。

*where* 元素只会在子元素返回任何内容的情况下才插入 “WHERE” 子句。而且，若子句的开头为 “AND” 或 “OR”，*where* 元素也会将它们去除。

```xml
    <select id="getBlogIf" parameterType="map" resultType="blog">
        select * from blog
        <where>
            <if test="title != null">
                title = #{title}
            </if>
            <if test="author != null">
               and author = #{author}
            </if>
        </where>
    </select>
```

### choose,when,otherwise

```xml
    <select id="getBlogChoose" parameterType="map" resultType="blog">
        select * from blog
        <where>
            <choose>
                <when test="title != null">
                    title=#{title}
                </when>
                <when test="author!=null">
                    and author=#{author}
                </when>
                <otherwise>
                    and views =#{views}
                </otherwise>
            </choose>
        </where>
    </select>
```

### set

*set* 元素会动态地在行首插入 SET 关键字，并会删掉额外的逗号

```xml
    <update id="updateBlog" parameterType="map">
        update mybatis.blog 
        <set>
            <if test="title!=null">
                title=#{title},
            </if>
            <if test="author!=null">
                author=#{author}
            </if>
        </set>
        where id=#{id}
    </update>
```

### trim

如果 *where* 元素与你期望的不太一样，你也可以通过自定义 trim 元素来定制 *where* 元素的功能

```xml
<trim prefix="WHERE" prefixOverrides="AND |OR ">
  ...
</trim>
```

*prefixOverrides* 属性会忽略通过管道符分隔的文本序列（注意此例中的空格是必要的）。上述例子会移除所有 *prefixOverrides* 属性中指定的内容，并且插入 *prefix* 属性中指定的内容。

### sql片段

我们可以将一些功能部分抽取出来，方便复用

- 使用sql标签抽取公共的部分

```xml
<sql id="if-title-author">
    <if test="title != null">
        title = #{title}
    </if>
    <if test="author != null">
        and author = #{author}
    </if>
</sql>
```

引用

```xml
<select id="getBlogIf" parameterType="map" resultType="blog">
     select * from blog
     <where>
         <include refid="if-title-author"></include>
     </where>
 </select>
```

### Foreach

```xml
    <select id="getBlogForeach" parameterType="map" resultType="Blog">
        select * from mybatis.blog
        <where>
            <foreach collection="ids" item="id" open="and(" close=")" separator="or">
                id = #{id}
            </foreach>
        </where>
    </select>
```

你可以将任何可迭代对象（如 List、Set 等）、Map 对象或者数组对象作为集合参数传递给 *foreach*。当使用可迭代对象或者数组时，index 是当前迭代的序号，item 的值是本次迭代获取到的元素。当使用 Map 对象（或者 Map.Entry 对象的集合）时，index 是键，item 是值。

```java
HashMap map = new HashMap();
ArrayList<Integer> ids = new ArrayList();
ids.add(1);

map.put("ids",ids);
List<Blog> blogs = mapper.getBlogForeach(map);
```

## 缓存

**缓存**：存在内存中的临时数据、将用户经常查询的数据放在缓存中，用户区查询数据就不用从磁盘上（关系型数据库数据文件）查询，从缓存中查询，从而提高查询效率，解决高并发的性能问题 。0

一级缓存自动开启

> 二级缓存

- 二级缓存也叫全局缓存，一级缓存作用域太低了，所以诞生了二级缓存
- 基于namespace级别的缓存，一个名称空间，对应一个二级缓存
- 工作机制
  - 一个会话查询一条数据，这个数据就会被放在当前会话的一级缓存中
  - 如果当前会话关闭了，这个会话对应的一级缓存就消失了；我们的要求是会话关闭后，一级缓存中的数据被保存到二级缓存中
  - 新的会话查询信息，可以从二级缓存中获取内容
  - 不同的mapper查出的数据会放在自己对应的缓存(map)中

- 在mapper.xml文件中开启二级缓存

使用步骤：

1.开启全局缓存

```xml
<setting name = "cacheEnabled" value="true"/>
```

2.在要使用二级缓存的Mapper中开启

```xml
<cache/>
```

3.测试

**需要将实体类序列化**

> 小结

- 只要开启了二级缓存，在同一个Mapper下有效
- 数据都会先放在一级缓存中
- 只有当会话提交或者关闭的时候，才会提交到二级缓存中 

  ## 自定义缓存-ehcache		