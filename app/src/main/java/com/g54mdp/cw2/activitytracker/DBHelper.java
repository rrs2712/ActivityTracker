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

    // Main table
    public static final String DB_NAME = "ActivityTrackerDB";
    public static final String TABLE_NAME = "GpsProvider";
    public static final String DB_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String KEY_ROWID = "_id";
    public static final String KEY_DATE = "date";
    public static final String KEY_LAT = "latitude";
    public static final String KEY_LON =  "longitude";
    public static final String KEY_ALT =  "altitude";


    // Day overview
    public static final String DAY_OVERVIEW_KEY_RECORDS = "_id";
    public static final String DAY_OVERVIEW_KEY_DATE = "date_no_time";
    public static final String DAY_OVERVIEW_FORMAT1 = "'%Y/%m/%d'";
    public static final String DAY_OVERVIEW_FORMAT2 = "'%m/%d/%Y'";
    public static final String DAY_OVERVIEW_FORMAT3 = "'%d/%m/%Y'";
    public static final String DAY_OVERVIEW_FORMAT4 = "'Year:%Y Month:%m Day:%d'";
    public static final String QUERY_DAY_OVERVIEW_1 = "select 'Records saved: '|| count(_id) " + DAY_OVERVIEW_KEY_RECORDS + ", strftime(" + DAY_OVERVIEW_FORMAT1 + ",date) " + DAY_OVERVIEW_KEY_DATE + ", date from GpsProvider group by strftime(" + DAY_OVERVIEW_FORMAT1 + ",date) order by date desc";
    public static final String QUERY_DAY_OVERVIEW_2 = "select 'Records saved: '|| count(_id) " + DAY_OVERVIEW_KEY_RECORDS + ", strftime(" + DAY_OVERVIEW_FORMAT2 + ",date) " + DAY_OVERVIEW_KEY_DATE + ", date from GpsProvider group by strftime(" + DAY_OVERVIEW_FORMAT2 + ",date) order by date desc";
    public static final String QUERY_DAY_OVERVIEW_3 = "select 'Records saved: '|| count(_id) " + DAY_OVERVIEW_KEY_RECORDS + ", strftime(" + DAY_OVERVIEW_FORMAT3 + ",date) " + DAY_OVERVIEW_KEY_DATE + ", date from GpsProvider group by strftime(" + DAY_OVERVIEW_FORMAT3 + ",date) order by date desc";
    public static final String QUERY_DAY_OVERVIEW_4 = "select 'Records saved: '|| count(_id) " + DAY_OVERVIEW_KEY_RECORDS + ", strftime(" + DAY_OVERVIEW_FORMAT4 + ",date) " + DAY_OVERVIEW_KEY_DATE + ", date from GpsProvider group by strftime(" + DAY_OVERVIEW_FORMAT4 + ",date) order by date desc";

    // Day formats
    public static final String QUERY_DAY_FORMATS = "select '1' _id, strftime("+DAY_OVERVIEW_FORMAT1+",'now') f1, strftime("+DAY_OVERVIEW_FORMAT2+",'now') f2, strftime("+DAY_OVERVIEW_FORMAT3+",'now') f3, strftime("+DAY_OVERVIEW_FORMAT4+",'now') f4";

    public static final int
            DB_VERSION = 1;

    private final String
            CREATE_TABLE = "CREATE TABLE if not exists " + TABLE_NAME + " (" +
            KEY_ROWID + " integer PRIMARY KEY autoincrement," +
            KEY_DATE + " TEXT(50)," +
            KEY_LAT + " DOUBLE," +
            KEY_LON + " DOUBLE," +
            KEY_ALT + " DOUBLE" +
            ");";
//    private final String
//            DEF_DATE = "'2016-12-20 19:12:52'";
//    private final double
//            DEF_LAT = 111.11,
//            DEF_LON = 222.22,
//            DEF_ALT = 0.0;
//    private final String
//            DEFAULT_INSERT = "INSERT INTO " + TABLE_NAME + " (" +
//            KEY_DATE + "," + KEY_LAT + "," + KEY_LON + "," + KEY_ALT + ") VALUES (" +
//            DEF_DATE + "," + DEF_LAT + "," + DEF_LON + "," + DEF_ALT + ");" ;

    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        Log.d(CLA,"DBHelper instantiated");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(CLA, "onCreate");

        db.execSQL(CREATE_TABLE);
        Log.d(CLA, "DB Created");

//        db.execSQL(DEFAULT_INSERT);
//        db.execSQL(DEFAULT_INSERT);
//        db.execSQL(DEFAULT_INSERT);
//        db.execSQL("delete from " + TABLE_NAME + " where _id=2");
//        Log.d(CLA, "Default record added");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(CLA, "onUpgrade");

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
}
