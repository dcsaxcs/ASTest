package com.itmeima.ling.myapplication;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private ListView mListView;

    private boolean isLoading = false;

    //数据集合
    private List<GirlBean.ResultsBean> mListData = new ArrayList<>();

    private Gson gb = new Gson();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mListView = (ListView) findViewById(R.id.lv);
        mListView.setAdapter(mBaseAdapter);
        mListView.setOnScrollListener( mOnScrollListener);
      //  ButterKnife.bind(this);
      //  sendSyncRequest();

        sendAsyncRequest();


    }
    private AbsListView.OnScrollListener mOnScrollListener = new AbsListView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
                //当listView是一个idie,判断是否滑动到底部
            if (scrollState ==SCROLL_STATE_IDLE){
                //还有判断是否正在加载数据
                if (mListView.getLastVisiblePosition()==mListData.size()-1 && !isLoading){
                    //滑动到底部
                    //加载更多数据
                    loadMoreData();
                }
            }
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

        }
    };

    private void loadMoreData() {
        isLoading = true;
        OkHttpClient okHttpClient = new OkHttpClient();
        //创建网络请求
        String url = "http://gank.io/api/data/福利/10/"+mListData.size()/10+1;
        Request request = new Request.Builder().get().url(url).build();
        //异步请求,不需要等待网络返回,及执行后面的代码
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }
            //在子线程被调用
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                GirlBean girlBean = gb.fromJson(result, GirlBean.class);
                //  Log.d(TAG, "onResponse: "+girlBean.getResults().get(0).getUrl());
                //将网络结果加入数据集合
                mListData.addAll(girlBean.getResults());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //通知adapter刷新列表
                        mBaseAdapter.notifyDataSetChanged();
                    }
                });
                isLoading = false;

            }
        });
        Log.d(TAG, "sendAsyncRequest: ");

    }

    private void sendAsyncRequest() {


                OkHttpClient okHttpClient = new OkHttpClient();
                //创建网络请求
                String url = "http://gank.io/api/data/福利/10/1";
                Request request = new Request.Builder().get().url(url).build();
                //异步请求,不需要等待网络返回,及执行后面的代码
                okHttpClient.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {

                    }
                    //在子线程被调用
                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String result = response.body().string();
                        GirlBean girlBean = gb.fromJson(result, GirlBean.class);
                      //  Log.d(TAG, "onResponse: "+girlBean.getResults().get(0).getUrl());
                       //将网络结果加入数据集合
                        mListData.addAll(girlBean.getResults());
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //通知adapter刷新列表
                                mBaseAdapter.notifyDataSetChanged();
                            }
                        });

                    }
                });
        Log.d(TAG, "sendAsyncRequest: ");

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
    private BaseAdapter mBaseAdapter = new BaseAdapter() {
        @Override
        public int getCount() {
            return mListData.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
           if(convertView ==null){
               convertView = View.inflate(MainActivity.this,R.layout.view_list_item,null);
               viewHolder = new ViewHolder(convertView);
               convertView.setTag(viewHolder);
           }else{
               viewHolder = (ViewHolder) convertView.getTag();
           }
            //绑定视图
            GirlBean.ResultsBean girBean = mListData.get(position);//拿到对应位置的数据
           //更新发布时间
            viewHolder.mTextView.setText(girBean.getPublishedAt());
            //刷新图片
            String url = girBean.getUrl();
            Glide.with(MainActivity.this).load(url).centerCrop().into(viewHolder.mImageView);
            return convertView;
        }
    };
    public  class  ViewHolder{
        ImageView mImageView;
        TextView mTextView;

        public ViewHolder(View v){
            mImageView = (ImageView) v.findViewById(R.id.image);
            mTextView  = (TextView) v.findViewById(R.id.publish_time);
        }
    }
}
