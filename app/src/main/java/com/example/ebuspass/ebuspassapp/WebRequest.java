package com.example.ebuspass.ebuspassapp;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/**
 * Created by SimonHanna on 16-01-08.
 */
public final class WebRequest {

    static String websiteUrl = "http://54.84.253.83";

    private static AsyncHttpClient mClient = new AsyncHttpClient();

    public static void getToken(AsyncHttpResponseHandler handler) {
        mClient.get(websiteUrl + "/generate_token/", handler);
    }

    public static void sendNonce(RequestParams params, AsyncHttpResponseHandler handler) {
        mClient.post(websiteUrl + "/process_nonce/", params, handler);
    }

    public static void getPassInformation(RequestParams params, AsyncHttpResponseHandler handler) {
        mClient.post(websiteUrl + "/get_pass_information/", params, handler);
    }

    public static void processBusRide(RequestParams params, AsyncHttpResponseHandler handler) {
        mClient.post(websiteUrl + "/ride_bus/", params, handler);
    }
}