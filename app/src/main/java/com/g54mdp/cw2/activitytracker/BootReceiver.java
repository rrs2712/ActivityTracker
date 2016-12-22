package com.g54mdp.cw2.activitytracker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

public class BootReceiver extends BroadcastReceiver {
    private final String
        CLA = "RRS BootReceiver",
        MSG_AUTO_START_SERVICE = "Auto start service disabled";
//    private LocationService service;
//    private Context context;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(CLA,"onReceive");
//        this.context = context;

//        startAndBindLocationService();
//        service.createNotification();
//        unbindLocationService();

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

    private boolean autoStartService(Context context){
        SharedPreferences settings = context.getSharedPreferences(LocationService.SHARED_PREF, 0);
        return settings.getBoolean(LocationService.SP_AUTO_CREATE,false);
    }

//    private void startAndBindLocationService() {
//        Log.d(CLA,"startAndBindLocationService");
//        Intent serviceIntent = new Intent(context,LocationService.class);
//        context.startService(serviceIntent);
//        bindLocationService(serviceIntent);
//    }
//
//    private void bindLocationService(Intent serviceIntent) {
//        Log.d(CLA,"bindLocationService");
//        context.bindService(serviceIntent,serviceConnection,Context.BIND_AUTO_CREATE);
//    }
//
//    private void unbindLocationService() {
//        Log.d(CLA,"unbindLocationService");
//        if(serviceConnection!=null){
//            context.unbindService(serviceConnection);
//        }
//    }
//
//    private ServiceConnection serviceConnection = new ServiceConnection() {
//        @Override
//        public void onServiceConnected(ComponentName name, IBinder service) {
//            Log.d(CLA, "onServiceConnected");
//            LocationService.LocationServiceBinder binder = (LocationService.LocationServiceBinder) service;
//            service = (IBinder) binder.getService();
//        }
//
//        @Override
//        public void onServiceDisconnected(ComponentName name) {
//            Log.d(CLA,"onServiceDisconnected");
//        }
//    };
}
