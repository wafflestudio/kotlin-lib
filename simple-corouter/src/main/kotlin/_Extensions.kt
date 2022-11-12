package io.wafflestudio.spring.corouter

import org.springframework.web.reactive.function.server.ServerRequest

internal fun ServerRequest.getParamString(name: String) = queryParam(name).run {
    if (isPresent) {
        get()
    } else {
        null
    }
}

internal fun ServerRequest.getParamInt(name: String) = getParamString(name)?.toIntOrNull()

internal fun ServerRequest.getParamLong(name: String) = getParamString(name)?.toLongOrNull()

internal fun ServerRequest.getParamBoolean(name: String) = getParamString(name)?.toBooleanStrictOrNull()
