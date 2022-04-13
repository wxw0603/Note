# JDBC

## 数据库连接

方法一：

```java
public void conn1(){
    String url = "jdbc:mysql://localhost:3306/note";
    Properties info = new Properties();
    info.setProperty("user","root");
    info.setProperty("password","wxw0603");
    try {
        Driver driver = new Driver();
        Connection conn = driver.connect(url,info);
    } catch (SQLException e) {
        e.printStackTrace();
    }
}
```

URL:jdbc:mysql://localhost:3306/数据库名

协议:子协议:子名称

Properties:包含了用户名和密码

方法二：

为提高程序可移植性

将第三方驱动通过类加载

```java
public void conn2() {
    String url = "jdbc:mysql://localhost:3306/note";
    Properties info = new Properties();
    info.setProperty("user", "root");
    info.setProperty("password", "wxw0603");
    try {
        Class clazz = Class.forName("com.mysql.cj.jdbc.Driver");
        Driver driver = (Driver) clazz.newInstance();
        Connection conn = driver.connect(url, info);
    } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | SQLException  e) {
        e.printStackTrace();
    }

}
```

方式三：

使用DriverManager取代驱动，管理驱动集合

```java
public void conn3() {
    String url = "jdbc:mysql://localhost:3306/note";
    Properties info = new Properties();
    info.setProperty("user", "root");
    info.setProperty("password", "wxw0603");
    try {
        Class clazz = Class.forName("com.mysql.cj.jdbc.Driver");
        Driver driver = (Driver) clazz.newInstance();
        //注册驱动
        DriverManager.registerDriver(driver);
        DriverManager.getConnection(url,info);
    } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | SQLException  e) {
        e.printStackTrace();
    }

}
```

方法四：

对方法三优化代码，在mysql驱动中的static静态代码块中注册drivermanager，不需要再注册

```java
public void conn3() {
    String url = "jdbc:mysql://localhost:3306/note";
    Properties info = new Properties();
    info.setProperty("user", "root");
    info.setProperty("password", "wxw0603");
    try {
        Class.forName("com.mysql.cj.jdbc.Driver");
        DriverManager.getConnection(url,info);
    } catch (ClassNotFoundException | SQLException  e) {
        e.printStackTrace();
    }

}
```

```java
public class Driver extends NonRegisteringDriver implements java.sql.Driver {
    public Driver() throws SQLException {
    }

    static {
        try {
            DriverManager.registerDriver(new Driver());
        } catch (SQLException var1) {
            throw new RuntimeException("Can't register driver!");
        }
    }
}
```

方式五：

代码与数据分离，将数据库注册信息抽离到配置文件

## 操作和访问数据库

Statement：用于执行静态sql语句并返回结果

PrepatedStatement：sql语句被预编译并储存在该对象中，可以使用此对象高效执行多次该语句

CallableStatement：用于执行sql储存过程

操做过程：如你所愿

字段通用查找

```java
ResultSet rs = ps.executeQuery();
//获取结果集元数据
ResultSetMetaData rsmd = rs.getMetaData;
//获取结果集中的列数
int columnCount = rsmd.getColumnCount();
if(rs.next()){
    for(int i = 0 ; i < columnCount ; i++ ){
        Object value = rs.getObject(i+1);
        //获取列值
        String name = rs.getColumnName(i+1);
        //下面通过反射注入字段
        ...
    }
}
```

表通用查找通过泛型类和反射确定返回类型实现

## Blob类型数据

blob是一个二进制大类型对象，可以存储大量数据的容器

插入blob类型的数据必须使用PreparedStatement，因为Blob类型数据无法用字符串拼写

插入时使用占位符，用文件输入流将指定文件输入

查询时获取Blob类型对象，获取输入输出流，输出文件

## 批量操作

用for循环即可，注意添加缓存，将注入的值添加进缓存，积累一定数量执行一次

mysql服务器默认关闭批处理，需要手动开启

把?rewriteBatchedStatements=true添加在jdbc连接后面

```java
ps.setObject(1,"haha");
//攒
ps.addBatch();
if(...){
    //执行
    ps.executeBatch();
    //清空
    ps.clearBatch();
}
```

## 事务

```java
conn.setAutoCommit(false);
conn.commit();
conn.rollback();
```

## 隔离级别

### 

| 隔离级别              | 脏读 | 不可重复读 | 幻读 |
| --------------------- | ---- | ---------- | ---- |
| read uncommitted      | ✔    | ✔          | ✔    |
| read committed        | ╳    | ✔          | ✔    |
| repeatable read(默认) | ╳    | ╳          | ✔    |
| serializable          | ╳    | ╳          | ╳    |

```java
conn.getTransactionIsolation();//获取事务隔离级别,返回为int
conn.setTransactionIsolation(int a);//设置隔离级别，
```

## 数据库连接池

需要一个控制器来管理所有的数据库连接使用数据库

### 基本思想：

为数据库连接建立缓冲池，预先在缓冲池中放入一定数量的连接，当需要建立数据库连接时，只需要从缓冲池中取出一个，使用完毕放回

## C3P0数据库连接池

```java
public class C3P0Test {
    public ComboPooledDataSource cp = null;

    C3P0Test()throws Exception{
        cp = new ComboPooledDataSource();
        cp.setDriverClass("com.mysql.cj.jdbc.Driver");
        cp.setJdbcUrl("jdbc:mysql://localhost:3306/note");
        cp.setUser("root");
        cp.setPassword("wxw0603");

        //设置初始时数据库连接池中的连接数
        cp.setInitialPoolSize(10);

        Connection conn = cp.getConnection();
    }

}
```

可以将配置信息抽离到配置文件c3p0-config.xml中

## DBCP数据库连接池

```java
public class DBCPTest {

    BasicDataSource source = null;

    DBCPTest()throws Exception{
        source = new BasicDataSource();
        //通过配置文件创建DBCP数据库连接池(工厂模式)
        source = (BasicDataSource) BasicDataSourceFactory.createDataSource(new Properties());
        source.setDriverClassName("jdbc:mysql://localhost:3306/note");
        source.setUrl("jdbc:mysql://localhost:8080/note");
        source.setUsername("root");
        source.setPassword("wxw0603");

        source.setInitialSize(10);

        Connection conn = source.getConnection();


    }
}
```

## Druid数据库连接池

使用方法与DBCP类似

## DBUtils工具类

 ```java
public class DBUtilsTest {
    public static void main(String[] args) {
        QueryRunner runner = new QueryRunner();
        runner.update(conn,sql,params);
        runner.query(conn,sql);
        ResultSetHandler resultSetHandler = new BeanHandler();//返回结果处理器
        DBUtils.close(conn);
    }
}
 ```

