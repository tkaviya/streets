package net.blaklizt.streets.android.common;

import android.app.Activity;
import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import net.blaklizt.streets.android.activity.Login;
import net.blaklizt.streets.android.activity.Register;
import net.blaklizt.streets.android.activity.Startup;
import net.blaklizt.streets.android.activity.Streets;
import net.blaklizt.streets.android.persistence.StreetsDBHelper;

import java.util.HashMap;
import java.util.Iterator;
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
	private static HashMap<String, String> userPreferences = new HashMap<>();

	public static StreetsCommon getInstance(Context context, Integer symbiosisUserID)
	{
		if (streetsCommon == null) streetsCommon = new StreetsCommon(context, symbiosisUserID);
		return streetsCommon;
	}

	private StreetsCommon(Context context, Integer symbiosisUserID)
	{
		this.context = context;

		this.symbiosisUserID = symbiosisUserID;

		//initialize DB
		getStreetsDBHelper();

		//initialize user preferences
		getUserPreferences();

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

//	public static void startNewActivity(Context baseContext, Class<? implements StreetsActivity> streetsActivityClass) {
//		Intent loginActivity = new Intent(baseContext, streetsActivityClass.getClass());
//		baseContext.startActivity(loginActivity);
//		activities.add((Class<StreetsActivity>)streetsActivityClass);
//	}

	public HashMap<String, String> getUserPreferences() {
		if (userPreferences.size() == 0)
		{
			Log.i(TAG, "Attempting to load user preferences");
			if (getStreetsDBHelper() != null)
			{
				LinkedList<UserPreference> userPreferenceList = getStreetsDBHelper().getUserPreferences(symbiosisUserID);

				for (UserPreference userPreference : userPreferenceList)
				{
					userPreferences.put(userPreference.preferenceName, userPreference.preferenceValue);
				}

				Log.i(TAG, "Got user preferences from database");
			}
		}
		return userPreferences;
	}

	public String getUserPreference(String preference) { return getUserPreferences().get(preference); }

	public static void registerStreetsActivity(Activity activity) { activities.add(activity); }

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


	public static void endApplication()
	{
		try
		{
			//destroy all application contexts in sequence
			Log.i(TAG, "Terminating app contexts.");
			if (Streets.getInstance() != null) Streets.getInstance().finish();
			if (Login.getInstance() != null) Login.getInstance().finish();
			if (Register.getInstance() != null) Register.getInstance().finish();
			if (Startup.getStartup() != null) Startup.getStartup().finish();
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			Log.e(TAG, "Failed to terminate app contexts cleanly: " + ex.getMessage(), ex);
		}

		try
		{
			//shutdown common classes
			Log.i(TAG, "Terminating common classes.");
			if (streetsDBHelper != null)	streetsDBHelper.close();
			if (ttsEngine != null)			ttsEngine.shutdown();
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			Log.e(TAG, "Failed to shutdown common classes cleanly: " + ex.getMessage(), ex);
		}

		try
		{
			//shutdown all activities in reverse creation order
			Log.i(TAG, "Terminating activities.");
			Iterator<Activity> allActivities = activities.descendingIterator();
			while (allActivities.hasNext()) {
				Activity activity = allActivities.next();
				Log.i(TAG, "Terminating activity " + activity.getClass().getSimpleName());
				activity.finish();
			}

		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			Log.e(TAG, "Failed to shutdown activities cleanly: " + ex.getMessage(), ex);
		}
	}

	public void setUserPreference(String preference, String value) {
		getStreetsDBHelper().setUserPreference(symbiosisUserID, preference, value);
	}
}
