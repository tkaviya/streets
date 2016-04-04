package net.blaklizt.streets.android.activity.helpers;

import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import net.blaklizt.streets.android.activity.AppContext;
import net.blaklizt.streets.android.activity.MenuLayout;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static net.blaklizt.streets.android.activity.AppContext.SUBURB_REFRESH_MINS;
import static net.blaklizt.streets.android.common.enumeration.TASK_TYPE.BG_VIEW_LOCATION_TASK;
import static net.blaklizt.symbiosis.sym_core_lib.utilities.CommonUtilities.minutesBetween;

/******************************************************************************
 * *
 * Created:     29 / 10 / 2015                                             *
 * Platform:    Red Hat Linux 9                                            *
 * Author:      Tich de Blak (Tsungai Kaviya)                              *
 * Copyright:   Blaklizt Entertainment                                     *
 * Website:     http://www.blaklizt.net                                    *
 * Contact:     blaklizt@gmail.com                                         *
 * *
 * This program is free software; you can redistribute it and/or modify    *
 * it under the terms of the GNU General Public License as published by    *
 * the Free Software Foundation; either version 2 of the License, or       *
 * (at your option) any later version.                                     *
 * *
 * This program is distributed in the hope that it will be useful,         *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of          *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.    See the         *
 * GNU General Public License for more details.                            *
 * *
 ******************************************************************************/
public class CurrentViewLocationTask extends StreetsAbstractTask {

	private static String currentCity, currentSuburb;
	private static Date lastUpdateDate = null;

	public CurrentViewLocationTask() {
        processDependencies = new ArrayList<>();
        viewDependencies = new ArrayList<>();
        allowOnlyOnce = false;
        allowMultiInstance = false;
        taskType = BG_VIEW_LOCATION_TASK;
    }

    @Override
    protected Object doInBackground(Object...params) {

	    if (lastUpdateDate != null && minutesBetween(new Date(), lastUpdateDate) < SUBURB_REFRESH_MINS) {
		    return null;
	    }

	    Geocoder geocoder = new Geocoder(AppContext.getApplicationContext(), Locale.getDefault());
	    List<Address> addresses;
	    try {
            if (AppContext.getAppContextInstance().getCurrentLocation() == null) {
                Location currentViewLocation = AppContext.getAppContextInstance().getCurrentLocation();
                addresses = geocoder.getFromLocation(currentViewLocation.getLatitude(), currentViewLocation.getLongitude(), 1);
                lastUpdateDate = new Date();
                currentCity = addresses.get(0).getLocality();
                currentSuburb = addresses.get(0).getSubLocality() != null ? addresses.get(0).getSubLocality() : addresses.get(0).getSubAdminArea();
                MenuLayout.getInstance().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (currentCity != null) {
                            MenuLayout.getInstance().currentCityTextView.setText(currentCity);
                        }
                        if (currentSuburb != null) {
                            MenuLayout.getInstance().currentSuburbTextView.setText(currentSuburb);
                        }
                    }
                });
            }
	    }
	    catch (IOException e) {
		    e.printStackTrace();
		    MenuLayout.getInstance().runOnUiThread(new Runnable() {
			    @Override
			    public void run() {
				    if (currentCity != null)    {   MenuLayout.getInstance().currentCityTextView.setText("");   }
				    if (currentSuburb != null)  {   MenuLayout.getInstance().currentSuburbTextView.setText(""); }
			    }
		    });
	    }
        return null;
    }
}
