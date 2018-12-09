package com.example.sellfindread.bitcointracker.Adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;

import com.example.sellfindread.bitcointracker.R;

public class LoadingViewHolder extends RecyclerView.ViewHolder {
    public ProgressBar progressBar;

    public LoadingViewHolder(@NonNull View itemView) {
        super(itemView);
        progressBar=(ProgressBar)itemView.findViewById(R.id.loading_layout_pb);
    }
}
