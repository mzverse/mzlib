#import "/lib/lib.typ": *;
#let title = [2.创建插件和模块];
#show: template.with(title: title);



让我们创建一个Bukkit插件

在项目资源中添加plugin.yml，并创建一个Bukkit的入口（JavaPlugin子类）

```yaml
name: MzLibDemo
version: 0.1
authors: [ mz ]
main: mz.mzlib.demo.entrypoint.DemoBukkit
depend: [ MzLib ]
api-version: 1.13
```

现在开始对接MzLib

= 创建主模块

```java
public class Demo extends MzModule
{
    public static String MOD_ID = "mzlibdemo";

    public static Demo instance = new Demo();

    @Override
    public void onLoad()
    {
        // 加载子模块和其它对象（如果有的话）
        this.register(DemoSubmodule.instance);
    }
}
```

= 从Bukkit加载主模块

主模块需要被手动加载和卸载，你有两种方法来实现

法一：调用load与unload

```java
public class DemoBukkit extends JavaPlugin
{
    @Override
    public void onEnable()
    {
        Demo.instance.load();
    }

    @Override
    public void onDisable()
    {
        Demo.instance.unload();
    }
}
```

法二：将其注册到MzLib并手动将其注销

在这种情况下，MzLib作为它的父模块，MzLib卸载时它也会被一起卸载

```java
public class DemoBukkit extends JavaPlugin
{
    @Override
    public void onEnable()
    {
        MzLib.instance.register(Demo.instance);
    }

    @Override
    public void onDisable()
    {
        MzLib.instance.unregister(Demo.instance);
    }
}
```