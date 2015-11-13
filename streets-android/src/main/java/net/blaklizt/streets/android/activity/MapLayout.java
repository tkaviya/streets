package net.blaklizt.streets.android.activity;

import android.content.Context;
import android.os.Bundle;
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
import net.blaklizt.streets.android.activity.helpers.StreetsAbstractView;
import net.blaklizt.streets.android.common.StreetsCommon;
import net.blaklizt.streets.android.location.navigation.Directions;
import net.blaklizt.streets.android.location.navigation.Navigator;

import static java.lang.String.format;
import static net.blaklizt.streets.android.activity.AppContext.getFragmentView;

/**
 * User: tkaviya
 * Date: 6/21/14
 * Time: 5:58 PM
 */
public class MapLayout extends StreetsAbstractView implements Navigator.OnPathSetListener, GoogleMap.InfoWindowAdapter

        //OnMarkerClickListener,
{
    private static final String TAG = StreetsCommon.getTag(MapLayout.class);
	private View mapView;
	private Navigator navigator;
	private ImageView location_image;
	private TextView location_info;
	private LayoutInflater inflater;

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

        Log.i(TAG, "Queuing tasks for dependency managed execution.");
        SequentialTaskManager.runImmediately(AppContext.getBackgroundExecutionTask(GoogleMapTask.class));
        SequentialTaskManager.runImmediately(AppContext.getBackgroundExecutionTask(LocationUpdateTask.class));
        SequentialTaskManager.runWhenAvailable(AppContext.getBackgroundExecutionTask(PlacesTask.class));
    }

	@Override
	public void onPause() {
        Log.i(TAG, "+++ ON PAUSE +++");
		super.onPause();
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
	}

	@Override
	public void onStart() {
		Log.i(TAG, "+++ ON START +++");
		super.onStart();
	}

    @Override
    public void onPathSetListener(String placeName, String address, String type, Directions directions) {
        //displace route paths
	    Log.d(TAG, "New path set");
        ((NavigationLayout)getFragmentView(NavigationLayout.class))
                .setDirections(placeName, address, type, directions.getRoutes().get(0).getLegs().get(0).getSteps());
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
