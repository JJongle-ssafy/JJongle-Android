package com.ssafy.jjongle.oxgame.domain.usecase

import com.ssafy.jjongle.common.domain.error.HttpResponseException
import com.ssafy.jjongle.common.domain.error.HttpResponseStatus
import com.ssafy.jjongle.common.domain.helper.MessageHelper
import com.ssafy.jjongle.common.domain.helper.NavigationHelper
import com.ssafy.jjongle.common.domain.helper.ResourceHelper
import com.ssafy.jjongle.tti.TTIHelper
import com.ssafy.jjongle.oxgame.domain.repository.OXGameHistoryPage
import com.ssafy.jjongle.oxgame.domain.repository.OXGameHistoryRepository
import com.ssafy.jjongle.oxgame.entity.OX
import com.ssafy.jjongle.oxgame.entity.OXGameWrongAnswerNote
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Get OXGame History Detail Use Case Test 회귀를 막기 위한 테스트입니다.
 *
 * 리팩터링이나 모듈 이동 중에도 기존 동작, 아키텍처 경계, 오류 처리 계약이 유지되는지 확인합니다.
 */
class GetOXGameHistoryDetailUseCaseTest {

    @Test
    fun invoke_returns_success_when_repository_returns_wrong_answer_notes() = runTest {
        val notes = persistentListOf(OXGameWrongAnswerNote(question = "question", answer = OX.O))
        val useCase = useCase(repository = FakeRepository(notes = notes))

        val result = useCase(historyId = 1L)

        assertTrue(result.isSuccess)
        assertSame(notes, result.getOrThrow())
    }

    @Test
    fun invoke_handles_common_http_error_before_returning_failure() = runTest {
        val messageHelper = RecordingMessageHelper()
        val exception = HttpResponseException(
            status = HttpResponseStatus.NotFound,
            rawCode = 404,
            errorRequestUrl = "https://example.test/ox/histories/1",
            msg = "Not Found",
        )
        val useCase = useCase(
            repository = FakeRepository(error = exception),
            messageHelper = messageHelper,
        )

        val result = useCase(historyId = 1L)

        assertTrue(result.isFailure)
        assertSame(exception, result.exceptionOrNull())
        assertEquals("지원하지 않는 기능입니다.", messageHelper.oneButtonDialogDesc)
    }

    private fun useCase(
        repository: OXGameHistoryRepository,
        messageHelper: MessageHelper = MessageHelper.NoOp,
    ): GetOXGameHistoryDetailUseCase = GetOXGameHistoryDetailUseCase(
        repo = repository,
        resourceHelper = object : ResourceHelper {
            override fun getString(id: Int): String = id.toString()
        },
        messageHelper = messageHelper,
        navigationHelper = NavigationHelper.NoOp,
        ttiHelper = TTIHelper.NoOp,
    )

    private class FakeRepository(
        private val notes: ImmutableList<OXGameWrongAnswerNote> = persistentListOf(),
        private val error: Throwable? = null,
    ) : OXGameHistoryRepository {
        override suspend fun getHistories(page: Int): OXGameHistoryPage = error("not used")

        override suspend fun getHistoryDetail(historyId: Long): ImmutableList<OXGameWrongAnswerNote> {
            error?.let { throw it }
            return notes
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
