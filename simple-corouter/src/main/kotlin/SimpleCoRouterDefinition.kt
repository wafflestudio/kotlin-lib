package io.wafflestudio.spring.corouter

import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import kotlin.reflect.KClass

interface SimpleCoRouterDefinition {
    enum class Method {
        GET, POST, DELETE, PATCH
    }

    data class RequestInfo(
        val method: Method,
        val pattern: String,
        val param: KClass<out RequestParams>? = null
    )

    fun routeRules(): Map<RequestInfo, suspend (ServerRequest) -> ServerResponse>
}
