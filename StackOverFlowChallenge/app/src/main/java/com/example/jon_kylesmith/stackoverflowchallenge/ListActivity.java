package com.example.jon_kylesmith.stackoverflowchallenge;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
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
    private static final String USER_DATA = "https://api.stackexchange.com/2.2/users?site=stackoverflow";

    //Global variables used in both UI and background thread
    private final Handler mMainThread = new Handler();
    private final ArrayList<String> mUserName = new ArrayList<>(); //ArrayList that will later store mUserName names to populate listview
    private final ArrayList<String> mImageURL = new ArrayList<>();  //ArrayList to store URLs for eventual bitmap conversion
    private final ArrayList<Bitmap> mImageList = new ArrayList<>(); //ArrayList to contain URLs after bitmap conversion
    private final ArrayList<String> mBadgeList = new ArrayList<>(); //ArrayList to contain Badges per User

    private ListView mListView;
    private JSONArray mJsonArray;

    CustomList searchAdapter;
    ProgressBar progressBar;
    SQLiteDatabase database;

    boolean searched;
    boolean insertTable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        mListView = findViewById(R.id.listView);
        progressBar = findViewById(R.id.progressbar);
        progressBar.setVisibility(View.INVISIBLE);
        insertTable = true;

        database = this.openOrCreateDatabase("Users", MODE_PRIVATE, null);
        database.execSQL("CREATE TABLE IF NOT EXISTS users (  name VARCHAR  , badges VARCHAR, images VARCHAR, rowNum INT(3))");

        Cursor cursor = database.rawQuery("SELECT * FROM users", null);

        int userIndex = cursor.getColumnIndex("name");
        int badgeIndex = cursor.getColumnIndex("badges");
        int imageIndex = cursor.getColumnIndex("images");

        try {
            cursor.moveToFirst();


            while(!cursor.isAfterLast()){

                mUserName.add(cursor.getString(userIndex));
                mBadgeList.add(cursor.getString(badgeIndex));
                String base64Byte =  cursor.getString(imageIndex);
                byte [] byteArray = Base64.decode(base64Byte, Base64.DEFAULT);
                Bitmap bitmap_decode = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
                mImageList.add(bitmap_decode);
                cursor.moveToNext();
                insertTable = false;
            }
        } catch (Exception e) {
            Log.e(TAG, "Exception thrown while moving through cursor", e);
        }
        if(!insertTable)
            createAdapter();
        //insertTable = false;

        DownloadTask task = new DownloadTask(this);
        task.execute(USER_DATA); // Run background thread to grab data from API endpoint
        cursor.close();
    }

    private void createAdapter() {
        CustomList adapter = new CustomList(this, mUserName, mImageList, mBadgeList);
        mListView.setAdapter(adapter);
    }

    public CustomList adapterUpdate(String s) {

        ArrayList<String> mSearchUser = new ArrayList<>();
        ArrayList<Bitmap> mSearchImage = new ArrayList<>();
        ArrayList<String> mSearchBadge = new ArrayList<>();

        for(int i =0; i < mUserName.size(); i++) { //insert results where searched string is substring of username
            if(mUserName.get(i).toLowerCase().contains(s.toLowerCase())) {
                mSearchUser.add(mUserName.get(i)); //store filtered username
                mSearchBadge.add(mBadgeList.get(i)); //store filtered username's badges
                mSearchImage.add(mImageList.get(i)); //store filtered usernames's image
            }
        }

        searchAdapter = new
                CustomList(this, mSearchUser,mSearchImage ,mSearchBadge); // create new adapter
        return searchAdapter;
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


                CustomList adap = adapterUpdate(s); //custom filter function built to filter custom adapter and return new adapter
                mListView.setAdapter(adap);

                searched = true;
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }



    private static class DownloadTask extends AsyncTask<String, Void, String> {

        private WeakReference<ListActivity> mWeakReference;


        DownloadTask(ListActivity listActivity) {
            mWeakReference = new WeakReference<>(listActivity);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            final ListActivity listActivity = mWeakReference.get();
            listActivity.progressBar.setVisibility(View.VISIBLE); // progress bar only placed when download occurring
        }

        @Override
        protected String doInBackground(String... urls) {
            StringBuilder webResult = new StringBuilder();
            InputStream inputStream;

            try {
                inputStream = createInputStream(urls[0]);
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

            try { //Wanted to show native android skills but a Get command would have worked just as well
                readData(bufferedReader, webResult);
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

            parseResult(webResult);
            return String.valueOf(webResult); //return the string built version of the string builder
        }

        @Override
        protected void onPostExecute(String webResult) {
           super.onPostExecute(webResult);
           try{
                final ListActivity listActivity = mWeakReference.get();
                listActivity.progressBar.setVisibility(View.INVISIBLE);

                if(listActivity.insertTable) {
                    listActivity.mMainThread.post(new Runnable() {
                        @Override
                        public void run() { //Interact with Main UI thread to create list of all user names badges and gravatars from API endpoint

                            CustomList adapter = new
                                    CustomList(listActivity, listActivity.mUserName, listActivity.mImageList, listActivity.mBadgeList);
                            listActivity.mListView.setAdapter(adapter);
                        }
                    });
                }
            } catch (Exception e) {
                Log.e(TAG, "General Exception thrown onPostExecute", e);
            }
        }


        private static InputStream createInputStream(String urlString) throws Exception {
            URL url = new URL(urlString); //store and cast string to url
            HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection(); //open the connection to the url
            return urlConnection.getInputStream();
        }

        private static void readData(BufferedReader reader, StringBuilder webResult) throws Exception {
            String line = reader.readLine();

            while(line != null){
                webResult.append(line);
                Log.i(TAG, line);
                line = reader.readLine(); // move to read and store next line of data
            }
        }

        private void parseResult(StringBuilder webResult) {
            final ListActivity listActivity = mWeakReference.get();

            try {
                JSONObject results = new JSONObject(String.valueOf(webResult));
                listActivity.mJsonArray = results
                        .getJSONArray("items"); // Store result from API endpoint into JSON array

                for (int i = 0; i < listActivity.mJsonArray.length(); i++) {
                    JSONObject schoolObject = listActivity.mJsonArray.getJSONObject(i);

                    String userName = schoolObject
                            .getString("display_name"); //Store the user name of each JSON object
                    String badeges = schoolObject
                            .getString("badge_counts");  //Store badges for each JSON object
                    String gravatr = schoolObject.getString("profile_image");

                    if (!listActivity.mUserName.contains(userName)) { //avoid duplicate entries

                        listActivity.mUserName
                                .add(userName); //add user name to overall UserName arraylist
                        badeges = badeges.substring(1,
                                badeges.length() - 1); //remove brackets around badge string
                        String[] result = badeges.split("\""); //cleaner use of badges information
                        StringBuilder badgesClean = new StringBuilder();
                        for (String word : result)
                            badgesClean.append(word);
                        listActivity.mBadgeList.add(badgesClean.toString());

                        listActivity.mImageURL.add(gravatr);
                        Bitmap bitmaps = null;

                        try {  //conversion of URL to image as well as updating table
                            bitmaps = BitmapFactory
                                    .decodeStream((InputStream) new URL(gravatr).getContent());
                        } catch (MalformedURLException e) {
                            Log.e(TAG, "MalformedURLException thrown while creating URL");
                        } catch (IOException e) {
                            Log.e(TAG, "IOException thrown while decoding Profile Image", e);
                        }

                        if (bitmaps != null) {
                            listActivity.mImageList.add(bitmaps);
                            ByteArrayOutputStream stream = new ByteArrayOutputStream();
                            bitmaps.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                            byte[] imageByteArray = stream.toByteArray();
                            String base64Image = Base64
                                    .encodeToString(imageByteArray, Base64.DEFAULT);

                            //If table is empty insert values else update them
                            if (listActivity.insertTable) {
                                listActivity.database.execSQL(
                                        "INSERT INTO users (name, badges, images, rowNum) VALUES ('" + userName + "', '" + badgesClean + "', '" + base64Image + "' ,'" + i + "' )");
                            } else {
                                listActivity.database.execSQL(
                                        "UPDATE users SET name = '" + userName + "', badges = '" + badgesClean + "', images = '" + base64Image + "' WHERE rowNum == '" + i + "'");
                            }
                        }
                    }
                }
            } catch (JSONException e) {
                Log.e(TAG, "JSONException thrown while parsing result", e);
            }
        }
    }
}
