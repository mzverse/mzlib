#import "/lib/lib.typ": *;
#let title = [映射系统];
#show: template.with(title: title);

映射系统提供了不同映射之间的转换功能。

= Mappings

`Mappings` 是映射接口。

```java
// 映射类名
String mappedClass = mappings.mapClass("net.minecraft.server.level.ServerPlayer");

// 映射字段
String mappedField = mappings.mapField("net.minecraft.server.level.ServerPlayer", "connection");

// 映射方法
String mappedMethod = mappings.mapMethod("net.minecraft.server.level.ServerPlayer", "method_14235", "()V");
```

= MappingsMerged

`MappingsMerged` 是合并映射。

```java
// 创建合并映射
MappingsMerged merged = MappingsMerged.of(mappings1, mappings2);

// 映射会按顺序尝试
String result = merged.mapClass("class.name");
```

= MappingsUtil

`MappingsUtil` 提供映射工具方法。

```java
// 获取当前映射
Mappings current = MappingsUtil.getCurrent();

// 获取 Mojang 映射
Mappings mojang = MappingsUtil.getMojang();

// 获取 Spigot 映射
Mappings spigot = MappingsUtil.getSpigot();

// 获取 Yarn 映射
Mappings yarn = MappingsUtil.getYarn();
```

= MinecraftMappingsFetcher

`MinecraftMappingsFetcher` 获取 Minecraft 映射。

```java
// 获取 Mojang 映射
Mappings fetcher = MinecraftMappingsFetcherMojang.instance;
Mappings mappings = fetcher.fetch(version);

// 获取 Spigot 映射
Mappings fetcher = MinecraftMappingsFetcherSpigot.instance;
Mappings mappings = fetcher.fetch(version);

// 获取 Yarn 映射
Mappings fetcher = MinecraftMappingsFetcherYarn.instance;
Mappings mappings = fetcher.fetch(version);
```

= 注意事项

- 映射用于版本兼容
- 不同映射有不同的命名约定
- 使用 `@WrapMinecraftClass` 等注解自动处理映射