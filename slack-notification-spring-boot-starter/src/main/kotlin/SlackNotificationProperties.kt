package io.wafflestudio.spring.slack

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConfigurationProperties("slack.notification")
@ConstructorBinding
data class SlackNotificationProperties(
    val token: String,
    val channelName: String,
    val queueSize: Int = 50,
    val enabled: Boolean = true,
)
