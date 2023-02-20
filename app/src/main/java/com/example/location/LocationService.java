package com.example.location;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class LocationService extends Service {
    FusedLocationProviderClient fusedLocationProviderClient;
    LocationCallback locationCallback;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
//                Log.d("mylog", "Lat is: " + locationResult.getLastLocation().getLatitude() + ", "
//                        + "Lng is: " + locationResult.getLastLocation().getLongitude());
//                Intent intent = new Intent("ACT_LOC");
//                intent.putExtra("latitude", locationResult.getLastLocation().getLatitude());
//                intent.putExtra("longitude", locationResult.getLastLocation().getLongitude());
//                sendBroadcast(intent);


                Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                List<Address> addresses = null;
                try {
                    addresses = geocoder.getFromLocation(locationResult.getLastLocation().getLatitude(), locationResult.getLastLocation().getLongitude(), 1);

                    Double Latitude = addresses.get(0).getLatitude();
                    Double Longtitude = addresses.get(0).getLongitude();
                    String AddressLine = addresses.get(0).getAddressLine(0);

                    Intent intent = new Intent("Location");
                    intent.putExtra("latitude", Latitude);
                    intent.putExtra("longitude", Longtitude);
                    intent.putExtra("Address", AddressLine);
                    sendBroadcast(intent);

                } catch (IOException e) {
                    Log.e(TAG, "onLocationResult: "+e.getMessage() );
                }


            }
        };
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {



        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
            createNotificationChanel();
        } else {
            startForeground(1, new Notification());
        }

        requestLocation();
        return super.onStartCommand(intent, flags, startId);
    }

    private void createNotificationChanel() {
        String notificationChannelId = "Cistron";
        String channelName = "Background Service";

        NotificationChannel chan = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            chan = new NotificationChannel(
                    notificationChannelId,
                    channelName,
                    NotificationManager.IMPORTANCE_NONE
            );
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            chan.setLightColor(Color.BLUE);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        }

        NotificationManager manager = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            manager = getSystemService(NotificationManager.class);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            manager.createNotificationChannel(chan);
        }

        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, notificationChannelId);

        Notification notification = notificationBuilder.setOngoing(true)
                .setContentTitle("Location updates:")
                .setPriority(NotificationManager.IMPORTANCE_MIN)
                .setCategory(Notification.CATEGORY_SERVICE)
                .build();
        startForeground(2, notification);
    }


    @SuppressLint("MissingPermission")
    private void requestLocation() {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
    }
}
