package com.ssafy.jjongle.common.domain.base

import com.ssafy.jjongle.common.domain.error.HttpResponseException
import com.ssafy.jjongle.common.domain.error.isCommonErrorHandling
import com.ssafy.jjongle.common.domain.helper.MessageHelper
import com.ssafy.jjongle.common.domain.helper.NavigationHelper
import com.ssafy.jjongle.common.domain.helper.ResourceHelper
import com.ssafy.jjongle.tti.TTIHelper

abstract class BaseUseCase(
    protected val resourceHelper: ResourceHelper,
    protected val messageHelper: MessageHelper,
    protected val navigationHelper: NavigationHelper,
    protected val ttiHelper: TTIHelper,
) {
    protected fun executeCommonErrorHanding(error: HttpResponseException) {
        if (!error.isCommonErrorHandling()) return

        val message = when (error.rawCode) {
            401 -> "세션이 만료되었습니다."
            404 -> "지원하지 않는 기능입니다."
            else -> "잠시 후 다시 시도해주세요."
        }
        messageHelper.showOneButtonDialog(descText = message)
    }

    protected fun <T> Result<T>.handleCommonHttpFailure(): Result<T> {
        val error = exceptionOrNull() as? HttpResponseException
        if (error != null) {
            executeCommonErrorHanding(error)
        }
        return this
    }

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
