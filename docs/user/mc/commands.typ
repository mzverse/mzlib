#import "/lib/lib.typ": *;
#let title = [命令];
#show: template.with(title: title);


MzLib提供了一系列实用的命令，所有命令都以`/mzlib`或`/mz`开头

= 基本命令

== test

测试MzLib的兼容性和功能

*用法*：`/mzlib test [level]`

*参数*：
- `level`（可选）：测试级别，可选值为`0`、`1`、`2`
  - `0`：快速测试（仅主要功能）
  - `1`：标准测试
  - `2`：完整测试（所有功能）

*示例*：
```command
/mzlib test
/mzlib test 1
```

*说明*：
- 玩家执行时进行完整测试
- 控制台执行时进行主要测试
- 测试完成后会显示测试结果

== lang

管理MzLib的语言设置和自定义翻译

*用法*：`/mzlib lang <子命令>`

*子命令*：

=== loadmc

重新加载Minecraft原版语言文件

*用法*：`/mzlib lang loadmc`

*说明*：从服务端重新加载所有原版语言文件

=== custom

管理自定义语言翻译

*用法*：`/mzlib lang custom <language> [key] [operate] [value]`

*参数*：
- `language`：语言代码（如`zh_cn`、`en_us`）
- `key`（可选）：翻译键名
- `operate`（可选）：操作类型，`set`或`remove`
- `value`（可选）：翻译值（仅`set`操作需要）

*示例*：
```command
/mzlib lang custom zh_cn
/mzlib lang custom zh_cn my.plugin.key
/mzlib lang custom zh_cn my.plugin.key set "我的自定义翻译"
/mzlib lang custom zh_cn my.plugin.key remove
```

*说明*：
- 不提供`key`时，打开语言编辑器UI（仅玩家）
- 提供`key`但不提供`operate`时，打开该键的编辑器UI（仅玩家）
- 使用`set`设置翻译值
- 使用`remove`删除翻译

== iteminfo

查看手中物品的详细信息

*用法*：`/mzlib iteminfo`

*说明*：
- 显示玩家手中物品的NBT数据
- 需要玩家权限
- 输出物品的完整NBT结构

*示例*：
```command
/mzlib iteminfo
```

== give

给予玩家MzItem物品

*用法*：`/mzlib give [player] <id> [data]`

*参数*：
- `player`（可选）：目标玩家，不指定则给予自己
- `id`：物品ID（MzItem标识符）
- `data`（可选）：物品的NBT数据

*示例*：
```command
/mzlib give my:item
/mzlib give Steve my:item
/mzlib give my:item {display:{Name:"自定义名称"}}
```

*说明*：
- 需要玩家权限
- 不指定`player`时给予自己
- `data`参数为JSON格式的NBT数据

== js

执行JavaScript代码

*用法*：`/mzlib js <code>`

*参数*：
- `code`：要执行的JavaScript代码

*示例*：
```command
/mzlib js "context.getSource().sendMessage(Text.literal('Hello World'))"
/mzlib js "1 + 1"
```

*说明*：
- 使用Rhino引擎执行JavaScript代码
- 代码中可以使用`context`变量访问命令上下文
- 执行结果会显示在聊天栏
- 警告：此命令具有潜在安全风险，请谨慎使用

= 权限

所有命令都需要相应权限才能使用：

- `mzlib.command.mzlib.test`：使用`test`命令
- `mzlib.command.mzlib.lang`：使用`lang`命令
- `mzlib.command.mzlib.iteminfo`：使用`iteminfo`命令
- `mzlib.command.mzlib.give`：使用`give`命令
- `mzlib.command.mzlib.js`：使用`js`命令

#cardAttention[
  建议使用权限插件（如LuckPerms）来管理这些权限，避免给予普通玩家过高的权限
]

= 提示

- 所有命令都支持Tab补全
- 使用`/mzlib help`查看命令帮助
- 某些命令可能需要玩家权限才能执行
- 建议在生产环境中限制`js`命令的使用权限