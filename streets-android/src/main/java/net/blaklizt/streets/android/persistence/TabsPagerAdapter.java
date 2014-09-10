package net.blaklizt.streets.android.persistence;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import net.blaklizt.streets.android.MapLayout;
import net.blaklizt.streets.android.location.StreetsLocation;

public class TabsPagerAdapter extends FragmentPagerAdapter {

	public TabsPagerAdapter(FragmentManager fm) {
		super(fm);
	}

	@Override
	public Fragment getItem(int index) {

		switch (index) {
			case 0:
				// Top Rated fragment activity
				return new MapLayout();
			case 1:
				// Games fragment activity
				return new StreetsLocation();
		}

		return null;
	}

	@Override
	public int getCount() {
		// get item count - equal to number of tabs
		return 3;
	}

}