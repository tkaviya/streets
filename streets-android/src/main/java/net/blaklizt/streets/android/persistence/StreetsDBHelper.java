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

import java.util.LinkedList;

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

	private static final String PLACE_TABLE = "place";

	private static final String PLACE_TYPE_TABLE = "place_type";

    private static final String LOCATION_HISTORY_TABLE = "location_history";

	private static final String USER_TABLE = "user";

	private static final String FRIEND_TABLE = "friend";

	private static final String SUPPLIER_TABLE = "supplier";

	private static final String USER_PREFERENCES = "user_preferences";

	private static final String PLACE_TYPES_OF_INTEREST = "selected_places";

	private SQLiteDatabase sqlLiteDatabase = null;

	public StreetsDBHelper(Context context) { super(context, DATABASE_NAME, null, DATABASE_VERSION); }

    public void onCreate(SQLiteDatabase db) {

        db.execSQL("CREATE TABLE IF NOT EXISTS " + USER_TABLE + " (" +
				"symbiosis_id INT(11)," +
				"imei VARCHAR(256)," +
				"imsi VARCHAR(128)," +
				"password VARCHAR(50)," +
				"last_location_id INT(11)," +
				"home_place_id INT(11)," +
				"type VARCHAR(50))");

        //fixed places that are never remove
        db.execSQL("CREATE TABLE IF NOT EXISTS " + PLACE_TABLE + " (" +
				"place_id INT(11)," +
				"place_type_id INT(11)," +
				"name VARCHAR(50)," +
				"reference VARCHAR(100)," +
				"latitude DOUBLE," +
				"longitude DOUBLE," +
				"type VARCHAR(50))");

        //table the grows and is truncated after a while
        db.execSQL("CREATE TABLE IF NOT EXISTS " + LOCATION_HISTORY_TABLE + " (" +
                "location_history_id INT(11)," +
                "latitude DOUBLE," +
                "longitude DOUBLE," +
                "update_date_time DATE))");

		db.execSQL("CREATE TABLE IF NOT EXISTS " + PLACE_TYPE_TABLE + " (" +
                "type_id INT(11)," +
                "type VARCHAR(50))");

		db.execSQL("CREATE TABLE IF NOT EXISTS " + FRIEND_TABLE + " (" +
				"symbiosis_id INT(11)," +
				"last_place_id INT(11)," +
				"home_place_id INT(11))");

		db.execSQL("CREATE TABLE IF NOT EXISTS " + SUPPLIER_TABLE + " (" +
				"symbiosis_id INT(11)," +
				"last_geolocation_id INT(11))");

		db.execSQL("CREATE TABLE IF NOT EXISTS " + USER_PREFERENCES + " (" +
				"name VARCHAR(50)," +
				"preference VARCHAR(50))");

	    db.execSQL("CREATE TABLE IF NOT EXISTS " + PLACE_TYPES_OF_INTEREST + " (place_type_id VARCHAR(50))");

		db.execSQL("REPLACE INTO " + USER_PREFERENCES + " VALUES (" +
                " ('symbiosis_user_id', SELECT symbiosis_id FROM " + USER_TABLE + " LIMIT 1)," +
                " ('show_intro', '1')," +
                " ('suggest_gps', '1')," +
                " ('auto_enable_gps', '1')," +
                " ('show_location', '1')," +
                " ('show_distance', '1')," +
                " ('receive_requests', '1')," +
                " ('show_history', '1')," +
                " ('ask_on_exit', '1')" +
				")");

	    LinkedList <String> defaultPlaces = PlaceTypes.getDefaultPlaces();

        db.execSQL("DELETE FROM " + PLACE_TYPES_OF_INTEREST);

		if (defaultPlaces.size() >= 1)
	    {
			String sql = "INSERT INTO " + PLACE_TYPES_OF_INTEREST + " (place_type_id) SELECT type_id FROM "
				+ PLACE_TYPE_TABLE + " WHERE type IN (";
		    for (String place : defaultPlaces)
		    {
			    sql += "'" + place + "',";
		    }
			sql = sql.substring(0, sql.length() - 2) + ")";

		    Log.d(TAG, "Inserting default places. SQL: " + sql);

		    db.execSQL(sql);
	    }

    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + USER_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + PLACE_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + FRIEND_TABLE);
		db.execSQL("DROP TABLE IF EXISTS " + USER_PREFERENCES);
	    db.execSQL("DROP TABLE IF EXISTS " + PLACE_TYPES_OF_INTEREST);
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
	public LinkedList<UserPreference> getUserPreferences()
	{
		try
		{
			Log.i(TAG, "Checking user preferences.");
			LinkedList<UserPreference> userPreferences = new LinkedList<>();

			Cursor preferences = getStreetsWritableDatabase().rawQuery(
				"SELECT * FROM " + USER_PREFERENCES, null);

			Log.i(TAG, "Found " + preferences.getCount() + " preferences.");
			preferences.moveToFirst();
			while (!preferences.isAfterLast())
			{
				for (int c = 0; c < preferences.getColumnCount(); c++) {
					Log.i(TAG, "Adding preference: " + preferences.getColumnName(c) + " = " + preferences.getString(c));
					userPreferences.add(new UserPreference(preferences.getColumnName(c), preferences.getString(c)));
				}
				preferences.moveToNext();
			}

			return userPreferences;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			Log.e(TAG, "Failed to get user preferences: " + e.getMessage(), e);
			return null;
		}
	}

	public void setUserPreference(String preference, String value)
	{
		Log.i(TAG, "Updating user preference " + preference + " to " + value);
		getStreetsWritableDatabase().execSQL("REPLACE INTO " + USER_PREFERENCES +
				" (name, preference) VALUES ('"+ preference + "','" + value + "')");
	}

	public LinkedList<Place> getNearbyFriendLocations()
	{
		try
		{
			Log.i(TAG, "Searching for nearby friends.");
			LinkedList<Place> resultList = new LinkedList<>();
			Cursor friends = getStreetsWritableDatabase().rawQuery(
				"SELECT ft.username, pt.reference, pt.latitude, pt.longitude, pt.place_id " +
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

	public LinkedList<String> getPlacesOfInterest()
	{
		try
		{
			Log.i(TAG, "Getting list of places of interest .");
			LinkedList<String> resultList = new LinkedList<>();
			Cursor places = getStreetsWritableDatabase().rawQuery(
					"SELECT pt.place_type FROM " + PLACE_TYPES_OF_INTEREST + " poi, " + PLACE_TYPE_TABLE + " pt," +
					"WHERE poi.place_type_id = pt.type_id", null);

			Log.i(TAG, "Found " + places.getCount() + " places.");
			places.moveToFirst();
			while (!places.isAfterLast())
			{
				Log.i(TAG, "Adding place: " + places.getString(0));
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
}
