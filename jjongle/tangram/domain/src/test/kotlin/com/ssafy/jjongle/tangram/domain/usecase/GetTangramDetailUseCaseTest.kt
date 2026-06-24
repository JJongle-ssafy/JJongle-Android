package com.ssafy.jjongle.tangram.domain.usecase

import com.ssafy.jjongle.common.domain.error.HttpResponseException
import com.ssafy.jjongle.common.domain.error.HttpResponseStatus
import com.ssafy.jjongle.common.domain.helper.MessageHelper
import com.ssafy.jjongle.common.domain.helper.NavigationHelper
import com.ssafy.jjongle.common.domain.helper.ResourceHelper
import com.ssafy.jjongle.tti.TTIHelper
import com.ssafy.jjongle.tangram.domain.repository.TangramGameRepository
import com.ssafy.jjongle.common.entity.AnimalType
import com.ssafy.jjongle.tangram.entity.TangramDetail
import com.ssafy.jjongle.tangram.entity.TangramHistoriesPage
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test

class GetTangramDetailUseCaseTest {

    @Test
    fun returns_success_result_when_repository_returns_detail() = runTest {
        val detail = TangramDetail(tangramId = 7L, animal = AnimalType.DOG, story = "story")
        val useCase = useCase(repository = FakeRepository(detail = detail))

        val result = useCase(id = 7L, type = AnimalType.DOG)

        assertTrue(result.isSuccess)
        assertEquals(detail, result.getOrThrow())
    }

    @Test
    fun handles_common_http_error_and_returns_failure_result() = runTest {
        val messageHelper = RecordingMessageHelper()
        val exception = HttpResponseException(
            status = HttpResponseStatus.NotFound,
            rawCode = 404,
            errorRequestUrl = "https://example.test/single-game/history/7",
            msg = "not found",
        )
        val useCase = useCase(
            repository = FakeRepository(detailError = exception),
            messageHelper = messageHelper,
        )

        val result = useCase(id = 7L, type = AnimalType.DOG)

        assertTrue(result.isFailure)
        assertSame(exception, result.exceptionOrNull())
        assertEquals("지원하지 않는 기능입니다.", messageHelper.oneButtonDialogDesc)
    }

    private fun useCase(
        repository: TangramGameRepository,
        messageHelper: MessageHelper = MessageHelper.NoOp,
    ): GetTangramDetailUseCase = GetTangramDetailUseCase(
        repo = repository,
        resourceHelper = object : ResourceHelper {
            override fun getString(id: Int): String = id.toString()
        },
        messageHelper = messageHelper,
        navigationHelper = NavigationHelper.NoOp,
        ttiHelper = TTIHelper.NoOp,
    )

    private class FakeRepository(
        private val detail: TangramDetail = TangramDetail(1L, AnimalType.TURTLE, ""),
        private val detailError: Throwable? = null,
    ) : TangramGameRepository {
        override suspend fun getCurrentChallengeStageId(): Int = 1

        override suspend fun getTangramHistories(page: Int, size: Int): TangramHistoriesPage =
            error("not used")

        override suspend fun getTangramDetail(tangramId: Long, type: AnimalType): TangramDetail {
            detailError?.let { throw it }
            return detail
        }
    }

    private class RecordingMessageHelper : MessageHelper {
        var oneButtonDialogDesc: String? = null

        override fun showToast(messageText: String) = Unit

        override fun showSnackBar(messageText: String) = Unit

        override fun showOneButtonDialog(
            cantIgnore: Boolean,
            descText: String,
            onClickButton: () -> Unit,
        ) {
            oneButtonDialogDesc = descText
        }

        override fun showTwoButtonDialog(
            descText: String,
            onClickPositive: () -> Unit,
            onClickNegative: () -> Unit,
        ) = Unit
    }
}
