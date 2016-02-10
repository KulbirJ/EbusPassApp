package com.example.ebuspass.ebuspassapp.helper;

import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.SyncHttpClient;
import com.loopj.android.http.RequestParams;

/**
 * Created by SimonHanna on 16-01-08.
 */
public final class WebRequest {

    static String websiteUrl = "http://www.ebuspass.com";

    private static AsyncHttpClient asyncClient = new AsyncHttpClient();
    private static SyncHttpClient syncClient = new SyncHttpClient();

    public static void getToken(AsyncHttpResponseHandler handler) {
        asyncClient.get(websiteUrl + "/generate_token/", handler);
    }

    public static void sendNonce(RequestParams params, AsyncHttpResponseHandler handler) {
        asyncClient.post(websiteUrl + "/process_nonce/", params, handler);
    }

    public static void getPassInformation(RequestParams params, AsyncHttpResponseHandler handler) {
        asyncClient.post(websiteUrl + "/get_pass_information/", params, handler);
    }

    public static void processBusRide(final RequestParams params, final JsonHttpResponseHandler handler) {

        Log.d("processBusRide", "posting");
        handler.setUseSynchronousMode(true);
        syncClient.post(websiteUrl + "/ride_bus/", params, handler);
    }
}