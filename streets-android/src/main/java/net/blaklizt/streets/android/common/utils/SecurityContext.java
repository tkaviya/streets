package net.blaklizt.streets.android.common.utils;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import net.blaklizt.streets.android.activity.Startup;
import net.blaklizt.streets.android.common.StreetsCommon;
import net.blaklizt.streets.android.common.SymbiosisUser;

import java.lang.reflect.Field;
import java.util.HashMap;

/******************************************************************************
 * *
 * Created:     01 / 11 / 2015                                             *
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


public class SecurityContext {

    private static final String TAG = StreetsCommon.getTag(SecurityContext.class);

    private static final HashMap<String, Object> defaultDataSet = new HashMap<>();

    private static SecurityContext securityContext = null;

    private Activity activity;

    static {
        defaultDataSet.put("symbiosisUserID", 0L);
        defaultDataSet.put("username", null);
        defaultDataSet.put("imei", null);
        defaultDataSet.put("imsi", null);
        defaultDataSet.put("password", null);
        defaultDataSet.put("last_location_id", null);
        defaultDataSet.put("home_place_id", null);
        defaultDataSet.put("type", null);
    }

    public SecurityContext(Activity activity) {
        this.activity = activity;
        securityContext = this;
    }

    public static SecurityContext getInstance(Activity activity) {
        if (securityContext == null) {
            securityContext = new SecurityContext(activity);
            Log.i(TAG, "Created new instance of StreetsCommon");
        }
        return securityContext;
    }

    public void securityTermination(String message) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(
                        Startup.getSecurityContext().activity,
                        "Security verification failed!\n\n" + message + "\n\nApplication will terminate.",
                        Toast.LENGTH_LONG).show();
                Startup.getInstance().finish();
                activity.finish();
            }
        });
    }

    public boolean verifyUserDetails(SymbiosisUser symbiosisUser) {

        Log.i(TAG, "Verifying details of current user.");

        if (symbiosisUser == null) {
            Log.e(TAG, "SymbiosisUser cannot be null!");
            return false;
        }

        SymbiosisUser dbUser = Startup.getStreetsCommon().getSymbiosisUser();

        for (Field field : symbiosisUser.getClass().getFields()) {
            String fieldName = field.getName();
            Log.i(TAG, "Verifying data off field " + fieldName);
            try {
                Object fieldValue = SymbiosisUser.class.getField(fieldName).get(symbiosisUser);
                Log.i(TAG, "CurrentUserValue: " + fieldValue);
                Object databaseValue = SymbiosisUser.class.getField(fieldName).get(dbUser);
                Log.i(TAG, "CurrentUserValue: " + databaseValue);

                if (fieldValue == null) {
                    if (databaseValue != null && defaultDataSet.get(fieldName) != null) {
                        securityTermination("Value of " + fieldName + " is invalid");
                        return false;
                    } else {
                        continue;
                    }
                }

                if (!fieldValue.equals(databaseValue)) {
                    if (!defaultDataSet.containsKey(fieldName)) {
                        securityTermination("Value of " + fieldName + " is invalid");
                        return false;
                    } else if (!fieldValue.equals(defaultDataSet.get(fieldName))) {
                        securityTermination("Value of " + fieldName + " is invalid");
                        return false;
                    }
                }
            } catch (Exception ex) {
                securityTermination("Failed to verify value of " + fieldName);
                Log.e(TAG, "Failed to verify value of " + fieldName, ex);
                ex.printStackTrace();
                return false;
            }
        }

        return true;
//        return ((dbUser.symbiosisUserID.equals(defaultDataSet.get("SymbiosisUserID")) || (dbUser.symbiosisUserID.equals(symbiosisUser.symbiosisUserID))) &&
//                ((dbUser.username == defaultDataSet.get("Username")) || (dbUser.username.equals(symbiosisUser.username))) &&
//                ((dbUser.imei  == defaultDataSet.get("Imei")) || (dbUser.imei.equals(symbiosisUser.imei))) &&
//                ((dbUser.imsi  == defaultDataSet.get("Imsi")) || (dbUser.imsi.equals(symbiosisUser.imsi))) &&
//                ((dbUser.password  == defaultDataSet.get("Password")) || (dbUser.password.equals(symbiosisUser.password))));
    }
}
