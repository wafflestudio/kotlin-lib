dependencies {
    compileOnly("org.springframework.boot:spring-boot-starter-web")
    compileOnly("org.springframework.boot:spring-boot-starter-webflux")
    implementation("com.slack.api:slack-api-client:1.24.0")
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
