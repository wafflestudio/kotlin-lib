package io.wafflestudio.spring.slack

data class SlackClientOptions(
    val token: String,
    val channelName: String,
    val maxQueueSize: Int,
    val enabled: Boolean
)
