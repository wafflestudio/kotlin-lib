dependencies {
    api("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
    compileOnly("org.springframework.boot:spring-boot-starter-webflux")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")

    val springdocVersion = "1.5.12"
    implementation("org.springdoc:springdoc-openapi-webflux-ui:$springdocVersion")
    implementation("org.springdoc:springdoc-openapi-kotlin:$springdocVersion")
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "${project.rootProject.group}"
            artifactId = "${project.name}"
            version = "${project.rootProject.version}"

            from(components["java"])
        }
    }
}
