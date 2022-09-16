package io.wafflestudio.spring.slack.samples

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.coRouter

@Configuration
class ExceptionRouterConfig {

    @Bean
    fun exceptionRouter() = coRouter {
        GET("/test") { _ -> error("This is test.") }
    }
}
