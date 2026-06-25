package com.ssafy.jjongle.common.domain.helper

/**
 * MessageHelper 관련 도메인 작업을 보조하는 컴포넌트입니다.
 *
 * - 계층: common/domain
 * - 책임: 반복되는 판단, 변환, 계산 로직을 별도 책임으로 분리합니다.
 */
interface MessageHelper {
    /**
     * 짧은 안내 메시지를 요청합니다.
     *
     * 현재 구현에서는 Toast 대신 Snackbar 효과로 위임됩니다.
     */
    fun showToast(messageText: String)

    /**
     * Snackbar 메시지를 요청합니다.
     */
    fun showSnackBar(messageText: String)

    /**
     * 확인 버튼 하나를 가진 Dialog를 요청합니다.
     *
     * @param cantIgnore true이면 사용자가 Dialog를 무시하고 닫을 수 없는 흐름에 사용합니다.
     * @param descText 사용자에게 보여줄 설명 문구입니다.
     * @param onClickButton 확인 버튼 클릭 시 실행할 동작입니다.
     */
    fun showOneButtonDialog(
        cantIgnore: Boolean = false,
        descText: String,
        onClickButton: () -> Unit = {},
    )

    /**
     * 확인/취소 버튼을 가진 Dialog를 요청합니다.
     *
     * @param descText 사용자에게 보여줄 설명 문구입니다.
     * @param onClickPositive 긍정 버튼 클릭 시 실행할 동작입니다.
     * @param onClickNegative 부정 버튼 클릭 시 실행할 동작입니다.
     */
    fun showTwoButtonDialog(
        descText: String,
        onClickPositive: () -> Unit,
        onClickNegative: () -> Unit = {},
    )

    /**
     * 테스트나 preview에서 메시지 표시를 무시하기 위한 빈 구현체입니다.
     */
    object NoOp : MessageHelper {
        override fun showToast(messageText: String) = Unit

        override fun showSnackBar(messageText: String) = Unit

        override fun showOneButtonDialog(
            cantIgnore: Boolean,
            descText: String,
            onClickButton: () -> Unit,
        ) = Unit

        override fun showTwoButtonDialog(
            descText: String,
            onClickPositive: () -> Unit,
            onClickNegative: () -> Unit,
        ) = Unit
    }
}
