package jp.thotta.android.recommendapps;

import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by thotta on 14/12/24.
 */
public class UsageHistory {
    private long rowId;
    private String packageName;
    private int weekday;
    private int start_hour;
    private double lat;
    private double lon;
    private int use_second;

    public UsageHistory(long rowId, String packageName) {
        this.rowId = rowId;
        this.packageName = packageName;
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
                "packageName=" + packageName + ", " +
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

    // Check if the input packageName is different from current packageName or not.
    public boolean isSameApp(String packageName) {
        return (packageName.equals(this.packageName));
    }

    //// Static methods
    // Create new record
    public static UsageHistory create(String packageName, double lat, double lon, SQLiteDatabase db) {
        ContentValues values = new ContentValues();
        Log.d("RecommendApps", "[UsageHistory.create] " +
                "packageName=" + packageName + ", " +
                "lat=" + lat + ", " +
                "lon=" + lon);
        values.put("package_name", packageName);
        values.put("lat", lat);
        values.put("lon", lon);
        long rowId = db.insert("usage_history", null, values);
        return new UsageHistory(rowId, packageName);
    }

    public static List<AppInfo> getRanking(SQLiteDatabase db, PackageManager pm, double lat, double lon) {
        UsageHistoryFilter filter = UsageHistoryFilter.createFilter(db, lat, lon);
        if(filter.isSelectionNeeded()) {
            return getRankingWithFilter(db, pm, filter.getSelection());
        } else {
            return getOverallRanking(db, pm);
        }
    }

    private static List<AppInfo> getRankingWithFilter(
            SQLiteDatabase db, PackageManager pm, String selection) {
        List<AppInfo> ranking = new LinkedList<AppInfo>();
        String sql =
                "SELECT package_name, SUM(use_second) " +
                        "FROM usage_history " +
                        selection +
                        "GROUP BY package_name " +
                        "ORDER BY SUM(use_second) DESC";
        Log.d("RecommendApps","[UsageHistory.getRankingWithFilter] SQL: " + sql);
        Cursor cursor = db.rawQuery(sql, null);
        while(cursor.moveToNext()) {
            AppInfo appInfo = new AppInfo(cursor.getString(0), cursor.getInt(1), pm);
            ranking.add(appInfo);
        }
        return ranking;
    }

    private static List<AppInfo> getOverallRanking(SQLiteDatabase db, PackageManager pm) {
        List<AppInfo> ranking = new LinkedList<AppInfo>();
        String sql =
                "SELECT package_name, SUM(use_second) " +
                "FROM usage_history " +
                "GROUP BY package_name " +
                "ORDER BY SUM(use_second) DESC";
        Cursor cursor = db.rawQuery(sql, null);
        while(cursor.moveToNext()) {
            AppInfo appInfo = new AppInfo(cursor.getString(0), cursor.getInt(1), pm);
            ranking.add(appInfo);
        }
        return ranking;
    }
}
