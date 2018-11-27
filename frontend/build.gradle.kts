import org.jetbrains.kotlin.cli.common.repl.KotlinJsr223JvmScriptEngineBase
import org.jetbrains.kotlin.gradle.frontend.FrontendPlugin
import org.jetbrains.kotlin.gradle.frontend.KotlinFrontendExtension
import org.jetbrains.kotlin.gradle.frontend.npm.NpmExtension
import org.jetbrains.kotlin.gradle.frontend.util.frontendExtension
import org.jetbrains.kotlin.gradle.frontend.webpack.*
import org.jetbrains.kotlin.gradle.tasks.Kotlin2JsCompile
import org.jetbrains.kotlin.gradle.tasks.KotlinJsDce
import org.jetbrains.kotlin.js.JavaScript

plugins {
    id("kotlin2js") version Versions.Kotlin
}

apply(plugin = "org.jetbrains.kotlin.frontend")
apply(plugin = "kotlin-dce-js")
apply(plugin = "kotlinx-serialization")

configure<KotlinFrontendExtension> {
    downloadNodeJsVersion = "10.13.0"
    sourceMaps = true
    version = "1.0.0"
    configure<NpmExtension> {
        dependency("webpack-cli", "v2.0.12")
        dependency("react", "16.6.0")
        dependency("react-dom", "16.6.0")
        dependency("react-router-dom", "4.3.1")
        dependency("antd", "3.10.8")
    }
    bundle<WebPackExtension>("webpack") {
        if (this is WebPackExtension) {
            bundleName = "main"
            contentPath = file("src/main/resources")
            proxyUrl = "http://localhost:8000"
        }
    }
}


dependencies {
    implementation(kotlin("stdlib-js"))
    implementation(Libs.KotlinxHtmlJs) { isForce = true }
    implementation(Libs.KotlinReactWrapper)
    implementation(Libs.KotlinStyledWrapper)
    implementation(Libs.KotlinReactDomWrapper)
    implementation(Libs.KotlinReactRouterDomWrapper)
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-runtime-js:0.9.0")
}

tasks {
    "compileKotlin2Js"(Kotlin2JsCompile::class) {
        kotlinOptions {
            metaInfo = true
            outputFile = "${project.buildDir.path}/js/${project.name}.js"
            sourceMap = true
            moduleKind = "commonjs"
            main = "call"
        }
    }
}
