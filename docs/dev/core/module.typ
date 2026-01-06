#import "/lib/lib.typ": *;
#let title = [模块系统];
#show: template.with(title: title);


模块系统是 MzLib 的核心概念之一，用于模块化编写各功能并管理生命周期。

= 核心概念

== MzModule

`MzModule` 提供模块的生命周期管理和组件注册功能。模块通常是单例常量（`static final instance`）。

```java
public class MzModule
{
    // 模块加载时调用
    public void onLoad() { }

    // 模块卸载时调用
    public void onUnload() throws Throwable { }

    // 注册组件到模块
    public void register(Object object) { }

    // 注销组件
    public void unregister(Object object) { }
}
```

= 模块生命周期

模块的生命周期分为两个阶段：

/ #[= 加载阶段 (load)]:

    模块加载时调用 `load()` 方法，用于：
    
    - 调用 `onLoad()` 初始化模块
    - 注册子模块
    - 注册组件（监听器、命令等）

/ #[= 卸载阶段 (unload)]:

    模块卸载时调用 `unload()` 方法，用于：
    
    - 按照注册相反的顺序自动注销所有组件
    - 卸载子模块
    - 调用 `onUnload()` 清理资源

= 使用方式

== 程序入口点

一个程序（插件）在入口点加载其主模块（通常是一个），在程序生命周期结束时卸载主模块。主模块使用 `load()` 和 `unload()` 手动管理：

```java
public class MyPlugin
{
    public static MyPlugin instance = new MyPlugin();
    public static final MainModule mainModule = new MainModule();

    public void onEnable()
    {
        // 加载主模块
        mainModule.load();
    }

    public void onDisable()
    {
        // 卸载主模块
        mainModule.unload();
    }
}
```

== 定义模块

模块通常定义为单例常量。主模块在 `onLoad()` 中注册子模块：

```java
public class MainModule extends MzModule
{
    public static final MainModule instance = new MainModule();

    @Override
    public void onLoad()
    {
        // 注册子模块
        this.register(CommandModule.instance);
        this.register(ListenerModule.instance);

        // 注册事件监听器
        this.register(new EventListener<>(MyEvent.class, Priority.HIGH, event -> {
            // 处理事件
        }));
    }

    @Override
    public void onUnload()
    {
        // 清理资源（所有注册的组件会自动注销）
    }
}
```

== 子模块

子模块只需在其父模块的 `onLoad()` 中注册即可，无需手动调用 `load()` 和 `unload()`：

```java
public class CommandModule extends MzModule
{
    public static final CommandModule instance = new CommandModule();

    @Override
    public void onLoad()
    {
        // 注册命令
        this.register(MyCommand.instance);
    }
}

public class ListenerModule extends MzModule
{
    public static final ListenerModule instance = new ListenerModule();

    @Override
    public void onLoad()
    {
        // 注册事件监听器
        this.register(new EventListener<>(MyEvent.class, event -> {
            // 处理事件
        }));
    }
}
```

**重要规则**：
- 主模块：在程序入口点使用 `load()` 和 `unload()` 手动管理
- 子模块：只需在父模块的 `onLoad()` 中通过 `this.register(subModule)` 注册即可
- 父模块卸载时会自动卸载所有子模块

= 自动注销机制

模块系统会自动管理组件的生命周期：

- 注册时记录所有组件
- 卸载时按照注册相反的顺序自动注销所有内容
- 包括子模块（子模块的注销即卸载）
- 无需手动管理注销逻辑

```java
public class MyModule extends MzModule
{
    public static final MyModule instance = new MyModule();

    @Override
    public void onLoad()
    {
        // 注册多个组件
        this.register(component1);
        this.register(component2);
        this.register(component3);
    }

    @Override
    public void onUnload()
    {
        // 无需手动注销，系统会自动按相反顺序注销：component3, component2, component1
    }
}
```

= 注册器系统

注册器是模块中的重要概念，用于管理特定类型对象的注册和注销。

== 注册器接口

```java
public interface IRegistrar<T>
{
    // 获取支持的类型
    Class<T> getType();

    // 判断对象是否可注册
    default boolean isRegistrable(T object)
    {
        return true;
    }

    // 注册对象
    void register(MzModule module, T object);

    // 注销对象
    void unregister(MzModule module, T object);

    // 获取依赖的注册器
    default Set<IRegistrar<?>> getDependencies()
    {
        return new HashSet<>();
    }
}
```

== 注册器的特性

/ #[= 注册器本身需要注册]:

    注册器本身同样需要注册到模块中，然后其支持的对象才可以被注册。

/ #[= 模块与作用域无关]:

    注册器所在的模块与被注册对象所在模块无需有关联。模块仅决定生命周期，不决定作用域。若想限制作用域，请使用 ClassLoader。

/ #[= 依赖管理]:

    注册器可以声明依赖其他注册器，系统会自动解析依赖关系并进行拓扑排序。

== 自定义注册器

```java
// 定义注册器
public class MyRegistrar implements IRegistrar<MyObject>
{
    public static final MyRegistrar instance = new MyRegistrar();

    @Override
    public Class<MyObject> getType()
    {
        return MyObject.class;
    }

    @Override
    public void register(MzModule module, MyObject object)
    {
        // 注册逻辑
    }

    @Override
    public void unregister(MzModule module, MyObject object)
    {
        // 注销逻辑
    }
}

// 在模块中注册注册器
public class MyModule extends MzModule
{
    public static final MyModule instance = new MyModule();

    @Override
    public void onLoad()
    {
        // 注册注册器
        this.register(MyRegistrar.instance);

        // 注册对象（系统会自动找到支持该对象类型的注册器）
        this.register(new MyObject());
    }
}
```

= Registrable 接口

`Registrable` 是一个简单实现，类只需实现它及其两个方法即可被注册注销而无需专用注册器。

== 接口定义

```java
public interface Registrable
{
    void onRegister(MzModule module);

    void onUnregister(MzModule module);
}
```

== 使用示例

```java
// 实现 Registrable 接口
public class MyComponent implements Registrable
{
    public static final MyComponent instance = new MyComponent();

    @Override
    public void onRegister(MzModule module)
    {
        // 注册时的初始化逻辑
        System.out.println("Registered to module: " + module);
    }

    @Override
    public void onUnregister(MzModule module)
    {
        // 注销时的清理逻辑
        System.out.println("Unregistered from module: " + module);
    }
}

// 在模块中注册
public class MyModule extends MzModule
{
    public static final MyModule instance = new MyModule();

    @Override
    public void onLoad()
    {
        // 直接注册，无需专用注册器
        this.register(MyComponent.instance);
    }
}
```

== 使用场景

`Registrable` 适用于固定的注册注销逻辑。如果需要动态的注册注销逻辑，则需要将注册器本身动态注册到模块中。

**使用 Registrable 的情况**：
- 注册和注销逻辑是固定的
- 不需要根据运行时条件改变注册行为
- 简单的组件生命周期管理

**使用专用注册器的情况**：
- 需要动态的注册注销逻辑
- 需要根据运行时条件改变注册行为
- 需要复杂的注册管理

= 依赖管理

注册器支持依赖管理，自动解析依赖关系并进行拓扑排序。

注册器通过 `getDependencies()` 声明依赖，系统会自动：
- 解析依赖关系
- 检测循环依赖
- 按照依赖顺序注册组件
- 按照相反顺序注销组件

= 最佳实践

/ #[= 使用单例常量]:

    模块通常使用 `static final` 单例常量：

    ```java
    public class MyModule extends MzModule
    {
        public static final MyModule instance = new MyModule();
    }
    ```

/ #[= 在 onLoad 中注册组件]:

    所有组件注册应该在 `onLoad` 中完成：

    ```java
    @Override
    public void onLoad()
    {
        this.register(myCommand);
        this.register(new EventListener<>(MyEvent.class, event -> {}));
        this.register(SubModule.instance);
    }
    ```

/ #[= 在 onUnload 中清理资源]:

    资源清理应该在 `onUnload` 中完成：

    ```java
    @Override
    public void onUnload()
    {
        // 清理资源（组件会自动注销）
        this.myService = null;
    }
    ```

/ #[= 利用自动注销]:

    不要在 `onUnload` 中手动注销组件，系统会自动处理：

    ```java
    @Override
    public void onUnload()
    {
        // ❌ 错误：不要手动注销
        // this.unregister(myCommand);
        
        // ✅ 正确：只清理自己的资源
        this.myService = null;
    }
    ```

/ #[= 选择合适的注册方式]:

    - 固定的注册注销逻辑：使用 `Registrable` 接口

    ```java
    // ✅ 推荐：固定逻辑使用 Registrable
    public class MyComponent implements Registrable { ... }
    ```

    - 动态的注册注销逻辑：将注册器本身动态注册到模块

    ```java
    // ✅ 推荐：动态逻辑使用动态注册的注册器
    public class MyRegistrar implements IRegistrar<MyObject> { ... }
    // 在模块中：this.register(MyRegistrar.instance);
    ```

= 完整示例

```java
import mz.mzlib.module.MzModule;
import mz.mzlib.module.Registrable;
import mz.mzlib.event.EventListener;
import mz.mzlib.event.Event;
import mz.mzlib.Priority;

// 主模块
public class MainModule extends MzModule
{
    public static final MainModule instance = new MainModule();

    @Override
    public void onLoad()
    {
        // 注册子模块
        this.register(CommandModule.instance);
        this.register(ListenerModule.instance);
        this.register(ServiceModule.instance);
    }
}

// 命令模块
public class CommandModule extends MzModule
{
    public static final CommandModule instance = new CommandModule();

    @Override
    public void onLoad()
    {
        // 注册命令（使用 Registrable）
        this.register(MyCommand.instance);
    }
}

// 监听器模块
public class ListenerModule extends MzModule
{
    public static final ListenerModule instance = new ListenerModule();

    @Override
    public void onLoad()
    {
        // 注册事件监听器
        EventListener<MyEvent> listener = new EventListener<>(
            MyEvent.class,
            Priority.HIGH,
            event -> {
                System.out.println("Event handled!");
            }
        );
        this.register(listener);
    }
}

// 服务模块
public class ServiceModule extends MzModule
{
    public static final ServiceModule instance = new ServiceModule();

    @Override
    public void onLoad()
    {
        // 注册服务（使用 Registrable）
        this.register(MyService.instance);
    }
}

// 命令组件
public class MyCommand implements Registrable
{
    public static final MyCommand instance = new MyCommand();

    @Override
    public void onRegister(MzModule module)
    {
        // 注册命令逻辑
    }

    @Override
    public void onUnregister(MzModule module)
    {
        // 注销命令逻辑
    }
}

// 服务组件
public class MyService implements Registrable
{
    public static final MyService instance = new MyService();

    @Override
    public void onRegister(MzModule module)
    {
        // 启动服务
        System.out.println("Service started");
    }

    @Override
    public void onUnregister(MzModule module)
    {
        // 停止服务
        System.out.println("Service stopped");
    }
}
```

#cardInfo[
    模块系统是 MzLib 的核心，理解模块系统对于使用 MzLib 开发非常重要。
    
    模块系统提供了：
    - 统一的组件注册机制
    - 自动依赖管理
    - 生命周期控制
    - 自动注销机制
    - 灵活的注册器系统
]