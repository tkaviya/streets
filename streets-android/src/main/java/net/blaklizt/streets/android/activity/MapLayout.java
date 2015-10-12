package net.blaklizt.streets.android.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.*;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
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
import net.blaklizt.streets.android.location.navigation.Directions;
import net.blaklizt.streets.android.location.navigation.Navigator;
import net.blaklizt.streets.android.location.places.Place;
import net.blaklizt.streets.android.location.places.PlacesService;

import java.util.*;

/**
 * User: tkaviya
 * Date: 6/21/14
 * Time: 5:58 PM
 */
public class MapLayout extends Fragment
	implements	LocationListener, OnMarkerClickListener, 
				Navigator.OnPathSetListener, GoogleMap.InfoWindowAdapter,
				GpsStatus.Listener
{
	private LinkedList<String> randomNews = new LinkedList<>();

	private LinkedList<AsyncTask> runningTasks = new LinkedList<>();

	private class LocationTask extends AsyncTask<Void, Void, Void> {

        LinkedList<Place> nearbyPlaces;

        ProgressDialog progressDialog;

        @Override
        protected Void doInBackground(Void... param) {

			if (currentLocation != null) {
				nearbyPlaces = PlacesService.nearby_search(
						currentLocation.getLatitude(),
						currentLocation.getLongitude(), 5000,
						Streets.getStreetsCommon().getStreetsDBHelper().getPlacesOfInterest()
				);
			}
			else {
				getActivity().runOnUiThread(new Runnable() {
					@Override
					public void run() {
						Toast.makeText(getActivity(), "Current location unknown.\n" +
								"Check location settings.", Toast.LENGTH_LONG).show();
					}
				});
			}
            return null;
        }

        @Override
        protected void onPreExecute()
        {
            progressDialog = ProgressDialog.show(
					getActivity(),
					"Updating location",
					"Updating location...",
					true, true);
            progressDialog.show();
        }

        @Override
        protected void onProgressUpdate(Void... progress) { }

        @Override
        protected void onPostExecute(Void result)
        {
			googleMap.clear();

            if (nearbyPlaces != null) for (Place place : nearbyPlaces) { drawPlaceMarker(place); }

			progressDialog.hide();

			runningTasks.remove(this);
        }
    }

    private static final String TAG = StreetsCommon.getTag(MapLayout.class);


	protected static MapLayout mapLayout = null;
    protected GoogleMap googleMap;
    protected Navigator navigator;
    protected Location currentLocation;
    protected LocationManager locationManager;
	protected HashMap <Integer, Place> map = new HashMap<>();
    protected boolean firstLocationUpdate = true;
    protected ImageView location_image;
    protected TextView location_name_text_view;
    protected TextView location_address_text_view;
    protected TextView location_categories_text_view;
	protected TextView status_text_view;
	protected LayoutInflater inflater;

	protected Random generator = new Random(new Date().getTime());

	//location provider data
	protected final static String PROVIDER_GPS = "gps";
	protected final static String PROVIDER_CHEAPEST = "passive";
	protected final static Integer MINIMUM_REFRESH_TIME = 20000;
	protected String defaultProvider = PROVIDER_CHEAPEST;		//default working provider
	protected LocationProvider currentProvider = null;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable  Bundle savedInstanceState) {
	    Log.i(TAG, "+++ ON CREATE VIEW +++");
	    super.onCreateView(inflater, container, savedInstanceState);

	    this.inflater = inflater;
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.map_layout, container, false);
        // do your view initialization here
        mapLayout = this;

		randomNews.add("Weather is 17 degrees");
		randomNews.add("Traffic expected for 2 hours");
		randomNews.add("DoubleBurger special @Wimpy 2day");
		randomNews.add("C.Nyovest @ Bar9 2night");
		randomNews.add("Your friend Ntwaizen is nearby");
		randomNews.add("Yo fav. food (KFC) is nearby!");
		randomNews.add("Distance 2 home: 10m | 1 min");
		randomNews.add("Distance 2 work: 1km | 5 min");

		status_text_view = (TextView) view.findViewById(R.id.status_text_view);
        location_image =  (ImageView) view.findViewById(R.id.location_image_view);
        location_name_text_view = (TextView) view.findViewById(R.id.location_name_text_view);
        location_address_text_view = (TextView) view.findViewById(R.id.location_address_text_view);
        location_categories_text_view = (TextView) view.findViewById(R.id.location_categories_text_view);

        initializeMap();

        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
	    Log.i(TAG, "+++ ON CREATE +++");
	    super.onCreate(savedInstanceState);
    }

	@Override
	public void onDestroy()
	{
		Log.i(TAG, "+++ ON DESTROY +++");
		for (AsyncTask runningTask : runningTasks) { runningTask.cancel(true); }
		runningTasks.clear();
	}

	public void refreshLocation()
	{
		runningTasks.add(new LocationTask().execute());
	}

    private void initializeMap()
    {
        try
        {
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
                MapFragment fm = (MapFragment)getActivity().getFragmentManager().findFragmentById(R.id.map_fragment);

                // Getting GoogleMap object from the fragment
                googleMap = fm.getMap();
                Log.i(TAG, "Got Google map");

				googleMap.setMyLocationEnabled(true);

				googleMap.getUiSettings().setZoomGesturesEnabled(true);

				googleMap.getUiSettings().setZoomControlsEnabled(true);

                googleMap.setOnMarkerClickListener(this);

	            googleMap.setInfoWindowAdapter(this);

                Log.i(TAG, "Getting system location service");
                // Getting LocationManager object from System Service LOCATION_SERVICE
                locationManager = (LocationManager)getActivity().getApplicationContext().getSystemService(Context.LOCATION_SERVICE);

				locationManager.addGpsStatusListener(this);


				if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
				{
					if (Streets.getStreetsCommon().getUserPreference("SuggestGPS").equals("1"))
					{
						GPSDialogueListener gpsDialogueListener = new GPSDialogueListener(getActivity());
						GPSDialogueListener.GPSDialogueOptionsListener gpsDialogueOptionsListener = new GPSDialogueListener.GPSDialogueOptionsListener();
						CharSequence[] items = new CharSequence[]{"Never ask again"};

						AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
						builder.setMessage("Turn on GPS?")
								.setMultiChoiceItems(items, new boolean[]{true}, gpsDialogueOptionsListener)
								.setPositiveButton("Yes", gpsDialogueListener)
								.setNegativeButton("No", gpsDialogueListener).show();
					}
					else if (Streets.getStreetsCommon().getUserPreference("AutoEnableGPS").equals("1"))
					{
						Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
						startActivity(myIntent);
					}
				}

				updateLocationProvider(true);
            }
        }
        catch (Exception ex)
        {
			ex.printStackTrace();
            Log.e(TAG, "Failed to initialize Google Map", ex);
        }
    }

	public void updateLocationProvider(boolean checkGPS)
	{
		try
		{
			if (checkGPS)
			{
				// Creating a criteria object to retrieve provider
				Log.i(TAG, "Checking for preferred location provider 'GPS' for best accuracy.");
				Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

				if (location != null)
				{
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
				}
				else
				{
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

				while (location == null && providerIterator.hasNext())
				{
					providerName = (String)providerIterator.next();
					if (providerName.equalsIgnoreCase(LocationManager.GPS_PROVIDER))	continue; //skip LocationManager.GPS_PROVIDER,		we just tried it above
					if (providerName.equalsIgnoreCase(defaultProvider))	continue; //skip defaultProvider,	we just tried it above
					Log.i(TAG, "Trying provider " + providerName);
					location = locationManager.getLastKnownLocation(providerName);
					if (location != null)
					{
						Log.i(TAG, "Found working provider: " + providerName);
						defaultProvider = providerName; //found a working provider, use this to do future updates
					}
				}
			}

			if (location != null)
			{
				//PLACE THE INITIAL MARKER
				Log.i(TAG, "Found location using provider '" + defaultProvider + "'. Placing initial marker.");
				drawMarker(location);
			}
			else {
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
		catch (Exception ex)
		{
			ex.printStackTrace();
			Log.e(TAG, "Failed to initialize Google Map", ex);
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

    public static MapLayout getInstance() { return mapLayout; }

    private void drawMarker(Location location){
        Log.i(TAG, "Found current location at " + location.getLatitude() + " : " + location.getLongitude());
        googleMap.clear();
        LatLng currentPosition = new LatLng(location.getLatitude(),location.getLongitude());
        googleMap.addMarker(new MarkerOptions()
                .position(currentPosition)
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
            Log.e(TAG, "Failed to draw place marker for place " + place.name, ex);
        }
    }

    @Override
    public void onStart() {
        Log.i(TAG, "+++ ON START +++");
        super.onStart();
		status_text_view.setText("I'm the streets look both way before you cross me");
    }

    @Override
    public void onResume() {
        Log.i(TAG, "+++ ON RESUME +++");
        super.onResume();

        try
        {
            if (currentLocation != null) {
				Log.i(TAG, "Data is stale. Refreshing location");
                refreshLocation();
            }
        }
        catch (Exception ex)
        {
            Log.e(TAG, "Failed to resume streets map layout", ex);
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
			status_text_view.setText(">>> " + info);

            if (firstLocationUpdate)
            {
				//immediately prevent all other processes from updating
				firstLocationUpdate = false;

				// Showing the current location in Google Map
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
                Log.i(TAG, "Camera moved to new location");

                // Zoom in the Google Map
                googleMap.animateCamera(CameraUpdateFactory.zoomTo(12), 3000, null);
                Log.i(TAG, "Camera zoomed to view");

                location_image.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.default_icon));
                location_name_text_view.setText("Current Location");
                location_address_text_view.setText("Latitude: " + latitude);
                location_categories_text_view.setText("Longitude: " + longitude);

                drawMarker(location);

	            refreshLocation();
            }
        }
        catch (Exception ex)
        {
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
                        location_image.setImageDrawable(getResources().getDrawable(R.drawable.friend));
                    }
                }
                catch (Exception e) { e.printStackTrace(); }
                location_name_text_view.setText(clickedPlace.name);
                location_address_text_view.setText(clickedPlace.formatted_address);
                location_categories_text_view.setText(clickedPlace.type);
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
