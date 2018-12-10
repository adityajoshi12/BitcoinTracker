package com.example.aditya.bitcointracker.Adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.aditya.bitcointracker.Interface.ItemClickListener;
import com.example.aditya.bitcointracker.R;

public class CoinMainViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public ImageView coinSymbol;
    public TextView coinTitle, coinPrice, coinPriceINR;
    public ItemClickListener itemClickListener;

    public CoinMainViewHolder(@NonNull View itemView) {
        super(itemView);
        coinSymbol=(ImageView)itemView.findViewById(R.id.coin_symbol);
        coinTitle=(TextView)itemView.findViewById(R.id.coin_name);
        coinPrice=(TextView)itemView.findViewById(R.id.coin_price_main);
        coinPriceINR=(TextView)itemView.findViewById(R.id.coin_price_inr);

        itemView.setOnClickListener(this);
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View view) {
        itemClickListener.onClick(view, getAdapterPosition(), false);

    }
}
