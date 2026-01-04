#import "/lib/lib.typ": *;
#let title = [1.Hello World];
#show: template.with(title: title);



MzLib的架构是模块化的，一个程序至少需要一个主模块

模块一般是单例

```java
public class Main extends MzModule
{
    public static Main instance = new Main();

    @Override
    public void onLoad()
    {
        System.out.println("Hello World!");
    }
}
```

无论程序以哪种方式引导，你都需要在启动（启用）时加载主模块，结束（禁用）时卸载主模块。

例如作为JavaApplication

```java
public class MyApplication
{
    public static void main(String[] args)
    {
        Main.instance.load();
        // do something
        Main.instance.unload();
    }
}
```

模块加载时，`onLoad`方法会被调用，成功打印出HelloWorld

为了使用MzLib的基本工具，请确保MzLib的模块已经加载

+ 作为MzLib的附属插件加载
+ 或者将MzLib shade到你的程序中，然后手动load它

手动load MzLib（不推荐）：

```java
public class MyApplication
{
    public static void main(String[] args)
    {
        MzLib.instance.load();
        Main.instance.load();
        // do something
        Main.instance.unload();
        MzLib.instance.unload();
    }
}
```