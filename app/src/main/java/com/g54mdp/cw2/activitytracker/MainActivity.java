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

//        saveRecord(0.1,0.2,0.3);
//        String toLog = getInfo();

        String toLog = "Does nothing so far";
        Toast.makeText(this,toLog,Toast.LENGTH_SHORT).show();
    }

//    private void saveRecord(double lat, double lon, double alt){
//        DateFormat df = new SimpleDateFormat(DBHelper.DB_DATE_FORMAT);
//        Calendar rightNow = Calendar.getInstance();
//        String timeStamp = df.format(rightNow.getTime());
//
//        ContentValues cv = new ContentValues();
//        cv.put(ProviderContract.DATE_TIME_ST,timeStamp);
//        cv.put(ProviderContract.LOCATION_LAT,lat);
//        cv.put(ProviderContract.LOCATION_LON,lon);
//        cv.put(ProviderContract.LOCATION_ALT,alt);
//
//        getContentResolver().insert(ProviderContract.LOCATION_URI,cv);
//    }


//    private String getInfo() {
//        String[] projection = new String[]{
//                ProviderContract._ID,
//                ProviderContract.DATE_TIME_ST,
//                ProviderContract.LOCATION_LAT,
//                ProviderContract.LOCATION_LON,
//                ProviderContract.LOCATION_ALT
//        };
//
//        String selection = "3";
//
//        Cursor cursor = getContentResolver().query(
//                ProviderContract.LOCATION_URI,
//                projection,
//                selection,null,null);
//
//        Log.d(CLA,cursor.toString() );
//
//        String toLog = "";
//
//        if(cursor.moveToFirst())
//        {
//            do
//            {
//                toLog += cursor.getString(0); toLog += " | ";
//                toLog += cursor.getString(1); toLog += " | ";
//                toLog += cursor.getString(2); toLog += " | ";
//                toLog += cursor.getString(3); toLog += " | ";
//                toLog += cursor.getString(4); toLog += " | ";
//                Log.d(CLA,toLog);
//            }
//            while(cursor.moveToNext());
//        }
//        return toLog;
//    }

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
