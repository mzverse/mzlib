#import "/lib/lib.typ": *;
#let title = [序列化];
#show: template.with(title: title);

序列化系统提供了数据的编码和解码功能。

= CodecV1600

`CodecV1600` 是编解码器（1.16+）。

```java
// 获取 ItemStack 编解码器
CodecV1600.Wrapper<ItemStack> codec = ItemStack.codecV1600();

// 编码
Result<Option<JsonElement>, String> encoded = codec.encodeStart(JsonOpsV1300.instance(), stack);

// 解码
Result<Option<ItemStack>, String> decoded = codec.parse(JsonOpsV1300.instance(), json);
```

= DynamicV1300

`DynamicV1300` 是动态值（1.13+）。

```java
// 创建动态值
DynamicV1300 dynamic = DynamicV1300.newInstance(NbtOpsV1300.instance(), nbt);

// 转换为 NBT
NbtElement nbt = dynamic.getValue();

// 转换为 JSON
JsonElement json = dynamic.convert(JsonOpsV1300.instance());
```

= JsonOpsV1300

`JsonOpsV1300` 是 JSON 操作（1.13+）。

```java
// 获取 JSON 操作实例
JsonOpsV1300 ops = JsonOpsV1300.instance();

// 编码为 JSON
Result<Option<JsonElement>, String> encoded = codec.encodeStart(ops, value);

// 从 JSON 解码
Result<Option<T>, String> decoded = codec.parse(ops, json);
```

= NbtOpsV1300

`NbtOpsV1300` 是 NBT 操作（1.13+）。

```java
// 获取 NBT 操作实例
NbtOpsV1300 ops = NbtOpsV1300.instance();

// 带注册表的 NBT 操作
NbtOpsV1300 opsWithRegistries = NbtOpsV1300.withRegistriesV1903();

// 编码为 NBT
Result<Option<NbtElement>, String> encoded = codec.encodeStart(ops, value);

// 从 NBT 解码
Result<Option<T>, String> decoded = codec.parse(ops, nbt);
```

= 使用示例

```java
public class SerializationExample
{
    // 序列化 ItemStack 为 JSON
    public String serializeToJson(ItemStack stack)
    {
        CodecV1600.Wrapper<ItemStack> codec = ItemStack.codecV1600();
        Result<Option<JsonElement>, String> result = codec.encodeStart(JsonOpsV1300.instance(), stack);

        for (JsonElement json : result.getValue())
        {
            return json.toString();
        }

        return null;
    }

    // 从 JSON 反序列化 ItemStack
    public ItemStack deserializeFromJson(String jsonStr)
    {
        JsonElement json = new Gson().fromJson(jsonStr, JsonElement.class);
        CodecV1600.Wrapper<ItemStack> codec = ItemStack.codecV1600();
        Result<Option<ItemStack>, String> result = codec.parse(JsonOpsV1300.instance(), json);

        for (ItemStack stack : result.getValue())
        {
            return stack;
        }

        return ItemStack.EMPTY;
    }

    // 序列化 ItemStack 为 NBT
    public NbtCompound serializeToNbt(ItemStack stack)
    {
        CodecV1600.Wrapper<ItemStack> codec = ItemStack.codecV1600();
        Result<Option<NbtElement>, String> result = codec.encodeStart(NbtOpsV1300.withRegistriesV1903(), stack);

        for (NbtElement nbt : result.getValue())
        {
            return (NbtCompound) nbt;
        }

        return NbtCompound.newInstance();
    }
}
```

= 注意事项

- Codec 用于类型安全的序列化
- Dynamic 用于动态类型转换
- 使用带注册表的 Ops 处理注册表引用
- 检查版本后再使用