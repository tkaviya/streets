package net.blaklizt.streets.android.location.places;

/**
 * Created with IntelliJ IDEA.
 * User: tkaviya
 * Date: 6/25/14
 * Time: 12:32 AM
 * To change this template use File | Settings | File Templates.
 */

import android.util.Log;
import net.blaklizt.streets.android.TheStreets;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

//import net.blaklizt.streets.common.configuration.Configuration;

/**
 * @author saxman
 */
public class PlacesService {
    private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";

    private static final String TYPE_AUTOCOMPLETE = "/autocomplete";
    private static final String TYPE_DETAILS = "/details";
    private static final String TYPE_SEARCH = "/search";
    private static final String TYPE_NEARBY_SEARCH = "/nearbysearch";

    private static final String OUT_JSON = "/json";

    // KEY!
    private static final String API_KEY = "AIzaSyD3e6Act_-IHtKtWmtRwyt6ftLQ90eOvcE";//Configuration.getInstance().getProperty("googleAPIKey");

    public static ArrayList<Place> autocomplete(String input) {
        ArrayList<Place> resultList = null;

        HttpURLConnection conn = null;
        StringBuilder jsonResults = new StringBuilder();
        try {
            StringBuilder sb = new StringBuilder(PLACES_API_BASE);
            sb.append(TYPE_AUTOCOMPLETE);
            sb.append(OUT_JSON);
            sb.append("?sensor=true");
            sb.append("&key=" + API_KEY);
            sb.append("&input=" + URLEncoder.encode(input, "utf8"));

            URL url = new URL(sb.toString());
            conn = (HttpURLConnection) url.openConnection();
            InputStreamReader in = new InputStreamReader(conn.getInputStream());

            int read;
            char[] buff = new char[1024];
            while ((read = in.read(buff)) != -1) {
                jsonResults.append(buff, 0, read);
            }
        } catch (MalformedURLException e) {
            Log.e(TheStreets.TAG, "Error processing Places API URL", e);
            return resultList;
        } catch (IOException e) {
            Log.e(TheStreets.TAG, "Error connecting to Places API", e);
            return resultList;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }

        try {
            // Create a JSON object hierarchy from the results
            JSONObject jsonObj = new JSONObject(jsonResults.toString());
            JSONArray predsJsonArray = jsonObj.getJSONArray("predictions");

            // Extract the Place descriptions from the results
            resultList = new ArrayList<Place>(predsJsonArray.length());
            for (int i = 0; i < predsJsonArray.length(); i++) {
                Place place = new Place();
                place.reference = predsJsonArray.getJSONObject(i).getString("reference");
                place.name = predsJsonArray.getJSONObject(i).getString("description");
                resultList.add(place);
            }
        } catch (JSONException e) {
            Log.e(TheStreets.TAG, "Error processing JSON results", e);
        }

        return resultList;
    }

    public static ArrayList<Place> search(String keyword, double lat, double lng, int radius, ArrayList<String> types) {
        Log.i(TheStreets.TAG, "Searching for places near current location");
        ArrayList<Place> resultList = null;

        HttpURLConnection conn = null;
        StringBuilder jsonResults = new StringBuilder();
        try {
            StringBuilder sb = new StringBuilder(PLACES_API_BASE);
            sb.append(TYPE_SEARCH);
            sb.append(OUT_JSON);
            sb.append("?sensor=false");
            sb.append("&key=" + API_KEY);
            sb.append("&keyword=" + URLEncoder.encode(keyword, "utf8"));
            sb.append("&location=" + String.valueOf(lat) + "," + String.valueOf(lng));
            sb.append("&radius=" + String.valueOf(radius));

            if (types.size() > 0) sb.append("&types=");
            for (int c = 0; c < types.size(); c++) {
                sb.append(types.get(c));
                if (c != types.size() - 1) sb.append("|");
            }

            Log.i(TheStreets.TAG, "Connecting to URL " + sb.toString());

            URL url = new URL(sb.toString());
            conn = (HttpURLConnection) url.openConnection();
            InputStreamReader in = new InputStreamReader(conn.getInputStream());

            int read;
            char[] buff = new char[1024];
            while ((read = in.read(buff)) != -1) {
                jsonResults.append(buff, 0, read);
            }

            Log.i(TheStreets.TAG, "Got result: " + jsonResults.toString());

        } catch (MalformedURLException e) {
            Log.e(TheStreets.TAG, "Error processing Places API URL", e);
            return resultList;
        } catch (IOException e) {
            Log.e(TheStreets.TAG, "Error connecting to Places API", e);
            return resultList;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }

        try {
            // Create a JSON object hierarchy from the results
            JSONObject jsonObj = new JSONObject(jsonResults.toString());
            JSONArray predsJsonArray = jsonObj.getJSONArray("results");
            Log.i(TheStreets.TAG, "Response Array: " + jsonObj.toString());

            // Extract the Place descriptions from the results
            resultList = new ArrayList<Place>(predsJsonArray.length());
            for (int i = 0; i < predsJsonArray.length(); i++) {
                Place place = new Place();
                place.reference = predsJsonArray.getJSONObject(i).getString("reference");
                place.name = predsJsonArray.getJSONObject(i).getString("name");

                Log.i(TheStreets.TAG, "Adding place to response: " + place.name + " : " + place.reference);

                resultList.add(place);
            }
        } catch (JSONException e) {
            Log.e(TheStreets.TAG, "Error processing JSON results", e);
        }

        return resultList;
    }

    public static ArrayList<Place> nearby_search(double lat, double lng, int radius, ArrayList<String> types) {
        Log.i(TheStreets.TAG, "Searching for places near current location");
        ArrayList<Place> resultList = null;

        HttpURLConnection conn = null;
        StringBuilder jsonResults = new StringBuilder();
        try {
            StringBuilder sb = new StringBuilder(PLACES_API_BASE);
            sb.append(TYPE_NEARBY_SEARCH);
            sb.append(OUT_JSON);
            sb.append("?sensor=false");
            sb.append("&key=" + API_KEY);
            sb.append("&location=" + String.valueOf(lat) + "," + String.valueOf(lng));
            sb.append("&radius=" + String.valueOf(radius));

            if (types.size() > 0) sb.append("&types=");
            for (int c = 0; c < types.size(); c++) {
                sb.append(types.get(c));
                if (c != types.size() - 1) sb.append("|");
            }

            Log.i(TheStreets.TAG, "Connecting to URL " + sb.toString());

            URL url = new URL(sb.toString());
            conn = (HttpURLConnection) url.openConnection();
            InputStreamReader in = new InputStreamReader(conn.getInputStream());

            int read;
            char[] buff = new char[1024];
            while ((read = in.read(buff)) != -1) {
                jsonResults.append(buff, 0, read);
            }

            Log.i(TheStreets.TAG, "Got result: " + jsonResults.toString());

        } catch (MalformedURLException e) {
            Log.e(TheStreets.TAG, "Error processing Places API URL", e);
            return resultList;
        } catch (IOException e) {
            Log.e(TheStreets.TAG, "Error connecting to Places API", e);
            return resultList;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }

        try {
            // Create a JSON object hierarchy from the results
            JSONObject jsonObj = new JSONObject(jsonResults.toString());
            JSONArray predsJsonArray = jsonObj.getJSONArray("results");
            Log.i(TheStreets.TAG, "Response Array: " + jsonObj.toString());

            // Extract the Place descriptions from the results
            resultList = new ArrayList<>(predsJsonArray.length());
            for (int i = 0; i < predsJsonArray.length(); i++) {
                Place place = new Place();
                place.reference = predsJsonArray.getJSONObject(i).getString("reference");
                place.name = predsJsonArray.getJSONObject(i).getString("name");
                JSONArray typeArray = predsJsonArray.getJSONObject(i).getJSONArray("types");
                place.icon = predsJsonArray.getJSONObject(i).getString("icon");
                for(int t = 0; t < typeArray.length(); t++) { place.type += (place.type.length() == 0 ? "" : "|") + typeArray.getString(t); }
                place.latitude = predsJsonArray.getJSONObject(i).getJSONObject("geometry").getJSONObject("location").getDouble("lat");
                place.longitude = predsJsonArray.getJSONObject(i).getJSONObject("geometry").getJSONObject("location").getDouble("lng");
                place.formatted_address = predsJsonArray.getJSONObject(i).getString("vicinity");
                Log.i(TheStreets.TAG, "Adding place to response: " + place.name + " : " + place.type.toString());
                resultList.add(place);
            }
        } catch (JSONException e) {
            Log.e(TheStreets.TAG, "Error processing JSON results", e);
        }

        return resultList;
    }


    public static Place details(String reference) {
        HttpURLConnection conn = null;
        StringBuilder jsonResults = new StringBuilder();
        try {
            StringBuilder sb = new StringBuilder(PLACES_API_BASE);
            sb.append(TYPE_DETAILS);
            sb.append(OUT_JSON);
            sb.append("?sensor=false");
            sb.append("&key=" + API_KEY);
            sb.append("&reference=" + URLEncoder.encode(reference, "utf8"));

            URL url = new URL(sb.toString());
            conn = (HttpURLConnection) url.openConnection();
            InputStreamReader in = new InputStreamReader(conn.getInputStream());

            // Load the results into a StringBuilder
            int read;
            char[] buff = new char[1024];
            while ((read = in.read(buff)) != -1) {
                jsonResults.append(buff, 0, read);
            }
        } catch (MalformedURLException e) {
            Log.e(TheStreets.TAG, "Error processing Places API URL", e);
            return null;
        } catch (IOException e) {
            Log.e(TheStreets.TAG, "Error connecting to Places API", e);
            return null;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }

        Place place = null;
        try {
            // Create a JSON object hierarchy from the results
            JSONObject jsonObj = new JSONObject(jsonResults.toString()).getJSONObject("result");
            Log.i(TheStreets.TAG, "Response Array: " + jsonObj.toString());
            place = new Place();
            place.icon = jsonObj.getString("icon");
            place.name = jsonObj.getString("name");
            place.formatted_address = jsonObj.getString("formatted_address");
            if (jsonObj.has("formatted_phone_number")) {
                place.formatted_phone_number = jsonObj.getString("formatted_phone_number");
            }
        } catch (JSONException e) {
            Log.e(TheStreets.TAG, "Error processing JSON results", e);
        }

        return place;
    }
}
