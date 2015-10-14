/*
 * Copyright (C) 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.blaklizt.streets.android.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ExpandableListView;

import net.blaklizt.streets.android.R;
import net.blaklizt.streets.android.adapter.PlacesListAdapter;
import net.blaklizt.streets.android.common.BackgroundRunner;
import net.blaklizt.streets.android.common.Group;
import net.blaklizt.streets.android.common.StreetsCommon;
import net.blaklizt.streets.android.location.places.PlaceTypes;
import net.blaklizt.streets.android.model.NavDrawerItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * TODO
 */
public class Streets extends AppCompatActivity implements BackgroundRunner, DialogInterface.OnClickListener {
    protected static final String TAG = StreetsCommon.getTag(Streets.class);
	protected static Streets streets = null;
	private static StreetsCommon streetsCommon;
    private DrawerLayout mDrawerLayout;
    protected ViewPager pager;
    protected ExpandableListView placesList;

    PlacesListAdapter placesAdapter;
    protected class BackHandler implements DialogInterface.OnMultiChoiceClickListener {
        @Override
        public void onClick(DialogInterface dialogInterface, int index, boolean isChecked) {
            boolean exit = (index == DialogInterface.BUTTON_POSITIVE);
            //only persist prefs on positive response
            if (exit) {
                Streets.getStreetsCommon().setUserPreference("ask_on_exit", !isChecked ? "1" : "0");
            }
        }

    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.streets_layout);
        streets = this;
        Log.i(TAG, "+++ ON CREATE +++");

        streetsCommon = StreetsCommon.getInstance(getApplicationContext(), 0);

		placesList = (ExpandableListView)findViewById(R.id.places_list);

		Log.i(TAG, "Displayed Main Layout");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_menu);
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setHomeButtonEnabled(true);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        if (navigationView != null) {
            setupDrawerContent(navigationView);
        }

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        if (viewPager != null) {
            setupViewPager(viewPager);
        }
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
    }

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
	}

    @Override
    public void onClick(DialogInterface dialogInterface, int index) {
        boolean exit = (index == DialogInterface.BUTTON_POSITIVE);
        if (exit) { finish(); }
    }

    public static Streets getInstance() { return streets; }

	public static StreetsCommon getStreetsCommon() { return streetsCommon; }

    @Override
    public void onBackPressed() {
        CharSequence[] items = new CharSequence[]{"Never ask again"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        BackHandler backHandler = new BackHandler();
        builder.setMessage("Exit application?")
            .setMultiChoiceItems(items, new boolean[]{true}, backHandler)
            .setPositiveButton("Yes", this)
            .setNegativeButton("No", this).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.option_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
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

    private void setupViewPager(ViewPager viewPager) {
        Adapter adapter = new Adapter(getSupportFragmentManager());
        adapter.addFragment(MapLayout.getInstance() == null ? new MapLayout() : null, "Streetz");
        adapter.addFragment(ProfileLayout.getInstance() == null ? new ProfileLayout() : null, "Info");
        adapter.addFragment(NavigationLayout.getInstance() == null ? new NavigationLayout() : null, "Nav");
        viewPager.setAdapter(adapter);
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                menuItem.setChecked(true);
                mDrawerLayout.closeDrawers();
                return true;
            }
        });
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

		placesList.expandGroup(0);
	}

	private void initializeSideMenuItems()
	{
		try
		{
			Log.i(TAG, "Initializing side menu data");

//			recyclerView = (RecyclerView) findViewById(R.id.drawerList);

            // set a custom shadow that overlays the main content when the drawer opens
//            mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
            // set up the drawer's list view with items and click listener

	        String[] allPlaces = PlaceTypes.getAllPlaces();

			List<NavDrawerItem> data = new ArrayList<>();

//			setPlaces(allPlaces);

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

    static class Adapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragments = new ArrayList<>();
        private final List<String> mFragmentTitles = new ArrayList<>();

        public Adapter(FragmentManager fm) {
            super(fm);
        }

        public void addFragment(Fragment fragment, String title) {
            mFragments.add(fragment);
            mFragmentTitles.add(title);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitles.get(position);
        }
    }
}
