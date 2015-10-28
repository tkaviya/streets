package net.blaklizt.streets.android.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.*;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.*;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import net.blaklizt.streets.android.R;
import net.blaklizt.streets.android.common.StreetsCommon;
import net.blaklizt.streets.android.common.utils.Optional;
import net.blaklizt.streets.android.listener.EnableGPSDialogueListener;
import net.blaklizt.streets.android.location.navigation.Directions;
import net.blaklizt.streets.android.location.navigation.Navigator;
import net.blaklizt.streets.android.location.places.Place;
import net.blaklizt.streets.android.location.places.PlacesService;

import java.util.*;

import static android.support.v4.content.PermissionChecker.PERMISSION_GRANTED;
import static android.support.v4.content.PermissionChecker.checkSelfPermission;
import static java.lang.String.format;
import static net.blaklizt.streets.android.listener.EnableGPSDialogueListener.EnableGPSOptionListener;

/**
 * User: tkaviya
 * Date: 6/21/14
 * Time: 5:58 PM
 */
public class MapLayout extends Fragment
		implements LocationListener, OnMarkerClickListener,
		Navigator.OnPathSetListener, GoogleMap.InfoWindowAdapter,
		GpsStatus.Listener, GoogleMap.OnMapLoadedCallback,
		ActivityCompat.OnRequestPermissionsResultCallback
{

	private LinkedList<String> randomNews = new LinkedList<>();
	private LinkedList<AsyncTask> runningTasks = new LinkedList<>();

	private class LocationTask extends AsyncTask<Void, Void, Void> {

        private final String TAG = StreetsCommon.getTag(LocationTask.class);

        Optional<ArrayList<Place>> nearbyPlaces = Optional.empty();

		ProgressDialog progressDialog;

		@Override
		protected Void doInBackground(Void... param) {
            Log.i(TAG, "+++ doInBackground +++");

			if (currentLocation != null) {
				nearbyPlaces = PlacesService.nearby_search(
						currentLocation.getLatitude(), currentLocation.getLongitude(), 5000,
						Startup.getStreetsCommon().getStreetsDBHelper().getPlacesOfInterest()
				);
			} else {
                if (getView() != null) {
                    getActivity().runOnUiThread(() -> Snackbar.make(getView(), "Current location unknown. Check location settings.", Snackbar.LENGTH_SHORT));
                }
			}
			return null;
		}

		@Override
		protected void onPreExecute() {
            Log.i(TAG, "+++ onPreExecute +++");
            super.onPreExecute();
			progressDialog = ProgressDialog.show(getActivity(), "Updating location", "Updating location...", true, true);
			progressDialog.show();
		}

        @Override
        protected void onCancelled() {
            Log.i(TAG, "+++ onCancelled +++");
            super.onCancelled();
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

            getGoogleMap().ifPresent(GoogleMap::clear);

            if (nearbyPlaces != null && nearbyPlaces.isPresent()) {
                ArrayList<Place> places = nearbyPlaces.get();
                for (Place place : places) { drawPlaceMarker(place); }
            }

            if (progressDialog != null) { progressDialog.hide(); }

            runningTasks.remove(this);
		}
	}

	private static final String TAG = StreetsCommon.getTag(MapLayout.class);

	private static final int PERMISSION_LOCATION_INFO = 6767;

	protected static MapLayout mapLayout = null;

	protected View mapView;

	protected GoogleMap googleMap;
	protected Navigator navigator;
	protected Location currentLocation;
	protected LocationManager locationManager;
	protected HashMap<Integer, Place> map = new HashMap<>();
	protected boolean firstLocationUpdate = true;
	protected ImageView location_image;
	protected TextView location_info;
	protected LayoutInflater inflater;
	protected ViewGroup container;
	protected boolean isMapLoaded = false;
	protected boolean arePermissionsGranted = false;

	protected Random generator = new Random(new Date().getTime());

	//location provider data
	protected final static String PROVIDER_CHEAPEST = "passive";
	protected final static Integer MINIMUM_REFRESH_TIME = 20000;
	protected String defaultProvider = PROVIDER_CHEAPEST;        //default working provider
	protected LocationProvider currentProvider = null;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Log.i(TAG, "+++ ON CREATE VIEW +++");
        super.onCreateView(inflater, container, savedInstanceState);
        mapLayout = this;

        Log.i(TAG, format("LayoutInflater: %s", inflater != null ? inflater.toString() : null));
        Log.i(TAG, format("ViewGroup: %s", container != null ? container.getTag() : null));
		Log.i(TAG, format("SavedInstanceState: %s", savedInstanceState != null ? savedInstanceState.toString() : null));

        this.inflater = inflater;
        this.container = container;

        doInit();

		randomNews.add("Weather is 17 degrees");
		randomNews.add("Traffic expected for 2 hours");
		randomNews.add("DoubleBurger special @Wimpy 2day");
		randomNews.add("C.Nyovest @ Bar9 2night");
		randomNews.add("Your friend Ntwaizen is nearby");
		randomNews.add("Yo fav. food (KFC) is nearby!");
		randomNews.add("Distance 2 home: 10m | 1 min");
		randomNews.add("Distance 2 work: 1km | 5 min");

		return mapView;
	}

    public void doInit() {

        if (mapView == null && inflater != null && container != null) {
            Log.i(TAG, "Layout not inflated yet, inflating layout");
            setRetainInstance(true);
            mapView = inflater.inflate(R.layout.map_layout, container, false);
            location_image = (ImageView) mapView.findViewById(R.id.location_image_view);
            location_info = (TextView) mapView.findViewById(R.id.location_categories_text_view);
        }

        if (mapView == null) {
            return;
        }

        if (!isMapLoaded) {
            Log.i(TAG, "Map not loaded yet, initialize map");
            initializeMap();
        }

        if (!getGoogleMap().isPresent()) {
            return;
        }

        if (locationManager == null) {
            Log.i(TAG, "Initializing location manager");
            //at activity start, if user has not disabled location stuff, request permissions.
            if (!arePermissionsGranted &&
                    (Startup.getStreetsCommon().getUserPreferenceValue("suggest_gps").equals("1") ||
                            Startup.getStreetsCommon().getUserPreferenceValue("auto_enable_gps").equals("1"))) {
                Startup.getStreetsCommon().setUserPreference("request_gps_perms", "1"); //reset preferences if permissions were updated
            }
            startLocationUpdates();
        }
    }

	@Override
	public void onPause() {
		Log.i(TAG, "+++ ON PAUSE +++");
		super.onPause();
		for (AsyncTask runningTask : runningTasks) {
			runningTask.cancel(true);
		}

        getGoogleMap().ifPresent(GoogleMap::stopAnimation);

		runningTasks.clear();
	}

	@Override
	public void onResume() {
		Log.i(TAG, "+++ ON RESUME +++");
		super.onResume();
        mapLayout = this;
		doInit();
	}

	@Override
	public void onAttach(Context context) {
		Log.i(TAG, "+++ ON ATTACH +++");
		super.onAttach(context);
		onResume();
	}

	@Override
	public void onDetach() {
		Log.i(TAG, "+++ ON DETACH +++");
		super.onDetach();
		onPause();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.i(TAG, "+++ ON CREATE +++");
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onDestroy() {
		Log.i(TAG, "+++ ON DESTROY +++");
		super.onDestroy();
		onDetach();
		mapView = null;
		googleMap = null;
	}

	@Override
	public void onStart() {
		Log.i(TAG, "+++ ON START +++");
		super.onStart();
	}

	@Override
	public void onMapLoaded() {
		isMapLoaded = true;
	}

	public void refreshLocation() {
		runningTasks.add(new LocationTask().execute());
	}

	private void initializeMap() {
		try {
			//Create global configuration and initialize ImageLoader with this configuration
			ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getActivity().getApplicationContext()).build();
			ImageLoader.getInstance().init(config);

			int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getActivity().getApplicationContext());
			// Showing status
			if (status != ConnectionResult.SUCCESS) // Google Play Services are not available
			{
				Log.i(TAG, "Google Play Services are not available");
			}
			else // Google Play Services are available
			{
				Log.i(TAG, "Google Play Services are available");
				// Getting reference to the SupportMapFragment of activity_main.xml
				MapFragment fm = (MapFragment) getActivity().getFragmentManager().findFragmentById(R.id.map_fragment);

				// Getting GoogleMap object from the fragment
				googleMap = fm.getMap();
				Log.i(TAG, "Got Google map");

				googleMap.setMyLocationEnabled(true);

				googleMap.getUiSettings().setZoomGesturesEnabled(true);

				googleMap.getUiSettings().setZoomControlsEnabled(true);

				googleMap.setOnMarkerClickListener(this);

				googleMap.setInfoWindowAdapter(this);

				googleMap.setOnMapLoadedCallback(this);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			Log.e(TAG, "Failed to initialize Google Map", ex);
		}
	}

	@SuppressWarnings("permission")
	private void startLocationUpdates() {

		if (!arePermissionsGranted && !checkAndRequestPermissions()) {
			Log.i(TAG, "Cannot run location updates. Insufficient permissions.");
			return;
		}

		Log.i(TAG, "Getting system location service");
		// Getting LocationManager object from System Service LOCATION_SERVICE
		locationManager = (LocationManager) getActivity().getApplicationContext().getSystemService(Context.LOCATION_SERVICE);

        try {
            locationManager.addGpsStatusListener(this);

            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                if (Startup.getStreetsCommon().getUserPreferenceValue("suggest_gps").equals("1")) {

                    EnableGPSDialogueListener enableGpsListener = new EnableGPSDialogueListener(getActivity());

                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setMessage("Turn on GPS?")
                            .setMultiChoiceItems(
                                    EnableGPSDialogueListener.getQuestionItems(),
                                    EnableGPSDialogueListener.getCheckedItems(),
                                    EnableGPSOptionListener.getInstance())
                            .setPositiveButton("Yes", enableGpsListener)
                            .setNegativeButton("No", enableGpsListener).create().show();
                } else if (Startup.getStreetsCommon().getUserPreferenceValue("auto_enable_gps").equals("1")) {
                    Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(myIntent);
                }
            }

            updateLocationProvider(true);
        } catch (SecurityException se) {
            //should never happen because we check for perms in advance
            se.printStackTrace();
            Log.e(TAG, "Failed to do location updates. " + se.getMessage(), se);
        }
	}

	private boolean checkAndRequestPermissions() {

		//check course location
		if (checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
			Log.i(TAG, "Permission " + Manifest.permission.ACCESS_COARSE_LOCATION + " is not allowed.");
			Startup.getStreetsCommon().addOutstandingPermission(Manifest.permission.ACCESS_COARSE_LOCATION);
			arePermissionsGranted = false;
		} else {
			Log.i(TAG, "Permission " + Manifest.permission.ACCESS_COARSE_LOCATION + " is allowed.");
			Startup.getStreetsCommon().removeOutstandingPermission(Manifest.permission.ACCESS_COARSE_LOCATION);
			arePermissionsGranted = false;
		}

		//check fine location
		if (checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
		if (outstandingPermissions.size() > 0 && Startup.getStreetsCommon().getUserPreferenceValue("request_gps_perms").equals("1")) {
			Log.i(TAG, "Not enough permissions to do location updates. Requesting from user.");
			requestPermissions(outstandingPermissions.toArray(new String[outstandingPermissions.size()]), PERMISSION_LOCATION_INFO);
			arePermissionsGranted = false;
		}
		else { arePermissionsGranted = true; }

        EnableGPSDialogueListener enableGpsListener = new EnableGPSDialogueListener(getActivity());

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Turn on GPS?")
                .setMultiChoiceItems(
                        EnableGPSDialogueListener.getQuestionItems(),
                        EnableGPSDialogueListener.getCheckedItems(),
                        EnableGPSOptionListener.getInstance())
                .setPositiveButton("Yes", enableGpsListener)
                .setNegativeButton("No", enableGpsListener).create().show();

        return arePermissionsGranted;
	}

	private Optional<GoogleMap> getGoogleMap() {
		return googleMap != null ? Optional.of(googleMap) : Optional.empty();
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
				Startup.getStreetsCommon().setUserPreference("request_gps_perms", "0"); //if user rejects, he probably does not want to be bothered
			} else {
				Log.i(TAG, "Permission granted for " + permissions[c]);
				Startup.getStreetsCommon().removeOutstandingPermission(permissions[c]);
				Startup.getStreetsCommon().setUserPreference("request_gps_perms", "1"); //reset preferences if permissions were updated
				arePermissionsGranted = false;
			}
		}

		if (Startup.getStreetsCommon().getOutstandingPermissions().size() == 0) { //we have everything we need! Great. Start location updates.
			Log.i(TAG, "All required permissions granted. Performing location updates");
			startLocationUpdates();
			arePermissionsGranted = true;
		}
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

    public static MapLayout getInstance() {
		if (mapLayout == null) {
			mapLayout = new MapLayout();
		}
		return mapLayout;
	}

    private void drawMarker(Location location){
        Log.i(TAG, "Found current location at " + location.getLatitude() + " : " + location.getLongitude());
        googleMap.clear();
        googleMap.addMarker(new MarkerOptions()
                .position(new LatLng(location.getLatitude(),location.getLongitude()))
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET))
                .title("Your current location"));
    }

    private void drawPlaceMarker(Place place){
        try
        {
            Log.i(TAG, "Drawing place marker for " + place.name + " at location " + place.latitude + " : " + place.longitude);
            LatLng currentPosition = new LatLng(place.latitude, place.longitude);
			MarkerOptions markerOptions = new MarkerOptions();
			markerOptions.position(currentPosition);
			markerOptions.snippet(place.formatted_address);
			markerOptions.icon(place.icon);
			markerOptions.alpha(0.7f);
			markerOptions.title(place.name);
			Marker placeMarker = googleMap.addMarker(markerOptions);

			//link marker to a place to display info later
			map.put(placeMarker.hashCode(), place);
        }
        catch (Exception ex)
        {
			ex.printStackTrace();
            Log.e(TAG, "Failed to draw place marker for place " + place.name, ex);
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "+++ ON LOCATION CHANGED +++");

        try
        {
            currentLocation = location;

            // Getting latitude of the current location
            double latitude = location.getLatitude();

            // Getting longitude of the current location
            double longitude = location.getLongitude();

            Log.i(TAG, "New location " + latitude + ":" + longitude);

            // Creating a LatLng object for the current location
            LatLng latLng = new LatLng(latitude, longitude);

			//show a random news items
			generator.setSeed(new Date().getTime());
			String info = randomNews.get(generator.nextInt(randomNews.size()));
			MenuActivity.getInstance().setAppInfo(">>> " + info);

            if (firstLocationUpdate)
            {
				// Showing the current location in Google Map
				if (getGoogleMap().isPresent()) {

					//prevent all other processes from updating
					firstLocationUpdate = false;

                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
                    Log.i(TAG, "Camera moved to new location");


                    // Zoom in the Google Map
                    googleMap.animateCamera(CameraUpdateFactory.zoomTo(14), 3000, null);
                    Log.i(TAG, "Camera zoomed to view");

                    location_image.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.default_icon));
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

    @Override
    public boolean onMarkerClick(Marker marker)
    {
        try
        {
            Place clickedPlace = map.get(marker.hashCode());
            if (clickedPlace != null)
            {
				//if clickedPlace != null, marker should link to a place
                try {
                    if (clickedPlace.image.startsWith("http")) {
                        location_image.setImageBitmap(ImageLoader.getInstance().loadImageSync(clickedPlace.image));
                    } else {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            location_image.setImageDrawable(getResources().getDrawable(R.drawable.icon_profile, getContext().getTheme()));
                        } else {
                            location_image.setImageDrawable(getResources().getDrawable(R.drawable.icon_profile));
                        }
                    }
                }
                catch (Exception e) { e.printStackTrace(); }
                location_info.setText(clickedPlace.name);
                location_info.setText(
                    clickedPlace.name + "\r\n" +
                    clickedPlace.type + "\r\n" +
                    clickedPlace.formatted_address
                );
            }

            if (navigator != null) {
                for (Polyline polyline : navigator.getPathLines()) {
                    polyline.remove();
                }
            }

			if (clickedPlace != null && currentLocation != null) {
				navigator = new Navigator(googleMap, clickedPlace.name, clickedPlace.formatted_address, clickedPlace.type,
						new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()),
						new LatLng(clickedPlace.latitude, clickedPlace.longitude));

				navigator.setOnPathSetListener(this);
				navigator.findDirections(false);
			}
            return true;
        }
        catch (Exception ex)
        {
			ex.printStackTrace();
			Log.e(TAG, "Failed to execute marker click event", ex);
            return false;
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
    public void onPathSetListener(String placeName, String address, String type, Directions directions) {
        //displace route paths
	    Log.d(TAG, "New path set");
	    NavigationLayout.getInstance().setDirections(placeName, address, type, directions.getRoutes().get(0).getLegs().get(0).getSteps());
    }

	@Override
	public View getInfoWindow(Marker marker)
	{
		return null;
	}

	@Override
	public View getInfoContents(Marker marker)
	{
		// Getting view from the layout file info_window_layout
		View v = inflater.inflate(R.layout.info_window_layout, null);

		// Getting reference to the TextView to set title
		TextView note = (TextView) v.findViewById(R.id.note);

		note.setText(marker.getTitle() );

		// Returning the view containing InfoWindow contents
		return v;
	}
}
