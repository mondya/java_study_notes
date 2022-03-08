# mybatis-plus
## 建表
```sql
CREATE TABLE IF NOT EXISTS `user`(
	`id` BIGINT(20) not null COMMENT 'id',
	`userId` BIGINT(20) NULL DEFAULT null	comment '用户id',
	`name` VARCHAR(20) NULL DEFAULT null comment '姓名',
	`age` INT(200) NULL DEFAULT NULL COMMENT '年龄',
	`email` VARCHAR(50) NULL DEFAULT NULL COMMENT '邮箱',
	`createTime` datetime null	 DEFAULT null comment '创建时间',
	`updateTime` datetime null	 DEFAULT null comment '修改时间',
	PRIMARY KEY(id)
)ENGINE=INNODB charset=utf8
```
常用的注解

```java
@TableId(Type = IdType.AUTO) //主键自增
```

##  乐观锁

乐观锁实现方式：

- 取出记录时，获取当前version
- 更新时，带上version
- 执行更新时，set version = newVersion where version = oldVersion
- 如果version不对，更新失败