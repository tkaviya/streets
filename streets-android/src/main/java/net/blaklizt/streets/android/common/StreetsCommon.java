package net.blaklizt.streets.android.common;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.speech.tts.TextToSpeech;
import android.support.design.widget.Snackbar;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import net.blaklizt.streets.android.activity.AppContext;
import net.blaklizt.streets.android.common.enumeration.SHARE_PROVIDER;
import net.blaklizt.streets.android.common.enumeration.STATUS_CODES;
import net.blaklizt.streets.android.common.enumeration.TASK_TYPE;
import net.blaklizt.streets.android.common.enumeration.USER_PREFERENCE;
import net.blaklizt.streets.android.common.utils.SecurityContext;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;

import static net.blaklizt.streets.android.activity.AppContext.getUserPreferenceValues;
import static net.blaklizt.symbiosis.sym_core_lib.utilities.Validator.isNullOrEmpty;
import static net.blaklizt.symbiosis.sym_core_lib.utilities.Validator.isValidMsisdn;

/**
 * Created with IntelliJ IDEA.
 * User: photon
 * Date: 2015/06/12
 * Time: 8:26 AM
 */
public class StreetsCommon
{
    public enum FILE_DATA_TYPE { ICON }
	//Tag all logs starting with the following tag
	private static final String TAG = "Streets";

    //Application context
	private Context context = null;

	//Application user
	private SymbiosisUser symbiosisUser = null;
	private String imei = null, imsi = null, phoneNumber = null, defaultEmail = null, defaultUsername = null;

    //static class reference
	private static StreetsCommon streetsCommon = null;

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
    }

    public static String getTag(Class streetsClass) { return StreetsCommon.TAG + "_" + streetsClass.getSimpleName(); }

    public static void showSnackBar(final Activity currentActivity, final String TAG, final String text, final int duration) {

        if      (duration == Snackbar.LENGTH_LONG)  { Log.w(TAG, text); }
        else if (duration == Snackbar.LENGTH_SHORT) { Log.i(TAG, text); }

        currentActivity.runOnUiThread(() -> Snackbar.make(currentActivity.getCurrentFocus(), text, duration).show());
    }

    public static void showToast(final Activity currentActivity, final String TAG, final String text, final int duration) {

        if      (duration == Toast.LENGTH_LONG)  { Log.w(TAG, text); }
        else if (duration == Toast.LENGTH_SHORT) { Log.i(TAG, text); }

        currentActivity.runOnUiThread(() -> Toast.makeText(currentActivity, text, duration).show());
    }

    public void speak(final String speechText)
    {
        if (getUserPreferenceValue(USER_PREFERENCE.ENABLE_TTS).equals("1")) {
            Log.i(TAG, "Speaking text: " + speechText);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                AppContext.getInstance().getTextToSpeech().speak(speechText, TextToSpeech.QUEUE_FLUSH, null, String.valueOf(new Date().getTime()));
            } else {
                AppContext.getInstance().getTextToSpeech().speak(speechText, TextToSpeech.QUEUE_FLUSH, null);
            }
        } else {
            Log.w(TAG, "Text to speech disabled! Cannot speak text " + speechText);
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

	public void setUserPreference(USER_PREFERENCE preference, String value) {
        try {
            Log.i(TAG, "Setting " + preference + " = " + value);
            String description = getUserPreferenceValues().get(preference.name()).pref_description;
            String data_type = getUserPreferenceValues().get(preference.name()).pref_data_type;
            getUserPreferenceValues().remove(preference.name());
            preference.pref_value = value;
            getUserPreferenceValues().put(preference.name(), preference);
            AppContext.getStreetsDBHelper().setUserPreference(preference, value, description, data_type);
		} catch (Exception ex) {
            SecurityContext.handleApplicationError(SecurityContext.ERROR_SEVERITY.SEVERE,
                "User preferences could not be updated.\n\n" +
                "Please update your application to the latest version to avoid potential data corruption.",
                ex.getStackTrace(), TASK_TYPE.USER_PREF_UPDATE);
        }
	}

    public static String getCacheDir(FILE_DATA_TYPE fileDataType) {
        String cacheFolder = null;
        switch (fileDataType) {
            case ICON: cacheFolder = "icon_cache";
        }

        cacheFolder = Environment.getExternalStorageDirectory()
                + File.separator + "blaklizt"
                + File.separator + "streets"
                + File.separator + cacheFolder
                + File.separator;

        Log.i(TAG, "Checking if cache directory exists: " + cacheFolder);

        if (!new File(cacheFolder).exists()) {
            if (new File(cacheFolder).mkdirs()) {
                Log.i(TAG, "Created streets cache folder: " + cacheFolder);
            } else {
                Log.e(TAG, "Failed to create streets cache folder: " + cacheFolder);
            }
        }

        return cacheFolder;
    }

	public ArrayList<String> getOutstandingPermissions() {
		return AppContext.getStreetsDBHelper().getOutstandingPermissions();
	}

	public void addOutstandingPermission(String permission) {
		AppContext.getStreetsDBHelper().addOutstandingPermission(permission);
	}

	public void removeOutstandingPermission(String permission) {
		AppContext.getStreetsDBHelper().removeOutstandingPermission(permission);
	}

    public SymbiosisUser getSymbiosisUser() {
        if (symbiosisUser == null) {
            symbiosisUser = AppContext.getStreetsDBHelper().getCurrentUser();
            Log.i(TAG, "Got current symbiosis user details for user id: " + symbiosisUser.symbiosisUserID);
            Log.i(TAG, "username -> " + symbiosisUser.username);
            Log.i(TAG, "imei     -> " + symbiosisUser.imei);
            Log.i(TAG, "imsi     -> " + symbiosisUser.imsi);
        }
        return symbiosisUser;
    }

    public void saveUserDetails() {
        AppContext.getStreetsDBHelper().saveCurrentUser(symbiosisUser);
    }

    public String getIMEI() {
        if (isNullOrEmpty(this.imei)) {
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if (!isNullOrEmpty(this.imei = telephonyManager.getDeviceId())) {
                Log.i(TAG, "Service 'TelephonyManager' returned IMEI: " + this.imei);
            } else {
                this.imei = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
                Log.i(TAG, "TelephonyManager was null. Service 'Settings.Secure' returned IMEI: " + this.imei);
            }
        }
        return this.imei;
    }

    public String getIMSI() {
        if (isNullOrEmpty(this.imsi)) {
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if (!isNullOrEmpty(this.imsi = telephonyManager.getSimSerialNumber())) {
                Log.i(TAG, "Service 'TelephonyManager' returned IMSI: " + this.imsi);
            }
        }
        return this.imsi;
    }

    public String getPhoneNumber() {
        if (isNullOrEmpty(this.phoneNumber)) {
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if (!isNullOrEmpty(this.phoneNumber = telephonyManager.getLine1Number())) {
                Log.i(TAG, "Service 'TelephonyManager' returned phone number: " + this.phoneNumber);
            } else if (!isNullOrEmpty(this.phoneNumber = telephonyManager.getVoiceMailNumber())) {
                Log.i(TAG, "Service 'TelephonyManager' returned approximate phone number: " + this.phoneNumber);
            } else {
                enumerateAccounts();
            }
        }
        return this.phoneNumber;
    }

    public String getDefaultEmail() {
        enumerateAccounts();
        return this.defaultEmail;
    }

    public String getDefaultUsername() {
        enumerateAccounts();
        return this.defaultUsername;
    }

    public void setDefaultUsername(String defaultUsername) {
        this.defaultUsername = defaultUsername;
    }

    public void enumerateAccounts() {
        if (isNullOrEmpty(this.defaultEmail)) {
            AccountManager am = AccountManager.get(context);
            Account[] accounts = am.getAccounts();

            for (Account account : accounts) {
                String accountName = account.name, accountType = account.type;
                Log.i(TAG, "Accounts : " + accountName + ", " + accountType);
                if (isNullOrEmpty(this.defaultEmail) && accountType.equals(SHARE_PROVIDER.GOOGLE.app_package)) {
                    this.defaultEmail = accountName;
                    Log.i(TAG, "Found default email: " + this.defaultEmail + " from account " + accountType);
                } else if (isNullOrEmpty(this.defaultUsername) && accountType.equals(SHARE_PROVIDER.TWITTER.app_package)) {
                    this.defaultUsername = accountName;
                    Log.i(TAG, "Found default username: " + this.defaultUsername + " from account " + accountType);
                } else if (isNullOrEmpty(this.phoneNumber) && (isValidMsisdn(accountName) || isValidMsisdn(accountName, "27"))) {
                    this.phoneNumber = accountName;
                    Log.i(TAG, "Found phone number: " + this.phoneNumber + " from account " + accountType);
                }
            }
        }
    }

    public void setUserID(Long userID) {
        getSymbiosisUser().symbiosisUserID = userID;
        saveUserDetails();
    }

    public void writeEventLog(TASK_TYPE task_type, STATUS_CODES status, String description) {
        AppContext.getStreetsDBHelper().logApplicationEvent(task_type, status, description);
    }

    public void logTaskEvent(TaskInfo taskInfo, STATUS_CODES status) {
        AppContext.getStreetsDBHelper().logTaskEvent(taskInfo, status);
    }
}
