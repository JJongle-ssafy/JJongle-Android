package com.ssafy.jjongle.oxgame.presentation.vision

import kotlin.math.min

/**
 * 얼굴 추적 결과와 이전 프레임의 객체를 최소 비용으로 매칭하는 Hungarian Algorithm 구현입니다.
 *
 * 카메라 프레임마다 감지 순서가 바뀌어도 같은 참여자를 안정적으로 이어 붙이기 위해 사용합니다.
 */
object HungarianAlgorithm {

    /** 매칭 불가를 나타내는 큰 비용 값 */
    const val DISALLOWED = 1e9f

    /**
     * 비용 행렬에서 최적 할당을 계산합니다.
     *
     * @param costMatrix [rows x cols] 비용 행렬. costMatrix[i][j] = track i와 detection j의 비용.
     *                   DISALLOWED 값은 매칭 금지를 의미합니다.
     * @return 매칭 결과 리스트 (row index, col index) 쌍
     */
    fun solve(costMatrix: Array<FloatArray>): List<Pair<Int, Int>> {
        val rows = costMatrix.size
        if (rows == 0) return emptyList()
        val cols = costMatrix[0].size
        if (cols == 0) return emptyList()

        // 정방 행렬로 패딩 (rows != cols인 경우)
        val n = maxOf(rows, cols)
        val cost = Array(n) { i ->
            FloatArray(n) { j ->
                if (i < rows && j < cols) costMatrix[i][j] else 0f
            }
        }

        // 1단계: 각 행에서 최소값을 빼기
        for (i in 0 until n) {
            val minVal = cost[i].min()
            for (j in 0 until n) {
                cost[i][j] -= minVal
            }
        }

        // 2단계: 각 열에서 최소값을 빼기
        for (j in 0 until n) {
            var minVal = Float.MAX_VALUE
            for (i in 0 until n) {
                if (cost[i][j] < minVal) minVal = cost[i][j]
            }
            for (i in 0 until n) {
                cost[i][j] -= minVal
            }
        }

        // 매칭 배열 초기화
        val rowMatch = IntArray(n) { -1 } // rowMatch[i] = i행에 매칭된 열, -1이면 미매칭
        val colMatch = IntArray(n) { -1 } // colMatch[j] = j열에 매칭된 행, -1이면 미매칭

        // 초기 탐욕적 매칭
        for (i in 0 until n) {
            for (j in 0 until n) {
                if (cost[i][j] == 0f && rowMatch[i] == -1 && colMatch[j] == -1) {
                    rowMatch[i] = j
                    colMatch[j] = i
                }
            }
        }

        // 반복: 증가 경로 찾기
        for (iteration in 0 until n * 2) {
            // 매칭되지 않은 행 찾기
            val unmatchedRow = (0 until n).firstOrNull { rowMatch[it] == -1 } ?: break

            // BFS로 증가 경로 찾기
            val visited = BooleanArray(n)
            val parent = IntArray(n) { -1 }
            val queue = ArrayDeque<Int>()

            // 시작 행에서 0인 열 찾기
            val rowSlack = FloatArray(n) { Float.MAX_VALUE }
            val slackRow = IntArray(n) { -1 }

            fun updateSlacks(row: Int) {
                for (j in 0 until n) {
                    if (!visited[j] && cost[row][j] < rowSlack[j]) {
                        rowSlack[j] = cost[row][j]
                        slackRow[j] = row
                    }
                }
            }

            updateSlacks(unmatchedRow)

            var foundCol = -1
            while (foundCol == -1) {
                // 최소 slack인 열 찾기
                var minSlack = Float.MAX_VALUE
                var minCol = -1
                for (j in 0 until n) {
                    if (!visited[j] && rowSlack[j] < minSlack) {
                        minSlack = rowSlack[j]
                        minCol = j
                    }
                }

                if (minCol == -1) break // 더 이상 진행 불가

                // slack이 0이 아니면 비용 조정
                if (minSlack > 0f) {
                    for (j in 0 until n) {
                        if (visited[j]) {
                            // 매칭된 열의 비용 증가
                            val matchedRow = colMatch[j]
                            if (matchedRow >= 0) {
                                cost[matchedRow][j] += minSlack
                            }
                        } else {
                            rowSlack[j] -= minSlack
                        }
                    }
                    // 방문한 행들의 비용 감소
                    cost[unmatchedRow].indices.forEach { j ->
                        // 이미 처리됨
                    }
                }

                visited[minCol] = true
                parent[minCol] = slackRow[minCol]

                if (colMatch[minCol] == -1) {
                    // 증가 경로 발견!
                    foundCol = minCol
                } else {
                    // 매칭된 행으로 이동
                    val nextRow = colMatch[minCol]
                    updateSlacks(nextRow)
                }
            }

            // 증가 경로를 따라 매칭 갱신
            if (foundCol != -1) {
                var col = foundCol
                while (col != -1) {
                    val row = parent[col]
                    val prevCol = rowMatch[row]
                    rowMatch[row] = col
                    colMatch[col] = row
                    col = prevCol
                }
            }
        }

        // 결과 추출: 원래 범위 내의 매칭만 반환
        val result = mutableListOf<Pair<Int, Int>>()
        for (i in 0 until rows) {
            val j = rowMatch[i]
            if (j in 0 until cols && costMatrix[i][j] < DISALLOWED) {
                result.add(i to j)
            }
        }

        return result
    }

    /**
     * IoU 비용 행렬을 생성합니다.
     * IoU가 높을수록 비용이 낮습니다 (1 - IoU).
     *
     * @param predictedBoxes 예측된 바운딩 박스 리스트 [cx, cy, w, h]
     * @param detectedBoxes  감지된 바운딩 박스 리스트 [cx, cy, w, h]
     * @param iouThreshold   IoU 임계값. 이 값 미만이면 매칭 불가 처리.
     * @return 비용 행렬 [predictedBoxes.size x detectedBoxes.size]
     */
    fun buildIoUCostMatrix(
        predictedBoxes: List<FloatArray>,
        detectedBoxes: List<FloatArray>,
        iouThreshold: Float = 0.1f,
    ): Array<FloatArray> {
        return Array(predictedBoxes.size) { i ->
            FloatArray(detectedBoxes.size) { j ->
                val iou = KalmanBoxTracker.computeIoU(predictedBoxes[i], detectedBoxes[j])
                if (iou >= iouThreshold) {
                    1f - iou // IoU가 높을수록 비용이 낮음
                } else {
                    DISALLOWED // 매칭 금지
                }
            }
        }
    }
}
