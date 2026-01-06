#import "/lib/lib.typ": *;
#set raw(lang: "java");
#let title = [ElementSwitcher];
#show: template.with(title: title);



ElementSwitcher 是 MzLib 中用于控制类、方法、字段等程序元素是否启用的核心机制。

= 作用

ElementSwitcher 提供了一个注解驱动的开关系统，允许开发者根据运行时条件（如 JVM 版本、Minecraft 版本、平台类型等）动态启用或禁用类、方法、字段等元素。

这个机制特别适用于：

- *跨版本兼容*：不同 Minecraft 版本中某些类或成员可能不存在或行为不同
- *平台适配*：不同平台（Bukkit、Fabric、NeoForge）可能有不同的实现
- *条件启用*：根据运行环境自动选择合适的实现

= 下游用法

ElementSwitcher 本身是一个接口，下游开发者主要通过使用预定义的注解来控制元素的启用。

== 使用预定义注解

MzLib 提供了多个预定义的注解，开发者可以直接使用：

```java
// 仅在 Java 11 及以上启用
@JvmVersion(begin = 11)
public class SomeClass {
    // ...
}

// 仅在 Minecraft 1.13 及以上启用
@VersionRange(begin = 1300)
public class NewFeatureV1300 {
    // ...
}

// 仅在 Bukkit 平台启用
@MinecraftPlatform.Enabled("bukkit")
public class BukkitSpecificClass {
    // ...
}

// 在 Fabric 平台禁用
@MinecraftPlatform.Disabled("fabric")
public class SomeClass {
    // ...
}
```

== 组合使用注解

多个注解可以组合使用，实现更复杂的条件判断：

```java
// 仅在 Bukkit 平台且 Minecraft 1.13 及以上启用（AND 逻辑）
@MinecraftPlatform.Enabled("bukkit")
@VersionRange(begin = 1300)
public class BukkitNewFeatureV1300 {
    // ...
}

// 在 [1.13, 1.19.3) 或 [1.19, ∞) 版本启用（OR 逻辑）
@VersionRange(begin = 1300, end = 1903)
@VersionRange(begin = 1900)
public class SomeClass {
    // ...
}
```

== 在不同层级使用

ElementSwitcher 可以在类、方法、字段等不同层级使用：

```java
// 类级别
@VersionRange(begin = 1300)
public class SomeClassV1300 {
    // 方法级别
    @VersionRange(end = 1600)
    public void oldMethod() {
        // ...
    }

    // 字段级别
    @VersionRange(begin = 1500)
    private String newField;
}
```

== 逻辑规则

- *AND 逻辑*：多个不同注解同时存在时，所有条件必须同时满足

ElementSwitcher 机制会在运行时自动检查这些条件，并根据实际环境决定是否启用相应的元素。