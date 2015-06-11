//package net.blaklizt.streets.android;
//
//import android.support.v4.app.Fragment;
//import android.support.v4.app.FragmentManager;
//import android.support.v4.app.FragmentPagerAdapter;
//import android.util.Log;
//
//public class TabsPagerAdapter extends FragmentPagerAdapter {
//
//	public TabsPagerAdapter(FragmentManager fm) {
//		super(fm);
//	}
//
//	@Override
//	public Fragment getItem(int index) {
//
//        Log.i(Streets.TAG, "Selecting tab fragment at index: " + index);
//
//        switch (index)
//        {
//			case 0:
//				// Top Rated fragment activity
//                Log.i(Streets.TAG, "Returning MapLayout");
//				return new MapLayout();
//			case 1:
//				// Games fragment activity
//                Log.i(Streets.TAG, "Returning NavigationLayout");
//				return new NavigationLayout();
//		}
//
//		return null;
//	}
//
//	@Override
//	public int getCount() {
//		// get item count - equal to number of tabs
//		return Streets.getMainActionBar().getTabCount();
//	}
//
//}