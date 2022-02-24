import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent

///////////////////////////////////////////////////////////////////////////
// Constants
///////////////////////////////////////////////////////////////////////////

val mainClassJvmName = "ru.foxesworld.foxxey.LauncherKt"

///////////////////////////////////////////////////////////////////////////
// Versions
///////////////////////////////////////////////////////////////////////////

val mockk = "1.12.2"
val kotest = "5.1.0"
val kotlinCoroutines = "1.6.0"
val hoplite = "1.4.16"
val koin = "3.1.5"
val junitBom = "5.8.2"
val logback = "1.2.10"
val kotlinLogging = "2.1.21"
val serializationJson = "1.3.2"

///////////////////////////////////////////////////////////////////////////
// Settings
///////////////////////////////////////////////////////////////////////////

plugins {
    kotlin("jvm") version "1.6.20-RC"
    kotlin("plugin.serialization") version "1.6.10"
    id("com.github.johnrengelman.shadow") version "7.1.2"
    application
}

group = "ru.foxesworld"
version = "1.0-SNAPSHOT"

application  {
    mainClass.set(mainClassJvmName)
}

repositories {
    mavenCentral()
    maven("https://us-central1-maven.pkg.dev/varabyte-repos/public")
}

///////////////////////////////////////////////////////////////////////////
// Dependencies
///////////////////////////////////////////////////////////////////////////

dependencies {
    // Kotlin
    implementation(kotlin("reflect"))
    implementation(kotlinCoroutines("core"))

    // Config
    implementation(hoplite("core"))
    implementation(hoplite("json"))

    // Serialization
    implementation("org.jetbrains.kotlinx", "kotlinx-serialization-json", serializationJson)

    // DI
    implementation(koin("core"))

    // Logging
    implementation("ch.qos.logback", "logback-classic", logback)
    implementation("io.github.microutils", "kotlin-logging", kotlinLogging)

    // Testing
    testImplementation(kotlinCoroutines("test"))
    testImplementation(koin("test-junit5"))
    testImplementation(kotest("runner-junit5-jvm"))
    testImplementation(kotest("assertions-core-jvm"))
    testImplementation(kotest("property-jvm"))
    testImplementation("io.mockk", "mockk", mockk)
}

///////////////////////////////////////////////////////////////////////////
// Tasks
///////////////////////////////////////////////////////////////////////////

tasks.create("createBuildInfoResource") {
    group = "build"
    File("${buildDir.mainResourcesDir.path}/config", "build-info.json").apply {
        parentFile.apply {
            if (!exists()) {
                mkdirs()
            }
        }
        if (!exists()) {
            createNewFile()
        }
        writeText("{\"v\":\"$version\"}")
    }
}

tasks.jar {
    dependsOn("createBuildInfoResource")
    manifest {
        attributes["Main-Class"] = mainClassJvmName
    }
}

tasks.test {
    useJUnitPlatform()
    testLogging {
        showExceptions = true
        showStandardStreams = true
        events = setOf(
            TestLogEvent.FAILED,
            TestLogEvent.PASSED
        )
        exceptionFormat = TestExceptionFormat.FULL
    }
}

///////////////////////////////////////////////////////////////////////////
// Helpers for beautify previous code
///////////////////////////////////////////////////////////////////////////

val File.mainResourcesDir: File
    get() = File(this, "resources/main/")

fun kotest(part: String) = "io.kotest:kotest-$part:$kotest"

fun kotlinCoroutines(part: String) = "org.jetbrains.kotlinx:kotlinx-coroutines-$part:$kotlinCoroutines"

fun hoplite(part: String) = "com.sksamuel.hoplite:hoplite-$part:$hoplite"

fun koin(part: String) = "io.insert-koin:koin-$part:$koin"
