package net.blaklizt.streets.android;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.actionbarsherlock.app.SherlockFragment;

/**
 * User: tkaviya
 * Date: 7/7/14
 * Time: 10:36 PM
 */
public class TabFragment extends SherlockFragment {
    // your member variables here
    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.streets_layout, container, false);

        return view;
    }

    public void setProviderId(String mTag) {}
}