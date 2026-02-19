plugins {
    kotlin("jvm") version "2.1.10" apply false
}

group = "org.ivcode"
version = "0.1-SNAPSHOT"


allprojects {
    group = rootProject.group
    version = rootProject.version
}

val jacksonVersion = "2.15.2"

val defaultVersions = mapOf(
    // central map of group:artifact -> version
    // populate this from your version catalog or BOM as needed
    "org.ivcode:spring-boot-starter-ollama" to "0.1-SNAPSHOT",
    "com.fasterxml.jackson.module:jackson-module-kotlin" to jacksonVersion,
    "com.fasterxml.jackson.core:jackson-databind" to jacksonVersion,
    "com.fasterxml.jackson.core:jackson-core" to jacksonVersion,
    "com.fasterxml.jackson.core:jackson-annotations" to jacksonVersion,
)

subprojects {
    apply(plugin = "org.jetbrains.kotlin.jvm")

    repositories {
        mavenCentral()
        maven { url = uri("https://s3.us-west-2.amazonaws.com/maven.ivcode.org/snapshot/") }
    }

    dependencies {
        add("implementation", kotlin("stdlib"))
    }

    // Apply default versions to dependencies without specified versions
    configurations.all {
        resolutionStrategy.eachDependency(object : Action<DependencyResolveDetails> {
            override fun execute(details: DependencyResolveDetails) {
                val requested = details.requested
                val group = requested.group
                val name = requested.name
                val version = requested.version ?: ""

                if (group.isNotBlank() && name.isNotBlank() && version.isBlank()) {
                    val coord = "$group:$name"
                    defaultVersions[coord]?.let { details.useVersion(it) }
                }
            }
        })
    }
}

tasks.register("classes") {
    dependsOn(subprojects.map { it.tasks.named("classes") })
}

tasks.register("testClasses") {
    dependsOn(subprojects.map { it.tasks.named("testClasses") })
}