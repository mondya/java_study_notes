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
|   zipStoreBase   |                        同上                        |
|   zipStorePath   |                        同上                        |
| distributionUrl  |             Gradle发行版压缩包下载地址             |

