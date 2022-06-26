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

## 1、JAVAWEB环境部署

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

## 2、数据库导入

1. 将本地数据库导出成sql文件
2. 通过FileZillaClient上传服务器
3. 在服务器数据库执行sql文件

## 3、war包部署

1. web项目导出成war包
2. 通过FileZillaClient上传服务器
3. 放到tomcat对应的webapps目录下

# 使用感受

1、连接方便，本地使用ssh指令连接服务器端非常方便

2、环境部署快速，适合快速搭建网站，安装环境非常快，把war包部署后经过少量debug即可使用