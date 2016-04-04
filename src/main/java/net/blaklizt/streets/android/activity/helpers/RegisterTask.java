package net.blaklizt.streets.android.activity.helpers;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;
import net.blaklizt.streets.android.activity.MenuLayout;
import net.blaklizt.streets.android.activity.Register;
import net.blaklizt.streets.android.common.enumeration.RegistrationServiceRequest;
import org.json.JSONObject;

import static net.blaklizt.streets.android.activity.AppContext.getStreetsCommon;
import static net.blaklizt.streets.android.common.StreetsCommon.getTag;
import static net.blaklizt.streets.android.common.StreetsCommon.showToast;
import static net.blaklizt.symbiosis.sym_core_lib.enumeration.SYM_RESPONSE_CODE.SUCCESS;

/**
 * Created by tsungai.kaviya on 2015-11-21.
 */
public class RegisterTask extends AsyncTask<Void, Void, Void> {
	private static final String TAG = getTag(RegisterTask.class);
    private Register register;
    ProgressDialog progressDialog;

    public static RegistrationServiceRequest registrationServiceRequest = new RegistrationServiceRequest();

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

		String registerResponse = "{response_code:0, response_message:\"success\", symbiosis_user_id:1}";

        if (registerResponse == null) {
            showToast(register, TAG, "Registration Failed. Check Internet Connection.", Toast.LENGTH_SHORT);
            return null;
        }

        try {
            JSONObject responseJSON = new JSONObject(registerResponse);

            if (responseJSON.getInt("response_code") == SUCCESS.code) {
				Long symbiosisUserID = responseJSON.getLong("symbiosis_user_id");
				getStreetsCommon().setUserID(symbiosisUserID);
                showToast(register, TAG, "Registration successful", Toast.LENGTH_SHORT);

                Intent mainActivity = new Intent(register, MenuLayout.class);
                register.startActivity(mainActivity);
            }
            else if (responseJSON.getInt("response_code") < 0) {
                showToast(register, TAG, "Registration Failed. An unknown error occurred on the server.", Toast.LENGTH_LONG);
            }
            else {
                final String registerResponseStr = responseJSON.getString("response_message");
                register.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showToast(register, TAG, registerResponseStr, Toast.LENGTH_SHORT);
                    }
                });
            }
        } catch (Exception e) {
            Log.e(TAG, "Registration failed: " + e.getMessage(), e);
            e.printStackTrace();
            showToast(register, TAG, "Registration Failed. An unknown error occurred on the server.", Toast.LENGTH_SHORT);
        }
        return null;
    }


    @Override
    protected void onPreExecute() {
        progressDialog = ProgressDialog.show(register, "Registering", "Registering...", true, false);
        progressDialog.show();
    }

    @Override
    protected void onPostExecute(Void result) {
        progressDialog.hide();
    }
}
