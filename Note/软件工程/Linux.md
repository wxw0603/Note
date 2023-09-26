# 常用命令

| 命令                                      | 功能                     |
| ----------------------------------------- | ------------------------ |
| mount  -t  vboxsf  VMshare  /mnt/VMshare/ | 将所选分区挂载到所选目录 |
| mkdir 目录名                              | 创建目录                 |
| ll                                        | 查看当前目录             |
| make && make install                      | 编译并且安装             |
|                                           |                          |
|                                           |                          |
|                                           |                          |

# 服务器端配置流程

1、JAVAWEB环境部署

| 命令                                                         | 功能                     |
| ------------------------------------------------------------ | ------------------------ |
| yum list java*                                               | 查看yum源中java版本      |
| yum -y install java-1.8.0-openjdk*                           | 安装jdk                  |
| java -version                                                | 查看java版本             |
| make && make install                                         | 编译并且安装             |
| wget http://dev.mysql.com/get/mysql57-community-release-el7-10.noarch.rpm                                                                    yum -y install mysql57-community-release-el7-10.noarch.rpm                                                                                                        yum -y install * --nogpgcheck mysql-community-server | 安装mysql                |
| systemctl start mysqld.service                               | 启动数据库               |
| grep "password" /var/log/mysqld.log                          | 查看Mysql登录密码        |
| mysql -uroot -p                                              | 登录                     |
| set global validate_password_policy=0;  #修改密码安全策略为低（只校验密码长度，至少8位）。                                                         ALTER USER 'root'@'localhost' IDENTIFIED BY '12345678'; | 修改密码                 |
| GRANT ALL PRIVILEGES ON *.* TO 'root'@'%' IDENTIFIED BY '12345678'; | 授予root用户远程管理权限 |
| wget --no-check-certificate https://labfileapp.oss-cn-hangzhou.aliyuncs.com/apache-tomcat-8.5.72.tar.gz | 下载tomcat安装包         |
| tar -zxvf apache-tomcat-8.5.72.tar.gz                        | 解压tomcat安装包         |
| mv apache-tomcat-8.5.72 /usr/local/Tomcat8.5                 | 修改tomcat名字           |
| chmod +x /usr/local/Tomcat8.5/bin/*.sh                       | 为tomcat授权             |
| sed -i 's/Connector port="8080"/Connector port="80"/' /usr/local/Tomcat8.5/conf/server.xml | 修改默认端口为80         |
| /usr/local/Tomcat8.5/bin/./startup.sh                        | 启动tomcat               |

2、数据库导入

1. 将本地数据库导出成sql文件
2. 通过FileZillaClient上传服务器
3. 在服务器数据库执行sql文件

3、war包部署

1. web项目导出成war包
2. 通过FileZillaClient上传服务器
3. 放到tomcat对应的webapps目录下

# 基础篇

## 目录结构

bin：二进制文件夹，存访可直接执行的常用命令

sbin：存访系统级二进制命令，基本上管理员才可以使用

lib:库目录，存访共享库文件

lib64：64位的重要库文件

usr：用户所有应用程序和所需要的数据

boot：存访引导启动的文件，单独挂载分区

dev：设备目录，管理当前设备，硬件设备

etc：系统配置文件和子目录的总目录

home：存访每个普通用户的信息和个性化设置

root：存访系统管理员的用户信息

opt：可选目录，给第三方软件包留的位置

media：识别可移动媒体设备，u盘光驱等识别后挂载在此

mnt：可以把外部存储挂载在mnt，和media类似

proc：进程目录，存访现有硬件和线程信息

run：运行目录，存访当前运行信息，关系自动清理

srv：service，跟系统服务相关

sys：系统硬件信息

tmp：临时目录，临时存放一下随时可删除

var：可变目录，存放可变化的目录

## 文件操作

| 命令           | 作用                           |
| -------------- | ------------------------------ |
| vi 文件名      | 打开文件编辑                   |
| :q             | 在编辑模式下退出编辑           |
| yy             | 复制（一般模式）还可以加参数   |
| p              | 粘贴（一般模式）还可以加数字   |
| dd             | 删除行（一般模式）还可以加数字 |
| y$             | 复制当前光标位置到行结束       |
| y^             | 复制当前光标位置到行开头       |
| w              | 跳到下一个单词                 |
| yw             | 复制下一个单词                 |
| dw             | 删除下一个单词                 |
| x              | 往后剪切                       |
| X              | 往前剪切                       |
| d$             | 删除光标后面的                 |
| d^             | 删除光标前面的                 |
| r              | 替换字符                       |
| R              | 连续替换                       |
| gg             | 移动到开头                     |
| G              | 移动到末尾                     |
| :w             | 保存                           |
| :wq            | 保存并退出                     |
| :q!            | 不保存强制退出                 |
| /要查找的词    | n查找下一个，N往上查找         |
| ：set nu       | 显示行号                       |
| ：set nonu     | 不显示行号                     |
| ：s/old/new    | 替换当前行匹配到的第一个词     |
| ：s/old/new/g  | 替换当前行所有匹配到的词       |
| ：%s/old/new   | 替换所有行的第一个匹配词       |
| ：%s/old/new/g | 替换整个文档所有匹配词         |
|                |                                |

三种模式

![捕获3](C:\Users\wxw\Desktop\学习\Note\images\捕获3.PNG)

# 实操篇

# shell编程