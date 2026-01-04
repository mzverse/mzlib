#import "/lib/lib.typ": *;
#let title = [数学工具];
#show: template.with(title: title);

数学工具包提供了各种数学运算和数据结构。

= Complex

`Complex` 表示复数，支持复数的各种运算。

```java
// 创建复数
Complex c1 = new Complex(3, 4);  // 3 + 4i
Complex c2 = new Complex(1, 2);  // 1 + 2i

// 加法
Complex sum = c1.add(c2);  // 4 + 6i

// 减法
Complex diff = c1.subtract(c2);  // 2 + 2i

// 乘法
Complex product = c1.multiply(c2);  // -5 + 10i

// 除法
Complex quotient = c1.divide(c2);

// 共轭
Complex conjugate = c1.conjugate();  // 3 - 4i

// 模
double magnitude = c1.magnitude();  // 5.0

// 幂
Complex power = c1.pow(2);  // -7 + 24i
```

= Quaternion

`Quaternion` 表示四元数，主要用于 3D 旋转。

```java
// 创建四元数
Quaternion q1 = new Quaternion(1, 2, 3, 4);

// 单位四元数
Quaternion identity = Quaternion.identity();

// 旋转四元数
Quaternion rotation = Quaternion.fromAxisAngle(new Vector3(0, 1, 0), Math.PI / 2);

// 乘法
Quaternion result = q1.multiply(q2);

// 共轭
Quaternion conjugate = q1.conjugate();

// 归一化
Quaternion normalized = q1.normalize();
```

= Monoid

`Monoid` 是幺半群，满足结合律和存在单位元。

```java
// 整数加法幺半群
Monoid<Integer> addMonoid = new Monoid<>(
    0,  // 单位元
    Integer::sum  // 结合运算
);

// 字符串连接幺半群
Monoid<String> concatMonoid = new Monoid<>(
    "",
    String::concat
);

// 折叠
List<Integer> list = Arrays.asList(1, 2, 3, 4);
Integer sum = addMonoid.fold(list);  // 10
```

= Group

`Group` 是群，在幺半群基础上增加了逆元。

```java
// 整数加法群
Group<Integer> addGroup = new Group<>(
    0,  // 单位元
    Integer::sum,  // 结合运算
    x -> -x  // 逆元
);

// 减法
int result = addGroup.subtract(10, 3);  // 7
```

= Ring

`Ring` 是环，在群基础上增加了乘法和分配律。

```java
// 整数环
Ring<Integer> integerRing = new Ring<>(
    0,  // 加法单位元
    Integer::sum,  // 加法
    x -> -x,  // 加法逆元
    1,  // 乘法单位元
    (a, b) -> a * b  // 乘法
);

// 运算
int sum = integerRing.add(3, 5);  // 8
int product = integerRing.multiply(3, 5);  // 15
```

= 使用示例

```java
// 复数运算
public Complex calculateComplex()
{
    Complex z1 = new Complex(2, 3);
    Complex z2 = new Complex(1, -1);
    return z1.multiply(z2).add(new Complex(5, 0));
}

// 旋转计算
public Quaternion rotateVector(Vector3 v, double angle, Vector3 axis)
{
    Quaternion q = Quaternion.fromAxisAngle(axis, angle);
    Quaternion p = new Quaternion(0, v.x, v.y, v.z);
    Quaternion result = q.multiply(p).multiply(q.conjugate());
    return new Vector3(result.x, result.y, result.z);
}
```

= 注意事项

- 数学运算可能产生精度误差
- 四元数用于 3D 旋转更稳定
- 使用代数结构可以简化某些算法