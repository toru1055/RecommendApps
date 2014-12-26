package jp.thotta.android.recommendapps;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.util.Log;

/**
 * Created by thotta on 14/12/26.
 */
public class MyLocationListener implements LocationListener {
    private static final String PREF_KEY = "location";
    private static final String KEY_LAT = "lat";
    private static final String KEY_LON = "lon";
    public double lat = 0.0;
    public double lon = 0.0;
    public boolean available = false;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;

    public MyLocationListener(Context context) {
        pref = context.getSharedPreferences(PREF_KEY, context.MODE_PRIVATE);
    }

    public static String getBestProvider(LocationManager locationManager) {
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_COARSE);
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        String provider = locationManager.getBestProvider(criteria, true);
        return provider;
    }

    public static MyLocationListener getStaticLocation(Context context) {
        MyLocationListener ll = new MyLocationListener(context);
        ll.read();
        return ll;
    }

    private void save() {
        if(available) {
            editor = pref.edit();
            editor.putFloat(KEY_LAT, (float) lat);
            editor.putFloat(KEY_LON, (float) lon);
            editor.commit();
        }
    }

    private void read() {
        this.lat = pref.getFloat(KEY_LAT, (float) 0.0);
        this.lon = pref.getFloat(KEY_LON, (float) 0.0);
        if(lat == 0.0 && lon == 0.0) {
            this.available = false;
        } else {
            this.available = true;
        }
        Log.d("RecommendApps", "[MyLocationListener.read] Lat=" + lat + ", Lon=" + lon);
    }

    @Override
    public void onLocationChanged(Location location) {
        this.lat = location.getLatitude();
        this.lon = location.getLongitude();
        this.available = true;
        save();
        Log.d("RecommendApps", "[MyLocationListener.onLocationChanged] Lat=" + lat + ", Lon=" + lon);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.d("RecommendApps", "[MyLocationListener.onStatusChanged]" + provider);
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.d("RecommendApps", "[MyLocationListener.onProviderEnabled]" + provider);
    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.d("RecommendApps", "[MyLocationListener.onProviderDisabled] " + provider);
    }
}
