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

## CompletableFuture常用方法

```java
completableFuture.get(); //方法阻塞，需要处理异常
completableFuture.get(2L, TimeUnit.SECONDS); //等待2秒，2秒后未执行完抛出超时异常
completableFuture.join(); //不需要抛出异常
completableFuture.getNow("xxx"); //方法未执行完，返回自定义xxx内容；否则返回方法返回参数
completableFuture.complete("completeValue"); //方法未执行完则立即打断并返回自定义内容，否则返回方法的返回值
```

## 对结果进行处理

计算结果存在依赖关系，线程串行化

### thenApply

计算结果存在依赖关系，线程串行化；由于存在依赖关系，某一步发生异常后，之后的操作都不会执行。

### handle

计算结果存在依赖关系，线程串行化；但是某一步发生异常，会继续执行。

## 对计算结果进行消费（thenAccept）

接收任务的处理结果，并消费处理，无返回结果

## thenRun，thenAccept，thenApply的区别

### thenRun(Runnable runnable)

任务A执行完执行任务B，==并且B不需要A的结果==，无返回值

### thenAccept(Consumer action)

任务A执行完执行B，==B需要A的结果==，但是任务B==无返回值==

### thenApply(Function fn)

任务A执行完执行B，==B需要A的结果==，同时任务B==有返回值==

## 使用线程池对thenRun，thenAccept，thenApply的影响

- 没有传入自定义线程池，都用默认线程池ForkJoinPool；
- 传入了自定义线程池，如果在执行第一个任务时传入了自定义线程，则
  - 调用thenRun方法执行第二个任务时，第二个任务和第一个任务共用一个线程池
  - 调用thenRunAsync执行第二个任务时，则第一个任务使用的是自定义线程池，第二个任务使用的是ForkJoinPool线程池

同理，thenAccept和thenApply也是这种情况。

# synchronized关键字

synchronized方法：锁对象是调用类的对象

静态synchronized方法：锁对象是类的Class对象

synchronized(obj)代码块：锁对象是obj

# 公平锁和非公平锁

默认为非公平锁，节省资源

# 可重入锁

隐式锁（即synchronized关键字修饰的锁）默认是可重入锁。

```java
        final Object o = new Object();
        
        new Thread(() -> {
            synchronized(o) {
                System.out.println("外部调用");
                synchronized (o) {
                    System.out.println("中间调用");
                    synchronized(o) {
                        System.out.println("最后调用");
                    }
                }
            }
        }).start();
```



显示锁（即Lock）也有ReentrantLock这样的可重入锁。(lock()和unlock()数量需要==一致==)

```java
        new Thread(() -> {
            lock.lock();
            try {
                System.out.println("外层调用");
                lock.lock();
                try {
                    System.out.println("内层调用");
                    lock.lock();
                } finally {
                    lock.unlock();
                }
            } finally {
                lock.unlock();
            }
            
            lock.unlock();
        }).start();
```

# 小结

![image-20230930161312855](.\images\image-20230930161312855.png)

# 线程中断机制

中断是一种协作协商机制，Java没有给中断增加任何语法，中断的过程完全需要程序员自己实现。若要中断一个线程，需要手动调用线程的interrupt方法，==该方法也仅仅是将线程对象的中断标识设置成true。

## 中断相关API方法

### public void interrupt()

实例方法interrupt()仅仅是设置线程的中断状态为true，发起一个协商而不会立即停止线程。

### public static boolean interrupted()

静态方法，Thread.interrupted();==返回当前线程的中断状态，测试当前线程是否已经被中断；将当前线程的中断状态设置清零并重新设置为false，清除线程的中断状态==。

### public boolean interrupted()

判断当前线程是否被中断（通过检查中断标志位）

## 中断机制示例

### 如何停止中断运行总的线程

#### 通过volatile变量实现

```java
public class InterruptDemo {
    static volatile boolean isStop = false;

    public static void main(String[] args) {
        new Thread(() -> {
            while (true) {
                if (isStop) {
                    System.out.println("isStop被设置为true，停止");
                    break;
                }

                System.out.println("hello volatile");
            }
        }).start();
        
        try {
            Thread.sleep(20);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        new Thread(() -> {
            isStop = true;
        }).start();
    }
}
```

#### 通过AtomicBoolean实现

```java
        new Thread(() -> {
            while (true) {
                if (atomicBoolean.get()) {
                    System.out.println("isStop被设置为true，停止");
                    break;
                }

                System.out.println("hello volatile");
            }
        }).start();

        try {
            Thread.sleep(20);
        } catch (Exception e) {
            e.printStackTrace();
        }

        new Thread(() -> {
            atomicBoolean.set(true);
        }).start();
```

#### 通过线程类自带的中断api实例方法实现

```java
    public static void main(String[] args) {
        Thread t1 = new Thread(() -> {
            while (true) {
                if (Thread.currentThread().isInterrupted()) {
                    System.out.println("isInterrupted 被设置为true，停止");
                    break;
                }

                System.out.println("hello isInterrupted");
            }
        });
        t1.start();

        try {
            Thread.sleep(20);
        } catch (Exception e) {
            e.printStackTrace();
        }

        new Thread(t1::interrupt).start();
    }
```

