plugins {
    kotlin("plugin.spring") version "2.1.10"
    id("io.spring.dependency-management") version "1.1.2"
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.boot:spring-boot-dependencies:3.2.0")
    }
}


dependencies {
    implementation(project(":common"))

    // spring annotations
    implementation("org.springframework:spring-context")
    implementation("org.springframework.boot:spring-boot-autoconfigure")

    // jackson
    implementation("com.fasterxml.jackson.core:jackson-databind")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

    // ehcache
    api("org.ehcache:ehcache")
}


tasks.test {
    useJUnitPlatform()
}

