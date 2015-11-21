package net.blaklizt.streets.android.activity.helpers;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import net.blaklizt.streets.android.activity.AppContext;
import net.blaklizt.streets.android.activity.MenuLayout;
import net.blaklizt.streets.android.activity.Register;

import org.json.JSONObject;

import static net.blaklizt.streets.android.common.StreetsCommon.getTag;
import static net.blaklizt.streets.android.common.StreetsCommon.showToast;

/**
 * Created by tsungai.kaviya on 2015-11-21.
 */
public class RegisterTask extends AsyncTask<Void, Void, Void> {
	private static final String TAG = getTag(RegisterTask.class);
    private Register register;
    ProgressDialog progressDialog;

    public RegisterTask(Register register) {
        this.register = register;
    }

    @Override
    protected Void doInBackground(Void... param) {

        Log.i(TAG, "Registering...");

//        String registerResponse = ServerCommunication.sendServerRequest(
//				"action=Register&channel=" + AppContext.CHANNEL +
//						"&firstName=" + register.firstName.getText().toString() +
//						"&lastName=" + register.lastName.getText().toString() +
//						"&username=" + register.username.getText().toString() +
//						"&msisdn=" + register.msisdn.getText().toString() +
//						"&email=" + register.email.getText().toString() +
//						"&password=" + register.password.getText().toString() +
//						"&imei=" + AppContext.getStreetsCommon().getIMEI() +
//						"&imsi=" + AppContext.getStreetsCommon().getIMSI());

		String registerResponse = "{response_code:1, response_message:\"success\", symbiosis_user_id:1}";

        if (registerResponse == null) {
            showToast(TAG, "Registration Failed. Check Internet Connection.", Toast.LENGTH_SHORT);
            return null;
        }

        try {
            JSONObject responseJSON = new JSONObject(registerResponse);

            if (responseJSON.getInt("response_code") == 1)//ResponseCode.SUCCESS.getValue())
            {
				Long symbiosisUserID = responseJSON.getLong("symbiosis_user_id");
				AppContext.getStreetsCommon().setUserID(symbiosisUserID);
				Log.i(TAG, "Registration successful");
                register.runOnUiThread(() -> showToast(TAG, "Registration successful", Toast.LENGTH_SHORT));
                Intent mainActivity = new Intent(Register.getInstance(), MenuLayout.class);
                register.startActivity(mainActivity);
            } else if (responseJSON.getInt("response_code") < 0) {
                Log.i(TAG, "Registration failed with internal error: " + responseJSON.getString("response_message"));
                register.runOnUiThread(() -> showToast(TAG, "Registration Failed. An unknown error occurred on the server.", Toast.LENGTH_SHORT));
            } else {
                final String registerResponseStr = responseJSON.getString("response_message");
                Log.i(TAG, "Registration failed: " + responseJSON.getString("response_message"));
                register.runOnUiThread(() -> showToast(TAG, registerResponseStr, Toast.LENGTH_SHORT));
            }
        } catch (Exception e) {
            Log.e(TAG, "Registration failed: " + e.getMessage(), e);
            e.printStackTrace();
            register.runOnUiThread(() -> showToast(TAG, "Registration Failed. An unknown error occurred on the server.", Toast.LENGTH_SHORT));
        }
        return null;
    }


    @Override
    protected void onPreExecute() {
        progressDialog = ProgressDialog.show(Register.getInstance(), "Registering", "Registering...", true, false);
        progressDialog.show();
    }

    @Override
    protected void onProgressUpdate(Void... progress) {
	}

    @Override
    protected void onPostExecute(Void result) {
        progressDialog.hide();
    }
}
