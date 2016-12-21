package com.g54mdp.cw2.activitytracker;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

/**
 * Created by rrs27 on 2016-12-20.
 */

public class ConProvider extends ContentProvider {

    private DBHelper dbHelper = null;
    private final String CLA = "RRS ConProvider";

    private static final UriMatcher uriMatcher;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(ProviderContract.AUTHORITY, "/location", 1);
        uriMatcher.addURI(ProviderContract.AUTHORITY, "/location/n/", 2);
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
            case 2:
                Log.d(CLA, " Case 2:");
                selection = "_id = " + selection;
                Log.d(CLA,selection);
            case 1:
                Log.d(CLA, " Case 1:");
                return db.query(DBHelper.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
            default:
                return null;
        }
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
