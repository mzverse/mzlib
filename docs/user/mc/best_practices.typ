#import "/lib/lib.typ": *;
#let title = [最佳实践];
#show: template.with(title: title);

= 最佳实践

== 代码风格

=== 使用函数式类型

```java
// 使用 Option
Option<String> name = player.getName();

// 使用 Result
Result<String, Error> result = doSomething();

// 使用 Either
Either<Error, String> value = getValue();
```

=== 使用 Editor 模式

```java
// 不好的做法
NbtCompound tag = itemStack.tagV_2005();
tag.put("key", "value");

// 好的做法
for (NbtCompound tag : itemStack.tagV_2005().revise())
{
    tag.put("key", "value");
}
```

== 错误处理

=== 使用 Result 处理错误

```java
Result<ItemStack, String> result = ItemStack.decode(nbt);

for (ItemStack stack : result.getValue())
{
    // 成功
}

for (String error : result.getError())
{
    // 失败
}
```

=== 使用 Option 处理可能为空的值

```java
Option<EntityPlayer> player = getPlayer();

for (EntityPlayer p : player)
{
    // 玩家存在
    p.sendMessage(Text.literal("Hello"));
}

// 或者
player.ifPresent(p -> p.sendMessage(Text.literal("Hello")));
```

== 事件处理

=== 使用注解注册事件

```java
@RegistrarEventListener
public class MyListener
{
    @RegistrarEventClass
    public static void onPlayerJoin(EventPlayerJoin event)
    {
        Player player = event.player;
        // 处理玩家加入
    }
}
```

=== 使用正确的优先级

```java
@RegistrarEventClass(priority = Priority.HIGHEST)
public static void onEvent(MyEvent event)
{
    // 高优先级处理
}
```

== 版本兼容

=== 使用版本注解

```java
@VersionRange(begin = 1300, end = 1400)
@WrapMinecraftMethod(@VersionName(name = "method_name"))
ReturnType method();
```

=== 检查版本

```java
if (MinecraftPlatform.instance.getVersion() >= 1300)
{
    // 1.13+ 的代码
}
else
{
    // 旧版本代码
}
```

== 数据存储

=== 使用 DataKey

```java
public static final DataKey<Player, String, ?> CUSTOM_DATA = DataKey.of("custom_data");

// 设置数据
CUSTOM_DATA.set(player, "value");

// 获取数据
Option<String> data = CUSTOM_DATA.get(player);
```

=== 使用 NBT

```java
// 使用 NbtCompound
NbtCompound nbt = NbtCompound.newInstance();
nbt.put("key", "value");
```

== 国际化

=== 使用 I18n

```java
// 翻译文本
Text text = MinecraftI18n.resolveText(player, "myplugin.message", "arg1");

// 添加翻译文件
// lang/zh_cn.json
{
    "myplugin.message": "消息: %s"
}
```

== 测试

=== 使用 Tester

```java
@Tester
public class MyTests
{
    @TesterFunction
    public void testSomething(TesterContext context)
    {
        // 测试代码
        assert condition;
    }
}
```

== 注意事项

- 遵循代码规范
- 正确处理错误
- 保持版本兼容
- 使用国际化
- 编写测试