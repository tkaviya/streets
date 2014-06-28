package net.blaklizt.streets.core.session;

import net.blaklizt.streets.core.CoreDaoManager;
import net.blaklizt.streets.core.event.BusinessProblemEvent;
import net.blaklizt.streets.core.menu.Menu;
import net.blaklizt.streets.persistence.User;

import java.util.LinkedList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: tkaviya
 * Date: 2013/07/08
 * Time: 11:32 PM
 */
public class UserSession
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
		CoreDaoManager.getInstance().getUserDao().refresh(user);
        return user;
    }

	@Override
	public boolean equals(Object o)
	{
		return this.user.equals(o);
	}
}
