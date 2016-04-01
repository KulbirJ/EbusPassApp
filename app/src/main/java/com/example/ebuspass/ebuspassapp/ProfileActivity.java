package com.example.ebuspass.ebuspassapp;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.ebuspass.ebuspassapp.helper.SQLiteHandler;
import com.example.ebuspass.ebuspassapp.helper.SessionManager;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by gangluo on 2016-02-20.
 */

public class ProfileActivity extends ActionBarActivity {

    TextView resultView;
    TableLayout resultTable;

    private final int WC = ViewGroup.LayoutParams.WRAP_CONTENT;
    private final int FP = ViewGroup.LayoutParams.FILL_PARENT;
    private SQLiteHandler db;
    private SessionManager session;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);
        StrictMode.enableDefaults();
        resultTable = (TableLayout) findViewById(R.id.profile_table);
        resultView = (TextView) findViewById(R.id.profile_text);
        resultTable.setStretchAllColumns(true);
        db = new SQLiteHandler(getApplicationContext());
        session = new SessionManager(getApplicationContext());
        getData();
    }

    public void getData() {
        SQLiteHandler sqlHandler = new SQLiteHandler(this.getApplicationContext());
        HashMap<String, String> userInfo = sqlHandler.getUserDetails();

        String useremail = userInfo.get("email");
        String userdateJoined = userInfo.get("date_joined");

        String result = "";
        InputStream isr = null;
        ArrayList<BasicNameValuePair> dataToSend = new ArrayList<>();
        dataToSend.add(new BasicNameValuePair("email", useremail));
        dataToSend.add(new BasicNameValuePair("date_joined",userdateJoined));

        try {

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost("https://www.ebuspass.com/hooks/Profile.php");
            httppost.setEntity(new UrlEncodedFormEntity(dataToSend));


            HttpResponse response = httpclient.execute(httppost);
            HttpEntity entity = response.getEntity();

            isr = entity.getContent();

        } catch (Exception e) {
            Log.e("log_tag", "Error in http connection" + e.toString());
            resultView.setText("Could not connect to database");
        }

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(isr, "iso-8859-1"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            reader.close();
            isr.close();
            result = sb.toString();
            System.out.println(result);
        } catch (Exception e) {
            Log.e("log_tag", "Error converting result" + e.toString());
        }

        try {
            String s = "";
            JSONArray jArray = new JSONArray(result);


            for (int i = 0; i < jArray.length(); i++) {
                JSONObject json = jArray.getJSONObject(i);


                TableRow tablerow1 = new TableRow(this);
                tablerow1.setBackgroundColor(Color.rgb(96, 125, 139));
                TextView tx1=new TextView(this);
                s=json.getString("username");
                tx1.setText("Username:   "+s);
                tx1.setTextSize(16);
                tablerow1.addView(tx1);
                resultTable.addView(tablerow1, new TableLayout.LayoutParams(FP, WC));

                TableRow tablerow2 = new TableRow(this);
                tablerow2.setBackgroundColor(Color.rgb(222, 220, 210));
                TextView tx2=new TextView(this);
                s=json.getString("first_name");
                tx2.setText("First name:   "+s);
                tx2.setTextSize(16);
                tablerow2.addView(tx2);
                resultTable.addView(tablerow2, new TableLayout.LayoutParams(FP, WC));

                TableRow tablerow3 = new TableRow(this);
                tablerow3.setBackgroundColor(Color.rgb(96, 125, 139));
                TextView tx3=new TextView(this);
                s=json.getString("last_name");
                tx3.setText("Last name:   "+s);
                tx3.setTextSize(16);
                tablerow3.addView(tx3);
                resultTable.addView(tablerow3, new TableLayout.LayoutParams(FP, WC));

                TableRow tablerow4 = new TableRow(this);
                tablerow4.setBackgroundColor(Color.rgb(222, 220, 210));
                TextView tx4=new TextView(this);
                s=json.getString("email");
                tx4.setText("Email Address:   "+s);
                tx4.setTextSize(16);
                tablerow4.addView(tx4);
                resultTable.addView(tablerow4, new TableLayout.LayoutParams(FP, WC));

                TableRow tablerow5 = new TableRow(this);
                tablerow5.setBackgroundColor(Color.rgb(96, 125, 139));
                TextView tx5=new TextView(this);
                s=json.getString("date_joined");
                tx5.setText("Date of Joined:   "+s);
                tx5.setTextSize(16);
                tablerow5.addView(tx5);
                resultTable.addView(tablerow5, new TableLayout.LayoutParams(FP, WC));

                TableRow tablerow6 = new TableRow(this);
                tablerow6.setBackgroundColor(Color.rgb(222, 220, 210));
                TextView tx6=new TextView(this);
                s=json.getString("last_login");
                tx6.setText("Last login website:   "+s);
                tx6.setTextSize(16);
                tablerow6.addView(tx6);
                resultTable.addView(tablerow6, new TableLayout.LayoutParams(FP, WC));



            }
        } catch (Exception e) {
//to do handle exception
            Log.e("Log_tag", "Error Parsing Data" + e.toString());
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_profile, menu);
        return true;



    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.loginmain) {
            startActivity(new Intent(this, LoginMain.class));
            return true;
        }
        else if (id == R.id.purchase_pass) {
            startActivity(new Intent(this, PurchasePassActivity.class));
            return true;
        }else if (id == R.id.purchase_history) {
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
        Intent intent = new Intent(this, LoginActivity.class);
        intent.putExtra("finish", true); // if you are checking for this in your other Activities
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                Intent.FLAG_ACTIVITY_CLEAR_TASK |
                Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}
