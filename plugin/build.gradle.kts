plugins {
    `kotlin-dsl`
    id("java-gradle-plugin")
    id(libs.plugins.mavenPublish.get().pluginId)
}

repositories {
    mavenCentral()
    google()
    gradlePluginPortal()
    maven(url = "https://plugins.gradle.org/m2/")
}

dependencies {
    implementation(gradleApi())
    implementation(libs.kotlin.gradle.plugin)
    implementation(libs.android.gradle.plugin)

    implementation(project(":core"))
    implementation(project(":reportGenerator"))
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}