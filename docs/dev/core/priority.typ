#import "/lib/lib.typ": *;
#let title = [优先级];
#show: template.with(title: title);

优先级枚举用于定义事件监听器的执行顺序。

= Priority

`Priority` 定义了不同的优先级等级。

```java
public enum Priority
{
    LOWEST,     // 最低优先级
    LOW,        // 低优先级
    NORMAL,     // 普通优先级
    HIGH,       // 高优先级
    HIGHEST,    // 最高优先级
    MONITOR     // 监控优先级，仅用于监听，不应修改事件
}
```

= 使用示例

```java
@RegistrarEventListener
public class MyListener
{
    @RegistrarEventClass(priority = Priority.HIGHEST)
    public static void onEvent(MyEvent event)
    {
        // 最高优先级，最先执行
    }

    @RegistrarEventClass(priority = Priority.LOWEST)
    public static void onEventLow(MyEvent event)
    {
        // 最低优先级，最后执行
    }
}
```

= 优先级顺序

从高到低：
1. HIGHEST
2. HIGH
3. NORMAL
4. LOW
5. LOWEST
6. MONITOR

= 注意事项

- MONITOR 优先级应该只用于监听，不应修改事件
- 相同优先级的监听器执行顺序不确定
- 使用 `@Cancellable` 的事件可以在高优先级监听器中取消