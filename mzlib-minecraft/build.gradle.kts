repositories {
    maven("https://maven.neoforged.net/releases")
}

dependencies {
    api(project(":mzlib-core")) {
        exclude("com.google.code.gson", "gson")
    }

    compileOnly("com.google.code.gson:gson:2.8.9")

    compileOnly("org.spigotmc:spigot-api:1.12.2-R0.1-SNAPSHOT")
    compileOnly("net.fabricmc:fabric-loader:0.16.10")

    // neoforge
//    compileOnly("net.neoforged:neoforge:21.11.17-beta") {
//        attributes {
//            attribute(Category.CATEGORY_ATTRIBUTE, objects.named(Category.LIBRARY))
//            attribute(Usage.USAGE_ATTRIBUTE, objects.named(Usage.JAVA_API))
//            attribute(Bundling.BUNDLING_ATTRIBUTE, objects.named(Bundling.EXTERNAL))
//            attribute(TargetJvmVersion.TARGET_JVM_VERSION_ATTRIBUTE, 25)
//        }
//    }
    compileOnly("net.neoforged.fancymodloader:loader:11.0.0") {
        attributes {
            attribute(Category.CATEGORY_ATTRIBUTE, objects.named(Category.LIBRARY))
            attribute(Usage.USAGE_ATTRIBUTE, objects.named(Usage.JAVA_API))
            attribute(Bundling.BUNDLING_ATTRIBUTE, objects.named(Bundling.EXTERNAL))
            attribute(TargetJvmVersion.TARGET_JVM_VERSION_ATTRIBUTE, 25)
        }
        isTransitive = false
    }
    compileOnly("org.apache.maven:maven-artifact:3.9.9")

    compileOnly("com.rylinaux:PlugMan:2.2.9")
    compileOnly("com.mojang:brigadier:1.3.10")
    compileOnly("io.netty:netty-all:4.1.76.Final")
    compileOnly("net.luckperms:api:5.4")
    compileOnly("it.unimi.dsi:fastutil:7.1.0")
    compileOnly("com.mojang:datafixerupper:4.0.26")
}

tasks.shadowJar {
    manifest {
        attributes(
            "paperweight-mappings-namespace" to "mojang", // fuck Paper
            "Main-Class" to "mz.mzlib.minecraft.vanilla.MzLibMinecraftInitializer",
        )
    }
}

ext["publishing"] = true