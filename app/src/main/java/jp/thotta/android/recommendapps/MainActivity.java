package jp.thotta.android.recommendapps;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.LinkedList;
import java.util.List;


public class MainActivity extends Activity {
    private static final int ITEM_ID = 0;
    private MainDBHelper dbHelper;
    private NoShowAppList noShowAppList;
    private FilterSetting filterSetting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("RecommendApps", "[MainActivity.onCreate] method was called.");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dbHelper = new MainDBHelper(this);
        this.startService(new Intent(this, RegisterReceiverService.class));
        this.startService(new Intent(this, RecorderService.class));
        noShowAppList = new NoShowAppList(this);
        filterSetting = new FilterSetting(this);
        ListView listView = (ListView) findViewById(R.id.listView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ListView lv = (ListView) parent;
                AppInfo appInfo = (AppInfo) lv.getItemAtPosition(position);
                try {
                    startActivity(appInfo.applicationIntent);
                } catch(NullPointerException e) {
                    Log.d("RecommendApps", "[MainActivity.listView.onItemClick] NullPointerException: " + e.getMessage());
                    showCantOpenErrorDialog(appInfo.packageName);
                }
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener(){
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                ListView lv = (ListView) parent;
                AppInfo appInfo = (AppInfo) lv.getItemAtPosition(position);
                showNoShowDialog(appInfo.packageName, "Add to No-Show List?");
                return true;
            }
        });
        FrameLayout adFrameLayout = (FrameLayout) findViewById(R.id.layout_ad_frame);
        Utility.appendAdView(MainActivity.this, adFrameLayout);
    }

    @Override
    protected void onResume() {
        super.onResume();
        ListView listView = (ListView) findViewById(R.id.listView);
        showListView(listView);
    }

    private void showListView(ListView listView) {
        MyLocationListener myLocationListener = MyLocationListener.getStaticLocation(this);
        double lat = 0.0;
        double lon = 0.0;
        String address = "---";
        if(myLocationListener.available) {
            lat = myLocationListener.lat;
            lon = myLocationListener.lon;
            if(address != null) {
                address = myLocationListener.getAddress(this);
            }
        }

        TextView textView = (TextView) findViewById(R.id.textView);
        TextView locationTextView = (TextView) findViewById(R.id.locationTextView);


        UsageHistoryFilter usageHistoryFilter;
        if(filterSetting.isManual()) {
            usageHistoryFilter =  new UsageHistoryFilter(
                    filterSetting.isLocation(),
                    filterSetting.isTime(),
                    filterSetting.isWeekday(),
                    lat,
                    lon
            );
            textView.setText("Manual Filter(" + usageHistoryFilter.toString() + ")");
        } else {
            usageHistoryFilter = UsageHistoryFilter.createAutoFilter(
                    dbHelper.getReadableDatabase(), lat, lon);
            textView.setText("Automatic Filter(" + usageHistoryFilter.toString() + ")");
        }
        if(usageHistoryFilter.isLocation()) {
            locationTextView.setText("Location=" + address);
        } else {
            locationTextView.setText(" ");
        }
        List<AppInfo> appRanking = UsageHistory.getRanking(
                dbHelper.getReadableDatabase(),
                getPackageManager(),
                usageHistoryFilter
        );
        appRanking = hideNoShowApps(appRanking);
        AppInfoListAdaptor adaptor = new AppInfoListAdaptor(this, appRanking);
        listView.setAdapter(adaptor);

    }

    private List<AppInfo> hideNoShowApps(List<AppInfo> appRanking) {
        List<AppInfo> appRankingFiltered = new LinkedList<AppInfo>();
        for(AppInfo appInfo : appRanking) {
            if(!noShowAppList.isNoShow(appInfo.packageName)) {
                appRankingFiltered.add(appInfo);
            }
        }
        return appRankingFiltered;
    }



    private void showCantOpenErrorDialog(final String packageName) {
        showNoShowDialog(packageName, "Can't open this app. Add to No-Show List?");
    }

    private void showNoShowDialog(final String packageName, String dialogMessage) {
        new AlertDialog.Builder(MainActivity.this)
                .setTitle(dialogMessage + " : " + packageName)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        noShowAppList.add(packageName);
                        //MainActivity.this.recreate();
                        ListView listView = (ListView) findViewById(R.id.listView);
                        showListView(listView);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).show();
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
        menu.add(Menu.NONE, ITEM_ID, Menu.NONE, R.string.no_show_list);
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
            Intent intent = new Intent(this, SettingActivity.class);
            startActivity(intent);
            return true;
        }
        if (id == ITEM_ID) {
            //Toast.makeText(this, getString(R.string.no_show_list), Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, NoShowListActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
