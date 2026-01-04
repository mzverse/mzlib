#import "/lib/lib.typ": *;
#let title = [数据修复];
#show: template.with(title: title);

数据修复系统提供了版本间数据格式的自动转换功能。

= DataFixerV1300

`DataFixerV1300` 是数据修复器（1.13+）。

```java
// 获取数据修复器
DataFixerV1300 fixer = MinecraftServer.instance.getDataUpdaterV1300();

// 修复数据
DynamicV1300 fixed = fixer.update(
    DataUpdateTypesV1300.itemStack(),
    dynamic,
    fromVersion,
    toVersion
);
```

= DataUpdaterV900_1300

`DataUpdaterV900_1300` 是数据更新器（1.9-1.13）。

```java
// 获取数据更新器
DataUpdaterV900_1300 updater = MinecraftServer.instance.getDataUpdaterV900_1300();

// 更新数据
NbtCompound updated = updater.update(
    DataUpdateTypesV900_1300.itemStack(),
    nbt,
    fromVersion
);
```

= DataUpdateTypesV1300

`DataUpdateTypesV1300` 定义了数据更新类型（1.13+）。

```java
// ItemStack 类型
DataUpdateTypeV1300 itemType = DataUpdateTypesV1300.itemStack();

// 其他类型
// ...
```

= DataUpdateTypesV900_1300

`DataUpdateTypesV900_1300` 定义了数据更新类型（1.9-1.13）。

```java
// ItemStack 类型
DataUpdateTypeV900_1300 itemType = DataUpdateTypesV900_1300.itemStack();

// 其他类型
// ...
```

= 使用示例

```java
public class DataFixerExample
{
    // 升级 ItemStack 数据
    public ItemStack upgradeItemStack(NbtCompound nbt, int fromVersion)
    {
        int currentVersion = MinecraftServer.instance.getDataVersion();

        if (fromVersion < 1300)
        {
            // 1.13 之前
            DataUpdaterV900_1300 updater = MinecraftServer.instance.getDataUpdaterV900_1300();
            nbt = updater.update(DataUpdateTypesV900_1300.itemStack(), nbt, fromVersion);
        }

        if (fromVersion >= 1300 && fromVersion < currentVersion)
        {
            // 1.13 及之后
            DataFixerV1300 fixer = MinecraftServer.instance.getDataUpdaterV1300();
            DynamicV1300 dynamic = DynamicV1300.newInstance(NbtOpsV1300.instance(), nbt);
            DynamicV1300 fixed = fixer.update(
                DataUpdateTypesV1300.itemStack(),
                dynamic,
                fromVersion,
                currentVersion
            );
            nbt = (NbtCompound) fixed.getValue();
        }

        return ItemStack.decode(nbt).getValue().unwrapOr(ItemStack.EMPTY);
    }
}
```

= 注意事项

- 数据修复会自动处理版本间的格式变化
- 使用 ItemStack 内置的 `upgrade` 方法更简单
- 修复可能改变数据结构