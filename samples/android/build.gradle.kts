plugins {
    kotlin("android")
    alias(libs.plugins.android.application)
    id("dev.shreyaspatil.compose-compiler-report-generator")
}

android {
    namespace = "dev.shreyaspatil.composeCompilerMetricsGenerator.samples.android"
    compileSdk = 34

    buildFeatures {
        buildConfig = false
        compose = true
    }

    defaultConfig {
        applicationId = "dev.shreyaspatil.composeCompilerMetricsGenerator.samples.android"
        minSdk = 24
        targetSdk = 34
        versionName = "1.0"
        versionCode = 1
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_17.toString()
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.14"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
    }

    packaging {
        resources {
            excludes += listOf("META-INF/*")
        }
    }
}

dependencies {
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.compose.bom))
    implementation(libs.ui)
    implementation(libs.foundation)
    implementation(libs.material3)
}
