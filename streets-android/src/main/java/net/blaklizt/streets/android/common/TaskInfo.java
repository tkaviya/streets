package net.blaklizt.streets.android.common;

import android.os.AsyncTask;
import android.util.Log;

import net.blaklizt.streets.android.activity.helpers.SequentialTaskManager;
import net.blaklizt.streets.android.activity.helpers.StatusChangeListener;
import net.blaklizt.streets.android.activity.helpers.StatusChangeNotifier;

import java.util.ArrayList;
import java.util.Date;

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
public class TaskInfo<T extends AsyncTask<Void,Void,Void> & StatusChangeNotifier> implements StatusChangeListener {

    private static final String TAG = StreetsCommon.getTag(TaskInfo.class);

    T currentTask = null;
    Class<T> taskType;
    ArrayList<T> tasks;
    boolean allowOnlyOnce;
    boolean allowMultiInstance;

    ArrayList<String> dependencies      = new ArrayList<>();
    ArrayList<Date> requestedTimes      = new ArrayList<>();
    ArrayList<Date> startTimes          = new ArrayList<>();
    ArrayList<Date> endTimes            = new ArrayList<>();

    public TaskInfo(T task, ArrayList<String> dependencies, boolean allowOnce, boolean allowConcurrent)
    {
        this.currentTask = task;
        this.dependencies = dependencies;
        this.allowOnlyOnce= allowOnce;
        this.allowMultiInstance = allowConcurrent;
    }

    public String name() { return currentTask != null ? currentTask.getClass().getSimpleName() : taskType.getSimpleName(); }

    public boolean allowsMultiInstance() { return allowMultiInstance; }

    public boolean allowsOnlyOnce() { return allowOnlyOnce; }

    public T getAsyncTask() {
        if (currentTask == null)
        {
            requestedTimes.add(new Date());

            Log.i(TAG, "Task request registered for task " + currentTask.getClass().getSimpleName());

            tasks.add(currentTask);

            currentTask.registerStatusChangeListener(this);
        }
        return currentTask;
    }

    public ArrayList<String> getDependencies() {
        return dependencies;
    }

    public void removeDependency(String dependency) {
        getDependencies().remove(dependency);
    }

    @Override
    public void onStatusUpdate(AsyncTask asyncTask, SequentialTaskManager.TaskStatus newStatus) {
        switch (newStatus) {
            case STARTED: { startTimes.add(new Date()); break; }
            case COMPLETED: { endTimes.add(new Date()); break; }
            case CANCELLED: { endTimes.add(new Date()); break; }
        }
    }
}
