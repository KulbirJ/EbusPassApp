package com.example.ebuspass.ebuspassapp;

import android.nfc.cardemulation.HostApduService;
import android.os.Bundle;
import android.util.Log;

import com.example.ebuspass.ebuspassapp.helper.SQLiteHandler;
import com.example.ebuspass.ebuspassapp.helper.ValidatePass;

import java.util.HashMap;
import java.util.concurrent.ExecutionException;

public class MyHostApduService extends HostApduService {

	private int messageCounter = 0;

	@Override
	public byte[] processCommandApdu(byte[] apdu, Bundle extras) {
		if (selectAidApdu(apdu)) {

			SQLiteHandler sqlHandler = new SQLiteHandler(this.getApplicationContext());
			HashMap<String, String> userInfo = sqlHandler.getUserDetails();

			String[] params = new String[2];

			params[0] = userInfo.get("email");
			params[1] = userInfo.get("date_joined");

			try {
				return (new ValidatePass().execute(params).get()).getBytes();
			} catch (InterruptedException e) {
				e.printStackTrace();
				return "falseError communicating with server".getBytes();
			} catch (ExecutionException e) {
				e.printStackTrace();
				return "falseError communication with server".getBytes();
			}
		}
		else {
			Log.i("HCEDEMO", "Received: " + new String(apdu));
			return getNextMessage();
		}
	}

	private byte[] getNextMessage() {
		return ("Message from android: " + messageCounter++).getBytes();
	}

	private boolean selectAidApdu(byte[] apdu) {
		return apdu.length >= 2 && apdu[0] == (byte)0 && apdu[1] == (byte)0xa4;
	}

	@Override
	public void onDeactivated(int reason) {
		Log.i("HCEDEMO", "Deactivated: " + reason);
	}
}