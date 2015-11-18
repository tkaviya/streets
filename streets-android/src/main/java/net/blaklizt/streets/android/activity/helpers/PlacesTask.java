package net.blaklizt.streets.android.activity.helpers;

import android.support.design.widget.Snackbar;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import net.blaklizt.streets.android.activity.AppContext;
import net.blaklizt.streets.android.activity.MapLayout;
import net.blaklizt.streets.android.common.StreetsCommon;
import net.blaklizt.streets.android.common.TASK_TYPE;
import net.blaklizt.streets.android.common.utils.Optional;
import net.blaklizt.streets.android.location.places.Place;
import net.blaklizt.streets.android.location.places.PlaceTypes;
import net.blaklizt.streets.android.location.places.PlacesService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import static java.util.Arrays.asList;
import static net.blaklizt.streets.android.common.StreetsCommon.showSnackBar;

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
public class PlacesTask extends StreetsAbstractTask {

    private final String TAG = StreetsCommon.getTag(PlacesTask.class);
    private Optional<ArrayList<Place>> nearbyPlaces = Optional.empty();
    private HashMap<Integer, Place> map = new HashMap<>();

    public PlacesTask() {
        processDependencies = new ArrayList<>(asList(GoogleMapTask.class.getSimpleName(), LocationUpdateTask.class.getSimpleName()));
        viewDependencies = new ArrayList<>(Collections.singletonList(MapLayout.class));
        allowOnlyOnce = false;
        allowMultiInstance = false;
        taskType = TASK_TYPE.BG_PLACES_TASK;
    }

    @Override
    protected Object doInBackground(Object[] params) {
        Log.i(TAG, "+++ doInBackground +++");

        if (AppContext.getInstance().getCurrentLocation().isPresent()) {
            nearbyPlaces = PlacesService.nearby_search(
                    AppContext.getInstance().getCurrentLocation().get().getLatitude(), AppContext.getInstance().getCurrentLocation().get().getLongitude(),
                5000, PlaceTypes.getDefaultPlaces());
        }
        else {
            showSnackBar(TAG, "Current location unknown. Check location settings.", Snackbar.LENGTH_SHORT);
        }
        return null;
    }

    @Override
    protected void onPreExecuteRelay(Object[] additionalParams) {
        Log.i(TAG, "+++ onPreExecuteRelay +++");
        showSnackBar(TAG, "Updating location", Snackbar.LENGTH_LONG);
    }

    @Override
    protected void onCancelledRelay() {
        Log.i(TAG, "+++ onCancelledRelay +++");
    }

    @Override
    protected void onProgressUpdate(Object [] args) {
        Log.i(TAG, "+++ onProgressUpdate +++");
        super.onProgressUpdate();
    }

    @Override
    protected void onPostExecuteRelay(Object result) {
        Log.i(TAG, "Places task completed with nearbyPlaces = " + (nearbyPlaces != null && nearbyPlaces.isPresent() ? nearbyPlaces.get().size() : 0));
        if (nearbyPlaces != null && nearbyPlaces.isPresent()) {
            Log.i(TAG, "Processing nearby places");
            ArrayList<Place> places = nearbyPlaces.get();
            if (AppContext.getInstance().getGoogleMap().isPresent()) {
                AppContext.getInstance().getGoogleMap().get().clear();
                for (Place place : places) {
                    drawPlaceMarker(place);
                }
            }
        }
    }

    private void drawPlaceMarker(Place place){
        Log.i(TAG, "Drawing place marker for " + place.name + " at location " + place.latitude + " : " + place.longitude);
        if (AppContext.getInstance().getGoogleMap().isPresent()) {
            LatLng currentPosition = new LatLng(place.latitude, place.longitude);
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(currentPosition);
            markerOptions.snippet(place.formatted_address);

//            Ion.with(imageView)
//                    .placeholder(R.drawable.placeholder_image)
//                    .error(R.drawable.error_image)
//                    .animateLoad(spinAnimation)
//                    .animateIn(fadeInAnimation)
//                    .load("http://example.com/image.png");


            markerOptions.icon(place.icon);
            markerOptions.alpha(0.7f);
            markerOptions.title(place.name);
            Marker placeMarker = AppContext.getInstance().getGoogleMap().get().addMarker(markerOptions);

            //link marker to a place to display info later
            map.put(placeMarker.hashCode(), place);
        } else {
            Log.i(TAG, "Map not ready for ");
        }
    }
}
