package com.ssafy.jjongle.oxgame.domain.usecase

import com.ssafy.jjongle.common.domain.error.HttpResponseException
import com.ssafy.jjongle.common.domain.error.HttpResponseStatus
import com.ssafy.jjongle.common.domain.helper.MessageHelper
import com.ssafy.jjongle.common.domain.helper.NavigationHelper
import com.ssafy.jjongle.common.domain.helper.ResourceHelper
import com.ssafy.jjongle.tti.TTIHelper
import com.ssafy.jjongle.oxgame.domain.repository.OXGameHistoryPage
import com.ssafy.jjongle.oxgame.domain.repository.OXGameHistoryRepository
import com.ssafy.jjongle.oxgame.entity.OXGameHistory
import com.ssafy.jjongle.oxgame.entity.OXGameWrongAnswerNote
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDateTime

class GetOXGameHistoriesUseCaseTest {

    @Test
    fun invoke_returns_success_when_repository_returns_page() = runTest {
        val page = OXGameHistoryPage(
            totalPages = 1,
            content = listOf(OXGameHistory(id = 1L, playedAt = LocalDateTime.of(2026, 6, 23, 10, 0)))
                .toPersistentList(),
        )
        val useCase = useCase(repository = FakeRepository(page = page))

        val result = useCase(page = 0)

        assertTrue(result.isSuccess)
        assertSame(page, result.getOrThrow())
    }

    @Test
    fun invoke_handles_common_http_error_before_returning_failure() = runTest {
        val messageHelper = RecordingMessageHelper()
        val exception = HttpResponseException(
            status = HttpResponseStatus.Unauthorized,
            rawCode = 401,
            errorRequestUrl = "https://example.test/ox/histories",
            msg = "Unauthorized",
        )
        val useCase = useCase(
            repository = FakeRepository(error = exception),
            messageHelper = messageHelper,
        )

        val result = useCase(page = 0)

        assertTrue(result.isFailure)
        assertSame(exception, result.exceptionOrNull())
        assertEquals("세션이 만료되었습니다.", messageHelper.oneButtonDialogDesc)
    }

    private fun useCase(
        repository: OXGameHistoryRepository,
        messageHelper: MessageHelper = MessageHelper.NoOp,
    ): GetOXGameHistoriesUseCase = GetOXGameHistoriesUseCase(
        repo = repository,
        resourceHelper = object : ResourceHelper {
            override fun getString(id: Int): String = id.toString()
        },
        messageHelper = messageHelper,
        navigationHelper = NavigationHelper.NoOp,
        ttiHelper = TTIHelper.NoOp,
    )

    private class FakeRepository(
        private val page: OXGameHistoryPage? = null,
        private val error: Throwable? = null,
    ) : OXGameHistoryRepository {
        override suspend fun getHistories(page: Int): OXGameHistoryPage {
            error?.let { throw it }
            return requireNotNull(this.page)
        }

        override suspend fun getHistoryDetail(historyId: Long): ImmutableList<OXGameWrongAnswerNote> =
            persistentListOf()
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
