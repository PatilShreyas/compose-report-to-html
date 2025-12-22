val VERSION_NAME: String by project
val GROUP: String by project

plugins {
    kotlin("jvm") version libs.versions.kotlin.get()
    alias(libs.plugins.spotless)
    alias(libs.plugins.mavenPublish) apply false
}

group = GROUP
version = VERSION_NAME

kotlin {
    jvmToolchain(libs.versions.java.get().toInt())
}

subprojects {
    apply(plugin = "com.diffplug.spotless")
    configure<com.diffplug.gradle.spotless.SpotlessExtension> {
        kotlin {
            target("**/*.kt")
            targetExclude("${layout.buildDirectory}/**/*.kt")
            targetExclude("bin/**/*.kt")

            ktlint()
            licenseHeaderFile(rootProject.file("spotless/copyright.kt"))
        }
    }
}
