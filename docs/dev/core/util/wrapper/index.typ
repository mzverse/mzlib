#import "/lib/lib.typ": *;
#set raw(lang: "java");
#let title = [包装器];
#show: template.with(title: title);



包装类是 MzLib 中非常常用的对象

因为 MC 的类在不同版本可能有所变化，所以我们必须给它包装一层，包装后你还可以越权访问它的成员

这里介绍包装类的基本用法

= 基本规则

包装类必须遵循以下基本规则：

- 所有包装类必须是 `interface`，不能是 `class`
- 所有包装类必须直接或间接继承 `WrapperObject`
- 每个包装类都应当添加 `FACTORY` 字段作为入口点
- 不要使用静态 `create` 方法（历史遗留，将被移除），直接使用 `FACTORY` 字段

= 包装已知类

如果你能直接访问这个类，可以使用 `@WrapClass`

这里以 ClassLoader 为例

```java
// 包装 ClassLoader
@WrapClass(ClassLoader.class)
// 包装类必须是 interface，并且是 WrapperObject 的子类
public interface WrapperClassLoader extends WrapperObject
{
    /**
     * FACTORY 字段：包装类的入口点
     * 每个包装类都应当添加此字段
     */
    WrapperFactory<WrapperClassLoader> FACTORY = WrapperFactory.of(WrapperClassLoader.class);

    /**
     * findClass 是非 public 方法，因此需要包装
     * @WrapMethod("findClass") 表示目标方法的名称是 findClass
     * 包装方法的返回值类型必须和目标方法的一致，或者是其封装类
     * 没必要写 throws
     */
    @WrapMethod("findClass")
    Class<?> findClass(String name) throws ClassNotFoundException;
}
```

若目标类的类名固定且已知，但代码中无法直接访问，可以使用 `@WrapClassForName` 代替 `@WrapClass`

```java
@WrapClassForName("java.lang.ClassLoader")
public interface WrapperClassLoader extends WrapperObject
{
    WrapperFactory<WrapperClassLoader> FACTORY = WrapperFactory.of(WrapperClassLoader.class);

    // ...
}
```

= 使用包装类

如果有一个目标类的实例，可以使用 FACTORY 将其包装为包装类实例，从而访问其成员

```java
// 目标类实例
ClassLoader cl = this.getClass().getClassLoader();
// 创建包装类实例（使用 FACTORY）
WrapperClassLoader wcl = WrapperClassLoader.FACTORY.create(cl);
// 调用包装方法或访问字段
wcl.findClass("java.lang.String");
```

= 拓展包装类

包装类可以被继承，当你需要包装它目标类的子类，或者你单纯想要拓展包装类的功能

```java
// 要包装的类和 WrapperClassLoader 的相同
@WrapSameClass(WrapperClassLoader.class)
// 继承 WrapperClassLoader，也可以先显式继承 WrapperObject
public interface ExtendedWrapperClassLoader extends WrapperObject, WrapperClassLoader
{
    WrapperFactory<ExtendedWrapperClassLoader> FACTORY = WrapperFactory.of(ExtendedWrapperClassLoader.class);

    // 这时候可以封装更多方法
    @WrapMethod("resolveClass")
    void resolveClass(Class<?> c);
}
```

如果你已有父封装类的实例，你当然可以拿到目标类的实例然后使用拓展封装类重新封装，从而调用拓展封装类中的方法

```java
ClassLoader cl = this.getClass().getClassLoader();
WrapperClassLoader wcl = WrapperClassLoader.FACTORY.create(cl);
// getWrapped 得到被包装的对象，然后使用拓展包装类的 FACTORY 重新封装
ExtendedWrapperClassLoader ewcl = ExtendedWrapperClassLoader.FACTORY.create(wcl.getWrapped());
// 调用拓展封装类中的方法
ewcl.resolveClass(String.class);
```

我们一般简化为 `castTo` 方法，参数是包装类的 FACTORY

```java
ClassLoader cl = this.getClass().getClassLoader();
WrapperClassLoader wcl = WrapperClassLoader.FACTORY.create(cl);
// castTo 将包装对象 wcl 转换为另一个包装类的对象，请勿使用强制转换
ExtendedWrapperClassLoader ewcl = wcl.castTo(ExtendedWrapperClassLoader.FACTORY);
ewcl.resolveClass(String.class);
```

= 包装字段访问器

显然由于我们的包装类是 interface 无法创建字段，所以我们将字段封装为 getter 和 setter（也可以只封装其中一个）

使用 `@WrapFieldAccessor`，若你的方法没有参数，代表这是一个 getter，否则代表 setter，setter 的返回值应该为 void

```java
@WrapSameClass(WrapperClassLoader.class)
public interface ExtendedWrapperClassLoader extends WrapperObject, WrapperClassLoader
{
    WrapperFactory<ExtendedWrapperClassLoader> FACTORY = WrapperFactory.of(ExtendedWrapperClassLoader.class);

    // 包装 parent 字段的 getter 和 setter
    @WrapFieldAccessor("parent")
    void setParent(ClassLoader parent);
    // 返回值上的 ClassLoader 换成它的包装类则会自动进行包装，getter 的参数也可以这样
    @WrapFieldAccessor("parent")
    ExtendedWrapperClassLoader getParent();
}
```

```java
// 设置一个 ClassLoader 的 parent 的 parent
ExtendedWrapperClassLoader.FACTORY.create(this.getClass().getClassLoader()) // 包装 ClassLoader
        .getParent() // 这样仍然得到一个包装过的 ClassLoader
        .setParent(null);
```

= 包装构造器

包装构造器使用 `@WrapConstructor` 注解，返回值必须是当前包装类，构造的实例会自动包装

```java
// 简单包装个 Object 类
@WrapClass(Object.class)
public interface ExampleWrapper extends WrapperObject
{
    WrapperFactory<ExampleWrapper> FACTORY = WrapperFactory.of(ExampleWrapper.class);

    // 包装 Object 的无参构造器
    @WrapConstructor
    ExampleWrapper static$of();
}
```

对了，你包装的方法必须是非静态的，这样我们才能继承和实现它，一般包装的构造器叫做 `of`

`static$` 开头的命名表示它的目标是静态的（构造器我们看成静态方法，这里指的不是 `<init>` 方法，而是返回实例的构造器）

作为包装类非静态方法，想调用它显然需要一个包装类实例，这样我们可以用 `FACTORY.getStatic()` 调用，因为我们调用目标类的静态方法所以不需要目标类的实例

为方便使用，我们可以再把它封装成静态方法

```java
// 然后我们自己封装成静态方法
static ExampleWrapper of()
{
    // 使用 FACTORY.getStatic() 调用
    return FACTORY.getStatic().static$of();
}

// 先用注解包装成非静态方法
@WrapConstructor
ExampleWrapper static$of();
```

构造器如此，静态方法和静态字段的访问器也是同理

= 版本特定实现

使用 `@SpecificImpl` 注解可以为同一个方法声明提供多个特定实现，根据运行时条件（如版本、平台）自动选择合适的实现。

详见 #link("specific_impl")[版本特定实现]。

= 为何它能高效运行

几乎每次使用包装器都要拆装箱，这看起来很糟糕，但实际上系统能够高效运行

- *JIT 逃逸分析*

    包装器全程没有使用反射或 native 等黑箱操作。对于简单生命周期的对象，JIT 很可能分析出它没有逃逸，并至少优化为栈上分配内存

- *年轻代 GC*

    对于短周期对象，GC 器有特殊优化并能及时回收内存

- *无锁内存分配*

    JVM 会预先给每个线程分配一块内存，以便随时快速地分配给小对象