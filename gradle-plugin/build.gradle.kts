val GROUP: String by project
val POM_NAME: String by project
val VERSION_NAME: String by project
val POM_DESCRIPTION: String by project

plugins {
    `kotlin-dsl`
    `java-gradle-plugin`
    alias(libs.plugins.gradle.plugin.publish)
}

group = GROUP
version = VERSION_NAME

dependencies {
    compileOnly(gradleApi())
    compileOnly(kotlin("stdlib"))
    compileOnly(libs.kotlin.gradle.plugin)
    compileOnly(libs.android.gradle.plugin)
    compileOnly(libs.compose.multiplatform.gradle.plugin)

    implementation(project(":core"))
    implementation(project(":report-generator"))
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
            implementationClass = "dev.shreyaspatil.composeCompilerMetricsGenerator.plugin.ReportGenPlugin"
            tags.set(listOf("android", "compose", "report", "jetpackcompose", "composecompiler"))
        }
    }
}
