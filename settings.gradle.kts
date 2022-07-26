rootProject.name = "yumarket"

include(
    ":application-query",
    ":application-command",
    ":domain-dynamo",
    ":domain-redis",
    ":domain-shopquery",
    ":domain-shopcommand",
    ":common",
    ":client-query",
    ":client-command",
    ":client-kafka"
)