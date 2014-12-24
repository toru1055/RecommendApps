package jp.thotta.android.recommendapps;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * Created by thotta on 14/12/24.
 */
public class UsageHistory {
    private long rowId;
    private String app_name;
    private int weekday;
    private int start_hour;
    private double lat;
    private double lon;
    private int use_second;

    public UsageHistory(long rowId, String app_name) {
        this.rowId = rowId;
        this.app_name = app_name;
    }

    //// Instance methods
    // Calculate use time and Update table
    public void update(SQLiteDatabase db, double lat, double lon) {
        String sql = "UPDATE usage_history SET " +
                "use_second = strftime('%s', 'now') - created_at, " +
                "lat = ?, " +
                "lon = ? " +
                "WHERE rowid = ?";
        String bindArgs[] = new String[3];
        bindArgs[0] = String.valueOf(lat);
        bindArgs[1] = String.valueOf(lon);
        bindArgs[2] = String.valueOf(rowId);
        Log.d("RecommendApps", "[UsageHistory.update] Params: " +
                "app_name=" + app_name + ", " +
                "lat=" + lat + ", " +
                "lon=" + lon + ", " +
                "rowId=" + rowId);
        db.execSQL(sql, bindArgs);
    }
    public void update(SQLiteDatabase db) {
        String sql = "UPDATE usage_history SET " +
                "use_second = strftime('%s', 'now') - created_at " +
                "WHERE rowid = ?";
        String bindArgs[] = new String[1];
        bindArgs[0] = String.valueOf(rowId);
        Log.d("RecommendApps", "[UsageHistory.update] SQL: " + sql + rowId);
        db.execSQL(sql, bindArgs);
    }

    // Check if the input app_name is different from current app_name or not.
    public boolean isSameApp(String app_name) {
        return (app_name.equals(this.app_name));
    }

    //// Static methods
    // Create new record
    public static UsageHistory create(String app_name, double lat, double lon, SQLiteDatabase db) {
        ContentValues values = new ContentValues();
        Log.d("RecommendApps", "[UsageHistory.create] " +
                "app_name=" + app_name + ", " +
                "lat=" + lat + ", " +
                "lon=" + lon);
        values.put("app_name", app_name);
        values.put("lat", lat);
        values.put("lon", lon);
        long rowId = db.insert("usage_history", null, values);
        return new UsageHistory(rowId, app_name);
    }
}
