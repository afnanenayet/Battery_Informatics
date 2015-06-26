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

import java.util.ArrayList;

public class MainActivity extends Activity {
    private ArrayList<String> battery_list = new ArrayList<String>();
    private String[] battery_stats = {"percentage", "capacity", "current", "health", "tempreature", "voltage", "technology", "status", "power", "mAh"};
    private ArrayAdapter<String> adapter;
    private BroadcastReceiver batteryInfoReceiver = new BroadcastReceiver() { //broadcast receiver gets realtime battery info
        @Override
        public void onReceive(Context context, Intent intent) {

            //battery health
            String health_string = "Battery health: ";
            int health = intent.getIntExtra(BatteryManager.EXTRA_HEALTH, 0);
            switch (health) {
                case 1:
                    health_string += "Unknown (according to your system)";
                    break;
                case 2:
                    health_string += "Good";
                    break;
                case 3:
                    health_string += "Overheating";
                    break;
                case 4:
                    health_string += "Dead";
                    break;
                case 5:
                    health_string += "Over voltage";
                    break;
                case 6:
                    health_string += "Unspecified failure (according to your system)";
                    break;
            }
            battery_stats[3] = health_string;

            int icon_small = intent.getIntExtra(BatteryManager.EXTRA_ICON_SMALL, 0);

            //battery percentage
            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
            battery_stats[0] = "Battery percentage: " + level + "%";

            //battery temperature
            int temperature = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0);
            float precise_temp = temperature / 10;
            battery_stats[4] = "Temperature: " + precise_temp + "°C";

            //battery voltage
            int voltage = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, 0);
            battery_stats[5] = "Voltage: " + (voltage) + " mV";

            //battery_technology
            String technology = intent.getExtras().getString(BatteryManager.EXTRA_TECHNOLOGY);
            battery_stats[6] = "Technology: " + technology;

            //Battery_status
            int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, 0);
            String status_string = "Error: could not read status";
            //turn int code into readable string for user
            switch (status) {
                case 1:
                    status_string = "Unknown";
                    break;
                case 2:
                    status_string = "Charging";
                    break;
                case 3:
                    status_string = "Discharging";
                    break;
                case 4:
                    status_string = "Not charging";
                    break;
                case 5:
                    status_string = "Battery full";
                    break;
            }
            battery_stats[7] = "Status: " + status_string;

            //power source
            int plugged = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, 0);
            String power_source_string;
            switch (plugged) {
                case 1:
                    power_source_string = "AC charger";
                    break;
                case 2:
                    power_source_string = "USB port";
                    break;
                case 4:
                    power_source_string = "Wireless charger";
                    break;
                default:
                    power_source_string = "No power source";
                    break;
            }
            battery_stats[8] = "Power source: " + power_source_string;

            BatteryManager batman = new BatteryManager(); //to get ints that can't be accessed through the intent (battery manager api)
            //battery capacity
            battery_stats[1] = "Battery capacity (of original): " + batman.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY) + "%";
            //instant current flow
            battery_stats[2] = "Instantaneous current flow: " + batman.getIntProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_NOW) + " μA";
            //maximum battery level
            battery_stats[9] = "Maximum battery level: " + intent.getIntExtra(BatteryManager.EXTRA_SCALE, 0) + "%";
            adapter.notifyDataSetChanged(); //so the listview can update as its source data is changed
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.registerReceiver(this.batteryInfoReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED)); //register receiver to receive battery info
        adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, battery_stats);
        ListView listView = (ListView) findViewById(R.id.battery_stats_list);
        listView.setAdapter(adapter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        this.unregisterReceiver(batteryInfoReceiver); //unregister receiver
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            this.unregisterReceiver(batteryInfoReceiver);
        } catch (IllegalArgumentException e) { //empty b/c this exception means that it has already been unregistered
        }                                      //so we don't have to do anything!
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.registerReceiver(this.batteryInfoReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        adapter.notifyDataSetChanged();
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
