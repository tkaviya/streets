package net.blaklizt.streets.android.model;

/**
 * Created with IntelliJ IDEA.
 * User: photon
 * Date: 2015/06/15
 * Time: 12:49 AM
 */
public class NavDrawerItem {

	private boolean showNotify;
	private String title;
			
	public NavDrawerItem() { }

	public NavDrawerItem(boolean showNotify, String title) {
		this.showNotify = showNotify;
		this.title = title;
	}

	public boolean isShowNotify() { return showNotify; }

	public void setShowNotify(boolean showNotify) { this.showNotify = showNotify; }

	public String getTitle() { return title; }

	public void setTitle(String title) { this.title = title; }
}
