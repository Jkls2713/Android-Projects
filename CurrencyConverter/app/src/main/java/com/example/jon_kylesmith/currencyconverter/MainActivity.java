package com.example.jon_kylesmith.currencyconverter;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    public double convert(double dollars){
        return (dollars * 0.81);
    }
    public void clickConvert(View view){
        EditText inputNum = (EditText)findViewById(R.id.inputNum);
        double dollars = Double.parseDouble(inputNum.getText().toString());
        double euros = convert(dollars);
        TextView outputNum = (TextView) findViewById(R.id.outputNum);
        String output = Double.toString(euros);
        Toast.makeText(MainActivity.this, "Conversion Done", Toast.LENGTH_LONG).show();
        outputNum.setText(output);
        Log.i("Euros", output);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}
