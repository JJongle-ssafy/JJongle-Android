// com.ssafy.jjongle.bridge.UnityBridge
package com.ssafy.jjongle.bridge

import android.app.Activity
import android.content.Intent
import com.ssafy.jjongle.presentation.ui.activity.MainActivity

object UnityBridge {
    @JvmStatic
    fun goToScreen(activity: Activity, dest: String, payload: String = "") {
        val intent = Intent(activity, MainActivity::class.java).apply {
            putExtra("dest_from_unity", dest)        // e.g. "stage", "camera"
            putExtra("payload_from_unity", payload)  // JSON 문자열 권장
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        }
        activity.startActivity(intent)
    }
}