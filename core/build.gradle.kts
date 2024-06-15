plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization") version libs.versions.kotlin.get()
    id(libs.plugins.mavenPublish.get().pluginId)
}

kotlin {
    applyDefaultHierarchyTemplate()

    jvm()

    sourceSets {
        commonMain.dependencies {
            implementation(libs.okio)
            implementation(kotlin("stdlib"))
            implementation(libs.kotlinx.serialization.json)
        }
    }
}
