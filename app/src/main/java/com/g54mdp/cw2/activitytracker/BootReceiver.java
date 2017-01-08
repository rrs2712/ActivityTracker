package com.g54mdp.cw2.activitytracker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

public class BootReceiver extends BroadcastReceiver {
    private final String CLA = "RRS BootReceiver";
    private final String MSG_AUTO_START_SERVICE = "Auto start service disabled";

    // ## Lifecycle management ## //

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(CLA,"onReceive");

        if(autoStartService(context)){
            Bundle bundle = new Bundle();
            bundle.putBoolean(LocationService.AUTO_CREATE_NOTIFICATION,true);

            Intent serviceIntent = new Intent(context,LocationService.class);
            serviceIntent.putExtras(bundle);

            context.startService(serviceIntent);
        }else {
            Log.d(CLA,MSG_AUTO_START_SERVICE);
        }

    }

    /**
     * Method used to check on sharedpreferences whether to run this app or not.
     * @param context
     * @return true if the user wants this app to run when phone boots, otherwise false.
     */
    private boolean autoStartService(Context context){
        SharedPreferences settings = context.getSharedPreferences(LocationService.SHARED_PREF, 0);
        return settings.getBoolean(LocationService.SP_ON_PHONE_START,false);
    }
}
