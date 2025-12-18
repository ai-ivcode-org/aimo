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
        maven { url = uri("https://s3.us-west-2.amazonaws.com/maven.ivcode.org/snapshot/") }
    }

    dependencies {
        add("implementation", kotlin("stdlib"))
    }
}
