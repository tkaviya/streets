package net.blaklizt.streets.android.activity;

import android.app.FragmentTransaction;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.widget.LinearLayout;

import net.blaklizt.streets.android.R;
import net.blaklizt.streets.android.sidemenu.fragment.ContentFragment;
import net.blaklizt.streets.android.common.StreetsCommon;
import net.blaklizt.streets.android.common.TaskInfo;
import net.blaklizt.streets.android.sidemenu.fragment.StreetsFragment;
import net.blaklizt.streets.android.sidemenu.interfaces.Resourceble;
import net.blaklizt.streets.android.sidemenu.model.SlideMenuItem;
import net.blaklizt.streets.android.sidemenu.util.ViewAnimator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.codetail.animation.SupportAnimator;
import io.codetail.animation.ViewAnimationUtils;

import static java.lang.String.format;

/******************************************************************************
 * *
 * Created:     02 / 11 / 2015                                             *
 * Platform:    Red Hat Linux 9                                            *
 * Author:      Tich de Blak (Tsungai Kaviya)                              *
 * Copyright:   Blaklizt Entertainment                                     *
 * Website:     http://www.blaklizt.net                                    *
 * Contact:     blaklizt@gmail.com                                         *
 * *
 * This program is free software; you can redistribute it and/or modify    *
 * it under the terms of the GNU General Public License as published by    *
 * the Free Software Foundation; either version 2 of the License, or       *
 * (at your option) any later version.                                     *
 * *
 * This program is distributed in the hope that it will be useful,         *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of          *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.    See the         *
 * GNU General Public License for more details.                            *
 * *
 ******************************************************************************/


public class MenuLayout extends AppCompatActivity implements ViewAnimator.ViewAnimatorListener {

    private static final String TAG = StreetsCommon.getTag(MenuLayout.class);
    public static final HashMap<String, StreetsFragment> streetsViews = new HashMap<>();
    private static MenuLayout menuLayout;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private StreetsFragment contentFragment;
    private ViewAnimator viewAnimator;
    private LinearLayout linearLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "+++ ON CREATE +++");
        Log.i(TAG, format("--- savedInstanceState: %s", savedInstanceState != null ? savedInstanceState.toString() : null));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_menu_layout);
        menuLayout = this;
        contentFragment = StreetsFragment.newInstance(R.layout.map_layout);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_frame, MapLayout.getInstance())
                .setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit();
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerLayout.setScrimColor(Color.TRANSPARENT);
        linearLayout = (LinearLayout) findViewById(R.id.left_drawer);
        linearLayout.setOnClickListener(v -> drawerLayout.closeDrawers());

        List<SlideMenuItem> menuItemList = new ArrayList<>();
        menuItemList.add(new SlideMenuItem("Close", R.drawable.icn_close));
        for (StreetsFragment fragment : streetsViews.values()) {
            menuItemList.add(fragment.getSlideMenuItem());
        }

        setActionBar();
        createMenuList();
        viewAnimator = new ViewAnimator<>(this, menuItemList, contentFragment, drawerLayout, this);

    }

    public static MenuLayout getInstance() { return menuLayout; }

    private void createMenuList() {
    }


    private void setActionBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        drawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                drawerLayout,         /* DrawerLayout object */
                toolbar,  /* nav drawer icon to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description */
                R.string.drawer_close  /* "close drawer" description */
        ) {

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                linearLayout.removeAllViews();
                linearLayout.invalidate();
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
                if (slideOffset > 0.6 && linearLayout.getChildCount() == 0)
                    viewAnimator.showMenuContent();
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };
        drawerLayout.setDrawerListener(drawerToggle);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        switch (item.getItemId()) {
            case R.id.action_settings:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private StreetsFragment getReplacementFragement(int position) {
        return streetsViews.get(position - 1);
    }

    private StreetsFragment replaceFragment(StreetsFragment streetsFragment, int topPosition) {
        View view = findViewById(R.id.content_frame);
        int finalRadius = Math.max(view.getWidth(), view.getHeight());
        SupportAnimator animator = ViewAnimationUtils.createCircularReveal(view, 0, topPosition, 0, finalRadius);
        animator.setInterpolator(new AccelerateInterpolator());
        animator.setDuration(ViewAnimator.CIRCULAR_REVEAL_ANIMATION_DURATION);
        animator.start();
        StreetsFragment replacementFragment = getReplacementFragement(topPosition);
        getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, replacementFragment).commit();
        return replacementFragment;
    }

    @Override
    public StreetsFragment onSwitch(Resourceble slideMenuItem, StreetsFragment screenShotable, int position) {
        switch (slideMenuItem.getName()) {
            case StreetsFragment.CLOSE:
                return screenShotable;
            default:
                return replaceFragment(screenShotable, position);
        }
    }

    @Override
    public void disableHomeButton() {
        getSupportActionBar().setHomeButtonEnabled(false);

    }

    @Override
    public void enableHomeButton() {
        getSupportActionBar().setHomeButtonEnabled(true);
        drawerLayout.closeDrawers();

    }

    @Override
    public void addViewToContainer(View view) {
        linearLayout.addView(view);
    }

}
