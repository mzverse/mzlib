#import "/lib/lib.typ": *;
#set raw(lang: "java");
#let title = `Item`;
#show: template.with(title: title);



= 自定义数据

使用数据键`Item.CUSTOM_DATA`

- == 获得自定义数据

    ```java
    Option<NbtCompound> customData = Item.CUSTOM_DATA.get(itemStack);
    ```
    不要修改得到的数据
- == 修改自定义数据

    - 在1.20.5之前，物品的所有拓展数据存在tag中，包括自定义数据；
    - 从1.20.5开始，物品的数据由物品组件存储，自定义数据是独立的组件，并且组件是只读的；

    在1.20.5+，多个物品可能共用一个自定义数据实例，所以不要直接修改它

    使用修订：
    ```java
    for(NbtCompound customData: Item.CUSTOM_DATA.revise(itemStack))
    {
        // nbt的修订
        for(NbtCompound myData: customData.reviseNbtCompoundOrNew("myData"))
        {
            myData.put("value1", 114514);
        }
    }
    ```
    修订的原理是浅克隆，永远不要修改get得到的数据，修改包括put和revise等
    ```java
    for(NbtCompound customData: Item.CUSTOM_DATA.revise(itemStack))
    {
        customData.put("myData", NbtCompound.newInstance()); // 对于立即创建的实例
        customData.getNbtCompound("myData").put("value1", 114514); // 你可以修改它

        // customData.getOrPutNewNbtCompound("unknownData") // 获取可能被复用的实例
            // .put("value1", 114514); // 错误，不要修改它
    }
    ```
    总结：revise得到的数据，你可以接着revise或put等；但get得到的数据尽量不要修改

    - === `continue`和`break`

        在修订的`for`中使用`continue`可以立即结束修订
        ```java
        for(NbtCompound customData: Item.CUSTOM_DATA.revise(itemStack))
        {
            customData.put("myData1", 1919810);
            if(!customData.containsKey("myData"))
                continue; // 结束修订
            for(NbtCompound myData: customData.reviseNbtCompoundOrNew("myData"))
            {
                myData.put("value1", 114514);
            }
        }
        ```
        如果把`continue`改为`break`，则直接取消修订，对`"myData1"`的修改也不会生效
        ```java
        for(NbtCompound customData: Item.CUSTOM_DATA.revise(itemStack))
        {
            customData.put("myData1", 1919810);
            for(NbtCompound myData: customData.reviseNbtCompoundOrNew("myData"))
            {
                myData.put("value1", 114514);
                break; // 取消对value1的修改
            }
            // 外层myData1的修改仍然生效
        }
        ```

    - === 传递revise

        你可能想将得到的revise传递出去，这是个错误示范：
        ```java
        public static Editor<NbtCompound> reviseMyData(ItemStack is)
        {
            for(NbtCompound customData: Item.CUSTOM_DATA.revise(itemStack))
            {
                return customData.reviseNbtCompoundOrNew("myData")); // 错误，return
            }
        }
        ```
        由于在`for`中使用`return`打断了外层的revise，所以修订不会完成

        换言之，只有在`for`内才能进行修订，所以你不能`return`到外部操作

        正确的方式是使用`Editor#then`：
        ```java
        public static Editor<NbtCompound> reviseMyData(ItemStack is)
        {
            return Item.CUSTOM_DATA.revise(itemStack)
                    .then(nbt -> nbt.reviseNbtCompoundOrNew("myData"));
        }
        ```