package net.blaklizt.streets.android.location.places;


import android.util.Log;

import net.blaklizt.streets.android.common.StreetsCommon;

import java.lang.reflect.Field;
import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: tkaviya
 * Date: 6/25/14
 * Time: 10:48 PM
 * To change this template use File | Settings | File Templates.
 */
public class PlaceTypes {

	private static final String TAG = StreetsCommon.getTag(PlaceTypes.class);

    public static final String accounting = "";
    public static final String airport = "airport";
    public static final String amusement_park = "";
    public static final String aquarium = "";
    public static final String art_gallery = "";
    public static final String atm = "atm";
    public static final String bakery = "";
    public static final String bank = "bank";
    public static final String bar = "bar";
    public static final String beauty_salon = "beauty_salon";
    public static final String bicycle_store = "";
    public static final String book_store = "book_store";
    public static final String bowling_alley = "";
    public static final String bus_station = "bus_station";
    public static final String cafe = "cafe";
    public static final String campground = "";
    public static final String car_dealer = "car_dealer";
    public static final String car_rental = "";
    public static final String car_repair = "car_repair";
    public static final String car_wash = "car_wash";
    public static final String casino = "casino";
    public static final String cemetery = "";
    public static final String church = "church";
    public static final String city_hall = "";
    public static final String clothing_store = "clothing_store";
    public static final String convenience_store = "convenience_store";
    public static final String courthouse = "courthouse";
    public static final String dentist = "dentist";
    public static final String department_store = "department_store";
    public static final String doctor = "doctor";
    public static final String electrician = "";
    public static final String electronics_store = "";
    public static final String embassy = "";
    public static final String establishment = "";
    public static final String finance = "";
    public static final String fire_station = "";
    public static final String florist = "";
    public static final String food = "food";
    public static final String funeral_home = "";
    public static final String furniture_store = "";
    public static final String gas_station = "gas_station";
    public static final String general_contractor = "";
    public static final String grocery_or_supermarket = "";
    public static final String gym = "";
    public static final String hair_care = "";
    public static final String hardware_store = "";
    public static final String health = "";
    public static final String hindu_temple = "";
    public static final String home_goods_store = "";
    public static final String hospital = "";
    public static final String insurance_agency = "";
    public static final String jewelry_store = "";
    public static final String laundry = "";
    public static final String lawyer = "";
    public static final String library = "";
    public static final String liquor_store = "liquor_store";
    public static final String local_government_office = "";
    public static final String locksmith = "";
    public static final String lodging = "";
    public static final String meal_delivery = "";
    public static final String meal_takeaway = "";
    public static final String mosque = "";
    public static final String movie_rental = "";
    public static final String movie_theater = "";
    public static final String moving_company = "";
    public static final String museum = "";
    public static final String night_club = "";
    public static final String painter = "";
    public static final String park = "";
    public static final String parking = "";
    public static final String pet_store = "";
    public static final String pharmacy = "";
    public static final String physiotherapist = "";
    public static final String place_of_worship = "";
    public static final String plumber = "";
    public static final String police = "police";
    public static final String post_office = "";
    public static final String real_estate_agency = "";
    public static final String restaurant = "";
    public static final String roofing_contractor = "";
    public static final String rv_park = "";
    public static final String school = "";
    public static final String shoe_store = "";
    public static final String shopping_mall = "shopping_mall";
    public static final String spa = "";
    public static final String stadium = "";
    public static final String storage = "";
    public static final String store = "store";
    public static final String subway_station = "";
    public static final String synagogue = "";
    public static final String taxi_stand = "";
    public static final String train_station = "train_station";
    public static final String travel_agency = "";
    public static final String university = "";
    public static final String veterinary_care = "";
    public static final String zoo = "";

    public static String[] getAllPlaces() {
		Log.i(TAG, "Getting all places");
	    Field[] placeTypes = PlaceTypes.class.getDeclaredFields();
	    String[] allPlaces = new String[placeTypes.length];

	    for (int c = 0; c < placeTypes.length; c++)
	    {
		    allPlaces[c] = placeTypes[c].getName();
	    }
		Log.i(TAG, "Returning " + allPlaces.length + " places");
        return allPlaces;
    }

    public static ArrayList<String> getDefaultPlaces() {

		Log.i(TAG, "Getting list of default places");
        ArrayList<String> defaultPlaces = new ArrayList<>();
        defaultPlaces.add(airport);
        defaultPlaces.add(atm);
        defaultPlaces.add(bank);
        defaultPlaces.add(bar);
        defaultPlaces.add(beauty_salon);
        defaultPlaces.add(bus_station);
        defaultPlaces.add(cafe);
        defaultPlaces.add(casino);
        defaultPlaces.add(doctor);
        defaultPlaces.add(food);
        defaultPlaces.add(gas_station);
        defaultPlaces.add(liquor_store);
        defaultPlaces.add(police);
        defaultPlaces.add(shopping_mall);
        defaultPlaces.add(store);
        defaultPlaces.add(train_station);
		Log.i(TAG, "Returning " + defaultPlaces.size() + " places");
        return defaultPlaces;
    }
}
