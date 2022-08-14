import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.6.21"
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

group = "com.kraskaska.minecraft.plugins"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
    maven("https://jitpack.io")
}

dependencies {
    implementation("org.spigotmc:spigot-api:1.19.1-R0.1-SNAPSHOT")
    compileOnly("me.clip:placeholderapi:2.11.2")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.13.3")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.13.3")
    compileOnly("com.github.MilkBowl:VaultAPI:1.7")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

fun shell(cmd: String): Int {
    return ProcessBuilder().command(
        "cmd.exe",
        "/c",
        cmd
    )
        .redirectOutput(ProcessBuilder.Redirect.INHERIT).redirectError(ProcessBuilder.Redirect.INHERIT).start()
        .waitFor()
}

val copyToServer by tasks.registering {
    doLast {
        shell("scp .\\build\\libs\\${project.name}-${project.version}-all.jar azureuser@test-mc-azure:~/${project.name}.jar")
        shell("ssh azureuser@test-mc-azure sudo mv ~/${project.name}.jar /opt/test-mc/data/plugins/${project.name}.jar")
    }
}
copyToServer {
    dependsOn(tasks.withType<ShadowJar>())
}