package net.blaklizt.streets.engine.menu;

import net.blaklizt.streets.engine.Streets;
import net.blaklizt.streets.engine.session.UserSession;

/**
 * Created with IntelliJ IDEA.
 * User: tkaviya
 * Date: 2013/07/08
 * Time: 11:06 PM
 */
public class DefaultMenu extends Menu
{
	public DefaultMenu(Streets streets, UserSession userSession) {
		super(streets, userSession);
	}

	@Override
	public String setStreetsEventFormat(String message) {
		return message;
	}

	@Override
	public String setUserEventFormat(String message) {
		return message;
	}

	@Override
	public String setMiniTitleFormat(String message) {
		return message;
	}
}