package team.bakkas.elasticsearch.repository


import org.springframework.data.elasticsearch.repository.ReactiveElasticsearchRepository
import team.bakkas.elasticsearch.entity.SearchShop

/**
 * @author Brian
 * @since 2022/11/06
 */
interface ShopSearchRepository: ReactiveElasticsearchRepository<SearchShop, String> {

}