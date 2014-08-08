package net.blaklizt.streets.android;

import android.content.res.Configuration;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import net.blaklizt.streets.android.location.StreetsLocation;
import net.blaklizt.streets.android.location.places.PlaceTypes;
import net.blaklizt.streets.android.persistence.Neighbourhood;

/**
 * User: tkaviya
 * Date: 6/29/14
 * Time: 7:00 PM
 */
<TabHost xmlns:android="http://schemas.android.com/apk/res/android"
        android:id="@android:id/tabhost"
        android:layout_width="fill_parent"
        android:layout_height="fill_parent"
        android:background="@color/background_activity">

        <LinearLayout
        android:orientation="vertical"
        android:layout_width="fill_parent"
        android:layout_height="fill_parent"
        android:padding="0dp">

        <FrameLayout
        android:id="@android:id/tabcontent"
        android:layout_width="fill_parent"
        android:layout_height="wrap_content"
        android:layout_weight="1"/>

        <TabWidget
        android:id="@android:id/tabs"
        style="@style/DropDownListView.Tabbar"
        android:layout_width="fill_parent"
        android:layout_height="wrap_content"
        android:layout_marginBottom="5dp"
        android:layout_weight="0"/>

        </LinearLayout>
        </TabHost>
 
 
 
 
 
 
 
 
 
public class StreetsLayout extends SherlockFragmentActivity {

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id) {

        }
    }

    private ActionBarDrawerToggle mDrawerToggle;

    public static final String TAG = "StreetsLayout";

    public static String ACTIVE_TAB = "MapLayout";

    protected DrawerLayout mDrawerLayout;
    protected ListView mDrawerList;

    protected TextView status_text_view;
    protected SQLiteDatabase neighbourhoodDB = null;
    protected static StreetsLayout streetsLayout = null;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "+++ ON CREATE +++");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.streets_layout);

        try {

            streetsLayout = this;
            neighbourhoodDB = new Neighbourhood(getApplicationContext()).getWritableDatabase();

            status_text_view = (TextView) findViewById(R.id.status_text_view);
//            TabHost tabHost = getTabHost();

            mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
            mDrawerList = (ListView) findViewById(R.id.left_drawer);

            // Set the adapter for the list view
            mDrawerList.setAdapter(
                    new ArrayAdapter<>(this, R.layout.drawer_list_item, PlaceTypes.getAllPlaces()));
            // Set the list's click listener
            mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

            mDrawerToggle = new ActionBarDrawerToggle(
                    this,                  /* host Activity */
                    mDrawerLayout,         /* DrawerLayout object */
                    R.drawable.ic_drawer,  /* nav drawer icon to replace 'Up' caret */
                    R.string.drawer_open,  /* "open drawer" description */
                    R.string.drawer_close  /* "close drawer" description */
            ) {

                /** Called when a drawer has settled in a completely closed state. */
                public void onDrawerClosed(View view) {
                    super.onDrawerClosed(view);
    //                getActionBar().setTitle(mTitle);
                }

                /** Called when a drawer has settled in a completely open state. */
                public void onDrawerOpened(View drawerView) {
                    super.onDrawerOpened(drawerView);
    //                getActionBar().setTitle(mDrawerTitle);
                }
            };

            // Set the drawer toggle as the DrawerListener
            mDrawerLayout.setDrawerListener(mDrawerToggle);

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);

//            // Tab for map
//            TabHost.TabSpec mapSpec = tabHost.newTabSpec("MAP");
//            mapSpec.setIndicator("MAP", Drawable.createFromPath("drawable-hdpi/ic_menu_mapmode.png"));
//            Intent mapIntent = new Intent(this, MapLayout.class);
//            mapSpec.setContent(mapIntent);
//
//            // Tab for info
//            TabHost.TabSpec infoSpec = tabHost.newTabSpec("INFO");
//            infoSpec.setIndicator("INFO", Drawable.createFromPath("drawable-hdpi/ic_dialog_info.png"));
//            Intent infoIntent = new Intent(this, StreetsLocation.class);
//            infoSpec.setContent(infoIntent);
//
//            // Adding all TabSpec to TabHost
//            tabHost.addTab(mapSpec); // Adding map tab
//            tabHost.addTab(infoSpec); // Adding map tab

            final ActionBar actionBar = getSupportActionBar();
            actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

            ActionBar.Tab map_tab = actionBar.newTab()
                    .setText(R.string.map_tab)
                    .setTabListener(new TabListener<MapLayout>(
                            this, "map_tab", MapLayout.class) {
                    });
            actionBar.addTab(map_tab);

            // add the second tab which is an instance of TabFragment2
            ActionBar.Tab info_tab = actionBar.newTab()
                    .setText(R.string.info_tab)
                    .setTabListener(new TabListener<StreetsLocation>(
                            this, "info_tab", StreetsLocation.class));
            actionBar.addTab(info_tab);

            // check if there is a saved state to select active tab
            if( savedInstanceState != null ){
                getSupportActionBar().setSelectedNavigationItem(
                        savedInstanceState.getInt(ACTIVE_TAB));
            }

            Log.i(TAG, "Displayed Main Layout");
        }
        catch (Exception ex) {
            Log.e(TAG, "Failed to instantiate the streets main layout", ex);
        }
    }

    public SQLiteDatabase getNeighbourhoodDB() { return neighbourhoodDB; }

    public static StreetsLayout getInstance() { return streetsLayout; }

    @Override
    public void onStart() {
        Log.i(TAG, "+++ ON START +++");
        status_text_view.setText("I'm the streets, look both way before you cross me!");
        super.onStart();
    }


    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        Log.i(TAG, "+++ ON POST CREATE +++");
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }
}