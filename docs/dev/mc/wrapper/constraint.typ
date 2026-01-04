#import "/lib/lib.typ": *;
#set raw(lang: "java");
#let title = [约束];
#show: template.with(title: title);

我们会检查您包装的成员是否存在，因此请标注成员支持的平台 \
无约束的成员则不需要标注 \
此外，包装类上也应当标注Minecraft类支持的平台

= 版本约束

使用`@VersionRange`，有`begin`和`end`属性，与#link("./index#@VersionName")[`@VersionName`]类似

```java
@VersionRange(begin = 1300) // 1.13起
@WrapMinecraftClass(@VersionName(name = "com.mojang.datafixers.DataFixer"))
public interface DataFixerV1300 extends WrapperObject
{
    // sth.
}
```

使用多个注解表示支持多个版本段

```java
// in class EntityPlayer
@VersionRange(end = 1600) // 1.16前
@VersionRange(begin = 2002) // 1.20.2起
@WrapMinecraftFieldAccessor({
    @VersionName(name = "language", end = 1400),
    @VersionName(name = "clientLanguage", begin = 1400, end = 1600),
    @VersionName(name = "language", begin = 2002)
})
String getLanguageV_1600__2002();
```

= 平台类型约束
 
使用`@MinecraftPlatform.Enabled`表示仅支持特定平台

```java
@MinecraftPlatform.Enabled(MinecraftPlatform.Tag.BUKKIT) // 仅Bukkit
@WrapSameClass(MinecraftServer.class)
public interface MinecraftServerBukkit extends MinecraftServer
{
    // sth.
}
```

混合端

```java
// 仅在Bukkit且Fabric里（混合端）启用
@MinecraftPlatform.Enabled(MinecraftPlatform.Tag.BUKKIT, MinecraftPlatform.Tag.FABRIC)
void example();
```

使用多个注解表示支持多个平台

```java
// 在Bukkit和Fabric里都启用
@MinecraftPlatform.Enabled(MinecraftPlatform.Tag.BUKKIT)
@MinecraftPlatform.Enabled(MinecraftPlatform.Tag.FABRIC)
void example();

// 在Bukkit且Fabric里（混合端）启用
// 在Paper中也启用
@MinecraftPlatform.Enabled(MinecraftPlatform.Tag.BUKKIT, MinecraftPlatform.Tag.FABRIC)
@MinecraftPlatform.Enabled(MinecraftPlatform.Tag.PAPER)
void example1();
```

使用`@MinecraftPlatform.Disabled`表示不支持特定平台

```java
// 在Bukkit中禁用，其它平台启用
@MinecraftPlatform.Disabled(MinecraftPlatform.Tag.BUKKIT)
void example();

// 在Bukkit中启用
// 但Fabric除外
@MinecraftPlatform.Enabled(MinecraftPlatform.Tag.BUKKIT)
@MinecraftPlatform.Disabled(MinecraftPlatform.Tag.FABRIC)
void example1();
```

也可多个组合使用