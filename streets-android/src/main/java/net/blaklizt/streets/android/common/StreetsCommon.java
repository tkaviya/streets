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
			if (streetsDBHelper != null)	streetsDBHelper.close();
			if (ttsEngine != null)			ttsEngine.shutdown();
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			Log.e(TAG, "Failed to shutdown common classes cleanly: " + ex.getMessage(), ex);
		}
	}

    public String getUserPreference(String preference) { return getUserPreferences().get(preference); }

    public HashMap<String, String> getUserPreferences() {
        if (userPreferences.size() == 0)
        {
            Log.i(TAG, "Attempting to load user preferences");
            if (getStreetsDBHelper() != null)
            {
                LinkedList<UserPreference> userPreferenceList = getStreetsDBHelper().getUserPreferences();

                for (UserPreference userPreference : userPreferenceList)
                {
                    userPreferences.put(userPreference.preferenceName, userPreference.preferenceValue);
                }

                Log.i(TAG, "Got user preferences from database");
            }
        }
        return userPreferences;
    }

	public void setUserPreference(String preference, String value) {
		getStreetsDBHelper().setUserPreference(preference, value);
	}
}
