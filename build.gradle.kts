import com.moowork.gradle.node.npm.NpmTask
import org.jetbrains.kotlin.gradle.frontend.Bundler
import org.jetbrains.kotlin.gradle.frontend.Dependency
import org.jetbrains.kotlin.gradle.frontend.KotlinFrontendExtension
import org.jetbrains.kotlin.gradle.frontend.PackageManager
import org.jetbrains.kotlin.gradle.frontend.config.BundleConfig
import org.jetbrains.kotlin.gradle.frontend.util.frontendExtension
import org.jetbrains.kotlin.gradle.frontend.util.nodePath
import org.jetbrains.kotlin.gradle.frontend.util.startWithRedirectOnFail
import org.jetbrains.kotlin.gradle.frontend.webpack.WebPackExtension
import org.jetbrains.kotlin.gradle.tasks.Kotlin2JsCompile

buildscript {
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.2.60")
        classpath("com.moowork.gradle:gradle-node-plugin:1.2.0")
        classpath("org.jetbrains.kotlin:kotlin-frontend-plugin:0.0.36")
    }
}

plugins {
    id("kotlin2js").version("1.2.60")
    id("com.moowork.node").version("1.2.0")
    id("org.jetbrains.kotlin.frontend").version("0.0.36")
    java
}

group = "com.dreamloom.kfep"
version = "1.0-SNAPSHOT"

repositories {
    jcenter()
    mavenLocal()
    mavenCentral()
}

dependencies {
    compile(kotlin("stdlib-js"))
}

configure<KotlinFrontendExtension> {
    downloadNodeJsVersion = "latest"
    sourceMaps = true
    define("PRODUCTION", true)

    bundler("browserify", BrowserifyBundler)
    bundle("browserify", delegateClosureOf<BrowserifyExtension> {})
}

tasks {
    "compileKotlin2Js"(Kotlin2JsCompile::class) {
        kotlinOptions {
            metaInfo = true
            outputFile = "${project.buildDir.path}/js/${project.name}.js"
            sourceMap = true
            sourceMapEmbedSources = "always"
            moduleKind = "umd"
        }
    }
}

object BrowserifyBundler : Bundler<BrowserifyExtension> {
    override val bundlerId = "browserify"

    override fun createConfig(project: Project) = BrowserifyExtension(project)

    override fun apply(project: Project,
                       packageManager: PackageManager,
                       packagesTask: Task,
                       bundleTask: Task,
                       runTask: Task,
                       stopTask: Task) {
        packageManager.require(listOf("browserify").map {
            Dependency(it, "*", Dependency.DevelopmentScope)
        })

        if (project.frontendExtension.sourceMaps) {
            packageManager.require("source-map-loader")
        }

        val browserify = project.tasks.create("browserify-browserify",
                BrowserifyBundleTask::class.java) {
            description = "Bundles all scripts with Browserify"
            group = "browserify"
        }
    }

    override fun outputFiles(project: Project): FileCollection {
        return listOf(
                project.tasks.withType(BrowserifyBundleTask::class.java).map { it.outputs.files }
        ).flatten()
                .takeIf { it.isNotEmpty() }
                ?.reduce { a, b -> a + b } ?: project.files()
    }
}

open class BrowserifyExtension(project: Project) : BundleConfig {
    @get:Input
    override val bundlerId: String
        get() = "browserify"

    @Input
    override var bundleName = project.name

    @Input
    override var sourceMapEnabled: Boolean = project.frontendExtension.sourceMaps
}

open class BrowserifyBundleTask : DefaultTask() {
    @get:OutputDirectory
    val bundleDir = project.frontendExtension.bundlesDirectory

    @TaskAction
    fun buildBundle() {
        println("no-op for demonstrating bug")
    }
}


