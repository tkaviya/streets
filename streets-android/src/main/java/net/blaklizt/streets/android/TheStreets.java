package net.blaklizt.streets.android;

import android.app.Dialog;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import net.blaklizt.streets.android.location.places.Place;
import net.blaklizt.streets.android.location.places.PlaceTypes;
import net.blaklizt.streets.android.location.places.PlacesService;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: tkaviya
 * Date: 6/21/14
 * Time: 5:58 PM
 * To change this template use File | Settings | File Templates.
 */
public class TheStreets extends FragmentActivity implements LocationListener {

    public static final String TAG = "TheStreets";
    public static final String TOAST = "toast";

    Location currentLocation;

    GoogleMap googleMap;
    private boolean firstLocationUpdate = true;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "+++ ON CREATE +++");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.streets_layout);

        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getBaseContext());

        // Showing status
        if(status != ConnectionResult.SUCCESS){ // Google Play Services are not available
            Log.i(TAG, "Google Play Services are not available");
            int requestCode = 10;
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status, this, requestCode);
            dialog.show();

        } else { // Google Play Services are available
            Log.i(TAG, "Google Play Services are available");
            // Getting reference to the SupportMapFragment of activity_main.xml
            SupportMapFragment fm = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

            // Getting GoogleMap object from the fragment
            googleMap = fm.getMap();
            Log.i(TAG, "Got Google map");
            // Enabling MyLocation Layer of Google Map
            googleMap.setMyLocationEnabled(true);

            // Getting LocationManager object from System Service LOCATION_SERVICE
            LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            Log.i(TAG, "Got LocationManager");
            // Creating a criteria object to retrieve provider
            Criteria criteria = new Criteria();

            // Getting the name of the best provider
            String provider = locationManager.getBestProvider(criteria, true);

            // Getting Current Location
            Location location = locationManager.getLastKnownLocation(provider);

             if(location!=null){
                //PLACE THE INITIAL MARKER
                 drawMarker(location);
            }
            locationManager.requestLocationUpdates(provider, 20000, 0, this);
        }
    }

    private void drawMarker(Location location){
        Log.i(TAG, "Drawing marker at location " + location.getLatitude() + " : " + location.getLongitude());
        googleMap.clear();
        LatLng currentPosition = new LatLng(location.getLatitude(),location.getLongitude());
        googleMap.addMarker(new MarkerOptions()
                .position(currentPosition)
                .snippet("Lat: " + location.getLatitude() + " Lng: "+ location.getLongitude())
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                .title("Your current location"));
    }

    private void drawPlaceMarker(Place place){
        Log.i(TAG, "Drawing place marker for " + place.name + " at location " + place.latitude + " : " + place.longitude);
        googleMap.clear();
        LatLng currentPosition = new LatLng(place.latitude, place.longitude);
        googleMap.addMarker(new MarkerOptions()
                .position(currentPosition)
                .snippet("Lat: " + place.latitude + " Lng: "+ place.longitude)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
//                .icon(BitmapDescriptorFactory.fromPath(place.icon))
                .title(place.type.toString() + "\n" + place.name + "\n" + place.formatted_address));
    }

    @Override
    public void onStart() {
        Log.i(TAG, "+++ ON START +++");
        super.onStart();
        TextView textView = (TextView) findViewById(R.id.text_view);
        textView.setText("I'm the streets, look both way before you cross me!");
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.i(TAG, "+++ ON LOCATION CHANGED +++");

        currentLocation = location;

        TextView tvLocation = (TextView) findViewById(R.id.text_view);

        // Getting latitude of the current location
        double latitude = location.getLatitude();

        // Getting longitude of the current location
        double longitude = location.getLongitude();

        Log.i(TAG, "New location " + latitude + ":" + longitude);

        // Creating a LatLng object for the current location
        LatLng latLng = new LatLng(latitude, longitude);

        if (firstLocationUpdate)
        {
            // Showing the current location in Google Map
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            Log.i(TAG, "Camera moved to new location");

            // Zoom in the Google Map
            googleMap.animateCamera(CameraUpdateFactory.zoomTo(15));
            Log.i(TAG, "Camera zoomed to view");

            firstLocationUpdate = false;
        }

        // Setting latitude and longitude in the TextView tv_location
        tvLocation.setText("Latitude:" + latitude + ", Longitude:" + longitude);

        drawMarker(location);
    }

    @Override
    public void onProviderDisabled(String provider) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onProviderEnabled(String provider) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.option_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.location:

            ArrayList<Place> nearbyPlaces = PlacesService.nearby_search(
                    currentLocation.getLatitude(), currentLocation.getLongitude(), 5000, PlaceTypes.getDefaultPlaces());

            for (Place place : nearbyPlaces) { drawPlaceMarker(place); }
//            Intent serverIntent = new Intent(this, StreetsLocation.class);
//            startActivityForResult(serverIntent, 0);
//            return true;
        }
        return false;
    }
}
