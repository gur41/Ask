package com.ask.iba_by.ask;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.StrictMode;
import android.support.constraint.ConstraintLayout;
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
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.UUID;

import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class AskActivity extends AppCompatActivity {

    private static String FILE_NAME ;
    public static String SERVER = "https://3dlab.icdc.io/geotracking/public/index.php";
    ArrayList<Question> questions = new ArrayList<>();
    AlertDialog.Builder ad;
    ArrayList<Integer> ids = new ArrayList<>();
    ArrayList<String> strings = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        FILE_NAME = getExternalFilesDir(null).getAbsolutePath() + "/ask.txt";
        openFile();
        setContentView(R.layout.activity_ask);
        super.onCreate(savedInstanceState);
        getQuestions();
        setUpAnsweres();


    }

    //get answers from android screen
    public void getAnswers() {
        for (int i = 0; i < ids.size(); i++) {
            Spinner one = (Spinner) findViewById(ids.get(i));
            Integer position = one.getSelectedItemPosition();
            Integer question = questions.get(i).getId();
            Integer answer = questions.get(i).getAnswers().get(position).getId();
            sendPostAnswers(strings.get(0),Integer.valueOf(strings.get(1)),question,answer);
        }
    }

    @SuppressLint("ResourceType")
    public void setUpAnsweres() {
        LinearLayout layout = (LinearLayout) findViewById(R.id.linearLayout);
        for (int i = 0; i < questions.size(); i++) {

            TextView textView = new TextView(this);
            textView.setText(questions.get(i).getQuestion());
            textView.setPadding(30,15,15,15);
            textView.setTextColor(Color.BLACK);
            textView.setTextSize(20);
            layout.addView(textView);

            Spinner spinner = new Spinner(this);
            ArrayList<String> spinnerArray = new ArrayList<String>();
            for (Answer answer: questions.get(i).getAnswers()) {
                spinnerArray.add(answer.getAnswer());
            }

            ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, spinnerArray);
            spinner.setAdapter(spinnerArrayAdapter);
            spinner.setPadding(45,0,15,0);
            spinner.setId(100 + i);
            ids.add(100 + i);

            layout.addView(spinner);

        }
        Button button = new Button(this);
        ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.MATCH_PARENT,
                ConstraintLayout.LayoutParams.MATCH_PARENT
        );
        params.setMargins(35, 0, 35, 0);
        button.setLayoutParams(params);

        button.setText("Ok");
        button.setId(10000);
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


    public void sendPostAnswers(final String userId, final Integer collectionId, final Integer questionId, final Integer answersId) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String run = strings.get(1);
                    URL url = new URL(SERVER+"/userFeedback/"+run+"&"+encrypt(run));
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                    conn.setRequestProperty("Accept", "application/json");
                    conn.setDoOutput(true);
                    conn.setDoInput(false);

                    JSONObject json = new JSONObject();
                    json.put("user_id", userId);
                    json.put("collection_id", collectionId);
                    json.put("question_id", questionId);
                    json.put("answers_id", answersId);

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

    public void getQuestions(){
        StrictMode.ThreadPolicy threadPolicy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(threadPolicy);
        String run = strings.get(1);
        try {
            String url1 = SERVER + "/combo/answers/" + run+"&"+encrypt(run)+"&"+run;
            URL url = new URL(url1);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            //conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
            //conn.setRequestProperty("Accept", "application/json");
            conn.setRequestProperty("User-Agent", "Mozilla/5.0");
            //Log.i("JSON", json.toString());
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            //os.writeBytes(URLEncoder.encode(jsonParam.toString(), "UTF-8"));
            String inputLine;
            StringBuffer response = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            Log.i("STATUS", String.valueOf(conn.getResponseCode()));
            Log.i("MSG", conn.getResponseMessage());
            Log.i("JSON", String.valueOf(response));
            parse(response.toString());


            conn.disconnect();

        } catch (Exception e) {
            e.printStackTrace();
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

    public void parse(String str){
        JSONArray myResponseArray = null;
        try {
            myResponseArray = new JSONArray(str);
            JSONArray myResponse = myResponseArray.getJSONArray(0);
            for (int i =0; i<myResponseArray.length();i++) {
                JSONObject o = myResponseArray.getJSONArray(i).getJSONObject(0);
                Question question = new Question();
                question.setId(o.getInt("id"));
                question.setQuestion(o.getString("question"));
                ArrayList<Answer> answersList = new ArrayList<>();
                JSONArray answers = new JSONArray(o.getString("answers"));
                for (int j =0;j<answers.length();j++){
                    JSONObject obj = answers.getJSONObject(j);
                    answersList.add(new Answer(obj.getInt("id"),obj.getString("answer")));
                }
                question.setAnswers(answersList);
                questions.add(question);
            }
            System.out.println("hello");
        } catch (JSONException e) {
            e.printStackTrace();
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
