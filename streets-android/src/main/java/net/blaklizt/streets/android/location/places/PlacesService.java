package net.blaklizt.streets.android.location.places;

/**
 * Created with IntelliJ IDEA.
 * User: tkaviya
 * Date: 6/25/14
 * Time: 12:32 AM
 * To change this template use File | Settings | File Templates.
 */

import android.util.Log;

import net.blaklizt.streets.android.activity.Startup;
import net.blaklizt.streets.android.common.StreetsCommon;
import net.blaklizt.streets.android.common.utils.Optional;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class PlacesService {

    private static final String TAG = StreetsCommon.getTag(PlacesService.class);
    private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";

    private static final String TYPE_AUTOCOMPLETE = "/autocomplete";
    private static final String TYPE_DETAILS = "/details";
    private static final String TYPE_SEARCH = "/search";
    private static final String TYPE_NEARBY_SEARCH = "/nearbysearch";

    private static final String OUT_JSON = "/json";

    private static final String PLACES_API_KEY = "AIzaSyD3e6Act_-IHtKtWmtRwyt6ftLQ90eOvcE";

//    public static ArrayList<Place> autocomplete(String input) {
//        ArrayList<Place> resultList = null;
//
//        HttpURLConnection conn = null;
//        StringBuilder jsonResults = new StringBuilder();
//        try {
//            StringBuilder sb = new StringBuilder(PLACES_API_BASE);
//            sb.append(TYPE_AUTOCOMPLETE);
//            sb.append(OUT_JSON);
//            sb.append("?sensor=true");
//            sb.append("&key=" + PLACES_API_KEY);
//            sb.append("&input=" + URLEncoder.encode(input, "utf8"));
//
//            URL url = new URL(sb.toString());
//            conn = (HttpURLConnection) url.openConnection();
//            InputStreamReader in = new InputStreamReader(conn.getInputStream());
//
//            int read;
//            char[] buff = new char[1024];
//            while ((read = in.read(buff)) != -1) {
//                jsonResults.append(buff, 0, read);
//            }
//        } catch (MalformedURLException e) {
//            Log.e(TAG, "Error processing Places API URL", e);
//            return resultList;
//        } catch (IOException e) {
//            Log.e(TAG, "Error connecting to Places API", e);
//            return resultList;
//        } finally {
//            if (conn != null) {
//                conn.disconnect();
//            }
//        }
//
//        try {
//            // Create a JSON object hierarchy from the results
//            JSONObject jsonObj = new JSONObject(jsonResults.toString());
//            JSONArray predsJsonArray = jsonObj.getJSONArray("predictions");
//
//            // Extract the Place descriptions from the results
//            resultList = new ArrayList<Place>(predsJsonArray.length());
//            for (int i = 0; i < predsJsonArray.length(); i++) {
//                Place place = new Place(
//                    predsJsonArray.getJSONObject(i).getString("description"),
//                    predsJsonArray.getJSONObject(i).getString("reference"),0.0,0.0,null);
//                resultList.add(place);
//            }
//        } catch (JSONException e) {
//            Log.e(TAG, "Error processing JSON results", e);
//        }
//
//        return resultList;
//    }

//    public static ArrayList<Place> search(String keyword, double lat, double lng, int radius, ArrayList<String> types) {
//        Log.i(TAG, "Searching for places near current location");
//        ArrayList<Place> resultList = null;
//
//        HttpURLConnection conn = null;
//        StringBuilder jsonResults = new StringBuilder();
//        try {
//            StringBuilder sb = new StringBuilder(PLACES_API_BASE);
//            sb.append(TYPE_SEARCH);
//            sb.append(OUT_JSON);
//            sb.append("?sensor=false");
//            sb.append("&key=" + PLACES_API_KEY);
//            sb.append("&keyword=" + URLEncoder.encode(keyword, "utf8"));
//            sb.append("&location=" + String.valueOf(lat) + "," + String.valueOf(lng));
//            sb.append("&radius=" + String.valueOf(radius));
//
//            if (types.size() > 0) sb.append("&types=");
//            for (int c = 0; c < types.size(); c++) {
//                sb.append(types.get(c));
//                if (c != types.size() - 1) sb.append("|");
//            }
//
//            Log.i(TAG, "Connecting to URL " + sb.toString());
//
//            URL url = new URL(sb.toString());
//            conn = (HttpURLConnection) url.openConnection();
//            InputStreamReader in = new InputStreamReader(conn.getInputStream());
//
//            int read;
//            char[] buff = new char[1024];
//            while ((read = in.read(buff)) != -1) {
//                jsonResults.append(buff, 0, read);
//            }
//
//            Log.d(TAG, "Got result: " + jsonResults.toString());
//
//        } catch (MalformedURLException e) {
//            Log.e(TAG, "Error processing Places API URL", e);
//            return resultList;
//        } catch (IOException e) {
//            Log.e(TAG, "Error connecting to Places API", e);
//            return resultList;
//        } finally {
//            if (conn != null) {
//                conn.disconnect();
//            }
//        }
//
//        try {
//            // Create a JSON object hierarchy from the results
//            JSONObject jsonObj = new JSONObject(jsonResults.toString());
//            JSONArray predsJsonArray = jsonObj.getJSONArray("results");
//            Log.i(TAG, "Response Array: " + jsonObj.toString());
//
//            // Extract the Place descriptions from the results
//            resultList = new ArrayList<Place>(predsJsonArray.length());
//            for (int i = 0; i < predsJsonArray.length(); i++) {
//                Place place = new Place(
//                        predsJsonArray.getJSONObject(i).getString("name"),
//                        predsJsonArray.getJSONObject(i).getString("reference"), lat, lng, null);
//                Log.i(TAG, "Adding place to response: " + place.name + " : " + place.reference);
//
//                resultList.add(place);
//            }
//        } catch (JSONException e) {
//            Log.e(TAG, "Error processing JSON results", e);
//        }
//
//        return resultList;
//    }

    public static Optional<ArrayList<Place>> nearby_search(double lat, double lng, int radius, ArrayList<String> types) {
        Log.i(TAG, "Searching for places near current location");
        ArrayList<Place> resultList = null;
        HttpURLConnection conn = null;
        StringBuilder jsonResults = new StringBuilder();
        try {
            StringBuilder sb = new StringBuilder(PLACES_API_BASE);

            sb.append(TYPE_NEARBY_SEARCH).append(OUT_JSON)
                    .append("?sensor=false").append("&key=" + PLACES_API_KEY)
                    .append("&location=").append(String.valueOf(lat)).append(",").append(String.valueOf(lng))
                    .append("&radius=").append(String.valueOf(radius));

            if (types != null && types.size() > 0) {
                sb.append("&types=");
                for (int c = 0; c < types.size(); c++) {
                    sb.append(types.get(c));
                    if (c != types.size() - 1) { sb.append("|"); }
                }
            }

            Log.i(TAG, "Connecting to URL " + sb.toString());

            URL url = new URL(sb.toString());
            conn = (HttpURLConnection) url.openConnection();
            InputStreamReader in = new InputStreamReader(conn.getInputStream());

            int read;
            char[] buff = new char[1024];
            while ((read = in.read(buff)) != -1) {
                jsonResults.append(buff, 0, read);
            }

            Log.d(TAG, "Got result: " + jsonResults.toString());

            // Create a JSON object hierarchy from the results
            JSONObject jsonObj = new JSONObject(jsonResults.toString());
            JSONArray predsJsonArray = jsonObj.getJSONArray("results");
            Log.i(TAG, "Response Array: " + jsonObj.toString());

            // Extract the Place descriptions from the results
            resultList = Startup.getStreetsCommon().getStreetsDBHelper().getNearbyFriendLocations();

            for (int i = 0; i < predsJsonArray.length(); i++) {
                JSONArray typeArray = predsJsonArray.getJSONObject(i).getJSONArray("types");
                Place place = new Place(
                        predsJsonArray.getJSONObject(i).getString("name"),
                        predsJsonArray.getJSONObject(i).getString("reference"),
                        predsJsonArray.getJSONObject(i).getJSONObject("geometry").getJSONObject("location").getDouble("lat"),
                        predsJsonArray.getJSONObject(i).getJSONObject("geometry").getJSONObject("location").getDouble("lng"),
                        typeArray.getString(0).replaceAll("_", " "),
//					CommonUtilities.toCamelCase(typeArray.getString(0).replaceAll("_", " ")),
                        predsJsonArray.getJSONObject(i).getString("icon")
                );
                place.formatted_address = predsJsonArray.getJSONObject(i).getString("vicinity");
                Log.i(TAG, "Adding place to response: " + place.name + " : " + place.type);
                resultList.add(place);
            }
        } catch (MalformedURLException e) {
            Log.e(TAG, "Error processing Places API URL for nearby search", e);
        } catch (IOException e) {
            Log.e(TAG, "Error connecting to Places API for nearby search", e);
        } catch (JSONException e) {
            Log.e(TAG, "Error processing nearby search JSON results", e);
        } finally { if (conn != null) { conn.disconnect(); } }

        return Optional.ofNullable(resultList);
    }

//    public static Place details(String reference) {
//        HttpURLConnection conn = null;
//        StringBuilder jsonResults = new StringBuilder();
//        try {
//            StringBuilder sb = new StringBuilder(PLACES_API_BASE);
//            sb.append(TYPE_DETAILS);
//            sb.append(OUT_JSON);
//            sb.append("?sensor=false");
//            sb.append("&key=" + PLACES_API_KEY);
//            sb.append("&reference=" + URLEncoder.encode(reference, "utf8"));
//
//            URL url = new URL(sb.toString());
//            conn = (HttpURLConnection) url.openConnection();
//            InputStreamReader in = new InputStreamReader(conn.getInputStream());
//
//            // Load the results into a StringBuilder
//            int read;
//            char[] buff = new char[1024];
//            while ((read = in.read(buff)) != -1) {
//                jsonResults.append(buff, 0, read);
//            }
//        } catch (MalformedURLException e) {
//            Log.e(TAG, "Error processing Places API URL", e);
//            return null;
//        } catch (IOException e) {
//            Log.e(TAG, "Error connecting to Places API", e);
//            return null;
//        } finally {
//            if (conn != null) {
//                conn.disconnect();
//            }
//        }
//
//        Place place = null;
//        try {
//            // Create a JSON object hierarchy from the results
//            JSONObject jsonObj = new JSONObject(jsonResults.toString()).getJSONObject("result");
//            Log.i(TAG, "Response Array: " + jsonObj.toString());
//            place = new Place();
//            place.icon = jsonObj.getString("icon");
//            place.name = jsonObj.getString("name");
//            place.formatted_address = jsonObj.getString("formatted_address");
//            if (jsonObj.has("formatted_phone_number")) {
//                place.formatted_phone_number = jsonObj.getString("formatted_phone_number");
//            }
//        } catch (JSONException e) {
//            Log.e(Mape) {
//            Log.e(TAG, "Error processing JSON results", e);
//        }
//
//        return place;
//    }
}
