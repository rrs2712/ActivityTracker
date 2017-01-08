package com.g54mdp.cw2.activitytracker;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import static com.g54mdp.cw2.activitytracker.R.drawable.gps_off;
import static com.g54mdp.cw2.activitytracker.R.drawable.gps_on;
import static com.g54mdp.cw2.activitytracker.R.drawable.ic_play_24dp;
import static com.g54mdp.cw2.activitytracker.R.drawable.ic_stop_24dp;

public class MainActivity extends AppCompatActivity {

    private final String CLA = "RRS MainActivity";

    private LocationService.LocationServiceBinder service;
    private boolean keepServiceRunning;

    // ## Lifecycle management ## //

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(CLA, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startAndBindLocationService();
    }

    @Override
    protected void onDestroy() {
        Log.d(CLA,"onDestroy");
        super.onDestroy();

        if(keepServiceRunning){
            service.createNotification();
        }
    }

    // ## Events management ## //

    /**
     * Toggles between starting and stopping the service on click event
     * @param view
     */
    public void onBtnAction(View view){
        Log.d(CLA, "onBtnStop");

        if(keepServiceRunning){
            unbindLocationService();
            stopLocationService();
        }else{
            startAndBindLocationService();
        }
    }

    /**
     * Launch an intent to see a list of records on click event
     * @param view
     */
    public void onBtnRecords(View view){
        Log.d(CLA, "onBtnRecords");

        Intent i = new Intent(MainActivity.this,RecordViewer.class);
        startActivity(i);
    }

    /**
     * Launch an intent to see the settings UI on click event
     * @param view
     */
    public void onBtnSettings(View view){
        Log.d(CLA, "onBtnSettings");

        Intent i = new Intent(MainActivity.this,Settings.class);
        startActivity(i);
    }

    // ## Class methods ## //

    /**
     * Changes elements on the GUI according to the events
     */
    private void manageGUI(){
        ImageButton btn = (ImageButton) findViewById(R.id.btn_action);
        ImageView img = (ImageView) findViewById(R.id.imgView_GPS);

        if(keepServiceRunning){
            btn.setImageResource(ic_stop_24dp);
            img.setImageResource(gps_on);
        }else{
            btn.setImageResource(ic_play_24dp);
            img.setImageResource(gps_off);
        }
    }

    /**
     * Starts and bind location service without creating a notification
     */
    private void startAndBindLocationService() {
        Bundle bundle = new Bundle();
        bundle.putBoolean(LocationService.AUTO_CREATE_NOTIFICATION,false);

        Intent serviceIntent = new Intent(MainActivity.this,LocationService.class);
        serviceIntent.putExtras(bundle);

        this.startService(serviceIntent);
        bindLocationService(serviceIntent);
        keepServiceRunning = true;
        manageGUI();
    }

    /**
     * Binds to location service
     * @param serviceIntent
     */
    private void bindLocationService(Intent serviceIntent) {
        this.bindService(serviceIntent,serviceConnection,Context.BIND_AUTO_CREATE);
    }

    /**
     * Unbinds from location service
     */
    private void unbindLocationService() {
        if(serviceConnection!=null){
            unbindService(serviceConnection);
        }
    }

    /**
     * Stop location service explicitly and changes GUI elements according to the new state
     */
    private void stopLocationService() {
        Intent serviceIntent = new Intent(MainActivity.this,LocationService.class);
        this.stopService(serviceIntent);
        keepServiceRunning = false;
        manageGUI();
    }

    /**
     * Connects to location service.
     */
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(CLA, "onServiceConnected");
            MainActivity.this.service = (LocationService.LocationServiceBinder) service;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(CLA,"onServiceDisconnected");
        }
    };
}
