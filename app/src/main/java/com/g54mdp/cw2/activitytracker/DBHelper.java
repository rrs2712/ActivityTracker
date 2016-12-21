package com.g54mdp.cw2.activitytracker;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by rrs27 on 2016-12-20.
 */

public class DBHelper extends SQLiteOpenHelper {
    private final String CLA = "RRS DBHelper";
    public static final String DB_DATE_FORMAT = "yyyyMMdd-HHmmss";
    public static final String
            KEY_ROWID = "_id",
            KEY_DATE = "date",
            KEY_LAT = "latitude",
            KEY_LON =  "longitude",
            KEY_ALT =  "altitude",
            DB_NAME = "ActivityTrackerDB",
            TABLE_NAME = "GpsProvider";
    public static final int
            DB_VERSION = 1;
    private final String
            CREATE_TABLE = "CREATE TABLE if not exists " + TABLE_NAME + " (" +
            KEY_ROWID + " integer PRIMARY KEY autoincrement," +
            KEY_DATE + " TEXT(20)," +
            KEY_LAT + " DOUBLE," +
            KEY_LON + " DOUBLE," +
            KEY_ALT + " DOUBLE" +
            ");";
    private final String
            DEF_DATE = "'20161220-191252'";
    private final double
            DEF_LAT = 111.11,
            DEF_LON = 222.22,
            DEF_ALT = 0.0;
    private final String
            DEFAULT_INSERT = "INSERT INTO " + TABLE_NAME + " (" +
            KEY_DATE + "," + KEY_LAT + "," + KEY_LON + "," + KEY_ALT + ") VALUES (" +
            DEF_DATE + "," + DEF_LAT + "," + DEF_LON + "," + DEF_ALT + ");"
            ;

    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        Log.d(CLA,"DBHelper instantiated");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(CLA, "onCreate");

        db.execSQL(CREATE_TABLE);
        Log.d(CLA, "DB Created");

        db.execSQL(DEFAULT_INSERT);
        db.execSQL(DEFAULT_INSERT);
        db.execSQL(DEFAULT_INSERT);
        db.execSQL("delete from " + TABLE_NAME + " where _id=2");
        Log.d(CLA, "Default record added");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(CLA, "onUpgrade");

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
}
