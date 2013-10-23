package net.blaklizt.streets.engine;

import net.blaklizt.streets.engine.menu.Menu;
import net.blaklizt.streets.engine.navigation.MapEngine;
import net.blaklizt.streets.engine.session.UserSession;

import java.util.LinkedList;
import java.util.Observable;

public class Streets extends Observable
{
	private static LinkedList<UserSession> loggedInUsers = new LinkedList<>();

    public Menu getMainMenu(UserSession userSession)
    {
        if (userSession != null && userSession.isLoggedIn())
            return MapEngine.getInstance().getMainMenu(this, userSession);
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
