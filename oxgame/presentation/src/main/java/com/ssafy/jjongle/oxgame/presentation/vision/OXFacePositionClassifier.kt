package com.ssafy.jjongle.oxgame.presentation.vision

/**
 * OX 게임 흐름에서 허용되는 OXAnswer Area 값의 집합입니다.
 *
 * 분기 가능한 상태나 이벤트를 타입으로 제한해 잘못된 문자열/숫자 값이 계층 사이로 전달되지 않게 합니다.
 */
enum class OXAnswerArea {
    O,
    X
}

/**
 * OXTracked Face는 OX 게임 흐름에서 계층 사이로 전달되는 도메인 값입니다.
 *
 * 원시 값 여러 개를 그대로 넘기지 않고 이름 있는 타입으로 묶어 호출 의도를 명확히 합니다.
 */
data class OXTrackedFace(
    val participantId: Int,
    val x: Double,
    val y: Double,
    val area: OXAnswerArea,
    val profileImageBase64: String? = null
)

/**
 * 추적된 얼굴의 화면 위치를 O/X 선택 영역으로 분류합니다.
 *
 * 아이들이 몸을 움직여 답을 선택하는 게임 규칙을 카메라 좌표 기반 판정으로 변환합니다.
 */
class OXFacePositionClassifier(
    private val splitX: Double = DEFAULT_SPLIT_X
) {
    init {
        require(splitX in 0.0..1.0) { "splitX must be between 0.0 and 1.0." }
    }

    fun classify(
        participantId: Int,
        centerX: Float,
        centerY: Float,
        imageWidth: Int,
        imageHeight: Int,
        mirrorHorizontally: Boolean
    ): OXTrackedFace? {
        if (participantId <= 0 || imageWidth <= 0 || imageHeight <= 0) return null

        val rawX = centerX.toDouble() / imageWidth.toDouble()
        val displayedX = if (mirrorHorizontally) 1.0 - rawX else rawX
        val normalizedX = displayedX.coerceIn(0.0, 1.0)
        val normalizedY = (centerY.toDouble() / imageHeight.toDouble()).coerceIn(0.0, 1.0)
        val area = if (normalizedX < splitX) OXAnswerArea.O else OXAnswerArea.X

        return OXTrackedFace(
            participantId = participantId,
            x = normalizedX,
            y = normalizedY,
            area = area
        )
    }

    companion object {
        private const val DEFAULT_SPLIT_X = 0.5
    }
}
