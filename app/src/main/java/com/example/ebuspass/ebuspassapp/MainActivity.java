package com.example.ebuspass.ebuspassapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Button ButtonLogin;
    EditText FirstName, UserName, LastName, Email;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
/*
        FirstName = (EditText) findViewById(R.id.First_Name);
        LastName = (EditText)findViewById((R.id.Last_Name));
        Email = (EditText)findViewById(R.id.Email);
        UserName = (EditText)findViewById(R.id.Username);
*/
        ButtonLogin = (Button) findViewById(R.id.ButtonLogin);
        ButtonLogin.setOnClickListener(this);

    }
   /* @Override
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
            startActivity(new Intent(this, Profile.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

*/
    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.ButtonLogin:
                startActivity(new Intent(this, LoginMain.class));
                break;
        }
    }
}
