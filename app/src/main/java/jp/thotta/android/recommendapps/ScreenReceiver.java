package jp.thotta.android.recommendapps;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class ScreenReceiver extends BroadcastReceiver {
    public ScreenReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if(Intent.ACTION_SCREEN_ON.equals(intent.getAction())) {
            Log.d("RecommendApps", "[ScreenReceiver.onReceive] ACTION_SCREEN_ON");
            context.startService(new Intent(context, RecorderService.class));
        } else if(Intent.ACTION_SCREEN_OFF.equals(intent.getAction())) {
            Log.d("RecommendApps", "[ScreenReceiver.onReceive] ACTION_SCREEN_OFF");
            context.stopService(new Intent(context, RecorderService.class));
        } else {
            Log.d("RecommendApps", "[ScreenReceiver.onReceive] Other action was received");
        }
    }
}
