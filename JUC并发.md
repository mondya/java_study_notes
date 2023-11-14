# 概念

并发（concurrent）：在同一个实体上的多个事件，在一台处理器上==同时==处理多个任务，同一时刻，只有一个事件在发生

并行（parallel）：在不同实体上的多个事件，在多台服务器上同时处理多个任务

进程：在系统中运行的任意一个程序就是一个进程，每一个进程有自己的内存空间和系统资源

线程：也被称为==轻量级进程==，在同一个进程中会有1个或者多个线程，是操作系统进行时序调度的基本单元

用户线程：是系统的工作线程，它会完成这个程序需要完成的业务操作

守护线程(Daemon Thread)：是一种特殊的线程，为其他线程服务，在后台完成一些系统性的服务，如果用户线程全部结束意味着程序需要完成的业务操作已经结束了，守护线程随着JVM一同结束工作。setDaemon(true)方法必须在start()之前设置，否则报错。垃圾回收线程就是一个守护线程。

# CompletableFuture

Future接口

定义了操作==异步任务的一些方法==，它提供了一种==异步并行计算的功能==。如获取异步任务的执行结果、取消任务的执行、判断任务是否被取消、判断任务执行是否完毕等。(即Future接口可以为主线程开一个分支任务，专门为主线程处理耗时和费力的复杂业务)

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

```java
    public static void main(String[] args) {
        ExecutorService executorService = Executors.newFixedThreadPool(5);
        try {
            CompletableFuture<Void> future = CompletableFuture.supplyAsync(() -> {
                try {
                    Thread.sleep(20);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                System.out.println("任务1 ： " + Thread.currentThread().getName());
                return "a";
            }, executorService).thenRunAsync(() -> {
                try {
                    Thread.sleep(20);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                System.out.println("任务2 ： " + Thread.currentThread().getName());
            }).thenRunAsync(() -> {
                try {
                    Thread.sleep(20);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                System.out.println("任务3 ： " + Thread.currentThread().getName());
            });



            System.out.println(future.get(2L, TimeUnit.SECONDS));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            executorService.shutdown();
        }
    }

// 结果
任务1 ： pool-1-thread-1
任务2 ： ForkJoinPool.commonPool-worker-19
任务3 ： ForkJoinPool.commonPool-worker-19
null
```



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

中断是一种协作协商机制，Java没有给中断增加任何语法，中断的过程完全需要程序员自己实现。若要中断一个线程，需要手动调用线程的interrupt方法，==该方法也仅仅是将线程对象的中断标识设置成true==。

## 中断相关API方法

### public void interrupt()

实例方法interrupt()仅仅是设置线程的中断状态为true，发起一个协商而不会立即停止线程。

### public static boolean interrupted()

静态方法，Thread.interrupted();==返回当前线程的中断状态，测试当前线程是否已经被中断；将当前线程的中断状态设置清零并重新设置为false，清除线程的中断状态==。

### public boolean interrupted()

判断当前线程是否被中断（通过检查中断标志位）

## 中断机制示例

### 如何停止中断运行中的线程

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

           //在这加入Thread.sleep()导致死循环，调用wait()，join()，sleep()等方法，中断状态被清除，即值被设置成false
 //       try {
 //           Thread.sleep(20);
 //       } catch (Exception e) {
 //           e.printStackTrace();
 //       }
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

## 小总结

- 如何线程处于正常活动状态，那么会将该线程的中断标志设置为true，被设置中断标志的线程将继续正常运行，不受影响。所以，interrupt()并不能真正的中断线程，需要被调用的线程自己进行配合。
- 如果线程处于被阻塞状态（==即调用wait()，join()，sleep()等方法，中断状态被清除==），在别的线程总调用当前线程的interrupt方法，那么线程将立即退出被阻塞状态，并抛出一个InterruptException异常。

# 线程等待唤醒机制

## wait和notify

Object类的wait和notify方法实现线程等待和唤醒(synchronized + wait + notify)

```java
public class LockSupportDemo {
    public static void main(String[] args) {
        Object o = new Object();
        new Thread(() -> {
            synchronized (o) {
                try {
                    System.out.println("come in");
                    o.wait();
                    System.out.println("notify");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

        try {
            Thread.sleep(1000);
        } catch (Exception e) {
            e.printStackTrace();
        }


        new Thread(() -> {
            synchronized (o) {
                o.notify();
                System.out.println("唤醒");
            }
        }).start();

    }
}
// wait和nitify需要配合synchronized使用，任何一处未加synchronized都会报错
// Exception in thread "Thread-1" java.lang.IllegalMonitorStateException
// 结果
come in
唤醒
notify
```

## await和signal

Condition接口中的await和signal方法实现线程的等待和唤醒(lock + await + signal)

```java
        Lock lock = new ReentrantLock();

        Condition condition = lock.newCondition();
        
        new Thread(() -> {
            lock.lock();
            try {
                System.out.println("come in");
                condition.await();
                System.out.println("come in 2");
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }
        }).start();
        
        new Thread(() -> {
            lock.lock();
            try {
                System.out.println("发出通知");
                condition.signal();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }
        }).start();

// 结果
come in
发出通知
come in 2
```

## Object.wait()和Condition.await()使用的限制条件

线程先要获得并持有锁，必须在锁块（synchronized或Lock）中

必须要先等待后唤醒，线程才能够被唤醒

## part和unpart

LockSupport类中的part等待和unpart唤醒；park()和unpark()的执行顺序对代码无影响。==注意点：park()和unpark()需要一一对应并且一个线程只有一个LockSupport.park()方法，因为unpark()方法发布的许可证不会累积，只有1个，只能通行一个park()，如果一个线程内有多个park()和多个unpark()，那么最终只有一个park()会通行，剩下的park()阻塞线程。==

```java
        Thread t1 = new Thread(() -> {
            System.out.println("come in");
            LockSupport.park();

            System.out.println("唤醒");
        });
        t1.start();

        
        new Thread(() -> {
            LockSupport.unpark(t1);
        }).start();

// 结果
come in
唤醒
```

# JMM规范

## 可见性

指当一个线程修改了某一个共享变量的值，其他线程是否能够立即知道该变更，JMM规定了所有的变量都存储在==主内存==中。

系统主内存共享变量数据修改被写入的时机是不确定的，==多线程并发下很可能会出现脏读==，所以每个线程都有自己的==工作内存==，线程自己的工作内存中保存了该线程使用的变量的==主内存副本拷贝==，线程对变量的所有操作（读取，赋值等）都必须在线程自己的工作内存中进行，而不能直接读写主内存中的变量。不同线程之间也无法直接访问对方工作内存中的变量，线程间变量值的传递都需要通过主内存来完成。

## 原子性

指一个操作是不可打断的，即多线程环境下，操作不能被其他线程干扰

## 有序性

对于一个线程的执行代码而言，为了提升性能，编译器和处理器通常会对指令序列进行重新排序。Java规范规定JVM线程内部维持顺序化语义，即只要线程的最终结果和它顺序化执行的结果相同，那么指令的执行顺序可以与代码顺序不一致，此过程叫做==指令的重排序==。有序性禁止指令的重排序

## 小总结

- 定义的所有共享变量都存储在==物理主内存==中
- 每个线程都有自己独立的工作内容，里面保存该线程使用到的变量的副本（主内存中该变量的一份拷贝）
- 线程对共享变量所有的操作都必须先在自己的工作内存中进行后写回主内存，不能直接从主内存中读写
- 不同线程之间也无法直接访问其他线程的工作内存中的变量，线程之间的变量值的传递需要通过主内存来进行

# volatile

## 两大特性（可见性和有序性）

- 当写一个volatile变量时，JMM会把该线程对应的本地内存中的共享变量值==立即刷新回主内存中==
- 当读一个volatile变量时，JMM会把该线程对应的本地内存设置为无效，重新回到主内存中读取最新共享变量

所以volatile的写内存语义是直接刷新到主内存中，读的内存语义是直接从主内存中读取

## 内存屏障

内存屏障（也称内存栅栏，屏障指令等，是一类同步屏障指令，是CPU或编译器在对内存随机访问的操作中的一个同步点，使得此点之前的所有读写操作都执行后才可以开始执行此点之后的操作），避免代码的重排序。内存排序就是一种JVM指令，Java内存模型的重排规则会==要求Java编译器在生成JVM指令时插入特定的内存屏障指令==，通过内存屏障指令，volatile实现了Java内存模型中的可见性和有序性，但是==volatile无法保证原子性。==

## 屏障的插入策略

![image-20231004174555118](.\images\image-20231004174555118.png)

| 第一个操作 | 第二个操作：普通读写 | 第二个操作：volatile读 | 第二个操作：volatile写 |
| ---------- | -------------------- | ---------------------- | ---------------------- |
| 普通读写   | 可以重排             | 可以重排               | 不可以重排             |
| volatile读 | 不可以重排           | 不可以重排             | 不可以重排             |
| volatile写 | 可以重排             | 不可以重排             | 不可以重排             |

- 当第一个操作为volatile读时，不论第二个操作是什么，都不能重排序。这个操作保证了volatile==读之后==的操作不会被重排到volotile读之前。
- 当第二个操作为volatile写时，不论第一个操作是什么，都不能重排序。这个操作保证了volatile==写之前==的操作不会被重排到volatile写之后。
- 当第一个操作为volatile写时，第二个操作为volatile读时，不能重排。

> 在每个volatile读操作的后面插入一个LoadLoad屏障，一个LoadStore屏障

![image-20231004183142318](.\images\image-20231004183142318.png)

> 在每个volatile写操作前面插入一个StoreStore屏障，后面插入一个StoreLoad屏障

![image-20231004183245071](.\images\image-20231004183245071.png)

## 可见性示例

```java
public class VolatileDemo {
    // static boolean flag = true，当参数没有volatile关键字修饰，flag is false 不会打印，线程没有停止
    static volatile boolean  flag = Boolean.TRUE;

    public static void main(String[] args) {
        new Thread(() -> {
            System.out.println("come in");
            while (flag) {
                
            }

            System.out.println("flag is false");
        }).start();
        
        
        try {
            Thread.sleep(200);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        flag = Boolean.FALSE;
    }
}
```



![image-20231004213330188](.\images\image-20231004213330188.png)

read：作用于主内存，将变量的值从主内存传输到工作内存，主内存到工作内存

load：作用于工作内存，将read从主内存传输的变量值放入工作==内存变量副本==中，即数据加载

use：作用于工作内存，将工作内存变量副本的值传递给执行引擎，每当JVM遇到需要该变量的字节码指令时会执行该操作

assign：作用于工作内存，将从执行引擎接收到的值赋值给工作内存变量，每当JVM遇到一个给变量赋值字节码指令时会执行该操作

store：作用于工作内存，将赋值完毕的工作变量的值写回给主内存

write：作用于==主内存==，将store传输过来的变量值赋值给主内存中的变量

lock和unlock作用于主内存，加锁

## 无原子性示例

```java
class MyNum {
    volatile int number;
    
    public void add() {
        number++;
    }
}


    public static void main(String[] args) {
        
        MyNum myNum = new MyNum();
        for (int i = 1; i <= 10; i++) {
            new Thread(() -> {
                for (int j = 1; j <= 1000; j++) {
                    myNum.add();
                }
            }, String.valueOf(i)).start();
        }
        
        try {
            Thread.sleep(2000);
        }catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println(myNum.number);
    }
// 每次输出的结果都不同；说明add方法没有加锁的情况下，volatile修饰的变量不能保证原子性
```

对于volatile变量具有可见性，JVM只是保证从主内存加载到工作内存的值是最新的，但是在多线程环境下，数据计算和数据赋值操作可能多次出现，若数据在加载之后，主内存volatile修饰变量发生修改，线程工作内存中的操作将会作废去读取最新值，操作出现写丢失问题（对于number++问题，当某一次number++在执行操作时，已经有别的number++修改并已经提交到主内存，此时本次的number不是最新值，就被废弃了，也即number++操作被丢弃）。即==各线程私有内存和主内存公共内存中变量不同步==，进而导致数据不一致。

# CAS

CAS有3个操作数，位置内存值V，旧的预期值A，要修改的更新值B。当且仅当旧的预期值和内存值相同时，将内存值V修改为B，否则什么都不做或者重来，重试的这种行为称为自旋。

## 源码

![image-20231008223454169](.\images\image-20231008223454169.png)

假设线程A和线程B两个线程同时执行getAndAddInt操作（分别在不同的CPU上）：

1.AtomicInteger里面的value原始值为3，即主内存中AtomicInteger的value为3，根据JMM模型，线程A和线程B各自持有一份值为3的value的副本分别到各自的工作内存

2.线程A通过getIntVolatile拿到value值3，这是线程A被挂起

3.线程B也通过getIntVolatile方法获取到值3，此时刚好线程B没有被挂起并执行compareAndSwapInt方法比较内存值也是3，成功修改内存值为4，线程B执行完毕

4.这是线程A恢复，执行compareAndSwapInt方法比较，发现本线程中的值3和主内存的值4不相同，说明该值已经被其他线程修改过，那么线程A本次修改失败，==只能重新在执行一边==

5.线程A重新获取到value值，因为变量value被volatile修饰，所以其他线程对它的修改，线程A总是能看到，线程A继续执行compareAndSwapInt进行比较替换，直到成功

## 原子引用

```java
@NoArgsConstructor
@AllArgsConstructor
@Data
class User {
    private int age;
    private String name;
}

public class AtomicDemoTest {
    public static void main(String[] args) {
        AtomicReference<User> atomicReference = new AtomicReference<>();

        User a = new User(12, "a");
        User b = new User(13, "b");
        
        atomicReference.set(a);

        System.out.println(atomicReference.compareAndSet(a, b) + "\t" + atomicReference.get().toString());
        System.out.println(atomicReference.compareAndSet(a, b) + "\t" + atomicReference.get().toString());
    }
}

// 结果
true	User(age=13, name=b)
false	User(age=13, name=b)
```

## 自旋锁（SpinLock）

CAS是实现自旋锁的基础，CAS利用CPU指令保证了操作的原子性，以达到锁的效果。自旋是指尝试获取锁的线程不会立即阻塞，而是采用==循环的方式去尝试获取锁==，当线程发现锁被占用时，会不断循环判断锁的状态，直到获取。这样的好处是减少线程上下文切换的消耗，缺点是循环会消耗CPU。

```java
/**
 * 自旋锁
 */
public class SpinLockDemo {
    
    AtomicReference<Thread> atomicReference = new AtomicReference<>();
    
    public void lock() {
        Thread thread = Thread.currentThread();
        System.out.println(thread.getName() + "come in");
        while (!atomicReference.compareAndSet(null, thread)) {
            
        }
    }
    
    public void unlock() {
        Thread thread = Thread.currentThread();
        atomicReference.compareAndSet(thread, null);
        System.out.println(thread.getName() + "unlock");
    }

    public static void main(String[] args) {
        SpinLockDemo spinLockDemo = new SpinLockDemo();
        
        new Thread(() -> {
            spinLockDemo.lock();
            
            try {
                Thread.sleep(5000);
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            spinLockDemo.unlock();
        }).start();
        
        
        new Thread(() -> {
            spinLockDemo.lock();
            spinLockDemo.unlock();
        }).start();
    }
}

// 结果
Thread-0come in
Thread-1come in
Thread-0unlock
Thread-1unlock
```

## 缺点

### CPU空转

do-while一直循环，cpu空转

### ABA问题

CAS算法实现一个重要前提需要取出内存中某时刻的数据并在当下时刻比较并替换，那么在这个时间差内会导致数据的变化。

比如说一个线程1从内存位置V中取值A，这时另一个线程2也从内存中取出A，并且线程2进行了一些操作将值变成了B，然后线程2又将V位置的数据变成A，这时候线程1进行CAS操作发现内存中仍然是A，预期OK，线程1操作成功。

==尽管线程1的CAS操作成功，但是不代表这个过程就是没有问题的==

## AtomicStampedReference

为了解决ABA问题，引入AtomicStampedReference增加版本号

```java
public class AtomicStampeDemo {
    public static void main(String[] args) {
        Book a = new Book(1, "java");
        
        AtomicStampedReference<Book> atomicStampedReference = new AtomicStampedReference<>(a, 1);
        System.out.println(atomicStampedReference.getReference() + "\t" + atomicStampedReference.getStamp());
        
        Book b = new Book(2, "mysql");
        boolean flag;
        
        flag = atomicStampedReference.compareAndSet(a, b, atomicStampedReference.getStamp(), atomicStampedReference.getStamp() + 1);
        System.out.println(flag + "\t" + atomicStampedReference.getReference() + "\t" + atomicStampedReference.getStamp());
        
        flag = atomicStampedReference.compareAndSet(b, a, atomicStampedReference.getStamp(), atomicStampedReference.getStamp() + 1);
        System.out.println(flag + "\t" + atomicStampedReference.getReference() + "\t" + atomicStampedReference.getStamp());
    }
}

// 结果
Book(id=1, name=java)	1
true	Book(id=2, name=mysql)	2
true	Book(id=1, name=java)	3
```

# 原子类

## 基本类型原子类

```java
public class AtomicIntegerDemo {
    
    public static final int SIZE = 50;

    public static void main(String[] args) throws Exception {
        MyNumber myNumber = new MyNumber();
        // 使用CountDownLatch来判断线程是否结束
        CountDownLatch countDownLatch = new CountDownLatch(SIZE);
        for (int i = 1; i <= SIZE ; i++) {
            new Thread(() -> {
                try {
                    for (int j = 1; j <= 1000; j++) {
                        myNumber.add();
                    }
                } finally {
                    countDownLatch.countDown();
                }
            }).start();
        }
        
        countDownLatch.await();
//        try {
//            Thread.sleep(500);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        System.out.println(Thread.currentThread().getName() + "\t" + "result: " + myNumber.atomicInteger.get());
    }
}

class MyNumber {
    AtomicInteger atomicInteger = new AtomicInteger();
    
    public void add() {
        atomicInteger.incrementAndGet();
    }
}
```

## 数组类型原子类

```java
public class AtomicArrayDemo {
    public static void main(String[] args) {
        AtomicIntegerArray atomicIntegerArray = new AtomicIntegerArray(5);

        for (int i = 0; i < atomicIntegerArray.length(); i++) {
            System.out.println(atomicIntegerArray.get(i));
        }

        System.out.println();
        
        int tmpInt = 0;
        
        tmpInt = atomicIntegerArray.getAndSet(0, 100);
        System.out.println(tmpInt + "\t" + atomicIntegerArray.get(0));

        tmpInt = atomicIntegerArray.getAndIncrement(0);
        System.out.println(tmpInt + "\t" + atomicIntegerArray.get(0));
    }
}

// 结果

0
0
0
0
0

0	100
100	101
```

## 引用类型原子类

### AtomicReference

### AtomicStampedReference

携带版本号的引用类型原子类，为了解决ABA问题而设计（修改了几次问题）

### AtomicMarkableReference

带标记的引用类型原子类，将状态简化为true|false,解决是否修改过问题

```java
public class AtomicMarkableDemo {
    public static void main(String[] args) {
        AtomicMarkableReference<Integer> markableReference = new AtomicMarkableReference<>(100, false);
        
        new Thread(() -> {
            boolean marked = markableReference.isMarked();
            System.out.println(Thread.currentThread().getName() + "默认标识" + marked);
            
            try {
                Thread.sleep(1000);
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            markableReference.compareAndSet(100, 1000, marked, !marked);
            
        }).start();
        
        new Thread(() -> {
            boolean marked = markableReference.isMarked();
            System.out.println(Thread.currentThread().getName() + "默认标识" + marked);

            try {
                Thread.sleep(2000);
            } catch (Exception e) {
                e.printStackTrace();
            }

            boolean b = markableReference.compareAndSet(100, 2000, marked, !marked);

            System.out.println(Thread.currentThread().getName() + "cas result:" +  b);
            System.out.println(Thread.currentThread().getName() + "mark:" +  markableReference.isMarked());
            System.out.println(Thread.currentThread().getName() + "value: " +  markableReference.getReference());
        }).start();
    }
}

// 结果
Thread-0默认标识false
Thread-1默认标识false
Thread-1cas result:false
Thread-1mark:true
Thread-1value: 1000
```

## 对象的属性修改原子类

AtomicIntegerFieldUpdater：原子更新对象中int类型字段的值

AtomicLongFieldUpdater：原子更新对象中Long类型字段的值

AtomicReferenceFieldUpdater：原子更新引用类型字段的值

==以一种线程安全的方式操作非线程安全对象内的某个字段==

### AtomicIntegerFieldUpdater：原子更新对象中int类型字段的值

```java
public class AtomicIntegerFieldUpdaterDemo {
    public static void main(String[] args) throws Exception{
        Bank bank = new Bank();

        CountDownLatch countDownLatch = new CountDownLatch(10);

        for (int i = 0; i < 10; i++) {
            new Thread(() -> {
                try {
                    for (int j = 1; j <=1000 ; j++) {
                        bank.transformMoney(bank);
                    }
                } finally {
                    countDownLatch.countDown();
                }
            }).start();
        }
        
        countDownLatch.await();

        System.out.println(Thread.currentThread().getName() + "result: " + bank.money);
    }
}
// 结果
main   result: 10000

class Bank {
    String bankName = "CCB";
    // 更新的对象属性必须使用public volatile 修饰符
    public volatile int money = 0;
    
    // 因为对象的属性修改类型原子类都是抽象类，所以每次使用都必须使用静态方法newUpdater()创建一个更新器，并且需要设置想要更新的类和属性
    
    AtomicIntegerFieldUpdater<Bank> fieldUpdater = AtomicIntegerFieldUpdater.newUpdater(Bank.class, "money");
    
    // 不使用synchronized，保证高性能原子性
    public void transformMoney(Bank bank) {
        fieldUpdater.getAndIncrement(bank);
    }
}
```

### AtomicReferenceFieldUpdater：原子更新引用类型字段的值

```java
public class AtomicReferenceFieldUpdaterDemo {

    public static void main(String[] args) {
        MyVar myVar = new MyVar();

        for (int i = 0; i < 5; i++) {
            new Thread(() -> {
                myVar.init(myVar);
            }).start();
        }
    }
}

class MyVar {
    public volatile Boolean isInit = Boolean.FALSE;
    
    AtomicReferenceFieldUpdater<MyVar, Boolean> fieldUpdater = AtomicReferenceFieldUpdater.newUpdater(MyVar.class, Boolean.class, "isInit");
    
    public void init(MyVar myVar) {
        if (fieldUpdater.compareAndSet(myVar, Boolean.FALSE, Boolean.TRUE)) {
            System.out.println(Thread.currentThread().getName() + "\t" + "...start init");
            
            try {
                Thread.sleep(2000);
            } catch (Exception e) {
                e.printStackTrace();
            }

            System.out.println(Thread.currentThread().getName() + "\t" + "over init");
        } else {
            System.out.println(Thread.currentThread().getName() + "\t" + "已经有线程在进行Init操作");
        }
    }
}
```

## 原子类LongAdder和LongAccumulator

LongAdder性能比AtomicLong更好，减少自旋的次数

```java
public class LongAdderDemo {
    public static void main(String[] args) {
        LongAdder longAdder = new LongAdder();
        
        longAdder.increment();
        longAdder.increment();
        longAdder.increment();

        System.out.println(longAdder); // longAdder获取的值并不是准确的，longAdder的sum()求和不具有原子性
        
        LongAccumulator longAccumulator = new LongAccumulator(Long::sum, 0);
        
        longAccumulator.accumulate(2);
        longAccumulator.accumulate(10);
        System.out.println(longAccumulator.get());
    }
}
```

### 点赞案例

```java
class ClickNumber {
    int number = 0;
    
    public synchronized void clickSynchronized() {
        number++;
    }
    
    AtomicLong atomicLong = new AtomicLong(0);
    public void clickAtomicLong() {
        atomicLong.getAndIncrement();
    }
    
    LongAdder longAdder = new LongAdder();
    public void clickLongAdder() {
        longAdder.increment();
    }
    
    LongAccumulator longAccumulator = new LongAccumulator(Long::sum, 0);
    public void clickAccumulator() {
        longAccumulator.accumulate(1);
    }
}

public class AccumulatorCompareDemo {

    public static final int _1W = 10000;
    
    public static final int THREAD = 50;
    
    public static void main(String[] args) throws Exception{
        ClickNumber clickNumber = new ClickNumber();
        
        long startTime;
        long endTime;

        CountDownLatch countDownLatch1 = new CountDownLatch(THREAD);
        CountDownLatch countDownLatch2 = new CountDownLatch(THREAD);
        CountDownLatch countDownLatch3 = new CountDownLatch(THREAD);
        CountDownLatch countDownLatch4 = new CountDownLatch(THREAD);
        
        startTime = System.currentTimeMillis();
        for (int i = 1; i <= THREAD ; i++) {
            new Thread(() -> {
                try {
                    for (int j = 1; j <= 100 * _1W ; j++) {
                        clickNumber.clickSynchronized();
                    }
                } finally {
                    countDownLatch1.countDown();
                }
            }).start();
        }
        
        countDownLatch1.await();
        endTime = System.currentTimeMillis();
        System.out.println("synchronized cost: " + (endTime - startTime) + "毫秒" + "\t" + "result:" + clickNumber.number);


        startTime = System.currentTimeMillis();
        for (int i = 1; i <= THREAD ; i++) {
            new Thread(() -> {
                try {
                    for (int j = 1; j <= 100 * _1W ; j++) {
                        clickNumber.clickAtomicLong();
                    }
                } finally {
                    countDownLatch2.countDown();
                }
            }).start();
        }

        countDownLatch2.await();
        endTime = System.currentTimeMillis();
        System.out.println("clickAtomicLong cost: " + (endTime - startTime) + "毫秒" + "\t" + "result:" + clickNumber.atomicLong.get());


        startTime = System.currentTimeMillis();
        for (int i = 1; i <= THREAD ; i++) {
            new Thread(() -> {
                try {
                    for (int j = 1; j <= 100 * _1W ; j++) {
                        clickNumber.clickLongAdder();
                    }
                } finally {
                    countDownLatch3.countDown();
                }
            }).start();
        }

        countDownLatch3.await();
        endTime = System.currentTimeMillis();
        System.out.println("clickLongAdder cost: " + (endTime - startTime) + "毫秒" + "\t" + "result:" + clickNumber.longAdder.sum());


        startTime = System.currentTimeMillis();
        for (int i = 1; i <= THREAD ; i++) {
            new Thread(() -> {
                try {
                    for (int j = 1; j <= 100 * _1W ; j++) {
                        clickNumber.clickAccumulator();
                    }
                } finally {
                    countDownLatch4.countDown();
                }
            }).start();
        }

        countDownLatch4.await();
        endTime = System.currentTimeMillis();
        System.out.println("clickAccumulator cost: " + (endTime - startTime) + "毫秒" + "\t" + "result:" + clickNumber.longAccumulator.get());
    }
    
    
}

// 运行结果
synchronized cost: 1025毫秒	result:50000000
clickAtomicLong cost: 601毫秒	result:50000000
clickLongAdder cost: 76毫秒	result:50000000
clickAccumulator cost: 57毫秒	result:50000000
```

## LongAdder原理分析

LongAdder在无竞争的情况下，和AtomicLong一样，对==同一个base==进行操作，当出现竞争关系时则是采用==化整为零分散热点==的做法。用空间换时间，用一个数组cells，将一个value拆分进这个数组cells。多个线程需要同时对value进行操作时，可以对线程id进行hash得到hash值，再根据hash值映射到这个数组cells的某个下标，再对该下标所对应的值进行自增操作。当所有线程操作完毕，将数组cells的所有值和base都加起来作为最终结果，缺点是这个最终结果不是准确的。

![image-20231029211248826](.\images\image-20231029211248826.png)

longAdder.increment()过程--add(1L) --> longAccumulae(x, null, uncontended) --> sum()

```java
    /**
     * Adds the given value.
     *
     * @param x the value to add
     */
    public void add(long x) {
        Cell[] cs; long b, v; int m; Cell c;
        if ((cs = cells) != null || !casBase(b = base, b + x)) {
            boolean uncontended = true;
            if (cs == null || (m = cs.length - 1) < 0 ||
                (c = cs[getProbe() & m]) == null ||
                !(uncontended = c.cas(v = c.value, v + x)))
                longAccumulate(x, null, uncontended);
        }
    }
```

- 如果cs（即Cell数组）为空，尝试用CAS更新base字段，成功则退出；
- 如果cs为空，CAS更新base字段失败，出现竞争，unconttended设置为true，调用longAccumulate;
- 如果cs非空，但是当前线程映射的槽为空, uncontended为true,调用longAccumulate;
- 如果cs非空，且当前线程映射的槽非空，CAS更新Cell的值，成功则返回；否则，uncontended设置为false，调用longAccumulate

### Striped64中一些变量或者方法的定义

- base：类似于AtomicLong中全局的value值。再没有竞争情况下数据直接累加到base上，或者cells扩容时，也需要将数据写入到base上
- collide：表示扩容意向，fase一定不会扩容，true可能会扩容
- cellsBusy：初始化cells或者扩容cells需要获取锁，0：表示无锁状态 1：表示其他线程已经持有锁
- casCellsBusy()：通过CAS操作修改cellsBusy的值，CAS成功代表获取锁，返回true
- NCPU：当前计算机CPU的数量，Cell数组扩容时会使用
- getProbe()：获取当前线程的hash值
- advanceProbe()：重置当前线程的hash值

### longAccumulate源码

```java
   final void longAccumulate(long x, LongBinaryOperator fn,
                              boolean wasUncontended) {
       // 存储线程的probe值
        int h;
       // 如果getProbe()方法返回0，说明随机数未初始化
        if ((h = getProbe()) == 0) {
            // 使用此方法为当前线程重新计算一个hash值，强制初始化
            ThreadLocalRandom.current(); // force initialization
            // 重新获取probe值，hash值被重置即变成全新的线程，所以wasUncontened竞争状态为true
            h = getProbe();
            wasUncontended = true;
        }
       // 如果hash取模运算得到的Cell单元不是null,则为true，此值也可以看作是扩容意向
        boolean collide = false;                // True if last slot nonempty
        done: for (;;) {
            Cell[] cs; Cell c; int n; long v;
            // CASE1: cells已经被初始化
            if ((cs = cells) != null && (n = cs.length) > 0) {
                if ((c = cs[(n - 1) & h]) == null) { // 当前线程的hash值运算后映射得到的Cell单元为null,说明该Cell没有被使用
                    if (cellsBusy == 0) {       // Try to attach new Cell Cell[]数组没有正在扩容
                        Cell r = new Cell(x);   // Optimistically create
                        if (cellsBusy == 0 && casCellsBusy()) { //尝试加锁，成功后cellsBusy == 1
                            try {               // Recheck under lock
                                Cell[] rs; int m, j;
                                if ((rs = cells) != null &&
                                    (m = rs.length) > 0 &&
                                    rs[j = (m - 1) & h] == null) { // 再有锁的情况下再检测一遍之前的判断
                                    rs[j] = r; // 将Cell单元附到Cell[]数组上
                                    break done;
                                }
                            } finally {
                                cellsBusy = 0;
                            }
                            continue;           // Slot is now non-empty
                        }
                    }
                    collide = false;
                }
                else if (!wasUncontended)       // CAS already known to fail 表示cell初始化后，当前线程竞争修改失败
                    wasUncontended = true;      // Continue after rehash 重新设置wasUncontended为true, 后面执行advanceProbe(h)重置当前线程的hash，重新循环
                else if (c.cas(v = c.value,
                               (fn == null) ? v + x : fn.applyAsLong(v, x))) // 说明当前线程对应的数组中已经存在数据，也重置过hash值，这时通过CAS操作尝试对当前数中的value进行累加x操作
                    break;
                // 如果n大于CPU最大数量，不可扩容，并通过下面的h=advanceProbe(h)方法修改线程的probe再重新尝试
                else if (n >= NCPU || cells != cs)
                    collide = false;            // At max size or stale
                else if (!collide)
                    collide = true;
                else if (cellsBusy == 0 && casCellsBusy()) {
                    try {
                        if (cells == cs)        // Expand table unless stale
                            // 扩容为原来的2倍
                            cells = Arrays.copyOf(cs, n << 1);
                    } finally {
                        cellsBusy = 0;
                    }
                    collide = false;
                    continue;                   // Retry with expanded table
                }
                h = advanceProbe(h);
            }
            // CASE2: cells没有加锁且没有初始化，则尝试对它进行加锁，并初始化cells数组
            else if (cellsBusy == 0 && cells == cs && casCellsBusy()) {
                // 两个cells == cs类似于单例模式的双重锁校验
                try {                           // Initialize table
                    if (cells == cs) {
                        Cell[] rs = new Cell[2];
                        rs[h & 1] = new Cell(x); // h & 1的值只有0或者1
                        cells = rs;
                        break done;
                    }
                } finally {
                    cellsBusy = 0;
                }
            }
            // Fall back on using base
            // CASE3: cells正在进行初始化，则尝试直接在基数base上进行累加操作
            else if (casBase(v = base,
                             (fn == null) ? v + x : fn.applyAsLong(v, x)))
                break done;
        }
    }

CASE1: Cell[]数组已经初始化
CASE2: Cell[]数组未初始化（首次新建）
CASE3: Cell[]数组正在初始化
```

# ThreadLocal

ThreadLocal提供线程局部变量。这些变量与正常的变量不同，因为每一个线程在访问ThreadLocal实例的时候（通过get或者set方法）都有自己的，独立初始化变量副本。ThreadLocal实例通常是类中的私有静态字段，使用它的目的是希望将状态（例如，用户ID或者事务ID）与线程关联起来。

```java
class House {
    int saleCount = 0;
    
    public synchronized void saleHouse() {
        saleCount++;
    }
    
    ThreadLocal<Integer> saleVolume = ThreadLocal.withInitial(() -> 0);
    
    public void saleVolumeByThreadLocal() {
        saleVolume.set(1 + saleVolume.get());
    }
}

public class ThreadLocalDemo {
    
    
    public static void main(String[] args) {
        House house = new House();

        for (int i = 1; i <= 5; i++) {
            new Thread(() -> {
                int size = new Random().nextInt(5) + 1;
                System.out.println(size);
                                try {
                    for (int j = 1; j<= size; j++) {
                        house.saleHouse();
                        house.saleVolumeByThreadLocal();
                    }
                    System.out.println("线程" + Thread.currentThread().getName() + "卖出" + house.saleVolume.get());
                } finally {
                    house.saleVolume.remove();
                }
            }).start();
        }

        try {
            Thread.sleep(200);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        System.out.println("总数：" + house.saleCount);
        
    }
}

// 结果
3
3
1
1
1
线程Thread-1卖出1
线程Thread-3卖出1
线程Thread-4卖出3
线程Thread-0卖出3
线程Thread-2卖出1
总数：9
```

## 注意点

必须回收自定义的ThreadLocal变量，尤其在多线程环境下，线程经常会被复用，如果不清理自定义的ThreadLocal变量，可能会影响后续业务逻辑和造成内存泄漏等问题。尽量在代码中使用try-finally块进行回收。