package net.blaklizt.streets.android.activity;

import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import net.blaklizt.streets.android.R;
import net.blaklizt.streets.android.activity.helpers.GoogleMapTask;
import net.blaklizt.streets.android.activity.helpers.LocationUpdateTask;
import net.blaklizt.streets.android.activity.helpers.PlacesTask;
import net.blaklizt.streets.android.activity.helpers.SequentialTaskManager;
import net.blaklizt.streets.android.activity.helpers.StreetsAbstractView;
import net.blaklizt.streets.android.common.StreetsCommon;
import net.blaklizt.streets.android.location.navigation.Navigator;
import net.blaklizt.streets.android.location.places.Place;

import static java.lang.String.format;

/**
 * User: tkaviya
 * Date: 6/21/14
 * Time: 5:58 PM
 */
public class MapLayout extends StreetsAbstractView implements GoogleMap.InfoWindowAdapter, GoogleMap.OnMarkerClickListener
{
    private static final String TAG = StreetsCommon.getTag(MapLayout.class);
	private View mapView;
	private ImageView location_image;
	private TextView location_info;
	private LayoutInflater inflater;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.i(TAG, "+++ ON CREATE VIEW +++");
        super.onCreateView(inflater, container, savedInstanceState);

        Log.i(TAG, format("LayoutInflater: %s", this.inflater != null ? this.inflater.toString() : null));
        Log.i(TAG, format("MapView: %s", mapView != null ? mapView.toString() : null));
        Log.i(TAG, format("ViewGroup: %s", container != null ? container.getTag() : null));
		Log.i(TAG, format("SavedInstanceState: %s", savedInstanceState != null ? savedInstanceState.toString() : null));

        this.inflater = inflater;

        if (mapView == null) {
            mapView = inflater.inflate(R.layout.map_layout, container, false);
            location_image = (ImageView) mapView.findViewById(R.id.location_image_view);
            location_info = (TextView) mapView.findViewById(R.id.location_categories_text_view);
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
        SequentialTaskManager.runWhenAvailable(AppContext.getBackgroundExecutionTask(GoogleMapTask.class));
        SequentialTaskManager.runWhenAvailable(AppContext.getBackgroundExecutionTask(LocationUpdateTask.class));
        SequentialTaskManager.runWhenAvailable(AppContext.getBackgroundExecutionTask(PlacesTask.class));
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
	    Place clickedPlace = AppContext.getInstance().getMarkerPlaces().get(marker.getId());
        location_info.setText(marker.getTitle() + "\n" + marker.getSnippet());
	    location_image.setImageDrawable(clickedPlace.image);
	    Location currentLocation = AppContext.getInstance().getCurrentLocation().get();
        Navigator navigator = new Navigator(AppContext.getInstance().getGoogleMap().get(), AppContext.getInstance().getMarkerPlaces().get(marker.getId()), marker,
                new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), marker.getPosition());
        navigator.setOnPathSetListener((NavigationLayout)AppContext.getFragmentView(NavigationLayout.class));
        navigator.findDirections(false);
        return false;
    }

	@Override
	public View getInfoWindow(Marker marker)
	{
		return null;
	}

	@Override
	public View getInfoContents(Marker marker)
	{
		View v = inflater.inflate(R.layout.info_window_layout, null);
		TextView note = (TextView) v.findViewById(R.id.note);
		note.setText(marker.getTitle());
//        note.setCompoundDrawables(null, null, ContextCompat.getDrawable(getContext(), R.drawable.compass), null);
//        note.setClickable(true);
//        note.setOnClickListener(v1 -> {
//            Log.i(TAG, "+++ ON INFO CONTENTS CLICK +++");
//            MenuLayout.getInstance().getSupportFragmentManager().beginTransaction()
//                    .replace(R.id.content_frame, getFragmentView(NavigationLayout.class), getFragmentView(NavigationLayout.class).getViewName())
//                    .setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
//                    .commit();
//        });

		// Returning the view containing InfoWindow contents
		return v;
	}

}
