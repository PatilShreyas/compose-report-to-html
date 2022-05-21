plugins {
    kotlin("jvm")
    application
}
val mainCliClassName = "dev.shreyaspatil.composeCompilerMetricsGenerator.cli.MainKt"
application {
    mainClass.set(mainCliClassName)
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":core"))
    implementation(project(":reportGenerator"))

    implementation(kotlin("stdlib"))
    implementation(libs.kotlinx.cli)
}

tasks.withType<Jar>() {
    manifest {
        attributes["Main-Class"] = mainCliClassName
    }
    val dependencies = configurations
        .runtimeClasspath
        .get()
        .map(::zipTree)
    from(dependencies)
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}
