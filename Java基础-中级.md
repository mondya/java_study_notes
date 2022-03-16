# 异常

==异常机制本质：当程序出现错误，程序安全退出的程序。==

**Java是采用面向对象的方式来处理异常的。处理过程：**

1. **抛出异常：**在执行一个方法时，如果发生异常，则这个方法生成代表该异常的一个对象，停止当前执行路径，并把异常对象提交给JRE。

2. **捕获异常：**JRE得到该异常后，寻找相应的代码来处理该异常。JRE在方法的调用栈中查找，从生成异常的方法开始回溯，直到找到相应的异常处理代码为止。

## 异常分类

Java对异常进行了分类，不同类型的异常分别用不同的Java类表示，所有异常的根类为java.lang.Throwable，Throwable下面又派生了两个子类：Error和Exception。

![图6-2 Java异常类层次结构图.png](.\images\1495272017528669.png)

### Error

Error是程序无法处理的错误，表示运行应用程序中较严重问题。大多数错误与代码编写者执行的操作无关，而表示代码运行时 JVM(Java 虚拟机)出现的问题。例如，Java虚拟机运行错误(Virtual MachineError)，当 JVM 不再有继续执行操作所需的内存资源时，将出现 OutOfMemoryError。这些异常发生时，Java虚拟机(JVM)一般会选择线程终止。

Error表明系统JVM已经处于不可恢复的崩溃状态中。我们不需要管它。

### Exception

Exception是程序本身能够处理的异常，如：空指针异常(NullPointerException)、数组下标越界异常(ArrayIndexOutOfBoundsException)、类型转换异常(ClassCastException)、算术异常(ArithmeticException)等。

Exception类是所有异常类的父类，其子类对应了各种各样可能出现的异常事件。 通常Java的异常可分为：

1. RuntimeException 运行时异常

2. CheckedException 已检查异常

#### RuntimeException

派生于RuntimeException的异常，如被 0 除、数组下标越界、空指针等，其产生比较频繁，处理麻烦，如果显式的声明或捕获将会对程序可读性和运行效率影响很大。 因此由系统自动检测并将它们交给缺省的异常处理程序(用户可不必对其处理)。

这类异常通常是由编程错误导致的，所以在编写程序时，并不要求必须使用异常处理机制来处理这类异常,经常需要通过增加“逻辑处理来避免这些异常”。

#### CheckedException

所有不是RuntimeException的异常，统称为Checked Exception，又被称为“已检查异常”，如IOException、SQLException等以及用户自定义的Exception异常。 这类异常在编译时就必须做出处理，否则无法通过编译

# 处理异常方式

## 捕获异常（try-catch-finally）

捕获异常是通过3个关键词来实现的：try-catch-finally。用try来执行一段程序，如果出现异常，系统抛出一个异常，可以通过它的类型来捕捉(catch)并处理它，最后一步是通过finally语句为异常处理提供一个统一的出口，finally所指定的代码都要被执行(catch语句可有多条;finally语句最多只能有一条，根据自己的需要可有可无)

```java
        FileReader reader = null;
        try {
            reader = new FileReader("d:/a.txt");
            char c1 = (char)reader.read();
            System.out.println(c1);
        } catch (FileNotFoundException e) {//子类在前
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if(reader!=null){
                    reader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
```

## 声明异常

当CheckedException产生时，不一定立刻处理它，可以再把异常throws出去。

在方法中使用try-catch-finally是由这个方法来处理异常。但是在一些情况下，当前方法并不需要处理发生的异常，而是向上传递给调用它的方法处理。

如果一个方法中可能产生某种异常，但是并不能确定如何处理这种异常，则应根据异常规范在方法的首部声明该方法可能抛出的异常。

如果一个方法抛出多个已检查异常，就必须在方法的首部列出所有的异常，之间以逗号隔开。

# 自定义异常

   1.在程序中，可能会遇到JDK提供的任何标准异常类都无法充分描述清楚我们想要表达的问题，这种情况下可以创建自己的异常类，即自定义异常类。

   2.自定义异常类只需从Exception类或者它的子类派生一个子类即可。

   3.自定义异常类如果继承Exception类，则为受检查异常，必须对其进行处理;如果不想处理，可以让自定义异常类继承运行时异常RuntimeException类。

   4.习惯上，自定义异常类应该包含2个构造器：一个是默认的构造器，另一个是带有详细信息的构造器。

> 自定义异常类

```java
/**IllegalAgeException：非法年龄异常，继承Exception类*/
class IllegalAgeException extends Exception {
    //默认构造器
    public IllegalAgeException() {
     
    }
    //带有详细信息的构造器，信息存储在message中
    public IllegalAgeException(String message) {
        super(message);
    }
}
```

> 自定义异常类的使用

```java
class Person {
    private String name;
    private int age;
 
    public void setName(String name) {
        this.name = name;
    }
 
    public void setAge(int age) throws IllegalAgeException {
        if (age < 0) {
            throw new IllegalAgeException("人的年龄不应该为负数");
        }
        this.age = age;
    }
 
    public String toString() {
        return "name is " + name + " and age is " + age;
    }
}
 
public class TestMyException {
    public static void main(String[] args) {
        Person p = new Person();
        try {
            p.setName("Lincoln");
            p.setAge(-1);
        } catch (IllegalAgeException e) {
            e.printStackTrace();
            System.exit(-1);
        }
        System.out.println(p);
    }
}
```

# 容器

 数组的优势：是一种简单的线性序列，可以快速地访问数组元素，效率高。如果从效率和类型检查的角度讲，数组是最好的。

   数组的劣势：不灵活。容量需要事先定义好，不能随着需求的变化而扩容。比如：我们在一个用户管理系统中，要把今天注册的所有用户取出来，那么这样的用户有多少个?我们在写程序时是无法确定的。因此，在这里就不能使用数组。

   基于数组并不能满足我们对于“管理和组织数据的需求”，所以我们需要一种更强大、更灵活、容量随时可扩的容器来装载我们的对象。

   泛型是JDK1.5以后增加的，它可以帮助我们建立类型安全的集合。在使用了泛型的集合中，遍历时不必进行强制类型转换。JDK提供了支持泛型的编译器，将运行时的类型检查提前到了编译时执行，提高了代码可读性和安全性。

   ==泛型==的本质就是“数据类型的参数化”。 我们可以把“泛型”理解为数据类型的一个占位符(形式参数)，即告诉编译器，在调用泛型时必须传入实际类型。

![图9-1容器的接口层次结构图.png](D:\images\1495613220648265.png)

## Collection接口

> Collection 表示一组对象，它是集中、收集的意思。Collection接口的两个子接口是List、Set接口

![表9-1 Collection接口中定义的方法.png](D:\images\1495614959696503.png)

### list接口

 **List是有序、可重复的容器**

>  **有序**:List中每个元素都有索引标记。可以根据元素的索引标记(在List中的位置)访问元素，从而精确控制这些元素

> **可重复**:List允许加入重复的元素。更确切地讲，List通常允许满足 e1.equals(e2) 的元素重复加入容器

==list接口常用的实现类有：ArrayList,  LinkedList  和  Vector==

除了Collection接口中的方法，List多了一些跟顺序有关的方法：

![表9-2 List接口中定义的方法.png](D:\images\1495616109914665.png)

==ArrayList底层是用数组实现的存储。特点：查询效率高，增删效率低，线程不安全。（一般使用它）==

==LinkedList底层用双向链表实现的存储。特点：查询效率低，增删效率高，线程不安全==

==Vector底层是用数组实现的List，相关的方法都加了同步检查，因此“线程安全,效率低”==    

### set接口

**Set容器特点：无序、不可重复**。无序指Set中的元素没有索引，我们只能遍历查找;不可重复指不允许加入重复的元素       

HashSet是采用哈希算法实现，底层实际是用HashMap实现的(HashSet本质就是一个简化版的HashMap)，因此，查询效率和增删效率都比较高

TreeSet底层实际是用TreeMap实现的，内部维持了一个简化版的TreeMap，通过key来存储Set的元素。 TreeSet内部需要对存储的元素进行排序，因此，我们对应的类需要实现Comparable接口。这样，才能根据compareTo()方法比较对象之间的大小，才能进行内部排序                      

## Map接口

Map就是用来存储“键(key)-值(value) 对”的。 Map类中存储的“键值对”通过键来标识，所以“键对象”不能重复。

Map 接口的实现类有HashMap、TreeMap、HashTable、Properties等。

> 常用的方法

![表9-3 Map接口中常用的方法.png](D:\images\1495617463792119.png)

### HashMap

HashMap采用哈希算法实现，是Map接口最常用的实现类。 由于底层采用了哈希表存储数据，我们要求键不能重复，如果发生重复，新的键值对会替换旧的键值对。 HashMap在查找、删除、修改方面都有非常高的效率

HashTable类和HashMap用法几乎一样，底层实现几乎一样，只不过HashTable的方法添加了synchronized关键字确保线程同步检查，效率较低

> HashMap和HashTable的区别

==HashMap线程不安全，效率高.允许key或value为null==
==HashTable线程安全，效率低。不允许key或value为null==

> HashMap底层原理

HashMap底层实现采用了Hash表（数据结构）。

数据结构中由数组和链表来存储数据，但是他们都各有优缺点.

> 数组：占用空间连续。查询效率高，增删效率低

> 链表：占用不连续空间。查询效率低，增删效率高

因此，结合了数组和链表优点的哈希表出现了。**其本质为“数组+链表”**

```java
public class HashMap<K,V>
    extends AbstractMap<K,V>
    implements Map<K,V>, Cloneable, Serializable
{

    /**
     * The default initial capacity - MUST be a power of two.
     */
    static final int DEFAULT_INITIAL_CAPACITY = 16;

    /**
     * The maximum capacity, used if a higher value is implicitly specified
     * by either of the constructors with arguments.
     * MUST be a power of two <= 1<<30.
     */
    static final int MAXIMUM_CAPACITY = 1 << 30;

    /**
     * The load factor used when none specified in constructor.
     */
    static final float DEFAULT_LOAD_FACTOR = 0.75f;

    /**
     * The table, resized as necessary. Length MUST Always be a power of two.
     */
    transient Entry<K,V>[] table;
```

从源码中可以看出，HashMap建立了Entry<K,V>[] table数组

```java
    static class Entry<K,V> implements Map.Entry<K,V> {
        final K key;
        V value;
        Entry<K,V> next;
        int hash;

        /**
         * Creates new entry.
         */
        Entry(int h, K k, V v, Entry<K,V> n) {
            value = v;
            next = n;
            key = k;
            hash = h;
        }
```

从源码分析，一个Entry对象存储了：

1.key：键对象 value:值对象

2.next:下一个结点

3.hash:键对象的hash值

![图9-14 Entry对象存储结构图.png](D:\images\1495619082593896.png)

![图9-15 Entry数组存储结构图.png](D:\images\1495619119905721.png)

当添加一个元素(key-value)时，首先计算key的hash值，以此确定插入数组中的位置，但是可能存在同一hash值的元素已经被放在数组同一位置了，这时就添加到同一hash值的元素的后面，他们在数组的同一位置，就形成了链表，同一个链表上的Hash值是相同的，所以说数组存放的是链表。 JDK8中，当链表长度大于8时，链表就转换为红黑树，这样又大大提高了查找的效率。

### HashMap扩容问题

HashMap的位桶数组，初始大小为16。实际使用时，显然大小是可变的。如果位桶数组中的元素达到(0.75*数组 length)， 就重新调整数组大小变为原来2倍大小。

### TreeMap

比较实现CompareTo接口

## Collections工具类

```java
public class Test {
    public static void main(String[] args) {
        List<String> aList = new ArrayList<String>();
        for (int i = 0; i < 5; i++){
            aList.add("a" + i);
        }
        System.out.println(aList);
        Collections.shuffle(aList); // 随机排列
        System.out.println(aList);
        Collections.reverse(aList); // 逆续
        System.out.println(aList);
        Collections.sort(aList); // 排序
        System.out.println(aList);
        System.out.println(Collections.binarySearch(aList, "a2")); 
        Collections.fill(aList, "hello");
        System.out.println(aList);
    }
}
```

# 多线程

**并发与并行**

==并发：指两个或多个事件在同一时间段内发生==

==并行：值两个或多个事件在同一时刻发生（同时发生）==

![image-20201124162503275](D:\images\image-20201124162503275.png)

**线程和进程**

- 进程：是指一个内存中运行的应用程序，每个进程都有一个独立的内存空间，一个应用程序可以同时运行多个进程；进程也是程序的一次执行过程，是系统运行程序的基本单位；系统运行一个程序即是一个进程从创建，运行到消亡的过程。
- 线程：线程是进程中的一个执行单元，负责当前进程中程序的执行，一个进程中至少有一个线程。一个进程中是可以有多个线程的，这个应用程序也可以称之为多线程程序。

![image-20201123180100519](D:\images\image-20201123180100519.png)



![image-20201123180010830](D:\images\image-20201123180010830.png)



## 创建多线程方式1

```java
package com.xhh.Thread2;
/**
 * 创建多线程程序的第一种方式：创建Thread类的子类
 * java.lang.Thread类是描述线程的类，我们想要实现多线程程序，就必须继承Thread类
 * 实现步骤：
 *  1.创建一个Thread类的子类
 *  2.在Thread类的子类中重写Thread中的run方法，设置线程任务
 *  3.创建Thread类中的start方法，开启新的线程，执行run方法
 *  void start()使该线程开始执行；Java虚拟机调用该线程的run方法
 *  结果是两个线程并发地运行；当前线程（main线程）和另一个线程（创建的新线程，执行其run方法）
 *  多次启动一个线程使非法的，特别是当线程已经结束执行后，不能再重新启动
 *  Java程序属于抢占式调度，哪个线程的优先级高，那个线程就优先执行；同一个优先级，随机一个执行
 */
/**
 * @author xhh
 * @date 2020/11/23 20:16
 */
public class Demo01Thread {
    public static void main(String[] args) {
        MyThread mt = new MyThread();

        mt.start();

        for(int i = 0;i<20;i++){
            System.out.println("run"+i);
        }
        //执行结果随机
    }
}

```

子类继承Thread类

```java
public class MyThread extends Thread {
        public void run(){
            for(int i = 0;i<20;i++){
                System.out.println("run"+i);
            }
        }
}

```



## 创建多线程方式2

```java
/**
 * 创建多线程的第二种方式：实现Runnable接口java.lang.Runnable
 * 实现步骤：
 *  1.创建一个Runnable接口的实现类
 *  2.在实现类中重写Runnable接口的run方法，设置线程任务
 *  3.创建一个Runnable接口的实现类对象
 *  4.创建Thread类对象，构造方法中传递Runnable接口的实现类对象
 *  5.调用Thread类中的start方法，开启新的形参run()方法
 */

/**
 * @author xhh
 * @date 2020/11/23 21:32
 */
public class Demo01Runnable {
    public static void main(String[] args) {
        RunnableImpl run = new RunnableImpl();
        Thread thread = new Thread(run);
        thread.start();

        for(int i = 0;i<20;i++){
            System.out.println(Thread.currentThread().getName()+i);
        }
    }
}
```

> 实现Runnable接口

```java
public class RunnableImpl implements Runnable {
    @Override
    public void run() {
        for(int i = 0;i<20;i++){
            System.out.println(Thread.currentThread().getName()+i);
        }
    }
}
```



## Thread类

thread类中定义了有关线程的方法：

**构造方法：**

- `public Thread()`：分配一个新的线程对象
- `public Thread(String name)`：分配一个指定名字的新的线程对象
- `public Thread(Runnable target)`:分配一个带有指定目标新的线程对象
- `public Thread(Runnable target,String name)`:分配一个带有指定目标的新的线程对象并指定名字

**常用方法：**

`public String getName()`:获取当前线程名称

`public void start()`：导致此线程开始执行；Java虚拟机调用此线程的run方法

`public void run()`:此线程要执行的任务在此处定义代码

`public static void sleep(long millis)`：使当前正在执行的线程以指定的毫秒数暂停（暂时停止执行）

`public static Thread currentThread()`:返回对当前正在执行的线程对象的引用

## Thread和Runnable的区别

如果一个类继承Thread，则不适合资源共享。但是如果实现了Runable接口，则很容易实现资源共享。

**总结：**

**实现Runnable接口比继承Thread类所具有的优势：**

1.适合多个相同的程序代码的线程去共享同一个资源

2.可以避免java中的单继承的局限性

3.增加程序的健壮性，实现解耦操作，代码可以被多个线程共享，代码和线程独立

4.线程池只能放入实现Runable或Callable类线程，不能直接放入继承Thread的类

> 扩充：在java中，每次程序运行至少启动两个线程，一个使main线程，一个使垃圾收集线程。因为每当使用java命令执行一个类的时候，实际上都会启动一个JVM，每一个JVM其实就是在操作系统中启动了一个进程

## 匿名内部类方式实现线程的创建

使用线程的内匿名内部类方式，可以方便的实现每个线程执行不同的线程任务操作

使用匿名内部类的方式实现Runnable接口，重写Runnable接口中的run方法

```java
package com.xhh.InnerClassThread;

/**
 * 匿名内部类方式实现线程的创建
 *
 * 匿名：没有名字
 * 内部类：写在其他类内部的类
 * 匿名内部类作用：简化代码
 *      把子类继承父类，重写父类的方法，创建子类对象合一步完成
 *      把实现类实现类接口，重写接口中的方法，创建实现类对象合一步完成
 * 最终：子类/实现类对象
 *
 * 格式：
 *      new 父类/接口（）{
 *          重写父类/接口中的方法
 *      }
 */

/**
 * @author xhh
 * @date 2020/11/23 23:28
 */
public class Demo01InnerClassThread {
    public static void main(String[] args) {
        //线程的父类是Thread
        new Thread(){
            @Override
            public void run() {
                for(int i =0;i<20;i++){
                    System.out.println(Thread.currentThread().getName()+i);
                }
            }
        }.start();

        //接口Runnable
        //Runnable r = new RunnableImpl();//多态
        Runnable r =new Runnable(){
            @Override
            public void run() {
                for(int i =0;i<20;i++){
                    System.out.println(Thread.currentThread().getName()+"hello world");
                }
            }
        };
        new Thread(r).start();
    }
}
```



## 线程安全

如果有多个线程在同时运行，而这些线程可能会同时运行这段代码。程序每次运行结果和单线程运行结果是一样的，而且其他的变量值也和预期一样，则线程是安全的。

> 安全问题案例

![image-20201124000704016](D:\images\image-20201124000704016.png)

> 安全问题分析

![image-20201124160822881](D:\images\image-20201124160822881.png)

## 线程同步

当我们使用多个线程访问同一资源，且多个线程中对资源有写的操作，就容易产生线程安全问题

要解决上述多线程并发访问一个资源的安全性问题，Java提供了同步机制（synchronized）来解决

### 同步代码块

- **同步代码块：**`synchronized`关键字可以用于方法中的某个区块中，表示只对这个区块的资源实行互斥访问

格式：

```javav
synchronized(同步锁){
	需要同步操作的代码
}
```

**同步锁：**

对象的同步锁只是一个概念，可以想象为在对像上标记了一个锁

​	1.锁对象可以是任意类型

​	2.多个线程对象，要使用同一把锁

> 注意：在任何时候，最多允许一个线程拥有同步锁，谁拿到锁就进入代码块，其他的线程只能在外面等待

```java
/**
 * 卖票案例出现了线程安全问题
 * 卖出了不存在的票和重复的票
 *
 * 解决线程安全问题的一种方法：使用同步代码块
 * 格式 
 *  synchronized(锁对象){
 *     代码块
 * }
 * 注意：1.通过代码块中的锁对象，可以使用任意的对象
 *      2.但是必须保证多个线程使用的锁对象是同一个
 *      3.锁对象作用：
 *              把同步代码块锁住，只让一个线程在同步代码块中执行
 *
 */    
public void run() {
        while (true){
        //同步代码块
        synchronized (obj){
            if(ticket>0){
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("正在卖第"+ticket+"张票");
                ticket--;
            }
        }
        }
    }
```

### 同步方法

> 同步方法使用的锁对象是实现类对象

```java
public synchronized void payTicket(){
      if(ticket>0){
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("正在卖第"+ticket+"张票");
                ticket--;
            }
        }
}
```

### 静态同步方法

> 静态同步方法的锁对象是本类的class属性

```java
public static /*synchronized*/ void payTicketStatic(){
    synchronized(RunnableImpl.class){
      if(ticket>0){
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("正在卖第"+ticket+"张票");
                ticket--;
            }
        }
} 
}
```

### Lock锁

```java
package com.xhh.SynchronizedLock;

/**
 * 解决线程问题的第三种方式：使用Lock锁
 * java.util.concurrent.locks.lock接口
 * Lock 实现提供了比使用synchronized方法和语句可获得的更广泛的锁定操作
 * Lock接口中的方法：
 *      void lock()获取锁
 *      void unlock()释放锁
 *      
 *      
 *      使用步骤：
 *      1.在成员位置创建一个ReentrantLock对象
 *      2.在可能会出现安全问题的代码前调用Lock接口中的方法lock获取锁
 *      3.在可能会出现安全问题的代码后调用Lock接口中的方法unlock释放锁
 * 
 */

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author xhh
 * @date 2020/11/24 15:31
 */
public class RunnableImpl implements Runnable{
    //1.在成员位置创建一个ReentrantLock对象
    Lock l = new ReentrantLock();
    

    private int ticket = 100;
    //设置线程任务：卖票
    @Override
    public void run() {
        while (true){
            l.lock();
            if(ticket>0){
                System.out.println("正在卖第"+ticket+"张票");
                ticket--;
            }
            l.unlock();
        }
    }
}
```

# 线程状态

### 线程状态概述

当线程被创建并启动之后，它既不是一启动就进入了执行状态，也不是一直处于执行状态。在API中`java.lang.Thread.State`这个枚举中给出了六种线程状态：

| 线程状态                  | 导致状态发生的条件                                           |
| ------------------------- | ------------------------------------------------------------ |
| NEW（新建）               | 线程刚被创建，但是并未启动。还没调用start方法                |
| Runnable(可运行)          | 线程可以在java虚拟机种运行的状态，可能正在运行自己的代码，也可能没有，则取决于操作系统处理器 |
| Blocked(锁阻塞)           | 当一个线程试图获取一个对象锁，而该对象锁被其他的线程持有，则该线程进入Blocked状态；当该线程持有锁时，该线程将变成Runnable状态 |
| Waiting(无限等待)         | 一个线程在等待另一个线程执行一个（唤醒）动作时，该线程进入Waiting状态。进入该状态后不能自动唤醒，必须等待另一个线程调用notify或者notifyAll方法才能够唤醒 |
| Timed Waiting（计时等待） | 同waiting状态，有几个方法有超时参数，调用他们将进入Timed Waiting状态。这一状态将一直保持到超时期满或者接收到唤醒通知。带有超时参数的常用方法有Thread.sleep,Object.wait |
| Terminated                | 已退出的线程处于这种状态                                     |

### 等待唤醒实例

```java
package com.xhh.WaitAndNotify;

/**
 * 等待唤醒案例：线程之间的通信
 *  创建一个顾客线程（消费者）：调用wait方法，放弃cpu的执行，进入到WAITING状态（无限等待）
 *  创建一个老板线程（生产者）：调用notify方法，唤醒消费者
 */

/**
 * @author xhh
 * @date 2020/11/24 23:45
 */
public class WaitAndNotify {
    public static void main(String[] args) {
        //创建锁对象
        final Object obj = new Object();

        //创建顾客线程
        new Thread(){
            @Override
            public void run() {
                synchronized (obj){
                    System.out.println("告知老板要的包子的种类和数量");
                    //调用wait方法，放弃cpu执行，进入到WAITING状态（无限等待）
                    try {
                        obj.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    //唤醒之后执行的代码
                    System.out.println("包子已经做好了，可以开吃了");
                }
            }
        }.start();

        //创建老板线程
        new Thread(){
            @Override
            public void run() {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                synchronized(obj){
                    System.out.println("告知顾客包子已经做好了");
                    obj.notify();
                }
            }
        }.start();
    }
}
```

# 等待唤醒机制

## 线程间通信

**概念：**多个线程在处理同一个资源，但是处理的动作（线程的任务）却不相同

例如:线程A用来生成包子的，线程B是用来吃包子的，包子可以理解为同一资源，线程A和线程B处理的动作，一个是生产，一个是消费，那么线程A和线程B之间就存在线程通信问题

**为什么要处理线程间通信：**

多个线程并发执行时，在默认情况下cpu是随机切换线程的，当我们需要多个线程来共同完成一件任务，并且我们希望他们有规律执行，那么多线程之间需要一些协调通信，以此来帮助我们达到多线程共同操作一份数据

**如何保证线程通信有效利用资源：**

多个线程在处理同一个资源，并且任务不同时，需要线程通信来帮助解决线程之间对同一个变量的使用和操作。就是多个线程在操作同一个数据时，避免对同一共享变量的争夺。也就是我们需要通过一定的手段使各个线程能有效的利用资源。而这种手段即--**等待唤醒机制**

## 等待唤醒机制需求分析

![image-20201125150301836](D:\images\image-20201125150301836.png)

# 线程池概念

**线程池：**是一个容纳多个线程的容器，其中的线程可以反复使用，省去了频繁创建线程对象的操作，无需反复创建线程而消耗过多资源

合理利用线程池能够带来三个好处：

1.降低资源消耗。减少了创建和销毁线程的次数，每个工作线程都可以被重复利用，可执行多个任务

2.提高响应速度。当任务到达时，任务可以不需要等到线程创建就能执行

3.提高线程的可管理性。可以根据系统的承受能力，调整线程池中工作线线程的数量，防止因为消耗过多的内存使服务器死机

```java
package com.xhh.ThreadPool;

/**
 * 线程池：JDK1.5之后提供
 * java.util.concurrent.Executors:线程池的工厂类，用来生成线程池‘
 * Executors类中的静态方法：
 *  static ExecutorService newFixedThreadPool(int nThreads)创建一个可重复使用固定线程数的线程池
 *  参数：
 *          int nThreads：创建线程池中包含的线程数量
 *  步骤：
 *  1.使用线程池的工厂类Executors里面提供的静态方法newFixedThreadPool生产一个指定线程数量的线程池
 *  2.创建一个类，实现Runnable接口，重写run方法，设置线程任务
 *  3.调用ExecutorService中的方法submit，传递线程任务（实现类），开启线程，执行run方法
 *  4.调用ExecutorService中的方法shutdown销毁线程池（不建议使用）
 *
 */

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author xhh
 * @date 2020/11/25 16:36
 */
public class ThreadPool {
    public static void main(String[] args) {
        // 1.使用线程池的工厂类Executors里面提供的静态方法newFixedThreadPool生产一个指定线程数量的线程池
        ExecutorService es = Executors.newFixedThreadPool(2);
        //3.调用ExecutorService中的方法submit，传递线程任务（实现类），开启线程，执行run方法
        es.submit(new RunnableImpl());
    }
}
```

# Lambda表达式

```java
package com.xhh.Lambda;
/**
 * Lambda表达式的标准格式：
 *      由3部分组成：
 *          1.一些参数
 *          2.一个箭头
 *          3.一段代码
 *      格式：
 *          （参数列表）->（一些重写方法的代码）;
 */

/**
 * @author xhh
 * @date 2020/11/25 19:23
 */
public class ThreadLambda {
    public static void main(String[] args) {
        //使用内部类方式实现多线程
        new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println(Thread.currentThread().getName()+"线程创建了");
            }
        }).start();
        //使用lambda表达式，实现多线程
        new Thread(()->{
            System.out.println(Thread.currentThread().getName());
        }).start();
    }

}
```

# IO

## IO的分类

根据数据的流向分为：**输入流和输出流**

- **输入流：**把数据从`其他设备`上读取到`内存`中的流
- **输出流：**把数据从`内存`中写出到`其他设备`上的流

格局数据的类型分为：字节流和字符流

## 字节流

### 字节输出流

`java.io.OutPutStream`抽象类是表示字节输出流的所有类的超类，将指定的字节信息写出到目的地。定义了字节输出流的基本共性功能方法

`public void close()`关闭此输出流并释放与此流相关的任何系统资源

`public void flush()`刷新此输出流并强制任何缓冲的输出字节被写出 

`public void write(byte[] b)`将b.length字节从指定的字节数组写入此输出流

```java
/*
字节输出流的使用步骤：
1.创建一个FileOutputStream对象，构造方法中传递写入数据的目的地
2.调用FileOutputStream对象中的方法write，把数据写入到文件中
3.释放资源
*/
package com.xhh.outputstream;

import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @author xhh
 * @date 2020/11/25 22:20
 */
public class DemoOutputStream {
    public static void main(String[] args) throws IOException {
        //1.创建一个FileOutputStream对象，构造方法中传递写入数据的目的地
        FileOutputStream fos = new FileOutputStream("d:/b.txt");
        //在文件中显示100
        fos.write(49);
        fos.write(48);
        fos.write(48);
        /**
         * public void write(byte[] b);将b.length字节从指定的字节数组写入此输出流
         * 一次写多个字符：
         *      如果写的第一个字节是正数（0-127），那么显示的时候会查询ASCII表
         *      如果写的第一个字节是负数，那第一个字节会和第二个字节，两个字节组成一个中文显示，查询系统默认码表（GBK）
         *      
         * 写入字符的方法：可以使用String类中的方法把字符串转化为字节数组
         */                              
        fos.close();
    }
}
```

### 字节输出流的续写和换行

```java
/*
追加写/续写操作：使用两个参数的构造方法
	FileOutputStream（String name,bollean append）创建一个向具有指定name的文件写入数据的输出文件流
	FileOutputStream（File file,bollean append)创建一个向指定File对象标识的文件中写入数据的文件输出流
	参数：
		String name,File file:写入数据的目的地
		boolean append:追加写开关
		true:创建对象不会覆盖源文件，继续在文件的末尾追加写数据
		false:创建一个新文件，覆盖源文件
*/
```

### 字节输入流

```java
/*
java.io.InputStream字节输入流
此抽象类是表示字节输入流的所有类的父类
子类方法：
	int read()从输入流中读取数据的下一个字节
	int read(byte[] b) 从输入流中读取一定数量的字节，并将其存储到缓冲区数组b中
	void close();
*/
        FileInputStream fis = new FileInputStream("d:/b.txt");
        int length = 0;
        while((length=fis.read())!=(-1)){
            System.out.println(length);
        }

        fis.close();
```

## 复制文件

```java
package com.xhh.outputstream;

/**
 * 明确：
 *  数据源：d:/a.txt
 *  目的地：e:/a.txt
 *
 *  文件复制的步骤：i
 *          1.创建一个字节输入流对象，构造方法中绑定要读取的数据源
 *          2.创建一个字节输出流对象，构造方法中写入目的地
 *          3.使用字节输入流中的read方法
 *          4.使用字节输出流中的write
 *          5.释放资源
 */

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @author xhh
 * @date 2020/11/26 14:56
 */
public class CopyFile {
    public static void main(String[] args) throws IOException {
        //1.创建一个字节输入流对象，构造方法中绑定要读取的数据源
        FileInputStream fis = new FileInputStream("d:/a.txt");

        //2.创建一个字节输出流对象，构造方法中写入目的地
        FileOutputStream fos = new FileOutputStream("e:/a.txt");

        //使用字节输入流中的read方法
        int length=0;
        while((length=fis.read())!=-1){
            //4.使用字节输出流中的write
            fos.write(length);
        }

        //关闭
        fos.close();
        fis.close();
    }
}
```

## 字符输出流

```java
package com.xhh.outputstream;
/**
 * java.io.FileWriter extends OutputStreamWriter
 * FileWriter:文件字符输出流
 * 作用：把内存中字符写入到文件中
 *
 *字符输出流的使用步骤：
 *  1.创建FileWriter对象，构造方法中绑定要写入数据的目的地
 *  2.使用FileWriter中的方法write，把数据写入到内存缓冲区中
 *  3.使用FileWriter中方法flush，把内存缓冲区中的数据，刷新到文件中
 *  4.关闭资源
 flush:属性缓存区，流对象可以继续使用
 close:先刷新缓冲区，然后通知系统释放资源。流对象不可以再被使用了
 */
import java.io.FileWriter;
import java.io.IOException;
/**
 * @author xhh
 * @date 2020/11/26 16:23
 */
public class Writer {
    public static void main(String[] args) throws IOException {
        FileWriter fw = new FileWriter("d:/a.txt");
        fw.write(98);
        char[] cs = {'a','b'};
        fw.write(cs);
        
        fw.write("你们好呀");
        fw.write("我是程序员",2,3);
        
        fw.flush();//刷新之后可以继续使用
        fw.close();//关闭之后就不能再使用
    }
}
```

## 字符输入流

`FileReader`

```java
/*
字符输入流的使用步骤：
	1.创建FileReader对象，构造方法中绑定要读取的数据源
	2.使用FileReader对象中的方法read读取文件
	3.释放资源
*/
```



## 字节缓冲输入流和字节缓冲输出流

缓冲流，也叫高效流，是对4个基本的File流的增强

- **字节缓冲流：**`BufferedInputStream`,`BufferedOutputStream`
- **字符缓冲流：**`BufferedReader`，`BufferedWriter`

缓冲流的基本原理，是在创建流对象时，会创建一个内置的默认大小的缓冲区数组，通过缓冲区读写，减少系统IO次数，从而提高读写效率。 

### 字节缓冲输入流

```java
public class TestBufferedInputStream {
    public static void main(String[] args) throws IOException {
        //创建FileInputStream对象，构造方法绑定数据源
        FileInputStream fis = new FileInputStream("d:/a.txt");
        //创建BufferedInputStream对象，传递fis对象，提高读取效率
        BufferedInputStream bis = new BufferedInputStream(fis);
        //使用read方法，读取文件
        int length = 0;//记录读取到的字节
        while((length=bis.read())!=(-1)){
            System.out.println(length);
        }
        
       bis.closed();
       fis.closed();
    }
}
```

### 字节缓冲输出流

```java
package com.xhh.outputstream;

/**
 * 字节缓冲输出流
 * 使用步骤：
 *      1.创建FileOutputStream对象，绑定输出的目的地
 *      2.创建BufferedOutputStream对象，构造方法中传递FileOutputStream对象，提高FileOutputStream对象效率
 *      3.使用BufferedOutputStream对象中的方法write，把数据写入输入缓冲区中
 *      4.使用BufferedOutputStream对象中的方法flush，把内部缓冲区中的数据，刷新到文件中
 *      5.释放资源
 */

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @author xhh
 * @date 2020/11/27 15:57
 */
public class TestBufferedOutputStream {
    public static void main(String[] args) throws IOException {
        // 1.创建FileOutputStream对象，绑定输出的目的地
        FileOutputStream fos = new FileOutputStream("d:/a.txt");
        //2.创建BufferedOutputStream对象，构造方法中传递FileOutputStream对象，提高FileOutputStream对象效率
        BufferedOutputStream bops = new BufferedOutputStream(fos);
        //3.使用BufferedOutputStream对象中的方法write，把数据写入输入缓冲区中
        bops.write("把数据写入到内部缓冲区中".getBytes());
        //释放资源
        bops.close();
    }
}
```

## 字符缓冲流

`public BufferedReader(Reader in)`：创建一个新的缓冲输入流

`public BufferedWriter(Writer out)`：创建一个新的缓冲输出流

### 字符缓冲输出流

```java
package com.xhh.outputstream;
/**
 * 字符缓冲输出流
 * 使用步骤：
 *      1.创建字符缓冲输出流对象，构造方法中传递字符输出流
 *      2.调用字符缓冲输出流中的方法write,把数据写入到内存缓冲区中
 *      3.调用字符缓冲输出流中的方法flush,把数据刷新到文件中
 *      4.释放资源
 */
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
/**
 * @author xhh
 * @date 2020/11/27 16:16
 */
public class TestBufferedWriter {
    public static void main(String[] args) throws IOException {
        //1.创建字符缓冲输出流对象，构造方法中传递字符输出流
        BufferedWriter bw = new BufferedWriter(new FileWriter("d:/a.txt"));
        bw.write("\n");
        bw.write("你们好啊");
        bw.close();
    }
}
```

### 字符缓冲输入流

```java
/*
	1.创建字符缓冲输入流对象，构造方法中传递字符输入流
	2.使用字符缓冲输入流对象中的方法read/readLine读取文本
	3.释放资源
*/
public class TestBufferedReader{
    public static void main(String[] args) throws IOException{
        //1.创建字符缓冲输入流对象，构造方法中传递字符输入流
        BufferedReader br = new BufferedReader(new FileReader("d:/a.txt"))；
         //2.使用字符缓冲输入流对象中的方法read/readLine读取文本
        String line = br.readLine();
        System.out.println(line);
    }
}
```



## 转换流

- **字符编码`Character Encoding`**：一套自然语言的字符与二进制数之间的对应规则
- **字符集`Charset`**：也叫编码表。是一个系统支持的所有字符的集合，包括国家文字，标点符号，图形符号，数字等    

### InputStreamReader类

转换流`java.io.InputStreamReader`，是Reader的子类，是从字节流到字符流的桥梁。读取字节，并使用指定的字符集将其解码为字符

```java
/*
使用步骤：
	1.创建InputStreamReader对象，构造方法中传递字节输入流和指定的编码表名称
	2.使用InputStreamReader对象中的方法read读取文件
	3.释放资源
注意事项：
	构造方法中指定的编码表名称要和文件的编码相同，否则会发生乱码
*/
```



### OutputStreamWriter类

```java
package com.xhh.outputstream;
/**
 * OutputStreamWriter:是字符流通向字节流的桥梁：可使用指定的charset将要写入流中的字符编码成字节
 * 方法：
 *      OutputStreamWriter(OutputStream out)创建使用默认字符编码的OutputStreamWriter
 *      OutputStreamWriter(OutputStream out,String charsetName)创建使用默认字符编码的OutputStreamWriter
 * 步骤：
 *      1.创建OutputStreamWriter对象，构造方法中传递字节输出流和指定的编码表名称
 *      2.使用OutputStreamWriter对象中的方法write，把字符转换为字节存储到缓冲区中
 *      3.flush
 *      4.close
 */

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

/**
 * @author xhh
 * @date 2020/11/27 17:34
 */
public class TestOutputStreamWriter {
    public static void main(String[] args) throws IOException {
        write_utf_8();
    }

    private static void write_utf_8() throws IOException {
        //1.创建OutputStreamWriter对象，构造方法中传递字节输出流和指定的编码表名称
        OutputStreamWriter opsw = new OutputStreamWriter(new FileOutputStream("d:/utf_8.txt"),"utf-8");
        //2.使用OutputStreamWriter对象中的方法write，把字符转换为字节存储到缓冲区中
        opsw.write("你好");

        opsw.flush();
        opsw.close();
    }
}

```



## 序列化和反序列化

- 把对象以流的方式，写入到文件中保存，叫做对象的序列化`ObjectOutputStream`
- 把文件中保存的对象，以流的方式读取出来，叫做对象的反序列化`ObjectInputStream`

### ObjectOutputStream类

`java.io.ObjectOutputStream`类，将java对象的原始数据类型写出到文件，实现对象的持久存储

### 序列化操作

一个对象想要序列化，必须满足两个条件：

- 该类必须实现`java.io.Serializable`接口，`Serializable`是一个标记接口，不实现此接口的类将不会使任何状态序列化或反序列化，会抛出`NotSerializableException`
- 该类的所有属性必须是可序列化的。如果有一个属性不需要可序列化，则该属性必须注明是瞬态的，使用`transient`关键字修饰

```java
package com.xhh.outputstream;
/**
 * java.io.ObjectOutputStream extends OutputStream
 * ObjectOutputStream：对象的序列化流
 * 作用：把对象以流的方式写入到文件中保存
 *  构造方法：
 *      ObjectOutputStream(OutputStream out):创建写入指定OutputStream的ObjectOutputStream
 *      OutputStream out：字节输出流
 *  特有的成员方法：
 *      void writeObject(Object obj)将指定的对象写入 ObjectOutputStream
 *
 *      使用步骤：
 *          1.创建ObjectOutputStream对象，构造方法中传递字节输出流
 *          2.创建ObjectOutputStream对象中的方法writeObject,把对象写入到文件中
 *          3.释放资源
 */
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
/**
 * @author xhh
 * @date 2020/11/27 18:35
 */
public class TestObjectOutputStream {
    public static void main(String[] args) throws IOException {
        //1.创建ObjectOutputStream对象，构造方法中传递字节输出流
        ObjectOutputStream oops = new ObjectOutputStream(new FileOutputStream("d:/a.txt"));
        //2.创建ObjectOutputStream对象中的方法writeObject,把对象写入到文件中
        oops.writeObject(new Person(18,"张三"));
        oops.close();
    }
}
```

