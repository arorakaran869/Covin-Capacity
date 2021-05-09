package com.dwitsolutions.cowincaptest;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.StaticLayout;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.airbnb.lottie.LottieAnimationView;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.dwitsolutions.cowincaptest.dao.CoWinDao;
import com.dwitsolutions.cowincaptest.dao.CoWinDaoRestImpl;

import java.util.Date;

public class PinCodeActivity extends AppCompatActivity {

    private TextView pincodeTV,relaxTV;
    AppCompatButton start,stop;
    EditText pincode;
    int pincodevalue;
    Intent intent;
    RequestQueue requestQueue;
    static CoWinDao coWinDao;
    LottieAnimationView relax;
    RadioGroup ageradiogroup;
    int age=0;

    static SharedPreferences defaultSharedPreference;
    static SharedPreferences.Editor defaultSharedPreferenceEditor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        defaultSharedPreference = PreferenceManager.getDefaultSharedPreferences(this);
        defaultSharedPreferenceEditor = defaultSharedPreference.edit();
        setContentView(R.layout.activity_pin_code);

        start = findViewById(R.id.startbypincode);
        stop = findViewById(R.id.stopbypincode);
        pincode = findViewById(R.id.pincode);
        pincodeTV = findViewById(R.id.pincodeTV);
        relax = findViewById(R.id.lottieanimerelax);
        relaxTV = findViewById(R.id.relaxTV);
        ageradiogroup = findViewById(R.id.ageradiogroup);

        ageradiogroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                switch (checkedId)
                {
                    case R.id.age18:
                        age=18;
                        Log.d("TAG","selected age"+age);
                        defaultSharedPreferenceEditor.putInt("age",age);
                        defaultSharedPreferenceEditor.commit();
                        break;
                    case R.id.age45:
                        age=45;
                        defaultSharedPreferenceEditor.putInt("age",age);
                        defaultSharedPreferenceEditor.commit();
                        Log.d("TAG","selected age"+age);
                        break;
                }
            }
        });


        requestQueue = Volley.newRequestQueue(this);
        coWinDao = new CoWinDaoRestImpl(requestQueue, getApplicationContext());

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(age==0){

                    Toast.makeText(PinCodeActivity.this,"Select Age",Toast.LENGTH_LONG).show();

                }
                else {
                    pincodevalue = Integer.valueOf(pincode.getText().toString());

                    defaultSharedPreferenceEditor.putString("type", "pincode");
                    defaultSharedPreferenceEditor.putInt("pincode", pincodevalue);
                    defaultSharedPreferenceEditor.commit();

                    pincode.setVisibility(View.GONE);
                    start.setVisibility(View.GONE);
                    stop.setVisibility(View.VISIBLE);
                    relax.setVisibility(View.VISIBLE);
                    pincodeTV.setVisibility(View.GONE);
                    relaxTV.setVisibility(View.VISIBLE);
                    ageradiogroup.setVisibility(View.GONE);

                    intent = new Intent(PinCodeActivity.this, backService.class);

                    startService(intent);
                }
            }
        });




        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                defaultSharedPreferenceEditor.remove("type");
                defaultSharedPreferenceEditor.remove("pincode");
                defaultSharedPreferenceEditor.commit();
                stopService(intent);

                start.setVisibility(View.VISIBLE);
                stop.setVisibility(View.GONE);
                pincode.setVisibility(View.VISIBLE);
                pincodeTV.setVisibility(View.VISIBLE);
                relax.setVisibility(View.GONE);
                pincode.setText("");
                relaxTV.setVisibility(View.GONE);
                ageradiogroup.setVisibility(View.VISIBLE);


            }
        });


        if(defaultSharedPreference.getInt("pincode",0)==0)
        {



            start.setVisibility(View.VISIBLE);
            stop.setVisibility(View.GONE);
            pincode.setVisibility(View.VISIBLE);
            pincodeTV.setVisibility(View.VISIBLE);
            relax.setVisibility(View.GONE);
            relaxTV.setVisibility(View.GONE);
            ageradiogroup.setVisibility(View.VISIBLE);



        }

        else {
            pincode.setVisibility(View.GONE);
            start.setVisibility(View.GONE);
            stop.setVisibility(View.VISIBLE);
            relax.setVisibility(View.VISIBLE);
            pincodeTV.setVisibility(View.GONE);
            relaxTV.setVisibility(View.VISIBLE);
            ageradiogroup.setVisibility(View.GONE);


            intent = new Intent(PinCodeActivity.this, backService.class);


            startService(intent);



        }







    }

    public static void checkcentersdata(int age)
    {
        coWinDao.fetchCenters(defaultSharedPreference.getInt("pincode",0),defaultSharedPreference.getInt("age",0));

        //coWinDao.fetchCenters(474006);
    }

}