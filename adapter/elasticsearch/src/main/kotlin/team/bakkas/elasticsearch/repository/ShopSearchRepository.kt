package team.bakkas.elasticsearch.repository


import org.springframework.data.elasticsearch.repository.ReactiveElasticsearchRepository
import team.bakkas.elasticsearch.entity.SearchShop

/**
 * ShopSearchRepository
 * elasticsearch에서 shop을 persist하기 위해 이용되는 interface
 * @author Brian
 * @since 2022/11/06
 */
interface ShopSearchRepository: ReactiveElasticsearchRepository<SearchShop, String> {

}