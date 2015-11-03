package net.blaklizt.streets.android.activity.helpers;

import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.widget.Toast;

import net.blaklizt.streets.android.activity.Startup;
import net.blaklizt.streets.android.common.STATUS_CODES;
import net.blaklizt.streets.android.common.StreetsCommon;
import net.blaklizt.streets.android.common.TASK_TYPE;
import net.blaklizt.streets.android.common.TaskInfo;
import net.blaklizt.streets.android.common.utils.SecurityContext;
import net.blaklizt.streets.android.common.utils.Try;

import java.util.Date;
import java.util.TreeMap;
import java.util.concurrent.CancellationException;

import static java.lang.String.format;

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


public class SequentialTaskManager {

    private static final String TAG = StreetsCommon.getTag(LocationUpdateTask.class);

    public enum TaskStatus { COMPLETED, STARTED, CANCELLED }

    private static TreeMap<String, TaskInfo> runningTasks          = new TreeMap<>();

    private static TreeMap<String, TaskInfo> outstandingTasks      = new TreeMap<>();

    private static TreeMap<String, TaskInfo> completedTasks        = new TreeMap<>();

    private static TreeMap<String, TaskInfo> processDependencyList = new TreeMap<>();

    private static TreeMap<String, Class> viewdependencyList    = new TreeMap<>();

    private static Try<TaskInfo, String> execute(TaskInfo newTaskInfo) {
        Log.i(TAG, format("Executing task %s", newTaskInfo.getClassName()));
        runningTasks.put(newTaskInfo.getClassName(), newTaskInfo);
        Date currentTime = new Date();

        try
        {
            newTaskInfo.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
        catch (CancellationException ex) {}
        catch (Exception ex) {
            ex.printStackTrace();
            SecurityContext.getInstance().handleApplicationError(SecurityContext.ERROR_SEVERITY.GENERAL,
                "Background task " + newTaskInfo.getClassName() + "failed to execute! ", ex.getStackTrace(), TASK_TYPE.SYS_TASK);
        }


        onTaskUpdate(newTaskInfo, TaskStatus.STARTED);
        newTaskInfo.setRequestedTimeIfNotSet(currentTime);
        newTaskInfo.setStartTime(currentTime);
        return Try.success(newTaskInfo);
    }

    public static Try<TaskInfo, String> runImmediately(TaskInfo newTaskInfo) {
        Log.i(TAG, format("Scheduling task %s for immediate execution", newTaskInfo.getClassName()));
        if (!outstandingTasks.containsKey(newTaskInfo.getClassName())) {
            outstandingTasks.put(newTaskInfo.getClassName(), newTaskInfo);
        }
        newTaskInfo.setRequestedTimeIfNotSet(new Date());
        return execute(newTaskInfo);
    }

    public static Try<TaskInfo, String> schedule(TaskInfo newTaskInfo) {
        Log.i(TAG, format("Scheduling task %s to run when prerequisites are met", newTaskInfo.getClassName()));
        if (outstandingTasks.containsKey(newTaskInfo.getClassName())) {
            Log.i(TAG, format("Scheduling task %s which already has a pending execution!", newTaskInfo.getClassName()));
        }
        outstandingTasks.put(newTaskInfo.getClassName(), newTaskInfo);
        newTaskInfo.setRequestedTimeIfNotSet(new Date());
        return Try.success(newTaskInfo);
    }

    public static Try<TaskInfo, String> isAllowedAtLeastOnce(TaskInfo newTaskInfo) {
        Log.i(TAG, format("Checking if we can run %s", newTaskInfo.getClassName()));

        if (newTaskInfo.allowsOnlyOnce()) {
            if (!(completedTasks.containsKey(newTaskInfo.getClassName()) ||
                outstandingTasks.containsKey(newTaskInfo.getClassName()) ||
                    runningTasks.containsKey(newTaskInfo.getClassName()))) {
                return Try.fail("Task is not allowed. It can only execute once: " + newTaskInfo.getClassName());
            }
        }
        Log.i(TAG, "Task isAllowedAtLeastOnce " + newTaskInfo.getClassName());
        return Try.success(newTaskInfo);
    }

    public static Try<TaskInfo, String> isNotBlockedInstance(TaskInfo newTaskInfo) {
        Log.i(TAG, format("Checking if %s isNotBlockedInstance", newTaskInfo.getClassName()));

        if (!newTaskInfo.allowsMultiInstance() && runningTasks.containsKey(newTaskInfo.getClassName())) {
            return Try.fail(format("Task %s cannot begin immediately. Only 1 instance can run at the same time", newTaskInfo.getClassName()));
        }
        Log.i(TAG, "Task isNotBlockedInstance " + newTaskInfo.getClassName());
        return Try.success(newTaskInfo);


    }

    public static Try<TaskInfo, String> hasNoProcessDependencies(TaskInfo newTaskInfo) {
        Log.i(TAG, format("Checking processDependencies for %s", newTaskInfo.getClassName()));

        if (newTaskInfo.getProcessDependencies().size() > 0) {
            if (!completedTasks.keySet().containsAll(newTaskInfo.getProcessDependencies())) {
                Log.i(TAG, format("Added %s to list of tasks awaiting processDependencies...", newTaskInfo.getClassName()));
                processDependencyList.put(newTaskInfo.getClassName(), newTaskInfo);
                return Try.fail(format("Task is %s cannot execute. Some processDependencies are still outstanding: ", newTaskInfo.getClassName()));
            }
        }
        Log.i(TAG, "Task hasNoOutstandingDependencies " + newTaskInfo.getClassName());
        return Try.success(newTaskInfo);
    }

    public static Try<TaskInfo, String> hasNoViewDependencies(TaskInfo newTaskInfo) {
        Log.i(TAG, format("Checking viewDependencies for %s", newTaskInfo.getClassName()));

        if (newTaskInfo.getViewDependencies().size() > 0) {
            return Try.fail("View dependencies not met");
        } else {
            return Try.success(newTaskInfo);
        }
    }

    public static Try<TaskInfo, String> runWhenAvailable(TaskInfo newTaskInfo) {

        Log.i(TAG, "Scheduling task for later execution: " + newTaskInfo);
        Log.i(TAG, "Allows MultiInstance: " + newTaskInfo.allowsMultiInstance());
        Log.i(TAG, "Allows Only Once: " + newTaskInfo.allowsOnlyOnce());
        Log.i(TAG, "Number of processDependencies: " + newTaskInfo.getProcessDependencies().size());

        newTaskInfo.setRequestedTimeIfNotSet(new Date());
        return isAllowedAtLeastOnce(newTaskInfo)
            .map(SequentialTaskManager::isNotBlockedInstance, (s) -> schedule(newTaskInfo))
            .map(SequentialTaskManager::runImmediately, (s) -> schedule(newTaskInfo))
            .map(SequentialTaskManager::hasNoProcessDependencies, (s) -> hasNoViewDependencies(newTaskInfo))
            .map(Try::success, Try::fail);
    }

    public static void cancelRunningTasks() {

        //prevent all waiting tasks from starting
        outstandingTasks.clear();

        for (String runningTaskName : runningTasks.keySet()) {
            Log.i(TAG, format("Terminating task %s", runningTaskName));
            runningTasks.get(runningTaskName).cancel(true);
        }
    }

    public static void onTaskUpdate(TaskInfo asyncTask, TaskStatus newStatus) {
        //initiate all tasks that were held up by the now completed task
        switch (newStatus) {
            case COMPLETED: {

                StreetsCommon.showSnackBar(TAG, "Background task " + asyncTask.getClassName() + " completed", Snackbar.LENGTH_SHORT);
                completedTasks.put(asyncTask.getClassName(), runningTasks.get(asyncTask.getClassName()));
                runningTasks.remove(asyncTask.getClassName());

                for (String awaitingTask : processDependencyList.keySet()) {
                    TaskInfo awaitingTaskInfo = processDependencyList.get(awaitingTask);
                    if (awaitingTask != null && awaitingTaskInfo.getProcessDependencies().contains(asyncTask.getClassName())) {
                        Log.i(TAG, format("Dependency %s resolved for awaiting task %s. Attempting to schedule.", asyncTask.getClassName(), awaitingTask));
                        if (runWhenAvailable(processDependencyList.get(awaitingTask)).isSuccess()) {
                            StreetsCommon.showSnackBar(TAG, "Starting task " + asyncTask.getClassName(), Snackbar.LENGTH_SHORT);
                            Log.i(TAG, format("Scheduled previously awaiting job %s.", awaitingTask));
                            processDependencyList.remove(awaitingTask);
                        }
                    }
                }
                break;
            }
        }
    }
}
