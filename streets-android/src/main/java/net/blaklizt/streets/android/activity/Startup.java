package net.blaklizt.streets.android.activity;

import android.content.Intent;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;

import net.blaklizt.streets.android.R;
import net.blaklizt.streets.android.activity.helpers.LocationUpdateTask;
import net.blaklizt.streets.android.activity.helpers.LoginTask;
import net.blaklizt.streets.android.activity.helpers.PlacesTask;
import net.blaklizt.streets.android.activity.helpers.SequentialTaskManager;
import net.blaklizt.streets.android.common.StreetsCommon;

import static android.location.LocationManager.GPS_PROVIDER;
import static android.media.MediaPlayer.OnCompletionListener;
import static android.support.v4.content.PermissionChecker.PERMISSION_GRANTED;
import static android.view.View.OnClickListener;
import static android.view.View.VISIBLE;
import static net.blaklizt.streets.android.activity.AppContext.MINIMUM_REFRESH_DISTACE;
import static net.blaklizt.streets.android.activity.AppContext.MINIMUM_REFRESH_TIME;
import static net.blaklizt.streets.android.common.enumeration.USER_PREFERENCE.AUTO_LOGIN;
import static net.blaklizt.streets.android.common.enumeration.USER_PREFERENCE.REQUEST_GPS_PERMS;
import static net.blaklizt.streets.android.common.enumeration.USER_PREFERENCE.SHOW_INTRO;

/**
 * Created with IntelliJ IDEA.
 * User: photon
 * Date: 2015/06/11
 * Time: 4:30 PM
 */
public class Startup extends AppCompatActivity implements OnClickListener, OnCompletionListener, ActivityCompat.OnRequestPermissionsResultCallback, GpsStatus.Listener, LocationListener
{
	public Button btnLogin = null;
	public EditText edtPassword = null;
	private CheckBox chkAutoLogin = null;

    private final String TAG = StreetsCommon.getTag(Startup.class);

	private VideoView videoView;

	private static Startup startup;

    @Override
	public void onCreate(Bundle savedInstanceState)
	{
        AppContext.getInstance(this.getApplicationContext()); startup = this;

        Log.i(TAG, "+++ ON CREATE +++");
		super.onCreate(savedInstanceState);
        setContentView(R.layout.intro_layout);

        findViewById(R.id.labelLoginHeader).setOnClickListener(view -> {
            Intent loginActivity = new Intent(getInstance(), Register.class);
            startActivity(loginActivity);
        });
        findViewById(R.id.labelGoToRegistration).setOnClickListener(view -> {
            Intent loginActivity = new Intent(getInstance(), Register.class);
            startActivity(loginActivity);
        });
        try
		{
			edtPassword = (EditText) findViewById(R.id.loginPin);
			btnLogin = (Button) findViewById(R.id.btnLogin);
			chkAutoLogin = (CheckBox) findViewById(R.id.chkAutoLogin);

			if (AppContext.getStreetsCommon().getUserPreferenceValue(SHOW_INTRO).equals("1")) {
                playIntroVideo();
			} else {
				onCompletion(null);
			}

		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			Log.e(TAG, "Failed to start application: " + ex.getMessage(), ex);
			runOnUiThread(() ->
                Toast.makeText(getApplication(), "Failed to start application! An error occurred.",
                Toast.LENGTH_SHORT).show());
			finish();
		}
	}

	@Override
	public void onCompletion(MediaPlayer mp) {
		Log.i(TAG, "+++ ON COMPLETION +++");

		if (AppContext.getStreetsCommon().getUserPreferenceValue(AUTO_LOGIN).equals("1")) {
			SequentialTaskManager.runWhenAvailable(new LoginTask(this));
		}
		else {
			edtPassword.setVisibility(VISIBLE);
			btnLogin.setVisibility(VISIBLE);
			chkAutoLogin.setVisibility(VISIBLE);
		}
	}

    public void playIntroVideo() {
        videoView = (VideoView) findViewById(R.id.videoView);
        videoView.setVideoURI(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.intro_clip));
        videoView.setMediaController(null);
        videoView.setClickable(false);
        videoView.setSoundEffectsEnabled(false);
        videoView.requestFocus();
        videoView.setOnPreparedListener(mp -> {
            mp.setVolume(0, 0);
            mp.setLooping(false);
            videoView.start();
        });
        videoView.setOnCompletionListener(this);
    }

	@Override
	@SuppressWarnings("unchecked")
	public void onClick(View view)
	{
        Log.i(TAG, "+++ ON CLICK +++");
		new LoginTask(this).execute();
	}

    @Override
    public void onDestroy() {

        Log.i(TAG, "+++ ON DESTROY +++");
        super.onDestroy();

        AppContext.shutdown();
    }

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
        Log.i(TAG, "+++ ON POST CREATE +++");
		super.onPostCreate(savedInstanceState);
		btnLogin.setOnClickListener(this);
	}

	public static Startup getInstance() { return startup; }

	@Override
	public void onGpsStatusChanged(int i)
	{
		String currentProviderName = AppContext.getInstance().getCurrentProvider() == null ? null : AppContext.getInstance().getCurrentProvider().getName();
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
			AppContext.getInstance().setCurrentProvider(AppContext.getInstance().getLocationManager().getProvider(GPS_PROVIDER));
			Location location = AppContext.getInstance().getLocationManager().getLastKnownLocation(GPS_PROVIDER);

			if (location != null) {
                AppContext.getInstance().setCurrentLocation(location);
				//PLACE THE INITIAL MARKER
				Log.i(TAG, "Found location using GPS.");
				AppContext.getInstance().setCurrentProvider(AppContext.getInstance().getLocationManager().getProvider(GPS_PROVIDER));

				Log.i(TAG, "Provider accuracy: " + AppContext.getInstance().getCurrentProvider().getAccuracy());
				Log.i(TAG, "Provider power: " + AppContext.getInstance().getCurrentProvider().getPowerRequirement());

				Log.i(TAG, "Starting location update requests with provider: " + GPS_PROVIDER);
				AppContext.getInstance().getLocationManager().requestLocationUpdates(GPS_PROVIDER, MINIMUM_REFRESH_TIME, MINIMUM_REFRESH_DISTACE, this);

				Log.i(TAG, "Placing initial marker.");
				AppContext.drawMarker(location);
				return;
			}
			else {
				AppContext.getInstance().setCurrentProvider(AppContext.getInstance().getLocationManager().getProvider(AppContext.getInstance().getDefaultProvider()));
				Log.i(TAG, "GPS location provider not available.");
			}

			SequentialTaskManager.runWhenAvailable(AppContext.getBackgroundExecutionTask(LocationUpdateTask.class).setAdditionalParams(true));
		}
		else if (i == GpsStatus.GPS_EVENT_STOPPED && GPS_PROVIDER.equalsIgnoreCase(currentProviderName))
		{
			//GPS was turned off, we need another location provider
			Log.i(TAG, "GPS was turned off and was current location provider. Trying to find alternative location provider.");
			SequentialTaskManager.runWhenAvailable(AppContext.getBackgroundExecutionTask(LocationUpdateTask.class).setAdditionalParams(false));
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
			SequentialTaskManager.runWhenAvailable(AppContext.getBackgroundExecutionTask(LocationUpdateTask.class));
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
			AppContext.getInstance().setCurrentLocation(location);

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
				if (AppContext.getInstance().getGoogleMap().isPresent()) {

					//prevent all other processes from updating
					AppContext.setIsFirstLocationUpdate(false);

					AppContext.getInstance().getGoogleMap().get().moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
					Log.i(TAG, "Camera moved to new location");


					// Zoom in the Google Map
					AppContext.getInstance().getGoogleMap().get().animateCamera(CameraUpdateFactory.zoomTo(13), 3000, null);
					Log.i(TAG, "Camera zoomed to view");

//                location_image.setImageDrawable(ContextCompat.getDrawable(mapLayout.getContext(), R.drawable.default_icon));
//                location_info.setText("Bugatti & Millitainment");

					AppContext.drawMarker(location);

					SequentialTaskManager.runWhenAvailable(AppContext.getBackgroundExecutionTask(PlacesTask.class));

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
