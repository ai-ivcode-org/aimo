plugins {
    kotlin("plugin.spring") version "2.1.10"
    id("org.springframework.boot") version "3.2.0"
    id("io.spring.dependency-management") version "1.1.2"
    id("org.ivcode.gradle-publish") version "0.1-SNAPSHOT"
}

dependencies {
    implementation(kotlin("stdlib"))

    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")

    implementation("org.ivcode:spring-boot-starter-ollama:0.1-SNAPSHOT")

    // TODO remove and move out to a addon cache module
    implementation("org.ehcache:ehcache:3.11.1")

    implementation(project(":plugin-api"))
    implementation(project(":common"))

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.junit.jupiter:junit-jupiter-api")
}

tasks.test {
    useJUnitPlatform()
}

tasks.named("test") {
    dependsOn(":test:test-plugin:build")
}
