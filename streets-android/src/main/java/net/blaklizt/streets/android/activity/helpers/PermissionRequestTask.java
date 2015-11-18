//package net.blaklizt.streets.android.activity.helpers;
//
//import android.Manifest;
//import android.app.AlertDialog;
//import android.content.Context;
//import android.content.pm.PackageManager;
//import android.location.LocationManager;
//import android.support.v4.content.ContextCompat;
//import android.util.Log;
//
//import net.blaklizt.streets.android.activity.AppContext;
//import net.blaklizt.streets.android.activity.MapLayout;
//import net.blaklizt.streets.android.common.StreetsCommon;
//import net.blaklizt.streets.android.common.TASK_TYPE;
//import net.blaklizt.streets.android.common.USER_PREFERENCE;
//import net.blaklizt.streets.android.listener.EnableGPSDialogueListener;
//
//import java.util.ArrayList;
//
///**
// * Created by tsungai.kaviya on 2015-11-14.
// */
//public class PermissionRequestTask extends StreetsAbstractTask {
//
//    private static final String TAG = StreetsCommon.getTag(PermissionRequestTask.class);
//    private static boolean arePermissionsGranted = false;
//    private static final int PERMISSION_LOCATION_INFO = 6767;
//
//    static {
//        processDependencies = new ArrayList<>();
//        allowOnlyOnce = true;
//        allowMultiInstance = false;
//        taskType = TASK_TYPE.BG_PERMISSIONS_TASK;
//    }
//
//    @Override
//    protected Object doInBackground(Object[] params) {
//        if (!AppContext.getInstance().getLocationManager().isPresent()) {
//            Log.i(TAG, "Initializing location manager");
//            //at activity start, if user has not disabled location stuff, request permissions.
//            if (!arePermissionsGranted &&
//                    (AppContext.getStreetsCommon().getUserPreferenceValue(USER_PREFERENCE.SUGGEST_GPS).equals("1") ||
//                            AppContext.getStreetsCommon().getUserPreferenceValue(USER_PREFERENCE.AUTO_ENABLE_GPS).equals("1"))) {
//                AppContext.getStreetsCommon().setUserPreference(USER_PREFERENCE.REQUEST_GPS_PERMS, "1"); //reset preferences if permissions were updated
//            }
//        }
//
//        Log.i(TAG, "Getting system location service");
//        // Getting LocationManager object from System Service LOCATION_SERVICE
//        AppContext.getInstance().setLocationManager((LocationManager) AppContext.getApplicationContext().getSystemService(Context.LOCATION_SERVICE));
//        return null;
//    }
//
//}
