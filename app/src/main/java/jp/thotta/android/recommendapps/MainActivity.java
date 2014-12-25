package jp.thotta.android.recommendapps;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;


public class MainActivity extends Activity {
    private MainDBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("RecommendApps", "[MainActivity.onCreate] method was called.");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dbHelper = new MainDBHelper(this);
        this.startService(new Intent(this, RegisterReceiverService.class));
        this.startService(new Intent(this, RecorderService.class));
    }

    @Override
    protected void onResume() {
        super.onResume();
        ListView listView = (ListView) findViewById(R.id.listView);
        List<AppInfo> appRanking = UsageHistory.getRanking(
                dbHelper.getReadableDatabase(),
                getPackageManager(),
                0,
                0
        );
        AppInfoListAdaptor adaptor = new AppInfoListAdaptor(this, appRanking);
        listView.setAdapter(adaptor);
    }

    private void setAppRanking(LinearLayout layout, SQLiteDatabase db) {
        List<AppInfo> appRanking = UsageHistory.getRanking(db, getPackageManager(), 0, 0);
        for(AppInfo appInfo : appRanking) {
            StringBuilder text = new StringBuilder();
            TextView v = new TextView(this);
            text.append(appInfo.applicationName);
            text.append(", " + appInfo.packageName);
            text.append(", " + appInfo.useSecond);
            v.setText(text);
            layout.addView(v, new LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT)
            );
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("RecommendApps", "[MainActivity.onDestroy] method was called.");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
