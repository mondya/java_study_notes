# 		  	Java基础

```java
public class Welcome {
    public static void main(String[] args){
        System.out.println("欢迎来到Java的世界");
    }
}
```

## 注意

1. Java对大小写敏感，如果出现大小写的错误，则代码无法运行。
2. 关键字public被称作访问修饰符，用于控制程序的其他部分对这段代码的访问级别。
3. 关键词class的意思是类
4. 一个源文件中至多只能声明一个public的类，其它类的个数不限，如果源文件中包含一个public类，源文件名必须和其中定义的public的类名相同，且以“.java”为扩展名。
5. 一个源文件可以包含多个类class。
6. 正确编译后的源文件，会得到相应的字节码文件，编译器为每个类生成独立的字节码文件，且将字节码文件自动命名为类的名字且以".class”为扩展名。
7. main方法是Java应用程序的入口方法，它有固定的书写格式︰public static void main(String[] args) {...}



## 一些DOS命令

![image-20201026204009775](D:.\images\image-20201026204009775.png)



## 标识符

- 标识符必须以<font color=red>字母，下划线_，美元符号$开头。</font>
- 标识符其他部分可以是字母，下划线_，美元符号$和数字的任意组合。
- Java标识符大小写敏感，且长度无限制。
- 标识符不可以是关键字。

### 注意事项

·表示类名的标识符：每个单词的首字母大写

·表示方法和变量的标识符：第一个单词小写，从第二个单词开始首字母大写，“驼峰原则”

·注意：<font color=red>可以使用汉字，但是不建议使用汉字</font>

```java
public class TestIdentifer {
    public static void main(String[] args) {
      int a123 = 1;
  //      int 123abc = 2;           //错误
        int $a = 3;
        int _abc = 4;
//        int #abc = 5;         //错误
        int 年龄 = 18;

        //int class = 4;    //关键字不能作为标识符
    }
}

```

## 变量的分类:

从整体上可以分为局部变量，成员变量和静态变量

|   类型   |      声明位置      |   从属于    |
| :------: | :----------------: | :---------: |
| 局部变量 |  方法或语句块内部  | 方法/语句块 |
| 成员变量 |  类内部，方法外部  |    对象     |
| 静态变量 | 类内部，static修饰 |     类      |

成员变量自动初始化该类型的默认初始值

| 数据类型 |  初始值  |
| :------: | :------: |
|   int    |    0     |
|   char   | '\u0000' |
| boolean  |  false   |
|  double  |   0.0    |



# 数据类型和运算符

### 整型变量/常量

| 类型  | 占用存储空间 |                        表数范围                        |
| :---: | :----------: | :----------------------------------------------------: |
| byte  |    1字节     |              $-2^{7}$~$2^7$-1 (-128~127)               |
| short |    2字节     |         $-2^{15}$~$2^{15}$-1 （-32768~32767）          |
|  int  |    4字节     | $-2^{31}$ ~  $2^{31}$-1 (-2147483648~2147483647)约21亿 |
| long  |    8字节     |                $-2^{63}$ ~  $2^{63}$-1                 |



<font color=red>Java语言的整型常数默认为int型，声明long型常量可以后加‘ l ’或‘ L ’</font>

```java
long a = 55555555;  //编译成功，在int表示的范围内(21亿内)。
long b = 55555555555;//不加L编译错误，已经超过int表示的范围。
```



### 浮点型变量/常量

float类型又被称作单精度类型，尾数可以精确到7位有效数字，在很多情况下，float类型的精度很难满足需求。而double表示这种类型的数值精度约是float类型的两倍，又被称作双精度类型，绝大部分应用程序都采用double类型。浮点型常量默认类型也是double。

 float类型的数值有一个后缀F或者f ，没有后缀F/f的浮点数值默认为double类型。也可以在浮点数值后添加后缀D或者d， 以明确其为double类型。

<font color=red>浮点类型float，double的数据不适合在不容许舍入误差的金融计算领域。如果需要进行不产生舍入误差的精确数字计算，需要使用BigDecimal类。</font>

默认为double类型

```java
        float f = 0.1f;
        double d = 1.0/10;
        System.out.println(f==d);//结果为false
```

```java
	float d1 = 423432423f;
	float d2 = d1+1;
	if(d1==d2){
  		 System.out.println("d1==d2");//输出结果为d1==d2 float在运算时会用科学计数法表示，此时d1变为4.23432423e8,float保留7位有效数字，所以相等
	}else{
   		 System.out.println("d1!=d2");
	}
```

使用BigDecimal进行浮点数的比较

```java
import java.math.BigDecimal;
public class Main {
    public static void main(String[] args) {
        BigDecimal bd = BigDecimal.valueOf(1.0);
        bd = bd.subtract(BigDecimal.valueOf(0.1));
        bd = bd.subtract(BigDecimal.valueOf(0.1));
        bd = bd.subtract(BigDecimal.valueOf(0.1));
        bd = bd.subtract(BigDecimal.valueOf(0.1));
        bd = bd.subtract(BigDecimal.valueOf(0.1));
        System.out.println(bd);//0.5
        System.out.println(1.0 - 0.1 - 0.1 - 0.1 - 0.1 - 0.1);//0.5000000000000001
    }
}
```

### boolean类型变量/常量

boolean类型有两个常量值，true和false，在内存中占一位（不是一个字节）不可以用`if(1)`，不可以使用 0 或非 0 的整数替代 true 和 false ，这点和C语言不同。 boolean 类型用来判断逻辑条件，一般用于程序流程控制 。

```java
        boolean man = false;
        if(man){
            System.out.println(man);
        }			//不会打印出结果
```

## 算术运算符

### 整数运算：

1.如果两个操作数有一个为Long,则结果也为long

2.没有long时，结果为int.即使操作数全为short,byte，结果也是int。

### 浮点运算：

3.如果两个操作数有一个为double，则结果为double

4.只有两个操作数都是float,则结果才为float

### 取模运算：

5.其操作数可以为浮点数，一般使用整数，结果是余数，余数符号和左边操作数符号相同，如：7%3=1，-7%3=-1，7%-3=1。

### 拓展运算符：

```java
int a=3;
int b=4;
a+=b;//相当于a=a+b;
System.out.println("a="+a+"\nb="+b);
a=3;
a*=b+3;//相当于a=a*(b+3)
System.out.println("a="+a+"\nb="+b);
```

### 逻辑运算符：

|  运算符  |      | 说明                                 |
| :------: | ---- | ------------------------------------ |
|  逻辑与  | &    | 两个操作为true，结果才为true         |
|  逻辑或  | \|   | 两个操作数有一个为true，结果就是true |
|  短路与  | &&   | 只要一个为false，则直接返回false     |
|  短路或  | \|\| | 只要一个为true，则直接返回true       |
|  逻辑非  | !    | 取反                                 |
| 逻辑异或 | ^    | 相同为false,不同为true               |

短路与和逻辑与

```java
//1>2的结果为false，那么整个表达式的结果即为false，将不再计算2>(3/0)
boolean c = 1>2 && 2>(3/0);
System.out.println(c);
//1>2的结果为false，那么整个表达式的结果即为false，还要计算2>(3/0)，0不能做除数，//会输出异常信息
boolean d = 1>2 & 2>(3/0);
System.out.println(d);
```

### 位运算符：

1. &和|既是逻辑运算符，也是位运算符。如果两侧操作数都是boolean类型，就作为逻辑运算符。如果两侧的操作数是整数类型，就是位运算符。
2. 不要把“^”当做数学运算“乘方”，是“位的异或”操作。

左移运算和右移运算

```java
int a = 3*2*2;
int b = 3<<2; //相当于：3*2*2;左移一位相当于乘以2
int c = 12/2/2;
int d = 12>>2; //相当于12/2/2;右移一位相当于除以2
```

### 字符串连接符：

“+”运算符两侧的操作数中只要有一个是字符串(String)类型，系统会自动将另一个操作数转换为字符串然后再进行连接。

### 条件运算符：

```java
x?y:z
```

其中 x 为 boolean 类型表达式，先计算 x 的值，若为true，则整个运算的结果为表达式 y 的值，否则整个运算结果为表达式 z 的值。

```java
        int score = 80; 
        int x = -100;
        String type =score<60?"不及格":"及格"; 
        int flag = x > 0 ? 1 : (x == 0 ? 0 : -1);
        System.out.println("type= " + type);
        System.out.println("flag= "+ flag);//type=及格，flag=-1
```

## 基本类型转化

 自动类型转换指的是容量小的数据类型可以自动转换为容量大的数据类型。如图，实线表示无数据丢失的自动类型转化，虚线表示在转换时可能会有精度的损失。

![1.png](D:\images\1494906265693111.png)

### 常见问题

```java
int money = 1000000000; //10亿
int years = 20;
int total = money*years;
System.out.println("total="+total);	//返回的total是负数，超过了int的范围
long total1 = money*years;
System.out.println("total1="+total1);//返回的total仍然是负数。默认是int，因此结果会转成int值，再转成long。但是已经发生了数据丢失 
long total2 = money*((long)years); 
System.out.println("total2="+total2);//返回的total2正确:先将一个因子变成long，整个表达式发生提升。全部用long来计算。
```

自动类型转化问题：

```java
long a = 3.14; // 3.14为double类型，不能进行转化
```

## 简单的键盘输入和输出

```java
import java.util.Scanner;

/**
 * 获得键盘的输入
 * @author 熊环环
 */
public class TestScanner {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("请输入你的名字:\n");
        String name = scanner.nextLine();

        System.out.println("请输入你的爱好:\n");
        String favor= scanner.nextLine();

        System.out.println("请输入你的年龄:\n");
        int age = scanner.nextInt();

        System.out.println("*******************************");
        System.out.println(name);
        System.out.println(favor);
        System.out.println(age);
    }
}

```

# 控制语句

### switch多选择结构

```java
public class TestSwitch {
    public static void main(String[] args){
        char c1 = 'a';
        char c2 = (char)(c1+(int)26*Math.random());
        System.out.println(c2+":");
        switch(c2){
            case 'a':
            case 'e':
            case 'i':
            case 'o':
            case 'u':
                System.out.println("元音");
                break;
            case 'y':
            case 'w':
                System.out.println("半元音");
                break;
            default:
                System.out.println("辅音");
        }
    }
}
```

### 嵌套循环

```java
public class TestQianTao {
    public static void main(String[] args){
//        for(int i=0;i<5;i++){
//            for(int j=0;j<5;j++){
//                System.out.print(i+1+"  ");
//            }
//            System.out.println();
//        }
        //九九乘法表
        for(int i=1;i<=9;i++){
            for(int j=1;j<=i;j++){
                System.out.print(j+"*"+i+"="+(i*j)+"\t");
            }
            System.out.println();
        }
    }
}
//在使用时要注意println()和print()
```

逗号运算符

```java
public class Test11 {
    public static void main(String[] args) { 
        for(int i = 1, j = i + 10; i < 5; i++, j = i * 2) {
            System.out.println("i= " + i + " j= " + j); 
        } 
    }
}
//按照顺序执行，先执行i=1;j=11;在判断，进入语句，输出i=1;j=11;在i++=2;j=4
```

### 两个题目

```java
public class TestWhile {
    public static void main(String[] args){
//        //用while循环分别计算100以内的奇数和偶数的和
//        int i = 0;
//        int j = 0;
//        int sum = 0;
//        while(i<101){
//            if(i%2==0){
//                j=j+i;
//            }
//            else{
//                sum=sum+i;
//            }
//            i++;
//        }
//        System.out.println("偶数和为："+j);
//        System.out.println("奇数和为："+sum);
        //用while循环输出1000以内能被5整除的数，每行五个
        int i=1;
        int h = 0;
        while(i<1001){
            if(i%5==0){
                System.out.print(i+"\t");
                h++;
            }
            if(h==5){
                System.out.println();
                h=0;
            }
            i++;
           /* if(i%25==0){
                System.out.println();
            }
            i++;
            */  
        }
    }
}
```

### break语句和continue语句

break用于强行退出循环，不执行循环中剩余的语句；continue用在循环语句体中，用于终止某次循环过程，即跳过循环体中尚未执行的语句，接着进行下一次是否执行循环的判定。

带标签的break和continue

```java
public class Test18 {
    public static void main(String args[]) {
        outer: for (int i = 101; i < 150; i++) {
            for (int j = 2; j < i / 2; j++) {	//如果不使用标签，则continue只会跳到这里
                if (i % j == 0){
                    continue outer;
                }
            }
            System.out.print(i + "  ");
        }
    }
}
```



## 方法

方法就是一段用来完成特定功能的代码片段，类似于其它语言的函数。

方法声明格式

```java
[修饰符1	修饰符2	...]	返回值类型	方法名（形式参数）{
    Java语句;... ...
}
```

方法的调用方式：

   对象名.方法名(实参列表)

   方法的详细说明:

1. 形式参数：在方法声明时用于接收外界传入的数据。

  2. 实参：调用方法时实际传给方法的数据。

  3. 返回值：方法在执行完毕后返还给调用它的环境的数据。

  4. 返回值类型：事先约定的返回值的数据类型，如无返回值，必须显示指定为为void。

```java
/**
 * @author 熊环环
 * @date 2020/10/28 23:34
 */
public class TestMethod {
    public static void main(String[] args){
//        TestMethod td = new TestMethod();
        int sum = add(10,20,30);
        //td.add(30, 40,50);
    }

    public static int add(int a,int b,int c){//使用static不用new对象
        int sum = a+b+c;
        System.out.println(sum);
        return sum;
    }
}
```

### 方法的重载(overload)

方法的重载是指一个类中可以定义多个方法名相同，但参数不同的方法。 调用时，会根据不同的参数自动匹配对应的方法。

<font color=red>重载的方法，实际上是完全不同的方法，只是名称相同而已。</font>

构成方法重载的条件：1.不同的含义：形参类型，形参个数，形参顺序不同

​								  2.只有返回值不同不构成方法重载

```java
int a(String str){} 和 void a(String str){}
```

​								  3.只有形参的名称不同，不构成方法的重载

```java
int a(String str){} 和 int a(String s){}
```

```java
/**
 * 测试方法的重载
 * @author xhh
 * @date 2020/10/29 15:09
 */
public class TestOverload {
    public static void main(String[] args){

    }

    public static int add(int a,int b,int c){
        int sum = a+b+c;
        return sum;
    }

    //方法名相同，形参个数不同
    public static int add(int a,int b){
        int sum = a+b;
        return sum;
    }
    //方法名相同，形参类型不同
    public static double add(double a,int b){
        double sum = a+b;
        return sum;
    }
    //方法名相同，形参顺序不同
    public static double add(int a,double b){
        double sum = a+b;
        return sum;
    }
    //方法名相同，返回值不同，不构成重载
 /*   public static double add(int a,int b,int c){
        int sum = a+b+c;
        return sum;
    }
  */
    //参数名称不同，不构成重载
   /* public static int add(int c,int b,int a){
        int sum = a+b+c;
        return sum;
    }    
    */

}

```

## 递归结构

递归是一种常见的解决问题的方法，即把问题逐渐简单化。递归的基本思想就是“自己调用自己”，一个使用递归技术的方法将会直接或者间接的调用自己。

```java
    public static long factorial(int n) {
        if (n == 1) {//递归头
            return 1;
        } else {    //递归体
            return n * factorial(n - 1);
        }
    }
```

### 递归的缺陷

简单的程序是递归的优点之一。但是递归调用会占用大量的系统堆栈，内存耗用多，<font color=red>在递归调用层次多时速度要比循环慢的多</font>，所以在使用递归时要慎重。任何能用递归解决的问题也能使用迭代解决（循环语句）。当递归方法可以更加自然地反映问题，并且易于理解和调试，并且不强调效率问题时，可以采用递归;在要求高性能的情况下尽量避免使用递归，递归调用既花时间又耗内存。

# JAVA面对对象基础

面对对象和面对过程都是对软件分析、设计和开发的一种思想，它指导人们以不同的方式去分析、设计和开发软件。面对过程思考问题时，我们首先考虑“怎么按步骤实现？”并将步骤对应成方法，一步一步完成。这个适合简单任务，不需要过多协作。举例，我们在开车时，开车的步骤是这样的：1.发动车 2.挂挡 3.踩油门 4.汽车移动 <font color=red>面向过程适合简单，不需要协作的程序。</font>

但是面对“如何造车？”的问题，使用面对过程的思想是很难完成的（即不能完整列出步骤），此时应该思考“车是由哪几个部分组成的？如何设计?”面对对象思考问题，发现造车需要轮胎，发动机，车壳，座椅，挡风玻璃这些<font color=red>对象</font>。为了便于协作，我们找轮胎厂完成制造轮胎的步骤，发动机厂完成制造发动机的步骤;这样，发现大家可以同时进行车的制造，最终进行组装，大大提高了效率。但是，具体到轮胎厂的一个流水线操作，仍然是有步骤的，还是<font color=red>离不开面向过程思想!</font>	

<font color=red>宏观上使用面向对象把握，微观处理上仍然是面向过程</font>

### 对象和类

1.对象是具体的事物;类是对对象的抽象;

2.类可以看成一类对象的模板，对象可以看成该类的一个具体实例。

3.类是用于描述同一类型的对象的一个抽象概念，类中定义了这一类对象所应具有的共同的属性、方法。

### 类的定义

```java
// 每一个源文件必须有且只有一个public class，并且类名和文件名保持一致！
public class Car { 
}
class Tyre { // 一个Java文件可以同时定义多个class
}
class Engine {
}
class Seat {
}
```

一个典型类的定义

```java
//模拟学生使用电脑
public class Stu{
    int id;
    String name;
    int age;
    Computer comp;//创建一个对象
    
    void Study(){
        System.out.println("我正在使用电脑学习，品牌是"+comp.brand);
    }
    public static void main(String[] args){
        Stu stu = new Stu();//构造方法
        stu.name  = "张三";
        Computer computer  = new Computer();
        computer.brand = "华硕";
        
        comp.brand = computer;//computer属于实例，把里面的所有参数传给comp实例
        stu.Study();
    }
}

class Computer{
    String brand;
}
```

### 面对对象的内存分析

JAVA虚拟机的内存可以分为三个区域：栈stack,堆heap，方法区method area。

![未命名文件](D:\images\未命名文件.png)

#### 栈的特点：

​				1.栈描述的是方法执行的内存模型。每个方法被调用都会创建一个栈帧（存储局部变量，操作数，方法出口等）。

​				2.JVM为每个线程创建一个栈，用于存放该线程执行方法的信息（实际参数，局部变量等）。

​				3.栈属于线程私有，不能实行线程之间的共享。

​				4.栈的存储特性是“先进后出，后进先出”。

​				5.栈由系统自动分配，速度快。栈是一个连续的存储空间。

#### 堆的特点：

​				1.堆用于存储创建好的对象和数组。

​				2.JVM只有一个堆，被所有线程共享。

​				3.堆是一个不连续的存储空间，分配灵活，速度慢。

#### 方法区特点：

​				1. JVM只有一个方法区，被所有线程共享! 

​                2.方法区实际也是堆，只是用于存储类、常量相关的信息!

​                3.用来存放程序中永远是不变或唯一的内容。(类信息【Class对象】、静态变量、字符串常量等)。

### 构造方法

构造器也叫构造方法（constructor），用于对象的初始化。

要点：1.通过new关键字调用

​		  2.构造器虽然有返回值，但是不能定义返回值类型(返回值的类型肯定是本类)，不能在构造器里使用return返回某个值。

​          3.如果我们没有定义构造器，则编译器会自动定义一个无参的构造函数。如果已定义则编译器不会自动添加!

​		  4.构造器的方法名必须和类名一致!

### 垃圾回收机制

>1.发现无用的对象
>
>2.回收无用对象占用的内存空间

### this关键字                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                               

1. 在程序中产生二义性之处，应使用this来指明当前对象;普通方法中，this总是指向调用该方法的对象。构造方法中，this总是指向正要初始化的对象。

2. 使用this关键字调用重载的构造方法，避免相同的初始化代码。但只能在构造方法中用，并且必须位于构造方法的第一句。

3. this不能用于static方法中。

```java
public class TestThis {
    int a, b, c;
 
    TestThis() {
        System.out.println("正要初始化一个Hello对象");
    }
    TestThis(int a, int b) {
        // TestThis(); //这样是无法调用构造方法的！
        this(); // 调用无参的构造方法，并且必须位于第一行！
        a = a;// 这里都是指的局部变量而不是成员变量
// 这样就区分了成员变量和局部变量. 这种情况占了this使用情况大多数！
        this.a = a;
        this.b = b;
    }
    TestThis(int a, int b, int c) {
        this(a, b); // 调用带参的构造方法，并且必须位于第一行！
        this.c = c;
    }
 
    void sing() {
    }
    void eat() {
        this.sing(); // 调用本类中的sing();
        System.out.println("你妈妈喊你回家吃饭！");
    }
 
    public static void main(String[] args) {
        TestThis hi = new TestThis(2, 3);
        hi.eat();
    }
}
```



### static关键字

　　1. 为该类的公用变量，属于类，被该类的所有实例共享，在类被载入时被显式初始化。

　　2. 对于该类的所有对象来说，static成员变量只有一份。被该类的所有对象共享!!

　　3. 一般用“类名.类属性/方法”来调用。(也可以通过对象引用或类名(不需要实例化)访问静态成员。)

　　4. 在static方法中不可直接访问非static的成员。

==static修饰的成员变量和方法，从属于类==

 ==普通变量和方法从属于对象的==

### 静态初始化块

静态初始化块执行顺序：

　　1. 上溯到Object类，先执行Object的静态初始化块，再向下执行子类的静态初始化块，直到我们的类的静态初始化块为止。

　　2. 构造方法执行顺序和上面顺序一样!!

```java
public class User3 {
    int id;        //id
    String name;   //账户名
    String pwd;   //密码
    static String company; //公司名称
    static {
        System.out.println("执行类的初始化工作");
        company = "北京尚学堂";
        printCompany();
    }  
    public static void printCompany(){
        System.out.println(company);
    }  
    public static void main(String[] args) {
        User3  u3 = new User3();
    }
}
```



### 参数传值机制

Java中，方法中所有参数都是“值传递”，也就是“传递的是值的副本”，但是引用类型指的是“对象的地址”。副本和原参数都指向了同一个“地址”，改变“副本指向地址对象的值，也意味着原参数指向对象的值也发生了改变”。

### 静态导入

静态导入(static import)作用是用于导入指定类的静态属性，这样可以直接使用静态属性。

### 调用this和super的注意点

> 1）调用super()必须写在子类构造方法的第一行，否则编译不通过。每个子类构造方法的第一条语句，都是隐含地调用super()，如果父类没有这种形式的构造函数，那么在编译的时候就会报错。
>
> 2）super()和this()类似,区别是，super从子类中调用父类的构造方法，this()在同一类内调用其它方法。
> 3）super()和this()均需放在构造方法内第一行。
> 4）尽管可以用this调用一个构造器，但却不能调用两个。
> 5）this和super不能同时出现在一个构造函数里面，因为this必然会调用其它的构造函数，其它的构造函数必然也会有super语句的存在，所以在同一个构造函数里面有相同的语句，就失去了语句的意义，编译器也不会通过。
> 6）this()和super()都指的是对象，所以，均不可以在static环境中使用。包括：static变量,static方法，static语句块。
>
> 7）从本质上讲，this是一个指向本对象的指针, 然而super是一个Java关键字。

# JAVA面对对象进阶

## 继承

==JAVA只有单继承，没有像C++那样的多继承，多继承会引起混乱==

==JAVA中类没有多继承，接口有多继承==

==子类继承父类的属性和方法（除了父类的构造方法），但不见得能直接访问（比如父类私有的属性和方法），通过继承父类的get,set方法可以做到访问父类私有属性和方法==

==如果定义一个类，没有使用extends，则他的父类是java.lang.Object==

## 方法的重写

**子类通过重写父类的方法**

## super关键字

- super是直接父类对象的引用，可以通过super来访问父类中被子类覆盖的方法或属性

- 使用super调用普通方法，语句没有位置限制，可以在子类中随便调用。

-  若是构造方法的第一行代码没有显式的调用super(...)或者this(...);那么Java默认都会调用super(),含义是调用父类的无参数构造方法。这里的super()可以省略。

```java
public class TestSuper01 { 
    public static void main(String[] args) {
        new ChildClass().f();
    }
}
class FatherClass {
    public int value;
    public void f(){
        value = 100;
        System.out.println ("FatherClass.value="+value);
    }
}
class ChildClass extends FatherClass {
    public int value;
    public void f() {
        super.f();  //调用父类对象的普通方法
        value = 200;
        System.out.println("ChildClass.value="+value);
        System.out.println(value);
        System.out.println(super.value); //调用父类对象的成员变量
    }
}
```



## 继承树追溯

**·属性/方法查找顺序：(比如：查找变量h)**

1. 查找当前类中有没有属性h

2. 依次上溯每个父类，查看每个父类中是否有h，直到Object

3. 如果没找到，则出现编译错误。

  4. 上面步骤，只要找到h变量，则这个过程终止。

**·构造方法调用顺序：**

   ==构造方法第一句总是：super(…)来调用父类对应的构造方法。==所以，流程就是：先向上追溯到Object，然后再依次向下执行类的初始化块和构造方法，直到当前子类为止。

```java
public class TestSuper02 { 
    public static void main(String[] args) {
        System.out.println("开始创建一个ChildClass对象......");
        new ChildClass();
    }
}
class FatherClass {
    public FatherClass() {
        System.out.println("创建FatherClass");
    }
}
class ChildClass extends FatherClass {
    public ChildClass() {
        //super(); 默认调用父类构造器
        System.out.println("创建ChildClass");
    }
}

返回的结果是：
开始创建一个ChildClass对象......
    创建FatherClass
    创建ChildClass
```

## 封装

1. 提高代码的安全性。

2. 提高代码的复用性。

  3. “高内聚”：封装细节，便于修改内部代码，提高可维护性。

  4. “低耦合”：简化外部调用，便于调用者使用，便于扩展和协作。

> 访问控制符

![图1.png](https://www.sxt.cn/360shop/Public/admin/UEditor/20170522/1495417749528447.png)

1. **private 表示私有，只有自己类能访问**

2. **default表示没有修饰符修饰，只有同一个包的类能访问**

3. **protected表示可以被同一个包的类以及其他包中的子类访问**

4. **public表示可以被该项目的所有包中的所有类访问**

## 多态

**多态指的是同一个方法调用，由于对象不同可能会有不同的行为。**

多态的要点：

1. 多态是方法的多态，不是属性的多态(多态与属性无关)。

2. 多态的存在要有3个必要条件：**继承，方法重写，父类引用指向子类对象**。

3. 父类引用指向子类对象后，用该父类引用调用子类重写的方法，此时多态就出现了。

```java
class Animal {
    public void shout() {
        System.out.println("叫了一声！");
    }
}
class Dog extends Animal {
    public void shout() {
        System.out.println("旺旺旺！");
    }
    public void seeDoor() {
        System.out.println("看门中....");
    }
}
class Cat extends Animal {
    public void shout() {
        System.out.println("喵喵喵喵！");
    }
}
public class TestPolym {
    public static void main(String[] args) {
        Animal a1 = new Cat(); // 向上可以自动转型
        //传的具体是哪一个类就调用哪一个类的方法。大大提高了程序的可扩展性。
        animalCry(a1);
        Animal a2 = new Dog();
        animalCry(a2);//a2为编译类型，Dog对象才是运行时类型。
         
        //编写程序时，如果想调用运行时类型的方法，只能进行强制类型转换。
        // 否则通不过编译器的检查。
        Dog dog = (Dog)a2;//向下需要强制类型转换
        dog.seeDoor();
    }
 
    // 有了多态，只需要让增加的这个类继承Animal类就可以了。
    static void animalCry(Animal a) {
        a.shout();
    }
 
    /* 如果没有多态，我们这里需要写很多重载的方法。
     * 每增加一种动物，就需要重载一种动物的喊叫方法。非常麻烦。
    static void animalCry(Dog d) {
        d.shout();
    }
    static void animalCry(Cat c) {
        c.shout();
    }*/
}
```

## 对象的转型

父类引用指向子类对象，我们称这个过程为向上转型，属于自动类型转换

## final关键字

1. 修饰变量: 被他修饰的变量不可改变。一旦赋了初值，就不能被重新赋值
2. 修饰方法：该方法不可被子类重写。但是可以被重载!
3. 修饰类: 修饰的类不能被继承。比如：Math、String等

## 抽象方法和抽象类

**抽象方法**：==使用abstract修饰的方法，没有方法体，只有声明。定义的是一种“规范”，就是告诉子类必须要给抽象方法提供具体的实现。==

**抽象类：**==包含抽象方法的类就是抽象类。通过abstract方法定义规范，然后要求子类必须定义具体实现。通过抽象类，我们就可以做到严格限制子类的设计，使子类之间更加通用。==



抽象类的使用要点：

1. ==有抽象方法的类只能定义成抽象类==

2. ==抽象类不能实例化，即不能用new来实例化抽象类==。

3. ==抽象类可以包含属性、方法、构造方法。但是构造方法不能用来new实例，只能用来被子类调用。==

4. ==抽象类只能用来被继承。==

5. ==抽象方法必须被子类实现。==

## 接口

**比抽象类还抽象的抽象类**

> 声明格式

```java
[访问修饰符] interface 接口名 [extends 父接口1，父接口2]{
    常量定义；
        方法定义；
}
```



**定义接口：**

1.访问修饰符：只能是public 或 默认

2.接口名：和类名采用相同命名机制

3.extends:接口可以多继承

4.常量：接口中的属性只能是常量，总是:`public static final `修饰，不写也是

5.接口中的方法只能是:`public abstract `省略也是

**要点：**

1.接口使用implements实现

2.接口不能创建实例，但是可用于声明引用变量类型

3.一个类实现了接口，必须实现接口中所有的方法，并且这些方法只能是public的

4.JDK1.7之前，接口中只能包含静态常量、抽象方法，不能有普通属性、构造方法、普通方法

5.JDK1.8后，接口中包含普通的静态方法

```java
public class TestInterface {
    public static void main(String[] args) {
        Volant volant = new Angel();
        volant.fly();
        System.out.println(Volant.FLY_HIGHT);
         
        Honest honest = new GoodMan();
        honest.helpOther();
    }
}
/**飞行接口*/
interface Volant { 
    int FLY_HIGHT = 100;  // 总是：public static final类型的；
    void fly();   //总是：public abstract void fly();
}
/**善良接口*/
interface Honest { 
    void helpOther();
}
/**Angle类实现飞行接口和善良接口*/
class Angel implements Volant, Honest{
    public void fly() {
        System.out.println("我是天使，飞起来啦！");
    }
    public void helpOther() {
        System.out.println("扶老奶奶过马路！");
    }
}
class GoodMan implements Honest {
   public void helpOther() {
        System.out.println("扶老奶奶过马路！");
    }  
}
class BirdMan implements Volant {
    public void fly() {
        System.out.println("我是鸟人，正在飞！");
    }
}
```



## 接口的多继承

```java
interface A {
    void testa();
}
interface B {
    void testb();
}
/**接口可以多继承：接口C继承接口A和B*/
interface C extends A, B {
    void testc();
}
public class Test implements C {
    public void testc() {
    }
    public void testa() {
    }
    public void testb() {
    }3
}
```

## 内部类

 一般情况，我们把类定义成独立的单元。有些情况下，我们把一个类放在另一个类的内部定义，称为内部类(innerclasses)。

 在Java中内部类主要分为成员内部类(非静态内部类、静态内部类)、匿名内部类、局部内部类。

**.成员内部类(可以使用private、default、protected、public任意进行修饰。 类文件：外部类$内部类.class)**

**a) 非静态内部类(外部类里使用非静态内部类和平时使用其他类没什么不同)**

   i. 非静态内部类必须寄存在一个外部类对象里。因此，如果有一个非静态内部类对象那么一定存在对应的外部类对象。非静态内部类对象单独属于外部类的某个对象。

   ii. 非静态内部类可以直接访问外部类的成员，但是外部类不能直接访问非静态内部类成员。

   iii. 非静态内部类不能有静态方法、静态属性和静态初始化块。

   iv. 外部类的静态方法、静态代码块不能访问非静态内部类，包括不能使用非静态内部类定义变量、创建实例。

   **v. 成员变量访问要点：**

1. 内部类里方法的局部变量：变量名。

2. 内部类属性：this.变量名。

3. .外部类属性：外部类名.this.变量名。

## String类常用方法

| charAt()                  | 返回下标...的值                                              |
| ------------------------- | ------------------------------------------------------------ |
| equals                    | 比较两个字符串是否相等                                       |
| equalsIgnoreCase          | 比较两个字符串是否相等(忽略大小写)                           |
| indexOf                   | 从开头查找是否包含字符串，如果包含返回第一个字符的下标，不包含返回-1 |
| lastIndexOf               | 从末尾查找是否包含字符串，如果包含返回第一个字符的下标，不包含返回-1 |
| length                    | 返回字符串长度                                               |
| replace('' '','' '')      | 返回一个新字符串，把一个字符替换成另一个字符                 |
| startsWith("string pri")  | 如果字符串以pri开头，则返回true                              |
| endsWith("string pri")    | 如果字符串以pri结尾，则返回true                              |
| substring(int beginIndex) | 返回一个新字符串，从指定下标到串尾                           |
| toLowerCase()             | 返回一个新字符串，把字符串的所有大写字母改为小写字母         |
| toUpperCase()             | 返回一个新字符串，把字符串的所有小写改为大写                 |
| trim()                    | 返回一个新字符串，删除了原字符串串头尾的空格                 |

```java
package com.xhh.test;

/**
 * 测试String类常用方法
 * @author xhh
 * @date 2020/11/20 15:41
 */
public class TestString {
    public static void main(String[] args) {
        String s1 = "core java";
        String s2 = "Core java";
        System.out.println(s1.charAt(5));  //提取下标为5的字符      .charAt()
        System.out.println(s1.length());

        System.out.println(s1.equals(s2));//false
        System.out.println(s1.equalsIgnoreCase(s2));//true

        System.out.println(s1.indexOf("ored"));//-1 开头查找
        System.out.println(s1.lastIndexOf(" java"));//末尾查找

        System.out.println(s1.replace("core","php is the best computer laguage"));
        System.out.println(s1.replace("c","d"));

        System.out.println(s1.substring(4));
        System.out.println(s1.substring(4,6));  //[4,6)

        System.out.println(s1.toUpperCase());
    }
}

```



# 数组

数组是==相同类型数据==的==有序==集合。

数组的三个基本特点：

1.长度是确定的。数组一旦被创建，它的大小就是不可以改变的

2.其元素必须是相同类型，不允许出现混合类型

3.数组类型可以是任何数据类型，包括基本类型和引用类型	

## 数组的三种初始化方式

```java
package com.xhh.array;

/**
 * 数组的三种初始化方式
 * @author xhh
 * @date 2020/11/19 17:36
 */
public class Test02 {
    public static void main(String[] args) {
        //数组静态初始化
        int[] a = {2,3,4};
        User[] b = {new User(1001,"张三"),new User(1002,"李四")};
        
        //默认初始化
        int[] c = new int[3];  //默认给数组的元素进行赋值，成员变量
        
        //动态初始化
        int[] d = new int[4];
        d[0] = 1;
        d[2] = 3;
    }
}

```

## for-each循环

1. for-each增强for循环在遍历数组过程中不能修改数组中某元素的值。

2. for-each仅适用于遍历，不涉及有关索引(下标)的操作。

```java
public class Test {
    public static void main(String[] args) {
        String[] ss = { "aa", "bbb", "ccc", "ddd" };
        for (String temp : ss) {
            System.out.println(temp);
        }
    }
}
```

## 数组的拷贝

```java
package com.xhh.test2;

/**
 * 数组的拷贝
 * @author xhh
 * @date 2020/11/20 16:39
 */
public class TestArrayCopy {
    public static void main(String[] args) {
        /*
        String[] a1 = {"aa","bb","cc","dd","ee"};
        String[] a2 = new String[10];
        System.arraycopy(a1,2,a2,6,3);//把a1拷贝给a2，从a1的下标2开始，拷贝到a2的下标6，拷贝长度为3

        for(int i =0;i<a2.length;i++){
            System.out.println(i+"----------"+a2[i]);
        }
        */
        String[] str = {"阿里","淘宝","京东","天猫","支付宝"};
        removeElement(str,2);
        for(int i=0;i<str.length;i++){
            System.out.println(str[i]);
        }
    }


    //测试从数组中删除某个元素（本质上还是数组的拷贝）
  /*  public static void testBasicCopy(){
        String[] s1 = {"aa","bb","cc","dd","ee"};

        System.arraycopy(s1,3,s1,2,2);

        for(int i = 0;i<s1.length;i++){
            if(i==(s1.length-1)){
                s1[i] = null;
            }else {
                System.out.println(s1[i]);
            }
        }
    }
   */
  //index需要删除的第几个位置
  public static String[] removeElement(String[] str,int index){
      System.arraycopy(str,index,str,index-1,str.length-index);
      str[str.length-1] = null;
      return str;
  }
}
```

## 数组的扩容

```java
    public static String[] extendRange(String[] str){
      String[] str2 = new String[str.length+10];
      System.arraycopy(str,0,str2,0,str.length);
      
      for(String temp:str2){
          System.out.println(temp);
      }
      
      return str2;
    }
```

## Arrays工具类

```java
import java.util.Arrays;
public class Test {
    public static void main(String args[]) {
        int[] a = {1,2,323,23,543,12,59};
        System.out.println(Arrays.toString(a));
        Arrays.sort(a);//排序，从大到小
        System.out.println(Arrays.toString(a));
    }
}
```

## 多维数组

```java
package com.xhh.test2;
import java.util.Arrays;

/**
 * @author xhh
 * @date 2020/11/21 10:39
 */
public class Test2DimengsionArray {
    public static void main(String[] args) {
        int[][] a = new int[3][];

        int []c[] = new int[3][];

        a[0] = new int[]{20,30};
        a[1] = new int[]{22,33,44,5};

        //静态初始化
        int[][] b = {{11,22},{22,33},{44,55,66}};

        Object[] temp1 = {1000,"张三",18,"1999-02-10"};
        Object[] temp2 = {1001,"张四",18,"1999-02-10"};
        Object[] temp3 = {1002,"张五",18,"1999-02-10"};

        Object[][] objects = new Object[3][];
        objects[0] = temp1;
        objects[1] = temp2;
        objects[2] = temp3;
        for (Object[] temp:objects){
            System.out.println(Arrays.toString(temp));
        }
    }
}

```

## 冒泡排序

```java
package com.xhh.test2;

import java.util.Arrays;

/**
 * 冒泡排序
 * @author xhh
 * @date 2020/11/21 11:10
 */
public class TestBubbleSort {
    public static void main(String[] args) {
        int[] value = {2,4,5,6,12,3,7,5,9,3};

        int[] value2 = BubbleSort(value);
        for(int temp:value){
            System.out.print(temp+"   ");
        }
        //按从小到大排序
    }

    public static int[] BubbleSort(int[] value){
//        for(int i=value.length-1;i>0;i--){
//            if(value[i]<value[i-1]){
//                int t = value[i-1];
//                value[i-1] = value[i];
//                value[i] = t;
//            }else{
//                i--;
//            }
//        }
        for(int j=0;j<value.length-1;j++){
            boolean flag=true;
            for(int i=value.length-1;i>0;i--){
                if(value[i]<value[i-1]){
                    int t = value[i-1];
                    value[i-1] = value[i];
                    value[i] = t;
                    flag = false;
                }
            }
           if(flag){
               break;
           }
        }
        return value;
    }
}

```

# 常用类

## 包装类

> 包装类的用途

1. 作为和基本数据类型对应的类型存在，方便涉及到对象的操作，如Object[]、集合等的操作。

2. 包含每种基本数据类型的相关属性如最大值、最小值等，以及相关的操作方法(这些操作方法的作用是在基本数据类型、包装类对象、字符串之间提供相互之间的转化!)。

```java
public class Test {
    /** 测试Integer的用法，其他包装类与Integer类似 */
    void testInteger() {
        // 基本类型转化成Integer对象
        Integer int1 = new Integer(10);
        Integer int2 = Integer.valueOf(20); // 官方推荐这种写法
        // Integer对象转化成int
        int a = int1.intValue();
        // 字符串转化成Integer对象
        Integer int3 = Integer.parseInt("334");
        Integer int4 = new Integer("999");
        // Integer对象转化成字符串
        String str1 = int3.toString();
        // 一些常见int类型相关的常量
        System.out.println("int能表示的最大整数：" + Integer.MAX_VALUE); 
    }
    public static void main(String[] args) {
        Test test  = new Test();
        test.testInteger();
    }
}
```



> 自动装箱和自动拆箱

```java
Integer i = 100;//自动装箱
//相当于编译器自动为您作以下的语法编译：
Integer i = Integer.valueOf(100);//调用的是valueOf(100)，而不是new Integer(100)
```

```java
Integer i = 100;
int j = i;//自动拆箱
//相当于编译器自动为您作以下的语法编译：
int j = i.intValue();
```

## String类

> String 类对象代表不可变的Unicode字符序列，因此我们可以将String对象称为“不可变对象”

```java
public class TestString1 {
    public static void main(String[] args) {
        String s1 = new String("abcdef");
        String s2 = s1.substring(2, 4);
        // 打印：ab199863
        System.out.println(Integer.toHexString(s1.hashCode()));
        // 打印：c61, 显然s1和s2不是同一个对象
        System.out.println(Integer.toHexString(s2.hashCode()));
    }
}
```

> 字符串常量拼接时优化

```java
public class TestString2 {
    public static void main(String[] args) {
        //编译器做了优化,直接在编译的时候将字符串进行拼接
        String str1 = "hello" + " java";//相当于str1 = "hello java";
        String str2 = "hello java";
        System.out.println(str1 == str2);//true
        String str3 = "hello";
        String str4 = " java";
        //编译的时候不知道变量中存储的是什么,所以没办法在编译的时候优化
        String str5 = str3 + str4;
        System.out.println(str2 == str5);//false
    }
}
```

> String类常用方法

1. String类的下述方法能创建并返回一个新的String对象: concat()、 replace()、substring()、 toLowerCase()、 toUpperCase()、trim()。

  2. 提供查找功能的有关方法: endsWith()、 startsWith()、 indexOf()、lastIndexOf()。

3. 提供比较功能的方法: equals()、equalsIgnoreCase()、compareTo()。

  4. 其它方法: charAt() 、length()。

### StringButter和StringBuilder

>  StringBuffer和StringBuilder非常类似，均代表可变的字符序列。 这两个类都是抽象类AbstractStringBuilder的子类，方法几乎一模一样

```java
public class TestStringBuilder {
    public static void main(String[] args) {
        String str;

        //StringBulider线程不安全，效率高（一般使用它）；StringBuffer线程安全，但是效率低
        StringBuilder sb= new StringBuilder("abcdefg");
        System.out.println(Integer.toHexString(sb.hashCode()));
        System.out.println(sb);

        sb.setCharAt(2,'T');
        System.out.println(Integer.toHexString(sb.hashCode()));
        System.out.println(sb);
        
        
        StringBuilder sb = new StringBuilder();

        for(int i=0;i<26;i++){
            char temp = (char)('a'+i);
            sb (temp);
        }
        System.out.println(sb);
        sb.reverse();//倒叙输出
        System.out.println(sb);
        sb.setCharAt(0,'熊');//替换某个字符
        System.out.println(sb);
        sb.insert(0,'哦').insert(1,'啊').insert(2,'嗯');//链式调用
        System.out.println(sb);
    }
}
```

> 方法总结：append()字符串拼接；reverse()倒叙输出;setCharAt()替换字符;insert()插入字符

## 时间处理相关类

### DateFormat类和SimpleDateFormat

> 把时间对象转化成指定格式的字符串。反之，把指定格式的字符串转化成时间对象
>
> DateFormat是一个抽象类，一般使用它的的子类SimpleDateFormat类来实现。

```java
public class TestDateFormat {
    public static void main(String[] args) throws ParseException {
        //把时间对象按照"格式字符串指定的格式" 转成相应的字符串
        DateFormat df = new SimpleDateFormat("yyyy年MM月dd日 hh:mm:ss");
        String str = df.format(new Date(4000000));
        System.out.println(str);
        //把字符串按照"格式字符串指定的格式"转成相应的时间
        DateFormat df2 = new SimpleDateFormat("yyyy年MM月dd日 hh:mm:ss");
        Date date = df2.parse("2020年11月21日 17:54:55");
        System.out.println(date);
    }
} 
```

### Calendar日历类

Calendar 类是一个抽象类，为我们提供了关于日期计算的相关功能，比如：年、月、日、时、分、秒的展示和计算。GregorianCalendar 是 Calendar 的一个具体子类，提供了世界上大多数国家/地区使用的标准日历系统。

```java
        //获得日期相关元素
        Calendar calendar = new GregorianCalendar(2020,5,21,19,47,50);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        System.out.println(year);
        System.out.println(month);
        System.out.println(day);

        //设置日期的相关元素
        GregorianCalendar c2 = new GregorianCalendar();
        c2.set(Calendar.YEAR,2030);
        System.out.println(c2);

        //日期的计算
        GregorianCalendar c3 = new GregorianCalendar();
        c3.add(Calendar.YEAR,-100);
        System.out.println(c3);

        //日期对象和时间对象的转化
        Date d4 = c3.getTime();
        GregorianCalendar c4 = new GregorianCalendar();
        c4.setTime(new Date());
```

## Math类

> math类常用方法：

1. abs 绝对值

2. acos,asin,atan,cos,sin,tan 三角函数

3. sqrt 平方根

4. pow(double a, double b) a的b次幂

5. max(double a, double b) 取大值

6. min(double a, double b) 取小值

7. ceil(double a) 大于a的最小整数

8. floor(double a) 小于a的最大整数

9. random() 返回 0.0 到 1.0 的随机数

10. long round(double a) double型的数据a转换为long型(四舍五入)

11. toDegrees(double angrad) 弧度->角度

12. toRadians(double angdeg) 角度->弧度

```java
public class TestMath {
    public static void main(String[] args) {
        //取整相关操作
        System.out.println(Math.ceil(3.2));
        System.out.println(Math.floor(3.2));
        System.out.println(Math.round(3.2));
        System.out.println(Math.round(3.8));
        //绝对值、开方、a的b次幂等操作
        System.out.println(Math.abs(-45));
        System.out.println(Math.sqrt(64));
        System.out.println(Math.pow(5, 2));
        System.out.println(Math.pow(2, 5));
        //Math类中常用的常量
        System.out.println(Math.PI);
        System.out.println(Math.E);
        //随机数
        System.out.println(Math.random());// [0,1)
    }
}
```

> Random常用方法

```java
import java.util.Random;
public class TestRandom {
    public static void main(String[] args) {
        Random rand = new Random();
        //随机生成[0,1)之间的double类型的数据
        System.out.println(rand.nextDouble());
        //随机生成int类型允许范围之内的整型数据
        System.out.println(rand.nextInt());
        //随机生成[0,1)之间的float类型的数据
        System.out.println(rand.nextFloat());
        //随机生成false或者true
        System.out.println(rand.nextBoolean());
        //随机生成[0,10)之间的int类型的数据
        System.out.print(rand.nextInt(10));
        //随机生成[20,30)之间的int类型的数据
        System.out.print(20 + rand.nextInt(10));
        //随机生成[20,30)之间的int类型的数据（此种方法计算较为复杂）
        System.out.print(20 + (int) (rand.nextDouble() * 10));
    }
}
```

## File类

 java.io.File类：代表文件和目录。 在开发中，读取文件、生成文件、删除文件、修改文件的属性时经常会用到本类

```java
       File f = new File("d:/a.txt");
        //File f = new File("d:\\a.txt");
        System.out.println(f);

        f.renameTo(new File("d:/bb.txt"));//修改文件名
        System.out.println(System.getProperty("user.dir"));

        File f2 = new File("gg.txt");
        f2.  createNewFile();

        File f3 = new File("d:/b.txt");
        System.out.println("File是否存在："+f3.exists());
        System.out.println("File是否是目录："+f3.isDirectory());
        System.out.println("File是否是文件："+f3.isFile());
        System.out.println("File最后修改时间："+new Date(f3.lastModified()));
        System.out.println("File的大小："+f3.length());
        System.out.println("File的文件名："+f3.getName());
        System.out.println("File的目录路径："+f3.getPath());
```

## 枚举

```java
/*enum  枚举名 {
      枚举体（常量列表）
}
*/
import java.util.Random;
public class TestEnum {
    public static void main(String[] args) {
        // 枚举遍历
        for (Week k : Week.values()) {
            System.out.println(k);
        }
        // switch语句中使用枚举
        int a = new Random().nextInt(4); // 生成0，1，2，3的随机数
        switch (Season.values()[a]) {
        case SPRING:
            System.out.println("春天");
            break;
        case SUMMER:
            System.out.println("夏天");
            break;
        case AUTUMN:
            System.out.println("秋天");
            break;
        case WINDTER:
            System.out.println("冬天");
            break;
        }
    }
}
/**季节*/
enum Season {
    SPRING, SUMMER, AUTUMN, WINDTER
}
/**星期*/
enum Week {
    星期一, 星期二, 星期三, 星期四, 星期五, 星期六, 星期日
}
```