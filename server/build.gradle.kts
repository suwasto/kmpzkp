plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.ktor)
    application
}

group = "io.github.suwasto.zkpsample"
version = "1.0.0"
application {
    mainClass.set("io.github.suwasto.zkpsample.ApplicationKt")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=${extra["io.ktor.development"] ?: "false"}")
}

dependencies {
    implementation(projects.shared)
    implementation(projects.zkpschnoorproofs)
    implementation(libs.logback)
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.netty)
    implementation(project(":zkpschnoorproofs"))
    testImplementation(libs.kotlin.test.junit)
}