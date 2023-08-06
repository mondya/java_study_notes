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

```properties
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
# 否则，如下
vim /etc/my.cnf
```

在my.cnf文件中添加配置（注意点：如果数据库在更改字符集之前就已经存在，则此更改不会对旧数据库生效）

```properties
character_set_server=utf8mb4
collation_server=utf8mb4_unicode_ci
```

```properties
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
charater_set_server=utf8mb4
collation_server=utf8mb4_unicode_ci
```

重启mysql

![image-20230711215942506](.\images\image-20230711215942506.png)

### utf8和utf8mb4

`utf8`：又叫utf8mb3，被阉割过的utf8字符集，只使用1-3个字节表示字符

`utf8mb4`：正宗的utf8字符集，使用1-4个字节表示字符（能够处理表情符号）

## 字符集的比较规则

```sql
# mysql5和mysql8在字符集的比较上有区别
show character set;
```

| Charset  | Description                     | Default collation     | Maxlen |
| :------- | :------------------------------ | :-------------------- | :----- |
| big5     | Big5 Traditional Chinese        | big5\_chinese\_ci     | 2      |
| dec8     | DEC West European               | dec8\_swedish\_ci     | 1      |
| cp850    | DOS West European               | cp850\_general\_ci    | 1      |
| hp8      | HP West European                | hp8\_english\_ci      | 1      |
| koi8r    | KOI8-R Relcom Russian           | koi8r\_general\_ci    | 1      |
| latin1   | cp1252 West European            | latin1\_swedish\_ci   | 1      |
| latin2   | ISO 8859-2 Central European     | latin2\_general\_ci   | 1      |
| swe7     | 7bit Swedish                    | swe7\_swedish\_ci     | 1      |
| ascii    | US ASCII                        | ascii\_general\_ci    | 1      |
| ujis     | EUC-JP Japanese                 | ujis\_japanese\_ci    | 3      |
| sjis     | Shift-JIS Japanese              | sjis\_japanese\_ci    | 2      |
| hebrew   | ISO 8859-8 Hebrew               | hebrew\_general\_ci   | 1      |
| tis620   | TIS620 Thai                     | tis620\_thai\_ci      | 1      |
| euckr    | EUC-KR Korean                   | euckr\_korean\_ci     | 2      |
| koi8u    | KOI8-U Ukrainian                | koi8u\_general\_ci    | 1      |
| gb2312   | GB2312 Simplified Chinese       | gb2312\_chinese\_ci   | 2      |
| greek    | ISO 8859-7 Greek                | greek\_general\_ci    | 1      |
| cp1250   | Windows Central European        | cp1250\_general\_ci   | 1      |
| gbk      | GBK Simplified Chinese          | gbk\_chinese\_ci      | 2      |
| latin5   | ISO 8859-9 Turkish              | latin5\_turkish\_ci   | 1      |
| armscii8 | ARMSCII-8 Armenian              | armscii8\_general\_ci | 1      |
| utf8     | UTF-8 Unicode                   | utf8\_general\_ci     | 3      |
| ucs2     | UCS-2 Unicode                   | ucs2\_general\_ci     | 2      |
| cp866    | DOS Russian                     | cp866\_general\_ci    | 1      |
| keybcs2  | DOS Kamenicky Czech-Slovak      | keybcs2\_general\_ci  | 1      |
| macce    | Mac Central European            | macce\_general\_ci    | 1      |
| macroman | Mac West European               | macroman\_general\_ci | 1      |
| cp852    | DOS Central European            | cp852\_general\_ci    | 1      |
| latin7   | ISO 8859-13 Baltic              | latin7\_general\_ci   | 1      |
| utf8mb4  | UTF-8 Unicode                   | utf8mb4\_general\_ci  | 4      |
| cp1251   | Windows Cyrillic                | cp1251\_general\_ci   | 1      |
| utf16    | UTF-16 Unicode                  | utf16\_general\_ci    | 4      |
| utf16le  | UTF-16LE Unicode                | utf16le\_general\_ci  | 4      |
| cp1256   | Windows Arabic                  | cp1256\_general\_ci   | 1      |
| cp1257   | Windows Baltic                  | cp1257\_general\_ci   | 1      |
| utf32    | UTF-32 Unicode                  | utf32\_general\_ci    | 4      |
| binary   | Binary pseudo charset           | binary                | 1      |
| geostd8  | GEOSTD8 Georgian                | geostd8\_general\_ci  | 1      |
| cp932    | SJIS for Windows Japanese       | cp932\_japanese\_ci   | 2      |
| eucjpms  | UJIS for Windows Japanese       | eucjpms\_japanese\_ci | 3      |
| gb18030  | China National Standard GB18030 | gb18030\_chinese\_ci  | 4      |

mysql共有41中字符集，其中Default collation列表示这种字符集中一种默认的比较规则，里面包含着该比较规则主要作用于哪种语言，比如unf8_polish_ci表示以波兰语的规则比较，utf8_spanish_ci是以西班牙语的规则比较，utf8_general_ci是一种通用的比较规则。

后缀表示该比较是否区分语言中重音、大小写。具体如下：

| 后缀 | 英文释义           | 描述             |
| ---- | ------------------ | ---------------- |
| _ai  | accent insensitive | 不区分重音       |
| _as  | accent sensitive   | 区分重音         |
| _ci  | case insensitive   | 不区分大小写     |
| _cs  | case sensitive     | 区分大小写       |
| _bin | binary             | 以二进制方式比较 |

最后一列Maxlen，它代表该种字符集表示一个字符最多需要几个字节

## 说明

### utf8_unicode_ci和utf8_general_ci

utf8_unicode_ci和utf8_general_ci对中、英文来说没有实质的区别。

utf8_general_ci校对数据快，但准确度稍差。

utf8_unicode_ci准确度高，但是校对数据稍慢。

一般情况下，使用utf8_general_ci就足够了，但如果你的应用有德语、法语、或者俄语，请一定要用utf8_unicode_ci

### 请求到响应过程中字符集的变化

从客户端发往服务器的请求本质上就是一个字符串，服务器向客户端返回的结果本质上也是一个字符串，而字符串是使用某种字符集编码的二进制数据。这个字符串，从请求到返回结果的过程中伴随着多次字符集的转化，在这个过程中会用到3个系统变量：

| 系统变量                 | 描述                                                         |
| ------------------------ | ------------------------------------------------------------ |
| character_set_client     | 服务器解码请求时使用的字符集                                 |
| character_set_connection | 服务器处理请求时会把请求字符串从character_set_client转为character_set_connection |
| character_set_results    | 服务器向客户端返回数据时使用的字符集                         |

举例：

```sql
set charater_set_connection = gbk;
```

字段s采用gbk字符集

```sql
select * from t where s = '我';
```

- 客户端发送请求所用的字符集，一般情况下客户端所用的字符集和当前操作系统一致（类unix系统所用的是utf8，windows使用的是gbk）。提示：如果使用了可视化工具，则和工具的字符编码有关。

  当客户端使用的是utf8字符集，字符`我`在发送给服务器的请求中的字节形式是`OxE68891`

- 服务器接收到客户端发送来的请求其实是一串二进制的字节，它会认为这串字节采用的字符集是`charater_set_client`(utf8)，解码得到`我`，然后把这串字节转换为`character_set_connection`字符集编码的字符(gbk)，得到的结果为`0xCED2`
- 因为表t的列col采用的是gbk字符集，与`character_set_connection`一致，所以直接到表中找字节值为`0xCED2`的记录。
- 找到`0xCED2`的值，col列是采用gbk进行编码的，所以首先会将这个字节串使用gbk进行解码，得到`我`，然后把这个字符串使用`character_set_results`代表的字符集(utf8)进行编码，得到新字节`0xE68891`，然后发送给客户端。
- 客户端使用utf8解码，得到结果`我`

![image-20230712220156025](.\images\image-20230712220156025.png)

![image-20230712234131208](.\images\image-20230712234131208.png)

### 全局设置字符集

```sql
set names utf8;
```

或者在配置文件中加上

```properties
[client]
default_character_set=utf8
```

## 大小写

```sql
show variables like '%lower_case_table_name%'
```

- 默认为0，大小写敏感
- 设置为1，大小写不敏感。创建的表，数据库都是以小写形式存放在磁盘上，对于sql语句都是转换为小写对表和数据库进行查找。
- 设置为2，创建的表和数据库依据语句上格式存放，凡是查找都是转换为小写进行。

> MySQL在Linux下数据库名、表名、列名、别名大小写规则是这样的：
>
> 1.数据库名、表名、表的别名、变量名是严格区分大小写的；
>
> 2.关键字、函数名在SQL中不区分大小写；
>
> 3.列名（或字段名）与列的别名（或字段别名）在所有情况下都是忽略大小写。

## sql_mode

sql_mode会影响MySQL支持的SQL语法以及它执行的==数据验证检查==。通过设置sql_mode，可以完成不同严格程度的数据校验，有效地保证数据准确性。

MySQL5.6和MySQL5.7默认的sql_mode模式参数不一样：

- 5.6的mode模式默认为空（即==NO_ENGINE_SUBSTITUTION==），可以理解为宽松模式，在这种设置下可以允许一些非法插入操作
- 5.7的mode模式为==STRICT_TRANS_TABLES==，严格模式。用于数据的严格校验，错误数据不能插入，报错，事务回滚。

### 宽松模式

假设给name字段设置为`varchar(10)`，在宽松模式下，插入的数据长度大于10，会截取前10个字符

### 严格模式

按照上面的例子，长度超过10个字符，会发生报错。（此时不允许插入0日期，例如表字段为`TIMESTAMP`属性，如果未声明NULL或者设置DEFAULT值，将自动分配'0000-00-00 00:00:00'，这不满足严格模式，也会发生报错）

## MySQL的数据目录

```bash
find / -name mysql
```

![image-20230713214232683](.\images\image-20230713214232683.png)

### 数据库文件的存放路径：/var/lib/mysql

### 命令目录：/usr/bin和/usr/sbin

### 配置文件目录：/etc/mysql/

## 数据库和文件系统的关系

### 表在文件系统中的表示

#### InnoDB（mysql5）

为了保存表结构，InnoDB在数据目录`/var/lib/mysql`对应的数据库子目录下创建了一个专门用于描述表结构的文件，文件名表示为==表名.frm==。

存储数据分为==**系统表空间**==：默认情况下，InnoDB会在数据目录下创建一个名为`ibdata1`，大小为`12M`（自扩展文件，大小会自动改变）

```properties
[server]
innodb_data_file_path = data1:512M;data2:512M;autoextend
```

这样在mysql启动时会创建这两个512m大小的文件作为==系统表空间==，其中==autoextend==表示自动扩展。==一个mysql服务器只会有一份系统表空间==

==**独立表空间**==：在mysql5.6.6以及之后的版本，InnoDB不会默认地把各个表的数据存储到系统表空间，而是为==每一个表建立一个独立表空间==，也就是有多少表，就有多少独立空间，文件名==表名.ibd==

#### InnoDB（mysql8）

mysql8移除了.frm文件，如果想要查看需要解析ibd文件，命令`ibd2sdi --dump-flie=表名.txt 表名.ibd`得到json格式的txt文件

#### MyISAM

在MyISAM中的索引全部都是二级索引，该存储引擎的==数据和索引是分开存放的==。假设有test表，则在MyISAM存储引擎下：

```txt
test.frm 存储表结构
test.MYD 存储数据（MYData）
test.MYI 存储索引（MYIndex）
```

### 系统表空间和独立表空间的设置

```properties
[server]
innodb_file_per_table=0 # 0:代表使用系统表空间 1：代表使用独立表空间
```

## 登入MySQL服务器

```mysql
mysql -h hostname|hostIp -P port -u username -p DataBaseName -e 'SQL语句'
```

`-h`：后面接主机名或者主机ip,hostname为主机，hostIp为主机IP

`-P`：后面接MySQL服务的端口，通过该参数连接到指定的端口。MySQL服务的默认端口是3306，不使用该参数时默认连接3306，port表示具体的端口号

`-u`：接用户名，username表示用户名

`-p`：提示输入密码

`DataBaseName`：指明登入到哪个数据库中，如果没有这个参数，就会直接登入到MySQL数据库中，然后使用user命令来选择数据库

`-e`：后面可以直接加SQL语句，登入mysql服务器以后即可执行这个mysql语句，然后退出MySQL服务器。

```mysql
mysql -uroot -p -hlocalhost -P3306 mysql -e "select host,user from user"
```

## 创建用户

使用==CREATE USER==语句创建用户

```mysql
# []表示可选项
CREATE USER 用户名[@'主机host'] [IDENTIFIED BY '密码'] 
```

### 更改当前用户密码

```mysql
# 修改当前用户的密码，mysql5.7有效
SET PASSWORD = PASSWORD('admin123')
```

推荐使用==ALTER USER==或者==SET==语句

```mysql
ALTER USER USER() IDENTIFIED BY 'admin123';

# 或者
SET PASSWORD = 'admin123';
```

### 更改其他用户密码

```mysql

ALTER USER '用户名'[@'host'] IDENTIFIED BY 'new_password';
# 或者
SET PASSWORD FOR '用户名'@'hostname' = 'new_password'               
```

### 手动设置密码过期

设置用户的密码立即过期：

```mysql
ALTER USER '用户名' PASSWORD EXPIRE;
```

- 使用sql语句更改该变量的值并且持久化

```mysql
SET PERSIST default_password_lifetime = 180; # 建立全局策略，设置密码每隔180天过期
```

- 配置my.cnf

```properties
[mysqld]
default_password_lifetime=180
```

![image-20230714234027198](.\images\image-20230714234027198.png)

## 创建角色

// 百度吧，和创建用户差不多

## 权限管理

```mysql
# 查看MySQL服务器支持的权限列表
show privileges;
# 查看用户的角色
show grants for 用户名;
```

### 授予权限

授权用户的方式有两种，分别是通过把==角色赋予用户给用户授权==和==直接给用户授权==

命令：

```mysql
GRANT 权限1,权限2,...权限n ON 数据库名称.表名称 TO 用户名@用户地址 [IDENTIFIED BY '密码']
# 全部权限 Grant all privileges ON ...
```

### 收回权限

```mysql
REVOKE 权限1,权限2...权限n ON 数据库名称.表名称 FROM 用户名@用户地址
```

### 权限表

MySQL服务器通过`权限表`来==控制用户对数据库的访问==，权限表存放在mysql数据库中，MySQL数据库系统会根据这些权限表的内容来为每个用户赋予相应的权限。这些权限表中最重要的是user表，db表，除此之外还有table-priv表，column_priv表和proc_priv表等。

## SQL执行流程

![image-20230716185631462](.\images\image-20230716185631462.png)

### MySQL查询流程

- 查询缓存：Server如果在查询缓存中发现了这条SQL语句，就会直接把结果返回给客户端。MySQL8之后已经抛弃此功能。

my.cnf配置：

```properties
# query_cache_type：0代表关闭查询缓存，1代表开启，2代表demand(按需使用)
query_cache_type = 2
```

```mysql
select SQL_CACHE * from test where id = 5;
select SQL_NO_CACHE * from test where id = 5;
```

- 解析器：在解析器中对SQL语句进行语法分析，语义分析

- 优化器：在优化器中会确定SQL语句的执行路径，比如是根据全表索引还是根据索引检索等。==一条查询可以有很多种执行方式，最后都返回相同的结果，优化器的作用就是找到其中最好的执行计划==

- 执行器：执行SQL查询并返回结果

## 索引

### 概述

==索引的本质：==索引是帮助MySQL高效获取数据的数据结构。

优点：

- 通过创建唯一索引，可以保证数据库表中每一行数据的唯一性
- 降低数据库的IO成本
- 加速表和表之间的连接
- 减少查询中分组和排序的时间

缺点：

- 耗费时间，创建索引需要时间，在表数据量很大时，时间也会变长。

- 占用磁盘空间
- 降低更新表的速度，当对表进行增加，删除和修改的时候，索引也要动态维护。

### 索引方案（初步）

![image-20230718223905860](.\images\image-20230718223905860.png)

以页28为例，它对应目录2，这个目录项中包含着该页的页号28以及该页中用户记录的最小主键值5，我们只需要把几个目录项在物理存储器上连续存储，就可以实现根据主键值快速查找某条记录的功能。

### 索引方案（迭代）

![image-20230718224757943](.\images\image-20230718224757943.png)

新分配了一个编号为30的页来专门存储目录项记录，不同点：

- 目录项记录的record_type值是1，而普通用户记录的record_type是0
- 目录项记录只有主键值和页的编号两个列，而普通的用户记录的列是用户自定义的
- 记录头信息里还有一个叫min_rec_mask的属性，只有在存储目录项记录的页中的主键值最小的目录项记录的min_rec_mask值为1，其他别的记录的min_rec_mask值都是0

随着数据的增加，一个目录页（16kb）不能满足需求

![image-20230718225817234](.\images\image-20230718225817234.png)

再次升级：

![image-20230718230232663](.\images\image-20230718230232663.png)

生成了一个存储更高级目录项的页33，这个页中的两条记录分别代表页30和页32，如果用户记录的主键值在[1,132)之间，则到页30中查找更详细的目录项记录，如果主键值不小于320，则到页32中查找更详细的目录项记录。==这个数据结构，就是B+树==

## B+树

不论是存放用户记录的数据页，还是存放目录项记录的数据页，我们都把他们存放在B+数这个数据结构中了，所以我们也称这些数据页为==节点==。从图中可以看出，实际用户记录存放在B+数的最底层的节点上，这些节点也被称为叶子节点，其余用来存放目录项的节点称为非==叶子节点==或者==内节点==，其中B+数最上面的节点也称为==根节点==

## 常见索引概念

### 聚簇索引

特点：

1.使用记录主键值的大小进行记录和页的排序，这包括三个方面的含义：

- 页内的记录是按照主键的大小顺序排成一个单向链表
- 各个存放用户记录的页也是根据页中用户记录的主键大小顺序排成一个双向链表
- 存放目录项记录的页分为不同的层次，在同一层次中的页也是根据页中目录项的主键大小顺序排成一个双向链表

2.B+数的叶子节点存放的是完整的用户记录

指这个记录中存储了所有列的值（包括隐藏列）

> 优点

- 数据访问更快，因为聚簇索引将索引和数据保存到同一个B+树种，因此从聚簇索引中获取数据比非聚簇索引更快
- 聚簇索引对于主键的排序查找和范围超早数据非常快
- 按照聚簇索引排列顺序，查询显示一定范围i数据的时候，由于数据都是紧密相连，数据库不用从多个数据块中提取数据，所以节省了大量的IO操作

> 缺点

- 插入速度严重依赖于插入顺序，按照主键的顺序插入是最快的方式，否则将会出现页分裂，严重影响性能。因此对于InnoDB表，一般定义一个自增的ID列作为主键
- 更新主键的代价非常高，因为将会导致被更新的行移动。
- 二级索引访问需要两次索引查找，第一次找到主键值，第二次根据主键值找到行数据

### 二级索引

多建几个B+树，不同的树的数据采用不同的排序规则

回表：根据列查找只能确定我们要查找记录的主键值，仍然还需要到聚簇索引中在查一遍，这个过程称为回表。

### 联合索引

![image-20230719222342402](.\images\image-20230719222342402.png)

以c2,c3列作为联合索引：

- 每条目录项记录都是由c2,c3,页号这三个部分组成，各条记录先按照c2列的值进行排序，如果记录的c2列相同，则按照c3列的值进行排序
- B+树叶子节点处的用户记录由c2,c3和主键c1列组成

## InnoDB的B+树索引的注意事项

### 根页面位置万年不动

- 每当为某个表创建一个B+树索引（聚簇索引不是人为创建的，默认就有）的时候，都会为这个索引创建一个根节点页面。
- 随后向表中插入用户记录时，先把用户记录存储到根节点中
- 当根节点中的可用空间用完时，此时会将根节点中的所有记录复制到一个新分配的页，比如页a中，然后对这个新页进行页分裂的操作，得到另一个新页，比如页b。这时新插入的记录根据键值（也就是聚簇索引中的主键值，二级索引中对应的索引列的值）的大小就会被分配到页a或者页b中，而根节点便升级为存储目录项记录的页。

### 内节点中目录项记录的唯一性

在构建索引时带上主键值构成唯一性

### 一个页面最少存储2条数据

一个B+树只需要很少的层级就可以轻松存储数亿条记录，这是因为B+数本质上就是一个大的多层级目录，每经过一个目录时都会过滤掉许多无效的子目录，知道最后访问到存储真实数据的目录

## 索引的代价

### 空间

每建立一个索引都要为它建立一颗B+树，每一颗B+树的每一个节点都是一个数据页，一个页默认会占用16kb的存储空间，一棵很大的树由许多数据页组成，是很大的一片存储空间

### 时间

每次对表中的数据进行增删改操作时，都需要去修改各个B+树索引，由于B+树每层节点都是按照索引列的值从小到大的顺序排序而组成了双向链表，不论是用户记录还是目录项记录，都是按照索引列的值从小到大的顺序而形成了一个单向链表。而增删改操作可能会对节点和记录的排序造成破坏，所以存储引擎需要额外的时间进行一些记录移位，页面分裂，页面回收等操作来维护好节点和记录的排序。如果建立的索引太多，对性能的影响也会变大。

## MySQL数码结构选择的合理性

### 全表遍历

### Hash结构

缺点：

- Hash索引仅仅能满足（=）（<>）和IN查询，进行范围查询时，哈希索引时间复杂度会退化为O(N)，而树形的有序结构，仍然能保持O(log2N)的高效率
- Hash索引的数据存储无序，在order by 情况下，使用Hash索引还需要对数据进行重新排序
- 对于联合索引的情况，Hash值是将联合索引键合并后一起计算，无法对单独的一个键或者几个索引键进行查询
- 对于等值查询，通常Hash的效率更高，但是索引列的重复值如果很多，效率就会降低。这是因为遇到Hash冲突时，需要遍历桶中的行指针来进行比较，找到查询的关键字，非常耗时。

==Redis存储的核心就是Hash表==

### 二叉搜索树

特点：

- 一个节点只能有两个子节点
- 左子节点<本节点；右子节点>=本节点

但是在某些情况下，二叉树会退化成链表，为了提高效率，就需要减少磁盘IO数，为了减少磁盘IO的次数，就需要尽量降低数的高度。

### AVL数（平衡二叉搜索树）

==它是一棵空树或者它的左右两个子树的高度差的绝对值不超过1，并且左右两个子树都是一棵平衡二叉树==

### B树

也叫多路平衡查找树

![image-20230720215458090](.\images\image-20230720215458090.png)

![image-20230720223949010](.\images\image-20230720223949010.png)

按照例子：把键值小于26的数据放在左节点，大于35的数据放在右节点，中间值自身存储

B树作为多路平衡查找树，它的每一个节点最多可以包括M个子节点，M称为B树的阶。每个磁盘中包括了关键字和子节点的指针。如果一个磁盘块中包括了x个关键字，那么指针数就是x+1。对于一个100阶的B树来说，如果有3层的话最多可以存储100万的索引数据

- B树在插入和删除节点的时候如果导致树不平衡，会通过自动调整节点的位置来保持树的自平衡。
- 关键字集合分布在整棵树中，即叶子节点和非叶子节点都存放数据，搜索有可能在非叶子节点结束
- 其搜索性能等价于在关键字全集内做一次二分查找

### B+树

B树的进阶

差异：

- B+树的中间节点并不直接存储数据
- B+树查询效率更加稳定，因为B+树每次只有访问到叶子节点才能找到数据，而在B树中，非叶子节点也存储数据，造成了访问数据的不稳定性
- B+树的查询效率更高。这是因为B+树比B树更矮胖（阶数更大，深度更低），查询的IO次数更少
- 在查询范围上，B+树的效率也比B树高。这是因为所有关键字都出现在B+树的叶子节点上，叶子节点之间会有指针，数据又是递增的，这使得范围查找可以通过指针链接查找。而在B树中需要通过中序遍历才能完成数据范围的查找，效率要低很多

![image-20230720222345347](C:\Users\19242\AppData\Roaming\Typora\typora-user-images\image-20230720222345347.png)

B+树内节点存储的是目录项记录，有主键最小值，字段和页号组成，而B树存储的是主键区间段的值。

## InnoDB数据存储结构

### 磁盘与内存交互基本单位：页

InnoDB将数据划分为若干个页，InnoDB中页的大小默认为==16kb==

以页作为磁盘和内存之间交互的基本单位，也就是一次最少从磁盘中读取16kb的内容到内存中，一次最少把内存中的16kb的内容刷新到磁盘中。也就是说，==在数据库中，不论都一行，还是读多行，都是将这些行所在的页进行加载。数据库管理存储空间的最小单位是页，数据库IO操作的最小单位是页==

### 页的上层结构

![image-20230722232516363](.\images\image-20230722232516363.png)

区（Extent）是比页大一级的存储结构，在InnoDB中，一个区会分配64个连续的页，大小为16 * 64 = 1Mb。

段（Segment）由一个或者多个区组成，区在文件系统是一个连续分配的空间，在段中不要求区与区之间相邻。==段是数据库中的分配单位，不同类型的数据库对象以不同的段形式存在==

### 页的内部结构

按照类型划分，常见的有`数据页（保存B+树节点）`、`系统页`、`Undo页`和`事务数据页`等。

数据页的16kb大小的存储空间被划分为七个部分，分别是文件头（File Header）、页头（Page Header）、最大最小记录（infimum+supermum）、用户记录（User Records）、空闲时间（Free Space）、页目录（Page Directory）和文件尾（File Trailer）

![image-20230723121542493](.\images\image-20230723121542493.png)

// todo

## 区、段与碎片区

### 区

B+树的每一层中的页都会形成一个双向链表，如果是以页为单位来分配存储空间的话，双向链表相邻的两个页之间的物理位置可能离的非常远，距离远带来随机IO问题，随机IO速度非常慢。引入区的概念，一个区就是物理位置上连续的64个页。因为InnoDB中的页大小默认16kb，所以一个区的大小是1MB。在表中数据量大的时候，为某个索引分配空间的时候就不再按照页为单位分配，而是按照区为单位分配。

### 段

对于范围查询，其实是对B+树叶子节点中的记录进行顺序扫描，如果不区分叶子节点和非叶子节点，统统把节点代表的页面放到申请区，效率太低，所以InnoDB对非叶子节点和叶子节点进行区别对待，分为叶子节点段和非叶子节点段。

## 索引

### 索引的分类

- 从==功能逻辑==上说，索引主要有4种，分别是普通索引、唯一索引、主键索引、全文索引
- 按照==物理实现方式==，索引分为聚簇索引和非聚簇索引
- 按照==作用字段个数==进行划分，分为单列索引和联合索引

==普通索引==

在创建普通索引时，不附加任何限制条件，只是用于提高查询效率。这类索引可以创建在任何数据类型中，其值是否唯一和非空，要由字段本身的完整性约束条件决定

==唯一性索引==

使用`UNIQUE参数`设置唯一性索引，限制该字段唯一，但允许有空值

==主键索引==

特殊的唯一性索引，在唯一性索引上增加了不为空的约束

==联合索引==

多列索引是在表的多个字段组合上创建一个索引，该索引指向创建时对应的多个字段，可以通过这几个字段进行查询，但是只有查询条件中使用了这些字段中的第一个字段时才会被使用。例如，在表中id,name,gender上建立联合索引，只有在查询条件中使用字段id时该索引才会被使用。使用组合索引时遵循==最左前缀索引==。

==全文索引==

全文索引是目前搜索引擎使用的一种关键技术，它能够利用分词技术等多种算法智能分析出文本文字中关键词的频率和重要性，然后按照一定的算法规则筛选结果，全文索引适合大型数据集。使用参数`FULLTEXT`可以设置索引为全文索引，在定义索引的列上支持值的全文查找，允许插入空值和重复值。全文索引只能创建在`char`,`varchar`,或者`text`类型及其系列类型的字段上，==查询数据量较大的字符串类型的字段时，使用全文索引可以提高查询速度==

## 创建索引

隐式创建：在声明有主键约束，唯一性约束，外键约束的字段上，会自动创建索引

```mysql
#排序默认asc
create index 索引名
    on 表名 (字段1 排序,字段2 排序...);
    
#示例
create [索引类型] index student_name_id_index
    on student (name desc, id asc);
```

全文索引使用match+against方式查询：

```mysql
select * from papers where match(title, content) against ('查询字符串');
```

## MySQL8.0索引新特性

### 支持降序索引

降序索引以降序存储键值。MySQL在8版本之前创建的仍然是升序索引，使用时进行反向扫描，这大大降低了数据库的效率。在某些场景，降序索引意义重大，例如，如果一个查询需要对多个字段进行排序，且字段排序要求不一致，那么使用降序索引将会避免数据库使用额外的文件排序操作，从而提高性能。

### 隐藏索引

在MySQL5版本之前，只能通过显示的方式删除索引，在表数据量很大时，会耗费过多资源。从8开始支持隐藏索引。==注意：主键不能设置为隐藏索引，当表中没有主键时，表中第一个唯一非空索引会称为隐式主键，也不能设置为隐藏索引==。通过`VISIBLE`或者`INVISIBLE`关键字设置索引的可见性。

```mysql
alter table 表名 alter index 索引名称 invisible| visible;
```

## 索引的设计原则

### 数据准备

```mysql
# 建表
create table student_info(
    id int(11) not null auto_increment,
    student_id int not null,
    name varchar(20) default null,
    course_id int default null,
    class_id int default null,
    create_time datetime default current_timestamp on update current_timestamp,
    primary key (id)
) engine=innodb auto_increment = 1 default charset =utf8mb4;

# 函数1：随机字符串
DELIMITER //
create function rand_string(n INT)
returns varchar(255)
begin 
    declare chars_str varchar(100) default 'abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ';
    declare return_str varchar(255) default '';
    declare i int default 0;
    while i < n DO
            set return_str = concat(return_str, substr(chars_str, floor(1+rand() * 52), 1));
            set i = i + 1;
        end while;
    return return_str;
end //
delimiter ;

# 函数2：随机数
delimiter //
create function rand_num(from_num INT, to_num INT)
returns int(11)
begin 
    declare i int default 0;
    set i = floor(from_num + rand() * (to_num - from_num + 1));
        return i;
end //
delimiter ;


# 存储过程1：创建插入课程表存储的过程
delimiter //
create procedure insert_course(max_num INT)
begin 
    declare  i int default 0;
    set autocommit=0;
    repeat 
        set i = i + 1;
        insert into course(course_id, course_name) values (rand_num(10000, 10100), rand_string(6));
        until i = max_num
    end repeat ;
    commit ;
end //
delimiter ;

# 存储过程2：创建插入学生信息表存储过程
delimiter //
create procedure insert_stu(max_num INT)
begin
    declare i int default 0;
    set autocommit = 0;
    repeat
    	set i = i + 1;
        insert into student_info(course_id, class_id, student_id, name)
        values (rand_num(10000, 10100), rand_num(10000, 10200), rand_num(1, 200000), rand_string(6));
    until i = max_num
        end repeat;
    commit;
end //
delimiter ;


call insert_course(100);
call insert_stu(1000000);
```

### 适合创建索引的情况

#### 1.字段的数值有唯一性的限制

业务上具有唯一特性的字段，即使是组合字段，也必须建成唯一索引。

#### 2.频繁作为WHERE查询条件的字段

#### 3.经常GROUP BY和ORDER BY的字段

如果group by和order by同时出现，应该建立联合索引，并且以group by字段顺序作为建立索引的顺序，其中如果order by字段倒序，在建立联合索引时也为排序字段建立倒序索引。

#### 4.UPDATE,DELETE的WHERE条件列

#### 5.DISTINCT字段需要创建索引

#### 6.多表JOIN连接操作时，创建索引注意事项

首先，==连表的数量不要操作3张==，因为每增加一张表就相当于增加了一次嵌套的循环，数量级增长会非常快，严重影响查询效率。其次，对==where条件创建索引==，最后，==对用于连接的字段创建索引==，并且该字段在多张表的类型一致（不一致会使用函数转换，使用函数导致索引失效）。

#### 7.使用列的类型小的创建索引

表示数据范围的大小，即能用INT就不要BIGINT，能用MEDIUMINT就不用INT

- 数据类型越小，在查询时进行的比较操作越快
- 数据类型越小，索引占用的空间就越少，在一个数据页可以放下更多记录，从而减少磁盘IO

#### 8.使用字符串前缀创建索引

假设字符串很长，那么存储一个字符串就需要占用很大的空间，在为这个字符串创建索引时，会有两个问题：

- B+数索引中的记录需要把该列的完整字符串存储起来，耗时，而且字符串越长，==在索引中占用的存储空间越大==。
- 如果B+数索引中索引列存储的字符串很长，那么在==比较时会占用更多时间==

通过截取字段的前面一部分内容建立索引，这个就叫做==前缀索引==。这样在查找记录时虽然不能精确定位到记录的位置，但是能定位到相同前缀所在的位置，然后根据前缀相同的记录的主键值回表查询完整的字符串值，既节省空间，又减少了字符串的比较时间。

```mysql
# 在创建索引时指定前缀索引的长度
create index idx_name on test.student_info(name(10));

# 截取长度计算公式，得到的值越接近1索引效率越好
count(distinct left(列名,索引长度)) / count(*)
```

引申的另外一个问题：我们只把字符串列指定的前n个字符放入二级索引中，如果遇到n个字符相同的数据，==无法支持使用索引排序，只能使用文件排序==

#### 9.区分度高（散列性高）的列适合作为索引

列的基数指的是某一列中不重复数据的个数，比如某个列的值是2，5，8，2，5，8，2，5，8，虽然有9个值，但是基数是3。也就是说，==在记录行数一定的情况下，列的基数越大，该列中的值越分散；列的基数越小，该列中的值越集中==

#### 10.使用最频繁的列放在联合索引的左侧

#### 11.在多个字段都要创建索引的情况下，联合索引优于单值索引

## 限制索引的数量

建议单张表索引数量==不超过6个==，原因：

- 每个索引都需要占用磁盘空间，索引越多，需要的磁盘空间就越大。
- 索引会影响INSERT,DELETE,UPDATE等语句的性能，因为表中的数据更改的同时，索引也会进行调整和更新，会影响性能。
- 优化器在选择如何优化查询时，会根据统一信息，对每一个可以用的索引来进行评估，以生成一个最好的执行计划，如果同时有很多个索引都可以用于查询，会增加MySQL优化器生成执行计划时间，降低查询性能。

## 不适合创建索引的情况

### 1.在where语句中使用不到的字段，不需要设置索引

### 2.数据量小的表最好不要使用索引

在数据表中数据行数比较少的情况下，比如不到1000行，是不需要创建索引的

### 3.有大量重复数据的列上不要建立索引

结论：当数据重复度大，比如==高于10%==的时候，也不需要对这个字段使用索引

### 4.避免对经常更新的表创建过多的索引

- 频繁更新的字段不一定要创建索引。因为更新数据的时候，也需要更新索引，如果索引太多，在更新索引的时候也会造成负担，从而影响效率。
- 避免对经常更新的表创建过多的索引，并且索引的列尽可能少。此时，虽然提高了查询数据，但是同时也降低了更新表的速度。

### 5.不建议用无序的值作为索引

### 6.删除不在使用或者很少使用的索引

### 7.不要定义冗余或重复的索引

- ==冗余索引：==同一列创建了多个索引，比如：index(a,b,c)相当于 index(a), index(a,b), index(a,b,c)
- ==重复索引：==比如在主键id又创建了普通索引（主键本身存在聚簇索引）

## 性能分析

```properties
[mysqld]
slow_query_log=ON #开启慢查询日志
slow_query_log_file=/var/lib/mysql/xxx-slow.log #慢日志的目录和文件名信息
long_query_time=3 #设置慢查询的阈值为3秒，超出此设定值的SQL即被记录到慢日志文件中
log_output=FILE
min_examined_row_limit=0 #默认为0，表示查询扫描过的最少记录数，即查询超过时间阈值并且返回的表行数大于此设定值则记录慢日志
```

### 慢查询日志分析工具：mysqldumpslow

```sh
# -s：排序, t:query_time; -t:返回前3条数据 ; 慢日志的文件名
mysqldumpslow -s t -t 3 /var/lib/mysql/xxx-slow.log
```

### 查看SQL执行的成本：SHOW PROFILE

用于分析当前会话中SQL都做了什么，执行的资源消耗情况，==默认情况下处于关闭状态==。

```mysql
# 开启
set profiling = 'ON'

# 查询倒数第2条sql的执行情况
show profile [cpu,block io for query 2];
```

show profile的常用查询参数：

`ALL`：显示所有的开销信息

`BLOCK IO`：显示块IO开销

`CONTEXT SWITCHES`：上下文切换开销

`CPU`：显示CPU开销信息

`IPC`：显示发送和接收开销信息

当出现：

- converting HEAP to MyISAM：查询结果太大，内存不够，数据往磁盘上搬了

- Creating tmp table：创建临时表，先拷贝数据到临时表，用完后在删除临时表
- copying to tmp table on disk：把内存中临时表复制到磁盘上
- locked

如果在show profile是出现这些情况，则sql需要优化

## 分析查询语句：EXPLAIN

explain语句输出的各个列的作用：

| 列名          | 描述                                                   |
| ------------- | ------------------------------------------------------ |
| id            | 在一个大的查询语句中每个SELECT关键字都对应一个唯一的id |
| select_type   | SELECT关键字对应的那个查询的类型                       |
| table         | 表名                                                   |
| partitions    | 匹配的分区信息                                         |
| type          | 针对单表的访问方法                                     |
| possible_keys | 可能用到的索引                                         |
| key           | 实际上使用的索引                                       |
| key_len       | 实际使用的索引长度                                     |
| ref           | 当使用索引列等值查询时，与索引列进行等值匹配的对象信息 |
| rows          | 预估的需要读取的记录条数                               |
| filtered      | 某个表经过搜索条件过滤后剩余记录条数的百分比           |
| Extra         | 一些额外的信息                                         |

### table,id

```mysql
# 1.table:表名，查询的每一行记录都对应一个单表
EXPLAIN SELECT * FROM s1;

# s1:驱动表  s2:被驱动表
EXPLAIN SELECT * FROM s1 INNER JOIN s2;

# 2行记录，2个select的id
EXPLAIN SELECT * FROM s1 WHERE key1 IN (SELECT key1 FROM s2) OR key3 = 'a';

# 2行记录，1个select的id, 查询优化器可能对涉及子查询的查询语句进行重写
EXPLAIN SELECT * FROM s1 WHERE key1 IN (SELECT key2 FROM s2 WHERE common_field = 'a');

# 3行记录，多的那行select_id为空，表名为<union1,2>：Union去重
EXPLAIN SELECT * FROM s1 UNION SELECT * FROM s2;

# 2行记录
EXPLAIN SELECT * FROM s1 UNION ALL SELECT * FROM s2;
```

小结：

- id相同，可以认为是一组，从上往下顺序执行
- 在所有组中，id值越大，优先级越高，越先执行
- 关注点：id号每个号码，表示一次独立查询，一个sql的查询次数越少越好

### select_type

一条大的查询语句可以包含若干个SELECT关键字，==每个SELECT关键字代表一个小的查询语句==，而每个SELECT关键字的FROM子句中都可以包含若干张表（这些表用来做连接查询），==每一张表都对应着执行计划输出中的一条记录==，对于同一个SELECT关键字中的表来说，他们的id值是相同的。

MySQL为每一个SELECT关键字代表的查询都定义了一个select_type属性，当我们知道select_type属性，就知道了该查询在整个查询中的角色。

```mysql
# 查询语句中不包含UNION或者子查询的查询都算做是SIMPLE类型
EXPLAIN SELECT * FROM s1;

# 连接查询也是SIMPLE类型
EXPLAIN SELECT * FROM s1 INNER JOIN s2;

#对于包含UNION或者UNION ALL的查询来说，最左表的查询select_type为PRIMARY，其他都是UNION，由于UNION需要去重，针对临时表的查询的select_type就是UNION RESULT
EXPLAIN SELECT * FROM s1 UNION SELECT * FROM s2;

#如果包含子查询的查询语句不能转为对应的semi-join的形式，并且该子查询是不相关子查询
#该子查询的第一个select关键字代表的那个查询select_type就是SUBQUERY
EXPLAIN SELECT * FROM s1 WHERE key1 IN (SELECT key1 FROM s2) OR key3 = 'a';

// todo
```

### type⭐

执行计划的一条记录就代表MySQL对某个表的执行查询时的访问方法，又称访问类型，在explain语句中表示为type列。比如，看到type列的值是ref，表明MySQL即将使用ref访问方法来执行对s1表的查询。

==`system`==>==`const`==>==`eq_ref`==>==`ref`==>`fulltext`>`ref_or_null`>`index_merge`>`unique_subquery`>`index_subquery`>==`range`==>==`index`==>==`ALL`==

SQL性能优化的目标：至少达到range级别，要求ref级别，最好const级别

```mysql
# 当根据主键或者唯一二级索引列与常数进行等值匹配时，对单表的访问类型就是const
EXPLAIN SELECT * FROM s1 WHERE id = 10006;

# 在连接查询时，如果被驱动表是通过主键或者唯一二级索引等值匹配的方式进行访问，则对该被驱动表的访问类型是eq_ref，左表的类型为ALL
EXPLAIN SELECT * FROM s1 INNER JOIN s2 ON s1.id = s2.id;

# 当通过普通二级索引进行等值匹配来查询某个表，那么对该表的访问类型是ref
EXPLAIN SELECT * FROM s1 WHERE key1 = 'a';

# 当对普通二级索引进行等值匹配查询，该索引的列也可以是null时，那么类型是ref_or_null
EXPLAIN SELECT * FROM s1 WHERE key1 = 'a' OR key1 IS NULL;

# unique_subquery是针对一些包含IN子查询的查询语句中，如果查询优化器决定将IN子查询转换成EXISTS子查询，而且子查询可以使用主键进行等值匹配，那么类型就是unique_subquery
EXPLAIN SELECT * FROM s1
WHERE key2 IN (SELECT id FROM s2 WHERE s1.key1 = s2.key1) OR key3 = 'a';

# 如果使用索引获取某些范围区间的记录，那么就是range
EXPLAIN SELECT * FROM s1 WHERE key1 IN ('a', 'b');

#  当使用索引覆盖，但需要扫描全部的索引记录时，该表的访问方法就是index(key_part2和key_part3在联合索引中)
EXPLAIN SELECT key_part2 FROM s1 WHERE key_part3 = 'a';
```

### possible_keys和key（可能用到的索引和实际用到的索引）

### key_len：实际使用到的索引长度（字节数）

### ref：当使用索引列等值查询时，与索引列进行等值匹配的对象信息

### rows：预估额需要读取的记录条数（越小越好）

### filtered：某个表经过搜索条件过滤后剩余记录条数的百分比

### Extra

## 索引优化与查询优化

### 物理查询优化和逻辑查询优化

- 物理查询优化是通过==索引==和==表连接方式==等技术来进行优化
- 逻辑查询优化是通过SQL==等价变换==提升查询效率，即换一种查询方式效率可能更高

### 索引失效

#### 最左前缀匹配

```mysql
create index idx_age on student(age);
create index idx_age_classid on student(age, classId);
create index idx_age_classid_name on student(age, classId, name);

# 未使用索引
EXPLAIN SELECT SQL_NO_CACHE * FROM student WHERE student.classid = 1 and student.name = 'ac';

# 使用了idx_age_classid_name索引
EXPLAIN SELECT SQL_NO_CACHE * FROM student WHERE classid = 4 and student.age = 4 and student.name = 'a';
```

#### 计算，函数，类型转换（自动或手动）导致索引失效

#### 范围条件的右边的索引失效

```mysql
create index idx_age_classid_name on student(age, classId, name);

# 使用了索引，但是key_len=10 , age5 + classId5，name没有用上（由于classId是范围查找，classId>20的索引下的name都会被扫描）
EXPLAIN SELECT * FROM student
WHERE age = 30 and classId > 20 and name = 'abc';
```

#### 不等于（!=或者<>）索引失效

#### IS NULL可以使用索引，IS NOT NULL无法使用索引（不是绝对，和字段是否能为null有关）

b+树存储null值，会有一个专门的自增指向null值所对应的行，当执行is null查询时，MySQL 查询优化器会检查相关的索引统计信息，如果发现索引中存在 NULL 值的指针，它会选择使用该索引来加速查询。通过索引的目录结构，MySQL 可以直接定位到包含 NULL 值的行，而无需扫描整个表

#### like以通配符%开头索引失效

#### OR前后存在非索引的列，索引失效

```mysql
CREATE INDEX idx_age ON student(age);

# 只有idx_age的情况下索引失效（另外一个字段没有索引会导致全表扫描）
EXPLAIN SELECT * FROM student WHERE age = 10 or classId = 100;
```

#### 数据库和表的字符集统一使用utf8mb4

统一使用utf8mb4兼容性更好，统一字符集可以避免由于字符集转换产生乱码，不同的字符集进行比较前需要进行转换会造成索引失效。

## 关联查询优化

### 外连接（左和右）

在连接字段上，添加被驱动表的索引

### 内连接

对于内连接来说，查询优化器可以决定谁作为驱动表，谁作为被驱动表，如果表的连接条件中只能有一个字段有索引，则有索引的字段所在表会被视为被驱动表

两表都存在索引的情况下，小表驱动大表

## JOIN语句原理

### 驱动表和被驱动表

- 对于内连接来说

```mysql
select * from a join b ON ...
```

a不一定是驱动表，优化器会根据查询语句做优化，决定先查哪张表，先查询的那张表就是驱动表，反之就是被驱动表。

- 对于外连接来说

```mysql
select * from a left join b on...
# 或者
select * from b right join a on ...
```

通常情况下，我们认为a是驱动表，但是

```mysql
# 此时驱动表为b，被优化器转化成内连接
EXPLAIN SELECT * FROM a left join b on a.f1 = b.f1 where a.f2 = b.f2
```

### Simple Nested-Loop Join（简单嵌套循环连接）

![image-20230804232325652](.\images\image-20230804232325652.png)

假设a表100条，b表1000条，则a*b=10万

| 开销统计         | SNLJ  |
| ---------------- | ----- |
| 外表扫描次数     | 1     |
| 内表扫描次数     | a     |
| 读取记录数       | a+a*b |
| join比较次数     | b*a   |
| 回表读取记录次数 | 0     |

### Index Nested-Loop Join（索引嵌套循环）

其优化思路主要是为了==减少内层表数据的匹配次数==，所以要求被驱动表上必须有索引，避免和内层表的每条记录去进行比较。

![image-20230804232943516](.\images\image-20230804232943516.png)

| 开销统计         | SNLJ      | INLJ            |
| ---------------- | --------- | --------------- |
| 外表扫描次数     | 1         | 1               |
| 内表扫描次数     | a         | 0               |
| 读取记录数       | a + b * a | a+b(macth)      |
| join比较次数     | b*a       | a*Index(Height) |
| 回表读取记录次数 | 0         | b(match)        |

### Block Nested-Loop Join（块嵌套循环连接）

将驱动表join相关的数据列缓存到Join Buffer中，然后全表扫描被驱动表，被驱动表的每一条记录一次性和join buffer中的所有驱动表记录进行匹配（内存中操作），将简单嵌套循环合并成一次

![image-20230804233718768](.\images\image-20230804233718768.png)
| 开销统计         | SNLJ      | INLJ            | BNLJ                                          |
| ---------------- | --------- | --------------- | --------------------------------------------- |
| 外表扫描次数     | 1         | 1               | 1                                             |
| 内表扫描次数     | a         | 0               | a * used_column_size / join_buffer_size       |
| 读取记录数       | a + b * a | a+b(macth)      | a+b*(a * used_column_size / join_buffer_size) |
| join比较次数     | b*a       | a*Index(Height) | b * a                                         |
| 回表读取记录次数 | 0         | b(match)        | 0                                             |

### 总结

- 整体效率：INLJ > BNLJ > SNLJ
- 永远用小结果集驱动大结果集（其本质就是减少外层循环的数据数量）（结果集可以理解为要查询的列*记录的行数）
- 为被驱动表匹配的调教增加索引（减少内层表的循环匹配次数）
- 增大join_buffer_size的大小（一次缓存的数据越多，内层包的扫表次数就越少）

## 子查询优化

==子查询是MySQL的一项重要的功能，可以帮助我们通过一个SQL语句实现比较复杂的查询，但是，子查询的执行效率不高。==

- 执行子查询时，MySQL需要为内层查询语句的查询结果==建立临时表==，然后外层查询语句从临时表中查询记录，查询完毕后，在==撤销这些临时表==。这样会消耗过多的CPU和IO资源，产生大量的慢查询。
- 子查询的结果集存储的临时表，不论是内存临时表还是磁盘临时表==都不会存在索引==，所以查询性能会受到影响。
- 对于返回的结果集比较大的子查询，其对查询性能的影响也就越大。

### 使用联表查询

==在MySQL中，可以使用连接（JOIN）查询来替代子查询，连接查询不需要建立临时表，其速度比子查询要快==，如果查询中使用索引，性能会更好。

==结论：尽量不要用NOT IN或者NOT EXISTS，用LEFT JOIN xxx ON xxx WHERE xxx IS NULL替代==

## 排序优化

在MySQL中，支持两种排序方式，分别是`FileSort`和`Index`排序

- Index排序中，索引可以保证数据的有序性，不需要在进行排序，效率更高
- FileSort排序则一般在内存中进行排序，占用CPU较多，如果结果较大，会产生临时文件IO到磁盘进行排序的情况，效率更低

优化建议：

- SQL中，可以在WHERE子句和ORDER BY 子句中使用索引，目的是在WHERE子句中避免全表扫描，在ORDER BY子句避免使用FileSort排序（虽然某些情况下全表扫描，或者FileSort排序不一定比索引慢）
- 尽量使用index完成order by排序，如果where和order by后面是相同的列就使用单列索引；如果不同就使用联合索引
- 无法使用Index时，需要对FileSort进行调优

### ORDER BY时不limit，索引失效

order by时不限制条数，回表导致扫描全表；如果只查询了order by中字段，则索引生效（不用进行回表操作）。

### ORDER BY时规则不一致，索引失效（顺序错，不索引；方向反，不索引）

例：创建2个字段联合索引，两个字段都是正序

对于联合索引，order by时一个字段倒序排列，一个字段正序排列，索引失效；两个字段都倒序排列，索引生效。

```mysql
# 索引student(age, classid, name), student(age, classid, stuno)

# 不使用索引
EXPLAIN SELECT * FROM student ORDER BY age DESC, classid ASC limit 10;

# 不使用索引
EXPLAIN SELECT * FROM student ORDER BY classid DESC , name DESC  limit 10;

# 不使用索引（由于排序条件与索引的顺序不完全匹配，优化器可能会认为使用索引排序的成本较高，因此选择执行全表扫描来满足排序需求，导致索引失效）
EXPLAIN SELECT * FROM student ORDER BY age ASC, classid DESC Limit 10;

# 使用索引
EXPLAIN SELECT * FROM stduent ORDER BY age DESC, classid DESC Limit 10;
```

### 无过滤，不索引

```mysql
# 索引student(age, classid, name), student(age, classid, stuno)

# 使用索引student(age, classid, stuno)，但是key_len只有5，只用上age
# 以过滤条件为主，当查询到的数据较少时,查询器认为不用走classid的索引
EXPLAIN SELECT * FROM student WHERE age = 45 ORDER BY classid;
EXPLAIN SELECT * FROM student WHERE age = 45 ORDER BY classid, name;

# 不使用索引
EXPLAIN SELECT * FROM student WHERE classid = 45 ORDER BY age;

#使用索引student(age, classid, name)，但是type是index，效率不高
EXPLAIN SELECT * FROM student WHERE classid = 45 ORDER BY age LIMIT 10;
```

### 总结

```mysql
INDEX a_b_c(a,b,c)

# order by 能使用索引最左前缀
- order by a
- order by a,b
- order by a,b,c
- order by a desc, b desc, c desc

# 如果where使用索引的最左前缀定义为常量，则order by 能使用索引
- where a = const order by b,c
- where a = const and b = const order by c
- where a = const order by b,c 
- where a = const and b > const order by b,c

# 不能使用索引进行排序
- orde by a ASC, b DESC, C DESC # 排序不一致
- where g = const order by b,c # 丢失a索引
- where a = const oder by c # 丢失b索引（用到索引，但是排序没有使用到）
- where a = const order by a,d # d不是索引的一部分
- where a in (...) order by b,c # 对于排序来说，多个相等条件也是范围查找
```

## GROUP BY优化

- group by使用索引的原则几乎和order by 一致，grou by 即使过滤条件没有用到索引，也可以直接使用索引

- group by先排序再分组，遵循左前缀法则
- 当无法使用索引列，增大`max_length_for_sort_data`和`sort_buffer_size`参数的设置
- where效率高于having，尽量使用where
- 减少使用order by。order by,group by, distinct这些语句较为耗费cpu
- 包含了order by，group by，distinct这些查询的语句，where条件过滤出来的结果集请保持再1000行以内，否则SQL会很慢

## 优化分页查询

一般分页查询时，通过创建覆盖索引能够比较好地提升性能。一个常见又非常头疼的问题就是limit 2000000,10，此时需要MySQL排序前2000010记录，仅仅返回2000000-2000010的记录，其他记录丢弃，查询排序的代价非常大。

```mysql
# 性能极低，type = all
EXPLAIN SELECT * FROM student limit 2000000,10;
```

### 优化思路1

在索引上完成排序分页操作，最后根据主键关联回表查询所需要的其他列内容

```mysql
EXPLAIN SELECT * FROM student t, (SELECT id FROM student ORDER BY id Limit 2000000, 10) a
WHERE t.id = a.id;
```

### 优化思路2

```mysql
EXPLAIN SELECT * FROM student WHERE id > 2000000 limit 10;
```

## 尽量使用覆盖索引（避免回表）

索引是高效找打行的一个方法，但是一般数据库也能使用索引找到一个列的数据，因此它不必读取整个行，毕竟索引叶子节点存储了他们的索引的数据；当能通过读取索引就可以得到想要的数据，那就不需要读取行。==一个索引包含了满足查询结果的数据就叫做覆盖索引==

优点：

- 避免Innodb表进行索引的二次查询（回表）
- 可以把随机IO变成顺序IO加快查询效率

## 索引下推（尽量减少回表）

Index Condition Pushdown(ICP)是MySQL5.6中新特性，是一种在存储引擎层使用索引过滤数据的优化方式

- 如果没有ICP，存储引擎会遍历索引以定位基表中的行，并将他们返回给MySQL服务器，由服务器评估where后面的条件是否保留行
- 启用ICP后，如果部分where条件可以用仅使用索引中的列进行筛选，则mysql服务器会把这部分where调教放到存储引擎中筛选。然后，存储引擎通过使用索引条目来筛选数据，并且只有在满足这一条件时才从表中读取行。
  - 好处：IPC可以减少存储引擎必须访问基表的次数和MySQL服务器必须访问存储引擎的次数
  - 但是ICP的加速效果取决于在存储引擎内通过ICP筛选掉的数据的比例。

```mysql
# using index condition, key1有索引（两个查询条件都是key1，like操作不需要回表查询）
EXPLAIN SELECT * FROM s1 WHERE key1 > 'z' and key1 like '%a';
```

### 使用条件

- 如果表访问的类型是range,ref,eq_ref和ref_or_null可以使用ICP
- ICP可以用于Innodb和MyISM表，包括分区表InnoDB和MyISAM表
- 对于InnoDB表，ICP仅仅用于二级索引。ICP的目标是减少全行读取次数，从而减少IO操作
- 当SQL使用覆盖索引时，不支持ICP，因为这种情况下使用ICP不会减少IO
- 相关子查询的条件不能使用ICP

## 其他优化策略

### EXISTS和IN的区分

索引是个前提，选择哪种方式需要看表的大小，尽量小表驱动大表

```mysql
select * from a where cc in (select cc from b)

select * from a where exists (select cc from b where b.cc = a.cc)

# 上面两条sql,当a表数据小于b表数据时，用exists更好。因为exists的实现，相当于外表循环，实现的逻辑类似于
for i in a
	for j in b
		if j.cc == i.cc then ...
		
# 当a大于b表数据时，用in,实现的逻辑类似于
for i in b
	for j in a
		if j.cc = i.cc then
```

a表小就用exist，b表小就用in

### COUNT(*),COUNT(1)和COUNT(具体字段)效率

前提：如果要统计的某个字段的非空数据行数

1：count(*)和count(1)都是对所有结果进行count，这两者在本质上没有区别（二者执行时间可能略有差别，不过还是可以堪称执行效率是相等的）。

2：如果是MyISAM存储引擎，统计数据表的行数只要O(1)复杂度，这是因为每张MyISAM的数据表都有一个meta信息存储了row_count值，而一致性有表级锁来保证。如果是InnoDB存储引擎，因为InnoDB支持事务，采用行级锁和MVCC机制，所以无法维护row_count值，因此需要全表扫描，复杂度O(n)

3：在InnoDB引擎中，如果采用count(具体字段)来统计行数，要尽量采用二级索引。因为主键采用的索引是聚簇索引，聚簇索引包含的信息多，明显会大于二级索引。对于count(*)和count(1)，他们不需要查找具体的行，只是统计行数，系统会自动选择占用空间更小的二级索引来进行统计。如果有多个二级索引，会使用key_len小的二级索引进行扫描。

### 关于select *

- mysql在解析的过程中，会通过查询数据字典将*转换成具体的字段，耗费资源
- 使用*无法使用覆盖索引

### limit 1对优化的影响

在字段没有唯一索引的情况下，如果确实数据只有一条，使用limit 1可以提升效率

### 多使用commit

在程序中尽量多使用commit，这样程序的性能得到提高，需求也会因为commit所释放的资源而减少

释放的资源：

- 回滚段上用于恢复数据的信息
- 被程序语句获得的锁
- redo / undo log bufer中的空间
- 管理上述3种资源中的内部花费