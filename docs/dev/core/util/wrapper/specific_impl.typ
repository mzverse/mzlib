#import "/lib/lib.typ": *;
#set raw(lang: "java");
#let title = [版本特定实现];
#show: template.with(title: title);



使用 `@SpecificImpl` 注解可以为同一个方法声明提供多个特定实现，根据运行时条件（如版本、平台）自动选择合适的实现。

= 基本用法

当同一个方法在不同版本或平台有不同的实现时，可以使用 `@SpecificImpl` 来标记这些特定实现的方法。

```java
// 方法声明（不能使用 @WrapMethod 等实现注解）
void setColor(TextColor value);

// 1.16 以下的特定实现
@SpecificImpl("setColor")
@VersionRange(end = 1600)
default Text setColorV_1600(TextColor value)
{
    this.style().setColorV_1600(value);
    return this;
}

// 1.16 及以上的特定实现
@SpecificImpl("setColor")
@VersionRange(begin = 1600)
default Text setColorV1600(TextColor value)
{
    this.setStyle(this.style().withColorV1600(value));
    return this;
}
```

**使用方式**：

```java
Text text = ...;
TextColor color = ...;

// 调用声明方法，系统会根据当前版本自动选择正确的实现
text.setColor(color);  // 1.16 以下调用 setColorV_1600，1.16+ 调用 setColorV1600
```

= 工作原理

1. **方法声明**：在接口中声明一个方法（可以是纯声明或带 default 实现）
2. **特定实现**：创建特定实现的方法，使用 `@SpecificImpl("声明方法名")` 标记
3. **自动选择**：Wrapper 系统根据 `ElementSwitcher` 机制（如 `@VersionRange`）自动选择启用的实现
4. **调用方式**：使用时只需调用原声明的方法，系统会自动路由到正确的实现

= 命名规范

特定实现方法的名称通常在原方法名后添加版本标识符（如 `V_1600`、`V1900`）

= 重要规则

- 声明方法不能使用 `@WrapMethod` 等实现注解
- 特定实现方法的参数类型必须与声明方法完全一致
- 特定实现方法通常与 `@VersionRange`、`@Enabled` 等 `ElementSwitcher` 注解配合使用

= 特殊情况：WrapperObject.equals

Java 的 interface 默认方法不能覆写 `Object` 的方法（如 `equals`、`hashCode`、`toString`）。WrapperObject 使用了巧妙的三层实现方式来解决这个问题。

详见 #link("wrapper_object_equals")[WrapperObject.equals 的巧妙实现]。