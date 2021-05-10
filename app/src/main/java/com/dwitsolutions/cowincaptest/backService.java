package com.dwitsolutions.cowincaptest;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.dwitsolutions.cowincaptest.MainActivity;
import com.dwitsolutions.cowincaptest.dao.CoWinDao;
import com.dwitsolutions.cowincaptest.dao.CoWinDaoRestImpl;

import static android.os.SystemClock.elapsedRealtime;

public class backService extends Service {

    CountDownTimer cdt;
    int age;
    Integer pincode,districtId;
    public static SharedPreferences defaultSharedPreference;
    public static SharedPreferences.Editor defaultSharedPreferenceEditor;
    RequestQueue requestQueue;
    static CoWinDao coWinDao;
    private String CHANNEL_ID = "NOTIFICATION_CHANNEL";
    public static boolean isServiceRunning;

    public backService() {
        isServiceRunning = false;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, 0);
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Covin Service is Running")
                .setContentText("Listening for any Slots in your area")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pendingIntent)
                .setColor(getResources().getColor(R.color.teal_200))
                .build();

        startForeground(1, notification);

        return START_STICKY;
    }


    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String appName = getString(R.string.app_name);
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    appName,
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        isServiceRunning = true;
        createNotificationChannel();
        requestQueue = Volley.newRequestQueue(this);
        coWinDao = new CoWinDaoRestImpl(requestQueue, getApplicationContext());

        defaultSharedPreference = PreferenceManager.getDefaultSharedPreferences(this);
        defaultSharedPreferenceEditor = defaultSharedPreference.edit();
        age = defaultSharedPreference.getInt("age",0);
        pincode = defaultSharedPreference.getInt("pincode",0);
        districtId = defaultSharedPreference.getInt("districtId",0);




        cdt = new CountDownTimer(1000*60*60*60, 60*1000) {
            @Override
            public void onTick(long millisUntilFinished) {
              //  PinCodeActivity.checkcentersdata(pincode,age);
                String type = defaultSharedPreference.getString("type","");

                if(type.equals("pincode"))
                    coWinDao.fetchCenters(pincode,age);
                else if(type.equals("district"))
                    coWinDao.fetchCentersbyDistrict(districtId,age);
            }

            @Override
            public void onFinish() {
                Log.i("TAG", "Timer finished");
            }
        };

        cdt.start();
    }

    @Override
    public void onDestroy() {


             isServiceRunning = false;
            stopForeground(true);
        // call MyReceiver which will restart this service via a worker
             Intent broadcastIntent = new Intent(this, Restarter.class);
             sendBroadcast(broadcastIntent);
             super.onDestroy();


    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        Log.d("TAG","removing Task");
        Intent restartServiceIntent = new Intent(getApplicationContext(), backService.class);
        PendingIntent restartServicePendingIntent = PendingIntent.getService(this, 1, restartServiceIntent, PendingIntent.FLAG_ONE_SHOT);
        getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        AlarmManager alarmService = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        alarmService.set(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + 1000, restartServicePendingIntent);
    }
}