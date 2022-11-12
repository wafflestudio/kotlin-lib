package io.wafflestudio.spring.corouter

import io.wafflestudio.spring.corouter.SimpleCoRouterDefinition.Method
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.reactor.mono
import kotlinx.coroutines.withContext
import org.springdoc.webflux.core.fn.SpringdocRouteBuilder
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.HandlerFunction
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.RouterFunctions
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.bodyValueAndAwait
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.reflect.KClass
import kotlin.reflect.full.createType
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.full.withNullability

@ComponentScan
@Configuration
internal class SimpleCoRouterFactory(
    private val resolvers: List<RequestContextResolver>,
    private val routeDefinitions: List<SimpleCoRouterDefinition>,
) {
    private val docBuilder = SpringdocRouteBuilder.route()

    @Suppress("UNCHECKED_CAST")
    @ConditionalOnBean(SimpleCoRouterDefinition::class)
    @Bean
    fun build(): RouterFunction<ServerResponse> {
        routeDefinitions.flatMap { it.routeRules().entries }
            .forEach { (requestInfo, block) ->
                val (method, pattern, params) = requestInfo

                require(params?.isData ?: true) // RequestParams should be data class

                when (method) {
                    Method.GET -> {
                        docBuilder.GET(pattern, asHandlerFunction(block, params)) {
                            it
                                .operationId(pattern)
                                .method(method.name)
                                .apply {
                                    if (params?.isSubclassOf(RequestGetParams::class) == true) {
                                        (params as KClass<RequestGetParams>).accept(this)
                                    }
                                }
                                .build()
                        }
                    }

                    Method.POST -> {
                        docBuilder.POST(pattern, asHandlerFunction(block)) {}
                    }

                    Method.PATCH -> {
                        docBuilder.PATCH(pattern, asHandlerFunction(block)) {}
                    }

                    Method.DELETE -> {
                        docBuilder.DELETE(pattern, asHandlerFunction(block)) {}
                    }
                }
            }

        return RouterFunctions.route()
            .add(docBuilder.build())
            .build()
    }

    private fun asHandlerFunction(
        block: suspend (ServerRequest) -> ServerResponse,
        params: KClass<out RequestParams>? = null,
    ) = HandlerFunction { request ->
        mono(Dispatchers.Unconfined) {
            runCatching {
                val context = resolvers.map { it.resolveContext(request) }
                    .fold(EmptyCoroutineContext as CoroutineContext) { acc, element ->
                        acc + element
                    }

                val paramsContext = params?.toCoroutineContext(request) ?: EmptyCoroutineContext

                withContext(context + paramsContext) {
                    block(request)
                }
            }.recoverCatching {
                when (it) {
                    is InvalidParameterException -> ServerResponse.status(400).bodyValueAndAwait(Unit)
                    else -> throw it
                }
            }.getOrThrow()
        }
    }

    private fun KClass<out RequestParams>.toCoroutineContext(request: ServerRequest): CoroutineContext =
        runCatching {
            primaryConstructor?.run {
                val args: List<Any?> = parameters.map {
                    if (it.name != null) {
                        when (it.type.withNullability(false)) {
                            String::class.createType() -> request.getParamString(it.name!!)
                            Int::class.createType() -> request.getParamInt(it.name!!)
                            Long::class.createType() -> request.getParamLong(it.name!!)
                            Boolean::class.createType() -> request.getParamBoolean(it.name!!)
                            else -> throw InvalidParameterException("Parameter ${it.type} in $name is not supported ")
                        }
                    } else {
                        null
                    }
                }

                call(*args.toTypedArray())
            } ?: EmptyCoroutineContext
        }.getOrElse {
            throw InvalidParameterException(it.message ?: "")
        }
}
