package net.blaklizt.streets.android.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.speech.tts.TextToSpeech;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;
import com.google.android.gms.maps.GoogleMap;
import net.blaklizt.streets.android.R.drawable;
import net.blaklizt.streets.android.activity.helpers.*;
import net.blaklizt.streets.android.common.StreetsCommon;
import net.blaklizt.streets.android.common.TaskInfo;
import net.blaklizt.streets.android.common.enumeration.STATUS_CODES;
import net.blaklizt.streets.android.common.enumeration.USER_PREFERENCE;
import net.blaklizt.streets.android.common.utils.Optional;
import net.blaklizt.streets.android.common.utils.SecurityContext;
import net.blaklizt.streets.android.listener.EnableGPSDialogueListener;
import net.blaklizt.streets.android.listener.PreferenceUpdateDialogueListener;
import net.blaklizt.streets.android.location.places.Place;
import net.blaklizt.streets.android.persistence.StreetsDBHelper;
import net.blaklizt.streets.android.sidemenu.model.SlideMenuItem;

import java.util.*;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;
import static android.location.LocationManager.GPS_PROVIDER;
import static android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS;
import static net.blaklizt.streets.android.activity.helpers.SequentialTaskManager.runWhenAvailable;
import static net.blaklizt.streets.android.common.StreetsCommon.getTag;
import static net.blaklizt.streets.android.common.StreetsCommon.showToast;
import static net.blaklizt.streets.android.common.enumeration.TASK_TYPE.BG_PERMISSIONS_TASK;
import static net.blaklizt.streets.android.common.enumeration.TASK_TYPE.USER_PREF_READ;
import static net.blaklizt.streets.android.common.enumeration.USER_PREFERENCE.*;
import static net.blaklizt.streets.android.common.utils.SecurityContext.handleApplicationError;

/**
 * Created by tsungai.kaviya on 2015-11-12.
 */
public class AppContext {

    /* =============== SYSTEM MANAGED CONTEXT INFORMATION =============== */
    private static Context applicationContext;
    private static final String TAG = getTag(AppContext.class);
    private static AppContext streetsApplicationContext;
    private static StreetsDBHelper streetsDBHelper = null;
    private static StreetsCommon streetsCommon = null;

    /*  If class has custom code to execute when application is terminating. onDestroy it should be registered in this list.
     *
     *  Call registerOnDestroyHandler(@Class implements StreetsInterfaceView) to add class to this list
     *
     *  When application is terminating, onTermination() is called for all classes in this list. */
    private static final LinkedList<StreetsInterfaceView> SHUTDOWN_CALLBACK_QUEUE = new LinkedList<>();

    /* List of background tasks that can execute on the application. */
    private static final LinkedHashMap<Class<? extends TaskInfo>, TaskInfo> TASK_EXECUTION_INFO = new LinkedHashMap<>();

    /* List of fragment views created for this application. */
    private static final LinkedHashMap<Class<? extends StreetsAbstractView>, StreetsAbstractView> STREETS_FRAGMENTS = new LinkedHashMap<>();

    /* Mapping of fragment views to their menus */
    private static final LinkedHashMap<Class<? extends StreetsAbstractView>, SlideMenuItem> FRAGMENT_MENU_REGISTRY = new LinkedHashMap<>();

    /* Mapping of menus to their fragments */
    private static final LinkedHashMap<String, Class<? extends StreetsAbstractView>> MENU_FRAGMENT_REGISTRY = new LinkedHashMap<>();

    public static LinkedHashMap<Class<? extends StreetsAbstractView>, StreetsAbstractView> getStreetsFragments() {
        return STREETS_FRAGMENTS;
    }

    public static LinkedHashMap<Class<? extends StreetsAbstractView>, SlideMenuItem> getFragmentMenuRegistry() {
        return FRAGMENT_MENU_REGISTRY;
    }

    public static LinkedHashMap<String, Class<? extends StreetsAbstractView>> getMenuFragmentRegistry() {
        return MENU_FRAGMENT_REGISTRY;
    }

    /* List of configured user preference options and values */
    private static HashMap<String, USER_PREFERENCE> userPreferenceValues = new HashMap<>();

    public static HashMap<String, USER_PREFERENCE> getUserPreferenceValues() {
        if (userPreferenceValues.size() == 0) { initUserPreferenceData(); }
        return userPreferenceValues;
    }

    public static void initUserPreferenceData() {
        Log.i(TAG, "Loading user preferences from DB");
        try { userPreferenceValues = getStreetsDBHelper().getUserPreferences(); }
        catch (Exception ex) {
            ex.printStackTrace();
            handleApplicationError(SecurityContext.EVENT_LEVEL.ERROR,
                    "User preferences could not be read from the database.\n\n" +
                            "Please update your application to the latest version to avoid potential data corruption.",
                    ex.getStackTrace(), USER_PREF_READ);
        }

        if (userPreferenceValues == null || userPreferenceValues.isEmpty()) {
            handleApplicationError(SecurityContext.EVENT_LEVEL.ERROR,
                    "User preferences could not be read from the database.\n\n" +
                            "Please update your application to the latest version to avoid potential data corruption.",
                    "getUserPreferences returned no results", USER_PREF_READ);
        }
    }


    /* =================== SHARED CONTEXT INFORMATION =================== */
    private GoogleMap googleMap;
    private Location currentLocation;
    private TextToSpeech ttsEngine;
    private LocationManager locationManager;
    private ArrayList<Place> nearbyPlaces;
    private HashMap<String, Place> markerPlaces = new HashMap<>();
    //location provider data
    public final static String PROVIDER_CHEAPEST = "passive";
    public final static Integer MINIMUM_REFRESH_TIME = 600000;
    public final static Integer SUBURB_REFRESH_MINS = 3;
    public final static Integer MINIMUM_REFRESH_DISTANCE = 10;
    public String defaultProvider = PROVIDER_CHEAPEST;        //default working provider

    public static boolean firstLocationUpdate = true;
    public LocationProvider currentProvider;

    public static boolean isLocationPermissionsGranted() {
        if (!locationPermissionsGranted) {
            locationPermissionsGranted = checkAndRequestPermissions();
        }
        return locationPermissionsGranted;
    }

    public static void setLocationPermissionsGranted(final boolean locationPermissionsGranted) {
        AppContext.locationPermissionsGranted = locationPermissionsGranted;
    }

    private static boolean locationPermissionsGranted = false;
    public static final int PERMISSION_LOCATION_INFO = 6767;

    /* Initialize Streets Items */
    static {
        Log.i(TAG, "Initializing task execution information.");
        if (TASK_EXECUTION_INFO.isEmpty()) {
            TASK_EXECUTION_INFO.put(GoogleMapTask.class, null);
            TASK_EXECUTION_INFO.put(LocationSettingsTask.class, null);
            TASK_EXECUTION_INFO.put(LocationUpdateTask.class, null);
            TASK_EXECUTION_INFO.put(PlacesTask.class, null);
            TASK_EXECUTION_INFO.put(CurrentViewLocationTask.class, null);
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

    public static final Class<? extends StreetsAbstractView> DEFAULT_FRAGMENT_VIEW = MapLayout.class;


    public static final String MNU_CLOSE = "Close";
    private static final String MNU_THA_STREETZ = "Tha Streetz";
    private static final String MNU_NAVIGATION = "Discover & Connect";
    private static final String MNU_PROFILE = "Profile";
//    private static final String MNU_CHAT = "Chat";
//    private static final String MNU_FRIENDS = "Friends";
//    private static final String MNU_INVITE_REFER = "Invite or Refer";
//    private static final String MNU_CONTACT_US = "Contact Us";

    static {
        Log.i(TAG, "Initializing fragment menu information.");
        if (FRAGMENT_MENU_REGISTRY.isEmpty()) {
            MENU_FRAGMENT_REGISTRY.put(MNU_CLOSE, null);
            MENU_FRAGMENT_REGISTRY.put(MNU_THA_STREETZ, MapLayout.class);
            MENU_FRAGMENT_REGISTRY.put(MNU_NAVIGATION, NavigationLayout.class);
            MENU_FRAGMENT_REGISTRY.put(MNU_PROFILE, ProfileLayout.class);

            FRAGMENT_MENU_REGISTRY.put(null,                    new SlideMenuItem(MNU_CLOSE,        drawable.res971));
            FRAGMENT_MENU_REGISTRY.put(MapLayout.class,         new SlideMenuItem(MNU_THA_STREETZ,  drawable.res605));
            FRAGMENT_MENU_REGISTRY.put(NavigationLayout.class,  new SlideMenuItem(MNU_NAVIGATION,   drawable.res1000));
            FRAGMENT_MENU_REGISTRY.put(ProfileLayout.class,     new SlideMenuItem(MNU_PROFILE,      drawable.res884));
//            MENU_VIEW_ITEMS.put(MNU_CHAT,            new SlideMenuItem(MNU_CHAT,         R.drawable.chat));
//            MENU_VIEW_ITEMS.put(MNU_FRIENDS,         new SlideMenuItem(MNU_FRIENDS,      R.drawable.friends_group));
//            MENU_VIEW_ITEMS.put(MNU_INVITE_REFER,    new SlideMenuItem(MNU_INVITE_REFER, R.drawable.plus));
//            MENU_VIEW_ITEMS.put(MNU_CONTACT_US,      new SlideMenuItem(MNU_CONTACT_US,   R.drawable.mail));
        }
    }

    private AppContext(final Context applicationContext) {
        if (AppContext.streetsApplicationContext == null) {
            AppContext.applicationContext = applicationContext;
            AppContext.streetsApplicationContext = this;
            getStreetsDBHelper();
            getUserPreferenceValues();
        }
    }

    public static AppContext getAppContextInstance(final Context applicationContext) {
        if (AppContext.streetsApplicationContext == null) {
            AppContext.streetsApplicationContext = new AppContext(applicationContext);
        }
        return AppContext.streetsApplicationContext;
    }

    public static AppContext getAppContextInstance() { return streetsApplicationContext; }

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
        if (streetsDBHelper == null)
        {
            Log.i(TAG, "Initializing Streets database");
            streetsDBHelper = new StreetsDBHelper(applicationContext);
        }
        return streetsDBHelper;
    }

    public static void setIsFirstLocationUpdate(final boolean isFirstLocationUpdate) {
        AppContext.firstLocationUpdate = isFirstLocationUpdate;
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
            showToast(Startup.getInstance(), TAG, "Failed to initialize text to speech!" + ex.getMessage(), Toast.LENGTH_LONG);
            new PreferenceUpdateDialogueListener(applicationContext,
                    "Speech failed to start. Would you like to turn off Text To Speech permanently?",
                    ENABLE_TTS, "Disable", "Cancel").show();
            return null;
        }
    }

    public String getDefaultProvider() {
        return defaultProvider;
    }

    public void setDefaultProvider(final String defaultProvider) {
        this.defaultProvider = defaultProvider;
    }

    public static boolean isFirstLocationUpdate() {
        return firstLocationUpdate;
    }

    public LocationProvider getCurrentProvider() {
        return currentProvider;
    }


    public void setCurrentProvider(final LocationProvider currentProvider) {
        this.currentProvider = currentProvider;
    }

    public void useGPSAsCurrentProvider() {
        this.currentProvider = getLocationManager().getProvider(GPS_PROVIDER);
    }

    public void useDefaultAsCurrentProvider() {
        this.currentProvider = getLocationManager().getProvider(getDefaultProvider());
    }
    /* ============== GLOBALLY ACCESSIBLE CONTEXT FUNCTIONS ============== */

    public static TaskInfo getBackgroundExecutionTask(Class<? extends TaskInfo> task) {

        if (TASK_EXECUTION_INFO.get(task) != null && TASK_EXECUTION_INFO.get(task).getEndTime() != null) {
            Log.i(TAG, "Task has executed and completed. New task will be created.");
            TASK_EXECUTION_INFO.put(task, null);
        }

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
        if (STREETS_FRAGMENTS.containsKey(view) && STREETS_FRAGMENTS.get(view) == null) {
            try {
                Log.i(TAG, "Instantiating fragment view: " + view.getSimpleName());
                STREETS_FRAGMENTS.put(view, view.newInstance());
                Log.i(TAG, format("Setting menu %s for view %s ", STREETS_FRAGMENTS.get(view).getClassName(), view.getSimpleName()));
                STREETS_FRAGMENTS.get(view).prepareMenu(FRAGMENT_MENU_REGISTRY.get(view));
                STREETS_FRAGMENTS.get(view).setRetainInstance(true);
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

    private static boolean checkAndRequestPermissions() {

        Context context = getApplicationContext().getApplicationContext();
        if (ContextCompat.checkSelfPermission(context, ACCESS_COARSE_LOCATION) != PERMISSION_GRANTED) {
            Log.i(TAG, "Permission " + ACCESS_COARSE_LOCATION + " is not allowed.");
            getStreetsCommon().addOutstandingPermission(ACCESS_COARSE_LOCATION);
            locationPermissionsGranted = false;
        } else {
            Log.i(TAG, "Permission " + ACCESS_COARSE_LOCATION + " is allowed.");
            getStreetsCommon().removeOutstandingPermission(ACCESS_COARSE_LOCATION);
            locationPermissionsGranted = false;
        }

        //check fine location
        if (ContextCompat.checkSelfPermission(context, ACCESS_FINE_LOCATION) != PERMISSION_GRANTED) {
            Log.i(TAG, "Permission " + ACCESS_FINE_LOCATION + " is not allowed.");
            getStreetsCommon().addOutstandingPermission(ACCESS_FINE_LOCATION);
            locationPermissionsGranted = false;
        } else {
            Log.i(TAG, "Permission " + ACCESS_FINE_LOCATION + " is allowed.");
            getStreetsCommon().removeOutstandingPermission(ACCESS_FINE_LOCATION);
            locationPermissionsGranted = false;
        }

        ArrayList<String> outstandingPermissions = getStreetsCommon().getOutstandingPermissions();

        final MapLayout mapLayout = (MapLayout)getFragmentView(MapLayout.class);

        Log.i(TAG, "Outstanding permissions: " + outstandingPermissions.size());
        String logMessage = "";

        for (String permission : outstandingPermissions) {
            logMessage += " | " + permission;
        }

        if (outstandingPermissions.size() > 0 && getStreetsCommon().getUserPreferenceValue(REQUEST_GPS_PERMS).equals("1")) {
            Log.i(TAG, "Not enough permissions to do location updates. Requesting from user.");
            mapLayout.requestPermissions(outstandingPermissions.toArray(new String[outstandingPermissions.size()]), PERMISSION_LOCATION_INFO);
            locationPermissionsGranted = false;
            getStreetsCommon().writeEventLog(BG_PERMISSIONS_TASK, STATUS_CODES.GENERAL_ERROR,
                "User did not accept all permissions. %d outstanding permissions: " + logMessage);
        }
        else {
            locationPermissionsGranted = true;
            getStreetsCommon().writeEventLog(BG_PERMISSIONS_TASK, STATUS_CODES.SUCCESS, "All required location permissions available.");
        }

        checkEnableGPS();

        return locationPermissionsGranted;
    }

    public static void checkEnableGPS() {
        Log.i(TAG, "Checking GPS availability");
        LocationManager locationManager = getAppContextInstance().getLocationManager();
        if (!locationManager.isProviderEnabled(GPS_PROVIDER)) {
            Log.i(TAG, "GPS not enabled");
            if (getStreetsCommon().getUserPreferenceValue(AUTO_ENABLE_GPS).equals("1")) {
                Log.i(TAG, "User has granted auto enable privilege");
                Intent myIntent = new Intent(ACTION_LOCATION_SOURCE_SETTINGS);
                MenuLayout.getInstance().startActivity(myIntent);
            } else if (getStreetsCommon().getUserPreferenceValue(SUGGEST_GPS).equals("1")) {
                Log.i(TAG, "Must request perms from use");
                EnableGPSDialogueListener enableGpsListener = new EnableGPSDialogueListener(MenuLayout.getInstance());
                AlertDialog.Builder builder = new AlertDialog.Builder(MenuLayout.getInstance());
                builder.setMessage("Turn on GPS?")
                        .setMultiChoiceItems(
                                EnableGPSDialogueListener.getQuestionItems(),
                                EnableGPSDialogueListener.getCheckedItems(),
                                EnableGPSDialogueListener.EnableGPSOptionListener.getInstance())
                        .setPositiveButton("Yes", enableGpsListener)
                        .setNegativeButton("No", enableGpsListener).create().show();
            }
        }
    }

    public static void shutdown() {
        Log.i(TAG, "+++ SHUTDOWN +++");

        StreetsCommon.showSnackBar(MenuLayout.getInstance(), TAG, "[- Now leaving Tha Streetz -]\n ...Goodbye...", Snackbar.LENGTH_SHORT);

        Log.i(TAG, "Terminating running tasks...");
        SequentialTaskManager.stopSchedulingNewTasks(true);
        for (StreetsInterfaceView view : SHUTDOWN_CALLBACK_QUEUE) {
            view.onTermination();
        }

        SHUTDOWN_CALLBACK_QUEUE.clear();


        try { //shutdown common classes
            Log.i(TAG, "Terminating common classes.");
            if (getAppContextInstance().ttsEngine != null) {
                getAppContextInstance().ttsEngine.shutdown();
            }
            if (streetsDBHelper != null) {
                streetsDBHelper.close();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            Log.e(TAG, "Failed to shutdown common classes cleanly: " + ex.getMessage(), ex);
        }

        if (Startup.getInstance() != null) {
            Startup.getInstance().finish();
        }
    }

    /* =========== GLOBALLY ACCESSIBLE CONTEXT DATA FUNCTIONS =========== */

    public Optional<GoogleMap> getGoogleMap() {
        return Optional.ofNullable(googleMap);
    }

    public void setGoogleMap(final GoogleMap googleMap) {
        this.googleMap = googleMap;
    }

    public Optional<Location> getCurrentLocation() {
        return Optional.ofNullable(currentLocation);
    }

    public void setCurrentLocation(final Location currentLocation) {
        this.currentLocation = currentLocation;
	    runWhenAvailable(CurrentViewLocationTask.class);
    }

    public LocationManager getLocationManager() {

        if (locationManager == null) {
            Log.i(TAG, "Initializing location manager");
            //at activity start, if user has not disabled location stuff, request permissions.
            if (!locationPermissionsGranted &&
                (getStreetsCommon().getUserPreferenceValue(SUGGEST_GPS).equals("1") ||
                 getStreetsCommon().getUserPreferenceValue(AUTO_ENABLE_GPS).equals("1"))) {
                 getStreetsCommon().setUserPreference(REQUEST_GPS_PERMS, "1"); //reset preferences if permissions were updated
            }

            Log.i(TAG, "Getting system location service");
            // Getting LocationManager object from System Service LOCATION_SERVICE
            locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);

            if (isLocationPermissionsGranted()) {
                try { locationManager.addGpsStatusListener(MenuLayout.getInstance()); }
                catch (SecurityException ex) {
                    ex.printStackTrace();  /* try catch done to please compiler. cant reach here because we check permissions beforehand */
                    Log.e(TAG, "Failed to setup location update requests! " + ex.getMessage(), ex);
                    throw new RuntimeException(ex);
                }
            }

        }
        return locationManager;
    }

    public ArrayList<Place> getNearbyPlaces() {
        return nearbyPlaces;
    }

    public void setNearbyPlaces(final ArrayList<Place> nearbyPlaces) {
        this.nearbyPlaces = nearbyPlaces;
    }

    public HashMap<String, Place> getMarkerPlaces() {
        return markerPlaces;
    }
}
