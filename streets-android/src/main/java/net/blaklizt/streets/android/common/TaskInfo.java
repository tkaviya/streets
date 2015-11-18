package net.blaklizt.streets.android.common;

import android.os.AsyncTask;
import android.util.Log;

import net.blaklizt.streets.android.activity.AppContext;
import net.blaklizt.streets.android.activity.helpers.SequentialTaskManager;
import net.blaklizt.streets.android.activity.helpers.StreetsAbstractView;
import net.blaklizt.streets.android.activity.helpers.StreetsInterfaceTask;

import java.util.ArrayList;
import java.util.Date;

import static java.lang.String.format;
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
    protected boolean initialized = false;
    protected Boolean allowOnlyOnce = null;
    protected Boolean allowMultiInstance = null;
    protected ArrayList<String> processDependencies = null;
    protected ArrayList<Class<? extends StreetsAbstractView>> viewDependencies = null;
    protected TASK_TYPE taskType = null;
    protected Object[] additionalParams = null;
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

    public TaskInfo setAdditionalParams(Object... additionalParams) {
        this.additionalParams = additionalParams;
        return this;
    }

    public Object[] getAdditionalParams() {
        return additionalParams;
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

    @Override
    public String getClassName() {
        return this.getClass().getSimpleName();
    }

    @Override
    /* only called if you call registerOnDestroyHandler on Startup class */
    public final void onTermination() {
        Log.i(TAG, "+++ onTermination +++");
        if (getStatus().equals(Status.RUNNING))
        {
            Log.i(TAG, format("Cancelling task %s which is in status %s", getClassName(), getStatus().name()));
            cancel(true);
        }
        onTerminationRelay();
    }

    /* this class needs to catch onPreExecute, onPostExecute, onCancelled, onTermination events for internal processing.
     * onPreExecute, onPostExecute, onCancelled, onTermination have been made final to restrict overriding to ensure correct class functionality.
     * to catch any of these events, the class forwards the events to onXxxRelay methods which can be overridden if required.
     * the relay classes operate identically and have identical data to the original classes. There is merely a slight processing time delay
     * they all save event time (startTime/endTime). when onTermination is called, it means thread was active, therefore onCancelled will be called) */
    @Override
    protected final void onPreExecute() {
        startTime = new Date();
        Log.i(TAG, "+++ onPreExecute +++");
        super.onPreExecute();
        setRequestedTimeIfNotSet(new Date());
        onTaskUpdate(this, SequentialTaskManager.TaskStatus.STARTED);
        onPreExecuteRelay(additionalParams);
    }

    /* this class needs to catch onPreExecute, onPostExecute, onCancelled, onTermination events for internal processing.
     * onPreExecute, onPostExecute, onCancelled, onTermination have been made final to restrict overriding to ensure correct class functionality.
     * to catch any of these events, the class forwards the events to onXxxRelay methods which can be overridden if required.
     * the relay classes operate identically and have identical data to the original classes. There is merely a slight processing time delay
     * they all save event time (startTime/endTime). when onTermination is called, it means thread was active, therefore onCancelled will be called) */
    @Override
    @SuppressWarnings("unchecked")
    protected final void onPostExecute(Object result) {
        endTime = new Date();
        Log.i(TAG, "+++ onPostExecute +++");
        super.onPostExecute(result);
        AppContext.getStreetsCommon().logTaskEvent(this, STATUS_CODES.SUCCESS);
        onTaskUpdate(this, SequentialTaskManager.TaskStatus.COMPLETED);
        onPostExecuteRelay(result);
    }


    /* this class needs to catch onPreExecute, onPostExecute, onCancelled, onTermination events for internal processing.
     * onPreExecute, onPostExecute, onCancelled, onTermination have been made final to restrict overriding to ensure correct class functionality.
     * to catch any of these events, the class forwards the events to onXxxRelay methods which can be overridden if required.
     * the relay classes operate identically and have identical data to the original classes. There is merely a slight processing time delay
     * they all save event time (startTime/endTime). when onTermination is called, it means thread was active, therefore onCancelled will be called) */
    @Override
    protected final void onCancelled() {
        endTime = new Date();
        Log.i(TAG, "+++ onCancelled +++");
        super.onCancelled();
        AppContext.getStreetsCommon().logTaskEvent(this, STATUS_CODES.CANCELLED);
        onTaskUpdate(this, SequentialTaskManager.TaskStatus.CANCELLED);
        onCancelledRelay();
    }

    /* this class needs to catch onPreExecute, onPostExecute, onCancelled, onTermination events for internal processing.
     * onPreExecute, onPostExecute, onCancelled, onTermination have been made final to restrict overriding to ensure correct class functionality.
     * to catch any of these events, the class forwards the events to onXxxRelay methods which can be overridden if required.
     * the relay classes operate identically and have identical data to the original classes. There is merely a slight processing time delay
     * they all save event time (startTime/endTime). when onTermination is called, it means thread was active, therefore onCancelled will be called) */
    protected void onPreExecuteRelay(Object[] additionalParams) {}

    /* this class needs to catch onPreExecute, onPostExecute, onCancelled, onTermination events for internal processing.
     * onPreExecute, onPostExecute, onCancelled, onTermination have been made final to restrict overriding to ensure correct class functionality.
     * to catch any of these events, the class forwards the events to onXxxRelay methods which can be overridden if required.
     * the relay classes operate identically and have identical data to the original classes. There is merely a slight processing time delay
     * they all save event time (startTime/endTime). when onTermination is called, it means thread was active, therefore onCancelled will be called) */
    protected void onPostExecuteRelay(Object result) {}

    /* this class needs to catch onPreExecute, onPostExecute, onCancelled, onTermination events for internal processing.
     * onPreExecute, onPostExecute, onCancelled, onTermination have been made final to restrict overriding to ensure correct class functionality.
     * to catch any of these events, the class forwards the events to onXxxRelay methods which can be overridden if required.
     * the relay classes operate identically and have identical data to the original classes. There is merely a slight processing time delay
     * they all save event time (startTime/endTime). when onTermination is called, it means thread was active, therefore onCancelled will be called) */
    protected void onCancelledRelay() {}

    /* this class needs to catch onPreExecute, onPostExecute, onCancelled, onTermination events for internal processing.
     * onPreExecute, onPostExecute, onCancelled, onTermination have been made final to restrict overriding to ensure correct class functionality.
     * to catch any of these events, the class forwards the events to onXxxRelay methods which can be overridden if required.
     * the relay classes operate identically and have identical data to the original classes. There is merely a slight processing time delay
     * they all save event time (startTime/endTime). when onTermination is called, it means thread was active, therefore onCancelled will be called) */
    protected void onTerminationRelay() {}}
