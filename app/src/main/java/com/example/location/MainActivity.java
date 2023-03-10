package com.example.location;

import static android.os.Environment.DIRECTORY_DOWNLOADS;
import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextPaint;
import android.util.Log;
import android.widget.Toast;
import android.Manifest;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    SupportMapFragment mapFragment;
    GoogleMap mMap;
    Marker marker;
    LocationBroadcastReceiver receiver;

    Polyline polyline = null;
    ArrayList<LatLng> latLngArrayList = new ArrayList<>();
    ArrayList<Marker> markerArrayList = new ArrayList<>();
    ArrayList<LocationList> locationLists = new ArrayList<>();
    String LAt;
    String Long;
    String Address;
    String filename, filepath;
    int Count = 1;
    int Head = 0;


    // Pdf Creator

    int pageHeight = 1120;
    int pagewidth = 792;


    @Override
    protected void onStop() {
        super.onStop();
        startLocService();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        filename = "Location.txt";
        filepath = Environment.getExternalStoragePublicDirectory(DIRECTORY_DOWNLOADS) + "/";


        receiver = new LocationBroadcastReceiver();
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            } else {
                //Req Location Permission
                startLocService();
            }
        } else {
            //Start the Location Service
            startLocService();

        }

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapFrag);
        mapFragment.getMapAsync(MainActivity.this);
    }

    void startLocService() {
        IntentFilter filter = new IntentFilter("Location");
        registerReceiver(receiver, filter);
        Intent intent = new Intent(MainActivity.this, LocationService.class);
        startService(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //startLocService();
                } else {
                    Toast.makeText(this, "Give me permissions", Toast.LENGTH_LONG).show();
                }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }

    private void CallPdf() {

//        PdfDocument pdfDocument = new PdfDocument();
//        for(int i=0;i<10;i++){
//            PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(100, 100, i).create();
//            PdfDocument.Page page = pdfDocument.startPage(pageInfo);
//            Canvas canvas = page.getCanvas();
//            Paint paint = new Paint();
//            canvas.drawText("Text : "+i, 10, 10, paint);
//            canvas.drawText("Text : "+i, 10, 20, paint);
//            pdfDocument.finishPage(page);
//        }
//
//        File file=new File(filepath,"pdf.pdf");
//        try {
//            pdfDocument.writeTo(new FileOutputStream(file));
//        }catch (Exception e){
//
//        }



//        Head++;
//        PdfDocument pdfDocument = new PdfDocument();
//        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(1190, 1684, 1).create();
//        PdfDocument.Page mypage1 = pdfDocument.startPage(pageInfo);
//        Canvas canvas = mypage1.getCanvas();
//
//        TextPaint mTextPaint=new TextPaint();
//        mTextPaint.setTextAlign(Paint.Align.CENTER);
//
//        String location = "";
//
//        for (int i = 0; i < locationLists.size(); i++) {
//
//            location = location + "Latitude --> " + locationLists.get(i).getLatitude() + "\n" + "Longitude --> " + locationLists.get(i).getLongtitude() + "\n" + "Address --> " + locationLists.get(i).getAddress();
//        }
//
//
//        mTextPaint.setTextSize(32f);
//
//        int x = 1120, y=300;
//
//        for (String line:location.split("\n")){
//            mypage1.getCanvas().drawText(line, x, y, mTextPaint);
//            y+=mTextPaint.descent()-mTextPaint.ascent();
//        }
//
//        mTextPaint.setTextAlign(Paint.Align.CENTER);
//        mTextPaint.setTextSize(32f);
//        canvas.drawText(location,595,1560,mTextPaint);
//        pdfDocument.finishPage(mypage1);
//
//
//
//        File file1 = new File(filepath,"pdf.pdf");
//        try {
//            pdfDocument.writeTo(new FileOutputStream(file1));
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//        pdfDocument.close();
     }

    public class LocationBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("Location")) {
                latLngArrayList.clear();
                double lat = intent.getDoubleExtra("latitude", 0f);
                double longitude = intent.getDoubleExtra("longitude", 0f);
                Address = intent.getStringExtra("Address");
                LAt = String.valueOf(lat);
                Long = String.valueOf(longitude);
                LocationList locationList = new LocationList(LAt, Long, Address);
                locationLists.add(locationList);
                if (mMap != null) {
                    LatLng latLng = new LatLng(lat, longitude);
                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.position(latLng);
                    if (marker != null)
                        marker.setPosition(latLng);
                    else
                        marker = mMap.addMarker(markerOptions);
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14));
                    latLngArrayList.add(latLng);
                    markerArrayList.add(marker);
                }

                PolylineOptions polylineOptions = new PolylineOptions().addAll(latLngArrayList).clickable(true);
                polyline = mMap.addPolyline(polylineOptions);

                CallPdf();

                String location = "";
                Head++;
                for (int i = 0; i < locationLists.size(); i++) {

                    location = location + "\n" + (Count + i) + ")" + "\n" + "\n" + "\n" + "Latitude --> " + locationLists.get(i).getLatitude() + "\n" + "Longitude --> " + locationLists.get(i).getLongtitude() + "\n" + "Address --> " + locationLists.get(i).getAddress() + "\n" + "\n" + "***********************************************" + "\n";
                }
                String Heading = "------------------- " + Head + " Location List -------------------" + "\n" + location;
                if (!Heading.equals("")) {
                    File myExternalFile = new File(filepath, filename);
                    Log.e(TAG, "onClick: " + myExternalFile);

                    try {
                        FileOutputStream fos = new FileOutputStream(myExternalFile);
                        fos.write(Heading.getBytes());
                        fos.close();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                } else {
                    Toast.makeText(MainActivity.this, "empty.", Toast.LENGTH_SHORT).show();
                }

                // Toast.makeText(MainActivity.this, "Latitude is: " + lat + ", Longitude is " + longitude, Toast.LENGTH_LONG).show();
            }
        }
    }
}