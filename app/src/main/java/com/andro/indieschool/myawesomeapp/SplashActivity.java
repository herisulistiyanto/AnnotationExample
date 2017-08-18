package com.andro.indieschool.myawesomeapp;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        handleNextPage();
    }

    private void handleNextPage() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                AwesomeNavigator.startMainActivity(SplashActivity.this);
                finish();
            }
        }, 5000);
    }
}
