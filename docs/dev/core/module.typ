#import "/lib/lib.typ": *;
#let title = [模块系统];
#show: template.with(title: title);


模块系统是 MzLib 的核心基础设施，提供模块化加载、依赖管理、生命周期控制等功能。

= 核心概念

== MzModule

`MzModule` 是所有模块的基类，提供模块的生命周期管理和组件注册功能。

```java
public abstract class MzModule
{
    // 模块加载时调用
    public void onLoad() { }

    // 模块启用时调用
    public void onEnable() { }

    // 模块禁用时调用
    public void onDisable() { }

    // 注册组件到模块
    public void register(Registrable component) { }

    // 注销组件
    public void unregister(Registrable component) { }
}
```

== 注册器模式

注册器模式是 MzLib 的核心设计模式，提供统一的组件注册机制。

=== IRegistrar

`IRegistrar` 是注册器接口，支持依赖管理和自动解析。

```java
public interface IRegistrar
{
    // 注册组件
    <T extends Registrable> T register(T component);

    // 注销组件
    <T extends Registrable> T unregister(T component);

    // 获取所有注册的组件
    List<Registrable> getRegistrables();

    // 获取依赖的注册器
    List<IRegistrar> getDependencies();
}
```

=== Registrable

`Registrable` 是可注册接口，所有可注册的组件都需要实现此接口。

```java
public interface Registrable
{
    // 注册时调用
    default void onRegister(IRegistrar registrar) { }

    // 注销时调用
    default void onUnregister(IRegistrar registrar) { }
}
```

= 模块生命周期

模块的生命周期分为三个阶段：

/ #[= 加载阶段 (onLoad)]:

    模块加载时调用，用于：
    
    - 初始化模块状态
    - 注册子模块
    - 注册组件
    - 加载配置
    - 加载国际化资源

/ #[= 启用阶段 (onEnable)]:

    模块启用时调用，用于：
    
    - 启动服务
    - 注册事件监听器
    - 注册命令
    - 启动定时任务

/ #[= 禁用阶段 (onDisable)]:

    模块禁用时调用，用于：
    
    - 停止服务
    - 注销事件监听器
    - 注销命令
    - 停止定时任务
    - 清理资源

= 依赖管理

模块系统支持依赖管理，自动解析依赖关系并进行拓扑排序。

== 声明依赖

```java
public class MyModule extends MzModule
{
    public MyModule()
    {
        // 添加依赖
        this.addDependency(OtherModule.instance);
    }
}
```

== 依赖解析

模块系统会自动：
- 解析依赖关系
- 检测循环依赖
- 按照依赖顺序加载模块
- 按照相反顺序卸载模块

= 子模块

模块支持嵌套，一个模块可以包含多个子模块。

```java
public class ParentModule extends MzModule
{
    @Override
    public void onLoad()
    {
        // 注册子模块
        this.register(ChildModule1.instance);
        this.register(ChildModule2.instance);
    }
}
```

= 注册事件监听器

使用 `@RegistrarEventListener` 注解自动注册事件监听器。

```java
@RegistrarEventListener
public class MyListener
{
    @RegistrarEventClass
    public static void onEvent(MyEvent event)
    {
        // 事件处理
    }
}
```

= 注册器管理

== RegistrarRegistrar

`RegistrarRegistrar` 是注册器的管理器，用于管理所有注册器。

```java
// 获取注册器管理器
IRegistrar registrar = MzModule.getRegistrar();

// 注册组件
registrar.register(myComponent);

// 注销组件
registrar.unregister(myComponent);
```

== SimpleRegistrar

`SimpleRegistrar` 是 `IRegistrar` 的简单实现，适用于不需要依赖管理的场景。

```java
IRegistrar registrar = new SimpleRegistrar();

// 注册组件
registrar.register(myComponent);
```

= 最佳实践

/ #[= 使用单例模式]:

    模块通常使用单例模式，方便访问：

    ```java
    public class MyModule extends MzModule
    {
        public static MyModule instance = new MyModule();
    }
    ```

/ #[= 在 onLoad 中注册组件]:

    所有组件注册应该在 `onLoad` 中完成：

    ```java
    @Override
    public void onLoad()
    {
        this.register(myCommand);
        this.register(MyListener.class);
        this.register(MyComponent.instance);
    }
    ```

/ #[= 在 onEnable 中启动服务]:

    服务启动应该在 `onEnable` 中完成：

    ```java
    @Override
    public void onEnable()
    {
        this.myService.start();
    }
    ```

/ #[= 在 onDisable 中清理资源]:

    资源清理应该在 `onDisable` 中完成：

    ```java
    @Override
    public void onDisable()
    {
        this.myService.stop();
        this.myService = null;
    }
    ```

/ #[= 使用依赖管理]:

    如果模块之间有依赖关系，使用依赖管理：

    ```java
    public class MyModule extends MzModule
    {
        public MyModule()
        {
            this.addDependency(OtherModule.instance);
        }
    }
    ```

= 示例

完整的模块示例：

```java
import mz.mzlib.module.MzModule;
import mz.mzlib.module.RegistrarEventListener;
import mz.mzlib.event.RegistrarEventClass;
import mz.mzlib.event.Event;
import mz.mzlib.Priority;

@RegistrarEventListener
public class MyModule extends MzModule
{
    public static MyModule instance = new MyModule();

    private MyService myService;

    @Override
    public void onLoad()
    {
        // 注册命令
        this.register(MyCommand.instance);

        // 注册事件监听器（自动）
        // MyListener 类上有 @RegistrarEventListener 注解

        // 注册服务
        this.myService = new MyService();
        this.register(this.myService);
    }

    @Override
    public void onEnable()
    {
        // 启动服务
        this.myService.start();
    }

    @Override
    public void onDisable()
    {
        // 停止服务
        this.myService.stop();
        this.myService = null;
    }
}

@RegistrarEventListener
class MyListener
{
    @RegistrarEventClass(priority = Priority.HIGHEST)
    public static void onMyEvent(MyEvent event)
    {
        // 高优先级处理
    }
}
```

#cardInfo[
    模块系统是 MzLib 的核心，理解模块系统对于使用 MzLib 开发插件非常重要。
    
    模块系统提供了：
    - 统一的组件注册机制
    - 自动依赖管理
    - 生命周期控制
    - 事件系统集成
]