package net.blaklizt.streets.android.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import net.blaklizt.streets.android.R;
import net.blaklizt.streets.android.common.StreetsCommon;
import net.blaklizt.streets.android.reside_menu.ResideMenu;
import net.blaklizt.streets.android.reside_menu.ResideMenuItem;
import net.blaklizt.streets.android.swipe.SwipeLayout;

import java.util.HashMap;
import java.util.Iterator;

import static java.lang.String.format;

public class MenuActivity extends AppCompatActivity implements
        View.OnClickListener,
        DialogInterface.OnClickListener,
        DialogInterface.OnMultiChoiceClickListener
{

    protected static final String TAG = StreetsCommon.getTag(MenuActivity.class);
    protected static MenuActivity menuActivity;
    protected ResideMenu resideMenu;
    protected DrawerLayout mDrawerLayout;
    protected SwipeLayout streetsSwipeMenu;
    protected ResideMenuItem itemMapLayout;
    protected ResideMenuItem itemProfileLayout;
    protected ResideMenuItem itemNavigationLayout;
    protected TextView status_text_view;
    protected NavigationView leftNavigationView;
    protected Menu streetsLeftMenu;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "+++ ON CREATE +++");
        Log.i(TAG, format("--- savedInstanceState: %s", savedInstanceState != null ? savedInstanceState.toString() : null));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reside_main);
        menuActivity = this;

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_menu);
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setHomeButtonEnabled(true);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerLayout.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean lostFocus) {
                Log.i(TAG, "+++ ON FOCUS CHANGE +++");
                Log.i(TAG, format("--- view: %s", view != null ? view.getTag(): null));
                Log.i(TAG, format("--- lostFocus: %s", lostFocus));
                Log.i(TAG, format("--- mDrawerLayout.isDrawerOpen: %s", mDrawerLayout.isDrawerOpen(GravityCompat.START)));

                if (!lostFocus) {
                    Log.i(TAG, "Disabling right menu swipe...");
                    resideMenu.setSwipeDirectionDisable(ResideMenu.DIRECTION_RIGHT);
                }
                else {
                    Log.i(TAG, "Enabling right menu swipe...");
                    resideMenu.setSwipeDirectionEnable(ResideMenu.DIRECTION_RIGHT);
                }
            }
        });

//        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
//        navigationView.setNavigationItemSelectedListener(this);

        status_text_view = (TextView) findViewById(R.id.status_text_view);
        setAppInfo("I'm the streets, look both way before you cross me!");

        setUpMenu();
        if( savedInstanceState == null )
            changeFragment(MapLayout.getInstance());

        streetsSwipeMenu = (SwipeLayout) findViewById(R.id.streets_swipe_menu);
        View swipeMenu = findViewById(R.id.swipe_menu);
        streetsSwipeMenu.setShowMode(SwipeLayout.ShowMode.LayDown);
        streetsSwipeMenu.addDrag(SwipeLayout.DragEdge.Right, swipeMenu);

        leftNavigationView = (NavigationView)findViewById(R.id.left_navigation_view);

        streetsLeftMenu = leftNavigationView.getMenu();
        SubMenu streetsOptions = streetsLeftMenu.addSubMenu("Streetz Options");

        Log.i(TAG, "Option menu: " + streetsOptions);

        HashMap<String, String> preferences = Startup.getStreetsCommon().getUserPreferenceValues();

        for (String preferenceName : preferences.keySet()) {
            String preferenceValue = preferences.get(preferenceName);
            String preferenceType = Startup.getStreetsCommon().getUserPreferenceTypes().get(preferenceName);
            TextView preferenceView = null;

            if (preferenceType.equalsIgnoreCase("boolean")) {
                preferenceView = new CheckBox(this);
                ((CheckBox) preferenceView).setChecked(Boolean.parseBoolean(preferenceValue));
            } else if (preferenceType.equalsIgnoreCase("string")) {
                preferenceView = new TextView(this);
            }

            if (preferenceView != null) {
                preferenceView.setText(Startup.getStreetsCommon().getUserPreferenceDescriptions().get(preferenceName));
//                streetsOptions.addView(preferenceView);
            }
        }
    }

    @Override
    public void onClick(DialogInterface dialogInterface, int index) {
        Log.i(TAG, dialogInterface.toString() + ": +++ ON CLICK +++");
        Log.i(TAG, format("--- dialogInterface: %s", dialogInterface != null ? dialogInterface.toString() : null));
        Log.i(TAG, format("--- index: %d", index));
        boolean exit = (index == DialogInterface.BUTTON_POSITIVE);
        if (exit) { Startup.getInstance().finish(); finish(); }
    }

    @Override
    public void onClick(View view) {
        Log.i(TAG, view.toString() + ": +++ ON CLICK +++");
        Log.i(TAG, format("--- view: %s", view != null ? view.getTag() : null));

        if (view == itemMapLayout){
            changeFragment(MapLayout.getInstance());
        }
        else if (view == itemNavigationLayout){
            changeFragment(NavigationLayout.getInstance());
        }
        else if (view == itemProfileLayout){
            changeFragment(ProfileLayout.getInstance());
        }

        resideMenu.closeMenu();
    }

    @Override
    public void onBackPressed() {
        Log.i(TAG, "+++ ON BACK PRESSED +++");
        Log.i(TAG, format("--- mDrawerLayout.isDrawerOpen: %s", mDrawerLayout.isDrawerOpen(GravityCompat.START)));
        Log.i(TAG, format("--- resideMenu.isOpened: %s", resideMenu.isOpened()));

        if (mDrawerLayout.isDrawerOpen(GravityCompat.START) || resideMenu.isOpened()) {
            mDrawerLayout.closeDrawers();
            resideMenu.closeMenu();
            resideMenu.setSwipeDirectionEnable(ResideMenu.DIRECTION_RIGHT);
            return;
        }

        CharSequence[] items = new CharSequence[]{"Never ask again"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Exit application?")
                .setMultiChoiceItems(items, new boolean[]{true}, this)
                .setPositiveButton("Yes", this)
                .setNegativeButton("No", this).show();
    }

    @Override
    public void onClick(DialogInterface dialogInterface, int index, boolean isChecked) {
        Log.i(TAG, dialogInterface.toString() + ": +++ ON CLICK +++");
        Log.i(TAG, format("--- dialogInterface: %s", dialogInterface != null ? dialogInterface.toString() : null));
        Log.i(TAG, format("--- index: %d", index));
        Log.i(TAG, format("--- isChecked: %s", isChecked));
        boolean exit = (index == DialogInterface.BUTTON_POSITIVE);
        //only persist prefs on positive response
        if (exit) {
            Startup.getStreetsCommon().setUserPreference("ask_on_exit", !isChecked ? "1" : "0");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.i(TAG, "+++ ON CREATE OPTIONS MENU +++");
        Log.i(TAG, format("--- menu: %s", menu));
        getMenuInflater().inflate(R.menu.option_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.i(TAG, "+++ ON OPTIONS ITEM SELECTED +++");
        Log.i(TAG, format("--- item: %s", item != null ? item.getTitle() : null));
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                Log.i(TAG, "Disabling right menu swipe...");
                resideMenu.setSwipeDirectionDisable(ResideMenu.DIRECTION_RIGHT);
                return true;
//            case R.id.action_search:
//                return true;
//            case R.id.action_settings:
//                return true;
            case R.id.action_dashboard:
                resideMenu.openMenu(ResideMenu.DIRECTION_RIGHT);
                resideMenu.setSwipeDirectionEnable(ResideMenu.DIRECTION_RIGHT);
                return true;
//            case R.id.action_get_location:
//                pager.setCurrentItem(0);
//                MapLayout.getInstance().refreshLocation();
//                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

//    @Override
//    public void onFocusChange(View view, boolean hasFocus) {
//        Log.i(TAG, "+++ ON FOCUS CHANGE +++");
//        Log.i(TAG, format("--- view: %s", view != null ? view.getTag(): null));
//        Log.i(TAG, format("--- hasFocus: %s", hasFocus));
//        if (view != null && view instanceof DrawerLayout && ((DrawerLayout)view).isDrawerOpen(GravityCompat.START)) {
//            Log.i(TAG, "Disabling right menu swipe...");
//            resideMenu.setSwipeDirectionDisable(ResideMenu.DIRECTION_RIGHT);
//        }
//        else if (view != null && view instanceof DrawerLayout && !((DrawerLayout)view).isDrawerOpen(GravityCompat.START)) {
//            Log.i(TAG, "Enabling right menu swipe...");
//            resideMenu.setSwipeDirectionEnable(ResideMenu.DIRECTION_RIGHT);;
//        }
//    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent motionEvent) {
        Log.i(TAG, "+++ DISPATCH TOUCH EVENT +++");
        Log.i(TAG, format("--- motionEvent: %s", motionEvent != null ? motionEvent.toString() : null));
        return resideMenu.dispatchTouchEvent(motionEvent);
    }

//    @Override
//    public boolean onNavigationItemSelected(MenuItem menuItem) {
//        Log.i(TAG, "+++ ON NAVIGATION ITEM SELECTED +++");
//        Log.i(TAG, format("--- menuItem: %s", menuItem != null ? menuItem.getTitle() : null));
//        if (menuItem.isCheckable()) {
//            menuItem.setChecked(!menuItem.isChecked());
////            menuItem.setIcon(menuItem.isChecked() ? checkBoxOn : checkBoxOff);
////                          mDrawerLayout.closeDrawers();
//        }
//        return true;
//    }

    private void setUpMenu() {

        // attach to current activity;
        resideMenu = new ResideMenu(this);
        resideMenu.setUse3D(false);
        resideMenu.setBackground(android.R.color.black);
        resideMenu.attachToActivity(this);
        //valid scale factor is between 0.0f and 1.0f. leftmenu'width is 150dip.
        resideMenu.setScaleValue(0.6f);

        // create menu items;
        itemMapLayout =         new ResideMenuItem(this, R.drawable.applications_internet,  "The Streetz");
        itemNavigationLayout =  new ResideMenuItem(this, R.drawable.navigation,             "Navigation");
        itemProfileLayout =     new ResideMenuItem(this, R.drawable.compass,                "Profile");

        itemMapLayout.setOnClickListener(this);
        itemNavigationLayout.setOnClickListener(this);
        itemProfileLayout.setOnClickListener(this);

        resideMenu.addMenuItem(itemMapLayout,           ResideMenu.DIRECTION_RIGHT);
        resideMenu.addMenuItem(itemNavigationLayout,    ResideMenu.DIRECTION_RIGHT);
        resideMenu.addMenuItem(itemProfileLayout,       ResideMenu.DIRECTION_RIGHT);

        // You can disable a direction by setting ->
         resideMenu.setSwipeDirectionDisable(ResideMenu.DIRECTION_LEFT);

//        findViewById(R.id.title_bar_right_menu).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                resideMenu.openMenu(ResideMenu.DIRECTION_RIGHT);
//            }
//        });
    }

//    private ResideMenu.OnMenuListener menuListener = new ResideMenu.OnMenuListener() {
//        @Override
//        public void openMenu() {
//            Toast.makeText(menuActivity, "Menu is opened!", Toast.LENGTH_SHORT).show();
//        }
//
//        @Override
//        public void closeMenu() {
//            Toast.makeText(menuActivity, "Menu is closed!", Toast.LENGTH_SHORT).show();
//        }
//    };

    public static MenuActivity getInstance() { return menuActivity; }

    public void setAppInfo(String information) { status_text_view.setText(information); }

    private void changeFragment(Fragment targetFragment){
        resideMenu.clearIgnoredViewList();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.main_fragment, targetFragment, "fragment")
                .setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit();
    }
}
