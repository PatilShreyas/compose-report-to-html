import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val VERSION_NAME: String by project
val GROUP: String by project

plugins {
    kotlin("jvm") version "1.7.20"
    alias(libs.plugins.spotless)
    alias(libs.plugins.mavenPublish) apply false
}

group = GROUP
version = VERSION_NAME

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