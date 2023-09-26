# JUC并发编程

JUC是指在java.util.concurrent在	并发编程中所使用的工具包

## 第一章

### 启动线程

在java原生native方法start0()中，由底层c++语言编写的程序调用操作系统层，由操作系统层分配调度线程

### 用户线程和守护线程

用户线程：用户程序，完成对应任务后退出

守护线程：为其他线程服务的系统线程，如jvm垃圾回收线程，在没有其他线程需要服务时退出

## 第二章

### Future接口

Future接口：定义了操作异步任务并行执行的一些方法，如获取异步任务执行结果、取消任务执行、判断任务是否完成等

Future接口可以为主线程开一个分支任务，专门为主线程处理耗时耗力的复杂业务

### Future接口常用实现类FutureTask异步任务

为什么要使用FutureTask实现类

Runnable接口的run方法是没有返回参数的，而Callable接口不满足异步任务的管理

所以FutureTask同时实现了Runnable接口和Future接口，同时又可以又可以使用Callable接口的构造注入

```java
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

public class Text_01 {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        //构造有返回值的异步任务
        FutureTask<String> futureTask = new FutureTask<>(new MyThread());
		//开启异步任务的线程
        Thread t1 = new Thread(futureTask,"t1");
        t1.start();
        //获取异步任务执行结果
        System.out.println(futureTask.get());
    }
}

class MyThread implements Callable<String>{

    @Override
    public String call() throws Exception {
        System.out.println("当前运行的线程为"+Thread.currentThread());
        return "----------------hello";
    }
}

```

### Future+线程池异步多线程任务显著提升程序执行效率

```java
import java.util.concurrent.*;

public class FutureThreadPoolDemo {
    public static void main(String[] args) {
        //创建线程池
        ExecutorService threadPool = Executors.newFixedThreadPool(3);
        long startTime = System.currentTimeMillis();
        FutureTask<String> t1 = new FutureTask<String>(()->{
            try{
                TimeUnit.MILLISECONDS.sleep(300);
            }catch (InterruptedException e){
                e.printStackTrace();
            }
            return "hello";
            });
        threadPool.submit(t1);
        FutureTask<String> t2 = new FutureTask<String>(()->{
            try{
                TimeUnit.MILLISECONDS.sleep(500);
            }catch (InterruptedException e){
                e.printStackTrace();
            }
            return "hello";
        });
        threadPool.submit(t2);
        FutureTask<String> t3 = new FutureTask<String>(()->{
            try{
                TimeUnit.MILLISECONDS.sleep(500);
            }catch (InterruptedException e){
                e.printStackTrace();
            }
            return "hello";
        });
        threadPool.submit(t3);
        try {
            System.out.println(t1.get()+t2.get()+t3.get());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        long endTime = System.currentTimeMillis();
        System.out.println("运行时间为"+(endTime-startTime));
        threadPool.shutdown();
    }
}
```

### Future优缺点

缺点：

get()容易阻塞，当子线程未执行完毕，主线程调用FutureTask的get方法会导致阻塞，等待子线程执行完毕

```java
//线程执行超过指定时间，不再等待，抛出异常
t1.get(3,TimeUnit.SECONDS)
```

isDone()轮询导致CUP空置，在实际工作FutureTask的get方法非常容易阻塞，所以要对子线程执行状态进行判断

```java
while(true){
    if(t1.isDone()){
        t1.get();
    }
    else{
        try{
            System.out.println("正在等待");
            TimeUnit.MILLISECONDS.sleep(500);
        }catch (InterruptedException e){
            e.printStackTrace();
        }
    }
}
```

轮询的检查方式会导致CPU资源的浪费，而且没执行完毕还是相当于阻塞

### Future的优化思路

1、回调通知：完成了子线程告知

2、和线程池配合。快速创建异步任务

3、多个任务要按先后顺序处理，相互依赖可以组合处理

4、对计算速度选最快的，优先返回最先结束的处理结果

### CompletableFuture讲解

```java
public class CompletableFuture<T> implements Future<T>, CompletionStage<T>
```

CompletionStage代表异步计算过程中的某一个阶段，一个阶段结束后可能会触发另一个阶段

### 四大静态方法

默认线程池开启的是守护线程

```java
CompletableFuture.runAsync(()->{});//无返回值
CompletableFuture.supplyAsync();//有返回值
```

### 回调函数

```java
CompletableFuture<String> c2 = CompletableFuture.supplyAsync(()->{return "2";}).whenComplete((v,e)->{
    if(e==null){//e代表上一步是否有异常
        System.out.println(v);//v为上一步返回值
    }
}).exceptionally(e->{
    e.printStackTrace();
    return null;
});
```

| 函数式接口名称 | 方法名称 | 参数   | 返回值   |
| -------------- | -------- | ------ | -------- |
| Runnable       | run      | 无参数 | 无返回值 |
| Function       | apply    | 1      | 1        |
| Consume        | accept   | 1      | 无返回值 |
| Supplier       | get      | 无参数 | 1        |
| BiConsumer     | accept   | 2      | 无返回值 |

### get和jion区别

没有太大区别，但是jion不会抛出检查型异常，get必须做异常处理

### 具体应用

```java
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 *案例说明：电商比价需求，模拟如下情况：
 *
 * 1、需求：
 *  1.1 同一款产品。同时搜索出同款产品在各大电商平台的售价
 *  1.2、同一款产品，同时搜索出本产品在同一个电商平台下，各个入驻卖家售价是多少
 *
 * 2、输出：出来结果希望是同款产品的在不同地方的价格清单列表，返回一个List<String>
 *  《mysql》 in jd price is 88.05
 *  《mysql》 in dangdang price is 76.05
 *
 * 3、技术要求：
 *  3.1 函数式编程
 *  3.2 链式编程
 *  3.3 Stream流式计算
 */

public class CompletableFutureDemmo {
    //模拟电商
    static List<NetMall> list = Arrays.asList(
            new NetMall("jd"),
            new NetMall("dangdang"),
            new NetMall("taobao")
    );

    /**
     * step by step 一步步搜索
     * @param list
     * @param productName
     * @return
     * 所花费时间为3102ms
     *
     */
    public static List<String> getPrice(List<NetMall> list,String productName){
        //stream+lambad表达式链式流式计算
        return list.stream().map(netMall -> String.format("《"+productName+"》 in %s price is %.2f",netMall.getNetMallName(),netMall.calcPrice(productName))).collect(Collectors.toList());
    }

    /**
     * 并发编程
     * @param list
     * @param productName
     * @return
     * 所花费时间为1060
     */
    public static List<String> getPriceCompletableFuture(List<NetMall> list,String productName){
        return list.stream().map(netMall -> CompletableFuture.supplyAsync(()->String.format("《"+productName+"》 in %s price is %.2f",netMall.getNetMallName(),netMall.calcPrice(productName))))
                .collect(Collectors.toList()).stream().map(stringCompletableFuture -> stringCompletableFuture.join()).collect(Collectors.toList());
    }

    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();
        List<String> list1 = getPrice(list,"mysql");
        for(String em:list1){
            System.out.println(em);
        }
        long endTime = System.currentTimeMillis();
        System.out.println("所花费时间为"+(endTime-startTime));

        startTime = System.currentTimeMillis();
        list1 = getPriceCompletableFuture(list,"mysql");
        for(String em:list1){
            System.out.println(em);
        }
        endTime = System.currentTimeMillis();
        System.out.println("所花费时间为"+(endTime-startTime));
    }
}

//电商类
class NetMall{
    private String netMallName;

    public String getNetMallName() {
        return netMallName;
    }

    public NetMall(String netMallName) {
        this.netMallName = netMallName;
    }

    public double calcPrice(String productName){
        try{
            TimeUnit.SECONDS.sleep(1);
        }catch (Exception e){
            e.printStackTrace();
        }
        return ThreadLocalRandom.current().nextDouble()*2+productName.charAt(0);
    }
}

```

### 常用用法

```java
/*
获得结果和触发计算
*/
get()
get(long timeout,TimeUnit nuit)
join()
getNow(T valueIfAbsent)//获取执行结果，如果执行没完成，返回传入的默认值，不阻塞
boolean complete(T value)//打断get和join的获取过程，下次获取的时候没有执行完则返回输入的值，打断成功为true

/*
对计算结果进行处理
*/
thenApply()//使两个线程串行化，有异常则短路，不执行后面步骤
handle()//使两个线程串行化，有异常也可以继续执行，并且将异常传入下个步骤
    
/*
对计算结果进行消费
*/
thenAccept()//消费型函数式接口，没有返回值
    
/*
异步串行和线程池
*/
thenApplyAsync()
thenRunAsync()//可以修改线程池
    
thenAccept()
thenApply()//继承上一个线程池
    
 /*
对计算速度进行选用
*/
applyToEither()//第一个参数是CompletabeFuture对象，第二个参数是比较完后的操作，函数式接口，返回值也是CompletabeFuture
    
 /*
对计算结果进行合并
*/
thenCombine()//第一个参数是CompletabeFuture对象,第二个参数是两个输入一个返回的函数式接口
```

### 如果你线程执太快了，main会帮你做掉？

## 第三章

### 锁案例演示

一个对象里面如果有多个非静态synchronized方法，只要一个线程去调用其中的一个synchronized方法了，其他的线程就只能等待，锁的是当前对象this，被锁定后其他线程不能进入当前对象其他synchronized方法，锁为实例对象本视---this

对于静态synchronized方法，锁的是当前类，锁为类对象本视---唯一模板Class

静态同步方法和普通同步方法之间没有竞态条件

对于同步代码块，锁的是括号里的对象

其一：非静态同步方法锁对象

```java
class Phone{
    public synchronized void sendEmail(){
        System.out.println("-----sendEmail");
    }
    public synchronized void sendSMS(){
        System.out.println("-----sendSMS");
    }
}
```

其二：静态同步方法锁对类

```java
class Phone{
    public static synchronized void sendEmail(){
        System.out.println("-----sendEmail");
    }
    public static synchronized void sendSMS(){
        System.out.println("-----sendSMS");
    }
}
```

### synchronized字节码分析

javap -c ***.class 反编译class文件

```java

class Phone{
    /**
  public synchronized void sendEmail();
    descriptor: ()V
    flags: (0x0021) ACC_PUBLIC, ACC_SYNCHRONIZED                   sy    nchronized标识
    Code:
      stack=2, locals=1, args_size=1
         0: getstatic     #7                  // Field java/lang/System.out:Ljava/io/PrintStream;
         3: ldc           #13                 // String -----sendEmail
         5: invokevirtual #15                 // Method java/io/PrintStream.println:(Ljava/lang/String;)V
         8: return
      LineNumberTable:
        line 4: 0
        line 5: 8
    */
    public synchronized void sendEmail(){
        System.out.println("-----sendEmail");
    }
    
    /**
  public static synchronized void sendSMS();
    descriptor: ()V
    flags: (0x0029) ACC_PUBLIC, ACC_STATIC, ACC_SYNCHRONIZED            synchronized标识
    Code:
      stack=2, locals=0, args_size=0
         0: getstatic     #7                  // Field java/lang/System.out:Ljava/io/PrintStream;
         3: ldc           #21                 // String -----sendSMS
         5: invokevirtual #15                 // Method java/io/PrintStream.println:(Ljava/lang/String;)V
         8: return
      LineNumberTable:
        line 7: 0
        line 8: 8

    */
    public static synchronized void sendSMS(){
        System.out.println("-----sendSMS");
    }
    
    /**
      public void pr();
    Code:
       0: new           #2                  // class java/lang/Object
       3: dup
       4: invokespecial #1                  // Method java/lang/Object."<init>":()V
       7: astore_1
       8: aload_1
       9: dup
      10: astore_2
      11: monitorenter                监视器进入
      12: getstatic     #7                  // Field java/lang/System.out:Ljava/io/PrintStream;
      15: ldc           #23                 // String hhahha
      17: invokevirtual #15                 // Method java/io/PrintStream.println:(Ljava/lang/String;)V
      20: aload_2
      21: monitorexit                 监视器退出
      22: goto          30
      25: astore_3
      26: aload_2
      27: monitorexit                 监视器异常退出
      28: aload_3
      29: athrow                      异常抛出
      30: return

    */
    public void pr(){
        Object o = new Object();
        synchronized(o){
            System.out.println("hhahha");
        }
    }
}

```

### 管程monitor

对象的头文件存储了锁的相关信息

每个对象天生带着一个对象监视器

每个被锁住的对象都会和Monitor关联起来

### 公平锁和非公平锁

ReentrantLock类，默认非公平锁

```java
ReentrantLock r = new ReentrantLock(false)//默认非公平锁
ReentrantLock r2 = new ReentrantLock(true)//公平锁
```



公平锁：多个线程按照申请锁的顺序来获取锁

非公平锁：多个线程不按照申请锁的顺序来获取锁,可以充分利用时间片，减少CPU空闲时间，减少线程间的切换开销

越快越好用非公平

对性能要求不高，对同步执行有要求

### 可重入锁

同一个线程，再进入该线程的内层方法，会自动获取锁 

不会被自己阻塞

java中synchronized和ReentrantLock都是可重入锁，可以一定程度上避免死锁

### 可重入锁种类

隐式锁：synchronized关键字默认是可重入锁

显式锁：ReentrantLock

```java
/**
 * 运行结果
 * C:\Users\Public\environment\java11\bin\java.exe "-javaagent:D:\IntelliJ IDEA 2022.2.2\lib\idea_rt.jar=65057:D:\IntelliJ IDEA 2022.2.2\bin" -Dfile.encoding=UTF-8 -classpath C:\Users\wxw\Desktop\java线程练习_可删除\out\production\java线程练习_可删除 Lock8Demo
 * 外层调用
 * 内层调用
 *
 * 进程已结束,退出代码0
 */
public class Lock8Demo {
    public static void main(String[] args) {
        Object o = new Object();

        new Thread(()->{
            synchronized (o){
                System.out.println("外层调用");
                synchronized (o){
                    System.out.println("内层调用");
                }
            }
        }).start();

    }
}
```

### 原理分析

在存储锁的信息中，有记录锁的重入次数

实现机制，每个锁对象都有一个锁计数器和一个指向持有该锁的线程的指针

synchronized是java默认实现的，而ReentrantLock没有，所以使用ReentrantLock要注意锁的重入次数，重入几次释放几次

加锁一次计数器+1，计数器初始为0，重为0时线程释放资源

### 死锁

两个及以上的程序互相争夺资源造成的一种互相等待的现象。

```java
import java.util.concurrent.TimeUnit;

/**
 * 死锁
 */
public class Lock8Demo {
    public static void main(String[] args) {
        Object A = new Object();
        Object B = new Object();

        new Thread(()->{
            synchronized (A){
                System.out.println("线程1获得A资源，请求获得B资源");
                try{
                    TimeUnit.SECONDS.sleep(4);
                }
                catch (Exception e){
                    e.printStackTrace();
                }
                synchronized (B){
                    System.out.println("线程1获得B资源");
                }
            }
        }).start();

        new Thread(()->{
            synchronized (B){
                System.out.println("线程2获得B资源，请求获得A资源");
                try{
                    TimeUnit.SECONDS.sleep(4);
                }
                catch (Exception e){
                    e.printStackTrace();
                }
                synchronized (A){
                    System.out.println("线程2获得A资源");
                }
            }
        }).start();
    }
}

```

### java排查死锁

命令行

jps -l //查看线程

jstak 线程号 //排查死锁，显示死锁信息

### 中断机制和终端协商机制简介

一个线程不应该由其他线程来中断或停止，而应该由线程自己停止

没有办法立即停止一个线程，而停止一个线程却是非常重要的操作。

java提供了一种用于停止线程的协商机制——中断，即中断标识协商机制。

手动调用线程的interrupt方法，将线程对象中的中断标识符设为true

需要自己编写代码，不断检测中断标识符，如果为true，标识别的线程请求这条线程中断

此时究竟该做什么由你自己实现

### 中断机制三大中断方法

java.lang.Thread

```java
void interrupt()//将一个线程的中断标识符设置为true

static boolen interrupted()//判断线程是否中断并且清除当前中断状态，会将中断标志位设置为false，两个操作

boolen isInterrupted()//判断线程是否中断 
```

演示：

如何中断运行中的线程？

```java
/**
 * 通过volatile实现线程中断
 */
public class Lock8Demo {
    static volatile boolean isStop = false;
    public static void main(String[] args) {
        new Thread(()->{
            while (true){
                if(isStop){
                    System.out.println("程序1中断");
                    break;
                }
                System.out.println("------hello");
            }
        }).start();

        new Thread(()->{
            try{
                TimeUnit.SECONDS.sleep(3);
            }
            catch (Exception e){
                e.printStackTrace();
            }
            isStop = true;
        }).start();

    }
}
```

```java
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 通过AtomicBoolean实现线程中断
 */
public class Lock8Demo {

    static AtomicBoolean atomicBoolean = new AtomicBoolean(false);

    public static void main(String[] args) {
        new Thread(()->{
            while (true){
                if(atomicBoolean.get()){
                    System.out.println("程序1中断");
                    break;
                }
                System.out.println("------hello");
            }
        }).start();

        new Thread(()->{
            try{
                TimeUnit.SECONDS.sleep(3);
            }
            catch (Exception e){
                e.printStackTrace();
            }
            atomicBoolean.set(true);
        }).start();

    }
}

```

```java
import java.util.concurrent.TimeUnit;

/**
 * 通过interrupt实现线程中断
 */
public class Lock8Demo {

    public static void main(String[] args) {
        Thread t1 = new Thread(()->{
            while (true){
                if(Thread.currentThread().isInterrupted()){
                    System.out.println("程序1中断");
                    break;
                }
                System.out.println("------hello");
            }
        });
        t1.start();

        Thread t2 = new Thread(()->{
            try{
                TimeUnit.SECONDS.sleep(3);
            }
            catch (Exception e){
                e.printStackTrace();
            }
            t1.interrupt();
        });

        t2.start();

    }
}

```

其中，中断标志为为true不会立即停止线程

而如果一个线程处于被阻塞状态，在别的线程中调用当前线程对象的interrupt方法，线程会立即退出被阻塞状态，并抛出异常

```java
import java.util.concurrent.TimeUnit;

/**
 * 通过interrupt中断被阻塞的程序
 * 抛出异常java.lang.InterruptedException: sleep interrupted
 */
public class Lock8Demo {

    public static void main(String[] args) {
        Thread t1 = new Thread(()->{
            try{
                TimeUnit.SECONDS.sleep(2);
            }
            catch (Exception e){
                e.printStackTrace();
            }
            while (true){
                if(Thread.currentThread().isInterrupted()){
                    System.out.println("程序1中断");
                    break;
                }
            }
        });
        t1.start();

        Thread t2 = new Thread(()->{
            try{
                TimeUnit.SECONDS.sleep(1);
            }
            catch (Exception e){
                e.printStackTrace();
            }
            t1.interrupt();
        });

        t2.start();

    }
}
//危险的是，程序并不会退出，因为标志位会被清空，所以程序会被一直运行，解决方案，在抛出异常时做异常处理，设置异常标志位
```

### LockSupport类和线程等待唤醒机制

LockSupport类用于创建锁和其他同步类的基本线程阻塞原语

```java
Object //中的wait() notify()方法可以让线程阻塞或唤醒
    使用wait方法和notify方法必须要在锁块内
JUC包中Condition的await() signal()方法可以让线程阻塞或唤醒
    使用await方法和signal方法也必须要在锁块内
LockSupport也可以阻塞和唤醒指定线程
    使用许可证与线程关联
    park方法检查许可证，没有就阻塞
    unpark方法会将指定线程的许可证发放，会自动唤醒park线程，而之前阻塞中的park方法将会执行结束并返回
    错误的先唤醒再阻塞，是不会报错的，因为通行证已经发放
```

这是一个技术更新过程

## 第四章

### java内存模型JMM

jvm规范中，试图定义一种java内存模型JMM来屏蔽掉各种硬件和操作系统的内存访问差异，让java程序在各个平台下都能达到一致的内存访问效果

JMM本身是一种抽象的并不存在的概念，他描述的是一组规范或约定，关键技术点都是围绕多线程的原子性、可见性和有序性展开的

### JMM三大特性

可见性

当一个线程修改了某一个共享变量的值，其他线程是否能够立即知道该变更

跟git类似，本地工作内存中存在共享变量的副本，在本地修改后提交主内存



原子性

跟事务原子性类似，防止线程脏读



有序性

指令重排序

编译器和处理器为了提升性能通常会对指令序列进行重新排序，如果是串行执行，会保证执行结果一致，但是多线程情况下会导致语义变化产生的脏读	

有时为了业务要求，会关闭指令重排序

### 多线程先行发生原则happens-before

在JMM中，如果一个操作的执行结果需要对另一个操作可见或者代码重排序，那么这两个操作直接必须存在happens-before原则，逻辑上的先后关系

### happens-before总原则

如果一个操作happens-before另一个操作，那么第一个操作的执行结果将对第二个操作可见，而且第一个操作的执行顺序排在第二个操作之前

两个操作之间的happens-before，并不意味着一定要按照happens-before原则指定的顺序来执行。如果重排序之后的执行结果一致，那么这种重排序并不非法

### 8条happens-before

1、次序规则

一个线程内，按照代码顺序，写在前面的操作先行发生于写在后面的操作

2、锁定规则

一个unLock先行发生于对同一个锁的lock操作

3、volatile变量规则

对一个volatile变量的写操作。先行发生于对这个变量的读操作

4、传递规则

A先行发生于B，B先行C，则A先行C

5、线程启动规则

Thread对象的start方法先行发生于此线程的每一个动作

6、线程中断规则

对线程interrupt方法的调用，先行发生于被中断线程的代码检测到中断事件的发生

可以通过Thread.interrupted()检测到是否发生中断

7、线程终止规则

线程中的所有操作都先行发生于对此线程的终止检测，可以通过isAlive方法检测线程是否终止运行

8、对象终结规则

一个对象的初始化完成，先行发生于它的finalize方法的开始

### volatile两大特性

可见性

有序性：可重排序



语义：直接写，直接读，不经过工作内存副本变量，直接更新和读取主内存

### 内存屏障

是一类同步屏障指令，是编译器或CPU在对内存随机访问过程中的一个同步点，使得此点之前的读写操作都完成后才可以开始执行之后的操作，是一种JVM指令，但volatile无法保障原子性

读屏障：所有缓冲数据失效，重新回到主内存读取数据

写屏障：强制将缓冲区的数据写入主内存

### 日常使用

1、单一赋值运算，i++这样的复合运算不能使用，会破坏原子性

2、状态标志，判断业务是否结束

3、开销低的读，写锁策略

4、DCL双端锁的发布

## 第五章

### CAS

compare and swap的缩写，比较与交换，包含内存位置，预期原值和更新值

juc包下的autmic原子类采用的是类似乐观锁的设计，使用版本控制，是通过硬件保证且效率更高，非阻塞且具有原子性，内部其实也会给总线加锁，但是排他的时间要短很多，会比synchronized好很多

比较当前值与旧值，如果相同则更新值，否则自旋

### Unsafe类

原子类内部使用的核心类，由于java无法直接访问操作系统底层，需要通过native方法访问，基于Unsafe类可以直接操作特定内存的数据，Unsafe在sun.misc包中，提供可以像C指针一样的直接操作内存，所以CAS依赖于Unsafe类

```java
atomicInteger.getAndIncrement()
```

原子类自增是原子的，不成功就会自旋

### 自定义原子类AtomicReference<V>

### 手写自旋锁

```java
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public class Main {
    public static void main(String[] args) {
        SpinLock lock = new SpinLock();
        Thread a = new Thread(()->{
            lock.lock();
            try{
                TimeUnit.SECONDS.sleep(5);
            }catch (Exception e){
                e.printStackTrace();
            }
            lock.unlock();
        },"a");
        Thread b = new Thread(()->{
           lock.lock();
           lock.unlock();
        },"b");

        a.start();
        try{
            TimeUnit.MICROSECONDS.sleep(500);
        }catch (Exception e){
            e.printStackTrace();
        }
        b.start();
    }
}

/**
 * 题目：实现一个自旋锁
 * 自旋锁好处，循环比较没有wait阻塞
 * 通过CAS操作来实现自旋，A线程先进来调用myLock方法自己持有锁五秒钟，B线程进来后发现当前线程持有
 * 锁，所以只能自旋等待，直到A线程释放锁
 */
class SpinLock{
    AtomicReference<Thread> atomicReference = new AtomicReference<>();
    public void lock(){
        while(!atomicReference.compareAndSet(null,Thread.currentThread())){
            System.out.println(Thread.currentThread().getName()+"尝试获取锁");
        }
        System.out.println(Thread.currentThread().getName()+"获取锁");
    }
    public void unlock() {
        atomicReference.compareAndSet(Thread.currentThread(),null);
        System.out.println(Thread.currentThread().getName()+"解锁 ");
    }

}
```

### CAS两大缺点

1、循环时间长开销大

如果CAS失败，长时间没获得资源，会给CPU带来很大开销

2、引出ABA问题

就是光根据值来判断版本是不严谨的，如果值是ABA型，中间过程是不严谨的

### 版本控制解决ABA问题

使用AtomicStampedReference，带流水版本号的自定义原子类

版本号不同将会作废

## 第六章

### 原子操作类

#### 线程计数器CountDownLatch

```java
用来检测线程是否都结束了
CountDownLatch c = new COuntDownLatch(size);
Thread t = new Thread(()->{
    c.countDown();
});
c.await();//主线程等待，直到计数器为0
...
```

#### 基本类型原子类

AtomicInteger

AtomicBoolean

AtomicLong

#### 数组类型原子类

AtomicIntegerArray

AtomicLongArray

AtomicReferenceArray

#### 引用类型原子类

AtomicRefence

AtomicStampedReference//记录版本号

AtomicMarkableReference//记录是否修改过

#### 对象的属性修改原子类

AtomicIntegerFieldUpdater//原子更新对象中int类型字段值

基于反射实现的，可以对指定的volatile int字段原子更新

AtomicLongFieldUpdater//同上

AtomicReferenceFieldUpdater

Updater都是通过静态方法newUpdater实现

```java
AtomicIntegerFieldUpdater up = AtomicIntegerFieldUpdater.newUpdater();
```

#### 原子操作增强类

DoubleAccumulator

DoubleAdder

LongAccumulator，提供一个带两个参数的函数式接口来选择运算方式，并提供设置初值

LongAdder只能用来计算加法并且从0开始

如果是jdk8，使用LongAdder比AtomicLong性能更好

#### LongAdder源码分析

分散热点

内部用base保存值，但是并发数量过多时，通过扩容Cell[]分散热点。相当于有三个值同时统计并发的结果，最终求和

## 第七章

### ThreadLocal

线程局部变量：每个线程都有自己所属的本地变量副本

尽量用完即回收，不然会影响线程复用

### 源码分析

ThreadLocalMap是ThreadLocal的内部静态类，保存了所有线程的kv对，k是ThreadLocal，v是值

### ThreadLocalMap默认弱引用

什么是内存泄漏？

不再会被使用的对象或者变量占用的内存不能被回收

### 强引用

对于强引用的对象，就算出现了OOM也不会对该对象回收，容易造成内存泄漏

```java
public class Main {
    public static void main(String[] args) {
        MyObject m = new MyObject();
        System.out.println(m);

        m=null;
        System.gc();//人工开启垃圾回收  
    }
}

class MyObject{
    protected void finalize() throws Throwable{
        System.out.println("我被回收啦");
    }
}
//因为已经不指向对象，所以被回收
```

### 软引用

相对强引用弱化一些

系统内存充足的 时候 不会回收

系统内存不足时会回收

```java
import java.lang.ref.SoftReference;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public class Main {
    public static void main(String[] args) {
        SoftReference<MyObject> m = new SoftReference<>(new MyObject());
        System.out.println(m);
        System.gc();
    }
}

class MyObject{
    protected void finalize() throws Throwable{
        System.out.println("我被回收啦");
    }
}

//因为内存充足，没有被回收
```

### 弱引用

只要垃圾回收机制一运行，不论是否被指向，不论内存空间是否足够，都会被回收

### 虚引用

需要和引用队列一同使用，用来处理回收的监听

虚引用的返回对象是null，没法对对象操作，只起监听作用

### 为何ThreadLocal要用弱引用

 因为ThreadLocal基本思创建的是强引用，如果使用结束被回收，但是entity里的ThreadLocal是强引用，那么entity的kv对就不会被回收，造成内存泄漏

所以key，也就是ThreadLocal被设置为弱引用，当ThreadLocal对象被回收，Map里的key也会被回收，就会出现k为null的v

所以要用remove来删除它

## 第八章

### java对象内存布局和对象头

必要知识：java中new出的对象，生成在堆，引用在栈里面。

对象在堆内部的构成元素是：对象头，实例数据，对齐填充

对象头包括：对象标记，类元信息，

### 对象头的对象标记MarkWord

包含了：哈希编码，GC标记，GC次数，同步锁标记，偏向锁持有者

64位系统中，markword占8字节，类型指针占8字节，一共是16字节

### 对象头的类元信息

对象头的类型指针，指向方法区的对应类的类元信息Klass

### 实例数据

存放类的属性数据信息，包括父类的属性信息

### 对齐填充

对象起始地址必须是8字节的整数倍，为了字节对齐加入填充![捕获](C:\Users\wxw\Desktop\学习\Note\images\捕获.PNG)

由于默认开启压缩指针，所以类元信息的指针可能不够8字节，但是会因为对齐，默认还是16字节

## 第九章

### Synchronized与锁升级

无锁->偏向锁->轻量锁->重量锁

Synchronized是重量级锁，偏向底层，需要在内核态和用户态之间切换，系统资源消耗过大，所以引入了轻量级锁和偏向锁

![捕获2](C:\Users\wxw\Desktop\学习\Note\images\捕获2.PNG)

偏向锁：MarkWord存储的是偏向的线程id

轻量锁：MarkWord存储的是指向线程栈中锁记录LockRecord的指针

重量锁：MarkWord存储的是指向堆中的monitor对象的指针

### 偏向锁：单线程竞争

当线程A第一次竞争到锁时，通过修改MarkWord中的偏向线程ID、偏向模式。

如果不存在其他线程竞争，那么持有锁的线程将永远不需要进行同步

也就是在并发量小的时候，一段同步代码一直被同一个线程所访问，那么在后续访问的时候会一直获得锁，减少用户态和内核态的切换

也就是说偏向锁会偏向于第一个访问锁的线程，也即偏向锁在资源没有竞争的情况下消除了同步语句，连CAS都懒得做了，直接提升程序性能。

```java
java -XX:+PrintFlagsInitial |grep BiasedLocak* //查看配置
-XX:+UseBiaseLocking -XX:BiasedLockingStartupDelay=0 //开启偏向锁并将启动延时设置为0
-XX:-UseBiasedLocaking//关闭偏向锁，关闭后默认直接进入轻量锁
```

实际上在jdk1.6后默认开启偏向锁，但是启动有延时	

当有另外的线程来争抢偏向锁时，就不能再使用偏向锁了，要升级为轻量锁，竞争线程尝试CAS更新对象头失败，会等待到全局安全点撤销偏向锁

1、如果一个线程正在执行同步块，还没有执行完，其他线程来抢夺，偏向锁会被取消掉并出现锁升级，此时轻量锁由原先持有偏向锁的线程持有，竞争的线程会进入自旋等待

2、第一个线程完成同步块，则将对象头设置成无锁状态并撤销偏向锁，重新偏向

偏向锁在java15后废除

### 轻量锁：CAS

存在多线程竞争，但是任意时刻最多只存在一个线程竞争，即不存在竞争太过激烈的情况，也就没有线程阻塞

通过cas自旋减少使用操作系统互斥量产生的性能消耗

升级时机，当关闭偏向锁功能或多线程竞争偏向锁会使偏向锁升级成为轻量级锁

```java
-XX:-UseBiasedLocking//关闭偏向锁可以直接进入轻量锁
```

当cas操作到一定次数没有成功时，将会升级为重量级锁

在java6之前，默认最大自旋次数10次，或自旋线程数超过cpu核速一半用-XX:PreBlockSpin=10来修改

java6之后，自适应自旋锁，如果该次自旋锁获取成功了，下次最大自旋次数就会增大，反之则反

### 重量级锁：monitor

如果是轻量级锁，jvm会在当前线程的栈帧中创建一个锁记录空间用来存访MarkWord的拷贝

重量级锁所指向的ObjectMonitor类里有字段可以记录非加锁状态，下的MarkWord，其中自然可以存储原来的hashcode

1、如果一个已经调用过hashcode的类要求加偏向锁是不允许的，将会跳过偏向锁直接进入轻量锁

2、如果一个偏向锁要求调用hashcode，将会膨胀为重量级锁

## 第十章

### AQS-AbstractQueuedSynchronizer

抽象的队列同步器，是用来实现锁或者其他同步器组件的公共基础部分的抽象实现。是重量级基础框架及整个JUC体系的基石，主要用于解决锁分配给谁的问题，依赖FIFO先进先出的等待队列，和一个原子int保存锁状态

锁，是面向锁的使用者

同步器，是面向锁的设计者

将要请求共享资源的线程及自身的等待状态封装成队列的节点对象(Node)，通过CAS、自旋以及LockSupport.park()的方式，维护state变量状态，使并发达到同步的效果 

### 源码分析

在AQS中有内部静态类Node

在AQS中有volatile原子int类state，来表示锁状态

而Node中也有一个waitstate表示状态

通过内置CLH队列完成资源排队

通过CAS完成对state的修改

```java
static final class Node {

    static final Node SHARED = new Node();//表示线程正在以共享的方式等待锁

    static final Node EXCLUSIVE = null;//表示线程正在以独占的方式等待锁


    static final int CANCELLED =  1;//线程被取消

    static final int SIGNAL    = -1;//后继线程需要唤醒

    static final int CONDITION = -2;//等待condition唤醒

    static final int PROPAGATE = -3;//共享式同步状态将会无条件传播下去


    volatile int waitStatus;//初始为0，状态为上面的那几种

    volatile Node prev;//前置节点

    volatile Node next;//后继节点

    volatile Thread thread;


    Node nextWaiter;


    final boolean isShared() {
        return nextWaiter == SHARED;
    }


    final Node predecessor() {
        Node p = prev;
        if (p == null)
            throw new NullPointerException();
        else
            return p;
    }

    Node() {}

    /** Constructor used by addWaiter. */
    Node(Node nextWaiter) {
        this.nextWaiter = nextWaiter;
        THREAD.set(this, Thread.currentThread());
    }

    /** Constructor used by addConditionWaiter. */
    Node(int waitStatus) {
        WAITSTATUS.set(this, waitStatus);
        THREAD.set(this, Thread.currentThread());
    }

    /** CASes waitStatus field. */
    final boolean compareAndSetWaitStatus(int expect, int update) {
        return WAITSTATUS.compareAndSet(this, expect, update);
    }

    /** CASes next field. */
    final boolean compareAndSetNext(Node expect, Node update) {
        return NEXT.compareAndSet(this, expect, update);
    }

    final void setPrevRelaxed(Node p) {
        PREV.set(this, p);
    }

    // VarHandle mechanics
    private static final VarHandle NEXT;
    private static final VarHandle PREV;
    private static final VarHandle THREAD;
    private static final VarHandle WAITSTATUS;
    static {
        try {
            MethodHandles.Lookup l = MethodHandles.lookup();
            NEXT = l.findVarHandle(Node.class, "next", Node.class);
            PREV = l.findVarHandle(Node.class, "prev", Node.class);
            THREAD = l.findVarHandle(Node.class, "thread", Thread.class);
            WAITSTATUS = l.findVarHandle(Node.class, "waitStatus", int.class);
        } catch (ReflectiveOperationException e) {
            throw new ExceptionInInitializerError(e);
        }
    }
}
```

### 从ReentrantLock解读源码

```java
    public ReentrantLock() {
        sync = new NonfairSync();//默认构造非公平锁
    }


    public ReentrantLock(boolean fair) {
        sync = fair ? new FairSync() : new NonfairSync();//如果是true创建公平锁
    }

    public void lock() {
        sync.acquire(1);//lock方法实际是操作sync
    }
	
	//这是AQS中的方法
    public final void acquire(int arg) {
        //第一部抢锁，第二部加入等待队列，第三步尝试抢锁
        if (!tryAcquire(arg) &&//tryAcquire试图抢锁，如果抢占成功返回true，取反为false，短路结束
            acquireQueued(addWaiter(Node.EXCLUSIVE), arg))//addWaiter加入排队，独占锁模式，acquireQueued
            selfInterrupt();
    }

	//Syns类实现了AQS
    abstract static class Sync extends AbstractQueuedSynchronizer {
        private static final long serialVersionUID = -5179523762034025860L;
 
        //非公平下试图抢锁
        @ReservedStackAccess
        final boolean nonfairTryAcquire(int acquires) {
            final Thread current = Thread.currentThread();
            int c = getState();
            if (c == 0) {
                if (compareAndSetState(0, acquires)) {//CAS尝试设置标志位为1
                    setExclusiveOwnerThread(current);//设置当前持有锁的线程
                    return true;
                }
            }
            else if (current == getExclusiveOwnerThread()) {
                int nextc = c + acquires;
                if (nextc < 0) // overflow
                    throw new Error("Maximum lock count exceeded");
                setState(nextc);
                return true;
            }
            return false;
        }
        }
    }

	//公平锁
   static final class FairSync extends Sync {
        private static final long serialVersionUID = -3000897897090466540L;
        /**
         * Fair version of tryAcquire.  Don't grant access unless
         * recursive call or no waiters or is first.
         */
        @ReservedStackAccess
        protected final boolean tryAcquire(int acquires) {
            final Thread current = Thread.currentThread();
            int c = getState();
            if (c == 0) {
                if (!hasQueuedPredecessors() &&
                    compareAndSetState(0, acquires)) {//唯一的差异就是多了一个hasQueuedPredecessors判断是否需要排队
                    setExclusiveOwnerThread(current);
                    return true;
                }
            }
            else if (current == getExclusiveOwnerThread()) {
                int nextc = c + acquires;
                if (nextc < 0)
                    throw new Error("Maximum lock count exceeded");
                setState(nextc);
                return true;
            }
            return false;
        }
    }

//公平锁讲究先来后到，线程在获取锁时，如果这个锁的等待队列中已经有线程在等待，那么线程会加入到等待队列中
//非公平锁不管是否有等待队列，如果可以获取锁则立刻占有锁对象，
```

## 第十二章

无锁->独占锁->读写锁->邮戳锁

### 读写锁发展历程

被定义为：一个资源能够被多个读线程访问，或被一个写线程访问，但是不能同时存在读写线程

为了解决，重量级锁将所有操作都互斥访问，产生了读写锁ReadWriteLock接口，实现类ReentrantReadWriteLock

缺点：

1、写锁饥饿问题：读操作过多的时候，由于读写操作互斥，会导致写操作一致无法抢占锁

2、锁降级问题

优点：读读可以共享

由读写锁的缺点，又产生了更高级的邮戳锁StampedLock

### 读写锁的锁降级

遵循获取写锁，获取读锁再释放写锁的次序，写锁能够降级成为读锁，不支持锁升级

也就是如果一个线程占有了写锁，在不释放写锁的情况下，它还能占有读锁，即写锁降级为读锁

总结：一个线程自己持有写锁时再去拿读锁，本质相当于重入，因为该线程独占

### 邮戳(版本)(票据)锁StampedLock

是java8中新增的读写锁

是对java5中的读写锁ReentrantReadWriteLock的优化

stamp（戳记）代表锁状态，当stamp返回0表示线程获取锁失败，并且，当释放锁或者转换锁的时候，都要传入最初获取的stamp值。

是由解决锁饥饿问题引出的

一般的读写锁，在读的时候，其他线程在尝试获取写锁的时候会被阻塞。但是StampedLock在采取乐观锁后，其他线程尝试获取写锁的时候不会被阻塞，这其实是对读锁的优化，所以还需要对结果进行检验与cas类似

所有获取锁的方法，都需要返回一个邮戳，为0表示获取失败，其余表示获取成功

所有释放锁的方法，都需要一个邮戳，这个邮戳必须是和成功获取锁得到的邮戳一致

邮戳锁不可重入，危险的是如果一个线程已经持有写锁，再去获取写锁就会死锁

三种模式：1、读悲观模式

2、写模式

3、乐观读模式：很乐观认为读时无人修改，在被修改的时候再升级为悲观读模式

```java
stampedLock.tryOpetimistcRead();//乐观读,返回一个戳记
stampedLock.validate(stampe);//检查是否被写过

```



缺点：1、不支持重入，没有Re开头2、悲观读写锁都不支持条件变量3、不能调用中断方法



# 完结