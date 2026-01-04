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