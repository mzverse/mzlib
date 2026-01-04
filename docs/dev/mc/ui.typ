#import "/lib/lib.typ": *;
#let title = [UI 系统];
#show: template.with(title: title);

UI 系统提供了创建和管理用户界面的功能。

= UiStack

`UiStack` 是 UI 栈管理器。

```java
// 打开 UI
UiStack.open(player, ui);

// 关闭 UI
UiStack.close(player);

// 获取当前 UI
Option<Ui> currentUi = UiStack.getCurrent(player);
```

= UiWrittenBook

`UiWrittenBook` 是成书 UI。

```java
// 创建成书 UI
UiWrittenBook bookUi = UiWrittenBook.newInstance();

// 设置标题
bookUi.setTitle(Text.literal("我的书"));

// 设置作者
bookUi.setAuthor("作者名");

// 设置页数
bookUi.setPages(pages);

// 打开成书
bookUi.open(player);
```

= UiAbstractWindow

`UiAbstractWindow` 是抽象窗口 UI。

```java
public class MyWindow extends UiAbstractWindow
{
    @Override
    public Identifier getId()
    {
        return Identifier.of("myplugin", "my_window");
    }

    @Override
    public Text getTitle()
    {
        return Text.literal("我的窗口");
    }

    @Override
    public int getSize()
    {
        return 9;  // 窗口大小（槽位数）
    }

    @Override
    public void onSlotClick(int slot, EntityPlayer player)
    {
        // 处理槽位点击
    }
}
```

= UiWindowAnvil

`UiWindowAnvil` 是铁砧 UI。

```java
// 创建铁砧 UI
UiWindowAnvil anvilUi = UiWindowAnvil.newInstance();

// 设置重命名文本
anvilUi.setName(Text.literal("新名称"));

// 设置物品
anvilUi.setInputLeft(itemStack);

// 打开铁砧
anvilUi.open(player);
```

= 使用示例

```java
public class UiExample
{
    // 打开自定义窗口
    public void openCustomWindow(EntityPlayer player)
    {
        MyWindow window = new MyWindow();
        UiStack.open(player, window);
    }

    // 打开成书
    public void openBook(EntityPlayer player)
    {
        UiWrittenBook bookUi = UiWrittenBook.newInstance();
        bookUi.setTitle(Text.literal("教程"));
        bookUi.setAuthor("MzLib");
        bookUi.setPages(Arrays.asList(
            Text.literal("第一页"),
            Text.literal("第二页")
        ));
        bookUi.open(player);
    }
}
```

= 注意事项

- UI 需要在主线程打开
- 窗口大小需要正确设置
- 处理槽位点击事件