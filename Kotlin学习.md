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