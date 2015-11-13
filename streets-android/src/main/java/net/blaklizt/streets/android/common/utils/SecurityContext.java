package net.blaklizt.streets.android.common.utils;

import android.util.Log;

import net.blaklizt.streets.android.activity.AppContext;
import net.blaklizt.streets.android.activity.Startup;
import net.blaklizt.streets.android.common.STATUS_CODES;
import net.blaklizt.streets.android.common.StreetsCommon;
import net.blaklizt.streets.android.common.SymbiosisUser;
import net.blaklizt.streets.android.common.TASK_TYPE;

import java.lang.reflect.Field;
import java.util.HashMap;

import static java.lang.String.format;

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

    private static final HashMap<String, Object> userDefaultDataSet = new HashMap<>();

    public enum ERROR_SEVERITY { GENERAL, SEVERE }

    static {
        userDefaultDataSet.put("symbiosisUserID", 0L);
        userDefaultDataSet.put("username", null);
        userDefaultDataSet.put("imei", null);
        userDefaultDataSet.put("imsi", null);
        userDefaultDataSet.put("password", null);
        userDefaultDataSet.put("last_location_id", null);
        userDefaultDataSet.put("home_place_id", null);
        userDefaultDataSet.put("type", "USER");
    }

    public static HashMap<String, Object> getUserDefaultDataSet() {
        return userDefaultDataSet;
    }

    public static void handleApplicationError(ERROR_SEVERITY errorSeverity, String displayMsg, StackTraceElement[] stackTrace, TASK_TYPE taskType) {
        StringBuilder stacktraceMessage = new StringBuilder();
        for (StackTraceElement stackTraceElement : stackTrace) {
            stacktraceMessage.append(stackTraceElement.toString()).append("\n");
        }
        handleApplicationError(errorSeverity, displayMsg, stacktraceMessage.toString(), taskType);
    }

    public static void handleApplicationError(ERROR_SEVERITY errorSeverity, String displayMsg, String error, TASK_TYPE taskType) {

        AppContext.getStreetsCommon().writeEventLog(taskType, STATUS_CODES.GENERAL_ERROR, error);
        if (errorSeverity == ERROR_SEVERITY.GENERAL) {
            Log.w(TAG, "A general error occurred: " + displayMsg + "\n" + error);
        } else if (errorSeverity == ERROR_SEVERITY.SEVERE) {
            Log.e(TAG, "A severe system error occurred: " + displayMsg + "\n" + error + "\nApplication will terminate.");
            Startup.getInstance().onDestroy();
        }
    }

    public static boolean verifyUserDetails(SymbiosisUser symbiosisUser) {

        Log.i(TAG, "Verifying details of current user.");

        if (symbiosisUser == null) {
            Log.e(TAG, "SymbiosisUser cannot be null!");
            handleApplicationError(ERROR_SEVERITY.SEVERE, "User session is invalid! Please login again.",
                "SymbiosisUser was null", TASK_TYPE.SYS_SECURITY);
            return false; /* app should have terminated */
        }

        SymbiosisUser dbUser = AppContext.getStreetsCommon().getSymbiosisUser();

        for (Field field : symbiosisUser.getClass().getFields()) {
            String fieldName = field.getName();
            Log.i(TAG, "Verifying data off field " + fieldName);
            try {
                Object fieldValue = SymbiosisUser.class.getField(fieldName).get(symbiosisUser);
                Log.i(TAG, "CurrentUserValue: " + fieldValue);
                Object databaseValue = SymbiosisUser.class.getField(fieldName).get(dbUser);
                Log.i(TAG, "CurrentUserValue: " + databaseValue);

                if (fieldValue == null) {
                    if (databaseValue != null && userDefaultDataSet.get(fieldName) != null) {
                        handleApplicationError(ERROR_SEVERITY.SEVERE, "Value of mandatory field " + fieldName + " is invalid.",
                                fieldName + " of SymbiosisUser was null.", TASK_TYPE.SYS_SECURITY);
                        return false;
                    } else { continue; }
                }

                if (!fieldValue.equals(databaseValue)) {
                    if (!userDefaultDataSet.containsKey(fieldName)) {
                        handleApplicationError(ERROR_SEVERITY.SEVERE, "Value of field " + fieldName + " is invalid.",
                            format(fieldName + " of SymbiosisUser was modified outside system context.\nDB Value = %s, Default Value = null",
                                databaseValue), TASK_TYPE.SYS_SECURITY);
                        return false;
                    } else if (!fieldValue.equals(userDefaultDataSet.get(fieldName))) {
                        handleApplicationError(ERROR_SEVERITY.SEVERE, "Value of field " + fieldName + " is invalid.",
                            format(fieldName + " of SymbiosisUser was modified outside system context.\nDB Value = %s, Default Value = %s",
                                databaseValue, userDefaultDataSet.get(fieldName)), TASK_TYPE.SYS_SECURITY);
                        return false;
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                handleApplicationError(ERROR_SEVERITY.SEVERE, "Failed to verify value of " + fieldName,
                    "Failed to verify value of " + fieldName + ": " + ex.getMessage(), TASK_TYPE.SYS_SECURITY);
                return false;
            }
        }
        return true;
    }
}
