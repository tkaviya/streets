//package net.blaklizt.streets.android;
//
//import android.content.Intent;
//import android.graphics.drawable.Drawable;
//import android.os.Bundle;
//import android.support.v4.app.FragmentActivity;
//import android.support.v4.app.FragmentTabHost;
//import android.util.Log;
//import android.widget.TabHost;
//import android.widget.TextView;
//
///**
// * User: tkaviya
// * Date: 6/29/14
// * Time: 7:00 PM
// */
//public class MainLayout extends FragmentActivity {
//
//    public static final String TAG = "MainLayout";
//
//    protected TextView status_text_view;
//
//    /** Called when the activity is first created. */
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        Log.i(TAG, "+++ ON CREATE +++");
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.streets_layout);
//
////        status_text_view = (TextView) findViewById(R.id.status_text_view);
//
////        TabHost tabHost = getTabHost();
//		FragmentTabHost tabHost = (FragmentTabHost)findViewById(android.R.id.tabhost);
//
//        // Tab for map
//        TabHost.TabSpec mapSpec = tabHost.newTabSpec("MAP");
//        mapSpec.setIndicator("MAP", Drawable.createFromPath("drawable-hdpi/ic_menu_mapmode.png"));
//        Intent mapIntent = new Intent(this, MapLayout.class);
//        mapSpec.setContent(mapIntent);
//
////        // Tab for info
//        TabHost.TabSpec infoSpec = tabHost.newTabSpec("INFO");
//        infoSpec.setIndicator("INFO", Drawable.createFromPath("drawable-hdpi/ic_dialog_info.png"));
//        Intent infoIntent = new Intent(this, NavigationLayout.class);
//        infoSpec.setContent(infoIntent);
//
//        // Adding all TabSpec to TabHost
//        tabHost.addTab(mapSpec); // Adding map tab
//        tabHost.addTab(infoSpec); // Adding map tab
//
//        Log.i(TAG, "Displayed Main Layout");
//    }
//
//    @Override
//    public void onStart() {
//        Log.i(TAG, "+++ ON START +++");
//        status_text_view.setText("I'm the streets, look both ways before you cross me!");
//        super.onStart();
//    }
//}