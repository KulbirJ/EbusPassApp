package com.example.ebuspass.ebuspassapp;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class RegisterActivity extends ActionBarActivity implements View.OnClickListener {
    Button ButtonSubmit;
    EditText FirstName, UserName, LastName, Email , Password;

    @Override
    protected  void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);
        FirstName = (EditText) findViewById(R.id.First_Name);
        LastName = (EditText)findViewById((R.id.Last_Name));
        Email = (EditText)findViewById(R.id.Email);
        UserName = (EditText)findViewById(R.id.Username);
        Password =(EditText)findViewById(R.id.Password);
        ButtonSubmit = (Button) findViewById(R.id.ButtonSubmit);

        ButtonSubmit.setOnClickListener(this);
    }
    @Override
    public void onClick(View v)
    {
        switch(v.getId()){
            case R.id.ButtonSubmit:
                break;
        }
    }

}
