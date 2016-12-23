package com.g54mdp.cw2.activitytracker;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class RecordViewer extends AppCompatActivity {

    private SimpleCursorAdapter dataAdapter;
    private final String
            CLA = "RRS RecordViewer";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(CLA,"onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_viewer);
    }

    @Override
    protected void onStart() {
        Log.d(CLA,"onStart");
        super.onStart();

        setWidgets();
    }

    private void setWidgets(){
        String[] projection = new String[]{
                ProviderContract._ID,
                ProviderContract.DATE_TIME_ST//,
//                ProviderContract.LOCATION_LAT,
//                ProviderContract.LOCATION_LON,
//                ProviderContract.LOCATION_ALT
        };

        String[] ColsToDisplay = new String[]{
                ProviderContract._ID,
                ProviderContract.DATE_TIME_ST//,
//                ProviderContract.LOCATION_LAT,
//                ProviderContract.LOCATION_LON,
//                ProviderContract.LOCATION_ALT

        };

        int[] ColResIDs = new int[]{
            R.id.idView,
            R.id.descView
        };

        Cursor cursor = getContentResolver().query(ProviderContract.LOCATION_URI,projection,null,null,null);

        dataAdapter = new SimpleCursorAdapter(this,R.layout.db_list_item_layout,cursor,ColsToDisplay,ColResIDs,0);

        final ListView lv = (ListView) findViewById(R.id.lv_db_records);
        lv.setAdapter(dataAdapter);
    }
}
