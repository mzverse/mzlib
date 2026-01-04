#import "/lib/lib.typ": *;
#let title = [性能优化];
#show: template.with(title: title);


本指南提供性能优化技巧和最佳实践，帮助你提高插件性能。

= 性能优化原则

/ #[= 避免不必要的计算]:

    只在需要时计算，避免重复计算。

/ #[= 使用缓存]:

    缓存频繁使用的数据。

/ #[= 异步处理]:

    将耗时操作放到异步线程。

/ #[= 减少对象创建]:

    重用对象，减少垃圾回收压力。

/ #[= 优化数据结构]:

    选择合适的数据结构。

= 对象创建优化

== 避免频繁创建对象

```java
// 不好的做法
for (int i = 0; i < 1000; i++)
{
    Text text = Text.literal("Message " + i);
    player.sendMessage(text);
}

// 好的做法
for (int i = 0; i < 1000; i++)
{
    player.sendMessage(Text.literal("Message " + i));
}
```

== 使用对象池

```java
// 使用对象池重用对象
ObjectPool<Text> textPool = new ObjectPool<>(() -> Text.literal(""));

public void sendMessage(String message)
{
    Text text = textPool.borrow();
    text.setContent(message);
    player.sendMessage(text);
    textPool.returnObject(text);
}
```

== 使用 StringBuilder

```java
// 不好的做法
String result = "";
for (String s : strings)
{
    result += s;  // 每次都创建新对象
}

// 好的做法
StringBuilder sb = new StringBuilder();
for (String s : strings)
{
    sb.append(s);
}
String result = sb.toString();
```

= 缓存优化

== 使用 ClassCache

```java
// 使用 ClassCache 缓存类信息
ClassCache<MyClass> cache = new ClassCache<>(MyClass.class);

// 获取缓存的方法
Method method = cache.getMethod("methodName");
```

== 使用 Cache

```java
// 使用 Cache 缓存计算结果
Cache<String, Text> textCache = new Cache<>(key -> Text.literal(key));

// 获取缓存
Text text = textCache.get("key");
```

== 使用 Map 缓存

```java
// 使用 Map 缓存
Map<String, Text> textCache = new HashMap<>();

public Text getText(String key)
{
    return textCache.computeIfAbsent(key, k -> Text.literal(k));
}
```

#cardTip[
    使用缓存时要注意内存使用，定期清理不再需要的缓存。
]

= 异步处理

== 使用 AsyncFunction

```java
// 使用 AsyncFunction 异步处理
AsyncFunction.run(() ->
{
    // 耗时操作
    return NbtIo.read(file);
}).then(data ->
{
    // 主线程操作
    player.sendMessage(Text.literal("Loaded"));
});
```

== 使用 CompletableFuture

```java
// 使用 CompletableFuture
CompletableFuture.supplyAsync(() ->
{
    // 耗时操作
    return loadData();
}).thenAcceptAsync(data ->
{
    // 主线程操作
    processData(data);
}, MinecraftServer.instance.getMainThreadExecutor());
```

== 使用 ExecutorService

```java
// 使用线程池
ExecutorService executor = Executors.newFixedThreadPool(4);

executor.submit(() ->
{
    // 耗时操作
    result = doWork();
});
```

= 事件处理优化

== 使用正确的优先级

```java
@RegistrarEventListener
public class MyListener
{
    @RegistrarEventClass(priority = Priority.HIGHEST)
    public static void onEvent(MyEvent event)
    {
        // 高优先级处理
        if (shouldCancel(event))
        {
            event.setCancelled(true);
            return;
        }
    }
}
```

== 尽早取消不必要的事件

```java
@RegistrarEventClass
public static void onChat(EventAsyncPlayerChat event)
{
    // 尽早检查
    if (!shouldHandle(event))
    {
        return;
    }
    
    // 处理事件
    processChat(event);
}
```

== 减少事件监听器数量

```java
// 合并多个监听器
@RegistrarEventListener
public class CombinedListener
{
    @RegistrarEventClass
    public static void onEvent1(Event1 event) { }

    @RegistrarEventClass
    public static void onEvent2(Event2 event) { }
}
```

= 内存管理

== 使用弱引用

```java
// 使用 RefWeak 避免内存泄漏
RefWeak<EntityPlayer> weakPlayer = new RefWeak<>(player);

// 检查引用是否有效
for (EntityPlayer p : weakPlayer.get())
{
    // 使用玩家
}
```

== 及时释放资源

```java
// 使用 try-with-resources
try (InputStream is = IOUtil.openFile(file))
{
    // 使用资源
}

// 手动关闭
InputStream is = IOUtil.openFile(file);
try
{
    // 使用资源
}
finally
{
    IOUtil.closeQuietly(is);
}
```

== 使用 Option 和 Result

```java
// 使用 Option 避免空指针
Option<EntityPlayer> player = getPlayer();
for (EntityPlayer p : player)
{
    // 使用玩家
}

// 使用 Result 处理错误
Result<String, Exception> result = loadData();
for (String data : result)
{
    // 使用数据
}
```

= 数据结构优化

== 使用合适的数据结构

```java
// 频繁查找：使用 HashMap
Map<String, Value> map = new HashMap<>();

// 需要保持顺序：使用 LinkedHashMap
Map<String, Value> map = new LinkedHashMap<>();

// 需要排序：使用 TreeMap
Map<String, Value> map = new TreeMap<>();

// 频增删：使用 LinkedList
List<Value> list = new LinkedList<>();

// 随机访问：使用 ArrayList
List<Value> list = new ArrayList<>();
```

== 使用原始类型数组

```java
// 不好的做法
List<Integer> list = new ArrayList<>();
for (int i = 0; i < 1000; i++)
{
    list.add(i);
}

// 好的做法
int[] array = new int[1000];
for (int i = 0; i < 1000; i++)
{
    array[i] = i;
}
```

= NBT 优化

== 使用 NbtScanner

```java
// 使用 NbtScanner 高效遍历 NBT
NbtScanner scanner = new NbtScanner(nbt);
scanner.forEach((key, value) ->
{
    // 处理每个键值对
});
```

== 避免深拷贝

```java
// 不好的做法
NbtCompound copy = nbt.copy();  // 深拷贝

// 好的做法
NbtCompound copy = nbt.shallowCopy();  // 浅拷贝
```

= 网络优化

== 减少数据包发送

```java
// 不好的做法 - 每次都发送
for (Player player : players)
{
    player.sendPacket(packet);
}

// 好的做法 - 批量发送
PacketBundle bundle = PacketBundle.newInstance();
for (Player player : players)
{
    bundle.add(packet);
}
server.sendPacket(bundle);
```

== 使用数据包捆绑

```java
// 使用数据包捆绑（1.19.4+）
PacketBundle bundle = PacketBundle.newInstance();
bundle.add(PacketS2cChatMessage.newInstance(Text.literal("Message 1")));
bundle.add(PacketS2cChatMessage.newInstance(Text.literal("Message 2")));
player.sendPacket(bundle);
```

== 减少数据包监听器

```java
// 合并多个监听器
this.register(new PacketListener<>(PacketC2sChatMessage.FACTORY, event ->
{
    // 处理所有逻辑
}));
```

= 监控和分析

== 使用 Profiler

```java
// 使用 Profiler 监控性能
Profiler profiler = Profiler.instance;
profiler.push("section");
// 执行代码
profiler.pop();
```

== 使用性能分析工具

- Spark - 性能分析插件
- Timings - Bukkit 性能分析
- VisualVM - Java 性能分析
- JProfiler - Java 性能分析

== 监控关键指标

- TPS（每秒刻数）
- 内存使用
- CPU 使用
- 磁盘 I/O
- 网络延迟

= 最佳实践总结

/ #[= 代码层面]:

    - 避免频繁创建对象
    - 使用缓存
    - 异步处理耗时操作
    - 使用合适的数据结构
    - 及时释放资源

/ #[= 事件层面]:

    - 使用正确的优先级
    - 尽早取消不必要的事件
    - 减少事件监听器数量

/ #[= 网络层面]:

    - 减少数据包发送
    - 使用数据包捆绑
    - 减少数据包监听器

/ #[= 内存层面]:

    - 使用弱引用
    - 使用 Option 和 Result
    - 及时释放资源

/ #[= 监控层面]:

    - 使用性能分析工具
    - 监控关键指标
    - 定期检查性能瓶颈

= 性能测试

== 使用测试框架

```java
@Tester
public class PerformanceTest
{
    @TesterFunction
    public void testPerformance(TesterContext context)
    {
        long start = System.currentTimeMillis();
        
        // 执行测试
        for (int i = 0; i < 1000; i++)
        {
            doSomething();
        }
        
        long end = System.currentTimeMillis();
        System.out.println("Time: " + (end - start) + "ms");
    }
}
```

== 使用基准测试

```java
// 使用 JMH 进行基准测试
@Benchmark
public void testMethod()
{
    // 测试代码
}
```

= 常见性能陷阱

== 避免在循环中创建对象

```java
// 不好的做法
for (int i = 0; i < 1000; i++)
{
    List<String> list = new ArrayList<>();  // 每次都创建
    list.add("item");
}

// 好的做法
List<String> list = new ArrayList<>();
for (int i = 0; i < 1000; i++)
{
    list.add("item");
}
```

== 避免在事件中进行耗时操作

```java
// 不好的做法
@RegistrarEventClass
public static void onEvent(MyEvent event)
{
    loadData();  // 耗时操作
}

// 好的做法
@RegistrarEventClass
public static void onEvent(MyEvent event)
{
    AsyncFunction.run(() -> loadData());
}
```

== 避免频繁的 NBT 操作

```java
// 不好的做法
for (int i = 0; i < 1000; i++)
{
    nbt.put("key" + i, "value" + i);  // 频繁修改
}

// 好的做法
Map<String, String> temp = new HashMap<>();
for (int i = 0; i < 1000; i++)
{
    temp.put("key" + i, "value" + i);
}
// 一次性写入
for (Map.Entry<String, String> entry : temp.entrySet())
{
    nbt.put(entry.getKey(), entry.getValue());
}
```

#cardInfo[
    性能优化是一个持续的过程，需要不断地监控、分析和优化。
    
    记住：
    - 过早优化是万恶之源
    - 先测量，再优化
    - 优化热点代码
    - 保持代码可读性
]