package com.andro.indieschool.myawesomeapp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.andro.LaunchNewIntent;

/**
 * Created by herisulistiyanto on 8/18/17.
 */
@LaunchNewIntent
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        testMethod();
    }

//    @LaunchNewIntent
    private void testMethod() {
        Toast.makeText(this, "MASUK MAIN PAGE", Toast.LENGTH_SHORT).show();
    }

//    @LaunchNewIntent
    private interface contohInterface {
        void dummyFunc();
    }
}
