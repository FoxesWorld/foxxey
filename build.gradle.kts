import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent

///////////////////////////////////////////////////////////////////////////
// Constants
///////////////////////////////////////////////////////////////////////////

val mainClass = "ru.foxesworld.foxxey.FoxxeyKt"

///////////////////////////////////////////////////////////////////////////
// Versions
///////////////////////////////////////////////////////////////////////////

val mockk = "1.12.2"
val kotest = "5.1.0"
val kotlinCoroutines = "1.6.0"
val hoplite = "1.4.16"
val koin = "3.1.5"
val mockito = "4.3.1"
val junitBom = "5.8.2"
val logback = "1.2.10"
val kotlinLogging = "2.1.21"
val serializationJson = "1.3.2"

///////////////////////////////////////////////////////////////////////////
// Settings
///////////////////////////////////////////////////////////////////////////

plugins {
    kotlin("jvm") version "1.6.10"
    kotlin("plugin.serialization") version "1.6.10"
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

group = "ru.foxesworld"
version = "1.0-SNAPSHOT"

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

tasks.jar {
    manifest {
        attributes["Main-Class"] = mainClass
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

fun kotest(part: String) = "io.kotest:kotest-$part:$kotest"

fun kotlinCoroutines(part: String) = "org.jetbrains.kotlinx:kotlinx-coroutines-$part:$kotlinCoroutines"

fun hoplite(part: String) = "com.sksamuel.hoplite:hoplite-$part:$hoplite"

fun koin(part: String) = "io.insert-koin:koin-$part:$koin"
