package jp.thotta.android.recommendapps;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import jp.thotta.android.recommendapps.R;

public class SettingActivity extends Activity {
    private FilterSetting filterSetting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        filterSetting = new FilterSetting(this);
        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId == R.id.setting_filter_manual) {
                    filterSetting.setIsManual(true);
                } else {
                    filterSetting.setIsManual(false);
                }
                showSettings();
            }
        });
        View.OnClickListener checkBoxClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckBox checkBox = (CheckBox) v;
                boolean checked = checkBox.isChecked();
                filterSetting.setManualFlag(v.getId(), checked);
            }
        };
        CheckBox location = (CheckBox) findViewById(R.id.setting_filter_flag_location);
        CheckBox time = (CheckBox) findViewById(R.id.setting_filter_flag_time);
        CheckBox weekday = (CheckBox) findViewById(R.id.setting_filter_flag_weekday);
        location.setOnClickListener(checkBoxClickListener);
        time.setOnClickListener(checkBoxClickListener);
        weekday.setOnClickListener(checkBoxClickListener);
        showSettings();
    }

    private void showSettings() {
        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        CheckBox location = (CheckBox) findViewById(R.id.setting_filter_flag_location);
        CheckBox time = (CheckBox) findViewById(R.id.setting_filter_flag_time);
        CheckBox weekday = (CheckBox) findViewById(R.id.setting_filter_flag_weekday);
        if(filterSetting.isManual()) {
            radioGroup.check(R.id.setting_filter_manual);
            location.setEnabled(true);
            time.setEnabled(true);
            weekday.setEnabled(true);
            location.setChecked(filterSetting.isLocation());
            time.setChecked(filterSetting.isTime());
            weekday.setChecked(filterSetting.isWeekday());
        } else {
            radioGroup.check(R.id.setting_filter_automatic);
            location.setChecked(false);
            location.setEnabled(false);
            time.setChecked(false);
            time.setEnabled(false);
            weekday.setChecked(false);
            weekday.setEnabled(false);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_setting, menu);
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
