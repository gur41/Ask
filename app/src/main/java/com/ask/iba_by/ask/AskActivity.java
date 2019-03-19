package com.ask.iba_by.ask;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.UUID;

import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;

import org.json.JSONObject;


public class AskActivity extends AppCompatActivity {

    private final static String FILE_NAME = "ask.txt";
    AlertDialog.Builder ad;
    String firstQuestion;
    String secondQuestion;
    String thirdQuestion;
    String fourthQuestion;
    String fifrthQuestion;
    String sixthQuestion;
    String id;
    ArrayList<Integer> ids = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_ask);
        super.onCreate(savedInstanceState);
        setUpAnsweres();

        /*Button button = (Button) findViewById(R.id.button);

        button.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                String button1String = "Nein";
                String button2String = "Ja";

                ad = new AlertDialog.Builder(AskActivity.this);
                ad.setTitle("Möchten Sie wirklich Daten senden?");  // заголовок
                //ad.setMessage("message"); // сообщение
                ad.setPositiveButton(button2String, new OnClickListener() {
                    public void onClick(DialogInterface dialog, int arg1) {

                        getAnswers();
                        //insert();
                        saveStatusAnswerIntoFile();
                        Intent intent = new Intent(AskActivity.this, MainActivity.class);
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
        });*/
    }

    //get answers from android screen
    public void getAnswers() {
        for (int i = 0; i < ids.size(); i++) {
            Spinner one = (Spinner) findViewById(ids.get(i));
            String one1 = one.getSelectedItem().toString();
            System.out.println(one1);
        }
    }

    @SuppressLint("ResourceType")
    public void setUpAnsweres() {
        for (int i = 0; i < 8; i++) {

            Spinner spinner = new Spinner(this);
            ArrayList<String> spinnerArray = new ArrayList<String>();
            spinnerArray.add("one");
            spinnerArray.add("two");
            spinnerArray.add("three");
            spinnerArray.add("four");
            spinnerArray.add("five");

            ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, spinnerArray);
            spinner.setAdapter(spinnerArrayAdapter);
            spinner.setId(100 + i);
            ids.add(100 + i);
            LinearLayout layout = (LinearLayout) findViewById(R.id.linearLayout);
            layout.addView(spinner);

        }
        Button button = new Button(this);
        button.setText("Ok");
        button.setId(10000);
        LinearLayout layout = (LinearLayout) findViewById(R.id.linearLayout);
        layout.addView(button);
        button.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                String button1String = "Nein";
                String button2String = "Ja";

                ad = new AlertDialog.Builder(AskActivity.this);
                ad.setTitle("Möchten Sie wirklich Daten senden?");  // заголовок
                //ad.setMessage("message"); // сообщение
                ad.setPositiveButton(button2String, new OnClickListener() {
                    public void onClick(DialogInterface dialog, int arg1) {

                        getAnswers();
                        //insert();
                        saveStatusAnswerIntoFile();
                        Intent intent = new Intent(AskActivity.this, MainActivity.class);
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


    public void saveStatusAnswerIntoFile() {
        String fileName = getExternalFilesDir(null).getAbsolutePath() + "/ask.txt";
        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter(new File(fileName), true);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write("answered" + System.getProperty("line.separator"));
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
