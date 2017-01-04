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
                ProviderContract.DAY_OVERVIEW_DATE,
                ProviderContract.DAY_OVERVIEW_RECORDS
        };

        String[] ColsToDisplay = new String[]{
                ProviderContract.DAY_OVERVIEW_DATE,
                ProviderContract.DAY_OVERVIEW_RECORDS
        };

        int[] ColResIDs = new int[]{
            R.id.idView,
            R.id.descView
        };

        Cursor cursor = getContentResolver().query(ProviderContract.DAY_OVERVIEW_URI,projection,null,null,null);

        String toLog = "\n";
        if(cursor.moveToFirst())
        {
            do
            {
                toLog += cursor.getString(0); toLog += " | ";
                toLog += cursor.getString(1); toLog += "\n";
            }
            while(cursor.moveToNext());
        }
        Log.d(CLA,toLog);

        dataAdapter = new SimpleCursorAdapter(this,R.layout.db_list_item_layout,cursor,ColsToDisplay,ColResIDs,0);

        final ListView lv = (ListView) findViewById(R.id.lv_db_records);
        lv.setAdapter(dataAdapter);
    }
}
