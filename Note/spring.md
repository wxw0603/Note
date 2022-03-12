# Spring5

Spring是一个轻量级的控制反转IOC和面向切面编程AOP的javaEE框架

## 创建Spring项目

网址

```http
https://repo.spring.io/release/org/springframework/spring/
```

除了这些jar包以外还需要commons-logging的日志jar包不然无法运行

## IOC底层原理

### 控制反转--设计原则

1、控制反转：把对象创建和对象之间的调用过程交给Spring进行管理

2、目的是降低代码间耦合度

### 底层原理

xml解析、工厂模式、反射

#### IOC解耦过程

读取xml文件，寻找到要创建的类

创建工厂类获取类名，用反射包中的类加载器创建类

#### IOC接口

##### 1、容器本质上就是对象工厂，IOC容器底层就是对象工厂

##### 2、Spring提供IOC实现的两种方式(BeanFactoy和ApplicationContext)

BeanFactoy：IOC容器基本实现，Spring内部的使用接口，不提供给开发人员

只有在获取bean的时候才创建对象，读取xml时不创建

ApplicationContext：BeanFactoy接口的子接口，提供了更多功能，面向开发人员的

加载配置文件的时候对象就已经创建

##### 3、主要实现类

1、FileSystemXmlApplicationContext

2、ClassPathXmlApplicationContext

//区别只有输入的绝对路径和相对路径

### 源码分析

#### BeanFactory接口

主要承诺了获取Bean的多个重载方法，是否包含Bean的方法，等等

##### HierarchicalBeanFactory接口

定义了层次获取Bean层次的方法

##### ListableBeanFactory接口



## AOP底层原理

### 面向切面编程

将功能封装成模块配置到

