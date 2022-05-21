plugins {
    kotlin("jvm")
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
