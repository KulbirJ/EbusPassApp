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

import com.braintreepayments.api.models.PaymentMethodNonce;

import com.braintreepayments.api.*;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

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
    TextView ErrorText;

    String amount;
    String quantity;
    String passType;
    String token;

    @Override
    protected void onCreate(Bundle savedInstanceSate){
        super.onCreate(savedInstanceSate);
        setContentView(R.layout.purchase_pass);

        WebRequest.getToken(new AsyncHttpResponseHandler() {
            String clientToken = "";

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                try {
                    setToken(new String(responseBody, "UTF-8"));
                    Log.d("getToken", getToken());
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                    setToken(null);
                    Log.d("getToken", getToken());
                }
                Log.v("Client Token", clientToken);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                setToken(null);
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

                setQuantity(PassQuantity.getText().toString());

                if(PassQuantity.getText().toString().length() == 0) {
                    displayError("You must enter a valid quantity");
                    return;
                }

                if(MonthlyRadioButton.isChecked()) {
                    if (MonthlyAdultButton.isChecked()) {
                        setPassType("Monthly - Adult");
                        setAmount(Integer.toString(Integer.parseInt(quantity) * MONTHLY_ADULT_COST));
                    } else if(MonthlyPostSecondaryButton.isChecked()) {
                        setPassType("Monthly - Post Secondary");
                        setAmount(Integer.toString(Integer.parseInt(quantity) * MONTHLY_POST_SECONDARY_COST));
                    } else if(MonthlyYouthButton.isChecked()) {
                        setPassType("Monthly - Youth");
                        setAmount(Integer.toString(Integer.parseInt(quantity) * MONTHLY_YOUTH_COST));
                    } else {
                        displayError("You must select a pass type");
                        return;
                    }
                } else if(PerRideRadioButton.isChecked()) {
                    if (PerRideAdultButton.isChecked()) {
                        setPassType("10 Rides - Adult");
                        setAmount(Integer.toString(Integer.parseInt(quantity) * PER_RIDE_ADULT_COST));
                    } else if(PerRideYouthButton.isChecked()) {
                        setPassType("10 Rides - Youth");
                        setAmount(Integer.toString(Integer.parseInt(quantity) * PER_RIDE_YOUTH_COST));
                    } else {
                        displayError("You must select a pass type");
                        return;
                    }
                } else {
                    displayError("You must select a pass type");
                    return;
                }

                setAmount(getAmount() + ".00");

                onBraintreeSubmit(v);
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

    public void onBraintreeSubmit(View v) {
        PaymentRequest paymentRequest = new PaymentRequest()
                .clientToken(getToken())
                .primaryDescription(getPassType())
                .secondaryDescription(getQuantity())
                .amount(getAmount());
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
                RequestParams params = new RequestParams();
                params.put("nonce", nonce);
                params.put("pass_type", getPassType());
                params.put("amount", getAmount());
                params.put("quantity", getQuantity());

                Log.d("onActivityResult", "Pass Type: " + getPassType());
                Log.d("onActivityResult","RequestParam set up, sending Nonce to server");

                WebRequest.sendNonce(params, new AsyncHttpResponseHandler() {

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        try {
                            Log.d("sendNonce", new String(responseBody, "UTF-8"));
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                            Log.d("sendNonce", "UnsupportedEncodingExeption");
                        }

                        displayError("Successfully sent nonce");

                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                        // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                        Log.d("sendNonce", "Failure");
                        displayError("Transaction could not be completed. Please try again.");
                    }
                });
                // Send the nonce to your server.
                Log.d("onActivityResult", "Returned from sending Nonce");
            }
        }
    }

    protected void displayError(String error) {
        ErrorText.setText(error);
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getPassType() {
        return passType;
    }

    public void setPassType(String passType) {
        this.passType = passType;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

}
