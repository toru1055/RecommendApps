package jp.thotta.android.recommendapps;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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
        MyLocationListener myLocationListener = MyLocationListener.getStaticLocation(this);
        double lat = 0.0;
        double lon = 0.0;
        if(myLocationListener.available) {
            lat = myLocationListener.lat;
            lon = myLocationListener.lon;
        }
        ListView listView = (ListView) findViewById(R.id.listView);
        List<AppInfo> appRanking = UsageHistory.getRanking(
                dbHelper.getReadableDatabase(),
                getPackageManager(),
                lat,
                lon
        );
        // TODO: Filter appRanking to delete Apps listed in No-Show list.(Use SharedPreferences);
        AppInfoListAdaptor adaptor = new AppInfoListAdaptor(this, appRanking);
        listView.setAdapter(adaptor);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ListView lv = (ListView) parent;
                AppInfo appInfo = (AppInfo) lv.getItemAtPosition(position);
                try {
                    startActivity(appInfo.applicationIntent);
                } catch(NullPointerException e) {
                    Log.d("RecommendApps", "[MainActivity.listView.onItemClick] NullPointerException: " + e.getMessage());
                    showCantOpenErrorDialog();
                }
            }
        });
    }

    private void showCantOpenErrorDialog() {
        new AlertDialog.Builder(MainActivity.this)
                .setTitle("Can't open this app. Add to No-Show List?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).show();
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
        dbHelper.close();
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
