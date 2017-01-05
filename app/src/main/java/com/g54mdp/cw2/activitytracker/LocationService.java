package com.g54mdp.cw2.activitytracker;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class LocationService extends Service {

    public static final String
        AUTO_CREATE_NOTIFICATION = "create",
        SHARED_PREF = "com.g54mdp.cw2.activitytracker",
        SP_ON_PHONE_START = "ux_choice_on_phone_start",
        SP_DATE_FORMAT = "ux_date_format_choice";

    private final String CLA = "RRS LocationService";
    private final String TITLE = "Activity Tracker";
    private final String CONTENT = "is tracking";
    private final String MSG = TITLE +" is now tracking in background =)";

    private final IBinder binder = new LocationServiceBinder();

    private LocationManager locationManager;
    private LocationListenerImpl locationListener;

    private final long MIN_TIME_LOC_UPDATES_MILLI_SECS = 5;
    private final float MIN_DIST_LOC_UPDATES_METERS = 5;


    @Override
    public IBinder onBind(Intent intent) {
        Log.d(CLA, "onBind");

        return binder;
    }

    @Override
    public void onCreate() {
        Log.d(CLA, "onCreate");
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        Log.d(CLA, "onDestroy");
        super.onDestroy();

        stopLocationManager();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(CLA, "onStartCommand");

        startLocationManager();

        Bundle bundle = intent.getExtras();
//        boolean createNot = bundle.getBoolean(AUTO_CREATE_NOTIFICATION);
//        if (createNot){
        if (bundle.getBoolean(AUTO_CREATE_NOTIFICATION)){
            createNotification();
            Log.d(CLA,"Notification auto created");
        }

        return Service.START_STICKY;
    }


    private void createNotification(){
        Log.d(CLA,"createNotification");

        Intent mainActivity = new Intent(this, MainActivity.class);
        PendingIntent pi = PendingIntent.getActivity(this,0,mainActivity,0);

        Notification notification = new NotificationCompat.Builder(this)
            .setTicker(MSG)
            .setSmallIcon(R.drawable.ic_notification_bar)
            .setContentTitle(TITLE)
            .setContentText(CONTENT)
            .setContentIntent(pi)
            .setAutoCancel(true) // Notification is automatically canceled when the user clicks it in the panel
            .setOngoing(true) //  Sorted above the regular notifications in the notification panel and do not have an 'X' close button, and are not affected by the "Clear all" button
            .build();

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(0,notification);
    }

    private void stopLocationManager() {
        Log.d(CLA,"stopLocationManager");

        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(
                        this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Log.d(CLA,"Location permissions denied, can't stop LocationManager");
            return;
        }

        try {
            locationManager.removeUpdates(locationListener);
        } catch (Exception e) {
            Log.w(CLA,e.toString());
        }
    }

    private void startLocationManager() {
        Log.d(CLA,"startLocationManager");

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListenerImpl(this);

        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(
                        this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Log.d(CLA,"Location permissions denied, can't start LocationManager");
            return;
        }

        try {
            locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    MIN_TIME_LOC_UPDATES_MILLI_SECS,
                    MIN_DIST_LOC_UPDATES_METERS,
                    locationListener
            );
        } catch (Exception e) {
            Log.w(CLA,e.toString());
        }
    }

    public class LocationServiceBinder extends Binder {
//        LocationService getService(){
//            return LocationService.this;
//        }

        void createNotification(){
            LocationService.this.createNotification();
        }
    }
}
