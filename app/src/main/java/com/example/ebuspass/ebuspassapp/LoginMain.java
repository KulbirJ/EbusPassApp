package com.example.ebuspass.ebuspassapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.ebuspass.ebuspassapp.helper.SQLiteHandler;
import com.example.ebuspass.ebuspassapp.helper.SessionManager;

import com.example.ebuspass.ebuspassapp.helper.SQLiteHandler;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;

import cz.msebera.android.httpclient.Header;

/**
 * Created by Kulbir on 2015-12-20.
 */
public class LoginMain extends ActionBarActivity {
    Button purchase;
    private SQLiteHandler db;
    private SessionManager session;
    TextView monthlyText, ridesRemainingText;


    private String ridesRemaining, monthlyPass;



    @Override
    protected  void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_main);

        // SqLite database handler
        db = new SQLiteHandler(getApplicationContext());

        // session manager
        session = new SessionManager(getApplicationContext());
        SQLiteHandler sqlHandler = new SQLiteHandler(this.getApplicationContext());
        HashMap<String, String> userInfo = sqlHandler.getUserDetails();
        if (!session.isLoggedIn()) {
            logoutUser();
        }

        monthlyText = (TextView) findViewById(R.id.MonthlyText);
        ridesRemainingText = (TextView) findViewById(R.id.RidesRemainingText);

        purchase = (Button)findViewById(R.id.button3);
        purchase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginMain.this, PurchasePassActivity.class));

            }
        });



        String email = "hanna25s@uregina.ca";
        String dateJoined = "2015-11-06 01:53:25";

        //No pass test info
        //String email = "gufan_chan@yahoo.com";
        //String dateJoined = "2016-01-06 21:46:21";

        RequestParams params = new RequestParams();
        params.put("email", email);
        params.put("date_joined", dateJoined);

        WebRequest.getPassInformation(params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                String response = "";
                JSONObject jObj = null;

                try {
                    response = new String(responseBody, "UTF-8");

                    if(response.equalsIgnoreCase("No Pass")) {
                        Log.d("getPasInformation", "No Pass");
                    } else if(response.equalsIgnoreCase("Invalid User")) {
                        Log.d("getPassInformation", "Invalid User");
                    } else {

                        jObj = new JSONObject(response);
                        String ridesRemaining = jObj.getString("rides");
                        String expiryDate = jObj.getString("monthly");

                        Log.d("getPassInformation", response);
                        Log.d("monthly", expiryDate);
                        Log.d("rides", ridesRemaining);

                        monthlyText.setText("Expires On: " + expiryDate);
                        ridesRemainingText.setText(ridesRemaining + " Rides Remaining");

                    }

                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                Log.d("getPassInformation","Failure");
            }
        });


    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;



    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.profile) {
            startActivity(new Intent(this, MainActivity.class));
            return true;
        }
        else if (id == R.id.purchase_history) {
            startActivity(new Intent(this, PurchasePassActivity.class));
            return true;
        }
        else if (id == R.id.logout)
        {
            logoutUser();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void logoutUser() {
        session.setLogin(false);

        db.deleteUsers();

        // Launching the login activity
        Intent intent = new Intent(LoginMain.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

}
