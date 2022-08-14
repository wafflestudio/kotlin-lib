package io.wafflestudio.spring.slack

interface SlackClient {
    fun isEnabled(): Boolean
    fun captureEvent(e: SlackEvent)
}
