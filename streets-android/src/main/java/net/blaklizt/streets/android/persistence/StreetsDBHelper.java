package net.blaklizt.streets.android.persistence;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import net.blaklizt.streets.android.common.StreetsCommon;
import net.blaklizt.streets.android.common.UserPreference;
import net.blaklizt.streets.android.location.places.Place;
import net.blaklizt.streets.android.location.places.PlaceTypes;

import java.util.ArrayList;
import java.util.HashMap;

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
    private static final int DATABASE_VERSION = 1;

	private static final String DATABASE_NAME = "Neighbourhood.db";

	private static final ArrayList<String> ALL_TABLES = new ArrayList<>();

	private static final String PLACE_TABLE = "place"; 							static { ALL_TABLES.add(PLACE_TABLE); }
	private static final String PLACE_TYPE_TABLE = "place_type";				static { ALL_TABLES.add(PLACE_TYPE_TABLE); }
	private static final String LOCATION_HISTORY_TABLE = "location_history";	static { ALL_TABLES.add(LOCATION_HISTORY_TABLE); }
	private static final String USER_TABLE = "user";							static { ALL_TABLES.add(USER_TABLE); }
	private static final String FRIEND_TABLE = "friend";						static { ALL_TABLES.add(FRIEND_TABLE); }
	private static final String SUPPLIER_TABLE = "supplier";					static { ALL_TABLES.add(SUPPLIER_TABLE); }
	private static final String USER_PREFERENCES = "user_preferences";			static { ALL_TABLES.add(USER_PREFERENCES); }
	private static final String PLACE_TYPES_OF_INTEREST = "selected_places";	static { ALL_TABLES.add(PLACE_TYPES_OF_INTEREST); }
	private static final String REQUIRED_PERMS = "outstanding_permissions";		static { ALL_TABLES.add(REQUIRED_PERMS); }

	private SQLiteDatabase sqlLiteDatabase = null;

	public StreetsDBHelper(Context context) { super(context, DATABASE_NAME, null, DATABASE_VERSION); }

    public void onCreate(SQLiteDatabase db) {

		for (String tableName : ALL_TABLES) {
			Log.i(TAG, "Dropping table " + tableName);
			db.execSQL("DROP TABLE IF EXISTS " + tableName);
		}

		Log.i(TAG, "Recreating table " + USER_TABLE);
        db.execSQL("CREATE TABLE IF NOT EXISTS " + USER_TABLE + " (" +
				"symbiosis_id INT(11) PRIMARY KEY," +
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

		Log.i(TAG, "Recreating table " + PLACE_TYPE_TABLE);
		db.execSQL("CREATE TABLE IF NOT EXISTS " + PLACE_TYPE_TABLE + " (" +
				"type_id INT(11) PRIMARY KEY," +
				"type VARCHAR(50))");

		Log.i(TAG, "Recreating table " + FRIEND_TABLE);
		db.execSQL("CREATE TABLE IF NOT EXISTS " + FRIEND_TABLE + " (" +
				"symbiosis_id INT(11) PRIMARY KEY," +
				"last_place_id INT(11)," +
				"home_place_id INT(11))");

		Log.i(TAG, "Recreating table " + SUPPLIER_TABLE);
		db.execSQL("CREATE TABLE IF NOT EXISTS " + SUPPLIER_TABLE + " (" +
				"symbiosis_id INT(11) PRIMARY KEY," +
				"last_geolocation_id INT(11))");

		Log.i(TAG, "Recreating table " + USER_PREFERENCES);
		db.execSQL("CREATE TABLE IF NOT EXISTS " + USER_PREFERENCES + " (" +
				"name VARCHAR(20) PRIMARY KEY," +
				"preference VARCHAR(20)," +
				"description VARCHAR(20)," +
				"data_type VARCHAR(20))");

		db.execSQL("REPLACE INTO " + USER_PREFERENCES + " (name, preference, description, data_type) VALUES " +
				"('show_intro',			'1', 'Enable intro video',	'boolean')," +
				"('auto_login',			'0', 'Login automatically',	'boolean')," +
				"('suggest_gps',		'1', 'Ask to enable GPS',	'boolean')," +
				"('auto_enable_gps',	'1', 'Auto enable GPS',		'boolean')," +
				"('show_location',		'1', 'Share location',		'boolean')," +
				"('show_distance',		'1', 'Share distance',		'boolean')," +
				"('receive_requests',	'1', 'Allow interaction',	'boolean')," +
				"('show_history', 		'1', 'Show history',		'boolean')," +
				"('ask_on_exit', 		'1', 'Check before exiting','boolean')," +
				"('request_gps_perms',	'1', 'Ask GPS permissions', 'boolean')");


		Log.i(TAG, "Recreating table " + REQUIRED_PERMS);
		db.execSQL("CREATE TABLE IF NOT EXISTS " + REQUIRED_PERMS + " (permission VARCHAR(50) PRIMARY KEY)");

		Log.i(TAG, "Recreating table " + PLACE_TYPES_OF_INTEREST);
		db.execSQL("CREATE TABLE IF NOT EXISTS " + PLACE_TYPES_OF_INTEREST + " (place_type_id VARCHAR(50) PRIMARY KEY)");

	    ArrayList <String> defaultPlaces = PlaceTypes.getDefaultPlaces();

        db.execSQL("DELETE FROM " + PLACE_TYPES_OF_INTEREST);

		if (defaultPlaces.size() >= 1)
	    {
			String sql = "INSERT INTO " + PLACE_TYPES_OF_INTEREST + " (place_type_id) SELECT type_id FROM "
				+ PLACE_TYPE_TABLE + " WHERE type IN (";
		    for (String place : defaultPlaces)
		    {
			    sql += "'" + place + "',";
		    }
			sql = sql.substring(0, sql.length() - 1) + ")";

		    Log.d(TAG, "Inserting default places. SQL: " + sql);

		    db.execSQL(sql);
	    }

    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		onUpgrade(db, oldVersion, newVersion);
    }

	private SQLiteDatabase getStreetsWritableDatabase()
	{
		if (sqlLiteDatabase == null)
		{
			sqlLiteDatabase = getWritableDatabase();
		}
		return sqlLiteDatabase;
	}

	//database functions
	public HashMap<String, HashMap<String, String>> getUserPreferences()
	{
		try
		{
			Log.i(TAG, "Checking user preferences.");
            HashMap<String, String> userPreferenceValues = new HashMap<>();
            HashMap<String, String> userPreferenceDescriptions = new HashMap<>();
            HashMap<String, String> userPreferenceTypes = new HashMap<>();

			Cursor preferences = getStreetsWritableDatabase().rawQuery(
				"SELECT name, preference, description, data_type FROM " + USER_PREFERENCES, null);

			Log.i(TAG, "Found " + preferences.getCount() + " preferences.");
			preferences.moveToFirst();
			while (!preferences.isAfterLast())
			{
				Log.d(TAG, "Adding "
					+ preferences.getString(3) + " preference: "
					+ preferences.getString(0) + " = "
					+ preferences.getString(1) + " : "
					+ preferences.getString(2));
                userPreferenceValues		.put(preferences.getString(0), preferences.getString(1)); //value
				userPreferenceDescriptions	.put(preferences.getString(0), preferences.getString(2)); //description
				userPreferenceTypes			.put(preferences.getString(0), preferences.getString(3)); //data_type
				preferences.moveToNext();
			}

			HashMap<String, HashMap<String, String>> prefInfo = new HashMap<>();
			prefInfo.put("values", userPreferenceValues);
			prefInfo.put("descriptions", userPreferenceDescriptions);
			prefInfo.put("data_types", userPreferenceTypes);

			return prefInfo;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			Log.e(TAG, "Failed to get user preferences: " + e.getMessage(), e);
			return null;
		}
	}

	public void setUserPreference(String preference, String value, String description, String data_type)
	{
		Log.d(TAG, "Updating user preference " + preference + " to " + value);
		getStreetsWritableDatabase().execSQL("REPLACE INTO " + USER_PREFERENCES +
				" (name, preference, description, data_type) VALUES" +
				" ('" + preference + "','" + value + "','" + description + "','" + data_type + "')");
	}

	public ArrayList<Place> getNearbyFriendLocations()
	{
		try
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

			return resultList;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			Log.e(TAG, "Failed to get nearby friend locations: " + e.getMessage(), e);
			return null;
		}
	}

	public ArrayList<String> getPlacesOfInterest()
	{
		try
		{
			Log.i(TAG, "Getting list of places of interest .");
			ArrayList<String> resultList = new ArrayList<>();
			Cursor places = getStreetsWritableDatabase().rawQuery(
					"SELECT pt.type FROM " + PLACE_TYPES_OF_INTEREST + " poi, " + PLACE_TYPE_TABLE + " pt " +
					"WHERE poi.place_type_id = pt.type_id", null);

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
		catch (Exception e)
		{
			e.printStackTrace();
			Log.e(TAG, "Failed to get nearby friend locations: " + e.getMessage(), e);
			return null;
		}
	}

    public void addPlaceOfInterest(String place) {
        String sql = "REPLACE INTO " + PLACE_TYPES_OF_INTEREST + " (place_type_id)" +
                " SELECT place_type_id FROM place_type WHERE type = '" + place + "'";
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
		try
		{
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
		catch (Exception e)
		{
			e.printStackTrace();
			Log.e(TAG, "Failed to get outstanding permissions: " + e.getMessage(), e);
			return null;
		}
	}
}
