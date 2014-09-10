//package net.blaklizt.streets.android;
//
//import android.app.ActionBar;
//import android.content.Intent;
//import android.content.res.Configuration;
//import android.database.Cursor;
//import android.database.sqlite.SQLiteDatabase;
//import android.graphics.drawable.Drawable;
//import android.os.Bundle;
//import android.support.v4.app.ActionBarDrawerToggle;
//import android.support.v4.app.FragmentActivity;
//import android.support.v4.app.FragmentTabHost;
//import android.support.v4.widget.DrawerLayout;
//import android.util.Log;
//import android.view.Menu;
//import android.view.View;
//import android.widget.*;
//import com.parse.Parse;
//import com.parse.ParseException;
//import com.parse.ParseUser;
//import com.parse.SignUpCallback;
//import net.blaklizt.streets.android.NavigationLayout;
//import net.blaklizt.streets.android.location.places.PlaceTypes;
//import net.blaklizt.streets.android.persistence.Neighbourhood;
//
///**
// * User: tkaviya
// * Date: 6/29/14
// * Time: 7:00 PM
// */
//
//public class StreetsLayout extends FragmentActivity {
//
//    private class DrawerItemClickListener implements ListView.OnItemClickListener {
//        @Override
//        public void onItemClick(AdapterView parent, View view, int position, long id) {
//
//        }
//    }
//
//    private ActionBarDrawerToggle mDrawerToggle;
//
//    public static final String TAG = "StreetsLayout";
//
//    public static final String PARSE_APP_ID = "gfTj5m1WpiOo9ZBOVjjCqIa1YsB4MJiiU62l13WI";
//
//    public static final String PARSE_API_KEY = "osUp0339QGvFdkmvmVfNewzsxIWA5GFxJRx9GnvO";
//
//    public static String ACTIVE_TAB = "MapLayout";
//
//    protected DrawerLayout mDrawerLayout;
//    protected ListView mDrawerList;
//
//    protected TextView status_text_view;
//    protected SQLiteDatabase neighbourhoodDB = null;
//    protected static StreetsLayout streetsLayout = null;
//
//    /** Called when the activity is first created. */
//
//	@Override
//	public void onCreate(Bundle savedInstanceState)
//	{
//		Log.i(TAG, "+++ ON CREATE +++");
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.streets_layout);
//
//		// Getting reference to the ActionBarDrawerToggle
//		mDrawerToggle = new ActionBarDrawerToggle( this,
//				mDrawerLayout,
//				R.drawable.ic_drawer,
//				R.string.drawer_open,
//				R.string.drawer_close){
//
//		};
//
//		try {
//
//            streetsLayout = this;
//            neighbourhoodDB = new Neighbourhood(getApplicationContext()).getWritableDatabase();
//
//            Parse.initialize(this, PARSE_APP_ID, PARSE_API_KEY);
//
////            status_text_view = (TextView) findViewById(R.id.status_text_view);
//
////            TabHost tabHost = getTabHost();
//			FragmentTabHost tabHost = (FragmentTabHost)findViewById(android.R.id.tabhost);
//
//            mDrawerLayout = (DrawerLayout) findViewById(R.id.left_drawer);
//
//			mDrawerList = (ListView) findViewById(R.id.left_drawer);
//
//            // Set the adapter for the list view
//            mDrawerList.setAdapter(new ArrayAdapter<>(this, R.layout.drawer_list_item, PlaceTypes.getAllPlaces()));
//
//            // Set the list's click listener
//            mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
//
//            mDrawerToggle = new ActionBarDrawerToggle(
//                    this,                  /* host Activity */
//                    mDrawerLayout,         /* DrawerLayout object */
//                    R.drawable.ic_drawer,  /* nav drawer icon to replace 'Up' caret */
//                    R.string.drawer_open,  /* "open drawer" description */
//                    R.string.drawer_close  /* "close drawer" description */
//            ) {
//
//
//				/** Called when drawer is closed */
//				public void onDrawerClosed(View view) {
//					getActionBar().setTitle(R.string.app_name);
//					invalidateOptionsMenu();
//				}
//
//				/** Called when a drawer is opened */
//				public void onDrawerOpened(View drawerView) {
//					getActionBar().setTitle("Select a river");
//					invalidateOptionsMenu();
//				}
//
//            };
//
//            // Set the drawer toggle as the DrawerListener
//            mDrawerLayout.setDrawerListener(mDrawerToggle);
//
//            getActionBar().setDisplayHomeAsUpEnabled(true);
//            getActionBar().setHomeButtonEnabled(true);
//
////            // Tab for map
//            TabHost.TabSpec mapSpec = tabHost.newTabSpec("MAP");
//            mapSpec.setIndicator("MAP", Drawable.createFromPath("drawable-hdpi/ic_menu_mapmode.png"));
//            Intent mapIntent = new Intent(this, MapLayout.class);
//            mapSpec.setContent(mapIntent);
//
//            // Tab for info
//            TabHost.TabSpec infoSpec = tabHost.newTabSpec("INFO");
//            infoSpec.setIndicator("INFO", Drawable.createFromPath("drawable-hdpi/ic_dialog_info.png"));
//            Intent infoIntent = new Intent(this, NavigationLayout.class);
//            infoSpec.setContent(infoIntent);
//
//            // Adding all TabSpec to TabHost
//            tabHost.addTab(mapSpec); // Adding map tab
//            tabHost.addTab(infoSpec); // Adding map tab
//
//            final ActionBar actionBar = getActionBar();
//            actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
//
////            ActionBar.Tab map_tab = actionBar.newTab()
////                    .setText(R.string.map_tab)
////                    .setTabListener(new StreetsTabListener<MapLayout>(
////                            this, "map_tab", MapLayout.class) {
////                    });
////            actionBar.addTab(map_tab);
////
////            // add the second tab which is an instance of TabFragment2
////            ActionBar.Tab info_tab = actionBar.newTab()
////                    .setText(R.string.info_tab)
////                    .setTabListener(new StreetsTabListener<NavigationLayout>(
////                            this, "info_tab", NavigationLayout.class));
////            actionBar.addTab(info_tab);
//
//            // check if there is a saved state to select active tab
//            if( savedInstanceState != null ){
//                getActionBar().setSelectedNavigationItem(
//                        savedInstanceState.getInt(ACTIVE_TAB));
//            }
//
//            Log.i(TAG, "Displayed Main Layout");
//        }
//        catch (Exception ex) {
//            Log.e(TAG, "Failed to instantiate the streets main layout", ex);
//        }
//
//
//
//
//
//
//
//
//
//
//
//
//	}
//
//    public SQLiteDatabase getNeighbourhoodDB() { return neighbourhoodDB; }
//
//    public static StreetsLayout getInstance() { return streetsLayout; }
//
//	private void loginParseUser() {
//
//		try {
//			Log.i(TAG, "Logging in parse user.");
//			if (StreetsLayout.getInstance().getNeighbourhoodDB() != null) {
//				Cursor userData = StreetsLayout.getInstance().getNeighbourhoodDB().rawQuery(
//						"SELECT ut.Username, ut.Password, ut.Email, pt.Latitude, pt.Longitude " +
//								" FROM " + Neighbourhood.USER_TABLE + " ut, " + Neighbourhood.PLACE_TABLE + " pt " +
//								" WHERE ut.LastPlaceID = pt.PlaceID", null);
//
//				userData.moveToFirst();
//				Log.i(TAG, "Logging in " + userData.getString(0));
//
//				ParseUser user = new ParseUser();
//				user.setUsername(userData.getString(0));
//				user.setPassword(userData.getString(1));
//				user.setEmail(userData.getString(2));
//				user.put("latitude", userData.getString(3));
//				user.put("longitude", userData.getString(4));
//
//				user.signUpInBackground(new SignUpCallback() {
//					public void done(ParseException e) {
//						if (e == null) {
//							Log.i(MapLayout.TAG, "User logged into parse");
//						} else {
//							Log.e(TAG, e.getMessage(), e);
//						}
//					}
//				});
//			} else {
//				Log.i(MapLayout.TAG, "NeighbourhoodDB is null");
//			}
//		}
//		catch (Exception ex) {
//			Log.e(MapLayout.TAG, "Failed to login to parse", ex);
//		}
//
//
//	}
//
//    @Override
//    public void onStart() {
//        Log.i(TAG, "+++ ON START +++");
//        status_text_view.setText("I'm the streets, look both way before you cross me!");
//        super.onStart();
//    }
//
//
//    @Override
//    protected void onPostCreate(Bundle savedInstanceState) {
//        Log.i(TAG, "+++ ON POST CREATE +++");
//        super.onPostCreate(savedInstanceState);
//        // Sync the toggle state after onRestoreInstanceState has occurred.
//        mDrawerToggle.syncState();
//
//        //loginParseUser();
//    }
//
//	/** Called whenever we call invalidateOptionsMenu() */
//	@Override
//	public boolean onPrepareOptionsMenu(Menu menu) {
//		// If the drawer is open, hide action items related to the content view
//		boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
//
////		menu.findItem(R.id.action_settings).setVisible(!drawerOpen);
//		return super.onPrepareOptionsMenu(menu);
//	}
//
//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//		// Inflate the menu; this adds items to the action bar if it is present.
//		getMenuInflater().inflate(R.menu.option_menu, menu);
//		return true;
//	}
//
//    @Override
//    public void onConfigurationChanged(Configuration newConfig) {
//        super.onConfigurationChanged(newConfig);
//        mDrawerToggle.onConfigurationChanged(newConfig);
//    }
//}