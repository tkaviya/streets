package net.blaklizt.streets.android.activity;

/**
 * Created with IntelliJ IDEA.
 * User: photon
 * Date: 2015/06/10
 * Time: 10:53 PM
 */

import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import net.blaklizt.streets.android.R;
import net.blaklizt.streets.android.adapter.PlacesListAdapter;
import net.blaklizt.streets.android.adapter.ViewPagerAdapter;
import net.blaklizt.streets.android.common.Group;
import net.blaklizt.streets.android.common.StreetsCommon;
import net.blaklizt.streets.android.layout.SlidingTabLayout;
import net.blaklizt.streets.android.location.places.PlaceTypes;
import net.blaklizt.streets.android.model.NavDrawerItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Streets extends ActionBarActivity implements FragmentDrawer.FragmentDrawerListener
{
	//streets
	protected static Streets streets = null;

	//common streets common context
	private static StreetsCommon streetsCommon;

	//get tag
	private static final String TAG = StreetsCommon.getTag(Streets.class);

	//Tab View
	protected Toolbar toolbar;
	protected ViewPager pager;
	protected ViewPagerAdapter adapter;
	protected SlidingTabLayout tabs;
	protected static final CharSequence TAB_TITLES [] = { "MAP", "NAV" };
	protected static final int TAB_COUNT = 2;

	//Sidebar
	protected ExpandableListView placesList;
	protected PlacesListAdapter placesAdapter;
	protected DrawerLayout mDrawerLayout; // Within which the entire activity is enclosed
//	private RecyclerView recyclerView;
	protected ActionBarDrawerToggle mDrawerToggle; // Navigation Drawer in the action bar

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		streets = this;

		Log.i(TAG, "+++ ON CREATE +++");

		streetsCommon = StreetsCommon.getInstance(getApplicationContext(), 0);

		setContentView(R.layout.activity_main);

		placesList = (ExpandableListView)findViewById(R.id.places_list);

		initializeTabs();

		initializeSideMenu();

		initializeSideMenuItems();

		Log.i(TAG, "Displayed Main Layout");

	}

	@Override
	public void onStart() {
		Log.i(TAG, "+++ ON START +++");
		super.onStart();
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		mDrawerToggle.syncState();
	}

	@Override
	public void onDestroy()
	{
//		super.onDestroy();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.option_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		// if click is from side menu, it will be handled elsewhere
		if (mDrawerToggle.onOptionsItemSelected(item)) { return true; }

		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.

		int id = item.getItemId();

		switch (id)
		{
            case R.id.action_search:
                return true;
            case R.id.action_settings:
                return true;
			case R.id.action_get_location:
				pager.setCurrentItem(0);
				MapLayout.getInstance().refreshLocation();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	/** Called whenever we call invalidateOptionsMenu() */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		return super.onPrepareOptionsMenu(menu);
	}

	public static Streets getInstance() { return streets; }

	public static StreetsCommon getStreetsCommon() { return streetsCommon; }

	private void initializeTabs()
	{
		try
		{
			Log.i(TAG, "Initializing tabs");
			// Creating The Toolbar and setting it as the Toolbar for the activity

			toolbar = (Toolbar) findViewById(R.id.tool_bar);
			setSupportActionBar(toolbar);


			// Creating The ViewPagerAdapter and Passing Fragment Manager, TAB_TITLES fot the Tabs and Number Of Tabs.
			adapter =  new ViewPagerAdapter(getSupportFragmentManager(), TAB_TITLES, TAB_COUNT);

			// Assigning ViewPager View and setting the adapter
			pager = (ViewPager) findViewById(R.id.pager);
			pager.setAdapter(adapter);

			// Assiging the Sliding Tab Layout View
			tabs = (SlidingTabLayout) findViewById(R.id.tabs);
			tabs.setDistributeEvenly(true); // To make the Tabs Fixed set this true, This makes the tabs Space Evenly in Available width

			// Setting Custom Color for the Scroll bar indicator of the Tab View
			tabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
				@Override
				public int getIndicatorColor(int position) {
					return getResources().getColor(R.color.tabsScrollColor);
				}
			});

			// Setting the ViewPager For the SlidingTabsLayout
			tabs.setViewPager(pager);
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			Log.e(TAG, "Failed to initialize app tabs", ex);
		}
	}

	private void initializeSideMenu()
    {
        try
        {
            Log.i(TAG, "Initializing side menu");
            // Getting reference to the DrawerLayout
            mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

//			getSupportActionBar().setDisplayUseLogoEnabled(true);
			getSupportActionBar().setHomeButtonEnabled(true);
			getSupportActionBar().setDisplayHomeAsUpEnabled(true);

			mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close)
			{
                /** Called when drawer is closed */
				@Override
                public void onDrawerClosed(View drawerView) {
					super.onDrawerClosed(drawerView);
					getSupportActionBar().setTitle(R.string.app_name);
                    invalidateOptionsMenu();

                }

                /** Called when a drawer is opened */
				@Override
                public void onDrawerOpened(View drawerView) {
					super.onDrawerOpened(drawerView);
					getSupportActionBar().setTitle("Change Settings");
                    invalidateOptionsMenu();
                }

				@Override
				public void onDrawerSlide(View drawerView, float slideOffset) {
					Log.i(TAG, "+++ ON DRAWER SLIDE +++");
					super.onDrawerSlide(drawerView, slideOffset);
					toolbar.setAlpha(1 - slideOffset / 2);
				}

            };

            // Setting DrawerToggle on DrawerLayout
            mDrawerLayout.setDrawerListener(mDrawerToggle);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            Log.e(TAG, "Failed to initialize the side menu", ex);
        }
    }

	private void initializeSideMenuItems()
	{
		try
		{
			Log.i(TAG, "Initializing side menu data");

//			recyclerView = (RecyclerView) findViewById(R.id.drawerList);

            // set a custom shadow that overlays the main content when the drawer opens
            mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
            // set up the drawer's list view with items and click listener

	        String[] allPlaces = PlaceTypes.getAllPlaces();

			List<NavDrawerItem> data = new ArrayList<>();

			setPlaces(allPlaces);

			// preparing navigation drawer items
			for (String title : allPlaces) {
				NavDrawerItem navItem = new NavDrawerItem();
				navItem.setTitle(title);
				data.add(navItem);
			}

            // Creating an ArrayAdapter to add items to the listview mDrawerList
            Log.i(TAG, "Getting menu item list");
//			placeListAdapater = new NavigationDrawerAdapter(this, data);
//			recyclerView.setAdapter(placeListAdapater);
//			recyclerView.setLayoutManager(new LinearLayoutManager(this));

//			Log.i(TAG, "Getting user defined places of interest");
//			LinkedList placesOfInterest = Streets.getStreetsCommon().getStreetsDBHelper().getPlacesOfInterest();

//	        Log.i(TAG, "User has " + placesOfInterest.size() + " places of interest");
//	        Log.i(TAG, "mDrawerList has " + recyclerView.getCount() + " items, " + recyclerView.getChildCount() + " children.");
//
//	        for (int c = 0; c < mDrawerList.getCount(); c++)
//	        {
//		        if (placesOfInterest.contains(recyclerView.getItemAtPosition(c)))
//		        {
//			        Log.d(TAG, "Selecting " + mDrawerList.getItemAtPosition(c));
//			        mDrawerList.setItemChecked(c, true);
//		        }
//	        }

			//Setting item click listener for the listview mDrawerList
//			recyclerView.setOnClickListener(this);
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			Log.e(TAG, "Failed to initialize the side menu", ex);
		}
	}

	@Override
	public void onDrawerItemSelected(View view, int position) {

	}

	public void setPlaces(String[] places)
	{
		Log.d(TAG, "Setting directions");

		SparseArray<Group> directionsList = new SparseArray<>();

		String header = "Show Places";

		directionsList.put(0, new Group(header));

		Collections.addAll(directionsList.get(0).children, places);

		placesAdapter = new PlacesListAdapter(this, directionsList);

		placesList.setAdapter(placesAdapter);

//		placesList.expandGroup(0);
	}

}
