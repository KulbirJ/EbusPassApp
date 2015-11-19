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

    Button ButtonLogout;
    EditText FirstName, UserName, LastName, Email;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        FirstName = (EditText) findViewById(R.id.First_Name);
        LastName = (EditText)findViewById((R.id.Last_Name));
        Email = (EditText)findViewById(R.id.Email);
        UserName = (EditText)findViewById(R.id.Username);

        ButtonLogout = (Button) findViewById(R.id.ButtonLogout);
        ButtonLogout.setOnClickListener(this);

    }

    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.ButtonLogout:
                startActivity(new Intent(this, LoginActivity.class));
                break;
        }
    }
}
