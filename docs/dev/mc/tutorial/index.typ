#import "/lib/lib.typ": *

#let title = [快速开始(Minecraft)]

#show: template.with(title: title)



*开始之前，您需要先学习#link("./../../core/tutorial/0")[MzLibCore的基本用法]*

*建议*：
    - 掌握一定的插件或mod的开发基础
    - 了解BukkitAPI

MzLib现在正处于早期开发阶段，你可能需要配合BukkitAPI来完成功能，见#link("./../bukkit")[配合Bukkit使用]

= 教程目录

本教程将引导你从零开始使用MzLib开发Minecraft插件

/ #[== 1.基本结构与约定]:

    了解MzLib的模块化设计、包装器系统和版本表示约定

    #link("./1")[开始学习]

/ #[== 2.创建插件和模块]:

    学习如何创建Bukkit插件并对接MzLib的主模块

    #link("./2")[开始学习]

/ #[== 3.创建简单命令]:

    学习如何使用MzLib的命令系统创建和管理命令

    #link("./3")[开始学习]

/ #[== 4.监听事件]:

    学习如何监听和处理Minecraft事件

    #link("./4")[开始学习]

/ #[== 5.配置文件]:

    学习如何使用JSON配置文件管理插件设置

    #link("./5")[开始学习]

= 学习路径

建议按照以下顺序学习：

1. 首先学习#link("./../../core/tutorial/0")[MzLibCore的基本用法]
2. 然后依次完成本教程的5个章节
3. 最后参考#link("./../index")[开发文档]深入了解各个API

= 进阶资源

完成基础教程后，你可以继续学习：

- #link("./../command")[命令系统进阶]
- #link("./../text")[文本组件]
- #link("./../nbt")[NBT操作]
- #link("./../window")[窗口系统]
- #link("./../network_packet")[网络数据包]