//enableFeaturePreview("GRADLE_METADATA")

rootProject.name = "kotlin-day"

pluginManagement {
    resolutionStrategy {
        eachPlugin {
            when (requested.id.id) {
                "kotlin2js" -> useModule("org.jetbrains.kotlin:kotlin-gradle-plugin:${requested.version}")
            }
        }
    }
}

include(":backend")
include(":frontend")