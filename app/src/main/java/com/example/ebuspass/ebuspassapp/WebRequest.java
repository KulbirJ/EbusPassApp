package com.example.ebuspass.ebuspassapp;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

/**
 * Created by SimonHanna on 16-01-08.
 */
public final class WebRequest {

    private static AsyncHttpClient mClient = new AsyncHttpClient();

    public static void getToken(AsyncHttpResponseHandler handler) {
        mClient.get("http://54.84.253.83/generate_token/", handler);
    }

    public static void sendNonce(AsyncHttpResponseHandler handler) {
        mClient.post("http://54.84.253.83", handler);
    }

}
