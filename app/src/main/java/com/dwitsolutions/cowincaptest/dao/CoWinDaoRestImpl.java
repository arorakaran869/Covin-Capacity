package com.dwitsolutions.cowincaptest.dao;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.dwitsolutions.cowincaptest.CenterList;
import com.dwitsolutions.cowincaptest.R;
import com.dwitsolutions.cowincaptest.model.Center;
import com.dwitsolutions.cowincaptest.model.District;
import com.dwitsolutions.cowincaptest.model.State;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CoWinDaoRestImpl implements CoWinDao {
    //Todo : Constants must be in property file
    public static String rootPath = "https://cdn-api.co-vin.in/api";
    public static String centerPincodeURL = rootPath + "/v2/appointment/sessions/public/findByPin?pincode={0}&date={1}";
    String finalListString;
    ArrayList<Center> finalList;
    SharedPreferences defaultSharedPreference;
    SharedPreferences.Editor defaultSharedPreferenceEditor;
    int count = 0;
    String statesURL = rootPath + "/v2/admin/location/states";
    String districtURL = rootPath + "/v2/admin/location/districts/";
    String centerDistrictIdURL = rootPath + "/v2/appointment/sessions/public/findByDistrict?district_id={0}&date={1}";
    private RequestQueue requestQueue;
    private Context context;

    public CoWinDaoRestImpl(RequestQueue requestQueue, Context _context) {
        this.requestQueue = requestQueue;
        context = _context;
        defaultSharedPreference = PreferenceManager.getDefaultSharedPreferences(context);
        defaultSharedPreferenceEditor = defaultSharedPreference.edit();
    }

    @Override
    public void fetchStates() {

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                statesURL,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.d("TAG", response.toString());
                            String actualResponse = response.getJSONArray("states").toString();
                            ObjectMapper mapper = new ObjectMapper();
                            List<State> stateList = mapper.readValue(actualResponse, new TypeReference<ArrayList<State>>() {
                            });
                            Log.e("Test-States", "" + stateList.size());
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

    @Override
    public void fetchDistricts(String stateName) {
    }

    @Override
    public void fetchDistricts(Long stateId) {
        String districtURL = this.districtURL + stateId;
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
                            Log.e("Test-Districts", "" + districtList.size());
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

    @Override
    public void fetchCenters(Integer pincode, int age) {
        count = 0;
        Date currentDate = new Date();
        for (int x = 0; x < 7; x++) {
            Date currentDatePlusXDay = null;
            LocalDateTime localDateTime = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                localDateTime = currentDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
                localDateTime = localDateTime.plusYears(0).plusMonths(0).plusDays(x);
                currentDatePlusXDay = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
            }
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");

            String centerPincodeURL = MessageFormat.format(CoWinDaoRestImpl.centerPincodeURL, new String[]{pincode.toString(), sdf.format(currentDatePlusXDay)});
            AsyncTask asyncTask = new async(centerPincodeURL, age);
            Log.d("TAG", "Async Execute");
            asyncTask.execute(new String[]{""});
        }


    }

    @Override
    public void fetchCentersbyDistrict(Integer districtId,int age) {

        count = 0;
        Date currentDate = new Date();
        for (int x = 0; x < 7; x++) {
            Date currentDatePlusXDay = null;
            LocalDateTime localDateTime = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                localDateTime = currentDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
                localDateTime = localDateTime.plusYears(0).plusMonths(0).plusDays(x);
                currentDatePlusXDay = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
            }
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            String centerDistrictIdURL = MessageFormat.format(this.centerDistrictIdURL, new String[]{districtId.toString(), sdf.format(currentDatePlusXDay)});
            AsyncTask asyncTask = new async(centerDistrictIdURL, age);
            Log.d("TAG", "Async Execute");
            asyncTask.execute(new String[]{""});
        }


    }

    @Override
    public void fetchCenters(String stateName, String districtName) {



    }


    class async extends AsyncTask<String, String, String> {
        String url;
        int age;


        public async(String url, int age) {
            this.url = url;
            this.age = age;
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
        }

        private void sendNotification() {
//            TODO : Make sure this method will be called only once in a Cycle in the last iteration only
            Intent intent = new Intent(context, CenterList.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0 /* Request code */, intent,
                    PendingIntent.FLAG_ONE_SHOT);
            String channelId = context.getString(R.string.default_notification_channel_id);
            NotificationCompat.Builder notificationBuilder =
                    new NotificationCompat.Builder(context, channelId)
                            .setSmallIcon(R.mipmap.ic_launcher)
                            .setContentTitle("You can Book Center Now")
                            .setContentText("Click to view current availability status")
                            .setAutoCancel(true)
                            .setContentIntent(pendingIntent);
            NotificationManager notificationManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            MediaPlayer mediaPlayer = MediaPlayer.create(context, R.raw.ring);
            // Since android Oreo notification channel is needed.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                // Channel human readable title
                NotificationChannel channel = new NotificationChannel(channelId,
                        "Cloud Messaging Service",
                        NotificationManager.IMPORTANCE_DEFAULT);
                notificationManager.createNotificationChannel(channel);
            }
            mediaPlayer.start();
            notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
        }


        @Override
        protected String doInBackground(String... params) {


            try {
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

                                    for (int i = 0; i < apiResponseCenters.size() && apiResponseCenters.get(i).getAvailableCapacity() > 0 && apiResponseCenters.get(i).getMinAge() == age; i++) {
                                        isDataPresentForTheDay = true;
                                    }
                                    if (isDataPresentForTheDay) {
                                        sendNotification();
                                        requestQueue.cancelAll(new RequestQueue.RequestFilter() {
                                            @Override
                                            public boolean apply(Request<?> request) {
                                                return true;
                                            }
                                        });
                                    }
                                } catch (JSONException | IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.e("TAG error", "on Response Error " + url);
                            }
                        }
                );
                requestQueue.add(jsonObjectRequest);
            } catch (Exception e) {
                Log.d("TAG", e.getMessage());
            }


            return null;
        }
    }
}

