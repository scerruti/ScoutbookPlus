package com.otabi.scoutbookplus;

/**
 * Created by Stephen on 4/15/2016.
 *
 * This is a plain old splash screen activity that transfers control to the main activity.
 */

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}