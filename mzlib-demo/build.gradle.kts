dependencies {
    compileOnlyApi("org.spigotmc:spigot-api:1.12.2-R0.1-SNAPSHOT")
    compileOnlyApi("net.fabricmc:fabric-loader:0.16.10")
    compileOnlyApi("io.github.karlatemp:unsafe-accessor:1.6.0")
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
    
    // Vert.x dependencies for SimpleDocsServer
    implementation("io.vertx:vertx-core:5.0.5")
    implementation("io.vertx:vertx-web:5.0.5")

    testImplementation("io.github.karlatemp:unsafe-accessor:1.6.0")
    testImplementation(project(":mzlib-core"))
    testImplementation(project(":mzlib-minecraft"))
    testImplementation(files("../lib"))
}

tasks.shadowJar {
    manifest {
        attributes(
            "paperweight-mappings-namespace" to "mojang", // fuck Paper
        )
    }
}
