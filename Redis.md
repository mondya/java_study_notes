# Redis

## NoSql数据库

### 概述

NoSql（Not only sql） ,不仅仅是sql,泛指**非关系型数据库**

NoSql不依赖业务逻辑方式存储，而已简单的**key-value**方式存储。因此大大增加了数据库的拓展能力

- 不支持sql标准
- 不支持ACID
- 远超sql的性能

**打破了传统关系型数据库以业务逻辑为依据的存储模式，而针对不同数据结构类型改为以性能为最优先的存储方式**

## Redis

- Redis是一个开源的**key-value**存储系统
- 支持的value类型有string,list(链表),set(集合)，zset（有序集合）和hash(hash类型)

### Redis的安装

- 下载redis安装包，上传到Linux服务器中，/opt目录
- 安装gcc编译器
  - yum install gcc
  - 测试gcc环境 `gcc --version`

- 解压缩 tar -zxvf [redis文件名]，完成后进入`cd [redis文件名]`
- 在redis文件下执行`make`命令（编译文件）
- 编译完成后执行`make install`,安装redis,默认安装目录`/usr/local/bin`

> 查看默认安装目录

- `redis-benchmark`：性能测试工具
- `redis-check-aof`：修复有问题的aof文件
- `redis-check-dump`：修复有问题的dump.rdb文件
- `redis-sentinel`：Redis集群使用
- <font color="red">redis-server</font>：Redis服务器启动命令
- <font color="red">redis-cli</font>：客户端，操作入口

### Redis的启动
> 前台启动

直接在安装目录下使用`redis -server`
> 后台启动
- 复制`opt/redis-6.2.5/redis.conf`文件到etc目录中，使用`cp /opt/redis-6.2.5/redis.conf /etc`
- 在etc目录下使用`vi`编辑redis.conf文件
- 按下`/`查找字符串daemonize,把no改成yes,使用`:wq!`（强制保存退出命令）退出
- 使用`redis-server /etc/redis.conf`后台启动redis
- `ps -ef | grep redis`查看后台
![](https://gitee.com/cnuto/images/raw/master/image/Snipaste_2021-08-27_23-34-42.jpg)

### Redis关闭
单实例关闭：`redis-cli shutdown`
进入终端后关闭：`shutdown/exit`
### Redis相关
- 默认端口号6379(Merz)
- 默认16个数据库，下标从0开始，<font color=red>初始默认使用0号库</font>
- 使用`select [index]`切换数据库
- Redis是**单线程+多路IO复用技术**

### Redis键（Key）
- `keys *`：查看当前库中的所有key
- `exists key`：判断某个key是否存在
- `type key`：查看key是什么类型
- `del key`：直接删除key
- `unlink key`:删除key，异步删除，仅仅将keys从keyspace元数据中删除，真正的删除会在后续异步操作
- `expire key 10`：为给定的key设置过期时间，10秒
- `ttl key`：查看key还有多少秒过期，-1表示永不过期，-2表示已经过期
- `select [index]`：切换数据库
- `dbsize`：查看当前数据库的key的数量
- `flushdb`：清空当前库
- `flushall`：通杀**全部**数据库

## Redis常用5大数据类型
### Redis字符串（String）
String类型是<font color=red>二进制安全的</font>。意味着Redis的String可以包含任何数据。比如jpg图片或者序列化对象。
String类型是Redis最基本的数据类型，一个Redis中字符串value最多可以是<font color="red">512M</font>
#### 常用命令
`set [key] [value]`：设置键值对
`get [key]`：查询对应键值
`append [key] [value]`：将给定的value追加到原值的末尾
`strlen [key]`：获取值的长度
`setnx [key] [value]`：只有在key不存在时，设置key的值
`incr [key]`：将key中的value值加1;只能对数字值操作，如果为空，新增值为1
`decr [key]`：将key中的值减1;只能对数字值操作，如果为空，新增值为-1
`incrby/decrby [key] [步长]`：将key中存储的数字值增减，自定义步长
**原子性**：指不会被线程机制打断的操作
- Java中的i++不是原子操作
- i=0;两个线程分别对i进行++100次，值[2,200]

`mset [key1][value1][key2][value2]...`：同时设置一个或多个key-value对
`mget [key1][key2]...`：同时获取一个或多个value
`msetnx [key1][value1][key2][value2]...`：同时设置一个或多个key-value对，当且仅当所有给定的key不存在
`getrange [key] [起始位置][结束位置]`：获取值的范围，类似java中的substring
`setrange [key][起始位置][value]`：用value覆写key中所存储的字符串值，从起始位置开始
`setex [key] [过期时间] [value]`：设置键值的同时，设置过期时间，单位秒
`getset [key] [value]`：设置新值同时获取旧值 
### Redis列表(List)
> 简介

- 单键多值
- Redis列表是简单的字符串列表，按照插入顺序排序
- 底层是==双向链表==，对两端的操作性能很高，通过索引下标的操作中间的节点性能可能会较差

#### 常见命令
`lpush/rpush [key] [value1] [value2]...`：从左边或者右边插入一个或多个值
`lpop/rpop`：从左边或者右边吐出一个值。==值在键在，值光键亡==
`rpoplpush [key1] [key2]`：从k1列表右边吐出一个值到k2列表左边
`lrange [key] [startIndex] [stopIndex]`：按照索引下标获得元素（0 -1 代表取出所有的值）
`lindex [key] [index]`：按照索引下标获得元素（从左到右）
`llen [key]`：获取列表长度
`linsert [key] before [value] [newvalue]`：在value值前面添加新的newvalue
`lrem [key] n [value]`：删除Key中n个value（从左到右）
`lset [key] [index] [value]`：将列表key下标为index的值替换成value
#### 数据结构
List的数据结构为快速链表quickList
首先在列表元素较少的情况下会使用一块连续的内存存储，这个结构就是ziplist,即压缩列表;当数据量比较多的时候才会改成quicklist
### Redis集合（Set）
Redis set对外提供的功能和list类似，是一个列表的功能，特殊之处在于set是可以==自动排重的==，当你需要存储一个列表数据，又不希望出现重复数据时，set是一个很好的选择
Redis的set是string类型的==无序集合==。==它底层其实是一个value为空hash表==，所以，添加删除查找的时间复杂度都是**O(1)**
#### 常用命令
`sadd [key] [value][value]`：将一个或多个member元素加入到集合key中，已经存在的member元素会被忽略
`smembers [key]`：取出该集合中所有的值
`sismember [key] [value]`：判断集合key中是否含有该value值，有1没有0
`scard [key]`：返回该集合的元素个数
`srem [key] [value] [value]`：删除key中某个元素
`spop [key]`：==随机从该集合中吐出一个值==
`srandmember [key] n`：随机从集合中取n个值，不会从集合中删除
`smove [source] [destination] [value]`：把集合中一个值从一个集合移动到另一个集合
`sinter [key1] [key2]`：返回两个集合的交集元素
`sunion [key1] [key2]`：返回两个集合的并集元素
`sdiff [key1] [key2]`：返回两个集合的差集元素(key1中的，不包含key2的)
### Redis哈希(Hash)
Redis hash是一个键值对集合
Redis hash是一个string类型的filed和value的映射表，hash特别适合用于存储对象，类似Java中map<string,Object>
####常用命令
`hset [key] [filed] [value]`：给key集合中的[filed]键赋值value
`hget [key] [filed]`：从key集合[filed]取出value
`hmset [key] [field1] [value1] [field2] [value2]`：批量设置hash的值
`hexists [key] [field]`：查看hash表key中，给定域field是否存在
`hkeys [key]`：列出该hash集合中所有的field
`hvals [key]`：列出该hash集合中所有的value
`hincrby [key] [field] [increment]`：给key中field的值增加increment
`hsetnx [key] [field] [value]`：设置value值，当且仅当field不存在时
#### 数据结构
Hash类型对应的数据结构是两种：ziplist(压缩列表),hashtable(哈希表)。当field-value长度较短且个数较少时，使用ziplist,否则使用hashtable
### Redis有序集合ZSet
Redis有序集合zset是一个没有重复元素的字符串集合
有序集合的每个成员都关联了一个评分(score)，这个评分被用来按照从最低分到最高分的方式排序集合中的成员。==集合的成员是唯一的，但是评分可以是重复的==
#### 常用命令
`zadd [key] [score1][value1] [score2][value2]`：将一个或多个member元素及其score值加入到有序集Key中
`zrange [key] [start][stop] [withsocres]`：返回有序集合中,下标在start到stop之间的元素，带withscores可以让分数和值一起返回到集合中
`zrangebyscore key minmax [withscores] [limit offset count]`：返回有序集合中，所有score值介于Min和max之间（包括min和max）的成员。有序集成员按score从小到大排列
`zrevrangebyscore key maxmin [withscores] [limit offset count] `：同上，改为从大到小排列
`zincrby [key] [increment] [value]`：为元素的score加上增量
`zrem [key] [value]`：删除该集合下，指定值的元素
`zcount [key] min max`：统计该集合下，分数在min到max的元素个数
`zrank [key] [value]`：返回该值在集合中的排名，从0开始
## Redis的发布和订阅
Redis发布和订阅(pub/sub)是一种消息通信模式：发送者(pub)发送消息，订阅者(sub)接收消息
Redis客户端可以订阅任意数量的频道
###发布订阅命令行实现
- 打开一个客户端订阅channel1`subscribe channel1`
- 打开另一个客户端，给channel1发布消息hello`publish channel1 hello`

## Redis新数据类型
### Bitmaps
合理地使用使用操作位能够有效地提高内存的使用率和开发效率
Redis提供了Bitmaps这个“数据类型”可以实现对位的操作
- Bitmaps本身不是一种数据类型，实际上他就是字符串（key-value），但是它可以对字符串的位进行操作
- Bitmaps单独提供了一套命令，所以在redis中使用bitmaps和使用字符串的方法不同。可以把bitmaps想象成一个以位为单位的数组，数组的每个单元只能存储1和0，数组的下标在bitmaps中叫做偏移量
![](https://gitee.com/cnuto/images/raw/master/image/微信截图_20210915193821.png)
#### 命令
`setbit [key] [offset] [value]`：设置bitmaps中某个偏移量的值(0或1)
`getbit [key] [offset]`：获取bitmaps中某个偏移量的值
`bitcount [key] [start end]`：统计字符串从start字节到end字节比特值为1的数量
==注意：redis的setbit设置或者清除的是bit位置，而bitcount计算的是byte位置==
`bitop and(or/not/xor) [destkey] [key...]`：bitop是一个复合操作，它可以做多个bitmaps的and（交集），or(并集),not(非),xor(异或)操作并将结果保存到destkey中
### HyperLogLog
Redis HyperLogLog是用来做基数统计的算法，HyperLogLog的优点是在输入元素的数量和体积非常非常大时，计算基数所需的空间总是固定的，并且很小很小。但是，因为HyperLogLog只会根据输入的元素来计算基数，而不会存储输入元素本身，所以HyperLogLog不能像集合那样，返回输入的各个元素。
基数：比如数据集{1,3,4,5,5,7,9,9},那么数据集的基数集为{1,3,4,5,7,9},基数（不重复元素）为6.基数估计就是在误差可接受的范围内，快速计算基数
#### 命令
`pfadd [key] [element] `：添加指定元素到HyperLogLog中
`pfcount [key]`：计算近似基数
`pfmerge [destkey] [sourcekey]..`：将一个或者多个HLL合并后的结果存储到另一个HLL中，比如每月活跃用户可以使用每天的活跃用户来合并计算可得
### Geospatial
Redis3.2 中增加了对GEO类型的支持。GEO,Geographic,地理信息的缩写。该类型就是元素的2维坐标，在地图上就是经纬度。redis基于该类型，提供了经纬度设置，查询，范围查询，距离查询，经纬度hash等常见操作
#### 命令
`geoadd [key] longitude latitude member`：添加地理位置（精度，维度，名称）
`geopos [key] member..`：获取指定地区的坐标值
`geodist key member1 member2 m/km/ft/mi`：获取两个位置之间的直线距离
## Redis案例-模拟验证码发送
### 分析
> 输入手机号，点击发送后随机生成6位数字码，2分钟有效

- 生成6位数字码，使用Ramdom
- 2分钟有效，把验证码放到redis中，设置过期时间120秒

> 输入验证码，点击验证，返回成功或失败

- 判断一致，从redis中获取验证码和输入的验证码进行比对

> 每个手机号每天只能输入3次

- incr 每次发送加1
- 大于2时，提交不能发送 

```java
public class PhoneCode {
    public static void main(String[] args) {
    //模拟验证码发送
        verifyCode("13767152962");
        //验证
       //getRedisCode("13767152962","301620");
    }

    //1.创建6位随机验证码
    public static String getCode(){
        Random random = new Random();
        String code = "";
        for (int i=0;i<6;i++){
            int code_int = random.nextInt(10);
            code += code_int;
        }
        return code;
    }

    //2.每个手机每天只能发送三次验证码到redis中，设置过期时间120s
    public static void verifyCode(String phone){
        //获取redis的连接
        Jedis jedis = new Jedis("120.25.224.173", 6379);
        jedis.auth("xhh1999.02.10");
        //手机发送次数key
        String countKey = "VerifyCode"+phone+":count";
        //手机验证阿门key
        String codeKey = "VerifyCode"+phone+":code";
        //每天只能发送三次
        String count = jedis.get(countKey);
        if (count == null){
            //说明没有发送次数
            jedis.setex(countKey,24*60*60, String.valueOf(1));
        }else if (Integer.parseInt(count) <= 2){
            jedis.incr(countKey);
        }else if (Integer.parseInt(count) > 2){
            //发送三次，已经不能发送
            System.out.println("已经发送三次了");
            jedis.close();
            return;
        }
        //发送验证码到redis中
        String code = getCode();
        jedis.setex(codeKey,120,code);
        jedis.close();
    }

    //验证码校验
    public static void getRedisCode(String phone,String code){
        //获取redis的连接
        Jedis jedis = new Jedis("120.25.224.173", 6379);
        jedis.auth("xhh1999.02.10");
        String redisCode = jedis.get("VerifyCode"+phone+":code");
        if (code.equals(redisCode)){
            System.out.println("成功");
        }else{
            System.out.println("失败");
        }
        jedis.close();
    }
}
```
