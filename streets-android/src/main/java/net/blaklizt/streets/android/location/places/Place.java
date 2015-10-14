package net.blaklizt.streets.android.location.places;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: tkaviya
 * Date: 6/25/14
 * Time: 10:26 PM
 */
public class Place {
    public String name;
    public String reference;
    public double latitude;
    public double longitude;
    public String type;
    public Date lastUpdateTime;
    public BitmapDescriptor icon;
    public String image;
    public String formatted_address;
    public String formatted_phone_number;

    public Place (String name, String reference, double latitude, double longitude, String type, String image) {
        this.name = name; this.reference = reference; this.latitude = latitude; this.longitude = longitude; this.type = type;

        switch (type)
        {
            case "Friend":
            {
                this.image = "tich";
                this.icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE);
                break;
            }
            default:
            {
                this.icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN);
                break;
            }
        }
    }
}
