package net.blaklizt.streets.android.activity;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.blaklizt.streets.android.R;
import net.blaklizt.streets.android.activity.helpers.StreetsAbstractView;

/**
 * Created by tsungai.kaviya on 2015-09-23.
 */
public class ProfileLayout extends StreetsAbstractView<ProfileLayout>
{
	private static final String TAG = StreetsCommon.getTag(ProfileLayout.class);
	private static ProfileLayout profileLayout;
	protected LayoutInflater inflater;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Log.i(TAG, "+++ ON CREATE VIEW +++");
		super.onCreateView(inflater, container, savedInstanceState);
		this.inflater = inflater;
		View view = inflater.inflate(R.layout.profile_layout, container, false);
		profileLayout = this;
        return view;
	}


    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "+++ ON CREATE +++");
        super.onCreate(savedInstanceState);
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

	public static ProfileLayout getInstance() {
		if (profileLayout == null) {
			profileLayout = new ProfileLayout();
		}
		return profileLayout;
	}
}
