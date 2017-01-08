package com.g54mdp.cw2.activitytracker;

import android.content.Intent;
import android.database.Cursor;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class RecordDetails extends AppCompatActivity {

    // Class variables
    private final String CLA = "RRS RecordDetails";
    private final String NUMERIC_FORMAT="###,###.##";
    private Cursor cursor;

    // Map URL building
    private final String MAP_URL = "http://maps.google.com/maps";
    private final String MAP_ADDRESS_1 = "?saddr=";
    private final String MAP_ADDRESS_2 = "&daddr=";
    private final String MAP_ADDRESS_N = "+to:";
    private final String MAP_WALKING = "&dirflg=w";
    private final int MAP_MAX_LOCATIONS = 25;

    // ## Lifecycle management ## //

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_details);

        Bundle bundle = getIntent().getExtras();
        String dayFo = bundle.getString(RecordViewer.TAG_DAY_FORMATED);
        String dayFi = bundle.getString(RecordViewer.TAG_DAY_FILTER);

        setGUI(dayFo, dayFi);
    }

    // ## Class methods ## //

    /**
     * Method used to retrieved and process information from the content provider in order to be
     * shown in the UI
     * @param dayFormatted
     * @param dayFilter
     */
    private void setGUI(String dayFormatted, String dayFilter) {
        TextView tvTitle = (TextView) findViewById(R.id.tv_title);
        TextView tvDistance = (TextView) findViewById(R.id.tv_distance);
        TextView tvTime = (TextView) findViewById(R.id.tv_time);
        TextView tvSpeed = (TextView) findViewById(R.id.tv_speed);
        TextView tvHeight = (TextView) findViewById(R.id.tv_height);

        cursor = getInfoFromDay(dayFilter);

        float fDistance = getTotalDistance(cursor);
        float fTime     = getTotalTime(cursor);
        float fSpeed    = getSpeed(fDistance,fTime);
        float fHeight   = getClimbedHeight(cursor);

        String sDistance = numberFormatter(fDistance) + " mts.";
        String sTime     = numberFormatter(fTime)     + " min";
        String sSpeed    = numberFormatter(fSpeed)    + " mt/mi";
        String sHeight   = numberFormatter(fHeight)   + " mts.";

        tvTitle.setText(dayFormatted);
        tvDistance.setText(sDistance);
        tvTime.setText(sTime);
        tvSpeed.setText(sSpeed);
        tvHeight.setText(sHeight);
    }

    /**
     * Formats a number to be shown in the UI
     * @param aNumber
     * @return
     */
    private String numberFormatter(float aNumber){
        DecimalFormat myFormatter = new DecimalFormat(NUMERIC_FORMAT);
        return myFormatter.format(aNumber);
    }

    /**
     * Calculates climbing
     * @param cursor
     * @return
     */
    private float getClimbedHeight(Cursor cursor){

        float distanceClimbed = 0f;
        Double previousHeight = null;
        Double currentHeight = null;

        if (cursor.moveToFirst()) {

            cursor.moveToFirst();
            while (cursor.isAfterLast() == false) {
                currentHeight = cursor.getDouble(4);

                if (previousHeight != null) {
                    if(currentHeight>previousHeight){
                        distanceClimbed += (currentHeight.floatValue() - previousHeight.floatValue());
                    }
                }

                previousHeight = currentHeight;
                cursor.moveToNext();
            }
        }

        return distanceClimbed;
    }

    /**
     * Calculates speed
     * @param distance
     * @param time
     * @return
     */
    private float getSpeed(float distance, float time){
        float x = distance/time;
        Log.d(CLA,"speed: " + x);
        return x;
    }

    /**
     * Calculates time between first and last record
     * @param cursor
     * @return
     */
    private float getTotalTime(Cursor cursor){
        float min=0f;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DBHelper.DB_DATE_FORMAT);

        if (cursor.moveToFirst()){

            try {
                cursor.moveToFirst();
                Date date1 = simpleDateFormat.parse(cursor.getString(1));
                Log.d(CLA,"d1: "+cursor.getString(1));

                cursor.moveToLast();
                Date date2 = simpleDateFormat.parse(cursor.getString(1));
                Log.d(CLA,"d2: "+cursor.getString(1));

                long d = date2.getTime() - date1.getTime();
                Log.d(CLA,"diff long: "+ d);

                float f = (float) d;
                Log.d(CLA,"diff float: "+ f);

                min = f/60000; // min = (f/1000)/60;

            } catch (ParseException e) {
                Log.e(CLA,e.getMessage());
            }
        }

        return min;
    }

    /**
     * Calculates distance between each record and returns the overall addition
     * @param cursor
     * @return overall addition
     */
    private float getTotalDistance(Cursor cursor) {

        Location locationPre = null;
        Location locationNow = new Location("");

        float distance = 0f;

        if (cursor.moveToFirst()) {

            cursor.moveToFirst();
            while (cursor.isAfterLast() == false) {

                locationNow.setLatitude(cursor.getDouble(2));
                locationNow.setLongitude(cursor.getDouble(3));
                locationNow.setAltitude(cursor.getDouble(4));

                if (locationPre != null) {
                    distance += locationPre.distanceTo(locationNow);
                }

                locationPre = new Location(locationNow);

                cursor.moveToNext();
            }
        }

        return distance;
    }

    /**
     * Obtains a cursor from the content provider to be used in the whole class for further
     * data analysis
     * @param day
     * @return
     */
    private Cursor getInfoFromDay(String day) {
        String[] projection = new String[]{
                ProviderContract._ID,
                ProviderContract.DATE_TIME_ST,
                ProviderContract.LOCATION_LAT,
                ProviderContract.LOCATION_LON,
                ProviderContract.LOCATION_ALT
        };

        return getContentResolver().query(ProviderContract.LOCATION_URI_ID, projection, day, null, null);
    }

    /**
     * Build an URL to show the followed route in a google map regarding the info contained in the
     * class cursor
     * @return URL to be used in an implicit intent
     */
    private String getUrlFollowedRoute(){

        String url = MAP_URL;
        int recordCounter = 0;

        recordCounter = (int)(cursor.getCount()/MAP_MAX_LOCATIONS);
        String locations[] = new String[MAP_MAX_LOCATIONS];

        cursor.moveToFirst();

        locations[0] = cursor.getDouble(2) + "," + cursor.getDouble(3);

        int i = 1;
        int moveCursorTo = recordCounter;
        while (i<=(MAP_MAX_LOCATIONS-2)){
            cursor.moveToFirst();
            cursor.move(moveCursorTo);
            locations[i] = cursor.getDouble(2) + "," + cursor.getDouble(3);
            moveCursorTo += recordCounter;
            i++;
        }

        cursor.moveToLast();
        locations[MAP_MAX_LOCATIONS-1] = cursor.getDouble(2) + "," + cursor.getDouble(3);

        for (int a=0; a<locations.length;a++){
            Log.d(CLA, "" + a + " = " + locations[a]);
            switch (a){
                case 0:
                    url += MAP_ADDRESS_1 + locations[0];
                    break;
                case 1:
                    url += MAP_ADDRESS_2 + locations[1];
                    break;
                default:
                    url += MAP_ADDRESS_N + locations[a];
            }
        }

        url += MAP_WALKING;
        Log.d(CLA,url);

        return  url;
    }

    /**
     * Determines if there is enough data in the class cursor in relation to saved locations
     * @return
     */
    private boolean isInfoEnoughToShowMap(){
        return (cursor.getCount() > MAP_MAX_LOCATIONS) ? true : false;
    }

    // ## Events management ## //

    /**
     * Action to perform on button clicked event
     * @param view
     */
    public void onMap(View view){

        if (isInfoEnoughToShowMap()){
            String myMap = getUrlFollowedRoute();
            Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(myMap));
            startActivity(i);
        }else{
            Toast.makeText(this,"Not enough info to show a map",Toast.LENGTH_LONG).show();
        }
    }

}
