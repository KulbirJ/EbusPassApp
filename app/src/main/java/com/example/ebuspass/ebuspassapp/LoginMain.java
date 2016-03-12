package com.example.ebuspass.ebuspassapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.nfc.NfcAdapter;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ebuspass.ebuspassapp.helper.SQLiteHandler;
import com.example.ebuspass.ebuspassapp.helper.SessionManager;

import com.example.ebuspass.ebuspassapp.helper.WebRequest;
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
    final Context context = this;

    private String ridesRemaining, monthlyPass;
    SQLiteHandler sqlHandler ;
    @Override
    protected  void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_main);

        // Network connectivity check
        NfcAdapter nfcAdpt = NfcAdapter.getDefaultAdapter(this);
        if(nfcAdpt!=null) {
            if (!nfcAdpt.isEnabled()) {

                    AlertDialog.Builder alertbox = new AlertDialog.Builder(context);
                    alertbox.setTitle("Info");
                    alertbox.setMessage("NFC Is disabled ");
                    alertbox.setPositiveButton("Turn On", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startNfcSettingsActivity();
                        }
                    });
                    alertbox.setNegativeButton("Close", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    alertbox.show();


            }
        }
        /*
        if(nfcAdpt!=null) {
            if (!nfcAdpt.isEnabled()) {
                //Nfc settings are disabled
                startNfcSettingsActivity();
            }
        }*/
        // SqLite database handler
        db = new SQLiteHandler(getApplicationContext());

        long rows = db.getProfilesCount();
        String srow = Long.toString(rows);
        Log.d(" Rows in data: ", srow+ "");
        // session manager
        session = new SessionManager(getApplicationContext());
        sqlHandler = new SQLiteHandler(this.getApplicationContext());
        final HashMap<String, String> userInfo = sqlHandler.getUserDetails();


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

        String email = userInfo.get("email");
        String dateJoined = userInfo.get("date_joined");

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
                    jObj = new JSONObject(response);

                    Boolean error = !jObj.isNull("error");
                    String key = jObj.getString("key");

                    if (error) {
                        monthlyText.setText("");
                        ridesRemainingText.setText(jObj.getString("error"));
                        db.addPass("01/01/1990", "0", key, userInfo.get("username"));
                    } else {
                        String ridesRemaining = jObj.getString("rides");
                        String expiryDate = jObj.getString("monthly");

                        db.addPass(expiryDate, ridesRemaining, key, userInfo.get("username"));
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

                HashMap<String, String> passInfo = sqlHandler.getPassDetails(userInfo.get("username"));
                String monthly = passInfo.get("monthlyPass");
                String rides = passInfo.get("rides");
                monthlyText.setText("Expires On: " + monthly);
                ridesRemainingText.setText(rides + " Rides Remaining");
                Log.d("getPassInformation", "Offline");
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
            startActivity(new Intent(this, ProfileActivity.class));
            return true;
        }
        else if (id == R.id.purchase_history) {
            startActivity(new Intent(this, PurchaseHistoryActivity.class));
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
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    protected void startNfcSettingsActivity() {
        Toast.makeText(getApplicationContext(), "Please activate NFC and press Back to return to the application!", Toast.LENGTH_LONG).show();
        if (android.os.Build.VERSION.SDK_INT >= 16) {

            startActivity(new Intent(android.provider.Settings.ACTION_NFC_SETTINGS));
        } else {
            startActivity(new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS));
        }
    }


}
