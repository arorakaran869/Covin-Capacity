package com.dwitsolutions.cowincaptest;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.dwitsolutions.cowincaptest.dao.CoWinDao;
import com.dwitsolutions.cowincaptest.dao.CoWinDaoRestImpl;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import java.util.Date;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    AppCompatButton bypincode,bycity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);




        bypincode = findViewById(R.id.bypincode);
        bycity = findViewById(R.id.bycity);
        bypincode.setOnClickListener(this);
        bycity.setOnClickListener(this);



        //coWinDao.fetchStates();
        //coWinDao.fetchDistricts(20l);
        //coWinDao.fetchCenters("328");
    }


    @Override
    public void onClick(View v) {

        switch (v.getId())
        {
            case R.id.bypincode:
                startActivity(new Intent(MainActivity.this,PinCodeActivity.class));
                break;
            case R.id.bycity:
                startActivity(new Intent(MainActivity.this,CityActivity.class));
                break;

        }

    }
}