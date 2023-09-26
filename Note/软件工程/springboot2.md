# SpringBoot

## yaml语法

属性与值大小写敏感

##### 值的写法：

字面量：普通值

k:v 直接写

字符串默认不用加引号

""双引号会转义特殊字符

''单引号不会

##### 对象、Map:

对象还是k:v的形式

```yaml
friend:
 age: 20
 name: fuck
maps:{k1:k2,k2:k3}
```

行内写法:

```yaml
friend:(age: 20,name: fuck)
```



##### 数组：

用-值表示一个元素

```yaml
pets:
 - cat
 - dog
 - pig
```

行内写法：

```yaml
pets: [cat,dog,pig]
```

##### 配置文件数据获取：

###### 方法一：@ConfigurationProertices

自动映射类属性值

```java
@ConfigurationProertices(prefix="前缀")
```

###### 方法二：@Value

```java
@Value("${person.name}")
```

比较

|                | @ConfigurationProertices | @Value   |
| -------------- | ------------------------ | -------- |
| 功能           | 批量注入类               | 单个指定 |
| 松散绑定       | 支持                     | 不支持   |
| SpEL表达式     | 不支持                   | 支持     |
| JSR303数据校验 | 支持                     | 不支持   |
| 复杂类型封装   | 支持                     | 不支持   |

JSR303校验:

```java
@Validated /*开启校验*/
public adwa{
    @Email  /*校验是否为邮箱格式*/
    private String a; 
}
```

##### 配置文件的加载：

###### @PropertiesSource

标志在需要的地方临时生效

```java
@PropertiesSource(value="classpath:...")
```



加载对应配置文件

###### @importResource

添加在主配置类上

```java
@importResource(value="classpath:...")
```

###### spring推荐全注解配置类@Configuration

```java
@Configuration
public class MyConfig{
    //将方法返回值添加到组件，id为方法名
    @Bean
    public Map(K,V) haha(){
        return new Map(String,String);
    }
} 
```

##### 通用占位符：

```yaml
a:
 - ${random.value}
 - ${random.int}
 - ${rondom.long}
 - ${rondom.int(10)}
 - ${rondom.int[1024,65536]}
 - ${前面配置的属性值}
```

##### Profile多环境支持:

```yaml
spring:
 profiles:
  active:dev

---

spring:
 profiles:dev
 
---
spring:
 profiles:prod
```

命令行参数指定:

--spring.profiles.active=dev

虚拟机参数指定:

-Dspring.proflies.active=dev

##### 配置文件扫描:

扫描一下路径：

```yaml
- file:./config/
- file:./
- classpath:/config/
- classpath:/
```

优先级从高到低排序

高优先级覆盖低优先级

##### 外部配置加载：

1、命令行参数

2、来自java系统属性（System.getPropertices()）

3、操作系统环境变量

5、RandomValueProperticesSource配置的random.*属性值

6、jar包外部的application-{proflie}.propertices或application.yaml(带spring.proflie)配置文件

7、jar包内部的application-{proflie}.propertices或application.yaml(带spring.proflie)配置文件

8、jar包外部的application-{proflie}.propertices或application.yaml(不带spring.proflie)配置文件

9、jar包内部的application-{proflie}.propertices或application.yaml(不带spring.proflie)配置文件

10、@Configuration注解类上的PropertySource

11、通过SpringApplication.setDefalutProperties指定的默认属性

从上到下优先级高到低

高优先级覆盖低优先级

## 日志

### SLF4j使用

```java
Logger logger = LoggerFactory.getLogger(要记录信息的类名);
logger.info("Hello Wprld");
```

### SLF4j与其它日志框架转换(waiting)

## 模板引擎

### Thymeleaf

引jar包

#### 识别路径:classpath:/templates/

#### 导入名称空间：

```html
<html lang="en" xmlns:th="http://www.thymeleaf.org"></html>
```

#### 使用Thymeleaf语法（waiting）

#### SpringMVC配置

```java
//不能标注EnableWebMvc
public class MvcConfiguration implements WebMvcConfigurer {
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addRedirectViewController("/,/*","classpath:/templates/login.html");
        WebMvcConfigurer.super.addViewControllers(registry);
    }
}
```

全面接管MVC:

```java
@EnableWebMvc
```

## web开发

### 国际化

1、国际化配置文件

放在i18n文件夹下：

2、配置文件配置国际化文件路径

```yaml
spring:
  messages:
    basename: i18n.Login
```

3、页面获取国际化内容

```html
<h1 text="haha" th:text="#{a}">
    
</h1>
```

4、配置国际化（Locale）（区域信息对象）;

LocalResolver 

```java
public class LoginLocaleResolver implements LocaleResolver {

    @Override
    public Locale resolveLocale(HttpServletRequest request) {
        String l = request.getParameter("l");
        Locale locale = Locale.getDefault();
        if(!StringUtils.isEmpty(l)){
            String[] split = l.split("_");
            locale = new Locale(split[0],split[1]);
        }
        return locale;
    }

    @Override
    public void setLocale(HttpServletRequest request, HttpServletResponse response, Locale locale) {

    }
}

```

在SpringMVC配置类中添加该组件并且Bean名必须为LocaleResolver

## 登录

### 重定向

```java
return "redirect:/main.html"
```

### 拦截器

```java
HandlerInterceptor 接口
```

```java
public class LoginHandlerInterceptor implements HandlerInterceptor {
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Object username = request.getSession().getAttribute("loginuser");
        if(username==null){
            request.setAttribute("msg","没有权限请先登录");
            request.getRequestDispatcher("/").forward(request,response);
            return false;
        }
        else{
            return true;
        }
    }

    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable ModelAndView modelAndView) throws Exception {
    }

    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable Exception ex) throws Exception {
    }
}
```

###### **注意**

拦截器会拦截静态资源，要排除对静态文件夹的拦截，“/”映射到static文件夹

```java
registry.addInterceptor(new LoginHandlerInterceptor()).addPathPatterns("/**").excludePathPatterns("/","/user/login").
                excludePathPatterns("/asserts/**");
```

### thymleleaf公共页抽取

1、th:insert

```html
1、抽取公共片段
<div th:fragment="copy">
    afwfdawd
</div>

2、引入公共片段
<div th:insert="~{footer::copy}">
    
</div>
/*
~{templatename::selector}:模板名::选择器
~{templatename::fragmentname}:模板名::片段名
*/
3、默认效果
insert的功能片段在div中生效
可能产生问题
```

2、th:replace

3、th:include

对比:

```html
<div th:insert="footer::copy">
    公共片段插入div
</div>
<div th:relace="footer::copy">
    公共片段替换div
</div>
<div th:include="footer::copy">
    公共片段内容加入div
</div>
```

### Controller使用

```java
    @Autowired
    EmployeeDao employeeDao;
    @Autowired
    DepartmentDao departmentDao;

    @GetMapping("/emps")
    public String list(Model model){
        Collection<Employee> employeeCollection = employeeDao.getAll();
        model.addAttribute("emps",employeeCollection);
        return "emp/list.html";
    }
//Model为表单域信息
    @GetMapping("/emp")
    public String toAddPage(Model model){
        Collection<Department> departments = departmentDao.getDepartments();
        model.addAttribute("depts",departments);
        return "emp/add";
    }

    @PostMapping("/emp")
    public String addEmp(Employee employee){
        employeeDao.save(employee);
        return "redirect:/emps";
    }
//如果类属性和表单名映射会自动注入
    @GetMapping("/emp/{id}")
    public String upData(@PathVariable("id") Integer id,Model model){
        Employee employee = employeeDao.get(id);
        model.addAttribute("emp",employee);
        return "emp/add";
    }
取链接信息
```

### Put请求

```html
<input type="hidden" name="_method" value="put" th:if="${emp!=null}"/>
```

1、springboot中已经配置好了HiddenHttpMethodFilter类

2、页面创建post表单

3、创建一个input项，name=“_method”，值就是指定的请求方式

### 错误处理机制

默认效果返回一个默认错误页面

#### 如何定制：

1、有模板情况下；error/状态码

```yaml
error
 /4xx.html
 /5xx.html
```

2、没有模板情况下；静态资源文件夹下找

#### 定制错误数据：

```java
@ControllerAdvice//一个Controller增强器,最常用的就是异常处理
public class ExceptionHandler{
    //浏览器和客户端返回都是json
    @ResponseBody
    @ExceptionHandler(UserNoExistException.class)
    public String handlerException(Exception e){
        return "lscb";
    }
    
    //浏览器和客户端返回区分
    @ExceptionHandler(UserNoExistException.class)
    public String handlerException(Exception e,HttpServletRequest request){
        request.setAttribute("javax.servlet.error.status_code",400);
        return "forward:/error";
    }
    
    
}
```

#### wait	

## 嵌入式Srevlet

SbringBoot默认使用tomcat作为嵌入式的Srevlet容器

### 如何定制tomcat

1、使用配置文件

2、编写WebServerFactoryCustomizer：嵌入式的Servlet容器定制器在MvcConfig

类中

```java
@Bean
public TomcatServletWebServerFactory tomcatServletWebServerFactory(){
    TomcatServletWebServerFactory servletWebServerFactory = new TomcatServletWebServerFactory();
    servletWebServerFactory.setPort(8081);
    return servletWebServerFactory;
}
```

### 注册三大组件

配置注册类

1、ServletRegistrationBean

2、FilterRegistrationBean

3、ServletListenerRegistrationBean

```java
@Bean
public ServletRegistrationBean haha(){
    ServletRegistrationBean ha = new ServletRegistrationBean(new MainServlet(),"/ha");
    return ha;
}
```

### 其他嵌入式容器

Jetty(长连接)

Undertow(不支持jsp)

Servlet容器配置

```java

```

### 外置Servlet容器

嵌入式Servlet容器：

​		优点：简单，便捷

​		缺点：默认不支持jsp，优化定制比较复杂

#### 外置Servlet：外面安装Tomact容器---应用war包方式

##### wait

## Docker

轻量级容器技术，使用数据隔离，类似虚拟机

#### 核心概念

docker镜像（images）：用于创建Docker容器的模板

docker容器（Container）容器时独立运行的一个或一组应用

docker客户端（Client）客户端通过命令行或者其他工具使用Docker

docker主机（Host）：一个物理或者虚拟的机器用于执行Docker守护进程和容器

docker仓库（Registry）：Docker仓库用来保存镜像，可以理解为代码控制中的代码仓库

## 数据库

### 1、JDBC