package com.example.ebuspass.ebuspassapp;

/**
 * Created by gangluo on 2016-01-15.
 */

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.ebuspass.ebuspassapp.helper.SQLiteHandler;

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

public class PurchaseHistoryActivity extends ActionBarActivity {

    TextView resultView;
    TableLayout resultTable;

    private final int WC = ViewGroup.LayoutParams.WRAP_CONTENT;
    private final int FP = ViewGroup.LayoutParams.FILL_PARENT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.purchase_history);
        StrictMode.enableDefaults();
        resultTable = (TableLayout) findViewById(R.id.purchase_history_table);
        resultView = (TextView) findViewById(R.id.purchase_history_text);
        resultTable.setStretchAllColumns(true);
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
        dataToSend.add(new BasicNameValuePair("email",useremail));
        dataToSend.add(new BasicNameValuePair("date_joined",userdateJoined));

        try {

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost("https://www.ebuspass.com/hooks/Transactions.php");
            System.out.println(dataToSend);
            httppost.setEntity(new UrlEncodedFormEntity(dataToSend));


            HttpResponse response = httpclient.execute(httppost);
            HttpEntity entity = response.getEntity();

            isr = entity.getContent();

        } catch (Exception e) {
            Log.e("log_tag", "Error in http connection" + e.toString());
            resultView.setText("Could not connect to database");
        }

//convert response to String

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(isr, "iso-8859-1"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            isr.close();
            result = sb.toString();
            System.out.println(result);
        } catch (Exception e) {
            Log.e("log_tag", "Error converting result" + e.toString());
        }

        try {
            String s = "";
            JSONArray jArray = new JSONArray(result);

            TableRow tablerow1 = new TableRow(this);
            tablerow1.setBackgroundColor(Color.rgb(96,125,139));

            TextView tx1=new TextView(this);
            tx1.setText("DATE & TIME");
            tx1.setTextSize(12);
            tablerow1.addView(tx1);

            TextView tx2=new TextView(this);
            tx2.setText("COST  ");
            tx2.setTextSize(12);
            tablerow1.addView(tx2);

            TextView tx3=new TextView(this);
            tx3.setText("PASS TYPE");
            tx3.setTextSize(12);
            tablerow1.addView(tx3);

            TextView tx4=new TextView(this);
            tx4.setText("QUANTITY");
            tx4.setTextSize(12);
            tablerow1.addView(tx4);

            resultTable.addView(tablerow1, new TableLayout.LayoutParams(FP, WC));

            for (int i = 0; i < jArray.length(); i++) {
                JSONObject json = jArray.getJSONObject(i);

                TableRow tablerow = new TableRow(this);
                tablerow.setBackgroundColor(Color.rgb(222, 220, 210));


                s=json.getString("date");
                TextView tv1=new TextView(this);
                tv1.setText(s);
                tv1.setTextSize(12);
                tablerow.addView(tv1);


                s=json.getString("cost");
                TextView tv2=new TextView(this);
                tv2.setText(" $"+s);
                tv2.setTextSize(12);
                tablerow.addView(tv2);



                s=json.getString("passtype");
                TextView tv3=new TextView(this);
                tv3.setText(s);
                tv3.setTextSize(12);
                tablerow.addView(tv3);

                s=json.getString("quantity");
                TextView tv4=new TextView(this);
                tv4.setText(s);
                tv4.setTextSize(12);
                tablerow.addView(tv4);
                /*
                s = s +
                        "DATE : " + json.getString("date") + "\n" +
                        "COST: " + json.getString("cost") + "\n" +
                        "PASS TYPE : " + json.getString("passtype") + "\n" +
                        "QUANTITY :" + json.getString("quantity") + "\n\n";
                        */


                resultTable.addView(tablerow, new TableLayout.LayoutParams(FP, WC));
            }

            //resultView.setText(s);
            //System.out.println(s);

        } catch (Exception e) {
//to do handle exception
            Log.e("Log_tag", "Error Parsing Data" + e.toString());
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_purchasehist, menu);
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
        }


        return super.onOptionsItemSelected(item);
    }
}
