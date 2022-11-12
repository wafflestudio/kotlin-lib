package io.wafflestudio.spring.slack.samples

import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.info.Info
import io.wafflestudio.spring.corouter.RequestContext
import io.wafflestudio.spring.corouter.RequestContextResolver
import io.wafflestudio.spring.corouter.RequestGetParams
import io.wafflestudio.spring.corouter.simpleCoRouter
import kotlinx.coroutines.currentCoroutineContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.bodyValueAndAwait
import org.springframework.web.reactive.function.server.buildAndAwait
import org.springframework.web.reactive.function.server.coRouter
import java.net.URI
import kotlin.coroutines.CoroutineContext

@OpenAPIDefinition(
    info = Info(
        title = "simple-corouter API",
        version = "v1"
    )
)
@Configuration
class SimpleCoRouterConfig {

    @Bean
    fun indexRouter() = coRouter {
        GET("/") { temporaryRedirect(URI("/swagger-ui.html")).buildAndAwait() }
    }

    @Bean
    fun router1() = simpleCoRouter {
        GET("/user", ::handleRequest, UserParams::class)
    }

    suspend fun handleRequest(request: ServerRequest): ServerResponse {
        UserContext.getOrNull()?.also(::println)
        UserParams.getOrNull()?.also(::println)

        return ServerResponse.ok().bodyValueAndAwait(Unit)
    }

    @Component
    class UserContextResolver : RequestContextResolver {
        override suspend fun resolveContext(serverRequest: ServerRequest): UserContext = UserContext(1)
    }

    data class UserContext(
        val id: Long,
    ) : RequestContext {
        override val key: CoroutineContext.Key<*> get() = UserContext

        companion object : CoroutineContext.Key<UserContext> {
            suspend fun getOrNull(): UserContext? = currentCoroutineContext()[this]
        }
    }

    data class UserParams(
        val id: Long,
        val name: String?,
    ) : RequestGetParams() {
        override val key: CoroutineContext.Key<*> = UserParams

        companion object : CoroutineContext.Key<UserParams> {
            suspend fun getOrNull(): UserParams? = currentCoroutineContext()[this]
        }
    }
}
