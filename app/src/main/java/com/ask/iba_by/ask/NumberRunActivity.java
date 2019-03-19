package com.ask.iba_by.ask;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.os.StrictMode;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class NumberRunActivity extends AppCompatActivity {

    //public static String DIRECTORY_NAME = this.getFilesDir()+"/Android/data/test";
    public static String SERVER = "https://3dlab.icdc.io/geotracking/public/index.php";
    public String FILE_NAME;
    public String run;
    public String start;
    public String end;
    public Integer runTime;
    public Integer timing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        askPermission();
        FILE_NAME = getExternalFilesDir(null).getAbsolutePath() + "/ask.txt";
        System.out.println(FILE_NAME);
        File file = new File(FILE_NAME);
        if (!file.exists()) {
            saveIntoFileId();
        } else if (checkIfSessionIsRun() && checkIfAnswered()) {
            Intent intent = new Intent(NumberRunActivity.this, MainActivity.class);
            startActivity(intent);
            this.finish();
        } else if (checkIfSessionIsRun() && !checkIfAnswered()) {
            Intent intent = new Intent(NumberRunActivity.this, AskActivity.class);
            startActivity(intent);
            this.finish();
        }
        //parse(setUp());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_number_run);

    }

    public String setUp() {
            String line1 = "[\n" +
                    "  [\n" +
                    "    {\n" +
                    "      \"id\": 1,\n" +
                    "      \"question\": \"валера гей?\",\n" +
                    "      \"sort\": 1,\n" +
                    "      \"answers\": [\n" +
                    "        {\n" +
                    "          \"id\": 1,\n" +
                    "          \"question_id\": 1,\n" +
                    "          \"answer\": \"да\",\n" +
                    "          \"sort\": 1,\n" +
                    "          \"created_at\": \"2019-03-19 12:50:07\",\n" +
                    "          \"updated_at\": \"1971-01-01 00:00:01\"\n" +
                    "        },\n" +
                    "        {\n" +
                    "          \"id\": 2,\n" +
                    "          \"question_id\": 1,\n" +
                    "          \"answer\": \"нет\",\n" +
                    "          \"sort\": 1,\n" +
                    "          \"created_at\": \"2019-03-19 12:50:20\",\n" +
                    "          \"updated_at\": \"1971-01-01 00:00:01\"\n" +
                    "        },\n" +
                    "        {\n" +
                    "          \"id\": 3,\n" +
                    "          \"question_id\": 1,\n" +
                    "          \"answer\": \"возможно\",\n" +
                    "          \"sort\": 1,\n" +
                    "          \"created_at\": \"2019-03-19 12:50:29\",\n" +
                    "          \"updated_at\": \"1971-01-01 00:00:01\"\n" +
                    "        }\n" +
                    "      ],\n" +
                    "      \"created_at\": \"2019-03-19T12:48:50.000000Z\",\n" +
                    "      \"updated_at\": \"1971-01-01T00:00:01.000000Z\"\n" +
                    "    }\n" +
                    "  ],\n" +
                    "  [\n" +
                    "    {\n" +
                    "      \"id\": 2,\n" +
                    "      \"question\": \"Рома женат?\",\n" +
                    "      \"sort\": 1,\n" +
                    "      \"answers\": [\n" +
                    "        {\n" +
                    "          \"id\": 4,\n" +
                    "          \"question_id\": 2,\n" +
                    "          \"answer\": \"да\",\n" +
                    "          \"sort\": 1,\n" +
                    "          \"created_at\": \"2019-03-19 12:50:37\",\n" +
                    "          \"updated_at\": \"1971-01-01 00:00:01\"\n" +
                    "        },\n" +
                    "        {\n" +
                    "          \"id\": 5,\n" +
                    "          \"question_id\": 2,\n" +
                    "          \"answer\": \"нет\",\n" +
                    "          \"sort\": 1,\n" +
                    "          \"created_at\": \"2019-03-19 12:51:06\",\n" +
                    "          \"updated_at\": \"1971-01-01 00:00:01\"\n" +
                    "        },\n" +
                    "        {\n" +
                    "          \"id\": 6,\n" +
                    "          \"question_id\": 2,\n" +
                    "          \"answer\": \"возможно\",\n" +
                    "          \"sort\": 1,\n" +
                    "          \"created_at\": \"2019-03-19 12:51:16\",\n" +
                    "          \"updated_at\": \"1971-01-01 00:00:01\"\n" +
                    "        }\n" +
                    "      ],\n" +
                    "      \"created_at\": \"2019-03-19T12:49:06.000000Z\",\n" +
                    "      \"updated_at\": \"1971-01-01T00:00:01.000000Z\"\n" +
                    "    }\n" +
                    "  ],\n" +
                    "  [\n" +
                    "    {\n" +
                    "      \"id\": 3,\n" +
                    "      \"question\": \"Рома разведен?\",\n" +
                    "      \"sort\": 1,\n" +
                    "      \"answers\": [\n" +
                    "        {\n" +
                    "          \"id\": 7,\n" +
                    "          \"question_id\": 3,\n" +
                    "          \"answer\": \"да\",\n" +
                    "          \"sort\": 1,\n" +
                    "          \"created_at\": \"2019-03-19 12:51:30\",\n" +
                    "          \"updated_at\": \"1971-01-01 00:00:01\"\n" +
                    "        },\n" +
                    "        {\n" +
                    "          \"id\": 8,\n" +
                    "          \"question_id\": 3,\n" +
                    "          \"answer\": \"нет\",\n" +
                    "          \"sort\": 1,\n" +
                    "          \"created_at\": \"2019-03-19 12:51:36\",\n" +
                    "          \"updated_at\": \"1971-01-01 00:00:01\"\n" +
                    "        },\n" +
                    "        {\n" +
                    "          \"id\": 9,\n" +
                    "          \"question_id\": 3,\n" +
                    "          \"answer\": \"возможно\",\n" +
                    "          \"sort\": 1,\n" +
                    "          \"created_at\": \"2019-03-19 12:51:43\",\n" +
                    "          \"updated_at\": \"1971-01-01 00:00:01\"\n" +
                    "        }\n" +
                    "      ],\n" +
                    "      \"created_at\": \"2019-03-19T12:49:18.000000Z\",\n" +
                    "      \"updated_at\": \"1971-01-01T00:00:01.000000Z\"\n" +
                    "    }\n" +
                    "  ]\n" +
                    "]";
            return line1;
    }
    public void parse(String str){
        JSONArray myResponseArray = null;
        try {
            myResponseArray = new JSONArray(str);
            JSONArray myResponse = myResponseArray.getJSONArray(0);
            ArrayList<Question> questions = new ArrayList<>();
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

    public void enterNumberRun(View view) {
        TextView numberRun = (TextView) findViewById(R.id.numberRun);
        String run = numberRun.getText().toString();
        this.run = run;
        System.out.println(run);
        ArrayList<String> strings = new ArrayList<>();
        //get   start run
        //      end run
        //      start session
        //      end session
        try {
            File file = new File(FILE_NAME);
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line = bufferedReader.readLine();
            strings.add(line);
            int i = 0;
            while (line != null) {
                System.out.println("line : " + line);
                line = bufferedReader.readLine();
                strings.add(line);
                i++;
            }
            bufferedReader.close();


            if (i == 1) {
                getOptions();
                FileWriter fileWriter = new FileWriter(file, true);
                BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
                System.out.println("one");
                bufferedWriter.write(run + System.getProperty("line.separator"));
                if (Calendar.getInstance().getTime().before(new Date(start))) {
                    bufferedWriter.write(start + System.getProperty("line.separator"));
                    bufferedWriter.write(new Date((new Date(start)).getTime() + TimeUnit.HOURS.toMillis(runTime)) + System.getProperty("line.separator"));
                } else {
                    Date currentTime = Calendar.getInstance().getTime();
                    bufferedWriter.write(currentTime + System.getProperty("line.separator"));
                    bufferedWriter.write(new Date(currentTime.getTime() + TimeUnit.HOURS.toMillis(runTime)) + System.getProperty("line.separator"));
                }
                bufferedWriter.write(timing + System.getProperty("line.separator"));
                bufferedWriter.close();
                Intent intent = new Intent(NumberRunActivity.this, AskActivity.class);
                startActivity(intent);
                this.finish();
            } else {
                getOptions();
                FileWriter fileRewriter = new FileWriter(file, false);
                BufferedWriter bufferedRewriter = new BufferedWriter(fileRewriter);
                System.out.println("size: " + strings.size());
                if (Calendar.getInstance().getTime().before(new Date(end))) {
                    if (numberRun.equals(strings.get(1))) {
                        bufferedRewriter.write(strings.get(0) + System.getProperty("line.separator"));
                        bufferedRewriter.write(strings.get(1) + System.getProperty("line.separator"));
                        Date currentTime = Calendar.getInstance().getTime();
                        bufferedRewriter.write(currentTime + System.getProperty("line.separator"));
                        bufferedRewriter.write(new Date(currentTime.getTime() + TimeUnit.HOURS.toMillis(runTime)) + System.getProperty("line.separator"));
                        bufferedRewriter.write(timing + System.getProperty("line.separator"));
                        bufferedRewriter.write("answered" + System.getProperty("line.separator"));
                        bufferedRewriter.close();
                        Intent intent = new Intent(NumberRunActivity.this, MainActivity.class);
                        startActivity(intent);
                        this.finish();
                    } else {
                        bufferedRewriter.write(strings.get(0) + System.getProperty("line.separator"));
                        bufferedRewriter.write(run + System.getProperty("line.separator"));
                        if (Calendar.getInstance().getTime().before(new Date(start))) {
                            bufferedRewriter.write(start + System.getProperty("line.separator"));
                            bufferedRewriter.write(new Date((new Date(start)).getTime() + TimeUnit.HOURS.toMillis(runTime)) + System.getProperty("line.separator"));
                        } else {
                            Date currentTime = Calendar.getInstance().getTime();
                            bufferedRewriter.write(currentTime + System.getProperty("line.separator"));
                            bufferedRewriter.write(new Date(currentTime.getTime() + TimeUnit.HOURS.toMillis(runTime)) + System.getProperty("line.separator"));
                        }
                        bufferedRewriter.write(timing + System.getProperty("line.separator"));
                        bufferedRewriter.close();
                        Intent intent = new Intent(NumberRunActivity.this, AskActivity.class);
                        startActivity(intent);
                        this.finish();
                    }
                }
                else{
                    for(String str:strings){
                        bufferedRewriter.write(str);
                    }
                    bufferedRewriter.close();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean checkIfSessionIsRun() {
        ArrayList<String> strings = new ArrayList<>();
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
        if (strings.size() < 4) {
            return false;
        } else {
            Date currentTime = Calendar.getInstance().getTime();
            System.out.println(currentTime);
            if (currentTime.before(new Date(strings.get(3)))) {
                return true;
            } else {
                return false;
            }
        }
    }

    public boolean checkIfAnswered() {
        ArrayList<String> strings = new ArrayList<>();
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
        if (strings.size() < 4) {
            return false;
        } else {
            if (strings.get(4).equals("answered")) {
                return true;
            } else {
                return false;
            }
        }
    }


    public void saveIntoFileId() {
        String idUser;
        try {
            File file = new File(FILE_NAME);
            FileWriter fileWriter = new FileWriter(file, false); // поток который подключается к текстовому файлу
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter); // соединяем FileWriter с BufferedWitter

            idUser = UUID.randomUUID().toString();
            System.out.println(idUser);

            bufferedWriter.write(idUser + System.getProperty("line.separator"));

            bufferedWriter.close(); // закрываем поток
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //ask all needed permissions
    public void askPermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(NumberRunActivity.this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_NETWORK_STATE,
                            android.Manifest.permission.INTERNET, android.Manifest.permission.RECEIVE_BOOT_COMPLETED,
                            android.Manifest.permission.ACCESS_COARSE_LOCATION
                    },
                    1);
        }

    }

    public void getOptions() {
        StrictMode.ThreadPolicy threadPolicy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(threadPolicy);
        try {
            String url1 = SERVER + "/collection/" + run;
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
            JSONArray myResponseArray = new JSONArray(response.toString());
            JSONObject myResponse = myResponseArray.getJSONObject(0);

            start = String.valueOf(parseDate(myResponse.getString("startdate")));
            System.out.println(start);
            end = String.valueOf(parseDate(myResponse.getString("enddate")));
            runTime = Integer.valueOf(myResponse.getInt("runtime"));
            timing = Integer.valueOf(myResponse.getInt("collect_interval"));

            conn.disconnect();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Date parseDate(String date) {
        try {
            System.out.println("DATE: "+new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(date));
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(date);
        } catch (ParseException e) {
            return null;
        }
    }


}
