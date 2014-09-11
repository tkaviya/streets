package net.blaklizt.streets.android;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created with IntelliJ IDEA.
 * User: tkaviya
 * Date: 6/22/14
 * Time: 12:05 AM
 */
public class NavigationLayout extends Fragment
{
    private static final String TAG = Streets.TAG + "_" + NavigationLayout.class.getSimpleName();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.i(TAG, "+++ ON CREATE VIEW +++");

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.nav_layout, container, false);

        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "+++ ON CREATE +++");
        super.onCreate(savedInstanceState);
    }
}