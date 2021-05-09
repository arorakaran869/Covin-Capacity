package com.dwitsolutions.cowincaptest;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.dwitsolutions.cowincaptest.R;
import com.dwitsolutions.cowincaptest.model.Center;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

import java.util.ArrayList;
import java.util.List;

public class CenterList extends AppCompatActivity {

    ListView listView;
    listAdapter listAdapter;
    Intent intent;
    String finalList;
    Bundle bundle;
    private InterstitialAd mInterstitialAd;
    boolean doubleBackToExitPressedOnce = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_center_list);

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
                AdRequest adRequest = new AdRequest.Builder().build();

                InterstitialAd.load(CenterList.this,"ca-app-pub-3940256099942544/1033173712", adRequest, new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        // The mInterstitialAd reference will be null until
                        // an ad is loaded.
                        mInterstitialAd = interstitialAd;
                        Log.i("TAG", "onAdLoaded");
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error
                        Log.i("TAG", loadAdError.getMessage());
                        mInterstitialAd = null;
                    }
                });

            }
        });

        listView = findViewById(R.id.listview);

        intent = getIntent();
        bundle = intent.getExtras();
        finalList = bundle.getString("data");

//        Log.d("TAG 37",finalList);



        try {
            String actualResponse = finalList;
            ObjectMapper mapper = new ObjectMapper();
            List<Center> centersList = mapper.readValue(actualResponse, new TypeReference<ArrayList<Center>>() {
            });
            Log.d("TAG 43",""+centersList.size());
            listAdapter = new listAdapter(getApplicationContext(),centersList);
            listView.setAdapter(listAdapter);
        }
        catch (Exception e)
        {
            Log.d("TAG exception",e.getMessage());
        }

      //


    }

    @Override
    public void onBackPressed() {

        if (doubleBackToExitPressedOnce) {

            if (mInterstitialAd != null) {
                mInterstitialAd.show(this);
            } else {
                //  Log.d("TAG", "The interstitial ad wasn't ready yet.");
            }
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }
    //show ad and exit



}