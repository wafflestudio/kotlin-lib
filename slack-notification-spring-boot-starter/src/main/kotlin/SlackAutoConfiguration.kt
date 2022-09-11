package io.wafflestudio.spring.slack

import io.wafflestudio.spring.slack.webflux.SlackWebExceptionHandler
import io.wafflestudio.spring.slack.webmvc.SlackExceptionResolver
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@EnableConfigurationProperties(SlackNotificationProperties::class)
@ConditionalOnProperty(name = ["slack.notification.token", "slack.notification.channel_name"])
@Configuration
class SlackAutoConfiguration {

    @Bean
    fun slackClient(properties: SlackNotificationProperties): SlackClient {
        val options = SlackClientOptions(
            token = properties.token,
            channelName = properties.channelName,
            maxQueueSize = properties.queueSize,
            enabled = properties.enabled
        )

        return AsyncSlackClient(options)
    }

    @ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
    @Configuration
    class SlackWebMvcConfiguration(
        private val slackClient: SlackClient,
    ) {

        @Bean
        fun exceptionResolver(): SlackExceptionResolver {
            // TODO()
            return SlackExceptionResolver(slackClient)
        }
    }

    @ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
    @Configuration
    class SlackWebfluxConfiguration(
        private val slackClient: SlackClient,
    ) {

        @Bean
        fun exceptionHandler(): SlackWebExceptionHandler {
            // TODO
            return SlackWebExceptionHandler(slackClient)
        }
    }
}
