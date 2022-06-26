# Mybatis

## 核心配置文件

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE configuration
        PUBLIC "-//mybatis.org//DTD Config 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-config.dtd">

<configuration>
    <!--配置连接数据库的环境-->
    <environments  default="development">
        <environment id="development">
            <transactionManager type="JDBC"/><!-- JDBC使用原生事务管理，MANAGED表示被接管 -->
            <dataSource type="POOLED"><!-- 数据源类型，POOLED使用数据库连接池/UNPOOLED不使用/JNDI使用上下文中数据源 -->
                <property name="driver" value=""/>
                <property name="url" value=""/>
                <property name="username" value=""/>
                <property name="password" value=""/>
            </dataSource>
        </environment>
    </environments>
    <!--引入映射文件-->
    <mappers>
        <mapper resource=""/>
        <package name=""/><!-- 以包来引入配置文件 -->
    </mappers>
</configuration>
```

## 创建mapper接口和映射文件

1、映射文件命名规则：

表所对应实体类的类名+Mapper.xml

2、映射文件存放位置：

src/main/resource/mappers目录下

3、mapper接口的全类名和映射文件的命名空间相同

4、mapper接口中方法的方法名和映射文件中编写sql的标签的id属性相同

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper
	PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
	"http://mybatis.org/dtd/mybatis-3-mapper.dtd">

<mapper namespace="接口全类名" >
    <select id="方法名" resultType="">
        select * from haha where id = 1
    </select>
</mapper>

```

## 具体使用

```java
@org.junit.Test
    public void test () throws Exception{
    //加载核心配置文件
    InputStream is = Resources.getResourceAsStream("mybatis-config.xml");
    //获取SqlSessionFactoryBuilder
    SqlSessionFactoryBuilder sqlSessionFactoryBuilder = new SqlSessionFactoryBuilder();
    //获取SqlSessionFactory
    SqlSessionFactory sqlSessionFactory = sqlSessionFactoryBuilder.build(is);
    //获取SqlSession代表java程序和数据库之间的会话
    //true为自动提交
    SqlSession session = sqlSessionFactory.openSession(true);
    //获取mapper接口对象
    UserMapper userMapper = session.getMapper(UserMapper.class);
    User user = userMapper.getUserById(1);
    System.out.println(user.getUsername());
}
```

## 获取值的方式

${} 字符串拼接需要注意引号

#{}  占位符赋值

1、获取有限个字面量

```xml
<mapper namespace="com.example.webapp.Mapper.UserMapper" >
    <select id="getUserById" resultType="com.example.webapp.pojo.User">
        select * from user where id = ${arg0};
        //多个参数时mybatis会以键值对形式存储数据
        //arg0、arg1...
        //param1、param2...
    </select>
</mapper>
```

2、获取单个集合

```xml
<mapper namespace="com.example.webapp.Mapper.UserMapper" >
    <select id="getUserById" resultType="com.example.webapp.pojo.User">
        select * from user where id = ${id};
    </select>
    //id存放在map中
</mapper>
```

3、获取实体类对象

```xml
<mapper namespace="com.example.webapp.Mapper.UserMapper" >
    <select id="getUserById" resultType="com.example.webapp.pojo.User">
        select * from user where id = ${id};
    </select>
    //输入User对象，通过属性名访问值
</mapper>
```

命名参数注解方式

```java
    public User getUserById(@Param("username") int id);
```

## 查询方式

1、查询实体类对象

返回值设置为实体类

2、查询list集合

返回值设置为List<实体类>

其中mapper中的返回类型只需要写查询的实体类即可

3、实体类也可以用Map代替

在方法上添加@MapKey注解可以以某个字段为标识

此时papper中返回类型需要写map

### 细节：

mybatis配置了默认的类型别名