package jp.thotta.android.recommendapps;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by thotta on 14/12/29.
 */
public class FilterSetting {
    public static final String PREF_KEY = "FilterSetting";
    public static final String KEY_IS_MANUAL = "is_manual";
    public static final String KEY_LOCATION_FLAG = "flag_location";
    public static final String KEY_TIME_FLAG = "flag_time";
    public static final String KEY_WEEKDAY_FLAG = "flag_weekday";
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private Context context;

    public FilterSetting(Context context) {
        this.context = context;
        this.pref = context.getSharedPreferences(PREF_KEY, context.MODE_PRIVATE);
    }

    public void setManualFlag(int flagId, boolean flag) {
        switch (flagId) {
            case R.id.setting_filter_flag_location:
                setFlag(KEY_LOCATION_FLAG, flag);
                break;
            case R.id.setting_filter_flag_time:
                setFlag(KEY_TIME_FLAG, flag);
                break;
            case R.id.setting_filter_flag_weekday:
                setFlag(KEY_WEEKDAY_FLAG, flag);
                break;
            default:
                return;
        }
    }

    public void setIsManual(boolean isManual) {
        setFlag(KEY_IS_MANUAL, isManual);
    }

    public boolean isManual() {
        return pref.getBoolean(KEY_IS_MANUAL, false);
    }

    public boolean isLocation() {
        return pref.getBoolean(KEY_LOCATION_FLAG, false);
    }

    public boolean isTime() {
        return pref.getBoolean(KEY_TIME_FLAG, false);
    }

    public boolean isWeekday() {
        return pref.getBoolean(KEY_WEEKDAY_FLAG, false);
    }

    public void clearManualSettings() {
        setFlag(KEY_LOCATION_FLAG, false);
        setFlag(KEY_TIME_FLAG, false);
        setFlag(KEY_WEEKDAY_FLAG, false);
    }

    private void setFlag(String key, boolean value) {
        editor = pref.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }
}
