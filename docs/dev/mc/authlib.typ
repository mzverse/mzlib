#import "/lib/lib.typ": *;
#let title = [Authlib];
#show: template.with(title: title);

Authlib 提供了玩家认证相关的功能。

= GameProfile

`GameProfile` 表示玩家配置文件。

```java
// 创建配置文件
GameProfile profile = new GameProfile(uuid, "Steve");

// 获取 UUID
UUID uuid = profile.getId();

// 获取名称
String name = profile.getName();

// 获取属性
PropertyMap properties = profile.getProperties();
```

= Property

`Property` 表示玩家属性。

```java
// 创建属性
Property property = new Property("textures", value, signature);

// 获取名称
String name = property.name();

// 获取值
String value = property.value();

// 获取签名
String signature = property.signature();
```

= PropertyMap

`PropertyMap` 是属性映射。

```java
// 创建属性映射
PropertyMap properties = new PropertyMap();

// 添加属性
properties.put("textures", property);

// 获取属性
Collection<Property> textures = properties.get("textures");

// 检查属性是否存在
boolean has = properties.containsKey("textures");
```

= GameProfile.Description

`GameProfile.Description` 是配置文件描述。

```java
// 创建纹理描述
GameProfile.Description description = GameProfile.Description.texturesUrl(
    Option.none(),  // name
    Option.some(uuid),  // uuid
    texture  // texture string
);

// 创建完整描述
GameProfile.Description description = GameProfile.Description.textures(
    Option.some(name),
    Option.some(uuid),
    texture
);
```

= 注意事项

- 用于玩家皮肤和披风
- 纹理需要从 Minecraft 获取
- 签名用于验证纹理的有效性