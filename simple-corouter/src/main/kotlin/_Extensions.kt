package io.wafflestudio.spring.corouter

import org.springframework.web.reactive.function.server.ServerRequest

internal fun ServerRequest.getParamString(name: String, type: RequestParameterType = RequestParameterType.QUERY) =
    when (type) {
        RequestParameterType.QUERY -> queryParam(name).run {
            if (isPresent) {
                get()
            } else {
                null
            }
        }

        RequestParameterType.PATH -> pathVariable(name)

        RequestParameterType.HEADER -> headers().firstHeader(name)
    }

internal fun ServerRequest.getParamInt(name: String, type: RequestParameterType) =
    getParamString(name, type)?.toIntOrNull()

internal fun ServerRequest.getParamLong(name: String, type: RequestParameterType) =
    getParamString(name, type)?.toLongOrNull()

internal fun ServerRequest.getParamBoolean(name: String, type: RequestParameterType) =
    getParamString(name, type)?.toBooleanStrictOrNull()
