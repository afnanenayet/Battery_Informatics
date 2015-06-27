package com.enayet.powinfo;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;


public class MainActivity extends Activity {
    private String[] mBatteryStats = {"percentage", "capacity", "current", "health", "tempreature", "voltage", "technology", "status", "power", "mAh",}; //TODO: add back energy to the array
    private ArrayAdapter<String> mAdapter;
    private BroadcastReceiver mReceiver = new BroadcastReceiver() { //broadcast receiver gets realtime battery info
        @Override
        public void onReceive(Context context, Intent intent) {

            BatteryManager mBatManager = new BatteryManager(); //to get ints that can't be accessed through the intent (battery manager api)
            String mPresent = "";
            //battery present?
            mBatteryStats[0] = getString(R.string.battery_present_prompt) + intent.getIntExtra(BatteryManager.EXTRA_PRESENT, 0);
            if (intent.getIntExtra(BatteryManager.EXTRA_PRESENT, 0) == 0) {
                mPresent = getString(R.string.yes);
            } else {
                mPresent = getString(R.string.no);
            }
            mBatteryStats[0] = getString(R.string.battery_present_prompt) + mPresent;
            //battery percentage
            int mLevel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
            mBatteryStats[1] = getString(R.string.battery_percentage_prompt) + mLevel + getString(R.string.percent_character);

            //instant current flow
            mBatteryStats[2] = getString(R.string.instant_currentflow_prompt) + mBatManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_NOW) + getString(R.string.micro_amperes);

            //battery health
            int strHealth = -1;
            int mHealth = intent.getIntExtra(BatteryManager.EXTRA_HEALTH, 0);
            switch (mHealth) {
                case 1:
                    strHealth = R.string.health_unknown;
                    break;
                case 2:
                    strHealth = R.string.health_good;
                    break;
                case 3:
                    strHealth = R.string.health_overheat;
                    break;
                case 4:
                    strHealth = R.string.health_dead;
                    break;
                case 5:
                    strHealth = R.string.health_overvolt;
                    break;
                case 6:
                    strHealth = R.string.health_failure;
                    break;
            }
            mBatteryStats[3] = getString(R.string.battery_health_prompt) + getString(strHealth);

            //battery temperature
            int mTemp = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0);
            float mPtemp = mTemp / 10;
            mBatteryStats[4] = getString(R.string.temperature_prompt) + mPtemp + getString(R.string.degrees_celsius);

            //battery voltage
            int mVoltage = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, 0);
            mBatteryStats[5] = getString(R.string.voltage_prompt) + (mVoltage) + getString(R.string.milivolts);

            //battery_technology
            String mTech = intent.getExtras().getString(BatteryManager.EXTRA_TECHNOLOGY);
            mBatteryStats[6] = getString(R.string.technology_prompt) + mTech;

            //Battery_status
            int mStatus = intent.getIntExtra(BatteryManager.EXTRA_STATUS, 0);
            int mStatusCode = -1;
            //turn int code into readable string for user
            switch (mStatus) {
                case 1:
                    mStatusCode = R.string.status_unknown;
                    break;
                case 2:
                    mStatusCode = R.string.status_charging;
                    break;
                case 3:
                    mStatusCode = R.string.status_discharging;
                    break;
                case 4:
                    mStatusCode = R.string.status_not_charging;
                    break;
                case 5:
                    mStatusCode = R.string.status_unknown;
                    break;
            }
            mBatteryStats[7] = getString(R.string.status_prompt) + getString(mStatusCode);

            //power source
            int mPlugged = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, 0);
            int power_source_string;
            switch (mPlugged) {
                case 1:
                    power_source_string = R.string.ac_charger;
                    break;
                case 2:
                    power_source_string = R.string.usb_charger;
                    break;
                case 4:
                    power_source_string = R.string.wireless_charger;
                    break;
                default:
                    power_source_string = R.string.no_charger;
                    break;
            }
            mBatteryStats[8] = getString(R.string.power_source_prompt) + getString(power_source_string);


            //maximum battery level
            mBatteryStats[9] = getString(R.string.max_battery_prompt) + intent.getIntExtra(BatteryManager.EXTRA_SCALE, 0) + getString(R.string.percent_character); //TODO: see if this should be 0 or 100
            mAdapter.notifyDataSetChanged(); //so the listview can update as its source data is changed
            //remaining battery energy
            // battery_stats[10] = "Remaining energy: " + batman.getIntProperty(BatteryManager.BATTERY_PROPERTY_ENERGY_COUNTER) + " nWh"; //TODO: fix
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.registerReceiver(this.mReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED)); //register receiver to receive battery info
        mAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, mBatteryStats);
        ListView mListView = (ListView) findViewById(R.id.battery_stats_list);
        mListView.setAdapter(mAdapter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        this.unregisterReceiver(mReceiver); //unregister receiver
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            this.unregisterReceiver(mReceiver);
        } catch (IllegalArgumentException e) { //empty b/c this exception means that it has already been unregistered
        }                                      //so we don't have to do anything!
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.registerReceiver(this.mReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED)); //register receiver as activity is back in focus
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

        return super.onOptionsItemSelected(item);
    }
}
