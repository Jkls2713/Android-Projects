package com.example.jon_kylesmith.numbershapes;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    class Num{
        double value;

        public boolean isSquare(){
            if (Math.sqrt(value) % 1 == 0)
                return true;
            return false;
        }
        public boolean isTriangular(){
            for(double i =1; i<value; i++){
                double tri = (i*(i+1) / 2);
                if(tri == value)
                    return true;
            }
            if(value == 1)
                return true;
            return false;
        }
    }

    public void testNumber(View view){
        EditText input = (EditText)findViewById(R.id.inputNum);
        TextView square = (TextView) findViewById(R.id.textSquare);
        TextView tri = (TextView) findViewById(R.id.textTri);
        if(input.getText().toString().isEmpty()) {
            Toast.makeText(MainActivity.this, "Please Enter a number", Toast.LENGTH_LONG).show();
            square.setText("");
            tri.setText("");
        }
        else {
            double inputNum = Double.parseDouble(input.getText().toString());
            Log.i("Main", input.getText().toString());
            Num number = new Num();
            number.value = inputNum;

            if (number.isSquare())
                square.setText("Yes");
            else
                square.setText("No");
            if (number.isTriangular())
                tri.setText("Yes");
            else
                tri.setText("No");
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}
