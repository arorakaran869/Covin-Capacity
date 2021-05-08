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

public class MainActivity extends AppCompatActivity {

    static CoWinDao coWinDao;
    EditText pincode;
    int pincodevalue;
    AppCompatButton start,stop;
    public static SharedPreferences sharedPreferences;
    public static SharedPreferences.Editor editor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        sharedPreferences = getSharedPreferences("data", MODE_PRIVATE);
        editor = sharedPreferences.edit();

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        coWinDao = new CoWinDaoRestImpl(requestQueue, getApplicationContext());
        pincode = findViewById(R.id.pincode);

        start = findViewById(R.id.start);
        stop = findViewById(R.id.stop);

        Intent intent = new Intent(MainActivity.this, backService.class);







        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                pincodevalue = Integer.valueOf(pincode.getText().toString());

                editor.putInt("pincode", pincodevalue);
                editor.commit();

                pincode.setVisibility(View.GONE);
                start.setVisibility(View.GONE);
                stop.setVisibility(View.VISIBLE);

                startService(intent);




            }
        });




            stop.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    editor.remove("pincode");
                    stopService(intent);

                    start.setVisibility(View.VISIBLE);
                    stop.setVisibility(View.GONE);
                    pincode.setVisibility(View.VISIBLE);


                }
            });


            if(sharedPreferences.getInt("pincode",0)==0)
            {

                Log.d("TAG shared data","no");

                pincode.setVisibility(View.VISIBLE);
                start.setVisibility(View.VISIBLE);
                stop.setVisibility(View.GONE);

            }

            else {
                pincode.setVisibility(View.GONE);
                start.setVisibility(View.GONE);
                stop.setVisibility(View.VISIBLE);
                Log.d("TAG shared data","yes");
            }







        //coWinDao.fetchStates();
        //coWinDao.fetchDistricts(20l);
        //coWinDao.fetchCenters("328");
    }




    public static void checkcentersdata()
    {
        coWinDao.fetchCenters(sharedPreferences.getInt("pincode",0));

    }


}