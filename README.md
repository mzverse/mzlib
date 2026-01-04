<div align=center>

<img src="https://raw.githubusercontent.com/mzverse/mzlib/main/.github/assets/banner.png"/>

***A Cross-Platform Minecraft Development Library***

![Code-Size](https://img.shields.io/github/languages/code-size/mzverse/mzlib?style=flat-square)
![Release](https://img.shields.io/github/v/release/mzverse/mzlib?style=flat-square)
![Actions](https://img.shields.io/github/actions/workflow/status/mzverse/mzlib/build.yml?style=flat-square)
![Group](https://img.shields.io/badge/group-750455476-yellow?style=flat-square)

</div>

<br>

# 📖 文档

[完整文档](https://mzverse.github.io/mzlib/) | [Wiki](https://github.com/mzverse/mzlib/wiki)

---

# 📦 依赖

确保环境变量中有 `GITHUB_USERNAME` 和 `GITHUB_TOKEN`（token 需要 `read:packages` 权限）。

[创建 Token](https://github.com/settings/tokens/new)

```kts
repositories {
    maven("https://maven.pkg.github.com/mzverse/mzlib") {
        credentials {
            username = System.getenv("GITHUB_USERNAME")
            password = System.getenv("GITHUB_TOKEN")
        }
    }
}

dependencies {
    compileOnly("org.mzverse:mzlib-minecraft:latest.integration")
}
```

仅依赖核心模块：

```kts
dependencies {
    compileOnly("org.mzverse:mzlib-core:latest.integration")
}
```

---

# 🌟 简介

MzLib 是一个跨平台的 Minecraft 开发类库，支持 Bukkit、Fabric、NeoForge。

## 特性

- **跨平台** - 统一 API，支持 Bukkit/Spigot/Paper、Fabric、NeoForge
- **版本兼容** - 支持 1.12 到最新版本，自动适配
- **丰富 API** - 命令、物品、NBT、文本、数据包、库存、配方、权限等
- **Wrapper 系统** - 优雅的原版类包装，无需反射
- **多语言** - 自动获取玩家客户端语言设置
- **数据修复** - 自动处理版本间数据格式变化

---

# 🚀 安装

## Bukkit/Spigot/Paper

1. 下载插件：[Releases](https://github.com/mzverse/mzlib/releases)
2. 放入 `plugins` 文件夹
3. 重启服务器

Docker 环境如需 MzLibAgent，将 `MzLibAgent.jar` 放入服务端根目录，启动参数添加 `-javaagent:MzLibAgent.jar`

## Fabric/NeoForge

将对应平台的模块放入 `mods` 文件夹

---

# 🔨 构建

```bash
./gradlew shadowJar
```

产物位于 `out` 文件夹。

---

# 🎮 附属插件

- [LoginAUI](https://www.mcbbs.net/thread-1324546-1-1.html) - 铁砧登录页面
- [MzBackwards](https://www.mcbbs.net/thread-1369629-1-1.html) - 回跨版本显示优化
- [MzItemStack](https://www.mcbbs.net/thread-1370314-1-1.html) - 自定义物品堆叠

---

# 💖 支持

- [Mcbbs](https://www.mcbbs.net/thread-1250793-1-1.html) | [Issues](https://github.com/mzverse/mzlib/issues)

![Plzzz](.github/assets/Plzzz.png)
![Pay](.github/assets/MzLibWePay.png)

---

# 📄 许可证

[Mozilla Public License Version 2.0](https://www.mozilla.org/en-US/MPL/)

---

# 🙏 鸣谢

- [ASM](https://gitlab.ow2.org/asm/asm)
- [Gson](https://github.com/google/gson)
- [FastUtil](https://fastutil.di.unimi.it/)

---

<div align=center>

Made with ❤️ by [MzVerse Team](https://github.com/mzverse/mzlib/graphs/contributors)

</div>