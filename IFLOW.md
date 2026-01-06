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
  - `ElementSwitcher.java`: 注解驱动开关系统
- `mz.mzlib.event`: 事件系统（Event、EventListener、Cancellable）
- `mz.mzlib.data`: 数据处理（DataHandler、DataKey）
- `mz.mzlib.i18n`: 国际化支持（I18n）
- `mz.mzlib.module`: 模块系统（MzModule、IRegistrar）
- `mz.mzlib.plugin`: 插件管理（Plugin、PluginManager）
- `mz.mzlib.tester`: 测试框架（Tester、TesterContext）
- `mz.mzlib.asm`: 完整的 ASM 字节码操作库（嵌入版本）

**核心工具类**:

#### ElementSwitcher 系统
ElementSwitcher 是一个通用的注解驱动开关系统，用于根据运行时条件动态启用或禁用类、方法、字段等元素。

**核心组件**:
- `ElementSwitcher<T extends Annotation>`: 核心接口，定义 `isEnabled()` 方法
- `ElementSwitcherClass`: 元注解，用于标记自定义注解并指定对应的处理器

**工作原理**:
1. 自定义注解使用 `@ElementSwitcherClass` 指定对应的 `ElementSwitcher` 实现类
2. `ElementSwitcher.isEnabled(AnnotatedElement)` 方法遍历元素的所有注解
3. 查找带有 `@ElementSwitcherClass` 的注解并实例化对应的处理器
4. 调用 `isEnabled()` 方法检查是否启用
5. 如果任何处理器返回 `false`，则整个元素被视为禁用

**常用注解**:

1. **@VersionRange**（Minecraft 版本检查，在 mzlib-minecraft 中）
   ```java
   @VersionRange(begin = 1300)  // 1.13及以上启用
   @VersionRange(end = 1200)    // 1.11.x及以下启用
   @VersionRange(begin = 1300, end = 1903)  // [1.13, 1.19.3)启用
   ```

2. **MinecraftPlatform.Enabled** / **MinecraftPlatform.Disabled**（平台标签检查，在 mzlib-minecraft 中）
   ```java
   @MinecraftPlatform.Enabled("bukkit")    // 仅在Bukkit平台启用
   @MinecraftPlatform.Enabled("paper")     // 仅在Paper平台启用
   @MinecraftPlatform.Disabled("fabric")   // 在Fabric平台禁用
   ```

**使用示例**:
```java
// 类级别：仅1.13+版本启用
@VersionRange(begin = 1300)
public class NewFeatureV1300 {
    // 方法级别：仅1.13-1.16版本启用
    @VersionRange(end = 1600)
    public void oldMethod() { ... }

    // 方法级别：仅Paper平台启用
    @MinecraftPlatform.Enabled("paper")
    public void paperOnlyMethod() { ... }
}

// 类级别：仅Bukkit平台启用
@MinecraftPlatform.Enabled("bukkit")
public class BukkitSpecificClass { ... }

// 组合示例：仅Bukkit平台的1.13+版本启用（AND 逻辑）
@MinecraftPlatform.Enabled("bukkit")
@VersionRange(begin = 1300)
public class BukkitNewFeatureV1300 { ... }
```

**逻辑规则**:

1. **AND 逻辑（多个不同注解）**
   - 多个不同注解同时存在时，取 AND 逻辑
   - 例如：`@MinecraftPlatform.Enabled("bukkit")` + `@VersionRange(begin = 1300)` 表示仅在 Bukkit 平台且版本 >= 1.13 时启用
   - 所有条件必须同时满足才启用

2. **OR 逻辑（注解自动合并）**
   - 同一注解多次标注时，使用注解的自动合并特性实现 OR 逻辑
   - 例如：标注多个 `@VersionRange` 会自动合并为 `@VersionRanges` 注解
   - `@VersionRanges` 自定义处理为在任意一个版本段均生效（OR）
   ```java
   @VersionRange(begin = 1300, end = 1600)
   @VersionRange(begin = 1900)
   public class SomeClass {
       // 等价于：在 [1.13, 1.16) 或 [1.19, ∞) 版本启用
   }
   ```

3. **优先级**
   - AND 逻辑的优先级高于 OR 逻辑
   - 先计算各注解的 AND 结果，再计算同一注解的 OR 结果

**设计优势**:
- 类型安全：基于注解和接口，编译时检查
- 可扩展：可以轻松添加新的条件检查注解
- 运行时灵活：根据实际运行环境动态启用/禁用代码
- 无侵入性：通过注解标记，不影响正常代码逻辑
- 组合使用：支持多个条件注解同时使用

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
- **内部类导入**: 不应当 import 内部类，而是每次写完整的 `外部类.内部类`
  - 避免使用 `import 外部类.内部类`
  - 例如：使用 `OuterClass.InnerClass` 而不是 `import OuterClass.InnerClass;`
- **@Enabled 完整路径**: 使用 `@Enabled` 注解时需要写内部类的完整路径
  - 外部类可以正常 import
  - 例如：`import mz.mzlib.minecraft.@MinecraftPlatform;` 然后使用 `@MinecraftPlatform.Enabled("bukkit")`
  - 或者直接使用：`@mz.mzlib.minecraft.@MinecraftPlatform.Enabled("bukkit")`
  - 这样可以明确注解的来源，避免混淆

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
- `name`: 类名、方法名或字段名（使用 Yarn 映射或 legacy Yarn 的名称）
- `remap`: 是否进行平台映射，默认为 `true`
  - `true`：根据所在平台自动映射（Fabric 使用 Yarn 名称，其他平台映射到 Mojang 映射）
  - `false`：直接使用 `name` 字段中的名称，不进行映射

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

#### 基本作用

Wrapper 系统主要用于以下两种场景：

1. **目标类或成员需要在运行时确定或可能不存在**
   - 例如：不同 Minecraft 版本中某些类或成员可能不存在
   - Wrapper 可以通过版本注解（如 `@VersionRange`）在编译时定义，在运行时根据实际环境选择实现
   - 避免了直接使用反射运行时查找的性能开销

2. **目标类或成员需要访问权限（如 private 或 protected）**
   - 例如：需要访问 Minecraft 原版类的私有字段或方法
   - Wrapper 可以突破访问限制，提供对私有成员的访问
   - 无需手动编写反射代码，类型安全且性能更好

Wrapper 系统提供优雅的原版类包装，无需反射。绝大部分情况下（为了兼容性），不直接调用 MC 的类和成员，而是使用 wrapper 工具对其进行封装，然后间接调用。

#### WrapperObject 基类

`WrapperObject` 是 Wrapper 系统的核心基类，所有 Wrapper 类都必须直接或间接继承它。

##### 重要规则

1. **所有 Wrapper 类必须是 interface，不能是 class**
   - Wrapper 类必须定义为接口
   - 这是 Wrapper 系统的工作机制决定的
   - 错误示例：`public class WrapperEntityPlayer { ... }`
   - 正确示例：`public interface WrapperEntityPlayer { ... }`

2. **所有 Wrapper 类必须直接或间接继承 WrapperObject**
   - WrapperObject 提供了基础的包装功能
   - 继承 WrapperObject 可以获得类型转换、实例检查等通用能力
   - 错误示例：`public interface WrapperEntityPlayer { ... }`（未继承 WrapperObject）
   - 正确示例：`public interface WrapperEntityPlayer extends WrapperObject { ... }`

##### WrapperObject 的特殊作用

`WrapperObject` 自身代表了 `Object` 的包装类，提供对 Object 的基本封装：

- **`toString()`**：调用被包装对象的 `toString()`
- **`hashCode()`**：调用被包装对象的 `hashCode()`
- **`equals(Object)`**：比较被包装对象（参见 @SpecificImpl 的巧妙实现）
- **`clone()`**：克隆被包装对象
- **`getWrapped()`**：获取被包装的对象
- **`setWrapped(Object)`**：设置被包装的对象

##### 示例

```java
// ✅ 正确：Wrapper 类必须是 interface 并继承 WrapperObject
@WrapClass(NmsEntityPlayer.class)
public interface WrapperEntityPlayer extends WrapperObject {
    WrapperFactory<WrapperEntityPlayer> FACTORY = WrapperFactory.of(WrapperEntityPlayer.class);

    @WrapMethod("getName")
    String getName();
}

// ❌ 错误：使用 class 定义 Wrapper
public class WrapperEntityPlayer {
    // ...
}

// ❌ 错误：未继承 WrapperObject
public interface WrapperEntityPlayer {
    // ...
}
```

#### FACTORY 字段

每个 Wrapper 类都应当添加 `FACTORY` 字段，这是 Wrapper 系统的重要入口点。

##### FACTORY 字段的作用

```java
WrapperFactory<WrapperEntityPlayer> FACTORY = WrapperFactory.of(WrapperEntityPlayer.class);
```

**重要性**：
- 提供对此 Wrapper 类的统一入口
- 相比 `Class<?>` 对象包含更多信息
- 可以由 `Class<?>` 对象得到，但直接使用 FACTORY 更高效

##### 使用方式

```java
// 创建实例
WrapperEntityPlayer player = WrapperEntityPlayer.FACTORY.create(rawPlayer);

// 类型检查
boolean isPlayer = wrapper.is(WrapperEntityPlayer.FACTORY);

// 类型转换
WrapperEntityPlayer player = wrapper.as(WrapperEntityPlayer.FACTORY);

// 获取静态成员
String staticField = WrapperEntityPlayer.FACTORY.getStatic().static$getStaticField();
```

##### 与 Class<?> 的区别

- **FACTORY**：包含 Wrapper 类的所有元信息，性能更高
- **Class<?>**：仅包含基本的类信息，需要额外查找

```java
// ✅ 推荐：使用 FACTORY（高效）
WrapperEntityPlayer player = WrapperEntityPlayer.FACTORY.create(rawPlayer);

// ⚠️ 不推荐：使用 Class<?>（效率较低）
WrapperFactory<WrapperEntityPlayer> factory = WrapperFactory.of(WrapperEntityPlayer.class);
WrapperEntityPlayer player = factory.create(rawPlayer);
```

##### 特殊情况

某些特殊用法（如 Nothing）不需要添加 FACTORY 字段，因为这些类不会被直接使用。

##### 静态 create 方法

**历史遗留产物（将被移除）**：

```java
// ❌ 不要使用：静态 create 方法（历史遗留，将被移除）
@Deprecated
static WrapperObject create(Object wrapped)
{
    return WrapperObject.create(WrapperObject.class, wrapped);
}
```

**新增的 Wrapper 类不需要写静态 create 方法**，直接使用 FACTORY 字段即可：

```java
// ✅ 正确：使用 FACTORY
WrapperEntityPlayer player = WrapperEntityPlayer.FACTORY.create(rawPlayer);
```

#### 基本用法

**定义 Wrapper 接口**：
```java
@WrapClass(NmsEntityPlayer.class)
public interface WrapperEntityPlayer extends WrapperObject {
    // 工厂字段（每个 wrapper 类都应当添加）
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
```

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

### @SpecificImpl 注解

`@SpecificImpl` 是 Wrapper 系统中的核心注解，用于为同一个方法声明提供多个特定实现，根据运行时条件（如版本、平台）自动选择合适的实现。

#### 核心用途

当同一个方法在不同版本或平台有不同的实现时，可以使用 `@SpecificImpl` 来标记这些特定实现的方法。

#### 工作原理

1. **方法声明**：在接口中声明一个方法（可以是纯声明或带 default 实现）
2. **特定实现**：创建特定实现的方法，使用 `@SpecificImpl("声明方法名")` 标记
3. **自动选择**：Wrapper 系统根据 `ElementSwitcher` 机制（如 `@VersionRange`）自动选择启用的实现
4. **调用方式**：使用时只需调用原声明的方法，系统会自动路由到正确的实现

#### 参数说明

- **value**: 声明方法的方法名（可以是抽象方法或接口方法），表示当前方法是该声明方法的特定实现

#### 命名规范

- 特定实现方法的名称通常在原方法名后添加版本标识符（如 `V_1900`、`V1900`）
- 也可以使用 `$impl` 后缀（如 `equals$impl`），但这主要用于处理特殊情况

#### 基本示例（来自 Text.java）

```java
// 方法声明（纯声明，不使用 @WrapMethod 等实现注解）
Text setColor(TextColor value);

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

#### 重要规则

1. **声明方法不能使用 `@WrapMethod` 等实现注解**
   - 如果在声明方法上使用 `@WrapMethod` 等注解，Wrapper 系统会为其生成实现
   - 这会与 `@SpecificImpl` 的特定实现产生冲突
   - 声明方法只能写为纯声明或带方法体的 default 方法

2. **声明方法可以使用开关注解**
   - 可以在声明方法上使用 `@VersionRange`、`@Enabled` 等 `ElementSwitcher` 开关注解
   - 这些注解用于控制声明方法本身是否启用
   - 但不能使用 `@WrapMethod`、`@WrapFieldAccessor` 等实现类注解

3. **参数类型必须一致**：特定实现方法的参数类型必须与声明方法完全一致

4. **自动路由**：当调用声明方法时，系统会根据 `ElementSwitcher` 机制自动选择正确的特定实现

5. **配合版本注解**：特定实现方法通常与 `@VersionRange`、`@Enabled` 等 `ElementSwitcher` 注解配合使用

#### 错误示例

```java
// ❌ 错误：声明方法使用了 @WrapMethod
@WrapMethod("setStyle")
void setStyle(TextStyle style);

// ❌ 错误：特定实现同时使用 @WrapMethod 和 default 实现
@WrapMethod("setStyle")
@SpecificImpl("setStyle")
@VersionRange(end = 1900)
default void setStyleV_1900(TextStyle style)
{
    // ...
}
```

#### 正确示例

```java
// ✅ 正确：声明方法仅声明
void setStyle(TextStyle style);

// ✅ 正确：特定实现使用 default 实现
@SpecificImpl("setStyle")
@VersionRange(end = 1900)
default void setStyleV_1900(TextStyle style)
{
    this.castTo(AbstractTextV_1900.FACTORY).setStyleV_1900(style);
}

// ✅ 正确：声明方法带 default 实现（但不能覆写 Object 方法）
@VersionRange(begin = 1300)
default String someMethod()
{
    return "default value";
}
```

#### 特殊情况：WrapperObject.equals 的巧妙实现

Java 的 interface 默认方法不能覆写 `Object` 的方法（如 `equals`、`hashCode`、`toString`）。WrapperObject 使用了巧妙的三层实现方式来解决这个问题。

##### 三层结构

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

##### 工作原理

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

##### 巧妙之处

- **符合直觉的用法**：`wrapper1.equals(wrapper2)` 比较包装对象，实际比较被包装对象
- **类型安全**：先进行类型检查，确保比较的是两个 Wrapper 对象
- **自动拆包**：`@WrapMethod` 自动处理参数拆包，无需手动调用 `getWrapped()`
- **解决 Java 限制**：通过 `@SpecificImpl` 机制绕过了 interface 不能覆写 Object 方法的限制
- **方法重载**：利用 Java 的方法重载机制，通过参数类型区分不同的实现

##### 使用示例

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

#### 工作原理

1. Wrapper 系统在运行时扫描接口中的所有方法
2. 找到带有 `@SpecificImpl` 注解的方法
3. 根据 `ElementSwitcher` 机制（如 `@VersionRange`）判断哪些实现应该启用
4. 将声明方法绑定到启用的实现上
5. 调用声明方法时，自动路由到正确的实现

这个机制使得 Wrapper 接口能够：
- 覆盖 Object 的方法（`equals`、`hashCode`、`toString`）
- 提供版本特定的方法实现
- 保持类型安全和代码清晰

### Minecraft 专用注解

Minecraft 模块提供了四个专用注解用于版本特定的包装，这些注解中的 `value` 字段是 `@VersionName` 注解数组，表示在各版本段的不同名称。

**重要特性**：
- 这些注解同时实现了 `ElementSwitcher` 接口和对应的 `WrappedClassFinder`/`WrappedMemberFinder` 接口
- 这意味着它们既具有开关功能（根据版本范围自动启用/禁用），又具有查找功能（根据版本范围查找对应的类/方法/字段）
- 只有当注解中的某个 `@VersionName` 匹配当前版本时，该元素才会被启用

**平台映射机制**：
- 注解中的 `name` 字段使用的是 **Yarn 表（或旧版本的 legacy Yarn）的名称**
- 运行时会根据**所在平台**进行自动映射：
  - **Fabric 平台**：直接使用 Yarn 名称
  - **Bukkit/Spigot/Paper 平台**：映射到 Mojang 映射
  - **NeoForge 平台**：映射到 Mojang 映射
  - **Vanilla 平台**：映射到 Mojang 映射
- 这使得代码可以在不同平台间共享，而无需重复定义

#### @WrapMinecraftClass
```java
@WrapMinecraftClass({
    @VersionName(name = "net.minecraft.screen.ScreenHandler", end = 1400),
    @VersionName(name = "net.minecraft.container.Container", begin = 1400, end = 1600),
    @VersionName(name = "net.minecraft.screen.ScreenHandler", begin = 1600)
})
public interface Window extends WrapperObject
```

这里的 `"net.minecraft.screen.ScreenHandler"` 是 Yarn 映射中的名称，运行时会：
- 在 Fabric 上：直接使用 `"net.minecraft.screen.ScreenHandler"`
- 在 Bukkit 上：映射到 Mojang 映射（如 `"net.minecraft.world.inventory.Menu"`）
- 在 NeoForge 上：映射到 Mojang 映射

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

这里的 `"field_15680"` 和 `"EMPTY"` 是 Yarn 映射中的字段名，会根据平台自动映射。

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
- **注解中的名称始终使用 Yarn 映射**（包括 legacy Yarn），系统会自动根据当前平台进行转换

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

### 11.2 自动更新规则
当用户要求"自动更新 IFLOW.md 而无需询问"时，应当直接更新 IFLOW.md 文件，不需要先询问用户确认。这适用于：
- 用户明确要求更新文档时
- 用户纠正错误并要求更新时
- 用户说"包括此规则"等包含性语句时
- 用户说"记住这个问题"时，应当立即记录到 IFLOW.md 中
- 用户说"叫你记住这个问题你没明白吗"等强调性语句时，必须立即记录

**重要原则**：总是自动记录重要信息到 IFLOW.md，不要等待用户再次提醒。当用户提到需要记住的规则、约定或重要信息时，应该立即更新 IFLOW.md，而不是等到下次需要时才想起来。

### 11.3 示例
当用户说"以后我教你新的内容你也应当更新到此md文件中"时，这个规则本身也应该被记录到 IFLOW.md 中（即本章）。

### 11.4 Typst 文档链接规则
在编写 Typst 文档时，使用 `#link()` 创建链接时必须省略 `.typ` 后缀，因为编译后文件是 HTML 格式。

**错误示例**：
```typst
#link("specific_impl.typ")[版本特定实现]
```

**正确示例**：
```typst
#link("specific_impl")[版本特定实现]
```

**原因**：
- Typst 源文件使用 `.typ` 后缀
- 编译后生成 HTML 文件
- 链接应该指向编译后的 HTML 文件，而不是源文件
- Typst 会自动处理文件扩展名

### 11.5 Typst 标题层级规则
在编写 Typst 文档时，必须遵守标题层级的规范：

**标题符号**：
- `=` - 一级标题
- `==` - 二级标题
- `===` - 三级标题
- `====` - 四级标题
- 以此类推

**层级规则**：
- 一级标题后不能直接跳到三级标题，必须先有二级标题
- 标题层级必须逐步递增，不能跳级

**错误示例**：
```typst
= 一级标题

=== 三级标题  // 错误：跳过了二级标题
```

**正确示例**：
```typst
= 一级标题

== 二级标题

=== 三级标题
```

**原因**：
- 保持文档结构的清晰和一致性
- 便于阅读和维护
- 符合 Typst 的语法规范

### 11.6 模块系统核心概念

模块系统是 MzLib 的核心概念之一，用于模块化编写各功能并管理生命周期。

#### 核心特性

1. **模块化设计**：将功能拆分为独立的模块，每个模块负责特定的功能
2. **生命周期管理**：提供加载和卸载机制，自动管理组件的生命周期
3. **自动注销**：模块卸载时自动注销所有注册的组件，无需手动管理
4. **单例常量**：模块通常定义为 `static final` 单例常量
5. **依赖管理**：注册器支持依赖管理，自动解析依赖关系并进行拓扑排序

#### 模块生命周期

模块的生命周期分为两个阶段：

1. **加载阶段 (load)**：
   - 调用 `load()` 方法
   - 调用 `onLoad()` 初始化模块
   - 注册子模块
   - 注册组件（监听器、命令等）

2. **卸载阶段 (unload)**：
   - 调用 `unload()` 方法
   - 按照注册相反的顺序自动注销所有组件
   - 卸载子模块
   - 调用 `onUnload()` 清理资源

#### 程序入口点

一个程序（插件）在入口点加载其主模块（通常是一个），在程序生命周期结束时卸载主模块。主模块使用 `load()` 和 `unload()` 手动管理：

```java
public class MyPlugin
{
    public static MyPlugin instance = new MyPlugin();
    public static final MainModule mainModule = new MainModule();

    public void onEnable()
    {
        // 加载主模块
        mainModule.load();
    }

    public void onDisable()
    {
        // 卸载主模块
        mainModule.unload();
    }
}
```

#### 模块定义

模块通常定义为单例常量。主模块在 `onLoad()` 中注册子模块：

```java
public class MainModule extends MzModule
{
    public static final MainModule instance = new MainModule();

    @Override
    public void onLoad()
    {
        // 注册子模块
        this.register(CommandModule.instance);
        this.register(ListenerModule.instance);

        // 注册事件监听器
        this.register(new EventListener<>(MyEvent.class, Priority.HIGH, event -> {
            // 处理事件
        }));
    }

    @Override
    public void onUnload()
    {
        // 清理资源（所有注册的组件会自动注销）
    }
}
```

#### 子模块

子模块只需在其父模块的 `onLoad()` 中注册即可，无需手动调用 `load()` 和 `unload()`：

```java
public class CommandModule extends MzModule
{
    public static final CommandModule instance = new CommandModule();

    @Override
    public void onLoad()
    {
        // 注册命令
        this.register(MyCommand.instance);
    }
}
```

**重要规则**：
- 主模块：在程序入口点使用 `load()` 和 `unload()` 手动管理
- 子模块：只需在父模块的 `onLoad()` 中通过 `this.register(subModule)` 注册即可
- 父模块卸载时会自动卸载所有子模块

#### 自动注销机制

模块系统会自动管理组件的生命周期：

- 注册时记录所有组件
- 卸载时按照注册相反的顺序自动注销所有内容
- 包括子模块（子模块的注销即卸载）
- 无需手动管理注销逻辑

#### 注册器系统

注册器是模块中的重要概念，用于管理特定类型对象的注册和注销。

**注册器的核心特性**：

1. **注册器本身需要注册**：注册器本身同样需要注册到模块中
2. **支持的对象类型**：每个注册器通过 `getType()` 方法指定其支持的对象类型
3. **可注册性检查**：通过 `isRegistrable()` 方法判断对象是否可以被注册
4. **依赖管理**：通过 `getDependencies()` 方法声明依赖的注册器，系统会自动解析依赖关系

**注册器接口**：

```java
public interface IRegistrar<T>
{
    // 获取支持的类型
    Class<T> getType();

    // 判断对象是否可注册
    default boolean isRegistrable(T object)
    {
        return true;
    }

    // 注册对象
    void register(MzModule module, T object);

    // 注销对象
    void unregister(MzModule module, T object);

    // 获取依赖的注册器
    default Set<IRegistrar<?>> getDependencies()
    {
        return new HashSet<>();
    }
}
```

**注册器管理器**：

`RegistrarRegistrar` 是注册器的管理器，用于管理所有注册器：

```java
public class RegistrarRegistrar implements IRegistrar<IRegistrar<?>>
{
    public static RegistrarRegistrar instance = new RegistrarRegistrar();

    // 按类型存储所有注册器
    public final Map<Class<?>, Set<IRegistrar<?>>> registrars = new ConcurrentHashMap<>();
}
```

**重要概念**：

1. **模块与作用域无关**：
   - 注册器所在的模块与被注册对象所在模块无需有关联
   - 模块仅决定生命周期，不决定作用域
   - 若想限制作用域，请使用 ClassLoader

2. **注册流程**：
   - 注册器先注册到模块中
   - 然后其支持的对象可以被注册
   - 系统会自动找到支持该对象类型的注册器

3. **依赖解析**：
   - 注册器可以声明依赖其他注册器
   - 系统会自动解析依赖关系并进行拓扑排序
   - 按照依赖顺序注册，按照相反顺序注销

**示例**：

```java
// 定义注册器
public class MyRegistrar implements IRegistrar<MyObject>
{
    public static final MyRegistrar instance = new MyRegistrar();

    @Override
    public Class<MyObject> getType()
    {
        return MyObject.class;
    }

    @Override
    public void register(MzModule module, MyObject object)
    {
        // 注册逻辑
    }

    @Override
    public void unregister(MzModule module, MyObject object)
    {
        // 注销逻辑
    }
}

// 在模块中注册注册器
public class MyModule extends MzModule
{
    public static final MyModule instance = new MyModule();

    @Override
    public void onLoad()
    {
        // 注册注册器
        this.register(MyRegistrar.instance);

        // 注册对象（系统会自动找到支持该对象类型的注册器）
        this.register(new MyObject());
    }
}
```

#### Registrable 接口

`Registrable` 是一个简单实现，类只需实现它及其两个方法即可被注册注销而无需专用注册器。

**Registrable 接口**：

```java
public interface Registrable
{
    void onRegister(MzModule module);

    void onUnregister(MzModule module);
}
```

**RegistrableRegistrar**：

`RegistrableRegistrar` 是 `Registrable` 的专用注册器，已经内置在系统中：

```java
public class RegistrableRegistrar implements IRegistrar<Registrable>
{
    public static RegistrableRegistrar instance = new RegistrableRegistrar();

    @Override
    public Class<Registrable> getType()
    {
        return Registrable.class;
    }

    @Override
    public void register(MzModule module, Registrable object)
    {
        object.onRegister(module);
    }

    @Override
    public void unregister(MzModule module, Registrable object)
    {
        object.onUnregister(module);
    }
}
```

**使用示例**：

```java
// 实现 Registrable 接口
public class MyComponent implements Registrable
{
    public static final MyComponent instance = new MyComponent();

    @Override
    public void onRegister(MzModule module)
    {
        // 注册时的初始化逻辑
        System.out.println("Registered to module: " + module);
    }

    @Override
    public void onUnregister(MzModule module)
    {
        // 注销时的清理逻辑
        System.out.println("Unregistered from module: " + module);
    }
}

// 在模块中注册
public class MyModule extends MzModule
{
    public static final MyModule instance = new MyModule();

    @Override
    public void onLoad()
    {
        // 直接注册，无需专用注册器
        this.register(MyComponent.instance);
    }
}
```

**使用场景**：

`Registrable` 适用于固定的注册注销逻辑。如果需要动态的注册注销逻辑，则需要将注册器本身动态注册到模块中。

- **固定逻辑**：使用 `Registrable` 接口，无需创建专用注册器
- **动态逻辑**：将注册器本身动态注册到模块中，支持运行时条件

#### 最佳实践

1. **使用单例常量**：模块通常使用 `static final` 单例常量
2. **在 onLoad 中注册组件**：所有组件注册应该在 `onLoad` 中完成
3. **在 onUnload 中清理资源**：资源清理应该在 `onUnload` 中完成
4. **利用自动注销**：不要在 `onUnload` 中手动注销组件，系统会自动处理
5. **选择合适的注册方式**：
   - 固定的注册注销逻辑：使用 `Registrable` 接口
   - 动态的注册注销逻辑：将注册器本身动态注册到模块中