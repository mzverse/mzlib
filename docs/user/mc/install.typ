#import "/lib/lib.typ": *;
#set raw(lang: "command");
#let title = [安装mzlib-minecraft];
#show: template.with(title: title);

本插件支持多种平台

= 建议的平台

+ mod平台（_Fabric_ / _NeoForge_）并搭配优化mod使用

    推荐的优化mod：
    / #link("https://modrinth.com/mod/lithium")[_Lithium_]: tick优化
    / #link("https://www.curseforge.com/minecraft/mc-mods/c2me")[$italic("C"^2"ME")$]: 多线程区块加载/生成
    / #link("https://modrinth.com/mod/krypton")[_Krypton_]: 网络优化

    #cardTip[
        在mod端建议安装#link("https://luckperms.net/")[LuckPerms]以管理权限
    ]

+ 插件平台（_Bukkit_ ）并搭配各种插件使用

    #cardTip[
        优先使用_Paper_ 端，其已包含了不少的优化
    ]

= 下载

在群内或#link("https://github.com/BugCleanser/MzLib/releases")[Github Release]中下载最新版mzlib-minecraft的jar

= 安装

== 在Fabric

将jar放入mods文件夹，就像安装任何mod一样

== 在NeoForge

将jar放入mods文件夹，就像安装任何mod一样

== 在Bukkit（Paper）

将jar放入plugins文件夹，就像安装任何插件一样

若已安装PlugMan，则可以使用`/plugman load MzLib`而无需重启服务端

#cardAttention[
    当你使用mod混合端时，不应该通过此种方式安装

    而应当作为mod安装
]

== 其它平台

暂不支持其它平台，如果有这方面的需求可以联系我们

== 热加载

Hey，说一个很酷的事：我们支持热加载（或卸载、重载）

使用`/bukkit:reload confirm`或`/plugman reload`等

#cardAttention[
    PlugMan不会自动处理依赖关系，卸载（或重载）一个插件前请确保所有依赖它的插件已被卸载

    并且PlugMan也不会处理版本，如果有同一个插件的两个版本都在plugins文件夹中，它经常会加载到旧的那一个
];

= 测试兼容性

首次安装在您所在的平台或MC版本，可以测试插件的兼容性，安装后使用命令

```command
/mzlib test
```

在控制台执行以进行主要测试，或玩家执行以进行全部测试