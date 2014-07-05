package net.blaklizt.streets.android.location.places;

/**
 * Created with IntelliJ IDEA.
 * User: tkaviya
 * Date: 6/25/14
 * Time: 10:26 PM
 */
public class Place {
    public String name;
    public String icon;
    public String formatted_address;
    public String formatted_phone_number;
    public String reference;
    public String type;
    public double latitude;
    public double longitude;

    public Place (String name, String reference, double latitude, double longitude, String type) {
        this.name = name; this.reference = reference; this.latitude = latitude; this.longitude = longitude; this.type = type;
    }
}
