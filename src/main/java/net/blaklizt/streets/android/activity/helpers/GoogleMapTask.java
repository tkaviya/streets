package net.blaklizt.streets.android.activity.helpers;

import android.util.Log;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration.Builder;
import net.blaklizt.streets.android.R;
import net.blaklizt.streets.android.activity.MapLayout;
import net.blaklizt.streets.android.common.StreetsCommon;

import java.util.ArrayList;

import static java.util.Collections.singletonList;
import static net.blaklizt.streets.android.activity.AppContext.getAppContextInstance;
import static net.blaklizt.streets.android.activity.AppContext.getFragmentView;
import static net.blaklizt.streets.android.common.enumeration.TASK_TYPE.BG_GOOGLE_MAP_TASK;

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
public class GoogleMapTask extends StreetsAbstractTask {

    private static final String TAG = StreetsCommon.getTag(GoogleMapTask.class);

    public GoogleMapTask() {
        processDependencies = new ArrayList<>();
        viewDependencies = new ArrayList<>(singletonList(MapLayout.class));
        allowOnlyOnce = true;
        allowMultiInstance = false;
        taskType = BG_GOOGLE_MAP_TASK;
    }

//    protected void onPostExecuteRelay(Object result) {
//
//        Log.i(TAG, "Setting map onMarkerClick listener to NavigationLayout");
//        final NavigationLayout navigationLayout= (NavigationLayout)AppContext.getFragmentView(NavigationLayout.class);
//        if (AppContext.getInstance().getGoogleMap().isPresent()) {
//            AppContext.getInstance().getGoogleMap().get().setOnMarkerClickListener(navigationLayout);
//        }
//    }

    @Override
    protected Object doInBackground(Object[] params) {
        Log.i(TAG, "Initializing Google Map");

        final MapLayout mapLayout = (MapLayout) getFragmentView(MapLayout.class);

        if (getAppContextInstance().getGoogleMap().isPresent()) {
            Log.i(TAG, "Google map already initialized");
            return null;
        }

        getFragmentView(MapLayout.class).getActivity().runOnUiThread(() -> {
            Log.i(TAG, "Initializing Google Map");

            //Create global configuration and initialize ImageLoader with this configuration
            ImageLoaderConfiguration config = new Builder(mapLayout.getActivity().getApplicationContext()).build();
            ImageLoader.getInstance().init(config);

            int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(mapLayout.getActivity().getApplicationContext());
            // Showing status
            if (status != ConnectionResult.SUCCESS) // Google Play Services are not available
            {
                Log.i(TAG, "Google Play Services are not available");
            } else // Google Play Services are available
            {
                Log.i(TAG, "Google Play Services are available");
                // Getting reference to the SupportMapFragment of activity_main.xml
                MapFragment fm = (MapFragment) mapLayout.getActivity().getFragmentManager().findFragmentById(R.id.map_fragment);

                // Getting GoogleMap object from the fragment
                getAppContextInstance().setGoogleMap(fm.getMap());

                Log.i(TAG, "Got Google map");

                getAppContextInstance().getGoogleMap().get().setMyLocationEnabled(true);
                getAppContextInstance().getGoogleMap().get().getUiSettings().setZoomGesturesEnabled(true);
                getAppContextInstance().getGoogleMap().get().getUiSettings().setZoomControlsEnabled(true);
                getAppContextInstance().getGoogleMap().get().setOnMarkerClickListener(mapLayout);
//                    AppContext.getInstance().getGoogleMap().get().setInfoWindowAdapter(mapLayout);
            }
        });

        return null;
    }

    @Override
    public void onCancelledRelay() {
        getAppContextInstance().getGoogleMap().ifPresent(GoogleMap::stopAnimation);
    }

    @Override
    public void onTerminationRelay() {
        Log.i(TAG, "Shutting down class " + getClassName());
        getAppContextInstance().getGoogleMap().ifPresent(GoogleMap::stopAnimation);
    }
}
