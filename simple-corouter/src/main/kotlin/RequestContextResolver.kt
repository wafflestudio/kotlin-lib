package io.wafflestudio.spring.corouter

import org.springframework.web.reactive.function.server.ServerRequest

interface RequestContextResolver {
    suspend fun resolveContext(serverRequest: ServerRequest): RequestContext?
}
