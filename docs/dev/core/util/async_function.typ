#import "/lib/lib.typ": *;
#set raw(lang: "java");
#let title = [异步函数];
#show: template.with(title: title);

也许你常有这样的烦恼（实则不然）：你需要在主线程上处理一些事物，但是其中你想进行一些延迟，显然你不能睡在主线程上

= 设置定时任务

最常见的方式就是创建一个计划任务了，服务端实例可以代表主线程的调度器

```java
MinecraftServer.instance.schedule(() ->
{
    System.out.println("Hello World");
}, new SleepTicks(20));
```

这将会在主线程的20个ticks后打印Hello World

其中`SleepTicks`的实例是被`MinecraftServer#schedule`处理的，实现了任务的创建

= 创建异步函数

你显然不太满意计划任务，因为它容易是你陷入回调地狱，因此我们使用嵌入控制流的延迟方法

首先你需要创建一个类并继承`AsyncFunction<R>`，其中R是返回值类型。这个类就表示一个异步函数，我们暂时先返回`Void`

```java
public class MyAsyncFunction extends AsyncFunction<Void>
{
    // 实现template，异步函数的逻辑写在这里
    @Override
    public Void template()
    {
        // 循环5次
        for(int i=0; i<5; i++)
        {
            // 打印Hello World
            System.out.println("Hello World");
            // “调用” await，延迟20个ticks
            await(new SleepTicks(20));
        }
        return null;
    }

    // 实现run方法，方法体留空
    @Override
    public void run()
    {
    }
}
```

这里的await是异步函数让步的一个标记，并不是真正调用了这个方法

= 启动异步函数

调用异步函数后，你可以认为它启动了一个协程，因此我们先构造它，然后调用start方法来启动

```java
new MyAsyncFunction().start(MinecraftServer.instance);
```

`start`方法的参数就是这个异步函数的runner，```java MinecraftServer.instance```则让它在主线程中运行

异步函数`await`的```java SleepTicks```实例也由```java MinecraftServer.instance```处理

异步函数启动后`start`方法会返回一个`CompletableFuture<R>`实例，当异步函数返回时它被完成

= 等待`CompletableFuture`

在异步函数中，你可以等待一个```java CompletableFuture```的完成

例如，你可以启动另一个异步函数并等待

```java
public class MyAsyncFunction2 extends AsyncFunction<Void>
{
    // 实现template，异步函数的逻辑写在这里
    @Override
    public Void template()
    {
        System.out.println("Hello CompletableFuture");
        // 使用相同的runner启动MyAsyncFunction，得到其CompletableFuture
        CompletableFuture<Void> cf = new MyAsyncFunction().start(this.getRunner());
        // 使用await0等待它完成
        await0(cf);
        System.out.println("Hello CompletableFuture");
        return null;
    }

    // 实现run方法，方法体留空
    @Override
    public void run()
    {
    }
}
```

= 在匿名内部类中使用

你也许想到：作为函数，异步函数应当可以有参数，但这样你需要创建构造器传入参数到字段，然后在`template`中使用

将异步函数写成匿名内部类并封装成方法可能会简化许多

```java
public static AsyncFunction<Void> newMyAsyncFunction(int i)
{
    return new AsyncFunction<Void>()
    {
        public Void template()
        {
            System.out.println("Hello World " + i);
        }
        public void run()
        {
        }
    };
}
```

通常情况下，如果你需要一个固定的`runner`，那你可以直接调用`start`再返回`CompletableFuture`

```java
public static CompletableFuture<Void> startMyAsyncFunction(int i)
{
    return new AsyncFunction<Void>()
    {
        public Void template()
        {
            await(new SleepTicks(20));
            System.out.println("Hello World " + i);
        }
        public void run()
        {
        }
    }.start(MinecraftServer.instance);
}
```
