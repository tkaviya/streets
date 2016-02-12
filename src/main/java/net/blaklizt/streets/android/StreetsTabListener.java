package net.blaklizt.streets.android;

/**
 * User: tkaviya
 * Date: 7/7/14
 * Time: 10:29 PM
 */
//public class StreetsTabListener<T extends Fragment> implements ActionBar.TabListener {
//    private Fragment mFragment;
//    private final Activity mActivity;
//    private final String mTag;
//    private final Class<T> mClass;
//
//    public StreetsTabListener(Activity activity, String tag, Class<T> clz) {
//        mActivity = activity;
//        mTag = tag;
//        mClass = clz;
//    }
//
//	@Override
//    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
//        // Check if the fragment is already initialized
//        if (mFragment == null) {
//            // If not, instantiate and add it to the activity
//            mFragment = Fragment.instantiate(
//                    mActivity, mClass.getName());
//            mFragment.setProviderId(mTag); // id for event provider
//            ft.add(android.R.id.content, mFragment, mTag);
//        } else {
//            // If it exists, simply attach it in order to show it
//            ft.attach(mFragment);
//        }
//
//    }
//
//	@Override
//    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
//        if (mFragment != null) {
//            // Detach the fragment, because another one is being attached
//            ft.detach(mFragment);
//        }
//    }
//
//	@Override
//    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {
//        // User selected the already selected tab. Usually do nothing.
//    }
//
//	@Override
//	public void onTabSelected(ActionBar.Tab tab, android.app.FragmentTransaction fragmentTransaction) {
//		//To change body of implemented methods use File | Settings | File Templates.
//	}
//
//	@Override
//	public void onTabUnselected(ActionBar.Tab tab, android.app.FragmentTransaction fragmentTransaction) {
//		if (mFragment != null) {
//			// Detach the fragment, because another one is being attached
//			fragmentTransaction.detach(mFragment);
//		}
//	}
//
//	@Override
//	public void onTabReselected(ActionBar.Tab tab, android.app.FragmentTransaction fragmentTransaction) {
//		//To change body of implemented methods use File | Settings | File Templates.
//	}
//}
