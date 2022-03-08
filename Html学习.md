# 网页

## 基本标签

```html
&gt; 大于号
&lt; 小于号
&copy;  版权声明符号
```

# JS

```javascript
    <script>
        //1.定义变量 变量类型 变量名 = 变量值；
        var num = 1;
        //console.log(num) 在浏览器控制台打印
        var person = {
            name: "xhh",
            age: 18,
            tags: ['ss','mm']
        }
    </script>
```

```javascript
/*
'use strict'; 严格检查模式，预防javaScript的随意性导致的问题
*/
```

## 数据类型

1.正常字符使用单引号或者双引号

2.注意转义字符

> 使用iterator来遍历迭代

```javascript
    let arr = [5,4,9,7];
    for (var x of arr){
        console.log(x);
    }
```

> 函数定义

方式一：

```javascript
function abs(x){}
```

方式二：

```javascript
var abs = function(x){}
```

匿名方式创建函数

`arguments`是js免费赠送的关键字，打印输入的所有的参数 

```javascript
function add(a,b,...rest){
    console.log("a=>"+a);
    console.log("b=>"+b);
    console.log(rest);
}
```

## json字符

```javascript
    <script>
        var user = {
            name:"xhh",
            age:18,
            sex:"男"
        }
        //对象转化为json字符
        var s = JSON.stringify(user);
//VM85:1 {"name":"xhh","age":18,"sex":"男"}
    //json    字符串转换为对象
        var obj = JSON.parse('{"name":"qingjiang","age":3,"sex":"男"}');
    </script>
```



> location

location代表当前页面的URL信息

```javascript
host:"www.baidu.com"
href:"https://www.baidu.com/"
location.assign('') 定义一个新地址
```

> document

```javascript
document.title
```

> 获得dom结点

```javascript
<div id="father">
    <h1>标题1</h1>
    <p id="p1">p1</p>
    <p class="p2">p2</p>
</div>
<script>
    //对应css选择器
    var p1 = document.getElementById('p1');
    var p2 = document.getElementsByClassName('p2');
    var father = document.getElementById('father');
</script>
```

```javascript
    let p1 = document.getElementById('p1');
    let father = p1.parentElement;
	father.removeChild('p1');  //移除p1元素
```

> 增加元素

```javascript
<p id="js">JavaScript</p>
<div id="list">
    <p id="se">JavaSE</p>
    <p id="ee">JavaEE</p>
    <p id="me">JavaME</p>
</div>

<script>
    let js = document.getElementById('js');//已经存在的节点
    let list  = document.getElementById('list');
    let newP = document.createElement('p');//创建一个p标签
    newP.id = 'newP';
    newP.innerText = 'Hello,World';
    list.append(newP);

    //创建一个Style标签
    let myStyle = document.createElement('style');
    myStyle.setAttribute('type','text/css');
    myStyle.innerHTML='body{background-color:chartreuse;}';
    document.getElementsByTagName('head')[0].appendChild(myStyle);//注意getElementsByTagName
</script>                                                                 
```

## 操作表单

- 文本框 text
- 下拉框 `<select>`
- 单选框 radio
- 多选框 checkbox
- 隐藏域 hidden
- 密码框 password

表单的目的：提交信息

```javascript
<form action="post">
    <p><span>用户名：</span><input type="text" id="username"></p>
    <p>
        <span>性别：</span>
        <input type="radio" name="sex" value="man" id="boy">男
        <input type="radio" name="sex" value="woman" id="girl">女
    </p>
</form>
<script>
    let username = document.getElementById('username');
    let radio_boy = document.getElementById('boy');
    let radio_girl = document.getElementById('girl');
</script>
```

> jQuery事件

# Vue

vue常用7个属性：

`el属性`:用来指示vue编译器从什么地方开始解析 vue的语法，相当于element id

`data属性`：用来组织从view中抽象出来的属性，可以说将视图的数据抽象出来存放在data中

`template属性`：用来设置模板，会替换页面元素，包括占位符

`methods`放置页面中的业务逻辑，js方法一般都放置在methods中

`render`创建真正的Virtual Dom

`computed`用来计算

`watch`:  监听data中数据的变化；watch:function(new,old){}；两个参数，一个返回新值，一个返回旧值	

`v-bind`指令，获取属性中的值

`v-model`在表单`<input>`,`<textarea>`,`<select>`元素上创建双向数据绑定。注意：v-model会忽略所有表单元素的value,checked,selected特性的初始值而总是将Vue实例的数据作为数据来源

## Vue生命周期

![img](https://upload-images.jianshu.io/upload_images/13119812-5890a846b6efa045.png?imageMogr2/auto-orient/strip|imageView2/2/w/1200/format/webp) 

#### beforeCreate( 创建前 )

在实例初始化之后，数据观测和事件配置之前被调用，此时组件的选项对象还未创建，el 和 data 并未初始化，因此无法访问methods， data， computed等上的方法和数据。

#### created ( 创建后 ）

实例已经创建完成之后被调用，在这一步，实例已完成以下配置：数据观测、属性和方法的运算，watch/event事件回调，完成了data 数据的初始化，el没有。 然而，挂在阶段还没有开始, $el属性目前不可见，这是一个常用的生命周期，因为你可以调用methods中的方法，改变data中的数据，并且修改可以通过vue的响应式绑定体现在页面上，，获取computed中的计算属性等等，通常我们可以在这里对实例进行预处理，也有一些童鞋喜欢在这里发ajax请求，值得注意的是，这个周期中是没有什么方法来对实例化过程进行拦截的，因此假如有某些数据必须获取才允许进入页面的话，并不适合在这个方法发请求，建议在组件路由钩子beforeRouteEnter中完成

#### beforeMount

挂在开始之前被调用，相关的render函数首次被调用（虚拟DOM），实例已完成以下的配置： 编译模板，把data里面的数据和模板生成html，完成了el和data 初始化，注意此时还没有挂在html到页面上。

#### mounted

挂在完成，也就是模板中的HTML渲染到HTML页面中，此时一般可以做一些ajax操作，mounted只会执行一次。

#### beforeUpdate

在数据更新之前被调用，发生在虚拟DOM重新渲染和打补丁之前，可以在该钩子中进一步地更改状态，不会触发附加地重渲染过程

#### updated（更新后）

在由于数据更改导致地虚拟DOM重新渲染和打补丁只会调用，调用时，组件DOM已经更新，所以可以执行依赖于DOM的操作，然后在大多是情况下，应该避免在此期间更改状态，因为这可能会导致更新无限循环，该钩子在服务器端渲染期间不被调用

#### beforeDestroy（销毁前）

在实例销毁之前调用，实例仍然完全可用，

1. 这一步还可以用this来获取实例，
2. 一般在这一步做一些重置的操作，比如清除掉组件中的定时器  和 监听的dom事件

#### destroyed（销毁后）

在实例销毁之后调用，调用后，所以的事件监听器会被移出，所有的子实例也会被销毁，该钩子在服务器端渲染期间不被调用
