package io.wafflestudio.spring.slack

import org.springframework.util.MultiValueMap
import org.springframework.util.MultiValueMapAdapter
import org.springframework.web.server.ServerWebExchange
import javax.servlet.http.HttpServletRequest

class SlackEvent {
    val uri: String
    val method: String
    val headers: MultiValueMap<String, String>
    val params: MultiValueMap<String, String>
    val ex: Throwable

    // uri
    constructor(exchange: ServerWebExchange, ex: Throwable) {
        this.uri = exchange.request.uri.rawPath
        this.method = exchange.request.method?.name ?: ""
        this.headers = exchange.request.headers
        this.params = exchange.request.queryParams
        this.ex = ex
    }

    constructor(request: HttpServletRequest, ex: Exception) {
        this.uri = request.requestURI
        this.method = request.method
        this.headers = MultiValueMapAdapter(
            request.headerNames.asSequence().associateWith { request.getHeaders(it).toList() }
        )
        this.params = MultiValueMapAdapter(request.parameterMap.mapValues { it.value.toList() })
        this.ex = ex
    }

    val title: String get() = ex.stackTrace.first().toString()
    val message: String
        get() = StringBuilder().run {
            append(ex.javaClass.simpleName)
            append("\n")
            append(ex.stackTrace.first())
            append("\n\n")
            append("[$method] $uri")
            append("\n")
            append(params.toFormattedString())
            append("\n\n")
            append(headers.toFormattedString())
            append("\n\n")
            append(ex.stackTraceToString())
            toString()
        }

    private fun MultiValueMap<String, String>.toFormattedString() =
        map { "${it.key} : ${it.value.joinToString(",")}" }.joinToString("\n")
}
