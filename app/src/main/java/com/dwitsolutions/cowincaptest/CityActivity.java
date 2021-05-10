package com.dwitsolutions.cowincaptest;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RadioGroup;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;

import com.airbnb.lottie.LottieAnimationView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.dwitsolutions.cowincaptest.dao.CoWinDao;
import com.dwitsolutions.cowincaptest.model.District;
import com.dwitsolutions.cowincaptest.model.State;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.material.progressindicator.CircularProgressIndicator;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CityActivity extends AppCompatActivity {

    LottieAnimationView circularProgressIndicator;
    Spinner spinner,districtSpinner;
    CoWinDao coWinDao;
    ArrayList<State> stateList = new ArrayList<>();
    RequestQueue requestQueue;
    String statesURL = "https://cdn-api.co-vin.in/api/v2/admin/location/states";
    Map<String, Integer> statesMap = new HashMap<>();
    Map<String, Integer> districtMap = new HashMap<>();
    List<String> stateName = new ArrayList<>();
    List<String> districtName = new ArrayList<>();
    AppCompatButton startcityservice;
    static SharedPreferences defaultSharedPreference;
    static SharedPreferences.Editor defaultSharedPreferenceEditor;
    Integer selectedDesId;
    RadioGroup ageradiogroupdistrict;
    int age;
    private final BroadcastReceiver restarter = new Restarter();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city);
        spinner = findViewById(R.id.autoCompleteCityName);
        circularProgressIndicator = findViewById(R.id.progressi);
        districtSpinner = findViewById(R.id.districtSpinner);
        startcityservice = findViewById(R.id.startcityservice);
        ageradiogroupdistrict = findViewById(R.id.ageradiogroupdistrict);

        ageradiogroupdistrict.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
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


        defaultSharedPreference = PreferenceManager.getDefaultSharedPreferences(this);
        defaultSharedPreferenceEditor = defaultSharedPreference.edit();

        requestQueue = Volley.newRequestQueue(this);

        fetchStates();

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                Integer stateId = statesMap.get(stateList.get(position));
                String stateName = (String) spinner.getItemAtPosition(position);
                Integer stateId = statesMap.get(stateName);
                Log.e("TEST", "State Name : " + stateName + " State Id :" + stateId);

                circularProgressIndicator.setVisibility(View.VISIBLE);
                fetchDistricts(stateId);



            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        districtSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String districtName = (String) districtSpinner.getItemAtPosition(position);
                Integer disId = districtMap.get(districtName);
                selectedDesId = disId;


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        //Log.d("TAG",stateList.get(0).getStateName());

        startcityservice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                defaultSharedPreferenceEditor.putString("type","district");
                defaultSharedPreferenceEditor.putInt("districtId",selectedDesId);
                defaultSharedPreferenceEditor.commit();
                startService();
            }
        });

    }

    public void fetchStates() {

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                statesURL,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            String actualResponse = response.getJSONArray("states").toString();
                            ObjectMapper mapper = new ObjectMapper();
                            ArrayList<State> stateList = mapper.readValue(actualResponse, new TypeReference<ArrayList<State>>() {
                            });

                            for (int i = 0; i < stateList.size(); i++) {
                                statesMap.put(stateList.get(i).getStateName(), stateList.get(i).getStateId());
                                stateName.add(stateList.get(i).getStateName());
                            }
                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(CityActivity.this, android.R.layout.simple_list_item_1, stateName);
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spinner.setAdapter(adapter);
                            spinner.setVisibility(View.VISIBLE);
                            circularProgressIndicator.setVisibility(View.GONE);

                            Log.d("TAG", stateList.get(0).getStateName());


                            //set adapter


                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (JsonMappingException e) {
                            e.printStackTrace();
                        } catch (JsonProcessingException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }

        );
        requestQueue.add(jsonObjectRequest);
//        Log.d("TAG",al[0].get(0).getStateName());

    }


    public void fetchDistricts(Integer stateId) {
        districtName = new ArrayList<>();
        String districtURL = "https://cdn-api.co-vin.in/api/v2/admin/location/districts/" +stateId;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                districtURL,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String actualResponse = response.getJSONArray("districts").toString();
                            ObjectMapper mapper = new ObjectMapper();
                            List<District> districtList = mapper.readValue(actualResponse, new TypeReference<ArrayList<District>>() {
                            });
                            Log.e("TAG", "" + districtList.size());

                            for (int i = 0; i < districtList.size(); i++) {
                                districtMap.put(districtList.get(i).getDistrictName(), districtList.get(i).getDistrictId());
                                districtName.add(districtList.get(i).getDistrictName());
                            }
                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(CityActivity.this, android.R.layout.simple_list_item_1, districtName);
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                            districtSpinner.setAdapter(adapter);
                            districtSpinner.setVisibility(View.VISIBLE);
                            circularProgressIndicator.setVisibility(View.GONE);
                            startcityservice.setVisibility(View.VISIBLE);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (JsonMappingException e) {
                            e.printStackTrace();
                        } catch (JsonProcessingException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }
        );
        requestQueue.add(jsonObjectRequest);
    }


    private void startService()
    {
        Intent serviceIntent = new Intent(this, backService.class);
        ContextCompat.startForegroundService(CityActivity.this,serviceIntent);
        registerReceiver(restarter,new IntentFilter());

    }


    private void stopService(){
        Intent serviceIntent = new Intent(this, backService.class);
        getApplicationContext().stopService(serviceIntent);

        unregisterReceiver(restarter);
    }

    @Override
    protected void onDestroy() {
        //stopService(mServiceIntent);
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction("restartservice");
        broadcastIntent.setClass(this, Restarter.class);
        this.sendBroadcast(broadcastIntent);
        super.onDestroy();
    }


}