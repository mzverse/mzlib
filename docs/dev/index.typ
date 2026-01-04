#import "/lib/lib.typ": *;
#set raw(lang: "java");
#let title = [开发文档];
#show: template.with(title: title);



这里是MzLib的开发文档

/ #[= 模块介绍]:

    MzLib 是一个充满魔法的 Minecraft 插件，主要包含以下三个模块：

/ #[== MzLibCore]:

    - *描述*：MzLibCore 是 MzLib 的根基，所有与 Minecraft 无关的功能都在这里。
    - *功能*：主要是一些 Java 工具类和实用方法。

/ #[== MzLibMinecraft]:

    - *描述*：MzLibMinecraft 包含 Minecraft 插件的主要代码，依赖于 MzLibCore。
    - *功能*：提供 Minecraft 相关的功能和工具。

/ #[== MzLibDemo]:

    - *描述*：这里是一些示例代码，帮助开发者快速上手。
    - *功能*：演示如何使用 MzLib 的各个功能。
