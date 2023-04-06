# MySQL进阶

## 数据库相关概念

| **DB：数据库（database）**                                   |
| ------------------------------------------------------------ |
| 存储数据的仓库，其本质是一个文件系统。它保存了一系列有组织的数据 |
| **DBMS：数据库管理系统（Database Management System）**       |
| 是一种操作和管理数据库的大型软件，用于建立、使用和维护数据库，对数据库进行同一管理和控制。用户通过数据库管理系统访问数据库中表内的结构 |
| **SQL：结构化查询语言（Structured Query Languate）**         |
| 专门用来与数据库通信的语言                                   |



## RDBMS与非RDBMS

### 关系型数据库（RDBMS）

- 关系型数据库模型是把复杂的数据结构归结为简单的==二元关系==，即二维表格形式。
- 关系型数据库以==行（row）==和==列（column）==的形式存储数据，这一系列的行和列被称为==表（table）==，一组表组成了一个==库（database）==。
- 关系型数据库，就是建立在==关系模型==基础上的数据库。

#### 优势

- **复杂查询**

  可以用SQL语句方便的在一个表以及多个表之间做非常复杂的数据查询

- **事务支持**

  使得对于安全性能很高的数据访问要求得以实现

### 非关系型数据库（非RDBMS）

非关系型数据库，可以看成传统关系型数据库的功能==阉割版本==，基于键值对存储数据，不需要经过SQL层的解析，==性能非常高==。

## MySQL的编码设置

MySQL5.7中，创建表默认编码`latin1`，表中插入中文数据存在问题

> 解决方式

步骤1：查看编码命令

```sql
show variables like 'character_%';
show variables like 'collation_%';  # 字符集的比较规则
```

步骤2：修改mysql的数据目录下的`my.ini`配置文件

```mysql
[mysql] #在大概63行左右，在其下添加
default-character-set=utf8 #默认字符集

[mysqld] # 大概76行左右
# 注：utf8mb4支持emoji
character-set-server=utf8
collation-server=utf8_general_ci
```

## SQL相关

### 空值运算问题

空值参与运算，其结果也一定为空，使用`IFNOLL(字段名, 默认值)`函数赋默认值

### 查询常数

```mysql
select 'user表', user.* from user;
```

![image-20230405164108981](.\images\image-20230405164108981.png)

### 显示表中字段详细信息

`describe/desc 表名`

### 等号运算符

> 规则

- 如果等号两边的值，字符串或表达式都为字符串，则MySQL会按照字符串进行比较，其比较的是每个字符串中的ANSI编码是否相等（默认忽略大小写）
- 如果等号两边的值都是整数，则直接进行比较
- 如果等号两边一个是整数，一个是字符串，则MySQL会将字符串转为数字（转成功为数字，不成功为0）
- 如果等号两边的值，字符串或表达式中有一个为null，则比较结果为null

#### 安全等于运算符

安全等于运算符`<=>`与等于运算符`=`的作用是相似的，==唯一的区别==是`<=>`可以用来对NULL进行判断。在两个操作符都是NULL时，其返回值为1，而不是NULL；当一个操作符为NULL时，其返回值为0，而不是NULL。

#### 不等于运算符(!=或者<>)

### 模糊查询

在模糊查询时需要注意特殊字符`_`，`%`，这两个在模糊查询时需要使用转义字符`\`，输入单个_,%会被转义成本身，不会当作通配符

```mysql
select * from user where name like '%\${searchValue}%';
```

或者使用ESCAPE，作用是声明ESCAPE之后的字符不作为通配符使用

```mysql
select * from user where name like '%/${searchValue}%' ESCAPE '/';
```

或者使用内置函数INSTR(表中的字段名,${searchValue})

```mysql
# 	mysql的内置函数instr(filed,str)，作用是返回str子字符串在filed字符串的第一次出现的位置。当instr(filed,str)=0时，表示子符串str不存在于字符串filed中，因此可以用来实现mysql中的模糊查询，与like用法类似。
select * from user where instr(name, '_');
```

#### 正则表达式（REGEXP \ RLIKE）

// todo，了解即可

### 逻辑运算符

#### XOR（非运算）

只要其中任何一个操作数据为null时，结果返回null;如果对于非null的操作数，如果两个操作数都是非0或者都是0，则返回值为0;如果一个为0，另一个为非0值，回值为1

```mysql
where a > 1 XOR b < 3; # 查询 a > 1 并且不满足 b < 3 的数据或者查询 b < 3 但是不满足 a > 1 的数据
```



