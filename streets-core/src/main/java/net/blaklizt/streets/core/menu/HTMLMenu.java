package net.blaklizt.streets.core.menu;

import net.blaklizt.streets.core.session.UserSession;
import net.blaklizt.symbiosis.sym_common.utilities.Format;

/**
 * Created with IntelliJ IDEA.
 * SymbiosisUser: tkaviya
 * Date: 8/28/13
 * Time: 3:45 PM
 */
public class HTMLMenu extends Menu
{
	public HTMLMenu( UserSession userSession) {
		super(userSession);
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
