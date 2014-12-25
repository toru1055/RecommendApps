package jp.thotta.android.recommendapps;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by thotta on 14/12/25.
 */
public class AppInfoListAdaptor extends ArrayAdapter<AppInfo> {
    public AppInfoListAdaptor(Context context, List<AppInfo> objects) {
        super(context, 0, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null) {
            LayoutInflater layoutInflater =
                    (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.app_record, null);
        }
        AppInfo appInfo = getItem(position);
        ImageView appIcon = (ImageView) convertView.findViewById(R.id.imageView_applicationIcon);
        appIcon.setImageDrawable(appInfo.applicationIcon);
        TextView appName = (TextView) convertView.findViewById(R.id.textView_applicationName);
        appName.setText(appInfo.applicationName);
        TextView packageName = (TextView) convertView.findViewById(R.id.textView_packageName);
        packageName.setText(appInfo.packageName);
        TextView useSecond = (TextView) convertView.findViewById(R.id.textView_useSecond);
        useSecond.setText(String.valueOf(appInfo.useSecond));

        return convertView;
    }
}
