  ## Groovy中?.,*.操作符
- `*.`运算符是一个快捷运算符，允许对集合的所有元素调用方法或属性

```groovy
    def list = ['abc','yyyy','1234467']  
    def sizes = list*.size()    
    sizes.each{
       println it
    }
    //打印结果为3,4,7
```
上面的例子,说明`*.`操作符操作的是list对象;返回的也是list对象
- `?.`用于判断非空，如man类，包含man.age,man.name
  - 在java中需要判断
```java
if(man!=null){
    if(man.name != null){
        System.out.println(man.name)
    }
}
```
  - 使用groovy的?.
```groovy
//如果man为空,则返回null;如果man.name为空，则返回空
print man?.name

//三元运算
//man.name是否存在，存在则显示本身;不存在则返回""
man.name?:""
```

## 闭包
Groovy提供了闭包的支持，语法和lambda表达式有些类似，简单来说就是一段可执行代码块或函数指针。闭包在Groovy中是`groovy.lang.Closure`类的实例，这使得闭包可以赋值给变量或者作为参数传递
- 闭包访问外部变量
```groovy
def str = 'hello'
def closure={
    println str
}
closure()//hello 
```
- 闭包调用的方式有两种，闭包.call(参数)或者闭包(参数)，在调用的时候可以省略圆括号

```groovy
def closure = {
    param -> println param
}
 
closure('hello')
closure.call('hello')
closure 'hello'
```
- 闭包的参数是可选的，如果没有参数的话可以省略`->`操作符

```groovy
def closure = {println 'hello'}
closure()
```
- 多个参数以逗号隔开，参数类型和方法一样可以显示声明也可省略

```groovy
def closure = { String x, int y ->                                
    println "hey ${x} the value is ${y}"
}
```
- 如果只有一个参数的话，也可省略参数的定义，Groovy提供了一个隐式的参数`it`来替代它

```groovy
def closure = { it -> println it } 
//和上面是等价的
def closure = { println it }   
closure('hello')
```
- 闭包可以作为参数传入，闭包作为方法唯一参数或最后一个参数时可省略括号

```groovy
def eachLine(lines, closure) {
    for (String line : lines) {
        closure(line)
    }
}

eachLine('a'..'z',{ println it }) 
//可省略括号，与上面等价
eachLine('a'..'z') { println it }
```