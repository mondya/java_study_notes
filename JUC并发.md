# 概念

并发（concurrent）：在同一个实体上的多个事件，在一台处理器上==同时==处理多个任务，同一时刻，只有一个事件在发生

并行（parallel）：在不同实体上的多个事件，在多台服务器上同时处理多个任务

进程：在系统中运行的任意一个程序就是一个进程，每一个进程有自己的内存空间和系统资源

线程：也被称为==轻量级进程==，在同一个进程中会有1个或者多个线程，是操作系统进行时序调度的基本单元

用户线程：是系统的工作线程，它会完成这个程序需要完成的业务操作

守护线程(Daemon Thread)：是一种特殊的线程，为其他线程服务，在后台完成一些系统性的服务，如果用户线程全部结束意味着程序需要完成的业务操作已经结束了，守护线程随着JVM一同结束工作。setDaemon(true)方法必须在start()之前设置，否则报错。垃圾回收线程就是一个守护线程。

# CompletableFuture

Future接口定义了操作==异步任务的一些方法==，它提供了一种==异步并行计算的功能==。如获取异步任务的执行结果、取消任务的执行、判断任务是否被取消、判断任务执行是否完毕等。(即Future接口可以为主线程开一个分支任务，专门为主线程处理耗时和费力的复杂业务)

Future接口的缺点：

- ==get阻塞==：一旦调用get()方法求结果，计算没有完成会造成程序阻塞
- ==isDone()轮询==：轮询的方式会耗费CPU资源

```java
// FutureTask<V> implements RunnableFuture<V> 
// public interface RunnableFuture<V> extends Runnable, Future<V>
public class FutureApiDemo {
    public static void main(String[] args) throws Exception {
       // FutureTask同时具备Runnable和Future<V>的属性
        FutureTask<String> stringFutureTask = new FutureTask<>(() -> {
            System.out.println(Thread.currentThread().getName() + "\t" + ".... come in");
            TimeUnit.SECONDS.sleep(5);
            return "task over";
        });
        
        
        Thread thread = new Thread(stringFutureTask, "t1");
        thread.start();

        System.out.println(Thread.currentThread().getName() + "主线程 ...");
        // 调用get会阻塞
//        System.out.println(stringFutureTask.get());
        // 3s无返回结果抛出超时异常
        System.out.println(stringFutureTask.get(3, TimeUnit.SECONDS));
    }
}
```

## CompletableFuture对Future的改进

```java
public class CompletableFuture<T> implements Future<T>, CompletionStage<T> 
```

CompletionStage代表异步计算过程中的某个阶段，一个阶段完成可能会触发另外一个阶段；一个阶段的计算执行可以是一个Funtion，Consumer或者Runnable。比如stage.thenApply(x -> square(x)).thenAccept(x -> System.out.println(x)).thenRun(() -> System.out.println())；一个阶段的执行可能是被单个阶段的完成触发，也可能是由多个阶段一起触发。

```java
// 在CompletableFuture类中，有4个常用的静态方法

// 有返回值
public static <U> CompletableFuture<U> supplyAsync(Supplier<U> supplier);
public static <U> CompletableFuture<U> supplyAsync(Supplier<U> supplier, Executor executor);

// 无返回值
public static CompletableFuture<Void> runAsync(Runnable runnable);
public static CompletableFuture<Void> runAsync(Runnable runnable,
                                                   Executor executor) ;

// 没有指定Executor的方法，直接使用默认的ForkJoinPool.commonPool()作为他的线程池执行异步代码；如果指定线程池，则使用我们自定义的线程池执行异步代码。
```

```java
        // 使用自定义的线程池，默认的线程池是守护线程，main线程执行太快导致异步任务没有执行
		ExecutorService thr = Executors.newFixedThreadPool(10);

        CompletableFuture.supplyAsync(() -> {
            System.out.println(Thread.currentThread().getName() + "...");
            try {
                TimeUnit.SECONDS.sleep(5);
                System.out.println("5s 后出结果");
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            
            if (true) {
                throw new RuntimeException("exception");
            }

            return "hello";
        }, thr).whenComplete((a, exception) -> {
            System.out.println("计算完成" + exception);
        }).exceptionally( e -> {
            e.printStackTrace();
            System.out.println("异常");
            return "";
        });
        // 这里whenComplete和exceptionally都会打印错误信息，区别：exceptionally有返回值
        // 资源回收
        thr.shutdown();
```

