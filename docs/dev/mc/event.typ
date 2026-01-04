#import "/lib/lib.typ": *;
#let title = [事件系统];
#show: template.with(title: title);

事件系统提供了对各种 Minecraft 事件的监听和处理功能。

= MinecraftEventModule

`MinecraftEventModule` 是 Minecraft 事件模块，负责注册所有事件监听器。

```java
public class MyModule extends MzModule
{
    @Override
    public void onLoad()
    {
        this.register(MinecraftEventModule.instance);
    }
}
```

= 玩家事件

== EventPlayer

玩家事件基类。

```java
// 获取玩家
Player player = event.player;

// 获取实体玩家（如果在线）
Option<EntityPlayer> entity = player.getEntity();
```

== EventPlayerJoin

玩家加入事件。

```java
@RegistrarEventClass
public static void onPlayerJoin(EventPlayerJoin event)
{
    Player player = event.player;
    player.sendMessage(Text.literal("Welcome to the server!"));
}
```

== EventPlayerQuit

玩家退出事件。

```java
@RegistrarEventClass
public static void onPlayerQuit(EventPlayerQuit event)
{
    Player player = event.player;
    System.out.println(player.getName() + " left the server");
}
```

== EventPlayerUseItem

玩家使用物品事件。

```java
@RegistrarEventClass
public static void onPlayerUseItem(EventPlayerUseItem event)
{
    EntityPlayer player = event.player;
    ItemStack stack = event.stack;
    Hand hand = event.hand;

    // 可以修改或取消事件
    event.cancel();
}
```

= 异步玩家事件

== EventAsyncPlayerChat

异步玩家聊天事件。

```java
@RegistrarEventClass
public static void onPlayerChat(EventAsyncPlayerChat event)
{
    Player player = event.player;
    Text message = event.message;

    // 修改消息
    event.message = Text.literal("[Filtered] ").append(message);

    // 取消消息
    event.cancel();
}
```

== EventAsyncPlayerMove

异步玩家移动事件。

```java
@RegistrarEventClass
public static void onPlayerMove(EventAsyncPlayerMove event)
{
    Player player = event.player;
    double x = event.x;
    double y = event.y;
    double z = event.z;

    // 检查是否在特定区域
    if (x < 0 || x > 100 || z < 0 || z > 100)
    {
        // 传送回边界
        event.cancel();
        player.getEntity().ifPresent(e -> e.teleport(
            Math.max(0, Math.min(100, x)),
            y,
            Math.max(0, Math.min(100, z))
        ));
    }
}
```

== EventAsyncPlayerDisplayItem

异步显示物品事件。

```java
@RegistrarEventClass
public static void onPlayerDisplayItem(EventAsyncPlayerDisplayItem event)
{
    Player player = event.player;
    ItemStack stack = event.stack;

    // 修改显示的物品
    event.stack = ItemStack.builder(stack)
        .customName(Text.literal("Custom Display"))
        .build();
}
```

= 实体事件

== EventEntity

实体事件基类。

```java
// 获取实体
Entity entity = event.entity;
```

== EventEntityLivingDamage

实体受伤事件。

```java
@RegistrarEventClass
public static void onEntityDamage(EventEntityLivingDamage event)
{
    EntityLiving entity = event.entity;
    float damage = event.damage;
    DamageSource source = event.source;

    // 修改伤害
    event.damage *= 0.5f;

    // 取消伤害
    event.cancel();
}
```

= 服务器事件

== EventServerStart

服务器启动事件。

```java
@RegistrarEventClass
public static void onServerStart(EventServerStart event)
{
    System.out.println("Server started!");
}
```

== EventServerStop

服务器停止事件。

```java
@RegistrarEventClass
public static void onServerStop(EventServerStop event)
{
    System.out.println("Server stopping!");
}
```

= 窗口事件

== EventAsyncWindowAction

异步窗口操作事件。

```java
@RegistrarEventClass
public static void onWindowAction(EventAsyncWindowAction event)
{
    Player player = event.player;
    int slot = event.slot;
    ItemStack stack = event.stack;

    // 处理窗口操作
    System.out.println("Player clicked slot " + slot);

    // 取消操作
    event.cancel();
}
```

== EventAsyncWindowClose

异步窗口关闭事件。

```java
@RegistrarEventClass
public static void onWindowClose(EventAsyncWindowClose event)
{
    Player player = event.player;
    int syncId = event.syncId;

    System.out.println("Window closed");
}
```

= 优先级

使用 `@RegistrarEventClass` 注解的 `priority` 参数设置优先级。

```java
@RegistrarEventClass(priority = Priority.HIGHEST)
public static void onEventHigh(MyEvent event)
{
    // 最高优先级，最先执行
}

@RegistrarEventClass(priority = Priority.LOWEST)
public static void onEventLow(MyEvent event)
{
    // 最低优先级，最后执行
}
```

= 可取消事件

使用 `@Cancellable` 注解标记可取消的事件。

```java
@RegistrarEventClass
public static void onEvent(MyEvent event)
{
    // 取消事件
    event.cancel();
}
```

= 使用示例

```java
@RegistrarEventListener
public class MyListener
{
    @RegistrarEventClass
    public static void onPlayerJoin(EventPlayerJoin event)
    {
        Player player = event.player;
        player.sendMessage(Text.literal("Welcome!"));
    }

    @RegistrarEventClass(priority = Priority.HIGH)
    public static void onChat(EventAsyncPlayerChat event)
    {
        // 过滤聊天消息
        String content = event.message.toLiteral();
        if (content.contains("badword"))
        {
            event.cancel();
            event.player.sendMessage(Text.literal("Please watch your language!"));
        }
    }

    @RegistrarEventClass
    public static void onDamage(EventEntityLivingDamage event)
    {
        // 保护新手
        if (event.entity instanceof EntityPlayer)
        {
            EntityPlayer player = (EntityPlayer) event.entity;
            if (player.getAbilities().creativeMode)
            {
                event.cancel();
            }
        }
    }
}
```

= 注意事项

- 异步事件需要快速处理，不要执行耗时操作
- 使用优先级控制事件处理顺序
- 可取消事件可以在高优先级监听器中取消
- 事件监听器需要在模块的 `onLoad` 中注册