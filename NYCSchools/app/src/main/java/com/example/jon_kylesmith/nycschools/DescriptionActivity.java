package com.example.jon_kylesmith.nycschools;

import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class DescriptionActivity extends AppCompatActivity {
    private static final String TAG = DescriptionActivity.class.getSimpleName();
    private static final String SCHOOL_SAT_DATA = "https://data.cityofnewyork.us/resource/734v-jeq5.json";

    //List of global variables needed by Main and background thread
    private final Handler mMainThread = new Handler();

    private String mSchoolName;
    private TextView mReadingScore;
    private TextView mMathScore;
    private TextView mWritingScore;
    private TextView mSchoolNameTextView;
    private TextView mOverView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_description);

        // Setting all views to proper resource
        mSchoolNameTextView = findViewById(R.id.schoolNameTextView);
        mOverView = findViewById(R.id.overviewTextView);
        mReadingScore = findViewById(R.id.readTextView);
        mMathScore = findViewById(R.id.mathTextView);
        mWritingScore = findViewById(R.id.writingTextView);

        try{ // Gathering and setting school specific data selected and  passed from ListActivity
            JSONObject schoolObject = new JSONObject(getIntent().getStringExtra("School"));
            mSchoolName = schoolObject.getString("school_name");
            mSchoolNameTextView.setText("School Name: "+ mSchoolName);
            mOverView.setText(schoolObject.getString("overview_paragraph"));
        } catch (JSONException e){
            Log.e(TAG, "JSONException thrown while trying to retrieve Intent", e);
        } catch (Exception e) {
            Log.e(TAG, "General Exception thrown while trying to retrieve Intent", e);
        }

        Log.i(TAG, mSchoolNameTextView.getText().toString()); // For Logging Purposes

        //Background thread to gather SAT scores from API endpoint
        DownloadTask task = new DownloadTask(this);
        task.execute(SCHOOL_SAT_DATA);
    }

    private static class DownloadTask extends AsyncTask<String, Void, String> {
        private final WeakReference<DescriptionActivity> mWeakReference;

        DownloadTask(DescriptionActivity descriptionActivity) {
            mWeakReference = new WeakReference<>(descriptionActivity);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(String... urls) { //Same functionality as ListView doInBackground
            StringBuilder webResult = new StringBuilder();
            InputStream inputStream;
            try {
                URL url = new URL(urls[0]);
                HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
                inputStream = urlConnection.getInputStream();
            } catch (MalformedURLException e) {
                Log.e(TAG, "MalformedURLException thrown while trying to create URL");
                return null;
            } catch (IOException e) {
                Log.e(TAG, "IOException thrown while trying to open URL", e);
                return null;
            } catch (Exception e) {
                Log.e(TAG, "General Exception thrown while trying to create/open URL", e);
                return null;
            }


            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            try {
                String line = bufferedReader.readLine();
                while(line != null){
                    webResult.append(line);
                    line = bufferedReader.readLine();
                }
                return String.valueOf(webResult);
            } catch(IOException e) {
                Log.e(TAG, "IOException thrown while reading from BufferedReader", e);
            } catch (Exception e) {
                Log.e(TAG, "General Exception thrown while reading from BufferedReader", e);
            } finally {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    Log.e(TAG, "Failed to close BufferedReader", e);
                }
            }
            return String.valueOf(webResult);
        }

        @Override
        protected void onPostExecute(String webResult) {
            super.onPostExecute(webResult);
            try{
                final DescriptionActivity descriptionActivity = mWeakReference.get();
                JSONArray jsonArray = new JSONArray(webResult);

                //put data from first API endpoint in all caps to match data in second API endpoint
                descriptionActivity.mSchoolName = descriptionActivity.mSchoolName.toUpperCase();

                for(int i =0; i<jsonArray.length(); i++){
                    JSONObject schoolObject = jsonArray.getJSONObject(i);
                    //store the school name
                    String nameJson = schoolObject.getString("school_name");

                   if(descriptionActivity.mSchoolName.equals(nameJson)) {
                       //Once the selected school list is found SAT scores are updated
                       //Storing of SAT scores
                      final String read = schoolObject.getString("sat_critical_reading_avg_score");
                      final String math = schoolObject.getString("sat_math_avg_score");
                      final String writing = schoolObject.getString("sat_writing_avg_score");

                       descriptionActivity.mMainThread.post(new Runnable() {
                           @Override
                           public void run() {
                               //Updating SAT scores to UI thread
                               descriptionActivity.mReadingScore.setText("Reading: "+ read);
                               descriptionActivity.mMathScore.setText("Math: " + math);
                               descriptionActivity.mWritingScore.setText("Writing: " + writing);
                           }
                       });
                   }
                }
            } catch (JSONException e){
                Log.e(TAG, "JSONException thrown while onPostExecute", e);
            } catch (Exception e) {
                Log.e(TAG, "General Exception thrown while onPostExecute", e);
            }
        }
    }
}
