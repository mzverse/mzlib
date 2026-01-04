#import "/lib/lib.typ": *;
#set raw(lang: "java");
#let title = [Core];
#show: template.with(title: title);

= 依赖mzlib-core

如果你要写mzlib-core的下游程序，依赖它

对于初学者，直接下载-all.jar然后导入

高手，请使用gradle(kts)导入，在build.gradle.kts中添加：

```kts
repositories {
    maven("https://maven.pkg.github.com/mzverse/mzlib") {
        credentials {
            username = System.getenv("GITHUB_USERNAME")
            password = System.getenv("GITHUB_TOKEN")
        }
    }
}
dependencies {
    compileOnly("org.mzverse:mzlib-core:latest.integration")
}
```

确保你的环境变量中有GITHUB_USERNAME和GITHUB_TOKEN，并且token具有read:packages权限。

以上步骤依赖了最新快照版，仅依赖最新版请将"latest.integration"改为"latest.release"

= 文档导航

/ #[= 核心系统]:
    #link("event")[事件系统] - Event, EventListener, Cancellable, ListenerHandler
    #link("module")[模块系统] - MzModule, IRegistrar, Registrable
    #link("i18n")[国际化] - I18n, RegistrarI18n
    #link("plugin")[插件管理] - Plugin, PluginManager
    #link("tester")[测试工具] - Tester, SimpleTester, TesterContext
    #link("priority")[优先级] - Priority 枚举
    #link("data")[数据处理] - DataHandler, DataKey

/ #[= 工具类]:
    #link("util/option")[Option] - Optional 类型封装
    #link("util/result")[Result] - 结果类型
    #link("util/either")[Either] - 二选一类型
    #link("util/pair")[Pair] - 键值对
    #link("util/ref")[Ref] - 引用类型（RefWeak, RefStrong）
    #link("util/cache")[Cache] - 缓存系统
    #link("util/class_cache")[ClassCache] - 类缓存
    #link("util/config")[Config] - 配置加载
    #link("util/io_util")[IOUtil] - IO 工具
    #link("util/collection_util")[CollectionUtil] - 集合工具
    #link("util/map_util")[MapUtil] - Map 工具
    #link("util/array_util")[ArrayUtil] - 数组工具
    #link("util/editor")[Editor] - 编辑器模式
    #link("util/async_function")[AsyncFunction] - 异步函数
    #link("util/auto_completable")[AutoCompletable] - 自动完成
    #link("util/compound")[Compound] - 复合对象
    #link("util/wrapper")[Wrapper] - Wrapper 系统
    #link("util/math")[数学工具] - Complex, Quaternion, Monoid, Group, Ring

/ #[= 教程]:
    #link("tutorial/index")[教程] - 从基础到进阶的完整教程