package com.ssafy.jjongle.oxgame.presentation.vision

import android.graphics.Rect
import android.os.SystemClock

/**
 * 프레임 사이에서 얼굴 특징과 위치를 비교해 같은 참여자를 다시 식별합니다.
 *
 * ML Kit 감지 결과의 순서가 바뀌거나 잠시 누락되어도 점수와 프로필을 같은 사용자에게 연결합니다.
 */
class FaceReidentifier(
    /** IoU 임계값 — 이 값 이상이어야 매칭 허용 */
    private val iouThreshold: Float = DEFAULT_IOU_THRESHOLD,
    /** 사라진 얼굴을 기억하는 최대 미감지 프레임 수 */
    private val maxAge: Int = DEFAULT_MAX_AGE,
    /** 신뢰할 수 있는 트랙으로 인정되기 위한 최소 연속 감지 횟수 */
    private val minHits: Int = DEFAULT_MIN_HITS,
    /** 사라진 얼굴을 기억하는 시간(ms) — 시간 기반 만료 */
    private val expiryMs: Long = DEFAULT_EXPIRY_MS,
) {
    /**
     * 추적 중인 참가자 정보.
     */
    class TrackedParticipant(
        val participantId: Int,
        /** 칼만 필터 기반 바운딩 박스 트래커 */
        val kalmanTracker: KalmanBoxTracker,
        /** 마지막으로 감지된 시각 (elapsedRealtime) */
        var lastSeenMs: Long,
        /** 현재 활성 ML Kit trackingId (null이면 사라진 상태) */
        var activeTrackingId: Int? = null,
    ) {
        /** 현재 예측된 바운딩 박스 [cx, cy, w, h] */
        val predictedBox: FloatArray get() = kalmanTracker.getPredictedBox()
    }

    /** ML Kit trackingId → participantId */
    private val trackingIdMap = mutableMapOf<Int, Int>()

    /** participantId → TrackedParticipant */
    private val participants = mutableMapOf<Int, TrackedParticipant>()

    private var nextParticipantId = 1

    /**
     * ML Kit에서 감지된 얼굴의 trackingId와 boundingBox를 받아
     * 안정적인 participantId를 반환합니다.
     *
     * 단일 얼굴 처리용 편의 메서드입니다. 다수 얼굴은 [resolveAll]을 사용하세요.
     */
    fun resolveParticipantId(
        trackingId: Int,
        boundingBox: Rect,
        imageWidth: Int,
        imageHeight: Int,
        currentTimeMs: Long = SystemClock.elapsedRealtime(),
    ): Int {
        // 이미 매핑된 trackingId라면 즉시 반환 (빠른 경로)
        trackingIdMap[trackingId]?.let { existingPid ->
            participants[existingPid]?.let { p ->
                val measurement = KalmanBoxTracker.rectToMeasurement(boundingBox)
                p.kalmanTracker.update(measurement)
                p.lastSeenMs = currentTimeMs
                p.activeTrackingId = trackingId
                return existingPid
            }
        }

        // 새로운 trackingId → 전체 매칭 프로세스 (단일 검출)
        val detections = listOf(Detection(trackingId, boundingBox))
        val results = performMatching(detections, currentTimeMs)
        return results[trackingId] ?: createNewParticipant(trackingId, boundingBox, currentTimeMs)
    }

    /**
     * 프레임의 모든 감지된 얼굴을 한번에 처리합니다.
     * 칼만 예측 → IoU 비용 행렬 → 헝가리안 매칭을 수행합니다.
     *
     * @param detections 이 프레임에서 감지된 모든 얼굴 정보
     * @param currentTimeMs 현재 시각
     * @return trackingId → participantId 매핑
     */
    fun resolveAll(
        detections: List<Detection>,
        currentTimeMs: Long = SystemClock.elapsedRealtime(),
    ): Map<Int, Int> {
        if (detections.isEmpty()) {
            // 감지 없음 → 모든 트랙에 predict만 호출
            predictAllTracks()
            return emptyMap()
        }

        // 1) 이미 매핑된 trackingId 처리 (빠른 경로)
        val resolved = mutableMapOf<Int, Int>()
        val unresolved = mutableListOf<Detection>()

        for (det in detections) {
            val existingPid = trackingIdMap[det.trackingId]
            if (existingPid != null && participants.containsKey(existingPid)) {
                // 기존 매핑 유지 — 칼만 업데이트
                val p = participants[existingPid]!!
                val measurement = KalmanBoxTracker.rectToMeasurement(det.boundingBox)
                p.kalmanTracker.update(measurement)
                p.lastSeenMs = currentTimeMs
                p.activeTrackingId = det.trackingId
                resolved[det.trackingId] = existingPid
            } else {
                unresolved.add(det)
            }
        }

        // 2) 미해결 detection에 대해 SORT 매칭 수행
        if (unresolved.isNotEmpty()) {
            val currentTrackingIds = detections.mapTo(mutableSetOf()) { it.trackingId }
            val matched = performMatching(unresolved, currentTimeMs, currentTrackingIds)
            resolved.putAll(matched)

            // 매칭 안 된 것들은 새 참가자로 등록
            for (det in unresolved) {
                if (det.trackingId !in resolved) {
                    val newPid = createNewParticipant(det.trackingId, det.boundingBox, currentTimeMs)
                    resolved[det.trackingId] = newPid
                }
            }
        }

        // 3) 이 프레임에서 매칭되지 않은 기존 트랙들 predict
        val matchedPids = resolved.values.toSet()
        for ((pid, participant) in participants) {
            if (pid !in matchedPids) {
                participant.kalmanTracker.predict()
                participant.activeTrackingId = null
            }
        }

        return resolved
    }

    /**
     * 만료된 참가자 정보를 정리합니다.
     */
    fun cleanupExpired(currentTimeMs: Long = SystemClock.elapsedRealtime()) {
        val expiredPids = participants.entries
            .filter { (_, p) ->
                val timeExpired = currentTimeMs - p.lastSeenMs > expiryMs
                val ageExpired = p.kalmanTracker.timeSinceUpdate > maxAge
                timeExpired || ageExpired
            }
            .map { it.key }

        for (pid in expiredPids) {
            val p = participants.remove(pid)
            if (p?.activeTrackingId != null) {
                trackingIdMap.remove(p.activeTrackingId)
            }
        }

        // trackingIdMap에서 orphan 항목 정리
        val validPids = participants.keys
        trackingIdMap.entries.removeAll { it.value !in validPids }
    }

    /**
     * 모든 상태를 초기화합니다.
     */
    fun reset() {
        trackingIdMap.clear()
        participants.clear()
        nextParticipantId = 1
    }

    // ---------- internal ----------

    /**
     * SORT 스타일 매칭: 칼만 예측 → IoU 비용 행렬 → 헝가리안 알고리즘
     */
    private fun performMatching(
        detections: List<Detection>,
        currentTimeMs: Long,
        currentTrackingIds: Set<Int> = detections.mapTo(mutableSetOf()) { it.trackingId },
    ): Map<Int, Int> {
        // 매칭 후보: 현재 프레임에 기존 active trackingId가 보이지 않는 참가자들
        val candidateTracks = participants.values.filter { p ->
            val timeSinceLastSeen = currentTimeMs - p.lastSeenMs
            val notExpired = timeSinceLastSeen <= expiryMs && p.kalmanTracker.timeSinceUpdate <= maxAge
            val activeTrackingId = p.activeTrackingId
            val activeTrackingIdStillPresent = activeTrackingId != null &&
                    activeTrackingId in currentTrackingIds &&
                    trackingIdMap[activeTrackingId] == p.participantId
            val recentActiveTrackStillPresent = activeTrackingIdStillPresent &&
                    timeSinceLastSeen <= ACTIVE_GRACE_MS
            notExpired && !recentActiveTrackStillPresent
        }

        if (candidateTracks.isEmpty()) return emptyMap()

        // 칼만 예측 수행 & 예측 박스 수집
        val predictedBoxes = candidateTracks.map { track ->
            track.kalmanTracker.predict()
            track.kalmanTracker.getPredictedBox()
        }

        // 감지된 박스 수집
        val detectedBoxes = detections.map { det ->
            KalmanBoxTracker.rectToMeasurement(det.boundingBox)
        }

        // IoU 비용 행렬 생성
        val costMatrix = HungarianAlgorithm.buildIoUCostMatrix(
            predictedBoxes = predictedBoxes,
            detectedBoxes = detectedBoxes,
            iouThreshold = iouThreshold,
        )

        // 헝가리안 매칭
        val assignments = HungarianAlgorithm.solve(costMatrix)

        // 매칭 결과 적용
        val result = mutableMapOf<Int, Int>()
        for ((trackIdx, detIdx) in assignments) {
            val track = candidateTracks[trackIdx]
            val det = detections[detIdx]
            val measurement = KalmanBoxTracker.rectToMeasurement(det.boundingBox)

            // 이전 trackingId 매핑 제거
            val oldTrackingId = track.activeTrackingId
            if (oldTrackingId != null) {
                trackingIdMap.remove(oldTrackingId)
            }

            // 칼만 업데이트
            track.kalmanTracker.update(measurement)
            track.lastSeenMs = currentTimeMs
            track.activeTrackingId = det.trackingId

            // 새 trackingId 매핑
            trackingIdMap[det.trackingId] = track.participantId
            result[det.trackingId] = track.participantId
        }

        return result
    }

    private fun createNewParticipant(
        trackingId: Int,
        boundingBox: Rect,
        currentTimeMs: Long,
    ): Int {
        val newPid = nextParticipantId++
        val measurement = KalmanBoxTracker.rectToMeasurement(boundingBox)
        val kalmanTracker = KalmanBoxTracker(measurement)

        val participant = TrackedParticipant(
            participantId = newPid,
            kalmanTracker = kalmanTracker,
            lastSeenMs = currentTimeMs,
            activeTrackingId = trackingId,
        )

        trackingIdMap[trackingId] = newPid
        participants[newPid] = participant
        return newPid
    }

    private fun predictAllTracks() {
        for ((_, participant) in participants) {
            participant.kalmanTracker.predict()
        }
    }

    /**
     * 감지 정보를 담는 데이터 클래스.
     */
    data class Detection(
        val trackingId: Int,
        val boundingBox: Rect,
    )

    companion object {
        /** IoU 임계값 — 0.1 이상이면 매칭 후보 (얼굴은 작으므로 낮게 설정) */
        const val DEFAULT_IOU_THRESHOLD = 0.1f

        /** 최대 미감지 프레임 수 (이후 트랙 삭제) */
        const val DEFAULT_MAX_AGE = 30

        /** 신뢰 트랙 최소 연속 감지 횟수 */
        const val DEFAULT_MIN_HITS = 1

        /** 사라진 얼굴을 기억하는 시간 (3초) */
        const val DEFAULT_EXPIRY_MS = 3000L

        /** 최근 활성 참가자로 판단하는 유예 시간 */
        private const val ACTIVE_GRACE_MS = 300L
    }
}
