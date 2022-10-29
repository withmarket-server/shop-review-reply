package team.bakkas.dynamo

import java.io.Serializable
import java.time.LocalDateTime

/** createdAt, updatedAt 정보를 저장하는 vo
 * @author Doyeop Kim
 * @since 2022/10/13
 */
open class BaseTimeEntity(
    open var createdAt: LocalDateTime = LocalDateTime.now(),
    open var deletedAt: LocalDateTime? = null
): Serializable {

}