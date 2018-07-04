package com.jovan_ristic.streetsmart.activity;

import android.content.Intent;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.jovan_ristic.streetsmart.R;

public class LoadingActivity extends AppCompatActivity {


    private FirebaseAuth auth;

    private ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try{
            setContentView(R.layout.activity_loading);
        }
        catch(Exception|OutOfMemoryError outOfMemoryError)
        {
            Toast.makeText(this,getString(R.string.errorMsg),Toast.LENGTH_SHORT).show();
            finish();
        }
        auth = FirebaseAuth.getInstance();
        progressBar = findViewById(R.id.progressBar);


        new CountDownTimer(5000, 45) {
            @Override
            public void onTick(long millisUntilFinished) {
                progressBar.setProgress(progressBar.getProgress() + 1);
            }

            @Override
            public void onFinish() {
                startNewActivity();
                finish();
            }
        }.start();
    }

    public void startNewActivity()
    {
        if (auth.getCurrentUser() != null) {
            startActivity(new Intent(LoadingActivity.this, ProfileActivity.class));
        }
        else {
            Intent gotoLogIn  = new Intent(LoadingActivity.this, MainActivity.class);
            startActivity(gotoLogIn);
        }

    }
}
