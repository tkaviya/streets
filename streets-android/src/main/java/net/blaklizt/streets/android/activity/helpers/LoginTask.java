package net.blaklizt.streets.android.activity.helpers;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.widget.Toast;
import net.blaklizt.streets.android.activity.MenuLayout;
import net.blaklizt.streets.android.activity.Startup;
import net.blaklizt.streets.android.common.StreetsCommon;
import org.json.JSONObject;

import java.util.ArrayList;

import static net.blaklizt.streets.android.activity.AppContext.getStreetsCommon;
import static net.blaklizt.streets.android.common.StreetsCommon.showSnackBar;
import static net.blaklizt.streets.android.common.StreetsCommon.showToast;
import static net.blaklizt.streets.android.common.enumeration.TASK_TYPE.FG_LOGIN_TASK;
import static net.blaklizt.streets.android.common.enumeration.USER_PREFERENCE.AUTO_LOGIN;
import static net.blaklizt.symbiosis.sym_core_lib.enumeration.SYM_RESPONSE_CODE.SUCCESS;

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

    private final String TAG = StreetsCommon.getTag(LoginTask.class);
    private int counter = 5;
    private ProgressDialog progressDialog;
    private Startup startup;

    public LoginTask(Startup startup) {
        this.startup = startup;
        processDependencies = new ArrayList<>();
        viewDependencies = new ArrayList<>();
        allowOnlyOnce = false;
        allowMultiInstance = false;
        taskType = FG_LOGIN_TASK;
    }

    @Override
    protected Void doInBackground(Object[] params) {
        Log.i(TAG, "Authenticating...");

//		ServerCommunication.sendServerRequest(
//          "action=Login&channel=" + StreetsCommon.CHANNEL +
//          "&username=" + username.getText().toString() +
//          "&edtPassword=" + edtPassword.getText().toString());

        String loginResponse = "{response_code:0, response_message:\"success\", symbiosis_user_id:1}";

        if (loginResponse == null) {
            showToast(startup, TAG, "Login Failed. Check internet connection and try again.", Toast.LENGTH_LONG);
            return null;
        }

        try {
            JSONObject responseJSON = new JSONObject(loginResponse);

            if (responseJSON.getInt("response_code") == SUCCESS.code) {
                Long symbiosisUserID = responseJSON.getLong("symbiosis_user_id");
                getStreetsCommon().setUserID(symbiosisUserID);
                showSnackBar(startup, TAG, "Login successful", Snackbar.LENGTH_SHORT);

                Intent mainActivity = new Intent(startup, MenuLayout.class);
                startup.startActivity(mainActivity);
            }
            else if (responseJSON.getInt("response_code") < 0) {
                showToast(startup, TAG, "Login Failed. An unknown error occurred on the server.", Toast.LENGTH_SHORT);
            }
            else {
                final String loginResponseStr = responseJSON.getString("response_message");
                getStreetsCommon().setUserPreference(AUTO_LOGIN, "0"); //disable auto login to prevent running out of attempts
                showToast(startup, TAG, loginResponseStr, Toast.LENGTH_SHORT);

                if (--counter <= 0) {
                    startup.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            startup.edtPassword.setEnabled(false);
                            startup.btnLogin.setEnabled(false);
                            showToast(startup, TAG, "Maximum login attempts. Please contact support", Toast.LENGTH_LONG);
                        }
                    });
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Login failed: " + e.getMessage(), e);
            e.printStackTrace();
            showToast(startup, TAG, "Login Failed. An unknown error occurred on the server.", Toast.LENGTH_SHORT);
        }
        return null;
    }

    @Override
    protected void onPreExecuteRelay(Object[] additionalParams) {
        progressDialog = ProgressDialog.show(startup, "Authenticating", "Authenticating...", true, true);
        progressDialog.show();
    }

    @Override
    public void onPostExecuteRelay(Object result) {
        progressDialog.hide();
    }
}
