repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation(project(":plugin-api"))

    //testImplementation("org.junit.jupiter:junit-jupiter-api")
}

tasks.named<Jar>("jar") {
    // set output directory relative to the project root
    destinationDirectory.set(file((layout.buildDirectory.dir("plugins/tool"))))

    // optional: customize the produced jar name
    archiveFileName.set("test-plugin.jar")
}

tasks.test {
    useJUnitPlatform()
}
