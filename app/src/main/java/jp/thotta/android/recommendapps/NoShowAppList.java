package jp.thotta.android.recommendapps;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Map;

/**
 * Created by thotta on 14/12/26.
 */
public class NoShowAppList {
    public static final String PREF_KEY = "NoShowAppList";
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;

    public NoShowAppList(Context context) {
        pref = context.getSharedPreferences(PREF_KEY, context.MODE_PRIVATE);
    }

    public void add(String packageName) {
        editor = pref.edit();
        editor.putBoolean(packageName, true);
        editor.commit();
    }

    public void del(String packageName) {
        editor = pref.edit();
        editor.putBoolean(packageName, false);
        editor.commit();
    }

    public boolean isNoShow(String packageName) {
        return pref.getBoolean(packageName, false);
    }

    public void clear() {
        editor = pref.edit();
        editor.clear();
        editor.commit();
    }
}
