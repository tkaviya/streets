package net.blaklizt.streets.android.sidemenu.model;


import net.blaklizt.streets.android.sidemenu.interfaces.Resourceble;

public class SlideMenuItem implements Resourceble {
    private String name;
    private int imageRes;

    public SlideMenuItem(String name, int imageRes) {
        this.name = name;
        this.imageRes = imageRes;
    }

    public String getName() {
        return name;
    }

    public int getImageRes() {
        return imageRes;
    }
}
