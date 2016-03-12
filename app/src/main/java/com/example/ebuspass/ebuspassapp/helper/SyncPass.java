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
 * Created by SimonHanna on 16-03-12.
 */
public class SyncPass extends BroadcastReceiver {

    private SQLiteHandler sqlHandler = null;

    public void onReceive(Context context, Intent intent) {
        Log.d("app", "Network connectivity change");

        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

        if(activeNetworkInfo == null || !activeNetworkInfo.isConnected()) {
            return;
        }

        sqlHandler = new SQLiteHandler(context.getApplicationContext());
        HashMap<String, String> userInfo = sqlHandler.getUserDetails();
        HashMap<String, String> passInfo = sqlHandler.getPassDetails(userInfo.get("username"));

        if(!passInfo.get("ridesTaken").equalsIgnoreCase("0")) {
            AsyncHttpResponseHandler asyncHandler = new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    Log.d("checkForOutdatedPass", "Success");
                    String response = "";
                    JSONObject jObj = null;

                    try {
                        response = new String(responseBody, "UTF-8");
                        jObj = new JSONObject(response);

                        Boolean error = !jObj.isNull("error");

                        if (!error) {
                            getSqlHandler().addPass(jObj.getString("monthly"), jObj.getString("rides"),
                                    jObj.getString("key"), jObj.getString("username"));
                            getSqlHandler().getPassDetails(jObj.getString("username"));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    Log.d("checkForOutdatedPass", "Failure");
                }
            };

            RequestParams params = new RequestParams();
            params.add("rides_taken", passInfo.get("ridesTaken"));
            params.add("email", userInfo.get("email"));
            params.add("date_joined", userInfo.get("date_joined"));

            WebRequest.syncPass(params, asyncHandler);
        }
    }

    public SQLiteHandler getSqlHandler() {
        return sqlHandler;
    }
}
