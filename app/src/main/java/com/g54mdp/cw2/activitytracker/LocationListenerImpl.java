package com.g54mdp.cw2.activitytracker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by rrs27 on 2016-12-18.
 */

public class LocationListenerImpl implements LocationListener {

    private final String CLA;
    private Context context;
    Location previousLocation;

    /**
     * Non parameter Class Constructor
     */
    public LocationListenerImpl(Context context) {
        CLA = "RRS LocationListener";
        this.context = context;
    }


    /**
     * Called when the location has changed.
     * <p>
     * <p> There are no restrictions on the use of the supplied Location object.
     *
     * @param location The new location, as a Location object.
     */
    @Override
    public void onLocationChanged(Location location) {
        Log.d(CLA, "onLocationChanged");

        String msg = "";

        if (previousLocation!=null){
//            msg = "Pre Loc" + "\tLAT: " + previousLocation.getLatitude() + "\tLON: " + previousLocation.getLongitude();
//            Log.d(CLA, msg);
//
//            msg = "New Loc" + "\tLAT: " + location.getLatitude() + "\tLON: " + location.getLongitude();
//            Log.d(CLA, msg);
//
//            float dist = previousLocation.distanceTo(location);
//            msg = "Distance between locations: " + dist + " meters.";
//            Log.d(CLA, msg);
        }

        saveRecord(location.getLatitude(),location.getLongitude(),location.getAltitude());
        msg = "New record added";
        Log.d(CLA,msg);

        previousLocation = location;
    }

    private void saveRecord(double lat, double lon, double alt){
        DateFormat df = new SimpleDateFormat(DBHelper.DB_DATE_FORMAT);
        Calendar rightNow = Calendar.getInstance();
        String timeStamp = df.format(rightNow.getTime());

        ContentValues cv = new ContentValues();
        cv.put(ProviderContract.DATE_TIME_ST,timeStamp);
        cv.put(ProviderContract.LOCATION_LAT,lat);
        cv.put(ProviderContract.LOCATION_LON,lon);
        cv.put(ProviderContract.LOCATION_ALT,alt);

        context.getContentResolver().insert(ProviderContract.LOCATION_URI,cv);
    }

    private String getInfo() {
        String[] projection = new String[]{
                ProviderContract._ID,
                ProviderContract.DATE_TIME_ST,
                ProviderContract.LOCATION_LAT,
                ProviderContract.LOCATION_LON,
                ProviderContract.LOCATION_ALT
        };

        String selection = "3";

        Cursor cursor = context.getContentResolver().query(
                ProviderContract.LOCATION_URI,
                projection,
                selection,null,null);

        Log.d(CLA,cursor.toString() );

        String toLog = "";

        if(cursor.moveToFirst())
        {
            do
            {
                toLog += cursor.getString(0); toLog += " | ";
                toLog += cursor.getString(1); toLog += " | ";
                toLog += cursor.getString(2); toLog += " | ";
                toLog += cursor.getString(3); toLog += " | ";
                toLog += cursor.getString(4); toLog += " | ";
            }
            while(cursor.moveToNext());
        }
        return toLog;
    }


    /**
     * Called when the provider status changes. This method is called when
     * a provider is unable to fetch a location or if the provider has recently
     * become available after a period of unavailability.
     *
     * @param provider the name of the location provider associated with this
     *                 update.
     * @param status   {LocationProvider#OUT_OF_SERVICE} if the
     *                 provider is out of service, and this is not expected to change in the
     *                 near future; {LocationProvider#TEMPORARILY_UNAVAILABLE} if
     *                 the provider is temporarily unavailable but is expected to be available
     *                 shortly; and {LocationProvider#AVAILABLE} if the
     *                 provider is currently available.
     * @param extras   an optional Bundle which will contain provider specific
     *                 status variables.
     *                 <p>
     *                 <p> A number of common key/value pairs for the extras Bundle are listed
     *                 below. Providers that use any of the keys on this list must
     *                 provide the corresponding value as described below.
     *                 <p>
     *                 <ul>
     *                 <li> satellites - the number of satellites used to derive the fix
     */
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        String loc = "onStatusChanged" + "\tProvider: " + status;
        Log.d(CLA, loc);
    }


    /**
     * Called when the provider is enabled by the user.
     *
     * @param provider the name of the location provider associated with this
     *                 update.
     */
    @Override
    public void onProviderEnabled(String provider) {
        String loc = "onProviderEnabled" + "\tProvider: " + provider;
        Log.d(CLA, loc);
    }


    /**
     * Called when the provider is disabled by the user. If requestLocationUpdates
     * is called on an already disabled provider, this method is called
     * immediately.
     *
     * @param provider the name of the location provider associated with this
     *                 update.
     */
    @Override
    public void onProviderDisabled(String provider) {
        String loc = "onProviderDisabled" + "\tProvider: " + provider;
        Log.d(CLA, loc);
    }
}
