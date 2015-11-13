package net.blaklizt.streets.android.activity.helpers;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.widget.Toast;

import net.blaklizt.streets.android.activity.AppContext;
import net.blaklizt.streets.android.activity.MenuLayout;
import net.blaklizt.streets.android.activity.Startup;
import net.blaklizt.streets.android.common.StreetsCommon;
import net.blaklizt.streets.android.common.TASK_TYPE;
import net.blaklizt.streets.android.common.USER_PREFERENCE;

import org.json.JSONObject;

import java.util.ArrayList;

import static net.blaklizt.streets.android.common.StreetsCommon.showSnackBar;
import static net.blaklizt.streets.android.common.StreetsCommon.showToast;

/******************************************************************************
 * *
 * Created:     03 / 11 / 2015                                             *
 * Platform:    Red Hat Linux 9                                            *
 * Author:      Tich de Blak (Tsungai Kaviya)                              *
 * Copyright:   Blaklizt Entertainment                                     *
 * Website:     http://www.blaklizt.net                                    *
 * Contact:     blaklizt@gmail.com                                         *
 * *
 * This program is free software; you can redistribute it and/or modify    *
 * it under the terms of the GNU General Public License as published by    *
 * the Free Software Foundation; either version 2 of the License, or       *
 * (at your option) any later version.                                     *
 * *
 * This program is distributed in the hope that it will be useful,         *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of          *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.    See the         *
 * GNU General Public License for more details.                            *
 * *
 ******************************************************************************/
public class LoginTask extends StreetsAbstractTask {

    static {
        processDependencies = new ArrayList<>();
        viewDependencies = new ArrayList<>();
        allowOnlyOnce = false;
        allowMultiInstance = false;
        taskType = TASK_TYPE.FG_LOGIN_TASK;
    }

    private final String TAG = StreetsCommon.getTag(LoginTask.class);
    private int counter = 5;
    private ProgressDialog progressDialog;
    private Startup startup;

    public LoginTask(Startup startup) {
        this.startup = startup;
    }

    @Override
    protected Void doInBackground(Object[] params) {
        Log.i(TAG, "Authenticating...");

//		ServerCommunication.sendServerRequest(
//          "action=Login&channel=" + StreetsCommon.CHANNEL +
//          "&username=" + username.getText().toString() +
//          "&edtPassword=" + edtPassword.getText().toString());

        String loginResponse = "{response_code:1, response_message:\"success\", symbiosis_user_id:1}";

        if (loginResponse == null) {
            showToast(TAG, "Login Failed. Check internet connection and try again.", Toast.LENGTH_LONG);
            return null;
        }

        try {
            JSONObject responseJSON = new JSONObject(loginResponse);

            if (responseJSON.getInt("response_code") == 1)//ResponseCode.SUCCESS.getValue())
            {
                Long symbiosisUserID = responseJSON.getLong("symbiosis_user_id");
                AppContext.getStreetsCommon().setUserID(symbiosisUserID);

                Log.i(TAG, "Login successful");
                showSnackBar(TAG, "Login successful", Snackbar.LENGTH_SHORT);

                Intent mainActivity = new Intent(startup, MenuLayout.class);
                startup.startActivity(mainActivity);
            } else if (responseJSON.getInt("response_code") < 0) {
                Log.i(TAG, "Login failed with internal error: " + responseJSON.getString("response_message"));
                showToast(TAG, "Login Failed. An unknown error occurred on the server.", Toast.LENGTH_SHORT);
            } else {
                final String loginResponseStr = responseJSON.getString("response_message");
                Log.i(TAG, "Login failed: " + responseJSON.getString("response_message"));
                AppContext.getStreetsCommon().setUserPreference(USER_PREFERENCE.AUTO_LOGIN, "0"); //disable auto login to prevent running out of attempts
                showToast(TAG, loginResponseStr, Toast.LENGTH_SHORT);

                if (--counter <= 0) {
                    startup.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            startup.edtPassword.setEnabled(false);
                            startup.btnLogin.setEnabled(false);
                            showToast(TAG, "Maximum login attempts. Please contact support", Toast.LENGTH_LONG);
                        }
                    });
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Login failed: " + e.getMessage(), e);
            e.printStackTrace();
            showToast(TAG, "Login Failed. An unknown error occurred on the server.", Toast.LENGTH_SHORT);
        }
        return null;
    }

    @Override
    protected void onPreExecute() {
        progressDialog = ProgressDialog.show(Startup.getInstance(), "Authenticating", "Authenticating...", true, false);
        progressDialog.show();
    }

    @Override
    protected void onProgressUpdate(Object... progress) {
    }

    @Override
    public void onPostExecuteRelay(Object result) {
        progressDialog.hide();
    }
}
