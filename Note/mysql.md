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

