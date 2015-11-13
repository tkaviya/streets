package net.blaklizt.streets.android.activity.helpers;

import android.support.v4.app.Fragment;

public abstract class StreetsAbstractView extends Fragment implements StreetsInterfaceView {

    /* only called if you call registerOnDestroyHandler on Startup class */
    public void onTermination() {}

    public String getClassName() {
        return this.getClass().getSimpleName();
    }
}
