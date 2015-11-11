package net.blaklizt.streets.android.common;

import android.os.AsyncTask;

import net.blaklizt.streets.android.activity.Startup;
import net.blaklizt.streets.android.activity.helpers.SequentialTaskManager;
import net.blaklizt.streets.android.activity.helpers.StreetsProviderPattern;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import static net.blaklizt.streets.android.activity.helpers.SequentialTaskManager.onTaskUpdate;

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
public abstract class TaskInfo extends AsyncTask implements StreetsProviderPattern {

    protected final String TAG = StreetsCommon.getTag(this.getClass());
    protected final boolean allowOnlyOnce;
    protected final boolean allowMultiInstance;
    protected ArrayList<String> processDependencies = new ArrayList<>();
    protected HashMap<String, Class> viewDependencies = new HashMap<>();
    protected Date requestedTime = null, startTime = null, endTime = null;

    protected TASK_TYPE taskType;
    protected STATUS_CODES finalStatus;

    protected TaskInfo(ArrayList<String> processDependencies, ArrayList<Class> viewDependencies,
           boolean allowOnlyOnce, boolean allowConcurrent, TASK_TYPE taskType)
    {
        if (processDependencies != null) {
            this.processDependencies = processDependencies;
        }

        if (viewDependencies != null) {
            for (Class dependency : viewDependencies) {
                this.viewDependencies.put(dependency.getSimpleName(), dependency);
            }
        }

        this.allowOnlyOnce = allowOnlyOnce;
        this.allowMultiInstance = allowConcurrent;
        this.taskType = taskType;

        Startup.getInstance().registerOnDestroyHandler(this);
    }

    public Date getRequestedTime() { return requestedTime; }

    public Date getStartTime() { return startTime; }

    public Date getEndTime() { return endTime; }

    public boolean allowsMultiInstance() { return allowMultiInstance; }

    public boolean allowsOnlyOnce() { return allowOnlyOnce; }

    public TASK_TYPE getTaskType() {
        return taskType;
    }

    public STATUS_CODES getFinalStatus() {
        return finalStatus;
    }

    public boolean setRequestedTimeIfNotSet(Date requestedTime) {
        if (this.requestedTime == null && requestedTime != null) {
            this.requestedTime = requestedTime;
            return true;
        }
        return false;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public ArrayList<String> getProcessDependencies() {
        return processDependencies;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected final void onPostExecute(Object result) {
        super.onPostExecute(result);
        endTime = new Date();
        Startup.getStreetsCommon().logTaskEvent(this, STATUS_CODES.SUCCESS);
        onTaskUpdate(this, SequentialTaskManager.TaskStatus.COMPLETED);
        onPostExecuteRelay(result);
    }

    @Override
    protected final void onCancelled() {
        super.onCancelled();
        endTime = new Date();
        Startup.getStreetsCommon().logTaskEvent(this, STATUS_CODES.CANCELLED);
        onTaskUpdate(this, SequentialTaskManager.TaskStatus.CANCELLED);
        onCancelledRelay();
    }

    @Override
    public final void onTermination() {
        if (!getStatus().equals(Status.FINISHED)) { cancel(true); }
        onTerminationRelay();
    }

    /* this method is only here to allow you to still catch onPostExecute */
    protected void onPostExecuteRelay(Object result) {}

    /* this method is only here to allow you to still catch onCancelledRelay */
    protected void onCancelledRelay() {}

    /* this method is only here to allow you to still catch onTerminationRelay */
    protected void onTerminationRelay() {}

    public HashMap<String, Class> getViewDependencies() {
        return viewDependencies;
    }
}
