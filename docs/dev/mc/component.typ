#import "/lib/lib.typ": *;
#let title = [组件系统 (1.20.5+)];
#show: template.with(title: title);

组件系统（1.20.5+）提供了新的数据组件管理方式。

= ComponentMapV2005

`ComponentMapV2005` 是组件映射。

```java
// 获取组件映射
ComponentMapV2005 components = itemStack.getComponentsV2005();

// 获取组件
Option<T> component = components.get(key);

// 检查组件是否存在
boolean has = components.has(key);
```

= ComponentKeyV2005

`ComponentKeyV2005` 是组件键。

```java
// 创建组件键
ComponentKeyV2005<String> key = ComponentKeyV2000.of(
    Registry.DATA_COMPONENT_TYPE,
    Identifier.of("mod:custom_component")
);

// 获取组件键 ID
Identifier id = key.getId();
```

= ComponentsAccessV2105

`ComponentsAccessV2105` 提供了组件访问功能。

```java
// 获取组件访问器
ComponentsAccessV2105 access = ComponentsAccessV2105.of(itemStack);

// 获取组件
Option<T> component = access.get(key);

// 设置组件
access.set(key, value);

// 移除组件
access.remove(key);

// 检查组件是否存在
boolean has = access.has(key);
```

= 内置组件

== GameProfileComponentV2005

玩家配置文件组件。

```java
// 创建组件
GameProfileComponentV2005 component = new GameProfileComponentV2005(profile);

// 获取配置文件
GameProfile profile = component.value();
```

== LoreComponentV2005

描述组件。

```java
// 创建组件
LoreComponentV2005 component = new LoreComponentV2005(lines);

// 获取描述行
List<Text> lines = component.lines();
```

== NbtCompoundComponentV2005

NBT 复合标签组件。

```java
// 创建组件
NbtCompoundComponentV2005 component = new NbtCompoundComponentV2005(nbt);

// 获取 NBT
NbtCompound nbt = component.value();
```

== WrittenBookContentComponentV2005

成书内容组件。

```java
// 创建组件
WrittenBookContentComponentV2005 component = new WrittenBookContentComponentV2005(
    title,
    author,
    generation,
    pages,
    resolved
);

// 获取内容
// ...
```

= 使用示例

```java
public class ComponentExample
{
    // 设置物品描述
    public void setLore(ItemStack stack, List<Text> lines)
    {
        ComponentsAccessV2105 access = ComponentsAccessV2105.of(stack);
        access.set(ComponentKeysV2005.LORE, new LoreComponentV2005(lines));
    }

    // 设置玩家头颅纹理
    public void setPlayerHeadTexture(ItemStack stack, GameProfile.Description description)
    {
        ComponentsAccessV2105 access = ComponentsAccessV2105.of(stack);
        access.set(ComponentKeysV2005.PROFILE, new GameProfileComponentV2005(description));
    }

    // 添加自定义 NBT 数据
    public void addCustomData(ItemStack stack, NbtCompound data)
    {
        ComponentsAccessV2105 access = ComponentsAccessV2105.of(stack);
        access.set(ComponentKeysV2005.CUSTOM_DATA, new NbtCompoundComponentV2005(data));
    }
}
```

= 注意事项

- 组件系统仅在 1.20.5+ 可用
- 组件是只读的，使用 `ComponentsAccessV2105` 修改
- 检查版本后再使用组件系统