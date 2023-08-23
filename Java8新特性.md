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

`map`:接收一个函数作为参数，改函数被应用到每个元素上，并映射成一个新的元素

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

Optional<T> reduce(BinaryOperator<T> accumulator)：第一次执行时，accumulator函数的第一个参数为流中的第一个元素，第二个参数为流中元素的第二个元素；第二次执行时，第一个参数为第一次函数执行的结果，第二个参数为流中的第三个元素；依次类推。
        T reduce(T identity, BinaryOperator<T> accumulator)：流程跟上面一样，只是第一次执行时，accumulator函数的第一个参数为identity，而第二个参数为流中的第一个元素。
        <U> U reduce(U identity,BiFunction<U, ? super T, U> accumulator,BinaryOperator<U> combiner)：在串行流(stream)中，该方法跟第二个方法一样，即第三个参数combiner不会起作用。在并行流(parallelStream)中,我们知道流被fork join出多个线程进行执行，此时每个线程的执行流程就跟第二个方法reduce(identity,accumulator)一样，而第三个参数combiner函数，则是将每个线程的执行结果当成一个新的流，然后使用第一个方法reduce(accumulator)流程进行规约。

```java
//经过测试，当元素个数小于24时，并行时线程数等于元素个数，当大于等于24时，并行时线程数为16
List<Integer> list = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24);
 
Integer v = list.stream().reduce((x1, x2) -> x1 + x2).get();
System.out.println(v);   // 300
 
Integer v1 = list.stream().reduce(10, (x1, x2) -> x1 + x2);
System.out.println(v1);  //310
 
Integer v2 = list.stream().reduce(0,
        (x1, x2) -> {
            System.out.println("stream accumulator: x1:" + x1 + "  x2:" + x2);
            return x1 - x2;
        },
        (x1, x2) -> {
            System.out.println("stream combiner: x1:" + x1 + "  x2:" + x2);
            return x1 * x2;
        });
System.out.println(v2); // -300
 
Integer v3 = list.parallelStream().reduce(0,
        (x1, x2) -> {
            System.out.println("parallelStream accumulator: x1:" + x1 + "  x2:" + x2);
            return x1 - x2;
        },
        (x1, x2) -> {
            System.out.println("parallelStream combiner: x1:" + x1 + "  x2:" + x2);
            return x1 * x2;
        });
System.out.println(v3); //197474048
```

> 收集操作

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

- ==方法体只有一句代码时大括号return和唯一一句代码的分号可以省略==

```java
String s1 = typeConver( (s) -> s + "a");
```

- 方法只有一个参数时小括号可以省略

```java
String s1 = typeConver( s -> s + "a");
```



