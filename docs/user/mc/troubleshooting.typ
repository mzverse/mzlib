#import "/lib/lib.typ": *;
#let title = [故障排除];
#show: template.with(title: title);


本指南帮助你解决使用 MzLib 时可能遇到的常见问题。

= 常见错误

== 插件无法加载

=== 症状

- 服务器启动时报错
- 插件未加载
- 控制台显示加载失败

=== 可能原因

1. 服务器版本不支持
2. 依赖缺失
3. 配置文件错误
4. Java 版本不兼容
5. 文件损坏

=== 解决方案

1. 检查服务器版本是否在支持范围内
2. 确认所有依赖已正确安装
3. 检查 `config.js` 语法是否正确
4. 确认 Java 版本（需要 Java 8+）
5. 重新下载插件文件

#cardAttention[
    如果是 Bukkit 平台，确保 `plugin.yml` 配置正确。
    
    如果是 Fabric/NeoForge，确保 `fabric.mod.json` 或 `mods.toml` 配置正确。
]

== NoClassDefFoundError

=== 症状

```
java.lang.NoClassDefFoundError: mz/mzlib/...
```

=== 可能原因

- MzLib 未正确加载
- 依赖版本不匹配
- 类路径问题

=== 解决方案

1. 确认 MzLib 已正确安装
2. 检查依赖版本是否匹配
3. 重启服务器
4. 清理缓存并重新加载

== NullPointerException

=== 症状

```
java.lang.NullPointerException
```

=== 可能原因

- 对象未初始化
- API 使用不当
- 版本兼容性问题

=== 解决方案

1. 检查代码中是否正确初始化对象
2. 使用 `Option` 类型处理可能为空的对象
3. 查看堆栈跟踪定位问题
4. 检查版本兼容性

== ClassCastException

=== 症状

```
java.lang.ClassCastException: class A cannot be cast to class B
```

=== 可能原因

- 类型转换错误
- Wrapper 使用不当
- 版本差异

=== 解决方案

1. 检查类型转换是否正确
2. 使用 `as()` 方法进行类型安全的 Wrapper 转换
3. 检查版本兼容性
4. 使用 `is()` 方法检查类型

== 性能问题

=== 症状

- 服务器 TPS 下降
- 玩家卡顿
- 内存占用过高

=== 可能原因

- 大量掉落物
- 配置过于频繁的更新
- 插件冲突
- 事件监听器过多
- 数据包监听器过多

=== 解决方案

1. 清理掉落物
2. 调整配置更新频率
3. 检查插件冲突
4. 优化事件监听器
5. 减少数据包监听器
6. 使用性能分析工具

#cardInfo[
    使用 `/mzlib test` 命令测试插件性能。
    
    使用 `/mzlib reload` 命令重新加载配置。
]

== 数据丢失

=== 症状

- 物品数据丢失
- 玩家数据重置
- 配置丢失

=== 可能原因

- 数据文件损坏
- 版本升级问题
- 权限问题
- 磁盘空间不足

=== 解决方案

1. 恢复备份
2. 检查文件权限
3. 查看日志中的错误信息
4. 使用数据修复功能
5. 检查磁盘空间

== 版本兼容性问题

=== 症状

- API 调用失败
- 方法不存在
- 字段不存在

=== 可能原因

- Minecraft 版本不兼容
- MzLib 版本过旧
- 版本注解使用不当

=== 解决方案

1. 检查 Minecraft 版本是否支持
2. 更新 MzLib 到最新版本
3. 使用版本注解确保兼容性
4. 查看版本更新日志

== 网络数据包问题

=== 症状

- 数据包无法发送
- 数据包监听器不工作
- 数据包修改无效

=== 可能原因

- 数据包类型错误
- 监听器未正确注册
- 数据包被取消
- 版本差异

=== 解决方案

1. 检查数据包类型是否正确
2. 确认监听器已正确注册
3. 检查数据包是否被取消
4. 使用 `ensureCopied()` 确保数据包是副本

= 调试

== 启用调试模式

在 `config.js` 中设置：

```javascript
{
    "debug": true
}
```

这将输出更多日志信息。

== 查看日志

日志文件位置：

- Bukkit: `logs/latest.log`
- Fabric: `logs/latest.log`
- NeoForge: `logs/latest.log`

== 使用测试命令

使用 `/mzlib test` 测试插件功能。

== 使用性能分析

使用性能分析工具：

- Spark - 性能分析插件
- Timings - Bukkit 性能分析
- VisualVM - Java 性能分析

= 获取帮助

== 查看文档

- #link("faq")[常见问题]
- #link("migration")[迁移指南]
- #link("performance")[性能优化]
- #link("best_practices")[最佳实践]

== 提交 Issue

如果问题无法解决，请提交 Issue：

https://github.com/mzverse/mzlib/issues

提交时请包含：

1. 服务器版本
2. MzLib 版本
3. 错误日志
4. 复现步骤
5. 配置文件（敏感信息删除）
6. 使用的其他插件

== 联系支持

- QQ 群: 750455476
- GitHub Issues: https://github.com/mzverse/mzlib/issues
- GitHub Discussions: https://github.com/mzverse/mzlib/discussions

= 预防措施

== 定期备份

定期备份以下内容：

- 插件数据目录
- 配置文件
- 玩家数据
- 世界数据

== 更新前测试

在更新到新版本前：

1. 在测试服务器测试
2. 备份所有数据
3. 查看更新日志
4. 阅读迁移指南
5. 检查版本兼容性

== 监控性能

定期检查：

- TPS（每秒刻数）
- 内存使用
- CPU 使用
- 磁盘 I/O
- 网络延迟

== 保持更新

定期更新：

- MzLib 到最新版本
- 服务器到推荐版本
- Java 到推荐版本
- 依赖库到最新版本

== 代码审查

开发时：

1. 使用类型检查
2. 处理异常
3. 使用 `Option` 和 `Result`
4. 编写测试
5. 代码审查

= 常见问题（FAQ）

== MzLib 支持哪些平台？

MzLib 支持：
- Fabric (1.14+)
- NeoForge (1.20.1+)
- Bukkit/Spigot (1.7.10+)
- Paper (1.7.10+)
- Folia (支持多线程)

== MzLib 需要什么 Java 版本？

MzLib 需要 Java 8 或更高版本。推荐使用 Java 17 或 Java 21。

== 如何更新 MzLib？

1. 停止服务器
2. 备份数据
3. 下载新版本
4. 替换旧文件
5. 启动服务器

== MzLib 会影响性能吗？

MzLib 经过优化，对性能影响很小。但如果使用大量功能，可能会有一定影响。

== 如何禁用某个功能？

在 `config.js` 中配置：

```javascript
{
    "features": {
        "feature_name": false
    }
}
```

== 如何迁移到新版本？

查看 #link("migration")[迁移指南] 了解详细信息。

= 已知问题

== Folia 平台限制

Folia 是多线程平台，某些功能可能不可用或需要特殊处理。

== 版本兼容性

某些功能只在特定版本可用，使用前请检查版本兼容性。

== 插件冲突

某些插件可能与 MzLib 冲突，使用前请测试。