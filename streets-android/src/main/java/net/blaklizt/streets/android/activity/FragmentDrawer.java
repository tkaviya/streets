package net.blaklizt.streets.android.activity;

/**
 * Created with IntelliJ IDEA.
 * User: photon
 * Date: 2015/06/15
 * Time: 1:01 AM
 */

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;

import net.blaklizt.streets.android.common.StreetsCommon;
import net.blaklizt.streets.android.model.NavDrawerItem;

import java.util.ArrayList;
import java.util.List;

//import net.blaklizt.streets.android.adapter.NavigationDrawerAdapter;


		
public class FragmentDrawer extends Fragment {
	private static String TAG = StreetsCommon.getTag(FragmentDrawer.class);
//	private RecyclerView recyclerView;
//	private ActionBarDrawerToggle mDrawerToggle;
//	private DrawerLayout mDrawerLayout;
//	private NavigationDrawerAdapter adapter;
	private View containerView;
	private static String[] titles = null;
//	private FragmentDrawerListener drawerListener;

	public FragmentDrawer() {}
	
//	public void setDrawerListener(FragmentDrawerListener listener) {
//		this.drawerListener = listener;
//	}
	
	public static List<NavDrawerItem> getData() {
		List<NavDrawerItem> data = new ArrayList<>();
		// preparing navigation drawer items
		for (String title : titles) {
			NavDrawerItem navItem = new NavDrawerItem();
			navItem.setTitle(title);
			data.add(navItem);
		}
		return data;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		try {
			Log.i(TAG, "+++ ON CREATE +++");
			super.onCreate(savedInstanceState);
			// drawer labels

			Log.i(TAG, "Loading places of interest");
			ArrayList placesOfInterest = AppContext.getInstance().getStreetsDBHelper().getPlacesOfInterest();
			String[] places = new String[placesOfInterest.size()];

			for (int c = 0; c < placesOfInterest.size(); c++) {
				places[c] = (String) placesOfInterest.get(c);
			}

			titles = places;

//			mDrawerLayout = (DrawerLayout)getActivity().findViewById(R.id.drawer_layout);
//			setUp(R.id.drawer_layout, mDrawerLayout, Streets.getInstance().toolbar);
//			setDrawerListener(Streets.getInstance());

//			mDrawerLayout = Streets.getInstance().mDrawerLayout;
//			mDrawerToggle = Streets.getInstance().mDrawerToggle;

//			mDrawerLayout.setDrawerListener(mDrawerToggle);
//			mDrawerLayout.post(new Runnable() { @Override public void run() { mDrawerToggle.syncState(); } });

		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			Log.e(TAG, "Failed to create FragmentDrawer: " + ex.getMessage());
		}
	}
	
//	@Override
//	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
//	{
//		Log.i(TAG, "+++ ON CREATE VIEW +++");
//		// Inflating view layout
//		View layout = inflater.inflate(R.layout.fragment_navigation_drawer, container, false);
//		recyclerView = (RecyclerView) layout.findViewById(R.id.drawerList);
//
////		adapter = new NavigationDrawerAdapter(getActivity(), getData());
////		recyclerView.setAdapter(adapter);
////		recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
////		recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), recyclerView, new ClickListener()
////		{
////			@Override
////			public void onClick(View view, int position) {
////				Log.i(TAG, "+++ ON CLICK +++");
////				drawerListener.onDrawerItemSelected(view, position);
////				mDrawerLayout.closeDrawer(containerView);
////			}
////
////			@Override
////			public void onLongClick(View view, int position) { Log.i(TAG, "+++ ON LONG CLICK +++"); }
////		}));
//
//		return layout;
//	}

			
//	public void setUp(int fragmentId, DrawerLayout drawerLayout, final Toolbar toolbar)
//	{
//		containerView = getActivity().findViewById(fragmentId);
//		mDrawerLayout = drawerLayout;
//		mDrawerToggle = new ActionBarDrawerToggle(getActivity(), drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close)
//		{
//			@Override
//			public void onDrawerOpened(View drawerView) {
//				super.onDrawerOpened(drawerView);
//				getActivity().invalidateOptionsMenu();
//			}
//
//			@Override
//			public void onDrawerClosed(View drawerView) {
//				super.onDrawerClosed(drawerView);
//				getActivity().invalidateOptionsMenu();
//			}
//
//			@Override
//			public void onDrawerSlide(View drawerView, float slideOffset) {
//				super.onDrawerSlide(drawerView, slideOffset);
//				toolbar.setAlpha(1 - slideOffset / 2);
//			}
//		};
//
//		mDrawerLayout.setDrawerListener(mDrawerToggle);
//		mDrawerLayout.post(new Runnable() { @Override public void run() { mDrawerToggle.syncState(); } });
//
//	}
	
	public static interface ClickListener {
		public void onClick(View view, int position);
		public void onLongClick(View view, int position);
	}
	
//	static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener
//	{
//		private GestureDetector gestureDetector;
//		private ClickListener clickListener;
//		public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final ClickListener clickListener)
//		{
//			this.clickListener = clickListener;
//			gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener()
//			{
//				@Override public boolean onSingleTapUp(MotionEvent e) { Log.i(TAG, "+++ ON SINGLE TAP UP +++"); return true; }
//				@Override public void onLongPress(MotionEvent e)
//				{
//					Log.i(TAG, "+++ ON LONG PRESS +++");
//					View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
//					if (child != null && clickListener != null)
//					{
//						clickListener.onLongClick(child, recyclerView.getChildPosition(child));
//					}
//				}
//			});
//		}
//
//		@Override
//		public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
//			Log.i(TAG, "+++ ON INTERCEPT TOUCH EVENT +++");
//			View child = rv.findChildViewUnder(e.getX(), e.getY());
//			if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
//				clickListener.onClick(child, rv.getChildPosition(child));
//				}
//			return false;
//			}
//
//		@Override public void onTouchEvent(RecyclerView rv, MotionEvent e) { Log.i(TAG, "+++ ON TOUCH EVENT +++"); }
//	}

	public interface FragmentDrawerListener { public void onDrawerItemSelected(View view, int position); }
}
