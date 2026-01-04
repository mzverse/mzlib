#import "/lib/lib.typ": *;
#let title = [3.创建简单命令];
#show: template.with(title: title);



这是MzLib命令系统的入门教程

一个命令应该是```java mz.mzlib.minecraft.command.Command```的实例

它的基本设置方法都会返回自身，因此我们可以用链式构造它，但下面的教程中我们会分步创建

= 创建Command实例

直接调用构造器，第一个参数是命令的名称，后面的参数是它的别名（可选）

```java
Command command = new Command("mzlibdemo", "mzd");
```

= 设置命名空间

命令可以以两种名称调用以防重名，/name 和 /namespace:name

其中name是你的命令名，namespace是命令的命名空间，默认是minecraft，一般设置成你的插件名(MOD_ID)

```java
command.setNamespace(Demo.MOD_ID);
```

= 设置命令处理器

你当然需要规定如何处理你的命令，使用`setHandler`

```java
command.setHandler(context->
{
    if(!context.successful)
        return;
    if(context.doExecute)
    {
        // do sth. on execute
        context.source.sendMessage(Text.literal("Hello World!"));
    }
});
```

其中`context`是一个`CommandContext`的实例

我们先判断`context.successful`，它代表命令是否被成功解析

然后我们判断`context.doExecute`，它表示命令是否应该被执行（否则只是需要补全命令）

示例中我们发送一条Hello World给命令发送者

= 注册命令

一般地，我们在一个模块中注册这个命令，不需要手动注销

```java
public class Demo extends MzModule
{
    public static Demo instance = new Demo();

    public Command command;

    @Override
    public void onLoad()
    {
        this.register(this.command=new Command("demo", "d").setHandler(context->{/* ... */}));
    }
}
```

= 添加子命令

使用```java Command#addChild```添加子命令，你可以在父命令创建时直接添加

如果子命令足够复杂，你也可以单独为它创建一个模块，并在`onLoad`中添加到父命令，并在`onUnload`中从父命令移除

```java
public class DemoSubcommand extends MzModule
{
    public static DemoSubcommand instance = new DemoSubcommand();

    public Command command;

    @Override
    public void onLoad()
    {
        Demo.instance.command.addChild(this.command=new Command("sub").setHandler(context->{/* ... */}));
    }

    @Override
    public void onUnload()
    {
        Demo.instance.command.removeChild(this.command);
    }
}
```

然后在父命令注册后注册这个模块

= 设置命令权限检查器

使用`setPermissionChecker`方法设置权限检查器

检查命令源的权限，如果权限不足，返回一个Text类型的提示，否则返回null

可以使用预设的静态方法```java Command#checkPermission```，也可以使用```java Command#permissionChecker```直接构造这个检查器

记得注册你的权限到模块中

```java
public class Demo extends MzModule
{
    public static Demo instance = new Demo();

    public String MOD_ID="mzlibdemo";

    public Permission permission=new Permission(this.MOD_ID+".command.demo");
    public Command command;

    @Override
    public void onLoad()
    {
        // 注册权限
        this.register(this.permission);
        // 注册命令
        this.register(this.command=new Command("demo").setNamespace(this.MOD_ID).setPermissionChecker(Command.permissionChecker(this.permission)));
    }
}
```

可以使用静态方法```java Command#checkPermissionSenderPlayer```要求发送者必须是一个玩家

可以使用`setPermissionCheckers`方法设置若干个权限检查器，通过全部检查器的发送者才能执行命令

```java
command.setPermissionCheckers(Command::checkPermissionSenderPlayer, Command.permissionChecker(this.permission));
```

= 进阶

命令系统的进阶用法参见#link("./../command")[进阶教程]
