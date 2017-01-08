package com.g54mdp.cw2.activitytracker;

import android.content.ContentValues;
import android.content.Context;
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

/**
 * Listener implementation for location
 */
public class LocationListenerImpl implements LocationListener {

    private final String CLA;
    private Context context;

    // ## Class methods ## //

    /**
     * Class Constructor
     */
    public LocationListenerImpl(Context context) {
        CLA = "RRS LocationListener";
        this.context = context;
    }

    /**
     * Saves a record in the database using the content provider and given a specific format to the
     * time stamp
     * @param lat = latitude
     * @param lon = longitude
     * @param alt = altitude
     */
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

    // ## Extended methods implementation ## //

    /**
     * Called when the location has changed.
     * @param location The new location, as a Location object.
     */
    @Override
    public void onLocationChanged(Location location) {
        Log.d(CLA, "onLocationChanged");

        saveRecord(location.getLatitude(),location.getLongitude(),location.getAltitude());
        Log.d(CLA,"New record added");
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
        String loc = "onStatusChanged" + "\tProvider: " + provider;
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
