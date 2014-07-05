package net.blaklizt.streets.android;

import android.app.Dialog;
import android.database.sqlite.SQLiteDatabase;
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
import android.widget.ImageView;
import android.widget.TextView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.*;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import net.blaklizt.streets.android.location.navigation.Directions;
import net.blaklizt.streets.android.location.navigation.Navigator;
import net.blaklizt.streets.android.location.places.Place;
import net.blaklizt.streets.android.location.places.PlaceTypes;
import net.blaklizt.streets.android.location.places.PlacesService;
import net.blaklizt.streets.android.persistence.Neighbourhood;

import java.util.HashMap;
import java.util.LinkedList;

/**
 * User: tkaviya
 * Date: 6/21/14
 * Time: 5:58 PM
 */
public class MapLayout extends FragmentActivity implements LocationListener, OnMarkerClickListener, Navigator.OnPathSetListener {

    public static final String TAG = "MapLayout";
    protected static MapLayout mapLayout;
    protected GoogleMap googleMap;
    protected Navigator navigator;
    protected Location currentLocation;
    protected HashMap<Marker, Place> map = new HashMap<>();
    protected boolean firstLocationUpdate = true;
    protected ImageView location_image;
    protected TextView location_name_text_view;
    protected TextView location_address_text_view;
    protected TextView location_categories_text_view;
    protected SQLiteDatabase neighbourhoodDB;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "+++ ON CREATE +++");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_layout);

        neighbourhoodDB = new Neighbourhood(getApplicationContext()).getWritableDatabase();

        mapLayout = this;

        location_image = (ImageView) findViewById(R.id.location_image_view);
        location_name_text_view = (TextView) findViewById(R.id.location_name_text_view);
        location_address_text_view = (TextView) findViewById(R.id.location_address_text_view);
        location_categories_text_view = (TextView) findViewById(R.id.location_categories_text_view);

        //Create global configuration and initialize ImageLoader with this configuration
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext()).build();
        ImageLoader.getInstance().init(config);
    }

    public static MapLayout getInstance() { return mapLayout; }

    public SQLiteDatabase getNeighbourhoodDB() { return neighbourhoodDB; }

    private void drawMarker(Location location){
//        Log.i(TAG, "Found current location at " + location.getLatitude() + " : " + location.getLongitude());
//        googleMap.clear();
//        LatLng currentPosition = new LatLng(location.getLatitude(),location.getLongitude());
//        googleMap.addMarker(new MarkerOptions()
//                .position(currentPosition)
//                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
//                .title("Your current location"));
    }

    private void drawPlaceMarker(Place place){
        Log.i(TAG, "Drawing place marker for " + place.name + " at location " + place.latitude + " : " + place.longitude);
        LatLng currentPosition = new LatLng(place.latitude, place.longitude);
        map.put(googleMap.addMarker(new MarkerOptions()
                .position(currentPosition)
                .snippet(place.formatted_address)
                .icon(!place.type.equals("home") ? BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)
                     : BitmapDescriptorFactory.fromResource(R.drawable.tich))
                .alpha(0.7f)
                .title(place.name)), place);
    }

    @Override
    public void onStart() {
        Log.i(TAG, "+++ ON START +++");
        super.onStart();

        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getBaseContext());
        // Showing status
        if (status != ConnectionResult.SUCCESS) // Google Play Services are not available
        {
            Log.i(TAG, "Google Play Services are not available");
            int requestCode = 10;
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status, this, requestCode);
            dialog.show();
        }
        else // Google Play Services are available
        {
            Log.i(TAG, "Google Play Services are available");
            // Getting reference to the SupportMapFragment of activity_main.xml
            SupportMapFragment fm = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

            // Getting GoogleMap object from the fragment
            googleMap = fm.getMap();
            Log.i(TAG, "Got Google map");
            // Enabling MyLocation Layer of Google Map
            googleMap.setMyLocationEnabled(true);

            googleMap.setOnMarkerClickListener(this);

            // Getting LocationManager object from System Service LOCATION_SERVICE
            LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            Log.i(TAG, "Got LocationManager");
            // Creating a criteria object to retrieve provider
            Criteria criteria = new Criteria();

            // Getting the name of the best provider
            String provider = locationManager.getBestProvider(criteria, true);

//            // Getting Current Location
//            Location location = locationManager.getLastKnownLocation(provider);
//
//             if(location!=null){
//                //PLACE THE INITIAL MARKER
//                 drawMarker(location);
//            }
            locationManager.requestLocationUpdates(provider, 20000, 0, this);
        }
    }


    @Override
    public void onResume() {
        Log.i(TAG, "+++ ON RESUME +++");
        super.onResume();

        if (currentLocation != null) {
            LinkedList<Place> nearbyPlaces = PlacesService.nearby_search(
                    currentLocation.getLatitude(), currentLocation.getLongitude(), 5000, PlaceTypes.getDefaultPlaces());

            for (Place place : nearbyPlaces) { drawPlaceMarker(place); }
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.i(TAG, "+++ ON LOCATION CHANGED +++");

        currentLocation = location;


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
            googleMap.animateCamera(CameraUpdateFactory.zoomTo(13));
            Log.i(TAG, "Camera zoomed to view");

            location_image.setImageDrawable(getResources().getDrawable(R.drawable.default_icon));
            location_name_text_view.setText("Current Location");
            location_address_text_view.setText("Latitude: " + latitude);
            location_categories_text_view.setText("Longitude: " + longitude);

            drawMarker(location);

            LinkedList<Place> nearbyPlaces = PlacesService.nearby_search(
                    currentLocation.getLatitude(), currentLocation.getLongitude(), 5000, PlaceTypes.getDefaultPlaces());

            for (Place place : nearbyPlaces) { drawPlaceMarker(place); }

            firstLocationUpdate = false;
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker){
        Place clickedPlace = map.get(marker);
        if (clickedPlace != null)
        {
            try { location_image.setImageBitmap(ImageLoader.getInstance().loadImageSync(clickedPlace.icon)); }
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
        navigator = new Navigator(googleMap,
            new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()),
            new LatLng(clickedPlace.latitude, clickedPlace.longitude));

        navigator.findDirections(false);

        navigator.setOnPathSetListener(this);

        return true;
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
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.option_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.location:
//            Intent serverIntent = new Intent(this, StreetsLocation.class);
//            startActivityForResult(serverIntent, 0);
//            return true;
        }
        return false;
    }

    @Override
    public void onPathSetListener(Directions directions) {
        //displace route paths
    }
}
