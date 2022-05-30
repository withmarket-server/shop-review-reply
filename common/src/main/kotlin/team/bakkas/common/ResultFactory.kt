package team.bakkas.common

/**
 * Result를 반환하는 메소드들을 모아둔 Factory class
 * @author Brian
 * @since 22/05/22
 */
object ResultFactory {

    // 성공에 대한 결과를 리턴하는 메소드
    fun getSuccessResult(): Results.CommonResult = Results.CommonResult(true)

    // 단일 데이터를 가지는 성공 결과를 반환하는 메소드
    fun <T> getSingleResult(data: T): Results.SingleResult<T> = Results.SingleResult(
        success = true,
        data = data
    )

    // 리스트 형태의 데이터를 가지는 성공 결과를 반환하는 메소드
    fun <T> getMultipleResult(data: List<T>): Results.MultipleResult<T> = Results.MultipleResult(
        success = true,
        data = data
    )

    // TODO Exception에 대한 Result를 뱉어주는 함수 정의
}