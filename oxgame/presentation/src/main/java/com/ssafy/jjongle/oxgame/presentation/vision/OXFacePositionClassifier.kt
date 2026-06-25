package com.ssafy.jjongle.oxgame.presentation.vision

/**
 * OXAnswerArea 모듈 기능을 표현하는 class 선언입니다.
 *
 * - 계층: oxgame/presentation
 * - 책임: 소속 계층의 역할을 타입으로 분리해 호출 경계를 명확히 합니다.
 */
enum class OXAnswerArea {
    O,
    X
}

/**
 * OXTrackedFace 모듈 기능을 표현하는 class 선언입니다.
 *
 * - 계층: oxgame/presentation
 * - 책임: 소속 계층의 역할을 타입으로 분리해 호출 경계를 명확히 합니다.
 */
data class OXTrackedFace(
    val participantId: Int,
    val x: Double,
    val y: Double,
    val area: OXAnswerArea,
    val profileImageBase64: String? = null
)

/**
 * OXFacePositionClassifier 관련 도메인 작업을 보조하는 컴포넌트입니다.
 *
 * - 계층: oxgame/presentation
 * - 책임: 반복되는 판단, 변환, 계산 로직을 별도 책임으로 분리합니다.
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
