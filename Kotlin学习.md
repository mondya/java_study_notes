# Kotlin

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
    
    x("hello")
    
    
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
    println("输出 :${it}")
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

## 高级

### 泛型