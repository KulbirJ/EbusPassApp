package com.example.ebuspass.ebuspassapp.helper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import java.util.HashMap;

import cz.msebera.android.httpclient.Header;

/**
   Sends Broadcast receiver to check network connectivity
 */
public class SyncPass extends BroadcastReceiver {

    private SQLiteHandler sqlHandler = null;

    public void onReceive(Context context, Intent intent) {
        SessionManager session = new SessionManager(context);
        if(!session.isLoggedIn()) {
            return;
        }

        Log.d("app", "Network connectivity change");

        sqlHandler = new SQLiteHandler(context.getApplicationContext());
        WebRequest.checkForOutdatedPass(sqlHandler, context);
    }
}
