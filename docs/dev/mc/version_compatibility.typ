#import "/lib/lib.typ": *;
#let title = [版本兼容性];
#show: template.with(title: title);

= 版本兼容性

MzLib 提供了强大的版本兼容性支持，让你的代码可以在多个 Minecraft 版本上运行。

= 版本号表示

MzLib 使用整数表示版本号：

- `1.x.y` → `x*100+y`
- 例如：`1.12.2` → `1202`, `1.14` → `1400`, `1.21.5` → `2105`

从 `26.1` 开始：
- `x.y` → `x*100+y*10`
- `x.y.z` → `x*100+y*10+z`
- 例如：`26.1` → `2610`, `26.1.1` → `2611`

= 版本段表示

使用 `v` 后缀表示版本范围：

- `V1300` - 从 1.13 开始有效 `[1.13, +∞)`
- `V_1400` - 在 1.14 之前有效 `[0, 1.14)`
- `V1300_1400` - 从 1.13 到 1.14 有效 `[1.13, 1.14)`
- `V_1300__1400_1600` - 在 (-∞, 1.13) ∪ [1.14, 1.16) 有效
- `V1600__1903` - 在 [0, 1.16) ∪ [1.19.3, +∞) 有效

= 检查版本

```java
// 获取当前版本
int version = MinecraftPlatform.instance.getVersion();

// 检查版本
if (version >= 1300)
{
    // 1.13+ 的代码
}
else
{
    // 旧版本代码
}

// 检查版本范围
if (version >= 1300 && version < 1400)
{
    // 1.13 到 1.14 之间
}
```

= 使用注解

```java
@VersionRange(begin = 1300, end = 1400)
@WrapMinecraftMethod(@VersionName(name = "method_name"))
ReturnType method()
{
    // 只在 1.13 到 1.14 之间执行
}
```

= 多版本实现

```java
public class VersionExample
{
    @VersionRange(end = 1300)
    public void doSomethingV_1300()
    {
        // 1.13 之前的实现
    }

    @VersionRange(begin = 1300)
    public void doSomethingV1300()
    {
        // 1.13+ 的实现
    }

    public void doSomething()
    {
        int version = MinecraftPlatform.instance.getVersion();

        if (version < 1300)
        {
            doSomethingV_1300();
        }
        else
        {
            doSomethingV1300();
        }
    }
}
```

= Wrapper 版本兼容

```java
@WrapMinecraftClass({
    @VersionName(name = "net.minecraft.server.level.ServerPlayer", end = 1400),
    @VersionName(name = "net.minecraft.server.level.ServerPlayer", begin = 1400, end = 1605),
    @VersionName(name = "net.minecraft.server.level.ServerPlayer", begin = 1605)
})
public interface EntityPlayer extends WrapperObject
{
    @VersionRange(end = 1300)
    @WrapMinecraftMethod(@VersionName(name = "getName"))
    String getNameV_1300();

    @VersionRange(begin = 1300)
    @WrapMinecraftMethod(@VersionName(name = "getName"))
    String getNameV1300();

    @SpecificImpl("getName")
    default String getName()
    {
        int version = MinecraftPlatform.instance.getVersion();

        if (version < 1300)
        {
            return getNameV_1300();
        }
        else
        {
            return getNameV1300();
        }
    }
}
```

= 常见版本差异

== ItemStack 数据格式

```java
// 1.20.5 之前
Option<NbtCompound> tag = itemStack.getTagV_2005();

// 1.20.5+
ComponentMapDefaultedV2005 components = itemStack.getComponentsV2005();
```

== 文本组件

```java
// 1.21 之前
Text text = Text.literal("Hello");

// 1.21+
TextLiteral text = TextLiteral.newInstance("Hello");
```

== 附魔显示

```java
// 1.13 之前
String enchantment = enchantment.getName();

// 1.13+
Text enchantment = enchantment.getDisplayName();
```

= 版本兼容最佳实践

== 1. 使用版本注解

```java
@VersionRange(begin = 1300)
public void featureV1300()
{
    // 明确标注版本范围
}
```

== 2. 提供默认实现

```java
@VersionRange(end = 1300)
default String getName()
{
    return "Unknown";
}
```

== 3. 使用 Optional 处理不存在的方法

```java
Option<String> name = Option.fromNullable(getNameIfAvailable());
```

== 4. 测试多个版本

```java
@Test
public void testCompatibility()
{
    // 在不同版本测试
    assert featureWorks();
}
```

= 支持的版本

== Bukkit/Spigot

- 1.12.2 及以上

== Fabric

- 1.14.4 及以上

== NeoForge

- 1.20.1 及以上

= 注意事项

- 使用版本注解确保代码在正确版本执行
- 测试所有支持的版本
- 查看版本更新日志了解变更
- 使用 Wrapper 系统处理版本差异
- 提供合理的默认值