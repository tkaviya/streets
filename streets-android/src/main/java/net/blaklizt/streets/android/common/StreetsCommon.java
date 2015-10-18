package net.blaklizt.streets.android.common;

import android.app.Activity;
import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import net.blaklizt.streets.android.persistence.StreetsDBHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
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

	//List of activities started, destroy all on EndApplication
	private static LinkedList<Activity> activities = new LinkedList<>();

	private static ArrayList<BackgroundRunner> backgroundTasks = new ArrayList<>();

	//Application context
	private Context context = null;

	//Applicate user
	private Integer symbiosisUserID = 0;

	//Streets DB classes
	protected static StreetsDBHelper streetsDBHelper = null;

	//Streets Text To Speech engine
	protected static TextToSpeech ttsEngine = null;

	//static class reference
	private static StreetsCommon streetsCommon = null;

	//user preferences
	private static HashMap<String, String> userPreferenceValues = new HashMap<>();
	private static HashMap<String, String> userPreferenceDescriptions = new HashMap<>();
	private static HashMap<String, String> userPreferenceTypes = new HashMap<>();

	public static StreetsCommon getInstance(Context context, Integer symbiosisUserID)
	{
		if (streetsCommon == null) {
			streetsCommon = new StreetsCommon(context, symbiosisUserID);
            Log.i(TAG, "Created new instance of StreetsCommon");
		}
		return streetsCommon;
	}

	private StreetsCommon(Context context, Integer symbiosisUserID)
	{
		this.context = context;

		this.symbiosisUserID = symbiosisUserID;

		//initialize DB
		getStreetsDBHelper();

		//initialize user preferences
		getUserPreferenceValues();

		//initialize TTS
		getTextToSpeech();

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
		try
		{
			if (ttsEngine == null) {
				Log.i(TAG, "Initializing text to speech engine");
				ttsEngine = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
					@Override
					public void onInit(int i) {
						Log.i(TAG, "Initialized text to speech engine");
						ttsEngine.setLanguage(Locale.US);
					}
				});
			}
			return ttsEngine;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			Log.e(TAG, "Failed to initialize ttsEngine", ex);
			return null;
		}
	}

	public void endApplication()
	{
		try
		{
			//shutdown common classes
			Log.i(TAG, "Terminating common classes.");
			if (streetsDBHelper != null) {
				streetsDBHelper.close();
				streetsDBHelper = null;
			}
			if (ttsEngine != null) {
				ttsEngine.shutdown();
				ttsEngine = null;
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			Log.e(TAG, "Failed to shutdown common classes cleanly: " + ex.getMessage(), ex);
		}
	}

    public String getUserPreferenceValue(String preference) {
        if (!getUserPreferenceValues().containsKey(preference)) {
            Log.i(TAG, "Preference " + preference + " does not exist");
        }
		Log.i(TAG, preference + " = " + getUserPreferenceValues().get(preference));
        return getUserPreferenceValues().get(preference);
    }

	public void initUserPreferenceData() {
		Log.i(TAG, "Loading user preferences from DB");
		userPreferenceValues		=	getStreetsDBHelper().getUserPreferences().get("values");
		userPreferenceDescriptions	=	getStreetsDBHelper().getUserPreferences().get("descriptions");
		userPreferenceTypes			=	getStreetsDBHelper().getUserPreferences().get("data_types");
	}

    public HashMap<String, String> getUserPreferenceValues() {
        if (userPreferenceValues.size() == 0) { initUserPreferenceData(); }
        return userPreferenceValues;
    }

    public HashMap<String, String> getUserPreferenceDescriptions() {
        if (userPreferenceDescriptions.size() == 0) { initUserPreferenceData(); }
        return userPreferenceDescriptions;
    }

    public HashMap<String, String> getUserPreferenceTypes() {
        if (userPreferenceTypes.size() == 0) { initUserPreferenceData(); }
        return userPreferenceTypes;
    }

	public void setUserPreference(String preference, String value) {
		Log.i(TAG, "Setting " + preference + " = " + value);
		String description = getUserPreferenceDescriptions().get(preference);
		String data_type = getUserPreferenceTypes().get(preference);
		if (description != null && data_type != null) {
			getUserPreferenceValues().remove(preference);
			getUserPreferenceValues().put(preference, value);
			getStreetsDBHelper().setUserPreference(preference, value, description, data_type);
		} else {
			throw new IllegalArgumentException("Preference type " + preference + " is unknown and cannot be saved. Your app needs an upgrade.");
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
}
