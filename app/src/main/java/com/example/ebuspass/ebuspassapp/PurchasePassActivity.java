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
import com.example.ebuspass.ebuspassapp.helper.SQLiteHandler;
import com.example.ebuspass.ebuspassapp.helper.WebRequest;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;

import cz.msebera.android.httpclient.Header;


public class PurchasePassActivity extends AppCompatActivity implements View.OnClickListener {

    static final int PER_RIDE_ADULT_COST = 27;
    static final int PER_RIDE_YOUTH_COST = 22;

    static final int MONTHLY_YOUTH_COST = 60;
    static final int MONTHLY_POST_SECONDARY_COST = 72;
    static final int MONTHLY_ADULT_COST = 84;

    Button CheckoutButton;
    RadioGroup PassTypeRadioGroup, MonthlyPassRadioGroup, PerRideRadioGroup;
    RadioButton PerRideRadioButton, MonthlyRadioButton, MonthlyAdultButton, MonthlyYouthButton,
                MonthlyPostSecondaryButton, PerRideAdultButton, PerRideYouthButton;
    EditText PassQuantity;
    TextView ErrorText;

    String amount;
    String quantity;
    String passType;
    String token;
    String email;
    String dateJoined;

    @Override
    protected void onCreate(Bundle savedInstanceSate){
        super.onCreate(savedInstanceSate);
        setContentView(R.layout.purchase_pass);

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

        SQLiteHandler sqlHandler = new SQLiteHandler(this.getApplicationContext());
        HashMap<String, String> userInfo = sqlHandler.getUserDetails();

        setEmail(userInfo.get("email"));
        setDateJoined(userInfo.get("date_joined"));

        //Obtain token from Django server
        WebRequest.getToken(new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                try {
                    setToken(new String(responseBody, "UTF-8"));
                    Log.d("Client Token", getToken());
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                    setToken(null);
                    Log.d("Client Token", getToken());
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                setToken(null);
                Log.d("Client Token", getToken());
            }
        });
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
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                PaymentMethodNonce paymentMethodNonce = data.getParcelableExtra(
                        BraintreePaymentActivity.EXTRA_PAYMENT_METHOD_NONCE
                );

                displayMessage("Processing purchase...");

                String nonce = paymentMethodNonce.getNonce();
                RequestParams params = new RequestParams();
                params.put("nonce", nonce);
                params.put("pass_type", getPassType());
                params.put("amount", getAmount());
                params.put("quantity", getQuantity());
                params.put("email", getEmail());
                params.put("date_joined",getDateJoined());

                        WebRequest.sendNonce(params, new AsyncHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                                String response = "";

                                try {
                                    response = new String(responseBody, "UTF-8");
                                    Log.d("sendNonceReturn",response);
                                } catch (UnsupportedEncodingException e) {
                                    e.printStackTrace();
                                    displayError("An error occurred during transmission");
                                    return;
                                }

                                if (response.equalsIgnoreCase("Success")) {
                                    Intent intent = new Intent(PurchasePassActivity.this, LoginMain.class);
                                    startActivity(intent);
                                    finish();
                                } else if (response.equalsIgnoreCase("Purchase Failed")) {
                                    displayError("The transaction could not be completed.");
                                } else {
                                    displayError("An error occurred during transmission. Please try again.");
                                }

                            }

                            @Override
                            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                                displayError("Transaction could not be completed. Please try again.");
                            }
                        });
            }
        }
    }

    protected void displayError(String error) {
        ErrorText.setText(error);
    }

    protected void displayMessage(String message) {
        ErrorText.setText(message);
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

    public String getDateJoined() {
        return dateJoined;
    }

    public void setDateJoined(String date_joined) {
        this.dateJoined = date_joined;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

}
