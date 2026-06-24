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
import com.ssafy.jjongle.tangram.entity.TangramHistory
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test

class TangramGameUseCaseTest {

    @Test
    fun get_current_challenge_stage_id_returns_success_when_repository_returns_stage() = runTest {
        val useCase = useCase(repository = FakeRepository(stage = 8))

        val result = useCase.getCurrentChallengeStageId()

        assertTrue(result.isSuccess)
        assertEquals(8, result.getOrThrow())
    }

    @Test
    fun get_current_challenge_stage_id_handles_common_http_error() = runTest {
        val messageHelper = RecordingMessageHelper()
        val exception = HttpResponseException(
            status = HttpResponseStatus.Unauthorized,
            rawCode = 401,
            errorRequestUrl = "https://example.test/single-game",
            msg = "Unauthorized",
        )
        val useCase = useCase(
            repository = FakeRepository(error = exception),
            messageHelper = messageHelper,
        )

        val result = useCase.getCurrentChallengeStageId()

        assertTrue(result.isFailure)
        assertSame(exception, result.exceptionOrNull())
        assertEquals("세션이 만료되었습니다.", messageHelper.oneButtonDialogDesc)
    }

    private fun useCase(
        repository: TangramGameRepository,
        messageHelper: MessageHelper = MessageHelper.NoOp,
    ): TangramGameUseCase = TangramGameUseCase(
        tangramGameRepository = repository,
        resourceHelper = object : ResourceHelper {
            override fun getString(id: Int): String = id.toString()
        },
        messageHelper = messageHelper,
        navigationHelper = NavigationHelper.NoOp,
        ttiHelper = TTIHelper.NoOp,
    )

    private class FakeRepository(
        private val stage: Int = 1,
        private val error: Throwable? = null,
    ) : TangramGameRepository {
        override suspend fun getCurrentChallengeStageId(): Int {
            error?.let { throw it }
            return stage
        }

        override suspend fun getTangramHistories(page: Int, size: Int): TangramHistoriesPage =
            TangramHistoriesPage()

        override suspend fun getTangramDetail(tangramId: Long, type: AnimalType): TangramDetail =
            error("not used")
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
