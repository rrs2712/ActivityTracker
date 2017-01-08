package com.g54mdp.cw2.activitytracker;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RadioButton;

public class Settings extends AppCompatActivity {

    private final String CLA = "RRS Settings";

    // ## Lifecycle management ## //

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(CLA,"onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        setSettings();
    }

    // ## Class methods ## //

    /**
     * Saves user preference regarding whether or not start app when phone boots
     */
    private void saveBootSettings(){
        Log.d(CLA,"saveBootSettings");

        CheckBox ch = (CheckBox) findViewById(R.id.chbox_boot);
        boolean autoCreate = ch.isChecked();

        SharedPreferences settings = getSharedPreferences(LocationService.SHARED_PREF, 0);
        SharedPreferences.Editor editor = settings.edit();

        editor.putBoolean(LocationService.SP_ON_PHONE_START,autoCreate);

        editor.commit();
    }

    /**
     * Saves user preference regarding date format to show in the rest of the app
     * @param formatID
     */
    private void saveDateFormat(int formatID) {
        Log.d(CLA,"saveDateFormat");

        SharedPreferences settings = getSharedPreferences(LocationService.SHARED_PREF, 0);
        SharedPreferences.Editor editor = settings.edit();

        editor.putInt(LocationService.SP_DATE_FORMAT,formatID);

        editor.commit();
    }

    /**
     * Retrieves and sets user saved preferences in the UI. In addition presents such information
     * using current date
     */
    private void setSettings(){
        Log.d(CLA,"setSettings");

        setOnPhoneStartSettings();
        setDateFormatSettings();
    }

    /**
     * Retrieves and sets user saved preferences in the UI regarding the boot phone system event
     */
    private void setOnPhoneStartSettings() {
        SharedPreferences settings = getSharedPreferences(LocationService.SHARED_PREF, 0);

        boolean startOnBoot = settings.getBoolean(LocationService.SP_ON_PHONE_START,false);
        CheckBox ch = (CheckBox) findViewById(R.id.chbox_boot);
        ch.setChecked(startOnBoot);
    }

    /**
     * Retrieves and sets user saved preferences in the UI regarding date format
     */
    private void setDateFormatSettings() {
        Log.d(CLA,"setDateFormatSettings");

        RadioButton rd1 = (RadioButton)findViewById(R.id.radio_btn_format1);
        RadioButton rd2 = (RadioButton)findViewById(R.id.radio_btn_format2);
        RadioButton rd3 = (RadioButton)findViewById(R.id.radio_btn_format3);
        RadioButton rd4 = (RadioButton)findViewById(R.id.radio_btn_format4);

        Cursor cursor = getContentResolver().query(ProviderContract.DAY_FORMAT_URI,null,null,null,null);
        if(cursor.moveToFirst())
        {
            do
            {
                rd1.setText(cursor.getString(1));
                rd2.setText(cursor.getString(2));
                rd3.setText(cursor.getString(3));
                rd4.setText(cursor.getString(4));
            }
            while(cursor.moveToNext());
        }

        SharedPreferences settings = getSharedPreferences(LocationService.SHARED_PREF, 0);
        int formatChoice = settings.getInt(LocationService.SP_DATE_FORMAT,1);

        switch (formatChoice){
            case 2:
                rd2.setChecked(true);
                break;
            case 3:
                rd3.setChecked(true);
                break;
            case 4:
                rd4.setChecked(true);
                break;
            case 1:
            default:
                rd1.setChecked(true);
        }
    }

    // ## Events management ## //

    /**
     * Action to perform when check box is clicked
     * @param v
     */
    public void onCheckBox_Boot(View v){
        Log.d(CLA,"onCheckBox_Boot");

        saveBootSettings();
    }

    /**
     * Action to perform when a radio button is clicked.
     * @param view
     */
    public void onRadioBtns(View view){
        switch(view.getId()) {
            case R.id.radio_btn_format1:
                saveDateFormat(1);
                break;
            case R.id.radio_btn_format2:
                saveDateFormat(2);
                break;
            case R.id.radio_btn_format3:
                saveDateFormat(3);
                break;
            case R.id.radio_btn_format4:
                saveDateFormat(4);
                break;
        }
    }
}
