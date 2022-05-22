package team.bakkas.common

sealed class Results {

    // 성공 여부만을 담당하는 클래스
    open class CommonResult(
        open var success: Boolean
    )

    // 단일 데이터에 대한 결과만을 처리하는 클래스
    data class SingleResult<T>(
        override var success: Boolean,
        var data: T
    ) : CommonResult(success)

    // 복수개의 데이터에 대한 결과를 처리하는 클래스
    data class MultipleResult<T>(
        override var success: Boolean,
        var data: List<T>
    ) : CommonResult(success)
}
