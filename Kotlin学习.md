#  Kotlin

## 初级

### 变量的声明和使用

```kotlin
// var可变，数据类型对应Java的包装类型
var [变量名称] : [数据类型]
// val不可变，相当于java的final
val [变量名称] : [数据类型]
```

### 位运算操作

- shl(bits)-有符号左移 （相当于Java << ）
- shr(bits)-有符号右移 (相当于Java >> )
- ushr(bits)-无符号右移
- ushl(bits)-无符号左移
- and(bits)-按位与
- or(bits)-按位或
- xor(bits)-按位异或
- inv(bits)-取反

### 字符串操作

```kotlin
fun main() {
    var x : Int = 1
    var text : String = "${x} \n个人"
    
    var text2: String = """类似于Groovy的
文本
    """
    
    println(text)
    println(text2)
}
```

![image-20250214231434300](https://gitee.com/cnuto/images/raw/master/image/image-20250214231434300.png)

### 选择结构（When）

如果将when作为表达式，则else分支必须存在。

```kotlin
fun main() {
    val str: Char = 'A'
    
    // 单行时可以省略 {}，类似于Java中的switch...case语句
    // 相同条件使用逗号分隔
    when (str) {
        'A', 'F', 'C' -> { println("AAA"); println("hello world") }
        'B' -> println("BBB")
        else -> println("CCC")
    }
}
```

### 基本数据类型

```kotlin
fun main() {
    var a: Int = 10001
    var b: Int = 10001
    
    // true，编译成Java的int类型
    println(a === b)
    
    
    var a1: Int? = 10001
    var b1: Int? = 10001
    // false， 编译成Java的Integer，比较的是内存地址
    println(a1 === b1)
    
    
    var a2: Int? = 111
    var b2: Int? = 111
    
    // true, 少于127，比较的是数值
    println(a2 === b2)
}
```



## 中级

### 函数创建

```kotlin
// 函数的形参默认情况下为常量，无法修改，只能使用。
fun 函数名称(参数a :参数类型, 参数b :参数类型) : 返回值类型 {
    // 具体函数
}
```

```kotlin

fun main() {
    printCustom(1, 2)
    println(sumDefaultValue(null, 2))
    println(commonParam)
    println(commonParam2)
}

fun printCustom(a: Int, b: Int): Unit {
    println(a)
    println(b)
}

fun sumDefaultValue(a: Int?, b: Int = 2): Int {
    return (a ?: 0) + b
}

// 简单写法
fun sumSimple(a: Int, b: Int) = a + b
```

### Lambda表达式和高阶函数

```kotlin
fun main() {
    // 使用Lambda函数定义一个函数
    val func: (String) -> Unit = {
        println(it)
    }
    
    func("hello")
    
    
    test(1) {
        println(it)
        0
    }
}
// 接收一个入参为String, 出参为Int的函数
fun test(a : Int, func: (String) -> Int) {
    println(func("hello world"))
}
```

### 内联函数

使用高阶函数可能会影响运行时的性能：每个函数都是一个对象，而且函数可以访问一些局部变量，但是这可能会在内存分配（用于函数对象和类）和虚拟调用时造成额外开销。

使用==inline==关键字会影响函数本身和传递给他的lambads，它能够让方法的调用在编译时，直接替换为方法的执行代码。

```kotlin
fun main() {
    test("hello")
    // 内联函数，相当于直接调用 println("hello") println("hello $str")
    
    testHighMethod { println("输出 $it") }
    // 相当于
    println("这是一个内联函数")
    val it = "helloWorld"
    println("输出 helloWorld")
}

inline fun test(str: String) {
    println(str)
    println("hello $str")
}

inline fun testHighMethod(func: (String) -> Unit) {
    println("这是一个内联函数")
    func("helloWorld")
}
```

![image-20250223144142865](https://gitee.com/cnuto/images/raw/master/image/image-20250223144142865.png)

### 类

```kotlin
// 构造函数
class Student public constructor(name: String, age: Int){
}

// 添加var或者val关键字使name和age变成属性
class Student public constructor(var name: String, var age: Int){
}

// 属性定义在内部，必须初始化值
class Student {

    var name: String = ""
    var age: Int = 0
}

class Student constructor(var name: String, var age: Int) {
    
    // 次要构造函数，代码块可不写；必须直接或者间接调用主构造函数
    constructor(name: String): this(name, 0) {
        println("次要构造函数")
    }
    
    // 重写toString()
    override fun toString(): String {
        return "Student(name='$name', age=$age)"
    }
    
    init {
        println("类的初始化代码块")
    }
}
```

### 运算符重载

Kotlin支持为程序中已知的运算符提供自定义实现，这些运算符具有固定的符号表示（如+或者-或者*）以及对应的优先级。

```kotlin
class Person(val name: String, val age: Int) {
    
    operator fun plus(person: Person): Int {
        return this.age + person.age;
    }
}

    val p1 = Person("hello", 1)
    val p2 = Person("hello", 2)
    // 结果为3
    println(p1 + p2)
```

### 中缀函数

使用==infix==关键字标记的函数称为中缀函数：

- 必须是成员函数
- 只能有一个参数
- 参数不能有默认值

```kotlin
class Person(val name: String, val age: Int) {
    
    infix fun test(str: String): String {
        return name + str
    }
}


fun main() {
    val person = Person("hello", 18)
    // helloworld
    println(person test "world")
    // 也可以当作普通函数调用
    println(person.test("world"))
}
```

中缀函数的优先级低于算术运算符、类型转换和`rangeTo`运算符

- `1 shl 2+3`相当于`1 shl (2+3)`
- `0 until n * 2`相当于`0 until (n * 2)`
- `xs union ys as Set<*>`相当于`xs union (ys as Set<*>)`

优先级高于布尔运算符`&&`和`||`、`is -`和`in -check`以及其他一些运算符的优先级

- `a && b xor c`相当于`a && (b xor c)`
- `a xor b in c`相当于`(a xor b) in c`

### 空值和空类型

```kotlin
fun main() {
    // kotlin默认变量不为空，下面代码编译不通过
    // val str: String = null
    
    // 可为空
    var student: Student? = null
    val name: String = student?.name ?: "default name"
    
    println(name)
}
```

### 解构声明

```kotlin
class Student constructor(var name: String, var age: Int) {
   
    // 解构函数
    operator fun component1() = name
    operator fun component2() = age
}    

val stu = Student("张三", 10)
val (a, b) = stu
    
println("name$a, age$b")

val func: (Student) -> Unit = {(a, b) ->
        println(a)
}    
func(stu)
```

### 类的继承

```kotlin
fun main() {
    val son = Son("son", 10)
//    son.parent()
//    println(son.name)
    baseEvent(son)
    
    val daughter = Daughter("daughter", 13)
    baseEvent(daughter)
}

fun baseEvent(basePerson: Parent) {
    println(basePerson.javaClass)
}


open class Parent(open var name: String?, open var age: Int?){
    open fun parent() {
        println("parent")
    }
}

// 父类存在有参数的构造函数，子类必须调用父类的构造函数
class Son(override var name: String?, age: Int) : Parent(name, age) {
    override fun parent() {
        println("son do something")
    }
}

class Daughter(name: String, age: Int) : Parent(name, age) {
    
}
```


### 扩展函数

```kotlin
fun main() {
    val student = Student(10)
    println(student.test()) // 打印 我是测试函数
    
    
    printClass(ArtStudent(0)) // 打印 student
    
    // String类型扩展，需要String.前缀
    val func: String.() -> Int = {
        this.length
    }
    
    println("h".func()) // 可以直接对符合类型的对象使用这个函数
    println(func("l")) // 直接调用，传入对应类型的对象作为首个参数，此时this指向传递的参数
}

open class Student(var age: Int) {
    fun hello() = println("hello")
}

fun Student.test() = println("我是测试函数")

// age被private修饰时这里会报错，受到访问控制
fun Student.t() = {
    this.age
}


// Student类中扩展一个name属性
//var field: String = ""
//var Student.name:String
//    get() = field
//    set(value){ field = value}

class ArtStudent(age: Int): Student(age) {
    
}

fun Student.getName() = "student"
fun ArtStudent.getName() = "artStudent" // 这里虽然同时扩展了父类和子类的getName函数

fun printClass(student: Student) { // 但是由于这里指定的类型是Student，所以编译时也只会使用Student扩展的getName函数
    println(student.getName())
}
```

### 官方扩展函数

```kotlin
    var a = Any()
		
		a.apply { 
        println(this)
    }

    var st: Int = a.let { 
        println(it)
        1
    }
```



## 高级

### 泛型

```kotlin
fun main() {
    val score = Score<Int>("高等数学", "123", 60)
    val value: Int = score.value
    
    val str = genericityMethod("hello") // 调用函数自动明确类型
    println(str)
}

class Score<T>(var name: String, var id: String, var value: T)

abstract class A<T>() {
    abstract fun test(): T;
}


// 子类直接声明为String类型
class B : A<String>() {
    override fun test(): String {
        return "hello"
    }
}

abstract class C<V>: A<V>() {
}


// 泛型函数
fun <T> genericityMethod(t: T): T {
    return t
}
```

### 协变和逆变

- `out`关键字用于标记一个类型参数作为协变，可以实现子类到父类的转换；使用out修饰的泛型不能用作函数的参数，对应类型的成员变量setter也会被限制，只能当作一个生产者使用。
- `in`关键字用于标记一个类型参数作为逆变，可以实现父类到子类的转换；使用In修饰的泛型不能用作函数的返回值，对应类型的成员变量getter也会被限制，只能当作一个消费者使用。

```kotlin
class Test<T>(var data: T)


fun main() {
    val test1 = Test<Int>(10)
    // 类似于Java的 ? extends
    // Test<Integer> test1 = new Test<Integer>(1);
    // Test<? extends Number> test2 = test1;
    val test2: Test<out Number> = test1
    
    // 这部分编译报错，不能设置具体的值；这里test2继承了Number， data泛型不能赋值
    // test2.data = 12
    println(test2.data)
    
    // 类似于Java的 ? super
    // Test<Object> test3 = new Test<Object>(10)
    // Test<? super Number> test4 = test3;  Number的父类为Object
    val test3: Test<Any> = Test(10)
    val test4: Test<in Number> = test3
    
    test4.data = 11
    println(test4.data)
    
    // 原生数组
    intArrayOf()
    byteArrayOf()
    doubleArrayOf()
}
```

### 数组和原生类型数组

创建方式

- 官方预设工具函数：==arrayOf()==, ==arrayOfNulls()==以及==emptyArray()==
- 使用类Array构造函数创建

```kotlin
fun main() {
    val array: Array<Int> = arrayOf(1, 2, 3)
    // 打印1，2，3
    for (element in array) {
        println(element)
    }
    
    // 数组拷贝
    val copyOf = array.copyOf()
    
    val nullCopyOf = array.copyOf(10)
    nullCopyOf.forEachIndexed { index : Int, v: Int? -> 
        println("$index$v")
    }


    // 坐标0, 值1
    // 坐标1, 值2
    // 坐标2, 值3
    for ((index, element) in array.withIndex()) {
        println("坐标$index, 值$element")
    }


    // i get 0 and value1
    // i get 1 and value2
    // i get 2 and value3
    array.forEachIndexed { index: Int, i: Int ->
        println("i get $index and value$i")
    }
    
    // 数组转字符串，默认逗号隔开1, 2, 3
    // (1-2-3)
    println(array.joinToString("-", "(" , ")"))
    
    
    val arrayByConstructor: Array<String> = Array(5, ) {
        "默认元素"
    }

    for (s in arrayByConstructor) {
        println(s)
    }
        
}
```

### 可变长参数

使用关键字`vararg`，同类型参数可以任意数量

```kotlin
fun main() {
    val array: Array<String> = arrayOf("a","b","c")
    test(*array)

    
    val arrayInt: Array<Int> = arrayOf(1,2,3)
    // 编译报错，泛型擦除，被转换为Integer
    // testInt(*arrayInt)

    val intArrayOf = intArrayOf(1, 1)
    testInt(*intArrayOf)

}

fun test(vararg str: String) {
    val array: Array<out String> = str
    println(array.joinToString())
}

fun testInt(vararg i: Int) {
    println(i.joinToString())
}
```

### 普通集合

```kotlin
package org.example

fun main() {
    val emptyList: MutableList<String> = mutableListOf()
    emptyList.add("hello")
    val list: MutableList<Int> = mutableListOf(1, 2, 3)

    val iterator: Iterator<String> = emptyList.iterator()
    while (iterator.hasNext()) {
        println(iterator.next())
    }

    // iterator是一次性的，上面使用之后就不能在使用了，下面报错
    // println(iterator.next())

    // 压缩操作，Pair   0 -> hello, 1 报错indexOutBound
    val zip = list.zip(emptyList)
    println(zip.get(0))
    
    /// 通过associate转换map映射
    emptyList.associate { s ->
        s.length to s
    }
    
    // 可以直接使用map.putAll方法把zip放入
    val zipMap = mutableMapOf<Int, String>()
    zipMap.putAll(zip)

    val emptySet: MutableSet<String> = mutableSetOf()
    emptySet.add("set")
    emptySet.add("set")
    
    
    val emptyMap = mutableMapOf<Any, Any>()
    emptyMap[1] = "world"
    emptyMap["hello"] = "world"
    
    val map: MutableMap<Int, String> = mutableMapOf(1 to "hello", 2 to "world")
    val value: String? = map.getOrDefault(3, "default value")
    // 和getOrDefault一样，只是getOrPut使用了函数式返回默认值
    map.getOrPut(5) { "default 5" }
    val newMap = map + mutableMapOf(4 to "four")
    if (3 !in map.keys) {
        println("not in 3")
    }
    for ((k, v) in map) {
        println("key$k value$v")
    }
    println(value)
    
    // 嵌套集合
    val flattenList = mutableListOf(mutableListOf("hello"), mutableListOf("world"))
    var flatten: List<String> = flattenList.flatten()

}
```

### 序列（Sequence）

序列是一个延迟获取数据的集合，只有在元素需要时才会生产元素并提供给外部，包括所有对元素的操作，并不是一次性全部处理。使用序列能够在处理大量数据时获得显著的提升。

```kotlin
package org.example

fun main() {
    
    // 不调用println语句不会打印
    val sequence: Sequence<Int> = generateSequence{
        println("生成sequence")
        10
    }
    
    
    val list = listOf("aa", "bbb", "ccc", "dd", "eee", "ff", "ggg", "hh")
   
    /* 不使用sequence前
    过滤操作aa
    过滤操作bbb
    过滤操作ccc
    过滤操作dd
    过滤操作eee
    过滤操作ff
    过滤操作ggg
    过滤操作hh
    进行大写转换
    进行大写转换
    进行大写转换
    进行大写转换
     */

    /** 使用sequence后
     * 过滤操作aa
     * 过滤操作bbb
     * 进行大写转换
     * 过滤操作ccc
     * 进行大写转换
     * BBB, CCC
     */
    
    // take(2)获取到2个后就停止
    val result = list.asSequence().filter { 
        println("过滤操作$it")
        it.length > 2
    }.map { 
        println("进行大写转换")
        it.uppercase()
    }.take(2)

    println(result.joinToString())
}
```

### 特殊类型

#### 数据类(data class)

在class关键字前面添加关键字`data`

数据类声明后，编译器会根据主构造函数中声明的所有属性自动为其生成以下函数:

- .equals() / .hashCode()
- .toString()
- .componentN() 按声明顺序自动生成结构函数
- .copy() 用于对对象的拷贝

数据类必须满足以下需求：

- 主构造函数至少有一个参数
- 主构造函数的参数必须标记为var或者val
- 数据类不能为抽象的，开放的，密封的或内部的

规则：

- 如果数据类主体中 .equals() .hashCode()或者.toString()等函数存在显示实现，或者父类中有final实现，则不会自动生成这些函数，并使用现有的实现。

```kotlin
data class User(var id: Long?, var name: String) {
    override fun toString(): String {
        return "我是自定义"
    }
}

fun main() {
    var user = User(null, "hello")
    println(user.toString())
}
```

- 如果超类具有open .componentN()函数并返回兼容类型，则为数据类生成相应的函数，并覆盖超类型的函数。如果由于一些关键字导致无法重写父类对应的函数会导致直接报错。

```kotlin
abstract class AbstractUser {
    // 此函数必须是open的，否则无法被数据类继承
    open operator fun component1() = "hl"
}

// 编译不通过
data class User(val id: String?, var name: String): AbstractUser() {
}
```



- 不允许为.componentN()和.copy()函数提供显示实现

注意，编译器会且只会根据主构造函数中定义的属性生成对应函数，如果有些时候我们不希望某些属性被添加到自动生成的函数中，我们每个需要手动将其移除主构造函数。

#### 枚举

// todo

#### 匿名类和伴生对象

```kotlin
fun interface Person {
    fun chat()
}

open class Human(val name: String)

fun main() {
    var obj = object: Human("小明"), Person { // 继承类时，同样需要调用其构造函数
        fun test() {}
        var age: Int = 10
        override fun chat() {
            TODO("Not yet implemented")
        }

        override fun toString(): String {
            return "我是$name"  // 子类，直接使用父类的属性
        }
    }
    
    // 当Person存在两个方法时，lambda不支持
    var p = Person {
        println("hl wd")
    }
    
    p.chat()
}
```

#### 单例类

使用`object`修饰。可以当作工具类使用。用起来和Java中的静态属性很类似，但是性质完全不一样

```kotlin
object Singleton {
    val name = "name"
}

fun main() {
    println(Singleton.name)
}
```

#### 伴生类

既支持单例类那样调用，又支持像一个普通class那样使用，可以使用伴生对象。

```kotlin
class Stu(val name: String, val age:Int) {
    // 使用companion关键字在内部编写一个伴生对象，它同样是单例的
    companion object Tools {
        fun create(name: String, age: Int) = Stu(name, age)
    }
}

fun main() {
    Stu.create("姓名", 10)
}
```

#### 委托

```kotlin
interface Base {
    fun print()
}

class BaseImpl(val x:Int) : Base {
    override fun print() {
        println("hl world $x")
    }
    
    // 属性也可委托
    val y : Int by ::x
}

class Derived(private val base: Base): Base by base { // 使用by关键字将所有接口待实现操作委托给指定成员
    override fun print() {
        base.print()
    }
}

fun main() {
    val base: Base = Derived(BaseImpl(10))
    base.print()
}
```

#### 密封类

使用`sealed`修饰。密封类将类的使用严格控制在了模块内部，包括密封接口及其实现也是如此：一旦编译了具有密封接口的模块，就不会出现新的实现类。

```kotlin
// 在其他包中使用这个密封类，在其他包或者模块中无法使用
class C: A() // 编译错误，不在同一个模块

fun main() {
    val b = B() // 编译错误，不可以实例化
}
```