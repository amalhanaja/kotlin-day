import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    application
    kotlin("jvm") version Versions.Kotlin
    id("com.github.johnrengelman.shadow") version Versions.Shadowjar
}

application {
    mainClassName = "io.ktor.server.netty.EngineMain"
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation(Libs.KtorNetty)
    implementation(Libs.Logback)
    implementation(Libs.KtorMetrics)
    implementation(Libs.KtorCore)
    implementation(Libs.KtorHostCommon)
    implementation(Libs.KtorGson)
    implementation(Libs.JodaTime)
    testImplementation(Libs.Junit)
    testImplementation(Libs.KtorTest)
    testImplementation(Libs.Mockk)
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}