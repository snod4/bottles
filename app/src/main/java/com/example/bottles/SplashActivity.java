package com.example.bottles;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.telecom.Call;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        Intent intent = new Intent();
        intent.setClass(this, MapsActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
      //  finish();
    }
}
