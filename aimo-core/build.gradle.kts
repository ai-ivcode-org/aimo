plugins {
    kotlin("jvm") version "2.1.10"
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

    implementation(kotlin("reflect"))

    testImplementation(kotlin("test"))
    testImplementation("org.junit.jupiter:junit-jupiter-api")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}