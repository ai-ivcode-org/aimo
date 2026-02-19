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
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")


    implementation("org.ehcache:ehcache")
    implementation("org.springframework.boot:spring-boot-starter-actuator")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.junit.jupiter:junit-jupiter-api")

    implementation(project(":ui"))
    implementation(project(":ollama"))
    implementation(project(":aimo-core"))
}

tasks.test {
    useJUnitPlatform()
}
