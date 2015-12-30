package net.blaklizt.streets.android.common.utils;

import android.util.Log;
import net.blaklizt.streets.android.activity.AppContext;
import net.blaklizt.streets.android.common.StreetsCommon;
import net.blaklizt.streets.android.common.SymbiosisUser;
import net.blaklizt.streets.android.common.enumeration.TASK_TYPE;

import java.lang.reflect.Field;
import java.util.HashMap;

import static java.lang.String.format;
import static net.blaklizt.streets.android.activity.AppContext.getStreetsCommon;
import static net.blaklizt.streets.android.common.enumeration.STATUS_CODES.GENERAL_ERROR;
import static net.blaklizt.streets.android.common.enumeration.TASK_TYPE.SYS_SECURITY;
import static net.blaklizt.streets.android.common.utils.SecurityContext.EVENT_LEVEL.ERROR;
import static net.blaklizt.streets.android.common.utils.SecurityContext.EVENT_LEVEL.WARNING;

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

    public enum EVENT_LEVEL { INFO, WARNING, ERROR }

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

    public static void handleApplicationError(EVENT_LEVEL eventLevel, String displayMsg, StackTraceElement[] stackTrace, TASK_TYPE taskType) {
        StringBuilder stacktraceMessage = new StringBuilder();
        for (StackTraceElement stackTraceElement : stackTrace) {
            stacktraceMessage.append(stackTraceElement.toString()).append("\n");
        }
        handleApplicationError(eventLevel, displayMsg, stacktraceMessage.toString(), taskType);
    }

    public static void handleApplicationError(EVENT_LEVEL eventLevel, String displayMsg, String error, TASK_TYPE taskType) {

        getStreetsCommon().writeEventLog(taskType, GENERAL_ERROR, error);
        if (eventLevel == WARNING) {
            Log.w(TAG, "Warning, an error occurred: " + displayMsg + "\n" + error);
        } else if (eventLevel == ERROR) {
            Log.e(TAG, "A severe error occurred: " + displayMsg + "\n" + error + "\nApplication will terminate.");
            AppContext.shutdown();
        }
    }

    public static boolean verifyUserDetails(SymbiosisUser symbiosisUser) {

        Log.i(TAG, "Verifying details of current user.");

        if (symbiosisUser == null) {
            Log.e(TAG, "SymbiosisUser cannot be null!");
            handleApplicationError(ERROR, "User session is invalid! Please login again.",
                "SymbiosisUser was null", SYS_SECURITY);
            return false; /* app should have terminated */
        }

        SymbiosisUser dbUser = getStreetsCommon().getSymbiosisUser();

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
                        handleApplicationError(ERROR, "Value of mandatory field " + fieldName + " is invalid.",
                                fieldName + " of SymbiosisUser was null.", SYS_SECURITY);
                        return false;
                    } else { continue; }
                }

                if (!fieldValue.equals(databaseValue)) {
                    if (!userDefaultDataSet.containsKey(fieldName)) {
                        handleApplicationError(ERROR, "Value of field " + fieldName + " is invalid.",
                            format(fieldName + " of SymbiosisUser was modified outside system context.\nDB Value = %s, Default Value = null",
                                databaseValue), SYS_SECURITY);
                        return false;
                    } else if (!fieldValue.equals(userDefaultDataSet.get(fieldName))) {
                        handleApplicationError(ERROR, "Value of field " + fieldName + " is invalid.",
                                format(fieldName + " of SymbiosisUser was modified outside system context.\nDB Value = %s, Default Value = %s",
                                        databaseValue, userDefaultDataSet.get(fieldName)), SYS_SECURITY);
                        return false;
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                handleApplicationError(ERROR, "Failed to verify value of " + fieldName,
                    "Failed to verify value of " + fieldName + ": " + ex.getMessage(), SYS_SECURITY);
                return false;
            }
        }
        return true;
    }
}
