package net.blaklizt.streets.android.activity;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import net.blaklizt.streets.android.R;
import net.blaklizt.streets.android.activity.helpers.StreetsAbstractView;
import net.blaklizt.streets.android.common.StreetsCommon;

/**
 * Created by tsungai.kaviya on 2015-09-23.
 */
public class ProfileLayout extends StreetsAbstractView
{
	private static final String TAG = StreetsCommon.getTag(ProfileLayout.class);
	protected LayoutInflater inflater;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Log.i(TAG, "+++ ON CREATE VIEW +++");
		super.onCreateView(inflater, container, savedInstanceState);
		this.inflater = inflater;
		return inflater.inflate(R.layout.profile_layout, container, false);
	}
}
