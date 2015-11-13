package net.blaklizt.streets.android.common;

import android.content.Context;
import android.os.Build;
import android.provider.Settings;
import android.speech.tts.TextToSpeech;
import android.support.design.widget.Snackbar;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import net.blaklizt.streets.android.activity.Startup;
import net.blaklizt.streets.android.common.utils.SecurityContext;
import net.blaklizt.streets.android.common.utils.Validator;
import net.blaklizt.streets.android.listener.PreferenceUpdateDialogueListener;
import net.blaklizt.streets.android.persistence.StreetsDBHelper;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

/**
 * Created with IntelliJ IDEA.
 * User: photon
 * Date: 2015/06/12
 * Time: 8:26 AM
 */
public class StreetsCommon
{
	//Tag all logs starting with the following tag
	private static final String TAG = "Streets";

	//Symbiosis channel
	public static final String CHANNEL = "ANDROID";

	//Application context
	private Context context = null;

	//Application user
	private SymbiosisUser symbiosisUser = null;
	private String imei = null;
	private String imsi = null;

	//Streets DB classes
	protected static StreetsDBHelper streetsDBHelper = null;

	//Streets Text To Speech engine
	protected static TextToSpeech ttsEngine = null;

	//static class reference
	private static StreetsCommon streetsCommon = null;

	//user preferences
	private static HashMap<String, USER_PREFERENCE> userPreferenceValues = new HashMap<>();

	public static StreetsCommon getInstance(Context context)
	{
		if (streetsCommon == null) {
			streetsCommon = new StreetsCommon(context);
            Log.i(TAG, "Created new instance of StreetsCommon");
		}
		return streetsCommon;
	}

	private StreetsCommon(Context context)
	{
		this.context = context;

        streetsCommon = this;

		//initialize DB
		getStreetsDBHelper();

		//initialize user preferences
		getUserPreferenceValues();

	}

	public static String getTag(Class streetsClass) { return TAG + "_" + streetsClass.getSimpleName(); }

	public StreetsDBHelper getStreetsDBHelper() {
		if (streetsDBHelper == null)
		{
			Log.i(TAG, "Initializing Streets database");
			streetsDBHelper = new StreetsDBHelper(context);
		}
		return streetsDBHelper;
	}

	public TextToSpeech getTextToSpeech()
	{
		try {
			if (ttsEngine == null) {
				Log.i(TAG, "Initializing text to speech engine");
				ttsEngine = new TextToSpeech(context, status -> {
                    Log.i(TAG, "Initialized text to speech engine");
                    ttsEngine.setLanguage(Locale.US);
                });
			} return ttsEngine;
		} catch (Exception ex) {
			ex.printStackTrace();
			Log.e(TAG, "Failed to initialize ttsEngine", ex);
			showToast(TAG, "Failed to initialize text to speech!" + ex.getMessage(), Toast.LENGTH_LONG);
            new PreferenceUpdateDialogueListener(context,
                "Speech failed to start. Would you like to turn off Text To Speech permanently?",
                USER_PREFERENCE.ENABLE_TTS, "Disable", "Cancel").show();
            return null;
		}
	}

    public void speak(final String speechText)
    {
        Log.i(TAG, "Speaking text: " + speechText);
        String ttsEnabled = getUserPreferenceValue(USER_PREFERENCE.ENABLE_TTS);
        if (Validator.isNullOrEmpty(ttsEnabled) && ttsEnabled.equals("1")) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getTextToSpeech().speak(speechText, TextToSpeech.QUEUE_FLUSH, null, String.valueOf(new Date().getTime()));
            } else {
                getTextToSpeech().speak(speechText, TextToSpeech.QUEUE_FLUSH, null);
            }
        }
    }

    public static void showSnackBar(final String TAG, final String text, final int duration) {

        if      (duration == Snackbar.LENGTH_LONG)  { Log.w(TAG, text); }
        else if (duration == Snackbar.LENGTH_SHORT) { Log.i(TAG, text); }

        if (Startup.getInstance() != null && Startup.getInstance().getCurrentFocus() != null) {
            Startup.getInstance().runOnUiThread(() ->
                    Snackbar.make(Startup.getInstance().getCurrentFocus(), text, duration).show());
        }
    }

    public static void showToast(final String TAG, final String text, final int duration) {

        if      (duration == Toast.LENGTH_LONG)  { Log.w(TAG, text); }
        else if (duration == Toast.LENGTH_SHORT) { Log.i(TAG, text); }

        if (Startup.getInstance() != null && Startup.getInstance().getCurrentFocus() != null) {
            Startup.getInstance().runOnUiThread(() ->
                    Toast.makeText(Startup.getInstance().getApplicationContext(), text, duration).show());
        }
    }

    public void endApplication()
	{
		try { //shutdown common classes
			Log.i(TAG, "Terminating common classes.");
			if (streetsDBHelper != null) {
				streetsDBHelper.close();
			}
			if (ttsEngine != null) {
				ttsEngine.shutdown();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			Log.e(TAG, "Failed to shutdown common classes cleanly: " + ex.getMessage(), ex);
		} finally {
            streetsDBHelper = null;
            ttsEngine = null;
        }
    }

    public String getUserPreferenceValue(USER_PREFERENCE preference) {
        if (!getUserPreferenceValues().containsKey(preference.name())) {
            Log.i(TAG, "Preference " + preference.name() + " does not exist");
            writeEventLog(TASK_TYPE.USER_PREF_READ, STATUS_CODES.GENERAL_ERROR,
                "Preference " + preference.name() + " does not exist in the database.\n\n" +
                "Please update your application to avoid data corruption, crashes & unexpected behaviour");
        }
		Log.i(TAG, preference.name() + " = " + getUserPreferenceValues().get(preference.name()).pref_value);
        return getUserPreferenceValues().get(preference.name()).pref_value;
    }

	public void initUserPreferenceData() {
		Log.i(TAG, "Loading user preferences from DB");
        try { userPreferenceValues = getStreetsDBHelper().getUserPreferences(); }
        catch (Exception ex) {
            ex.printStackTrace();
            SecurityContext.handleApplicationError(SecurityContext.ERROR_SEVERITY.SEVERE,
                "User preferences could not be read from the database.\n\n" +
                "Please update your application to the latest version to avoid potential data corruption.",
                ex.getStackTrace(), TASK_TYPE.USER_PREF_READ);
        }

        if (userPreferenceValues == null || userPreferenceValues.isEmpty()) {
            SecurityContext.handleApplicationError(SecurityContext.ERROR_SEVERITY.SEVERE,
                "User preferences could not be read from the database.\n\n" +
                "Please update your application to the latest version to avoid potential data corruption.",
                "getUserPreferences returned no results", TASK_TYPE.USER_PREF_READ);
        }
	}

    public HashMap<String, USER_PREFERENCE> getUserPreferenceValues() {
        if (userPreferenceValues.size() == 0) { initUserPreferenceData(); }
        return userPreferenceValues;
    }

	public void setUserPreference(USER_PREFERENCE preference, String value) {
        try {
            Log.i(TAG, "Setting " + preference + " = " + value);
            String description = getUserPreferenceValues().get(preference.name()).pref_description;
            String data_type = getUserPreferenceValues().get(preference.name()).pref_data_type;
            getUserPreferenceValues().remove(preference.name());
            preference.pref_value = value;
            getUserPreferenceValues().put(preference.name(), preference);
            getStreetsDBHelper().setUserPreference(preference, value, description, data_type);
		} catch (Exception ex) {
            SecurityContext.handleApplicationError(SecurityContext.ERROR_SEVERITY.SEVERE,
                "User preferences could not be updated.\n\n" +
                "Please update your application to the latest version to avoid potential data corruption.",
                ex.getStackTrace(), TASK_TYPE.USER_PREF_UPDATE);
        }
	}

	public ArrayList<String> getOutstandingPermissions() {
		return getStreetsDBHelper().getOutstandingPermissions();
	}

	public void addOutstandingPermission(String permission) {
		getStreetsDBHelper().addOutstandingPermission(permission);
	}

	public void removeOutstandingPermission(String permission) {
		getStreetsDBHelper().removeOutstandingPermission(permission);
	}

    public SymbiosisUser getSymbiosisUser() {
        if (symbiosisUser == null) {
            symbiosisUser = getStreetsDBHelper().getCurrentUser();
            Log.i(TAG, "Got current symbiosis user details for user id: " + symbiosisUser.symbiosisUserID);
            Log.i(TAG, "username -> " + symbiosisUser.username);
            Log.i(TAG, "imei     -> " + symbiosisUser.imei);
            Log.i(TAG, "imsi     -> " + symbiosisUser.imsi);
        }
        return symbiosisUser;
    }

    public void saveUserDetails() {
        getStreetsDBHelper().saveCurrentUser(symbiosisUser);
    }

    public String getIMEI() {
        if (Validator.isNullOrEmpty(this.imei)) {
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if (telephonyManager != null && !Validator.isNullOrEmpty(this.imei = telephonyManager.getDeviceId())) {
                Log.i(TAG, "Service 'TelephonyManager' returned IMEI: " + this.imei);
            } else {
                this.imei = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
                Log.i(TAG, "TelephonyManager was null. Service 'Settings.Secure' returned IMEI: " + this.imei);
            }
        }
        return this.imei;
    }

    public String getIMSI() {
        if (Validator.isNullOrEmpty(this.imsi)) {
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if (telephonyManager != null && !Validator.isNullOrEmpty(this.imsi = telephonyManager.getSimSerialNumber())) {
                Log.i(TAG, "Service 'TelephonyManager' returned IMSI: " + this.imsi);
            }
        }
        return this.imsi;
    }

    public void setUserID(Long userID) {
        getSymbiosisUser().symbiosisUserID = userID;
        saveUserDetails();
    }

    public void writeEventLog(TASK_TYPE task_type, STATUS_CODES status, String description) {
        getStreetsDBHelper().logApplicationEvent(task_type, status, description);
    }

    public void logTaskEvent(TaskInfo taskInfo, STATUS_CODES status) {
        getStreetsDBHelper().logTaskEvent(taskInfo, status);
    }
}
