package com.g54mdp.cw2.activitytracker;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.SharedPreferences;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

/**
 * Created by rrs27 on 2016-12-20.
 */

public class ConProvider extends ContentProvider{

    private DBHelper dbHelper = null;
    private final String CLA = "RRS ConProvider";

    private static final UriMatcher uriMatcher;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(ProviderContract.AUTHORITY, "/location", 1);
        uriMatcher.addURI(ProviderContract.AUTHORITY, "/location/n/", 2);
        uriMatcher.addURI(ProviderContract.AUTHORITY, "/day_overview", 3);
        uriMatcher.addURI(ProviderContract.AUTHORITY, "/day_format", 4);

    }

    @Override
    public boolean onCreate() {
        Log.d(CLA, "onCreate");
        this.dbHelper = new DBHelper(this.getContext(), DBHelper.DB_NAME, null, DBHelper.DB_VERSION);
        return true;
    }

    @Override
    public String getType(Uri uri) {

        String contentType;

        if (uri.getLastPathSegment()==null)
        {
            contentType = ProviderContract.CONTENT_TYPE_MULTIPLE;
        }
        else
        {
            contentType = ProviderContract.CONTENT_TYPE_SINGLE;
        }

        return contentType;
    }


    @Override
    public Uri insert(Uri uri, ContentValues values) {
        Log.d(CLA,"insert");

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String tableName;

        switch(uriMatcher.match(uri))
        {
            case 1:
            default:
                tableName = DBHelper.TABLE_NAME;
                break;
        }

        long id = db.insert(tableName, null, values);
        db.close();
        Uri nu = ContentUris.withAppendedId(uri, id);

        Log.d(CLA, nu.toString());

        getContext().getContentResolver().notifyChange(nu, null);

        return nu;
    }


    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Log.d(CLA,"query");

        Log.d(CLA, uri.toString() + " " + uriMatcher.match(uri));

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        switch(uriMatcher.match(uri))
        {
            case 1:
                Log.d(CLA, " Case 1:");
                return db.query(DBHelper.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
            case 2:
                Log.d(CLA, " Case 2:");
                sortOrder = "date asc";
                selection = "substr(date,0,11) = " + selection;
//                Log.d(CLA,selection+ " | " +  sortOrder) ;
                return db.query(DBHelper.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
            case 3:
                Log.d(CLA, " Case 3:");
                String userFormatChoice = getQueryFormat();
                return db.rawQuery(userFormatChoice,null);
            case 4:
                Log.d(CLA, " Case 4:");
                return db.rawQuery(DBHelper.QUERY_DAY_FORMATS,null);
            default:
                return null;
        }
    }

    private String getQueryFormat(){
        Log.d(CLA,"getQueryFormat");

        String queryFormat = "";

        SharedPreferences settings = getContext().getSharedPreferences(LocationService.SHARED_PREF, 0);
        int formatChoice = settings.getInt(LocationService.SP_DATE_FORMAT,1);

        switch (formatChoice){
            case 2:
                queryFormat = DBHelper.QUERY_DAY_OVERVIEW_2;
                break;
            case 3:
                queryFormat = DBHelper.QUERY_DAY_OVERVIEW_3;
                break;
            case 4:
                queryFormat = DBHelper.QUERY_DAY_OVERVIEW_4;
                break;
            case 1:
            default:
                queryFormat = DBHelper.QUERY_DAY_OVERVIEW_1;
        }

        return queryFormat;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        Log.d(CLA,"update");

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String tableName;

        switch(uriMatcher.match(uri))
        {
            case 1:
            default:
                tableName = DBHelper.TABLE_NAME;
                break;
        }

        int a = db.update(tableName,values,selection,selectionArgs);
        db.close();

        return a;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        Log.d(CLA, "delete");

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String tableName;

        switch(uriMatcher.match(uri))
        {
            case 1:
            default:
                tableName = DBHelper.TABLE_NAME;
                break;
        }

        int a = db.delete(tableName,selection,selectionArgs);
        db.close();

        return a;
    }
}
