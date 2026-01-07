dependencies {
    @Suppress("VulnerableLibrariesLocal", "RedundantSuppression")
    api("moe.karla.unsafe:unsafe-accessor-root:1.2.0")
    api("moe.karla.unsafe:unsafe-accessor-unsafe:1.2.0")
    api("moe.karla.unsafe:unsafe-accessor-unsafe-java9:1.2.0")
    api("org.mozilla:rhino:1.7.15.1")
    api("com.google.code.gson:gson:2.8.9")
    api("net.bytebuddy:byte-buddy-agent:1.12.22")
//    api("com.google.guava:guava:32.0.0-jre")
//    api("org.graalvm.polyglot:polyglot:latest.release")
//    api("org.graalvm.polyglot:js:latest.release")
    compileOnlyApi("jakarta.annotation:jakarta.annotation-api:latest.release")
}

ext["publishing"] = true