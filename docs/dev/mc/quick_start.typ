#import "/lib/lib.typ": *;
#let title = [快速入门];
#show: template.with(title: title);

= 快速入门

本教程将带你快速了解如何使用 MzLib 开发 Minecraft 插件。

= 创建第一个模块

```java
import mz.mzlib.module.MzModule;
import mz.mzlib.module.RegistrarEventListener;
import mz.mzlib.event.RegistrarEventClass;

@RegistrarEventListener
public class MyPlugin extends MzModule
{
    public static MyPlugin instance = new MyPlugin();

    @Override
    public void onLoad()
    {
        // 模块加载时执行
        System.out.println("MyPlugin loaded!");
    }
}
```

= 监听事件

```java
import mz.mzlib.minecraft.event.player.EventPlayerJoin;
import mz.mzlib.minecraft.Player;
import mz.mzlib.minecraft.text.Text;

@RegistrarEventClass
public static void onPlayerJoin(EventPlayerJoin event)
{
    Player player = event.player;

    // 发送欢迎消息
    player.sendMessage(Text.literal("欢迎来到服务器！"));
}
```

= 创建命令

```java
import mz.mzlib.minecraft.command.Command;
import mz.mzlib.minecraft.command.CommandContext;
import mz.mzlib.minecraft.command.CommandSource;
import mz.mzlib.minecraft.text.Text;

public Command myCommand = new Command("mycommand", "mc")
    .setHandler(context ->
    {
        CommandSource source = context.source;
        source.sendMessage(Text.literal("命令执行成功！"));
    });

// 在 onLoad 中注册
this.register(myCommand);
```

= 操作物品

```java
import mz.mzlib.minecraft.item.ItemStack;
import mz.mzlib.minecraft.item.Items;
import mz.mzlib.minecraft.nbt.NbtCompound;

// 创建物品
ItemStack diamond = ItemStack.newInstance(Items.DIAMOND);

// 设置数量
diamond.setCount(64);

// 设置自定义名称
diamond.customName(Text.literal("我的钻石"));

// 设置 NBT 数据
NbtCompound tag = diamond.tagV_2005();
tag.put("custom_data", "value");

// 给玩家物品
player.getInventory().addItem(diamond);
```

= 发送消息

```java
import mz.mzlib.minecraft.text.Text;
import mz.mzlib.minecraft.text.TextColor;
import mz.mzlib.minecraft.text.TextTranslatable;

// 简单文本
player.sendMessage(Text.literal("Hello World"));

// 带颜色
player.sendMessage(Text.literal("红色文本").setColor(TextColor.RED));

// 可翻译文本
player.sendMessage(TextTranslatable.newInstance("chat.type.text", "Steve", "Hello"));

// 带样式的文本
player.sendMessage(
    Text.literal("点击这里")
        .setColor(TextColor.BLUE)
        .setUnderlined(true)
        .setClickEvent(new TextClickEvent(
            TextClickEvent.Action.OPEN_URL,
            "https://example.com"
        ))
);
```

= NBT 操作

```java
import mz.mzlib.minecraft.nbt.NbtCompound;

// 创建 NBT
NbtCompound nbt = NbtCompound.newInstance();
nbt.put("name", "test");
nbt.put("value", 123);

// 嵌套 NBT
NbtCompound nested = nbt.getNbtCompoundOrNew("data");
nested.put("x", 10);
nested.put("y", 20);

// 读取 NBT
Option<String> name = nbt.getString("name");
Option<Integer> value = nbt.getInt("value");
```

= 异步操作

```java
import mz.mzlib.util.async.AsyncFunction;
import mz.mzlib.minecraft.text.Text;

// 异步执行任务
AsyncFunction.run(() ->
{
    // 耗时操作（如数据库查询）
    Thread.sleep(1000);
    return "操作完成";
}).then(result ->
{
    // 在主线程执行
    player.sendMessage(Text.literal(result));
});
```

= 完整示例

```java
import mz.mzlib.module.MzModule;
import mz.mzlib.module.RegistrarEventListener;
import mz.mzlib.event.RegistrarEventClass;
import mz.mzlib.minecraft.event.player.EventPlayerJoin;
import mz.mzlib.minecraft.Player;
import mz.mzlib.minecraft.text.Text;
import mz.mzlib.minecraft.command.Command;
import mz.mzlib.minecraft.command.CommandContext;
import mz.mzlib.minecraft.item.ItemStack;
import mz.mzlib.minecraft.item.Items;

@RegistrarEventListener
public class MyPlugin extends MzModule
{
    public static MyPlugin instance = new MyPlugin();

    @Override
    public void onLoad()
    {
        System.out.println("MyPlugin loaded!");

        // 注册命令
        this.register(new Command("hello", "hi")
            .setHandler(context ->
            {
                context.source.sendMessage(Text.literal("Hello!"));
            })
        );
    }

    @RegistrarEventClass
    public static void onPlayerJoin(EventPlayerJoin event)
    {
        Player player = event.player;

        // 发送欢迎消息
        player.sendMessage(Text.literal("欢迎来到服务器！"));

        // 给玩家一个钻石
        ItemStack diamond = ItemStack.newInstance(Items.DIAMOND);
        player.getInventory().addItem(diamond);
    }
}
```

= 下一步

- 阅读完整的 #link("tutorial/index")[教程]
- 查看 #link("item/index")[物品系统] 文档
- 了解 #link("event")[事件系统]
- 学习 #link("permission")[权限系统]