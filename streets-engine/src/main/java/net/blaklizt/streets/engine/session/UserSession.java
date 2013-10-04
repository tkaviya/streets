package net.blaklizt.streets.engine.session;

import net.blaklizt.streets.engine.event.BusinessProblemEvent;
import net.blaklizt.streets.engine.menu.Menu;
import net.blaklizt.streets.persistence.User;

import java.util.LinkedList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

/**
 * Created with IntelliJ IDEA.
 * User: tkaviya
 * Date: 2013/07/08
 * Time: 11:32 PM
 */
public class UserSession implements Observer
{
	public enum SessionType
	{
		PLAIN_TEXT,
		HTML
	}

    private User user;
    private Menu currentMenu;
    private boolean loggedIn = false;
	private SessionType sessionType = SessionType.PLAIN_TEXT;
	private List<BusinessProblemEvent> businessProblemEvents = new LinkedList<>();

	public UserSession(User user, SessionType sessionType)
	{
		this.user = user;
		this.sessionType = sessionType;
		if (user != null) loggedIn = true;
	}

    @Override
    public void update(Observable o, Object arg)
    {
//        EventEngine eventEngine = (EventEngine)arg;
//        currentMenu = eventEngine.getEvent();
    }

    public void setCurrentMenu(Menu currentMenu)
    {
        this.currentMenu = currentMenu;
    }

    public Menu getCurrentMenu()
    {
        return currentMenu;
    }

	public SessionType getSessionType()
	{
		return sessionType;
	}

	public List<BusinessProblemEvent> getBusinessProblemEvents()
	{
		return businessProblemEvents;
	}

	public void setBusinessProblemEvents(List<BusinessProblemEvent> businessProblemEvents)
	{
		this.businessProblemEvents = businessProblemEvents;
	}

	public boolean isLoggedIn()
    {
        return loggedIn;
    }

    public void setLoggedIn(boolean loggedIn)
    {
        this.loggedIn = loggedIn;
    }

    public void setUser(User user)
    {
        this.user = user;
    }

    public User getUser()
    {
        return user;
    }

	@Override
	public boolean equals(Object o)
	{
		return this.user.equals(o);
	}
}
