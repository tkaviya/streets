package net.blaklizt.streets.android.activity.helpers;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import net.blaklizt.streets.android.activity.MapLayout;
import net.blaklizt.streets.android.activity.Startup;
import net.blaklizt.streets.android.common.StreetsCommon;
import net.blaklizt.streets.android.common.TASK_TYPE;
import net.blaklizt.streets.android.common.TaskInfo;
import net.blaklizt.streets.android.common.USER_PREFERENCE;
import net.blaklizt.streets.android.listener.EnableGPSDialogueListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import static android.support.v4.content.PermissionChecker.PERMISSION_GRANTED;
import static java.util.Collections.singletonList;

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
public class LocationUpdateTask extends TaskInfo
        implements GpsStatus.Listener, LocationListener,
        ActivityCompat.OnRequestPermissionsResultCallback {

    private static final String TAG = StreetsCommon.getTag(LocationUpdateTask.class);
    private static boolean arePermissionsGranted = false;
    private static final int PERMISSION_LOCATION_INFO = 6767;

    //location provider data
    private final static String PROVIDER_CHEAPEST = "passive";
    private final static Integer MINIMUM_REFRESH_TIME = 600000;
    private static LocationManager locationManager = null;
    private String defaultProvider = PROVIDER_CHEAPEST;        //default working provider
    private boolean firstLocationUpdate = true;

    private MapLayout mapLayout;
    private LocationProvider currentProvider = null;

    public LocationUpdateTask() {
        super(new ArrayList<>(singletonList(GoogleMapTask.class.getSimpleName())),
              new ArrayList<>(Collections.singletonList(MapLayout.class)), true, false, TASK_TYPE.BG_PLACES_TASK);
        this.mapLayout = MapLayout.getInstance();
    }

    @Override
    protected Object doInBackground(Object[] params) {

        Looper.prepare();

        if (mapLayout.getLocationManager().isPresent()) {
            locationManager = mapLayout.getLocationManager().get();
            Log.i(TAG, "Initializing location manager");
            //at activity start, if user has not disabled location stuff, request permissions.
            if (!arePermissionsGranted &&
                    (Startup.getStreetsCommon().getUserPreferenceValue(USER_PREFERENCE.SUGGEST_GPS).equals("1") ||
                            Startup.getStreetsCommon().getUserPreferenceValue(USER_PREFERENCE.AUTO_ENABLE_GPS).equals("1"))) {
                Startup.getStreetsCommon().setUserPreference(USER_PREFERENCE.REQUEST_GPS_PERMS, "1"); //reset preferences if permissions were updated
            }
        }

        Log.i(TAG, "Getting system location service");
        // Getting LocationManager object from System Service LOCATION_SERVICE
        mapLayout.setLocationManager((LocationManager) mapLayout.getActivity().getApplicationContext().getSystemService(Context.LOCATION_SERVICE));

        Looper.loop();
        try {
            locationManager.addGpsStatusListener(this);

            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                if (Startup.getStreetsCommon().getUserPreferenceValue(USER_PREFERENCE.SUGGEST_GPS).equals("1")) {

                    EnableGPSDialogueListener enableGpsListener = new EnableGPSDialogueListener(mapLayout.getActivity());

                    AlertDialog.Builder builder = new AlertDialog.Builder(mapLayout.getActivity());
                    builder.setMessage("Turn on GPS?")
                            .setMultiChoiceItems(
                                    EnableGPSDialogueListener.getQuestionItems(),
                                    EnableGPSDialogueListener.getCheckedItems(),
                                    EnableGPSDialogueListener.EnableGPSOptionListener.getInstance())
                            .setPositiveButton("Yes", enableGpsListener)
                            .setNegativeButton("No", enableGpsListener).create().show();
                } else if (Startup.getStreetsCommon().getUserPreferenceValue(USER_PREFERENCE.AUTO_ENABLE_GPS).equals("1")) {
                    Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    mapLayout.startActivity(myIntent);
                }
            }

            updateLocationProvider(true);
        } catch (SecurityException se) {
            //should never happen because we check for perms in advance
            se.printStackTrace();
            Log.e(TAG, "Failed to do location updates. " + se.getMessage(), se);
        }

        Looper.getMainLooper().quit();
        return null;
    }

    public void updateLocationProvider(boolean checkGPS) {
        try {
            if (!arePermissionsGranted && !checkAndRequestPermissions()) {
                Log.i(TAG, "Cannot run location updates. Insufficient permissions.");
                return;
            }

            if (checkGPS) {
                // Creating a criteria object to retrieve provider
                Log.i(TAG, "Checking for preferred location provider 'GPS' for best accuracy.");
                Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                if (location != null) {
                    //PLACE THE INITIAL MARKER
                    Log.i(TAG, "Found location using GPS.");
                    currentProvider = locationManager.getProvider(LocationManager.GPS_PROVIDER);

                    Log.i(TAG, "Provider accuracy: " + currentProvider.getAccuracy());
                    Log.i(TAG, "Provider power: " + currentProvider.getPowerRequirement());

                    Log.i(TAG, "Starting location update requests with provider: " + LocationManager.GPS_PROVIDER);
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MINIMUM_REFRESH_TIME, 0, this);

                    Log.i(TAG, "Placing initial marker.");
                    drawMarker(location);
                    return;
                } else {
                    Log.i(TAG, "GPS location provider not available.");
                }
            }

            Log.i(TAG, "Searching for location provider with fine accuracy and low power.");

            Criteria criteria = new Criteria();
            criteria.setAccuracy(Criteria.ACCURACY_FINE);
            criteria.setPowerRequirement(Criteria.POWER_LOW);

            // Getting the name of the best provider
            defaultProvider = locationManager.getBestProvider(criteria, true);

            Log.i(TAG, "Got defaultProvider (fail safe) as: " + defaultProvider);

            // Getting Current Location
            Location location = locationManager.getLastKnownLocation(defaultProvider);

            if (location == null) {
                Log.i(TAG, "Best location provider not available. Getting all other providers.");
                List<String> providers = locationManager.getProviders(true);
                Iterator providerIterator = providers.iterator();
                String providerName;

                while (location == null && providerIterator.hasNext()) {
                    providerName = (String) providerIterator.next();
                    if (providerName.equalsIgnoreCase(LocationManager.GPS_PROVIDER))
                        continue; //skip LocationManager.GPS_PROVIDER,		we just tried it above
                    if (providerName.equalsIgnoreCase(defaultProvider))
                        continue; //skip defaultProvider,	we just tried it above
                    Log.i(TAG, "Trying provider " + providerName);
                    location = locationManager.getLastKnownLocation(providerName);
                    if (location != null) {
                        Log.i(TAG, "Found working provider: " + providerName);
                        defaultProvider = providerName; //found a working provider, use this to do future updates
                    }
                }
            }

            if (location != null) {
                //PLACE THE INITIAL MARKER
                Log.i(TAG, "Found location using provider '" + defaultProvider + "'. Placing initial marker.");
                drawMarker(location);
            } else {
                //All providers failed, may as well poll using least battery consuming provider
                defaultProvider = PROVIDER_CHEAPEST;
                Log.i(TAG, "All providers failed. Polling location with cheapest provider: " + defaultProvider);
            }

            Log.i(TAG, "Starting location update requests with provider: " + defaultProvider);

            locationManager.requestLocationUpdates(defaultProvider, MINIMUM_REFRESH_TIME, 0, this);
            locationManager.addGpsStatusListener(this);

            currentProvider = locationManager.getProvider(defaultProvider);
            Log.i(TAG, "Provider accuracy: " + currentProvider.getAccuracy());
            Log.i(TAG, "Provider power: " + currentProvider.getPowerRequirement());

        }
        catch (SecurityException se) {
            se.printStackTrace();
            Log.e(TAG, "Failed to do location updates! " + se.getMessage(), se);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            Log.e(TAG, "Failed to initialize Google Map! " + ex.getMessage(), ex);
        }

    }

    private boolean checkAndRequestPermissions() {

        //check course location
        if (ContextCompat.checkSelfPermission(mapLayout.getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.i(TAG, "Permission " + Manifest.permission.ACCESS_COARSE_LOCATION + " is not allowed.");
            Startup.getStreetsCommon().addOutstandingPermission(Manifest.permission.ACCESS_COARSE_LOCATION);
            arePermissionsGranted = false;
        } else {
            Log.i(TAG, "Permission " + Manifest.permission.ACCESS_COARSE_LOCATION + " is allowed.");
            Startup.getStreetsCommon().removeOutstandingPermission(Manifest.permission.ACCESS_COARSE_LOCATION);
            arePermissionsGranted = false;
        }

        //check fine location
        if (ContextCompat.checkSelfPermission(mapLayout.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.i(TAG, "Permission " + Manifest.permission.ACCESS_FINE_LOCATION + " is not allowed.");
            Startup.getStreetsCommon().addOutstandingPermission(Manifest.permission.ACCESS_FINE_LOCATION);
            arePermissionsGranted = false;
        } else {
            Log.i(TAG, "Permission " + Manifest.permission.ACCESS_FINE_LOCATION + " is allowed.");
            Startup.getStreetsCommon().removeOutstandingPermission(Manifest.permission.ACCESS_FINE_LOCATION);
            arePermissionsGranted = false;
        }

        ArrayList<String> outstandingPermissions = Startup.getStreetsCommon().getOutstandingPermissions();

        Log.i(TAG, "Outstanding permissions: " + outstandingPermissions.size());
        if (outstandingPermissions.size() > 0 && Startup.getStreetsCommon().getUserPreferenceValue(USER_PREFERENCE.REQUEST_GPS_PERMS).equals("1")) {
            Log.i(TAG, "Not enough permissions to do location updates. Requesting from user.");
            mapLayout.requestPermissions(outstandingPermissions.toArray(new String[outstandingPermissions.size()]), PERMISSION_LOCATION_INFO);
            arePermissionsGranted = false;
        }
        else { arePermissionsGranted = true; }

        EnableGPSDialogueListener enableGpsListener = new EnableGPSDialogueListener(mapLayout.getActivity());

        AlertDialog.Builder builder = new AlertDialog.Builder(mapLayout.getActivity());
        builder.setMessage("Turn on GPS?")
                .setMultiChoiceItems(
                        EnableGPSDialogueListener.getQuestionItems(),
                        EnableGPSDialogueListener.getCheckedItems(),
                        EnableGPSDialogueListener.EnableGPSOptionListener.getInstance())
                .setPositiveButton("Yes", enableGpsListener)
                .setNegativeButton("No", enableGpsListener).create().show();

        return arePermissionsGranted;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (!(requestCode == PERMISSION_LOCATION_INFO)) {
            return; //not our required permissions
        }

        for (int c = 0; c < grantResults.length; c++) {
            if (grantResults[c] != PERMISSION_GRANTED) {
                Log.i(TAG, "Permission was denied for " + permissions[c] + ". Aborting location updates.");
                Startup.getStreetsCommon().addOutstandingPermission(permissions[c]);
                Startup.getStreetsCommon().setUserPreference(USER_PREFERENCE.REQUEST_GPS_PERMS, "0"); //if user rejects, he probably does not want to be bothered
            } else {
                Log.i(TAG, "Permission granted for " + permissions[c]);
                Startup.getStreetsCommon().removeOutstandingPermission(permissions[c]);
                Startup.getStreetsCommon().setUserPreference(USER_PREFERENCE.REQUEST_GPS_PERMS, "1"); //reset preferences if permissions were updated
                arePermissionsGranted = false;
            }
        }

        if (Startup.getStreetsCommon().getOutstandingPermissions().size() == 0) { //we have everything we need! Great. Start location updates.
            Log.i(TAG, "All required permissions granted. Performing location updates");
            SequentialTaskManager.runWhenAvailable(mapLayout.TASK_EXECUTION_INFO.get(LocationUpdateTask.class.getSimpleName()));
            arePermissionsGranted = true;
        }
    }

    @Override
    public void onGpsStatusChanged(int i)
    {
        String currentProviderName = currentProvider == null ? null : currentProvider.getName();
        if ((i == GpsStatus.GPS_EVENT_STARTED || i == GpsStatus.GPS_EVENT_FIRST_FIX) && !LocationManager.GPS_PROVIDER.equalsIgnoreCase(currentProviderName))
        {
            //GPS was turned on/is now ready, it may now be best location provider
            Log.i(TAG, "GPS started, trying switch to GPS as preferred location provider from current provider: " + currentProviderName);
            updateLocationProvider(true);
        }
        else if (i == GpsStatus.GPS_EVENT_STOPPED && LocationManager.GPS_PROVIDER.equalsIgnoreCase(currentProviderName))
        {
            //GPS was turned off, we need another location provider
            Log.i(TAG, "GPS was turned off and was current location provider. Trying to find alternative location provider.");
            updateLocationProvider(false);
        }
    }

    @Override
    public void onProviderEnabled(String provider) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onProviderDisabled(String provider) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "+++ ON LOCATION CHANGED +++");

        try
        {
            mapLayout.setCurrentLocation(location);

            // Getting latitude of the current location
            double latitude = location.getLatitude();

            // Getting longitude of the current location
            double longitude = location.getLongitude();

            Log.i(TAG, "New location " + latitude + ":" + longitude);

            // Creating a LatLng object for the current location
            LatLng latLng = new LatLng(latitude, longitude);

            //show a random news items
            //MenuActivity.getInstance().setAppInfo(">>> " + random news);

            if (firstLocationUpdate)
            {
                // Showing the current location in Google Map
                if (mapLayout.getGoogleMap().isPresent()) {

                    //prevent all other processes from updating
                    firstLocationUpdate = false;

                    mapLayout.getGoogleMap().get().moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
                    Log.i(TAG, "Camera moved to new location");


                    // Zoom in the Google Map
                    mapLayout.getGoogleMap().get().animateCamera(CameraUpdateFactory.zoomTo(14), 3000, null);
                    Log.i(TAG, "Camera zoomed to view");

//                location_image.setImageDrawable(ContextCompat.getDrawable(mapLayout.getContext(), R.drawable.default_icon));
//                location_info.setText("Bugatti & Millitainment");

                    drawMarker(location);

                    refreshLocation();
                }
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            Log.e(TAG, "Failed to update to new location", ex);
        }
    }

    public void drawMarker(Location location){
        Log.i(TAG, "Found current location at " + location.getLatitude() + " : " + location.getLongitude());
        mapLayout.getGoogleMap().get().clear();
        mapLayout.getGoogleMap().get().addMarker(new MarkerOptions()
                .position(new LatLng(location.getLatitude(), location.getLongitude()))
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET))
                .title("Your current location"));
    }

    public void refreshLocation() {
        //runningTasks.add(new LocationTask(this).execute());
        SequentialTaskManager.runWhenAvailable(mapLayout.TASK_EXECUTION_INFO.get(PlacesTask.class.getSimpleName()));
    }




}
