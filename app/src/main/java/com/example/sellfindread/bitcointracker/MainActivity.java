package com.example.sellfindread.bitcointracker;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.example.sellfindread.bitcointracker.Adapter.CoinMainAdapter;
import com.example.sellfindread.bitcointracker.Adapter.CustomItemDecorator;
import com.example.sellfindread.bitcointracker.Interface.ILoadMore;
import com.example.sellfindread.bitcointracker.Model.CoinModel;
import com.example.sellfindread.bitcointracker.Model.ExchangeModel;
import com.example.sellfindread.bitcointracker.Model.Rates;
import com.example.sellfindread.bitcointracker.Model.RatesRoot;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    List<CoinModel> itemList=new ArrayList<>();
    CoinMainAdapter adapter;
    RecyclerView recyclerView;

    OkHttpClient client;
    OkHttpClient exchangeClient;
    Request request;

    SwipeRefreshLayout swipeRefreshLayout;

    float exchangeRate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        swipeRefreshLayout=(SwipeRefreshLayout)findViewById(R.id.mainSwipeLayout);
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                loadExchangeRate();
                loadCoinData();
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                itemList.clear();
                loadExchangeRate();
                loadCoinData();
                setupAdapter();
            }
        });

        recyclerView=(RecyclerView)findViewById(R.id.coinMainRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new CustomItemDecorator(this, DividerItemDecoration.VERTICAL, 36));
        setupAdapter();
    }

    private void setupAdapter() {
        adapter=new CoinMainAdapter(recyclerView, MainActivity.this, itemList);
        recyclerView.setAdapter(adapter);
        adapter.setiLoadMore(new ILoadMore() {
            @Override
            public void onLoadMore() {
                if(itemList.size()<=1000){
                    loadExchangeRate();
                    loadCoinData();
                }else{
                    Toast.makeText(getApplicationContext(), "Max Size Reached", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

//    private void loadNext10Coin(int size) {
//        client=new OkHttpClient();
//        request=new Request.Builder().url(String.format("https://api.coinmarketcap.com/v1/ticker/?start=%d&limit=10",size)).build();
//        swipeRefreshLayout.setRefreshing(true);
//        client.newCall(request).enqueue(new Callback() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
//                String body = response.body().toString();
//                Gson gson = new Gson();
//                String err = gson.fromJson(body, new TypeToken<String>() {
//                }.getType());
//                Log.e("Error", err);
////                final List<CoinModel> newList=gson.fromJson(body, new TypeToken<List<CoinModel>>(){}.getType());
////
////                runOnUiThread(new Runnable() {
////                    @Override
////                    public void run() {
////                        itemList.addAll(newList);
////                        adapter.setLoaded();
////                        adapter.updateData(itemList);
////                        swipeRefreshLayout.setRefreshing(false);
////                    }
////                });
//
//            }
//        });
//    }

//    private void loadFirst10Coin(int i) {
//        client=new OkHttpClient();
//        request=new Request.Builder().url(String.format("https://api.coinmarketcap.com/v1/ticker/?start=%d&limit=10",i)).build();
//        swipeRefreshLayout.setRefreshing(true);
//        client.newCall(request).enqueue(new Callback() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
//                String body=response.body().string();
//                Gson gson=new Gson();
//                final List<CoinModel> newList=gson.fromJson(body, new TypeToken<List<CoinModel>>(){}.getType());
//
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        adapter.updateData(newList);
//                    }
//                });
//
//            }
//        });
//
//        if(swipeRefreshLayout.isRefreshing()) swipeRefreshLayout.setRefreshing(false);
//
//    }

    public void loadCoinData(){
        client=new OkHttpClient();
        request=new Request.Builder().url(String.format("https://api.coinmarketcap.com/v1/ticker/?start=%d&limit=100",0)).build();
        swipeRefreshLayout.setRefreshing(true);
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String body=response.body().string();
                Gson gson=new Gson();
                final List<CoinModel> newList=gson.fromJson(body, new TypeToken<List<CoinModel>>(){}.getType());

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.updateData(newList);
                    }
                });

            }
        });

        if(swipeRefreshLayout.isRefreshing()) swipeRefreshLayout.setRefreshing(false);
    }

    public void loadExchangeRate(){
        do {
            exchangeClient = new OkHttpClient();
            Request exchangeRequest = new Request.Builder().url("https://free.currencyconverterapi.com/api/v6/convert?q=USD_INR&compact=ultra").build();
            exchangeClient.newCall(exchangeRequest).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
//                Toast.makeText(getApplicationContext(),e.getMessage(), Toast.LENGTH_SHORT).show();
                    exchangeRate = 71;
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String body = response.body().string();
                    Gson gson = new Gson();
//                RatesRoot ratesRoot=gson.fromJson(body, new TypeToken<RatesRoot>(){}.getType());
                    ExchangeModel exchangeModel = gson.fromJson(body, new TypeToken<ExchangeModel>() {
                    }.getType());


                    double currUSD = exchangeModel.getUSD_INR();
                    exchangeRate = (float) currUSD;
                }
            });
        }while (exchangeRate==0);

        Log.e("Exchange",String.valueOf(exchangeRate));
        SharedPreferences sharedPreferences=getApplicationContext().getSharedPreferences("Exchange Rate", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putFloat("Rate",exchangeRate);
        editor.commit();
    }

}
