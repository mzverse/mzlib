#import "/lib/lib.typ": *;
#let title = [优先级];
#show: template.with(title: title);

优先级枚举用于定义事件监听器的执行顺序。

= Priority

`Priority` 接口定义了用于指定事件监听器执行顺序的优先级常量。

= 优先级常量

```java
public interface Priority
{
    float LOWEST = -1000.f;          // 最低优先级
    float VERY_VERY_LOW = -100.f;    // 极极低优先级
    float VERY_LOW = -10.f;          // 极低优先级
    float LOW = -1.f;                // 低优先级
    float NORMAL = 0.f;              // 普通优先级（默认）
    float HIGH = 1.f;                // 高优先级
    float VERY_HIGH = 10.f;          // 极高优先级
    float VERY_VERY_HIGH = 100.f;    // 极极高优先级
    float HIGHEST = 1000.f;          // 最高优先级
}
```

= 使用示例

```java
// 创建事件监听器
EventListener<MyEvent> listener = new EventListener<>(
    MyEvent.class,
    Priority.HIGH,
    event -> {
        // 处理事件，高优先级
        System.out.println("High priority listener");
    }
);

// 使用默认优先级（NORMAL）
EventListener<MyEvent> normalListener = new EventListener<>(
    MyEvent.class,
    event -> {
        // 处理事件，普通优先级
        System.out.println("Normal priority listener");
    }
);

// 最低优先级
EventListener<MyEvent> lowestListener = new EventListener<>(
    MyEvent.class,
    Priority.LOWEST,
    event -> {
        // 处理事件，最低优先级
        System.out.println("Lowest priority listener");
    }
);

// 注册监听器
module.register(listener);
```

= 优先级顺序

从高到低：
1. HIGHEST (1000.f)
2. VERY_VERY_HIGH (100.f)
3. VERY_HIGH (10.f)
4. HIGH (1.f)
5. NORMAL (0.f)
6. LOW (-1.f)
7. VERY_LOW (-10.f)
8. VERY_VERY_LOW (-100.f)
9. LOWEST (-1000.f)

= 自定义优先级

由于 `Priority` 是浮点数常量，可以使用任意浮点数值：

```java
// 使用自定义优先级（介于 NORMAL 和 HIGH 之间）
EventListener<MyEvent> customListener = new EventListener<>(
    MyEvent.class,
    0.5f,
    event -> {
        // 处理事件
        System.out.println("Custom priority listener");
    }
);

// 使用负数自定义优先级（介于 LOW 和 VERY_LOW 之间）
EventListener<MyEvent> customLowListener = new EventListener<>(
    MyEvent.class,
    -5.f,
    event -> {
        // 处理事件
        System.out.println("Custom low priority listener");
    }
);
```

= 注意事项

- 优先级值越大，执行顺序越靠前
- 相同优先级的监听器执行顺序不确定
- 实现 `Cancellable` 接口的事件可以在高优先级监听器中取消
- 可以使用任意浮点数值，不限于预定义的常量