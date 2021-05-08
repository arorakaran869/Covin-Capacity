package com.dwitsolutions.cowincaptest;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.util.Log;

import com.dwitsolutions.cowincaptest.MainActivity;

public class backService extends Service {

    CountDownTimer cdt;


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

        cdt = new CountDownTimer(1000*60*60*60, 60*1000*5) {
            @Override
            public void onTick(long millisUntilFinished) {

                for (int x = 0; x < 7; x++) {
                    // Log.d("TAG", "Countdown seconds remaining: " + millisUntilFinished / 1000);
                 //   MainActivity.checkcentersdata();
                }


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