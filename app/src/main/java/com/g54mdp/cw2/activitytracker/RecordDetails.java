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

    private final String CLA = "RRS RecordDetails";
    private final String NUMERIC_FORMAT="###,###.##";
    private final String MAP_URL = "http://maps.google.com/maps";
    private final String MAP_ADDRESS_1 = "?saddr=";
    private final String MAP_ADDRESS_2 = "&daddr=";
    private final String MAP_ADDRESS_N = "+to:";
    private final String MAP_WALKING = "&dirflg=w";

    private final int MAP_MAX_LOCATIONS = 25;

    private Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_details);

        Bundle bundle = getIntent().getExtras();
        String dayFo = bundle.getString(RecordViewer.TAG_DAY_FORMATED);
        String dayFi = bundle.getString(RecordViewer.TAG_DAY_FILTER);

        setGUI(dayFo, dayFi);
    }

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

    private String numberFormatter(float aNumber){
        DecimalFormat myFormatter = new DecimalFormat(NUMERIC_FORMAT);
        return myFormatter.format(aNumber);
    }

    private float getClimbedHeight(Cursor cursor){

        float distanceClimbed = 0f;
        Double previousHeight = null;
        Double currentHeight = null;

        if (cursor.moveToFirst()) {

            cursor.moveToFirst();
            while (cursor.isAfterLast() == false) {

                currentHeight = cursor.getDouble(4);
//                Log.d(CLA, "Altitude now " + currentHeight);

                if (previousHeight != null) {
//                    Log.d(CLA, "Altitude pre " + previousHeight);

                    if(currentHeight>previousHeight){
                        distanceClimbed += (currentHeight.floatValue() - previousHeight.floatValue());
//                        Log.d(CLA, "Climbed (mts) = " + distanceClimbed);
                    }

                }

                previousHeight = currentHeight;

                cursor.moveToNext();
            }
        }

        Log.d(CLA, "Total distance (mts) = " + distanceClimbed);

        return distanceClimbed;
    }

    private float getSpeed(float distance, float time){
        float x = distance/time;
        Log.d(CLA,"speed: " + x);
        return x;
    }

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
//                Log.d(CLA, "Now: LAT=" + locationNow.getLatitude() + "\tLON=" + locationNow.getLongitude());
                if (locationPre != null) {
//                    Log.d(CLA, "Longitude pre " + locationPre.getLongitude());
                    distance += locationPre.distanceTo(locationNow);
//                    Log.d(CLA, "Distance (mts) = " + distance);
                }

                locationPre = new Location(locationNow);

                cursor.moveToNext();
            }
        }

        Log.d(CLA, "Total distance (mts) = " + distance);

        return distance;
    }

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


    public void onMap(View view){

        if (isInfoEnoughToShowMap()){
            String myMap = getUrlFollowedRoute();
            Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(myMap));
            startActivity(i);
        }else{
            Toast.makeText(this,"Not enough info to show a map",Toast.LENGTH_LONG).show();
        }


//        String loc1 = "52.935476666666666,-1.2099366666666667";
//        String loc2 = "52.93706,-1.2152183333333333";
//        String loc3 = "52.94745833333333,-1.18347";
//        String loc4 = "52.93431999999999,-1.20717";
//
//        String navigationUrl = "http://maps.google.com/maps?saddr="+loc1+"&daddr="+loc2+"+to:"+loc3+"+to:"+loc4;
//        Intent navIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(navigationUrl));
//        startActivity(navIntent);

    }

    private String getUrlFollowedRoute(){

        String url = MAP_URL;
        int recordCounter = 0;

//        if (cursor.getCount()>MAP_MAX_LOCATIONS){
            recordCounter = (int)(cursor.getCount()/MAP_MAX_LOCATIONS);
            String locations[] = new String[MAP_MAX_LOCATIONS];
//            Log.d(CLA,"Cursor=" + cursor.getCount() + "\tMax loc=" + MAP_MAX_LOCATIONS + "\tFactor=" + recordCounter);

//            if (cursor.moveToFirst()) {

            cursor.moveToFirst();
//            Log.d(CLA, "First: " + cursor.getDouble(2) + "   " + cursor.getDouble(3));
            locations[0] = cursor.getDouble(2) + "," + cursor.getDouble(3);

            int i = 1;
            int moveCursorTo = recordCounter;
            while (i<=(MAP_MAX_LOCATIONS-2)){
//                try {
//                    Log.d(CLA,"i=" + i + " moveCursorTo=" + moveCursorTo);
                    cursor.moveToFirst();
                    cursor.move(moveCursorTo);
//                    Log.d(CLA, "" + i + ":" + cursor.getDouble(2) + "   " + cursor.getDouble(3));
                    locations[i] = cursor.getDouble(2) + "," + cursor.getDouble(3);
                    moveCursorTo += recordCounter;
                    i++;
//                } catch (Exception e) {
//                    Log.d(CLA,e.getMessage());
//                    break;
//                }
            }

            cursor.moveToLast();
//            Log.d(CLA, "Last: " + cursor.getDouble(2) + "   " + cursor.getDouble(3));
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
//            }
//        }
        Log.d(CLA,url);

        return  url;
    }

    private boolean isInfoEnoughToShowMap(){
        return (cursor.getCount() > MAP_MAX_LOCATIONS) ? true : false;
    }
}
