package net.blaklizt.streets.engine;

import net.blaklizt.streets.common.configuration.Configuration;
import net.blaklizt.streets.engine.menu.Menu;
import net.blaklizt.streets.engine.navigation.MapEngine;
import net.blaklizt.streets.engine.session.UserSession;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

public class Streets extends Observable
{
	@Autowired
	private EventEngine eventEngine;

	private static final Logger log4j = Configuration.getNewLogger(Streets.class.getSimpleName());

	private static LinkedList<UserSession> loggedInUsers = new LinkedList<>();

	Streets()
	{
		Timer theStreets = new Timer();

		TimerTask businessProblems = new TimerTask()
		{
			@Override
			public void run() { eventEngine.runBusinessProblems(); }
		};

		Date now = new Date();

		eventEngine.populateUserBusinessProblems();

		theStreets.scheduleAtFixedRate(businessProblems, new Date(now.getTime() + 30000), 30000);
	}

    public Menu getMainMenu(UserSession userSession)
    {
        if (userSession != null && userSession.isLoggedIn())
        {
            return MapEngine.getInstance().getMainMenu(userSession);
        }

		return null;
    }

	public static LinkedList<UserSession> getLoggedInUsers()
	{
		return loggedInUsers;
	}

	public void addLoggedInUser(UserSession userSession) {
		loggedInUsers.add(userSession);
	}

	public void removeLoggedInUser(UserSession userSession) {
		loggedInUsers.remove(userSession);
	}
}
