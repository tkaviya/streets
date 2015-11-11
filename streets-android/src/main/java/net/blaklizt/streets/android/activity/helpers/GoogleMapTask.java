package net.blaklizt.streets.android.activity.helpers;

import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import net.blaklizt.streets.android.R;
import net.blaklizt.streets.android.activity.MapLayout;
import net.blaklizt.streets.android.common.StreetsCommon;
import net.blaklizt.streets.android.common.TASK_TYPE;
import net.blaklizt.streets.android.common.TaskInfo;

import java.util.ArrayList;
import java.util.Collections;

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
public class GoogleMapTask extends TaskInfo implements StreetsProviderPattern {

    private static final String TAG = StreetsCommon.getTag(GoogleMapTask.class);

    private MapLayout mapLayout;

    public GoogleMapTask() {
        super(null, new ArrayList<>(Collections.singletonList(MapLayout.class)), true, false, TASK_TYPE.BG_GOOGLE_MAP_TASK);
        mapLayout = MapLayout.getInstance();
    }

    @Override
    protected Object doInBackground(Object[] params) {

        if (mapLayout.getGoogleMap().isPresent()) {
            Log.i(TAG, "Google map already initialized");
            return null;
        }

        mapLayout.getActivity().runOnUiThread(new Runnable() {
            @Override public void run() {
                Log.i(TAG, "Initializing Google Map");

                //Create global configuration and initialize ImageLoader with this configuration
                ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(mapLayout.getActivity().getApplicationContext()).build();
                ImageLoader.getInstance().init(config);

                int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(mapLayout.getActivity().getApplicationContext());
                // Showing status
                if (status != ConnectionResult.SUCCESS) // Google Play Services are not available
                {
                    Log.i(TAG, "Google Play Services are not available");
                }
                else // Google Play Services are available
                {
                    Log.i(TAG, "Google Play Services are available");
                    // Getting reference to the SupportMapFragment of activity_main.xml
                    MapFragment fm = (MapFragment) mapLayout.getActivity().getFragmentManager().findFragmentById(R.id.map_fragment);

                    // Getting GoogleMap object from the fragment
                    mapLayout.setGoogleMap(fm.getMap());

                    Log.i(TAG, "Got Google map");

                    mapLayout.getGoogleMap().get().setMyLocationEnabled(true);

                    mapLayout.getGoogleMap().get().getUiSettings().setZoomGesturesEnabled(true);

                    mapLayout.getGoogleMap().get().getUiSettings().setZoomControlsEnabled(true);

//                  mapLayout.getGoogleMap().get().setOnMarkerClickListener(mapLayout);

                    mapLayout.getGoogleMap().get().setInfoWindowAdapter(mapLayout);

//                  mapLayout.getGoogleMap().get().setOnMapLoadedCallback(mapLayout);
                }
            }
        });

        return null;
    }

    @Override
    public void onCancelledRelay() {
        mapLayout.getGoogleMap().ifPresent(GoogleMap::stopAnimation);
    }

    @Override
    public void onTerminationRelay() {
        Log.i(TAG, "Shutting down class " + getClassName());
        mapLayout.getGoogleMap().ifPresent(GoogleMap::stopAnimation);
    }
}
