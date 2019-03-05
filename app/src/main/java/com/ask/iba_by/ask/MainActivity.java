package com.ask.iba_by.ask;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.StrictMode;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;

import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import org.json.JSONObject;


public class MainActivity extends AppCompatActivity {

    private final static String FILE_NAME = "content.txt";
    AlertDialog.Builder ad;
    String firstQuestion;
    String secondQuestion;
    String thirdQuestion;
    String fourthQuestion;
    String fifrthQuestion;
    String sixthQuestion;
    String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        askPermission();
        setContentView(R.layout.activity_main);
        super.onCreate(savedInstanceState);
        if (openText()) {       //check if file doesn't exist   | if file exists close mainActivity and open second
        } else {
            Intent intent = new Intent(MainActivity.this, Main2Activity.class);
            startActivity(intent);
            finish();
        }

        Button button = (Button) findViewById(R.id.button);

        button.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                String button1String = "Nein";
                String button2String = "Ja";

                ad = new AlertDialog.Builder(MainActivity.this);
                ad.setTitle("Möchten Sie wirklich Daten senden?");  // заголовок
                //ad.setMessage("message"); // сообщение
                ad.setPositiveButton(button2String, new OnClickListener() {
                    public void onClick(DialogInterface dialog, int arg1) {

                        //
                        saveText();//create file for check if this application was launched
                        getAnswers();
                        insert();
                        Intent intent = new Intent(MainActivity.this, Main2Activity.class);
                        intent.putExtra("id", id);
                        startActivity(intent);
                        finish();
                    }
                });
                ad.setNegativeButton(button1String, new OnClickListener() {
                    public void onClick(DialogInterface dialog, int arg1) {

                    }
                });
                ad.setCancelable(true);
                ad.setOnCancelListener(new OnCancelListener() {
                    public void onCancel(DialogInterface dialog) {

                    }
                });
                ad.show();
            }
        });
    }

    //get answers from android screen
    public void getAnswers() {
        final Spinner spinner1 = (Spinner) findViewById(R.id.spinner1);
        final Spinner spinner2 = (Spinner) findViewById(R.id.spinner2);
        final Spinner spinner3 = (Spinner) findViewById(R.id.spinner3);
        final Spinner spinner4 = (Spinner) findViewById(R.id.spinner4);
        final Spinner spinner5 = (Spinner) findViewById(R.id.spinner5);
        final Spinner spinner6 = (Spinner) findViewById(R.id.spinner6);
        firstQuestion = spinner1.getSelectedItem().toString();
        DataOutputStream os = new DataOutputStream(new OutputStream() {
            @Override
            public void write(int b) throws IOException {

            }
        });
        try {
            os.writeBytes(firstQuestion);
        } catch (IOException e) {
            e.printStackTrace();
        }
        secondQuestion = spinner2.getSelectedItem().toString();
        thirdQuestion = spinner3.getSelectedItem().toString();
        fourthQuestion = spinner4.getSelectedItem().toString();
        fifrthQuestion = spinner5.getSelectedItem().toString();
        sixthQuestion = spinner6.getSelectedItem().toString();
    }


    public void saveText() {

        FileOutputStream fos = null;
        try {
            id = UUID.randomUUID().toString();
            System.out.println(id);

            fos = openFileOutput(FILE_NAME, MODE_APPEND);
            fos.write(id.getBytes());

        } catch (IOException ex) {


        } finally {
            try {
                if (fos != null)
                    fos.close();
            } catch (IOException ex) {


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


    public void sendPostAnswers() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL("https://3dlab.icdc.io/kontrakt/public/index.php/quiz");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                    conn.setRequestProperty("Accept", "application/json");
                    conn.setDoOutput(true);
                    conn.setDoInput(false);

                    JSONObject json = new JSONObject();
                    json.put("id", id);
                    json.put("gender", firstQuestion);
                    json.put("age", secondQuestion);
                    json.put("engagement", thirdQuestion);
                    json.put("driving_licence", fourthQuestion);
                    json.put("car_availability", fifrthQuestion);
                    json.put("bike", sixthQuestion);


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
        });

        thread.start();
    }

    //ask all needed permissions
    public void askPermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    1);
        }
        ;
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION},
                    1);
        }
        ;
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.RECEIVE_BOOT_COMPLETED)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{android.Manifest.permission.RECEIVE_BOOT_COMPLETED},
                    1);
        }
        ;
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.INTERNET)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{android.Manifest.permission.INTERNET},
                    1);
        }
        ;
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_NETWORK_STATE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{android.Manifest.permission.ACCESS_NETWORK_STATE},
                    1);
        }
    }


    //insert answers into database
    public void insert() {
        String url = "jdbc:mysql://sql7.freemysqlhosting.net:3306/sql7281136";
        String user = "sql7281136";
        String password = "dJPTekAKJz";
        StrictMode.ThreadPolicy threadPolicy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(threadPolicy);

        try {
            Class.forName("com.mysql.jdbc.Driver");

            Connection conn = DriverManager.getConnection(url, user, password);
            Statement st = conn.createStatement();
            st.execute("INSERT INTO `quiz` (`id`, `gender`, `age`, `engagement`, `driving_licence`, `car_availability`, `bike`) VALUES\n" +
                    "('" + id + "', '" + firstQuestion + "', '" + secondQuestion + "', '" + thirdQuestion + "', '" + fourthQuestion + "', '" + fifrthQuestion + "', '" + sixthQuestion + "');");

            conn.close();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }


}
