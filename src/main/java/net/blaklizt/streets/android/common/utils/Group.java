package net.blaklizt.streets.android.common.utils;

import java.util.ArrayList;

/**
 * Created by tsungai.kaviya on 2015-11-13.
 */
public class Group {

    public ArrayList<Object> groupItems = new ArrayList<>();

    Group(Object...members) {
        for (Object member : members) {
            groupItems.add(member);
        }
    }

    public Object g(int index) {
        return index < groupItems.size() ? groupItems.get(index) : null;
    }

}
