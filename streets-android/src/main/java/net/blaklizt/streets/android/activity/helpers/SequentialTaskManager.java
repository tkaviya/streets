package net.blaklizt.streets.android.activity.helpers;

import android.support.design.widget.Snackbar;
import android.util.Log;

import net.blaklizt.streets.android.common.StreetsCommon;
import net.blaklizt.streets.android.common.TaskInfo;
import net.blaklizt.streets.android.common.utils.SecurityContext.ERROR_SEVERITY;
import net.blaklizt.streets.android.common.utils.Try;

import java.util.ArrayList;
import java.util.Date;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.CancellationException;

import static android.os.AsyncTask.THREAD_POOL_EXECUTOR;
import static java.lang.String.format;
import static net.blaklizt.streets.android.activity.AppContext.getBackgroundExecutionTask;
import static net.blaklizt.streets.android.activity.AppContext.getStreetsFragments;
import static net.blaklizt.streets.android.common.enumeration.TASK_TYPE.SYS_TASK;
import static net.blaklizt.streets.android.common.utils.SecurityContext.handleApplicationError;
import static net.blaklizt.streets.android.common.utils.Try.fail;
import static net.blaklizt.streets.android.common.utils.Try.success;

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

    private static final String TAG = StreetsCommon.getTag(SequentialTaskManager.class);

    public enum TaskStatus { COMPLETED, STARTED, CANCELLED }

    private static TreeMap<String, TaskInfo> runningTasks          = new TreeMap<>();

    private static TreeMap<String, TaskInfo> outstandingTasks      = new TreeMap<>();

    private static TreeMap<String, TaskInfo> completedTasks        = new TreeMap<>();

    private static TreeMap<String, TaskInfo> processDependencyList = new TreeMap<>();

    private static TreeMap<String, TaskInfo> viewDependencyList    = new TreeMap<>();

    private static boolean holdPendingQueue = false;

    @SuppressWarnings("unchecked")
    private synchronized static Try<TaskInfo, String> execute(TaskInfo bgTask) {

        if (!holdPendingQueue) {
            Log.i(TAG, format("Executing task %s", bgTask.getClassName()));
            runningTasks.put(bgTask.getClassName(), bgTask);
            try {
                bgTask.executeOnExecutor(THREAD_POOL_EXECUTOR, bgTask.getAdditionalParams());
                outstandingTasks.remove(bgTask.getClassName());
            } catch (CancellationException ex) {
                Log.w(TAG, format("Task %s was cancelled before completion!", bgTask.getClassName()));
            } catch (Exception ex) {
                ex.printStackTrace();
                handleApplicationError(ERROR_SEVERITY.GENERAL,
                    "Background task " + bgTask.getClassName() + "failed to complete execution! ", ex.getStackTrace(), SYS_TASK);
            }
        }
        return success(bgTask);
    }

    private static Try<TaskInfo, String> schedule(TaskInfo bgTask) {
        Log.i(TAG, format("Scheduling task %s to run when prerequisites are met", bgTask.getClassName()));
        if (outstandingTasks.containsKey(bgTask.getClassName())) {
            Log.i(TAG, format("Task %s already has a pending execution! Will not schedule another instance.", bgTask.getClassName()));
        } else {
            outstandingTasks.put(bgTask.getClassName(), bgTask);
            bgTask.setRequestedTimeIfNotSet(new Date());
        }
        return success(bgTask);
    }

    private static Try<TaskInfo, String> isAllowedAtLeastOnce(TaskInfo bgTask) {
        Log.i(TAG, format("Checking if we can run %s", bgTask.getClassName()));

        if (bgTask.allowsOnlyOnce()) {
            if (completedTasks.containsKey(bgTask.getClassName()) ||
              outstandingTasks.containsKey(bgTask.getClassName()) ||
                  runningTasks.containsKey(bgTask.getClassName())) {
                return fail("Task is not allowed. It can only execute once: " + bgTask.getClassName());
            }
        }
        Log.i(TAG, "Task isAllowedAtLeastOnce " + bgTask.getClassName());
        return success(bgTask);
    }

    private static Try<TaskInfo, String> isNotBlockedInstance(TaskInfo bgTask) {
        Log.i(TAG, format("Checking if %s isNotBlockedInstance", bgTask.getClassName()));

        if (!bgTask.allowsMultiInstance() && runningTasks.containsKey(bgTask.getClassName())) {
            return fail(format("Task %s cannot begin immediately. Only 1 instance can run at the same time", bgTask.getClassName()));
        }
        Log.i(TAG, "Task isNotBlockedInstance " + bgTask.getClassName());
        return success(bgTask);
    }

    private static Try<TaskInfo, String> hasNoProcessDependencies(TaskInfo bgTask) {
        Log.i(TAG, format("Checking processDependencies for %s", bgTask.getClassName()));

        if (bgTask.getProcessDependencies().size() > 0) {

            if (!completedTasks.keySet().containsAll(bgTask.getProcessDependencies())) {

                String processDependencies = "";
                for (String dependency : bgTask.getProcessDependencies()) {
                    processDependencies += "|" + dependency;
                }
                Log.i(TAG, format("ProcessDependencies %s", processDependencies));

                String completedTaskList = "";
                for (String completedTask : completedTasks.keySet()) {
                    completedTaskList += "|" + completedTask;
                }
                Log.i(TAG, format("CompletedTasks %s", completedTaskList));


                Log.i(TAG, format("Adding %s to list of tasks awaiting process dependencies...", bgTask.getClassName()));
                if (!processDependencyList.containsKey(bgTask.getClassName())) {
                    processDependencyList.put(bgTask.getClassName(), bgTask);
                }
                return fail(format("Task is %s cannot execute. Some process dependencies are still outstanding: ", bgTask.getClassName()));
            }
        }
        Log.i(TAG, "Task hasNoProcessDependencies: " + bgTask.getClassName());
        return success(bgTask);
    }

    private static Try<TaskInfo, String> hasNoViewDependencies(TaskInfo bgTask) {
        Log.i(TAG, format("Checking viewDependencies for %s", bgTask.getClassName()));

        if (bgTask.getViewDependencies().size() > 0) {
            for (Class<? extends StreetsAbstractView> viewDependency : bgTask.getViewDependencies()) {
                if (getStreetsFragments().get(viewDependency) == null) {
                    Log.i(TAG, format("Adding %s to list of tasks awaiting view dependencies on view: %s",
                            bgTask.getClassName(), viewDependency.getSimpleName()));
                    if (!viewDependencyList.containsKey(bgTask.getClassName())) {
                        viewDependencyList.put(bgTask.getClassName(), bgTask);
                    }
                    return fail(format("Task is %s cannot execute. Some view dependencies are still outstanding: ", bgTask.getClassName()));
                }
            }
        }
        Log.i(TAG, "Task hasNoViewDependencies: " + bgTask.getClassName());
        return success(bgTask);
    }

    private static Try<TaskInfo, String> checkReadyToExecute(TaskInfo bgTask) {

        Try<TaskInfo, String> result = isNotBlockedInstance(bgTask);
        if (result.isFailure()) { return result; }
        result = hasNoProcessDependencies(bgTask);  if (result.isFailure()) { return result; }
        result = hasNoViewDependencies(bgTask);     if (result.isFailure()) { return result; }
        return success(bgTask);
    }

    public static Try<TaskInfo, String> runWhenAvailable(TaskInfo bgTask) {

        Log.i(TAG, "Scheduling task for later execution: " + bgTask.getClassName());
        Log.i(TAG, "Allows MultiInstance: " + bgTask.allowsMultiInstance());
        Log.i(TAG, "Allows Only Once: " + bgTask.allowsOnlyOnce());
        Log.i(TAG, "Number of processDependencies: " + bgTask.getProcessDependencies().size());

        bgTask.setRequestedTimeIfNotSet(new Date());

        Try<TaskInfo, String> result = isAllowedAtLeastOnce(bgTask);
        if (result.isFailure()) {
            return result;
        } else if (checkReadyToExecute(bgTask).isFailure()) {
            return schedule(bgTask);
        } else {
            return schedule(bgTask).map((t) -> execute(bgTask).map(Try::success, Try::fail), Try::fail);
        }
    }

    public static void stopSchedulingNewTasks(boolean stopSchedulingNewTasks) {
        SequentialTaskManager.holdPendingQueue = stopSchedulingNewTasks;
    }

    public static void cancelRunningTasks() {

        Log.i(TAG, "Terminating running tasks...");
        //prevent all waiting tasks from starting
        holdPendingQueue = true;
        outstandingTasks.clear();

        if (runningTasks != null) {
            for (String runningTaskName : runningTasks.keySet()) {
                Log.i(TAG, format("Terminating task %s", runningTaskName));
                runningTasks.get(runningTaskName).cancel(true);
            }

            runningTasks.clear();
        }
    }

    public static void onViewInitialization(StreetsAbstractView initializedView, TaskStatus newStatus) {
        switch (newStatus) {
            case COMPLETED: {
                //initiate all tasks that were held up by the now initialized view
                String initializedViewName = initializedView.getClassName();

                Set<String> tasksWithDependencies = viewDependencyList.keySet();

                for (String awaitingTask : tasksWithDependencies) {
                    TaskInfo awaitingTaskInfo = viewDependencyList.get(awaitingTask);
                    if (awaitingTaskInfo.getViewDependencies().contains(initializedView.getClass())) {
                        awaitingTaskInfo.getViewDependencies().remove(initializedView.getClass());
                        Log.i(TAG, format("View dependency %s resolved for awaiting task %s. Attempting to schedule.", initializedViewName, awaitingTask));
                        if (runWhenAvailable(viewDependencyList.get(awaitingTask)).isSuccess()) {
                            StreetsCommon.showSnackBar(TAG, "Starting task " + awaitingTask, Snackbar.LENGTH_SHORT);
                            Log.i(TAG, format("Scheduled previously awaiting job %s.", awaitingTask));
                            viewDependencyList.remove(awaitingTask);
                        }
                    }
                }
                break;
            }
        }
    }

    public static void onTaskUpdate(TaskInfo asyncTask, TaskStatus newStatus) {
        switch (newStatus) {
            case CANCELLED: {
                String cancelledTask = asyncTask.getClassName();
                runningTasks.remove(cancelledTask);
                if (!asyncTask.allowsOnlyOnce()) {
                    getBackgroundExecutionTask(asyncTask.getClass());
                    return;
                }

                Set<String> tasksWithDependencies = processDependencyList.keySet();
                for (String awaitingTask : tasksWithDependencies) {
                    TaskInfo awaitingTaskInfo = processDependencyList.get(awaitingTask);
                    if (awaitingTaskInfo.getProcessDependencies().contains(cancelledTask)) {
                        Log.w(TAG, format("Dependency %s cannot be resolved for awaiting task %s because " +
                            "the task was cancelled and permits only a single run", cancelledTask, awaitingTask));
                    }
                }
                break;
            }
            case COMPLETED: {
                //initiate all tasks that were held up by the now completed task
                String completedTask = asyncTask.getClassName();
                StreetsCommon.showSnackBar(TAG, "Background task " + completedTask + " completed", Snackbar.LENGTH_SHORT);
                completedTasks.put(completedTask, runningTasks.get(completedTask));
                runningTasks.remove(completedTask);

                Set<String> tasksWithDependencies = processDependencyList.keySet();
                ArrayList<String> resolvedDependencies = new ArrayList<>();
                for (String awaitingTask : tasksWithDependencies) {
                    TaskInfo awaitingTaskInfo = processDependencyList.get(awaitingTask);
                    if (awaitingTaskInfo.getProcessDependencies().contains(completedTask)) {
                        awaitingTaskInfo.getProcessDependencies().remove(completedTask);
                        Log.i(TAG, format("Dependency %s resolved for awaiting task %s. Attempting to schedule.", completedTask, awaitingTask));
                        if (checkReadyToExecute(outstandingTasks.get(awaitingTask)).isSuccess()) {
                            Log.i(TAG, format("Ready to start %s.", awaitingTask));
                            resolvedDependencies.add(awaitingTask);
                        }
                    }
                }

                for (String resolvedDependency : resolvedDependencies) {
                    Log.i(TAG, format("Removing resolved dependency %s.", resolvedDependency));
                    processDependencyList.remove(resolvedDependency);
                    StreetsCommon.showSnackBar(TAG, "Starting task " + resolvedDependency, Snackbar.LENGTH_SHORT);
                    execute(outstandingTasks.get(resolvedDependency));
                }

                if (resolvedDependencies.size() > 0) {
                    return;
                }

                for (String outstandingTask : outstandingTasks.keySet()) {
                    if (checkReadyToExecute(outstandingTasks.get(outstandingTask)).isSuccess()) {
                        Log.i(TAG, format("Scheduled previously awaiting job %s.", outstandingTasks.get(outstandingTask)));
                        execute(outstandingTasks.get(outstandingTask));
                    }
                }
                break;
            }
        }
    }
}
