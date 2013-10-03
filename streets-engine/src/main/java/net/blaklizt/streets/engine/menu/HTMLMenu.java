package net.blaklizt.streets.engine.menu;

/**
 * Created with IntelliJ IDEA.
 * User: tkaviya
 * Date: 8/28/13
 * Time: 3:45 PM
 */
public class HTMLMenu extends Menu
{
	@Override
	public String toString()
	{
		String result = (errorMessage == null || errorMessage.matches("")) ? "" : errorMessage + "<br />";
		result += description + "<br /><br />";

		for (int c = 0; c < menuItems.size(); c++)
			result += c + 1 + ". " + menuItems.get(c) + "<br />";

		return result + "<br />";
	}

}
