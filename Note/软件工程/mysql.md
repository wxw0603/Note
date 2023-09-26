# Mysql

## SQL

### 分类表

| 分类 | 说明           |
| ---- | -------------- |
| DDL  | 数据库定义语言 |
| DML  | 数据库操作语言 |
| DQL  | 数据查询语言   |
| DCL  | 数据控制语言   |

## DDL

### 数据库操作

```mysql
show databases; //查询所有数据库
select database(); //查询当前数据库
create database [if not exists] 数据库名 [default charset 字符集] [collate 排序规则]; //创建数据库
drop database [if not exists] 数据库名; //删除数据库
use 数据库名; //使用数据库
```

### 数据表操作

```mysql
show tables; //查询当前数据库查询所有表
desc 表名; //查看表结构
show create table 表名; //查询指定建表语句
create table 表名(
    字段1 字段1类型[comment 字段1注释],
    字段2 字段2类型[comment 字段2注释],
    字段3 字段3类型[comment 字段3注释],
    ... 
    字段n 字段n类型[comment 字段n注释]
    
)[comment 表注释]; //创建表名
alter table 表名 add 字段名 类型 [comment 注释] [约束]; //添加字段
alter table 表名 modify 字段名 新数据类型; //修改数据类型
alter table 表名 change 旧字段名 新字段名 类型 [comment 注释] [注释]; //修改字段名和字段类型
alter table 表名 drop 字段名; //删除字段
alter table 表名 rename to 新表名; //修改表名
drop table [if exists] 表名; //删除表
truncate table 表名; //删除表并重新创建该表
```

#### 数据类型

| 类型                 | 范围                            |
| -------------------- | ------------------------------- |
| tinyint              | (-128,127);(0,255)              |
| smallint             | (-32768,32767);(0,65535)        |
| mediumint            | (-8388608,8388607);(0,16777215) |
| int/integer          | (-21亿,21亿);(0,42亿)           |
| bigint               | (-2^63,2^63-1);(0,2^64-1)       |
| float                |                                 |
| double               |                                 |
| dectmal              |                                 |
| char(定长，性能好)   | 0~255bytes                      |
| varchar(变长,性能差) | 0~65535bytes                    |
| tinyblob             | 0~255bytes                      |
| tinytext             | 0~255bytes                      |
| blob                 | 0~65535bytes                    |
| text                 | 0~65535bytes                    |
| mediumblob           | 0~16777215bytes                 |
| mediumtext           | 0~16777215bytes                 |
| longblob             | 0~42亿bytes                     |
| longtext             | 0~42亿bytes                     |
| date                 | yyyy-mm-dd                      |
| time                 | hh:mm:ss                        |
| year                 | yyyy                            |
| datetime             | yyyy-mm-dd hh:mm:ss             |
| timestamp            | yyyy-mm-dd hh:mm:ss 包含时间戳  |
|                      |                                 |

## DML

### 插入

```mysql
insert into 表名(字段1,字段2,...) value(值1,值2,...); //给指定字段添加数据
insert into 表名 value(值1,值2,...); //给全部字段添加数据
insert into 表名(字段1,字段2,...) value(值1,值2,...),(值1,值2,...),(值1,值2,...); //批量给指定字段添加数据
insert into 表名 value(值1,值2,...),(值1,值2,...),(值1,值2,...); //批量给全部字段添加数据
//字符串和日期型数据应该包含在引号当中
```

### 更新/删除

```mysql
update 表名 set 字段1=值1,字段2=值2,... [where 条件]; //更新，没有条件就更新整张表字段
delete from 表名 [where 条件]; //删除，没有条件就删除整张表数据

```

## DQL

### 语法结构--编写顺序

```mysql
select
	字段列表
from
	表名列表
where
	条件列表
group by
	分组字段列表
having
	分组后条件列表
order by
	排序字段列表
limit
	分页参数
```



### 基础查询

```mysql
select 字段1,字段2,字段3... from 表名; //查询多个字段
select * from 表名; //查询所有字段
select 字段1 [as 别名],字段2 [as 别名]... from 表名; //设置别名
select distinct 字段列表 from 表名; //去重
```

### 条件查询

```mysql
select 字段列表 from 表名 where 条件列表;
```





| 比较运算符          | 功能                                   |
| ------------------- | -------------------------------------- |
| >                   |                                        |
| >=                  |                                        |
| <                   |                                        |
| <=                  |                                        |
| =                   |                                        |
| <>或者!=            |                                        |
| between ... and ... | 否个范围之内，含最大最小值             |
| in(...)             | 在in列表之中的某个值,多选一            |
| like  占位符        | 模糊匹配,_匹配单个字符,%匹配任意个字符 |
| is NULL             | 是null                                 |
| and或&&             | 逻辑与                                 |
| or或\|\|            | 逻辑或                                 |
| not或!              | 逻辑非                                 |



### 聚合函数

将一列数据作为一个整体,进行纵向计算

| 函数  | 功能     |
| ----- | -------- |
| count | 统计数量 |
| max   | 最大值   |
| min   | 最小值   |
| avg   | 平均值   |
| sum   | 求和     |

```mysql
select 聚合函数(字段列表) from 表名; //所有null值不参与聚合函数运算
```



### 分组查询

```mysql
select 字段列表 from 表名 [where 条件] group by 分组字段名 [having 分组后过滤条件];
//having可以使用聚合函数，where不行
```

### 排序查询

```mysql
select 字段列表 from 表名 order by 字段1 排序方式1,字段2 排序方式2;；
```

#### 排序方式:

asc:升序(默认)

desc:降序

字段顺序确定排序关键字优先级

### 分页查询

```mysql
select 字段列表 from 表名 limit 起始索引,查询记录数;
```

起始索引=(查询页码-1)*每页显示记录数

分页查询在不同数据库中有不同实现

如果查询的是第一页数据,起始索引可以省略

### 执行顺序

```mysql
select
	字段列表-4
from
	表名列表--1
where
	条件列表--2
group by
	分组字段列表--3
having
	分组后条件列表
order by
	排序字段列表--5
limit
	分页参数--6
```

## DCL

### 查询用户

```mysql
use mysql;
select * from user;
```

### 创建用户

```mysql
create user '用户名'@'主机名' identified by '密码';
```



### 修改用户密码

```mysql
alter user '用户名'@'主机名' identified with mysql_native_password by '新密码';
```

### 删除用户

```mysql
drop user '用户名'@'主机名';
```

### 权限控制

| 权限                | 说明               |
| ------------------- | ------------------ |
| all, all privileges | 所有权限           |
| select              | 查询数据           |
| insert              | 插入数据           |
| update              | 修改数据           |
| delete              | 删除数据           |
| alter               | 修改表             |
| drop                | 删除数据库/表/视图 |
| create              | 创建数据库/表      |

### 查询权限

```mysql
show grants for '用户名'@'主机名';
```

### 授予权限

```mysql
grant 权限列表 on 数据库名.表名 to '用户名'@'主机名';
```

### 撤销权限

```mysql
revoke 权限列表 on 数据库名.表名 from '用户名'@'主机名';
```

## 函数

### 字符串函数

| 函数                     | 功能                                              |
| ------------------------ | ------------------------------------------------- |
| concat(s1,s2,s3...)      | 字符串拼接                                        |
| lower(str)               | 转为小写                                          |
| upper(str)               | 转为大写                                          |
| lpad(str,n,pad)          | 左填充，用pad对str左边进行填充，达到n个字符串长度 |
| rpad(str,n,pad)          | 右填充，用pad对str右边进行填充，达到n个字符串长度 |
| trim(str)                | 去掉字符串头和尾空格                              |
| substring(str,start,len) | 截取str从start开始的len个长度的字符串             |

### 数值函数

| 函数       | 功能                              |
| ---------- | --------------------------------- |
| ceil(x)    | 向上取整                          |
| floor(x)   | 向下取整                          |
| mod(x,y)   | x mod y                           |
| rand()     | 0~1随机数                         |
| round(x,y) | 求参数x的四舍五入的值,保留y位小数 |
|            |                                   |

### 日期函数

| 函数                              | 功能                                          |
| --------------------------------- | --------------------------------------------- |
| curdate()                         | 返回当前日期                                  |
| curtime()                         | 返回当前时间                                  |
| now()                             | 返回当前时间和日期                            |
| year(date)                        | 获取指定date的年份                            |
| month(date)                       |                                               |
| day(date)                         |                                               |
| date_add(date,interval expr type) | 返回一个日期/时间加上一个时间间隔expr后的时间 |
| datediff(date1,date2)             | 返回date1到date2之间的天数                    |

### 流程函数

| 函数                                                       | 功能                                                     |
| ---------------------------------------------------------- | -------------------------------------------------------- |
| if(value,t,f)                                              | 如果value为true，返回t不然返回f                          |
| ifnull(value1,value2)                                      | 如果value1不为空，返回value1，不然返回value2             |
| case when [val1] then [res1] ... ELSE [default] END        | 如果vla1为true，返回res1，...否则返回default默认值       |
| case [expr] when [val1] then [res1] ... ELSE [default] END | 如果expr的值等于val1，返回res1，...不然返回default默认值 |

## 约束

### 概念

作用与表中字段上的规则，用来限制数据，保证数据库中数据的正确、有效、完整性

### 分类

| 约束     | 描述                                                   | 关键字      |
| -------- | ------------------------------------------------------ | ----------- |
| 非空约束 | 限制字段值不能为空                                     | not null    |
| 唯一约束 | 保证该字段值都是唯一的，不重复的                       | unique      |
| 主键约束 | 主键是一行数据的唯一标识，要求非空且唯一               | primary key |
| 默认约束 | 如果未指定该字段值，采用默认值                         | default     |
| 检查约束 | 保证字段值满足某个条件                                 | check(条件) |
| 外键约束 | 用来让两张表数据之间建立连接，保证数据的一致性和完整性 | foreign key |

### 外键约束

```mysql
create table 表名(
	字段名 数据类型,
    ...
    [constraint] [外键名称] foreign key(外键字段名) references 主表(列表名)
);//建表时添加
```

```mysql
alter table 表名 add constraint 外键名称 foreign key(外键字段名) references 主表(主列表名) [on 行为 行为约束];//修改
```

```mysql
alter table 表名 drop foreign key 外键名称;
```

| 行为约束    | 说明                                                         |
| ----------- | ------------------------------------------------------------ |
| no action   | 当在父表中删除/更新应记录时，首先检查该记录是否有对应外键，如果有则不允许删除/更新 |
| restrict    | 同no action                                                  |
| cascade     | 当在父表中删除/更新应记录时，首先检查该记录是否有对应外键，如果有则也删除/更新外键在子表中的记录 |
| set null    | 当在父表中删除/更新应记录时，首先检查该记录是否有对应外键，如果有则更新外键在子表中的记录为null |
| set default | 父表有变更时，子表将外键设置成一个默认值(innodb不支持)       |

## 多表查询

### 多表关系

1:1

单表拆分，在任意一方加入外键，关联另一方主键，并且设置外键为唯一的(unique)

1:N

在多的一方建立外键，指向一的一方

N:M

建立第三张中间表，中间表至少包含两个外键，分别关联两方主键

### 多表查询概述和分类

指从多张表中查询数据

笛卡尔积:笛卡尔乘积是指在数学中，两个集合A和集合B的所有组合情况。(在多表查询时需要消除无效的笛卡尔积)

#### 连接查询

##### 内连接

相当于查询A、B交集部分数据

##### 外连接

左外连接：查询左表所有数据以及交集数据

右外连接：查询右表所有数据以及交集数据

##### 自连接

当前表与自身的连接查询，自连接必须使用别名

#### 子查询



### 连接查询

#### 内连接

隐式内连接

```mysql
select 字段列表 from 表1,表2 where 条件...;
```



显式内连接

```mysql
select 字段列表 from 表1 [inner] join 表2 on 连接条件...;
```

#### 外连接

左外连接

```mysql
select 字段列表 from 表1 left [outer] join 表2 on 条件...;//完全包含左表数据，包含交集数据
```

右外连接

```mysql
select 字段列表 from 表1 right [outer] join 表2 on 条件...;//完全包含右表数据，包含交集数据
```

#### 自连接



```mysql
select 字段列表 from 表A 别名A join 表A 别名B on 条件...;//用来自我迭代
select 字段列表 from 表1 别名1,表1 别名2 where 条件...;
```

#### 联合查询

把多次查询的结果合并起来，形成新结果集

```mysql
select 字段列表 from 表A...
union [all]//加all不去重
select 字段列表 from 表B...;
```

### 子查询

#### 概念

sql语句中嵌套select语句，称为嵌套查询，又称子查询

根据查询结果将子查询分为

| 分类       | 结果           |
| ---------- | -------------- |
| 标量子查询 | 结果为单个值   |
| 列子查询   | 结果为一列     |
| 行子查询   | 结果为一行     |
| 表子查询   | 结果为多行多列 |

#### 标量子查询

```mysql
select * from 表1 where 字段1 = (select 字段名称 from 表2 where 字段2 = 值2);
```

#### 列子查询

##### 操作符

| 操作符 | 描述                             |
| ------ | -------------------------------- |
| in     | 指定合集范围内多选一             |
| not in | 不在指定的集合范围内             |
| any    | 子查询返回列表中，有一个满足即可 |
| some   | 与any等同                        |
| all    | 子查询返回列表的所有值都必须满足 |
|        |                                  |

#### 行子查询

同上

## 事务

是一组操作的集合，它是一个不可分割的工作单位，事务会把所有的操作作为一个整体一起向系统提交或撤销操作请求，即这些操作要么同时成功，要么同时失败。

mysql默认自动提交sql语句

### 事务操作

```mysql
select @@autocommit;//查看事务提交方式
set @@autocommit = 0 ;//设置事务提交方式 
commit;//提交事务
rollback;//回滚事务
start transaction 或者 begin;//开启事务，可以在自动提交的方式下管理事务
```

### 特性

原子性一致性隔离性持久性

### 并发问题

| 问题       | 描述                                                         |
| ---------- | ------------------------------------------------------------ |
| 脏读       | 一个事务读取到另一个事务还没有提交的事务                     |
| 不可重复读 | 一个事务先后读取同一条数据但是两次读的数据不同               |
| 幻读       | 一个事务查询数据时没有对应数据行，但在插入的时候又发现已经存在 |

### 事务隔离级别

| 隔离级别              | 脏读 | 不可重复读 | 幻读 |
| --------------------- | ---- | ---------- | ---- |
| read uncommitted      | ✔    | ✔          | ✔    |
| read committed        | ╳    | ✔          | ✔    |
| repeatable read(默认) | ╳    | ╳          | ✔    |
| serializable          | ╳    | ╳          | ╳    |

```mysql
select @@transaction_isolation;//查看事务隔离级别
set [session|global] transaction isolation level {read uncommitted | read committed　｜　repeatable read　｜　serializable};
```

## 存储引擎

### 简介

存储数据、建立索引、更新/查询数据等技术的实现方式。是基于表的，而不是基于库的，所以存储引擎也可被称为表类型。

```mysql
create table 表名(
    字段1 字段1类型[comment 字段1注释],
    字段2 字段2类型[comment 字段2注释],
    字段3 字段3类型[comment 字段3注释],
    ... 
    字段n 字段n类型[comment 字段n注释]
    
)engine = innodb [comment 表注释]; //创建表时设置存储引擎

show engines;//查看当前数据库支持的存储引擎
```

### InnoDB

介绍：

mysql默认存储引擎，高可靠，高性能。

特点：

DML操作遵循ACID(事务四大特性)模型，支持事务

行级锁，提高并发访问性能

支持外键foreign key约束，保证数据完整性和正确性

文件：

xxx.ibd，innodb引擎的每张表都会对应这样一个表空间文件，存储该表表结构、数据和索引。

参数：innodb_file_per_table

逻辑存储结构：

tablespece：表空间

segment：段

extent：区

page：页

row：行

### MyISAM

介绍：

早期mysql默认存储引擎

特点：

不支持事务，不支持外键

支持表锁，不支持行锁

访问速度快

文件：

xxx.sdi：存储表结构信息

xxx.MYD：存储数据

xxx.MYI：存储索引

### Memory

介绍：

Memory引擎的表数据存储在内存中，作为临时表或缓存使用

特点：

内存存放

hash索引

文件：

xxx.sdi：存储表结构信息

## Linux安装Mysql

# 索引

## 结构

Mysql索引在存储引擎实现，不同存储引擎有不同结构

| 索引结构          | 描述                                                         | InnoDB    | MyISAM | Memory |
| ----------------- | ------------------------------------------------------------ | --------- | ------ | ------ |
| B+Tree索引        | 最常见的索引，大部分引擎都支持                               | 支持      | 支持   | 支持   |
| Hash索引          | 底层使用Hash表实现，不支持范围查询                           | 不支持    | 不支持 | 支持   |
| R-tree空间索引    | MyISAM引擎的一个特殊索引，主要用于地址空间数据类型，使用较少 | 不支持    | 支持   | 不支持 |
| Full-text全文索引 | 通过建立倒排索引，快速匹配文档，使用较少                     | 5.6后支持 | 支持   | 不支持 |

## Btree

B树就是B-树

m阶B-数具有条件：

1、树中每个节点最多有m棵子树

2、除根节点和叶节点外每个节点至少有[m/2]棵子树

3、根节点如果不是叶子节点则至少有两棵子树

4、所有叶节点在同一层上

具有性质：

```properties
树的度数=一个节点的子节点个数
树的阶数=最大度数
每个节点最多存储的key值=树的阶数-1
每个节点的子节点最大个数=树的阶数
```



## B+tree

## hash

