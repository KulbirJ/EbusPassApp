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


public class PurchasePassActivity extends ActionBarActivity implements View.OnClickListener {

    Button ButtonPurchase;

    @Override
    protected void onCreate(Bundle savedInstanceSate){
        super.onCreate(savedInstanceSate);
        setContentView(R.layout.purchase_pass);

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
