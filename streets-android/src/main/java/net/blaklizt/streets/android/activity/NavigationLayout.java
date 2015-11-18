package net.blaklizt.streets.android.activity;

import android.location.Location;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import net.blaklizt.streets.android.R;
import net.blaklizt.streets.android.activity.helpers.StreetsAbstractView;
import net.blaklizt.streets.android.adapter.NavigationListAdapter;
import net.blaklizt.streets.android.common.Group;
import net.blaklizt.streets.android.common.StreetsCommon;
import net.blaklizt.streets.android.location.navigation.Directions;
import net.blaklizt.streets.android.location.navigation.Navigator;
import net.blaklizt.streets.android.location.navigation.Steps;
import net.blaklizt.streets.android.location.places.Place;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: tkaviya
 * Date: 6/22/14
 * Time: 12:05 AM
 */
public class NavigationLayout extends StreetsAbstractView implements Navigator.OnPathSetListener, GoogleMap.OnMarkerClickListener {
    private static final String TAG = StreetsCommon.getTag(NavigationLayout.class);
    ExpandableListView navigation_steps;
    TextView nav_location_name;
    TextView nav_location_address;
    TextView nav_location_categories;
    NavigationListAdapter navStepsAdapter;
    ArrayList<Steps> directions;
    LayoutInflater inflater;

    private String currentPlaceName, currentAddress, currentPlaceType;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.i(TAG, "+++ ON CREATE VIEW +++");
        super.onCreateView(inflater, container, savedInstanceState);
        this.inflater = inflater;
        View view = inflater.inflate(R.layout.navigation_layout, container, false);

        // Inflate the layout for this fragment
        navigation_steps = (ExpandableListView) view.findViewById(R.id.nav_location_steps);
        nav_location_name = (TextView) view.findViewById(R.id.nav_location_name);
        nav_location_address = (TextView) view.findViewById(R.id.nav_location_address);
        nav_location_categories = (TextView) view.findViewById(R.id.nav_location_categories);

        nav_location_name.setText("This page will show\nyou directions to any\nlocation/person you\nselect on the MAP page.");

        if (currentPlaceName != null) {
            showNavigationSteps();
            MenuLayout.setAppInfo("[click on list item to get audio announcement]");
        }
        return view;
    }

    public void showNavigationSteps() {
        if (currentPlaceName != null) {

            /* Prepare TTS before first use */
            AppContext.getInstance().getTextToSpeech();

            SparseArray<Group> directionsList = new SparseArray<>();

            nav_location_name.setText(currentPlaceName);
            nav_location_address.setText(currentAddress);
            nav_location_categories.setText(currentPlaceType);
            String header = directions.get(0).getStepTravelMode() + " directions to " + currentPlaceName;

            directionsList.put(0, new Group(header));

            for (Steps step : directions) {
                directionsList.get(0).children.add(Html.fromHtml(step.getStepInstructions()).toString());
            }

            navStepsAdapter = new NavigationListAdapter(this.inflater, directionsList);
            navigation_steps.setAdapter(navStepsAdapter);
            navigation_steps.expandGroup(0);
        }
    }

    public void setDirections(String placeName, String address, String type, ArrayList<Steps> directions) {
        Log.i(TAG, "Setting directions to " + placeName);
        this.directions = directions;
        this.currentPlaceName = placeName;
        this.currentAddress = address;
        this.currentPlaceType = type;
        if (this.inflater != null) {
            showNavigationSteps();
        }
    }

    @Override
    public void onPathSetListener(Place clickedPlace, Marker placeMarker, Directions directions) {
        //displace route paths
        Log.d(TAG, "New path set");
        setDirections(clickedPlace.name, clickedPlace.formatted_address, clickedPlace.type, directions.getRoutes().get(0).getLegs().get(0).getSteps());
        Log.i(TAG, "Set directions to place " + clickedPlace.name);
        placeMarker.showInfoWindow();
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        Location currentLocation = AppContext.getInstance().getCurrentLocation().get();
        Navigator navigator = new Navigator(AppContext.getInstance().getGoogleMap().get(), AppContext.getInstance().getMarkerPlaces().get(marker.getId()), marker,
                new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), marker.getPosition());
        navigator.setOnPathSetListener(this);
        navigator.findDirections(false);
        return false;
    }
}