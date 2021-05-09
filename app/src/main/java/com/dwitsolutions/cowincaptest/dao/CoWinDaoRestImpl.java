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
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.airbnb.lottie.L;
import com.android.volley.AsyncRequestQueue;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import com.dwitsolutions.cowincaptest.CenterList;
import com.dwitsolutions.cowincaptest.MainActivity;
import com.dwitsolutions.cowincaptest.R;
import com.dwitsolutions.cowincaptest.Splash;
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
    ArrayList<Center> finalList;


    //Todo : Constants must be in property file
    public static String rootPath = "https://cdn-api.co-vin.in/api";
    String statesURL = rootPath + "/v2/admin/location/states";
    String districtURL = rootPath + "/v2/admin/location/districts/";
    public static String centerPincodeURL = rootPath + "/v2/appointment/sessions/public/findByPin?pincode={0}&date={1}";
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

        finalList = new ArrayList<>();
        Splash.editor.remove("finalList");
        Splash.editor.commit();

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

            String centerPincodeURL = MessageFormat.format(CoWinDaoRestImpl.centerPincodeURL, new String[]{pincode.toString(), sdf.format(currentDatePlusXDay)});
            //   Log.d("TAG URL", centerPincodeURL);


            AsyncTask asyncTask = new async(centerPincodeURL, age);
            Log.d("TAG", "executomh");
            asyncTask.execute(new String[]{""});

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
            String fls = Splash.splashSP.getString("finallist", "");
            if (!fls.isEmpty()) {
                sendNotification();
            }
        }

        private void sendNotification() {
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

            Log.d("TAG", "yes");
            try {
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                        Request.Method.GET,
                        url,
                        null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {

                                    String actualResponse = response.getJSONArray("sessions").toString();
                                    ObjectMapper mapper = new ObjectMapper();
                                    List<Center> centersList = mapper.readValue(actualResponse, new TypeReference<ArrayList<Center>>() {
                                    });
                                    Log.d("TAG", url);
//                                    Log.d("TAG", "Pincode = " + pincode + " center = " +  centersList.size());
                                    // Todo : Check for availability,and raise Reminder with name of hospital with the vaccine type and age,
                                    //  put filtered result in recycler view , set data in the global list first and
                                    //  finally when all 7 day result is accumulated show it in recycler view.
                                    if (centersList.size() > 0) {
                                        for (int i = 0; i < centersList.size(); i++) {
                                            Center c = centersList.get(i);
                                            if (c.getAvailableCapacity() > 0 && c.getMinAge() == age) {
                                                try {
                                                    String fls = Splash.splashSP.getString("finallist", "");
                                                    // Log.d("TAG",fls);
                                                    if (!fls.equals("")) {
                                                        //fetching list from shared preference
                                                        ObjectMapper m = new ObjectMapper();
                                                        List<Center> cl = m.readValue(fls, new TypeReference<ArrayList<Center>>() {
                                                        });
                                                        Log.d("TAG djd", cl.get(0).getName());
                                                        //adding to the list
                                                        cl.add(c);
                                                        //list to string conversion
                                                        final ByteArrayOutputStream out2 = new ByteArrayOutputStream();
                                                        final ObjectMapper mapper2 = new ObjectMapper();
                                                        mapper2.writeValue(out2, cl);
                                                        final byte[] data = out2.toByteArray();
                                                        Log.d("TAG list aa gyi", new String(data));
                                                        Splash.editor.remove("finallist");
                                                        Splash.editor.commit();
                                                        Splash.editor.putString("finallist", new String(data));
                                                        Splash.editor.commit();
                                                    } else {
                                                        Log.d("TAG", "kuch aur dikkat");
                                                        List<Center> arrayList = new ArrayList<>();
                                                        arrayList.add(c);
                                                        try {
                                                            final ByteArrayOutputStream out2 = new ByteArrayOutputStream();
                                                            final ObjectMapper mapper2 = new ObjectMapper();
                                                            mapper2.writeValue(out2, arrayList);
                                                            final byte[] data = out2.toByteArray();
                                                            Splash.editor.putString("finallist", new String(data));
                                                            Splash.editor.commit();
                                                        } catch (Exception e) {
                                                        }
                                                        //raise alarm
                                                    }
                                                } catch (Exception e) {
                                                    Log.d("TAG", "ye dikkat hai" + e.getMessage());
                                                    e.printStackTrace();
                                                }
                                            }
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
                                Log.e("TAG error", "on Response Error" + centerPincodeURL);
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

