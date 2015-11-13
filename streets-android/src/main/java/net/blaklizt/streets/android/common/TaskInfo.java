package net.blaklizt.streets.android.common;

import android.os.AsyncTask;

import net.blaklizt.streets.android.activity.AppContext;
import net.blaklizt.streets.android.activity.helpers.SequentialTaskManager;
import net.blaklizt.streets.android.activity.helpers.StreetsAbstractView;
import net.blaklizt.streets.android.activity.helpers.StreetsInterfaceTask;

import java.util.ArrayList;
import java.util.Date;

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
public abstract class TaskInfo extends AsyncTask implements StreetsInterfaceTask {

    protected final String TAG = StreetsCommon.getTag(this.getClass());
    protected static boolean initialized = false;

    protected static Boolean allowOnlyOnce = null;


    protected static Boolean allowMultiInstance = null;
    protected static ArrayList<String> processDependencies = null;
    protected static ArrayList<Class<? extends StreetsAbstractView>> viewDependencies = null;
    protected static TASK_TYPE taskType = null;

    protected Date requestedTime = null, startTime = null, endTime = null;
    protected STATUS_CODES finalStatus;

    public TaskInfo validateInitialized() {
        if (!initialized) {
            if (processDependencies == null || viewDependencies == null ||
                allowMultiInstance == null || allowOnlyOnce == null ||
                taskType == null) {
                throw new RuntimeException("Task cannot be used until it has been initialized. You must call setExecutionRestrictions()");
            }
            else {
                AppContext.registerOnDestroyHandler(this);
                initialized = true;
            }
        }
        return this;
    }

    public Date getRequestedTime() { return requestedTime; }

    public Date getStartTime() { return startTime; }

    public Date getEndTime() { return endTime; }

    public boolean allowsMultiInstance() { return validateInitialized().allowMultiInstance; }

    public boolean allowsOnlyOnce() { return validateInitialized().allowOnlyOnce; }

    public TASK_TYPE getTaskType() { return validateInitialized().taskType; }

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
        return validateInitialized().processDependencies;
    }

    public ArrayList<Class<? extends StreetsAbstractView>> getViewDependencies() {
        return validateInitialized().viewDependencies;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected final void onPostExecute(Object result) {
        super.onPostExecute(result);
        endTime = new Date();
        AppContext.getStreetsCommon().logTaskEvent(this, STATUS_CODES.SUCCESS);
        onTaskUpdate(this, SequentialTaskManager.TaskStatus.COMPLETED);
        onPostExecuteRelay(result);
    }

    @Override
    protected final void onCancelled() {
        super.onCancelled();
        endTime = new Date();
        AppContext.getStreetsCommon().logTaskEvent(this, STATUS_CODES.CANCELLED);
        onTaskUpdate(this, SequentialTaskManager.TaskStatus.CANCELLED);
        onCancelledRelay();
    }

    @Override
    /* only called if you call registerOnDestroyHandler on Startup class */
    public final void onTermination() {
        if (!getStatus().equals(Status.FINISHED)) { cancel(true); }
        onTerminationRelay();
    }

    /* this method is only here to allow you to still catch onPostExecute */
    protected void onPostExecuteRelay(Object result) {}

    /* this method is only here to allow you to still catch onCancelledRelay */
    protected void onCancelledRelay() {}

    /* this method is only here to allow you to still catch onTerminationRelay */
    protected void onTerminationRelay() {}}
