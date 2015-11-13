package net.blaklizt.streets.android.activity.helpers;

import net.blaklizt.streets.android.common.TaskInfo;

public abstract class StreetsAbstractTask extends TaskInfo implements StreetsInterfaceView {

    public String getClassName() {
        return this.getClass().getSimpleName();
    }
}
