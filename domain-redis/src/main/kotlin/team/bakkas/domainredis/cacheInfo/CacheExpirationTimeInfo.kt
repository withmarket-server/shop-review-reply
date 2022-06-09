package team.bakkas.domainredis.cacheInfo

object CacheExpirationTimeInfo {
    val DEFAULT_EXPIRATION_SEC: Long = 90 // 1 hour

    val ZONE_EXPIRATION_SEC: Long = 1800 // 30 minute

    val SHOP_LIST_EXPIRATION_SEC: Long = 30 // 30 second

    val SHOP_REVIEW_LIST_EXPIRATION_SEC: Long = 30 // 30 second
}