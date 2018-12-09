package com.example.sellfindread.bitcointracker.CoinItem;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sellfindread.bitcointracker.MainActivity;
import com.example.sellfindread.bitcointracker.Model.CoinModel;
import com.example.sellfindread.bitcointracker.R;
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

public class CoinItemView extends AppCompatActivity {
    public ImageView imageView;
    public TextView itemTitle,itemRank,itemPriceUSD,itemPriceINR,itemChange1h,itemChange24h;

    public SwipeRefreshLayout refreshLayout;

    public CoinModel itemModel;

    public int position;

    public List<CoinModel> itemList=new ArrayList<>();

    public boolean isConnected;

    OkHttpClient client;
    Request request;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.coin_item);

        imageView=(ImageView)findViewById(R.id.coinItemImage);

        itemTitle=(TextView)findViewById(R.id.coinItemTitle);
        itemRank=(TextView)findViewById(R.id.coinItemRank);
        itemPriceUSD=(TextView)findViewById(R.id.coinItemPriceUSD);
        itemPriceINR=(TextView)findViewById(R.id.coinItemPriceINR);
        itemChange1h=(TextView)findViewById(R.id.coinItemChange1h);
        itemChange24h=(TextView)findViewById(R.id.coinItemChange24h);

        refreshLayout=(SwipeRefreshLayout)findViewById(R.id.coinItemRoot);

        refreshLayout.post(new Runnable() {
            @Override
            public void run() {
                loadCoinItemData();
            }
        });

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                isConnected=connectionResult();
                if(isConnected) {
                    itemList.clear();
                    loadCoinItemData();
                }else{
                    Snackbar snackbar=Snackbar.make(refreshLayout,"No Internet Connection", Snackbar.LENGTH_LONG);
                    snackbar.show();
                    refreshLayout.setRefreshing(false);
                }
            }
        });

    }

    private void setUpVIew(List<CoinModel> coinList) {
        isConnected=connectionResult();

        if(isConnected) {
            SharedPreferences sharedPreferences = getSharedPreferences("Selected Coin", Context.MODE_PRIVATE);
            position = sharedPreferences.getInt("Coin", 1);

            SharedPreferences preferences = getSharedPreferences("Exchange Rate", Context.MODE_PRIVATE);
            float rate = preferences.getFloat("Rate", 1);
            Log.e("Test", String.valueOf(rate));

            itemModel = coinList.get(position);

            float inrPrice = (Float.valueOf(itemModel.getPrice_usd())) * rate;
            String title = itemModel.getName() + "(" + itemModel.getSymbol() + ")";

            itemTitle.setText(title);
            itemRank.setText(itemModel.getRank());
            itemPriceUSD.setText(itemModel.getPrice_usd());
            itemPriceINR.setText(String.valueOf(inrPrice));
            itemChange1h.setText(itemModel.getPercent_change_1h() + "%");
            itemChange24h.setText(itemModel.getPercent_change_24h() + "%");

            itemChange1h.setTextColor(itemModel.getPercent_change_1h().contains("-") ?
                    Color.parseColor("#FF0000") : Color.parseColor("#32CD32"));

            itemChange24h.setTextColor(itemModel.getPercent_change_24h().contains("-") ?
                    Color.parseColor("#FF0000") : Color.parseColor("#32CD32"));

        }else{
            Snackbar snackbar=Snackbar.make(refreshLayout,"No Internet Connection", Snackbar.LENGTH_LONG);
            snackbar.show();
        }

    }

    private void loadCoinItemData() {
        client=new OkHttpClient();
        request=new Request.Builder().url(String.format("https://api.coinmarketcap.com/v1/ticker/?start=%d&limit=100",0)).build();
        refreshLayout.setRefreshing(true);
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
                        setUpVIew(newList);
                    }
                });

            }
        });

        if(refreshLayout.isRefreshing()) refreshLayout.setRefreshing(false);
    }

    public boolean connectionResult(){
        ConnectivityManager connectivityManager=(ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo=connectivityManager.getActiveNetworkInfo();

        if(networkInfo!=null && networkInfo.isConnected()){
            return true;
        }else {
            return false;
        }
    }

    public CoinItemView() {
    }
}
