package net.blaklizt.streets.android.activity;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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
public class DetailsLayout extends Fragment
{
	private static final String TAG = StreetsCommon.getTag(DetailsLayout.class);
	private static DetailsLayout detailsLayout;
	protected LayoutInflater inflater;

	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		Log.i(TAG, "+++ ON CREATE VIEW +++");
		super.onCreateView(inflater, container, savedInstanceState);
		this.inflater = inflater;
		View view = inflater.inflate(R.layout.activity_detail, container, false);
		detailsLayout = this;

		// Inflate the layout for this fragment

        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Service has been requested successfully\n" +
                                "Estimated time of arrival: 22 minutes\n",
                        Snackbar.LENGTH_INDEFINITE).setAction("Action", null).show();
            }
        });

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

	public static DetailsLayout getInstance() { return detailsLayout; }
}
