import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    java
    `maven-publish`
    kotlin("jvm") version "2.0.20"
    kotlin("kapt") version "2.0.20"
    id("io.github.goooler.shadow") version "8.1.8"
    id("org.ajoberstar.grgit") version "4.1.1"
}

var currentBranch: String = grgit.branch.current().name


allprojects {
    group = "gg.scala.staff"
    version = "1.2.2${
    if (currentBranch == "develop") "-dev" else ""
}"


    repositories {
        mavenLocal()
        mavenCentral()
        maven("https://repo.codemc.io/repository/maven-snapshots/")
        maven("https://jitpack.io")
        configureScalaRepository()
    }
}

dependencies {
    compileOnly(kotlin("stdlib"))

    kapt("gg.scala.commons:bukkit:4.0.7")
    kapt("gg.scala.store:shared:1.0.0")
    compileOnly("gg.scala.commons:bukkit:4.0.7")

    compileOnly("gg.scala.store:spigot:1.0.0")
    compileOnly("gg.scala.store:shared:1.0.0")
    compileOnly("gg.scala.basics.plugin:scala-basics-plugin:1.1.8")
    compileOnly("gg.scala.spigot:server:1.1.3")

    compileOnly("gg.scala.lemon:bukkit:2.0.1")
    compileOnly("gg.scala.cloudsync:spigot:1.0.5")
    compileOnly("gg.scala.aware:aware:2.0.0")
    compileOnly("org.mongodb:mongo-java-driver:3.12.11")
}

kotlin {
    jvmToolchain(jdkVersion = 21)
}

tasks.withType<ShadowJar> {
    archiveClassifier.set("")

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
    compilerOptions {
        javaParameters.set(true)
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21)
    }
}

publishing {
    repositories.configureScalaRepository(dev = currentBranch != "master")

    publications {
        register(
            name = "mavenJava",
            type = MavenPublication::class,
            configurationAction = shadow::component
        )
    }
}


fun RepositoryHandler.configureScalaRepository(dev: Boolean = false) {
    maven("${property("artifactory_contextUrl")}/gradle-${if (dev) "dev" else "release"}") {
        name = "scala"
        credentials {
            username = property("artifactory_user").toString()
            password = property("artifactory_password").toString()
        }
    }
}