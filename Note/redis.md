# Redis

## 介绍

一种Nosql（非关系型）数据库，没有数据表，只有键值对

#### 与关系型数据库的对比

SQL                                           						NOSQL

结构化：对表的约束                                       非结构化

关系型：表之间有关系约束							无关联

SQL查询															非SQL或其他

ACID特性													       BACE：要么没有事务要么无法满足ACID

储存在磁盘														 在内存上

#### Redis特性

键值对型，value支持多种不同数据结构

单线程，每个命令具备原子性

低延迟，速度快（基于内存，IO多路复用，良好的编码

支持数据持久化，定期将数据从内存存到磁盘

支持主从集群，分片集群

支持多语言客户端，比如java

#### 安装

Redis没有windows版本，只有Linux版本

需要安装gcc依赖

## 简易使用

### 启动方式

1、命令行启动

| 命令         | 功能                |
| ------------ | ------------------- |
| redis-server | 从命令端口启动redis |

2、指定配置启动

| 命令                         | 功能                       |
| ---------------------------- | -------------------------- |
| cp redis.conf redis.conf.bck | 备份redis配置文件          |
| redis-server redis.conf      | 根据指定配置文件启动redist |

```xml
bind 0.0.0.0 监听地址0.0.0.0表示所有ip
daemonize yes 守护进程，yes即可后台运行
requirepass 123 密码 	
```

3、开机自启

创建系统服务文件

| 命令                                 | 功能                       |
| ------------------------------------ | -------------------------- |
| vi /ect/systemd/system/redis.service | 创建配置文件               |
| redis-server redis.conf              | 根据指定配置文件启动redist |
| systemctl daemon-reload              | 重新加载配置文件           |
| systemctl start redis                | 启动                       |
| systemctl stop redis                 | 停止                       |
| systemctl restart redis              | 重启                       |
| systemctl status redis               | 查看状态                   |
| systemctl enable redis               | 开机自启                   |

```xml
[Unit]
Description=redis-server
After=network.target
[Service]
Type=forking
ExecStart=/mnt/redis-server /mnt/redis-7.0.0/redis.conf
PrivateTmp=true
[Install]
wantedBy=multi-user.target
```

### Redis客户端

命令行客户端

| 命令                          | 功能             |
| ----------------------------- | ---------------- |
| redis-cli [options] [commond] | 打开命令行客户端 |
| -h 0.0.0.0                    | 指定要连接的ip   |
| -p 6379                       | 指定端口号       |
| -a 123                        | 指定redis的密码  |

图形化界面客户端

不用了

#### 编程客户端

##### 数据结构

key一般为String

| value     | 类型               |
| --------- | ------------------ |
| String    | 字符串             |
| Hash      | 哈希表             |
| List      | 链表               |
| Set       | 无序集合，不可重复 |
| SortedSet | 有序集合，不可重复 |
| GEO       | 经纬度             |
| BitMap    | 二进制             |
| HyperLog  | 二进制             |

##### 通用命令

| 命令   | 功能                                             |
| ------ | ------------------------------------------------ |
| keys   | 查看符合模板的所有key                            |
| DEL    | 删除一个指定的key                                |
| exists | 判断key存在                                      |
| expire | 给一个key设置有效期，有效期到期时key会被自动删除 |
| TTL    | 查看一个key的剩余有效期，-1代表永久有效          |

##### String类型

String:普通字符串

int:整型，可以自增自减

float:浮点型，可以自增自减

| KEY   | VALUE  |
| ----- | ------ |
| msg   | hellow |
| num   | 10     |
| score | 92.5   |

| 命令        | 功能                                            |
| ----------- | ----------------------------------------------- |
| SET         | 添加或修改一个String类型的键值对                |
| GET key     | 获取String类型的value                           |
| MSET        | 批量添加多个String类型的键值对                  |
| MGET key    | 批量获取多个String类型的value                   |
| INCR        | 让一个整型的值自增1                             |
| INCRBY      | 让一个整型的值自增指定步长                      |
| INCRBYFLOAT | 让一个浮点型的值自增指定步长                    |
| SETNX       | 添加一个String类型的键值对，前提是这个key不存在 |
| SETEX       | 添加一个String类型的键值对，并且指定有效期      |

##### KEY的层级格式

Key允许有多个单词形成层级结构，多个单词间使用:连接

项目名：业务名：类型：id

可以将value序列化为json字符串储存

'{"id":1,"name":"哈哈","age":18}'

##### Hash类型

也叫散列，其value是一个无需字典

| 命令                 | 功能                                             |
| -------------------- | ------------------------------------------------ |
| HSET key field value | 添加或修改一个hash类型的键值的field值            |
| HGET key field       | 获取hash类型的field的value                       |
| HMSET                | 批量添加多个hash类型的键值的field值              |
| HMGET                | 批量获取多个hash类型的键值的field值              |
| HGETALL              | 获取一个hash类型的key中所有的field               |
| HKEYS                | 获取一个hash类型key中所有的field                 |
| HVALS                | 获取一个hash类型key中所有的value                 |
| INCR                 | 让一个整型的值自增1                              |
| HINCRBY              | 让一个hash的值自增指定步长                       |
| HSETNX               | 添加一个hash类型的field值，前提是这个field不存在 |

##### List类型

可以看作双向链表：有序，元素可重复，插入和删除块，查询速度一般  

| 命令                | 功能                                             |
| ------------------- | ------------------------------------------------ |
| LPUSH key element   | 左侧插入                                         |
| LPOP key            | 移除并返回左侧的元素没有则返回null               |
| RPUSH key element   | 右侧插入                                         |
| RPOP key            | 移除并返回右侧的元素没有则返回null               |
| LRANGE key star end | 返回一段角标内所有元素，从0开始                  |
| BLPOP和BRPOP        | 与LPOP和RPOP类似，只不过在没有元素时等待指定时间 |

使用List模拟栈

限定从一个放向插入与删除

使用List模拟队列

两个放向插入和删除

使用List模拟阻塞队列

两个放向插入和删除并且使用BLPOP和BRPOP

##### SET类型

无序，元素不可重复，查找快，支持交集、并集、差集等功能

| 命令                 | 功能                        |
| -------------------- | --------------------------- |
| SADD key member      | 向set中添加一个或多个元素   |
| SREM key member      | 移除set中指定元素           |
| SCARD                | 返回集合中元素个数          |
| SISMEMBER key member | 判断一个元素是否存在于set中 |
| SMEMBERS             | 获取集合中所有元素          |
| SINTER key1 key2     | 求交集                      |
| SDIFF                | 求差集(key1-key2)           |
| SUNION               | 求并集                      |

##### SortedSet类型

可排序set集合

根据score(得分)排序

元素不重复

查询速度快常用来实现排行榜

| 命令                         | 功能                                                  |
| ---------------------------- | ----------------------------------------------------- |
| ZADD key score member        | 向sorte set中添加一个或多个元素,已经存在则更新score值 |
| ZREM key member              | 移除sorte set中指定元素                               |
| ZCARD                        | 返回集合中元素个数                                    |
| ZSCORE key member            | 获取stored set中的指定元素的score值                   |
| ZRANK key mbmber             | 获取sorte set 中的指定元素的排名                      |
| ZCOUNT key min max           | 统计score值在给定范围内的元素个数                     |
| ZINCRBY key increment member | 让sorte set中的指定元素自增，步长指定                 |
| ZRANGE key min max           | 按照scoe排序后，获取指定排名范围内的元素              |
| ZRANGEBYSCORE key min max    | 按照scoe排序后，获取指定score范围内的元素             |
| ZINTER key1 key2             | 求交集                                                |
| ZDIFF                        | 求差集(key1-key2)                                     |
| ZUNION                       | 求并集                                                |

注意：所有排名默认时升序，如果要降序则在命令的Z后面添加REV(反转)