plugins {
    kotlin("jvm") version "2.1.20-Beta2"
    id("com.gradleup.shadow") version "8.3.5"
}

group = "net.xtb7"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/") {
        name = "spigotmc-repo"
    }
    maven("https://oss.sonatype.org/content/groups/public/") {
        name = "sonatype"
    }
}

dependencies {
    compileOnly("org.spigotmc:spigot-api:1.13-R0.1-SNAPSHOT")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("net.kyori:adventure-api:4.18.0")
    implementation("net.kyori:adventure-platform-bukkit:4.3.0")
    implementation("net.kyori:adventure-text-logger-slf4j:4.13.1")
    implementation("com.google.code.gson:gson:2.8.9")
    implementation("net.kyori:adventure-text-minimessage:4.18.0")
    implementation("org.xerial:sqlite-jdbc:3.34.0")
}

val targetJavaVersion = 21
kotlin {
    jvmToolchain(targetJavaVersion)
}

tasks.build {
    dependsOn("shadowJar")
}

tasks.withType<Jar> {
    doLast {
        println(this.name)
        val jar = archiveFile.get().asFile
        jar.copyTo(File("/home/tom/Desktop/AlcMC/TestServer/plugins/", jar.name), true)
    }
}

tasks.processResources {
    val props = mapOf("version" to version)
    inputs.properties(props)
    filteringCharset = "UTF-8"
    filesMatching("plugin.yml") {
        expand(props)
    }
}

////tasks.processResources {
////    exclude("**/LICENSE*", "**/*.md", "**/NOTICE*", "**/*.kotlin_metadata")
////}
//
//tasks.shadowJar {
////    minimize()
//
//    // Optional: exclude metadata and signature files
//    exclude("META-INF/*.SF", "META-INF/*.DSA", "META-INF/*.RSA")
//}
