///////////////////////////////////////////////////////////////////////////
// Constants
///////////////////////////////////////////////////////////////////////////

val mainClass = "ru.foxesworld.foxxey.FoxxeyKt"

///////////////////////////////////////////////////////////////////////////
// Versions
///////////////////////////////////////////////////////////////////////////

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
    testImplementation(platform("org.junit:junit-bom:$junitBom"))
    testImplementation("org.junit.jupiter", "junit-jupiter")
    testImplementation(koin("test-junit5"))
    testImplementation("org.mockito", "mockito-core", mockito)
}

///////////////////////////////////////////////////////////////////////////
// Tasks
///////////////////////////////////////////////////////////////////////////

tasks.jar {
    manifest {
        attributes["Main-Class"] = mainClass
    }
}

///////////////////////////////////////////////////////////////////////////
// Helpers for beautify previous code
///////////////////////////////////////////////////////////////////////////

fun kotlinCoroutines(part: String) = "org.jetbrains.kotlinx:kotlinx-coroutines-$part:$kotlinCoroutines"

fun hoplite(part: String) = "com.sksamuel.hoplite:hoplite-$part:$hoplite"

fun koin(part: String) = "io.insert-koin:koin-$part:$koin"
