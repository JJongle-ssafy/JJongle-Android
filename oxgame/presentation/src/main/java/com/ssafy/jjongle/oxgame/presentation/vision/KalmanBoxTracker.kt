package com.ssafy.jjongle.oxgame.presentation.vision

/**
 * 카메라 프레임 사이에서 얼굴 박스 위치를 예측하고 보정하는 Kalman tracker입니다.
 *
 * 일시적인 감지 흔들림에도 참여자 위치가 급격히 튀지 않도록 추적 상태를 유지합니다.
 */
class KalmanBoxTracker(
    initialBox: FloatArray, // [cx, cy, w, h]
    private val processNoisePos: Float = DEFAULT_PROCESS_NOISE_POS,
    private val processNoiseVel: Float = DEFAULT_PROCESS_NOISE_VEL,
    private val measurementNoise: Float = DEFAULT_MEASUREMENT_NOISE,
) {
    /** 
     * 독립적인 1D 칼만 필터 (위치와 속도 추적)
     * 상태: [pos, vel]^T
     */
    private class KalmanFilter1D(
        initialPos: Float,
        initPosVariance: Float,
        initVelVariance: Float
    ) {
        var pos: Float = initialPos
        var vel: Float = 0f
        
        // 2x2 공분산 행렬 P
        var p00: Float = initPosVariance
        var p01: Float = 0f
        var p10: Float = 0f
        var p11: Float = initVelVariance
        
        fun predict(qPos: Float, qVel: Float) {
            // 1. 상태 전이 (State Prediction)
            // pos_new = pos + vel (등속 운동 모델, dt = 1)
            // vel_new = vel
            pos += vel
            
            // 2. 공분산 예측 (Covariance Prediction)
            // P_new = F * P * F^T + Q
            // 여기서 F = [[1, 1], [0, 1]]
            val nextP00 = p00 + p10 + p01 + p11 + qPos
            val nextP01 = p01 + p11
            val nextP10 = p10 + p11
            val nextP11 = p11 + qVel
            
            p00 = nextP00
            p01 = nextP01
            p10 = nextP10
            p11 = nextP11
        }
        
        fun update(measurement: Float, r: Float) {
            // 1. 잔차 (Innovation)
            val y = measurement - pos
            // 잔차 공분산 S = H * P * H^T + R = p00 + r
            val s = p00 + r
            
            if (s <= 0f) return // 예외 처리 방지
            
            // 2. 칼만 게인 (Kalman Gain) K = P * H^T / S
            val k0 = p00 / s
            val k1 = p10 / s
            
            // 3. 상태 업데이트
            pos += k0 * y
            vel += k1 * y
            
            // 4. 공분산 업데이트 P = (I - K * H) * P
            val nextP00 = (1f - k0) * p00
            val nextP01 = (1f - k0) * p01
            val nextP10 = p10 - k1 * p00
            val nextP11 = p11 - k1 * p01
            
            p00 = nextP00
            p01 = nextP01
            p10 = nextP10
            p11 = nextP11
        }
    }

    // cx, cy, w, h 에 대한 4개의 독립 필터
    private val filters = Array(MEASURE_DIM) { i ->
        KalmanFilter1D(
            initialPos = initialBox[i],
            initPosVariance = measurementNoise,
            initVelVariance = measurementNoise * INITIAL_VELOCITY_UNCERTAINTY
        )
    }

    /** 마지막 업데이트 이후 predict만 호출된 횟수 */
    var timeSinceUpdate: Int = 0
        private set

    /** 총 업데이트(관측) 횟수 */
    var hitCount: Int = 0
        private set

    /** 연속 업데이트 횟수 (predict 후 update 안 되면 0으로 리셋) */
    var hitStreak: Int = 0
        private set

    init {
        require(initialBox.size == MEASURE_DIM) {
            "initialBox must have $MEASURE_DIM elements [cx, cy, w, h]"
        }
    }

    /**
     * 한 타임스텝 예측합니다.
     */
    fun predict(): FloatArray {
        for (i in 0 until MEASURE_DIM) {
            filters[i].predict(processNoisePos, processNoiseVel)
        }

        // 크기가 음수가 되지 않도록 보정 (w, h는 index 2, 3)
        filters[2].pos = filters[2].pos.coerceAtLeast(1f) // width
        filters[3].pos = filters[3].pos.coerceAtLeast(1f) // height

        timeSinceUpdate++

        return getPredictedBox()
    }

    /**
     * 관측값으로 상태를 보정합니다.
     *
     * @param measurement [cx, cy, w, h] 관측된 바운딩 박스
     */
    fun update(measurement: FloatArray) {
        require(measurement.size == MEASURE_DIM) {
            "measurement must have $MEASURE_DIM elements [cx, cy, w, h]"
        }

        for (i in 0 until MEASURE_DIM) {
            filters[i].update(measurement[i], measurementNoise)
        }

        timeSinceUpdate = 0
        hitCount++
        hitStreak++
    }

    /**
     * 현재 예측된 바운딩 박스를 반환합니다.
     *
     * @return [cx, cy, w, h]
     */
    fun getPredictedBox(): FloatArray {
        return floatArrayOf(
            filters[0].pos, // cx
            filters[1].pos, // cy
            filters[2].pos.coerceAtLeast(1f), // w
            filters[3].pos.coerceAtLeast(1f), // h
        )
    }

    /**
     * 현재 상태를 Rect 형태의 [left, top, right, bottom]으로 반환합니다.
     */
    fun getPredictedRect(): FloatArray {
        val box = getPredictedBox()
        val cx = box[0]
        val cy = box[1]
        val w = box[2]
        val h = box[3]
        return floatArrayOf(
            cx - w / 2f,  // left
            cy - h / 2f,  // top
            cx + w / 2f,  // right
            cy + h / 2f,  // bottom
        )
    }

    companion object {
        /** 관측 벡터 차원: [cx, cy, w, h] */
        const val MEASURE_DIM = 4

        /** 위치 프로세스 노이즈 기본값 (위치의 불확실성 증가량) */
        const val DEFAULT_PROCESS_NOISE_POS = 1f
        
        /** 속도 프로세스 노이즈 기본값 (속도는 변화량이 작으므로 노이즈를 작게 부여) */
        const val DEFAULT_PROCESS_NOISE_VEL = 0.01f

        /** 관측 노이즈 기본값 (ML Kit의 Bounding Box 측정 오차) */
        const val DEFAULT_MEASUREMENT_NOISE = 10f

        /** 초기 속도 불확실성 배수 (초기에는 속도를 모르므로 크게 설정) */
        private const val INITIAL_VELOCITY_UNCERTAINTY = 10f

        /**
         * 바운딩 박스 [left, top, right, bottom] → [cx, cy, w, h] 변환
         */
        fun rectToMeasurement(left: Float, top: Float, right: Float, bottom: Float): FloatArray {
            val w = right - left
            val h = bottom - top
            return floatArrayOf(
                left + w / 2f,
                top + h / 2f,
                w,
                h,
            )
        }

        /**
         * android.graphics.Rect → [cx, cy, w, h] 변환
         */
        fun rectToMeasurement(rect: android.graphics.Rect): FloatArray {
            return rectToMeasurement(
                rect.left.toFloat(),
                rect.top.toFloat(),
                rect.right.toFloat(),
                rect.bottom.toFloat(),
            )
        }

        /**
         * 두 바운딩 박스의 IoU (Intersection over Union)를 계산합니다.
         *
         * @param boxA [cx, cy, w, h]
         * @param boxB [cx, cy, w, h]
         * @return IoU 값 (0.0 ~ 1.0)
         */
        fun computeIoU(boxA: FloatArray, boxB: FloatArray): Float {
            // [cx, cy, w, h] → [left, top, right, bottom]
            val aL = boxA[0] - boxA[2] / 2f
            val aT = boxA[1] - boxA[3] / 2f
            val aR = boxA[0] + boxA[2] / 2f
            val aB = boxA[1] + boxA[3] / 2f

            val bL = boxB[0] - boxB[2] / 2f
            val bT = boxB[1] - boxB[3] / 2f
            val bR = boxB[0] + boxB[2] / 2f
            val bB = boxB[1] + boxB[3] / 2f

            // 교집합
            val interL = maxOf(aL, bL)
            val interT = maxOf(aT, bT)
            val interR = minOf(aR, bR)
            val interB = minOf(aB, bB)

            val interW = (interR - interL).coerceAtLeast(0f)
            val interH = (interB - interT).coerceAtLeast(0f)
            val interArea = interW * interH

            // 합집합
            val areaA = (aR - aL) * (aB - aT)
            val areaB = (bR - bL) * (bB - bT)
            val unionArea = areaA + areaB - interArea

            return if (unionArea > 0f) interArea / unionArea else 0f
        }
    }
}
