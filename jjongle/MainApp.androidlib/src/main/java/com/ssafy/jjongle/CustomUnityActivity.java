// app/src/main/java/com/ssafy/jjongle/CustomUnityActivity.java
package com.ssafy.jjongle;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.unity3d.player.UnityPlayer;
import com.unity3d.player.UnityPlayerActivity;

public class CustomUnityActivity extends UnityPlayerActivity {

    private String accessToken;
    private String refreshToken;
    private int stageId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        accessToken  = intent.getStringExtra("accessToken");
        refreshToken = intent.getStringExtra("refreshToken");
        // 앱에서 putExtra("stageId", int)로 보냈으면 int로 받는 게 맞음
        stageId      = intent.getIntExtra("stageId", -1);

        Log.d("Intent", "onCreate: accessToken=" + accessToken);
        Log.d("Intent", "onCreate: refreshToken=" + refreshToken);
        Log.d("Intent", "onCreate: stageId=" + stageId);

        // ❌ 여기서 UnitySendMessage 호출하지 않음
    }

    // ✅ Unity가 첫 씬 로드 후 호출 (AndroidIntentReader.Start -> activity.Call("onUnityReady"))
    public void onUnityReady() {
        runOnUiThread(() -> {
            // APIManager(혹은 GameManager) GameObject 이름/메서드 정확히 일치해야 함
            UnityPlayer.UnitySendMessage("APIManager", "SetToken", accessToken != null ? accessToken : "");
            UnityPlayer.UnitySendMessage("APIManager", "SetRefreshToken", refreshToken != null ? refreshToken : "");
            UnityPlayer.UnitySendMessage("APIManager", "SetStageId", String.valueOf(stageId));

            Log.d("Intent", "onUnityReady: UnitySendMessage 완료");
        });
    }
}