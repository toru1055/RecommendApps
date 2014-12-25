package jp.thotta.android.recommendapps;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * Created by thotta on 14/12/25.
 */
public class UsageHistoryFilter {
    private static final int MINIMUM_CANDIDATE_NUM = 10;
    private static final int CANDIDATE_APPS_TIME = 60;
    private static final int ACCURACY_OF_LOCATION = 3;
    private boolean location;
    private boolean startHour;
    private boolean weekday;
    private double lat;
    private double lon;

    public UsageHistoryFilter(boolean location, boolean startHour, boolean weekday,
                              double lat, double lon) {
        this.location = location;
        this.startHour = startHour;
        this.weekday = weekday;
        this.lat = lat;
        this.lon = lon;
    }

    public String getSelection() {
        String selection = "WHERE 1 = 1 ";
        if(location) {
            selection += "AND round(lat," + ACCURACY_OF_LOCATION + ") = round(" + String.valueOf(lat) + "," + ACCURACY_OF_LOCATION + ") ";
            selection += "AND round(lon," + ACCURACY_OF_LOCATION + ") = round(" + String.valueOf(lon) + "," + ACCURACY_OF_LOCATION + ") ";
        }
        if(startHour) {
            selection += "AND start_hour = strftime('%H', 'now') ";
        }
        if(weekday) {
            selection += "AND weekday = strftime('%w', 'now') ";
        }
        return selection;
    }

    public boolean isSelectionNeeded() {
        return (location||startHour||weekday);
    }

    public static UsageHistoryFilter createFilter(SQLiteDatabase db, double lat, double lon) {
        boolean location = false;
        boolean startHour = false;
        boolean weekday = false;

        if(getLocationCandidateCount(db, lat, lon) >= MINIMUM_CANDIDATE_NUM) {
            location = true;
        }
        if(getHourCandidateCount(db, lat, lon, location) >= MINIMUM_CANDIDATE_NUM) {
            startHour = true;
        }
        if(getWeekdayCandidateCount(db, lat, lon, location, startHour) >= MINIMUM_CANDIDATE_NUM) {
            weekday = true;
        }
        return new UsageHistoryFilter(location, startHour, weekday, lat, lon);
    }

    private static int getLocationCandidateCount(SQLiteDatabase db, double lat, double lon) {
        String sql = "SELECT package_name FROM usage_history WHERE 1 = 1 ";
        sql += "AND round(lat," + ACCURACY_OF_LOCATION + ") = round(" + String.valueOf(lat) + "," + ACCURACY_OF_LOCATION + ") ";
        sql += "AND round(lon," + ACCURACY_OF_LOCATION + ") = round(" + String.valueOf(lon) + "," + ACCURACY_OF_LOCATION + ") ";
        sql += "GROUP BY package_name HAVING SUM(use_second) > " + String.valueOf(CANDIDATE_APPS_TIME);
        Cursor cursor = db.rawQuery(sql, null);
        Log.d("RecommendApps", "[getLocationCandidateCount] count: " + cursor.getCount());
        return cursor.getCount();
    }
    private static int getHourCandidateCount(SQLiteDatabase db,
                                              double lat,
                                              double lon,
                                              boolean location) {
        String sql = "SELECT package_name FROM usage_history WHERE 1 = 1 ";
        if(location) {
            sql += "AND round(lat," + ACCURACY_OF_LOCATION + ") = round(" + String.valueOf(lat) + "," + ACCURACY_OF_LOCATION + ") ";
            sql += "AND round(lon," + ACCURACY_OF_LOCATION + ") = round(" + String.valueOf(lon) + "," + ACCURACY_OF_LOCATION + ") ";
        }
        sql += "AND start_hour = strftime('%H', 'now') ";
        sql += "GROUP BY package_name HAVING SUM(use_second) > 60";
        Cursor cursor = db.rawQuery(sql, null);
        Log.d("RecommendApps", "[getHourCandidateCount] count: " + cursor.getCount());
        return cursor.getCount();
    }
    private static int getWeekdayCandidateCount(SQLiteDatabase db,
                                             double lat,
                                             double lon,
                                             boolean location,
                                             boolean startHour) {
        String sql = "SELECT package_name FROM usage_history WHERE 1 = 1 ";
        if(location) {
            sql += "AND round(lat," + ACCURACY_OF_LOCATION + ") = round(" + String.valueOf(lat) + "," + ACCURACY_OF_LOCATION + ") ";
            sql += "AND round(lon," + ACCURACY_OF_LOCATION + ") = round(" + String.valueOf(lon) + "," + ACCURACY_OF_LOCATION + ") ";
        }
        if(startHour) {
            sql += "AND start_hour = strftime('%H', 'now') ";
        }
        sql += "AND weekday = strftime('%w', 'now') ";
        sql += "GROUP BY package_name HAVING SUM(use_second) > 60";
        Cursor cursor = db.rawQuery(sql, null);
        Log.d("RecommendApps", "[getWeekdayCandidateCount] count: " + cursor.getCount());
        return cursor.getCount();
    }
}
