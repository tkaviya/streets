package net.blaklizt.streets.android.activity;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.speech.tts.TextToSpeech;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;

import net.blaklizt.streets.android.activity.helpers.GoogleMapTask;
import net.blaklizt.streets.android.activity.helpers.LocationUpdateTask;
import net.blaklizt.streets.android.activity.helpers.PlacesTask;
import net.blaklizt.streets.android.activity.helpers.SequentialTaskManager;
import net.blaklizt.streets.android.activity.helpers.StreetsAbstractView;
import net.blaklizt.streets.android.activity.helpers.StreetsInterfaceView;
import net.blaklizt.streets.android.common.STATUS_CODES;
import net.blaklizt.streets.android.common.StreetsCommon;
import net.blaklizt.streets.android.common.TASK_TYPE;
import net.blaklizt.streets.android.common.TaskInfo;
import net.blaklizt.streets.android.common.USER_PREFERENCE;
import net.blaklizt.streets.android.common.utils.Optional;
import net.blaklizt.streets.android.common.utils.SecurityContext;
import net.blaklizt.streets.android.listener.PreferenceUpdateDialogueListener;
import net.blaklizt.streets.android.persistence.StreetsDBHelper;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Locale;

import static net.blaklizt.streets.android.common.TASK_TYPE.USER_PREF_READ;
import static net.blaklizt.streets.android.common.utils.SecurityContext.handleApplicationError;

/**
 * Created by tsungai.kaviya on 2015-11-12.
 */
public class AppContext {

    /* =============== SYSTEM MANAGED CONTEXT INFORMATION =============== */

    private static Context applicationContext;

    private static final String TAG = StreetsCommon.getTag(AppContext.class);

    private static AppContext streetsApplicationContext = null;
    private static StreetsDBHelper streetsDBHelper = null;
    private static StreetsCommon streetsCommon = null;

    /*  If class has custom code to execute when application is terminating. onDestroy it should be registered in this list.
     *
     *  Call registerOnDestroyHandler(@Class implements StreetsInterfaceView) to add class to this list
     *
     *  When application is terminating, onTermination() is called for all classes in this list. */
    private static final LinkedList<StreetsInterfaceView> SHUTDOWN_CALLBACK_QUEUE = new LinkedList<>();
    /* List of background tasks that can execute on the application. */
    private static final HashMap<Class<? extends TaskInfo>, TaskInfo> TASK_EXECUTION_INFO = new HashMap<>();
    /* List of fragment views created for this application. */
    private static final HashMap<Class<? extends StreetsAbstractView>, StreetsAbstractView> STREETS_FRAGMENTS = new HashMap<>();
    /* List of configured user preference options and values */
    private static HashMap<String, USER_PREFERENCE> userPreferenceValues = new HashMap<>();

    public static HashMap<Class<? extends TaskInfo>, TaskInfo> getTaskExecutionInfo() {
        return TASK_EXECUTION_INFO;
    }

    public static HashMap<Class<? extends StreetsAbstractView>, StreetsAbstractView> getStreetsFragments() {
        return STREETS_FRAGMENTS;
    }

    public static HashMap<String, USER_PREFERENCE> getUserPreferenceValues() {
        if (AppContext.userPreferenceValues.size() == 0) { initUserPreferenceData(); }
        return AppContext.userPreferenceValues;
    }

    public static void initUserPreferenceData() {
        Log.i(TAG, "Loading user preferences from DB");
        try { userPreferenceValues = getStreetsDBHelper().getUserPreferences(); }
        catch (Exception ex) {
            ex.printStackTrace();
            handleApplicationError(SecurityContext.ERROR_SEVERITY.SEVERE,
                    "User preferences could not be read from the database.\n\n" +
                            "Please update your application to the latest version to avoid potential data corruption.",
                    ex.getStackTrace(), USER_PREF_READ);
        }

        if (AppContext.userPreferenceValues == null || AppContext.userPreferenceValues.isEmpty()) {
            handleApplicationError(SecurityContext.ERROR_SEVERITY.SEVERE,
                    "User preferences could not be read from the database.\n\n" +
                            "Please update your application to the latest version to avoid potential data corruption.",
                    "getUserPreferences returned no results", USER_PREF_READ);
        }
    }


    /* =================== SHARED CONTEXT INFORMATION =================== */

    private GoogleMap googleMap;
    private Location currentLocation = null;
    private TextToSpeech ttsEngine = null;

    private LocationManager locationManager = null;

    /* Initialize Streets Items */
    static {
        Log.i(TAG, "Initializing task execution information.");
        if (TASK_EXECUTION_INFO.isEmpty()) {
            TASK_EXECUTION_INFO.put(GoogleMapTask.class, null);
            TASK_EXECUTION_INFO.put(LocationUpdateTask.class, null);
            TASK_EXECUTION_INFO.put(PlacesTask.class, null);
        }
    }

    static {
        Log.i(TAG, "Initializing fragment view information.");
        if (STREETS_FRAGMENTS.isEmpty()) {
            STREETS_FRAGMENTS.put(MapLayout.class, null);
            STREETS_FRAGMENTS.put(NavigationLayout.class, null);
            STREETS_FRAGMENTS.put(ProfileLayout.class, null);
        }
    }

    private AppContext(Context applicationContext) {
        if (streetsApplicationContext == null) {
            AppContext.applicationContext = applicationContext;
            AppContext.streetsApplicationContext = this;
            getStreetsDBHelper();
            getUserPreferenceValues();
        }
    }

    public static AppContext getInstance(Context applicationContext) {
        if (streetsApplicationContext == null) {
            streetsApplicationContext = new AppContext(applicationContext);
        }
        return streetsApplicationContext;
    }

    public static AppContext getInstance() { return streetsApplicationContext; }

    public static Context getApplicationContext() {
        return applicationContext;
    }

    public static StreetsCommon getStreetsCommon() {
        if (streetsCommon == null) {
            streetsCommon = StreetsCommon.getInstance(applicationContext);
        }
        return streetsCommon;
    }

    public static StreetsDBHelper getStreetsDBHelper() {
        if (AppContext.streetsDBHelper == null)
        {
            Log.i(TAG, "Initializing Streets database");
            AppContext.streetsDBHelper = new StreetsDBHelper(applicationContext);
        }
        return AppContext.streetsDBHelper;
    }

    public TextToSpeech getTextToSpeech()
    {
        try {
            if (ttsEngine == null) {
                Log.i(TAG, "Initializing text to speech engine");
                ttsEngine = new TextToSpeech(applicationContext, status -> {
                    Log.i(TAG, "Initialized text to speech engine");
                    ttsEngine.setLanguage(Locale.US);
                });
            } return ttsEngine;
        } catch (Exception ex) {
            ex.printStackTrace();
            Log.e(TAG, "Failed to initialize ttsEngine", ex);
            StreetsCommon.showToast(TAG, "Failed to initialize text to speech!" + ex.getMessage(), Toast.LENGTH_LONG);
            new PreferenceUpdateDialogueListener(applicationContext,
                    "Speech failed to start. Would you like to turn off Text To Speech permanently?",
                    USER_PREFERENCE.ENABLE_TTS, "Disable", "Cancel").show();
            return null;
        }
    }

    /* ============== GLOBALLY ACCESSIBLE CONTEXT FUNCTIONS ============== */

    public static TaskInfo getBackgroundExecutionTask(Class<? extends TaskInfo> task) {
        if (TASK_EXECUTION_INFO.get(task) == null) {
            try {
                Log.i(TAG, "Instantiating background task: " + task.getSimpleName());
                TASK_EXECUTION_INFO.put(task, task.newInstance());
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
        return TASK_EXECUTION_INFO.get(task);
    }

    public static StreetsAbstractView getFragmentView(Class<? extends StreetsAbstractView> view) {
        if (STREETS_FRAGMENTS.get(view) == null) {
            try {
                Log.i(TAG, "Instantiating fragment view: " + view.getSimpleName());
                STREETS_FRAGMENTS.put(view, view.newInstance());
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
        return STREETS_FRAGMENTS.get(view);
    }

    public static void registerOnDestroyHandler(StreetsInterfaceView onDestroyHandler) {
        if (!SHUTDOWN_CALLBACK_QUEUE.contains(onDestroyHandler)) {
            SHUTDOWN_CALLBACK_QUEUE.add(onDestroyHandler);
        }
    }

    public static void shutdown() {
        Log.i(TAG, "+++ ON DESTROY +++");

        Log.i(TAG, "Terminating background tasks...");
        SequentialTaskManager.cancelRunningTasks();

        StreetsCommon.showSnackBar(TAG, "[- Now leaving Tha Streetz -]\n ...Goodbye...", Snackbar.LENGTH_SHORT);

        Iterator<StreetsInterfaceView> streetsInterfaceViewIterator = SHUTDOWN_CALLBACK_QUEUE.descendingIterator();

        while (streetsInterfaceViewIterator.hasNext()) {
            streetsInterfaceViewIterator.next().onTermination();
        }

        SHUTDOWN_CALLBACK_QUEUE.clear();

        AppContext.getStreetsCommon().writeEventLog(TASK_TYPE.SYS_TASK, STATUS_CODES.SUCCESS, "Shutdown completed cleanly");

        try { //shutdown common classes
            Log.i(TAG, "Terminating common classes.");
            if (getStreetsDBHelper() != null) {
                getStreetsDBHelper().close();
            }
            if (AppContext.getInstance().getTextToSpeech() != null) {
                AppContext.getInstance().getTextToSpeech().shutdown();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            Log.e(TAG, "Failed to shutdown common classes cleanly: " + ex.getMessage(), ex);
        }
    }

    /* =========== GLOBALLY ACCESSIBLE CONTEXT DATA FUNCTIONS =========== */

    public Optional<GoogleMap> getGoogleMap() {
        return Optional.ofNullable(googleMap);
    }

    public void setGoogleMap(GoogleMap googleMap) {
        this.googleMap = googleMap;
    }

    public Optional<Location> getCurrentLocation() {
        return Optional.ofNullable(currentLocation);
    }

    public void setCurrentLocation(Location currentLocation) {
        this.currentLocation = currentLocation;
    }

    public Optional<LocationManager> getLocationManager() {
        return Optional.ofNullable(locationManager);
    }

    public void setLocationManager(LocationManager locationManager) {
        this.locationManager = locationManager;
    }

}
