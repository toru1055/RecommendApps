package jp.thotta.android.recommendapps;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

public class RegisterReceiverService extends Service {
    static final long DURATION_MILLS = 1000 * 60 * 1; // 1 min.
    private BroadcastReceiver mReceiver;
    private IntentFilter intentFilter;

    public RegisterReceiverService() {
        Log.d("RecommendApps", "[RegisterReceiverService] Constructor was called.");
        intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_SCREEN_ON);
        intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
        mReceiver = new ScreenReceiver();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("RecommendApps", "[RegisterReceiverService.onCreate] called method.");
        registerReceiver(mReceiver, intentFilter);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("RecommendApps", "[RegisterReceiverService.onStartCommand]Received start id " + startId + ": " + intent);
        scheduleNext();
        return START_STICKY;
    }

    private void scheduleNext() {
        Intent intent = new Intent(this, RegisterReceiverService.class);
        PendingIntent pendingIntent = PendingIntent.getService(
                this,
                0,
                intent,
                PendingIntent.FLAG_CANCEL_CURRENT
        );
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.set(
                AlarmManager.RTC,
                System.currentTimeMillis() + DURATION_MILLS,
                pendingIntent
        );
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("RecommendApps", "[RegisterReceiverService.onDestroy] called method.");
        Intent intent = new Intent(this, RegisterReceiverService.class);
        PendingIntent pendingIntent = PendingIntent.getService(
                this,
                0,
                intent,
                PendingIntent.FLAG_CANCEL_CURRENT
        );
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
        if(mReceiver != null) {
            unregisterReceiver(mReceiver);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
