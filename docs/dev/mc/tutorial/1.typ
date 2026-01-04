#import "/lib/lib.typ": *;
#let title = [1.基本结构与约定];
#show: template.with(title: title);



我们的设计哲学与Fabric类似，但我们要支持热加卸载和多版本兼容

这几乎依赖于我们的模块化和包装器

= 模块化

你的插件应该由若干个模块构成，其它东西几乎都应该被注册到模块中，模块卸载时会自动将它们注销

注册是一个耗时操作，一般只在模块加载时注册其它对象

在下一章你将学会创建和加载模块

= 包装器

包装器是指WrapperObject的子类，是对Minecraft类的基本封装

相比被它包装的类，它是一个interface，因此它没有JVM概念的构造器，取而代之的是静态方法newInstance

例如你要新建一个ItemStack实例

```java
ItemStack is = ItemStack.newInstance(Identifier.newInstance("minecraft:diamond"));
```

若要取得被包装的Minecraft的对象，使用getWrapped方法

```java
Object nms = is.getWrapped();
```

不过你一般用不到这样，只应使用包装器来操作这些Minecraft对象。

若你通过某种方法得到了Minecraft的对象，通过它创建一个包装器实例，使用包装器的create方法

```java
Entity entity = Entity.create(nmsEntity);
```

值得注意的是，wrapper不应该使用JVM概念的instanceof和强制转换，因为Wrapper.create返回的永远只是Wrapper的实例

取而代之的是wrapper.isInstanceOf和wrapper.castTo，参数是另一个包装器的create方法引用，例如

```java
if(entity.isInstanceOf(EntityPlayer::create))
{
    EntityPlayer player = entity.castTo(EntityPlayer::create);
    // do something
}
```

有关如何创建一个包装器类型，见#link("./../../core/wrapper")[包装器的进阶教程]

= 版本表示和名称约定

为了简单起见，我们使用一个整数表示一个MC版本

如"1.12"表示为`1200`，"1.14"表示为`1400`，"1.21"表示为`2100`，"1.21.5"表示为`2105`，"1.21.11"表示为`2111`

版本段使用左闭右开区间表示，若一个类或成员仅支持特定版本段，在标识符后加上"v"和版本段。例如`exampleV1300_1400`支持版本$[1.13, 1.14)$，`exampleV1300`支持版本$[1.13, +infinity)$，`exampleV_1400`支持版本$[0, 1.14)$

详见#link("./index#版本表示和名称约定")[版本表示和名称约定]