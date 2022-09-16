package io.wafflestudio.spring.slack.samples

import io.wafflestudio.spring.corouter.RequestContextResolver
import io.wafflestudio.spring.corouter.simpleCoRouter
import kotlinx.coroutines.currentCoroutineContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.bodyValueAndAwait
import kotlin.coroutines.CoroutineContext

@Configuration
class SimpleCoRouterConfig {

    @Bean
    fun router1() = simpleCoRouter {
        GET("/user", ::handleRequest)
        POST("/user", ::handleRequest)
    }

    @Bean
    fun router2() = simpleCoRouter {
        DELETE("/user2", ::handleRequest)
        PATCH("/user2", ::handleRequest)
    }

    suspend fun handleRequest(request: ServerRequest): ServerResponse {
        val userContext = UserContext.getOrNull()

        return ServerResponse.ok().bodyValueAndAwait(userContext?.id ?: 0)
    }

    @Component
    class UserContextResolver : RequestContextResolver {
        override suspend fun resolveContext(serverRequest: ServerRequest): UserContext = UserContext(1)
    }

    data class UserContext(
        val id: Long,
    ) : CoroutineContext.Element {
        override val key: CoroutineContext.Key<*> get() = UserContext

        companion object : CoroutineContext.Key<UserContext> {
            suspend fun getOrNull(): UserContext? = requireNotNull(currentCoroutineContext()[this])
        }
    }
}
