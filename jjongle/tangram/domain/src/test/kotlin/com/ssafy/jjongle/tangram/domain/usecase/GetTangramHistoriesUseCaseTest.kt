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
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test

class GetTangramHistoriesUseCaseTest {

    @Test
    fun returns_success_result_when_repository_returns_histories() = runTest {
        val histories = listOf(TangramHistory(stage = 3, tangramId = 7L, animal = AnimalType.DOG))
        val page = TangramHistoriesPage(content = histories.toPersistentList(), isEnd = true)
        val useCase = useCase(repository = FakeRepository(historiesPage = page))

        val result = useCase(page = 0, size = 200)

        assertTrue(result.isSuccess)
        assertEquals(page, result.getOrThrow())
        assertTrue(result.getOrThrow().isEnd)
    }

    @Test
    fun handles_common_http_error_and_returns_failure_result() = runTest {
        val messageHelper = RecordingMessageHelper()
        val exception = HttpResponseException(
            status = HttpResponseStatus.ServerError,
            rawCode = 500,
            errorRequestUrl = "https://example.test/single-game/histories",
            msg = "server error",
        )
        val useCase = useCase(
            repository = FakeRepository(historiesError = exception),
            messageHelper = messageHelper,
        )

        val result = useCase(page = 0, size = 200)

        assertTrue(result.isFailure)
        assertSame(exception, result.exceptionOrNull())
        assertEquals("잠시 후 다시 시도해주세요.", messageHelper.oneButtonDialogDesc)
    }

    private fun useCase(
        repository: TangramGameRepository,
        messageHelper: MessageHelper = MessageHelper.NoOp,
    ): GetTangramHistoriesUseCase = GetTangramHistoriesUseCase(
        repo = repository,
        resourceHelper = object : ResourceHelper {
            override fun getString(id: Int): String = id.toString()
        },
        messageHelper = messageHelper,
        navigationHelper = NavigationHelper.NoOp,
        ttiHelper = TTIHelper.NoOp,
    )

    private class FakeRepository(
        private val historiesPage: TangramHistoriesPage = TangramHistoriesPage(),
        private val historiesError: Throwable? = null,
    ) : TangramGameRepository {
        override suspend fun getCurrentChallengeStageId(): Int = 1

        override suspend fun getTangramHistories(page: Int, size: Int): TangramHistoriesPage {
            historiesError?.let { throw it }
            return historiesPage
        }

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
