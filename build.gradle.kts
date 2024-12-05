plugins {
    kotlin("jvm") version "2.1.0"
    id("io.papermc.paperweight.userdev") version "1.7.1"
    id("net.minecrell.plugin-yml.bukkit") version "0.6.0"
    id("com.gradleup.shadow") version "8.3.4"
    id("xyz.jpenilla.run-paper") version "2.3.0"
}

group = "dev.kingrabbit"
version = "1.0"

repositories {
    mavenCentral()
    maven("https://jitpack.io")
    maven("https://repo.flyte.gg/releases")
}

dependencies {
    paperweight.paperDevBundle("1.21.3-R0.1-SNAPSHOT")

    implementation("gg.flyte:twilight:1.1.16")

    implementation("io.github.revxrsal:lamp.common:4.0.0-beta.19")
    implementation("io.github.revxrsal:lamp.bukkit:4.0.0-beta.19")
    implementation("io.github.revxrsal:lamp.brigadier:4.0.0-beta.19")
}

val targetJavaVersion = 21
kotlin {
    compilerOptions {
        javaParameters = true
    }

    jvmToolchain(targetJavaVersion)
}

tasks {
    shadowJar {
        minimize()
    }
    build {
        dependsOn(shadowJar)
    }
}

bukkit {
    main = "dev.kingrabbit.punishmentManager.PunishmentManager"
    apiVersion = "1.21"

    name = getName()
    description = "A plugin to handle a variety of punishments."
    version = getVersion().toString()
    author = "KingsDev"
    website = "https://kingrabbit.dev/"
}
