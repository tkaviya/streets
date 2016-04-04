package net.blaklizt.streets.android.location.places;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.nostra13.universalimageloader.core.ImageLoader;
import net.blaklizt.streets.android.R;
import net.blaklizt.streets.android.activity.AppContext;
import net.blaklizt.streets.android.common.StreetsCommon;

import java.io.*;
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

	private static final HashMap<String, Integer> mappedPlaceTypeIcons = new HashMap<>();

	static {
		mappedPlaceTypeIcons.put("airport", R.drawable.res23);
		mappedPlaceTypeIcons.put("atm", R.drawable.res125);
		mappedPlaceTypeIcons.put("bank", R.drawable.res126); //575
		mappedPlaceTypeIcons.put("bar", R.drawable.res744);
		mappedPlaceTypeIcons.put("book store", R.drawable.res488);
		mappedPlaceTypeIcons.put("bus station", R.drawable.res178);
		mappedPlaceTypeIcons.put("cafe", R.drawable.res133);
		mappedPlaceTypeIcons.put("church", R.drawable.res813);
		mappedPlaceTypeIcons.put("casino", R.drawable.res86);
		mappedPlaceTypeIcons.put("clothing store", R.drawable.res703);
		mappedPlaceTypeIcons.put("convenience store", R.drawable.res593);
		mappedPlaceTypeIcons.put("courthouse", R.drawable.res534);
		mappedPlaceTypeIcons.put("doctor", R.drawable.res508); //518
		mappedPlaceTypeIcons.put("department store", R.drawable.res93);
		mappedPlaceTypeIcons.put("dentist", R.drawable.res501);
		mappedPlaceTypeIcons.put("hospital", R.drawable.res610);
		mappedPlaceTypeIcons.put("food", R.drawable.res672); //641+
		mappedPlaceTypeIcons.put("restaurant", R.drawable.res672); //641+
		mappedPlaceTypeIcons.put("meal takeaway", R.drawable.res672); //641+
		mappedPlaceTypeIcons.put("gas station", R.drawable.res85);
		mappedPlaceTypeIcons.put("liquor store", R.drawable.res653);
		mappedPlaceTypeIcons.put("police", R.drawable.res30);
		mappedPlaceTypeIcons.put("shopping mall", R.drawable.res593);
		mappedPlaceTypeIcons.put("store", R.drawable.res593);
		mappedPlaceTypeIcons.put("convenience store", R.drawable.res593);
		mappedPlaceTypeIcons.put("grocery", R.drawable.res593);
		mappedPlaceTypeIcons.put("train station", R.drawable.res683);
	}

    private static HashMap<String, BitmapDescriptor> cachedIcons = new HashMap<>();
	private static HashMap<String, Drawable> cachedImages = new HashMap<>();

	private final String TAG = StreetsCommon.getTag(Place.class);

    public String name;
    public String reference;
    public double latitude;
    public double longitude;
    public String type;
    public Date lastUpdateTime;
    public BitmapDescriptor icon;
    public String icon_url;
    public Drawable image;
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

        if (cachedIcons.get(type) == null) {

	        Bitmap scaledBitmap;

            try {
	            if (mappedPlaceTypeIcons.get(type) != null) {
		            Bitmap rawBitmap = BitmapFactory.decodeResource(AppContext.getApplicationContext().getResources(), mappedPlaceTypeIcons.get(type));
		            scaledBitmap = createScaledBitmap(rawBitmap, rawBitmap.getWidth() / 4, rawBitmap.getHeight() / 4, false);
		            this.image = new BitmapDrawable(AppContext.getApplicationContext().getResources(), rawBitmap);
		            this.icon = BitmapDescriptorFactory.fromBitmap(scaledBitmap);

	                /* cache bitmap in memory */
		            cachedImages.put(type, this.image);
		            cachedIcons.put(type, this.icon);
	            } else {
		            String fileName = icon_url.substring(icon_url.lastIndexOf("/") + 1);
		            String filePath = getCacheDir(StreetsCommon.FILE_DATA_TYPE.ICON);
		            if (!filePath.endsWith(File.separator)) {
			            filePath += File.separator;
		            }
		            filePath += fileName;
		            File iconFile;

		            if (!(iconFile = new File(filePath)).exists()) {
			            Log.i(TAG, "Loading icon for " + name + " from " + icon_url);
			            Bitmap rawBitmap = ImageLoader.getInstance().loadImageSync(icon_url);
			            scaledBitmap = createScaledBitmap(ImageLoader.getInstance().loadImageSync(icon_url), rawBitmap.getWidth() / 3, rawBitmap.getHeight() / 3, false);
			            this.image = new BitmapDrawable(AppContext.getApplicationContext().getResources(), rawBitmap);
			            this.icon = BitmapDescriptorFactory.fromBitmap(scaledBitmap);


	                    /* cache bitmap in memory */
			            cachedImages.put(type, this.image);
			            cachedIcons.put(type, this.icon);

	                    /* save downloaded bitmap to file cache */
			            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(iconFile));
			            scaledBitmap.compress(Bitmap.CompressFormat.PNG, 100, bufferedOutputStream);
			            bufferedOutputStream.flush();
			            bufferedOutputStream.close();
		            } else {
			            Log.i(TAG, "Loading icon for " + name + " from " + filePath);

	                    /* load bitmap from file cache */
			            scaledBitmap = BitmapFactory.decodeStream(new BufferedInputStream(new FileInputStream(iconFile)));
			            //TODO set image to correct size
			            this.image = new BitmapDrawable(AppContext.getApplicationContext().getResources(), scaledBitmap);
			            this.icon = BitmapDescriptorFactory.fromBitmap(scaledBitmap);
		            }
	            }
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (this.icon == null) {
                this.icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE);
            }

            /* cache bitmap in memory */
	        cachedImages.put(type, this.image);
            cachedIcons.put(type, this.icon);

        } else {
            Log.i(TAG, "Getting cached bitmap for " + name + " for icon url " + type);
            this.icon = cachedIcons.get(type);
            this.image = cachedImages.get(type);
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