package com.example.jon_kylesmith.nycschools;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import javax.net.ssl.HttpsURLConnection;

public class ListActivity extends AppCompatActivity {

    //Global variables used in both UI and background thread
    Handler mainThread = new Handler();
    ListView schoolListView;
    JSONArray jsonArray;
    ArrayList<String> school;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        schoolListView = (ListView) findViewById(R.id.schoolsListView);
        school = new ArrayList<String>(); //ArrayList that will later store school names to populate listview


        DownloadTask task = new DownloadTask();
        task.execute("https://data.cityofnewyork.us/resource/97mf-9njv.json"); // Run background thread to grab data from API endpoint


        schoolListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {//Functionality for selection of an item in Listview

                //Alert user which school they have selected
                Toast.makeText(getApplicationContext(), "School: " + school.get(i), Toast.LENGTH_SHORT).show();

                //Pass the selected school JSON object converted to a string to next activity
                Intent intent = new Intent(getBaseContext(), DescriptionActivity.class);
                try {
                    intent.putExtra("School",jsonArray.getJSONObject(i).toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                startActivity(intent); //Start of the next activity

            }
        });
    }
    public class DownloadTask extends AsyncTask<String, Void, String>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(String... urls) {
            StringBuilder webResult = new StringBuilder();
            URL url;
            HttpsURLConnection urlConnection = null;


            try{
                url = new URL(urls[0]); //store and cast string to url

                urlConnection = (HttpsURLConnection) url.openConnection(); //open the connection to the url

                InputStream inputStream = urlConnection.getInputStream();

                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);

                /*TIME SAVER: buffered reader allows for the result to be read line by rather than
                character by character which greatly reduces the time of this thread given the data volume
                being read*/
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);


                String line ="";
                line = bufferedReader.readLine();


                while(line!= null){
                    /*TIME SAVER: using string builder and append allows for the string to be added
                    to rather than rebuilding with a new value every time;
                    */
                    webResult.append(line);

                    line = bufferedReader.readLine(); //move to read and store next line of data

                }
                return String.valueOf(webResult); //return the string built version of the string builder

            }
            catch(Exception e){
                e.printStackTrace();

            }
            return null;
        }

        @Override
        protected void onPostExecute(String webResult) {

            super.onPostExecute(webResult);
            try{

                jsonArray = new JSONArray(webResult); // Store result from API endpoint into JSON array
                for(int i =0; i<jsonArray.length(); i++){
                    JSONObject schoolObject = jsonArray.getJSONObject(i);
                    String school_Name = schoolObject.getString("school_name"); //Store the school name of each JSON object
                    school.add(school_Name); //add school name to overall school arraylist
                    Log.i("Info", school_Name);
                }

                mainThread.post(new Runnable() {
                    @Override
                    public void run() { //Interact with Main UI thread to create list of all school names from API endpoint
                        ArrayAdapter<String> newAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, school);
                        schoolListView.setAdapter(newAdapter);

                    }
                });

            }
            catch (Exception e){

                e.printStackTrace();
            }


        }
    }
}
