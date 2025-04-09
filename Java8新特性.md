# Java8 stream的详细用法

## 筛选(filter)，去重(distinct)，跳过(skip)，截取(limit)

```java
Stream<Integer> stream = Stream.of(6, 4, 6, 7, 3, 9, 8, 10, 12, 14, 14);
 
Stream<Integer> newStream = stream.filter(s -> s > 5) //6 6 7 9 8 10 12 14 14  过滤
        .distinct() //6 7 9 8 10 12 14  去重
        .skip(2) //9 8 10 12 14	跳过2个元素
        .limit(2); //9 8	获取2个元素
```

## 映射(map)

`map`:接收一个函数作为参数，该函数被应用到每个元素上，并映射成一个新的元素

`flatmap`:接收一个函数作为参数，将流中的每个值都换成另一个流，然后把所有流连接成一个流

```java
String[] strings = {"one", "two", "three", "five", "", "four", "four", "two"};
List<String> list = Arrays.asList(strings);
// map()中函数不能返回bool类型，否则报错
List<String> stringsList = list.stream().map(s -> s.substring(0,2)).collect(Collectors.toList());



Stream<String> s3 = list.stream().flatMap(s -> {
    //将每个元素转换成一个stream
    String[] split = s.split(",");
    Stream<String> s2 = Arrays.stream(split);
    return s2;
});
s3.forEach(System.out::println); // a b c 1 2 3
```

## 排序

`sorted`:自然排序

`sorted(Comparator com)`：定制排序，自定义Comparator排序器

```java
        List<String> sortList = list.stream().filter(string -> !string.isEmpty()).sorted((String a, String b) -> Integer.compare(a.length(), b.length())).collect(Collectors.toList());
        System.out.println("sortList:" + sortList);

        list.sort((String a, String b) -> Integer.compare(a.length(), b.length()));
        System.out.println("list:" + list);
```

## peek消费

`peek`：如同map,能得到流中的每一个元素。但是map接收的是Function函数，有返回值；而peek接收的是Consumer表达式，没有返回值

```java
Student s1 = new Student("aa", 10);
Student s2 = new Student("bb", 20);
List<Student> studentList = Arrays.asList(s1, s2);
 
studentList.stream()
        .peek(o -> o.setAge(100))
        .forEach(System.out::println);   
 
//结果：
Student{name='aa', age=100}
Student{name='bb', age=100}  
```

## 匹配，聚合操作

`allMatch`：接收一个 Predicate 函数，当流中每个元素都符合该断言时才返回true，否则返回false

`noneMatch`：接收一个 Predicate 函数，当流中每个元素都不符合该断言时才返回true，否则返回false

`anyMatch`：接收一个 Predicate 函数，只要流中有一个元素满足该断言则返回true，否则返回false

`findFirst`：返回流中第一个元素

`findAny`：返回流中的任意元素

`count`：返回流中元素的总个数

`max`：返回流中元素最大值

`min`：返回流中元素最小值

```java
List<Integer> list = Arrays.asList(1, 2, 3, 4, 5);
 
boolean allMatch = list.stream().allMatch(e -> e > 10); //false
boolean noneMatch = list.stream().noneMatch(e -> e > 10); //true
boolean anyMatch = list.stream().anyMatch(e -> e > 4);  //true
 
Integer findFirst = list.stream().findFirst().get(); //1
Integer findAny = list.stream().findAny().get(); //1
 
long count = list.stream().count(); //5
Integer max = list.stream().max(Integer::compareTo).get(); //5
Integer min = list.stream().min(Integer::compareTo).get(); //1
```

## 规约操作（reduce）

对流中的数据按照指定的计算方式计算出一个结果。

reduce的作用是把stream中的元素组合起来，我们可以传入一个初始值，它会按照我们的计算方式依次拿流中的元素和初始化值进行计算，计算结果再和后面的元素计算。

T reduce(T identity, BinaryOperator<T> accumulator);

```java
T result = identity;
for (T element : this stream)      
    result = accumulator.apply(result, element)  
return result
```

不带参数的reduce（相当于默认把第一个参数作为第一个元素）

```java
boolean foundAny = false;  
T result = null;  
for (T element : this stream) {      
    if (!foundAny) {          
        foundAny = true;          
        result = element;      
    } else          
        result = accumulator.apply(result, element);  
}  
return foundAny ? Optional.of(result) : Optional.empty()
```

Optional<T> reduce(BinaryOperator<T> accumulator)：第一次执行时，accumulator函数的第一个参数为流中的第一个元素，第二个参数为流中元素的第二个元素；第二次执行时，第一个参数为第一次函数执行的结果，第二个参数为流中的第三个元素；依次类推。
        T reduce(T identity, BinaryOperator<T> accumulator)：流程跟上面一样，只是第一次执行时，accumulator函数的第一个参数为identity，而第二个参数为流中的第一个元素。
        <U> U reduce(U identity,BiFunction<U, ? super T, U> accumulator,BinaryOperator<U> combiner)：在串行流(stream)中，该方法跟第二个方法一样，即第三个参数combiner不会起作用。在并行流(parallelStream)中,我们知道流被fork join出多个线程进行执行，此时每个线程的执行流程就跟第二个方法reduce(identity,accumulator)一样，而第三个参数combiner函数，则是将每个线程的执行结果当成一个新的流，然后使用第一个方法reduce(accumulator)流程进行规约。==并行流中的计算需要满足结合律和可交换性：加法和乘法满足，减法和除法不满足（计算结果不是预期的结果）==

```java
//经过测试，当元素个数小于24时，并行时线程数等于元素个数，当大于等于24时，并行时线程数为16
List<Integer> list = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24);
 
Integer v = list.stream().reduce((x1, x2) -> x1 + x2).get();
System.out.println(v);   // 1+2+3+...+24 = 300
 
Integer v1 = list.stream().reduce(10, (x1, x2) -> x1 + x2);
System.out.println(v1);  //10 + 1 + 2+...+24 = 310
 
Integer v2 = list.stream().reduce(0,
        (x1, x2) -> {
            System.out.println("stream accumulator: x1:" + x1 + "  x2:" + x2);
            return x1 - x2;
        },
        (x1, x2) -> {
            System.out.println("stream combiner: x1:" + x1 + "  x2:" + x2);
            return x1 * x2;
        });
System.out.println(v2); // 0-1-2-3-4...-24 = -300, stream combiner不起作用

// 并行执行 0 -1 = -1, 0-2 = -2 , 0-3 = -3...，然后用把每一结果通过x1 * x2计算
Integer v3 = list.parallelStream().reduce(0,
        (x1, x2) -> {
            System.out.println("parallelStream accumulator: x1:" + x1 + "  x2:" + x2);
            return x1 - x2;
        },
        (x1, x2) -> {
            System.out.println("parallelStream combiner: x1:" + x1 + "  x2:" + x2);
            return x1 * x2;
        });
System.out.println(v3); //197474048 -1*-2*。。。*-24溢出

// 使用reduce求最大值
Integer v4 = list.stream().reduce(Integer.MIN_VALUE, (result, element) -> result < element ? element : result);

// 使用reduce求最小值
Integer v5 = list.stream().reduce(Integer.MAX_VALUE, (result, element) -> result < element ? result : element);
```

## 收集操作(Collector)

Collector工具库:  Collectors

```java
Student s1 = new Student("aa", 10,1);
Student s2 = new Student("bb", 20,2);
Student s3 = new Student("cc", 10,3);
List<Student> list = Arrays.asList(s1, s2, s3);
 
//装成list
List<Integer> ageList = list.stream().map(Student::getAge).collect(Collectors.toList()); // [10, 20, 10]2
 
//转成set
Set<Integer> ageSet = list.stream().map(Student::getAge).collect(Collectors.toSet()); // [20, 10]
 
//转成map,注:key不能相同，否则报错
Map<String, Integer> studentMap = list.stream().collect(Collectors.toMap(Student::getName, Student::getAge)); // {cc=10, bb=20, aa=10}
 
//字符串分隔符连接
String joinName = list.stream().map(Student::getName).collect(Collectors.joining(",", "(", ")")); // (aa,bb,cc)
 
//聚合操作
//1.学生总数	
Long count = list.stream().collect(Collectors.counting()); // 3
//2.最大年龄 (最小的minBy同理)
Integer maxAge = list.stream().map(Student::getAge).collect(Collectors.maxBy(Integer::compare)).get(); // 20
//3.所有人的年龄
Integer sumAge = list.stream().collect(Collectors.summingInt(Student::getAge)); // 40
//4.平均年龄
Double averageAge = list.stream().collect(Collectors.averagingDouble(Student::getAge)); // 13.333333333333334
// 带上以上所有方法
DoubleSummaryStatistics statistics = list.stream().collect(Collectors.summarizingDouble(Student::getAge));
System.out.println("count:" + statistics.getCount() + ",max:" + statistics.getMax() + ",sum:" + statistics.getSum() + ",average:" + statistics.getAverage());
 
//分组
Map<Integer, List<Student>> ageMap = list.stream().collect(Collectors.groupingBy(Student::getAge));
//多重分组,先根据类型分再根据年龄分
Map<Integer, Map<Integer, List<Student>>> typeAgeMap = list.stream().collect(Collectors.groupingBy(Student::getType, Collectors.groupingBy(Student::getAge)));
 
//分区
//分成两部分，一部分大于10岁，一部分小于等于10岁
Map<Boolean, List<Student>> partMap = list.stream().collect(Collectors.partitioningBy(v -> v.getAge() > 10));
 
//规约
Integer allAge = list.stream().map(Student::getAge).collect(Collectors.reducing(Integer::sum)).get(); //40
```

# Date用法

```java
package utils;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Date;


public class TimeUtils {
    //日期格式:yyyy-MM-dd
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    //日期+时间格式：yyyy-MM-dd HH:mm:ss
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    //时间格式: HH:mm:ss
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");

    /**
     * LocalDate转Date
     */
    static Date localDateToDate(LocalDate date){
        return Date.from(date.atStartOfDay(ZoneOffset.ofHours(8)).toInstant());
    }

    /**
     * LocalDateTime转Date
     */
    static Date localDateTimeToDate(LocalDateTime dateTime){
        return Date.from(dateTime.atZone(ZoneOffset.ofHours(8)).toInstant());
    }

    /**
     * Date转LocalDate
     */
    static LocalDate dateToLocalDate(Date date){
        return date.toInstant().atZone(ZoneOffset.ofHours(8)).toLocalDate();
    }

    /**
     * Date转LocalDateTime
     */
    static LocalDateTime dateToLocalDateTime(Date date){
        return date.toInstant().atZone(ZoneOffset.ofHours(8)).toLocalDateTime();
    }

    /**
     * LocalDate转时间戳
     */
    static long localDateToTimeStamp(LocalDate localDate){
        return localDate.atStartOfDay(ZoneOffset.ofHours(8)).toInstant().toEpochMilli();
    }

    /**
     * LocalDateTime转时间戳
     */
    static long localDateTimeToTimeStamp(LocalDateTime localDateTime){
        return localDateTime.atZone(ZoneOffset.ofHours(8)).toInstant().toEpochMilli();
    }

    /**
     * 时间戳转LocalDate
     */
    static LocalDate timeStampToLocalDate(Long timeStamp){
        return Instant.ofEpochMilli(timeStamp).atZone(ZoneOffset.ofHours(8)).toLocalDate();
    }

    /**
     * 时间戳转LocalDateTime
     */
    static LocalDateTime timeStampToLocalDateTime(Long timeStamp){
        return Instant.ofEpochMilli(timeStamp).atZone(ZoneOffset.ofHours(8)).toLocalDateTime();
    }

    /**
     * 把yyyy-MM-dd格式时间字符串转化为LocalDate
     * @param time "yyyy-MM-dd"
     * @return
     */
    static LocalDate getLocalDate(String time){
        return LocalDate.parse(time,DATE_FORMATTER);
    }
    
    static LocalDateTime getLocalDateTime(String time){
        return LocalDateTime.parse(time,DATE_TIME_FORMATTER);
    }

    /**
     * 获取两个相隔日期之间的年数(可以用来计算年龄)
     * @param startDate 开始时间
     * @param endDate 结束时间
     * @return
     */
    static long getYears(LocalDate startDate,LocalDate endDate){
        return startDate.until(endDate, ChronoUnit.YEARS);
    }

    /**
     * 获取两个相隔日期之间的月数
     * @param startDate 开始时间
     * @param endDate 结束时间
     * @return
     */
    static long getMonths(LocalDate startDate,LocalDate endDate){
        return startDate.until(endDate, ChronoUnit.MONTHS);
    }

    /**
     * 获取两个相隔日期之间的天数
     * @param startDate 开始时间
     * @param endDate 结束时间
     * @return
     */
    static long getDays(LocalDate startDate,LocalDate endDate){
        return startDate.until(endDate,ChronoUnit.DAYS);
    }
    /**
     * 获取yyyy-MM-dd格式字符串时间的时间戳
     * @param time 时间
     * @return
     */
    static long getDateStamp(String time){
       return getLocalDate(time).atStartOfDay(ZoneOffset.ofHours(8)).toInstant().toEpochMilli();
    }

    /**
     * yyyy-MM-dd HH:mm:ss格式字符串转为时间戳
     * @param time
     * @return
     */
    static long getDateTimeStamp(String time){
        return getLocalDateTime(time).atZone(ZoneOffset.ofHours(8)).toInstant().toEpochMilli();
    }

    public static String getSectionDayStr(Long startDate, Long endDate){
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        String startLocalDate = Instant.ofEpochMilli(startDate).atOffset(ZoneOffset.ofHours(8)).toLocalDate().format(dateTimeFormatter);
        String endLocalDate = Instant.ofEpochMilli(endDate).atOffset(ZoneOffset.ofHours(8)).toLocalDate().format(dateTimeFormatter);
        return startLocalDate + "-" + endLocalDate;
    }
    
    public static void main(String[] args){
        //时间转化思路
        /**
         * date转localDate/localDateTime: 先转为瞬时实例Instant,在通过时区设置atZone/atOffset转化为ZoneDateTime/OffsetDateTime,这两个类都有toLocalDate,toLocalDateTime,toLocalTime方法
         * localDate/localDateTime转时间戳: localDate.atStartOfDay()/localDateTime.atZone,转化为ZoneDateTime/OffsetDateTime,然后转为Instant实例，调用toEpochMilli()方法获得时间戳
         * 时间戳转localDate/localDateTime: Instant.ofEpochMilli()获取Instant实例，通过atZone/atOffset转化为ZoneDateTime/OffsetDateTime
         * localDate/localDateTime转Date: 把localDate/localDateTime通过atStartOfDay(),atZone方法转为ZoneDateTime/OffsetDateTime，然后toInstant返回实例，使用Date.form()方法返回Date实例
         * 日期计算使用minus()和plus方法
         * 总结：转化为ZoneDateTime或者OffsetDateTime;
         * ZoneDateTime和OffsetDateTime之间可以通过toXXX方法互相转换，Instant通过atZone()或atOffset()转为ZoneDateTime和OffsetDateTime，
         * ZoneDateTime和OffsetDateTime也可以通过toInstant()方法获得Instant
         */
    }
}

```

# [java8 四大核心函数式接口Function、Consumer、Supplier、Predicate](https://www.cnblogs.com/powerwu/articles/10365446.html)

只有一个抽象方法的接口称之为函数接口，JDK的函数式接口都加上了@FunctionalInterface注解进行标识。但是无论是否加上该注解只要接口中只有一个抽象方法，都是函数式接口。

> Function<T,R>

T：入参类型；R：出参类型

调用方法：R apply(T t); 

定义函数示例：Function<Integer, Integer> func = p -> p * 10;  // 输出入参的10倍

调用函数示例：func.apply(10);  // 结果100

> Consumer<T>

T：入参类型；没有出参

调用方法：void accept(T t);

定义函数示例：Consumer<String> consumer= p -> System.out.println(p);  // 因为没有出参，常用于打印、发送短信等消费动作

调用函数示例：consumer.accept("18800008888");

> Supplier<T>

T：出参类型；没有入参

调用方法：T get();

定义函数示例：Supplier<Integer> supplier= () -> 100;  // 常用于业务“有条件运行”时，符合条件再调用获取结果的应用场景；运行结果须提前定义，但不运行。

调用函数示例：supplier.get();

> Predicate<T>

T：入参类型；出参类型是Boolean

调用方法：boolean test(T t);

定义函数示例：Predicate<Integer> predicate = p -> p % 2 == 0;  // 判断是否、是不是偶数

调用函数示例：predicate.test(100);  // 运行结果true

## 常见的默认方法

- and

我们在使用Predicate接口时可能需要进行判断条件的拼接，而and方法相当于使用&&来拼接两个判断条件

```java
// 一般在调用包中方法，我们可以使用 && 拼接， 自定义方法可以使用and

    public static void printNum2(IntPredicate predicate, IntPredicate predicate2) {
        int[] arr = {1,2,3,4,5,6,7,8,9,10};

        for (int i : arr) {
            if (predicate.and(predicate2).test(i)) {
                System.out.println(i);
            }
        }
    }

        printNum2(new IntPredicate() {
            @Override
            public boolean test(int value) {
                return value > 0;
            }
        }, new IntPredicate() {
            @Override
            public boolean test(int value) {
                return value < 10;
            }
        });

```



- or

// 同and方式， 用 || 拼接

- negate

取反，相当于在判断前加上！

# Lambda表达式

## 函数式编程只关注参数和具体的操作

```java
public class LambdaDemo01 {
    public static void main(String[] args) {
        // 基本写法
        int num1 = calculateNum(new IntBinaryOperator() {
            @Override
            public int applyAsInt(int left, int right) {
                return left + right;
            }
        });
        
        // 进阶写法
        int num2 = calculateNum((int left, int right) -> {
             return left + right;
        });
        // 或者
        int num3 = calculateNum((int left, int right) -> left + right);

        
        // 最终写法
        int num4 = calculateNum(Integer::sum);
        
        printNum(new IntPredicate() {
            @Override
            public boolean test(int value) {
                return value%2 == 0;
            }
        });
        
        // 泛型，根据返回类型
        Integer conver2Integer1 = typeConver(new Function<String, Integer>() {
            @Override
            public Integer apply(String s) {
                return Integer.parseInt(s);
            }
        });

        String s1 = typeConver((String s) -> {
            return s + "a";
        });

    }
    
    public static int calculateNum(IntBinaryOperator operator) {
        int a = 10;
        int b = 20;
        return operator.applyAsInt(10, 20);
    }
    
    public static void printNum(IntPredicate predicate) {
        int[] arr = {1,2,3,4,5,6,7,8,9,10};

        for (int i : arr) {
            if (predicate.test(i)) {
                System.out.println(i);
            }
        }
    }
    
    // 泛型
    public static <R> R typeConver(Function<String, R> function) {
        String str = "1234";
        R result = function.apply(str);
        return result;
    }
}
```

## 省略规则

- 参数类型可以省略

```java
String s1 = typeConver( (s) -> {
            return s + "a";
        });
```

- ==方法体只有一句代码时大括号，return和唯一一句代码的分号可以省略==

```java
String s1 = typeConver( (s) -> s + "a");
```

- 方法只有一个参数时小括号可以省略

```java
String s1 = typeConver( s -> s + "a");
```

# Optional

使用Optional的静态方法==ofNullable()==来把数据封装成一个Optional方法，无论传入的参数是否为null都不会出现问题。

如果==确定一个对象不是空==可以使用==Opational.of()==方法把数据封装成Optional对象

==推荐使用ofNullable()方法，它内部会判断空对象==

```java
    public static void main(String[] args) {
        Optional<Author> author = getAuthor();
        // isPresent() 判断内部value值是否为空
        if (!author.isPresent()) {
            System.out.println("异常");
        }
        
        // 过滤
        Optional<Author> author1 = author.filter(s -> s.getAge() > 0);

        // map获取数据，返回一个Optional
        Optional<List<Book>> books = author.map(Author::getBookList);
        books.ifPresent(System.out::println);
    }
    
    public static Optional<Author> getAuthor() {
        Author author = new Author(1L, "蒙多", 33, "一个从菜刀中明悟哲理的祖安人", null);
        return Optional.ofNullable(author);
    }
```

# 方法引用

类名或者对象名::方法名

## 引用类的静态方法

前提：如果在重写方法时，方法体中只有==一行代码==，并且这行代码==调用了某个类的静态方法==，并且我们==把要重写的抽象方法中所有的参数都按照顺序传入了这个静态方法中==，这个时候我们就可以引用类的静态方法。

```java
    private static void testStatic() {
        List<Author> authors = getAuthors();
        List<String> collect = authors.stream().map(s -> s.getId()).map(new Function<Long, String>() {
            @Override
            public String apply(Long aLong) {
                return String.valueOf(aLong);
            }
        }).collect(Collectors.toList());
        // 转换成
        authors.stream().map( s -> s.getId()).map(String::valueOf).collect(Collectors.toList());
    }
```

## 引用对象的实例方法

类名::方法名

前提：重写方法时，方法体中只有==一行代码==，并且这行代码==调用了某个对象的成员方法==，并且我们==把要重写的抽象方法中所有的参数都按照顺序传入这个成员方法中==，这个时候我们可以引用对象的实例方法。

```java
// 上面的方法
authors.stream().map(Author::getId).map(String::valueOf).collect(Collectors.toList());

    interface UseString{
        String subStr(String str, int start, int end);
    }

    private static void testMethod(String str, UseString useString) {
        int start = 0;
        int end = 1;
        useString.subStr(str, start, end);
    }

    testMethod("hello", new UseString() {
            @Override
            public String subStr(String str, int start, int end) {
                return str.substring(start, end);
            }
        });
// 转换成
	testMethod("hello", String::substring);
```

## 构造器引用

类名::new

前提：方法体中只有==一行代码==，并且这行代码调用了==某个类的构造方法==，并且我们==把要重写的抽象方法中所有的参数都按照顺序传入这个构造方法中==，这个时候我们可以引用对象的构造器

# 基本数据类型优化

在stream流的操作中，使用的都是引用数据类型，即使是整数，操作的也是他们的包装类。包装类在进行一些运算时，涉及到自动拆箱和自动装箱，这会损耗一些性能，针对这一个问题，Stream提供了专门针对基本数据类型的方法。例如，mapToInt, mapToLong, mapToDouble, flatMapToInt, flatMapToLong，flatMapToDouble。

# 并行流

当流中有大量元素时，我们可以使用并行流去提高操作的效率。并行流就是把任务分配给多个线程去完成。

```java
Integer[] integers = {1,2,3,4};
Arrays.stream(integers).parallel().map(s -> String.valueOf(s) + Thread.currentThread().getName()).forEach(System.out::println);

// 结果，分成多个线程执行，没有parallel()只会在main线程中执行
3main
4main
1ForkJoinPool.commonPool-worker-23
2ForkJoinPool.commonPool-worker-19
```