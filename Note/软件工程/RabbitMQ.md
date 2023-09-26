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

## 不公平分发

默认rabbitmq采用轮询分发，但是如果一个消费者处理很快，一个很慢，会造成处理快的消费者长时间处于空闲状态。

为了避免这种情况，我们可以设置参数

```java
Channel.basicQos(1); //消费方设置qos参数为1，表示不公平分发，默认是0
```

## 预取值

```java
Channel.basicQos(预取值); //表示不公平分发，表示分发给单个信道的最多消息数量
```

在消息堆积的时候演示会很明显，预取值就是设置该消费者可以积压多少消息

## 发布确认(结局消息不丢失)

队列持久化保证队列不丢失、消息持久化保证消息不丢失，但是仍然存在传输过程中出现问题，导致还未完全保存到磁盘就宕机的可能。

所以需要发布确认：在保存完毕，持久化完成，向生产者发布确认。

开启发布确认：

```java
channel.confirmSelect();
```

### 单个发布确认(同步)

发一条确认一次，发布速度慢

### 批量发布确认

极大提高吞吐量，但是出现问题是，不知道具体是哪个消息出现问题

### 异步发布确认

批量、编号的发送消息，会收到确定的丢失的消息编号。通过回调函数，broker会通知生产者哪些成功接受，哪些丢失。

也就是，要写监听器。

```java
channel.addConfirmListener((l,n)->{},(l,n)->{});	//两个函数式接口分别表示监听成功的和监听失败的
```

接收应当要开启一个线程，负责接收所有消息的确认，中间需要用多线程的ConncurrentLinkedQueue队列或者ConcurrentSkipListMap哈希表来连接

### 生产者

```java
package four;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.MessageProperties;
import tools.RabbitMQUtils;

import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;

/**
 * 发布确认模式
 * 1、单个确认
 * 2、批量确认
 * 3、异步批量确认
 */
public class Task04 {
    //信道名称
    public static final String QUEUE_NAME="durable_queue";

    //批量发消息的个数
    public static final int COUNT = 1000;

    /**
     * 单个确认
     * 执行用时2013毫秒
     */
    public static void publishMessageOne(){
        //获取信道
        Channel channel = RabbitMQUtils.getChannel();
        try {
            //开启确认发布功能
            channel.confirmSelect();

            //创建持久化队列
            channel.queueDeclare(QUEUE_NAME,true,false,false,null);
            //批量发消息
            for(int i = 0 ; i < COUNT ; i++){
                String msg = i+"";
                channel.basicPublish("",QUEUE_NAME,MessageProperties.PERSISTENT_TEXT_PLAIN,msg.getBytes("UTF-8"));
                System.out.println("发送消息："+msg);
                //单个消息确认
                boolean flag = channel.waitForConfirms();
                if(!flag){
                    System.out.println("未收到确认，发生异常，暂停发送");
                    break;
                }
                System.out.println("收到"+msg+"消息的确认");
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 批量确认
     * 执行用时587毫秒
     */
    public static void publishMessageMany(){
        //获取信道
        Channel channel = RabbitMQUtils.getChannel();

        int basicSize = 100;

        try {
            //开启确认发布功能
            channel.confirmSelect();

            //创建持久化队列
            channel.queueDeclare(QUEUE_NAME,true,false,false,null);
            //批量发消息
            for(int i = 1 ; i <= COUNT ; i++){
                String msg = i+"";
                channel.basicPublish("",QUEUE_NAME,MessageProperties.PERSISTENT_TEXT_PLAIN,msg.getBytes("UTF-8"));
                System.out.println("发送消息："+msg);
                //批量消息确认
                if(i%basicSize==0){
                    boolean flag = channel.waitForConfirms();
                    if(!flag){
                        System.out.println("未收到确认，发生异常，暂停发送");
                        break;
                    }
                    System.out.println("到第"+i+"的前一百条消息已经收到");
                }
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 异步发布确认
     * 执行用时623毫秒
     */
    public static void publishMessageAsync(){
        //获取信道
        Channel channel = RabbitMQUtils.getChannel();

        int basicSize = 100;

        try {
            //开启确认发布功能
            channel.confirmSelect();

            //线程安全有序的一个哈希表，适用于高并发
            ConcurrentSkipListMap<Long,String> map = new ConcurrentSkipListMap<>();

            //消息监听器，监听哪些消息发送成功，哪些消息发送失败
            channel.addConfirmListener((l,n)->{
                if(n){
                    //批量确认，批量删除
                    ConcurrentNavigableMap<Long,String> concurrentNavigableMap = map.headMap(l);
                    concurrentNavigableMap.clear();
                }
                else{
                    //单个删除
                    map.remove(l);
                }
                    System.out.println("消息发送成功,消息编号:"+l);
                },(l,n)->{System.out.println("消息发送失败,消息编号:"+l);});

            //创建持久化队列
            channel.queueDeclare(QUEUE_NAME,true,false,false,null);
            //批量发消息
            for(int i = 1 ; i <= COUNT ; i++){
                String msg = i+"";
                channel.basicPublish("",QUEUE_NAME,MessageProperties.PERSISTENT_TEXT_PLAIN,msg.getBytes("UTF-8"));
                map.put(channel.getNextPublishSeqNo(),msg);//第一个参数是消息编号
                System.out.println("发送消息："+msg);
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();
        //publishMessageOne();
        //publishMessageMany();
        publishMessageAsync();
        long endTime = System.currentTimeMillis();
        System.out.println("执行用时"+(endTime-startTime)+"毫秒");
    }

}
```



## 交换机(Exchanges)

概念：rabbitmq的核心思想是生产者生产的信息不会直接发送到队列，之前的都是发到默认交换机。

作用：从信道接受消息，复制，路由到所需队列，实现一个任务可以被处理两次，具体由交换机的类型决定。

类型：

direct直接类型(路由类型)、topic主题类型、fanout扇出类型(发布订阅类型)、无名类型(默认类型)

### 创建临时队列

连接断开就会被自动删除的队列就是临时队列，也就是不带持久化的队列就是临时队列，可以让rabbitmq帮助我们创建名称随机的临时对列

```java
channel.queueDedare().getQueue();//返回值为队列名称
```

### 绑定

交换机与队列之间的捆绑关系，在图形化界面绑定，需要填写routingkey。routingkey是绑定的表识。

## fanout交换机(扇出)(广播)(发布订阅模式)

### 生产者

```java
package five;

import com.rabbitmq.client.Channel;
import tools.RabbitMQUtils;

import java.io.IOException;
import java.util.Scanner;


/**
 * 生产者
 */
public class Task05 {
    //交换机名称
    public static final String EXCHANGE_NAME="myexchange";

    public static void main(String[] args) throws IOException {
        Channel channel = RabbitMQUtils.getChannel();

        //声明交换机，第一个参数是交换机名字，第二个是交换机类型
        channel.exchangeDeclare(EXCHANGE_NAME,"fanout");

        //生成临时队列
        String queue = channel.queueDeclare().getQueue();

        /**
         *  队列绑定交换机
         *  1、队列名
         *  2、交换机名
         *  3、routingkey
         */
        channel.queueBind(queue,EXCHANGE_NAME,"");

        Scanner sc = new Scanner(System.in);
        while(sc.hasNext()){
            String msg = sc.next();
            channel.basicPublish(EXCHANGE_NAME,queue,false,null,msg.getBytes("UTF-8"));
            System.out.println("生产者发出消息:"+msg);
        }


    }
}

```

### 消费者

两个就一点区别，所已只展示一个

```java
package five;

import com.rabbitmq.client.Channel;
import tools.RabbitMQUtils;

import java.io.IOException;

/**
 *消费者
 */
public class Worker05 {
    //交换机名称
    public static final String EXCHANGE_NAME="myexchange";

    public static void main(String[] args) throws IOException {
        Channel channel = RabbitMQUtils.getChannel();

        //声明交换机，第一个参数是交换机名字，第二个是交换机类型
        channel.exchangeDeclare(EXCHANGE_NAME,"fanout");

        //生成临时队列
        String queue = channel.queueDeclare().getQueue();

        /**
         *  队列绑定交换机
         *  1、队列名
         *  2、交换机名
         *  3、routingkey
         *  这边是消费者所有不需要标识
         */
        channel.queueBind(queue,EXCHANGE_NAME,"");

        channel.basicConsume(queue,true,(x,y)->System.out.println("生产者05接受到消息:"+new String(y.getBody())),
                (x)->System.out.println(x));

    }
}

```

## direct直接交换机(路由交换机)

和发布订阅模式类似，但是发布订阅模式的routingkey不起作用，而direct交换机可以使用routingkey绑定，生产者可以定向给想要送到的消费者发送。需要注意的是这个时候生产者向交换机发送消息，不需要创建信道了，而消费者需要创建不同信道，使用不同的routingkey。

### 生产者

```java
package six;

import com.rabbitmq.client.Channel;
import tools.RabbitMQUtils;

import java.io.IOException;
import java.util.Scanner;

/**
 * 
 */
public class Task06 {
    //交换机名称
    public static final String EXCHANGE_NAME="direct_change";

    public static void main(String[] args) throws IOException {
        Channel channel = RabbitMQUtils.getChannel();

        //声明交换机，第一个参数是交换机名字，第二个是交换机类型
        channel.exchangeDeclare(EXCHANGE_NAME,"direct");


        Scanner sc = new Scanner(System.in);
        while(sc.hasNext()){
            String msg = sc.next();
            String aClass = sc.next();
            channel.basicPublish(EXCHANGE_NAME,aClass,false,null,msg.getBytes("UTF-8"));
            System.out.println("生产者发出消息到消费者"+aClass+":"+msg);
        }


    }
}

```

### 消费者

有三个，这边给出一个

```java
package six;

import com.rabbitmq.client.Channel;
import tools.RabbitMQUtils;

import java.io.IOException;

public class Worker0601 {
    //交换机名称
    public static final String EXCHANGE_NAME="direct_change";

    public static void main(String[] args) throws IOException {
        Channel channel = RabbitMQUtils.getChannel();

        //声明交换机，第一个参数是交换机名字，第二个是交换机类型
        channel.exchangeDeclare(EXCHANGE_NAME,"direct");

        //生成临时队列
        String queue = channel.queueDeclare().getQueue();

        /**
         *  队列绑定交换机
         *  1、队列名
         *  2、交换机名
         *  3、routingkey
         *
         */
        channel.queueBind(queue,EXCHANGE_NAME,"1");

        channel.basicConsume(queue,true,(x,y)->System.out.println("消费者1接受到消息:"+new String(y.getBody())),
                (x)->System.out.println(x));

    }
}

```

## Topic交换机

和direct交换机类似但是topic交换机的routingkey必须是一个单词列表，以点号分隔开，最长不超过255个字节。还可以使用表达式占位符，*代替一个单词，#代替0个或多个单词。

注意：当一个队列绑定#,将接受所有数据，与fanout相同，没有*与#出现就像direct

### 生产者

```java
package seven;

import com.rabbitmq.client.Channel;
import tools.RabbitMQUtils;

import java.util.Scanner;

/**
 * topic交换机测试
 * 生产者
 */
public class Task07 {
    //交换机名称
    public static final String EXCHANGE_NAME="topic_exchange";

    public static void main(String[] args) throws Exception {
        Channel channel = RabbitMQUtils.getChannel();

        //声明交换机
        channel.exchangeDeclare(EXCHANGE_NAME,"topic");

        Scanner sc = new Scanner(System.in);
        while(sc.hasNext()){
            String msg = sc.next();
            String aClass = sc.next();
            channel.basicPublish(EXCHANGE_NAME,aClass,false,null,msg.getBytes());
            System.out.println("生产者7发送消息："+msg);
        }

    }


}
```



### 消费者

```java
package seven;

import com.rabbitmq.client.Channel;
import tools.RabbitMQUtils;

/**
 * topic交换机测试
 * 消费者01
 */
public class Worker0701 {
    public static final String EXCHANGE_NAME = "topic_exchange";

    public static void main(String[] args) throws Exception {
        Channel channel = RabbitMQUtils.getChannel();

        channel.exchangeDeclare(EXCHANGE_NAME,"topic");

        String queue = channel.queueDeclare().getQueue();

        channel.queueBind(queue,EXCHANGE_NAME,"*.abc.*");

        channel.basicConsume(queue,true,(x,y)->System.out.println("消费者07-1接受到消息："+new String(y.getBody())),
                x->System.out.println(x));

    }
}

```

## 死信队列

概念：无法被消费的消息叫做死信，由于特定的原因导致queue中的某些消息无法被消费，这样的消息如果没有后续处理，就变为了死信，有死信自然有了死信队列。

应用场景：为了保证订单业务的消息数据不消失，需要使用到Rabbitmq的死信队列机制，当消息消费发生异常时，将消息投入死信队列中，还有比如说：用户在商城下单成功并点击去支付后在指定时间未支付时自动失效。

死信要从普通队列转发到死信队列。

### 死信来源

1、消息TTL过期(存活时间)

2、队列达到最大长度

3、消息被拒绝(消费者产生拒绝应答，并且不重新发送)



## 时间过期死信

将消费者01启动然后关闭，打开生产者，消息没有01接收所以变为死信

### 生产者

```java
package eight;

import com.rabbitmq.client.Channel;
import tools.RabbitMQUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 死信队列
 * 消费者1，正常消费者
 */
public class Consumer0801 {
    //普通交换机名称
    public static final String NORMAL_EXCHANGE="normal_exchange";

    //死信交换机名称
    public static final String DEAD_EXCHANGE="dead_exchange";

    //普通队列名称
    public static final String NORMAL_QUEUE="normal_queue";

    //死信队列名称
    public static final String DEAD_QUEUE="dead_queue";


    public static void main(String[] args) throws IOException {
        Channel channel = RabbitMQUtils.getChannel();

        //声明交换机，类型为direct
        channel.exchangeDeclare(NORMAL_EXCHANGE,"direct");
        channel.exchangeDeclare(DEAD_EXCHANGE,"direct");

        //声明普通队列,同时设置转发到死信队列的参数
        Map<String, Object> map = new HashMap<>();
        //要设置：1、过期时间2、转发到的死信交换机
        //map.put("x-message-ttl",10000);//ms
        map.put("x-dead-letter-exchange",DEAD_EXCHANGE);
        //设置死信routingKey
        map.put("x-dead-letter-routing-key","lisi");

        channel.queueDeclare(NORMAL_QUEUE,false,false,false,map);


        //声明死信队列
        channel.queueDeclare(DEAD_QUEUE,false,false,false,null);


        //绑定队列
        channel.queueBind(NORMAL_QUEUE,NORMAL_EXCHANGE,"zhangsan");
        channel.queueBind(DEAD_QUEUE,DEAD_EXCHANGE,"lisi");



        channel.basicConsume(NORMAL_QUEUE,true,(x,y)->System.out.println("Consumer1收到消息:"+new String(y.getBody(),"UTF-8")),
                (x)->System.out.println(x));

    }

}
```

```java
package eight;

import com.rabbitmq.client.Channel;
import tools.RabbitMQUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 死信队列
 * 消费者2，死信消费者
 */
public class Consumer0802 {


    //死信队列名称
    public static final String DEAD_QUEUE="dead_queue";


    public static void main(String[] args) throws IOException {
        Channel channel = RabbitMQUtils.getChannel();

        channel.basicConsume(DEAD_QUEUE,true,(x,y)->System.out.println("Consumer2收到消息:"+new String(y.getBody(),"UTF-8")),
                (x)->System.out.println(x));
    }
}

```

### 消费者

```java
package eight;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import tools.RabbitMQUtils;

import java.io.IOException;
import java.util.Scanner;

/**
 * 死信队列
 * 生产者
 */
public class Producer08 {
    //普通交换机名称
    public static final String NORMAL_EXCHANGE="normal_exchange";


    public static void main(String[] args) throws IOException {
        Channel channel = RabbitMQUtils.getChannel();



        Scanner sc = new Scanner(System.in);

        //死信消息 设置TTL时间
        AMQP.BasicProperties properties = new AMQP.BasicProperties().builder()
                .expiration("10000").build();//ms


        while(sc.hasNext()){
            String msg = sc.next();


            channel.basicPublish(NORMAL_EXCHANGE,"zhangsan",properties,msg.getBytes("UTF-8"));
        }
    }


}
```

## 队列达到最大长度死信

代码在普通队列声明时加入配置最大队列长度

```java
//要设置：1、过期时间2、转发到的死信交换机3、最大队列changdu
//map.put("x-message-ttl",100000);//ms
map.put("x-dead-letter-exchange",DEAD_EXCHANGE);
//设置死信routingKey
map.put("x-dead-letter-routing-key","lisi");
//设置最大队列长度
map.put("x-max-length","6");

channel.queueDeclare(NORMAL_QUEUE,false,false,false,map);
```

在生产者中加入大量发送消息的代码

```java
new Thread(()->{
    while(true){
        try {
            channel.basicPublish(NORMAL_EXCHANGE,"zhangsan",properties,"1".getBytes("UTF-8"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}).start();
```

其余和上面的一样，注意这边因为上个程序刚运行过，队列已经存在，所以把原先的队列删掉再运行。

啊，操了都，哪怕发的再快消费者1都能处理掉，只能让消费者1休眠了O.o

在消费者1中的处理成功函数式接口中加入线程休眠

```java
channel.basicConsume(NORMAL_QUEUE,true,(x,y)->{
    try {
        TimeUnit.SECONDS.sleep(2);
    }catch (Exception e){
        e.printStackTrace();
    }
    System.out.println("Consumer1收到消息:"+new String(y.getBody(),"UTF-8"));
},(x)->System.out.println(x));
```

## 队列消息被拒绝，并且不放会队列

记得先将上面代码的消息过期时间和队列长度调正常，然后在消费者1中开启手动应答，并将"114514"消息过滤出来拒绝。

消费者1的消费代码如下:

```java
        //主动应答
        channel.basicConsume(NORMAL_QUEUE,false,(x,y)->{
//            try {
//                TimeUnit.SECONDS.sleep(2);
//            }catch (Exception e){
//                e.printStackTrace();
//            }
            System.out.println("Consumer1收到消息:"+new String(y.getBody(),"UTF-8"));
            if(new String(y.getBody(),"UTF-8").equals("114514")){
                //否定应答，并且不放回普通队列
                channel.basicReject(y.getEnvelope().getDeliveryTag(),false);
            }
            else{
                //正常应答，不开启批量
                channel.basicAck(y.getEnvelope().getDeliveryTag(),false);
            }
                },
                (x)->System.out.println(x));
```

最后结果消费者1任然会打印，因为是我们主动否定应答的，所以消费者1其实拿到过了。

死信队列会收到消息，然后打印

## 延迟队列

是死信队列的一种，消息过期产生的死信队列就是延迟队列，延迟队列队列内部有序，队列中的元素是希望在指定时间之前或之后取出或处理的

使用场景：

1、订单在十分钟之内未支付自动取消

2、新创建的店铺，如果十天之内没有上传过商品，自动发送消息提醒

3、用户注册成功后，如果三天没有登录，则进行短信提醒

4、用户发起退款，如果三天没有得到处理，则通知相关运营人员。

5、预定会议后，需要在预定的时间前十分钟通知相关人员参加会议。

## 整合springboot

### 依赖:

```xml
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <optional>true</optional>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>org.springframework.amqp</groupId>
    <artifactId>spring-rabbit-test</artifactId>
    <scope>test</scope>
</dependency>

<dependency>
    <groupId>com.alibaba</groupId>
    <artifactId>fastjson</artifactId>
    <version>2.0.23</version>
</dependency>


<dependency>
    <groupId>io.springfox</groupId>
    <artifactId>springfox-swagger2</artifactId>
    <version>2.9.2</version>
</dependency>

<dependency>
    <groupId>io.springfox</groupId>
    <artifactId>springfox-spring-web</artifactId>
    <version>2.9.2</version>
</dependency>

<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>

<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
</dependency>

<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-amqp</artifactId>
</dependency>

<dependency>
    <groupId>io.springfox</groupId>
    <artifactId>springfox-swagger-ui</artifactId>
    <version>2.9.2</version>
</dependency>


<dependency>
    <groupId>org.springframework.amqp</groupId>
    <artifactId>spring-rabbit-test</artifactId>
    <scope>test</scope>
</dependency>
```

### 配置文件:

```properties
spring.rabbitmq.host=192.168.204.129
spring.rabbitmq.port=5672
spring.rabbitmq.username=admin
spring.rabbitmq.password=123
```

### swagger配置类:

```java
package com.wxw.rabbitmqspringboot.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfiguration {

    @Bean
    public Docket webApiConfig(){

        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("webApi")
                .apiInfo(webApiInfo())
                .select()
                .build();
    }

    private ApiInfo webApiInfo(){

        return new ApiInfoBuilder()
                .title("rabbitmq接口文档")
                .description("本文档描述了rabbitmq微服务接口定义")
                .version("1.0")
                .contact(new Contact("wxw0603","http://wxwniubi.com","1711911489@qq.com"))
                .build();
    }
}
```

### 项目结构:

![捕获4](C:\Users\wxw\Desktop\学习\Note\images\捕获4.PNG)

### 项目队列和交换机配置:

```java
package com.wxw.rabbitmqspringboot.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;

@Configuration
public class TtlQueueConfiguration {
    //普通队列QA
    public static final String  A_QUEUE="QA";
    //普通队列QB
    public static final String  B_QUEUE="QB";

    //死信队列QD
    public static final String D_DEAD_LETTER__QUEUE = "QD";

    //普通交换机X
    public static final String X_EXCHANGE="X";

    //死信交换机Y
    public static final String Y_DEAD_LETTER_EXCHANGE="Y";

    //声明普通交换机X
    @Bean("X")
    public DirectExchange getXExchange(){
        return new DirectExchange(X_EXCHANGE);
    }

    //声明普通交换机Y
    @Bean("Y")
    public DirectExchange getYExchange(){
        return new DirectExchange(Y_DEAD_LETTER_EXCHANGE);
    }

    //声明普通队列QA
    @Bean("QA")
    public Queue getQA(){
        HashMap<String,Object> map = new HashMap<>(3);
        map.put("x-message-ttl",10000);
        map.put("x-dead-letter-exchange",Y_DEAD_LETTER_EXCHANGE);
        map.put("x-dead-letter-routing-key","YD");
        return QueueBuilder.durable(A_QUEUE).withArguments(map).build();
    }

    //声明普通队列QA
    @Bean("QB")
    public Queue getQB(){
        HashMap<String,Object> map = new HashMap<>(3);
        map.put("x-message-ttl",40000);
        map.put("x-dead-letter-exchange",Y_DEAD_LETTER_EXCHANGE);
        map.put("x-dead-letter-routing-key","YD");
        return QueueBuilder.durable(B_QUEUE).withArguments(map).build();
    }

    //声明死信队列QD
    @Bean("QD")
    public Queue getQD(){
        return QueueBuilder.durable(D_DEAD_LETTER__QUEUE).build();
    }

    //绑定QA和X
    @Bean
    public Binding qaX(@Qualifier("QA")Queue QA,@Qualifier("X")DirectExchange X){
        return BindingBuilder.bind(QA).to(X).with("XA");
    }

    //绑定QB和X
    @Bean
    public Binding qbX(@Qualifier("QB")Queue QB,@Qualifier("X")DirectExchange X){
        return BindingBuilder.bind(QB).to(X).with("XB");
    }

    //绑定QD和Y
    @Bean
    public Binding qdY(@Qualifier("QD")Queue QD,@Qualifier("Y")DirectExchange Y){
        return BindingBuilder.bind(QD).to(Y).with("YD");
    }
}
```

### 生产者

```java
package com.wxw.rabbitmqspringboot.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

/**
 * 发送延迟消息
 *
 * http://localhost:8080/ttl/sendMesg/嗨嗨嗨
 */

@Slf4j
@RestController
@RequestMapping(("/ttl"))
public class SendMessageController {

    //spring公司提供的工具类
    @Autowired
    private RabbitTemplate rabbitTemplate;

    //开始发消息
    @GetMapping("sendMesg/{message}")
    public void sendMessage(@PathVariable String message){
        log.info("当前时间:{},发送一条消息到两个队列:{}",new Date().toString(),message);
        rabbitTemplate.convertAndSend("X","XA","10s前来自QA的消息:"+message);
        rabbitTemplate.convertAndSend("X","XB","40s前来自QB的消息:"+message);
    }

}
```

### 消费者

```java
package com.wxw.rabbitmqspringboot.consumer;

import com.rabbitmq.client.AMQP;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Date;

@Slf4j
@Component
public class DeadLetterQueueConsumer {

    //接受消息
    @RabbitListener(queues = "QD")
    public void receiveD(Message message, AMQP.Channel channel)throws Exception{
        String s = new String(message.getBody());
        log.info("当前时间:{},收到死信队列的消息:{}",new Date().toString(),s);
    }
}
```

### 某些报错:

SpringBoot集成Swagger报错

报错提示：

```
Failed to start bean 'documentationPluginsBootstrapper';
```

如下图：

报错原因：

由于**Spring Boot 2.6.x** 请求路径与 Spring MVC 处理映射匹配的默认策略从`AntPathMatcher`更改为`PathPatternParser`。所以需要设置`spring.mvc.pathmatch.matching-strategy为ant-path-matcher`来改变它。

## 延时队列优化

新增一个QC，QC和X和Y绑定XC和YD，但是不设置延迟时间，由生产者根据需求设置ttl，用来满足变化的需求。

### 配置类代码

在上面的配置文件中加入以下代码

```java
//优化队列QC
public static final String C_QUEUE="QC";

//声明优化队列QC
@Bean("QC")
public Queue getQC(){
    HashMap<String,Object> map = new HashMap<>();
    map.put("x-dead-letter-exchange",Y_DEAD_LETTER_EXCHANGE);
    map.put("x-dead-letter-routing-key","YD");
    return QueueBuilder.durable(C_QUEUE).withArguments(map).build();
}

//绑定QC和X
@Bean
public Binding qcX(@Qualifier("QC") Queue QC,@Qualifier("X")DirectExchange X){
    return BindingBuilder.bind(QC).to(X).with("XC");
}
```

### 生产者

在Controller中加入

```java
//开始发消息
@GetMapping("/sendTtlMsg/{message}/{ttlTime}")
public void sendMessage(@PathVariable String message,@PathVariable String ttlTime){
    log.info("当前时间:{},发送一条消息到QC队列:{}",new Date().toString(),message);


    rabbitTemplate.convertAndSend("X","XC",ttlTime+"ms前来自QC的消息"+message,(msg)->{
        msg.getMessageProperties().setExpiration(ttlTime);
        return msg;
    });

}
```

### 基于死信存在的问题

存在问题就是如果使用在消息属性上设置ttl的方式，消息可能并不会按时死亡，因为rabbitmq只会检查第一个消息是否过期，如果过期就丢到死信队列，如果第一个消息延时时间很长，而第二个消息时间很短，第二个消息不会优先执行。

## 基于插件的延时队列

一个rabbitmq的插件，用于解决上面说的问题，实现消息粒度的延时队列[Community Plugins — RabbitMQ](https://www.rabbitmq.com/community-plugins.html)中的[rabbitmq_delayed_message_exchange-3.8.0.ez](https://github.com/rabbitmq/rabbitmq-delayed-message-exchange/releases/download/v3.8.0/rabbitmq_delayed_message_exchange-3.8.0.ez)

### 插件安装

解压安装包[rabbitmq_delayed_message_exchange-3.8.0.ez](https://github.com/rabbitmq/rabbitmq-delayed-message-exchange/releases/download/v3.8.0/rabbitmq_delayed_message_exchange-3.8.0.ez)到/usr/lib/rabbitmq_server-3.8.8/plugs

进入RabbitMQ的安装目录下的plugs目录/usr/lib/rabbitmq_server-3.8.8/plugs

执行命令rabbitmq-plugins enable rabbitmq_delayed_message_exchange

安装后重启rabbitmq服务

插件提供了新的交换机类型x-delayed-message，由交换机来控制延迟时间，而不是队列控制

![捕获5](C:\Users\wxw\Desktop\学习\Note\images\捕获5.PNG)

### 配置类

```java
package com.wxw.rabbitmqspringboot.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;

/**
 * 基于插件的死信队列
 */
@Configuration
public class PlugsDelayConfiguration {
    //交换机
    public static final String DELAYED_EXCHANGE = "delayed.exchange";
    //队列
    public static final String DELAYED_QUEUE = "delayed.queue";
    //routingkey
    public static final String DELAYED_ROUTING_KEY = "delayed.routingkey";

    @Bean(DELAYED_QUEUE)
    public Queue getQ(){
        return QueueBuilder.durable(DELAYED_QUEUE).build();
    }

    /**
     * 1、交换机名称
     * 2、自定义类型交换机
     * 3、是否需要持久化
     * 4、是否需要自动删除
     * 5、其他参数
     */
    @Bean(DELAYED_EXCHANGE)
    CustomExchange getE(){
        HashMap<String,Object> map = new HashMap<>();
        //参数定义交换机直接类型
        map.put("x-delayed-type","direct");
        return new CustomExchange(DELAYED_EXCHANGE,"x-delayed-message",true,false,map);
    }

    @Bean
    Binding getB(@Qualifier(DELAYED_QUEUE)Queue q,@Qualifier(DELAYED_EXCHANGE)CustomExchange e){
        return BindingBuilder.bind(q).to(e).with(DELAYED_ROUTING_KEY).noargs();
    }

}
```

### 生产者

```java
    //开始发消息
    @GetMapping("/send/{message}/{ttlTime}")
    public void sendDelay(@PathVariable String message,@PathVariable Integer ttlTime){
        log.info("当前时间:{},发送一条消息到延迟队列:{}",new Date().toString(),message);
        rabbitTemplate.convertAndSend(PlugsDelayConfiguration.DELAYED_EXCHANGE,PlugsDelayConfiguration.DELAYED_ROUTING_KEY
                ,ttlTime+"ms前来自延迟对列的消息"+message,(msg)->{
            msg.getMessageProperties().setDelay(ttlTime);
            return msg;
        });
    }
```

### 消费者

```java
package com.wxw.rabbitmqspringboot.consumer;

import com.wxw.rabbitmqspringboot.config.PlugsDelayConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * 基于插件的延迟队列消费者
 */
@Component
@Slf4j
public class DealyQueueConsumer {

    //监听消息
    @RabbitListener(queues = PlugsDelayConfiguration.DELAYED_QUEUE )
    public void receiveDelayQueue(Message message){
        String msg = new String(message.getBody());
        log.info("收到延迟队列的消息:{}，在{}",msg,new Date());
    }
}
```

解决了上面的队列中的消息排队处理的问题。

# 高级部分

## 发布确认高级

在生产环境中由于一些特殊原因导致rabbitmq重启，在rabbitmq重启期间生产者消息投递失败，导致消息丢失。需要手动处理和恢复。如何进行rabbitmq的消息可靠投递呢？就需要让交换机向生产者发布确认，来让生产者决定是否丢弃信息、缓存信息或者重新发送。同时让队列向交换机发布确认，来让交换机缓存消息，确保消息不丢失。其实是让发布确认多一个应答的功能，来确保消息送到。

### 代码架构

![捕获6](C:\Users\wxw\Desktop\学习\Note\images\捕获6.PNG)

### 配置

```java
package com.wxw.rabbitmqspringboot.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 高级发布确认配置文件
 */
@Configuration
public class ConfirmConfiguration {
    //交换机
    public static final String CONFIRM_EXCHANGE = "confirm.exchange";

    //队列
    public static final String CONFIRM_QUEUE = "confirm.queue";

    @Bean(CONFIRM_EXCHANGE)
    public DirectExchange getE(){
        return new DirectExchange(CONFIRM_EXCHANGE);
    }

    @Bean(CONFIRM_QUEUE)
    public Queue getQ(){
        return QueueBuilder.durable(CONFIRM_QUEUE).build();
    }

    @Bean
    public Binding getB(@Qualifier(CONFIRM_EXCHANGE)DirectExchange e,@Qualifier(CONFIRM_QUEUE)Queue q){
        return BindingBuilder.bind(q).to(e).with("key1");
    }

}
```

### 生产者

```java
package com.wxw.rabbitmqspringboot.controller;

import com.wxw.rabbitmqspringboot.config.ConfirmConfiguration;
import io.swagger.annotations.ApiKeyAuthDefinition;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 发布确认高级生产者
 */
@RestController
@RequestMapping("/confirm")
@Slf4j
public class ProducerController {

    @Autowired
    RabbitTemplate template;

    //发消息
    @GetMapping("/sendMessage/{message}")
    public void sendMessage(@PathVariable String message){
        template.convertAndSend(ConfirmConfiguration.CONFIRM_EXCHANGE,"key1",message);
        log.info("发送消息内容为{}",message);
    }

}
```

### 消费者

```java
package com.wxw.rabbitmqspringboot.consumer;

import com.wxw.rabbitmqspringboot.config.ConfirmConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * 发布确认高级
 * 消费者
 */
@Component
@Slf4j
public class Consumer {

    @RabbitListener(queues = ConfirmConfiguration.CONFIRM_QUEUE)
    public void receiveConfirmMessage(Message message){
        String s = new String(message.getBody());
        log.info("接收到消息:{}",s);

    }
}
```

### 回调接口(如果交换机收不到消息)

由生产者调用回调接口

开启yaml中的交换机确认配置,默认为none禁用。

```properties
spring.rabbitmq.publisher-confirm-type=correlated
```

```java
//自行创建回调时的标记
        template.convertAndSend(ConfirmConfiguration.CONFIRM_EXCHANGE,"key1",message,new CorrelationData("欸嘿"));    
		 /**
         * 1消息接收成功
         * 1.1、保存消息的id和相关信息
         * 1.2、交换机是否收到消息 true
         * 1.3、null
         * 2消息接受失败
         * 2.1、保存消息的id和相关信息
         * 2.2、false
         * 2.3、失败原因
         */
        template.setConfirmCallback((messageID,ack,cause)->{});
```

```java
package com.wxw.rabbitmqspringboot.controller;

import com.wxw.rabbitmqspringboot.config.ConfirmConfiguration;
import io.swagger.annotations.ApiKeyAuthDefinition;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 发布确认高级生产者
 */
@RestController
@RequestMapping("/confirm")
@Slf4j
public class ProducerController {

    @Autowired
    RabbitTemplate template;

    //发消息
    @GetMapping("/sendMessage/{message}")
    public void sendMessage(@PathVariable String message){
        //自行创建回调时的标记
        template.convertAndSend(ConfirmConfiguration.CONFIRM_EXCHANGE,"key1",message,new CorrelationData("欸嘿"));

        /**
         * 1消息接收成功
         * 1.1、保存消息的id和相关信息
         * 1.2、交换机是否收到消息 true
         * 1.3、null
         * 2消息接受失败
         * 2.1、保存消息的id和相关信息
         * 2.2、false
         * 2.3、失败原因
         */
        template.setConfirmCallback((messageID,ack,cause)->{
            if(ack){
                log.info("生产者检测到消息{}被交换机成功接收",messageID.getId());
            }
            else{
                log.info("生产者检测到交换机接收消息{}失败",messageID.getId());
            }
        });
        log.info("发送消息内容为{}",message);
    }

}
```

可以解决生产者和交换机之间的可靠传输，但不能解决交换机和队列之间的可靠传输。

### 回退消息(解决队列无法收到消息)

在仅开启了生产者确认机制的情况下，交换机接收到消息后，会直接给消息生产者发送确认消息，如果发现该消息不可路由，那么消息会被直接丢弃，此时生产者是不知道丢弃消息这个事件的。那么如何让无法路由的消息帮我想办法处理一下，最起码通知生产者，通过设置mandatory参数可以在消息传递过程中不可达目的地时将消息返还给生产者。

打开回退消息配置

```properties
spring.rabbitmq.publisher-returns=true
```

```java
        template.setReturnsCallback((msg)->{
            log.info("消息被回退",msg);
        });
```

## 备份交换机

无法投递的消息将发送给备份交换机，备份交换机通过自己的队列传输给消费者或者报警装置。

在上一个发布确认高级的代码基础上增加功能

架构图:

![捕获7](C:\Users\wxw\Desktop\学习\Note\images\捕获7.PNG)



由于报警消费者和备份消费者差不多，所以我就不写备份的队列和备份消费者队列，只写报警队列了。

### 配置

```java
@Bean(CONFIRM_EXCHANGE)
public DirectExchange getE(){
    HashMap<String,Object> map = new HashMap<String, Object>();
    //将交换机与默认交换机绑定
    map.put("alternate-exchange",BackupConfiguration.BACKUP_EXCHANGE);
    return ExchangeBuilder.directExchange(CONFIRM_EXCHANGE).withArguments(map).build();
}
```

```java
package com.wxw.rabbitmqspringboot.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 备份交换机配置
 */
@Configuration
public class BackupConfiguration {

    //备份交换机
    public static final String BACKUP_EXCHANGE="back.exchange";

    //报警队列
    public static final String WARNING_QUEUE="warning.queue";

    //声明交换机
    @Bean(BACKUP_EXCHANGE)
    public FanoutExchange getE(){
        return new FanoutExchange(BACKUP_EXCHANGE);
    }

    //声明队列
    @Bean(WARNING_QUEUE)
    public Queue getQ(){
        return QueueBuilder.durable(WARNING_QUEUE).build();
    }

    //绑定
    @Bean("b1")//这边是因为默认springboot不允许存在两个相同类型的没有名字的bean
    public Binding getB(@Qualifier(WARNING_QUEUE)Queue q,@Qualifier(BACKUP_EXCHANGE) FanoutExchange e){
        return BindingBuilder.bind(q).to(e);
    }

}

```

### 报警消费者

```java
package com.wxw.rabbitmqspringboot.consumer;

import com.wxw.rabbitmqspringboot.config.BackupConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * 备份交换机
 * 报警消费者
 */
@Component
@Slf4j
public class WarningConsumer {

    @RabbitListener(queues = BackupConfiguration.WARNING_QUEUE)
    public void receiveMsg(Message message){
        String msg = new String(message.getBody());
        log.info("警告，原交换机不可用，转到备用交换机，接收到消息:{}",msg);
    }

}

```

## RabbitMQ其他知识点

### 幂等性

用户对于同一操作发起的一次或者多次请求的结果是一致的，不会因为多次点击而产生副作用。就像支付，用户购买后支付扣款成功，但是返回结果的时候网络异常，用户再次点击按钮，此时会进行二次扣款，返回结果成功，用户查询余额发现多扣了，流水记录也是两条。在单应用操作中我们只需要放入事务，发生错误立即回滚，但是在响应客户端的时候也可能出现网络中断或者异常等等。



如：消费者消费在MQ中的消息时，MQ把消息发送给消费者，消费者在给MQ返回ack时网络中断，MQ未收到消息应答，该消息会重新发送给别的消费者，或者在网络重连后再次发送给该消费者，造成了消息的重复消费。



所以，一般使用全局id解决幂等性问题，或者写个唯一标识比如时间戳，或者uuid或者订单消费者消费MQ中的消息也可用MQ的id来判断，或者按自己的的规则生成一个全局唯一id，每次消费消息时用该id判断是否已经消费过。

### 消费端的幂等性保障

在订单海量生成的业务高峰时期，生产端有可能重复发送消息，这时候要求消费端实现幂等性。业界主流的幂等性有两种操作:a、唯一id+指纹码机制，利用数据库去重b、利用redis的原子性去实现

唯一id+指纹码机制：保障唯一性的一串id，利用查询得知id是否已经在数据库中存在，优势是实现简单，但是在高并发的时候如果是单个数据库就会有写入性能瓶颈，虽然可以优化但是也不是最推荐的方式。

redis原子性：利用redis执行setnx命令，天然具有幂等性，从而实现不重复消费。

## 优先级队列

使用场景：在我们系统中会用到一个订单催付功能，我们的客户在天猫下的订单，淘宝会及时将订单推送给我们，如果在用户设定的时间内未付款那么就会给用户推送一条短信提醒，并且要求苹果小米这样的大商家的订单需要优先处理，而我们后台系统使用的是用redis来存放的定时轮询，redis只能用List做一个简单的消息队列，不能实现优先级场景。

所以订单量大了后采用RabbitMQ进行改造和优化，如果发现是大客户的订单给一个相对较高的优先级，否则就是默认优先级。

队列对其进行排序。

对消息设置优先级，优先级范围是0~255，越大越优先，然后排队，优先级高的先被消费。

### 配置

在新建队列的是时候加入x-max-priority参数，设置最大优先级，范围0~255，然后就可以在发送消息的同时，携带优先级。

注意这边需要先启动生产者，生产多条消息之后再开启消费者，模仿高并发情况。

### 生产者

```java
package Nine;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import tools.RabbitMQUtils;
import java.util.HashMap;
import java.util.Scanner;

/**
 * 优先级队列生产者
 *
 * 测试用例:
 * A 1
 * B 6
 * C 4
 * D 2
 * E 5
 * F 3
 * 运行结果：
 * 消费者接收到消息B
 * 消费者接收到消息E
 * 消费者接收到消息C
 * 消费者接收到消息F
 * 消费者接收到消息D
 * 消费者接收到消息A
 */
public class Producer {
    //队列名
    public static final String FIRST_QUEUE = "first.queue";


    public static void main(String[] args) throws Exception {
        Channel channel = RabbitMQUtils.getChannel();

        HashMap<String,Object> map = new HashMap<>();
        map.put("x-max-priority",10);//设置最大优先级为10，不要过大，浪费CPU内存

        channel.queueDeclare(FIRST_QUEUE,false,false,false,map);

        Scanner sc = new Scanner(System.in);
        while(sc.hasNext()){
            String message = sc.next();
            int priority = sc.nextInt();
            //设置优先级参数
            AMQP.BasicProperties properties = new AMQP.BasicProperties().builder().priority(priority).build();
            channel.basicPublish("",FIRST_QUEUE,properties,message.getBytes("UTF-8"));
        }
    }
}
```

## 惰性队列

指消息保存在内存中还是在磁盘上，正常情况下，消息是保存在内存中的，但是在惰性队列，消息保存在磁盘中。

应用场景：消费者宕机，而MQ持续接收消息，积压了100万条，这个时候将消息转存到磁盘，而不是停留在内存。

```java
map.put("x-queue-mode","lazy");//在声明队列时加入参数
```



# 集群部分

## 克隆

对虚拟机克隆，这样就不用再新建配置了，嗨嗨嗨，三台虚拟机就够了。

## 搭建集群

启动三台虚拟机我的三台虚拟机的虚拟网络分别是192.168.204.134、133、135其中将134作为一号节点

1、修改三台机器的主机名称

vim /etc/hostname

改为node1、2、3，133是2号

重启加载名称

2、配置host文件，让各个节点能够根据域名互相访问

vim /etc/hosts

```properties
192.168.204.134 node1
192.168.204.133 node2
192.168.204.135 node3
```

3、确保各个节点的cookie文件使用的是同一个值

在主机1使用远程复制命令，将主机1的cookie文件复制到2和3

```shell
scp /var/lib/rabbitmq/.erlang.cookie root@node2:/var/lib/rabbitmq/.erlang.cookie

scp /var/lib/rabbitmq/.erlang.cookie root@node3:/var/lib/rabbitmq/.erlang.cookie
```

4、在三台虚拟机上重启RabbitMQ服务，Erlang虚拟机和RabbitMQ应用服务

rabbitmq-server -detached

5、开启node1,node2的4369和25672端口

6、在2、3号机上执行如下代码

rabbitmqctl stop_app	(rabbitmqctl stop 会将erlang虚拟机关闭，rabbitmqctl stop只关闭Rabbitmq服务)

rabbitmqctl reset

rabbitmqctl join_cluster rabbit@node1

rabbitmqctl start_app

三号节点加给二号

7、集群状态

rabbitmqctl cluster_status

8、重新注册

三台机器都需要注册一遍，命令看上面

登录就能看到集群

9、如何解除集群

rabbitmqctl stop_app

rabbitmqctl reset

rabbitmqctl start_app

rabbitmqctl forget_cluster_node rabbit@node2(node1上执行)

## 镜像队列

上述搭建的镜像不可复用，因为node1上的队列不会保存到node2，如果node1宕机，队列中的消息即丢失

可以通过rabbitmqctl stop_app让node1人工宕机

搭建镜像队列也就是备份

搭建过程

在图形化界面的admin中，点击策略policies

![捕获8](C:\Users\wxw\Desktop\学习\Note\images\捕获8.PNG)

|             |                                        |
| ----------: | -------------------------------------- |
|       Name: | 名字，随便起                           |
|    Pattern: | 规则，正则表达式，什么样的队列需要镜像 |
|   Apply to: | 应用范围                               |
|   Priority: |                                        |
| Definition: | 参数                                   |

就可以实现队列备份，其中参数中的备份数量，是算上自己的数量

如果其中一台宕机，会保持消息备份的数量，如果node1宕机，会自动多备份一份到node3

## 高可用的负载均衡

直接访问node1或者23，会发现就是只能连接其中一个节点，当节点发生宕机，就无法连接另一个，也就是无法自动变更ip的问题，可以基于nginx实现反向代理和负载均衡或者其他的负载均衡软件，还有keepalived软件保证高可用性

## FederationExchange联邦交换机

如果有一台北京的broker和一台深圳的broker，最好就是北京的用户去访问北京的，深圳的用户访问深圳的服务器。而两台服务器之间相互访问有较大延迟。就设计到了数据一致性问题，北京的数据要同步到深圳，深圳的数据同步到北京，来保证用户能访问到所有数据。

搭建步骤：

1、需要保证每台节点单独运行

2、在每台机器上开启federation相关插件

rabbitmq-plugins enable rabbitmq_federation

rabbitmq-plugins enable rabbitmq_federation_management

3、先在下游节点创建联邦队列

4、在下游节点中配置上游节点

5、添加policy策略

剩下的活请交给运维

## shovel

将源的消息持续转发给目标，就是复制转发消息的插件，用来同步数据

rabbitmq-plugins enable rabbitmq_shovel

rabbitmq-plugins enable rabbit_shovel_management



