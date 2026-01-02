import com.matthewprenger.cursegradle.CurseArtifact
import com.matthewprenger.cursegradle.CurseProject
import com.matthewprenger.cursegradle.CurseRelation
import com.matthewprenger.cursegradle.Options
import org.jetbrains.kotlin.cli.common.toBooleanLenient

plugins {
    id("fabric-loom")
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
val minecraftVersion: String by project
version = "$modVersion+$minecraftVersion"
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
        name = "Jitpack"
        url = uri("https://jitpack.io")
    }
    maven {
        name = "FzzyMaven"
        url = uri("https://maven.fzzyhmstrs.me/")
    }
    mavenCentral()
}
dependencies {
    val guavaVersion: String by project
    implementation("com.google.guava:guava:$guavaVersion")
    val minecraftVersion: String by project
    minecraft("com.mojang:minecraft:$minecraftVersion")
    val yarnMappings: String by project
    mappings("net.fabricmc:yarn:$yarnMappings:v2")
    val loaderVersion: String by project
    modImplementation("net.fabricmc:fabric-loader:$loaderVersion")
    val fabricVersion: String by project
    modImplementation("net.fabricmc.fabric-api:fabric-api:$fabricVersion")
    val fabricKotlinVersion: String by project
    modImplementation("net.fabricmc:fabric-language-kotlin:$fabricKotlinVersion")

    val fzzyConfigVersion: String by project
    modImplementation("me.fzzyhmstrs:fzzy_config:$fzzyConfigVersion+$minecraftVersion"){
        exclude("net.fabricmc.fabric-api")
    }

    val cmVersion: String by project
    implementation("me.fallenbreath:conditional-mixin:$cmVersion")
    include("me.fallenbreath:conditional-mixin:$cmVersion")

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
        val loaderVersion: String by project
        val fabricKotlinVersion: String by project
        val fzzyConfigVersion: String by project
        inputs.property("version", project.version)
        inputs.property("id", base.archivesName.get())
        inputs.property("loaderVersion", loaderVersion)
        inputs.property("fabricKotlinVersion", fabricKotlinVersion)
        inputs.property("fzzyConfigVersion", fzzyConfigVersion)
        filesMatching("fabric.mod.json") {
            expand(mutableMapOf(
                "version" to project.version,
                "id" to base.archivesName.get(),
                "loaderVersion" to loaderVersion,
                "fabricKotlinVersion" to fabricKotlinVersion,
                "fzzyConfigVersion" to fzzyConfigVersion)
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
        versionNumber.set("$modVersion+$minecraftVersion")
        versionName.set("${base.archivesName.get()}-$modVersion+$minecraftVersion")
        versionType.set(releaseType)
        uploadFile.set(tasks.remapJar.get())
        gameVersions.addAll(mcVersions.split(","))
        loaders.addAll("fabric", "quilt")
        detectLoaders.set(false)
        changelog.set(log.readText())
        dependencies {
            required.project("fabric-api")
            required.project("fabric-language-kotlin")
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
            for (ver in mcVersions.split(",")){
                addGameVersion(ver)
            }
            addGameVersion("Fabric")
            addGameVersion("Quilt")
            mainArtifact(tasks.remapJar.get().archiveFile.get(), closureOf<CurseArtifact> {
                displayName = "${base.archivesName.get()}-$modVersion+$minecraftVersion"
                relations(closureOf<CurseRelation>{
                    this.requiredDependency("fabric-api")
                    this.requiredDependency("fabric-language-kotlin")
                    this.requiredDependency("fzzy-config")
                })
            })
            relations(closureOf<CurseRelation>{
                this.requiredDependency("fabric-api")
                this.requiredDependency("fabric-language-kotlin")
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