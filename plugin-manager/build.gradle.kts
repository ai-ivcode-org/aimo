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
    implementation(kotlin("stdlib"))

    implementation("org.springframework:spring-context")
    implementation("org.springframework:spring-web")

    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")

    implementation(project(":plugin-api"))
    implementation(project(":common"))

    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
}


tasks.test {
    useJUnitPlatform()
}

tasks.named("test") {
    dependsOn(":test:test-plugin:build")
}
