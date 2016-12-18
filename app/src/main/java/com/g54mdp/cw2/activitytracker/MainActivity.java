package com.g54mdp.cw2.activitytracker;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends AppCompatActivity {

    private final String CLA = "RRS MainActivity";
    private final long MIN_TIME_LOC_UPDATES_MILLI_SECS = 5;
    private final float MIN_DIST_LOC_UPDATES_METERS = 5;

    private LocationManager locationManager;
    private LocationListenerImpl locationListener;

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

        startLocationManager();
    }

    @Override
    protected void onStop() {
        Log.d(CLA, "onStop");
        super.onStop();

        stopLocationManager();
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
        locationListener = new LocationListenerImpl();

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
}
