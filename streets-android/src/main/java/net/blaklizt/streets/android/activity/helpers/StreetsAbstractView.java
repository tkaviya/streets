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

    private final String TAG = StreetsCommon.getTag(MenuLayout.class);
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
        Log.i(TAG, "+++ ON VIEW CREATED +++");
        Log.i(TAG, format("--- savedInstanceState: %s", savedInstanceState != null ? savedInstanceState.toString() : null));
        super.onViewCreated(view, savedInstanceState);
        this.containerView = view.findViewById(R.id.content_frame);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}
