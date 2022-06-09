rootProject.name = "yumarket"

include(
    ":application-query",
    ":domain-dynamo",
    ":domain-redis",
    ":domain-queryservice",
    ":common",
    ":client-mobilequery"
)