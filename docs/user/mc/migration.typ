#import "/lib/lib.typ": *;
#let title = [迁移指南];
#show: template.with(title: title);

= 迁移指南

== 从旧版本迁移

MzLib 提供了数据自动升级功能，大部分数据会自动转换。

== ItemStack 数据迁移

```java
// 旧版本数据
NbtCompound oldNbt = ...;

// 自动升级
ItemStack stack = ItemStack.decode(oldNbt).getValue().unwrapOr(ItemStack.EMPTY);
```

== 配置文件迁移

旧版本的配置文件可能需要手动更新：

1. 备份旧配置文件
2. 查看新版本的配置示例
3. 更新配置项
4. 重启服务器

== API 变更

=== 1.20.5 组件系统

从 1.20.5 开始，物品数据使用组件系统：

```java
// 旧版本（1.20.5 之前）
NbtCompound tag = itemStack.getTagV_2005().unwrap();

// 新版本（1.20.5+）
ComponentMapV2005 components = itemStack.getComponentsV2005();
```

=== 文本组件

文本组件 API 保持兼容，但建议使用新方法：

```java
// 旧方法（仍然支持）
Text text = Text.literal("Hello");

// 推荐方法
TextLiteral text = TextLiteral.newInstance("Hello");
```

== 版本兼容

使用版本注解确保代码在不同版本正确运行：

```java
@VersionRange(begin = 1300)
@WrapMinecraftMethod(@VersionName(name = "method_name"))
ReturnType method();
```

== 注意事项

- 迁移前务必备份数据
- 测试迁移结果
- 查看更新日志了解变更