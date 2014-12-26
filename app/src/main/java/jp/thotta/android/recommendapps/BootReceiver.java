package jp.thotta.android.recommendapps;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BootReceiver extends BroadcastReceiver {
    public BootReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if(Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            Log.d("RecommendApps", "[BootReceiver.onReceive] ACTION_BOOT_COMPLETED");
            context.startService(new Intent(context, RegisterReceiverService.class));
        }
    }
}
