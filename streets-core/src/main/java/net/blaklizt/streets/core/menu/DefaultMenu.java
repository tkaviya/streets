package net.blaklizt.streets.core.menu;

import net.blaklizt.streets.core.session.UserSession;

/**
 * Created with IntelliJ IDEA.
 * User: tkaviya
 * Date: 2013/07/08
 * Time: 11:06 PM
 */
public class DefaultMenu extends Menu
{
	public DefaultMenu(UserSession userSession) { super(userSession); }

	@Override public String setStreetsEventFormat(String message) { return message; }

	@Override public String setUserEventFormat(String message) { return message; }

	@Override public String setMiniTitleFormat(String message) { return message; }
}