plugins {
    kotlin("multiplatform")
    alias(libs.plugins.android.library)
    alias(libs.plugins.compose.multiplatform)
    id("dev.shreyaspatil.compose-compiler-report-generator")
}

kotlin {
    androidTarget {
        compilations.all {
            kotlinOptions {
                jvmTarget = "17"
            }
        }
    }
    jvm() {
        jvmToolchain(17)
    }

    sourceSets {
        commonMain.dependencies {
            implementation(compose.ui)
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.compiler.auto)
            implementation(compose.material3)
        }
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions.jvmTarget = "17"
}

android {
    namespace = "dev.shreyaspatil.composeCompilerReportGenerator.samples.multiplatform"
    compileSdk = 34

    defaultConfig {
        minSdk = 24
    }

    composeOptions {
        kotlinCompilerExtensionVersion = compose.dependencies.compiler.forKotlin(libs.versions.kotlin.get())
    }
}