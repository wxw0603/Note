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

## Bean管理

### xml方式

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd">
	<!-- class:全类名  id:别名 -->
    <bean class="User" id="user" name = "haha"  ></bean>
    <!-- 创建对象默认使用无参构造方法-->
    <!-- set方法注入属性-->
    <bean id = "user" class="User">
        
    	<property name = "name" value = "张三"></property>     
        
    	<property name = "age" value = "100"></property>   
        
        <!-- 向属性中注入空值 -->
        <property name = "test1">
        	<null/>
        </property>
        
        <!-- 向属性中注入特殊值 -->
        <property name = "test1">
            <value><![CDATA[具体值]]></value>
        </property>
        
        <!-- 注入外部bean，其中ref需要与注入的bean的id相同 -->
    	<property name = "boss" ref="id"></property> 
        
        
        <!-- 注入内部bean -->
        <property name = "test2">
            <bean id = "dept" class = "...">
                <property name = "..." value = "..."></property>
            </bean>
        </property>
        
        
        <!-- 级联赋值 -->
        <property name = "boss" ref="dept"> </property>
        <property name = "dept.name" value="..."></property>
        
        <!-- 注入数组 -->
        <property name="a">
            <array>
                <value>haha</value>
                <value>ha3</value>
                <value>ha2</value>
            </array>
        </property>
        <!-- 注入List -->
         <property name="a">
            <list>
                <value>haha</value>
                <value>ha3</value>
                <value>ha2</value>
        		<!-- 在List中注入bean -->
          		<ref bean=""></ref>
            </list>
        </property>
        <!-- 注入Map -->
        <property name="a">
            <map>
                <entry key = "" value = ""></value>
            </map>
        </property>        
        <!-- 注入Set -->
        <property name="a">
            <set>
                <value>haha</value>
                <value>ha3</value>
                <value>ha2</value>
            </set>
        </property>        
        
    </bean>
    <!-- 有参构造注入属性-->
	<bean id = "user" calss="User" >
    	 <constructor-arg name = "name" value="张三"></constructor-arg>
        <!-- 可以使用索引注入 -->
    	 <constructor-arg name = "1" value="100"></constructor-arg>
    </bean>
</beans>
```

### 注解方式

1、引入aop包

2、开启组件扫描

创建配置类

```java
@Configuration
@ComponentScan(basePackage={"com.haha"})
public class Config(){
    
}
```



3、添加注解

```java
@Service
@Commopment
@Repository
@Controller
@Autowired类型自动
@Qualifier属性名
@Resource类型和名称都可
@Value普通类型
```



## Bean作用域和生命周期

### 作用域：

scope属性，设置bean是单例还是多例

### 生命周期：

(1)通过构造器创建bean实例

(2)注入属性

(3)调用bean初始化init方法

(4)容器关闭时销毁

## 自动装配

```xml
<bean id="" class="" autowire="byName"></bean>
<!-- byName根据属性名称注入,注入的bean的id值和类属性名称一样 byType根据属性类型注入 -->
```





## AOP底层原理

### 面向切面编程

### 使用方法

1、连接点

可以被增强的方法

2、切入点

实际被增强的方法

3、通知(增强)

实际增强的逻辑部分

分为

前置通知：前

后置通知：后

环绕通知：前后

异常通知：异常

最终通知：异常的finally



4、切面

将通知应用到切入点过程



一般基于AspectJ实现AOP操作

5、导入AOP和AspectJ.jar包

6、切入点表达式

作用：知道对哪个类中的哪个方法进行增强

结构：execution([权限修饰符] [返回类型] [全类名] [方法名称] ([参数列表]) )

```java
execution(*add(..))
execution(* com.Dao.*(..))//对类中所有方法增强
execution(* com.*.*(..))//对所有类里的所有方法增强
```

7、创建增强类

在增强类里面创建方法，让不同方法代表不同通知

在增强类上，添加AspectJ注解，在内部为各个方法添加相应注解，并应用切入点表达式

8、开启注解扫描并开启生成代理对象

9、抽取相同切入点

将公共切入点写在一个方法上，其他方法注解里面调用方法

10、有多个增强类增强同一个方法使用@Order(数字)调整优先级

