package com.g54mdp.cw2.activitytracker;

import android.net.Uri;

/**
 * Created by rrs27 on 2016-12-20.
 */

public class ProviderContract {

    public static final String AUTHORITY = "com.g54mdp.cw2.activitytracker.ConProvider";

    public static final Uri LOCATION_URI = Uri.parse("content://"+AUTHORITY+"/location");
    public static final Uri LOCATION_URI_ID = Uri.parse("content://"+AUTHORITY+"/location/n/");

    public static final String _ID = DBHelper.KEY_ROWID;
    public static final String DATE_TIME_ST = DBHelper.KEY_DATE;
    public static final String LOCATION_LAT = DBHelper.KEY_LAT;
    public static final String LOCATION_LON = DBHelper.KEY_LON;
    public static final String LOCATION_ALT = DBHelper.KEY_ALT;

    public static final String CONTENT_TYPE_SINGLE = "vnd.android.cursor.item/ConProvider.data.text";
    public static final String CONTENT_TYPE_MULTIPLE = "vnd.android.cursor.dir/ConProvider.data.text";
}
