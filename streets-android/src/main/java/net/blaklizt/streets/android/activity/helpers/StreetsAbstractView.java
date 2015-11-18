package net.blaklizt.streets.android.activity.helpers;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;

import net.blaklizt.streets.android.R;
import net.blaklizt.streets.android.activity.MenuLayout;
import net.blaklizt.streets.android.common.StreetsCommon;
import net.blaklizt.streets.android.sidemenu.model.SlideMenuItem;

import static java.lang.String.format;

public abstract class StreetsAbstractView extends Fragment implements StreetsInterfaceView {

    private static final String TAG = StreetsCommon.getTag(MenuLayout.class);
    private static StreetsInterfaceView instance;
    protected String TAB_HEADER;
    protected int menuIconResourceId;
    protected SlideMenuItem slideMenuItem;
    private View containerView;

    /* only called if you call registerOnDestroyHandler on Startup class */
    public void onTermination() {}

    public String getClassName() { return this.getClass().getSimpleName(); }

    public void prepareMenu(SlideMenuItem slideMenuItem) {
        this.TAB_HEADER = slideMenuItem.getName();
        this.menuIconResourceId = slideMenuItem.getImageRes();
        this.slideMenuItem = slideMenuItem;
    }

    public int getMenuIconResourceId() {
        return menuIconResourceId;
    }

    public SlideMenuItem getSlideMenuItem() { return slideMenuItem; }

    public String getViewName() {
        return this.getClass().getSimpleName();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        instance = this;
        Log.i(TAG, "+++ ON VIEW CREATED +++");
        Log.i(TAG, format("--- savedInstanceState: %s", savedInstanceState != null ? savedInstanceState.toString() : null));
        super.onViewCreated(view, savedInstanceState);
        this.containerView = view.findViewById(R.id.content_frame);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        res = getArguments().getInt(Integer.class.getName());
    }
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
//        return rootView;
//    }



    /*
        private int menuIconResourceId;


//    protected int res;
//    private Bitmap bitmap;

//    public static StreetsFragment newInstance(int resId) {
//        StreetsFragment contentFragment = new StreetsFragment();
////        Bundle bundle = new Bundle();
////        bundle.putInt(Integer.class.getName(), resId);
////        contentFragment.setArguments(bundle);
//        return contentFragment;
//    }

     */
}
