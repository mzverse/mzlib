#import "/lib/lib.typ": *;
#set raw(lang: "java");
#let title = [网络数据包];
#show: template.with(title: title);


网络数据包系统允许你拦截、修改和发送 Minecraft 的网络数据包，实现各种高级功能。

= 核心概念

== Packet

`Packet` 是所有数据包的基类，代表客户端到服务器（C2S）或服务器到客户端（S2C）的数据包。

```java
public interface Packet
{
    // 数据包工厂
    PacketFactory<?> getFactory();
}
```

== PacketListener

`PacketListener` 是数据包监听器，用于拦截和处理数据包。

```java
public class PacketListener<P extends Packet>
{
    private final PacketFactory<P> factory;
    private final Consumer<PacketEvent.Specialized<P>> handler;

    public PacketListener(PacketFactory<P> factory, Consumer<PacketEvent.Specialized<P>> handler)
    {
        this.factory = factory;
        this.handler = handler;
    }
}
```

== PacketEvent

`PacketEvent` 是数据包事件，包含数据包和相关上下文信息。

```java
public class PacketEvent
{
    // 获取数据包
    public P getPacket();

    // 取消数据包
    public void setCancelled(boolean cancelled);

    // 确保数据包是副本
    public void ensureCopied();

    // 同步处理
    public void sync(Runnable action);
}
```

= 发送数据包

向玩家发送数据包：

```java
import mz.mzlib.minecraft.entity.EntityPlayer;
import mz.mzlib.minecraft.network.PacketS2cChatMessage;
import mz.mzlib.minecraft.text.Text;

// 创建聊天消息数据包
PacketS2cChatMessage packet = PacketS2cChatMessage.newInstance(
    Text.literal("Hello from server!")
);

// 发送给玩家
player.sendPacket(packet);
```

= 接收数据包

模拟服务器接收玩家的数据包：

```java
import mz.mzlib.minecraft.network.PacketC2sChatMessage;

// 创建客户端到服务器的聊天消息数据包
PacketC2sChatMessage packet = PacketC2sChatMessage.newInstance("Hello from client!");

// 服务器接收数据包
player.receivePacket(packet);
```

= 监听数据包

监听数据包的发送和接收，用于拦截和修改数据包。

== 基本监听

```java
import mz.mzlib.minecraft.network.PacketC2sChatMessage;

@Override
public void onLoad()
{
    // 监听客户端到服务器的聊天消息
    this.register(new PacketListener<>(PacketC2sChatMessage.FACTORY, packetEvent ->
    {
        System.out.println("收到聊天消息: " + packetEvent.getPacket().getMessage());
        
        // 取消数据包
        packetEvent.setCancelled(true);
    }));
}
```

== 监听服务器到客户端的数据包

```java
import mz.mzlib.minecraft.network.PacketS2cWindowSlotUpdate;

@Override
public void onLoad()
{
    // 监听服务器到客户端的窗口槽位更新
    this.register(new PacketListener<>(PacketS2cWindowSlotUpdate.FACTORY, packetEvent ->
    {
        System.out.println("发送窗口槽位更新");
        
        // 获取玩家
        Option<EntityPlayer> player = packetEvent.getPlayer();
        
        if (player.isSome())
        {
            System.out.println("发送给玩家: " + player.unwrap().getName());
        }
    }));
}
```

= 修改数据包

修改数据包内容，需要先确保数据包是副本。

```java
@Override
public void onLoad()
{
    this.register(new PacketListener<>(PacketS2cWindowSlotUpdate.FACTORY, packetEvent ->
    {
        // 确保数据包是副本（避免影响其他玩家）
        packetEvent.ensureCopied();
        
        // 修改数据包
        PacketS2cWindowSlotUpdate packet = packetEvent.getPacket();
        packet.setItemStack(ItemStack.empty());
        
        System.out.println("已修改窗口槽位更新");
    }));
}
```

= 同步处理

有时需要在主线程处理数据包，使用 `sync` 方法。

```java
import mz.mzlib.minecraft.network.PacketC2sCloseWindow;

@Override
public void onLoad()
{
    this.register(new PacketListener<>(PacketC2sCloseWindow.FACTORY, packetEvent ->
    {
        // 同步处理
        packetEvent.sync(() ->
        {
            // 在主线程执行
            System.out.println("关闭窗口");
            
            // 取消数据包
            packetEvent.setCancelled(true);
        });
    }));
}
```

= 数据包类型

MzLib 支持多种数据包类型：

== 客户端到服务器（C2S）

```java
// 聊天消息
PacketC2sChatMessage

// 关闭窗口
PacketC2sCloseWindow

// 点击窗口
PacketC2sClickWindow

// 使用物品
PacketC2sUseItem

// 移动
PacketC2sMovePlayerPos
PacketC2sMovePlayerPosRot
PacketC2sMovePlayerRot

// 交互
PacketC2sInteract
```

== 服务器到客户端（S2C）

```java
// 聊天消息
PacketS2cChatMessage

// 窗口槽位更新
PacketS2cWindowSlotUpdate

// 打开窗口
PacketS2cOpenWindow

// 关闭窗口
PacketS2cCloseWindow

// 设置物品
PacketS2cSetSlot

// 实体移动
PacketS2cEntityMove
PacketS2cEntityPosition
PacketS2cEntityRotation

// 实体属性
PacketS2cUpdateAttributes
```

= 数据包捆绑（1.19.4+）

Minecraft 1.19.4+ 支持数据包捆绑，可以一次性发送多个数据包。

```java
import mz.mzlib.minecraft.network.PacketBundle;

// 创建数据包捆绑
PacketBundle bundle = PacketBundle.newInstance();

// 添加数据包
bundle.add(PacketS2cChatMessage.newInstance(Text.literal("Message 1")));
bundle.add(PacketS2cChatMessage.newInstance(Text.literal("Message 2")));

// 发送捆绑
player.sendPacket(bundle);
```

= 数据包回调（1.19.1+）

Minecraft 1.19.1+ 支持数据包回调，可以跟踪数据包的确认状态。

```java
import mz.mzlib.minecraft.network.PacketCallbacksV1901;

// 发送带回调的数据包
player.sendPacket(packet, new PacketCallbacksV1901()
{
    @Override
    public void onSuccess()
    {
        System.out.println("数据包发送成功");
    }

    @Override
    public void onFailure()
    {
        System.out.println("数据包发送失败");
    }
});
```

= 最佳实践

/ #[= 不要缓存数据包]:

    不要将 `packetEvent.getPacket()` 的结果缓存，因为它可能会改变。

/ #[= 使用 ensureCopied]:

    修改数据包前，确保调用 `ensureCopied()` 避免影响其他玩家。

/ #[= 同步处理]:

    需要在主线程操作时，使用 `sync()` 方法。

/ #[= 取消数据包]:

    谨慎取消数据包，可能影响游戏正常功能。

/ #[= 性能考虑]:

    数据包监听器在网络线程执行，避免耗时操作。

= 示例

完整的数据包拦截和修改示例：

```java
import mz.mzlib.module.MzModule;
import mz.mzlib.minecraft.entity.EntityPlayer;
import mz.mzlib.minecraft.item.ItemStack;
import mz.mzlib.minecraft.network.*;
import mz.mzlib.minecraft.text.Text;
import mz.mzlib.util.Option;

public class PacketInterceptor extends MzModule
{
    public static PacketInterceptor instance = new PacketInterceptor();

    @Override
    public void onLoad()
    {
        // 监听聊天消息
        this.register(new PacketListener<>(PacketC2sChatMessage.FACTORY, this::onChatMessage));

        // 监听窗口槽位更新
        this.register(new PacketListener<>(PacketS2cWindowSlotUpdate.FACTORY, this::onWindowSlotUpdate));

        // 监听关闭窗口
        this.register(new PacketListener<>(PacketC2sCloseWindow.FACTORY, this::onCloseWindow));
    }

    private void onChatMessage(PacketEvent.Specialized<PacketC2sChatMessage> event)
    {
        PacketC2sChatMessage packet = event.getPacket();
        String message = packet.getMessage();

        System.out.println("收到聊天消息: " + message);

        // 检查敏感词
        if (message.contains("badword"))
        {
            event.setCancelled(true);
            System.out.println("取消包含敏感词的消息");
        }
    }

    private void onWindowSlotUpdate(PacketEvent.Specialized<PacketS2cWindowSlotUpdate> event)
    {
        // 确保数据包是副本
        event.ensureCopied();

        PacketS2cWindowSlotUpdate packet = event.getPacket();
        ItemStack item = packet.getItemStack();

        // 修改物品显示
        if (!item.isEmpty())
        {
            ItemStack newItem = ItemStack.builder()
                .from(item)
                .customName(Text.literal("Modified Item"))
                .build();
            
            packet.setItemStack(newItem);
        }
    }

    private void onCloseWindow(PacketEvent.Specialized<PacketC2sCloseWindow> event)
    {
        // 同步处理
        event.sync(() ->
        {
            Option<EntityPlayer> player = event.getPlayer();

            if (player.isSome())
            {
                EntityPlayer p = player.unwrap();
                p.sendMessage(Text.literal("窗口已关闭"));
            }

            // 取消关闭
            event.setCancelled(true);
        });
    }
}
```

#cardInfo[
    网络数据包系统提供了强大的数据包拦截和修改功能。
    
    主要功能：
    - 发送和接收数据包
    - 监听数据包
    - 修改数据包内容
    - 同步处理
    - 数据包捆绑（1.19.4+）
    - 数据包回调（1.19.1+）
]
