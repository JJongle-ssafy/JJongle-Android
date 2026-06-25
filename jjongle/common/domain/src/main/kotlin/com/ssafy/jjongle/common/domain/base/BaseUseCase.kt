package com.ssafy.jjongle.common.domain.base

import com.ssafy.jjongle.common.domain.error.HttpResponseException
import com.ssafy.jjongle.common.domain.error.isCommonErrorHandling
import com.ssafy.jjongle.common.domain.helper.MessageHelper
import com.ssafy.jjongle.common.domain.helper.NavigationHelper
import com.ssafy.jjongle.common.domain.helper.ResourceHelper
import com.ssafy.jjongle.tti.TTIHelper

/**
 * BaseUseCase 비즈니스 시나리오를 실행하는 유스케이스입니다.
 *
 * - 계층: common/domain
 * - 책임: ViewModel이 필요한 domain 작업을 단일 진입점으로 제공합니다.
 */
abstract class BaseUseCase(
    protected val resourceHelper: ResourceHelper,
    protected val messageHelper: MessageHelper,
    protected val navigationHelper: NavigationHelper,
    protected val ttiHelper: TTIHelper,
) {
    /**
     * 공통으로 처리할 HTTP 에러를 사용자 메시지로 변환합니다.
     *
     * feature 고유 에러는 각 UseCase에서 별도로 처리하고, 401/404/5xx처럼 앱 전반에서
     * 같은 UX를 가져야 하는 에러만 이 함수에서 처리합니다.
     */
    protected fun executeCommonErrorHanding(error: HttpResponseException) {
        if (!error.isCommonErrorHandling()) return

        val message = when (error.rawCode) {
            401 -> "세션이 만료되었습니다."
            404 -> "지원하지 않는 기능입니다."
            else -> "잠시 후 다시 시도해주세요."
        }
        messageHelper.showOneButtonDialog(descText = message)
    }

    /**
     * 이미 만들어진 [Result]에 공통 HTTP 에러 처리를 적용한 뒤 원본 [Result]를 그대로 반환합니다.
     */
    protected fun <T> Result<T>.handleCommonHttpFailure(): Result<T> {
        val error = exceptionOrNull() as? HttpResponseException
        if (error != null) {
            executeCommonErrorHanding(error)
        }
        return this
    }

    /**
     * suspend 블록을 실행하고, HTTP 에러는 공통 UX 처리 후 [Result.failure]로 감쌉니다.
     *
     * 단순 조회 UseCase에서 try/catch 반복을 줄일 때 사용합니다.
     */
    protected suspend fun <T> executeWithCommonHttpHandling(block: suspend () -> T): Result<T> {
        return try {
            Result.success(block())
        } catch (e: HttpResponseException) {
            executeCommonErrorHanding(e)
            Result.failure(e)
        } catch (e: Throwable) {
            Result.failure(e)
        }
    }
}
