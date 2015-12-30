package net.blaklizt.streets.android.activity.helpers;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import net.blaklizt.streets.android.common.StreetsCommon;
import net.blaklizt.streets.android.sidemenu.model.SlideMenuItem;

import static net.blaklizt.streets.android.activity.helpers.SequentialTaskManager.TaskStatus.COMPLETED;

public abstract class StreetsAbstractView extends Fragment implements StreetsInterfaceView {

    private final String TAG = StreetsCommon.getTag(this.getClass());
    protected String TAB_HEADER;
    protected int menuIconResourceId;
    protected SlideMenuItem slideMenuItem;

    /* only called if you call registerOnDestroyHandler on Startup class */
    public void onTermination() {}

    public String getClassName() { return this.getClass().getSimpleName(); }

    public void prepareMenu(SlideMenuItem slideMenuItem) {
        this.TAB_HEADER = slideMenuItem.getName();
        this.menuIconResourceId = slideMenuItem.getImageRes();
        this.slideMenuItem = slideMenuItem;
    }

    @Override
    public final void onViewCreated(View view, Bundle savedInstanceState) {
        Log.i(TAG, "+++ ON VIEW CREATED +++");
        super.onViewCreated(view, savedInstanceState);
        SequentialTaskManager.onViewInitialization(this, COMPLETED);
    }
}
