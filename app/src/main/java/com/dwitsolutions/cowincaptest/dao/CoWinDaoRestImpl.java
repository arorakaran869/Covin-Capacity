package com.dwitsolutions.cowincaptest.dao;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import com.dwitsolutions.cowincaptest.CenterList;
import com.dwitsolutions.cowincaptest.MainActivity;
import com.dwitsolutions.cowincaptest.R;
import com.dwitsolutions.cowincaptest.model.Center;
import com.dwitsolutions.cowincaptest.model.District;
import com.dwitsolutions.cowincaptest.model.State;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;

import java.util.List;

public class CoWinDaoRestImpl implements CoWinDao {
    private RequestQueue requestQueue;
    private Context context;
    String finalListString;

    //Todo : Constants must be in property file
    String rootPath = "https://cdn-api.co-vin.in/api";
    String statesURL = rootPath + "/v2/admin/location/states";
    String districtURL = rootPath + "/v2/admin/location/districts/";
    String centerPincodeURL = rootPath + "/v2/appointment/sessions/public/findByPin?pincode={0}&date={1}";
    String centerDistrictIdURL = rootPath + "/v2/appointment/sessions/public/findByDistrict?district_id={0}&date={1}";

    public CoWinDaoRestImpl(RequestQueue requestQueue, Context _context) {
        this.requestQueue = requestQueue;
        context = _context;
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
                            Log.d("TAG",response.toString());
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
    public void fetchCenters(Integer pincode) {
        Date currentDate = new Date();
        for (int x = 0; x < 7; x++) {
            LocalDateTime localDateTime = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                localDateTime = currentDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                localDateTime = localDateTime.plusYears(0).plusMonths(0).plusDays(x);
            }
            Date currentDatePlusXDay = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                currentDatePlusXDay = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
            }
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");

            String centerPincodeURL = MessageFormat.format(this.centerPincodeURL, new String[]{pincode.toString(), sdf.format(currentDatePlusXDay)});
            Log.d("Pincode URL", centerPincodeURL);

            try{
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                    Request.Method.GET,
                    centerPincodeURL,
                    null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {

                                String actualResponse = response.getJSONArray("sessions").toString();
                                ObjectMapper mapper = new ObjectMapper();
                                List<Center> centersList = mapper.readValue(actualResponse, new TypeReference<ArrayList<Center>>() {
                                });
                                Log.d("TAG", "Pincode = " + pincode + " center = " +  centersList.size());
                                // Todo : Check for availability,and raise Reminder with name of hospital with the vaccine type and age,
                                //  put filtered result in recycler view , set data in the global list first and
                                //  finally when all 7 day result is accumulated show it in recycler view.


                                if(centersList.size()>0)
                                {

                                    List<Center> finalList = new ArrayList<>();

                                  for(int i=0;i<centersList.size();i++)
                                  {

                                      Center c = centersList.get(i);

                                      if(c.getAvailableCapacity()>0 && c.getMinAge()==45)
                                      {
                                          Log.d("TAGcentername",c.getName());

                                          sendNotification();
                                          finalList.add(centersList.get(i));




                                          //raise alarm


                                      }

                                  }

                                    final ByteArrayOutputStream out2 = new ByteArrayOutputStream();
                                    final ObjectMapper mapper2 = new ObjectMapper();

                                    try {
                                        mapper2.writeValue(out2, finalList);

                                        final byte[] data = out2.toByteArray();
                                        Log.d("TAG",new String(data));

                                        finalListString = new String(data);

                                    }
                                    catch (Exception e)
                                    {
                                        Log.d("TAG exception",e.getMessage());
                                    }




                                }


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
                            Log.e("ERROR","on Response Error"+ centerPincodeURL);


                        }
                    }
            );
                requestQueue.add(jsonObjectRequest);
            }
            catch (Exception e){
                Log.d("TAG",e.getMessage());
            }


        }
    }

    @Override
    public void fetchCenters(String districtId) {
        Date currentDate = new Date();
        for (int x = 0; x < 7; x++) {
            LocalDateTime localDateTime = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                localDateTime = currentDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                localDateTime = localDateTime.plusYears(0).plusMonths(0).plusDays(x);
            }
            Date currentDatePlusXDay = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                currentDatePlusXDay = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
            }
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");

            String centerDistrictIdURL = MessageFormat.format(this.centerDistrictIdURL, new String[]{districtId, sdf.format(currentDatePlusXDay)});
            Log.d("District URL", centerDistrictIdURL);
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                    Request.Method.GET,
                    centerDistrictIdURL,
                    null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                String actualResponse = response.getJSONArray("sessions").toString();
                                ObjectMapper mapper = new ObjectMapper();
                                List<Center> centersList = mapper.readValue(actualResponse, new TypeReference<ArrayList<Center>>() {
                                });
                                Log.e("Test-Centers", "District Id = " + districtId + " center = " + centersList.size());
                                // Todo : Check for availability,and raise Reminder with name of hospital with the vaccine type and age,
                                //  put filtered result in recycler view , set data in the global list first and
                                //  finally when all 7 day result is accumulated show it in recycler view.
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
                            Log.e("ERROR", centerDistrictIdURL);
                        }
                    }
            );
            requestQueue.add(jsonObjectRequest);
        }
    }

    @Override
    public void fetchCenters(String stateName, String districtName) {
    }

    private void sendNotification() {
        Intent intent = new Intent(context, CenterList.class).putExtra("data",finalListString);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);
        String channelId = context.getString(R.string.default_notification_channel_id);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(context, channelId)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("You can Book Center Now")
                        .setContentText("Click to view current availability status")
                        .setAutoCancel(true)
                        .setContentIntent(pendingIntent);
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        MediaPlayer mediaPlayer = MediaPlayer.create(context,R.raw.ring);
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
}

