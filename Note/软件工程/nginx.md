# nginx是什么

高性能web服务器

优点占用内存少，并发能力强

# nginx在Linux的安装

1、使用远程连接工具连接linux操作系统

2、官网下载

3、相关依赖

```yaml
yum install gcc-c++
yum install -y pcre pcre-devel
yum install -y zlib zlib-devel
yum install -y openssl openssl-devel
tar -zxvf nginx-1.16.1.tar.gz //解压Nginx
cd nginx

./configure

make

make install
```

4、启动

在usr/local/nginx/sbin下有启动程序

# 常用命令

必须进入nginx的目录/usr/local/nginx/sbin

| 命令              | 作用            |
| ----------------- | --------------- |
| ./nginx -v        | 查看nginx版本号 |
| ./nginx           | 启动nginx       |
| ./nginx -s stop   | 关闭nginx       |
| ./nginx -s reload | 重新加载nginx   |

# 配置文件

nginx.conf

第一部分：全局块

```yaml
worker_processes  1;//处理并发数量的值
```

第二部分：events块

影响用户和服务器之间的网络连接

```yaml
events {
    worker_connections  1024;
}
```

第三部分：http块

配置最频繁的部分

分为http和server块

包括文件引入、MIME-Type定义、日志自定义、连接超时时间、单链接请求数上限等

```yaml
http {
    include       mime.types;
    default_type  application/octet-stream;

    #log_format  main  '$remote_addr - $remote_user [$time_local] "$request" '
    #                  '$status $body_bytes_sent "$http_referer" '
    #                  '"$http_user_agent" "$http_x_forwarded_for"';

    #access_log  logs/access.log  main;

    sendfile        on;
    #tcp_nopush     on;

    #keepalive_timeout  0;
    keepalive_timeout  65;
    #gzip  on;

    server {
        listen       80;
        server_name  localhost;

        #charset koi8-r;

        #access_log  logs/host.access.log  main;

        location / {
            root   html;
            index  index.html index.htm;
        }

        #error_page  404              /404.html;

        # redirect server error pages to the static page /50x.html
        #
        error_page   500 502 503 504  /50x.html;
        location = /50x.html {
        root   html;
        }

        # proxy the PHP scripts to Apache listening on 127.0.0.1:80
        #
        #location ~ \.php$ {
        #    proxy_pass   http://127.0.0.1;
        #}

        # pass the PHP scripts to FastCGI server listening on 127.0.0.1:9000
        #
        #location ~ \.php$ {
        #    root           html;
        #    fastcgi_pass   127.0.0.1:9000;
        #    fastcgi_index  index.php;
        #    fastcgi_param  SCRIPT_FILENAME  /scripts$fastcgi_script_name;
        #    include        fastcgi_params;
        #}

        # deny access to .htaccess files, if Apache's document root
        # concurs with nginx's one
  		#
        #location ~ /\.ht {
        #    deny  all;
        #}
    }

```

# 反向代理配置

1、实现效果：当浏览器访问www.123.com时，跳转到linux系统tomcat主页

2、需要tomcat服务

3、对外开放访问的端口

http默认访问80端口而https默认443端口

这边必须开放两个端口，一个是实际访问的端口，另一个是转发的端口

firewall-cmd --add-port=80/tcp --permanent

firewall-cmd --add-port=8080/tcp --permanent

重新加载

firewall-cmd --reload



查看已经开放的端口

firewall-cmd --list-all



## 反向代理实例1

第一步

本地host文件中配置域名和ip对应关系配置，实际工作中交给DNS分配

C:\Windows\System32\drivers\etc，是默认存放host文件的位置

这个时候已经可以通过www.123.com:8080访问到虚拟机的tomcat主页了但是默认http端口号是80.显然跟平常的不太一样

第二步

进入nginx的配置文件nginx.conf

```yaml
server {
    listen       80;//监听的端口号
    server_name  192.168.204.128;//监听的ip地址

    #charset koi8-r;

    #access_log  logs/host.access.log  main;

    location / {
    	proxy_pass http://127.0.0.1:8080;//转发到的路径
        root   html;
        index  index.html index.htm;

    }
    。。。
    。。。
}
```

启动nginx

再次从主机直接访问www.123.com发现不用加8080端口也可以直接访问了，配置成功

## 反向代理实例2

实现效果：使用nginx反向代理，根据访问的路径不同跳转到不同端口的服务当中

nginx监听端口为9001

额这边老师装了两个tomcat，但是我是yum源装的，两个不太方便，并且实际也不会出现，所以我只转发一个端口

访问http://192.168.204.128:9001/1 直接跳转到tomcat主页

访问http://192.168.204.128:9001/2跳转到etc下的a.html页



修改nginx配置文件：

```yaml
location [ =/~/~*/^~ ] url{
	
}
```

=:用于不含正则表达式的url，要求严格匹配

~:表示包含正则表达式，且区分大小写

~*:包含正则表达式但不区分大小写

^~:不包含正则表达式，要求nginx找到与标识url匹配度最高的location后使用该location处理请求



```yaml
server {
    listen       9001;//监听的端口号
    server_name  192.168.204.128;//监听的ip地址

    #charset koi8-r;

    #access_log  logs/host.access.log  main;

    location ~ /{
    proxy_pass http://127.0.0.1:8080;
    }
    location ~ /etc/{
    proxy_pass ;
    }
    root   html;
    index  index.html index.htm;

    。。。
    。。。
}
```

因为这边location只能跳转到端口，所以我没有办法测试，但是实际应该可以

开启9001端口的防火墙



# 负载均衡配置

实现效果：浏览器输入地址，nginx将请求平均到8080和8081端口中



配置文件：

```yaml
http {
    include       mime.types;
    default_type  application/octet-stream;

	#负载均衡服务列表，填写均匀分发的地址
	upstream myserver{
		server 192.168.204.128:8080;
		server 192.168.204.128:8081;
	}

    sendfile        on;



    keepalive_timeout  65;


    server {
        listen       80;
        server_name  localhost;



		#转发地址就填写负载均衡服务的名字
        location / {
        	proxy_pass http://myserver;
            root   html;
            index  index.html index.htm;
        }


        error_page   500 502 503 504  /50x.html;
        location = /50x.html {
        root   html;
        }


    }

```



分配策略：

1、轮询（默认）：每个请求按照访问时间逐一分配到不同的服务器，会自动剔除down掉的服务器

2、权重(weight)：权重默认为1，越大访问到的概率越高

```yaml
	upstream myserver{
		server 192.168.204.128:8080 weight = 2;
		server 192.168.204.128:8081 weight = 1;
	}
```

3、ip_hash:

开启方法

```yaml
	upstream myserver{
	    ip_hash
		server 192.168.204.128:8080;
		server 192.168.204.128:8081;
	}
```

每个请求按访问ip的hash结果分配，所以每个访客固定访问一个后端服务器，可以解决session问题

4、fair(第三方):

开启方法

	upstream myserver{
		server 192.168.204.128:8080;
		server 192.168.204.128:8081;
		fair
	}
按照后端服务器的响应时间来分配，响应时间短的先分配



# 动静分离配置

目前主要用两种实现方式：

1、把静态文件独立成单独的域名，放在独立的服务器上，这也是主流推崇的方法

2、动态和静态文件混合在一起发布，通过nginx分开



expires参数给资源设置一个浏览器缓存过期时间，减少与服务器之间的请求和流量，

准备：准备多种静态资源文件，放在服务器上，要求访问不同路径，能够访问到不同的静态资源



我准备的静态资源：在根目录/下创建data文件夹，data/下有image文件夹和www文件夹。image中有一张图片1.jpg，www中有一个html文件a.html

要求是通过nginx能够访问静态资源

```yaml
server {
    listen       80;//监听的端口号
    server_name  192.168.204.128;//监听的ip地址


	#好像没设么好讲的
    location /www/ {
        root   /data/;
        index  index.html index.htm;

    }
    location /image/ {
        root   /data/;
        autoindex on;

    }
    。。。
    。。。
}
```



通过[www.123.com/www/a.html](http://www.123.com/www/a.html)

和www.123.com/image/1.jpg

就可以访问到对应的资源

如果访问http://www.123.com/image/  注意最后这个/不能够去掉，就会显示image文件夹下的内容，这是因为autoindex on;这句开启列表访问的作用。

# 高可用配置

nginx可能会宕机，这时请求会失效

什么是高可用？

当我一个nginx服务器宕机，还能够处理请求，一台主服务器master一台备份服务器backup，当主服务器宕机，自动切换到备份服务器



需要用到的软件：keepalived



keepalived起到路由作用，使用虚拟ip，keepalived会通过脚本检测nginx服务器是否宕机，如果宕机就将虚拟ip绑定到另一个

keep的安装可以使用yum命令，也是非常方便了

```yaml
yum -y install keepalived
```

默认安装在/etc/keepalived

里面有配置文件keepalived.conf

```yaml
! Configuration File for keepalived

global_defs {
   notification_email {
     acassen@firewall.loc
     failover@firewall.loc
     sysadmin@firewall.loc
   }
   notification_email_from Alexandre.Cassen@firewall.loc
   smtp_server 192.168.200.1
   smtp_connect_timeout 30
   router_id LVS_DEVEL		#主机名，需要在服务器的/etc/hosts文件中配置
   vrrp_skip_check_adv_addr
   vrrp_strict
   vrrp_garp_interval 0
   vrrp_gna_interval 0
}

vrrp_script chk_http_port {
   script "/usr/local/src/nginx_check.sh"	#脚本文件位置
   interval 2       #检测脚本执行的间隔
   weight 2		#权重 
}

vrrp_instance VI_1 {
    state MASTER		#备份服务器上将MASTER改成BACKUP
    interface eth0		#网卡,使用ifconfig可以查看
    virtual_router_id 51	#主备用机的id值必须相同
    priority 100		#主备用机使用不同的优先级，主机要高与备用机
    advert_int 1		#每隔多少时间发送一个心跳，检验一下是否存货
    authentication {		#权限校验，使用密码校验
        auth_type PASS
        auth_pass 1111
    }
    virtual_ipaddress {
        192.168.204.129	#vrrp H虚拟地址
    }
}

```

```shell
n=`ps -C nginx --no-heading|wc -l`

#当nginx停止后，直接干掉keepalived，这样备收不到心跳包就会接管vip了
if [ $n -eq "0" ]; then
  /usr/bin/systemctl stop keepalived
fi
```

修改配置文件，然后新建脚本文件到指令路径

启动keepalived和nginx就可以使用了

```mysql
systemctl start keepalived.service
```

注意：虚拟ip一定要和主备用机在一个网段内

# 原理

worker数和cup数相同可以压榨cpu性能

worker连接数是2或者4



# 完结散花，休息一下把