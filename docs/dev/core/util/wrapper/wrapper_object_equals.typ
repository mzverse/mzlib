#import "/lib/lib.typ": *;
#set raw(lang: "java");
#let title = [WrapperObject.equals 的巧妙实现];
#show: template.with(title: title);



Java 的 interface 默认方法不能覆写 `Object` 的方法（如 `equals`、`hashCode`、`toString`）。WrapperObject 使用了巧妙的三层实现方式来解决这个问题。

= 三层结构

```java
// 第一层：声明方法（名义上 override Object 的 equals）
@Override
boolean equals(Object object);

// 第二层：特定实现（处理类型检查和转换）
@SpecificImpl("equals")
default boolean equals$impl(Object object)
{
    if(this == object)
        return true;
    if(!(object instanceof WrapperObject))
        return false;
    return this.equals$impl((WrapperObject) object);
}

// 第三层：委托实现（自动拆包并调用被包装对象的 equals）
@WrapMethod("equals")
boolean equals$impl(WrapperObject object);
```

= 工作原理

1. **调用 `wrapper1.equals(wrapper2)`**
   - 自动路由到 `equals$impl(Object object)`

2. **类型检查和转换**
   - 检查 `object` 是否为 `WrapperObject` 实例
   - 如果是，转换为 `WrapperObject` 类型
   - 调用重载的 `equals$impl(WrapperObject object)`

3. **自动拆包和委托**
   - `@WrapMethod("equals")` 会自动拆包参数
   - 实际调用：`this.getWrapped().equals(object.getWrapped())`
   - 比较被包装对象

= 巧妙之处

- *符合直觉的用法*：`wrapper1.equals(wrapper2)` 比较包装对象，实际比较被包装对象
- *类型安全*：先进行类型检查，确保比较的是两个 Wrapper 对象
- *自动拆包*：`@WrapMethod` 自动处理参数拆包，无需手动调用 `getWrapped()`
- *解决 Java 限制*：通过 `@SpecificImpl` 机制绕过了 interface 不能覆写 Object 方法的限制
- *方法重载*：利用 Java 的方法重载机制，通过参数类型区分不同的实现

= 使用示例

```java
WrapperObject wrapper1 = WrapperObject.FACTORY.create(obj1);
WrapperObject wrapper2 = WrapperObject.FACTORY.create(obj2);

// 正确用法：比较两个 wrapper
boolean result = wrapper1.equals(wrapper2);
// 实际执行：obj1.equals(obj2)

// 与非 Wrapper 对象比较
boolean result2 = wrapper1.equals(obj1);
// 返回 false（因为 obj1 不是 WrapperObject 实例）
```

这个设计展示了 `@SpecificImpl` 注解的强大功能，它不仅可以用于版本特定实现，还可以用于解决 Java 语言本身的限制。