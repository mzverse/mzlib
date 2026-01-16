dependencies {
    api("org.jetbrains.kotlin:kotlin-stdlib-jdk8:latest.release")
    api("org.jetbrains.kotlinx:kotlinx-coroutines-core:latest.release")

    compileOnlyApi("org.spigotmc:spigot-api:1.12.2-R0.1-SNAPSHOT")
    compileOnlyApi("net.fabricmc:fabric-loader:0.16.10")
    compileOnlyApi(project(":mzlib-minecraft"))
    compileOnly("net.neoforged.fancymodloader:loader:11.0.0") {
        attributes {
            attribute(Category.CATEGORY_ATTRIBUTE, objects.named(Category.LIBRARY))
            attribute(Usage.USAGE_ATTRIBUTE, objects.named(Usage.JAVA_API))
            attribute(Bundling.BUNDLING_ATTRIBUTE, objects.named(Bundling.EXTERNAL))
            attribute(TargetJvmVersion.TARGET_JVM_VERSION_ATTRIBUTE, 25)
        }
        isTransitive = false
    }
}

tasks.shadowJar {
    manifest {
        attributes(
            "paperweight-mappings-namespace" to "mojang", // fuck Paper
        )
    }
}
