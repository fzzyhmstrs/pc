pluginManagement {
    repositories {
        maven("https://maven.fabricmc.net") { name = "Fabric" }
        maven("https://maven.architectury.dev/")
        maven("https://files.minecraftforge.net/maven/")
        mavenCentral()
        gradlePluginPortal()
    }
    plugins {
        val loomVersion: String by settings
        id("dev.architectury.loom").version(loomVersion)
        val kotlinVersion: String by System.getProperties()
        kotlin("jvm").version(kotlinVersion)
    }
}