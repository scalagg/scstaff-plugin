import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    java
    `maven-publish`
    kotlin("jvm") version "1.9.20"
    kotlin("kapt") version "1.9.20"
    id("com.github.johnrengelman.shadow") version "7.1.2"
    id("org.ajoberstar.grgit") version "4.1.1"
}

var currentBranch: String = grgit.branch.current().name

allprojects {
    group = "gg.scala.staff"
    version = "1.1.0${
        if (currentBranch == "develop") "-dev" else ""
    }"

    repositories {
        mavenCentral()
        maven(url = "https://jitpack.io")
        configureScalaRepository()
    }
}

dependencies {
    compileOnly(kotlin("stdlib"))
    api(fileTree("lib"))

    kapt("gg.scala.commons:bukkit:3.4.5")
    compileOnly("gg.scala.commons:bukkit:3.4.5")

    api("com.github.Karhu-Anticheat:KarhuAPI:eb5d50297e")

    compileOnly("gg.scala.store:spigot:0.1.8")
    compileOnly("gg.scala.basics.plugin:scala-basics-plugin:1.0.5")
    compileOnly("gg.scala.spigot:server:1.1.0")

    compileOnly("gg.scala.lemon:bukkit:1.9.1")
    compileOnly("gg.scala.cloudsync:spigot:1.0.1")
}

kotlin {
    jvmToolchain(jdkVersion = 17)
}

tasks.withType<ShadowJar> {
    archiveClassifier.set("")
    exclude(
        "**/*.kotlin_metadata",
        "**/*.kotlin_builtins",
        "META-INF/"
    )

    archiveFileName.set(
        "scstaff-plugin.jar"
    )
}

tasks.withType<JavaCompile> {
    options.compilerArgs.add("-parameters")
    options.fork()
    options.encoding = "UTF-8"
}

tasks.withType<KotlinCompile> {
    kotlinOptions.javaParameters = true
    kotlinOptions.jvmTarget = "17"
}

publishing {
    repositories.configureScalaRepository(
        dev = currentBranch == "develop"
    )

    publications {
        register(
            name = "mavenJava",
            type = MavenPublication::class,
            configurationAction = shadow::component
        )
    }
}

tasks.getByName("build")
    .dependsOn(
        "shadowJar",
        "publishMavenJavaPublicationToScalaRepository"
    )

fun RepositoryHandler.configureScalaRepository(dev: Boolean = false)
{
    maven("${property("artifactory_contextUrl")}/gradle-${if (dev) "dev" else "release"}") {
        name = "scala"
        credentials {
            username = property("artifactory_user").toString()
            password = property("artifactory_password").toString()
        }
    }
}
