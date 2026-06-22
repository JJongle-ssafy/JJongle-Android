package com.ssafy.jjongle.presentation.ui.component

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
import android.view.ViewGroup
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetector
import com.google.mlkit.vision.face.FaceDetectorOptions
import com.ssafy.jjongle.presentation.vision.OXFacePositionClassifier
import com.ssafy.jjongle.presentation.vision.OXTrackedFace
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
            .setMinFaceSize(0.08f)
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

                val preview = Preview.Builder().build().also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }
                val imageAnalysis = ImageAnalysis.Builder()
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
                        println("DEBUG: 카메라 프레임 크기 - width: $frameWidth, height: $frameHeight")
                    }
                } catch (exc: Exception) {
                    println("카메라 바인딩 실패: ${exc.message}")
                }
            }, ContextCompat.getMainExecutor(ctx))

            previewView
        }
    )
}

@ExperimentalGetImage
private class OXFaceFrameAnalyzer(
    private val detector: FaceDetector,
    private val classifier: OXFacePositionClassifier,
    private val resultExecutor: Executor,
    private val onFacePositionsChanged: (List<OXTrackedFace>) -> Unit
) : ImageAnalysis.Analyzer {
    private val trackingIdToParticipantId = mutableMapOf<Int, Int>()
    private val profileImagesByParticipantId = mutableMapOf<Int, String>()
    private var nextParticipantId = 1
    private var lastAnalyzedAt = 0L

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
                onFacePositionsChanged(faces.toTrackedFaces(imageProxy, imageWidth, imageHeight))
            }
            .addOnFailureListener(resultExecutor) {
                onFacePositionsChanged(emptyList())
            }
            .addOnCompleteListener(resultExecutor) {
                imageProxy.close()
            }
    }

    private fun List<Face>.toTrackedFaces(
        imageProxy: ImageProxy,
        imageWidth: Int,
        imageHeight: Int
    ): List<OXTrackedFace> {
        val trackedFaces = mapNotNull { face ->
            val trackingId = face.trackingId ?: return@mapNotNull null
            val participantId = trackingIdToParticipantId.getOrPut(trackingId) {
                nextParticipantId++
            }
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
        private const val ANALYSIS_INTERVAL_MS = 200L
        private const val PROFILE_IMAGE_SIZE = 160
        private const val PROFILE_JPEG_QUALITY = 72
        private const val FACE_PADDING_RATIO = 0.20f
    }
}

@Composable
private fun PermissionRequestUI(modifier: Modifier, onRequest: () -> Unit) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "카메라 권한이 필요합니다",
                color = Color.White,
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = onRequest) {
                Text("권한 허용")
            }
        }
    }
} 
