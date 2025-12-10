plugins {
    kotlin("jvm") version "2.1.10" apply false
}

group = "org.ivcode"
version = "0.1-SNAPSHOT"


allprojects {
    group = rootProject.group
    version = rootProject.version
}

subprojects {
    apply(plugin = "org.jetbrains.kotlin.jvm")

    repositories {
        mavenCentral()
    }

    dependencies {
        add("implementation", kotlin("stdlib"))
    }
}
