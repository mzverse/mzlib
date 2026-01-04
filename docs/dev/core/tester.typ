#import "/lib/lib.typ": *;
#let title = [测试工具];
#show: template.with(title: title);

测试工具提供了单元测试和功能测试的支持。

= Tester

`Tester` 是测试器接口，定义了测试的基本结构。

```java
public interface Tester<T>
{
    void test(T context) throws Throwable;
}
```

= SimpleTester

`SimpleTester` 是简单的测试器实现，用于快速编写测试。

```java
public class ExampleTest
{
    public static void runTests()
    {
        SimpleTester.Builder<TesterContext> builder = SimpleTester.Builder.common()
            .setName("ExampleTest")
            .setFunction(context ->
            {
                // 测试代码
                int result = 2 + 2;
                if (result != 4)
                    throw new AssertionError("2 + 2 should be 4");
            });

        Tester tester = builder.build();
        tester.test(new TesterContext());
    }
}
```

= TesterContext

`TesterContext` 是测试上下文，包含测试运行时的信息。

```java
public class TesterContext
{
    public int getLevel();
    public void log(String message);
    public void error(String message);
}
```

= 使用示例

```java
public class DataTest
{
    public static void registerTests()
    {
        SimpleTester.Builder<TesterContext> testBuilder = SimpleTester.Builder.common()
            .setName(NbtCompound.class.getName())
            .setMinLevel(1)
            .setFunction(context ->
            {
                NbtCompound nbt = NbtCompound.newInstance();
                nbt.put("test", 123);
                if (!nbt.getInt("test").isSome())
                    throw new AssertionError("Failed to get integer");
            });

        TestModule.instance.register(testBuilder.build());
    }
}
```

= 注意事项

- 测试应该在模块的 `onLoad` 中注册
- 使用 `setMinLevel` 设置测试的最小等级
- 测试失败会抛出异常