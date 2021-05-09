package com.dwitsolutions.cowincaptest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import java.util.Timer;
import java.util.TimerTask;

public class Splash extends AppCompatActivity {

    public static SharedPreferences splashSP;
    public static SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        splashSP = PreferenceManager.getDefaultSharedPreferences(this);
        editor = splashSP.edit();

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {

                if(Splash.splashSP.getString("type","none").equals("none"))
                {
                    startActivity(new Intent(Splash.this,MainActivity.class));

                }
                else
                {
                    startActivity(new Intent(Splash.this,PinCodeActivity.class));


                }

                finish();

            }
        }, 3000);







    }
}