package com.example.jon_kylesmith.nycschools;

import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class DescriptionActivity extends AppCompatActivity {


    //List of global variables needed by Main and background thread
    Handler mainThread = new Handler();
    String school_Name;
    TextView readingScore;
    TextView mathScore;
    TextView writingScore;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_description);

        // Setting all views to proper resource
        TextView schoolName = (TextView)findViewById(R.id.schoolNameTextView);

        TextView overView = (TextView)findViewById(R.id.overviewTextView);

        readingScore = (TextView) findViewById(R.id.readTextView);

        mathScore = (TextView) findViewById(R.id.mathTextView);

        writingScore = (TextView) findViewById(R.id.writingTextView);

        try{ // Gathering and setting school specific data selected and  passed from ListActivity
            JSONObject schoolObject = new JSONObject(getIntent().getStringExtra("School"));
            school_Name = schoolObject.getString("school_name");
            schoolName.setText("School Name: "+school_Name);
            String over_View = schoolObject.getString("overview_paragraph");
            overView.setText(over_View);

        }
        catch (Exception e){
            e.printStackTrace();
        }

        Log.i("Info", schoolName.getText().toString());

        //Background thread to gather SAT scores from API endpoint
        DownloadTask task = new DownloadTask();
        task.execute("https://data.cityofnewyork.us/resource/734v-jeq5.json");

    }

    public class DownloadTask extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(String... urls) { //Same functionality as ListView doInBackground
            StringBuilder webResult = new StringBuilder();
            URL url;
            HttpsURLConnection urlConnection = null;


            try{
                url = new URL(urls[0]);
                urlConnection = (HttpsURLConnection) url.openConnection();
                InputStream inputStream = urlConnection.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String line ="";
                line = bufferedReader.readLine();

                while(line != null){

                    webResult.append(line);
                    line = bufferedReader.readLine();


                }

                return String.valueOf(webResult);

            }
            catch(Exception e){
                e.printStackTrace();

            }
            return null;
        }

        @Override
        protected void onPostExecute(String webResult) {
            Log.i("Info", "Step 3.1 ");
            super.onPostExecute(webResult);
            try{
              JSONArray jsonArray = new JSONArray(webResult);

                //put data from first API endpoint in all caps to match data in second API endpoint
                school_Name = school_Name.toUpperCase();

                for(int i =0; i<jsonArray.length(); i++){
                    JSONObject schoolObject = jsonArray.getJSONObject(i);
                    //store the school name
                    String nameJson = schoolObject.getString("school_name");

                   if(school_Name.equals(nameJson)) {
                       //Once the selected school list is found SAT scores are updated
                       Log.i("Info", "Step 3.2");
                       //Storing of SAT scores
                      final String read = schoolObject.getString("sat_critical_reading_avg_score");
                      final String math = schoolObject.getString("sat_math_avg_score");
                      final String writing = schoolObject.getString("sat_writing_avg_score");

                       mainThread.post(new Runnable() {
                           @Override
                           public void run() {
                               //Updating SAT scores to UI thread
                               readingScore.setText("Reading: "+ read);
                               mathScore.setText("Math: " + math);
                               writingScore.setText("Writing: " + writing);
                           }
                       });

                   }
                }


            }
            catch (Exception e){

                e.printStackTrace();
            }


        }
    }
}
