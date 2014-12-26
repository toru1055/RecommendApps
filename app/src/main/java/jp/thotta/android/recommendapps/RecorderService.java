package jp.thotta.android.recommendapps;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

public class RecorderService extends Service {
    static final long DURATION_MILLS = 1000 * 10; // 10 sec.
    private MainDBHelper dbHelper;
    private UsageHistory usageHistory;
    private LocationManager locationManager;
    private MyLocationListener myLocationListener;

    public RecorderService() {
        Log.d("RecommendApps", "[RecorderService] Constructor was called.");
//        intentFilter = new IntentFilter(Intent.ACTION_SCREEN_ON);
//        intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
//        mReceiver = new ScreenReceiver();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("RecommendApps", "[RecorderService.onCreate] called method.");
        //registerReceiver(mReceiver, intentFilter);
        this.dbHelper = new MainDBHelper(this);
        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        myLocationListener = MyLocationListener.getStaticLocation(this);
        String provider = MyLocationListener.getBestProvider(locationManager);
        Log.d("RecommendApps", "[RecorderService.onCreate] Location Provider: " + provider);
        locationManager.requestLocationUpdates(provider, 0, 100, myLocationListener);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        Log.d("RecommendApps", "[RecorderService.onStartCommand]Received start id " + startId + ": " + intent);


        //TODO: SCREEN_ONであることをチェックする
        execTask();
        scheduleNext();
        return START_STICKY;
    }

    private void execTask() {
        ActivityManager activityManager = (ActivityManager) getSystemService(Service.ACTIVITY_SERVICE);
        String packageName = activityManager.getRunningAppProcesses().get(0).processName;
        double lat = myLocationListener.lat;
        double lon = myLocationListener.lon;
        Log.d("RecommendApps", "[RecorderService.execTask] Lat=" + lat + ", Lon=" + lon);
        Log.d("RecommendApps", "[RecorderService.execTask] packageName: " + packageName);
        if(usageHistory != null) {
            if(!usageHistory.isSameApp(packageName)) {
                Log.d("RecommendApps", "[RecorderService.execTask] packageName is changed.");
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                if(lat == 0.0 && lon == 0.0) {
                    usageHistory.update(db);
                } else {
                    usageHistory.update(db, lat, lon);
                }
                usageHistory = UsageHistory.create(packageName, lat, lon, db);
            } else {
                Log.d("RecommendApps", "[RecorderService.execTask] packageName is NOT changed.");
            }
        } else {
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            Log.d("RecommendApps", "[RecorderService.execTask] usageHistory is NULL");
            usageHistory = UsageHistory.create(packageName, lat, lon, db);
        }
    }

    private void scheduleNext() {
        Intent intent = new Intent(this, RecorderService.class);
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
        Log.d("RecommendApps", "[RecorderService.onDestroy] called method.");
        Intent intent = new Intent(this, RecorderService.class);
        PendingIntent pendingIntent = PendingIntent.getService(
                this,
                0,
                intent,
                PendingIntent.FLAG_CANCEL_CURRENT
        );
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if(!myLocationListener.available) {
            usageHistory.update(db);
        } else {
            double lat = myLocationListener.lat;
            double lon = myLocationListener.lon;
            usageHistory.update(db, lat, lon);
        }
        locationManager.removeUpdates(myLocationListener);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
