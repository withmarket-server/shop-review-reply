package team.bakkas.common.category

/** Shop에 대한 세부 분류를 담당하는 enum class
 * @author Brian
 * @since 22/06/02
 */
enum class DetailCategory(val title: String) {
    /* ==================== [Town Market] ====================*/
    FOOD("식/음료"),
    MART("편의점/마트"),
    SERVICE("서비스업종"),
    FASHION("패션의류"),
    ACCESSORY("패션잡화"),

    /* ==================== [Food] ====================*/
    JOKBAL_BOSSAM("족발/보쌈"),
    CUTLET_JAPAN("돈가스.일식"),
    CAFE_BREAD("카페/빵집"),
    DESSERT("디저트"),
    FAST_FOOD("패스트푸드"),
    CHINA_FOOD("중국집"),
    KOREAN_FOOD("한식"),
    PIZZA("피자"),
    FOOD_EXTRA("기타 음식"),

    /* ==================== [Mart] ====================*/
    SNACK_BREAD("과자/빵"),
    BEVERAGE_COFFEE_MILK_PRODUCT("음료/커피/유제품"),
    INSTANT_COOK("즉석조리식품"),
    WASHING_PRODUCT("세탁용품"),
    SUPER_MARKET("슈퍼마켓"),
    MART_EXTRA("기타 용품"),

    /* ==================== [Service] ====================*/
    HAIR_SHOP("헤어샵/미용실"),
    MASSAGE_AND_SKIN_CARE("마사지/피부관리"),
    STUDY_CAFE("스터디카페"),
    SERVICE_EXTRA("기타 업종"),

    /* ==================== [Fashion] ====================*/
    MAN_TOP("남성상의"),
    MAN_PANTS("남성하의"),
    WOMAN_TOP("여성상의"),
    WOMAN_PANTS("여성하의"),
    FASHION_EXTRA("기타 의류"),

    /* ==================== [Accessory] ====================*/
    SHOES("신발"),
    MAN_BAG("남성가방"),
    WOMAN_BAG("여성가방"),
    EARRINGS("귀걸이"),
    ACCESSORY_EXTRA("기타 잡화"),

    /* ==================== [ETC] ====================*/
    ETC_ALL("전체");
}