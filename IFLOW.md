# MzLib 项目上下文

## 1. 项目概述

### 1.1 项目简介
MzLib 是一个跨平台的 Minecraft 开发类库，旨在简化 Minecraft 插件/模组开发，提供统一的 API 抽象层。项目支持多平台（Bukkit/Spigot/Paper、Fabric、NeoForge、Vanilla），版本兼容范围从 1.8.8 到 1.21.11+。

### 1.2 技术栈
- **构建工具**: Gradle 8.10+ (Kotlin DSL)
- **编程语言**: Java (兼容 Java 8+，构建使用 JDK 21)
- **测试框架**: JUnit 5 (Jupiter 5.9.2)
- **文档系统**: Typst → HTML
- **CI/CD**: GitHub Actions
- **版本控制**: Git
- **包管理**: GitHub Packages

### 1.3 架构设计
项目主要由两部分组成：

```
mzlib-demo (示例和演示)
    ↓ 依赖
mzlib-minecraft (Minecraft 平台适配)
    ↓ 依赖
mzlib-core (核心功能模块，与 MC 无关)
```

- **mzlib-core**: 与 Minecraft 无关的核心功能模块，提供通用的 Java 工具和基础设施，包括事件系统、模块系统、数据系统、国际化、工具类等
- **mzlib-minecraft**: Minecraft 平台特定实现，提供跨平台 API 抽象，包括命令系统、物品系统、UI 系统、数据包监听等
- **mzlib-demo**: 示例和演示模块，展示最佳实践和使用方法

**重要说明**: mzlib-core 是完全独立的，不依赖任何 Minecraft 相关的类或 API，可以单独使用于其他 Java 项目。

### 1.4 平台支持
- **Bukkit/Spigot/Paper/Folia**: 完整支持
- **Fabric**: 完整支持（包括客户端）
- **NeoForge**: 实验性支持
- **Vanilla**: 实验性支持

### 1.5 推荐的 Minecraft 版本
- **推荐**: 1.8.8, 1.12.2, 1.14.4, 1.16.4, 1.19.4, 1.20.4, 1.21.5+
- **不推荐**: 1.16.5 (内部改动大), 1.17 (实现未稳定)

## 2. 构建和运行

### 2.1 环境要求
- **JDK**: 17 或更高版本（项目兼容 Java 8+）
- **Gradle**: 8.10+（项目包含 Gradle Wrapper）
- **Git**: 用于版本控制
- **Typst**: 用于文档构建（可选）

### 2.2 构建命令

#### 基础构建
```bash
# 完整构建（所有模块）
./gradlew build

# Windows
gradlew.bat build

# 清理构建产物
./gradlew clean

# 清理并重新构建
./gradlew clean build
```

#### 模块特定构建
```bash
# 构建 Core 模块
./gradlew :mzlib-core:build

# 构建 Minecraft 模块
./gradlew :mzlib-minecraft:build

# 构建 Demo 模块
./gradlew :mzlib-demo:build

# 构建特定模块的 JAR
./gradlew :mzlib-core:shadowJar
```

### 2.3 测试命令
```bash
# 运行所有测试
./gradlew test

# 运行特定模块测试
./gradlew :mzlib-core:test
./gradlew :mzlib-minecraft:test
./gradlew :mzlib-demo:test

# 查看测试报告（报告位于 build/reports/tests/）
./gradlew test --continue

# 运行特定测试类
./gradlew :mzlib-core:test --tests "mz.mzlib.util.TestClassUtil"
```

### 2.4 文档构建
```bash
# 构建所有文档
./gradlew buildDocs

# 构建开发者文档
./gradlew :docs:dev:compileTypst

# 构建用户文档
./gradlew :docs:user:compileTypst

# 启动文档预览服务器（端口 8080）
./gradlew serveDocs

# 文档输出位置
# - 开发文档: docs/build/dev/
# - 用户文档: docs/build/user/
```

### 2.5 发布流程
```bash
# 本地发布到 Maven 仓库
./gradlew publishToMavenLocal

# 发布到远程仓库（需要配置 GitHub Packages）
./gradlew publish

# 设置构建类型（用于区分 release 和 snapshot）
# Release 构建
BUILD_TYPE=release ./gradlew build

# Snapshot 构建（默认）
./gradlew build
```

### 2.6 其他有用命令
```bash
# 查看所有可用任务
./gradlew tasks

# 查看项目依赖
./gradlew dependencies

# 查看特定模块依赖
./gradlew :mzlib-minecraft:dependencies

# 获取详细调试信息
./gradlew build --debug

# 获取依赖更新
./gradlew build --refresh-dependencies
```

## 3. 模块说明

### 3.1 mzlib-core
**职责**: 提供与 Minecraft 无关的核心功能，完全独立的通用 Java 工具库

**主要包结构**:
- `mz.mzlib.util`: 工具类集合
  - `asm/`: ASM 字节码操作工具
  - `async/`: 异步函数支持
  - `compound/`: 复合模式实现（运行时动态类生成）
  - `math/`: 数学工具（复数、四元数、群论）
  - `nothing/`: 零开销注入系统
  - `wrapper/`: 包装器系统
  - `Option.java`, `Result.java`, `Either.java`: 函数式编程类型
- `mz.mzlib.event`: 事件系统（Event、EventListener、Cancellable）
- `mz.mzlib.data`: 数据处理（DataHandler、DataKey）
- `mz.mzlib.i18n`: 国际化支持（I18n）
- `mz.mzlib.module`: 模块系统（MzModule、IRegistrar）
- `mz.mzlib.plugin`: 插件管理（Plugin、PluginManager）
- `mz.mzlib.tester`: 测试框架（Tester、TesterContext）
- `mz.mzlib.asm`: 完整的 ASM 字节码操作库（嵌入版本）

**依赖**: 无（基础模块）

**核心特性**:
- 模块化和插件系统
- 事件驱动架构
- 类型安全的包装器系统
- 运行时动态类生成
- 函数式编程支持

### 3.2 mzlib-minecraft
**职责**: Minecraft 平台特定实现，提供跨平台 API 抽象

**主要功能模块**:
- `mz.mzlib.minecraft.bukkit/`: Bukkit 平台实现
- `mz.mzlib.minecraft.fabric/`: Fabric 平台实现
- `mz.mzlib.minecraft.neoforge/`: NeoForge 平台实现
- `mz.mzlib.minecraft.vanilla/`: Vanilla 平台实现
- `mz.mzlib.minecraft.command/`: 命令系统（Command、CommandManager、ArgumentParser）
- `mz.mzlib.minecraft.item/`: 物品系统（Item、ItemStack、ItemStackBuilder）
- `mz.mzlib.minecraft.ui/`: UI 系统（UiWindow、UiWrittenBook）
- `mz.mzlib.minecraft.nbt/`: NBT 数据操作
- `mz.mzlib.minecraft.text/`: 文本组件系统
- `mz.mzlib.minecraft.inventory/`: 库存系统
- `mz.mzlib.minecraft.recipe/`: 配方系统（Recipe、RecipeManager）
- `mz.mzlib.minecraft.entity/`: 实体系统（Entity、EntityPlayer、DamageSource）
- `mz.mzlib.minecraft.world/`: 世界系统
- `mz.mzlib.minecraft.mappings/`: 映射系统（Mojang、Yarn、Spigot）
- `mz.mzlib.minecraft.network.packet/`: 数据包监听和处理
- `mz.mzlib.minecraft.permission/`: 权限系统
- `mz.mzlib.minecraft.serialization/`: 序列化（Codec、DynamicOps）
- `mz.mzlib.minecraft.component/`: 物品组件系统（1.20.5+）
- `mz.mzlib.minecraft.datafixer/`: 数据修复器（版本间数据迁移）

**依赖**: mzlib-core

**核心特性**:
- 跨平台 API 统一
- 版本兼容性支持（1.8.8 - 1.21.11+）
- 数据包监听和注入
- 自定义物品和 UI
- 命令系统（基于 Brigadier）
- 配方和合成系统

### 3.3 mzlib-demo
**职责**: 示例和演示模块，展示最佳实践

**主要示例**:
- `entrypoint/`: 各平台入口点（DemoBukkit、DemoFabric、DemoNeoForge）
- `game/chinesechess/`: 中国象棋游戏示例
- `game/tictactoe/`: 井字棋游戏示例
- `SimpleDocsServer.java`: 文档预览服务器（使用 Vert.x）

**依赖**: mzlib-core, mzlib-minecraft

**核心特性**:
- 实际使用示例
- 最佳实践演示
- 功能测试用例

### 3.4 模块依赖关系
```
mzlib-demo
    ↓
mzlib-minecraft
    ↓
mzlib-core
```

**外部依赖**:
- **mzlib-core**: 
  - `io.github.karlatemp:unsafe-accessor:1.7.0`
  - `org.mozilla:rhino:1.7.15.1` (JavaScript 引擎)
  - `com.google.code.gson:gson:2.8.9`
  - `net.bytebuddy:byte-buddy-agent:1.12.22`

- **mzlib-minecraft**:
  - `org.spigotmc:spigot-api:1.12.2-R0.1-SNAPSHOT` (Bukkit API)
  - `net.fabricmc:fabric-loader:0.16.10` (Fabric 加载器)
  - `net.neoforged.fancymodloader:loader:11.0.0` (NeoForge 加载器)
  - `com.mojang:brigadier:1.3.10` (命令系统)
  - `io.netty:netty-all:4.1.76.Final` (网络库)
  - `net.luckperms:api:5.4` (权限 API)
  - `it.unimi.dsi:fastutil:7.1.0` (高效集合)
  - `com.mojang:datafixerupper:4.0.26` (数据修复)

- **mzlib-demo**:
  - `io.vertx:vertx-core:5.0.5`
  - `io.vertx:vertx-web:5.0.5`

## 4. 开发约定

### 4.1 命名规范

#### 基本命名规则
- **包名**: 全小写，使用点分隔（如 `mz.mzlib.util`）
- **类名**: 大驼峰（PascalCase），如 `ElementSwitcherClass`
- **方法名**: 小驼峰（camelCase），如 `getElement`
- **常量**: 全大写，下划线分隔，如 `MAX_SIZE`
- **私有字段**: 小驼峰，可选下划线前缀，如 `_internalField`

#### Minecraft 平台特定命名规则
**NMS（Net Minecraft Server）包装类**:
- 类名前添加 `Nms`
- 示例：`NmsEntityPlayer`, `NmsWorld`

**CraftBukkit 包装类**:
- 类名前添加 `Obc`
- 示例：`ObcCraftServer`, `ObcInventory`

**Paper 独有类**:
- 类名末尾添加 `Paper`
- 示例：`EntityPlayerPaper`, `WorldPaper`

#### 版本标识符规则
使用 `@VersionRange` 注解标记版本特定的代码。

**层级说明**:
- **最高层级**: 类（不能用于包名）
- **方法层级**: 类中的方法可以单独标识版本

**命名约定**:
- 类名中应标明支持的版本范围
- 使用 `v` 前缀表示版本标识符
- 格式：`类名 + vA` 或 `类名 + vA_B`
- 方法名中只需要标明与类不同的版本范围

**类级别示例**:
```java
@VersionRange(begin = 1200) // v1200: 1.12 及以上
class SomeClassV1200 {
    // ...
}

@VersionRange(begin = 1300, end = 1903) // v1300_1903: [1.13, 1.19.3)
class SomeClassV1300_1903 {
    // ...
}

@VersionRange(end = 1200) // v_1200: 1.11.x 及以下
class SomeClassV_1200 {
    // ...
}
```

**方法级别示例**:
```java
@VersionRange(begin = 1300) // v1300: 1.13 及以上
class SomeClassV1300 {
    // 此方法支持 [1.13, 1.16)
    @VersionRange(end = 1600) // v_1600: 与类 v1300 结合，实际范围为 [1.13, 1.16)
    void someMethodV_1600() {
        // ...
    }

    // 此方法支持 [1.13, 1.20)
    @VersionRange(end = 2000) // v_2000: 与类 v1300 结合，实际范围为 [1.13, 1.20)
    void anotherMethodV_2000() {
        // ...
    }
}
```

**重要规则**:
- 即使一个包里所有的类都仅支持 v1300，也应当在每个类名后面加 V1300，而不是在包名后加 V1300
- 方法名中的版本标识符只需要标明与类不同的部分
- 例如：类 `SomeClassV1300` 中的方法 `someMethodV_1600` 表示 `[1.13, 1.16)`（因为类已经限定了下界 1.13）

**参数说明**:
- `begin`: 下界（包含），默认为 0
- `end`: 上界（不包含），默认为 `Integer.MAX_VALUE`

**版本标识符简写**:
在注释、文档或快速引用中，使用简写形式 `vA_B`：
- `v1200`: 1.12 及以上版本（对应 `@VersionRange(begin = 1200)`）
- `v_1200`: 1.11.x 及以下版本（对应 `@VersionRange(end = 1200)`）
- `v1300_1903`: [1.13, 1.19.3)（对应 `@VersionRange(begin = 1300, end = 1903)`）

**版本号转换规则**:
- 格式：`1.x.y` → `x * 100 + y`
- 示例：
  - `1.12.2` → `1202`
  - `1.13.0` → `1300`
  - `1.19.3` → `1903`
  - `1.20.5` → `2005`

#### 注册器命名
- 格式：`Registrar` + 功能名
- 示例：`RegistrarI18n`, `RegistrarCommand`, `RegistrarRecipe`

#### 模块命名
- 格式：`Module` + 功能名
- 示例：`ModuleRecipe`, `ModuleCommand`, `ModuleUI`

### 4.2 代码风格
- **代码风格配置**: 使用项目提供的 `codestyle.xml`
- **缩进**: 4 空格
- **行宽**: 120 字符
- **注释**: JavaDoc 格式，中英文均可，保持一致性
- **导入顺序**: 标准库 → 第三方库 → 项目内部

#### 代码示例
```java
// NMS 包装类示例
@WrapClass(NmsEntityPlayer.class)
public interface WrapperEntityPlayer extends WrapperObject {
    @WrapMethod("getBukkitEntity")
    EntityPlayer getBukkitEntity();
    
    @WrapFieldAccessor("health")
    double getHealth();
    void setHealth(double health);
}

// 版本特定代码示例
@VersionRange(begin = 1300, end = 1903) // v1300_1903: [1.13, 1.19.3)
public class SomeClassV1300_1903 {
    // ...
}

// 注册器示例
public class RegistrarRecipe implements IRegistrar<Recipe> {
    @Override
    public void register(Recipe recipe) {
        // ...
    }
}
```

### 4.3 Git 工作流

#### 分支策略
- **主分支**: `main`（稳定版本）
- **开发分支**: `dev`（开发中）
- **功能分支**: `feature/功能名`
- **修复分支**: `fix/问题描述`
- **文档分支**: `docs/文档更新`

#### 提交信息规范
遵循 Conventional Commits 规范：
- 格式：`<type>(<scope>): <description>`

**类型（type）**:
- `feat`: 新功能
- `fix`: 修复 bug
- `docs`: 文档变更
- `style`: 代码格式（不影响代码运行的变动）
- `refactor`: 重构（既不是新增功能，也不是修改 bug 的代码变动）
- `test`: 增加测试
- `chore`: 构建过程或辅助工具的变动

**示例**:
```
feat(core): add ElementSwitcherClass utility
fix(minecraft): resolve packet listener issue
docs(readme): update installation guide
style(core): format code according to codestyle.xml
refactor(command): improve command parser performance
test(wrapper): add unit tests for WrapperFactory
chore(build): upgrade Gradle to 8.10
```

### 4.4 代码审查流程
1. 所有 PR 必须经过至少一人审查
2. 确保 CI 通过（构建和测试）
3. 代码风格符合规范（使用 `codestyle.xml`）
4. 文档已更新（如需要）
5. 测试覆盖率充足

### 4.5 文档贡献规范
- 不需要构建的文档提交信息中包含 `[ci skip]` 以跳过构建
- 英文与中文之间包含一个半角空格
- 中文语句需要以句号结尾
- 语法类英文使用内联代码包裹

**示例**:
```
docs(readme): update quick start guide [ci skip]

添加了快速开始指南的详细说明，包括环境要求和构建步骤。
```

## 5. 平台兼容性

### 5.1 支持的平台
- **Bukkit/Spigot/Paper/Folia**: 完整支持
- **Fabric**: 完整支持（包括客户端）
- **NeoForge**: 实验性支持
- **Vanilla**: 实验性支持

### 5.2 版本兼容范围
- **最低版本**: 1.8.8
- **最高版本**: 1.21.11+
- **推荐版本**: 1.12.2, 1.14.4, 1.16.4, 1.19.4, 1.20.4, 1.21.5+

### 5.3 版本兼容实现

#### 注解驱动
使用 `@VersionRange` 和 `@VersionName` 注解标记版本特定的代码：

```java
@VersionRange(begin = 1300, end = 1903) // v1300_1903: [1.13, 1.19.3)
public class SomeClassV1300_1903 {
    // ...
}

@VersionName(begin = 2000, name = "NewClassName") // v2000: 1.20 及以上
public interface WrapperClassV2000 {
    // ...
}
```

**注解使用规则**:
- `@VersionRange` 和 `@VersionName` 只能用于类级别（不能用于包名）
- 类名中应标明支持的版本范围
- 使用 `v` 前缀表示版本标识符

**@VersionName 参数说明**:
- `begin`: 下界（包含），默认为 0
- `end`: 上界（不包含），默认为 `Integer.MAX_VALUE`
- `name`: 类名或方法名
- `remap`: 是否重新映射，默认为 true

#### 映射系统
项目使用映射系统适配不同版本的 Minecraft 类名和方法名：

**映射文件位置**:
- `mappings/Mojang/`: Mojang 官方映射
- `mappings/Spigot/`: Spigot 映射
- `mappings/Yarn/`: Yarn 映射（Fabric）
- `mappings/YarnIntermediary/`: Yarn 中间映射

**映射系统特点**:
- 支持运行时动态加载映射
- 支持版本特定的映射
- 提供统一的 API 访问不同版本的原版类

### 5.4 Wrapper 系统
Wrapper 系统提供优雅的原版类包装，无需反射。绝大部分情况下（为了兼容性），不直接调用 MC 的类和成员，而是使用 wrapper 工具对其进行封装，然后间接调用。

#### 基本用法

**定义 Wrapper 接口**：
```java
@WrapClass(NmsEntityPlayer.class)
public interface WrapperEntityPlayer extends WrapperObject {
    // 工厂字段（每个 wrapper 类都需要）
    WrapperFactory<WrapperEntityPlayer> FACTORY = WrapperFactory.of(WrapperEntityPlayer.class);
    
    // 普通方法
    @WrapMethod("getBukkitEntity")
    EntityPlayer getBukkitEntity();
    
    // 字段 getter
    @WrapFieldAccessor("health")
    double getHealth();
    
    // 字段 setter（需要与 getter 相同的注解）
    @WrapFieldAccessor("health")
    void setHealth(double health);
}
```

#### 类包装注解

**`@WrapClass`** - 参数直接写目标类（Class 对象）
```java
@WrapClass(NmsEntityPlayer.class)
public interface WrapperEntityPlayer extends WrapperObject {
    // ...
}
```

**`@WrapClassForName`** - 参数是完整类名字符串（用于动态加载）
```java
@WrapClassForName("net.minecraft.entity.player.ServerPlayerEntity")
public interface WrapperEntityPlayer extends WrapperObject {
    // ...
}
```

**`@WrapInnerClass`** - 包装内部类
```java
@WrapClass(OuterClass.class)
public interface WrapperOuterClass extends WrapperObject {
    @WrapInnerClass(outer = WrapperOuterClass.class, name = "Inner")  // name 是 $ 之后的部分
    interface Inner extends WrapperObject {
        // ...
    }
}
```

**重要说明**：
- `@WrapClass`: 直接传入 Class 对象（推荐用于已知的类）
- `@WrapClassForName`: 传入完整类名字符串（用于动态加载或避免编译时依赖）
- `@WrapInnerClass`: 用于包装内部类，`outer` 参数是外部包装类，`name` 参数是内部类名（`$` 之后的部分）

**创建实例**：
```java
// 由已有对象直接创建包装
WrapperEntityPlayer player = WrapperEntityPlayer.FACTORY.create(rawPlayer);
```

**使用 Wrapper**：
```java
double health = player.getHealth();
player.setHealth(20.0);
```

**使用静态成员和构造器**：
```java
// 实例方法
WrapperEntityPlayer player = WrapperEntityPlayer.FACTORY.create(rawPlayer);
player.getHealth();

#### 静态成员和构造器

**重要规则**：
- 静态成员或构造器封装为普通方法时，名称前应当加上 `static$` 前缀以防重名
- 然后封装一个没有此前缀的真正静态方法，通过 `FACTORY.getStatic()` 实例去调用伪静态方法
- 就像反射调用填 null 一样的原理
- 最外层封装完全是自己实现的，不需要加额外注解
- 构造器一般叫 `of` 而不是 `create`

**完整示例**：
```java
@WrapClass(NmsEntityPlayer.class)
public interface WrapperEntityPlayer extends WrapperObject {
    // 工厂字段
    WrapperFactory<WrapperEntityPlayer> FACTORY = WrapperFactory.of(WrapperEntityPlayer.class);
    
    // 普通方法
    @WrapMethod("getBukkitEntity")
    EntityPlayer getBukkitEntity();
    
    // 字段 getter/setter
    @WrapFieldAccessor("health")
    double getHealth();
    @WrapFieldAccessor("health")
    void setHealth(double health);
    
    // 构造器（封装为普通方法，名称前加 static$）
    @WrapConstructor
    WrapperEntityPlayer static$of(World world, GameProfile profile);
    
    // 真正的静态方法（自己实现，不需要注解）
    static WrapperEntityPlayer of(World world, GameProfile profile) {
        return FACTORY.getStatic().static$of(world, profile);
    }
    
    // 静态方法（封装为普通方法，名称前加 static$）
    @WrapMethod("staticMethod")
    String static$staticMethod();
    
    // 真正的静态方法（自己实现）
    static String staticMethod() {
        return FACTORY.getStatic().static$staticMethod();
    }
    
    // 静态字段 getter（封装为普通方法）
    @WrapFieldAccessor("STATIC_FIELD")
    String static$getStaticField();
    
    // 真正的静态 getter（自己实现）
    static String getStaticField() {
        return FACTORY.getStatic().static$getStaticField();
    }
    
    // 静态字段 setter（封装为普通方法）
    @WrapFieldAccessor("STATIC_FIELD")
    void static$setStaticField(String value);
    
    // 真正的静态 setter（自己实现）
    static void setStaticField(String value) {
        FACTORY.getStatic().static$setStaticField(value);
    }
}
```

**使用静态成员和构造器**：
```java
// 实例方法
WrapperEntityPlayer player = WrapperEntityPlayer.FACTORY.wrap(rawPlayer);
player.getHealth();

// 静态方法（直接调用静态方法）
WrapperEntityPlayer.staticMethod();

// 构造器（使用 of）
WrapperEntityPlayer newPlayer = WrapperEntityPlayer.of(world, profile);

// 静态字段
WrapperEntityPlayer.getStaticField();
WrapperEntityPlayer.setStaticField("value");
```

#### 常见误解和澄清

**误解 1**: 使用 `WrapperFactory.wrap(NmsEntityPlayer.class, rawPlayer)`
**澄清**: 应该使用 `WrapperEntityPlayer.FACTORY.create(rawPlayer)`，通过工厂字段创建实例

**误解 2**: 使用 `WrapperFactory.create()` 创建工厂
**澄清**: 应该使用 `WrapperFactory.of(WrapperEntityPlayer.class)`，参数是 wrapper 类自身，而不是被包装的类

**误解 3**: `@WrapClass` 的参数是完整类名字符串
**澄清**: `@WrapClass` 的参数应该直接写目标类（Class 对象），如 `@WrapClass(NmsEntityPlayer.class)`。如果需要使用完整类名字符串，应该使用 `@WrapClassForName`，如 `@WrapClassForName("net.minecraft.entity.player.ServerPlayerEntity")`

**误解 4**: 将静态成员封装为静态方法
**澄清**: 静态成员应该封装为普通方法（名称前加 `static$`），然后通过真正的静态方法调用，以便可以被继承

**误解 5**: 构造器叫 `create`
**澄清**: 构造器一般叫 `of` 而不是 `create`

**误解 6**: setter 不需要注解
**澄清**: setter 需要和 getter 使用相同的 `@WrapFieldAccessor` 注解

**误解 7**: `@WrapInnerClass` 的 `name` 参数是完整内部类名
**澄清**: `@WrapInnerClass` 的 `name` 参数是内部类名（`$` 之后的部分），例如 `Outer$Inner` 中 `name` 应该是 `"Inner"`

#### 特点
- 类型安全
- 无反射开销
- 支持版本特定的包装实现
- 集成 Option 类型
- 支持静态成员和构造器
- 可继承（通过封装为普通方法）
- 支持内部类包装（`@WrapInnerClass`）
- 支持动态类加载（`@WrapClassForName`）

### Minecraft 专用注解

Minecraft 模块提供了四个专用注解用于版本特定的包装，这些注解中的 `value` 字段是 `@VersionName` 注解数组，表示在各版本段的不同名称。

#### @WrapMinecraftClass
```java
@WrapMinecraftClass({
    @VersionName(name = "net.minecraft.screen.ScreenHandler", end = 1400),
    @VersionName(name = "net.minecraft.container.Container", begin = 1400, end = 1600),
    @VersionName(name = "net.minecraft.screen.ScreenHandler", begin = 1600)
})
public interface Window extends WrapperObject
```

#### @WrapMinecraftMethod
```java
@WrapMinecraftMethod({
    @VersionName(name = "getInvSize", end = 1600),
    @VersionName(name = "size", begin = 1600)
})
int size();
```

#### @WrapMinecraftFieldAccessor
```java
@WrapMinecraftFieldAccessor({
    @VersionName(name = "field_15680", end = 1400),
    @VersionName(name = "EMPTY", begin = 1400)
})
IngredientVanilla static$emptyV1200_2102();
```

#### @WrapMinecraftInnerClass
```java
@WrapMinecraftInnerClass(outer = FontDescriptionV2109.class, name = @VersionName(name = "Font"))
interface Resource extends FontDescriptionV2109
```

**重要说明**：
- `@WrapMinecraftClass`、`@WrapMinecraftMethod`、`@WrapMinecraftFieldAccessor`、`@WrapMinecraftInnerClass` 的 `value`/`name` 字段都是 `@VersionName` 注解数组
- 单元素数组可以省略花括号
- `@VersionName` 支持版本范围（`begin`、`end`）和自动重映射（`remap`）
- 通过 `@VersionName` 数组可以处理类名、方法名、字段名在不同版本间的变化

### 5.5 数据包监听
支持服务端和客户端数据包监听：

```java
// 注册数据包监听器
PacketListenerRegistry.register(new PacketListenerAdapter() {
    @Override
    public void onPacketSend(PacketEvent event) {
        // 处理发送的数据包
    }
    
    @Override
    public void onPacketReceive(PacketEvent event) {
        // 处理接收的数据包
    }
});
```

**特点**:
- 使用 Netty Channel 拦截
- 支持数据包修改和替换
- 支持数据包捆绑（PacketBundle, 1.19.4+）

## 6. 关键文件

### 6.1 构建配置文件
- **`build.gradle.kts`**: 根项目构建配置，定义通用依赖和插件
- **`settings.gradle.kts`**: 项目设置，声明所有模块
- **`gradle/wrapper/gradle-wrapper.properties`**: Gradle Wrapper 配置
- **`gradle/utils.gradle.kts`**: Gradle 工具函数
- **`mzlib-core/build.gradle.kts`**: Core 模块构建配置
- **`mzlib-minecraft/build.gradle.kts`**: Minecraft 模块构建配置
- **`mzlib-demo/build.gradle.kts`**: Demo 模块构建配置

### 6.2 文档配置文件
- **`docs/index.typ`**: 文档主入口
- **`docs/lib/`**: 文档库和样式文件
  - `template.typ`: 文档模板
  - `style.typ`: 样式定义
  - `lib.typ`: 库函数
- **`docs/dev/`**: 开发者文档
- **`docs/user/`**: 用户文档

### 6.3 CI/CD 配置文件
- **`.github/workflows/build.yml`**: 构建和测试流程
  - 使用 JDK 21
  - 运行 Gradle 构建
  - 发布到 GitHub Packages
  - 自动创建 GitHub Release（标签推送时）
- **`.github/workflows/docs.yml`**: 文档部署流程
  - 使用 Rust Toolchain
  - 安装 Typst CLI
  - 构建文档
  - 部署到 GitHub Pages

### 6.4 其他重要文件
- **`README.md`**: 项目简介和快速开始
- **`CONTRIBUTING.md`**: 贡献指南
- **`CHANGELOG.md`**: 版本历史和变更日志
- **`LICENSE`**: Mozilla Public License Version 2.0
- **`codestyle.xml`**: 代码风格配置
- **`.gitignore`**: Git 忽略规则

## 7. 故障排除

### 7.1 常见问题

#### 构建失败
**问题**: Gradle 构建失败
**解决方案**:
1. 检查 JDK 版本（需要 JDK 17+）
2. 清理构建产物：`./gradlew clean`
3. 刷新依赖：`./gradlew build --refresh-dependencies`
4. 检查网络连接（需要访问 Maven 仓库）

#### 测试失败
**问题**: 单元测试失败
**解决方案**:
1. 查看测试报告：`build/reports/tests/`
2. 运行特定测试：`./gradlew test --tests "类名"`
3. 检查环境配置
4. 使用 `--continue` 参数继续运行所有测试

#### 文档构建失败
**问题**: Typst 文档构建失败
**解决方案**:
1. 确保 Typst 已安装：`typst --version`
2. 检查 Typst 语法
3. 查看构建日志获取详细错误信息

#### 版本兼容问题
**问题**: 某些 Minecraft 版本不兼容
**解决方案**:
1. 检查版本标识符是否正确
2. 查看映射文件是否包含对应版本
3. 参考 `minecraftchangelog/` 目录了解版本变更

### 7.2 调试技巧

#### 获取详细日志
```bash
# Gradle 构建调试
./gradlew build --debug

# 查看 Gradle 任务详情
./gradlew build --info

# 查看所有可用任务
./gradlew tasks
```

#### 查看依赖树
```bash
# 查看项目所有依赖
./gradlew dependencies

# 查看特定模块依赖
./gradlew :mzlib-minecraft:dependencies

# 查看依赖配置
./gradlew :mzlib-core:dependencies --configuration compileClasspath
```

#### 查看测试报告
```bash
# 测试报告位置
# - HTML: build/reports/tests/test/index.html
# - XML: build/test-results/test/

# 在浏览器中打开报告
open build/reports/tests/test/index.html  # macOS
xdg-open build/reports/tests/test/index.html  # Linux
start build/reports/tests/test/index.html  # Windows
```

#### 查看构建报告
```bash
# 构建报告位置
# - HTML: build/reports/build/index.html

# 在浏览器中打开报告
open build/reports/build/index.html  # macOS
```

### 7.3 性能优化

#### 加速构建
```bash
# 使用 Gradle 守护进程（默认启用）
./gradlew build --daemon

# 并行构建
./gradlew build --parallel

# 配置离线模式（如果依赖已缓存）
./gradlew build --offline
```

#### 减少内存占用
```bash
# 调整 JVM 堆大小
./gradlew build -Dorg.gradle.jvmargs="-Xmx2g -Xms1g"
```

## 8. 项目元信息

- **当前版本**: 10.0.1-beta.17
- **许可证**: Mozilla Public License Version 2.0
- **组织**: MzVerse Team
- **仓库**: https://github.com/mzverse/mzlib
- **文档**: https://mzverse.github.io/mzlib/
- **QQ 群**: 750455476

## 9. 附属插件

- **LoginAUI**: 铁砧登录页面
  - 链接: https://www.mcbbs.net/thread-1324546-1-1.html
- **MzBackwards**: 回跨版本显示优化
  - 链接: https://www.mcbbs.net/thread-1369629-1-1.html
- **MzItemStack**: 自定义物品堆叠
  - 链接: https://www.mcbbs.net/thread-1370314-1-1.html

## 10. 快速参考

### 项目结构
项目主要由两部分组成：
- **mzlib-core**: 与 Minecraft 无关的核心功能模块
- **mzlib-minecraft**: Minecraft 平台适配模块

### 常用命令速查
```bash
# 构建
./gradlew build
./gradlew clean build

# 测试
./gradlew test
./gradlew :mzlib-core:test

# 文档
./gradlew buildDocs
./gradlew serveDocs

# 发布
./gradlew publishToMavenLocal
```

### 关键包路径
- **核心工具**: `mz.mzlib.util`
- **事件系统**: `mz.mzlib.event`
- **模块系统**: `mz.mzlib.module`
- **Minecraft API**: `mz.mzlib.minecraft`
- **Wrapper 系统**: `mz.mzlib.util.wrapper`

### 版本标识符速查
- `v_1300`: 1.12.x 及以下（对应 `@VersionRange(end = 1300)`）
- `v1300`: 1.13 及以上（对应 `@VersionRange(begin = 1300)`）
- `v1300_1903`: [1.13, 1.19.3)（对应 `@VersionRange(begin = 1300, end = 1903)`）
- `v2000`: 1.20 及以上（对应 `@VersionRange(begin = 2000)`）
- `v2000_2100`: [1.20, 1.21)（对应 `@VersionRange(begin = 2000, end = 2100)`）

## 11. AI 助手规则

### 11.1 知识更新规则
当用户教给我新的内容时，必须将这些内容更新到 `IFLOW.md` 文件中，而不是使用 `save_memory` 工具。

**适用场景**:
- 用户介绍项目特定的开发约定
- 用户说明代码架构的设计理念
- 用户提供项目特有的工作流程
- 用户分享最佳实践和经验

**更新原则**:
- 将新内容添加到 IFLOW.md 的适当章节
- 保持文档结构清晰和连贯
- 使用一致的格式和风格
- 在相关章节添加交叉引用（如需要）

**不适用场景**:
- 用户个人偏好（如颜色喜好、编辑器选择等）
- 临时性的调试信息
- 与项目无关的通用知识

### 11.2 示例
当用户说"以后我教你新的内容你也应当更新到此md文件中"时，这个规则本身也应该被记录到 IFLOW.md 中（即本章）。