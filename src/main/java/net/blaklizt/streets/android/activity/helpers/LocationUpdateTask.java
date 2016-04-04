package net.blaklizt.streets.android.activity.helpers;

import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;
import net.blaklizt.streets.android.activity.MapLayout;
import net.blaklizt.streets.android.activity.MenuLayout;
import net.blaklizt.streets.android.activity.Startup;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static android.location.LocationManager.GPS_PROVIDER;
import static java.util.Collections.singletonList;
import static net.blaklizt.streets.android.activity.AppContext.*;
import static net.blaklizt.streets.android.common.enumeration.TASK_TYPE.BG_LOCATION_TASK;

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
public class LocationUpdateTask extends StreetsAbstractTask {

    LocationManager locationManager;

    public LocationUpdateTask() {
        processDependencies = new ArrayList<>(singletonList(GoogleMapTask.class.getSimpleName()));
        viewDependencies = new ArrayList<>(singletonList(MapLayout.class));
        allowOnlyOnce = false;
        allowMultiInstance = false;
        taskType = BG_LOCATION_TASK;
        additionalParams = new Object[] { true };
        locationManager = getAppContextInstance().getLocationManager();
    }

    /* params[0] = shouldEnableGPS : boolean */
    @Override public void onPreExecuteRelay(Object...params) { if ((boolean)params[0]) { checkEnableGPS(); } }

    protected void onPostExecuteRelay(Object result) {
        if (!isLocationPermissionsGranted()) {
            Log.i(TAG, "Cannot run location updates. Insufficient permissions.");
            return;
        }
        try {
            Log.i(TAG, "Starting location update requests with provider: " + GPS_PROVIDER);
            locationManager.requestLocationUpdates(GPS_PROVIDER, MINIMUM_REFRESH_TIME, MINIMUM_REFRESH_DISTANCE, MenuLayout.getInstance());
        } catch (SecurityException ex) {
            ex.printStackTrace();
            Log.e(TAG, "Failed to setup location update requests! " + ex.getMessage(), ex);
            throw new RuntimeException(ex);
        }
    }

    @Override
    /* params[0] = shouldEnableGPS : boolean */
    protected Object doInBackground(Object...params) {
        Log.i(TAG, "Running LocationUpdateTask with enableGPS = " + params[0]);
        try
        {
            if (!isLocationPermissionsGranted()) {
                Log.i(TAG, "Cannot run location updates. Insufficient permissions.");
                return null;
            }

            if ((boolean)params[0]) {
                Log.i(TAG, "Checking for preferred location provider 'GPS' for best accuracy.");
                final Location location = locationManager.getLastKnownLocation(GPS_PROVIDER);

                if (location != null) {
                    Startup.getInstance().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            MenuLayout.getInstance().onLocationChanged(location);
                        }
                    });

                    Log.i(TAG, "Found location using GPS.");
                    getAppContextInstance().setCurrentProvider(locationManager.getProvider(GPS_PROVIDER));

                    Log.i(TAG, "Provider accuracy: " + getAppContextInstance().getCurrentProvider().getAccuracy());
                    Log.i(TAG, "Provider power: " + getAppContextInstance().getCurrentProvider().getPowerRequirement());

                    Log.i(TAG, "Placing initial marker.");
                    return null;
                } else {
                    Log.i(TAG, "GPS location provider not available.");
                }
            }

            Log.i(TAG, "Searching for location provider with fine accuracy and low power.");

            Criteria criteria = new Criteria();
            criteria.setAccuracy(Criteria.ACCURACY_FINE);
            criteria.setPowerRequirement(Criteria.POWER_LOW);

            // Getting the name of the best provider
            getAppContextInstance().setDefaultProvider(locationManager.getBestProvider(criteria, true));

            Log.i(TAG, "Got defaultProvider (fail safe) as: " + getAppContextInstance().getDefaultProvider());

            // Getting Current Location
            Location location = locationManager.getLastKnownLocation(getAppContextInstance().getDefaultProvider());

            if (location == null) {
                Log.i(TAG, "Best location provider not available. Getting all other providers.");
                List<String> providers = locationManager.getProviders(true);
                Iterator providerIterator = providers.iterator();
                String providerName;

                while (location == null && providerIterator.hasNext()) {
                    providerName = (String) providerIterator.next();
                    if (providerName.equalsIgnoreCase(GPS_PROVIDER))                    { continue; } //skip GPS provider,    we just tried it above
                    if (providerName.equalsIgnoreCase(getAppContextInstance().getDefaultProvider()))   { continue; } //skip defaultProvider, we just tried it above
                    Log.i(TAG, "Trying provider " + providerName);
                    location = locationManager.getLastKnownLocation(providerName);
                    if (location != null) {
                        Log.i(TAG, "Found working provider: " + providerName);
                        getAppContextInstance().setDefaultProvider(providerName); //found a working provider, use this to do future updates
                    }
                }
            }

            if (location != null) {
                final Location finalLocation = location;
                Startup.getInstance().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        MenuLayout.getInstance().onLocationChanged(finalLocation);
                    }
                });
                Log.i(TAG, "Found location using provider '" + getAppContextInstance().getDefaultProvider() + "'. Placing initial marker.");
            } else {
                //All providers failed, may as well poll using least battery consuming provider
                getAppContextInstance().setDefaultProvider(PROVIDER_CHEAPEST);
                Log.i(TAG, "All providers failed. Polling location with cheapest provider: " + getAppContextInstance().getDefaultProvider());
            }

            Log.i(TAG, "Starting location update requests with provider: " + getAppContextInstance().getDefaultProvider());

            getAppContextInstance().setCurrentProvider(locationManager.getProvider(getAppContextInstance().getDefaultProvider()));
            Log.i(TAG, "Provider accuracy: " + getAppContextInstance().getCurrentProvider().getAccuracy());
            Log.i(TAG, "Provider power: " + getAppContextInstance().getCurrentProvider().getPowerRequirement());

        }
        catch (SecurityException se) {
            se.printStackTrace();
            Log.e(TAG, "Failed to do location updates! " + se.getMessage(), se);
            throw new RuntimeException(se);
        }
        return null;
    }
}
