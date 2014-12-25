package jp.thotta.android.recommendapps;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;

/**
 * Created by thotta on 14/12/25.
 */
public class AppInfo {
    public String packageName;
    public int useSecond;
    public String applicationName;
    public Drawable applicationIcon;
    public boolean isApplicationInfoEnable = false;

    public AppInfo(String packageName, int useSecond, PackageManager pm) {
        this.packageName = packageName;
        this.useSecond = useSecond;
        try {
            ApplicationInfo applicationInfo = pm.getApplicationInfo(packageName, PackageManager.GET_META_DATA);
            this.applicationName = applicationInfo.loadLabel(pm).toString();
            this.applicationIcon = pm.getApplicationIcon(packageName);
            isApplicationInfoEnable = true;
        } catch (PackageManager.NameNotFoundException e) {
            isApplicationInfoEnable = false;
            //e.printStackTrace();
        }
    }
}
