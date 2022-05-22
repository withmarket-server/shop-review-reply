package team.bakkas.domaindynamo.entity

import java.time.LocalDateTime

open class BaseEntity(
    open var createdAt: LocalDateTime,
    open var updatedAt: LocalDateTime?
)