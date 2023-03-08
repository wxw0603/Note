# 入门

## 什么是MQ

消息队列，先进先出的，有了MQ队列上游向下游发送信息只需要依赖MQ而不需要其他服务

## MQ的流量消峰

通过消息队列的缓冲可以让一秒内下的订单分散成一段时间来处理，虽然处理时间没有变化但是可以提高系统的最大负载，最起码比请求失败要好

## MQ的应用解耦

降低多个系统之间的耦合度，比如订单系统和库存，支付系统相关，使用消息队列可以对数据进行保存，使得支付系统出错，消息也得以保存，在修复完成后任然可以恢复数据

## MQ的异步处理

多线程的时候，分支线程的回调可以直接往MQ队列中传输消息，较为清晰。

## MQ的分类

1、ActiveMQ

优点：单机吞吐量万级，时效性ms级，可用性高，基于主从架构实现高可用性，基本上消息不会丢失

缺点：高吞吐量场景较少使用

2、Kafka

大数据的杀手锏，以其百万级的吞吐量名胜大噪，在数据采集，传输，存储的过程中发挥着举足轻重的作用。

优点：性能卓越，单机写入TPS约在百万条/秒，吞吐量高，时效性ms级可用性非常高，分布式，一数据多副本，少数机器宕机不会丢失数据

缺点：单机超过64个队列，Load会发生明显的飙高现象，队列越多，load越高

3、RocketMQ

出自阿里巴巴的开源产品，用java语言实现，参考了Kafka并做出了自己的改进。

优点：单机吞吐量十万级，可用性非常高，分布式架构，消息可以做到0丢失，MQ功能完善，支持10亿级别的消息堆积

缺点：支持的客户端语言不多

4、RabbitMQ

当前最主流的消息中间件之一。

优点：高并发，性能较好，吞吐量到达万级，MQ功能完备，健壮，稳定，易用，跨平台，支持多种语言

缺点：商业版需要收费

## RabbitMQ的概念

是一个消息中间件，接收并转发消息，存储和转发消息数据

## 四大核心概念

生产者：产生数据，发送消息的程序

交换机：接收来自生产者的消息，将消息分发推送到队列中

队列：内部数据结构，消息缓冲区

消费者：等待消息的程序

## 重要名词解释

Broker：接受和分发消息的应用，RabbitMQ Server就是Message Broker

Exchange：交换机，可对应多个队列

Queue：队列

Channel：信道，生产者和MQ之间通过信道建立连接，每个连接中有多条信道

Binding：交换机和队列之间的联系

## RabbitMQ六大核心部分（模式）

1、"Hello World!"简单模式

2、Work queues工作模式

3、Public/Subscribe发布订阅模式

4、Routing路由模式

5、Topics主题模式

6、Publisher Confirms发布确认模式

# 核心部分

## 安装

下载erlang安装包(rabbitmq前置语言包)[Releases · rabbitmq/erlang-rpm (github.com)](https://github.com/rabbitmq/erlang-rpm/releases)

下载rabbitmq[Downloading and Installing RabbitMQ — RabbitMQ](https://www.rabbitmq.com/download.html)

yum安装socat

```yaml
rpm -ivh erlang-21.3.1-1.el7.x86_64.rpm
yum -y install socat
rpm -ivh rabbitmq-server-3.8.8-1.el7.noarch.rpm
```

## 基本命令

```yaml
chkconfig rabbitmq-server on	#添加开机自启动mq

/sbin/service rabbitmq.server start		#启动rabbitmq

/sbin/service rabbitmq.server status		#查看rabbitmq状态

/sbin/service rabbitmq.server stop		#停止rabbitmq

```

## 安装Web界面插件

```yaml
rabbitmq-plugins enable rabbitmq_management
```

使用http访问该虚拟机的15672端口即可

## 为Web界面添加新用户

```yaml
rabbitmqctl add_user admin 123	#创建新用户
rabbitmqctl set_user_tags admin administrator	#设置用户角色

rabbitmqctl set_permissions -p "/" admin ".*" ".*" ".*" 	#设置用户权限，用户具有vhost1这个virtual host中所有资源的配置、写、读权限。每个vhost代表一个库，不同库中的交换机和队列是不同的

rabbitmqctl list_users	#查看用户

```

## "Hello World!"简单模式

搭建maven项目

```xml
  <dependencies>
    <dependency>

      <groupId>junit</groupId>
      <artifactId>junit</artifactId>
      <version>3.8.1</version>
      <scope>test</scope>

      <!-- https://mvnrepository.com/artifact/com.rabbitmq/amqp-client -->
      <dependency>
        <groupId>com.rabbitmq</groupId>
        <artifactId>amqp-client</artifactId>
        <version>5.8.0</version>
      </dependency>
      
      <!-- https://mvnrepository.com/artifact/commons-io/commons-io -->
      <dependency>
        <groupId>commons-io</groupId>
        <artifactId>commons-io</artifactId>
        <version>2.6</version>
      </dependency>

    </dependency>
  </dependencies>
```



### 生产者代码

rabbitmq默认端口5672，开放5672端口

```java
package one;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

/**
 * 生产者
 * 作用：发消息
 */
public class Producer {
    public static final String QUEUE_NAME = "Hello";    //队列名

    public static void main(String[] args) throws Exception{
        //创建一个连接工厂
        ConnectionFactory factory = new ConnectionFactory();

        //工厂ip 连接rabbit的队列
        factory.setHost("192.168.204.128");

        //用户名和密码
        factory.setUsername("admin");
        factory.setPassword("123");

        //创建连接
        Connection conn = factory.newConnection();

        //获取信道
        Channel channel = conn.createChannel();

        /**
         * 创建一个队列
         * 1、名称
         * 2、是否需要保存消息，即持久化，默认消息存储在内存中即不具有持久化
         * 3、该队列是否只供一个消费者消费，是否进行消息共享
         * 4、是否自动删除
         * 5、其他参数
         */
        channel.queueDeclare("Hello",false,false,false,null);

        //发消息
        String message = "Hello World!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!";

        /**
         * 发送消息
         * 1、发送到哪个交换机
         * 2、路由的key值
         * 3、其他参数信息
         * 4、发送消息的消息体
         */
        channel.basicPublish("",QUEUE_NAME,null,message.getBytes());

        System.out.println("消息发送完毕");
    }

}

```

发送完毕后，页面中有显示队列



### 消费者代码

```java
package one;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;


/**
 * 消费者 接收消息
 */
public class Consumer {
    //队列名称
    public static final String QUEUE_NAME = "Hello";

    //接受消息
    public static void main(String[] args) throws Exception{
        //创建连接工厂
        ConnectionFactory factory = new ConnectionFactory();

        //设置连接ip和账号密码
        factory.setHost("192.168.204.128");
        factory.setUsername("admin");
        factory.setPassword("123");

        //创建连接
        Connection conn = factory.newConnection();

        //创建信道
        Channel channel = conn.createChannel();

        /**
         * 消费者消费消息
         * 1、队列名
         * 2、消费成功后是否自动应答
         * 3、消费者未成功消费的回调
         * 4、消费者取录消费的回调
         */
        channel.basicConsume(QUEUE_NAME,true,(x,y)->{System.out.println("haihai"+x+new String(y.getBody()));},(x)->System.out.println("haihai"+x));

    }

}

```

## Work queues工作模式

工作队列，又称任务队列，主要思想是避免立即执行资源密集型任务，安排任务在之后执行，把任务封装为消息并将其发送到队列。由多个工作线程，抢占式的执行任务，轮询的抢占，类似于nginx的worker

### 抽取连接工具类

```java
package tools;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * 此类为连接工厂，创建信道的工具类
 */
public class RabbitMQUtils {
    public static Channel getChannel(){
        Connection connection = null;
        Channel channel = null;
        ConnectionFactory factory = new ConnectionFactory();
        factory.setUsername("admin");
        factory.setPassword("123");
        factory.setHost("192.168.204.128");
        try {
            connection = factory.newConnection();
            channel = connection.createChannel();
        }catch (IOException | TimeoutException e) {
            e.printStackTrace();
        }
        return channel;
    }
}
```

### 多例消费者/工作线程代码

在IDEA中开启这个类的允许多个实例设置，然后修改输出的woerk01数据，就可以一个类开启多个进程

```java
package tow;

import com.rabbitmq.client.Channel;
import tools.RabbitMQUtils;

import java.io.IOException;

/**
 * 这是一个工作线程，也就是一个消费者
 */
public class Worker01 {
    //队列名称
    public static final String QUEUE_NAME = "Hello";

    //接受消息
    public static void main(String[] args) throws IOException {
        Channel channel = RabbitMQUtils.getChannel();
        System.out.println("Worker02等待接受消息.......");
        channel.basicConsume(QUEUE_NAME,true,(x,y)->System.out.println(new String(y.getBody())),(x)->System.out.println("获取失败"));
    }
}

```

这边开启两个worker01和worker02

### 生产者代码

```java
package tow;

import com.rabbitmq.client.Channel;
import tools.RabbitMQUtils;

import java.io.IOException;
import java.util.Scanner;

/**
 *
 * 生产者，可以发送大量消息
 */
public class Task01 {

    //队列名称
    public static final String QUEUE_NAME="Hello";

    //发送大量消息
    public static void main(String[] args) throws IOException {
        Channel channel = RabbitMQUtils.getChannel();
        channel.queueDeclare(QUEUE_NAME,false,false,false,null);

        //从控制台接受消息
        Scanner sc = new Scanner(System.in);
        while(sc.hasNext()){
            String message = sc.next();
            channel.basicPublish("",QUEUE_NAME,null,message.getBytes());
            System.out.println("发送消息:"+message+"完成");
        }
    }
}

```

结果是发送AA、BB、CC、DD结果发现，worker01收到AACC，worker02收到BBDD，说明任务轮询的被分配给了两个消费者。

## 消息应答

概念：消费者处理一个任务可能需要一段时间，如果处理一个较长的任务，而中途突然挂掉了，会发生什么情况呢。消息可能会丢失，因为消费者并没有把任务完成，而队列不知道消费者出现问题，将对列中的消息删除了，就发生了信息丢失。为了保证消息在发送过程中不丢失，rabbitmq引入了消息应答机制：消费者在接受到消息并处理该消息之后，告诉rabbitmq它已经处理了，rabbitmq可以把消息删除了。

### 自动应答

以接收到消息为准，在接收到消息后立即做出应答，再执行程序，速度快但是安全性差，所以自动应答尽量少使用。

### 手动应答

```java
Channel.basicAck(用于肯定确认)	//知道该消息并且已经处理成功，可以将其丢弃了
Channel.basicNack(用于否定确认)		//不处理该消息了，直接丢弃
Channel.baiscReject(用于否定确认)		//相比Channel.basicNack少了一个批量处理的参数

```

### 批量应答

批量应答是手动应答的一个好处，可以减少网络拥堵

```java
Channel.basicAck(xxxx,true)	//第二个参数为multiple，为true标识批量应答channel上未应答的消息，比如channel上有，4，5，6，7，8，收到了8的应答，那么4567都会被收到应答确认。
//false则只会应答第8个
```

### 消息重新入队

如果消费者由于某些原因失去连接(其通道已关闭、连接已关闭或TCP连接丢失)，导致消息未发送ack确认。Rabbitmq将知道，消息未完全处理，将对其重新排队。如果此时其他消费者可以处理，将会很快将其重新分发给另一个消费者。这样即使某个消费者偶尔死亡，也可以确保不会丢失任何消息。

#### 生产者代码

```java
package three;

import com.rabbitmq.client.Channel;
import tools.RabbitMQUtils;

import java.util.Scanner;

/**
 * 目标：实现在自动应答时，确认消息不丢失、丢失的消息重回对列
 */
public class Task02 {
    public static final String QUEUE_NAME="ack_queue";

    public static void main(String[] args) {
        Channel channel = RabbitMQUtils.getChannel();

        try {
            //声明队列
            channel.queueDeclare(QUEUE_NAME, false, false, false, null);

            Scanner sc = new Scanner(System.in);
            while(sc.hasNext()){
                String msg = sc.next();
                channel.basicPublish("",QUEUE_NAME,null,msg.getBytes("UTF-8"));
                System.out.println("生产者发出消息: "+msg);
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }
}

```

#### 消费者代码(两个)

```java
package three;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;
import tools.RabbitMQUtils;

import java.util.function.BiConsumer;

/**
 * 手动应答时不丢失
 */
public class Worker02 {
    public static final String QUEUE_NAME="ack_queue";

    public static void main(String[] args) {
        Channel channel = RabbitMQUtils.getChannel();
        System.out.println("C1等待接受消息处理，时间较短");

        DeliverCallback deliverCallback = (x,y)->{
            try {
                //沉睡一秒
                Thread.sleep(1000);

                System.out.println("Worker02处理完毕消息："+ new String(y.getBody()));

                /**
                 * 手动应答
                 * 第一个参数：消息标记，也就是回应哪个消息的完成消息
                 * 第二个参数：是否批量应答
                 */
                channel.basicAck(y.getEnvelope().getDeliveryTag(),false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        };

        try {
            //采用手动应答
            channel.basicConsume(QUEUE_NAME, false,deliverCallback, (x) -> System.out.println("worker02取消消费，接口回调"));
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
```

```java
package three;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;
import tools.RabbitMQUtils;

/**
 * 手动应答时不丢失
 */
public class Worker03 {
    public static final String QUEUE_NAME="ack_queue";

    public static void main(String[] args) {
        Channel channel = RabbitMQUtils.getChannel();
        System.out.println("C2等待接受消息处理，时间较长");

        DeliverCallback deliverCallback = (x, y)->{
            try {
                //沉睡一秒
                Thread.sleep(1000*30);

                System.out.println("Worker03处理完毕消息："+ new String(y.getBody()));

                /**
                 * 手动应答
                 * 第一个参数：消息标记，也就是回应哪个消息的完成消息
                 * 第二个参数：是否批量应答
                 */
                channel.basicAck(y.getEnvelope().getDeliveryTag(),false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        };

        try {
            //采用手动应答
            channel.basicConsume(QUEUE_NAME, false,deliverCallback, (x) -> System.out.println("worker03取消消费，接口回调"));
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}

```

结果，先发送aa，bb，消息被两个消费者收到，再发送cc，dd，在处理时间长的消费者未处理完之前停止，会发现dd最后由另一个处理。

## 队列持久化

概念：如何保障当Rabbitmq服务停掉以后消息生产者发送过来的消息不丢失呢，我们需要将队列和消息都标记为持久化。

```java
channel.queueDeclare(QUEUE_NAME, true, false, false, null);	//第二个参数改为true就可以开启队列持久化
```

注意如果先前声明过同名的队列但不是持久化的，要把原先的删除再创建，不然会报错

## 消息持久化

队列如果持久化，只是队列持久化，还是不能保存用户发来的数据的，发消息的时候就需要说明，这个消息是持久化的。

```java
channel.basicPublish("",QUEUE_NAME,MessageProperties.PERSISTENT_TEXT_PLAIN,msg.getBytes("UTF-8"));		//第三个参数将消息标记为持久化，告诉rabbitmq将消息存储在磁盘，但是这里依然存在消息刚准备存储时，还未存储完时发生意外，持久性保证并不强，如果需要更强有力的持久化策略，参考后面的发布确认技术。
```



# 高级部分

# 集群部分

