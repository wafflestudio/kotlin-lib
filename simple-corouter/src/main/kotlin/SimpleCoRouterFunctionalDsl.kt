package io.wafflestudio.spring.corouter

import io.wafflestudio.spring.corouter.SimpleCoRouterDefinition.Method
import io.wafflestudio.spring.corouter.SimpleCoRouterDefinition.RequestInfo
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse

class SimpleCoRouterFunctionalDsl internal constructor(
    private val init: (SimpleCoRouterFunctionalDsl.() -> Unit),
) {

    private val routeDefinitions = mutableMapOf<RequestInfo, suspend (ServerRequest) -> ServerResponse>()

    fun GET(pattern: String, f: suspend (ServerRequest) -> ServerResponse) {
        routeDefinitions[RequestInfo(Method.GET, pattern)] = f
    }

    fun POST(pattern: String, f: suspend (ServerRequest) -> ServerResponse) {
        routeDefinitions[RequestInfo(Method.POST, pattern)] = f
    }

    fun PATCH(pattern: String, f: suspend (ServerRequest) -> ServerResponse) {
        routeDefinitions[RequestInfo(Method.PATCH, pattern)] = f
    }

    fun DELETE(pattern: String, f: suspend (ServerRequest) -> ServerResponse) {
        routeDefinitions[RequestInfo(Method.DELETE, pattern)] = f
    }

    internal fun build(): SimpleCoRouterDefinition = object : SimpleCoRouterDefinition {
        override fun routeRules(): Map<RequestInfo, suspend (ServerRequest) -> ServerResponse> {
            init()
            return routeDefinitions.toMap()
        }
    }
}

fun simpleCoRouter(init: (SimpleCoRouterFunctionalDsl.() -> Unit)): SimpleCoRouterDefinition =
    SimpleCoRouterFunctionalDsl(init).build()
