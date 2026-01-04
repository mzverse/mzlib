#import "/lib/lib.typ": *;
#let title = [Minecraft];
#show: template.with(title: title);

/ #[= 为什么要使用MzLibMinecraft]:

    MzLibMinecraft是一套跨平台的高效接口，兼容性良好

    基于它的插件也通常是高效的

/ #[= 快速开始]:

    #link("../dev/mc/quick_start")[快速入门] - 5分钟快速上手

/ #[= 安装与配置]:

    #link("./install")[安装MzLibMinecraft] - 安装指南
    #link("./config")[配置说明] - config.js 配置项说明
    #link("./features")[功能说明] - 可用功能介绍

/ #[= 使用指南]:

    #link("./commands")[命令列表] - 可用命令
    #link("./version_compatibility")[版本兼容性] - 版本兼容说明

/ #[= 问题解决]:

    #link("./faq")[常见问题] - FAQ
    #link("./troubleshooting")[故障排除] - 问题排查指南

/ #[= 进阶指南]:

    #link("./migration")[迁移指南] - 从旧版本迁移
    #link("./performance")[性能优化] - 性能建议
    #link("./best_practices")[最佳实践] - 开发建议

/ #[= 推荐的平台]:

    - Paper 拥有Bukkit生态，稳定流畅插件多

    - Fabric 轻量且高效

/ #[= 推荐的Minecraft版本]:

    - 1.8.8 此时还未加入攻击冷却，用来开小游戏服挺不错

    - 1.12.2 经典版本，功能齐全，运行流畅

    - 1.14.4 经典版本，有更完善的功能

    - 1.16.4 经典版本，兼容的mod较多

    - 1.19.4 经典版本

    - 1.20.4 物品组件更新前的最后版本

    - 1.21.5+ 进行了较多优化

    - 最新版本 优化较好，玩家较多

    / #[== 不推荐的版本]:

        - 1.16.5 虽然是1.16的最终修订版，实则内部改动很大，请改用1.16.4

        - 1.17 内部实现尚未稳定，请改用1.17.1

/ #[= 交流与反馈]:

    QQ群：750455476

    如果您是开发者，请参阅#link(meta.root+"dev/index")[开发文档]