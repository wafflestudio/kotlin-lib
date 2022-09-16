package io.wafflestudio.spring.corouter

import io.wafflestudio.spring.corouter.SimpleCoRouterDefinition.Method
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.reactor.mono
import kotlinx.coroutines.withContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.HandlerFunction
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.RouterFunctions
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

@ComponentScan
@Configuration
internal class SimpleCoRouterFactory(
    private val resolvers: List<RequestContextResolver>,
    private val routeDefinitions: List<SimpleCoRouterDefinition>,
) {
    private val builder = RouterFunctions.route()

    @Bean
    fun build(): RouterFunction<ServerResponse> {
        routeDefinitions.flatMap { it.routeRules().entries }
            .forEach { (requestInfo, block) ->
                val (method, pattern) = requestInfo

                when (method) {
                    Method.GET -> builder.GET(pattern, asHandlerFunction(block))
                    Method.POST -> builder.POST(pattern, asHandlerFunction(block))
                    Method.PATCH -> builder.PATCH(pattern, asHandlerFunction(block))
                    Method.DELETE -> builder.DELETE(pattern, asHandlerFunction(block))
                }
            }

        return builder.build()
    }

    private fun asHandlerFunction(block: suspend (ServerRequest) -> ServerResponse) = HandlerFunction { request ->
        mono(Dispatchers.Unconfined) {
            val context = resolvers.map { it.resolveContext(request) }
                .fold(EmptyCoroutineContext as CoroutineContext) { acc, element ->
                    acc + element
                }

            withContext(context) {
                block(request)
            }
        }
    }
}
