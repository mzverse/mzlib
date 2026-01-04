#import "/lib/lib.typ": *;
#let title = [权限系统];
#show: template.with(title: title);

权限系统提供了权限检查和管理功能。

= Permission

`Permission` 表示一个权限。

```java
// 创建权限
Permission permission = new Permission("mzlib.command.example");

// 获取权限 ID
String id = permission.getId();

// 检查权限是否默认授予
boolean defaultGranted = permission.isDefaultGranted();
```

= PermissionHelp

`PermissionHelp` 提供权限检查的帮助方法。

```java
// 获取权限帮助实例
PermissionHelp helper = PermissionHelp.instance;

// 检查权限
boolean hasPermission = helper.check(source, permission);

// 检查权限等级
boolean hasLevel = helper.checkLevel(source, 4);
```

= EventCheckPermission

`EventCheckPermission` 是权限检查事件。

```java
@RegistrarEventListener
public class PermissionListener
{
    @RegistrarEventClass
    public static void onCheckPermission(EventCheckPermission event)
    {
        // 检查权限
        boolean granted = event.granted;

        // 修改权限检查结果
        event.granted = true;

        // 取消权限检查
        event.cancel();
    }
}
```

= 使用示例

```java
public class PermissionExample
{
    // 定义权限
    public static final Permission ADMIN = new Permission("mzlib.admin");
    public static final Permission MODERATOR = new Permission("mzlib.moderator");

    // 检查玩家权限
    public boolean hasPermission(EntityPlayer player, Permission permission)
    {
        CommandSource source = CommandSource.of(player);
        return PermissionHelp.instance.check(source, permission);
    }

    // 检查命令权限
    public Text checkCommandPermission(CommandSource source)
    {
        if (!source.getPlayer().isSome())
        {
            return Text.literal("Only players can use this command");
        }

        if (!PermissionHelp.instance.check(source, ADMIN))
        {
            return Text.literal("You don't have permission");
        }

        return null;
    }

    // 设置命令权限检查器
    public void setupCommand(Command command)
    {
        command.setPermissionCheckers(
            source -> Command.checkPermissionSenderPlayer(source),
            source -> Command.checkPermission(source, ADMIN)
        );
    }
}
```

= 注意事项

- 权限检查应该在命令执行前进行
- 使用 `PermissionHelp` 进行权限检查
- 可以通过事件修改权限检查结果
- 权限 ID 应该使用小写和下划线