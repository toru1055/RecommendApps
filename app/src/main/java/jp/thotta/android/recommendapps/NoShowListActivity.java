package jp.thotta.android.recommendapps;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.List;


public class NoShowListActivity extends Activity {
    private NoShowAppList noShowAppList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_show_list);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        noShowAppList = new NoShowAppList(this);
        ListView listView = (ListView) findViewById(R.id.listView);
        showListView(listView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ListView lv = (ListView) parent;
                AppInfo appInfo = (AppInfo) lv.getItemAtPosition(position);
                showDelNoShowDialog(appInfo.packageName, "Delete from No-Show List?");
            }
        });
    }

    private void showListView(ListView listView) {
        List<AppInfo> appInfoList = noShowAppList.getAll();
        AppInfoListAdaptor adaptor = new AppInfoListAdaptor(this, appInfoList);
        listView.setAdapter(adaptor);
    }

    private void showDelNoShowDialog(final String packageName, String dialogMessage) {
        new AlertDialog.Builder(NoShowListActivity.this)
                .setTitle(dialogMessage + " : " + packageName)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        noShowAppList.del(packageName);
                        //NoShowListActivity.this.recreate();
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_no_show_list, menu);
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

        if(id == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
