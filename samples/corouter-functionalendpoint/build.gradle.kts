dependencies {
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation(project(":slack-notification-spring-boot-starter"))
    implementation(project(":simple-corouter"))

    val springdocVersion = "1.5.12"
    implementation("org.springdoc:springdoc-openapi-webflux-ui:$springdocVersion")
    implementation("org.springdoc:springdoc-openapi-kotlin:$springdocVersion")
}
