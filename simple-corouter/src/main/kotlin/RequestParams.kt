package io.wafflestudio.spring.corouter

import io.swagger.v3.oas.annotations.enums.ParameterIn
import kotlin.coroutines.CoroutineContext
import kotlin.reflect.KClass
import kotlin.reflect.full.createType
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.full.withNullability

sealed class RequestParams : CoroutineContext.Element

abstract class RequestGetParams : RequestParams()

enum class RequestParameterType {
    QUERY, PATH, HEADER
}

internal fun KClass<out RequestGetParams>.accept(builder: org.springdoc.core.fn.builders.operation.Builder) {
    val annotations = members.filter { it.annotations.isNotEmpty() }
        .associate { it.name to it.annotations }

    builder.apply {
        primaryConstructor?.parameters?.forEach {
            parameter(
                org.springdoc.core.fn.builders.parameter.Builder.parameterBuilder()
                    .name(it.name)
                    .required(!it.type.isMarkedNullable)
                    .`in`(
                        when {
                            annotations[it.name]?.any { it.annotationClass == RequestHeader::class }
                                ?: false -> ParameterIn.HEADER

                            annotations[it.name]?.any { it.annotationClass == RequestPath::class }
                                ?: false -> ParameterIn.PATH

                            else -> ParameterIn.QUERY
                        }
                    )
                    .schema(
                        org.springdoc.core.fn.builders.schema.Builder.schemaBuilder()
                            .type(
                                when (it.type.withNullability(false)) {
                                    String::class.createType() -> "string"
                                    Int::class.createType(), Long::class.createType() -> "integer"
                                    Boolean::class.createType() -> "boolean"
                                    else -> throw InvalidDefinitionException("${it.type} is not supported.")
                                }
                            )
                    )
            )
        }
    }
}
