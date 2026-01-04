#import "/lib/lib.typ": *;
#let title = [功能说明];
#show: template.with(title: title);

= 功能说明

MzLib 提供了许多实用的功能来增强 Minecraft 服务器体验。

= 附魔显示优化

启用后，附魔等级将以数字形式显示，而不是乱码。

== 效果

- 附魔等级显示为罗马数字或阿拉伯数字
- 支持多语言
- 不影响原版功能

== 配置

在 `config.js` 中设置：

```javascript
{
    "features": {
        "enchantDisplay": true
    }
}
```

= 掉落物名称显示

启用后，掉落的物品将显示自定义名称。

== 效果

- 掉落物上方显示物品名称
- 支持多语言
- 实时更新

== 配置

```javascript
{
    "features": {
        "dropItemDisplay": true
    }
}
```

= 手持物品展示

启用后，聊天消息中的 `%i` 将被替换为手持物品信息。

== 效果

- 聊天消息中显示手持物品
- 支持物品名称和图标
- 可自定义显示格式

== 配置

```javascript
{
    "features": {
        "heldItemDisplay": true
    }
}
```

== 使用

在聊天消息中使用 `%i`：

```
/mztest 我的手中拿着 %i
```

= 多语言支持

MzLib 自动检测玩家客户端语言并显示相应语言的文本。

== 支持的语言

- 中文（简体）
- 英文
- 其他语言（需添加翻译文件）

== 添加翻译

在 `lang/` 目录下创建语言文件：

```json
{
    "myplugin.message": "我的消息"
}
```

= 命令系统

MzLib 提供了强大的命令系统。

== 可用命令

详见 #link("commands")[命令列表]

== 自定义命令

开发者可以使用 MzLib 的命令 API 创建自定义命令。

= 事件系统

MzLib 提供了丰富的事件系统供开发者使用。

== 常用事件

- 玩家加入/退出
- 玩家聊天
- 玩家移动
- 玩家使用物品
- 窗口操作
- 实体受伤

详见开发文档中的 #link("../dev/mc/event")[事件系统]。

= 数据修复

MzLib 自动处理版本间的数据格式转换。

== 支持的版本

- 1.12.2 到最新版本
- 自动升级 ItemStack 数据
- 自动升级 NBT 数据

= 性能优化

MzLib 经过优化，对服务器性能影响很小。

== 优化措施

- 异步处理耗时操作
- 使用缓存减少重复计算
- 优化事件处理
- 减少不必要的对象创建

详见 #link("performance")[性能优化]。

= 跨平台支持

MzLib 支持多个 Minecraft 平台。

== 支持的平台

- Bukkit/Spigot/Paper
- Fabric
- NeoForge

== 平台特性

每个平台都有特定的特性和限制，详见各平台的安装说明。