package net.blaklizt.streets.android.adapter;

/**
 * Created with IntelliJ IDEA.
 * User: photon
 * Date: 2015/06/10
 * Time: 10:53 PM
 */

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import net.blaklizt.streets.android.activity.MapLayout;
import net.blaklizt.streets.android.activity.NavigationLayout;

public class ViewPagerAdapter extends FragmentStatePagerAdapter {

	CharSequence Titles[]; // This will Store the TAB_TITLES of the Tabs which are Going to be passed when ViewPagerAdapter is created
	int NumbOfTabs; // Store the number of tabs, this will also be passed when the ViewPagerAdapter is created


	// Build a Constructor and assign the passed Values to appropriate values in the class
	public ViewPagerAdapter(FragmentManager fm, CharSequence mTitles[], int mNumbOfTabsumb) {
		super(fm);

		this.Titles = mTitles;
		this.NumbOfTabs = mNumbOfTabsumb;

	}

	//This method return the fragment for the every position in the View Pager
	@Override
	public Fragment getItem(int position) {

		if (position == 0) // if the position is 0 we are returning the First tab
		{
			MapLayout mapLayout = new MapLayout();
			return mapLayout;
		}
		else // As we are having 2 tabs if the position is now 0 it must be 1 so we are returning second tab
		{
			NavigationLayout navigationLayout = new NavigationLayout();
			return navigationLayout;
		}


	}

	// This method return the titles for the Tabs in the Tab Strip

	@Override
	public CharSequence getPageTitle(int position) {
		return Titles[position];
	}

	// This method return the Number of tabs for the tabs Strip

	@Override
	public int getCount() {
		return NumbOfTabs;
	}
}