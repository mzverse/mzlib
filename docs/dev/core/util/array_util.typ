#import "/lib/lib.typ": *;
#let title = [ArrayUtil];
#show: template.with(title: title);

`ArrayUtil` 提供了基本类型数组和包装类型数组之间的转换方法。

= Box 操作（基本类型数组 → 包装类型数组）

将基本类型数组转换为对应的包装类型数组。

```java
// int → Integer
int[] intArray = {1, 2, 3, 4, 5};
Integer[] integerArray = ArrayUtil.box(intArray);

// long → Long
long[] longArray = {1L, 2L, 3L};
Long[] longWrapperArray = ArrayUtil.box(longArray);

// double → Double
double[] doubleArray = {1.0, 2.0, 3.0};
Double[] doubleWrapperArray = ArrayUtil.box(doubleArray);

// boolean → Boolean
boolean[] booleanArray = {true, false, true};
Boolean[] booleanWrapperArray = ArrayUtil.box(booleanArray);

// char → Character
char[] charArray = {'a', 'b', 'c'};
Character[] charWrapperArray = ArrayUtil.box(charArray);

// byte → Byte
byte[] byteArray = {1, 2, 3};
Byte[] byteWrapperArray = ArrayUtil.box(byteArray);

// short → Short
short[] shortArray = {1, 2, 3};
Short[] shortWrapperArray = ArrayUtil.box(shortArray);

// float → Float
float[] floatArray = {1.0f, 2.0f, 3.0f};
Float[] floatWrapperArray = ArrayUtil.box(floatArray);

// 使用 Object 参数自动识别类型
Object primitiveArray = new int[]{1, 2, 3};
Object[] boxedArray = ArrayUtil.box(primitiveArray);
```

= Unbox 操作（包装类型数组 → 基本类型数组）

将包装类型数组转换为对应的基本类型数组。

```java
// Integer → int
Integer[] integerArray = {1, 2, 3, 4, 5};
int[] intArray = ArrayUtil.unbox(integerArray);

// Long → long
Long[] longWrapperArray = {1L, 2L, 3L};
long[] longArray = ArrayUtil.unbox(longWrapperArray);

// Double → double
Double[] doubleWrapperArray = {1.0, 2.0, 3.0};
double[] doubleArray = ArrayUtil.unbox(doubleWrapperArray);

// Boolean → boolean
Boolean[] booleanWrapperArray = {true, false, true};
boolean[] booleanArray = ArrayUtil.unbox(booleanWrapperArray);

// Character → char
Character[] charWrapperArray = {'a', 'b', 'c'};
char[] charArray = ArrayUtil.unbox(charWrapperArray);

// Byte → byte
Byte[] byteWrapperArray = {1, 2, 3};
byte[] byteArray = ArrayUtil.unbox(byteWrapperArray);

// Short → short
Short[] shortWrapperArray = {1, 2, 3};
short[] shortArray = ArrayUtil.unbox(shortWrapperArray);

// Float → float
Float[] floatWrapperArray = {1.0f, 2.0f, 3.0f};
float[] floatArray = ArrayUtil.unbox(floatWrapperArray);

// 使用 Object 参数自动识别类型
Object[] wrapperArray = new Integer[]{1, 2, 3};
Object primitiveArray = ArrayUtil.unbox(wrapperArray);
```

= 使用示例

```java
// 将基本类型数组用于需要泛型的场景
public List<Integer> intArrayToList(int[] array)
{
    return Arrays.asList(ArrayUtil.box(array));
}

// 从包装类型数组恢复为基本类型数组
public int[] listToIntArray(List<Integer> list)
{
    return ArrayUtil.unbox(list.toArray(new Integer[0]));
}

// 对基本类型数组进行流式处理
public int[] filterAndMap(int[] array, Predicate<Integer> predicate, Function<Integer, Integer> mapper)
{
    return ArrayUtil.unbox(
        Arrays.stream(ArrayUtil.box(array))
            .filter(predicate)
            .map(mapper)
            .toArray(Integer[]::new)
    );
}

// 示例：过滤出偶数并乘以 2
int[] numbers = {1, 2, 3, 4, 5, 6};
int[] result = filterAndMap(numbers, i -> i % 2 == 0, i -> i * 2);
// result = [4, 8, 12]
```

= 注意事项

- `box` 操作会创建新的包装类型数组，不会修改原数组
- `unbox` 操作会创建新的基本类型数组，不会修改原数组
- 使用 `Object` 参数的方法会自动识别数组类型，但如果传入非基本类型数组会抛出 `ClassCastException`
- 所有方法都支持所有 8 种基本类型（int、long、double、boolean、char、byte、short、float）