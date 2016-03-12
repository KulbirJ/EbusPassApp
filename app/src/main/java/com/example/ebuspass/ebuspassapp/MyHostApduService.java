package com.example.ebuspass.ebuspassapp;

import android.nfc.cardemulation.HostApduService;
import android.os.Bundle;
import android.util.Log;

import com.example.ebuspass.ebuspassapp.helper.SQLiteHandler;
import com.example.ebuspass.ebuspassapp.helper.ValidatePass;

import java.util.HashMap;
import java.util.concurrent.ExecutionException;

public class MyHostApduService extends HostApduService {

	@Override
	public byte[] processCommandApdu(byte[] apdu, Bundle extras) {

		SQLiteHandler sqlHandler = new SQLiteHandler(getApplicationContext());
		HashMap<String, String> userInfo = sqlHandler.getUserDetails();
		HashMap<String, String> passInfo = sqlHandler.getPassDetails(userInfo.get("username"));

		if (selectAidApdu(apdu)) {

			String monthly = passInfo.get("monthlyPass");
			String rides = passInfo.get("rides");
			String key = passInfo.get("key");

			if(monthly == null || monthly.equalsIgnoreCase("None")) {
				monthly = "1990/01/01";
			}

			if(rides == null) {
				rides = "0";
			}

			String response = key + monthly + rides;
			Log.d("Returning", response);
			return response.getBytes();
		}
		else {
			String message = new String(apdu).substring(0, apdu.length - 1);
			if(message.equalsIgnoreCase("ReduceRides")) {
				Log.d("NFC", "Matches. Reduce");
				sqlHandler.increaseRidesTaken(passInfo.get("username"));
			}
			Log.i("NFC", "Received: " + message);
			return null;
		}
	}

	private boolean selectAidApdu(byte[] apdu) {
		return apdu.length >= 2 && apdu[0] == (byte)0 && apdu[1] == (byte)0xa4;
	}

	@Override
	public void onDeactivated(int reason) {
		Log.i("NFC", "Deactivated: " + reason);
	}
}