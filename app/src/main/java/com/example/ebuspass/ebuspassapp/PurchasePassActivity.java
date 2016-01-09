package com.example.ebuspass.ebuspassapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.ebuspass.ebuspassapp.WebRequest;

import com.braintreepayments.api.models.PaymentMethodNonce;

import com.braintreepayments.api.*;
import com.braintreepayments.cardform.*;
import com.loopj.android.http.AsyncHttpResponseHandler;

import java.io.UnsupportedEncodingException;

import cz.msebera.android.httpclient.Header;


public class PurchasePassActivity extends AppCompatActivity implements View.OnClickListener {

    static final int PER_RIDE_ADULT_COST = 27;
    static final int PER_RIDE_YOUTH_COST = 22;

    static final int MONTHLY_YOUTH_COST = 60;
    static final int MONTHLY_POST_SECONDARY_COST = 72;
    static final int MONTHLY_ADULT_COST = 84;

    Button CheckoutButton;
    RadioGroup PassTypeRadioGroup, MonthlyPassRadioGroup, PerRideRadioGroup;
    RadioButton PerRideRadioButton, MonthlyRadioButton, MonthlyAdultButton, MonthlyYouthButton, MonthlyPostSecondaryButton,
                PerRideAdultButton, PerRideYouthButton;
    EditText PassQuantity;
    String clientToken;
    TextView ErrorText;

    @Override
    protected void onCreate(Bundle savedInstanceSate){
        super.onCreate(savedInstanceSate);
        setContentView(R.layout.purchase_pass);

        WebRequest.getToken(new AsyncHttpResponseHandler() {
            String clientToken = "";

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                try {
                    clientToken = new String(responseBody, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                    this.clientToken = null;
                }
                Log.v("Client Token", clientToken);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                this.clientToken = null;
                Log.v("Client Token", clientToken);
            }
        });

        CheckoutButton = (Button) findViewById(R.id.ButtonPurchase);

        PassTypeRadioGroup = (RadioGroup) findViewById(R.id.PassTypeRadioGroup);
        MonthlyPassRadioGroup = (RadioGroup) findViewById(R.id.MonthlyPassRadioGroup);
        PerRideRadioGroup = (RadioGroup) findViewById(R.id.PerRideRadioGroup);

        PerRideRadioButton = (RadioButton) findViewById(R.id.PerRideRadioButton);
        PerRideYouthButton = (RadioButton) findViewById(R.id.PerRideYouth);
        PerRideAdultButton = (RadioButton) findViewById(R.id.PerRideAdult);

        MonthlyRadioButton = (RadioButton) findViewById(R.id.MonthlyRadioButton);
        MonthlyAdultButton = (RadioButton) findViewById(R.id.MonthlyAdult);
        MonthlyYouthButton = (RadioButton) findViewById(R.id.MonthlyYouth);
        MonthlyPostSecondaryButton = (RadioButton) findViewById(R.id.MonthlyPostSecondary);

        PassQuantity = (EditText) findViewById(R.id.PassQuantity);

        ErrorText = (TextView) findViewById(R.id.ErrorText);

        CheckoutButton.setOnClickListener(this);
        MonthlyRadioButton.setOnClickListener(this);
        PerRideRadioButton.setOnClickListener(this);

        MonthlyPassRadioGroup.setVisibility(View.GONE);
        PerRideRadioGroup.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ButtonPurchase:

                String amount, passType, quantity;

                quantity = PassQuantity.getText().toString();

                if(PassQuantity.getText().toString().length() == 0) {
                    displayError("You must enter a valid quantity");
                    return;
                }

                if(MonthlyRadioButton.isChecked()) {
                    if (MonthlyAdultButton.isChecked()) {
                        passType = "Monthly - Adult";
                        amount = Integer.toString(Integer.parseInt(quantity) * MONTHLY_ADULT_COST);
                    } else if(MonthlyPostSecondaryButton.isChecked()) {
                        passType = "Monthly - Post Secondary";
                        amount = Integer.toString(Integer.parseInt(quantity) * MONTHLY_POST_SECONDARY_COST);
                    } else if(MonthlyYouthButton.isChecked()) {
                        passType = "Monthly - Youth";
                        amount = Integer.toString(Integer.parseInt(quantity) * MONTHLY_YOUTH_COST);
                    } else {
                        displayError("You must select a pass type");
                        return;
                    }
                } else if(PerRideRadioButton.isChecked()) {
                    if (PerRideAdultButton.isChecked()) {
                        passType = "10 Rides - Adult";
                        amount = Integer.toString(Integer.parseInt(quantity) * PER_RIDE_ADULT_COST);
                    } else if(PerRideYouthButton.isChecked()) {
                        passType = "10 Rides - Youth";
                        amount = Integer.toString(Integer.parseInt(quantity) * PER_RIDE_YOUTH_COST);
                    } else {
                        displayError("You must select a pass type");
                        return;
                    }
                } else {
                    displayError("You must select a pass type");
                    return;
                }

                amount += ".00";

                onBraintreeSubmit(v, amount, passType, quantity);
                break;
            case R.id.MonthlyRadioButton:
                MonthlyPassRadioGroup.setVisibility(View.VISIBLE);
                PerRideRadioGroup.setVisibility(View.GONE);
                break;
            case R.id.PerRideRadioButton:
                MonthlyPassRadioGroup.setVisibility(View.GONE);
                PerRideRadioGroup.setVisibility(View.VISIBLE);
                break;
        }
    }

    public void onBraintreeSubmit(View v, String amount, String passType, String quantity) {
        PaymentRequest paymentRequest = new PaymentRequest()
                .clientToken("eyJ2ZXJzaW9uIjoyLCJhdXRob3JpemF0aW9uRmluZ2VycHJpbnQiOiJiNjQ3ODMxNzZjMzEyNzdlZTFiMWE2Yjg1MmU3NjNkNTE4M2QxOWI1MjVkOTJjNDMzNzQ3ZDExY2ZkODM1ZmJhfGNyZWF0ZWRfYXQ9MjAxNi0wMS0wOFQwMDozNjoyNy4zMzM1NjgxMjUrMDAwMFx1MDAyNm1lcmNoYW50X2lkPTM0OHBrOWNnZjNiZ3l3MmJcdTAwMjZwdWJsaWNfa2V5PTJuMjQ3ZHY4OWJxOXZtcHIiLCJjb25maWdVcmwiOiJodHRwczovL2FwaS5zYW5kYm94LmJyYWludHJlZWdhdGV3YXkuY29tOjQ0My9tZXJjaGFudHMvMzQ4cGs5Y2dmM2JneXcyYi9jbGllbnRfYXBpL3YxL2NvbmZpZ3VyYXRpb24iLCJjaGFsbGVuZ2VzIjpbXSwiZW52aXJvbm1lbnQiOiJzYW5kYm94IiwiY2xpZW50QXBpVXJsIjoiaHR0cHM6Ly9hcGkuc2FuZGJveC5icmFpbnRyZWVnYXRld2F5LmNvbTo0NDMvbWVyY2hhbnRzLzM0OHBrOWNnZjNiZ3l3MmIvY2xpZW50X2FwaSIsImFzc2V0c1VybCI6Imh0dHBzOi8vYXNzZXRzLmJyYWludHJlZWdhdGV3YXkuY29tIiwiYXV0aFVybCI6Imh0dHBzOi8vYXV0aC52ZW5tby5zYW5kYm94LmJyYWludHJlZWdhdGV3YXkuY29tIiwiYW5hbHl0aWNzIjp7InVybCI6Imh0dHBzOi8vY2xpZW50LWFuYWx5dGljcy5zYW5kYm94LmJyYWludHJlZWdhdGV3YXkuY29tIn0sInRocmVlRFNlY3VyZUVuYWJsZWQiOnRydWUsInRocmVlRFNlY3VyZSI6eyJsb29rdXBVcmwiOiJodHRwczovL2FwaS5zYW5kYm94LmJyYWludHJlZWdhdGV3YXkuY29tOjQ0My9tZXJjaGFudHMvMzQ4cGs5Y2dmM2JneXcyYi90aHJlZV9kX3NlY3VyZS9sb29rdXAifSwicGF5cGFsRW5hYmxlZCI6dHJ1ZSwicGF5cGFsIjp7ImRpc3BsYXlOYW1lIjoiQWNtZSBXaWRnZXRzLCBMdGQuIChTYW5kYm94KSIsImNsaWVudElkIjpudWxsLCJwcml2YWN5VXJsIjoiaHR0cDovL2V4YW1wbGUuY29tL3BwIiwidXNlckFncmVlbWVudFVybCI6Imh0dHA6Ly9leGFtcGxlLmNvbS90b3MiLCJiYXNlVXJsIjoiaHR0cHM6Ly9hc3NldHMuYnJhaW50cmVlZ2F0ZXdheS5jb20iLCJhc3NldHNVcmwiOiJodHRwczovL2NoZWNrb3V0LnBheXBhbC5jb20iLCJkaXJlY3RCYXNlVXJsIjpudWxsLCJhbGxvd0h0dHAiOnRydWUsImVudmlyb25tZW50Tm9OZXR3b3JrIjp0cnVlLCJlbnZpcm9ubWVudCI6Im9mZmxpbmUiLCJ1bnZldHRlZE1lcmNoYW50IjpmYWxzZSwiYnJhaW50cmVlQ2xpZW50SWQiOiJtYXN0ZXJjbGllbnQzIiwiYmlsbGluZ0FncmVlbWVudHNFbmFibGVkIjp0cnVlLCJtZXJjaGFudEFjY291bnRJZCI6ImFjbWV3aWRnZXRzbHRkc2FuZGJveCIsImN1cnJlbmN5SXNvQ29kZSI6IlVTRCJ9LCJjb2luYmFzZUVuYWJsZWQiOmZhbHNlLCJtZXJjaGFudElkIjoiMzQ4cGs5Y2dmM2JneXcyYiIsInZlbm1vIjoib2ZmIn0=")
                .primaryDescription(passType)
                .secondaryDescription(quantity)
                .amount(amount);
        startActivityForResult(paymentRequest.getIntent(this), 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.v("onActivity", "Entering onActivityResult");
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                PaymentMethodNonce paymentMethodNonce = data.getParcelableExtra(
                        BraintreePaymentActivity.EXTRA_PAYMENT_METHOD_NONCE
                );
                String nonce = paymentMethodNonce.getNonce();
                Log.v("Nonce", nonce);
                // Send the nonce to your server.
            }
        }
    }

    protected void displayError(String error) {
        ErrorText.setText(error);
    }
}
