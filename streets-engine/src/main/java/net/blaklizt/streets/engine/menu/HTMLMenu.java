package net.blaklizt.streets.engine.menu;

import net.blaklizt.streets.common.utilities.Format;
import net.blaklizt.streets.engine.Streets;
import net.blaklizt.streets.engine.session.UserSession;

/**
 * Created with IntelliJ IDEA.
 * User: tkaviya
 * Date: 8/28/13
 * Time: 3:45 PM
 */
public class HTMLMenu extends Menu
{
	public HTMLMenu(Streets streets, UserSession userSession) {
		super(streets, userSession);
	}

	@Override
	public String toString()
	{
		return super.toString().replaceAll("\r\n","<br />");
	}

	@Override
	public String setStreetsEventFormat(String message) {
		return Format.formatColor(message, "#AAAAFF");
	}

	@Override
	public String setUserEventFormat(String message) {
		return Format.formatColor(message, Format.HTML_COLOR.ORANGE.getColor());
	}

	@Override
	public String setMiniTitleFormat(String message) {
		return "<span style=\"font-size:0.8em;\">" + Format.formatColor(message, "#FFDD22") + "</span>";
	}
}
