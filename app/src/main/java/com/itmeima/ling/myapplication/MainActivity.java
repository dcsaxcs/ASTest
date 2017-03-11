package com.itmeima.ling.myapplication;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mListView = (ListView) findViewById(R.id.lv);
      //  ButterKnife.bind(this);
        sendSyncRequest();

    }

    private void sendSyncRequest() {
        new  Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient okHttpClient = new OkHttpClient();
                //创建网络请求
                String url = "http://gank.io/api/data/福利/10/1";
                Request request = new Request.Builder().get().url(url).build();
                try {
                    //网络同步
                    Response response = okHttpClient.newCall(request).execute();
                    //同步请求,就是等到网络请求返回回来之后,你猜能走后面的代码
                    Log.d(TAG, "sendSyncRequest: "+ response.body().string());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }
}
