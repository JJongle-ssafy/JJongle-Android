package com.ssafy.jjongle.presentation.vision

enum class OXAnswerArea {
    O,
    X
}

data class OXTrackedFace(
    val participantId: Int,
    val x: Double,
    val y: Double,
    val area: OXAnswerArea,
    val profileImageBase64: String? = null
)

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
