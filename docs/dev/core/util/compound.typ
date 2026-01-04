#import "/lib/lib.typ": *;
#let title = [Compound类];
#show: template.with(title: title);



Compound类是基于包装类的，请确保你已经学习了#link("./wrapper")[包装类]

Compound类的主要作用是继承包装类和多继承

通过这种方法继承包装类你可以得到继承类的包装

假设类a有包装类A，而a是你不能直接访问的，但你想要构造一个a的子类b，这时候你就可以用`Compound`创建一个A的子类，也就是b的包装类B

这个类本质上还是一个包装类，你至少继承一个`WrapperObject`

Compound类的子类也必须是Compound类

a的这个子类b会原封不动地继承a的所有构造器（并调用父类构造器），因此你只需要像封装a的构造器那样封装b的构造器

覆写父类的方法必须带上`@CompoundOverride`注解，如果要调用父方法，则需先用`@CompoundSuper`封装父方法

详见代码中注释：

= 继承包装类

```java
// 必须加上这个注解
@Compound
public interface WindowSlotButton extends WindowSlot // 直接继承即可
{
    // 作为一个WrapperObject的子类，应有creator
    @WrapperCreator
    static WindowSlotButton create(Object wrapped)
    {
        return WrapperObject.create(WindowSlotButton.class, wrapped);
    }

    /**
     * 构造器会直接原封不同继承
     * 对其包装即可
     */
    @WrapConstructor
    WindowSlot staticNewInstance(Inventory inventory, int index, int x, int y);
    static WindowSlot newInstance(Inventory inventory, int index)
    {
        return create(null).staticNewInstance(inventory, index, 0, 0);
    }

    /**
     * 继承一个方法需要加@CompoundOverride注解
     * parent表示这个方法所在的包装类
     * method表示对应的包装方法
     * 参数和返回值类型必须与被继承方法完全一致
     */
    @Override
    @CompoundOverride(parent=WindowSlot.class, method="canPlace")
    default boolean canPlace(ItemStack itemStack)
    {
        return false;
    }

    /**
     * 封装父方法，签名也必须一致
     */
    @CompoundSuper(parent=WindowSlot.class, method="canTake")
    boolean superCanTake(AbstractEntityPlayer player);

    @Override
    @CompoundOverride(parent=WindowSlot.class, method="canTake")
    default boolean canTake(AbstractEntityPlayer player)
    {
        // 这样就可以调用父方法了
        return superCanTake(player);
    }
}
```

= 多继承和属性

作为interface，包装类天然支持多继承

为了让它像class一样好用，我们为其添加属性

类似于字段，但你只需要写他的getter和setter，并加上注解

注解的参数为属性名，相同的名称代表同一个属性

```java
@Compound
public interface ExampleCompound extends WrapperObject
{
    WrapperFactory<ExampleCompound> FACTORY = WrapperFactory.find(ExampleCompound.class);
    @Deprecated
    @WrapperCreator
    static ExampleCompound create(Object wrapped)
    {
        return WrapperObject.create(ExampleCompound.class, wrapped);
    }

    /**
     * 封装构造器，用不到的话也可以子类再封装
     */
    static ExampleCompound newInstance()
    {
        return create(null).staticNewInstance();
    }
    @WrapConstructor
    ExampleCompound staticNewInstance();

    @PropAccessor("p1")
    int getP1();
    @PropAccessor("p1")
    void setP1(int value);
}
```
