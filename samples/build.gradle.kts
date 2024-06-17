plugins {
    kotlin("jvm") version libs.versions.kotlin.get() apply false
    kotlin("multiplatform") version libs.versions.kotlin.get() apply false
    kotlin("android") version libs.versions.kotlin.get() apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.compose.multiplatform) apply false
    id("dev.shreyaspatil.compose-compiler-report-generator") version libs.versions.project.get() apply false
}
