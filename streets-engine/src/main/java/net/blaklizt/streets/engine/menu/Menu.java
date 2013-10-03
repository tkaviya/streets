package net.blaklizt.streets.engine.menu;

import net.blaklizt.streets.engine.Streets;
import net.blaklizt.streets.engine.event.BusinessProblemEvent;
import net.blaklizt.streets.engine.session.UserSession;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

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

	protected List<BusinessProblemEvent> businessProblemEvents;
    protected String errorMessage;
	protected String description;
    protected ArrayList<MenuItem> menuItems = new ArrayList<>();
    protected boolean allowsStringResponse = false;
    protected String validator = "\\d";

    @Override
    public abstract String toString();

	public abstract void setBusinessProblemEvents(UserSession userSession,
		List<BusinessProblemEvent> businessProblemEvents);

	public static Menu createMenu(UserSession.SessionType sessionType)
	{
		switch (sessionType)
		{
			case HTML:			return new HTMLMenu();
			case PLAIN_TEXT:	return new DefaultMenu();
			default:			return new DefaultMenu();
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

	public List<BusinessProblemEvent> getBusinessProblemEvents()
	{
		return businessProblemEvents;
	}

	public String getErrorMessage()
	{
		return errorMessage;
	}

    public String getDescription()
    {
        return description;
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
