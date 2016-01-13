package com.example.ebuspass.ebuspassapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.example.ebuspass.ebuspassapp.helper.SQLiteHandler;
import com.example.ebuspass.ebuspassapp.helper.SessionManager;

/**
 * Created by Kulbir on 2015-12-20.
 */
public class LoginMain extends ActionBarActivity {
    Button purchase;
    private SQLiteHandler db;
    private SessionManager session;
    @Override
    protected  void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_main);
       purchase = (Button)findViewById(R.id.button3);
        purchase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginMain.this, PurchasePassActivity.class));

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
