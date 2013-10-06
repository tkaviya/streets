package net.blaklizt.streets.engine.menu;

import net.blaklizt.streets.common.utilities.CommonUtilities;
import net.blaklizt.streets.engine.Streets;
import net.blaklizt.streets.engine.event.BusinessEvent;
import net.blaklizt.streets.engine.event.BusinessProblemEvent;
import net.blaklizt.streets.engine.session.UserSession;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: tkaviya
 * Date: 2013/07/08
 * Time: 10:55 PM
 */
public abstract class Menu
{
	@Autowired
	protected Streets streets;

    protected String errorMessage;
	protected String description;
    protected ArrayList<MenuItem> menuItems = new ArrayList<>();
    protected boolean allowsStringResponse = false;
    protected String validator = "\\d";
	protected UserSession userSession;

	public Menu(Streets streets, UserSession userSession)
	{
		this.streets = streets;
		this.userSession = userSession;
	}

    @Override
	public String toString()
	{
		String result = (errorMessage == null || errorMessage.matches("")) ? "" : errorMessage + "\r\n\r\n";

		for (BusinessEvent businessEvent : streets.getEventEngine().getBusinessEvents())
		{
			result += setStreetsEventFormat(businessEvent.getDescription()) + "\r\n\r\n";
		}

		for (BusinessProblemEvent businessProblemEvent : userSession.getBusinessProblemEvents())
		{
			result += setMiniTitleFormat(">> " +
				businessProblemEvent.getLocation().getLocationName().toUpperCase() + " " +
				businessProblemEvent.getBusinessProblem().getBusinessType().toUpperCase() + " BUSINESS NEEDS " +
				CommonUtilities.formatDoubleToMoney(businessProblemEvent.getBusinessProblem().getCost(),true) + "\r\n")
				+ setUserEventFormat(businessProblemEvent.getDescription()) + "\r\n\r\n";
		}

		result += description + "\r\n\r\n";

		for (int c = 0; c < menuItems.size(); c++)
			result += c + 1 + ". " + menuItems.get(c) + "\r\n";

		return result + "\r\n";
	}

	public abstract String setStreetsEventFormat(String message);

	public abstract String setUserEventFormat(String message);

	public abstract String setMiniTitleFormat(String message);

	public static Menu createMenu(Streets streets, UserSession userSession)
	{
		switch (userSession.getSessionType())
		{
			case HTML:			return new HTMLMenu(streets, userSession);
			case PLAIN_TEXT:	return new DefaultMenu(streets, userSession);
			default:			return new DefaultMenu(streets, userSession);
		}
	}

    public void addItem(MenuItem menuItem)
    {
		if (menuItem != null)
			menuItems.add(menuItem);
    }

	public void setErrorMessage(String errorMessage)
	{
		this.errorMessage = errorMessage;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}

    public ArrayList<MenuItem> getMenuItems()
    {
        return menuItems;
    }

    public Menu execute(int itemNumber, UserSession userSession)
    {
        return menuItems.get(itemNumber).execute(userSession);
    }

    public static boolean isValid(String response, Menu currentMenu)
    {
		boolean isValid = false;
		currentMenu.setErrorMessage("");
		if (currentMenu != null && response != null)
        {
            if (currentMenu.allowsStringResponse)
			{
				isValid = response.matches(currentMenu.validator);
				currentMenu.setErrorMessage("That response is not valid");
			}
            else
            {
                try
				{
					int index = Integer.parseInt(response);
					if (index > currentMenu.getMenuItems().size() || index < 1)
					{
						isValid = false;
						currentMenu.setErrorMessage("You must choose a number on the menu.");
					}
					else isValid = true;

				}
                catch (NumberFormatException e)
				{
					isValid = false;
					currentMenu.setErrorMessage("You must choose a number on the menu.");
				}
            }
        }
        return isValid;
    }
}
