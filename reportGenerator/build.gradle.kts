plugins {
    kotlin("jvm")
    id(libs.plugins.mavenPublish.get().pluginId)
}
repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":core"))
    implementation(kotlin("stdlib"))

    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.html.jvm)
}
