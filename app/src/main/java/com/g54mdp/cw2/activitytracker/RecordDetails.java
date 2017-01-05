package com.g54mdp.cw2.activitytracker;

import android.database.Cursor;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class RecordDetails extends AppCompatActivity {

    private final String CLA = "RRS RecordDetails";
    private final String NUMERIC_FORMAT="###,###.##";
//    /**
//     * ATTENTION: This was auto-generated to implement the App Indexing API.
//     * See https://g.co/AppIndexing/AndroidStudio for more information.
//     */
//    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_details);

        Bundle bundle = getIntent().getExtras();
        String dayFo = bundle.getString(RecordViewer.TAG_DAY_FORMATED);
        String dayFi = bundle.getString(RecordViewer.TAG_DAY_FILTER);

        setGUI(dayFo, dayFi);
//        // ATTENTION: This was auto-generated to implement the App Indexing API.
//        // See https://g.co/AppIndexing/AndroidStudio for more information.
//        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    private void setGUI(String dayFormatted, String dayFilter) {
        TextView tvTitle = (TextView) findViewById(R.id.tv_title);
        TextView tvDistance = (TextView) findViewById(R.id.tv_distance);
        TextView tvTime = (TextView) findViewById(R.id.tv_time);
        TextView tvSpeed = (TextView) findViewById(R.id.tv_speed);
        TextView tvHeight = (TextView) findViewById(R.id.tv_height);

        Cursor cursor = getInfoFromDay(dayFilter);

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
//                Calendar calendar1 = Calendar.getInstance();
//                calendar1.setTime(date1);

                cursor.moveToLast();
                Date date2 = simpleDateFormat.parse(cursor.getString(1));
                Log.d(CLA,"d2: "+cursor.getString(1));
//                Calendar calendar2 = Calendar.getInstance();
//                calendar2.setTime(date2);

                long d = date2.getTime() - date1.getTime();
                Log.d(CLA,"diff long: "+ d);

                float f = (float) d;
                Log.d(CLA,"diff float: "+ f);

//                min = (f/1000)/60;
                min = f/60000;

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
//                Log.d(CLA, "Longitude now " + locationNow.getLongitude());
                if (locationPre != null) {
//                    Log.d(CLA, "Longitude pre " + locationPre.getLongitude());
                    distance += locationPre.distanceTo(locationNow);
//                    Log.d(CLA, "Distance (mts) = " + distance);
                }

                locationPre = new Location(locationNow);

                cursor.moveToNext();
            }
////                toLog += cursor.getString(0); toLog += " | "; //id
////                toLog += cursor.getString(1); toLog += " | "; //date
////                toLog += cursor.getString(2); toLog += " | "; //lat
////                toLog += cursor.getString(3); toLog += " | "; //lon
////                toLog += cursor.getString(4); toLog += "\n"; //alt
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

//    /**
//     * ATTENTION: This was auto-generated to implement the App Indexing API.
//     * See https://g.co/AppIndexing/AndroidStudio for more information.
//     */
//    public Action getIndexApiAction() {
//        Thing object = new Thing.Builder()
//                .setName("RecordDetails Page") // TODO: Define a title for the content shown.
//                // TODO: Make sure this auto-generated URL is correct.
//                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
//                .build();
//        return new Action.Builder(Action.TYPE_VIEW)
//                .setObject(object)
//                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
//                .build();
//    }

//    @Override
//    public void onStart() {
//        super.onStart();
//
//        // ATTENTION: This was auto-generated to implement the App Indexing API.
//        // See https://g.co/AppIndexing/AndroidStudio for more information.
//        client.connect();
//        AppIndex.AppIndexApi.start(client, getIndexApiAction());
//    }
//
//    @Override
//    public void onStop() {
//        super.onStop();
//
//        // ATTENTION: This was auto-generated to implement the App Indexing API.
//        // See https://g.co/AppIndexing/AndroidStudio for more information.
//        AppIndex.AppIndexApi.end(client, getIndexApiAction());
//        client.disconnect();
//    }
}
