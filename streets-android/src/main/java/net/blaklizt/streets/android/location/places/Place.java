package net.blaklizt.streets.android.location.places;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.nostra13.universalimageloader.core.ImageLoader;

import net.blaklizt.streets.android.common.StreetsCommon;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;

import static android.graphics.Bitmap.createScaledBitmap;
import static net.blaklizt.streets.android.common.StreetsCommon.getCacheDir;

/**
 * Created with IntelliJ IDEA.
 * User: tkaviya
 * Date: 6/25/14
 * Time: 10:26 PM
 */
public class Place {

    private static HashMap<String, BitmapDescriptor> cachedBitmaps = new HashMap<>();
    private final String TAG = StreetsCommon.getTag(Place.class);

    public String name;
    public String reference;
    public double latitude;
    public double longitude;
    public String type;
    public Date lastUpdateTime;
    public BitmapDescriptor icon;
    public String icon_url;
    //    public String image;
    public String formatted_address;
    public String formatted_phone_number;
    public double rating;

    public Place(String name, String reference, double latitude, double longitude, String type,
                 String icon_url, String formatted_address, String formatted_phone_number, double rating) {
        this.name = name;
        this.reference = reference;
        this.latitude = latitude;
        this.longitude = longitude;
        this.type = type;
        this.icon_url = icon_url;
        this.formatted_address = formatted_address;
        this.formatted_phone_number = formatted_phone_number;
        this.rating = rating;

        this.lastUpdateTime = new Date();


        if (cachedBitmaps.get(icon_url) == null) {

            String fileName = icon_url.substring(icon_url.lastIndexOf("/") + 1);
            String filePath = getCacheDir(StreetsCommon.FILE_DATA_TYPE.ICON);
            if (!filePath.endsWith(File.separator)) {
                filePath += File.separator;
            }
            filePath += fileName;

            Bitmap scaledBitmap;
            File iconFile;

            try {
                if (!(iconFile = new File(filePath)).exists()) {
                    Log.i(TAG, "Loading icon for " + name + " from " + icon_url);
                    Bitmap rawBitmap = ImageLoader.getInstance().loadImageSync(icon_url);
                    scaledBitmap = createScaledBitmap(ImageLoader.getInstance().loadImageSync(icon_url), rawBitmap.getWidth() / 3, rawBitmap.getHeight() / 3, false);
                    this.icon = BitmapDescriptorFactory.fromBitmap(scaledBitmap);


                    /* cache bitmap in memory */
                    cachedBitmaps.put(icon_url, this.icon);

                    /* save downloaded bitmap to file cache */
                    BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(iconFile));
                    scaledBitmap.compress(Bitmap.CompressFormat.PNG, 100, bufferedOutputStream);
                    bufferedOutputStream.flush();
                    bufferedOutputStream.close();
                } else {
                    Log.i(TAG, "Loading icon for " + name + " from " + filePath);

                    /* load bitmap from file cache */
                    scaledBitmap = BitmapFactory.decodeStream(new BufferedInputStream(new FileInputStream(iconFile)));
                    this.icon = BitmapDescriptorFactory.fromBitmap(scaledBitmap);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (this.icon == null) {
                this.icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE);
            }

            /* cache bitmap in memory */
            cachedBitmaps.put(icon_url, this.icon);

        } else {
            Log.i(TAG, "Getting cached bitmap for " + name + " for icon url " + icon_url);
            this.icon = cachedBitmaps.get(icon_url);
        }

    }

    public Place(String name, double latitude, double longitude, String type, BitmapDescriptor icon,
                 Date lastUpdateTime, String formatted_address, String formatted_phone_number, double rating) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.type = type;
        this.icon = icon;
        this.lastUpdateTime = lastUpdateTime;
        this.formatted_address = formatted_address;
        this.formatted_phone_number = formatted_phone_number;
        this.rating = rating;
    }
}