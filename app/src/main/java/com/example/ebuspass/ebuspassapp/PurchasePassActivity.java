package com.example.ebuspass.ebuspassapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;


public class PurchasePassActivity extends AppCompatActivity implements View.OnClickListener {

    Button ButtonPurchase;
    RadioGroup PassTypeRadioGroup, MonthlyPassRadioGroup;
    RadioButton PerRideRadioButton, MonthlyRadioButton, AdultRadioButton,
                YouthRadioButton, PostSecondaryRadioButton;
    EditText PassQuantity;
    TextView Cost, CostLabel;

    @Override
    protected void onCreate(Bundle savedInstanceSate){
        super.onCreate(savedInstanceSate);
        setContentView(R.layout.purchase_pass);

        ButtonPurchase = (Button) findViewById(R.id.ButtonPurchase);

        PassTypeRadioGroup = (RadioGroup) findViewById(R.id.PassTypeRadioGroup);
        MonthlyPassRadioGroup = (RadioGroup) findViewById(R.id.MonthlyPassRadioGroup);

        PerRideRadioButton = (RadioButton) findViewById(R.id.PerRideRadioButton);
        MonthlyRadioButton = (RadioButton) findViewById(R.id.MonthlyRadioButton);
        AdultRadioButton = (RadioButton) findViewById(R.id.AdultRadioButton);
        YouthRadioButton = (RadioButton) findViewById(R.id.YouthRadioButton);
        PostSecondaryRadioButton = (RadioButton) findViewById(R.id.PostSecondaryRadioButton);

        PassQuantity = (EditText) findViewById(R.id.PassQuantity);

        Cost = (TextView) findViewById(R.id.Cost);
        CostLabel = (TextView) findViewById(R.id.CostLabel);

        ButtonPurchase.setOnClickListener(this);
        MonthlyRadioButton.setOnClickListener(this);
        PerRideRadioButton.setOnClickListener(this);

        MonthlyPassRadioGroup.setVisibility(View.GONE);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ButtonPurchase:

                break;
            case R.id.MonthlyRadioButton:
                MonthlyPassRadioGroup.setVisibility(View.VISIBLE);
                break;
            case R.id.PerRideRadioButton:
                MonthlyPassRadioGroup.setVisibility(View.GONE);
                break;
        }


    }

}
