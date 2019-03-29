package com.ask.iba_by.ask;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Looper;
import android.os.StrictMode;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.android.gms.location.*;

import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;


public class WorkerService extends Service {

    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    public static String SERVER = "https://3dlab.icdc.io/geotracking/public/index.php";
    private long millsecond =0;

    public static String id_user;
    public static String id_collection;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        id_user = intent.getStringExtra("id_user");//get id from Worker
        id_collection = intent.getStringExtra("id_collection");
        Log.d("workmng", "location");
        getLocation();      //get location
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return START_NOT_STICKY;
        }
        return START_NOT_STICKY;
    }


    public void getLocation() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        buildLocationRequest();
        builtLocationCallback();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
    }

    private void builtLocationCallback() {
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                for (Location location : locationResult.getLocations()) {
                    Log.d("workmng", String.valueOf(location.getLatitude()));
                    Log.d("workmng", String.valueOf(location.getLongitude()));

                    //insertLocation(location); //insert in database coordinates
                    long currentTime = Calendar.getInstance().getTimeInMillis();
                    if(Math.abs(currentTime - millsecond)> 30 *1000 ) {
                        sendPost(location);
                        millsecond=currentTime;
                    }

                }
            }
        };

    }

    @SuppressLint("RestrictedApi")
    private void buildLocationRequest() {
        locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setSmallestDisplacement(10);
    }

    public void sendPost(final Location location) {
        StrictMode.ThreadPolicy threadPolicy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(threadPolicy);
        String run = id_collection;
        try {
            URL url = new URL(SERVER+"/collectionData/"+run+"&"+encrypt(run));
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
            conn.setRequestProperty("Accept", "application/json");
            conn.setDoOutput(true);
            conn.setDoInput(false);

            JSONObject json = new JSONObject();
            json.put("degree_latitude", location.getLatitude());
            json.put("degree_longitude", location.getLongitude());
            json.put("collection_id", id_collection);
            json.put("user_id", id_user);

            Log.i("JSON", json.toString());
            DataOutputStream os = new DataOutputStream(conn.getOutputStream());
            //os.writeBytes(URLEncoder.encode(json.toString(), "Windows-1250"));
            os.write(json.toString().getBytes("UTF-8"));
            //os.writeUTF(json.toString());
            //ObjectOutputStream os = new ObjectOutputStream(conn.getOutputStream());
            //os.writeObject(json.toString());
            os.flush();
            os.close();

            Log.i("STATUS", String.valueOf(conn.getResponseCode()));
            Log.i("MSG", conn.getResponseMessage());

            conn.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
            Log.i("Exception", "12345");
        }

    }

    public String encrypt(String run) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        byte[] bytesOfMessage = (run+"idontknow").getBytes("UTF-8");
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] thedigest = md.digest(bytesOfMessage);
        StringBuilder str = new StringBuilder();
        for (byte b: thedigest) {
            str.append(String.format("%02x",b));
        }
        return str.toString();
    }


}