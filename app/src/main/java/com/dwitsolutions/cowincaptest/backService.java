package com.dwitsolutions.cowincaptest;

import android.app.Service;
import android.content.Intent;
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

        cdt = new CountDownTimer(30000000, 10000) {
            @Override
            public void onTick(long millisUntilFinished) {

               // Log.d("TAG", "Countdown seconds remaining: " + millisUntilFinished / 1000);
                MainActivity.checkcentersdata();
            }

            @Override
            public void onFinish() {
                Log.i("TAG", "Timer finished");
            }
        };

        cdt.start();
    }
}