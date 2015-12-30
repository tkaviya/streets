package net.blaklizt.streets.android.activity.helpers;

import android.app.AlertDialog;
import android.content.Intent;
import android.util.Log;
import net.blaklizt.streets.android.activity.MenuLayout;
import net.blaklizt.streets.android.listener.EnableGPSDialogueListener;

import java.util.ArrayList;

import static android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS;
import static net.blaklizt.streets.android.activity.AppContext.getStreetsCommon;
import static net.blaklizt.streets.android.common.enumeration.TASK_TYPE.BG_VIEW_LOCATION_TASK;
import static net.blaklizt.streets.android.common.enumeration.USER_PREFERENCE.AUTO_ENABLE_GPS;
import static net.blaklizt.streets.android.common.enumeration.USER_PREFERENCE.SUGGEST_GPS;

/******************************************************************************
 * *
 * Created:     29 / 10 / 2015                                             *
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
public class LocationSettingsTask extends StreetsAbstractTask {

	public LocationSettingsTask() {
        processDependencies = new ArrayList<>();
        viewDependencies = new ArrayList<>();
        allowOnlyOnce = false;
        allowMultiInstance = false;
        taskType = BG_VIEW_LOCATION_TASK;
    }

    @Override
    protected Object doInBackground(Object...params) {
        Log.i(TAG, "GPS not enabled");
        if (getStreetsCommon().getUserPreferenceValue(AUTO_ENABLE_GPS).equals("1")) {
            Log.i(TAG, "User has granted auto enable privilege");
            Intent myIntent = new Intent(ACTION_LOCATION_SOURCE_SETTINGS);
            MenuLayout.getInstance().startActivityForResult(myIntent, Integer.parseInt(BG_VIEW_LOCATION_TASK.task_type_id));
        } else if (getStreetsCommon().getUserPreferenceValue(SUGGEST_GPS).equals("1")) {
            Log.i(TAG, "Must request perms from use");
            EnableGPSDialogueListener enableGpsListener = new EnableGPSDialogueListener(MenuLayout.getInstance());
            AlertDialog.Builder builder = new AlertDialog.Builder(MenuLayout.getInstance());
            builder.setMessage("Turn on GPS?")
                    .setMultiChoiceItems(
                            EnableGPSDialogueListener.getQuestionItems(),
                            EnableGPSDialogueListener.getCheckedItems(),
                            EnableGPSDialogueListener.EnableGPSOptionListener.getInstance())
                    .setPositiveButton("Yes", enableGpsListener)
                    .setNegativeButton("No", enableGpsListener).create().show();
        }
        return null;
    }
}
