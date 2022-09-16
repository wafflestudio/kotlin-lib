package io.wafflestudio.spring.slack.samples

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class ExceptionController {

    @GetMapping("/test")
    fun test() {
        error("This is test.")
    }
}
