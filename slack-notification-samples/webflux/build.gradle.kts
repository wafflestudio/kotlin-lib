dependencies {
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation(project(":slack-notification-spring-boot-starter"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
}
