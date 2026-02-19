plugins {
    kotlin("jvm") version "2.1.10"
    kotlin("plugin.spring") version "2.1.10"
    id("org.ivcode.gradle-publish") version "0.1-SNAPSHOT"
    id("java-library")
}

group = "org.ivcode"
version = "0.1-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    // SLF4J Logging
    api("org.slf4j:slf4j-api:2.0.9")
    runtimeOnly("org.slf4j:slf4j-simple:2.0.9")

    api(project(":aimo-core"))

    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("com.fasterxml.jackson.core:jackson-databind")
    implementation("com.fasterxml.jackson.core:jackson-core")
    implementation("com.fasterxml.jackson.core:jackson-annotations")

    testImplementation(kotlin("test"))
    testImplementation("org.junit.jupiter:junit-jupiter-api")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}