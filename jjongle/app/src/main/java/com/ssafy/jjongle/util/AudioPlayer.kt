package com.ssafy.jjongle.util

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Build
import android.util.Log
import com.ssafy.jjongle.data.model.TTSResponseWrapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class AudioPlayer(private val context: Context) {

    private var mediaPlayer: MediaPlayer? = null
    private var isAudioPlaying = false
    private var onPlaybackCompleted: (() -> Unit)? = null
    private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    private var audioFocusRequest: AudioFocusRequest? = null

    /**
     * 현재 재생 중인지 확인
     */
    fun isPlaying(): Boolean = isAudioPlaying

    /**
     * 재생 완료 콜백 설정
     */
    fun setOnPlaybackCompleted(callback: () -> Unit) {
        onPlaybackCompleted = callback
    }

    /**
     * 오디오 포커스 요청
     */
    private fun requestAudioFocus(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val audioAttributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                .build()

            audioFocusRequest =
                AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK)
                    .setAudioAttributes(audioAttributes)
                    .setOnAudioFocusChangeListener { focusChange ->
                        when (focusChange) {
                            AudioManager.AUDIOFOCUS_LOSS -> stop()
                            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> mediaPlayer?.pause()
                            AudioManager.AUDIOFOCUS_GAIN -> mediaPlayer?.start()
                        }
                    }
                    .build()

            val result = audioManager.requestAudioFocus(audioFocusRequest!!)
            result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED
        } else {
            @Suppress("DEPRECATION")
            val result = audioManager.requestAudioFocus(
                { focusChange ->
                    when (focusChange) {
                        AudioManager.AUDIOFOCUS_LOSS -> stop()
                        AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> mediaPlayer?.pause()
                        AudioManager.AUDIOFOCUS_GAIN -> mediaPlayer?.start()
                    }
                },
                AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK
            )
            result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED
        }
    }

    /**
     * 오디오 포커스 해제
     */
    private fun abandonAudioFocus() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            audioFocusRequest?.let { request ->
                audioManager.abandonAudioFocusRequest(request)
            }
        } else {
            @Suppress("DEPRECATION")
            audioManager.abandonAudioFocus(null)
        }
    }

    /**
     * TTS 응답을 재생 (오디오 길이 정보 포함)
     * @param ttsResponseWrapper TTS 응답 래퍼
     */
    suspend fun playTTS(ttsResponseWrapper: TTSResponseWrapper) {
        try {
            val audioFile = createTempAudioFile(ttsResponseWrapper)
            playAudioFile(audioFile)
        } catch (e: Exception) {
            Log.e("AudioPlayer", "TTS 재생 실패", e)
        }
    }



    /**
     * 임시 오디오 파일 생성 (TTSResponseWrapper 사용)
     */
    private suspend fun createTempAudioFile(ttsResponseWrapper: TTSResponseWrapper): File =
        withContext(Dispatchers.IO) {
            val tempFile = File.createTempFile("tts_audio", ".mp3", context.cacheDir)
            try {
                val bytes = ttsResponseWrapper.audioBytes
                if (bytes != null) {
                    FileOutputStream(tempFile).use { outputStream ->
                        outputStream.write(bytes)
                        outputStream.flush()
                        try {
                            outputStream.fd.sync()
                        } catch (_: Exception) {}
                    }
                } else {
                    Log.e("AudioPlayer", "바이트 배열이 null입니다")
                    throw IOException("바이트 배열이 null입니다")
                }
                
                tempFile
            } catch (e: IOException) {
                Log.e("AudioPlayer", "임시 파일 생성 실패", e)
                tempFile.delete()
                throw e
            }
        }

    /**
     * 오디오 파일 재생 (prepareAsync 적용 버전)
     */
    private fun playAudioFile(audioFile: File) {
        try {
            if (!requestAudioFocus()) {
                Log.e("AudioPlayer", "오디오 포커스를 얻을 수 없습니다")
                audioFile.delete() // 파일을 사용하지 않으니 삭제
                return
            }
            if (audioFile.length() == 0L) {
                abandonAudioFocus()
                return
            }
            mediaPlayer?.release()
            mediaPlayer = MediaPlayer().apply {
                // 오디오 속성 설정 (새 API)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .build()
                    )
                } else {
                    @Suppress("DEPRECATION")
                    setAudioStreamType(AudioManager.STREAM_MUSIC)
                }

                // 우선 Context + Uri 경로로 시도 → 실패 시 절대 경로 → 실패 시 FD 순으로 폴백
                var dataSourceSet = false
                try {
                    val uri = android.net.Uri.fromFile(audioFile)
                    setDataSource(context, uri)
                    dataSourceSet = true
                } catch (_: Exception) {}

                if (!dataSourceSet) {
                    try {
                        setDataSource(audioFile.absolutePath)
                        dataSourceSet = true
                    } catch (_: Exception) {}
                }

                if (!dataSourceSet) {
                    try {
                        val fileLen = audioFile.length()
                        java.io.FileInputStream(audioFile).use { fis ->
                            setDataSource(fis.fd, 0, fileLen)
                        }
                        dataSourceSet = true
                    } catch (e: Exception) { throw e }
                }

                // 준비 완료 리스너 설정
                setOnPreparedListener { mp ->
                    try {
                        mp.start()
                        isAudioPlaying = true
                    } catch (e: Exception) {
                        Log.e("AudioPlayer", "MediaPlayer start() 실패", e)
                    }
                }

                // 완료 리스너 설정
                setOnCompletionListener {
                    isAudioPlaying = false
                    abandonAudioFocus()
                    release()
                    mediaPlayer = null
                    audioFile.delete()
                    onPlaybackCompleted?.invoke()
                }

                // 에러 리스너 설정
                setOnErrorListener { mp, what, extra ->
                    Log.e("AudioPlayer", "재생 오류: what=$what, extra=$extra")
                    isAudioPlaying = false
                    abandonAudioFocus()
                    release()
                    mediaPlayer = null
//                    audioFile.delete()
                    true // 에러를 처리했음을 의미
                }

                // 비동기 준비 시작
                prepareAsync()
            }
        } catch (e: Exception) {
            Log.e("AudioPlayer", "오디오 재생 실패 (setDataSource 등)", e)
            isAudioPlaying = false
            mediaPlayer?.release()
            mediaPlayer = null
            audioFile.delete()
        }
    }

    /**
     * 재생 중지
     */
    fun stop() {
        mediaPlayer?.let { player ->
            if (player.isPlaying) {
                player.stop()
            }
            player.release()
        }
        mediaPlayer = null
        abandonAudioFocus()
        isAudioPlaying = false
    }

    /**
     * 리소스 정리
     */
    fun release() {
        stop()
    }
}