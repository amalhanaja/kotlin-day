import com.bmuschko.gradle.docker.tasks.container.DockerCreateContainer
import com.bmuschko.gradle.docker.tasks.container.DockerStartContainer
import com.bmuschko.gradle.docker.tasks.container.DockerStopContainer
import com.bmuschko.gradle.docker.tasks.image.DockerBuildImage
import com.bmuschko.gradle.docker.tasks.image.Dockerfile
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.kotlin.gradle.frontend.util.frontendExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

version = "1.0.0"

plugins {
    application
    kotlin("jvm") version Versions.Kotlin
    id("com.github.johnrengelman.shadow") version Versions.Shadowjar
    id("com.bmuschko.docker-remote-api") version Versions.DockerRemoteAPI
}

application {
    mainClassName = "io.ktor.server.netty.EngineMain"
    applicationName = "kotlin-day-backend"
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
    implementation(Libs.Exposed)
    implementation(Libs.H2Database)
    testImplementation(Libs.Junit)
    testImplementation(Libs.KtorTest)
    testImplementation(Libs.Mockk)
    testImplementation(Libs.H2Database)
}

tasks {
    withType<KotlinCompile> {
        kotlinOptions.jvmTarget = "1.8"
    }

    withType<ShadowJar> {
        classifier = "all"
    }

    val createDockerFile: Dockerfile by creating(Dockerfile::class) {
        val appJar = "${rootProject.name}-${project.name}.jar"
        from("adoptopenjdk/openjdk8-openj9:latest")
        destFile.set(file("${project.buildDir.path}/Dockerfile"))
        label(mapOf("maintainer" to """Alfian Akmal Hanantio 'amalhanaja@gmail.com'"""))
        volume("/app")
        environmentVariable("PORT", port)
        environmentVariable("DATABASE_URL", System.getenv("DATABASE_URL"))
        environmentVariable("DATABASE_DRIVER", System.getenv("DATABASE_DRIVER"))
        System.getenv("DATABASE_USER")?.also { environmentVariable("DATABASE_USER", it) }
        System.getenv("DATABASE_PASSWORD")?.also { environmentVariable("DATABASE_PASSWORD", it) }
        copyFile("./libs/${project.name}-$version-all.jar", appJar)
        exposePort(port.toInt())
        entryPoint(
            "java", "-server", "-XX:+UnlockExperimentalVMOptions", "-XX:+UseCGroupMemoryLimitForHeap",
            "-XX:InitialRAMFraction=2", "-XX:MinRAMFraction=2", "-XX:MaxRAMFraction=2",
            "-XX:+UseG1GC", "-XX:MaxGCPauseMillis=100", "-XX:+UseStringDeduplication",
            "-XX:+IdleTuningGcOnIdle", "-Xmx128m", "-Xscmx128m",
            "-Xtune:virtualized", "-Xscmaxaot100m", "-Xscmaxaot100m", "-Xscmaxaot100m",
            "-jar", appJar
        )
    }

    val buildDockerImage: DockerBuildImage by creating(DockerBuildImage::class) {
        dependsOn(createDockerFile)
        inputDir.set(createDockerFile.destFile.get().asFile.parentFile)
        tag.set("io.github.amalhanaja/${rootProject.name}-${project.name}:$version")
    }

    val createDockerContainer: DockerCreateContainer by creating(DockerCreateContainer::class) {
        dependsOn(buildDockerImage)
        targetImageId(buildDockerImage.imageId)
        portBindings.set(listOf("$port:$port"))
        memory.set(160_000_000) // 160MB
        autoRemove.set(true)
    }

    val runDockerContainer: DockerStartContainer by creating(DockerStartContainer::class) {
        dependsOn(createDockerContainer)
        targetContainerId(createDockerContainer.containerId)
    }

    val stopDockerContainer: DockerStopContainer by creating(DockerStopContainer::class) {
        targetContainerId(createDockerContainer.containerId)
    }
}

val port: String get() = System.getenv("PORT") ?: "8080"