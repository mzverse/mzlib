#import "/lib/lib.typ": *;
#set raw(lang: "java");
#let title = [事件系统];
#show: template.with(title: title);


事件系统是 MzLib 的核心功能之一，提供高性能的事件分发机制，支持优先级、异步任务调度等功能。

= 核心概念

== Event

`Event` 是所有事件的基类，提供事件的基本功能。

```java
public abstract class Event
{
    // 触发所有监听器
    public abstract void call();

    // 完成事件
    public void complete() { }

    // 检查事件是否被取消
    public boolean isCancelled() { return false; }

    // 取消事件
    public void setCancelled(boolean cancelled) { }

    // 添加延迟任务
    public void schedule(Runnable task) { }

    // 添加延迟任务（带延迟）
    public void schedule(Runnable task, long delay) { }
}
```

== EventListener

`EventListener` 是事件监听器接口，用于监听事件。

```java
public interface EventListener<T extends Event>
{
    // 处理事件
    void handle(T event) throws Throwable;
}
```

== Cancellable

`Cancellable` 是可取消事件接口，允许事件被取消。

```java
public interface Cancellable
{
    boolean isCancelled();
    void setCancelled(boolean cancelled);
}
```

= 注册事件类

创建自定义事件类，继承 `Event`，实现 `call` 方法。

```java
public class MyEvent extends Event implements Cancellable
{
    private boolean cancelled = false;
    private String message;

    public MyEvent(String message)
    {
        this.message = message;
    }

    @Override
    public void call()
    {
        // 这里不用写任何代码
    }

    @Override
    public boolean isCancelled()
    {
        return this.cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled)
    {
        this.cancelled = cancelled;
    }

    public String getMessage()
    {
        return this.message;
    }

    public void setMessage(String message)
    {
        this.message = message;
    }

    // 处理该事件的模块
    public static class Module extends MzModule
    {
        public static Module instance = new Module();

        @Override
        public void onLoad()
        {
            // 注册事件类
            this.register(MyEvent.class);
        }
    }
}
```

= 注册事件监听器

使用 `@RegistrarEventListener` 和 `@RegistrarEventClass` 注解自动注册事件监听器。

```java
import mz.mzlib.module.RegistrarEventListener;
import mz.mzlib.event.RegistrarEventClass;
import mz.mzlib.Priority;

@RegistrarEventListener
public class MyListener
{
    @RegistrarEventClass
    public static void onMyEvent(MyEvent event)
    {
        System.out.println("事件触发: " + event.getMessage());
    }

    @RegistrarEventClass(priority = Priority.HIGHEST)
    public static void onMyEventHighest(MyEvent event)
    {
        System.out.println("高优先级处理");
    }

    @RegistrarEventClass(priority = Priority.LOWEST)
    public static void onMyEventLowest(MyEvent event)
    {
        System.out.println("低优先级处理");
    }
}
```

= 事件优先级

事件监听器支持优先级，优先级高的监听器会先执行。

```java
import mz.mzlib.Priority;

// 优先级从高到低
@RegistrarEventClass(priority = Priority.HIGHEST)    // 最高
@RegistrarEventClass(priority = Priority.HIGH)       // 高
@RegistrarEventClass(priority = Priority.NORMAL)     // 正常（默认）
@RegistrarEventClass(priority = Priority.LOW)        // 低
@RegistrarEventClass(priority = Priority.LOWEST)     // 最低
```

= 触发事件

创建事件实例，调用 `call` 方法触发所有监听器。

```java
MyEvent event = new MyEvent("Hello World");

// 触发监听器
event.call();

// 检查事件是否被取消
if (!event.isCancelled())
{
    // 执行事件逻辑
    System.out.println("事件执行: " + event.getMessage());
}

// 完成事件
event.complete();
```

= 取消事件

如果事件实现了 `Cancellable` 接口，可以在监听器中取消事件。

```java
@RegistrarEventClass(priority = Priority.HIGHEST)
public static void onMyEvent(MyEvent event)
{
    if (event.getMessage().equals("cancel"))
    {
        event.setCancelled(true);
        System.out.println("事件已取消");
    }
}
```

= 异步任务调度

事件系统支持异步任务调度，可以在事件完成后执行任务。

```java
public class MyEvent extends Event
{
    @Override
    public void call()
    {
        // 触发监听器
    }

    @Override
    public void complete()
    {
        // 完成事件后执行延迟任务
        super.complete();
    }
}

// 在监听器中添加延迟任务
@RegistrarEventClass
public static void onMyEvent(MyEvent event)
{
    // 立即执行的任务
    event.schedule(() ->
    {
        System.out.println("延迟任务 1");
    });

    // 延迟执行的任务（单位：毫秒）
    event.schedule(() ->
    {
        System.out.println("延迟任务 2（延迟 1000ms）");
    }, 1000);
}
```

= 事件传播

事件系统支持事件传播，可以控制事件是否继续传播。

```java
@RegistrarEventClass(priority = Priority.HIGHEST)
public static void onMyEvent(MyEvent event)
{
    // 取消事件，阻止后续监听器执行
    event.setCancelled(true);
}

@RegistrarEventClass(priority = Priority.NORMAL)
public static void onMyEventNormal(MyEvent event)
{
    // 如果事件被取消，此监听器不会执行
    System.out.println("这个消息不会显示");
}
```

= 性能优化

事件系统使用 `MethodHandles` 和 `CallSite` 进行性能优化。

```java
// ListenerHandler 使用 invokeDynamic 优化
public class ListenerHandler<T extends Event>
{
    private final MethodHandle handle;

    public ListenerHandler(EventListener<T> listener, Method method)
    {
        // 使用 MethodHandles 创建高效的调用句柄
        this.handle = MethodHandles.lookup().unreflect(method);
    }

    public void handle(T event) throws Throwable
    {
        // 直接调用，避免反射开销
        this.handle.invokeExact(listener, event);
    }
}
```

= 最佳实践

/ #[= 使用优先级]:

    合理使用优先级，确保监听器按预期顺序执行。

/ #[= 取消事件]:

    只在必要时取消事件，避免影响其他监听器。

/ #[= 异步任务]:

    使用异步任务处理耗时操作，避免阻塞事件线程。

/ #[= 事件完成]:

    始终调用 `complete()` 方法，确保延迟任务得到执行。

/ #[= 错误处理]:

    监听器中的异常会被捕获，不会影响其他监听器。

= 示例

完整的事件系统示例：

```java
import mz.mzlib.event.Event;
import mz.mzlib.event.Cancellable;
import mz.mzlib.module.MzModule;
import mz.mzlib.module.RegistrarEventListener;
import mz.mzlib.event.RegistrarEventClass;
import mz.mzlib.Priority;

// 自定义事件
public class ChatEvent extends Event implements Cancellable
{
    private boolean cancelled = false;
    private String message;
    private String player;

    public ChatEvent(String player, String message)
    {
        this.player = player;
        this.message = message;
    }

    @Override
    public void call() { }

    @Override
    public boolean isCancelled()
    {
        return this.cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled)
    {
        this.cancelled = cancelled;
    }

    public String getMessage()
    {
        return this.message;
    }

    public String getPlayer()
    {
        return this.player;
    }

    public static class Module extends MzModule
    {
        public static Module instance = new Module();

        @Override
        public void onLoad()
        {
            this.register(ChatEvent.class);
        }
    }
}

// 事件监听器
@RegistrarEventListener
public class ChatListener
{
    // 高优先级：检查敏感词
    @RegistrarEventClass(priority = Priority.HIGHEST)
    public static void onChatHighest(ChatEvent event)
    {
        String message = event.getMessage();
        if (message.contains("badword"))
        {
            event.setCancelled(true);
            System.out.println("消息包含敏感词，已取消");
        }
    }

    // 正常优先级：记录日志
    @RegistrarEventClass(priority = Priority.NORMAL)
    public static void onChatNormal(ChatEvent event)
    {
        System.out.println(event.getPlayer() + ": " + event.getMessage());
        
        // 添加延迟任务
        event.schedule(() ->
        {
            System.out.println("延迟任务：保存聊天记录");
        }, 1000);
    }

    // 低优先级：发送到其他服务器
    @RegistrarEventClass(priority = Priority.LOWEST)
    public static void onChatLowest(ChatEvent event)
    {
        if (!event.isCancelled())
        {
            System.out.println("转发消息到其他服务器");
        }
    }
}

// 触发事件
public class Main
{
    public static void main(String[] args)
    {
        // 注册模块
        MzModule.getRegistrar().register(ChatEvent.Module.instance);

        // 触发事件
        ChatEvent event = new ChatEvent("Player1", "Hello World");
        event.call();

        if (!event.isCancelled())
        {
            System.out.println("消息已发送");
        }

        event.complete();
    }
}
```

#cardInfo[
    事件系统是 MzLib 的核心功能，提供：
    - 高性能的事件分发
    - 优先级支持
    - 异步任务调度
    - 事件取消机制
    - 自动注册监听器
]