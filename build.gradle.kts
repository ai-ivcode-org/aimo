plugins {
    kotlin("jvm") version "2.1.10"
    kotlin("plugin.spring") version "2.1.10"
    id("org.springframework.boot") version "3.2.0"
    id("io.spring.dependency-management") version "1.1.2"
    id("org.ivcode.gradle-publish") version "0.1-SNAPSHOT"
}

group = "org.ivcode"
version = "0.1-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))

    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")

    // TODO remove and move out to a addon cache module
    implementation("org.ehcache:ehcache:3.11.1")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
}
