package com.ssafy.jjongle.oxgame.presentation.vision

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

/**
 * OXFace Position Classifier Test 회귀를 막기 위한 테스트입니다.
 *
 * 리팩터링이나 모듈 이동 중에도 기존 동작, 아키텍처 경계, 오류 처리 계약이 유지되는지 확인합니다.
 */
class OXFacePositionClassifierTest {
    private val classifier = OXFacePositionClassifier()

    @Test
    fun classify_returnsOArea_whenFaceCenterIsLeftOfSplit() {
        val face = classifier.classify(
            participantId = 1,
            centerX = 100f,
            centerY = 50f,
            imageWidth = 400,
            imageHeight = 200,
            mirrorHorizontally = false
        )

        assertEquals(OXAnswerArea.O, face?.area)
        assertEquals(0.25, face?.x ?: -1.0, 0.001)
        assertEquals(0.25, face?.y ?: -1.0, 0.001)
    }

    @Test
    fun classify_returnsXArea_whenFaceCenterIsRightOfSplit() {
        val face = classifier.classify(
            participantId = 1,
            centerX = 300f,
            centerY = 50f,
            imageWidth = 400,
            imageHeight = 200,
            mirrorHorizontally = false
        )

        assertEquals(OXAnswerArea.X, face?.area)
        assertEquals(0.75, face?.x ?: -1.0, 0.001)
    }

    @Test
    fun classify_mirrorsXCoordinate_forFrontCameraPreview() {
        val face = classifier.classify(
            participantId = 1,
            centerX = 300f,
            centerY = 50f,
            imageWidth = 400,
            imageHeight = 200,
            mirrorHorizontally = true
        )

        assertEquals(OXAnswerArea.O, face?.area)
        assertEquals(0.25, face?.x ?: -1.0, 0.001)
    }

    @Test
    fun classify_returnsNull_forInvalidImageSize() {
        val face = classifier.classify(
            participantId = 1,
            centerX = 100f,
            centerY = 50f,
            imageWidth = 0,
            imageHeight = 200,
            mirrorHorizontally = false
        )

        assertNull(face)
    }
}
