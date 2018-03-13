package com.example.jon_kylesmith.weatherapp;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

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
    DownloadTask task = new DownloadTask();
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

    /*TODO: // test the retrieval of other info
      TODO: // Retrieve Forecast and detailed 
     */


    public void onClick(View view){
        EditText editForecast = (EditText)findViewById(R.id.editForecast);
        EditText editDetailed = (EditText)findViewById(R.id.editDetail);
        EditText editCity = (EditText)findViewById(R.id.editCity);
        String city = editCity.getText().toString();
        EditText editCountry = (EditText)findViewById(R.id.editCountry);
        String country = editCountry.getText().toString();
        String loc = city + "," + country;
        String weatherURL = "http://api.openweathermap.org/data/2.5/weather?q=";
        String appID = "&appid=9f552dfe6b3b3231a46bb5a893b8d78f";
        try {
            task.execute(weatherURL+loc+appID);
            others = new JSONArray(mainInfo);
            other = others.getJSONObject(0);
            String tem = other.getString(("temp"));
            Log.i("Info", "");
            editForecast.setText(mainForecast);
            editDetailed.setText(detailedForecast);

        }catch (Exception e){

        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



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


            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }
}
