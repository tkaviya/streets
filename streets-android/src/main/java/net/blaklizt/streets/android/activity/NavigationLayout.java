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

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: tkaviya
 * Date: 6/22/14
 * Time: 12:05 AM
 */
public class NavigationLayout extends StreetsAbstractView implements Navigator.OnPathSetListener, GoogleMap.OnMarkerClickListener {
    private static final String TAG = StreetsCommon.getTag(NavigationLayout.class);
    private Navigator navigator;
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

        nav_location_name.setText("This page will show\n" +
                "you directions to any\n" +
                "location/person you\n" +
                "select on the MAP page.");

        showNavigationSteps();
        return view;
    }

    public void showNavigationSteps() {
        if (currentPlaceName != null) {
            SparseArray<Group> directionsList = new SparseArray<>();

            nav_location_name.setText(currentPlaceName);
            nav_location_address.setText(currentAddress);
            nav_location_categories.setText(currentPlaceType);
            String header = "NAVIGATION [Click on item for speech]";

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
    public void onPathSetListener(String placeName, String address, String type, Directions directions) {
        //displace route paths
        Log.d(TAG, "New path set");
        setDirections(placeName, address, type, directions.getRoutes().get(0).getLegs().get(0).getSteps());
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        Location currentLocation = AppContext.getInstance().getCurrentLocation().get();
        navigator = new Navigator(AppContext.getInstance().getGoogleMap().get(), marker.getTitle(), marker.getSnippet(), null,
                new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), marker.getPosition());
        navigator.setOnPathSetListener(this);
        navigator.findDirections(false);
        return false;
    }
}