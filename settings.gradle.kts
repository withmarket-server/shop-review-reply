rootProject.name = "yumarket"

include(
    ":application-query",
    ":domain-rds",
    ":domain-dynamo",
    ":domain-queryservice",
    ":common",
    ":client-mobilequery"
)