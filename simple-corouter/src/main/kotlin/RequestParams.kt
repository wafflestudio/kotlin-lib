package io.wafflestudio.spring.corouter

import kotlin.coroutines.CoroutineContext
import kotlin.reflect.KClass
import kotlin.reflect.full.createType
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.full.withNullability

sealed class RequestParams : CoroutineContext.Element

abstract class RequestGetParams : RequestParams()

internal fun KClass<out RequestGetParams>.accept(builder: org.springdoc.core.fn.builders.operation.Builder) {
    builder.apply {
        primaryConstructor?.parameters?.forEach {
            parameter(
                org.springdoc.core.fn.builders.parameter.Builder.parameterBuilder()
                    .required(!it.type.isMarkedNullable)
                    .name(it.name)
                    .schema(
                        org.springdoc.core.fn.builders.schema.Builder.schemaBuilder()
                            .type(
                                when (it.type.withNullability(false)) {
                                    String::class.createType() -> "string"
                                    Int::class.createType(), Long::class.createType() -> "integer"
                                    Boolean::class.createType() -> "boolean"
                                    else -> error("")
                                }
                            )
                    )
            )
        }
    }
}
