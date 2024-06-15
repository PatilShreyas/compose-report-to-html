plugins {
    kotlin("jvm")
    application
}
val mainCliClassName = "dev.shreyaspatil.composeCompilerMetricsGenerator.cli.MainKt"
application {
    mainClass.set(mainCliClassName)
}

dependencies {
    implementation(project(":core"))
    implementation(project(":report-generator"))

    implementation(kotlin("stdlib"))
    implementation(libs.okio)
    implementation(libs.kotlinx.cli)
}

tasks.withType<Jar> {
    manifest {
        attributes["Main-Class"] = mainCliClassName
    }
    val dependencies =
        configurations
            .runtimeClasspath
            .get()
            .map(::zipTree)
    from(dependencies)
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}
