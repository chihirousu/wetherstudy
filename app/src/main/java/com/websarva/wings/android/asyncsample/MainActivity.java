package com.websarva.wings.android.asyncsample;

import androidx.annotation.UiThread;
import androidx.annotation.WorkerThread;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.os.HandlerCompat;
import androidx.core.text.SpannableStringBuilderKt;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.SpannableStringBuilder;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private InputMethodManager mInputMethodManager;

    //ログに記載するタグ用の文字列
    private  static final String DEBUG_TAG = "AsynSample";
    //お天気情報のURL
    private static final String WEATHERINFO_URL ="https://api.openweathermap.org/data/2.5/weather?lang=ja";
    //お天気APIにアクセスするためのAPIキー
    private static final String APP_ID = "c3cd44f35c1908efb0247c72ed39b446";
    //リストビューに表示させるリストデータ
    private List<Map<String, String>> _list;

    EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editText = findViewById(R.id.edit_text);




        // 表示ボタンであるButtonオブジェクトを取得。
        Button btClick = findViewById(R.id.btClick);
        // リスナクラスのインスタンスを生成。
        HelloListener listener = new HelloListener();
        listener.context = this;
        // 表示ボタンにリスナを設定。
        btClick.setOnClickListener(listener);

    }

    // コンテキストと都市名から緯度経度を取得するメソッド
    public double[] getLocationFromCityName(Context context, String name) {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        try {
            List<Address> location = geocoder.getFromLocationName(name, 1);
            if (location == null || location.size() < 1) {
                return null;
            }

            Address address = location.get(0);
            double[] latlng = { address.getLatitude(), address.getLongitude() };
            return latlng;
        }
        catch (IOException e) {
            // 例外処理
            return null;
        }
    }

    //緯度と経度を引数にとってURL文字列を生成して返すメソッド
    public String getURLStringFromLatLong(double lat,double lon) {
        String urlStr = WEATHERINFO_URL + "&lat=" + lat + "&lon=" + lon + "&appid=" + APP_ID;
        return urlStr;
    }

    private class HelloListener implements View.OnClickListener {

        MainActivity context;
        @Override
        public void onClick(View view) {
            // 入力欄であるEditTextオブジェクトを取得。
            EditText input = findViewById(R.id.edit_text);

            // タップされた画面部品のidのR値を取得。
            int id = view.getId();
            // idのR値に応じて処理を分岐。
            //if (id == R.id.btClick) {
            if (id == R.id.btClick) {
                // 入力された名前文字列を取得。
                String inputStr = input.getText().toString();
                // メッセージを表示。
                System.out.println("入力文字は:" + inputStr);

                double[] citylatlong = getLocationFromCityName(context, inputStr);
                for(double d: citylatlong) {
                    System.out.println(d + "緯度");

                }
                Log.d("ちひろ",citylatlong.toString());

                // 緯度
                double lat = citylatlong[0];
                // 経度
                double lon = citylatlong[1];
                String urlbox = getURLStringFromLatLong(lat, lon);
                // リクエスト（URLで通信をする）開始
                receiveWeatherInfo(urlbox);


            }
        }


        //お天気情報の取得処理を行うメソッド
        @UiThread
        private void receiveWeatherInfo(final String urlFull) {
            //ここに非同期で天気情報を取得する処理を記述
            Looper mainLooper = Looper.getMainLooper();
            Handler handler = HandlerCompat.createAsync(mainLooper);
            WeatherInfoBackgroundReceiver backgroundReceiver =
                    new WeatherInfoBackgroundReceiver(handler, urlFull);

            ExecutorService executorService = Executors.newSingleThreadExecutor();
            executorService.submit(backgroundReceiver);
        }


        //非同期でお天気情報APIにアクセスするためのクラス

        private class WeatherInfoBackgroundReceiver implements Runnable {
            //ハンドラオブジェクト
            private final Handler _handler;
            //お天気情報を取得するURL
            private final String _urlFull;

            //コンストラクタ
            private WeatherInfoBackgroundReceiver(Handler handler, String urlFull) {
                _handler = handler;
                _urlFull = urlFull;
            }


            @WorkerThread
            @Override
            public void run() {
                //Http接続を行うHTTPURL＿Connectionオブジェクトを宣言。finallyで解放するためにtry外で宣言
                HttpURLConnection con = null;
                //HTTP接続のレスポンスデータとして取得するInputStreamオブジェクトを宣言。同じくtry外で宣言
                InputStream is = null;
                //天気情報サービスから取得したJSON文字列。天気情報が格納されている
                String result = "";
                try {
                    //URLオブジェクトを生成
                    URL url = new URL(_urlFull);
                    //URLオブジェクトからHttpURL_Connectionオブジェクトを取得
                    con = (HttpURLConnection) url.openConnection();
                    //接続に使っても良い時間を設定
                    con.setConnectTimeout(1000);
                    //データ取得に使っても良い時間
                    con.setReadTimeout(1000);
                    //HTTPメソッドをGETに設定
                    con.setRequestMethod("GET");
                    //接続
                    con.connect();
                    //HTTPURLConnectionオブジェクトからレスポンスデータを取得
                    is = con.getInputStream();
                    //レスポンスデータであるInputStreamオブジェクトを文字列に変換
                    result = is2String(is);
                } catch (MalformedURLException ex) {
                    Log.e(DEBUG_TAG, "URL変換失敗", ex);
                }

                //タイムアウトの場合の例外処理
                catch (SocketTimeoutException ex) {
                    Log.w(DEBUG_TAG, "通信タイムアウト", ex);
                } catch (IOException ex) {
                    Log.e(DEBUG_TAG, "通信失敗", ex);
                } finally {
                    //HTTPURL_Connectionオブジェクトがnullでないなら解放
                    if (con != null) {
                        con.disconnect();
                    }
                    //InputStreamオブジェクトがnullでないなら解放
                    if (is != null) {
                        try {
                            is.close();
                        } catch (IOException ex) {
                            Log.e(DEBUG_TAG, "InputStream解放失敗", ex);
                        }
                    }
                }

                //ここにweb　APIにアクセスするコードを記述
                WeatherInfoPostExecutor postExecutor = new WeatherInfoPostExecutor(result);
                _handler.post(postExecutor);
            }

            private String is2String(InputStream is) throws IOException {
                BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                StringBuffer sb = new StringBuffer();
                char[] b = new char[1024];
                int line;
                while (0 <= (line = reader.read(b))) {
                    sb.append(b, 0, line);
                }

                return sb.toString();


            }

        }

        //非同期でお天気情報を取得した後にUIスレッドでその情報を表示するためのクラス
        private class WeatherInfoPostExecutor implements Runnable {
            //取得したお天気情報JSON文字列
            private final String _result;
            private static final String DEBUG_TAG = "AsynSample";

            //コンストラクタ
            public WeatherInfoPostExecutor(String result) {
                _result = result;
            }

            @UiThread
            @Override
            public void run() {
                //ここにUIスレッドで行う処理コードを記述
                //都市名
                String cityName = "";
                //天気
                String weather = "";
                //緯度
                String latitude = "";
                //経度
                String longitude = "";
                try {
                    //ルートJSONオブジェクトを生成
                    JSONObject rootJSON = new JSONObject(_result);
                    //都市名文字列を取得
                    cityName = rootJSON.getString("name");
                    //緯度経度情報JSONオブジェクトを取得
                    JSONObject coordJSON = rootJSON.getJSONObject("coord");
                    //緯度情報文字列を取得
                    latitude = coordJSON.getString("lat");
                    //経度情報文字列を取得
                    longitude = coordJSON.getString("lon");
                    //天気情報JSON配列オブジェクトを取得
                    JSONArray weatherJSONArray = rootJSON.getJSONArray("weather");
                    //現在の天気情報JSONオブジェクトを取得
                    JSONObject weatherJSON = weatherJSONArray.getJSONObject(0);
                    //現在の天気の文字列を取得
                    weather = weatherJSON.getString("description");
                } catch (JSONException ex) {
                    Log.e(DEBUG_TAG, "JSON解析失敗", ex);
                }

                //画面に表示する「⚪︎の天気」文字列を生成
                String telop = editText.getText().toString() + "の天気";
                //天気の情報を表示する文字列を生成
                String desc = "現在は" + weather + "です。\n緯度は" + latitude + "度で経度は" +
                        longitude + "です。";

                Log.d("上條テロ",telop);
                Log.d("上條デス",desc);


                //ここで次の画面にいく
                // TODO: -画面遷移はこうやる
                Intent intent = new Intent(MainActivity.this, ResultActivity.class);
                intent.putExtra("TEROP", telop);
                intent.putExtra("DESC",desc);
                startActivity(intent);

            }

        }
    }




}