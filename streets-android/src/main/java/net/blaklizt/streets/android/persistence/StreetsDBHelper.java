package net.blaklizt.streets.android.persistence;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import net.blaklizt.streets.android.activity.Startup;
import net.blaklizt.streets.android.common.STATUS_CODES;
import net.blaklizt.streets.android.common.StreetsCommon;
import net.blaklizt.streets.android.common.SymbiosisUser;
import net.blaklizt.streets.android.common.TASK_TYPE;
import net.blaklizt.streets.android.common.TaskInfo;
import net.blaklizt.streets.android.common.USER_PREFERENCE;
import net.blaklizt.streets.android.common.utils.SecurityContext;
import net.blaklizt.streets.android.location.places.Place;
import net.blaklizt.streets.android.location.places.PlaceTypes;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import static java.lang.String.format;
import static net.blaklizt.streets.android.common.utils.SecurityContext.handleApplicationError;

/**
 * User: tkaviya
 * Date: 7/5/14
 * Time: 12:24 PM
 */
public class StreetsDBHelper extends SQLiteOpenHelper {
    /**
     * Created by Tsungai on 2014/03/31.
     */
    private static final String TAG = StreetsCommon.getTag(StreetsDBHelper.class);

    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 4;

	private static final String DATABASE_NAME = "Neighbourhood.db";

	private static final ArrayList<String> ALL_TABLES = new ArrayList<>();

    /* static / setup tables */
    private static final String TASK_TYPE_TABLE = "task_type";                  static { ALL_TABLES.add(TASK_TYPE_TABLE); }
    private static final String PLACE_TYPE_TABLE = "place_type";				static { ALL_TABLES.add(PLACE_TYPE_TABLE); }
    private static final String STATUS_CODE_TABLE = "status_code";              static { ALL_TABLES.add(STATUS_CODE_TABLE); }
	private static final String PLACE_TABLE = "place"; 							static { ALL_TABLES.add(PLACE_TABLE); }

    /* user data */
    private static final String USER_TABLE = "user";							static { ALL_TABLES.add(USER_TABLE); }
    private static final String FRIEND_TABLE = "friend";						static { ALL_TABLES.add(FRIEND_TABLE); }
    private static final String USER_PREFERENCE_TABLE = "user_preferences";		static { ALL_TABLES.add(USER_PREFERENCE_TABLE); }
    private static final String PLACE_TYPES_OF_INTEREST = "selected_places";	static { ALL_TABLES.add(PLACE_TYPES_OF_INTEREST); }
    private static final String LOCATION_HISTORY_TABLE = "location_history";	static { ALL_TABLES.add(LOCATION_HISTORY_TABLE); }
    private static final String SUPPLIER_TABLE = "supplier";					static { ALL_TABLES.add(SUPPLIER_TABLE); }
    private static final String REQUIRED_PERMS = "outstanding_permissions";		static { ALL_TABLES.add(REQUIRED_PERMS); }

    /* logging tables */
    private static final String TASK_HISTORY_TABLE  = "task_history";            static { ALL_TABLES.add(TASK_HISTORY_TABLE); }
    private static final String EVENT_HISTORY_TABLE = "event_history";          static { ALL_TABLES.add(EVENT_HISTORY_TABLE); }

    private SQLiteDatabase sqlLiteDatabase = null;

    public StreetsDBHelper(Context context) { super(context, DATABASE_NAME, null, DATABASE_VERSION); }

    public void onCreate(SQLiteDatabase db) {

		for (String tableName : ALL_TABLES) {
			Log.i(TAG, "Dropping table " + tableName);
			db.execSQL("DROP TABLE IF EXISTS " + tableName);
		}

        Log.i(TAG, "Recreating table " + TASK_TYPE_TABLE);
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TASK_TYPE_TABLE + " (" +
                "task_type_id INT(11) PRIMARY KEY," +
                "task_type_name VARCHAR(30)," +
                "description VARCHAR(50)");

        Log.i(TAG, "Recreating table " + STATUS_CODE_TABLE);
        db.execSQL("CREATE TABLE IF NOT EXISTS " + STATUS_CODE_TABLE + " (" +
                "status_code INT(11) PRIMARY KEY," +
                "status_name VARCHAR(30)," +
                "status_description VARCHAR(50)");

        Log.i(TAG, "Recreating table " + PLACE_TYPE_TABLE);
        db.execSQL("CREATE TABLE IF NOT EXISTS " + PLACE_TYPE_TABLE + " (" +
                "place_type_id INT(11) PRIMARY KEY," +
                "place_type_name VARCHAR(50))");

        Log.i(TAG, "Recreating table " + USER_TABLE);
        db.execSQL("CREATE TABLE IF NOT EXISTS " + USER_TABLE + " (" +
                "symbiosis_id INT(11) PRIMARY KEY," +
                "username VARCHAR(256)," +
                "imei VARCHAR(256)," +
                "imsi VARCHAR(128)," +
                "password VARCHAR(50)," +
                "last_location_id INT(11)," +
                "home_place_id INT(11)," +
                "type VARCHAR(50))");

        //fixed places that are never remove
        Log.i(TAG, "Recreating table " + PLACE_TABLE);
        db.execSQL("CREATE TABLE IF NOT EXISTS " + PLACE_TABLE + " (" +
                "place_id INT(11) PRIMARY KEY," +
                "place_type_id INT(11)," +
                "name VARCHAR(50)," +
                "reference VARCHAR(100)," +
                "latitude DOUBLE," +
                "longitude DOUBLE," +
                "type VARCHAR(50))");

        //table the grows and is truncated after a while
        Log.i(TAG, "Recreating table " + LOCATION_HISTORY_TABLE);
        db.execSQL("CREATE TABLE IF NOT EXISTS " + LOCATION_HISTORY_TABLE + " (" +
                "location_history_id INT(11) PRIMARY KEY," +
                "latitude DOUBLE," +
                "longitude DOUBLE," +
                "update_date_time DATETIME)");

		Log.i(TAG, "Recreating table " + FRIEND_TABLE);
		db.execSQL("CREATE TABLE IF NOT EXISTS " + FRIEND_TABLE + " (" +
                "symbiosis_id INT(11) PRIMARY KEY," +
                "last_place_id INT(11)," +
                "home_place_id INT(11))");

		Log.i(TAG, "Recreating table " + SUPPLIER_TABLE);
		db.execSQL("CREATE TABLE IF NOT EXISTS " + SUPPLIER_TABLE + " (" +
                "symbiosis_id INT(11) PRIMARY KEY," +
                "last_geolocation_id INT(11))");

		Log.i(TAG, "Recreating table " + USER_PREFERENCE_TABLE);
		db.execSQL("CREATE TABLE IF NOT EXISTS " + USER_PREFERENCE_TABLE + " (" +
                "pref_id VARCHAR(20) PRIMARY KEY," +
                "pref_name VARCHAR(20)," +
                "pref_value VARCHAR(20)," +
                "pref_description VARCHAR(20)," +
                "pref_data_type VARCHAR(20))");

        Log.i(TAG, "Recreating table " + TASK_HISTORY_TABLE);
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TASK_HISTORY_TABLE + " (" +
                "task_history_id INT(11) AUTO_INCREMENT PRIMARY KEY," +
                "task_type_id VARCHAR(30)," +
                "request_time DATETIME," +
                "start_time DATETIME," +
                "end_time DATETIME," +
                "final_status INT(3)");

        Log.i(TAG, "Recreating table " + EVENT_HISTORY_TABLE);
        db.execSQL("CREATE TABLE IF NOT EXISTS " + EVENT_HISTORY_TABLE + " (" +
                "history_id INT(11) AUTO_INCREMENT PRIMARY KEY," +
                "event_type_id INT(11)," +
                "event_time DATETIME," +
                "final_status INT(3)," +
                "description BLOB");

        initTaskData();

        initPlaceData();

        initUserData();

        initPreferenceData();
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) { onCreate(db); }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		onUpgrade(db, oldVersion, newVersion);
    }

	private SQLiteDatabase getStreetsWritableDatabase() {
		if (sqlLiteDatabase == null) { sqlLiteDatabase = getWritableDatabase(); }
		return sqlLiteDatabase;
	}

    public void initUserData() {
        Log.i(TAG, "Populating data for current user " + REQUIRED_PERMS);
        getStreetsWritableDatabase().execSQL("INSERT INTO " + USER_TABLE +
                " (symbiosis_id,username,imei,imsi,password,last_location_id,home_place_id,type) VALUES" +
                " (" + SecurityContext.getUserDefaultDataSet().get("symbiosis_id") + "," +
                " (" + SecurityContext.getUserDefaultDataSet().get("username") + "," +
                " (" + Startup.getStreetsCommon().getIMEI() + "," +
                " (" + Startup.getStreetsCommon().getIMSI() + "," +
                " (" + SecurityContext.getUserDefaultDataSet().get("password") + "," +
                " (" + SecurityContext.getUserDefaultDataSet().get("last_location_id") + "," +
                " (" + SecurityContext.getUserDefaultDataSet().get("home_place_id") + "," +
                " (" + SecurityContext.getUserDefaultDataSet().get("type") + ")");

        Log.i(TAG, "Recreating table " + REQUIRED_PERMS);
        getStreetsWritableDatabase().execSQL("CREATE TABLE IF NOT EXISTS "
                + REQUIRED_PERMS + " (permission VARCHAR(50) PRIMARY KEY)");
    }

    public void initPlaceData() {
        Log.i(TAG, "Recreating table " + PLACE_TYPES_OF_INTEREST);
        getStreetsWritableDatabase().execSQL("CREATE TABLE IF NOT EXISTS "
                + PLACE_TYPES_OF_INTEREST + " (place_type_id VARCHAR(50) PRIMARY KEY)");

        ArrayList <String> defaultPlaces = PlaceTypes.getDefaultPlaces();
        getStreetsWritableDatabase().execSQL("DELETE FROM " + PLACE_TYPES_OF_INTEREST);

        if (defaultPlaces.size() >= 1) {
            String sql = "INSERT INTO " + PLACE_TYPES_OF_INTEREST + " (place_type_id) SELECT place_type_id FROM "
                    + PLACE_TYPE_TABLE + " WHERE place_type_name IN (";
            for (String place : defaultPlaces) {
                sql += "'" + place + "',";
            }

            sql = sql.substring(0, sql.length() - 1) + ")";
            Log.d(TAG, "Inserting default places. SQL: " + sql);
            getStreetsWritableDatabase().execSQL(sql);
        }
    }

    public void initPreferenceData() {
        String sql = "REPLACE INTO " + USER_PREFERENCE_TABLE +
            " (pref_id, pref_name, pref_value, pref_description, pref_data_type) VALUES ";

        for (USER_PREFERENCE pref : USER_PREFERENCE.values()) {
            sql += "('" + pref.pref_id + "', '" + pref.pref_name + "', '" + pref.pref_value + "', '"
                        + pref.pref_description + "', '" + pref.pref_data_type + "'),";
        }

        sql = sql.substring(0, sql.length() - 1) + ")";
        Log.d(TAG, "Inserting default preference data. SQL:\n" + sql);
        getStreetsWritableDatabase().execSQL(sql);
    }

    public void initTaskData() {
        String sql = "REPLACE INTO " + TASK_TYPE_TABLE + " (task_type_id, task_type_name, description) VALUES ";

        for (TASK_TYPE taskType : TASK_TYPE.values()) {
            sql += "('" + taskType.task_type_id + "', '" + taskType.task_type_name + "', '" + taskType.description + "'),";
        }

        sql = sql.substring(0, sql.length() - 1) + ")";
        Log.d(TAG, "Inserting task type data. SQL:\n" + sql);
        getStreetsWritableDatabase().execSQL(sql);
    }

    public void logApplicationEvent(TASK_TYPE task_type, STATUS_CODES status, String description) {

        String sql = "INSERT INTO " + EVENT_HISTORY_TABLE + " (event_type, event_time, final_status, description) VALUES " +
                "('" + task_type.task_type_id + "', sysdate(), " +  status.status_code + ", '" + description + "'),";

        Log.i(TAG, "Inserting application event log for event type: " + task_type.task_type_name + " | Status: " + status.status_name);
        if (status.status_code != STATUS_CODES.GENERAL_ERROR.status_code) {
            Log.d(TAG, "If : " + task_type.task_type_name);
        }
        getStreetsWritableDatabase().execSQL(sql);
    }

    public void logTaskEvent(TaskInfo taskInfo, STATUS_CODES status) {
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy", Locale.ENGLISH);
        String sql = "INSERT INTO " + TASK_HISTORY_TABLE + " (task_type_id, request_time, start_time, end_time, final_status) VALUES " +
                "('" + taskInfo.getTaskType() + "', " +
                (taskInfo.getRequestedTime() == null ? "'null'" : "'" + sdf.format(taskInfo.getRequestedTime()) + "', ") +
                (taskInfo.getStartTime() == null ? "'null'" : "'" + sdf.format(taskInfo.getStartTime()) + "', ") +
                (taskInfo.getEndTime() == null ? "'null'" : "'" + sdf.format(taskInfo.getEndTime()) + "', ") +
                status.status_code + "),";

        Log.i(TAG, "Inserting task event log. SQL: " + sql);
        getStreetsWritableDatabase().execSQL(sql);
    }

	//database functions
	public HashMap<String, USER_PREFERENCE> getUserPreferences()
	{
        try {
            Log.i(TAG, "Checking user preferences.");
            HashMap<String, USER_PREFERENCE> userPreferenceValues = new HashMap<>();
            Cursor preferences = getStreetsWritableDatabase().rawQuery(
                    "SELECT pref_name, pref_value, pref_description, pref_data_type FROM " + USER_PREFERENCE_TABLE, null);

            Log.i(TAG, "Found " + preferences.getCount() + " preferences.");
            preferences.moveToFirst();
            while (!preferences.isAfterLast())
            {
                Log.d(TAG, "Adding "
                        + preferences.getString(3) + " preference: "
                        + preferences.getString(0) + " = "
                        + preferences.getString(1) + " : "
                        + preferences.getString(2));
                USER_PREFERENCE result = USER_PREFERENCE.valueOf(preferences.getString(0));
                result.pref_value = preferences.getString(1);
                userPreferenceValues.put(preferences.getString(0), result); //value
                preferences.moveToNext();
            }
            preferences.close();
            return userPreferenceValues;
        } catch (Exception ex) {
            handleApplicationError(SecurityContext.ERROR_SEVERITY.SEVERE, "Failed to get user preferences! " +
                ex.getMessage(), ex.getStackTrace(), TASK_TYPE.SYS_DB);
            return null; //will never return
        }
	}

	public void setUserPreference(USER_PREFERENCE preference, String value, String description, String data_type)
	{
		Log.d(TAG, "Updating user preference " + preference + " to " + value);
		getStreetsWritableDatabase().execSQL("REPLACE INTO " + USER_PREFERENCE_TABLE +
				" (pref_id, pref_name, pref_value, pref_description, pref_data_type) VALUES" +
				" ('" + preference.pref_id + "','" + preference.pref_name +
                "','" + value + "','" + description + "','" + data_type + "')");
	}

	public ArrayList<Place> getNearbyFriendLocations()
	{
        Log.i(TAG, "Searching for nearby friends.");
        ArrayList<Place> resultList = new ArrayList<>();
        Cursor friends = getStreetsWritableDatabase().rawQuery(
            "SELECT 'username', pt.reference, pt.latitude, pt.longitude, pt.place_id " +
            "FROM " + FRIEND_TABLE + " ft, " + PLACE_TABLE + " pt " +
            "WHERE ft.last_place_id = pt.place_id", null);

        Log.i(TAG, "Found " + friends.getCount() + " friends.");
        friends.moveToFirst();
        while (!friends.isAfterLast())
        {
            Log.i(TAG, "Adding friend: " + friends.getString(0));
            resultList.add(new Place(
                friends.getString(0),
                friends.getString(1),
                friends.getDouble(2),
                friends.getDouble(3),
                friends.getString(4),
                null));
            friends.moveToNext();
        }
        friends.close();

        return resultList;
	}

    public SymbiosisUser getCurrentUser()
	{
        Log.i(TAG, "Getting symbiosis user details.");

        Cursor symbiosisUser = getStreetsWritableDatabase().rawQuery(
            "SELECT symbiosis_id, username, imei, imsi, password, last_location_id, home_place_id, type " +
            "FROM " + USER_TABLE + " WHERE type = 'USER' LIMIT 1", null);

        Log.i(TAG, "Found " + symbiosisUser.getCount() + " records.");

        symbiosisUser.moveToFirst();

        Log.i(TAG, "Populating data for user id: " + symbiosisUser.getString(0));

        return new SymbiosisUser(
            symbiosisUser.getLong(0),   symbiosisUser.getString(1), symbiosisUser.getString(2),
            symbiosisUser.getString(3), symbiosisUser.getString(4), symbiosisUser.getLong(5),
            symbiosisUser.getLong(6),   symbiosisUser.getString(7));
	}

    public ArrayList<String> getPlacesOfInterest()
	{
        Log.i(TAG, "Getting list of places of interest .");
        ArrayList<String> resultList = new ArrayList<>();
        Cursor places = getStreetsWritableDatabase().rawQuery(
                "SELECT pt.place_type_name FROM " + PLACE_TYPES_OF_INTEREST + " poi, " + PLACE_TYPE_TABLE + " pt " +
                "WHERE poi.place_type_id = pt.place_type_id", null);

        Log.i(TAG, "Found " + places.getCount() + " places.");
        places.moveToFirst();
        while (!places.isAfterLast())
        {
            Log.d(TAG, "Adding place: " + places.getString(0));
            resultList.add(places.getString(0));
            places.moveToNext();
        }

        return resultList;
	}

    public void addPlaceOfInterest(String place) {
        String sql = "REPLACE INTO " + PLACE_TYPES_OF_INTEREST + " (place_type_id)" +
                " SELECT place_type_id FROM place_type WHERE place_type_name = '" + place + "'";
        Log.d(TAG, "Inserting new place of interest. SQL:\n" + sql);
        getStreetsWritableDatabase().execSQL(sql);
    }

    public void removePlaceOfInterest(String place) {
        String sql = "DELETE FROM " + PLACE_TYPES_OF_INTEREST + " WHERE place_type_id = " +
                " SELECT place_type_id FROM place_type WHERE type = '" + place + "'";
        Log.d(TAG, "Removing place of interest. SQL:\n" + sql);
        getStreetsWritableDatabase().execSQL(sql);
    }

	public void addOutstandingPermission(String permission) {
		String sql = "REPLACE INTO " + REQUIRED_PERMS + " (permission) VALUES ('" + permission + "')";
		Log.d(TAG, "Inserting new outstanding permission. SQL:\n" + sql);
		getStreetsWritableDatabase().execSQL(sql);
	}

	public void removeOutstandingPermission(String permission) {
		String sql = "DELETE FROM " + REQUIRED_PERMS + " WHERE permission = '" + permission + "'";
		Log.d(TAG, "Removing outstanding permission. SQL:\n" + sql);
		getStreetsWritableDatabase().execSQL(sql);
	}

	public ArrayList<String> getOutstandingPermissions() {
        Log.i(TAG, "Getting list of outstanding permissions.");
        ArrayList<String> resultList = new ArrayList<>();
        Cursor permissions = getStreetsWritableDatabase().rawQuery("SELECT permission FROM " + REQUIRED_PERMS, null);

        Log.i(TAG, "Found " + permissions.getCount() + " permissions.");
        permissions.moveToFirst();
        while (!permissions.isAfterLast())
        {
            Log.d(TAG, "Adding permission: " + permissions.getString(0));
            resultList.add(permissions.getString(0));
            permissions.moveToNext();
        }

        return resultList;
	}

    public void saveCurrentUser(SymbiosisUser symbiosisUser) {

        SecurityContext.verifyUserDetails(symbiosisUser);

        Log.i(TAG, "Updating symbiosis user details.");

        getWritableDatabase().execSQL(format("REPLACE INTO " + USER_TABLE +
                        " (symbiosis_id,username,imei,imsi,password,last_location_id,home_place_id,type) VALUES" +
                        " (%d, %s, %s, %s, %s, %d, %d, %s)",
                symbiosisUser.symbiosisUserID,
                "'" + symbiosisUser.username + "'",
                "'" + symbiosisUser.imei + "'",
                "'" + symbiosisUser.imsi + "'",
                "'" + symbiosisUser.password + "'",
                symbiosisUser.lastLocationID,
                symbiosisUser.homePlaceID,
                "'" + symbiosisUser.type + "'"
        ));
    }
}
