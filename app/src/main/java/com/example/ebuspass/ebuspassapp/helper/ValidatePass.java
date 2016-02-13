package com.example.ebuspass.ebuspassapp.helper;

import android.os.AsyncTask;
import android.util.Log;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

/**
 * Created by SimonHanna on 16-02-09.
 */
public class ValidatePass extends AsyncTask<String, Void, String> {

    private String message;
    private String isValid;
    private String passType;

    @Override
    protected String doInBackground(String... requestParams) {

        RequestParams params = new RequestParams();

        params.put("email", requestParams[0]);
        params.put("date_joined", requestParams[1]);

        WebRequest.processBusRide(params, new JsonHttpResponseHandler() {

            @Override
            public void onStart() {
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    setIsValid(response.getString("isValid"));
                    setMessage(response.getString("message"));
                    if(getIsValid().equalsIgnoreCase("true")) {
                        setPassType(response.getString("passType"));
                    }
                } catch (JSONException e) {
                    setIsValid("0");
                    setMessage("Invalid Response");
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                setIsValid("0");
                setMessage("Unable to contact server");
            }

        });

        if(getIsValid().equalsIgnoreCase("true")) {
            return getHeaderInfo() + getMessage();
        } else if (getMessage() == null) {
            return "0" + "Communication Error";
        } else {
            return "0" + getMessage();
        }
    }

    public String getHeaderInfo() {
        if (getPassType().equalsIgnoreCase("Monthly")) {
            return "10";
        } else if (getPassType().equalsIgnoreCase("PerRide")) {
            return "11";
        } else {
            return "0";
        }
    }

    public String getIsValid() {
            return isValid;
    }

    public void setIsValid(String isValid) {
            this.isValid = isValid;
    }

    public String getMessage() {
            return message;
    }

    public void setMessage(String message) {
            this.message = message;
    }

    public String getPassType() {
        return passType;
    }

    public void setPassType(String passType) {
        this.passType = passType;
    }
}
