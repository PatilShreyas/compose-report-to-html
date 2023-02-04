val GROUP: String by project
val POM_NAME: String by project
val VERSION_NAME: String by project
val POM_DESCRIPTION: String by project

plugins {
    `kotlin-dsl`
    `java-gradle-plugin`
    alias(libs.plugins.gradle.plugin.publish)
}

repositories {
    mavenCentral()
    google()
    gradlePluginPortal()
    maven(url = "https://plugins.gradle.org/m2/")
}

group = GROUP
version = VERSION_NAME

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

gradlePlugin {
    website.set("https://github.com/PatilShreyas/compose-report-to-html")
    vcsUrl.set("https://github.com/PatilShreyas/compose-report-to-html")
    plugins {
        create("reportGenPlugin") {
            id = "dev.shreyaspatil.compose-compiler-report-generator"
            displayName = POM_NAME
            description = POM_DESCRIPTION
            implementationClass = "dev.shreyaspatil.composeCompilerMetricsGenerator.plugin.ReportPlugin"
            tags.set(listOf("android", "compose", "report", "jetpackcompose", "composecompiler"))
        }
    }
}
