#import "/lib/lib.typ": *;
#set raw(lang: "java");
#let title = [网络数据包];
#show: template.with(title: title);



= 发包

发送一个数据包非常简单，只需调用`EntityPlayer#sendPacket`

= 收包

使得服务器认为收到了一个玩家的数据包并进行处理，调用`EntityPlayer#receivePacket`

= 收发包监听

对于一个`Packet`的子类，我们可以监听它实例的发送或接收

只需创建一个`PacketListener`实例并注册到你的模块中

监听器不一定在主线程上触发

例如我们需要监听`PacketC2sChatMessage`

```java
// in your module
@Override
public void onLoad()
{
    this.register(new PacketListener<>(PacketC2sChatMessage.FACTORY, packetEvent->
    {
        packetEvent.setCancelled(true); // 取消收包，如果你想这么做的话
    }));
}
```

== 从数据包事件中获取和修改数据包

当数据包监听器被调用时，相关信息会被封装在数据包事件`PacketEvent.Specialized<P>`

其中包含了这个数据包，使用时调用`packetEvent#getPacket`但不要将这个结果储存，因为它随时可能会改变

如果需要修改发包的内容，你可能需要先确保这个数据包是副本（因为Mojang可能会把同一个数据包发送给不同玩家），通过`packetEvent#ensureCopied`

```java
this.register(new PacketListener<>(PacketS2cWindowSlotUpdate.FACTORY, packetEvent->
{
    // 确保数据包是副本
    packetEvent.ensureCopied();
    // 修改数据包
    packetEvent.getPacket().setItemStack(ItemStack.empty());
}));
```

== 同步监听

异步监听能进行的操作有限

有时处理收发包时你可能需要进行同步操作

这时调用`sync`方法，参数是你要执行的操作action

如果该监听器本身是在主线程中触发，则该action会立即完成

否则该数据包会被推迟到主线程下一个tick再继续处理

如果MC本身就要在主线程处理这个数据包（如`PacketC2sCloseWindow`），则调用并`sync`方法不会导致更多延迟

```java
this.register(new PacketListener<>(PacketC2sCloseWindow.FACTORY, packetEvent->
{
    // 如果需要同步处理
    packetEvent.sync(()->
    {
        // 如果需要则取消事件
        packetEvent.setCancelled(true); // 取消事件
    });
}));
```
