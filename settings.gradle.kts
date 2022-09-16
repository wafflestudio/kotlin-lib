rootProject.name = "slack-notif"

include(
    "slack-notification-spring",
    "slack-notification-spring-boot-starter",
    "samples:webmvc",
    "samples:webflux",
    "samples:webflux-functionalendpoint",
    "samples:corouter-functionalendpoint",
    "simple-corouter"
)
