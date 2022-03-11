# JAVA

# 第一章

## 基本类型

#### 整型

| 类型  | 存储需求 | 取值范围     |
| ----- | -------- | ------------ |
| int   | 4字节    | +-21亿左右   |
| short | 2字节    | -32768~32767 |
| long  | 8字节    | 很大         |
| byte  | 1字节    | -128~127     |

特点

L或l为后缀 长整型

0x前缀  十六进制数

0前缀  八进制数

0b或0B  二进制数(java7开始)

数字间可以用下划线分割例如:1_000_000(java7开始)

默认没有unsigned的类型需要可以通过

Long.toUnsignedLong(num);转换

#### 浮点类型

#### 

| 类型   | 存储需求 | 取值范围    |
| ------ | -------- | ----------- |
| flaot  | 4字节    | 有效位数6~7 |
| double | 8字节    | 有效位数15  |

溢出与出错的三个浮点数值

- 正无穷大  Double.POSITIVE_INFINITY
- 负无穷大  Double.NEGATIVE_INFINITY
- NaN  Double.NaN

所有NaN被认为是不相同的，要判断是否为NaN只有通过

```java
if(Double.isNaN(x))
```

#### char类型

char表示单个字符也可用Unicode字符描述

转义字符

| 转义序列 | 名称   | Unicode值 |
| -------- | ------ | --------- |
| \b       | 退格   | \u0008    |
| \t       | 制表   | \u0009    |
| \n       | 换行   | \u000a    |
| \r       | 回车   | \u000d    |
| \ '      | 单引号 | \u0022    |
| \ "      | 双引号 | \u0027    |
| \ \      | 反斜杠 | \u005c    |

#### 局部类型

可以从初始值推断出类型的局部变量可以使用var

```java
for(var i=0;i<n;i++){
}
```



#### 字符串转义细节

1、Unicode转义序列会在解析代码之前处理

也就是

```shell
jshell> "\u0022+\u0022" //先转义为""+""再字符串拼接
$1 ==> ""
```

2、Unicode字符大小写不敏感

\u000a<->\u000A

3、注释中的Unicode字符也会被解析，尽量避免在注释中出现/...

```java
//\u000ASystem.out.println("ha");
        System.out.println("haha");
```

执行结果

```java
ha
haha
```



## 变量与常量

#### 常量

final表示常量，只能赋值一次不可被更改

一般常量名全大写

```java
public class Test {
    public static void main(String[] args) {
		final int NUM=0;
        System.out.println(NUM);
    }
}
```

一个类的多个方法中使用的常量称为类常量，用static final来设置

```java
public class Test {
    static final int NUM=0;
    public static void main(String[] args) {
        System.out.println(NUM);
    }
}
```

//const是java保留字还未使用

#### 枚举类型（waiting第五章节）

```java
enum Size {
    SMALL,
    HAHA,
    NM
}
Size s = SMALL;
```

Size类型的变量只能存储这个类型声明中的某个枚举值

## 运算符细节

1、整数/0 报除0错误，浮点数/0 结果为无穷大或者NaN

2、java虚拟机默认计算浮点数的寄存器为64位，而有些处理器采用80位寄存器

默认使用处理器为先，不会产生溢出

要想开启严格的浮点数计算可以在类/方法前加上strictfp修饰词

```java
public class Test {
    public static strictfp void main(String[] args) {
        System.out.println(5.0/9);
    }
}
```

3、少数几个结合性从右到左的运算符

```java
! ~ ++ -- + - (强转) new
?: = += -= /= %= &= |= ^= <<= >>= >>>=
```



## 数学函数和数学常量

```java
Math.sqrt(num);//平方根运算
Math.pow(double num1,double num2);//幂运算
Math.sin(num);
Math.cos(num);
Math.tan(num);
Math.atan(num);
Math.atan2(num);
Math.exp(num);
Math.log(num);//自然对数
Math.log10(num);
Math.PI//圆周率近似值
Math.E//e近似值
Math.round(num);//小数舍入
```

#### 独特的解决方案Math.floorMod

floorMod(position+adjustment,12);总会得到一个0~11之间的数

也就是((position+adjustment)%12+12)%12

也就是约瑟夫环考虑双向循环

#### 细节

1、可以直接导入静态资源就无需使用Math.

```java
import static java.lang.Math.*;
pow(2,3);
```

2、为了达到更好的性能所以Math默认使用计算机浮点单元中的例程，如果想要严格按照java虚拟机64位浮点运算就要使用StrictMath类确保在所有平台上取得相同的结果

3、Math提供了有异常处理的基本运算

```java
Math.multiplyExact(1e9,3);//抛出异常
1e9*3//结果为负数
```

具体实现

```java
public static int multiplyExact(int x, int y) {
    long r = (long)x * (long)y;
    if ((int)r != r) {
        throw new ArithmeticException("integer 				overflow");
    }
    return (int)r;
}
```

## 类型转换细节

1、int->float和long->double都会产生精度损失

所以计算中自动类型转换优先级

double>float>long>int

而long对float也会产生精度损失，尽量避免这种情况

2、int x=0; x+=3.5  <=> x= (int)(x+3,5)

如果运算得到的值与左侧类型不同就会发生强制类型转换

## 位运算符

```java
&//与
|//或
^//异或
~//非
>>//右移
<<//左移
>>>//无符号右移，空位补零
```

```java
int a;
int three=(a & 0b0100)/0b0100;//获取二进制下第三位数字,掩码技术
int three=(a & (1 << 2)) >> 2;
```

## 字符串

实际为Unicode字符序列，没有字符串基本类型，而是在java类库中提供了一个预定义类，叫做String，每个用双引号括起来的字符串都是String类的一个实例

### 字串

```java
"HaHa".substring(0,3);
//substring第二个参数表示不想复制的第一个字符
//所以上面值为"HaH"
```



### 某些奇怪方法

```java
String.join("/","S","M","L");//"S/M/L"
"java".repeat(3);//"javajavajava"
```

### 码点与代码单元

```java
System.out.println("\ud835\udd46"+"\ud835\udd46".length());

//𝕆2
```

#### warning：

" ".length()方法返回代码单元的数量而不是字符个数，准确的返回字符串长度应当使用返回码点数量的codePointCout(0,字符串代码单元数量)

```java
"HaHa".codePointCout(0,"HaHa".length());

System.out.println("\ud835\udd46"+"\ud835\udd46".codePointCount(0,"\ud835\udd46".length()));
```

而“”.charAt(i)返回的是第i个代码单元，应当使用"".codePointAt(i);

```java

int i = "".offsetByCodePoints(0,i);
"".codePointAt(i);
```

如何精准便利字符串

```java
public static void main(String[] args) {
    String a = "\ud835\udd46abcderf";
    int len = a.codePointCount(0,a.length());
    for(int i = 0 ; i < len ; i++ ){
        System.out.println( Character.toString(a.codePointAt(a.offsetByCodePoints(0,i))));
    }
}
```

或者可以使用codePoints方法

将字符串转为一个Unicode编码流，可以转化为数组

```java
public static void main(String[] args) {
    String a = "\ud835\udd46abcderf";
    int[] b = a.codePoints().toArray();
    for(int i = 0 ; i < b.length ; i++ ){
        System.out.println(Character.toString(b[i]));
    }

}
```

### StingAPI(waiting)





### 字符串构建

StringBuilder类

```java
StringBuilder a = new StringBuilder();
a.append("haha");
a.append("lala");
a.toString();
```

#### 常用方法(waiting)

```java
import java.lang.StringBuilder
StringBuilder s = new StringBuilder();
s.append("hahha");
s.delete(int starIndex,int endIndex);
//删除从starIndex到endIndex-1的代码单元并返回
```







## 输入输出

### 标准输入/出流

```java
System.out.println("haha");//标准输出
Scanner in = new Scanner(System.in);//Scanner类定义在java.util包下
in.next();
in.nextLine();
Console cons = System.Console();
//Console类定义在java.io包下
cons.readLine(String prompt,Object... arg);
cons.readPassword(String prompt,Object... arg);
//显示提示信息prompt，arg参数用于格式参数
```

### 格式化输出

java沿用C语言格式输出

```java
System.out.print("1000.0/3.0");//3333.33333333333335
System.out.printf("%8.2",x);//8个字符，小数点后保留两位
//前导空格+3333.33
```

#### 格式输出转换表

| 转换符(%+) | 类型                     | 示例       |
| ---------- | ------------------------ | ---------- |
| d          | 十进制整数               | 159        |
| x          | 十六进制整数             | 9f         |
| o          | 八进制整数               | 237        |
| f          | 定点浮点数               | 15.9       |
| e          | 指数浮点数               | 1.59e+01   |
| g          | 通用浮点数(e和f中较短的) | ——         |
| a          | 十六进制浮点数           | 0x1.fccdp3 |
| s          | 字符串                   | Hello      |
| c          | 字符                     | H          |
| b          | 布尔                     | true       |
| h          | 散列码                   | 42628b2    |
| tx或者Tx   | 日期时间                 | 过期格式   |
| %          | 百分号                   | %          |
| n          | 分隔符(与平台有关)       | ——         |

#### 特殊外观格式化表(waiting)



#### 日期时间转换符(waiting)



### 文件输入输出

```java
Scanner in = new Sanner(Path.of("mytext.text"),StandarCharsets.UTF_8);
PrintWriter out = new PrintWrite("mytext.text",StandarCharsets.UTF_8);//文件不存在会创建文件
```

#### 细节:

1、如果构造一个带有字符串参数的Scanner，会把字符串解释为数据而不是文件名

2、当文件路径是相对路径，路径位于相对java虚拟机**启动目录**的位置，如果在命令行模式下启动，**启动目录**就是命令行解释器当前目录，如果使用集成开发环境**启动目录**由IDE控制。

```java
String dir = System.getProperty("user.dir");
//返回启动目录
```

3、可以使用shell关联文件(waiting)



## switch细节:

1、case类型可以是

char、byte、short、int

枚举常量

字符串字面量(java7开始)

## break细节:

1、带标签的break

```java
read_data:
for(var i = 0 ; i < n ; i++ ){
    for(var j = 0 ; j < n ; j++ ){
        break read_data;//跳出最外层循环
    }
}
//跳转到带标签语句的末尾
```

2、break可以应用在任意代码块

## 大数运算

```java
BigInteger//任意精度整数运算
BigDecimal//任意精度浮点数运算
//两个java.math包下的类
```

## 数组

### 数组创建

```java
int[] a = new int[100];//创建数组
int[] a = {1,2,3,4,5};//创建并且赋值
a = new int[]{1,2,3};//新建赋值并覆盖
```

### 数组拷贝

```java
nums2 = Arrays.copyof(nums1,length);
//Arrays在java.util包下
//该函数将nums1拷贝到nums2，length参数是新生成的数组长度
//如果数组长度大于nums1额外元素赋值0或false，如果小于则拷贝不全
//该函数常用于数组扩充
```

### 多维数组

本质上是数组的数组

#### 不规则数组

```java
int[][] a = new int[MAX][];
for(var i = 0 ; i < MAX ; i++ ){
    a[i]=new int[i];
}
```

# 第二章

## 对象与对象变量细节:

1、new操作符返回值是一个引用

也就是，构造函数创造对象，new操作符返回引用

2、调用对象方法时参数分为隐式参数和显式参数

```java
a.add(b);
//隐式参数是a，而在方法中隐式参数用this指代
//显式参数是b
```



## 封装细节:

1、不要编写返回可变对象的访问器方法

```java
class A{
    private Date b;
    public Date getDate(){
        return b;
    }
}
getDate返回值和A.b引用同一个对象,破坏封装性
如果要返回一个可变对象的引用应当返回他的克隆
    return (Date)b.clone();
```

## final实例字段

必须在构造对象时初始化，作为常量使用

尽量不要使用final修饰可变对象，会造成混乱

```java
final StringBuilder a = new StringBuilder();
a.append("A");
```

## static静态字段和静态方法

### 静态字段

#### 静态变量

每个类只有一个static修饰的静态字段，也叫类字段

这个类的所有实例都将共享这个字段，就算没有实例化对象，静态字段也存在，静态字段属于类，不属于任何单个对象。

#### 静态常量

同时用final和static修饰，可以通过类名直接访问

```java
class Math{
    public static final double PI = 3.14159265358...;
}
double a = Math.PI;
```

#### 静态方法

通过static修饰方法

可以直接通过类名调用

静态方法没有隐式参数，也无法使用this

#### 工厂方法

不是用构造器而使用静态构造方法构造对象

#### main方法

main方法不对任何对象进行操作

静态的main方法将执行并构造程序所需要的对象

### 方法参数

对于所有语言的函数参数，都有按值调用和按引用调用两种

java总是按值调用，也就是java方法参数都是副本

```java
int a = 0;
public void b(int v){
    v=1;
}
b(a);
//a=0
```

而对于对象的引用，变量名就是对对象的引用，所以参数和变量名引用同一个对象而参数仍然是变量的副本，只是指向同一个对象。

### 对象构造

#### 重载

多个方法相同名字，不同参数。

#### 默认字段初始化

构造器中没有给字段赋初值就会自动赋默认值

数值为0

布尔值为false

对象引用为null

#### 无参构造

如果没有构造器，会自动提供无参构造，将所有字段值赋默认值

如果有无参构造和有参构造就应用重载

如果有有参构造没有无参构造，那么构造对象时如果不提供数据就是不合法的

#### 参数名细节：

参数名会遮蔽字段名,这时候字段需要this来引用

```java
class a{
    pravite int b;
    public a(int b){
        this.b=b;
    }
}
```

#### 调用另一个构造器

this(...)可以调用另一个构造器

```java
class a{
    pravite int b;
    public a(){
        this(5);
    }
    public a(int b){
        this.b=b;
    }
}
```

#### 初始化块

```java
class a{
    pravite int b;
    {
        b=0;
    }
    public a(){
        b=0;
    }
    public a(int b){
        this.b=b;
    }
}
```

无论调用哪个构造器，都会首先运行初始化块如何才能运行构造器主体部分

#### 调用构造器具体处理步骤

1、如果构造器第一行调用了另一个构造器，则根据提供的参数执行第二个构造器

2、否则，所有字段初始化为默认值，执行所有字段初始化方法和初始化块

3、执行构造主题

### 包

#### 导包细节：

1、通过*一次只能导入一个包，import	java.time.*/*

2、一旦使用import在使用类时就不必写出全名

3、导入两个包中有相同类名会出现类名冲突

```java
import java.util.*;
import java.sql.*;

    Date a;
//两个包中都有Date类
```

解决方法

```java
//1、增加特定import语句
import java.util.Date;
//2、完整类名
java.util.Date a;
```

4、在文件开头package表明包名

#### 静态导入

可以导入类的静态方法和静态字段

```java
import static java.lang.System.*;

out.println();
```

#### 类路径

类文件可以储存在jar(java归档)文件中，用zip格式组织文件和子目录。可以使用任何zip工具查看jar文件

为了使一个类能够被多个程序共享，需要做到下面几点：

1、把类文件放到一个目录中，该目录为基目录

2、jar文件放在一个目录中

3、设置类路径，类路是所有包含类文件的路径的集合

UNIX环境中，类路径中各项之间用(:)分隔

/类路径:.:/XXX.jar

java6开始可以在jar文件目录中使用通配符/XXX/*

windows环境中，以(;)分隔

\类路径;.;\XXX.jar

都以(.)表示当前目录

4、类路径包括

基目录

当前目录(.)；

jar文件



##### 细节：

编译器编译时考虑类的import，在类路径所有位置上搜索，查看源文件是否比类文件新，是就更新。

虚拟机

查看javaAPI，扫描类路径

#### 设置类路径(wating)

使用-classpath(-cp或者java9中的--class-path)指定类路径

```java
java -classpath C:\XXX.jar;.;C:/haha MyMain
```

### JAR文件(java归档文件)

使用了zip格式压缩的包含大量类文件的目录结构

#### 创建jar文件

使用jar工具制作JAR文件，在jdk中工具位于jdk/bin下

```java
jar cvf jarFileName file1 file2 ...
```

通常，jar命令格式为

```java
jar options files1 files2 ...
```

#### jar程序选项(waiting)



#### 清单文件

除了类文件和静态资源以外，每个jar文件还包含一个清单文件，用于描述归档文件的特殊特性

位于文件夹MEAT-INF中，命名为MANIFES.MF

```xml
Manifest-Version: 1.0
Created-By: 17.0.1 (Oracle Corporation)
```

想要在打jar包时生成定制的清单文件需要一个希望加入的MF文本文件然后运行

```xml
jar cfm jarFileName manifestFile ...
//将文本文件处理为MF文件
jar cfm Myjar.jar jarFileName.mf *.class
//创建一个包含清单文件的jar文件
jar ufm Myjar.jar jarFileName.mf
//更新一个jar文件中的清单
```

#### 可执行jar文件

可以使用jar命令中的e选项指定程序入口，即此处有main方法

##### 方法一：

```xml
jar cvfe Myjar.jar com.MainClass Files ...
```



##### 方法二：

在清单文件中指定Main-Class:com.MainClass

注意不要写拓展名.class

##### 执行

```java
java -jar Myjar.jar
```

#### 多版本jar文件(java9引入)(waiting)

为了保证版本兼容，类文件按版本放在META-INF/versions目录中

要增加不同版本类文件，可以使用--release标志

jar uf Myjar.jar --release 9 Application.class

#### 命令行选项(waiting)



### 文档注释

jdk包含一个javadoc工具，可以由源文件生成一个HTML文档

javadoc 实用工具从下面几项中抽取信息:

```yaml
模块
包
公共类与接口
公共的和受保护的字段
公共的方法和受保护的构造器和方法
```

可以为以上各个特性编写注释，注释放置在所描述特性的前面，以/**开始，

*/结束。

每个/**

...

*/

文档注释包含标记以及之后紧跟着的自由格式文本，标记以@开始

#### 类注释

放在import之前

#### 方法注释

在方法之前

标记

| 标记    | 作用                 |
| ------- | -------------------- |
| @param  | 给参数添加一个条目   |
| @return | 给返回值添加一个条目 |
| @throws | 给异常添加一个条目   |



#### 字段注释

在字段之前

#### 通用注释

| 标记     | 作用               |
| -------- | ------------------ |
| @since   | 给添加一个条目     |
| @author  | 给作者添加一个条目 |
| @version | 给版本添加一个条目 |

#### 包注释

##### 方法一

提供一个package-info.java的java文件,其中必须包含一个/**

*/的注释，后面是一个package语句，不能更多

##### 方法二

提供一个名为package.html的HTML文件，会抽取<body></body>之间所有文本

#### 注释抽取

```java
javadoc -d 目标文件夹 package1 package2...
```

## 第三章

### 类,超类,子类

#### 覆盖方法

##### super关键字

不是对象的引用，而是指示编辑器调用超类方法的特殊关键字

##### 细节:

在覆盖一个方法时子类方法不能低于超类方法可见性

#### 子类构造器

```java
public B(String a,int b){
    super(a,b);
}
//由于子类无法访问超类的字段，所以必须调用超类构造器来初始化超类的字段，
//构造器语句必须是子类构造器第一句语句
//如果子类没有超类构造器就会默认调用超类无参构造，如果没有就会报错
```

#### 多态

一个对象变量可以指定多种不同类型对象的现象

java中超类对象可以使用子类对象替换，编译器任将他当作超类对象看待

```java
public class A{
    public int a(){
        return 1;
    }
}
public class B extends A{
    public int b(){
        return 2;
    }
}

B i = new B();
A j = i;

//可以调用i.b();
//但是不能调用j.b();
//因为编译器将i当作A类的对象处理
```

##### 细节:

java中子类引用的数组可以转换成超类引用的数组而不需要强制转换符

```java
public class A{
    public int a(){
        return 1;
    }
}
public class B extends A{
    public int b(){
        return 2;
    }
}

B[] i = new B[10];
A[] j = i;

//这是合法的
//i和j引用的是同一个数组,令人震惊的是
//j[0]=new A();
//是合法的,而调用i[0].b();时会试图调用一个不存在的方法,造成混乱
//所以所有数组都要牢记创建时的元素类型
```

#### 理解方法调用

对于列如

```java
test.a(args);
```

1、编译器查看对象声明类型和方法名，一一列举所有该对象类型中的相同名字的重载方法以及超类中的同名可访问方法(pravite修饰不可访问)

至此编译器知晓所有可能被调用的候选方法

2、重载解析，选择重方法中与提供参数相匹配的方法，如果编译器没有找到匹配的方法或者经过类转型以后有多个方法匹配就会报错

至此编译器知晓需要调用的方法的名字和参数

3、如果是prative方法、static方法、final方法或构造器，那么编译器可以准确调用，这就是静态绑定。如果要调用的方法依赖于隐式参数实际类型，必须在运行时使用动态绑定。

4、每次调用都要从子类开始搜索到超类，时间开销大，所以虚拟机为每个类预先计算了一个方法表，其中列出了所有方法的签名和要调用的实际方法。

如果调用是super.XX();那么编译器会对隐式参数超类的方法表进行搜索

#### 阻止继承:final类和方法

对一个类添加fianl修饰符表明类不允许继承

其中的方法都会成为final而其中的字段不会

#### 强制类型转换

1、只能在继承层次内进行强制转换

2、在超类强制转换成子类之前，应当使用instanceof进行检查

```java
if(a instanceof B){
    b = (B)a;
}
```

#### 抽象类

包含一个或多个抽象方法的类本身必须被声明为抽象的

抽象类还可以包含字段和具体方法

拓展抽象类可以将子类中部分或所有抽象方法任未定义，那子类也就是抽象方法

也可以将子类中所有抽象方法定义，那就不需要声明为抽象类了，但是即使不包含抽象方法也可以声明为抽象类

抽象类不能实例化

可以定义抽象类的变量，但是只能引用非抽象子类的对象

#### 受保护访问

1、对本类可见 private

2、对外部完全可见 public

3、对本包和子类可见 protect

4、对本包可见 无

### Object—所有类的超类

没有明确指出超类，那么Object就被认为是这个类的超类

#### Object类型的变量

在java中只有基本类型不是对象

所有数组类型都拓展了Object类

#### equals方法

用于检测一个对象是否等于另一个对象

```java
//java.lang.Object
public boolean equals(Object obj) {
    return (this == obj);
}
```

一般来说，调用子类的equals方法时，优先调用超类的equals方法，如果超类的方法检测失败那对象不可能相等

#### 相等测试和继承

如果子类可以有自己的相等性概念，则强制使用getClass检测

如果由超类进行相等性检测，那就可以使用instanceof在不同子类对象间进行比较

#### hashCode(散列码)方法(不知道干啥用下次再看)

散列码是由对象导出的一个整型值，没有规律

Object默认的hashCode方法会从对象存储地址得出散列码

#### toString方法

绝大多数toString方法格式:类名+[字段值1名+字段1、字段值2+字段2、、、]

只要对象与一个字符串通过+连接起来，java编译器就会自动的调用toString方法来获得这个对象的字符串描述

### 泛型数组列表

ArrayList<CLass> 是一个有类型参数的泛型类，为了指定数组列表保存的元素对象的类型。

#### 声明数组列表

```java
ArrayList<String> a = new ArrayList<String>();
var a = new ArrayList<String>();//java10开始
ArrayList<String> a = new ArrayList<>();
//第三种使用菱形语法，编译器会根据方法，变量，参数的泛型类型将对应的泛型类型放在<>中
//var a = new ArrayList<>();会生成一个ArrayList<Object>();尽量不要使用
```

用add方法将元素加入数组列表

```java
a.add("haha");
```

如果内部引用数组已满，会自动扩容

```java
a.ensureCapacity(100);//确定初始数组大小为100
ArrayList<String> a = new ArrayList<String>(100);//确定初始数组大小为100
a.size();//返回数组列表中包含的实际元素个数
a.trimToSize();//调整存储块大小为保存当前元素所需要的存储空间，垃圾回收器将回收多余存储空间
```

#### 访问数组列表元素

设置第i个元素

```java
a.set(i,num);
```

得到第i个元素

```java
a.get(i);
```

插入一个元素

```java
a.add(i,num);//n以及之后的元素后移
```

删除一个元素

```java
a.remove(i);//删除一个元素，后方元素前移
```



##### 一举两得的方法

我们使用ArrayList的原因是它具有良好的扩容功能，而它对于数据访问并不友好，为此我们可以将它转换为数组

```java
String[] b = new String[a.size()];
a.toArray(b);
```

##### 细节

1、只有当数组列表的大小大于i时，才可以调用a.set(i,num)，要用add为数组添加新元素，set方法是用来替换数组中已经加入新的元素

### 对象包装和自动装箱

特点：所有基本类型都有其包装器，其包装器类不可变，一旦构造，不允许更改包装在其中的值，包装类是final的

#### 自动装箱

```java
Integer a;
a=3;
//3会被自动变换成Integer.valueOf(3);
//java.lang.Integer
```

#### 自动拆箱

```java
Integer a;
a=3;
int b = a;
//a会被自动变换成a.intValue();
```

#### 细节：

1、自动装箱规范要求Boolean，byte，char<=127，介于-128到127间的short和int被包装到固定的对象中

2、装箱拆箱是编译器要做的工作而不是虚拟机

3、不要想通过方法来修改Integer内部值，因为它不可变

### 参数数量可变的方法

```java
int sum(int... args){
    int count=0;
    for(int i:args)count+=i;
    return i;
}
```

实际上int... args传入了args[]数组

### 枚举类

```java
public enum Size{
    SMALL,
    BIG,
    FUCK
}
```

实际上这定义了一个类，这个类刚好有四个实例，所有这个类的对象都是这四个实例中一个，因此比较两个枚举类型的值时只需要==就可以

如果需要可以为枚举类添加构造器，方法和字段

```java
enum Size{
    BIG("B"), FUCK("F"), SMALL("S");
//每一个枚举值就是一个对象，所以会在构造枚举值的时候调用构造函数
    private String a;
    Size(String a){
        this.a = a;
    }
    String getA(){
        return a;
    }
}
```

枚举类构造器是私有的，如果不是private会报错

所有枚举类都是Enum的子类

#### toString的 逆方法--静态方法valueOf

```java
Size s = Enum.valueOf(Size.class,"SMALL");
//返回一个Size实例
```

#### values方法a

````java
Size[] values=Size.values;
//每个枚举类型都有的方法，返回全部枚举值的数组
````

#### ordinal方法

```java
Size.FUCK.ordinal();
//返回enum声明中枚举常量的位置，从0开始
```

### 反射--在程序中分析类

#### Class类

java运行时系统为所有对象维护一个运行时类型标识Class对象

##### 获取class对象的方法

1、Object类中的getClass()方法

```java
Integer a;
a.getClass();
```

2、Class.forName()

```java
Class.ForName("Integer");
//加载名为Integer的类
//如果输入是一个类名或者方法名，可以正常运行，不然报检查型异常
```

3、T.class

```java
int.class;
Integer.class;
```

##### Class类的方法

1、getName()

返回带包名的类名，由于历史遗留问题可能返回奇怪的字符串

2、使用==号比较

3、构造类实例

```java
Class a = Class.forName("B");
B b = a.getConstructor().newInstance();
//如果没有无参构造，getConstructor方法会抛出异常
```



#### 资源

java中与类关联的图像和声音文件或者包含消息字符串和按钮标签的文本文件称为资源

查找资源文件方法

1、获得拥有资源的Class对象

2、如果是一些图片之类，需要提供URL的资源，需要调用Class类的getResource("文件地址");方法

3、否则使用Class类的getResourceAsStream("文件地址");方法获取文件输入流

```java
java.lang.Class
    URL getResource(String name);
	InputStream getResourceAtStream(String name);
```

#### 利用反射分析类的能力(waiting)

```java
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Constructor;
import java.lang.Class;

Class a = Integer.class;

a.getFields();//返回这个类和其超类的公共字段
a.getDeclaredFields();//返回这个类(不含超类)所有字段
//如果没有字段或是基本类型，返回长度为0的数组

a.getMethod();//返回这个类和其超类的公共方法
a.getDeclaredFields();//返回这个类(不含超类)所有方法

a.getConstructors();//返回这个类和其超类的公共方法
a.getDeclaredConstructors();//返回这个类(不含超类)所有构造器

a.getPackageName();//返回类的包名，如果是数组类型返回元素的包名
//如果是基本类型返回java.lang

//Field，Method，Constructor上的方法
getDeclaringClass();//返回定义这个构造器，方法，字段的类
getExceptionTypes();//返回一个Class数组，里面有这个方法中所有抛出的的异常类型(只有Method，Constructor上的方法有)
getModifiers();//返回前修饰符，public之类的，用Modifier中的方法来分析这个返回值
getName();//返回名字
getParameterTypes();//返回一个Class数组，里面有这个方法的参数(只有Method，Constructor上的方法有)
getReturnTypes();//返回这个方法的返回类型
```



#### 使用反射在运行时分析对象(waiting)

```java
import java.lang.reflect.AccessibleObject;
setAccessible(boolean flag);//设置或取消这个可访问对象的可访问标志，如果拒绝则抛出一个IllegalAccessException异常
trySetAccessible(boolean flag);//设置或取消这个可访问对象的可访问标志，如果拒绝则返回false
isAccessible()//得到这个可访问对象的可访问标志
static setAccessible(AccessibleObject[] array,boolean flag);
//静态方法，设置一个对象数组的可访问标志

get(Object a);//返回a对象中的该字段
set(Object a,Object value);//将a对象的该字段值设置为value

```

#### 使用反射编写泛型数组代码(比较复杂用到再说)

#### 调用任意方法和构造器(比较复杂用到再说)

## 第四章

### 接口

```java
interface Arrays{
    public void sort();//承诺可以对对象数组进行排序
    void fuck();//接口中所有方法都是自动是public，所有可以省略public
    
}
```

#### 细节:

1、接口没有实例字段，在java8之前接口没有实现的方法，接口可以有定义常量

2、在实现类时由于接口方法默认public所以实现类方法如果是protect就会报错

3、Arrays中的sort方法可以接受Object类型的数组，但是必须要求该数组元素实现Comparable接口

```java
    private static int countRunAndMakeAscending(Object[] a, int lo, int hi) {
        assert lo < hi;
        int runHi = lo + 1;
        if (runHi == hi)
            return 1;

        // Find end of run, and reverse range if descending
        if (((Comparable) a[runHi++]).compareTo(a[lo]) < 0) { // Descending
            while (runHi < hi && ((Comparable) a[runHi]).compareTo(a[runHi - 1]) < 0)
                runHi++;
            reverseRange(a, lo, runHi);
        } else {                              // Ascending
            while (runHi < hi && ((Comparable) a[runHi]).compareTo(a[runHi - 1]) >= 0)
                runHi++;
        }

        return runHi - lo;
    }
```

(Comparable) a[runHi++]//它没有限制而是会笨拙的进行强制转换，所以如果参数没有实现Comparable接口就会抛出异常

### 接口的属性

接口不是类但是可以有自己的变量。

```java
List a;
a = new ArrayList();
```

可以使用instanceof判断某个类是否实现了接口

```java
a instanceof List
```

接口可以被拓展，使用extends

接口中可以定义常量，其中定义的常量被自动定义成private static final

这些常量可以被实现它的类继承并使用

### 静态和私有方法

在java8中允许接口在增加静态方法，可以减少一些伴随类

java9中可以使用私有方法

### 默认方法

可以为接口方法提供一个默认实现

可以提供接口演化和默认方法

```java
public interface A{
    default int pr(){
        System.out.println("A");
    }
}
```

