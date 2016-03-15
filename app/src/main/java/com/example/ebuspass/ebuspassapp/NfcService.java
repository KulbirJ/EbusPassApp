package com.example.ebuspass.ebuspassapp;

import android.nfc.cardemulation.HostApduService;
import android.os.Bundle;
import android.util.Log;

import com.example.ebuspass.ebuspassapp.helper.SQLiteHandler;
import com.example.ebuspass.ebuspassapp.helper.WebRequest;

import java.util.HashMap;

public class NfcService extends HostApduService {

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

			String response= key + monthly + rides;
            Log.d("NFC Response", response);

            response = encrypt(response);
            Log.d("NFC Encrypted", response);

			return response.getBytes();
		}
		else {
			String message = new String(apdu).substring(0, apdu.length - 1);
			if(message.equalsIgnoreCase("ReduceRides")) {
				Log.d("NFC", "Matches. Reduce");
				sqlHandler.increaseRidesTaken(passInfo.get("username"));
				WebRequest.checkForOutdatedPass(sqlHandler, this.getApplicationContext());
			}
			Log.i("NFC", "Received: " + message);
			return null;
		}
	}
    public String encrypt(String Text)
    {
        long m = 1;
        int n = 14351;
        int e = 11;
        char[] plainText=Text.toCharArray();
        String reply = "";


        for(int i = 0; i < plainText.length; i++) {
            for(int j = 0; j < e; j++) {
                m = (m * plainText[i]) % n;
            }

            String x = Long.toHexString(m & 0x00ff);
            String y = Long.toHexString((m & 0xff00) >> 8);

            if(x.length() == 1) {
                x = "0" + x;
            }

            if(y.length() == 1) {
                y = "0" + y;
            }

            reply += x + y;

            m = 1;
        }

        return reply;
    }



    private boolean selectAidApdu(byte[] apdu) {
		return apdu.length >= 2 && apdu[0] == (byte)0 && apdu[1] == (byte)0xa4;
	}

	@Override
	public void onDeactivated(int reason) {
		Log.i("NFC", "Deactivated: " + reason);
	}
}