dependencies {
    compileOnly("org.springframework.boot:spring-boot-starter-web")
    compileOnly("org.springframework.boot:spring-boot-starter-webflux")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
    implementation(project(":slack-notification-spring"))
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
