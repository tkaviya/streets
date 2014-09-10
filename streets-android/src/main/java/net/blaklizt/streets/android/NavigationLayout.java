package net.blaklizt.streets.android;

import android.os.Bundle;
import android.support.v4.app.Fragment;

/**
 * Created with IntelliJ IDEA.
 * User: tkaviya
 * Date: 6/22/14
 * Time: 12:05 AM
 */
public class NavigationLayout extends Fragment
{
    private static NavigationLayout navigationLayout = null;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        navigationLayout = this;
    }

    public static Fragment getInstance()
    {
        return navigationLayout;
    }
}