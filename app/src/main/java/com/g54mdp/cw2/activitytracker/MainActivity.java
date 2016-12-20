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
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private final String CLA = "RRS MainActivity";

    private LocationService service;

    private boolean keepServiceRunning;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(CLA, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onStart() {
        Log.d(CLA, "onStart");
        super.onStart();

        startAndBindLocationService();
    }

    @Override
    protected void onStop() {
        Log.d(CLA, "onStop");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Log.d(CLA,"onDestroy");
        super.onDestroy();

        if(keepServiceRunning){
            service.createNotification();
        }
    }

    public void onBtnStart(View view){
        Log.d(CLA, "onBtnStart");

//        startAndBindLocationService();
        Toast.makeText(this,"Now this button does nothing",Toast.LENGTH_SHORT).show();
    }

    public void onBtnStop(View view){
        Log.d(CLA, "onBtnStop");

        if(keepServiceRunning){
            unbindLocationService();
            stopLocationService();
        }else{
            startAndBindLocationService();
        }

    }

    private void manageGUI(){
        Button btn = (Button) findViewById(R.id.btn_stop);

        if(keepServiceRunning){
            btn.setText("Stop service");
        }else{
            btn.setText("Start service");
        }
    }

    private void startAndBindLocationService() {
        Intent serviceIntent = new Intent(MainActivity.this,LocationService.class);
        this.startService(serviceIntent);
        bindLocationService(serviceIntent);
        keepServiceRunning = true;
        manageGUI();
    }

    private void bindLocationService(Intent serviceIntent) {
        this.bindService(serviceIntent,serviceConnection,Context.BIND_AUTO_CREATE);
    }

    private void unbindLocationService() {
        if(serviceConnection!=null){
            unbindService(serviceConnection);
        }
    }

    private void stopLocationService() {
        Intent serviceIntent = new Intent(MainActivity.this,LocationService.class);
        this.stopService(serviceIntent);
        keepServiceRunning = false;
        manageGUI();
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(CLA, "onServiceConnected");
            LocationService.LocationServiceBinder binder = (LocationService.LocationServiceBinder) service;
            MainActivity.this.service = binder.getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(CLA,"onServiceDisconnected");
        }
    };

}
