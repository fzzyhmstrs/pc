import com.matthewprenger.cursegradle.CurseArtifact
import com.matthewprenger.cursegradle.CurseProject
import com.matthewprenger.cursegradle.CurseRelation
import com.matthewprenger.cursegradle.Options
import org.jetbrains.kotlin.cli.common.toBooleanLenient
import java.net.URI

plugins {
    id("dev.architectury.loom")
    val kotlinVersion: String by System.getProperties()
    kotlin("jvm").version(kotlinVersion)
    id("com.modrinth.minotaur") version "2.+"
    id("com.matthewprenger.cursegradle") version "1.4.0"
}
base {
    val archivesBaseName: String by project
    archivesName.set(archivesBaseName)
}
val log: File = file("changelog.md")
val modVersion: String by project
version = modVersion
val mavenGroup: String by project
group = mavenGroup
println("## Changelog for ${base.archivesName.get()} $modVersion \n\n" + log.readText())
println(base.archivesName.get().replace('_','-'))
repositories {
    maven {
        name = "FallenBreath"
        url = uri("https://maven.fallenbreath.me/releases")
    }
    maven {
        name = "Modrinth"
        url = uri("https://api.modrinth.com/maven")
        content {
            includeGroup("maven.modrinth")
        }
    }
    maven {
        url = URI("https://maven.neoforged.net/releases")
    }
    maven {
        url = URI("https://thedarkcolour.github.io/KotlinForForge/")
    }
    maven {
        name = "Jitpack"
        url = uri("https://jitpack.io")
    }
    mavenCentral()
}
dependencies {
    val guavaVersion: String by project
    implementation("com.google.guava:guava:$guavaVersion")
    val minecraftVersion: String by project
    minecraft("com.mojang:minecraft:$minecraftVersion")
    val yarnMappings: String by project
    val yarnMappingsPatchVersion: String by project
    mappings( loom.layered {
        mappings("net.fabricmc:yarn:$yarnMappings:v2")
        mappings("dev.architectury:yarn-mappings-patch-neoforge:$yarnMappingsPatchVersion")
    })
    val loaderVersion: String by project
    neoForge("net.neoforged:neoforge:$loaderVersion")

    val kotlinForForgeVersion: String by project
    modImplementation("thedarkcolour:kotlinforforge-neoforge:$kotlinForForgeVersion")

    val fzzyConfigVersion: String by project
    modImplementation("maven.modrinth:fzzy-config:$fzzyConfigVersion") {
        exclude("net.fabricmc.fabric-api")
    }

    val cmVersion: String by project
    implementation("com.github.Fallen-Breath.conditional-mixin:conditional-mixin-neoforge:$cmVersion")
    include("com.github.Fallen-Breath.conditional-mixin:conditional-mixin-neoforge:$cmVersion")

    runtimeOnly("net.peanuuutz.tomlkt:tomlkt:0.3.7")
    runtimeOnly("blue.endless:jankson:1.2.3")
}

tasks {
    val javaVersion = JavaVersion.VERSION_21
    withType<JavaCompile> {
        options.encoding = "UTF-8"
        sourceCompatibility = javaVersion.toString()
        targetCompatibility = javaVersion.toString()
        options.release.set(javaVersion.toString().toInt())
    }
    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions { jvmTarget = javaVersion.toString() }
    }

    jar {
        from("LICENSE") { rename { "${base.archivesName.get()}_${it}" } }
    }
    jar {
        from( "credits.txt") { rename { "${base.archivesName.get()}_${it}" } }
    }

    processResources {
        inputs.property("version", project.version)
        filesMatching("META-INF/neoforge.mods.toml") {
            expand(mutableMapOf(
                "version" to project.version)
            )
        }
    }
    java {
        toolchain { languageVersion.set(JavaLanguageVersion.of(javaVersion.toString())) }
        sourceCompatibility = javaVersion
        targetCompatibility = javaVersion
        //withSourcesJar()
    }

    modrinth.get().group = "upload"
    modrinthSyncBody.get().group = "upload"
}

if (System.getenv("MODRINTH_TOKEN") != null) {
    modrinth {
        val releaseType: String by project
        val mcVersions: String by project
        val uploadDebugMode: String by project
        val modrinthSlugName: String by project

        token.set(System.getenv("MODRINTH_TOKEN"))
        projectId.set(modrinthSlugName)
        versionNumber.set(modVersion)
        versionName.set("${base.archivesName.get()}-$modVersion")
        versionType.set(releaseType)
        uploadFile.set(tasks.remapJar.get())
        gameVersions.addAll(mcVersions.split(","))
        loaders.addAll("neoforge")
        detectLoaders.set(false)
        changelog.set(log.readText())
        dependencies {
            required.project("kotlin-for-forge")
            required.project("fzzy-config")
        }
        debugMode.set(uploadDebugMode.toBooleanLenient() ?: true)
    }
}

if (System.getenv("CURSEFORGE_TOKEN") != null) {
    curseforge {
        val releaseType: String by project
        val mcVersions: String by project
        val uploadDebugMode: String by project

        apiKey = System.getenv("CURSEFORGE_TOKEN")
        project(closureOf<CurseProject> {
            id = "985426"
            changelog = log
            changelogType = "markdown"
            this.releaseType = releaseType
            for (ver in mcVersions.split(",")) {
                addGameVersion(ver)
            }
            addGameVersion("NeoForge")
            mainArtifact(tasks.remapJar.get().archiveFile.get(), closureOf<CurseArtifact> {
                displayName = "${base.archivesName.get()}-$modVersion"
                relations(closureOf<CurseRelation> {
                    this.requiredDependency("kotlin-for-forge")
                    this.requiredDependency("fzzy-config")
                })
            })
            relations(closureOf<CurseRelation> {
                this.requiredDependency("kotlin-for-forge")
                this.requiredDependency("fzzy-config")
            })
        })
        options(closureOf<Options> {
            javaIntegration = false
            forgeGradleIntegration = false
            javaVersionAutoDetect = false
            debug = uploadDebugMode.toBooleanLenient() ?: true
        })
    }
}

tasks.register("uploadAll") {
    group = "upload"
    dependsOn(tasks.modrinth.get())
    dependsOn(tasks.curseforge.get())
}