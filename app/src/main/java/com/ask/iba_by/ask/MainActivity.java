package com.ask.iba_by.ask;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private final static String FILE_NAME = "content.txt";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(!openText()) {
            Intent intent = getIntent();
            String id = intent.getStringExtra("id");
            Log.d("workmng", "doWork: start1");
            //WorkManager.getInstance().cancelAllWorkByTag("Cleaning");
            for (int i = 0; i < 288; i++) {       // start 288 jobs every 5 minutes
                Data myData = new Data.Builder()
                        .putString("id", id)
                        .build();
                OneTimeWorkRequest myWorkRequest = new OneTimeWorkRequest.Builder(Worker.class).setInputData(myData)
                        .setInitialDelay(5 * i, TimeUnit.MINUTES).addTag("Cleaning").build();
                WorkManager.getInstance().enqueue(myWorkRequest);
            }
        }
    }

    public boolean openText() {

        FileInputStream fin = null;
        try {
            fin = openFileInput(FILE_NAME);
            byte[] bytes = new byte[fin.available()];
            fin.read(bytes);
            String text = new String(bytes);
            if (text.length() > 0)
                return false;
            else
                return true;
        } catch (IOException ex) {


        } finally {

            try {
                if (fin != null)
                    fin.close();
            } catch (IOException ex) {


            }
        }
        return true;
    }

}
