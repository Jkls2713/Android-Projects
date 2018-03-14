package com.example.jon_kylesmith.weatherapp;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {
    DownloadTask task ;
    String mainInfo;
    JSONArray others;
    JSONObject other;
    EditText temp;
    EditText minTemp;
    EditText maxTemp;
    EditText humidity;
    EditText forecast;
    EditText detailed;
    EditText windspeed;
    String mainForecast;
    String detailedForecast;
    EditText editForecast;
    EditText editDetailed;
    EditText editCity;
    EditText editCountry;
    /*TODO: // change results to regular Textviews and not Editviews
     */


    public void onClick(View view){

        task  = new DownloadTask();
        String city = editCity.getText().toString();
        String country = editCountry.getText().toString();
        String loc = city + "," + country;
        String weatherURL = "http://api.openweathermap.org/data/2.5/weather?q=";
        String appID = "&appid=9f552dfe6b3b3231a46bb5a893b8d78f";
        try {
            //TODO: handle thread background thread to get info back to main thread
            task.execute(weatherURL+loc+appID);
            //

        }catch (Exception e){
            Log.i("Errorss", "onClick: ");
            e.printStackTrace();
        }

    }

    public String convertTemp(String Ktemp){

       Double number = Double.parseDouble(Ktemp);

       number = (number - 273)*(9/5);
       number += 32;
       return Double.toString(number);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        temp = (EditText)findViewById(R.id.editTemp);
        humidity = (EditText)findViewById(R.id.editHumidity);
        minTemp = (EditText)findViewById(R.id.editMin);
        maxTemp = (EditText)findViewById(R.id.editMax);
        editForecast = (EditText)findViewById(R.id.editForecast);
        editDetailed = (EditText)findViewById(R.id.editDetail);
        editCity = (EditText)findViewById(R.id.editCity);
         editCountry = (EditText)findViewById(R.id.editCountry);



        //task.execute("http://api.openweathermap.org/data/2.5/weather?q=London,uk&appid=9f552dfe6b3b3231a46bb5a893b8d78f");


    }

//    public class DownloadTask extends AsyncTask<String, Void, String>{
//
//        @Override
//        protected String doInBackground(String... strings) {
//            URL url;
//            String result = "";
//            HttpURLConnection urlConnection = null;
//            try {
//               url = new URL(strings[0]);
//                 urlConnection = (HttpURLConnection) url.openConnection();
//                urlConnection.connect();
//                InputStream inputStream = urlConnection.getInputStream();
//                InputStreamReader reader = new InputStreamReader(inputStream);
//                int data = reader.read();
//                while(data != -1){
//                    result += (char)data;
//                    data = reader.read();
//                }
//                return result;
//            } catch (MalformedURLException e) {
//                e.printStackTrace();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//            return null;
//        }
public class DownloadTask extends AsyncTask<String, Void, String> {

    @Override
    protected String doInBackground(String... urls) {

        String result = "";
        URL url;
        HttpURLConnection urlConnection = null;

        try {
            url = new URL(urls[0]);

            urlConnection = (HttpURLConnection) url.openConnection();

            InputStream in = urlConnection.getInputStream();

            InputStreamReader reader = new InputStreamReader(in);

            int data = reader.read();

            while (data != -1) {

                char current = (char) data;

                result += current;

                data = reader.read();

            }

            return result;

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }


    @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            try {

                JSONObject jsonObject = new JSONObject(result);

                String weatherInfo = jsonObject.getString("weather");

                Log.i("Weather content", weatherInfo);

                JSONArray arr = new JSONArray(weatherInfo);

                for (int i = 0; i < arr.length(); i++) {

                    JSONObject jsonPart = arr.getJSONObject(i);

                    Log.i("main", jsonPart.getString("main"));
                    mainForecast = jsonPart.getString("main");
                    Log.i("description", jsonPart.getString("description"));
                    detailedForecast = jsonPart.getString("description");

                }

                mainInfo = jsonObject.getString("main");

                Log.i("Other content", mainInfo);

                String [] otherInfo = mainInfo.split("[:,}]");
                Log.i("Test Split", otherInfo[0]);
                Log.i("Test Split", otherInfo[1]);
                Log.i("Test Split", otherInfo[2]);
                Log.i("Test Split", otherInfo[3]);
                Log.i("Test Split", otherInfo[4]);
                Log.i("Test Split", otherInfo[5]);
                editForecast.setText(mainForecast, TextView.BufferType.EDITABLE);
                editDetailed.setText(detailedForecast);

                String F_temp = convertTemp(otherInfo[1]);
                temp.setText(F_temp + " degrees");
                humidity.setText(otherInfo[5]);
                String Fmin = convertTemp(otherInfo[7]);
                String Fmax = convertTemp(otherInfo[9]);
                minTemp.setText(Fmin + " degrees");
                maxTemp.setText(Fmax + " degrees");

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }
}
