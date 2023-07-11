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

### DDL,DML,DCL

- DDL（数据定义语言）：用于定义和管理数据库对象，如表，视图，索引，它包括创建、修改和删除数据库对象的命令

  - `CREATE`：用于创建数据库对象，如表、视图、索引等。

  - `ALTER`：用于修改数据库对象的结构，如添加、修改或删除列、约束等。

  - `DROP`：用于删除数据库对象，如表、视图、索引等。

  - `TRUNCATE`：用于删除表中的所有数据。

- DML（数据操作语言）：用于对数据库中数据进行增删改查操作。常见命令`select`，`insert`，`update`，`delete`

- DCL（数据控制语言）：用于数据库的访问控制和安全性管理。它包括授予或者撤销对象的访问权限。

  - `GRANT`：用于授予用户或角色对数据库对象的访问权限。
  - `REVOKE`：用于撤销用户或角色对数据库对象的访问权限。

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

### SQL标准

SQL有两个主要的标准，分别是`SQL92`和`SQL99`。一般来说92的形式更简单，但是写的SQL语句会比较长，可读性较差。而99相比于92来说语法更加复杂，但是可读性更强。

## 多表查询的分类

### 等值连接与非等值连接

非等值连接

```mysql
select e.last_name, e.salary, j.grade_level from employees e, job_grades j
where e.salary between j.lowest_sal and j.highest_sal;
```

### 自连接与非自连接

```mysql
select emp.employee_id, emp.last_name, mgr.employee_id, mgr.last_name
from employees emp, employees mgr
where emp.manager_id = mgr.employee_id;
```

### 内连接与外连接

MySQL不支持SQL92语法中外连接的写法

```sql
select last_name, department_name from employees e, department d
where e.department_id = d.department_id(+);
```

### 满外连接

MySQL不支持这种形式的满外连接

```sql
select last_name, department_name from employees e FULL OUTER JOIN department d
on e.department_id = d.department_id;
```

### UNION的使用

==合并查询结果==：利用UNION关键字，可以给出多条select语句，并将他们的结果组合成单个结果集。合并时，两个表对应的列数和类型必须相同，并且相互对应。各个select语句之间使用UNION或者UNION ALL关键字分割。

==UNION==：操作符返回两个查询的结果集的并集，去除重复记录

==UNION ALL==：操作符返回两个查询的结果集的并集。对于两个结果集的重复部分，不去重。

注意：执行UNION ALL语句时所需要的资源比UNION语句少。如果明确知道合并数据后的结果数据不存在重复数据，或者不需要去重，则尽量使用UNION ALL语句，以提高查询的效率。

### 7种JOIN图解

![image-20230412220839305](.\images\image-20230412220839305.png)

其中FULL OUTER JOIN 在MySQL中不支持，需要结合UNION查询

> 图6可以看作是图1 + 图5的结合

```sql
select a, b from a left join b on a.key = b.key
union all
select a, b from a right join b on a.key = b.key where a.key is null
```

### NATURAL JOIN

自然连接：自动查询两张连接表中**所有相同字段**，然后进行等值连接。

## 日期和时间函数

### 日期与时间戳的转换

`UNIX_TIMESTAMP()`：以UNIX时间戳的形式返回当前时间。

`UNIX_TIMESTAMP(date)`：将时间date以UNIX时间戳的形式返回。

`FROM_UNIXTIME(timestamp)`：将UNIX时间戳的时间转换为普通格式的时间。

### 流程处理函数

`IF(value, value1, value2)`：如果value的值为TRUE，返回value1,否则返回value2

`IFNULL(value1, value2)`：如果value1不为null，返回value1，否则返回value2

`CASE WHEN 条件1 THEN 结果1 WHEN 条件2 THEN 结果2 ...[ELSE 结果3] END`：相当于if...else if ...else...

`CASE expr WHEN 常量1 THEN 值1 WHEN 常量2 THEN 值2 ... [ELSE 结果3] END`：相当于switch...case...

## SQL的执行原理

SELECT是先执行FROM这布。在这个阶段，如果是多张表联查，还会经历下面的几个步骤：

1.首先通过CROSS JOIN求笛卡尔积，相当于得到虚拟表vt1-1;

2.通过ON进行筛选，在虚拟表vt1-1的基础上进行筛选，得到虚拟表vt1-2;

3。添加外部行。如果我们使用的是左连接或者右连接或者全连接，就会涉及到外部行，也就是在虚拟表vt1-2的基础上增加外部行，得到虚拟表vt1-3。

如果操作两张以上的表，还会重复以上的步骤，直到所有的表都被处理完为止。

当我们查询到了数据表的原始数据vt1，就可以在此进行`WHERE`阶段，得到vt2。

然后进入`GROUP BY`和`HAVING`阶段，得到vt3,vt4。

当完成条件筛选部分后，就可以筛选表中提取的字段，也就是进入到`SELECT DISTINCT`阶段

最终进行`ORDER BY`和`LIMIT`

## 子查询

### 在SELECT中，除了GROUP BY 和LIMIT之外，其他位置都可以声明子查询

### 单行子查询（子查询中的值只能有一个）

| 操作符 | 含义     |
| ------ | -------- |
| =      | 等于     |
| >      | 大于     |
| >=     | 大于等于 |
| <      | 小于     |
| <=     | 小于等于 |
| <>     | 不等于   |

### 多行子查询

| 操作符 | 含义                                                         |
| ------ | ------------------------------------------------------------ |
| IN     | 等于列表中的==任意一个==                                     |
| ANY    | 需要和单行比较操作符一起使用，和子查询返回的==某一个==值比较 |
| ALL    | 需要和单行比较操作符一起使用，和子查询返回的==所有==值比较   |
| SOME   | 实际上是ANY的别名，作用相同，一般常使用ANY                   |

### 空值问题

```sql
SELECT last_name
FROM employees
WHERE employee_id NOT IN (SELECT manager_id FROM employees);
```

上面这条sql查询出来的结果为空值，因为SELECT manager_id FROM employees的结果存在一条空值，==IN可以接收Null值进行OR运算，相当于多个OR，但是NOT IN相当于多个and连接，null做运算导致结果为空==

### 相关子查询

执行流程：如果子查询的执行依赖于外部查询，通常情况下都是因为子查询中的表用到了外部的表，并进行了条件关联，因此每执行一次外部查询，子查询都要重新计算一次，这样的子查询就称为==关联子查询==

![image-20230504222421672](.\images\image-20230504222421672.png)

### 子查询举例

```sql
# 查询平均工资最高的Job信息，employees表

# 方式1 多层子查询
select *
from jobs
where job_id = (select job_id
                from employees
                group by job_id
                having AVG(salary) = (
                    # 查询最高工资 
                    select max(avg_sal)
                    from (
                             # 查询每组job_id的平均工资
                             select AVG(salary) avg_sal
                             from employees
                             group by job_id) t_job_avg_sal));


# 方式2 使用all
select *
from jobs
where job_id = (select job_id
                from employess
                group by job_id
                having avg(salary) >= all (select AVG(salary)
                                           from employess
                                           group by job_id));
                                           
# 方式3  使用limit
select *
from jobs
where job_id = (select job_id
                from employess
                group by job_id
                having avg(salary) = (select AVG(salary) avg_sal
                                           from employess
                                           group by job_id
                                     	   order by avg_sal DESC 
                                     	   limit 0,1));

# 方式4 联表查询
select j.*
from jobs j,
     (select job_id, avg(salary) avg_sal
      from employess
      group by job_id
      order by avg_sal DESC
      limit 0,1) t_avg_salary
where j.id = avg_salary.job_id;
```

## 数据精度说明

对于浮点类型，在MySQL中单精度值使用4个字节，双精度使用8个字节。

- MySQL允许使用非标准语法：`FLOAT(M,D)`或者`DOUBLE(M,D)`。M称为精度，D称为标度。（M,D）中M=整数位+小数位，D=小数位，D<=M<=255，0<=D<=30。例如，定义为FLOAT(5,2)的一个列可以显示为-999.99-999.99，如果超过这个范围会报错。

### 浮点数精度丢失问题

在一张有double属性(double未指定M,D)的表中插入0.47, 0.44, 0.19三条数据，使用sum查询到的结果为==1.09999999999999==,当把属性改成float（未指定M,D）时，得到的结果是==1.0999999940395355==，误差更大了。

原因：MySQL用4个字节存储FLOAT类型数据，用8个字节来存储DOUBLE类型数据，他们都是采用二进制的方式来进行存储的。比如9.625，用二进制来表示为1001.101，或者表达成1.001101*2^3。如果尾数不是0或者5（比如9.624），就无法用一个二进制来精确表达，进而造成数据的误差。

### 定点数类型

- MySQL中的定点数类型只有DECIMAL一种类型。

  使用DECIMAL(M,D)的方式表示高精度小数。其中，M被称为精度，D被称为标度。0<=M<=65，0<=D<=30，D<M，例如定义DECIMAL(5,2)，表述范围为-999.99-999.99。

- DECIMAL(M,D)的最大取值范围与DOUBLE类型一样，但是有效的数据范围是由M和D决定的，DECIMAL的存储空间并不是固定的，由精度值M决定，总共占用的存储空间为M+2个字节。也就是说，在一些对精度要求不高的场景下，比如占用同样字节长度的定点数，浮点数表达的数值范围可以更大一些。

- 定点数在MySQL内部是以==字符串==的形式进行存储，这说明了它一定是精确的。

- 当DECIMAL不指定精度和标度时，其默认为DECIMAL(10,0)。当数据的精度超出了定点数类型的精度范围时，则MySQL同样会进行四舍五入处理。

## 视图

- 视图是一种虚拟表，本身是不具有数据的，占用的内存空间很少。
- 视图建立在已有表的基础上，视图赖以建立的这些表称为基表。
- 视图的本质，可以看作是存储起来的SELECT语句

### 创建视图

```sql
create view 视图名称 [(字段列表)]
as 查询语句
```

## 存储过程

一组预先编译的SQL语句的封装。执行过程：存储过程预先存储在MySQL服务器上，需要执行的时候，客户端只需要向服务器端发出调用存储过程的命令，服务器端就可以把预先存储好的这一系列SQL语句全部执行。

### 分类

存储过程的参数类型可以是IN,OUT和INOUT：

- 没有参数（无参数无返回）
- 仅仅带IN类型（有参数无返回）
- 仅仅带OUT类型（无参数有返回）
- 既带IN又带OUT（有参数有返回）
- 带INOUT（有参数有返回）

```sql
create procedure 存储过程名(IN|OUT|INOUT 参数名 参数类型, ...) 
[characteristics ...]
begin
	存储过程体
end
```

示例

```sql
# 存储过程的创建
delimiter $;
create procedure select_all_user()
begin 
    select * from user;
end $;
delimiter ;

# 存储过程的调用
call select_all_user();

# 输出参数到ms
delimiter $;
create procedure select_all_user_ms(OUT ms varchar(255))
begin 
    select name into ms from user where id = 1;
end $;
delimiter ;

call select_all_user_ms(@ms);
select @ms;

# 根据id查询指定的字段
delimiter $;
create procedure select_user_by_id(in id_key bigint )
begin 
    select id , name, date_created, teacher_id from user where id = id_key;
end $;
delimiter ;

select * from user where id = 11;
call select_user_by_id(11);

# 根据id查询指定字段，并且输出
DELIMITER $$
CREATE PROCEDURE `get_user_by_id`(IN id_key BIGINT, OUT out_name VARCHAR(255), OUT out_date_created DATETIME, OUT out_teacher_id BIGINT)
BEGIN
    SELECT name, date_created, teacher_id INTO out_name, out_date_created, out_teacher_id FROM user WHERE id = id_key;
END$$
DELIMITER ;

CALL get_user_by_id(11, @name, @date_created, @teacher_id);
SELECT @name, @date_created, @teacher_id;
```

## 存储函数

```sql
create function 函数名(参数名 参数类型, ...)
returns 返回值类型
[characteristics ...]

BEGIN
	函数体 #函数体中肯定有 return 语句
END
```

```sql
# 存储函数
delimiter //
create function get_user()
returns varchar(255)
    deterministic 
    contains sql 
    reads sql data 
begin
    return (select name from user where id = 1);
end //

# 调用存储函数
select get_user();
```

## 对比存储函数和存储过程

|          | 关键字    | 调用语法        | 返回值              | 应用场景                         |
| -------- | --------- | --------------- | ------------------- | -------------------------------- |
| 存储过程 | PROCEDURE | CALL 存储过程() | 理解为有0个或者多个 | 一般用于更新                     |
| 存储函数 | FUNCTION  | SELECT 函数()   | 只能是一个          | 一般用于查询结果为一个值并返回时 |

另外，==存储函数可以放在查询语句中使用，存储过程不行==。反之，存储过程的功能更加强大，包括能够执行对表的操作（比如创建表，删除表等）和事务操作，这些功能是存储函数不具备的。

## 触发器

创建触发器：

```sql
create trigger 触发器名称
{BEFORE|AFTER} {INSTERT|UPATE|DELETE} ON 表名
FOR EACH ROW
触发器执行的语句块;
```

## 窗口函数

在需要用到分组统计的结果对每一条记录进行计算的场景下，使用窗口函数更好。

| 函数分类 | 函数               | 函数说明                                      |
| -------- | ------------------ | --------------------------------------------- |
| 序号函数 | ROW_NUMBER()       | 顺序排序                                      |
|          | RANK()             | 并列排序，会跳过重复的序号，比如序号为1，1，3 |
|          | DENSE_RANK()       | 并列排序，不会跳过重复的序号，比如序号1，1，2 |
| 分布函数 | PERCENT_RANK()     | 等级值百分比                                  |
|          | CUME_DIST()        | 累计分布值                                    |
| 前后函数 | LAG(expr, n)       | 返回当前行的前n行的expr的值                   |
|          | LEAD(expr, n)      | 返回当前行的后n行的expr的值                   |
| 首位函数 | FIRST_VALUE(expr)  | 返回第一个expr的值                            |
|          | LAST_VALUE(expr)   | 返回最后一个expr的值                          |
| 其他函数 | NTH_VALUE(expr, n) | 返回第n个expr的值                             |
|          | NTILE(n)           | 将分区中的有序数据分为n个桶，记录桶编号       |

### 语法结构

窗口函数的语法结构是：

```sql
函数 OVER([PARTITION BY 字段名 ORDER BY 字段名 ASC|DESC])
```

或者是：

```sql
函数 OVER 窗口名 _WINDOW 窗口名 AS ([PARTITION BY 字段名 ORDER BY 字段名 ASC|DESC])
```

## MySQL5更改默认字符集

默认路径：

```git
# 如果是docker启动并且指定了配置文件的挂载路径，则在挂载目录下新建my.cnf文件
vim /etc/my.cnf
```

在my.cnf文件中添加配置（注意点：如果数据库在更改字符集之前就已经存在，则此更改不会对旧数据库生效）

```sql
character_set_server=utf8
```

```cnf
[mysqld]
## 设置servier_id，同一局域网中需要唯一
server_id=101
## 指定不需要同步的数据库名称
binlog-ignore-db=mysql
## 开启二进制日志功能
log-bin=mall-mysql-bin
## 设置二进制日志使用内存大小
binlog_cache_size=1M
## 设置使用的二进制日志格式(mixed,statement,row)
binlog_format=mixed
## 二进制日志过期清理时间，默认为0，表示不自动清理
expire_logs_days = 7
## 跳过主从复制中遇到的所有错误或指定类型的错误，避免slave端复制中断
## 如1062错误：是指一些主键重复，1032错误指主从数据库数据不一致
slave_skip_errors=1062
charater_set_server=utf8
```

重启mysql

![image-20230711215942506](.\images\image-20230711215942506.png)