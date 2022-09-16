rootProject.name = "slack-notif"

include(
    "slack-notification-spring",
    "slack-notification-spring-boot-starter",
    "slack-notification-samples:webmvc",
    "slack-notification-samples:webflux",
    "slack-notification-samples:webflux-functionalendpoint",
    "simple-corouter"
)
