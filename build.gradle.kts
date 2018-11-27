import org.gradle.api.tasks.testing.logging.TestLogEvent


allprojects {
    group = "io.github.amalhanaja"

    repositories {
        mavenCentral()
        jcenter()
        maven { url = uri("https://dl.bintray.com/kotlin/kotlin-eap/") }
        maven { url = uri("https://kotlin.bintray.com/ktor") }
        maven { url = uri("https://dl.bintray.com/kotlin/exposed") }
        maven { url = uri("https://kotlin.bintray.com/kotlin-js-wrappers") }
        maven { url = uri("https://kotlin.bintray.com/kotlinx") }
    }

    tasks.withType(Test::class.java) {
        testLogging {
            showStandardStreams = true
            events = setOf(TestLogEvent.PASSED, TestLogEvent.FAILED)
        }
    }
}

subprojects {
    buildscript {

        repositories {
            mavenCentral()
            jcenter()
            maven { url = uri("https://dl.bintray.com/kotlin/kotlin-eap/") }
            maven { url = uri("https://kotlin.bintray.com/ktor") }
            maven { url = uri("https://dl.bintray.com/kotlin/exposed") }
            maven { url = uri("https://kotlin.bintray.com/kotlin-js-wrappers/") }
            maven { url = uri("https://kotlin.bintray.com/kotlinx") }

        }

        dependencies {
            classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.Kotlin}")
            classpath("org.jetbrains.kotlin:kotlin-frontend-plugin:${Versions.KotlinFrontend}")
            classpath("org.jetbrains.kotlin:kotlin-serialization:${Versions.Kotlin}")
        }
    }
}