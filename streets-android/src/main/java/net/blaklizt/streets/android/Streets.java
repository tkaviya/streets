package net.blaklizt.streets.android;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import net.blaklizt.streets.android.location.places.PlaceTypes;
import net.blaklizt.streets.android.persistence.Neighbourhood;

/**
 * Created with IntelliJ IDEA.
 * User: tkaviya
 * Date: 8/31/14
 * Time: 1:40 AM
 */
public class Streets extends FragmentActivity implements ActionBar.TabListener {

    public static final String TAG = "Streets";

	// Within which the entire activity is enclosed
	DrawerLayout mDrawerLayout;

	// ListView represents Navigation Drawer
	ListView mDrawerList;

	// ActionBarDrawerToggle indicates the presence of Navigation Drawer in the action bar
	ActionBarDrawerToggle mDrawerToggle;

//    protected TextView status_text_view;
    protected SQLiteDatabase neighbourhoodDB = null;
    protected static Streets streets = null;

    //tab data
	private ViewPager viewPager;
	private TabsPagerAdapter mAdapter;
	private ActionBar actionBar;
    private ActionBar.Tab mapTab;
    private ActionBar.Tab navigationTab;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "+++ ON CREATE +++");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_streets);

        initializeStreetsData();

        initializeSideMenu();

        initializeTabs();

        Log.i(TAG, "Displayed Main Layout");
	}

    @Override
    public void onStart() {
        Log.i(TAG, "+++ ON START +++");
        super.onStart();
    }

    public SQLiteDatabase getNeighbourhoodDB() { return neighbourhoodDB; }

    public static Streets getInstance() { return streets; }

    private void initializeStreetsData()
    {
        streets = this;
        neighbourhoodDB = new Neighbourhood(getApplicationContext()).getWritableDatabase();
    }

    private void initializeSideMenu()
    {
        try
        {
            Log.i(TAG, "Initializing side menu");
            // Getting reference to the DrawerLayout
            mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

            mDrawerList = (ListView) findViewById(R.id.drawer_list);

            // set a custom shadow that overlays the main content when the drawer opens
            mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
            // set up the drawer's list view with items and click listener

            // Creating an ArrayAdapter to add items to the listview mDrawerList
            Log.i(TAG, "Getting all places");
            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                    getBaseContext(),
                    R.layout.drawer_list_item  , PlaceTypes.getAllPlaces()
            );

            mDrawerList.setAdapter(adapter);

            // enable ActionBar app icon to behave as action to toggle nav drawer
            getActionBar().setDisplayHomeAsUpEnabled(true);
            getActionBar().setHomeButtonEnabled(true);

            // Getting reference to the ActionBarDrawerToggle
            mDrawerToggle = new ActionBarDrawerToggle(	this,
                    mDrawerLayout,
                    R.drawable.ic_drawer,
                    R.string.drawer_open,
                    R.string.drawer_close){

                /** Called when drawer is closed */
                public void onDrawerClosed(View view) {
                    getActionBar().setTitle(R.string.app_name);
                    invalidateOptionsMenu();

                }

                /** Called when a drawer is opened */
                public void onDrawerOpened(View drawerView) {
                    getActionBar().setTitle("What are you interested in?");
                    invalidateOptionsMenu();
                }

            };

            // Setting DrawerToggle on DrawerLayout
            mDrawerLayout.setDrawerListener(mDrawerToggle);

            // Setting item click listener for the listview mDrawerList
            mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {


                @Override
                public void onItemClick(AdapterView<?> parent,
                                        View view,
                                        int position,
                                        long id) {

                    // Getting an array of rivers
                    String[] places = PlaceTypes.getAllPlaces();

                    String enablePlace = places[position];

                    // Closing the drawer
                    //mDrawerLayout.closeDrawer(mDrawerList);

                }
            });
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            Log.e(TAG, "Failed to initialize the side menu", ex);
        }
    }

    private void initializeTabs()
    {
        try
        {
            Log.i(TAG, "Initializing tabs");
            viewPager = (ViewPager) findViewById(R.id.pager);
            actionBar = getActionBar();
            mAdapter = new TabsPagerAdapter(getSupportFragmentManager());

            Log.i(TAG, "Adding tabs to action bar");

            // Adding Tabs
            mapTab          = actionBar.newTab();
            navigationTab   = actionBar.newTab();

            mapTab          .setText(R.string.map_tab_name);
            navigationTab   .setText(R.string.navigation_tab_name);

            mapTab          .setTabListener(this);
            navigationTab   .setTabListener(this);

            mapTab          .setIcon(R.drawable.applications_internet);
            navigationTab   .setIcon(R.drawable.navigation);

            actionBar       .addTab(mapTab);
            actionBar       .addTab(navigationTab);


            viewPager.setAdapter(mAdapter);
            actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
            actionBar.selectTab(navigationTab);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            Log.e(TAG, "Failed to initialize app tabs", ex);
        }
    }

    public static ActionBar getMainActionBar() { return getInstance().actionBar; }

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		mDrawerToggle.syncState();
	}

	/** Handling the touch event of app icon */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}


	/** Called whenever we call invalidateOptionsMenu() */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// If the drawer is open, hide action items related to the content view
		boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);

		menu.findItem(R.id.location).setVisible(!drawerOpen);
		return super.onPrepareOptionsMenu(menu);
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.option_menu, menu);
		return true;
	}

	@Override
	public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
		//To change body of implemented methods use File | Settings | File Templates.
        //actionBar.selectTab(tab);
	}

	@Override
	public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
		//To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
		//To change body of implemented methods use File | Settings | File Templates.
	}
}
