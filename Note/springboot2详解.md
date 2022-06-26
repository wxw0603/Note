# 基础

## 创建

### IDEA创建(需要联网)

GAV坐标

G(group)

A(artifact)

V(version)

注意删一点packgae

### 官网创建

https://start.springboot.io

Spring initializr

### 阿里云版

IDEA原本就是从springboot官网下载项目

可以将IDEA网址换为http://start.aliyun.com

### 手工导入

在断网情况下如果曾经创建过springboot项目，可以手写配置文件和启动程序

## springboot起步依赖

- parent

  继承maven的parent文件统一版本管理

  再也不用调包了嗨嗨

- starter

  根据技术来定义需要的依赖，减少依赖配置

- 引导类

- 内嵌tomcat



## REST风格

隐藏资源的访问行为，无法通过地址得知对资源是何种操作

- http://localhost/user GET(查询所有)
- http://localhost/user/1 GET(查询单个)
- http://localhost/user POST(新增/保存)
- http://localhost/user PUT(修改/更新)
- http://localhost/user/1  DELETE(删除)

### RESTFUL开发

```java
//如果要想获取到/user/1   路径这个数据1，使用注解
@ResquestMapping(value="/user/{id}",method = RequestMethod.DELETE)
public String delete(@PathVariable int id){
    
}

@RequestBody接受json数据
@RequestParam接受url地址传参或表单传参
@PathVariable接受路径参数
```

## 基础配置

application.properties配置文件

### 常用配置

可以到springboot官网查询配置内容

springboot的配置是基于环境和技术的

```properties
#服务器端口配置
servet.port=80
#修改banner
spring.main.banner-mode=off
spring.banner.image.location=test.png
#日志
logging.level.root=debug
```

### 三种文件类型

yml(推荐)、yaml、properties

优先级：properties>yml>yaml

### yaml数据格式

大小写敏感

属性层级关系使用多行描述，每行结尾冒号结束
使用缩进表示层级，同层级左侧对其，只允许使用空格

属性值前添加空格

#为注释

```yaml
#正常
user: wxw
password: 123456

user:
 name: wxw
 password: 123456

#数组
likes:
 - heihei
 - haha
 - hehe

like2: [heihei,haha,hehe]


user:
 - name: hh
   age: 12
 - name: aa
   age: 11

user: [{name:hh,age:12},{name:aa,age:11}]

```

### ymal数据读取

###### 读取单一数据

```yaml
hello: "欢迎!"

user:
  name: wxw

a:
  - "哈哈"
  - "你好"
  - "嘿嘿"
```

```java
@RestController
public class HellowController {

    @Value("${hello}")
    private String haha;
    @Value("${user.name}")
    private String heihei;
    @Value("${a[1]}")
    private String haihai;

    @RequestMapping("/*")
    public String hellow(){
        return haha+heihei+haihai;
    }

}
```

###### 变量引用

```yaml
#使用￥{属性名}引用数据
baseURL: c:\windows

curentURL: ${baseURL}\bin

```

###### 读取全部属性

使用容器中的Enviroment对象读取

```java
    @Autowired  
    private Environment env;

    @RequestMapping("/*")
    public String hellow(){

        System.out.println(env.getProperty("a[0]"));

        return haha+heihei+haihai;
    }
```

###### yaml实现类型数据封装

```yaml
user1:
  name: wxw
  password: 123456
  age: 3
```



```java
//加入容器
@Component
//加载配置文件数据
@ConfigurationProperties( prefix = "user1")
public class User {
    private String name;
    private String password;
    private int age;
    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", password='" + password + '\'' +
                ", age=" + age +
                '}';
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public int getAge() {
        return age;
    }
    public void setAge(int age) {
        this.age = age;
    }
}
```

## 整合第三方技术

### 整合JUnit

springboot默认整合了JUnit

测试类使用@SpringBootTest注解修饰

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
</dependency>
```

使用test文件夹下的测试类进行测试

```java
package com.project_01.wxw_spring_test;

import com.project_01.wxw_spring_test.Entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class WxwSpringTestApplicationTests {
    //1、注入要测试的对象
    @Autowired
    private User user;
    //2、执行要的方法
    @Test
    void contextLoads() {
        System.out.println(user.getName());
    }

}
```

###### classes属性

测试类如果在引导类的对应包下则没有问题，如果不在对应包下则报错，原因是出在与JUnit的整合中

要想不在对应包中运行需要

```java
@SpringBootTest(classes = WxwSpringTestApplication.class)
@ContextConfigration(classes = WxwSpringTestApplication.class)
```

出错原因：找不到引导类

解决方法：显示的写出引导类

原理：SpringTest整合JUnit测试是需要获取到配置文件或配置类中的容器中对象的，默认会去寻找当前包下的带有SpringBootConfigration注解的类，正常情况下能够找到。

### 整合Mybatis

1、导入相关starter和mysql驱动

```xml
<dependency>
    <groupId>org.mybatis.spring.boot</groupId>
    <artifactId>mybatis-spring-boot-starter</artifactId>
    <version>2.2.0</version>
</dependency>

<dependency>
    <groupId>mysql</groupId>
    <artifactId>mysql-connector-java</artifactId>
</dependency>
```



2、配置相关信息

```yaml
#mybatis相关信息
spring:
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3306/watersystem
    username: root
    password: wxw0603
```

3、创建Dao层

```java
@Mapper
public interface UserDao {

    @Select("select * from user where id = #{id}")
    public User getUserById(int id);

}
```

### 整合Mybatis-Plus

1、导入相关jar包

```xml
<!-- https://mvnrepository.com/artifact/com.baomidou/mybatis-plus-boot-starter -->
<dependency>
    <groupId>com.baomidou</groupId>
    <artifactId>mybatis-p lus-boot-starter</artifactId>
    <version>3.5.2</version>
</dependency>

```

2、配置相关信息

同上

3、创建Dao层

```java
@Mapper
public interface UserDao extends BaseMapper<User> {
}
```

### 整合Druid

1、导入jar包

```xml
<dependency>
    <groupId>com.alibaba</groupId>
    <artifactId>druid-spring-boot-starter</artifactId>
    <version>1.2.9</version>
</dependency>
```

2、配置

```yaml
spring:
  datasource:
    druid:
      driver-class-name: com.mysql.cj.jdbc.Driver
      url: jdbc:mysql://localhost:3306/watersystem
      username: root
      password: "wxw0603"
      type: com.alibaba.druid.pool.DruidDataSource
```

## SSMP整合案例

- 实体类开发——使用Lombook快速制作实体类
- Dao开发——整合MyBatisPlus，制作数据层测试类
- Service开发——基于MyBatisPlus进行增量开发，制作业务层测试类
- Controller开发——基于Restful开发

#### Lombok实现实体类快速开发

```xml
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <scope>provided</scope>
</dependency>
```

```java
@Data
public class Book {
    private Integer id;
    private String type;
    private String name;
    private String description;
}
```

#### 数据层标准开发

yaml配置

```yaml
#Tomcat配置
server:
  port: 80
#Mybatis-plus配置
spring:
  datasource:
    druid:
      username: root
      password: wxw0603
      url: jdbc:mysql://localhost:3306/bookmanager
      driver-class-name: com.mysql.cj.jdbc.Driver
```

mysql结构

```mysql
+-------------+--------------+------+-----+---------+----------------+
| Field       | Type         | Null | Key | Default | Extra          |
+-------------+--------------+------+-----+---------+----------------+
| id          | int          | NO   | PRI | NULL    | auto_increment |
| type        | varchar(20)  | YES  |     | NULL    |                |
| name        | varchar(50)  | YES  |     | NULL    |                |
| description | varchar(100) | YES  |     | NULL    |                |
+-------------+--------------+------+-----+---------+----------------+

+----+------+------------------+------------------------------------------+
| id | type | name             | description                              |
+----+------+------------------+------------------------------------------+
|  1 | 数学 | 《高等数学》     | 教的数学，考的玄学，这本书终将教你做人。 |
|  2 | 英语 | 《学术英语》     | 哇哦                                     |
|  3 | 美术 | 《世间的美与丑》 | 由某个艺术家所撰写的，小有名气           |
+----+------+------------------+------------------------------------------+
```

#### 开启MP运行日志

配置开启

```yaml
mybatis-plus:
  configuration:
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl
```

#### 分页

1、添加mybaits-puls分页功能拦截器

```java
@Configuration
public class MPConfig {

    @Bean
    public MybatisPlusInterceptor getMybatisInterceptor(){
        MybatisPlusInterceptor mybatisPlusInterceptor = new MybatisPlusInterceptor();
        InnerInterceptor innerInterceptor = new PaginationInnerInterceptor();
        mybatisPlusInterceptor.addInnerInterceptor(innerInterceptor);
        return mybatisPlusInterceptor;
    }

}
```

2、创建IPage对象封装当前页和每页数据个数

```java
@Test
void contextLoads() {
    IPage page = new Page(2,2);
    bookDao.selectPage(page,null);//会将查询到的数据放入传进去的page对象当中
    System.out.println(page.getRecords());
}
```

#### 按条件查询

1、创建QueryWrapper对象，设置查询条件

```java
@Test
void contextLoads() {
    QueryWrapper<Book> wrapper = new QueryWrapper<Book>();
    wrapper.eq("id",1);
    System.out.println(bookDao.selectList(wrapper));
}
```

2、LanbdaQueryWrapper，提供检查错误功能

```java
@Test
void contextLoads() {
    LambdaQueryWrapper<Book> wrapper = new LambdaQueryWrapper<>();
    String id = "1";
    wrapper.eq(id!=null,Book::getId,id);//如果第一个参数为true才执行，否则不执行
    System.out.println(bookDao.selectList(wrapper));
}
```

#### 业务层标准开发

正常开发即可

#### 业务层快速开发

如果需要实现新业务需要添加接口方法和实现方法，最好自己的方法前添加标识，防止覆盖

```java
//mybatis-plus框架Service接口
public interface BookService extends IService<Book> {
}

//mybatis-plus框架Service实现类,其中两个泛型写的是实体类和泛型类
@Service
public class BookServiceImpl extends ServiceImpl<BookDao,Book> implements BookService{
}
```

#### 表现层标准开发

使用rest风格开发

```java
@RestController
@RequestMapping("/books")
public class BookController {

    @Autowired
    private BookService bookService;

    @GetMapping
    public List<Book> getALL(){
        return bookService.list();
    }
    @PostMapping
	//使用请求体参数
    public Boolean save(@RequestBody Book book){
        return bookService.save(book);
    }
    @PutMapping
	//使用请求体参数
    public Boolean update(@RequestBody Book book){
        return bookService.update(book,null);
    }
    @DeleteMapping("{id}")//参数路径
    public Boolean delete(@PathVariable int id){
        return bookService.removeById(id);
    }
    @GetMapping("{id}")//参数路径
    public  Book getById(@PathVariable int id){
        return bookService.getById(id);
    }
    @GetMapping("{currentPage}/{pageSize}")
    public IPage<Book> getPage(@PathVariable int currentPage,@PathVariable int pageSize){
        return bookService.getPage(currentPage,pageSize);
    }
}
```

#### 表现层数据一致性处理

返回前端数据具有多样性，不方便前端人员开发

改进方案：

设计表现层返回结果的模型类，用于后端与前端进行数据格式统一，也称为前后端数据协议

```java
@Data
public class R {
    private Boolean flag;//表示运行是否出错
    private Object data;//返回的数据
    public R(){

    }
    public R(Boolean flag){
        this.flag = flag;
    }
    public R(Boolean flag, Object data) {
        this.flag = flag;
        this.data = data;
    }
}
```

```java
@RestController
@RequestMapping("/books")
public class BookController {
    @Autowired
    private BookService bookService;

    @GetMapping
    public R getALL(){
        return new R(true,bookService.list());
    }
    @PostMapping
    public R save(@RequestBody Book book){
        return new R(bookService.save(book));
    }
    @PutMapping
    public R update(@RequestBody Book book){
        return new R(bookService.update(book,null));
    }
    @DeleteMapping("{id}")
    public R delete(@PathVariable int id){
        return new R(bookService.removeById(id));
    }
    @GetMapping("{id}")//参数路径
    public  R getById(@PathVariable int id){
        return new R(true,bookService.getById(id));
    }

    @GetMapping("{currentPage}/{pageSize}")
    public R getPage(@PathVariable int currentPage,@PathVariable int pageSize){
        return new R(true,bookService.getPage(currentPage,pageSize));
    }
}
```

