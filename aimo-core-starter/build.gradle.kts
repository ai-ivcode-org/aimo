plugins {
    kotlin("jvm") version "2.1.10"
    kotlin("plugin.spring") version "2.1.10"
    id("org.springframework.boot") version "3.2.2"
    id("io.spring.dependency-management") version "1.1.4"
    id("org.ivcode.gradle-publish") version "0.1-SNAPSHOT"
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

    implementation("org.springframework.boot:spring-boot-starter")

    api(project(":aimo-core"))

    testImplementation(kotlin("test"))
    testImplementation("org.junit.jupiter:junit-jupiter-api")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}