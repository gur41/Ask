package com.ask.iba_by.ask;

import android.content.Intent;
import android.os.Build;
import android.os.StrictMode;
import android.os.SystemClock;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Chronometer;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    public String FILE_NAME;
    public static String SERVER = "https://3dlab.icdc.io/geotracking/public/index.php";
    public ArrayList<String> strings = new ArrayList<>();
    Chronometer simpleChronometer;
    Chronometer timeTillEndRun;
    String endRun;


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        FILE_NAME = getExternalFilesDir(null).getAbsolutePath() + "/ask.txt";
        openFile();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView textView = (TextView)findViewById(R.id.main_number_run);
        textView.setText("Run number : "+strings.get(1));
        getOptions();
        setUpTimeTillEndRun();
        setUpChronometer();
        String id_user = strings.get(0);
        String id_collection = strings.get(1);
        Date currentTime = Calendar.getInstance().getTime();
        long period = new Date(strings.get(3)).getTime() - new Date(strings.get(2)).getTime();
        Integer timing = Integer.valueOf(strings.get(4));
        long timingLong = Long.parseLong(strings.get(4)) * 60 * 1000;
        long number = period / timingLong;
        Log.d("workmng", "doWork: start1");
        WorkManager.getInstance().cancelAllWorkByTag("Cleaning");
        if (currentTime.before(new Date(strings.get(2)))) {
            Long delay = (new Date(strings.get(2)).getTime() - currentTime.getTime()) / 60 / 1000;
            Integer delayMinutes = delay.intValue();
            for (int i = 0; i < number; i++) {       // start 288 jobs every 5 minutes
                Data myData = new Data.Builder()
                        .putString("id_user", id_user)
                        .putString("id_collection", id_collection)
                        .build();
                OneTimeWorkRequest myWorkRequest = new OneTimeWorkRequest.Builder(Worker.class).setInputData(myData)
                        .setInitialDelay(delayMinutes + timing * i +1, TimeUnit.MINUTES).addTag("Cleaning").build();
                WorkManager.getInstance().enqueue(myWorkRequest);
            }
        } else {
            for (int i = 0; i < number; i++) {       // start 288 jobs every 5 minutes
                Data myData = new Data.Builder()
                        .putString("id_user", id_user)
                        .putString("id_collection", id_collection)
                        .build();
                OneTimeWorkRequest myWorkRequest = new OneTimeWorkRequest.Builder(Worker.class).setInputData(myData)
                        .setInitialDelay(timing * i, TimeUnit.MINUTES).addTag("Cleaning").build();
                WorkManager.getInstance().enqueue(myWorkRequest);
            }
        }
    }

    public void openFile() {
        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader(FILE_NAME));
            String line = reader.readLine();
            strings.add(line);
            while (line != null) {
                System.out.println(line);
                line = reader.readLine();
                strings.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void setUpChronometer() {
        simpleChronometer = (Chronometer) findViewById(R.id.simpleChronometer); // initiate a chronometer
        simpleChronometer.setCountDown(true);
        final long time = SystemClock.elapsedRealtime();
        Date currenttime = Calendar.getInstance().getTime();
        final Date start = new Date(strings.get(2));
        final Date end = new Date(strings.get(3));
        final Date endRunDate = new Date(endRun);
        if (currenttime.before(start)) {
            //time till start
            final TextView textView = (TextView)findViewById(R.id.time_till_end_of_session);
            textView.setText("Time till start of run :");
            simpleChronometer.setBase(SystemClock.elapsedRealtime() - currenttime.getTime()+start.getTime());
            simpleChronometer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
                @Override
                public void onChronometerTick(Chronometer chronometer) {
                    long elapsedMillis = simpleChronometer.getBase() - SystemClock.elapsedRealtime();
                    if (elapsedMillis <= 0 && Calendar.getInstance().getTime().before(endRunDate)) {
                        //time till end session
                        textView.setText("Time till end of session :");
                        chronometer.setBase(SystemClock.elapsedRealtime()+end.getTime()-start.getTime());
                        chronometer.start();
                    }
                    if(elapsedMillis <=0 && Calendar.getInstance().getTime().after(endRunDate)){
                        chronometer.setBase(SystemClock.elapsedRealtime());
                        chronometer.stop();
                        MainActivity.this.finish();
                    }
                }
            });
            simpleChronometer.start();
        }
        else {
            //time till end session
            simpleChronometer.setBase(SystemClock.elapsedRealtime() - currenttime.getTime()+end.getTime());
            simpleChronometer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
                @Override
                public void onChronometerTick(Chronometer chronometer) {
                    long elapsedMillis = simpleChronometer.getBase() - SystemClock.elapsedRealtime();
                    if (elapsedMillis < 0) {
                        chronometer.setBase(SystemClock.elapsedRealtime());
                        chronometer.stop();
                        MainActivity.this.finish();
                    }
                }
            });
            simpleChronometer.start();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void setUpTimeTillEndRun(){
        timeTillEndRun = (Chronometer) findViewById(R.id.time_till_end_run_chronometer); // initiate a chronometer
        timeTillEndRun.setCountDown(true);
        Date currenttime = Calendar.getInstance().getTime();
        Date end = new Date(endRun);
        timeTillEndRun.setBase(SystemClock.elapsedRealtime() - currenttime.getTime()+end.getTime());
        timeTillEndRun.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            @Override
            public void onChronometerTick(Chronometer chronometer) {
                System.out.println(SystemClock.elapsedRealtime());
                long elapsedMillis =timeTillEndRun.getBase() - SystemClock.elapsedRealtime();
                System.out.println("endrun" + elapsedMillis);
                if (elapsedMillis < 0) {
                    //time till end session
                    chronometer.setBase(SystemClock.elapsedRealtime());
                    chronometer.stop();
                    MainActivity.this.finish();
                }
            }
        });
        timeTillEndRun.start();
    }

    public void getOptions() {
        StrictMode.ThreadPolicy threadPolicy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(threadPolicy);
        String run = strings.get(1);
        try {

            String url1 = SERVER + "/collection/byid/" + run+"&"+encrypt(run)+"&"+run;
            URL url = new URL(url1);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("User-Agent", "Mozilla/5.0");
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            Log.i("STATUS", String.valueOf(conn.getResponseCode()));
            Log.i("MSG", conn.getResponseMessage());
            Log.i("JSON", String.valueOf(response));
            JSONArray myResponseArray = new JSONArray(response.toString());
            JSONObject myResponse = myResponseArray.getJSONObject(0);


            endRun = String.valueOf(parseDate(myResponse.getString("enddate")));

            conn.disconnect();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Date parseDate(String date) {
        try {
            System.out.println("DATE: " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(date));
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(date);
        } catch (ParseException e) {
            return null;
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
