import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val libraryVersion: String by project
val libraryGroup: String by project

plugins {
    kotlin("jvm") version "1.7.0"
    alias(libs.plugins.spotless)
}

group = libraryGroup
version = libraryVersion

repositories {
    mavenCentral()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

subprojects {
    apply(plugin = "com.diffplug.spotless")
    configure<com.diffplug.gradle.spotless.SpotlessExtension> {
        kotlin {
            target("**/*.kt")
            targetExclude("$buildDir/**/*.kt")
            targetExclude("bin/**/*.kt")

            ktlint()
            licenseHeaderFile(rootProject.file("spotless/copyright.kt"))
        }
        kotlinGradle {
            target("*.gradle.kts")
            ktlint()
        }
    }
}