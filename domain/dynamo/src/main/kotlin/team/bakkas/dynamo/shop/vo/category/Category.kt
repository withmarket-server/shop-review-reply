package team.bakkas.dynamo.shop.vo.category

/** Shop의 대분류에 대한 enum class
 * @author Brian
 * @since 22/06/02
 */
enum class Category(val title: String) {
    FOOD_BEVERAGE("식/음료"),
    MART("편의점/마트"),
    SERVICE("서비스업종"),
    FASHION("패션의류"),
    ACCESSORY("패션잡화"),
    ETC("그외/마켓");
}