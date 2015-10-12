package net.blaklizt.streets.android.activity;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import net.blaklizt.streets.android.R;
import net.blaklizt.streets.android.common.StreetsCommon;

/**
 * Created by tsungai.kaviya on 2015-09-23.
 */
public class BounceLayout extends Fragment
{
	private static final String TAG = StreetsCommon.getTag(BounceLayout.class);
	private static BounceLayout bounceLayout;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Log.i(TAG, "+++ ON CREATE VIEW +++");
		super.onCreateView(inflater, container, savedInstanceState);

		bounceLayout = this;

		// Inflate the layout for this fragment

		return inflater.inflate(R.layout.bounce_layout, container, false);
	}

	@Override
	public void onStart() {
		Log.i(TAG, "+++ ON START +++");
		super.onStart();
	}

	@Override
	public void onResume() {
		Log.i(TAG, "+++ ON RESUME +++");
		super.onResume();
	}

	public static BounceLayout getInstance() { return bounceLayout; }
}
