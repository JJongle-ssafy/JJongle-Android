package com.ssafy.jjongle.oxgame.presentation.ui.component

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageFormat
import android.graphics.Matrix
import android.graphics.Rect
import android.graphics.YuvImage
import android.media.Image
import android.os.SystemClock
import android.util.Base64
import android.view.Surface
import android.view.ViewGroup
import android.view.WindowManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.OptIn
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.Face
import com.ssafy.jjongle.common.presentation.ui.component.ArchiText
import com.ssafy.jjongle.common.presentation.ui.theme.ArchiThemeImpl
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetector
import com.google.mlkit.vision.face.FaceDetectorOptions
import com.ssafy.jjongle.oxgame.presentation.vision.FaceReidentifier
import com.ssafy.jjongle.oxgame.presentation.vision.OXFacePositionClassifier
import com.ssafy.jjongle.oxgame.presentation.vision.OXTrackedFace
import java.util.concurrent.Executors
import java.io.ByteArrayOutputStream
import java.util.concurrent.Executor

@Composable
fun CameraComponent(
    modifier: Modifier = Modifier,
    onFacePositionsChanged: (List<OXTrackedFace>) -> Unit,
    onCameraFrameSizeChanged: ((Int, Int) -> Unit)? = null
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val currentOnFacePositionsChanged by rememberUpdatedState(onFacePositionsChanged)

    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted ->
            hasCameraPermission = granted
        }
    )

    LaunchedEffect(Unit) {
        if (!hasCameraPermission) {
            launcher.launch(Manifest.permission.CAMERA)
        }
    }

    if (hasCameraPermission) {
        CameraPreview(
            modifier = modifier,
            context = context,
            lifecycleOwner = lifecycleOwner,
            onFacePositionsChanged = { currentOnFacePositionsChanged(it) },
            onCameraFrameSizeChanged = onCameraFrameSizeChanged
        )
    } else {
        // 권한이 없을 때 표시할 UI
        PermissionRequestUI(modifier) {
            launcher.launch(Manifest.permission.CAMERA)
        }
    }
}

@Composable
@OptIn(ExperimentalGetImage::class)
private fun CameraPreview(
    modifier: Modifier = Modifier,
    context: Context,
    lifecycleOwner: LifecycleOwner,
    onFacePositionsChanged: (List<OXTrackedFace>) -> Unit,
    onCameraFrameSizeChanged: ((Int, Int) -> Unit)? = null
) {
    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }
    val classifier = remember { OXFacePositionClassifier() }
    val faceDetector = remember {
        val options = FaceDetectorOptions.Builder()
            .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
            .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_NONE)
            .setContourMode(FaceDetectorOptions.CONTOUR_MODE_NONE)
            .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_NONE)
            .setMinFaceSize(0.12f)
            .enableTracking()
            .build()
        FaceDetection.getClient(options)
    }

    DisposableEffect(Unit) {
        onDispose {
            cameraExecutor.shutdown()
            faceDetector.close()
            onFacePositionsChanged(emptyList())
        }
    }

    AndroidView(
        modifier = modifier,
        factory = { ctx ->
            val previewView = PreviewView(ctx).apply {
                // 화면을 빈 공간 없이 가득 채우되, 필요 시 잘라내기(FILL_CENTER)
                implementationMode = PreviewView.ImplementationMode.COMPATIBLE
                this.scaleType = PreviewView.ScaleType.FILL_CENTER
                this.layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
            }

            val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)

            cameraProviderFuture.addListener({
                val cameraProvider = cameraProviderFuture.get()
                val windowManager = ctx.getSystemService(Context.WINDOW_SERVICE) as? WindowManager
                @Suppress("DEPRECATION")
                val targetRotation = previewView.display?.rotation
                    ?: windowManager?.defaultDisplay?.rotation
                    ?: Surface.ROTATION_90

                val preview = Preview.Builder()
                    .setTargetRotation(targetRotation)
                    .build()
                    .also {
                        it.setSurfaceProvider(previewView.surfaceProvider)
                    }
                val imageAnalysis = ImageAnalysis.Builder()
                    .setTargetRotation(targetRotation)
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build()
                    .also {
                        it.setAnalyzer(
                            cameraExecutor,
                            OXFaceFrameAnalyzer(
                                detector = faceDetector,
                                classifier = classifier,
                                resultExecutor = cameraExecutor,
                                onFacePositionsChanged = onFacePositionsChanged
                            )
                        )
                    }

                try {
                    cameraProvider.unbindAll()
                    val camera = cameraProvider.bindToLifecycle(
                        lifecycleOwner,
                        CameraSelector.DEFAULT_FRONT_CAMERA,
                        preview,
                        imageAnalysis
                    )

                    // 카메라 프레임 크기 정보 가져오기
                    camera.cameraInfo.sensorRotationDegrees.let { rotation ->
                        // 카메라 센서의 기본 해상도 (일반적으로 1920x1080 또는 1280x720)
                        val sensorWidth = 1280
                        val sensorHeight = 720
                        
                        // 회전에 따른 실제 프레임 크기 계산
                        val frameWidth = if (rotation == 90 || rotation == 270) sensorHeight else sensorWidth
                        val frameHeight = if (rotation == 90 || rotation == 270) sensorWidth else sensorHeight
                        
                        onCameraFrameSizeChanged?.invoke(frameWidth, frameHeight)
                    }
                } catch (_: Exception) {
                }
            }, ContextCompat.getMainExecutor(ctx))

            previewView
        }
    )
}

/**
 * OXFaceFrameAnalyzer Compose UI를 구성합니다.
 *
 * - 계층: oxgame/presentation
 * - 책임: 상태를 표시하고 사용자 이벤트를 상위 콜백이나 ViewModel로 전달합니다.
 */
@ExperimentalGetImage
private class OXFaceFrameAnalyzer(
    private val detector: FaceDetector,
    private val classifier: OXFacePositionClassifier,
    private val resultExecutor: Executor,
    private val onFacePositionsChanged: (List<OXTrackedFace>) -> Unit
) : ImageAnalysis.Analyzer {
    private val faceReidentifier = FaceReidentifier()
    private val profileImagesByParticipantId = mutableMapOf<Int, String>()
    private var lastAnalyzedAt = 0L
    private var lastSuccessfulResult: List<OXTrackedFace> = emptyList()
    private var lastSuccessfulResultAt = 0L
    private var cleanupCounter = 0

    override fun analyze(imageProxy: ImageProxy) {
        val now = SystemClock.elapsedRealtime()
        if (now - lastAnalyzedAt < ANALYSIS_INTERVAL_MS) {
            imageProxy.close()
            return
        }
        lastAnalyzedAt = now

        val mediaImage = imageProxy.image
        if (mediaImage == null) {
            imageProxy.close()
            return
        }

        val rotationDegrees = imageProxy.imageInfo.rotationDegrees
        val inputImage = InputImage.fromMediaImage(mediaImage, rotationDegrees)
        val imageWidth = if (rotationDegrees == 90 || rotationDegrees == 270) {
            imageProxy.height
        } else {
            imageProxy.width
        }
        val imageHeight = if (rotationDegrees == 90 || rotationDegrees == 270) {
            imageProxy.width
        } else {
            imageProxy.height
        }

        detector.process(inputImage)
            .addOnSuccessListener(resultExecutor) { faces ->
                val tracked = faces.toTrackedFaces(imageProxy, imageWidth, imageHeight, now)
                if (tracked.isNotEmpty()) {
                    lastSuccessfulResult = tracked
                    lastSuccessfulResultAt = now
                    onFacePositionsChanged(tracked)
                } else {
                    // Grace period: 얼굴이 순간적으로 감지되지 않아도 직전 결과 유지
                    if (now - lastSuccessfulResultAt < GRACE_PERIOD_MS) {
                        onFacePositionsChanged(lastSuccessfulResult)
                    } else {
                        lastSuccessfulResult = emptyList()
                        onFacePositionsChanged(emptyList())
                    }
                }
                // 주기적 cleanup (매 50프레임마다)
                if (++cleanupCounter % 50 == 0) {
                    faceReidentifier.cleanupExpired(now)
                }
            }
            .addOnFailureListener(resultExecutor) {
                val now2 = android.os.SystemClock.elapsedRealtime()
                if (now2 - lastSuccessfulResultAt < GRACE_PERIOD_MS) {
                    onFacePositionsChanged(lastSuccessfulResult)
                } else {
                    onFacePositionsChanged(emptyList())
                }
            }
            .addOnCompleteListener(resultExecutor) {
                imageProxy.close()
            }
    }

    private fun List<Face>.toTrackedFaces(
        imageProxy: ImageProxy,
        imageWidth: Int,
        imageHeight: Int,
        currentTimeMs: Long
    ): List<OXTrackedFace> {
        // 1) ML Kit 얼굴 → Detection 리스트 변환
        val detections = mapNotNull { face ->
            val trackingId = face.trackingId ?: return@mapNotNull null
            FaceReidentifier.Detection(trackingId, face.boundingBox)
        }

        // 2) SORT 기반 배치 매칭 (칼만 예측 → IoU → 헝가리안)
        val idMapping = faceReidentifier.resolveAll(detections, currentTimeMs)

        // 3) trackingId → (participantId, face) 매핑
        val trackedFaces = mapNotNull { face ->
            val trackingId = face.trackingId ?: return@mapNotNull null
            val participantId = idMapping[trackingId] ?: return@mapNotNull null
            participantId to face
        }

        val needsProfile = trackedFaces.any { (participantId, _) ->
            !profileImagesByParticipantId.containsKey(participantId)
        }
        val frameBitmap = if (needsProfile) imageProxy.toUprightBitmapOrNull() else null

        return trackedFaces.mapNotNull { (participantId, face) ->
            val profile = profileImagesByParticipantId[participantId]
                ?: frameBitmap?.cropFaceToBase64(face.boundingBox)?.also {
                    profileImagesByParticipantId[participantId] = it
                }

            classifier.classify(
                participantId = participantId,
                centerX = face.boundingBox.exactCenterX(),
                centerY = face.boundingBox.exactCenterY(),
                imageWidth = imageWidth,
                imageHeight = imageHeight,
                mirrorHorizontally = true
            )?.copy(profileImageBase64 = profile)
        }
    }

    private fun ImageProxy.toUprightBitmapOrNull(): Bitmap? {
        val sourceImage = image ?: return null
        return runCatching {
            val nv21 = sourceImage.toNv21()
            val yuvImage = YuvImage(nv21, ImageFormat.NV21, sourceImage.width, sourceImage.height, null)
            val output = ByteArrayOutputStream()
            yuvImage.compressToJpeg(Rect(0, 0, sourceImage.width, sourceImage.height), 85, output)
            val bitmapBytes = output.toByteArray()
            val bitmap = BitmapFactory.decodeByteArray(bitmapBytes, 0, bitmapBytes.size)
                ?: return null

            if (imageInfo.rotationDegrees == 0) {
                bitmap
            } else {
                val matrix = Matrix().apply { postRotate(imageInfo.rotationDegrees.toFloat()) }
                Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
            }
        }.getOrNull()
    }

    private fun Image.toNv21(): ByteArray {
        val yPlane = planes[0]
        val uPlane = planes[1]
        val vPlane = planes[2]
        val ySize = width * height
        val chromaWidth = width / 2
        val chromaHeight = height / 2
        val nv21 = ByteArray(ySize + chromaWidth * chromaHeight * 2)

        copyLumaPlane(
            plane = yPlane,
            imageWidth = width,
            imageHeight = height,
            output = nv21
        )

        val uBuffer = uPlane.buffer.duplicate()
        val vBuffer = vPlane.buffer.duplicate()
        var outputOffset = ySize

        for (row in 0 until chromaHeight) {
            val uRowStart = row * uPlane.rowStride
            val vRowStart = row * vPlane.rowStride
            for (col in 0 until chromaWidth) {
                val uIndex = uRowStart + col * uPlane.pixelStride
                val vIndex = vRowStart + col * vPlane.pixelStride
                nv21[outputOffset++] = vBuffer.get(vIndex)
                nv21[outputOffset++] = uBuffer.get(uIndex)
            }
        }

        return nv21
    }

    private fun copyLumaPlane(
        plane: Image.Plane,
        imageWidth: Int,
        imageHeight: Int,
        output: ByteArray
    ) {
        val buffer = plane.buffer.duplicate()
        var outputOffset = 0

        for (row in 0 until imageHeight) {
            val rowStart = row * plane.rowStride
            for (col in 0 until imageWidth) {
                val index = rowStart + col * plane.pixelStride
                output[outputOffset++] = buffer.get(index)
            }
        }
    }

    private fun Bitmap.cropFaceToBase64(faceBounds: Rect): String? {
        return runCatching {
            val padding = (maxOf(faceBounds.width(), faceBounds.height()) * FACE_PADDING_RATIO).toInt()
            val left = (faceBounds.left - padding).coerceIn(0, width - 1)
            val top = (faceBounds.top - padding).coerceIn(0, height - 1)
            val right = (faceBounds.right + padding).coerceIn(left + 1, width)
            val bottom = (faceBounds.bottom + padding).coerceIn(top + 1, height)
            val cropped = Bitmap.createBitmap(this, left, top, right - left, bottom - top)
            val scaled = Bitmap.createScaledBitmap(cropped, PROFILE_IMAGE_SIZE, PROFILE_IMAGE_SIZE, true)
            val output = ByteArrayOutputStream()
            scaled.compress(Bitmap.CompressFormat.JPEG, PROFILE_JPEG_QUALITY, output)
            Base64.encodeToString(output.toByteArray(), Base64.NO_WRAP)
        }.getOrNull()
    }

    companion object {
        /** 분석 간격 — 10ms로 단축하여 추적 연속성 향상 */
        private const val ANALYSIS_INTERVAL_MS = 10L
        private const val PROFILE_IMAGE_SIZE = 160
        private const val PROFILE_JPEG_QUALITY = 90
        private const val FACE_PADDING_RATIO = 0.20f
        /** 얼굴 감지 실패 시 직전 결과를 유지하는 유예 시간 */
        private const val GRACE_PERIOD_MS = 1000L
    }
}

@Composable
private fun PermissionRequestUI(modifier: Modifier, onRequest: () -> Unit) {
    val colors = ArchiThemeImpl.archiColor
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(colors.bgBrandLevel0),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ArchiText(
                text = "카메라 권한이 필요합니다",
                color = colors.contentOnBrand,
                style = ArchiThemeImpl.typeScale.textStrongL
            )
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = onRequest,
                colors = ButtonDefaults.buttonColors(
                    containerColor = colors.bgDefaultLevel0,
                    contentColor = colors.contentDefaultLevel0,
                )
            ) {
                ArchiText(
                    text = "권한 허용",
                    style = ArchiThemeImpl.typeScale.textStrongM,
                    color = colors.contentDefaultLevel0
                )
            }
        }
    }
} 
