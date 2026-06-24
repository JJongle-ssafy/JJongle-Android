package com.ssafy.jjongle.oxgame.presentation.vision

import android.graphics.Rect
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class FaceReidentifierTest {

    private lateinit var reidentifier: FaceReidentifier

    @Before
    fun setUp() {
        reidentifier = FaceReidentifier()
    }

    // ========== 기본 동작 ==========

    @Test
    fun `새로운 얼굴은 새로운 participantId를 받는다`() {
        val pid1 = reidentifier.resolveParticipantId(
            trackingId = 1,
            boundingBox = Rect(100, 100, 200, 200),
            imageWidth = 640,
            imageHeight = 480,
            currentTimeMs = 1000L
        )
        val pid2 = reidentifier.resolveParticipantId(
            trackingId = 2,
            boundingBox = Rect(400, 100, 500, 200),
            imageWidth = 640,
            imageHeight = 480,
            currentTimeMs = 1000L
        )

        assertNotEquals("서로 다른 위치의 얼굴은 다른 participantId", pid1, pid2)
    }

    @Test
    fun `동일한 trackingId는 항상 같은 participantId를 반환한다`() {
        val pid1 = reidentifier.resolveParticipantId(
            trackingId = 1,
            boundingBox = Rect(100, 100, 200, 200),
            imageWidth = 640,
            imageHeight = 480,
            currentTimeMs = 1000L
        )
        val pid2 = reidentifier.resolveParticipantId(
            trackingId = 1,
            boundingBox = Rect(110, 110, 210, 210), // 약간 이동
            imageWidth = 640,
            imageHeight = 480,
            currentTimeMs = 1200L
        )

        assertEquals("같은 trackingId는 같은 participantId", pid1, pid2)
    }

    // ========== SORT 매칭 (칼만+IoU 기반) ==========

    @Test
    fun `trackingId가 바뀌어도 겹치는 위치면 같은 참가자로 인식한다`() {
        // 첫 번째 프레임: trackingId=10으로 감지
        val pid1 = reidentifier.resolveParticipantId(
            trackingId = 10,
            boundingBox = Rect(100, 100, 200, 200),
            imageWidth = 640,
            imageHeight = 480,
            currentTimeMs = 1000L
        )

        // 두 번째 프레임: trackingId가 바뀜 (20), 비슷한 위치
        // 칼만 예측이 이전 위치 근처를 예측하므로 IoU가 충분히 높음
        val pid2 = reidentifier.resolveParticipantId(
            trackingId = 20,
            boundingBox = Rect(110, 105, 210, 205), // 약간 이동 (IoU > 0.1)
            imageWidth = 640,
            imageHeight = 480,
            currentTimeMs = 1100L
        )

        assertEquals("겹치는 위치에서 새 trackingId → 같은 participantId", pid1, pid2)
    }

    @Test
    fun `위치가 완전히 다르면 다른 참가자로 인식한다`() {
        val pid1 = reidentifier.resolveParticipantId(
            trackingId = 10,
            boundingBox = Rect(10, 10, 110, 110),
            imageWidth = 640,
            imageHeight = 480,
            currentTimeMs = 1000L
        )

        // 화면 반대편에서 새 trackingId
        val pid2 = reidentifier.resolveParticipantId(
            trackingId = 20,
            boundingBox = Rect(500, 350, 600, 450),
            imageWidth = 640,
            imageHeight = 480,
            currentTimeMs = 1100L
        )

        assertNotEquals("먼 위치의 새 trackingId → 새 participantId", pid1, pid2)
    }

    // ========== 배치 처리 (resolveAll) ==========

    @Test
    fun `resolveAll은 여러 얼굴을 한번에 처리한다`() {
        // 첫 프레임: 두 명
        val detections1 = listOf(
            FaceReidentifier.Detection(1, Rect(50, 50, 150, 150)),
            FaceReidentifier.Detection(2, Rect(400, 50, 500, 150)),
        )
        val result1 = reidentifier.resolveAll(detections1, 1000L)

        assertEquals(2, result1.size)
        assertNotEquals(result1[1], result1[2])

        // 두 번째 프레임: 같은 trackingId로 약간 이동
        val detections2 = listOf(
            FaceReidentifier.Detection(1, Rect(55, 55, 155, 155)),
            FaceReidentifier.Detection(2, Rect(405, 55, 505, 155)),
        )
        val result2 = reidentifier.resolveAll(detections2, 1100L)

        assertEquals("같은 trackingId → 같은 pid", result1[1], result2[1])
        assertEquals("같은 trackingId → 같은 pid", result1[2], result2[2])
    }

    @Test
    fun `resolveAll에서 trackingId가 바뀌어도 매칭 가능`() {
        // 첫 프레임
        val detections1 = listOf(
            FaceReidentifier.Detection(1, Rect(100, 100, 200, 200)),
            FaceReidentifier.Detection(2, Rect(400, 100, 500, 200)),
        )
        val result1 = reidentifier.resolveAll(detections1, 1000L)
        val pidA = result1[1]!!
        val pidB = result1[2]!!

        // 두 번째 프레임: trackingId가 모두 바뀜, 비슷한 위치
        val detections2 = listOf(
            FaceReidentifier.Detection(10, Rect(105, 105, 205, 205)),  // A 근처
            FaceReidentifier.Detection(20, Rect(405, 105, 505, 205)),  // B 근처
        )
        val result2 = reidentifier.resolveAll(detections2, 1100L)

        assertEquals("A는 A로 재식별", pidA, result2[10])
        assertEquals("B는 B로 재식별", pidB, result2[20])
    }

    @Test
    fun `resolveAll 빈 감지 시 빈 결과 반환`() {
        // 첫 프레임에 등록
        reidentifier.resolveAll(
            listOf(FaceReidentifier.Detection(1, Rect(100, 100, 200, 200))),
            1000L
        )

        // 빈 프레임
        val result = reidentifier.resolveAll(emptyList(), 1100L)
        assertTrue("빈 감지 → 빈 결과", result.isEmpty())
    }

    // ========== 칼만 필터 ==========

    @Test
    fun `칼만 필터가 움직이는 물체를 예측하여 매칭한다`() {
        // 등속으로 오른쪽으로 이동하는 얼굴 시뮬레이션
        val frames = listOf(
            Pair(1, Rect(100, 100, 200, 200)),  // frame 1
            Pair(1, Rect(120, 100, 220, 200)),  // frame 2: +20px
            Pair(1, Rect(140, 100, 240, 200)),  // frame 3: +20px
        )

        var lastPid = -1
        for ((i, frame) in frames.withIndex()) {
            val pid = reidentifier.resolveParticipantId(
                trackingId = frame.first,
                boundingBox = frame.second,
                imageWidth = 640,
                imageHeight = 480,
                currentTimeMs = 1000L + i * 100L
            )
            if (lastPid >= 0) {
                assertEquals("등속 이동 시 같은 participantId 유지", lastPid, pid)
            }
            lastPid = pid
        }

        // trackingId가 바뀌고 칼만 예측 위치 근처에서 재등장
        val pid4 = reidentifier.resolveParticipantId(
            trackingId = 99, // 새 trackingId
            boundingBox = Rect(160, 100, 260, 200), // 예측: ~160px (이전 패턴 유지)
            imageWidth = 640,
            imageHeight = 480,
            currentTimeMs = 1300L
        )

        assertEquals("칼만 예측 위치와 IoU 매칭 → 같은 참가자", lastPid, pid4)
    }

    // ========== IoU ==========

    @Test
    fun `IoU 계산이 정확하다`() {
        // 완전히 겹침
        val boxA = floatArrayOf(150f, 150f, 100f, 100f) // [cx, cy, w, h]
        val boxB = floatArrayOf(150f, 150f, 100f, 100f)
        assertEquals("완전 겹침 IoU = 1.0", 1.0f, KalmanBoxTracker.computeIoU(boxA, boxB), 0.001f)

        // 겹침 없음
        val boxC = floatArrayOf(50f, 50f, 50f, 50f)    // [25,25 ~ 75,75]
        val boxD = floatArrayOf(200f, 200f, 50f, 50f)   // [175,175 ~ 225,225]
        assertEquals("겹침 없음 IoU = 0.0", 0.0f, KalmanBoxTracker.computeIoU(boxC, boxD), 0.001f)

        // 부분 겹침
        val boxE = floatArrayOf(100f, 100f, 100f, 100f)  // [50,50 ~ 150,150]
        val boxF = floatArrayOf(125f, 125f, 100f, 100f)  // [75,75 ~ 175,175]
        val iou = KalmanBoxTracker.computeIoU(boxE, boxF)
        assertTrue("부분 겹침 IoU는 0과 1 사이", iou > 0f && iou < 1f)
        // 교집합: [75,75 ~ 150,150] = 75*75 = 5625
        // 합집합: 10000 + 10000 - 5625 = 14375
        // IoU = 5625/14375 ≈ 0.3913
        assertEquals("부분 겹침 IoU 값", 5625f / 14375f, iou, 0.01f)
    }

    // ========== 만료 ==========

    @Test
    fun `expiry 시간 이후에는 새로운 참가자로 처리한다`() {
        val pid1 = reidentifier.resolveParticipantId(
            trackingId = 10,
            boundingBox = Rect(100, 100, 200, 200),
            imageWidth = 640,
            imageHeight = 480,
            currentTimeMs = 1000L
        )

        // cleanup 수행 후 expiry 이후에 같은 위치에서 재등장
        reidentifier.cleanupExpired(1000L + FaceReidentifier.DEFAULT_EXPIRY_MS + 1)

        val pid2 = reidentifier.resolveParticipantId(
            trackingId = 20,
            boundingBox = Rect(100, 100, 200, 200),
            imageWidth = 640,
            imageHeight = 480,
            currentTimeMs = 1000L + FaceReidentifier.DEFAULT_EXPIRY_MS + 2
        )

        assertNotEquals("만료+cleanup 후에는 새 participantId", pid1, pid2)
    }

    // ========== reset ==========

    @Test
    fun `reset 후에는 모든 상태가 초기화된다`() {
        reidentifier.resolveParticipantId(
            trackingId = 1,
            boundingBox = Rect(100, 100, 200, 200),
            imageWidth = 640,
            imageHeight = 480,
            currentTimeMs = 1000L
        )

        reidentifier.reset()

        val pid2 = reidentifier.resolveParticipantId(
            trackingId = 1,
            boundingBox = Rect(100, 100, 200, 200),
            imageWidth = 640,
            imageHeight = 480,
            currentTimeMs = 2000L
        )

        assertEquals("reset 후 nextParticipantId가 1부터 다시 시작", 1, pid2)
    }

    // ========== 칼만 필터 단위 테스트 ==========

    @Test
    fun `KalmanBoxTracker 예측이 등속 운동을 따른다`() {
        val tracker = KalmanBoxTracker(floatArrayOf(100f, 100f, 50f, 50f))

        // 속도를 학습시키기 위해 여러번 업데이트
        tracker.predict()
        tracker.update(floatArrayOf(110f, 100f, 50f, 50f)) // +10 이동

        tracker.predict()
        tracker.update(floatArrayOf(120f, 100f, 50f, 50f)) // +10 이동

        // 다음 predict는 약 130 근처를 예측해야 함
        val predicted = tracker.predict()
        assertTrue(
            "등속 운동 예측: cx ≈ 130 근처",
            predicted[0] > 125f && predicted[0] < 140f
        )
    }

    @Test
    fun `KalmanBoxTracker update 후 timeSinceUpdate이 0이 된다`() {
        val tracker = KalmanBoxTracker(floatArrayOf(100f, 100f, 50f, 50f))

        tracker.predict()
        assertEquals(1, tracker.timeSinceUpdate)

        tracker.predict()
        assertEquals(2, tracker.timeSinceUpdate)

        tracker.update(floatArrayOf(100f, 100f, 50f, 50f))
        assertEquals(0, tracker.timeSinceUpdate)
    }

    @Test
    fun `KalmanBoxTracker rectToMeasurement 변환이 정확하다`() {
        val measurement = KalmanBoxTracker.rectToMeasurement(
            Rect(100, 200, 300, 400) // left=100, top=200, right=300, bottom=400
        )

        assertEquals("cx = 200", 200f, measurement[0], 0.1f)
        assertEquals("cy = 300", 300f, measurement[1], 0.1f)
        assertEquals("w = 200", 200f, measurement[2], 0.1f)
        assertEquals("h = 200", 200f, measurement[3], 0.1f)
    }

    // ========== 헝가리안 알고리즘 ==========

    @Test
    fun `헝가리안 알고리즘 기본 매칭`() {
        val costMatrix = arrayOf(
            floatArrayOf(0.1f, 0.9f),
            floatArrayOf(0.8f, 0.2f),
        )
        val result = HungarianAlgorithm.solve(costMatrix)

        assertEquals(2, result.size)
        assertTrue("(0,0) 매칭", result.contains(0 to 0))
        assertTrue("(1,1) 매칭", result.contains(1 to 1))
    }

    @Test
    fun `헝가리안 알고리즘 DISALLOWED 처리`() {
        val costMatrix = arrayOf(
            floatArrayOf(0.1f, HungarianAlgorithm.DISALLOWED),
            floatArrayOf(HungarianAlgorithm.DISALLOWED, 0.2f),
        )
        val result = HungarianAlgorithm.solve(costMatrix)

        assertEquals(2, result.size)
        assertTrue("(0,0) 매칭", result.contains(0 to 0))
        assertTrue("(1,1) 매칭", result.contains(1 to 1))
    }

    @Test
    fun `헝가리안 알고리즘 비정방 행렬`() {
        // 3 tracks, 2 detections
        val costMatrix = arrayOf(
            floatArrayOf(0.1f, 0.9f),
            floatArrayOf(0.8f, 0.2f),
            floatArrayOf(0.5f, 0.5f),
        )
        val result = HungarianAlgorithm.solve(costMatrix)

        // 최적: (0,0)=0.1 + (1,1)=0.2 = 0.3
        assertEquals(2, result.size)
    }
}
