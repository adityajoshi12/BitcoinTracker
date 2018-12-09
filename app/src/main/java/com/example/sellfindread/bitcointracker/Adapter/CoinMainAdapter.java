package com.example.sellfindread.bitcointracker.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.sellfindread.bitcointracker.CoinItem.CoinItemView;
import com.example.sellfindread.bitcointracker.Interface.ILoadMore;
import com.example.sellfindread.bitcointracker.Interface.ItemClickListener;
import com.example.sellfindread.bitcointracker.Model.CoinModel;
import com.example.sellfindread.bitcointracker.R;

import java.util.List;

public class CoinMainAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    ILoadMore iLoadMore;
    boolean isLoading;
    Activity activity;
    List<CoinModel> coinList;

    int visibleThreshold=5, lastVisibleItem, totalItemCount;

    public CoinMainAdapter(RecyclerView recyclerView, Activity activity, List<CoinModel> coinList) {
        this.activity = activity;
        this.coinList = coinList;

        final LinearLayoutManager linearLayoutManager=(LinearLayoutManager)recyclerView.getLayoutManager();
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                totalItemCount=linearLayoutManager.getItemCount();
                lastVisibleItem=linearLayoutManager.findLastVisibleItemPosition();

                if(!isLoading && totalItemCount <= (lastVisibleItem+visibleThreshold)){
                    if(iLoadMore!=null){
                        iLoadMore.onLoadMore();
                        isLoading=true;
                    }
                }
            }
        });
    }

    public void setiLoadMore(ILoadMore iLoadMore) {
        this.iLoadMore = iLoadMore;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view=LayoutInflater.from(activity).inflate(R.layout.coin_card_main, viewGroup, false);
        return new CoinMainViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        CoinModel coinModel=coinList.get(i);

        final CoinMainViewHolder mainViewHolder=(CoinMainViewHolder)viewHolder;

        SharedPreferences preferences=activity.getSharedPreferences("Exchange Rate",Context.MODE_PRIVATE);
        float rate=preferences.getFloat("Rate",1);
        float inrPrice=(Float.valueOf(coinModel.getPrice_usd()))*rate;

        mainViewHolder.coinTitle.setText(coinModel.getName());
        mainViewHolder.coinPrice.setText(coinModel.getPrice_usd());
        mainViewHolder.coinPriceINR.setText(String.valueOf(inrPrice));

        ((CoinMainViewHolder) viewHolder).setItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position, boolean isLongClick) {
                if(!isLongClick){
                    int selectedCoinId=position;
                    SharedPreferences sharedPreferences=activity.getSharedPreferences("Selected Coin",Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor=sharedPreferences.edit();
                    editor.putInt("Coin", selectedCoinId);
                    editor.commit();

                    Intent intent=new Intent(activity,CoinItemView.class);
                    activity.startActivity(intent);
                }

            }
        });

    }

    @Override
    public int getItemCount() {
        return coinList.size();
    }

    public void setLoaded(){
        isLoading=false;
    }

    public void updateData(List<CoinModel> updatedList){
        this.coinList=updatedList;
        notifyDataSetChanged();
    }

}
