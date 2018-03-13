package com.example.jon_kylesmith.timestables;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SeekBar;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public void updateList(int timestable,  ListView listView) {
        ArrayList<String> numbers = new ArrayList<String>();
        for (int i = 1; i <= 10; i++) {
            numbers.add(Integer.toString(timestable * i));
        }
        ArrayAdapter arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, numbers);
        listView.setAdapter(arrayAdapter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final SeekBar timestableseeker = (SeekBar)findViewById(R.id.seekBar);

        timestableseeker.setMax(20);
        timestableseeker.setProgress(10);


        final ListView listView = (ListView) findViewById(R.id.listView);


        timestableseeker.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                int minVal = 1;
                int timestableVal;
                if(i < minVal)
                    timestableVal = minVal;
                else
                    timestableVal = i ;
                timestableseeker.setProgress(timestableVal);
                Log.i("Seeker spot: ", Integer.toString(timestableVal));
                updateList(timestableVal, listView);



            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }



        });
        updateList(10, listView);


    }

}
