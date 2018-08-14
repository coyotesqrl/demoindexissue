pluginManagement {
    repositories {
        gradlePluginPortal()
        maven { setUrl("https://jcenter.bintray.com/") }
        maven { setUrl("https://dl.bintray.com/kotlin/kotlin-eap") }
    }
    resolutionStrategy {
        eachPlugin {
            when (requested.id.id) {
                "org.jetbrains.kotlin.frontend" ->
                    useModule("org.jetbrains.kotlin:kotlin-frontend-plugin:${requested.version}")
                "kotlin2js" ->
                    useModule("org.jetbrains.kotlin:kotlin-gradle-plugin:${requested.version}")
            }
        }
    }
}
rootProject.name = "demoindexissue"

