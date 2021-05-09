package com.dwitsolutions.cowincaptest;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

import com.dwitsolutions.cowincaptest.MainActivity;

public class backService extends Service {

    CountDownTimer cdt;
    int age;
    SharedPreferences defaultSharedPreference;
    SharedPreferences.Editor defaultSharedPreferenceEditor;

    public backService() {
    }


    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        defaultSharedPreference = PreferenceManager.getDefaultSharedPreferences(this);
        defaultSharedPreferenceEditor = defaultSharedPreference.edit();
        age = defaultSharedPreference.getInt("age",0);

        cdt = new CountDownTimer(1000*60*60*60, 60*1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                PinCodeActivity.checkcentersdata(age);
            }

            @Override
            public void onFinish() {
                Log.i("TAG", "Timer finished");
            }
        };

        cdt.start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        cdt.cancel();
    }
}