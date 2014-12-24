package jp.thotta.android.recommendapps;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by thotta on 14/12/24.
 */
public class MainDBHelper extends SQLiteOpenHelper {
    public MainDBHelper(Context context) {
        super(context, "recommend_apps.db", null, 4);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create tables
        createUsageHistory(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS usage_history");
        createUsageHistory(db);
    }

    private static void createUsageHistory(SQLiteDatabase db) {
        String sql = "CREATE TABLE usage_history(" +
                "app_name TEXT," +
                "lat REAL, " +
                "lon REAL," +
                "weekday INTEGER DEFAULT (strftime('%w', 'now'))," +
                "start_hour INTEGER DEFAULT (strftime('%H', 'now'))," +
                "use_second INTEGER DEFAULT 1," +
                "created_at INTEGER DEFAULT (strftime('%s', 'now'))" +
                ")";
        db.execSQL(sql);
    }
}
