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

//    private String SERVICE_STATE = "current_state_of_service_when_using_other_activities";

    private LocationService service;

    private boolean keepServiceRunning;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(CLA, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startAndBindLocationService();

//        if(savedInstanceState!=null){
//            boolean x = savedInstanceState.getBoolean(SERVICE_STATE);
//            Log.d(CLA,"onCreateInstanceState: " + x);
//        }

    }

    @Override
    protected void onStart() {
        Log.d(CLA, "onStart");
        super.onStart();


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

//    public void onBtnStart(View view){
//        Log.d(CLA, "onBtnStart");
//
////        saveRecord(0.1,0.2,0.3);
////        String toLog = getInfo();
//
//        String toLog = "Does nothing so far";
//        Toast.makeText(this,toLog,Toast.LENGTH_SHORT).show();
//    }

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

    public void onBtnAction(View view){
        Log.d(CLA, "onBtnStop");

        if(keepServiceRunning){
            unbindLocationService();
            stopLocationService();
        }else{
            startAndBindLocationService();
        }

    }

    public void onBtnRecords(View view){
        Log.d(CLA, "onBtnRecords");

        Intent i = new Intent(MainActivity.this,RecordViewer.class);
        startActivity(i);
    }

    public void onBtnSettings(View view){
        Log.d(CLA, "onBtnSettings");

        Intent i = new Intent(MainActivity.this,Settings.class);
        startActivity(i);
    }

    private void manageGUI(){
        ImageButton btn = (ImageButton) findViewById(R.id.btn_action);
        ImageView img = (ImageView) findViewById(R.id.imgView_GPS);

        if(keepServiceRunning){
//            btn.setText("Stop");
            btn.setImageResource(ic_stop_24dp);
            img.setImageResource(gps_on);
        }else{
//            btn.setText("Start");
            btn.setImageResource(ic_play_24dp);
            img.setImageResource(gps_off);
        }
    }

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

//    @Override
//    protected void onSaveInstanceState(Bundle outState) {
//        Log.d(CLA,"onSaveInstanceState: " + keepServiceRunning);
//
//        outState.putBoolean(SERVICE_STATE,keepServiceRunning);
//        super.onSaveInstanceState(outState);
//    }
//
//    @Override
//    protected void onRestoreInstanceState(Bundle savedInstanceState) {
//        super.onRestoreInstanceState(savedInstanceState);
//
//        boolean x = savedInstanceState.getBoolean(SERVICE_STATE);
//        Log.d(CLA,"onRestoreInstanceState: " + x);
//    }
}
