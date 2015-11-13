package net.blaklizt.streets.android.activity;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import net.blaklizt.streets.android.R;
import net.blaklizt.streets.android.activity.helpers.GoogleMapTask;
import net.blaklizt.streets.android.activity.helpers.LocationUpdateTask;
import net.blaklizt.streets.android.activity.helpers.PlacesTask;
import net.blaklizt.streets.android.activity.helpers.SequentialTaskManager;
import net.blaklizt.streets.android.common.StreetsCommon;
import net.blaklizt.streets.android.common.TaskInfo;
import net.blaklizt.streets.android.common.utils.Optional;
import net.blaklizt.streets.android.location.navigation.Directions;
import net.blaklizt.streets.android.location.navigation.Navigator;
import net.blaklizt.streets.android.navigation.Places;
import net.blaklizt.streets.android.sidemenu.fragment.StreetsFragment;

import java.util.ArrayList;
import java.util.HashMap;

import static java.lang.String.format;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;

/**
 * User: tkaviya
 * Date: 6/21/14
 * Time: 5:58 PM
 */
public class MapLayout extends StreetsFragment implements Navigator.OnPathSetListener, GoogleMap.InfoWindowAdapter

        //OnMarkerClickListener,
{

    public final HashMap<String, TaskInfo> TASK_EXECUTION_INFO = new HashMap<>();

    private static final String TAG = StreetsCommon.getTag(MapLayout.class);

	private static MapLayout mapLayout = null;

    private static LocationUpdateTask locationUpdateTask = null;

	private View mapView;

	private GoogleMap googleMap;
    private Location currentLocation = null;

    private LocationManager locationManager = null;

	private Navigator navigator;
	private ImageView location_image;
	private TextView location_info;
	private LayoutInflater inflater;

    public MapLayout() {
        mapLayout = this;
        registerStreetsFragment("Tha Streetz", R.drawable.icn_close);
    }

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.i(TAG, "+++ ON CREATE VIEW +++");
        super.onCreateView(inflater, container, savedInstanceState);

        Log.i(TAG, format("LayoutInflater: %s", inflater != null ? inflater.toString() : null));
        Log.i(TAG, format("ViewGroup: %s", container != null ? container.getTag() : null));
		Log.i(TAG, format("SavedInstanceState: %s", savedInstanceState != null ? savedInstanceState.toString() : null));

        this.inflater = inflater;

        if (mapView == null) {
            mapView = inflater.inflate(R.layout.map_layout, container, false);
            location_image = (ImageView) mapView.findViewById(R.id.location_image_view);
            location_info = (TextView) mapView.findViewById(R.id.location_categories_text_view);
            setRetainInstance(true);


            setLocationManager((LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE));
        }

        startTasks();

        return mapView;
	}

    public void startTasks() {
        Log.i(TAG, "+++ START TASKS +++");

        if (mapView == null) {
            Log.i(TAG, "View is not ready to start running tasks. Will not start tasks.");
            return;
        }

        String gMapTask = GoogleMapTask.class.getSimpleName();
        String locationTask = LocationUpdateTask.class.getSimpleName();
        String placesTask = Places.class.getSimpleName();

        if (TASK_EXECUTION_INFO.isEmpty()) {
            Log.i(TAG, "Initializing task execution information.");

            TASK_EXECUTION_INFO.put(gMapTask,     new GoogleMapTask());
            TASK_EXECUTION_INFO.put(locationTask, new LocationUpdateTask());
            TASK_EXECUTION_INFO.put(placesTask,   new PlacesTask());
        }

        Log.i(TAG, "Queuing tasks for dependency managed execution.");
        SequentialTaskManager.runImmediately(TASK_EXECUTION_INFO.get(gMapTask));
        SequentialTaskManager.runImmediately(TASK_EXECUTION_INFO.get(locationTask));
        SequentialTaskManager.runWhenAvailable(TASK_EXECUTION_INFO.get(placesTask));
    }

	@Override
	public void onPause() {
        Log.i(TAG, "+++ ON PAUSE +++");
		super.onPause();
        SequentialTaskManager.cancelRunningTasks();
	}

	@Override
	public void onResume() {
		Log.i(TAG, "+++ ON RESUME +++");
		super.onResume();
//		startTasks();
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

    public void setGoogleMap(GoogleMap googleMap) {
        this.googleMap = googleMap;
    }

    public Optional<GoogleMap> getGoogleMap() {
        return Optional.ofNullable(googleMap);
    }

    public void setCurrentLocation(Location currentLocation) {
        this.currentLocation = currentLocation;
    }

    public Optional<Location> getCurrentLocation() {
        return Optional.ofNullable(currentLocation);
    }

	public Optional<LocationManager> getLocationManager() {
		return Optional.ofNullable(locationManager);
	}

    public void setLocationManager(LocationManager locationManager) {
        this.locationManager = locationManager;
        this.locationManager.addGpsStatusListener(locationUpdateTask);
    }

    public static MapLayout getInstance() {
		if (mapLayout == null) {
			mapLayout = new MapLayout();
		}
		return mapLayout;
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

		note.setText(marker.getTitle());

		// Returning the view containing InfoWindow contents
		return v;
	}

}
