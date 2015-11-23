package com.example.ebuspass.ebuspassapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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

        AdultRadioButton.setOnClickListener(this);
        PostSecondaryRadioButton.setOnClickListener(this);
        YouthRadioButton.setOnClickListener(this);

        MonthlyPassRadioGroup.setVisibility(View.GONE);
    }


    public void updateCost(double cost) {
        if(PassQuantity.getText().toString().trim().length() == 0) {
            Cost.setText("0.00");
            return;
        }

        int quantity = Integer.parseInt(PassQuantity.getText().toString());
        Cost.setText(String.format("%.2f", quantity * cost));

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ButtonPurchase:
                Log.v("Cost", Cost.getText().toString());
                break;
            case R.id.MonthlyRadioButton:
                MonthlyPassRadioGroup.setVisibility(View.VISIBLE);
                updateCost(0);
                break;
            case R.id.PerRideRadioButton:
                MonthlyPassRadioGroup.setVisibility(View.GONE);
                updateCost(24.50);
                break;
            case R.id.AdultRadioButton:
                updateCost(75.00);
                break;
            case R.id.PostSecondaryRadioButton:
                updateCost(65.00);
                break;
            case R.id.YouthRadioButton:
                updateCost(55);
                break;
        }
    }

}
