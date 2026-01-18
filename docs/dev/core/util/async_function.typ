#import "/lib/lib.typ": *;
#set raw(lang: "java");
#let title = [异步函数];
#show: template.with(title: title);

也许你常有这样的烦恼（实则不然）：你需要在主线程上处理一些事物，但是其中你想进行一些延迟，显然你不能睡在主线程上

= 核心概念

异步函数是一种协程实现，它允许你在代码中使用类似 `await` 的语法来处理异步操作，而不会陷入回调地狱。

== AsyncFunctionRunner

`AsyncFunctionRunner` 决定了异步函数在哪里（线程）运行。它继承自 `Executor` 接口，提供了两个核心方法：

```java
public interface AsyncFunctionRunner extends Executor
{
    void schedule(Runnable function);
    void schedule(Runnable function, BasicAwait await);
}
```

- `schedule(Runnable)` - 普通调度，立即执行任务
- `schedule(Runnable, BasicAwait)` - 带额外信息的调度，由 Runner 决定如何处理

== BasicAwait

`BasicAwait` 是一个标记接口，用于传递额外的调度信息。它只是一个数据载体，不包含任何逻辑。

```java
public interface BasicAwait
{
}
```

例如，我们可以定义一个延迟等待的实现：

```java
static class SleepMillis implements BasicAwait
{
    final long millis;

    SleepMillis(long millis)
    {
        this.millis = millis;
    }

    long getMillis()
    {
        return millis;
    }
}
```

处理 `BasicAwait` 的逻辑完全由 `AsyncFunctionRunner` 实现：

```java
static class SleepAwareRunner implements AsyncFunctionRunner
{
    ScheduledExecutorService service = Executors.newScheduledThreadPool(2);

    @Override
    public void schedule(Runnable function)
    {
        this.service.execute(function);
    }

    @Override
    public void schedule(Runnable function, BasicAwait await)
    {
        if(await instanceof SleepMillis)
            this.service.schedule(function, ((SleepMillis) await).getMillis(), TimeUnit.MILLISECONDS);
        else
            throw new UnsupportedOperationException(Objects.toString(await));
    }
}
```

= 创建异步函数

首先你需要创建一个类并继承 `AsyncFunction<R>`，其中 `R` 是返回值类型。

```java
public class MyAsyncFunction extends AsyncFunction<Void>
{
    @Override
    public void run()
    {
        // 方法体留空，由 Runner 调用
    }

    @Override
    protected Void template()
    {
        // 异步函数的逻辑写在这里
        System.out.println("Hello World");
        // 使用 await 让出控制权
        await(new SleepMillis(100));
        System.out.println("Hello World again");
        return null;
    }
}
```

== await 方法

异步函数提供了两个 `await` 方法：

* `await(BasicAwait)` - 让出控制权，传递调度信息给 Runner
* `await0(CompletableFuture)` - 等待 CompletableFuture 完成

== 重要：避免阻塞调用

在异步函数中，*不应该直接使用 `join()` 或 `get()`*，因为它们是阻塞的。正确的做法是先使用 `await0()` 确保 Future 完成：

```java
// ❌ 错误：阻塞调用
CompletableFuture<Integer> future = asyncAdd(1, 2);
return future.join();

// ✅ 正确：非阻塞等待
CompletableFuture<Integer> future = asyncAdd(1, 2);
await0(future);  // 确保 Future 完成
return future.join();  // 此时不会阻塞
```

= 启动异步函数

调用 `start` 方法启动异步函数：

```java
new MyAsyncFunction().start(runner);
```

`start` 方法返回一个 `CompletableFuture<R>`，当异步函数完成时它会被完成。

= 并发执行

异步函数可以并发执行，充分利用多线程优势：

```java
public static CompletableFuture<Void> concurrentExample(AsyncFunctionRunner runner)
{
    return new AsyncFunction<Void>()
    {
        @Override
        public void run()
        {
        }

        @Override
        protected Void template()
        {
            // 同时启动100个协程
            List<CompletableFuture<Integer>> futures = new ArrayList<>();
            for(int i = 0; i < 100; i++)
            {
                futures.add(sleepCoroutine(i, runner));
            }

            // 同时等待所有协程完成
            for(CompletableFuture<?> future : futures)
            {
                await0(future);
            }

            return null;
        }
    }.start(runner);
}

static CompletableFuture<Integer> sleepCoroutine(int index, AsyncFunctionRunner runner)
{
    return new AsyncFunction<Integer>()
    {
        @Override
        public void run()
        {
        }

        @Override
        protected Integer template()
        {
            await(new SleepMillis(100));
            return index;
        }
    }.start(runner);
}
```

由于所有协程并发执行，总用时接近 100ms（而不是 100 \* 100 = 10000ms）。

= 在匿名内部类中使用

将异步函数写成匿名内部类并封装成方法可以简化参数传递：

```java
public static CompletableFuture<Void> startMyAsyncFunction(int i, AsyncFunctionRunner runner)
{
    return new AsyncFunction<Void>()
    {
        @Override
        public void run()
        {
        }

        @Override
        protected Void template()
        {
            await(new SleepMillis(100));
            System.out.println("Hello World " + i);
            return null;
        }
    }.start(runner);
}
```

= 执行顺序

异步函数的执行顺序由 Runner 决定。使用 `TaskQueue` 可以确保任务按顺序执行：

```java
TaskQueue taskQueue = new TaskQueue();
List<Integer> order = new ArrayList<>();

new AsyncFunction<Void>()
{
    @Override
    public void run()
    {
    }

    @Override
    protected Void template()
    {
        order.add(0);
        CompletableFuture<Void> f1 = fun1(), f2 = fun2();
        order.add(1);
        await0(f1);
        await0(f2);
        order.add(4);
        return null;
    }
}.start(taskQueue);

taskQueue.run();
// order = [0, 1, 2, 3, 4]
```

在这个例子中：
1. 主协程执行到 `order.add(0)`
2. 创建子协程但还未执行
3. 主协程继续执行到 `order.add(1)`
4. `await0()` 让出控制权，子协程开始执行
5. 子协程完成后，主协程恢复执行 `order.add(4)`