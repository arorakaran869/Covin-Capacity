package com.dwitsolutions.cowincaptest;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.dwitsolutions.cowincaptest.adapter.CenterAdapter;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class CenterList extends AppCompatActivity {

    RecyclerView recyclerView;
    CenterAdapter centerAdapter;
    listAdapter listAdapter;
    Intent intent;
    String finalList;
    Bundle bundle;
    boolean doubleBackToExitPressedOnce = false;
    int pincode;
    int age;
    SharedPreferences defaultSharedPreference;
    SharedPreferences.Editor defaultSharedPreferenceEditor;
    private InterstitialAd mInterstitialAd;
    private RequestQueue requestQueue;
    private List<Center> centersList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestQueue = Volley.newRequestQueue(this);
        defaultSharedPreference = PreferenceManager.getDefaultSharedPreferences(this);
        defaultSharedPreferenceEditor = defaultSharedPreference.edit();
        setContentView(R.layout.activity_center_list);
        recyclerView = findViewById(R.id.center_list_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        pincode = defaultSharedPreference.getInt("pincode", 110005);
        age = defaultSharedPreference.getInt("age", 45);
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
                AdRequest adRequest = new AdRequest.Builder().build();

                InterstitialAd.load(CenterList.this, "ca-app-pub-3940256099942544/1033173712", adRequest, new InterstitialAdLoadCallback() {
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
        fetchCenters();
        centerAdapter = new CenterAdapter(getApplicationContext(), centersList);
        recyclerView.setAdapter(centerAdapter);


    }

    private void fetchCenters() {
        Date currentDate = new Date();
        for (int x = 0; x < 7; x++) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(currentDate);
            calendar.add(Calendar.DATE, x);  // number of days to add
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            Date currentDatePlusXDay = calendar.getTime();
            String dateToQuery = sdf.format(currentDatePlusXDay);
            String url = "https://cdn-api.co-vin.in/api/v2/appointment/sessions/public/findByPin?pincode=" + pincode + "&date=" + dateToQuery;
            getCentersList(url);
        }

    }

    public void getCentersList(String url) {
        try {
            Log.d("Center-Activity", url);
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                    Request.Method.GET,
                    url,
                    null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                boolean isDataPresentForTheDay = false;
                                String actualResponse = response.getJSONArray("sessions").toString();
                                ObjectMapper mapper = new ObjectMapper();
                                List<Center> apiResponseCenters = mapper.readValue(actualResponse, new TypeReference<ArrayList<Center>>() {
                                });
                                for (Center center : apiResponseCenters) {
                                    if (center.getAvailableCapacity() > 0 && center.getMinAge() >= age)
                                        centersList.add(center);
                                    centerAdapter = new CenterAdapter(getApplicationContext(), centersList);
                                    recyclerView.setAdapter(centerAdapter);

                                }
                            } catch (JSONException | IOException e) {
                                e.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e("TAG error", "on Response Error" + url);
                        }
                    }
            );
            requestQueue.add(jsonObjectRequest);
        } catch (Exception e) {
            Log.d("TAG", e.getMessage());
        }
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


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


}