#import "/lib/lib.typ": *;
#set raw(lang: "java");
#let title = `PlayerHead`;
#show: template.with(title: title);



= 构造物品

通过`ItemStack.builder().playerHead().texturesUrl(url).build()`或其重载方法，其中`url`是贴图的链接，
一般在http://textures.minecraft.net下

= 皮肤数据

玩家头颅的皮肤通过玩家档案描述`GameProfile.Description`储存

= 玩家档案描述

玩家档案描述表示玩家档案`GameProfile`的部分或全部，包括可选的`id`、`name`、属性表

在低版本（具体待考证）中，至少需要包含`id`和`name`中的一个

= 内部细节

 - 1.20.5前，玩家档案描述直接由玩家档案`GameProfile`表示
   - 1.20.2前，`GameProfile`的`name`和`id`可空，直接由`null`表示空
   - 1.20.2起，`GameProfile`的`name`由空字符串表示空，`id`由全0表示空
 - 1.20.5起，由`GameProfileComponentV2005`表示
   - 1.21.9起，细化了这个类
