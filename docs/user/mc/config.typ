#import "/lib/lib.typ": *;
#let title = [配置说明];
#show: template.with(title: title);

MzLib 的配置文件为 `config.js`，位于插件数据目录。

= 配置文件位置

- Bukkit: `plugins/MzLib/config.js`
- Fabric: `config/mzlib/config.js`
- NeoForge: `config/mzlib/config.js`

= 配置项

```javascript
{
    // 调试模式
    "debug": false,

    // 语言设置
    "language": "zh_cn",

    // 启用的功能
    "features": {
        // 附魔显示优化
        "enchantDisplay": true,

        // 掉落物名称显示
        "dropItemDisplay": true,

        // 手持物品展示
        "heldItemDisplay": true
    }
}
```

= 功能说明

== debug

- 类型: boolean
- 默认值: false
- 说明: 启用调试模式，输出更多日志信息

== language

- 类型: string
- 默认值: "zh_cn"
- 说明: 默认语言，支持的语言代码

== features.enchantDisplay

- 类型: boolean
- 默认值: true
- 说明: 启用附魔显示优化，将乱码替换为数字

== features.dropItemDisplay

- 类型: boolean
- 默认值: true
- 说明: 启用掉落物名称显示

== features.heldItemDisplay

- 类型: boolean
- 默认值: true
- 说明: 启用聊天消息中手持物品的展示

= 注意事项

- 修改配置后需要重启服务器或重载插件
- 配置文件使用 JavaScript 格式
- 确保语法正确