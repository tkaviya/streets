package net.blaklizt.streets.core.navigation;

/**
 * Created with IntelliJ IDEA.
 * User: tkaviya
 * Date: 2013/07/08
 * Time: 11:18 PM
 */

//@Singleton
public class MapEngine {

    private static MapEngine mapEngine = null;

    private MapEngine() {} //make this a singleton

    public static MapEngine getInstance()
    {
        if (mapEngine == null)
        {
            mapEngine = new MapEngine();
        }
        return mapEngine;
    }
}
