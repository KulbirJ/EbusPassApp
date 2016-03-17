package com.example.ebuspass.ebuspassapp.helper;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import java.util.HashMap;

import cz.msebera.android.httpclient.Header;

/**
 * Created by SimonHanna on 16-01-08.
 */
public final class WebRequest {

    static String websiteUrl = "https://www.ebuspass.com";

    private static AsyncHttpClient asyncClient = new AsyncHttpClient();

    public static void getToken(AsyncHttpResponseHandler handler) {
        asyncClient.get(websiteUrl + "/generate_token/", handler);
    }

    public static void sendNonce(RequestParams params, AsyncHttpResponseHandler handler) {
        asyncClient.post(websiteUrl + "/process_nonce/", params, handler);
    }

    public static void getPassInformation(RequestParams params, AsyncHttpResponseHandler handler) {
        asyncClient.post(websiteUrl + "/get_pass_information/", params, handler);
    }

    public static void syncPass(RequestParams params, AsyncHttpResponseHandler handler) {
        asyncClient.post(websiteUrl + "/sync_pass/", params, handler);
    }

    public static void checkForOutdatedPass(final SQLiteHandler sqlHandler, final Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

        if(activeNetworkInfo == null || !activeNetworkInfo.isConnected()) {
            Intent intent = new Intent("com.ebuspass.updatepass");
            context.sendBroadcast(intent);
            return;
        }

        final HashMap<String, String> userInfo = sqlHandler.getUserDetails();
        HashMap<String, String> passInfo = sqlHandler.getPassDetails(userInfo.get("username"));
        if(!passInfo.get("ridesTaken").equalsIgnoreCase("0")) {
            AsyncHttpResponseHandler asyncHandler = new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    String response = "";
                    JSONObject jObj = null;

                    try {
                        response = new String(responseBody, "UTF-8");
                        jObj = new JSONObject(response);

                        Boolean error = !jObj.isNull("error");

                        if (!error) {
                            sqlHandler.addPass(jObj.getString("monthly"), jObj.getString("rides"),
                                    jObj.getString("key"), jObj.getString("username"));
                            sqlHandler.getPassDetails(jObj.getString("username"));
                            Intent intent = new Intent("com.ebuspass.updatepass");
                            sqlHandler.resetRidesTaken(userInfo.get("username"));
                            context.sendBroadcast(intent);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    Intent intent = new Intent("com.ebuspass.updatepass");
                    context.sendBroadcast(intent);
                }
            };

            RequestParams params = new RequestParams();
            params.add("rides_taken", passInfo.get("ridesTaken"));
            params.add("email",userInfo.get("email"));
            params.add("date_joined", userInfo.get("date_joined"));

            syncPass(params, asyncHandler);
        }
    }
}