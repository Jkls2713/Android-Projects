package com.example.jon_kylesmith.nycschools;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

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
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

public class ListActivity extends AppCompatActivity {
    private static final String TAG = ListActivity.class.getSimpleName();
    private static final String CITY_DATA = "https://data.cityofnewyork.us/resource/97mf-9njv.json";

    //Global variables used in both UI and background thread
    private final Handler mMainThread = new Handler();
    private final ArrayList<String> mSchoolList = new ArrayList<>(); //ArrayList that will later store mSchoolList names to populate listview
    ArrayAdapter<String> adapter;
    private ListView mSchoolListView;
    private JSONArray mJsonArray;
    boolean searched = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        mSchoolListView = findViewById(R.id.schoolsListView);

        DownloadTask task = new DownloadTask(this);
        task.execute(CITY_DATA); // Run background thread to grab data from API endpoint
        setOnClickListeners();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) { //inflates menu bar to include search bar
        MenuInflater mInflater = getMenuInflater();
        mInflater.inflate(R.menu.menu_search, menu);
        MenuItem mItem = menu.findItem(R.id.menuSearch);
        SearchView mSearchView = (SearchView)mItem.getActionView();

        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) { //filter search when a char is added or removed
                adapter = (ArrayAdapter<String>) mSchoolListView.getAdapter();
                adapter.getFilter().filter(s);
                mSchoolListView.setAdapter(adapter);
                searched = true;
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    private void setOnClickListeners() {
        mSchoolListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {//Functionality for selection of an item in Listview
                if(searched) {//adjust postions if search has filetered results
                    String named = (String) adapter.getItem(i);
                    //Alert user which mSchoolList they have selected
                    Toast.makeText(ListActivity.this, "School: " + named, Toast.LENGTH_SHORT).show();
                    int position = 0;
                    for (int pos = 0; pos < mJsonArray.length(); pos++) {
                        try {
                            if (mJsonArray.getJSONObject(position).getString("school_name") != named) {
                                position = pos;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    //Pass the selected mSchoolList JSON object converted to a string to next activity
                    Intent intent = new Intent(ListActivity.this, DescriptionActivity.class);
                    try {
                        intent.putExtra("School", mJsonArray.getJSONObject(position).toString());
                    } catch (JSONException e) {
                        Log.e(TAG, "Could not getJSONObject from mJsonArray", e);
                    }
                    startActivity(intent); //Start of the next activity
                }

                else{//if no search filtering has been done keep original positions
                    Toast.makeText(ListActivity.this, "School: " + mSchoolList.get(i), Toast.LENGTH_SHORT).show();


                    //Pass the selected mSchoolList JSON object converted to a string to next activity
                    Intent intent = new Intent(ListActivity.this, DescriptionActivity.class);
                    try {
                        intent.putExtra("School", mJsonArray.getJSONObject(i).toString());
                    } catch (JSONException e) {
                        Log.e(TAG, "Could not getJSONObject from mJsonArray", e);
                    }
                    startActivity(intent); //Start of the next activity
                }
            }
        });
    }

    private static class DownloadTask extends AsyncTask<String, Void, String>{
        private WeakReference<ListActivity> mWeakReference;


        DownloadTask(ListActivity listActivity) {
            mWeakReference = new WeakReference<>(listActivity);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(String... urls) {
            StringBuilder webResult = new StringBuilder();
            InputStream inputStream;

            try {
                URL url = new URL(urls[0]); //store and cast string to url
                HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection(); //open the connection to the url
                inputStream = urlConnection.getInputStream();
            } catch (MalformedURLException e) {
                Log.e(TAG, "MalformedURLException thrown while creating URL");
                return null;
            } catch (IOException e) {
                Log.e(TAG, "IOException thrown while opening URL", e);
                return null;
            } catch (Exception e) {
                Log.e(TAG, "General Exception thrown while creating/opening URL", e);
                return null;
            }

            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            /*TIME SAVER: buffered reader allows for the result to be read line by rather than
            character by character which greatly reduces the time of this thread given the data volume
            being read*/
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            try {
                String line = bufferedReader.readLine();
                while(line != null){
                    /*TIME SAVER: using string builder and append allows for the string to be added
                    to rather than rebuilding with a new value every time*/
                    webResult.append(line);
                    line = bufferedReader.readLine(); //move to read and store next line of data
                }
            } catch (IOException e) {
                Log.e(TAG, "IOException thrown while reading from BufferedReader", e);
            }
            catch(Exception e){
                Log.e(TAG, "General Exception thrown while reading from BufferedReader", e);
            } finally {
                try {
                    bufferedReader.close(); // No need to close InputStreamReader - closed by default with BufferedReader
                } catch (IOException e) {
                    Log.e(TAG, "Failed to close BufferedReader", e);
                }
            }
            return String.valueOf(webResult); //return the string built version of the string builder
        }

        @Override
        protected void onPostExecute(String webResult) {
            super.onPostExecute(webResult);

            try{
                final ListActivity listActivity = mWeakReference.get();
                listActivity.mJsonArray = new JSONArray(webResult); // Store result from API endpoint into JSON array
                for(int i =0; i< listActivity.mJsonArray.length(); i++){
                    JSONObject schoolObject = listActivity.mJsonArray.getJSONObject(i);
                    String schoolName = schoolObject.getString("school_name"); //Store the schoolList name of each JSON object
                    listActivity.mSchoolList.add(schoolName); //add school name to overall schoolList arraylist
                    Log.i("Info", schoolName);
                }

                listActivity.mMainThread.post(new Runnable() {
                    @Override
                    public void run() { //Interact with Main UI thread to create list of all school names from API endpoint
                        ArrayAdapter<String> newAdapter = new ArrayAdapter<>(listActivity, android.R.layout.simple_list_item_1, listActivity.mSchoolList);
                        listActivity.mSchoolListView.setAdapter(newAdapter);


                    }
                });

            } catch (JSONException e){
                Log.e(TAG, "JSONException thrown onPostExecute", e);
            } catch (Exception e) {
                Log.e(TAG, "General Exception thrown onPostExecute", e);
            }
        }
    }
}
