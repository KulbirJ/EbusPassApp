package com.example.ebuspass.ebuspassapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class LoginActivity extends ActionBarActivity implements View.OnClickListener {

    Button ButtonLogin;
    EditText UserName, Password;
    TextView RegisterLink;

    @Override
    protected void onCreate(Bundle savedInstanceSate){
        super.onCreate(savedInstanceSate);
        setContentView(R.layout.activity_login);
        UserName = (EditText) findViewById(R.id.Username);
        Password = (EditText) findViewById(R.id.Password);
        ButtonLogin = (Button)findViewById((R.id.ButtonLogin));
        RegisterLink = (TextView)findViewById(R.id.RegisterLink);

        ButtonLogin.setOnClickListener(this);

        RegisterLink.setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ButtonLogin:

                break;
            case R.id.RegisterLink:
                startActivity(new Intent(this, RegisterActivity.class));
                break;
        }


    }

}
