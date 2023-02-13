# Gradle

## 项目结构

Gradle项目默认目录结构和Maven项目的目录结构一致，都是基于约定大于配置

![image-20230210232346502](D:.\images\image-20230210232346502.png)

## 创建

### spring创建

todo

### 命令创建

- 新建包
- `gradle init`初始化gradle项目
- 2：生成应用  --->  语言选择java  ---> 单应用/多应用(1:单应用)  ---> 构建脚本 (1:groovy)--->使用新API方法(no) --->选择测试框架(1:JUnit4)  --->项目名（默认） --->源码包位置（例：com.xhh）

### 常用命令

gradle指令需要在含有build.gradle的目录执行

|    常用gradle命令    |            作用            |
| :------------------: | :------------------------: |
|     gradle clean     |       清空build目录        |
|    gradle classes    |   编译业务代码和配置文件   |
|     gradle test      | 编译测试文件，生成测试报告 |
|     gradle build     |          构建项目          |
| gradle build -x test |      跳过测试构建项目      |

## 使用国内镜像

在gradle的Init.d目录下创建以.gradle结尾的文件，.gradle文件可以实现在build开始之前的操作

> 在init.d文件夹创建init.gradle文件

配置了mavenLocal()本地仓库后会导致Idea索引变慢，并且拖慢gradle的构建前操作，只需要配置maven镜像源和中央源，使用gradle自己的本地仓库结构去存储依赖即可

```java
allprojects {
    repositories {
        maven { url 'file:///D:/Program Files/maven-repository'}
        mavenLocal()
        maven { name "Alibaba" ; url "https://maven.aliyun.com/repository/public" }
        maven { name "Bstek" ; url "https://nexus.bsdn.org/content/groups/public/" }
        mavenCentral()
    }

    buildscript { 
        repositories { 
            maven { name "Alibaba" ; url 'https://maven.aliyun.com/repository/public' }
            maven { name "Bstek" ; url 'https://nexus.bsdn.org/content/groups/public/' }
            maven { name "M2" ; url 'https://plugins.gradle.org/m2/' }
        }
    }
}
```

### 启用init.gradle文件的方式

- 在命令行指定文件，例如gradle --init-script yourdir/init.grade -q youProjectName，可以多次输入命令来指定多个init文件
- 把init.gradle文件放入USER_HOME/.gradle/目录下
- 把以.gradle结尾的文件放入USER_HOME/.gradle/init.d/ 目录下
- 把以.gradle结尾的文件放到 GRADLE_HOME/init.d/ 目录下

### 仓库地址说明

`mavenLocal()`：指定使用maven本地仓库，而本地仓库在配置maven时settings文件指定的仓库位置，查找jar包顺序:USER_HOME/.m2/setting.xml >> M2_HOME/conf/settings.xml >> USER_HOME/.m2/repository

`maven{url 地址}`：指定maven仓库，一般用私有库或其他三方库

`mavenCentral()`：Maven中央仓库，无需配置，直接声明

gradle可以通过指定仓库地址为本地maven仓库地址和远程仓库地址相结合的方式，避免每次都去远程仓库下载依赖库，这种方式存在的问题是，如果本地maven仓库存在依赖，则会直接加载本地依赖，如果本地仓库没有依赖，则还是会从远程仓库下载依赖，但是下载的Jar包不会存储在本地maven仓库中，而是放在自己的缓存目录中，默认在USER_HOME/.gradle/caches目录。如果配置了GRADLE_USER_HOME，则会放在/caches目录，gradle cache不能指向maven repository，cache下载的文件不是按照maven仓库的存储方式存储

![image-20230211005124706](.\images\image-20230211005124706.png)

https://developer.aliyun.com/mvn/guide

IDEA配置gradle user home

![image-20230212001048310](.\images\image-20230212001048310.png)

## Wrapper包装器

Gradle Wrapper实际上就是对Gradle的一层包装，用于解决实际开发中可能遇到的不同的项目需要不同版本的Gradle问题。例如，把自己的代码共享给其他人使用，可能出现的情况：

- 对方电脑没有安装gradle
- 对方电脑安装过gradle，但是版本太旧了

这时候，就需要考虑Gradle Wrapper，实际上有了Gradle Wrapper之后，我们本地可以不需要配置Gradle，使用gradle项目自带的wrapper操作也是可以的，项目中的gradlew,gradlew.bat脚本用的就是wrapper中规定的gradle版本

| 参数名                    | 说明                              |
| ------------------------- | --------------------------------- |
| --gradle-version          | 用于指定使用的Gradle版本          |
| --gradle-distribution-url | 用于指定下载Gradle发行版的url地址 |

`gradle wrapper -gradle-version=4.4`:升级wapper版本号，只是修改gradle.properties中wrapper版本号，未实际下载

注意：如果没有配置GRADLE_USER_HOME环境变量，则会在当前用户目录下的.gradle文件夹中

![image-20230211011701667](.\images\image-20230211011701667.png)

|      字段名      |                        说明                        |
| :--------------: | :------------------------------------------------: |
| distributionBase |        下载的gradle压缩包解压后存储的主目录        |
| distributionPath | 相对于distributionBase的解压后的Gradle压缩包的路径 |
|   zipStoreBase   |            同distributionBase，存放zip             |
|   zipStorePath   |            同distributionPath，存放zip             |
| distributionUrl  |             Gradle发行版压缩包下载地址             |

注意：如果更改了IDEA配置的use gradle from ，则使用gradle图形界面和控制台使用gradle命令的版本可能不一致，图形界面使用的是warpper文件中的版本，控制台使用的是本地配置的gradle版本

![image-20230212005025312](.\images\image-20230212005025312.png)

## 测试类

```groovy
tasks.named('test') {
    enabled(false) // build时是否生成报告
    useJUnitPlatform() //使用Junit5
    include('com/xhh/**')
}
```

## 生命周期

Gradle项目的生命周期分为三大阶段：Initializaiton -> Configuration -> Execution。

![image-20230212171053585](.\images\image-20230212171053585.png)

## settings文件

作用：主要是在项目初始化阶段确定引入哪些工程需要**加入到项目**构建中，为构建项目工程树做准备

工程数：gradle中有工程树的概念，类似于maven中的project和module

内容：里面主要定义了当前gradle项目及子project的项目名称

位置：必须放在当前项目根工程目录下

名字：为settings.gradle文件，不能发生变化

对应实例：与org.gradle.api.initialization.Settings实例是一一对应的关系。每个项目只有一个settings文件

关注：作为开发者只需要关注include方法。使用相对路径引入子工程。一个子工程只有在settings文件中配置了才会被gradle识别，这样在构建的时候才会被包含进去

```groovy
rootProject.name = "root"
// 子工程
include 'subject01'
include 'subject02'

//包含子工程下的子工程名称
include 'subjcet01:subproject011'
include 'subject02:subproject012'
```

项目名称中`:`相当于路径中的`/`。如果以`:`开头则标识相对于root.project，然后gradle会为每个带有build.gradle脚本文件的工程构建一个与之对应的project对象

## Task

项目本质上是Task对象的集合。一个Task标识一个逻辑上较为独立的执行过程，比如编译java源代码，拷贝文件，打包jar文件，甚至可以是执行一个系统命令。另外，一个Task可以读取和设置project的Property以完成特定的操作

### 用法

```groovy
def map = ["action" : {println 'action start ....'}]

// 启动gradle -i task1
task (map,"task1") {
    // 任务的配置段：在配置阶段执行
    println "hello world"
    // 任务的行为：在执行阶段执行，doFist会在doLast执行之前执行
    doFirst() {
        println "doFirst"
    }

    doLast() {
        println "doLast"
    }
}


task1.doFirst {
    println "doFirst outer"
}

task1.doLast{
    println 'doLast outer'
}
```

![image-20230212183001862](.\images\image-20230212183001862.png)

本质上类似于一个链表，中间放action,左边放doFisrt，先放入的后执行，右边放入doLast，先放入的先执行

### 任务的依赖方式

```groovy
task 'A' {
    doLast() {
        println "A"
    }
}

task 'B' {
    doLast() {
        println "B"
    }
}

// 参数依赖
task 'C'(dependsOn: ['A','B']){
    // 内部依赖
    dependsOn = ['A', 'B']  或者  dependsOn('A,B')
    
    // 跨项目依赖，依赖根工程下的subject01项目中的taskA
    dependsOn(":subject01:A")
    doLast() {
        println "C"
    }
}

C.dependsOn = ['A', 'B']
```

当一个Task依赖多个Task的时候，被依赖的Task之间如果没有依赖关系，那么他们的执行顺序是随机的，无影响

重复依赖的Task只会执行一次。如：A->B,C   B->C  任务A依赖B,C，任务B依赖C，那么C只会执行一次

### 任务执行

语法：`gradle [taskName...] [--option-name...]`

> 常见任务

`gradle build`：构建项目编译、测试、打包等操作

`gradle run`：运行一个服务，需要application插件支持，并且指定了主启动类才能运行

`gradle clean`：清空当前项目的build目录

`gradle init`：初始化gradle项目使用

`gradle wrapper`：生成wrapper文件夹

> 项目报告相关任务

`gradle projects`：列出所选项目以及子项目列表，以层次结构的形式显示

`gradle tasks`：列出所选项目【当前project，不包含父子】的已分配给任务组的任务

`gradle tasks --all`：列出所选项目的所有任务

`gradle tasks --group = "build setup"`：列出所选项目中指定分组的任务

`gradle help --task someTask`：显示某个任务的详细信息

`gradle dependencies`：查看整个项目的依赖信息，以依赖树的方式显示

`gradle properties`：列出所选项目的属性列表

> 调试相关

`-h, --help`：查看帮助信息

`-v, --version`：打印Gradle，Groovy，Ant，JVM和操作系统版本信息

`-S, --full-stacktrace`：打印所有异常的完整（非常详细）堆栈跟踪信息

`-s, --stacktrace`：打印出用户异常的堆栈跟踪（例如编译错误）

`-Dorg.gradle.daemon.debug=true`:调试gradle守护进程

`-Dorg.gradle.debug=true`：调试Gradle客户端（非daemon）进程

`-Dorg.gradle.debug.port = xxx`：指定启用调试时要监听的端口号。默认值为5005

> 性能选项：gradle.peoperties文件

```properties
# gradle.propeties里面定义的属性是全局的，可以在各个模块的build.gradle里面直接引用
# JVM堆内存大小
org.gradle.jvmargs=-Xmx5120 -XX:MaxPermSize=1280m -Dfile.encoding=UTF-8
# 通过开启守护进程，下一次构建的时候，将会连接这个守护进程进行构建，而不是重新fork一个gradle构建进程
org.gradle.daemon = true
# 按需加载
org.gradle.configureondemand=true
# 并行编译
org.gradle.parallel = true
# 开启gradle缓存
org.gradle.caching = true
```

> 日志选项

`-Dorg.gradle.logging.level = (quiet,warn,lifecycle,info,debug)`

`-q,-quiet`：只能记录错误信息

`-w,-warn`：设置日志级别为warn

`-i, -info`：将日志级别设置为info

`-d, -debug`：debug

> 其他

`gradle build --return-tasks`：强制执行任务，忽略up-to-date

`gradle build -continue`：忽略前面失败的任务，继续执行，而不是在遇到第一个失败的任务就立即停止执行

`gradle init --type pom`：**将maven项目转为gradle项目（根目录执行）**

拓展：gradle 任务名缩写：任务名支持驼峰命名分割的任务名缩写，如connectTask, gradle cT.

### 任务定义方式

任务定义方式，总体分为两大类，一种是通过Project中的`task()`方法，一种是通过`tasks对象的create或者register方法`

```groovy
task('A', { //任务名称，闭包作为参数
    println 'A'
})

task('A') { //闭包作为最后一个参数可以直接在括号中拿出来
    println 'A'
}

task A { //groovy简写方式
    println 'A'
}
// 上面三种都是同一种


tasks.create('A') { //tasks.create方法
    println 'A'
}

tasks.register('A') { //register执行的是延迟创建，只有当task被需要使用时才会被创建
    println 'A'
}
```

可以在创建任务的同时指定任务的属性

`type:`基于一个存在的task来创建，和类继承类似

`overwrite`:是否替换存在的task，这个和type配合使用

`dependsOn`:用于配置任务的依赖

`action`:添加到任务中的一个action或者闭包

`description`:用于配置任务的描述

`group`:用于配置任务的分组

### 任务的开启和关闭

`enable(false)`

### 任务超时

`timeout = Duration.ofMills(500) `

### 任务查找

```groovy
tasks.findByName('A').doFisrt{println 'A'}

tasks.findByPath(':A').foFirt{pritnln 'A'}
```

默认任务

`defaultTasks 'A','B'`：项目build时自动执行A,B任务

## Gradle中文件操作

### 本地文件

```groovy
//相对路径
File configFile = file("src/conf.xml")
configFile.createNewFile();
//绝对路径
configFile = file('D:\\conf.xml')

//使用文件对象
configFile = new File("src/config.xml")
```

### 文件集合

```groovy
FileCollection files = files("src/conf1.xml", "src/conf2.xml")
```

### 文件树

```groovy
ConfigurabelFileTree configurableFileTree = fileTree("src/main")
```

### 文件拷贝

使用Copy任务来拷贝文件

```groovy
task copyTask(type: Copy) {
    from 'src/main/resources'
    into 'build/config'
}
```

form方法接收的参数和文件集合files()一样，当参数为一个目录时，该目录下的所有文件都会被拷贝到指定目录下（目录自身不会被拷贝）；当参数为一个文件时，该文件会被拷贝到指定目录；如果参数指定的文件不存在，则忽略；当参数为一个Zip文件，该压缩问价的内容会被拷贝到指定目录。

### 文件归档

```groovy
task myZip(type: Zip) {
    from 'src/main'
    into 'build' //保存到build目录
    baseName = 'myGrame'
}
```

使用Projects.zipTree()或者tarTree()方法创建访问ZIP压缩包的文件树

## Dependencies（依赖）

依赖方式：

- 本地依赖：依赖本地的某个jar包，可通过文件集合、文件树方式指定

```groovy
implementation files('lib/msysql.jar', 'lib/log4j.jar')
implementation fileTree("dir":"lib", includes: ['*.jar'], excludes:[''])
```



- 项目依赖：依赖某个project

**需要注意在settings.gradle文件中引入 include 'subproject01'**

```groovy
implementation project(':subproject01')
```



- 直接依赖： 依赖的类型   依赖的组名  依赖的名称  依赖的版本号

```groovy
implementation group: 'com.fasterxml.jackson.datatype', name: 'jackson-datatype-guava', version: '2.8.8'
// 简写
implementation 'com.fasterxml.jackson.datatype:jackson-datatype-guava:2.8.8'
```

### 依赖的类型

类似maven的scope标签

| compileOnly        | 由java插件提供，曾短暂叫做provided，后续版本改为compileOnly，适用于编译期需要而不需要打包的情况 |
| ------------------ | ------------------------------------------------------------ |
| runtimeOnly        | **由java插件提供**，只在运行期有效，编译时不需要，比如mysql驱动包，取代老版本runtime |
| **implementation** | **由java插件提供**，针对源码'src/main'目录，在编译，运行时都有效，取代老版本compile |
| testCompileOnly    | **由java插件提供**，用于编译测试的依赖项，运行时不需要       |
| testRuntimeOnly    | **由java插件提供**，只在测试运行时使用，而不是在测试编译时需要，取代老版本testRuntime |
| testImplementation | **由java插件提供**，针对测试代码'src/test'目录，取代老版本testCompile |
| providedCompile    | war插件提供支持，编译，测试阶段代码需要依赖此类jar包，而运行阶段容器已经提供了相应的支持，所以无需将这些文件导入war包，例如servlet-api.jar,jsp-api.jar |
| compile            | gradle7移除                                                  |
| runtime            | gradle7移除                                                  |
| api                | java-library插件提供支持，这些依赖可以传递性的导出给使用者，用于编译时和运行时，取代老版本compile |
| compileOnlyApi     | java-library插件提供支持，在声明模块和使用者在编译时需要的依赖库，但只运行时不需要 |

### api和implementation区别

|          |                      api                       |                implementation                |
| -------- | :--------------------------------------------: | :------------------------------------------: |
| 编译时   | 能进行依赖传递，底层变，全部都要变，编译速度慢 | 不能进行依赖传递，不用全部都要变，编译速度快 |
| 运行时   |     运行时会加载，所有模块的class都要加载      |   运行时会加载，所有模块的class都要被加载    |
| 应用场景 |       适用于多模块依赖，避免重复依赖模块       |                多数情况下使用                |

![image-20230212232256166](.\images\image-20230212232256166.png)

编译时：如果libC的内容发生变化，由于使用的是api依赖，依赖会传递，所以libC，libA，projectX都要发生变化，都需要重新编译，速度慢；运行时：libA，libC，projectA中的class都要被加载

编译时：如果libD发生变化，由于implementation方式，依赖不会传递，只有libD，libB要变化并重新编译，速度快；运行时：libD，libB，projectA中的class都要被加载

> 案例分析

api的适用场景是多module依赖，moduleA工程依赖了moduleB，同时moduleB又依赖了moduleC，moduleA工程也需要去依赖moduleC，这个时候避免重复依赖，可以使用moduleB api依赖的方式去依赖moduleC，modelA工程只需要依赖moduleB即可

ABCD四个模块

- A implementation B, B implementation C, 则A不能使用C
- A implementation B, B api C, 则A可以使用C
- A implementaion B, B implementation C, C api D，则B可以使用D，A不能使用D
- A implementation B, B api C , C api D ,A 可以使用D

### 依赖冲突和解决方案

依赖冲突：在编译过程中，如果存在某个依赖的多个版本，构建系统应该选择哪个进行构建的问题

Project -- >  B ---->log4j 1.4.2        ProjcetA -->C ---->log4j 2.2.4

A，B，C都是本地子项目module，log4j是远程依赖

编译时：B，C使用的log4j版本不一致，B和C之间没有冲突

打包时：只能有一个版本的代码最终打包进A对应的jar| war包，对于Gradle来说存在冲突

**默认情况下，Gradle会使用最新的版本，实际开发中，建议使用官方方案。当然，Gradle也提供了一系列的解决依赖的方法：exclude方法移除依赖，不允许依赖传递，强制使用某个版本**

> Exclude排除

```groovy
implementation 'org.hibernate:hibernate-core:3.6.3.Final' {
    exclude group: 'org.slf4j'
    exclude module 'org.slf4j'
    exclude group: 'org.slf4j',module 'org.slf4j'
}

implementation 'org.slf4j:slf4j-api:1.4.0'
```

> 不允许依赖传递

```groovy
implementation 'org.hibernate:hibernate-core:3.6.3.Final' {
    transitive(false) //不建议使用，关闭后，内部的所有依赖都不会添加到编译和运行时的类路径
}

implementation 'org.slf4j:slf4j-api:1.4.0'
```

> 强制使用某个版本

```groovy
implementation 'org.hibernate:hibernate-core:3.6.3.Final' {
    // 方式一
    version {
        strictly("1.4.0")
    }
}
// !!强制使用 方式二
implementation 'org.slf4j:slf4j-api:1.4.0!!'
```

### 查看jar包冲突

```groovy
// 配置，当Gradle构建遇到依赖冲突时，就立即构建失败
configuration.all {
    Configuration configuration ->
    	configuration.resolutionStrategy.failOnVersionConfict()
}
```

## Gradle插件