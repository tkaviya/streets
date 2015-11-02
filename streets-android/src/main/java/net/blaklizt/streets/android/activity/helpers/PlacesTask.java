package net.blaklizt.streets.android.activity.helpers;

import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import net.blaklizt.streets.android.activity.MapLayout;
import net.blaklizt.streets.android.activity.Startup;
import net.blaklizt.streets.android.common.StreetsCommon;
import net.blaklizt.streets.android.common.utils.Optional;
import net.blaklizt.streets.android.location.places.Place;
import net.blaklizt.streets.android.location.places.PlacesService;

import java.util.ArrayList;
import java.util.HashMap;

/******************************************************************************
 * *
 * Created:     29 / 10 / 2015                                             *
 * Platform:    Red Hat Linux 9                                            *
 * Author:      Tich de Blak (Tsungai Kaviya)                              *
 * Copyright:   Blaklizt Entertainment                                     *
 * Website:     http://www.blaklizt.net                                    *
 * Contact:     blaklizt@gmail.com                                         *
 * *
 * This program is free software; you can redistribute it and/or modify    *
 * it under the terms of the GNU General Public License as published by    *
 * the Free Software Foundation; either version 2 of the License, or       *
 * (at your option) any later version.                                     *
 * *
 * This program is distributed in the hope that it will be useful,         *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of          *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.    See the         *
 * GNU General Public License for more details.                            *
 * *
 ******************************************************************************/
public class PlacesTask extends AsyncTask<Void, Void, Void> implements StatusChangeNotifier {

    private final String TAG = StreetsCommon.getTag(PlacesTask.class);
    private Optional<ArrayList<Place>> nearbyPlaces = Optional.empty();
    private HashMap<Integer, Place> map = new HashMap<>();
    private MapLayout mapLayout;

    public PlacesTask(MapLayout mapLayout) {
        this.mapLayout = mapLayout;
    }

    @Override
    protected Void doInBackground(Void... param) {
        Log.i(TAG, "+++ doInBackground +++");

        if (mapLayout.getCurrentLocation().get() != null) {
            nearbyPlaces = PlacesService.nearby_search(
                    mapLayout.getCurrentLocation().get().getLatitude(), mapLayout.getCurrentLocation().get().getLongitude(), 5000,
                    Startup.getStreetsCommon().getStreetsDBHelper().getPlacesOfInterest()
            );
        }
        else {
            showSnackbar("Current location unknown. Check location settings.", Snackbar.LENGTH_SHORT);
        }
        return null;
    }

    private void showSnackbar(final String text, final int duration) {
        if (mapLayout.getView() != null) {
            mapLayout.getActivity().runOnUiThread(
                new Runnable() {
                    @Override public void run() {
                        Snackbar snackbar = Snackbar.make(mapLayout.getView(), text, duration);
                        snackbar.show();
                    }
                }
            );
        }
    }

    @Override
    protected void onPreExecute() {
        Log.i(TAG, "+++ onPreExecute +++");
        super.onPreExecute();
        onStatusChange(this, SequentialTaskManager.TaskStatus.STARTED);
        showSnackbar("Updating location", Snackbar.LENGTH_LONG);
    }

    @Override
    protected void onCancelled() {
        Log.i(TAG, "+++ onCancelled +++");
        super.onCancelled();
        onStatusChange(this, SequentialTaskManager.TaskStatus.CANCELLED);
    }

    @Override
    protected void onProgressUpdate(Void... progress) {
        Log.i(TAG, "+++ onProgressUpdate +++");
        super.onProgressUpdate();
    }

    @Override
    protected void onPostExecute(Void result) {
        Log.i(TAG, "+++ onPostExecute +++");
        super.onPostExecute(result);
        onStatusChange(this, SequentialTaskManager.TaskStatus.COMPLETED);

        if (nearbyPlaces != null && nearbyPlaces.isPresent()) {
            ArrayList<Place> places = nearbyPlaces.get();

            if (mapLayout.getGoogleMap().isPresent()) {
                mapLayout.getGoogleMap().get().clear();
                for (Place place : places) {
                    drawPlaceMarker(place);
                }
            }
        }
    }

    private void drawPlaceMarker(Place place){
        Log.i(TAG, "Drawing place marker for " + place.name + " at location " + place.latitude + " : " + place.longitude);
        if (mapLayout.getGoogleMap().isPresent()) {
            LatLng currentPosition = new LatLng(place.latitude, place.longitude);
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(currentPosition);
            markerOptions.snippet(place.formatted_address);
            markerOptions.icon(place.icon);
            markerOptions.alpha(0.7f);
            markerOptions.title(place.name);
            Marker placeMarker = mapLayout.getGoogleMap().get().addMarker(markerOptions);

            //link marker to a place to display info later
            map.put(placeMarker.hashCode(), place);
        } else {
            Log.i(TAG, "Map not ready for ");
        }
    }
}
