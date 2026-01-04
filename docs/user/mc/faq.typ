#import "/lib/lib.typ": *;
#let title = [常见问题];
#show: template.with(title: title);

= 常见问题

== Q: 插件无法加载？

A: 检查以下几点：
1. 确认服务器版本符合要求
2. 检查是否有依赖冲突
3. 查看服务器日志获取详细错误信息

== Q: 如何更新插件？

A:
1. 停止服务器
2. 备份插件数据
3. 替换插件 JAR 文件
4. 启动服务器

== Q: 支持哪些 Minecraft 版本？

A:
- Bukkit/Spigot: 1.12.2 及以上
- Fabric: 1.14.4 及以上
- NeoForge: 1.20.1 及以上

== Q: 如何获取帮助？

A:
- 加入 QQ 群: 750455476
- 提交 Issue: https://github.com/mzverse/mzlib/issues
- 查看文档: https://mzverse.github.io/mzlib/

== Q: 如何贡献代码？

A: 详见 #link("https://github.com/mzverse/mzlib/blob/main/CONTRIBUTING.md")[CONTRIBUTING.md]。

== Q: 插件性能如何？

A: MzLib 经过优化，对服务器性能影响很小。如果遇到性能问题，请：
1. 检查服务器配置
2. 查看日志中的警告信息
3. 在 Issue 中报告问题

== Q: 如何调试插件？

A: 在配置文件中设置 `debug: true`，查看详细日志。

== Q: 兼容其他插件吗？

A: MzLib 设计为与其他插件兼容。如果遇到冲突，请在 Issue 中报告。

== Q: 如何配置插件？

A: 详见 #link("./config")[配置说明]。

== Q: 遇到问题如何排查？

A: 详见 #link("./troubleshooting")[故障排除]。

== Q: 如何从旧版本迁移？

A: 详见 #link("./migration")[迁移指南]。

== Q: 如何优化性能？

A: 详见 #link("./performance")[性能优化]。