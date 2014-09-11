package net.blaklizt.streets.android;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.*;
import net.blaklizt.streets.android.location.places.PlaceTypes;
import net.blaklizt.streets.android.persistence.StreetsDBHelper;

/**
 * Created with IntelliJ IDEA.
 * User: tkaviya
 * Date: 8/31/14
 * Time: 1:40 AM
 */
public class Streets extends FragmentActivity implements ActionBar.TabListener, ViewPager.OnPageChangeListener, AdapterView.OnItemClickListener {

    public static final String TAG = "Streets";

	// Within which the entire activity is enclosed
	DrawerLayout mDrawerLayout;

	// ListView represents Navigation Drawer
	ListView mDrawerList;

	// ActionBarDrawerToggle indicates the presence of Navigation Drawer in the action bar
	ActionBarDrawerToggle mDrawerToggle;

    protected TextView status_text_view;
	protected StreetsDBHelper streetsDBHelper = null;
    protected static Streets streets = null;

    //tab data
	private ViewPager viewPager;
	private TabsPagerAdapter mAdapter;
	private ActionBar actionBar;
    private ActionBar.Tab mapTab;
    private ActionBar.Tab navigationTab;
	ArrayAdapter<String> placeListAdapater;

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

	public StreetsDBHelper getStreetsDBHelper() { return streetsDBHelper; }

    public static Streets getInstance() { return streets; }

    private void initializeStreetsData()
    {
	    try
	    {
		    Log.i(TAG, "Initializing streets core data");
			streets = this;
		    streetsDBHelper = new StreetsDBHelper(getApplicationContext());
//			status_text_view = (TextView)findViewById(R.id.status_text_view);
	    }
	    catch (Exception ex)
	    {
		    ex.printStackTrace();
		    Log.e(TAG, "Failed to initialize streets core data", ex);
	    }
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
            placeListAdapater = new ArrayAdapter<>(
                    getBaseContext(),
                    R.layout.drawer_list_item, PlaceTypes.getAllPlaces()
            );

            mDrawerList.setAdapter(placeListAdapater);

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
            mDrawerList.setOnItemClickListener(this);
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
            actionBar = getActionBar();

            mAdapter = new TabsPagerAdapter(getSupportFragmentManager());

            Log.i(TAG, "Adding tabs to action bar");

            mapTab          = createActionBarTab(R.string.map_tab_name,          R.drawable.applications_internet);
	        navigationTab   = createActionBarTab(R.string.navigation_tab_name,   R.drawable.compass);

            actionBar.addTab(mapTab);
            actionBar.addTab(navigationTab);

            viewPager = (ViewPager) findViewById(R.id.pager);
            viewPager.setAdapter(mAdapter);
            viewPager.setOnPageChangeListener(this);

            actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
            actionBar.selectTab(mapTab);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            Log.e(TAG, "Failed to initialize app tabs", ex);
        }
    }

	private void initializeSearch()
	{
		final AutoCompleteTextView keyword = (AutoCompleteTextView) findViewById(R.id.txt_search);
		keyword.setOnTouchListener(new RightDrawableOnTouchListener(keyword)
		{
			@Override
			public boolean onDrawableTouch(final MotionEvent event) { return onClickSearch(event, keyword); }
		});
	}

	private boolean onClickSearch(final MotionEvent event, final View view) {
		// do something
		event.setAction(MotionEvent.ACTION_CANCEL);
		return false;
	}

    private ActionBar.Tab createActionBarTab(int tabName, int iconResource)
    {
	    Log.i(TAG, "Creating tab " + getResources().getString(tabName));

        ActionBar.Tab newTab = actionBar.newTab();
        View tabViewItem = getLayoutInflater().inflate(R.layout.tab_layout, null);
	    tabViewItem.setMinimumHeight(140);

	    newTab.setCustomView(tabViewItem);
	    newTab.setTabListener(this);
	    newTab.setText(tabName);

        TextView tabText = (TextView) tabViewItem.findViewById(R.id.tabText);
        tabText.setText(tabName);

        ImageView tabImage = (ImageView) tabViewItem.findViewById(R.id.tabImage);
        tabImage.setImageResource(iconResource);

//	    Drawable drawable = getResources().getDrawable(iconResource);
//	    drawable.setBounds(0,1,0,0);
//
//	    newTab.setIcon(drawable);
//	    newTab.setText(tabName);

        return newTab;
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
		if (mDrawerToggle.onOptionsItemSelected(item)) { return true; }
        switch (item.getItemId())
        {
            case R.id.action_search:
                return true;
            case R.id.action_settings:
                return true;
            case R.id.action_get_location:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
	}


	/** Called whenever we call invalidateOptionsMenu() */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// If the drawer is open, hide action items related to the content view
		boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);

		menu.findItem(R.id.action_get_location).setVisible(!drawerOpen);
        menu.findItem(R.id.action_search).setVisible(!drawerOpen);
        menu.findItem(R.id.action_settings).setVisible(!drawerOpen);
		return super.onPrepareOptionsMenu(menu);
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.option_menu, menu);
        return super.onCreateOptionsMenu(menu);
	}

	@Override
	public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        Log.i(TAG, "Switching to tab " + tab.getText());
        viewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        Log.i(TAG, "Leaving tab " + tab.getText());
	}

	@Override
	public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        Log.i(TAG, "Reselecting tab " + tab.getText());
	}

    @Override
    public void onPageScrolled(int i, float v, int i2) {}

    @Override
    public void onPageSelected(int position) { actionBar.setSelectedNavigationItem(position); }

    @Override
    public void onPageScrollStateChanged(int i) {}

	@Override
	public void onItemClick(AdapterView<?> adapterView, View view, int position, long id)
	{
		String item = (String) adapterView.getAdapter().getItem(position);
		CheckedTextView checkedTextView = (CheckedTextView) view;

		//int row_id = (Integer) checkedTextView.getTag();

		ListView listView = (ListView)adapterView;

		listView.setItemChecked(position, !listView.isItemChecked(position));

		if (checkedTextView.isSelected() || checkedTextView.isChecked())
		{
			checkedTextView.setSelected(false);
			checkedTextView.setChecked(false);
			checkedTextView.setBackgroundColor(android.R.color.background_light);
			Toast.makeText(Streets.getInstance(), item + " selected", Toast.LENGTH_SHORT).show();
		}
		else
		{
			checkedTextView.setSelected(true);
			checkedTextView.setChecked(true);
			checkedTextView.setBackgroundColor(android.R.color.black);
			Toast.makeText(Streets.getInstance(), item + " not selected", Toast.LENGTH_SHORT).show();
		}

		placeListAdapater.notifyDataSetChanged();
	}
}
