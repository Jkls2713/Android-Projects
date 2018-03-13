package com.example.jon_kylesmith.braintrainer;

import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    TextView timer ;
    TextView problem ;
    TextView score ;
    TextView start ;
    TextView correct ;

    Button choice1 ;
    Button choice2 ;
    Button choice3 ;
    Button choice4 ;
    int answer;
    int sum;
    int score_count =0;
    int total =0;
    CountDownTimer myTimer;


    public void updateScore(){
        total++;
        String stringScore = Integer.toString(score_count) + "/" + Integer.toString(total);
        score.setText(stringScore);
    }

    public void chooseAnswer(View view) throws InterruptedException {
         answer = 0;
        switch(view.getId()){
            case R.id.button3:
                answer = Integer.parseInt(choice1.getText().toString());
                break;
            case R.id.button4:
                answer = Integer.parseInt(choice2.getText().toString());
                break;
            case R.id.button5:
                answer = Integer.parseInt(choice3.getText().toString());
                break;
            case R.id.button6:
                answer = Integer.parseInt(choice4.getText().toString());
                break;
        }
        Log.i("Answer", Integer.toString(answer));
        if(sum == answer) {
            //correct.setVisibility(View.VISIBLE);
            Toast.makeText(getApplicationContext(), "Correct", Toast.LENGTH_SHORT).show();
            score_count ++;
        }
        else{
            Toast.makeText(getApplicationContext(), "Wrong", Toast.LENGTH_SHORT).show();
        }
       updateScore();
        myTimer.cancel();
        newQuestion();

    }

    public String setChoice(int skip){
       int setChoice = (int) (Math.random() * (200));
       if (setChoice == skip)
                setChoice += (int)Math.random() * 5;
       return Integer.toString(setChoice);
    }
    //ImageView imageVImageViewiew.animate();
    public void newChoices(int correctAnswer){
        int spot = (int) (Math.random()*4);

        choice1.setText(setChoice(correctAnswer));
        choice2.setText(setChoice(correctAnswer));
        choice3.setText(setChoice(correctAnswer));
        choice4.setText(setChoice(correctAnswer));
        if(spot == 0){
            choice1.setText(Integer.toString(correctAnswer));
        }
        if(spot == 1){
            choice2.setText(Integer.toString(correctAnswer));
        }
        if(spot == 2){
            choice3.setText(Integer.toString(correctAnswer));
        }
        if(spot == 3){
            choice4.setText(Integer.toString(correctAnswer));
        }
    }

    public void newQuestion(){
        //correct.setVisibility(View.INVISIBLE);
        String newProblem;
        int num1 = (int) (Math.random()*100);
        int num2 = (int) (Math.random()*100);
         sum = num1 + num2;
        newChoices(sum);
        newProblem =  Integer.toString(num1) + " + " + Integer.toString(num2);
        Log.i("Question", "newQuestion: " + newProblem);
        problem.setText(newProblem);


        myTimer = new CountDownTimer (30000, 1000){
            public void onTick(long millisecondsLeft){
                String timeLeft = String.valueOf(millisecondsLeft/(1000));
            timer.setText(timeLeft);
            }
            public void onFinish(){
                updateScore();
                newQuestion();
            }
        }.start();

    }

    public void onClickStart(View view){
        timer.setVisibility(View.VISIBLE);
        problem.setVisibility(View.VISIBLE);
        score.setVisibility(View.VISIBLE);
        //correct.setVisibility(View.VISIBLE);
        choice1.setVisibility(View.VISIBLE);
        choice2.setVisibility(View.VISIBLE);
        choice3.setVisibility(View.VISIBLE);
        choice4.setVisibility(View.VISIBLE);
        start.setVisibility(View.INVISIBLE);

        newQuestion();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        timer = (TextView) findViewById(R.id.Timer);
        problem = (TextView) findViewById(R.id.problem);
        score = (TextView)findViewById(R.id.score);
        correct = (TextView)findViewById(R.id.correct);
        choice1 = (Button)findViewById(R.id.button3);
        choice2 = (Button)findViewById(R.id.button4);
        choice3 = (Button)findViewById(R.id.button5);
        choice4 = (Button)findViewById(R.id.button6);
        start = (TextView)findViewById(R.id.Start);




        start.setVisibility(View.VISIBLE);
        timer.setVisibility(View.INVISIBLE);
        problem.setVisibility(View.INVISIBLE);
        score.setVisibility(View.INVISIBLE);
        correct.setVisibility(View.INVISIBLE);
        choice1.setVisibility(View.INVISIBLE);
        choice2.setVisibility(View.INVISIBLE);
        choice3.setVisibility(View.INVISIBLE);
        choice4.setVisibility(View.INVISIBLE);


    }
}
