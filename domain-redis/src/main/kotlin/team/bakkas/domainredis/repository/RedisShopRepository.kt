package team.bakkas.domainredis.repository

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import team.bakkas.domainredis.entity.RedisShop

/** RedisShop에 대한 repository. CrudRepository를 상속한다.
 * @author Brian
 * @since 22/06/09
 */
@Repository
interface RedisShopRepository: CrudRepository<RedisShop, String> {

}