pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        maven { url = uri("https://s3.us-west-2.amazonaws.com/maven.ivcode.org/snapshot/") }
    }
}

rootProject.name = "aimo"

//include(":ui")
//include(":ollama")
//include(":server")
include(":aimo-core")
//include(":aimo-core-starter")
include(":aimo-ollama")
