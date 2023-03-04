# 类初始化

```java
class A{
	static {
		System.out.println("父类静态代码块");
	}
	public A(){
		System.out.println("父类构造方法");
	}
	{
		System.out.println("父类初始化块");
	}
}
public class B extends A{
	static{
		System.out.println("子类静态代码块");
	}
	public B(){
		System.out.println("子类构造方法");
	}
	{
		System.out.println("子类初始化块");
	}
	public static void main(String[] args){
		new B();
	}
}
```
执行顺序：父类静态代码块->子类静态代码块->父类普通代码块->父类构造方法->子类代码块->子类构造方法

# 线程
==线程私有==：程序计数器，虚拟机栈，本地方法栈
==线程共享==：堆，方法区
## 抛异常InterruptedException
- `java.lang.Object`类的wait方法
- `java.lang.Thread`类的sleep方法
- `java.lang.Thread`类的join方法
# 接口
JDK8中，接口中的方法可以被default和static修饰，但是，**被修饰的方法必须有方法体**
## 关于抽象类
jdk1.8以前，抽象类的方法默认访问权限是protected
jdk1.8之后，抽象类的方法默认访问权限是default（不写）
## 关于接口
jdk1.8之前，方法必须为public
jdk1.8时，方法可以为public，也可以是default和static，但是必须要有方法体
# Static
被static修饰的变量叫做静态变量，静态变量从属于类，局部变量从属于方法，只能在方法内生效，所以**static不能修饰局部变量**
# 两个数值进行二元操作
基本类型之间比较时低精度自动转化为高精度
- 如果两个操作数其中有一个是double类型，另一个操作就会转换为double类型。
- 否则，如果其中一个操作数是float类型，另一个将会转换为float类型。
- 否则，如果其中一个操作数是long类型，另一个会转换为long类型。
- 否则，两个操作数都转换为int类型

# 包装类比较
以Integer包装类为例
- int和Integer比较时，Integer自动拆箱，==和equals都相等
- Integer和Integer比较时，如果数值在[-128,127]之间，说明直接在缓存中取值，此时相等。如果不在范围内，==不相等，equals相等
- Integer和new Integer比较时，==为false,equals比较为true
- new Integer和new Integer比较时，==为false,equals比较为true
- Byte,Short,Long情况类似，**Float和Double在任何数值==比较都不相等,equals相等**
# 不会类初始化子类的几种情况
1.调用的是父类的static方法或者字段，只会触发子类的加载，父类的初始化，不会导致子类初始化
2.调用final方法或者字段，常量在编译时会存入调用类的常量池中，本质上没有直接引用到定义常量的类，因此不会触发定义常量的类的初始化
3.通过数组定义来引用类
# Integer对象的方法
**Integer.parseInt("")** 表示将字符串类型转型为int的基本数据类型
**Integer.valueOf("")** 表示将字符串类型数据转换为Integer对象
**Integer.intValue()** 表示把Integer对象转换为int基本数据类型

# For循环语句执行顺序
```java
for(条件1;条件2;条件3) {
    //语句
}
```
顺序：条件1->条件2->语句->条件3->条件2->语句->条件3->条件2->语句...直到条件2为false退出循环
# 反射机制
Java反射机制主要提供了以下功能：
- 在运行时判断任意一个对象所属的类
- 在运行时构造任意一个类的对象
- 在运行时判断任意一个类所具有的成员变量和方法
- 在运行时调用任意一个对象的方法