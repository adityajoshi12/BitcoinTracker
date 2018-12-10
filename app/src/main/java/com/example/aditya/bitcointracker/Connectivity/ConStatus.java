package com.example.aditya.bitcointracker.Connectivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import com.example.aditya.bitcointracker.MainActivity;

import static com.example.aditya.bitcointracker.MainActivity.verifyConnection;

public class ConStatus extends BroadcastReceiver{
   MainActivity mainActivity=null;

    public void mainActivityHandler(MainActivity main) {
        mainActivity=main;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        try{
            if(isConnected(context)){
                mainActivity.connectionResult(true);
                Toast.makeText(context, "Connected", Toast.LENGTH_LONG).show();

            }else{
                mainActivity.connectionResult(false);
                Toast.makeText(context, "Disconnected", Toast.LENGTH_LONG).show();
                verifyConnection(false);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private boolean isConnected(Context context){
        try{
            ConnectivityManager manager=(ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo info=manager.getActiveNetworkInfo();
            return (info!=null && info.isConnected());
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }
}
