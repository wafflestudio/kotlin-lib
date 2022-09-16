package io.wafflestudio.spring.corouter

import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse

interface SimpleCoRouterDefinition {
    enum class Method {
        GET, POST, DELETE, PATCH
    }

    data class RequestInfo(
        val method: Method,
        val pattern: String,
    )

    fun routeRules(): Map<RequestInfo, suspend (ServerRequest) -> ServerResponse>
}
