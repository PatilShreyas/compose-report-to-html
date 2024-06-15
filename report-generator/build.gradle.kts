plugins {
    kotlin("multiplatform")
    id(libs.plugins.mavenPublish.get().pluginId)
}

kotlin {
    applyDefaultHierarchyTemplate()

    jvm()

    sourceSets {
        commonMain.dependencies {
            implementation(project(":core"))
            implementation(kotlin("stdlib"))

            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kotlinx.html.jvm)
        }
    }
}
