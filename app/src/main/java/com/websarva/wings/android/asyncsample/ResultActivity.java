package com.websarva.wings.android.asyncsample;

import androidx.annotation.UiThread;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
public class ResultActivity extends AppCompatActivity {
    TextView tvWeatherTelop;
    TextView tvWeatherDesc;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        //天気を表示するtextビューを取得
        tvWeatherTelop = findViewById(R.id.tvWeatherTelop);
        tvWeatherDesc = findViewById(R.id.tvWeatherDesc);
        //天気情報を表示
        //tvWeatherTelop.setText(telop);
        //tvWeatherDesc.setText(desc);

    }
}