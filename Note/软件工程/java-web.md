# JAVAWeb

## HTML

## CSS

## JS

## Web

CS：客户端服务器模式

BS：浏览器服务器模式

### Servlet

#### 工作流程

1、用户发送请求

2、项目中扫描web.xml文件找到对应的servlet映射

3、找到对应的servlet程序

4、根据method执行相应的postget方法

##### 细节：

1、get方法发送的中文数据转码需要打散成字节数组ISO-8859-1编码

再从新组装(tomcat8之前)

### Servlet继承关系

#### Servlet接口

init(),service(),destroy()

初始化方法有无参和有参构造

init(){

}

init(ServletConfig config){

}

如果想要在初始化时期做一些准备工作可以重写init方法

可以通过注解方式配置

可以通过ServletConfig类获取配置时的参数

##### GenericServlet抽象类

###### HttpServlet

服务方法:有请求时service方法自动响应，tomcat自动调用

HttpServlet帐这些do方法默认都是405实现，没有配置的请求会报405错误

### Servlet生命周期

第一次接受请求时实例化初始化

Servlet实例对象只会创建一个所有请求都是这个去响应

容器销毁时Servlet销毁

可以通过<load-on-starup>变更加载时间，数值从1

开始，越小加载越靠前

Servlet在容器中单例的，线程不安全的

## Http协议

### 请求报文

浏览器发送给服务器的数据

#### 1、请求行

展示当前请求最基本信息

请求方式

访问地址

HTTP协议版本

#### 2、请求消息头

用具体的参数对本次请求进行详细说明

浏览器型号，版本，能接受的内容类型等等

Host:服务器主机地址

Accept:声明当前请求能接受的媒体类型

Referer:来源页面地址

Content-Type:请求体的内容类型，值为媒体类型中某一种

Cookie:访问携带的Cookie数据

#### 3、请求主体

get方式:没有请求体，但有一个queryString,跟在URL后面的参数

post方式:有请求体，form data格式

json格式:有请求体，request payload

### 响应报文

服务器发送给浏览器的数据

#### 1、响应行

展示当前请求最基本信息

响应状态码

响应状态

HTTP协议版本

#### 2、响应消息头

服务器发送给浏览器信息

内容媒体类型，编码，内容长度，服务器类型

#### 3、响应主体

响应的实际内容

## 会话

原因是HTTP无状态

客户端第一次发请求，服务器获取session，获取不到则创建一个如何响应给客户端

下次客户端给服务器发请求会把seesionID带给服务器

### 常用API

```java
request.getSession();
request.getSession(true);//同上
request.getSession(false);//获取当前会话，没有返回null
session.getId();
session.idNew();//判断是否为新建的
session.getMaxInactiveInterval()//非激活间隔时长，默认半小时
session.invalidate()//强制使会话失效
```

### Session保存作用域

session.setAttribute(k,v);

session保存作用域是和具体的某一个session对应的

## 服务器内部转发和客户端重定向

### 服务器内部转发

一次请求响应的过程，对于客户端而言，内部经过多少次转发，客户端不知道

### 客户端重定向

两次请求响应过程

## Thymeleaf

```java
public class ViewBaseServlet extends HttpServlet {

    private TemplateEngine templateEngine = null;

    public void init() {
        //获取ServletContext对象
        ServletContext servletContext = this.getServletContext();

        //创建Thymeleaf解析器对象
        ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver((javax.servlet.ServletContext) servletContext);

        //给解析器对象设置参数
        templateResolver.setTemplateMode(TemplateMode.HTML);

        //设置前后缀
        String prefix = servletContext.getInitParameter("view-prefix");
        String suffix = servletContext.getInitParameter("view-suffix");
        templateResolver.setPrefix(prefix);
        templateResolver.setSuffix(suffix);

        //设置缓存过期时间(毫秒)
        templateResolver.setCacheTTLMs(60000L);

        //设置服务器编码格式
        templateResolver.setCharacterEncoding("utf-8");

        //创建模板引擎对象
        templateEngine = new TemplateEngine();

        //给模板引擎对象设置模板解析器
        templateEngine.setTemplateResolver(templateResolver);

    }

    protected void processTemplate(String templateName, HttpServletRequest req,HttpServletRequest resp){
        //设置响应体字符集
        try {
            resp.setCharacterEncoding("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        //创建WebContext对象
        WebContext webContext = new WebContext(req,resp,this.getServletContext());
        //处理模板数据
        templateEngine.process(templateName,webContext,resp.getWriter());


    }

}
```



使用ViewBaseServlet中的super.processTemplate(".html",req,resp);

方法渲染页面

## 作用域

### page(页面级别)

### request(一次请求响应级别)

### session(一次会话级别)

### application(整个应用程序级别)

## 路径问题

尽量使用绝对路径:http://localhost:8080/

## 注解

```java
@WebServlet
```

## ServletContext

可以通过多种方式获取

ServletContext  c =  getServletContext();

通过request对象获取

## 业务层

### MVC：Model、View、Controller

View：用于做数据展示及和用户交互的界面

Controller：接受客户端请求，具体业务功能还是借助于模型

Model：数据载体pojo、业务模型，数据Dao模型

1、pojo:值对象

2、DAO:数据访问对象

3、BO:业务对象

### 区分业务对象和数据访问对象

1、DAO中的方法都是单精度方法：一个方法只考虑一个操作

2、BO中的方法属于业务方法，较为复杂

## Servlet优化

1、访问一个servlet时携带参数，同时用switch判断要执行的方法

2、通过反射替代switch

3、所有servlet变为Controller，转为一个核心控制servlet管理

4、用类解析器来做一个通用的核心控制器，让Controller中跳转方法返回字符串在核心中集中处理，方法通过类加载器根据参数对比集中处理。

5、将Controller中方法的获取参数抽取出来放在核心控制器中，用类加载得到的方法对象获取方法参数，在parameter中寻找对应参数调用方法，从jdk8开始的新特性，在java编译器中加入参数-parameters，表示函数形参不擦除，否则函数参数返回arg0,arg1...注意参数类型转换

其中所有Controller的类在核心控制器实例化时解析xml后创建实例存放在map中，可以称之为容器

## xml解析

```java
//加载xml文件为输入流
InputStream inputstream=getClass().getClassLoder().getResourceAsStream("application.xml");
//创建文件创建工厂类
DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
//创建文件创建对象
DocumentBuilder db = dbf.newDocumentBuilder();
//创建文档对象
Document doc = db.parse(inputstream);//org.w3c包下
NodeList beanList = doc.getElementByTagName("bean");
//Node节点
//	Element元素节点
//	Text文本节点
```

## Service服务

操作数据库，在Controller中调用Service方法而不是直接调用DAO层

## IOC

高内聚低耦合：层内部的代码应当高内聚，层与层之间低耦合，最理想状态是零耦合

将三层的实例对象都交给beanfactory统一管理，在xml里面说明依赖注入，在beanfactory中创建实例化对象，存入map，组装bean间的依赖关系

## filter

拦截过程：

1、拦截

2、代码块

3、放行

```java
implements Filter

doFilter(){
    //执行第二
    filterChain.doFilter(req,resp);//放行
    //执行第三
}
```



4、响应拦截

5、代码块

6、发送

过滤器链可以有多个过滤器，过滤器链按照xml文件配置顺序或者用注解的时候按照全类名字母顺序排序

## 事务管理

DAO层的操作

1、获取链接，取消自动提交//conn.setAutoCommit(false);

try{

​	执行操作

​	提交事务

}catch(Exception e){

​	回滚事务//conn.rollback();

}

当一个Service调用了多个DAO层方法必须保证所有方法同时成功，其中有一个失败就回滚

结论：事务层不能像DAO层一样用单精度方法为单位而是要以业务层的方法为单位

## OpenSessionInViewFilter

将事务操作放在一个filter中，当用户访问被拦截的路径就会开启事务

```java
try{
    autoCommit(false);
    放行();-->执行事务
    commit();
}catch(Exception e){
    roback();
}
//这就要求事务中的数据库操作用同一个Connection操作，使用ThreadLocal类
```

```java
public class ConnUtil {
    public static String Driver = null;
    public static String URL = null;
    public static String User = null;
    public static String Pwd = null;

    private static ThreadLocal<Connection> threadLocal = new ThreadLocal<Connection>();

    public static Connection getConnection(){
        Connection conn = threadLocal.get();
        if(conn==null) {
            try {
                Class.forName(Driver);
                conn = DriverManager.getConnection(URL, User, Pwd);
                threadLocal.set(conn);
            } catch (ClassNotFoundException | SQLException e) {
                e.printStackTrace();
            }

        }
        return threadLocal.get();
    }

    public static void closeConnection() throws SQLException{
        Connection conn = threadLocal.get();
        if(conn!=null&&!conn.isClosed()){
            conn.close();
            threadLocal.set(null);
        }
    }
}
```

## ThreadLocal

-get();

```java
public T get() {
    Thread t = Thread.currentThread();
    ThreadLocalMap map = getMap(t);
    if (map != null) {
        ThreadLocalMap.Entry e = map.getEntry(this);
        if (e != null) {
            @SuppressWarnings("unchecked")
            T result = (T)e.value;
            return result;
        }
    }
    return setInitialValue();
}
```



-set();

```java
public void set(T value) {
    Thread t = Thread.currentThread();//获取当前线程
    ThreadLocalMap map = getMap(t);//每个线程维护各自的一个容器(ThreadLocalMap)
    if (map != null) {
        map.set(this, value);
    } else {
        createMap(t, value);
    }
}
```



称之为本地线程

## Listener监听器

1、ServletConetxtListener-监听ServletContext对象创建和销毁的过程

2、HttpSessionListener-监听HttpSession对象创建和销毁的过程

3、ServletRequestListener-监听ServletRequest对象创建和销毁的过程



4、ServletConetxtAttributeListener-监听ServletContext作用域改动的过程

5、HttpSessionAttributeListener-监听HttpSession作用域改动的过程

6、RequestAttributeListener-监听Request作用域改动的过程



7、HttpSessionBindingListener-监听某个对象在Session作用域中创建和移除的过程

8、HttpSessionActivationListener-监听某个对象在Session中的序列化和反序列化

## Cookie

```java
Cookie cookie = new Cookie(K,V);//创建Cookie对象
resp.addCookie(cookie);//将cookie响应给浏览器，保存cookie
cookie.setMaxAge(20);//设置过期时间，以秒为单位
cookie.setDomain(pattern);
cookie.setPath(url);
```

## Vue