package com.websarva.wings.android.asyncsample;

import androidx.annotation.UiThread;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
public class ResultActivity extends AppCompatActivity {
    TextView tvWeatherTelop;
    TextView tvWeatherDesc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {



        Intent intent = getIntent();
        String telop = intent.getStringExtra("TEROP");
        String desc = intent.getStringExtra("DESC");


        Log.i("AsyncSample","Result onCreate() called.");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        //天気を表示するtextビューを取得
        tvWeatherTelop = findViewById(R.id.tvWeatherTelop);
        tvWeatherDesc = findViewById(R.id.tvWeatherDesc);
        //天気情報を表示
        tvWeatherTelop.setText(telop);
        tvWeatherDesc.setText(desc);

        //アクションバー
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // アクションバーがnullじゃないことを担保して実行する
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem menuButton){
        int buttonId = menuButton.getItemId();
        if (buttonId == android.R.id.home){
            finish();
            return true;
        }
        return false;
    }


    @Override
    public void onStart(){
        Log.i("AsyncSample","Result onCreate() called.");
        super.onStart();
    }

    @Override
    public void onRestart(){
        Log.i("AsyncSample","Result onCreate() called.");
        super.onRestart();
    }

    @Override
    public void onResume(){
        Log.i("AsyncSample","Result onCreate() called.");
        super.onResume();
    }

    @Override
    public void onPause(){
        Log.i("AsyncSample","Result onCreate() called.");
        super.onPause();
    }

    @Override
    public void onStop(){
        Log.i("AsyncSample","Result onCreate() called.");
        super.onStop();
    }

    @Override
    public void onDestroy(){
        Log.i("AsyncSample","Result onCreate() called.");
        super.onDestroy();
    }


}