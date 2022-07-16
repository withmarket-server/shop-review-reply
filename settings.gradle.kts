rootProject.name = "yumarket"

include(
    ":application-query",
    ":domain-dynamo",
    ":domain-redis",
    ":domain-service",
    ":common",
    ":client-mobilequery"
)