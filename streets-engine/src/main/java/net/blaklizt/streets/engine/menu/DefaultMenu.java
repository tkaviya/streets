package net.blaklizt.streets.engine.menu;

/**
 * Created with IntelliJ IDEA.
 * User: tkaviya
 * Date: 2013/07/08
 * Time: 11:06 PM
 */
public class DefaultMenu extends Menu
{
	@Override
    public String toString()
	{
        String result = (errorMessage == null || errorMessage.matches("")) ? "" : errorMessage + "\r\n";
		result += description + "\r\n\r\n";

        for (int c = 0; c < menuItems.size(); c++)
            result += c + 1 + ". " + menuItems.get(c) + "\r\n";

        return result + "\r\n";
    }
}
