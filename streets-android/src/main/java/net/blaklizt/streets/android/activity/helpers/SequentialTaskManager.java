package net.blaklizt.streets.android.activity.helpers;

import android.os.AsyncTask;
import android.util.Log;

import net.blaklizt.streets.android.common.StreetsCommon;
import net.blaklizt.streets.android.common.TaskInfo;
import net.blaklizt.streets.android.common.utils.Try;

import java.util.ArrayList;
import java.util.TreeMap;

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


public class SequentialTaskManager implements StatusChangeListener {

    private static final String TAG = StreetsCommon.getTag(LocationUpdateTask.class);

    public static ArrayList<StatusChangeListener> statusChangeListeners;

    public enum TaskStatus { COMPLETED, STARTED, CANCELLED }

    private static TreeMap<String, TaskInfo> runningTasks          = new TreeMap<>();

    private static TreeMap<String, TaskInfo> outstandingTasks      = new TreeMap<>();

    private static TreeMap<String, TaskInfo> completedTasks        = new TreeMap<>();

    private static TreeMap<String, TaskInfo> dependencyList        = new TreeMap<>();

    private static Try<TaskInfo, String> execute(TaskInfo newTaskInfo) {
        Log.i(TAG, format("Executing task %s", newTaskInfo.name()));
        runningTasks.put(newTaskInfo.name(), newTaskInfo);
        newTaskInfo.getAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        return Try.success(newTaskInfo);
    }

    public static Try<TaskInfo, String> runImmediately(TaskInfo newTaskInfo) {
        Log.i(TAG, format("Scheduling task %s for immediate execution", newTaskInfo.name()));
        if (!outstandingTasks.containsKey(newTaskInfo.name())) {
            outstandingTasks.put(newTaskInfo.name(), newTaskInfo);
        }
        return execute(newTaskInfo);
    }

    public static Try<TaskInfo, String> schedule(TaskInfo newTaskInfo) {
        Log.i(TAG, format("Scheduling task %s to run when prerequisites are met", newTaskInfo.name()));
        if (outstandingTasks.containsKey(newTaskInfo.name())) {
            Log.i(TAG, format("Scheduling task %s which already has a pending execution!", newTaskInfo.name()));
        }
        outstandingTasks.put(newTaskInfo.name(), newTaskInfo);
        return Try.success(newTaskInfo);
    }

    public static Try<TaskInfo, String> isAllowedAtLeastOnce(TaskInfo newTaskInfo) {
        Log.i(TAG, format("Checking if we can run %s", newTaskInfo.name()));

        if (newTaskInfo.allowsOnlyOnce()) {
            if (!(completedTasks.containsKey(newTaskInfo.name()) ||
                outstandingTasks.containsKey(newTaskInfo.name()) ||
                    runningTasks.containsKey(newTaskInfo.name()))) {
                return Try.fail("Task is not allowed. It can only execute once: " + newTaskInfo.name());
            }
        }
        Log.i(TAG, "Task isAllowedAtLeastOnce " + newTaskInfo.name());
        return Try.success(newTaskInfo);
    }

    public static Try<TaskInfo, String> isNotBlockedInstance(TaskInfo newTaskInfo) {
        Log.i(TAG, format("Checking if %s isNotBlockedInstance", newTaskInfo.name()));

        if (!newTaskInfo.allowsMultiInstance() && runningTasks.containsKey(newTaskInfo.name())) {
            return Try.fail(format("Task %s cannot begin immediately. Only 1 instance can run at the same time", newTaskInfo.name()));
        }
        Log.i(TAG, "Task isNotBlockedInstance " + newTaskInfo.name());
        return Try.success(newTaskInfo);


    }

    public static Try<TaskInfo, String> hasNoOutstandingDependencies(TaskInfo newTaskInfo) {
        Log.i(TAG, format("Checking dependencies for %s", newTaskInfo.name()));

        if (newTaskInfo.getDependencies().size() > 0) {
            if (!completedTasks.keySet().containsAll(newTaskInfo.getDependencies())) {
                Log.i(TAG, format("Added %s to list of tasks awaiting dependencies...", newTaskInfo.name()));
                dependencyList.put(newTaskInfo.name(), newTaskInfo);
                return Try.fail(format("Task is %s cannot execute. Some dependencies are still outstanding: ", newTaskInfo.name()));
            }
        }
        Log.i(TAG, "Task hasNoOutstandingDependencies " + newTaskInfo.name());
        return Try.success(newTaskInfo);
    }

    public static Try<TaskInfo, String> runWhenAvailable(TaskInfo newTaskInfo) {

        Log.i(TAG, "Scheduling task for later execution: " + newTaskInfo);
        Log.i(TAG, "Allows MultiInstance: " + newTaskInfo.allowsMultiInstance());
        Log.i(TAG, "Allows Only Once: " + newTaskInfo.allowsOnlyOnce());
        Log.i(TAG, "Number of dependencies: " + newTaskInfo.getDependencies().size());

        return isAllowedAtLeastOnce(newTaskInfo)
            .map(SequentialTaskManager::isNotBlockedInstance, (s) -> schedule(newTaskInfo))
            .map(SequentialTaskManager::runImmediately, (s) -> schedule(newTaskInfo))
            .map(SequentialTaskManager::hasNoOutstandingDependencies, Try::fail);
    }

    public static void cancelRunningTasks() {

        //prevent all waiting tasks from starting
        outstandingTasks.clear();

        for (String runningTaskName : runningTasks.keySet()) {
            Log.i(TAG, format("Terminating task %s", runningTaskName));
            runningTasks.get(runningTaskName).getAsyncTask().cancel(true);
        }
    }

    @Override
    public void onStatusUpdate(AsyncTask asyncTask, TaskStatus newStatus) {
        //initiate all tasks that were held up by the now completed task
        String taskName = asyncTask.getClass().getSimpleName();
        switch (newStatus) {
            case COMPLETED: {
                if (outstandingTasks != null) {
                    completedTasks.put(taskName, runningTasks.get(taskName));
                    runningTasks.remove(taskName);
                    outstandingTasks.remove(taskName);

                    for (String awaitingTask : dependencyList.keySet()) {
                        if (dependencyList.get(awaitingTask) != null &&
                            dependencyList.get(awaitingTask).getDependencies().contains(taskName)) {
                            Log.i(TAG, format("Dependency %s resolved for awaiting task %s. Attempting to schedule.", taskName, awaitingTask));
                            if (runWhenAvailable(dependencyList.get(awaitingTask)).isSuccess()) {
                                Log.i(TAG, format("Scheduled previously awaiting job %s.", awaitingTask));
                                dependencyList.remove(awaitingTask);
                            }
                        }
                    }

                }
                break;
            }
        }
    }
}
