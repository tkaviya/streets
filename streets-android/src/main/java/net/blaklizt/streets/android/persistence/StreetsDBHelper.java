package net.blaklizt.streets.android.persistence;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import net.blaklizt.streets.android.Streets;
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
    private static final String TAG = Streets.TAG + "_" + StreetsDBHelper.class.getSimpleName();

    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 2;

	private static final String DATABASE_NAME = "Neighbourhood.db";

	private static final String PLACE_TABLE = "Place";

	private static final String USER_TABLE = "User";

	private static final String FRIEND_TABLE = "Friend";

	private static final String SELECTED_PLACES = "SelectedPlaces";

	private SQLiteDatabase sqlLiteDatabase = null;

    public StreetsDBHelper(Context context) { super(context, DATABASE_NAME, null, DATABASE_VERSION); }

    public void onCreate(SQLiteDatabase db) {

        db.execSQL("CREATE TABLE IF NOT EXISTS " + USER_TABLE + " (" +
                "SymbiosisID INT(11)," +
                "Username VARCHAR(50)," +
                "Password VARCHAR(50)," +
                "Email VARCHAR(50)," +
                "LastPlaceID INT(11)," +
                "HomePlaceID INT(11)," +
                "Type VARCHAR(50))");

        db.execSQL("CREATE TABLE IF NOT EXISTS " + FRIEND_TABLE + " (" +
                "SymbiosisID INT(11)," +
                "Username VARCHAR(50)," +
                "UserGroupID VARCHAR(50)," +
                "Email VARCHAR(50)," +
                "LastPlaceID INT(11)," +
                "HomePlaceID INT(11))");

        db.execSQL("CREATE TABLE IF NOT EXISTS " + PLACE_TABLE + " (" +
                "PlaceID INT(11)," +
                "Name VARCHAR(50)," +
                "Reference VARCHAR(100)," +
                "Latitude DOUBLE," +
                "Longitude DOUBLE," +
                "Type VARCHAR(50))");

	    db.execSQL("CREATE TABLE IF NOT EXISTS " + SELECTED_PLACES + " (PlaceType VARCHAR(50))");

        db.execSQL("INSERT INTO " + PLACE_TABLE + " VALUES (0,'Home',NULL,-26.092154565,28.216708950,'ME')");
        db.execSQL("INSERT INTO " + USER_TABLE + " VALUES (0,'tkaviya','ImTheStreets','tsungai.kaviya@gmail.com',0,0,'ME')");

	    LinkedList <String> defaultPlaces = PlaceTypes.getDefaultPlaces();

	    if (defaultPlaces.size() >= 1)
	    {
		    //create bulk insert SQL
			String sql = "INSERT INTO " + SELECTED_PLACES + " SELECT '" + defaultPlaces.pop() + "' AS PlaceType ";
		    for (String place : defaultPlaces)
		    {
			    sql += " UNION SELECT '" + place + "' ";
		    }

		    Log.d(TAG, "Inserting default places. SQL: " + sql);

		    db.execSQL(sql);
	    }

    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + USER_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + PLACE_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + FRIEND_TABLE);
	    db.execSQL("DROP TABLE IF EXISTS " + SELECTED_PLACES);
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + USER_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + PLACE_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + FRIEND_TABLE);
	    db.execSQL("DROP TABLE IF EXISTS " + SELECTED_PLACES);
        onCreate(db);
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
	public LinkedList<Place> getNearbyFriendLocations()
	{
		try
		{
			Log.i(TAG, "Searching for nearby friends.");
			LinkedList<Place> resultList = new LinkedList<>();
			Cursor friends = getStreetsWritableDatabase().rawQuery(
				"SELECT ft.Username, pt.Reference, pt.Latitude, pt.Longitude, pt.Type " +
				"FROM " + FRIEND_TABLE + " ft, " + PLACE_TABLE + " pt " +
				"WHERE ft.LastPlaceID = pt.PlaceID", null);

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
			Cursor places = getStreetsWritableDatabase().rawQuery("SELECT PlaceType FROM " + StreetsDBHelper.SELECTED_PLACES, null);

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
}