package com.example.location;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.Manifest;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

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
    Activity activity;

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
                    Log.e(TAG, "onLocationResult: " + e.getMessage());
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

    @SuppressLint("NewApi")
    private void createNotificationChanel() {
        String notificationChannelId = "Cistron";
        String channelName = "Background Service";

        NotificationChannel chan = null;

        chan = new NotificationChannel(
                notificationChannelId,
                channelName,
                NotificationManager.IMPORTANCE_NONE
        );

        chan.setLightColor(Color.BLUE);

        chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

        NotificationManager manager = null;
        manager = getSystemService(NotificationManager.class);


        manager.createNotificationChannel(chan);


        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, notificationChannelId);

        Notification notification = notificationBuilder.setOngoing(true)
                .setContentTitle("Location updates:")
                .setPriority(NotificationManager.IMPORTANCE_MIN)
                .setCategory(String.valueOf(Notification.FOREGROUND_SERVICE_IMMEDIATE))
                .build();
        startForeground(2, notification);
    }


    private void requestLocation() {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
        }

}
