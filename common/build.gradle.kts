dependencies {
    implementation(kotlin("stdlib"))

    //testImplementation("org.junit.jupiter:junit-jupiter-api")
}

tasks.test {
    useJUnitPlatform()
}

