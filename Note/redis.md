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

