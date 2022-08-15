package io.wafflestudio.spring.slack

import com.slack.api.Slack
import org.slf4j.LoggerFactory
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Future
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadFactory
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

class AsyncSlackClient(
    private val options: SlackClientOptions,
) : SlackClient {

    private val logger = LoggerFactory.getLogger(javaClass)

    private val runner = QueuedThreadExecutor(maxQueueSize = options.maxQueueSize)
    private val slack = Slack.getInstance()

    override fun isEnabled() = options.enabled

    override fun captureEvent(e: SlackEvent) {
        if (isEnabled()) {
            runner.submit { doSend(e) }
        }
    }

    private fun doSend(e: SlackEvent) {
        // TODO
        slack.methods(options.token)
            .filesUpload { builder ->
                builder.channels(listOf(options.channelName))
                    .filetype("text")
                    .filename("error.txt")
                    .content(e.message)
                    .initialComment(e.title)
            }
    }

    class QueuedThreadExecutor : ThreadPoolExecutor {
        private val logger = LoggerFactory.getLogger(javaClass)

        private val maxQueueSize: Int
        private val leftTaskCnt = AtomicInteger()

        constructor(maxQueueSize: Int) : super(
            1,
            maxQueueSize,
            0L,
            TimeUnit.MILLISECONDS,
            LinkedBlockingQueue(),
            DaemonThreadFactory()
        ) {
            this.maxQueueSize = maxQueueSize
        }

        override fun submit(task: Runnable): Future<*> {
            logger.debug("task sumitted. queue size : ${leftTaskCnt.get()}")

            if (leftTaskCnt.get() > maxQueueSize) {
                logger.warn("job can't be assigned since job queue is full.")
                return CompletableFuture.completedFuture(Unit)
            }

            taskCount.inc()

            return super.submit(task)
        }

        override fun afterExecute(r: Runnable?, t: Throwable?) {
            taskCount.dec()
            super.afterExecute(r, t)
        }

        private class DaemonThreadFactory : ThreadFactory {
            private var cnt = 0

            override fun newThread(r: Runnable): Thread {

                val ret = Thread(r, "SlackAsyncConnection-" + cnt++)
                ret.isDaemon = true
                return ret
            }
        }
    }
}
