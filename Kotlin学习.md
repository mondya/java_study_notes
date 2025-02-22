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

### Lambda表达式

```kotlin
fun main() {
    val x: (String) -> Unit = {
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

