#import "/lib/lib.typ": *;
#let title = [插件系统];
#show: template.with(title: title);

插件系统提供了插件管理和生命周期管理功能。

= Plugin

`Plugin` 是插件的基本接口，定义了插件的生命周期方法。

```java
public class MyPlugin implements Plugin
{
    @Override
    public void onLoad()
    {
        // 插件加载时调用
    }

    @Override
    public void onEnable()
    {
        // 插件启用时调用
    }

    @Override
    public void onDisable()
    {
        // 插件禁用时调用
    }
}
```

= PluginManager

`PluginManager` 负责管理所有插件的加载、启用和禁用。

```java
// 获取插件管理器
PluginManager manager = PluginManager.instance;

// 加载插件
manager.loadPlugin(plugin);

// 启用插件
manager.enablePlugin(plugin);

// 禁用插件
manager.disablePlugin(plugin);

// 获取所有插件
Collection<Plugin> plugins = manager.getPlugins();
```

= 使用示例

```java
public class MyModule extends MzModule
{
    @Override
    public void onLoad()
    {
        this.register(PluginManager.instance);
    }
}
```

= 生命周期

插件的生命周期包括以下几个阶段：

1. *加载* - `onLoad()` 被调用
2. *启用* - `onEnable()` 被调用
3. *禁用* - `onDisable()` 被调用

= 注意事项

- 插件的加载和启用是分离的
- 确保在 `onDisable()` 中清理资源
- 不要在 `onLoad()` 中执行耗时操作