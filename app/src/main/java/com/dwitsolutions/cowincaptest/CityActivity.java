package com.dwitsolutions.cowincaptest;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Spinner;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.dwitsolutions.cowincaptest.dao.CoWinDao;
import com.dwitsolutions.cowincaptest.dao.CoWinDaoRestImpl;
import com.dwitsolutions.cowincaptest.model.State;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class CityActivity extends AppCompatActivity {

    Spinner spinner;
    CoWinDao coWinDao;
    ArrayList<State> stateList = new ArrayList<>();
    RequestQueue requestQueue;
    String statesURL = "https://cdn-api.co-vin.in/api/v2/admin/location/states";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city);
        spinner = findViewById(R.id.autoCompleteCityName);
        requestQueue = Volley.newRequestQueue(this);

        fetchStates();
        //Log.d("TAG",stateList.get(0).getStateName());

    }

    private static final String[] COUNTRIES = new String[] {
            "Belgium", "France", "Italy", "Germany", "Spain"
    };



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

                            Log.d("TAG",stateList.get(0).getStateName());


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


}