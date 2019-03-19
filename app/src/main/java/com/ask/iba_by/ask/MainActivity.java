package com.ask.iba_by.ask;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    public String FILE_NAME;
    public ArrayList<String> strings = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        FILE_NAME = getExternalFilesDir(null).getAbsolutePath() + "/ask.txt";
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        openFile();
        String id_user = strings.get(0);
        String id_collection = strings.get(1);
        Date currentTime = Calendar.getInstance().getTime();
        long period = new Date(strings.get(3)).getTime() - new Date(strings.get(2)).getTime();
        Integer timing = Integer.valueOf(strings.get(4));
        long timingLong = Long.parseLong(strings.get(4)) * 60 * 1000;
        long number = period / timingLong;
        Log.d("workmng", "doWork: start1");
        WorkManager.getInstance().cancelAllWorkByTag("Cleaning");
        if(currentTime.before(new Date(strings.get(2)))) {
            Long delay = new Date(strings.get(2)).getTime()-currentTime.getTime()/60/1000;
            Integer delayMinutes = delay.intValue();
            for (int i = 0; i < number; i++) {       // start 288 jobs every 5 minutes
                Data myData = new Data.Builder()
                        .putString("id_user", id_user)
                        .putString("id_collection",id_collection)
                        .build();
                OneTimeWorkRequest myWorkRequest = new OneTimeWorkRequest.Builder(Worker.class).setInputData(myData)
                        .setInitialDelay(delayMinutes + timing * i, TimeUnit.MINUTES).addTag("Cleaning").build();
                WorkManager.getInstance().enqueue(myWorkRequest);
            }
        }
        else{
            for (int i = 0; i < number; i++) {       // start 288 jobs every 5 minutes
                Data myData = new Data.Builder()
                        .putString("id_user", id_user)
                        .putString("id_collection",id_collection)
                        .build();
                OneTimeWorkRequest myWorkRequest = new OneTimeWorkRequest.Builder(Worker.class).setInputData(myData)
                        .setInitialDelay( timing * i, TimeUnit.MINUTES).addTag("Cleaning").build();
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

}
