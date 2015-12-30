package net.blaklizt.streets.android.activity;

import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.content.res.Configuration;
import android.graphics.Color;
import android.location.GpsStatus;
import android.location.GpsStatus.Listener;
import android.location.Location;
import android.location.LocationListener;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat.OnRequestPermissionsResultCallback;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import io.codetail.animation.SupportAnimator;
import io.codetail.animation.ViewAnimationUtils;
import net.blaklizt.streets.android.R;
import net.blaklizt.streets.android.activity.helpers.LocationUpdateTask;
import net.blaklizt.streets.android.activity.helpers.PlacesTask;
import net.blaklizt.streets.android.activity.helpers.StreetsAbstractView;
import net.blaklizt.streets.android.common.StreetsCommon;
import net.blaklizt.streets.android.sidemenu.interfaces.Resourceble;
import net.blaklizt.streets.android.sidemenu.model.SlideMenuItem;
import net.blaklizt.streets.android.sidemenu.util.ViewAnimator;
import net.blaklizt.streets.android.sidemenu.util.ViewAnimator.ViewAnimatorListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import static android.location.LocationManager.GPS_PROVIDER;
import static android.support.v4.content.PermissionChecker.PERMISSION_GRANTED;
import static java.lang.String.format;
import static net.blaklizt.streets.android.activity.AppContext.*;
import static net.blaklizt.streets.android.activity.helpers.SequentialTaskManager.runWhenAvailable;
import static net.blaklizt.streets.android.common.enumeration.USER_PREFERENCE.ASK_ON_EXIT;
import static net.blaklizt.streets.android.common.enumeration.USER_PREFERENCE.REQUEST_GPS_PERMS;

/******************************************************************************
 * *
 * Created:     02 / 11 / 2015                                             *
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


public class MenuLayout extends AppCompatActivity implements
        ViewAnimatorListener, OnClickListener,
        OnMultiChoiceClickListener, OnRequestPermissionsResultCallback, Listener, LocationListener {

    private final String TAG = StreetsCommon.getTag(MenuLayout.class);
    private final List<SlideMenuItem> menuItemList = new ArrayList<>();
    private final HashMap<String, StreetsAbstractView> streetsViews = new HashMap<>();
    private static MenuLayout menuLayout = null;
	public TextView currentCityTextView;
	public TextView currentSuburbTextView;
	private View mainContentTable;
	private float lastTranslate = 0.0f;
	private DrawerLayout drawerLayout;
	private ActionBarDrawerToggle drawerToggle;
	private ViewAnimator viewAnimator;
	private LinearLayout linearLayout;
	private TextView statusTextView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "+++ ON CREATE +++");
        Log.i(TAG, format("--- savedInstanceState: %s", savedInstanceState != null ? savedInstanceState.toString() : null));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_menu_layout);
        menuLayout = this;

	    currentCityTextView = (TextView)findViewById(R.id.current_city);
	    currentSuburbTextView = (TextView)findViewById(R.id.current_suburb);

	    mainContentTable = findViewById(R.id.main_content_table);

        Log.i(TAG, "Creating toolbar");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        menuItemList.add(AppContext.getFragmentMenuRegistry().get(null));

        Log.i(TAG, "Initializing streets views and menus");

        Set<Class<? extends StreetsAbstractView>> streetsFragments = getStreetsFragments().keySet();

        Log.i(TAG, format("Found %d views ready to initialize", streetsFragments.size()));

        for (Class<? extends StreetsAbstractView> streetsFragment : streetsFragments) {
            Log.i(TAG, format("Instantiating view %s ", streetsFragment.getSimpleName()));
            streetsViews.put(streetsFragment.getSimpleName(), getFragmentView(streetsFragment));
            menuItemList.add(AppContext.getFragmentMenuRegistry().get(streetsFragment));
        }

        StreetsAbstractView initialView = getStreetsFragments().get(DEFAULT_FRAGMENT_VIEW);
        initialView.setRetainInstance(true);

        Log.i(TAG, format("Setting initial view to %s ", initialView));

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_frame, initialView, initialView.getClassName())
                .setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit();

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerLayout.setScrimColor(Color.TRANSPARENT);
        linearLayout = (LinearLayout) findViewById(R.id.left_drawer);
        linearLayout.setOnClickListener(v -> drawerLayout.closeDrawers());

        Log.i(TAG, "Status text view");
        statusTextView = (TextView) findViewById(R.id.status_text_view);
        setAppInfo("I'm the streets, look both ways before you cross me!");

        setActionBar();
        viewAnimator = new ViewAnimator<>(this, menuItemList, initialView, drawerLayout, this);

    }

    public static MenuLayout getInstance() {
        return menuLayout;
    }

    public static void setAppInfo(String appInfo) {
        getInstance().statusTextView.setText(appInfo);
    }

    @Override
    public void onClick(DialogInterface dialogInterface, int index, boolean isChecked) {
        Log.i(TAG, dialogInterface.toString() + ": +++ ON CLICK +++");
        Log.i(TAG, format("--- dialogInterface: %s", dialogInterface != null ? dialogInterface.toString() : null));
        Log.i(TAG, format("--- index: %d", index));
        Log.i(TAG, format("--- isChecked: %s", isChecked));
        boolean exit = (index == DialogInterface.BUTTON_POSITIVE);
        //only persist prefs on positive response
        if (exit) {
            AppContext.getStreetsCommon().setUserPreference(ASK_ON_EXIT, !isChecked ? "1" : "0");
        }
    }

    @Override
    public void onClick(DialogInterface dialogInterface, int index) {
        Log.i(TAG, dialogInterface.toString() + ": +++ ON CLICK +++");
        Log.i(TAG, format("--- dialogInterface: %s", dialogInterface != null ? dialogInterface.toString() : null));
        Log.i(TAG, format("--- index: %d", index));
        boolean exit = (index == DialogInterface.BUTTON_POSITIVE);
        if (exit) { AppContext.shutdown(); finish(); }
    }

    @Override
    public void onBackPressed() {
        Log.i(TAG, "+++ ON BACK PRESSED +++");
        Log.i(TAG, format("--- mDrawerLayout.isDrawerOpen: %s", drawerLayout.isDrawerOpen(GravityCompat.START)));
//        Log.i(TAG, format("--- resideMenu.isOpened: %s", resideMenu.isOpened()));

        if (drawerLayout.isDrawerOpen(GravityCompat.START) /*|| resideMenu.isOpened() */) {
            drawerLayout.closeDrawers();
//            resideMenu.closeMenu();
//            resideMenu.setSwipeDirectionEnable(ResideMenu.DIRECTION_RIGHT);
            return;
        }

        CharSequence[] items = new CharSequence[]{"Never ask again"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Exit application?")
                .setMultiChoiceItems(items, new boolean[]{true}, this)
                .setPositiveButton("Yes", this)
                .setNegativeButton("No", this).show();
    }

    private void setActionBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        drawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                drawerLayout,         /* DrawerLayout object */
                toolbar,  /* nav drawer icon to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description */
                R.string.drawer_close  /* "close drawer" description */
        ) {

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                linearLayout.removeAllViews();
                linearLayout.invalidate();
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
                if (slideOffset > 0.6 && linearLayout.getChildCount() == 0) {
                    viewAnimator.showMenuContent();
                }

                float moveFactor = (linearLayout.getWidth() * slideOffset);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
                {
                    mainContentTable.setTranslationX(moveFactor);
                }
                else
                {
                    TranslateAnimation anim = new TranslateAnimation(lastTranslate, moveFactor, 0.0f, 0.0f);
                    anim.setDuration(0);
                    anim.setFillAfter(true);
                    mainContentTable.startAnimation(anim);

                    lastTranslate = moveFactor;
                }
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };
        drawerLayout.setDrawerListener(drawerToggle);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main_left, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.i(TAG, format("Performing onOptionsItemSelected on MenuItem %s", item.getTitle()));
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        switch (item.getItemId()) {
            case R.id.action_settings:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private StreetsAbstractView replaceFragment(StreetsAbstractView streetsFragment, int topPosition) {
        View view = findViewById(R.id.content_frame);
        int finalRadius = Math.max(view.getWidth(), view.getHeight());
        SupportAnimator animator = ViewAnimationUtils.createCircularReveal(view, 0, topPosition, 0, finalRadius);
        animator.setInterpolator(new AccelerateInterpolator());
        animator.setDuration(ViewAnimator.CIRCULAR_REVEAL_ANIMATION_DURATION);
        animator.start();
        getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, streetsFragment).commit();
        return streetsFragment;
    }

    @Override
    public StreetsAbstractView onSwitch(Resourceble slideMenuItem, StreetsAbstractView streetsAbstractView, int position) {

        Log.i(TAG, format("Performing onSwitch on slideMenuItem %s, streetsAbstractView %s, position %d", slideMenuItem.getName(), streetsAbstractView.getClassName(), position));

        switch (slideMenuItem.getName()) {
            case AppContext.MNU_CLOSE:
                return streetsAbstractView;
            default:
                Log.i(TAG, format("Replacing fragment with %s", slideMenuItem.getName()));
                return replaceFragment(getStreetsFragments().get(getMenuFragmentRegistry().get(slideMenuItem.getName())), position);
        }
    }

    @Override
    public void disableHomeButton() {
        getSupportActionBar().setHomeButtonEnabled(false);

    }

    @Override
    public void enableHomeButton() {
        getSupportActionBar().setHomeButtonEnabled(true);
        drawerLayout.closeDrawers();

    }

    @Override
    public void addViewToContainer(View view) {
        Log.i(TAG, format("Adding view %s", view.getTag()));
        linearLayout.addView(view);
    }


	@Override
	public void onGpsStatusChanged(int i)
	{
		String currentProviderName = getAppContextInstance().getCurrentProvider() == null ? null : getAppContextInstance().getCurrentProvider().getName();
		if ((i == GpsStatus.GPS_EVENT_STARTED || i == GpsStatus.GPS_EVENT_FIRST_FIX) && !GPS_PROVIDER.equalsIgnoreCase(currentProviderName))
		{
			//GPS was turned on, is now ready & is now best location provider
			Log.i(TAG, "GPS started, trying switch to GPS as preferred location provider from current provider: " + currentProviderName);

			if (!AppContext.isLocationPermissionsGranted()) {
				Log.i(TAG, "Cannot run location updates. Insufficient permissions.");
				return;
			}

			// Creating a criteria object to retrieve provider
			Log.i(TAG, "Checking for preferred location provider 'GPS' for best accuracy.");
            getAppContextInstance().useGPSAsCurrentProvider();

			Location location;
			try { location = getAppContextInstance().getLocationManager().getLastKnownLocation(GPS_PROVIDER); }
			catch (SecurityException ex) {
				ex.printStackTrace();  /* try catch done to please compiler. cant reach here because we check permissions beforehand */
				Log.e(TAG, "Failed to change to GPS location provider! " + ex.getMessage(), ex);
				throw new RuntimeException(ex);
			}

			if (location != null) {
				getAppContextInstance().setCurrentLocation(location);
				//PLACE THE INITIAL MARKER
				Log.i(TAG, "Found location using GPS.");
				getAppContextInstance().useGPSAsCurrentProvider();

				Log.i(TAG, "Provider accuracy: " + getAppContextInstance().getCurrentProvider().getAccuracy());
				Log.i(TAG, "Provider power: " + getAppContextInstance().getCurrentProvider().getPowerRequirement());

				Log.i(TAG, "Starting location update requests with provider: " + GPS_PROVIDER);
				try { getAppContextInstance().getLocationManager().requestLocationUpdates(GPS_PROVIDER, MINIMUM_REFRESH_TIME, MINIMUM_REFRESH_DISTANCE, this); }
				catch (SecurityException ex) {
					ex.printStackTrace();  /* try catch done to please compiler. cant reach here because we check permissions beforehand */
					Log.e(TAG, "Failed to start location updates with GPS location provider! " + ex.getMessage(), ex);
					throw new RuntimeException(ex);
				}
				return;
			}
			else {
				getAppContextInstance().useDefaultAsCurrentProvider();
				Log.i(TAG, "GPS location provider not available.");
			}

			runWhenAvailable(getBackgroundExecutionTask(LocationUpdateTask.class).setAdditionalParams(true));
		}
		else if (i == GpsStatus.GPS_EVENT_STOPPED && GPS_PROVIDER.equalsIgnoreCase(currentProviderName))
		{
			//GPS was turned off, we need another location provider
			Log.i(TAG, "GPS was turned off and was current location provider. Trying to find alternative location provider.");
			runWhenAvailable(getBackgroundExecutionTask(LocationUpdateTask.class).setAdditionalParams(false));
		}
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

		if (!(requestCode == AppContext.PERMISSION_LOCATION_INFO)) {
			return; //not our required permissions
		}

		for (int c = 0; c < grantResults.length; c++) {
			if (grantResults[c] != PERMISSION_GRANTED) {
				Log.i(TAG, "Permission was denied for " + permissions[c] + ". Aborting location updates.");
				AppContext.getStreetsCommon().addOutstandingPermission(permissions[c]);
				AppContext.getStreetsCommon().setUserPreference(REQUEST_GPS_PERMS, "0"); //if user rejects, he probably does not want to be bothered
			} else {
				Log.i(TAG, "Permission granted for " + permissions[c]);
				AppContext.getStreetsCommon().removeOutstandingPermission(permissions[c]);
				AppContext.getStreetsCommon().setUserPreference(REQUEST_GPS_PERMS, "1"); //reset preferences if permissions were updated
				AppContext.setLocationPermissionsGranted(false);
			}
		}

		if (AppContext.getStreetsCommon().getOutstandingPermissions().size() == 0) { //we have everything we need! Great. Start location updates.
			Log.i(TAG, "All required permissions granted. Performing location updates");
			runWhenAvailable(getBackgroundExecutionTask(LocationUpdateTask.class));
			AppContext.setLocationPermissionsGranted(true);
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
			getAppContextInstance().setCurrentLocation(location);

			// Getting latitude of the current location
			double latitude = location.getLatitude();

			// Getting longitude of the current location
			double longitude = location.getLongitude();

			Log.i(TAG, "New location " + latitude + ":" + longitude);

			// Creating a LatLng object for the current location
			LatLng latLng = new LatLng(latitude, longitude);

			//show a random news items
			//MenuActivity.getInstance().setAppInfo(">>> " + random news);

			if (AppContext.isFirstLocationUpdate())
			{
				// Showing the current location in Google Map
				if (getAppContextInstance().getGoogleMap().isPresent()) {

					//prevent all other processes from updating
					AppContext.setIsFirstLocationUpdate(false);

					getAppContextInstance().getGoogleMap().get().moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
					Log.i(TAG, "Camera moved to new location");

					// Zoom in the Google Map
					getAppContextInstance().getGoogleMap().get().animateCamera(CameraUpdateFactory.zoomTo(13), 3000, null);
					Log.i(TAG, "Camera zoomed to view");

					runWhenAvailable(getBackgroundExecutionTask(PlacesTask.class));
				}
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			Log.e(TAG, "Failed to update to new location", ex);
		}
	}

}
