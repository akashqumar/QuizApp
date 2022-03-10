package com.akash.trivia;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.akash.trivia.data.Repositary;
import com.akash.trivia.databinding.ActivityMainBinding;
import com.akash.trivia.model.Question;
import com.akash.trivia.model.Score;
import com.akash.trivia.util.Prefs;
import com.google.android.material.snackbar.Snackbar;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity  {
    List<Question>questionList;
    private ActivityMainBinding binding;
    private int currentQuestionIndex=0;
    private int scorecounter=0;
    private Score score;
    private Prefs prefs;




    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        binding= DataBindingUtil.setContentView(this,R.layout.activity_main);
        score = new Score();
        prefs=new Prefs(MainActivity.this);
        currentQuestionIndex= prefs.getState();

        //Log.d("TAG", "onCreate: " +prefs.getHighestScore());

        binding.highscoreText.setText(MessageFormat.format("Highest: {0}", String.valueOf(prefs.getHighestScore())));
        binding.scoreText.setText(MessageFormat.format("Current score: {0}", String.valueOf(score.getScore())));

        questionList=new Repositary().getQuestions(questionArrayList -> {
                    binding.questionTextView.setText(questionArrayList.get(currentQuestionIndex).getAnswer());

                    update_counter(questionArrayList);


                }
        );
        binding.buttonNext.setOnClickListener(view -> {
            getNextQuestion();
            //prefs.savehighestScore(scorecounter);

        });

        binding.buttonTrue.setOnClickListener(view -> {
            checkAnswer(true);
            updateQuestion();

        });

        binding.buttonFalse.setOnClickListener(view -> {
            checkAnswer(false);
            updateQuestion();

        });
}

    private void getNextQuestion() {
        currentQuestionIndex=(currentQuestionIndex+1) % questionList.size();
        updateQuestion();
    }

    private void checkAnswer(boolean userchooseCorrect) {
        boolean answer = questionList.get(currentQuestionIndex).isAnswerTrue();
        int snackMsgId;
        if(userchooseCorrect ==answer) {
            snackMsgId = R.string.correct_answer;
            fadeAnimation();
            addPoints();
        }else {
            deductPoint();
            snackMsgId = R.string.incorrect_answer;
            shakeAnimation();
        }
        Snackbar.make(binding.cardView,snackMsgId,Snackbar.LENGTH_SHORT)
                .show();
    }

    private void update_counter(ArrayList<Question> questionArrayList) {
        binding.textViewOutOf.setText(String.format(getString(R.string.text_formatted), currentQuestionIndex, questionArrayList.size()));
    }

    private void fadeAnimation()
    {
        AlphaAnimation alphaAnimation= new AlphaAnimation(1.0f,0.0f);
        alphaAnimation.setDuration(300);
        alphaAnimation.setRepeatCount(1);
        alphaAnimation.setRepeatMode(Animation.REVERSE);

        binding.cardView.setAnimation(alphaAnimation);

        alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                binding.questionTextView.setTextColor(Color.GREEN);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                binding.questionTextView.setTextColor(Color.WHITE);
                getNextQuestion();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    private void updateQuestion() {
        String question = questionList.get(currentQuestionIndex).getAnswer();
        binding.questionTextView.setText(question);
        update_counter((ArrayList<Question>) questionList);

    }
    private void shakeAnimation() {
        Animation shake = AnimationUtils.loadAnimation(MainActivity.this,R.anim.shake_animation);
        binding.cardView.setAnimation(shake);

        shake.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                binding.questionTextView.setTextColor(Color.RED);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                binding.questionTextView.setTextColor(Color.WHITE);
                getNextQuestion();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

    }

    private void deductPoint(){

        if(scorecounter>0){
            scorecounter-= 100;
            score.setScore(scorecounter);
            binding.scoreText.setText(MessageFormat.format("Current score : {0}", String.valueOf(score.getScore())));
        }else{
            scorecounter=0;
            score.setScore(scorecounter);
            binding.scoreText.setText(MessageFormat.format("Current score : {0}", String.valueOf(score.getScore())));
        }
    }
    private void addPoints() {
        scorecounter += 100;
        score.setScore(scorecounter);
        binding.scoreText.setText(String.valueOf(score.getScore()));
        binding.scoreText.setText(MessageFormat.format("Current score : {0}", String.valueOf(score.getScore())));
    }

    @Override
    protected void onPause() {
        prefs.saveHighestScore(score.getScore());
        binding.highscoreText.setText(MessageFormat.format("Highest: {0}", String.valueOf(prefs.getHighestScore())));
        prefs.setState(currentQuestionIndex);
        //Log.d("State", "onPause: saving state " + prefs.getState() );
        //Log.d("Pause", "onPause: saving score " + prefs.getHighestScore() );
        super.onPause();
    }
}