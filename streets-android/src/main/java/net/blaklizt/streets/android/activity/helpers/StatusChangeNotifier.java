package net.blaklizt.streets.android.activity.helpers;

import android.os.AsyncTask;

import net.blaklizt.streets.android.common.StreetsCommon;

/******************************************************************************
 * *
 * Created:     30 / 10 / 2015                                             *
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



public interface StatusChangeNotifier {

    String TAG = StreetsCommon.getTag(StatusChangeListener.class.getDeclaringClass());

    default void registerStatusChangeListener(StatusChangeListener statusChangeListener) {
//        Log.i(TAG, format("Adding status change listener for %s", statusChangeListener.getClass().getSimpleName()));
//        SequentialTaskManager.statusChangeListeners.add(statusChangeListener);
    }

    default void onStatusChange(AsyncTask managedAsyncTask, SequentialTaskManager.TaskStatus newStatus) {
//        for (StatusChangeListener listener : SequentialTaskManager.statusChangeListeners) {
//            Log.i(TAG, format("Received new status : %s Invoking listener %s ", listener.getClass().getSimpleName(), newStatus));
//            listener.onStatusUpdate(managedAsyncTask, newStatus);
//        }
    }

}